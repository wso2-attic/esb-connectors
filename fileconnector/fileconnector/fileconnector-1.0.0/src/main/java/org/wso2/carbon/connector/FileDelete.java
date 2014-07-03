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

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public class FileDelete extends AbstractConnector implements Connector {

	private static Log log = LogFactory.getLog(FileCreate.class);

	public void connect(MessageContext messageContext) throws ConnectException {
		System.out.println("File deletion started...");
		String fileLocation =
		                      getParameter(messageContext, "filelocation") == null ? "" : getParameter(
		                                                                                               messageContext,
		                                                                                               "filelocation").toString();
		String filename =
		                  getParameter(messageContext, "file") == null ? "" : getParameter(
		                                                                                   messageContext,
		                                                                                   "file").toString();

		String filebeforepprocess =
		                            getParameter(messageContext, "filebeforeprocess") == null ? "" : getParameter(
		                                                                                                          messageContext,
		                                                                                                          "filebeforeprocess").toString();
		if (log.isDebugEnabled()) {
			log.info("File deletion started..." + filename.toString());
			log.info("File Location..." + fileLocation);
		}

		boolean resultStatus = false;
		try {
			resultStatus = deleteFile(fileLocation, filename, filebeforepprocess);
		} catch (FileSystemException e) {
			generateResults(messageContext, resultStatus);
		}

		generateResults(messageContext, resultStatus);

	}

	/**
	 * Generate the result
	 * 
	 * @param messageContext
	 * @param resultStatus
	 */
	private void generateResults(MessageContext messageContext, boolean resultStatus) {
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

	/**
	 * Delete the file
	 * 
	 * @param fileLocation
	 * @param filename
	 * @param filebeforepprocess
	 * @return
	 */
	private boolean deleteFile(String fileLocation, String filename, String filebeforepprocess)
	                                                                                           throws FileSystemException {

		boolean resultStatus = false;

		FileSystemOptions opts = FTPSiteUtils.createDefaultOptions();
		FileSystemManager manager = VFS.getManager();

		// Create remote object
		FileObject remoteFile = manager.resolveFile(fileLocation + filename, opts);
		if (!filebeforepprocess.equals("")) {
			FileObject fBeforeProcess = manager.resolveFile(filebeforepprocess + filename, opts);
			fBeforeProcess.copyFrom(remoteFile, Selectors.SELECT_SELF);
		}

		if (remoteFile.exists()) {
			remoteFile.delete();
			resultStatus = true;
			if (log.isDebugEnabled()) {
				log.info("Delete remote file success");
			}
		}

		return resultStatus;
	}
}
