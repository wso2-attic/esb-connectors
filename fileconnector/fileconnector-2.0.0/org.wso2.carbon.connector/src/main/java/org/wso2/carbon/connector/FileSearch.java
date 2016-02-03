/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.connector.util.FileConnectorUtils;
import org.wso2.carbon.connector.util.FileConstants;
import org.wso2.carbon.connector.util.FilePattenMatcher;
import org.wso2.carbon.connector.util.ResultPayloadCreate;

public class FileSearch extends AbstractConnector implements Connector {
    private static final Log log = LogFactory.getLog(FileSearch.class);

    public void connect(MessageContext messageContext) {
        String source = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.FILE_LOCATION);
        String filePattern = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.FILE_PATTERN);
        String recursiveSearch =(String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.RECURSIVE_SEARCH);

        search(source, filePattern, recursiveSearch, messageContext);
    }

    /**
     * Generate the file search
     *
     * @param source         Location fo the file
     * @param filePattern    Pattern of the file
     * @param recursiveSearch check whether recursively search or not
     * @param messageContext The message context that is processed by a handler in the handle method
     */
    private void search(String source, String filePattern, String recursiveSearch, MessageContext
            messageContext) {
        ResultPayloadCreate resultPayload = new ResultPayloadCreate();
        StandardFileSystemManager manager = null;
        if (StringUtils.isEmpty(filePattern)) {
            log.error("FilePattern should not be null");
        } else {
            try {
                manager = FileConnectorUtils.getManager();
                FileSystemOptions opt = FileConnectorUtils.init(messageContext);
                FileObject remoteFile = manager.resolveFile(source, opt);
                if (remoteFile.exists()) {
                    FileObject[] children = remoteFile.getChildren();
                    OMFactory factory = OMAbstractFactory.getOMFactory();
                    String outputResult;
                    OMNamespace ns = factory.createOMNamespace(FileConstants.FILECON,
                            FileConstants.NAMESPACE);
                    OMElement result = factory.createOMElement(FileConstants.RESULT, ns);
                    resultPayload.preparePayload(messageContext, result);
                    FilePattenMatcher fpm = new FilePattenMatcher(filePattern);
                    recursiveSearch = recursiveSearch.trim();

                    for (FileObject child : children) {
                        try {
                            if (child.getType() == FileType.FILE && fpm.validate(child.getName().
                                    getBaseName().toLowerCase())) {
                                outputResult = child.getName().getPath();
                                OMElement messageElement = factory.createOMElement(FileConstants.FILE,
                                        ns);
                                messageElement.setText(outputResult);
                                result.addChild(messageElement);
                            } else if (child.getType() == FileType.FOLDER && "true".equals
                                    (recursiveSearch)) {
                                searchSubFolders(child, filePattern, messageContext, factory, result, ns);
                            }
                        } catch (IOException e) {
                            handleException("Unable to search a file.", e, messageContext);
                        } finally {
                            try {
                                if (child != null) {
                                    child.close();
                                }
                            } catch (IOException e) {
                                log.error("Error while closing Directory: " + e.getMessage(),
                                        e);
                            }
                        }
                    }
                    messageContext.getEnvelope().getBody().addChild(result);
                } else {
                    log.error("File location does not exist.");
                }
            } catch (IOException e) {
                handleException("Unable to search a file.", e, messageContext);
            } finally {
                if (manager != null) {
                    manager.close();
                }
            }
        }
    }

    /**
     *
     * @param dir sub directory
     * @param fileList list of file inside directory
     * @param messageContext the message context that is generated for processing the file
     */
    private void getAllFiles(FileObject dir, List<FileObject> fileList, MessageContext
            messageContext) {
        try {
            FileObject[] children = dir.getChildren();
            for (FileObject child : children) {
                fileList.add(child);
            }
        } catch (IOException e) {
            handleException("Unable to list all folders", e, messageContext);
        } finally {
            try {
                if (dir != null) {
                    dir.close();
                }
            } catch (IOException e) {
                log.error("Error while closing Directory: " + e.getMessage(), e);
            }
        }
    }

    /**
     *
     * @param child sub folder
     * @param filePattern pattern of the file to be searched
     * @param messageContext the message context that is generated for processing the file
     * @param factory OMFactory
     * @param result OMElement
     * @param ns OMNamespace
     * @throws IOException
     */
    private void searchSubFolders(FileObject child, String filePattern, MessageContext
            messageContext, OMFactory factory, OMElement result, OMNamespace ns) throws
            IOException {
        List<FileObject> fileList = new ArrayList<FileObject>();
        getAllFiles(child, fileList, messageContext);
        FilePattenMatcher fpm = new FilePattenMatcher(filePattern);
        String outputResult;
        try {
            for (FileObject file : fileList) {
                if (file.getType() == FileType.FILE) {
                    if (fpm.validate(file.getName().getBaseName().toLowerCase())) {
                        outputResult = file.getName().getPath();
                        OMElement messageElement = factory.createOMElement(FileConstants.FILE,
                                ns);
                        messageElement.setText(outputResult);
                        result.addChild(messageElement);
                    }
                } else if (file.getType() == FileType.FOLDER) {
                    searchSubFolders(file, filePattern, messageContext, factory, result, ns);
                }
            }
        } catch (IOException e) {
            handleException("Unable to search a file in sub folder.", e, messageContext);
        }finally {
            try {
                if (child != null) {
                    child.close();
                }
            } catch (IOException e) {
                log.error("Error while closing Directory: " + e.getMessage(), e);
            }
        }
    }
}

