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
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.synapse.MessageContext;

import org.codehaus.jettison.json.JSONException;

import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.connector.util.FileConnectorUtils;
import org.wso2.carbon.connector.util.FileConstants;
import org.wso2.carbon.connector.util.ResultPayloadCreate;

public class FileAppend extends AbstractConnector implements Connector {
    private static final String DEFAULT_ENCODING = "UTF8";
    private static final Log log = LogFactory.getLog(FileAppend.class);

    public void connect(MessageContext messageContext) {
        String destination = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.NEW_FILE_LOCATION);
        String content = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.CONTENT);
        String encoding = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.ENCODING);
        boolean resultStatus = appendFile(destination, content, encoding, messageContext);
        generateResult(messageContext, resultStatus);
    }

    /**
     * Generate the result
     *
     * @param messageContext The message context that is generated for processing the file
     * @param resultStatus   true/false
     */
    private void generateResult(MessageContext messageContext, boolean resultStatus) {
        ResultPayloadCreate resultPayload = new ResultPayloadCreate();
        String response = FileConstants.START_TAG + resultStatus + FileConstants.END_TAG;
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
     * @param destination    Location if the file
     * @param content        Content that is going to be added
     * @param encoding       Encoding type
     * @param messageContext The message context that is generated for processing the file
     * @return true/false
     */
    private boolean appendFile(String destination, String content,
                               String encoding, MessageContext messageContext) {
        OutputStream out = null;
        boolean resultStatus = false;
        FileObject fileObj = null;
        StandardFileSystemManager manager = null;
        try {
            manager = FileConnectorUtils.getManager();
            fileObj = manager.resolveFile(destination, FileConnectorUtils.init(messageContext));
            if (!fileObj.exists()) {
                fileObj.createFile();
            }
            out = fileObj.getContent().getOutputStream(true);
            if (StringUtils.isEmpty(encoding)) {
                IOUtils.write(content, out, DEFAULT_ENCODING);
            } else {
                IOUtils.write(content, out, encoding);
            }
            resultStatus = true;

            if (log.isDebugEnabled()) {
                log.debug("File appending completed. " + destination);
            }
        } catch (IOException e) {
            handleException("Error while appending a file.", e, messageContext);
        } finally {
            try {
                if (fileObj != null) {
                    //close the file object
                    fileObj.close();
                }
            } catch (FileSystemException e) {
                log.error("Error while closing FileObject: " + e.getMessage(), e);
            }
            try {
                if (out != null) {
                    //close the output stream
                    out.close();
                }
            } catch (IOException e) {
                log.error("Error while closing OutputStream: " + e.getMessage(), e);
            }
            if (manager != null) {
                //close the StandardFileSystemManager
                manager.close();
            }
        }
        return resultStatus;
    }
}
