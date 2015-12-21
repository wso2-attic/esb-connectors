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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.connector.util.*;

public class FileArchives extends AbstractConnector implements Connector {
    private static final Log log = LogFactory.getLog(FileArchives.class);
    private final byte[] bytes = new byte[FileConstants.BUFFER_SIZE];

    public void connect(MessageContext messageContext) {
        String source = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.FILE_LOCATION);
        String destinstion = (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                FileConstants.NEW_FILE_LOCATION);
        boolean resultStatus = fileCompress(messageContext, source, destinstion);
        generateResults(messageContext, resultStatus);
    }

    /**
     * @param messageContext The message context that is generated for processing the file
     * @param source         The file to be archived
     * @param destination    Destination of the archived file
     * @return return status
     * @throws SynapseException
     */
    private boolean fileCompress(MessageContext messageContext, String source, String destination) {
        boolean resultStatus = false;
        StandardFileSystemManager manager;
        FileSystemOptions opts = FileConnectorUtils.init(messageContext);
        try {
            manager = FileConnectorUtils.getManager();
            FileObject fileObj = manager.resolveFile(source, opts);
            FileObject destObj = manager.resolveFile(destination, opts);
            if (fileObj.exists()) {
                if (fileObj.getType() == FileType.FOLDER) {
                    List<FileObject> fileList = new ArrayList<FileObject>();
                    getAllFiles(fileObj, fileList, messageContext);
                    writeZipFiles(fileObj, destObj, fileList, messageContext);
                } else {
                    ZipOutputStream outputStream = null;
                    InputStream fileIn = null;
                    try {
                        outputStream = new ZipOutputStream(destObj.getContent().getOutputStream());
                        fileIn = fileObj.getContent().getInputStream();
                        ZipEntry zipEntry = new ZipEntry(fileObj.getName().getBaseName());
                        outputStream.putNextEntry(zipEntry);
                        int length;
                        while ((length = fileIn.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, length);
                        }
                    } catch (Exception e) {
                        log.error("Unable to compress a file." + e.getMessage());
                    } finally {
                        try {
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            log.error("Error while closing ZipOutputStream: " + e.getMessage(), e);
                        }
                        try {
                            if (fileIn != null) {
                                fileIn.close();
                            }
                        } catch (IOException e) {
                            log.error("Error while closing InputStream: " + e.getMessage(), e);
                        }
                        manager.close();
                    }
                }
                resultStatus = true;

                if (log.isDebugEnabled()) {
                    log.debug("File archiving completed." + destination);
                }
            } else {
                log.error("The File location does not exist.");
                resultStatus = false;
            }
        } catch (IOException e) {
            handleException("Unable to process the zip file", e, messageContext);
        }
        return resultStatus;
    }

    /**
     * @param dir            source file directory
     * @param fileList       list of file inside directory
     * @param messageContext The message context that is generated for processing the file
     */
    private void getAllFiles(FileObject dir, List<FileObject> fileList, MessageContext
            messageContext) {
        try {
            FileObject[] children = dir.getChildren();
            for (FileObject child : children) {
                fileList.add(child);
                if (child.getType() == FileType.FOLDER) {
                    getAllFiles(child, fileList, messageContext);
                }
            }
        } catch (IOException e) {
            handleException("Unable to get all files", e, messageContext);
        }
    }

    /**
     * @param fileObj        source fileObject
     * @param directoryToZip destination fileObject
     * @param fileList       list of files to be compressed
     * @param messageContext The message context that is generated for processing the file
     * @throws IOException
     */
    private void writeZipFiles(FileObject fileObj, FileObject directoryToZip,
                               List<FileObject> fileList, MessageContext messageContext)
            throws IOException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(directoryToZip.getContent().getOutputStream());
            for (FileObject file : fileList) {
                if (file.getType() == FileType.FILE) {
                    addToZip(fileObj, file, zos);
                }
            }
        } catch (IOException e) {
            handleException("Error occur in writing files", e, messageContext);
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
    }

    /**
     * @param fileObject   Source fileObject
     * @param file         The file inside source folder
     * @param outputStream ZipOutputStream
     */
    private void addToZip(FileObject fileObject, FileObject file, ZipOutputStream outputStream) {
        InputStream fin = null;
        try {
            fin = file.getContent().getInputStream();
            String name = file.getName().toString();
            String entry = name.substring(fileObject.getName().toString().length() + 1,
                    name.length());
            ZipEntry zipEntry = new ZipEntry(entry);
            outputStream.putNextEntry(zipEntry);
            int length;
            while ((length = fin.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
            }
        } catch (IOException e) {
            log.error("Unable to add a file into the zip file directory." + e.getMessage());
        } finally {
            try {
                outputStream.closeEntry();
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                log.error("Error while closing InputStream: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Generate the results
     *
     * @param messageContext The message context that is generated for processing the file
     * @param resultStatus   output result (true/false)
     */
    private void generateResults(MessageContext messageContext, boolean resultStatus) {
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
