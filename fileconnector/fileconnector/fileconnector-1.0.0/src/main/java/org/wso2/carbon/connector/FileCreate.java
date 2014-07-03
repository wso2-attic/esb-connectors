/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FTPSiteUtils;
import org.wso2.carbon.connector.util.ResultPayloadCreater;

public class FileCreate extends AbstractConnector implements Connector {

	private static final String DEFAULT_ENCODING = "UTF8";
	private static Log log = LogFactory.getLog(FileCreate.class);

	/**
	 * @param messageContext
	 */

	public void connect(MessageContext messageContext) throws ConnectException {

		String fileLocation =
		                      getParameter(messageContext, "filelocation") == null ? "" : getParameter(
		                                                                                               messageContext,
		                                                                                               "filelocation").toString();
		String filename =
		                  getParameter(messageContext, "file") == null ? "" : getParameter(
		                                                                                   messageContext,
		                                                                                   "file").toString();
		String content =
		                 getParameter(messageContext, "content") == null ? "" : getParameter(
		                                                                                     messageContext,
		                                                                                     "content").toString();

		String contentType =
		                     getParameter(messageContext, "contenttype") == null ? "" : getParameter(
		                                                                                             messageContext,
		                                                                                             "contenttype").toString();

		String encoding =
		                  getParameter(messageContext, "encoding") == null ? "" : getParameter(
		                                                                                       messageContext,
		                                                                                       "encoding").toString();
		String filebeforepprocess =
		                            getParameter(messageContext, "filebeforepprocess") == null ? "" : getParameter(
		                                                                                                           messageContext,
		                                                                                                           "filebeforepprocess").toString();
		String fileafterprocsess =
		                           getParameter(messageContext, "fileafterprocsess") == null ? "" : getParameter(
		                                                                                                         messageContext,
		                                                                                                         "fileafterprocsess").toString();
		boolean isFolder =
		                   getParameter(messageContext, "isfolder") == null ? false : Boolean.parseBoolean(getParameter(
		                                                                                                                messageContext,
		                                                                                                                "isfolder").toString());
		if (log.isDebugEnabled()) {
			log.info("File creation started..." + filename);
		}
		boolean resultStatus = false;
		try {
			resultStatus =
			               createFile(fileLocation, filename, content, encoding, fileafterprocsess,
			                          isFolder);
		} catch (IOException e) {
			log.error(e.getMessage());
			handleException(e.getMessage(), messageContext);
		}

		generateOutput(messageContext, resultStatus);

		if (log.isDebugEnabled()) {
			log.info("File create completed....");
		}

	}

	/**
	 * Create a file with Apache commons
	 * 
	 * @param fileLocation
	 * @param filename
	 * @param content
	 * @param encoding
	 * @return
	 */
	private boolean createFile(String fileLocation, String filename, String content,
	                           String encoding, String fileAProcess, Boolean isFolder)
	                                                                                  throws FileSystemException,
	                                                                                  IOException {
		String sftpURL = fileLocation + filename;

		boolean resultStatus = false;

		FileSystemOptions opts = FTPSiteUtils.createDefaultOptions();

		OutputStream out = null;

		FileSystemManager manager = VFS.getManager();
		if (manager != null) {

			FileObject remoteFile = manager.resolveFile(sftpURL, opts);

			if (isFolder) {
				remoteFile.createFolder();
			} else {
				if (content.toString().equals("")) {
					remoteFile.createFile();
				} else {
					FileContent fileContent = remoteFile.getContent();

					out = fileContent.getOutputStream(true);

					if (encoding.equals("")) {
						IOUtils.write(content, out, DEFAULT_ENCODING);

					} else {
						IOUtils.write(content, out, encoding);
					}
				}
			}

			if (!fileAProcess.equals("")) {
				FileObject fileAfterProcess = manager.resolveFile(fileAProcess + filename, opts);
				fileAfterProcess.copyFrom(remoteFile, Selectors.SELECT_SELF);
			}
			if (remoteFile != null) {
				remoteFile.close();
			}

			resultStatus = true;
		}

		resultStatus = true;

		return resultStatus;
	}

	/**
	 * Generate the output payload
	 * 
	 * @param messageContext
	 * @param resultStatus
	 */
	private void generateOutput(MessageContext messageContext, boolean resultStatus) {
		ResultPayloadCreater resultPayload = new ResultPayloadCreater();
		String responce = "<result><success>" + resultStatus + "</success></result>";

		try {
			OMElement element = resultPayload.performSearchMessages(responce);
			resultPayload.preparePayload(messageContext, element);
		} catch (XMLStreamException e) {
			log.error(e.getMessage());
			handleException(e.getMessage(), messageContext);
		} catch (IOException e) {
			log.error(e.getMessage());
			handleException(e.getMessage(), messageContext);
		} catch (JSONException e) {
			log.error(e.getMessage());
			handleException(e.getMessage(), messageContext);
		}

	}
}
