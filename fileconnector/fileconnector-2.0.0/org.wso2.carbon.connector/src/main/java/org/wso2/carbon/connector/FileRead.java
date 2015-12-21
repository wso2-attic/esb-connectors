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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.connector.util.FileConnectorUtils;
import org.wso2.carbon.connector.util.FileConstants;
import org.wso2.carbon.connector.util.ResultPayloadCreate;

public class FileRead extends AbstractConnector implements Connector {
    private static final Log log = LogFactory.getLog(FileRead.class);

    public void connect(MessageContext messageContext) {
        String fileLocation = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.FILE_LOCATION);
        String contentType = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.CONTENT_TYPE);
        String filePattern = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.FILE_PATTERN);
        FileObject fileObj = null;
        StandardFileSystemManager manager = null;
        try {
            manager = FileConnectorUtils.getManager();
            fileObj = manager.resolveFile(fileLocation, FileConnectorUtils.init(messageContext));
            if (fileObj.exists()) {
                if (fileObj.getType() == FileType.FOLDER) {
                    FileObject[] children = fileObj.getChildren();
                    if (children == null || children.length == 0) {
                        log.warn("Empty folder.");
                        handleException("Empty folder.", messageContext);
                    } else if (filePattern != null && !filePattern.trim().equals("")) {
                        boolean bFound = false;
                        for (FileObject child : children) {
                            if (child.getName().getBaseName().matches(filePattern)) {
                                fileObj = child;
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound) {
                            log.warn("File does not exists for the mentioned pattern.");
                            handleException("File does not exists for the mentioned pattern.",
                                    messageContext);
                        }
                    } else {
                        fileObj = children[0];
                    }
                } else if (fileObj.getType() != FileType.FILE) {
                    log.warn("File does not exists, or an empty folder.");
                    handleException("File does not exists, or an empty folder.", messageContext);
                }
            } else {
                log.warn("File/Folder does not exists");
                handleException("File/Folder does not exists", messageContext);
            }
            ResultPayloadCreate.buildFile(fileObj, messageContext, contentType);
            if (log.isDebugEnabled()) {
                log.debug("File read completed." + fileLocation);
            }
        } catch (Exception e) {
            handleException(e.getMessage(), messageContext);
        } finally {
            try {
                // Close the File system if it is not already closed by the finally block of
                // processFile method
                if (fileObj != null && fileObj.getParent() != null
                        && fileObj.getParent().getFileSystem() != null) {
                    manager.closeFileSystem(fileObj.getParent().getFileSystem());
                }
            } catch (FileSystemException warn) {
                // ignore the warning, since we handed over the stream close job to
                // AutoCloseInputStream..
            }
            try {
                if (fileObj != null) {
                    fileObj.close();
                }
            } catch (Exception e) {
                // ignore the warning, since we handed over the stream close job to
                // AutoCloseInputStream..
            }
        }
    }
}