/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.ResultPayloadCreater;

public class FTPFileDownload extends AbstractConnector implements Connector {

	private static Log log = LogFactory.getLog(FileCreate.class);
	FTPClient ftp = null;

	public void connect(MessageContext messageContext) throws ConnectException {

		String host =
		              getParameter(messageContext, "host") == null ? "" : getParameter(
		                                                                               messageContext,
		                                                                               "host").toString();
		String username =
		                  getParameter(messageContext, "username") == null ? "" : getParameter(
		                                                                                       messageContext,
		                                                                                       "username").toString();
		String password =
		                  getParameter(messageContext, "password") == null ? "" : getParameter(
		                                                                                       messageContext,
		                                                                                       "password").toString();

		String filename =
		                  getParameter(messageContext, "file") == null ? "" : getParameter(
		                                                                                   messageContext,
		                                                                                   "file").toString();
		String content =
		                 getParameter(messageContext, "content") == null ? "" : getParameter(
		                                                                                     messageContext,
		                                                                                     "content").toString();
		String filelocation =
		                      getParameter(messageContext, "filelocation") == null ? "" : getParameter(
		                                                                                               messageContext,
		                                                                                               "filelocation").toString();
		String newfilelocation =
		                         getParameter(messageContext, "newfilelocation") == null ? "" : getParameter(
		                                                                                                     messageContext,
		                                                                                                     "newfilelocation").toString();
		if (log.isDebugEnabled()) {
			log.info("File creation started..." + filename.toString());
			log.info("Host name..." + host.toString());
			log.info("File content..." + content.toString());
		}

		boolean resultStatus = false;
		try {
			resultStatus = downloadFile(host, username, password, filelocation, newfilelocation);
		} catch (Exception e) {
			handleException(e.getMessage(), messageContext);
		} finally {
			disconnect();
		}

		ResultPayloadCreater resultPayload = new ResultPayloadCreater();

		generateResults(messageContext, resultStatus, resultPayload);

	}

	/**
	 * 
	 * @param messageContext
	 * @param resultStatus
	 * @param resultPayload
	 */
	private void generateResults(MessageContext messageContext, boolean resultStatus,
	                             ResultPayloadCreater resultPayload) {
		String responce = "<result><success>" + resultStatus + "</success></result>";
		OMElement element;
		try {
			element = resultPayload.performSearchMessages(responce);
			resultPayload.preparePayload(messageContext, element);
		} catch (XMLStreamException | IOException | JSONException e) {
			handleException(e.getMessage(), messageContext);
		}
	}

	/**
	 * 
	 * @param fileLocation
	 * @param filename
	 * @param newFileLocation
	 * @return
	 * @throws Exception
	 */
	private boolean downloadFile(String host, String username, String password,
	                             String fileLocation, String newFileLocation) throws Exception {
		boolean resultStatus = false;

		try {

			ftp = new FTPClient();
			ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
			int reply;
			ftp.connect(host);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new Exception("Exception in connecting to FTP Server");
			}
			ftp.login(username, password);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();

			try (FileOutputStream fos = new FileOutputStream(newFileLocation)) {
				this.ftp.retrieveFile(fileLocation, fos);
			}

			resultStatus = true;
			if (log.isDebugEnabled()) {
				log.info("File copying completed..." + fileLocation.toString());
			}

		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			resultStatus = false;

		}
		return resultStatus;
	}

	private void disconnect() {
		if (this.ftp.isConnected()) {
			try {
				this.ftp.logout();
				this.ftp.disconnect();
			} catch (IOException f) {

			}
		}
	}

}
