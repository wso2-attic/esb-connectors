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
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
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

public class FileMove extends AbstractConnector implements Connector {
    private static final Log log = LogFactory.getLog(FileMove.class);

    public void connect(MessageContext messageContext) {
        String source = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.FILE_LOCATION);
        String destination = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.NEW_FILE_LOCATION);

        boolean resultStatus = moveFile(source, destination, messageContext);
        generateResults(messageContext, resultStatus);
    }

    /**
     * Generate the result
     *
     * @param messageContext The message context that is processed by a handler in the handle method
     * @param resultStatus   Result of the status (true/false)
     */
    private void generateResults(MessageContext messageContext, boolean resultStatus) {
        ResultPayloadCreate resultPayload = new ResultPayloadCreate();
        String response = FileConstants.START_TAG + resultStatus + FileConstants.END_TAG;
        try {
            OMElement element = resultPayload.performSearchMessages(response);
            resultPayload.preparePayload(messageContext, element);
        } catch (XMLStreamException e) {
            handleException(e.getMessage(), messageContext);
        } catch (IOException e) {
            handleException(e.getMessage(), messageContext);
        } catch (JSONException e) {
            handleException(e.getMessage(), messageContext);
        }
    }

    /**
     * Move the files
     *
     * @param source      Location of the file
     * @param destination Destination of the file
     * @return return a resultStatus
     */
    private boolean moveFile(String source, String destination, MessageContext messageContext) {
        boolean resultStatus = false;
        StandardFileSystemManager manager = null;
        try {
            manager = FileConnectorUtils.getManager();
            // Create remote object
            FileObject remoteFile = manager.resolveFile(source, FileConnectorUtils.init(messageContext));
            if (remoteFile.exists()) {
                FileObject file = manager.resolveFile(destination, FileConnectorUtils.init(messageContext));
                if (!file.exists()) {
                    file.createFolder();
                }
                if (remoteFile.getType() == FileType.FOLDER) {
                    remoteFile.moveTo(file);
                } else if (remoteFile.getType() == FileType.FILE) {
                    FileObject newFile = manager.resolveFile(destination + File.separator +
                            remoteFile.getName().getBaseName(), FileConnectorUtils.init(messageContext));
                    remoteFile.moveTo(newFile);
                }
                resultStatus = true;
                if (log.isDebugEnabled()) {
                    log.debug("File move completed from " + source + " to " + destination);
                }
            } else {
                log.error("The file/folder location does not exist.");
                resultStatus = false;
            }
        } catch (IOException e) {
            handleException("Unable to move a file/folder.", e, messageContext);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }
        return resultStatus;
    }
}
