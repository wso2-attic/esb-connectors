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

import java.io.IOException;
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
import org.wso2.carbon.connector.util.ResultPayloadCreate;

public class FileCreate extends AbstractConnector implements Connector {
    private static final String DEFAULT_ENCODING = FileConstants.DEFAULT_ENCODING;
    private static final Log log = LogFactory.getLog(FileCreate.class);

    /**
     * @param messageContext The message context that is processed by a handler in the handle method
     */
    public void connect(MessageContext messageContext) {
        String source = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.FILE_LOCATION);
        String content = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.CONTENT);
        String encoding = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.ENCODING);
        boolean resultStatus = createFile(source, content, encoding, messageContext);
        generateOutput(messageContext, resultStatus);
    }

    /**
     * Create a file with Apache commons
     *
     * @param source         Location of the file/folder
     * @param content        Content in a file
     * @param encoding       Encoding type
     * @param messageContext The message context that is generated for processing the file
     * @return Return the status
     */
    private boolean createFile(String source, String content,
                               String encoding, MessageContext messageContext) {
        boolean resultStatus = false;
        StandardFileSystemManager manager;
        try {
            OutputStream out = null;
            manager = FileConnectorUtils.getManager();
            if (manager != null) {
                FileObject sourceFile = manager.resolveFile(source
                        , FileConnectorUtils.init(messageContext));
                try {
                    if (FileConnectorUtils.isFolder(sourceFile)) {
                        sourceFile.createFolder();
                    } else {
                        if (StringUtils.isEmpty(content)) {
                            sourceFile.createFile();
                        } else {
                            FileContent fileContent = sourceFile.getContent();
                            out = fileContent.getOutputStream(true);
                            if (StringUtils.isEmpty(encoding)) {
                                IOUtils.write(content, out, DEFAULT_ENCODING);
                            } else {
                                IOUtils.write(content, out, encoding);
                            }
                        }
                    }
                    resultStatus = true;
                    if (log.isDebugEnabled()) {
                        log.debug("File creation completed with " + source);
                    }
                } finally {
                    try {
                        if (sourceFile != null) {
                            sourceFile.close();
                        }
                    } catch (FileSystemException e) {
                        log.error("Error while closing FileObject: " + e.getMessage(), e);
                    }
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        log.error("Error while closing OutputStream: " + e.getMessage(), e);
                    }
                    manager.close();
                }
            }
        } catch (IOException e) {
            handleException("Unable to create a file/folder.", e, messageContext);
        }
        return resultStatus;
    }

    /**
     * Generate the output payload
     *
     * @param messageContext The message context that is processed by a handler in the handle method
     * @param resultStatus   Result of the status (true/false)
     */
    private void generateOutput(MessageContext messageContext, boolean resultStatus) {
        ResultPayloadCreate resultPayload = new ResultPayloadCreate();
        String response = FileConstants.START_TAG + resultStatus + FileConstants.END_TAG;

        try {
            OMElement element = resultPayload.performSearchMessages(response);
            resultPayload.preparePayload(messageContext, element);
        } catch (XMLStreamException e) {
            handleException(e.getMessage(), e, messageContext);
        } catch (IOException e) {
            handleException(e.getMessage(), e, messageContext);
        } catch (JSONException e) {
            handleException(e.getMessage(), e, messageContext);
        }
    }
}
