/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.connector.util.FileConnectorUtils;
import org.wso2.carbon.connector.util.FileConstants;
import org.wso2.carbon.connector.util.ResultPayloadCreate;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileListZip extends AbstractConnector implements Connector {
    private static final Log log = LogFactory.getLog(FileListZip.class);

    public void connect(MessageContext messageContext) {
        String source = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.FILE_LOCATION);
        list(messageContext, source);
    }

    /**
     * @param messageContext The message context that is generated for processing the file
     * @param source         Location of the zip file
     */
    private void list(MessageContext messageContext, String source) {
        StandardFileSystemManager manager = null;
        try {
            manager = FileConnectorUtils.getManager();
            ResultPayloadCreate resultPayload = new ResultPayloadCreate();

            // Create remote object
            FileObject remoteFile = manager.resolveFile(source
                    , FileConnectorUtils.init(messageContext));
            if (remoteFile != null && remoteFile.exists()) {
                // open the zip file
                InputStream input = remoteFile.getContent().getInputStream();
                ZipInputStream zip = new ZipInputStream(input);
                OMFactory factory = OMAbstractFactory.getOMFactory();
                String outputResult;
                OMNamespace ns = factory.createOMNamespace(FileConstants.FILECON,
                        FileConstants.NAMESPACE);
                OMElement result = factory.createOMElement(FileConstants.RESULT, ns);
                resultPayload.preparePayload(messageContext, result);
                ZipEntry zipEntry;
                // iterates over entries in the zip file
                while ((zipEntry = zip.getNextEntry()) != null) {
                    if (!zipEntry.isDirectory()) {
                        //add the entries
                        outputResult = zipEntry.getName();
                        OMElement messageElement = factory.createOMElement(FileConstants.FILE, ns);
                        messageElement.setText(outputResult);
                        result.addChild(messageElement);
                    }
                }
                messageContext.getEnvelope().getBody().addChild(result);
                if (log.isDebugEnabled()) {
                    log.debug("The envelop body with the read files path is " +
                            messageContext.getEnvelope().getBody().toString());
                }
                //we must always close the zip file
                zip.close();
                if (log.isDebugEnabled()) {
                    log.debug("File listZip completed with. " + source);
                }
            } else {
                log.error("Zip file does not exist.");
            }
        } catch (IOException e) {
            handleException("Error while processing a file.", e, messageContext);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }
    }
}

