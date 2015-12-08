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

        search(source, filePattern, messageContext);
    }

    /**
     * Generate the file search
     *
     * @param source         Location fo the file
     * @param filePattern    Pattern of the file
     * @param messageContext The message context that is processed by a handler in the handle method
     */
    private void search(String source, String filePattern, MessageContext messageContext) {
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

                    for (FileObject child : children) {
                        if (child.getType() == FileType.FILE && fpm.validate(child.getName().
                                getBaseName().toLowerCase())) {
                            outputResult = child.getName().getBaseName();
                            OMElement messageElement = factory.createOMElement(FileConstants.FILE,
                                    ns);
                            messageElement.setText(outputResult);
                            result.addChild(messageElement);
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
}
