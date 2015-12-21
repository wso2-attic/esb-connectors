/*
 *  Copyright (c)2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FTPSiteUtils;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileList extends AbstractConnector implements Connector {

    private static Log log = LogFactory.getLog(FileList.class);

    public void connect(MessageContext messageContext) throws ConnectException {

        String fileLocation =
                getParameter(messageContext, "filelocation") == null ? "" : getParameter(
                        messageContext,
                        "filelocation").toString();

        list(messageContext, fileLocation);
        log.info("All files are listed......");
    }

    public void list(MessageContext messageContext, String fileLocation) throws SynapseException {

        try {
            FileSystemOptions opts = FTPSiteUtils.createDefaultOptions();
            FileSystemManager manager = VFS.getManager();

            // Create remote object
            FileObject remoteFile = manager.resolveFile(fileLocation, opts);
            if (remoteFile.exists()) {
                log.info("Reading a zip File.");
                // open the zip file
                InputStream input = remoteFile.getContent().getInputStream();
                ZipInputStream zip = new ZipInputStream(input);
                OMFactory factory = OMAbstractFactory.getOMFactory();
                String outputResult;
                OMNamespace ns = factory.createOMNamespace(FileConnectorConstants.FILECON,
                        FileConnectorConstants.NAMESPACE);
                OMElement result = factory.createOMElement(FileConnectorConstants.RESULT, ns);
                ZipEntry zipEntry;
                // iterates over entries in the zip file
                while ((zipEntry = zip.getNextEntry()) != null) {
                    if (!zipEntry.isDirectory()) {
                        //add the entries
                        outputResult = zipEntry.getName();
                        OMElement messageElement = factory.createOMElement(FileConnectorConstants.FILE, ns);
                        messageElement.setText(outputResult);
                        result.addChild(messageElement);
                    }
                }
                messageContext.getEnvelope().getBody().addChild(result);

                if (log.isDebugEnabled()) {
                    log.info("The envelop body with the read files path is " +
                            messageContext.getEnvelope().getBody().toString());
                }
                //we must always close the zip file
                zip.close();
            } else {
                log.error("Zip file does not exist.");
            }
        } catch (IOException e) {
            handleException("Unable to process the zip file", e, messageContext);
        }
    }
}

