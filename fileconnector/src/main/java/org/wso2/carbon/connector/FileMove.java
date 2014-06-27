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
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.VFS;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FTPSiteUtils;
import org.wso2.carbon.connector.util.ResultPayloadCreater;

public class FileMove extends AbstractConnector implements Connector {

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
		String fileafterprocess =
		                          getParameter(messageContext, "fileafterprocess") == null ? "" : getParameter(
		                                                                                                       messageContext,
		                                                                                                       "fileafterprocess").toString();
		String newFileLocation =
		                         getParameter(messageContext, "newfilelocation") == null ? "" : getParameter(
		                                                                                                     messageContext,
		                                                                                                     "newfilelocation").toString();
		if (log.isDebugEnabled()) {
			log.info("File deletion started..." + filename.toString());
			log.info("File Location..." + fileLocation);
		}

		boolean resultStatus = false;
		try {
			resultStatus =
			               moveFile(fileLocation, filename, filebeforepprocess, fileafterprocess,
			                        newFileLocation);
		} catch (FileSystemException e) {
			handleException(e.getMessage(), messageContext);
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

		} catch (XMLStreamException | IOException | JSONException e) {
			log.error(e.getMessage());
			handleException(e.getMessage(), messageContext);

		}
	}

	/**
	 * Move the files
	 * 
	 * @param fileLocation
	 * @param filename
	 * @param filebeforepprocess
	 * @return
	 */
	private boolean moveFile(String fileLocation, String filename, String filebeforepprocess,
	                         String fileafterprocess, String newFileLocation)
	                                                                         throws FileSystemException {

		boolean resultStatus = false;

		FileSystemOptions opts = FTPSiteUtils.createDefaultOptions();
		FileSystemManager manager = VFS.getManager();
		// Create remote object
		FileObject remoteFile = manager.resolveFile(fileLocation + filename, opts);
		FileObject newFile = manager.resolveFile(newFileLocation + filename, opts);
		if (!filebeforepprocess.equals("")) {
			FileObject fBeforeProcess = manager.resolveFile(filebeforepprocess + filename, opts);
			fBeforeProcess.copyFrom(remoteFile, Selectors.SELECT_SELF);
		}

		if (remoteFile.exists()) {
			remoteFile.moveTo(newFile);
			resultStatus = true;
			if (log.isDebugEnabled()) {
				log.info("Move remote file success");
			}
		}

		if (!fileafterprocess.equals("")) {
			FileObject fAfterProcess = manager.resolveFile(fileafterprocess + filename, opts);
			fAfterProcess.copyFrom(newFile, Selectors.SELECT_SELF);
		}

		return resultStatus;
	}
}
