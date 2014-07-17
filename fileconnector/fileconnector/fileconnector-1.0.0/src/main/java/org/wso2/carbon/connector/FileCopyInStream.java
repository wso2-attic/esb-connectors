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
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FTPSiteUtils;
import org.wso2.carbon.connector.util.ResultPayloadCreater;

public class FileCopyInStream extends AbstractConnector implements Connector {

	private static Log log = LogFactory.getLog(FileCreate.class);

	public void connect(MessageContext messageContext) throws ConnectException {

		String fileLocation =
		                      getParameter(messageContext, "filelocation") == null ? "" : getParameter(
		                                                                                               messageContext,
		                                                                                               "filelocation").toString();
		String filename =
		                  getParameter(messageContext, "file") == null ? "" : getParameter(
		                                                                                   messageContext,
		                                                                                   "file").toString();

		String newFileLocation =
		                         getParameter(messageContext, "newfilelocation") == null ? "" : getParameter(
		                                                                                                     messageContext,
		                                                                                                     "newfilelocation").toString();
		if (log.isDebugEnabled()) {
			log.info("File creation started..." + filename.toString());
			log.info("File Location..." + fileLocation.toString());
		}

		boolean resultStatus = false;
		try {
			resultStatus = copyLargeFiles(fileLocation, filename, newFileLocation);
		} catch (IOException e) {
			handleException(e.getMessage(), messageContext);
		}

		generateResults(messageContext, resultStatus);

	}

	/**
	 * Generate the results
	 * 
	 * @param messageContext
	 * @param resultStatus
	 */
	private void generateResults(MessageContext messageContext, boolean resultStatus) {
		ResultPayloadCreater resultPayload = new ResultPayloadCreater();

		String responce = "<result><copylarge>" + resultStatus + "</copylarge></result>";

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

	/**
	 * Copy the large files
	 * 
	 * @param fileLocation
	 * @param filename
	 * @param newFileLocation
	 * @return
	 */
	private boolean copyLargeFiles(String fileLocation, String filename, String newFileLocation)
	                                                                                            throws IOException {

		String sftpURL = newFileLocation + filename.toString();
		FileSystemOptions opts;
		boolean resultStatus = false;

		opts = FTPSiteUtils.createDefaultOptions();

		FileSystemManager manager = VFS.getManager();
		FileObject localFile = manager.resolveFile(fileLocation + filename, opts);
		FileObject remoteFile = manager.resolveFile(sftpURL, opts);

		InputStream fin = localFile.getContent().getInputStream();
		OutputStream fout = remoteFile.getContent().getOutputStream();

		IOUtils.copyLarge(fin, fout);

		resultStatus = true;

		if (log.isDebugEnabled()) {
			log.info("File copying completed..." + filename.toString());
		}

		return resultStatus;
	}
}
