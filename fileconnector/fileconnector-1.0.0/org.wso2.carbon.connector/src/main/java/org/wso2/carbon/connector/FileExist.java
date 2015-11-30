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

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FTPSiteUtils;
import org.wso2.carbon.connector.util.ResultPayloadCreater;

public class FileExist extends AbstractConnector implements Connector {

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
		String newFileName =
		                     getParameter(messageContext, "newfilename") == null ? "" : getParameter(
		                                                                                             messageContext,
		                                                                                             "newfilename").toString();

		String filebeforepprocess =
		                            getParameter(messageContext, "filebeforepprocess") == null ? "" : getParameter(
		                                                                                                           messageContext,
		                                                                                                           "filebeforepprocess").toString();

		boolean isFileExist = false;
		try {
			isFileExist = isFileExist(fileLocation, filename, content);
		} catch (FileSystemException e) {
			handleException(e.getMessage(),e, messageContext);
		}

		generateResults(messageContext, isFileExist);

	}

	/**
	 * Generate the result
	 * 
	 * @param messageContext
	 * @param isFileExist
	 */
	private void generateResults(MessageContext messageContext, boolean isFileExist) {
		ResultPayloadCreater resultPayload = new ResultPayloadCreater();
		String responce = "<result><fileexits>" + isFileExist + "</fileexits></result>";
		OMElement element;
		try {
			element = resultPayload.performSearchMessages(responce);
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
	 * Check is that file exists
	 * 
	 * @param fileLocation
	 * @param filename
	 * @param content
	 * @return
	 * @throws FileSystemException
	 */
	private boolean isFileExist(String fileLocation, String filename, String content)
	                                                                                 throws FileSystemException {
		boolean isFileExist = false;
		if (log.isDebugEnabled()) {
			log.info("File creation started..." + filename.toString());
			log.info("File Location..." + fileLocation.toString());
			log.info("File content..." + content.toString());
		}

		FileSystemManager manager = VFS.getManager();

		// Create remote object
		FileObject remoteFile =
		                        manager.resolveFile(fileLocation.toString() + filename.toString(),
		                                            FTPSiteUtils.createDefaultOptions());

		if (remoteFile.exists()) {

			isFileExist = true;
		}
		if (log.isDebugEnabled()) {
			log.info("File exist " + isFileExist);
		}
		return isFileExist;
	}
}
