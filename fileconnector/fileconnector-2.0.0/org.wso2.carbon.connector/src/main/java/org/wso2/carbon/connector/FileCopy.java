/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.connector.util.FileConnectorUtils;
import org.wso2.carbon.connector.util.FileConstants;
import org.wso2.carbon.connector.util.FilePattenMatcher;
import org.wso2.carbon.connector.util.ResultPayloadCreate;

public class FileCopy extends AbstractConnector implements Connector {
    private static final Log log = LogFactory.getLog(FileCopy.class);

    public void connect(MessageContext messageContext) {
        String source = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.FILE_LOCATION);
        String destination = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.NEW_FILE_LOCATION);
        String filePattern = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.FILE_PATTERN);
        FileSystemOptions opts = FileConnectorUtils.init(messageContext);
        boolean resultStatus = copyFile(source, destination, filePattern, messageContext, opts);
        ResultPayloadCreate resultPayload = new ResultPayloadCreate();
        generateResults(messageContext, resultStatus, resultPayload);
    }

    /**
     * Generate the results
     *
     * @param messageContext The message context that is processed by a handler in the handle method
     * @param resultStatus   Result of the status (true/false)
     * @param resultPayload  result payload create
     */
    private void generateResults(MessageContext messageContext, boolean resultStatus,
                                 ResultPayloadCreate resultPayload) {
        String response = FileConstants.START_TAG + resultStatus + FileConstants.END_TAG;
        OMElement element;
        try {
            element = resultPayload.performSearchMessages(response);
            resultPayload.preparePayload(messageContext, element);
        } catch (XMLStreamException e) {
            log.error(e.getMessage());
            handleException(e.getMessage(), e, messageContext);
        } catch (IOException e) {
            log.error(e.getMessage());
            handleException(e.getMessage(), e, messageContext);
        } catch (JSONException e) {
            log.error(e.getMessage());
            handleException(e.getMessage(), e, messageContext);
        }
    }

    /**
     * Copy files
     *
     * @param source         Location of the file
     * @param destination    new file location
     * @param filePattern    pattern of the file
     * @param messageContext The message context that is generated for processing the file
     * @param opts           FileSystemOptions
     * @return return a resultStatus
     */
    private boolean copyFile(String source, String destination, String filePattern,
                             MessageContext messageContext, FileSystemOptions opts) {
        boolean resultStatus = false;
        StandardFileSystemManager manager = null;
        try {
            manager = FileConnectorUtils.getManager();
            FileObject souFile = manager.resolveFile(source, opts);
            FileObject destFile = manager.resolveFile(destination, opts);
            if (StringUtils.isNotEmpty(filePattern)) {
                FileObject[] children = souFile.getChildren();
                for (FileObject child : children) {
                    if (child.getType() == FileType.FILE) {
                        copy(source, destination, filePattern, opts);
                    } else if (child.getType() == FileType.FOLDER) {
                        String newSource = source + File.separator + child.getName().getBaseName();
                        copyFile(newSource, destination, filePattern, messageContext, opts);
                    }
                }
                resultStatus = true;
            } else {
                if (souFile.exists()) {
                    if (souFile.getType() == FileType.FILE) {
                        InputStream fileIn = null;
                        OutputStream fileOut = null;
                        try {
                            String name = souFile.getName().getBaseName();
                            FileObject outFile = manager.resolveFile(destination + File.separator
                                    + name, opts);
                            //TODO make parameter sense
                            fileIn = souFile.getContent().getInputStream();
                            fileOut = outFile.getContent().getOutputStream();
                            IOUtils.copyLarge(fileIn, fileOut);
                            resultStatus = true;
                        } catch (FileSystemException e) {
                            log.error("Error while copying a file " + e.getMessage());
                        } finally {
                            try {
                                if (fileOut != null) {
                                    fileOut.close();
                                }
                            } catch (Exception e) {
                                log.error("Error while closing OutputStream: " + e.getMessage(), e);
                            }
                            try {
                                if (fileIn != null) {
                                    fileIn.close();
                                }
                            } catch (Exception e) {
                                log.error("Error while closing InputStream: " + e.getMessage(), e);
                            }
                        }
                    } else if (souFile.getType() == FileType.FOLDER) {
                        destFile.copyFrom(souFile, Selectors.SELECT_ALL);
                        resultStatus = true;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("File copying completed from " + source + "to" + destination);
                    }
                } else {
                    log.error("The File Location does not exist.");
                    resultStatus = false;
                }
            }
            return resultStatus;
        } catch (IOException e) {
            handleException("Unable to copy a file/folder", e, messageContext);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }
        return resultStatus;
    }

    /**
     * @param source      file location
     * @param destination target file location
     * @param filePattern pattern of the file
     * @param opts        FileSystemOptions
     * @throws IOException
     */
    private void copy(String source, String destination, String filePattern, FileSystemOptions opts)
            throws IOException {
        StandardFileSystemManager manager = FileConnectorUtils.getManager();
        FileObject souFile = manager.resolveFile(source, opts);
        FileObject[] children = souFile.getChildren();
        FilePattenMatcher patternMatcher = new FilePattenMatcher(filePattern);
        for (FileObject child : children) {
            try {
                if (patternMatcher.validate(child.getName().getBaseName())) {
                    String name = child.getName().getBaseName();
                    FileObject outFile = manager.resolveFile(destination + File.separator + name,
                            opts);
                    outFile.copyFrom(child, Selectors.SELECT_FILES);
                }
            } catch (IOException e) {
                log.error("Error occurred while copying a file. " + e.getMessage(), e);
            }
        }
        manager.close();
    }
}

