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

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.connector.util.FileConnectorUtils;
import org.wso2.carbon.connector.util.FileConstants;
import org.wso2.carbon.connector.util.ResultPayloadCreate;

public class FileExist extends AbstractConnector implements Connector {
    private static final Log log = LogFactory.getLog(FileExist.class);

    public void connect(MessageContext messageContext) {
        String source = (String) ConnectorUtils.lookupTemplateParamater(messageContext
                , FileConstants.FILE_LOCATION);
        boolean isFileExist = isFileExist(source, messageContext);
        generateResults(messageContext, isFileExist);
    }

    /**
     * Generate the result
     *
     * @param messageContext The message context that is generated for processing the file
     * @param isFileExist    Result of the status (true/false)
     */
    private void generateResults(MessageContext messageContext, boolean isFileExist) {
        ResultPayloadCreate resultPayload = new ResultPayloadCreate();
        String response = FileConstants.FILE_EXIST_START_TAG + isFileExist +
                FileConstants.FILE_EXIST_END_TAG;
        OMElement element;
        try {
            element = resultPayload.performSearchMessages(response);
            resultPayload.preparePayload(messageContext, element);
        } catch (XMLStreamException e) {
            handleException(e.getMessage(), e, messageContext);
        } catch (IOException e) {
            handleException(e.getMessage(), e, messageContext);
        } catch (JSONException e) {
            handleException(e.getMessage(), e, messageContext);
        }
    }

    /**
     * Check is that file exists
     *
     * @param source         Location of the file
     * @param messageContext The message context that is generated for processing the file
     * @return return a resultStatus
     */
    private boolean isFileExist(String source, MessageContext messageContext) {
        boolean isFileExist = false;
        StandardFileSystemManager manager = null;
        FileSystemOptions opt = FileConnectorUtils.init(messageContext);
        try {
            manager = FileConnectorUtils.getManager();
            // Create remote object
            FileObject remoteFile = manager.resolveFile(source, opt);
            if (remoteFile.exists()) {
                isFileExist = true;
            }
            if (log.isDebugEnabled()) {
                log.debug("File exist completed with. " + source);
            }
        } catch (IOException e) {
            isFileExist = false;
            handleException("Error while processing a file.", e, messageContext);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }
        return isFileExist;
    }
}
