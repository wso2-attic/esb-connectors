/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipError;
import java.util.zip.ZipInputStream;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.*;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.FileConnectorConstants;


public class FileUnzipUtil {

    private static Log log = LogFactory.getLog(FileUnzipUtil.class);

    /**
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public boolean unzip(String zipFilePath, String destDirectory, MessageContext messageContext) {
        log.info("Decompressing a File");

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(FileConnectorConstants.FILECON, FileConnectorConstants.NAMESPACE);
        OMElement result = factory.createOMElement(FileConnectorConstants.RESULT, ns);
        boolean resultStatus = false;

        try {
            FileSystemOptions opts = FTPSiteUtils.createDefaultOptions();
            FileSystemManager manager = VFS.getManager();
            // Create remote object
            FileObject remoteFile = manager.resolveFile(zipFilePath, opts);
            FileObject remoteDesFile = manager.resolveFile(destDirectory, opts);
            // File destDir = new File(destDirectory);
            if (remoteFile.exists()) {
                if (!remoteDesFile.exists()) {
                    remoteDesFile.createFolder();
                }
                //open the zip file
                ZipInputStream zipIn = new ZipInputStream(remoteFile.getContent().getInputStream());
                ZipEntry entry = zipIn.getNextEntry();
                try {
                    // iterates over entries in the zip file
                    while (entry != null) {
                        boolean testResult = false;
                        String filePath = destDirectory + File.separator + entry.getName();
                        // Create remote object
                        FileObject remoteFilePath = manager.resolveFile(filePath, opts);
                        if (log.isDebugEnabled()) {
                            log.info("The created path is " + remoteFilePath.toString());
                        }
                        try {
                            if (!entry.isDirectory()) {
                                // if the entry is a file, extracts it
                                extractFile(zipIn, filePath);
                                testResult = true;
                                OMElement messageElement = factory.createOMElement(FileConnectorConstants.FILE, ns);
                                messageElement.setText(entry.getName() + " | status: " + testResult);
                                result.addChild(messageElement);
                            } else {
                                // if the entry is a directory, make the directory
                                remoteFilePath.createFolder();
                            }
                        } catch (IOException e) {
                            log.error("Unable to process a zip file, ", e);
                        } finally {
                            zipIn.closeEntry();
                            entry = zipIn.getNextEntry();
                        }
                    }
                    messageContext.getEnvelope().getBody().addChild(result);
                    resultStatus = true;
                } finally {
                    //we must always close the zip file
                    zipIn.close();
                }
                if (log.isDebugEnabled()) {
                    log.info("Decompressing the file is done");
                }
            } else {
                log.error("Zip file does not exist");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            resultStatus = false;
            throw new ZipError(e.getMessage());
        }
        return resultStatus;
    }

    /**
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) {
        BufferedOutputStream bos = null;
        try {
            FileSystemOptions opts = FTPSiteUtils.createDefaultOptions();
            FileSystemManager manager = VFS.getManager();
            // Create remote object
            FileObject remoteilePath = manager.resolveFile(filePath, opts);
            //open the zip file
            OutputStream fout = remoteilePath.getContent().getOutputStream();
            bos = new BufferedOutputStream(fout);
            byte[] bytesIn = new byte[FileConnectorConstants.BUFFER_SIZE];

            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            //we must always close the zip file
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}