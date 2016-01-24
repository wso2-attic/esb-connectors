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
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FTPSiteUtils;
import org.wso2.carbon.connector.util.ResultPayloadCreater;

public class FileAppend extends AbstractConnector implements Connector {

	private static final String DEFAULT_ENCODING = "UTF8";
	private static Log log = LogFactory.getLog(FileAppend.class);

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
		String ftpFileLocation =
		                         getParameter(messageContext, "ftpfilelocation") == null ? "" : getParameter(
		                                                                                                     messageContext,
		                                                                                                     "ftpfilelocation").toString();

		String filebeforepprocess =
		                            getParameter(messageContext, "filebeforepprocess") == null ? "" : getParameter(
		                                                                                                           messageContext,
		                                                                                                           "filebeforepprocess").toString();
		String fileafterprocsess =
		                           getParameter(messageContext, "fileafterprocsess") == null ? "" : getParameter(
		                                                                                                         messageContext,
		                                                                                                         "fileafterprocsess").toString();
		String encoding =
		                  getParameter(messageContext, "encoding") == null ? "" : getParameter(
		                                                                                       messageContext,
		                                                                                       "encoding").toString();
		int offset =
		             getParameter(messageContext, "offset") == null ? 0 : Integer.parseInt(getParameter(
		                                                                                                messageContext,
		                                                                                                "offset").toString());
		if (log.isDebugEnabled()) {
			log.info("File append start with" + filename.toString());
		}

		boolean resultStatus = false;
		try {
			resultStatus =
			               appendFile(fileLocation, filename, content, encoding,
			                          filebeforepprocess, fileafterprocsess, offset);
		} catch (IOException e) {
			handleException("Error while appending a file.", e, messageContext);
		}

		generateResult(messageContext, resultStatus);

	}

	/**
	 * Generate the result
	 * 
	 * @param messageContext
	 * @param resultStatus
	 */
	private void generateResult(MessageContext messageContext, boolean resultStatus) {
		ResultPayloadCreater resultPayload = new ResultPayloadCreater();
		String responce = "<result><success>" + resultStatus + "</success></result>";
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
	 * 
	 * Append the content to the existing file
	 * 
	 * @param fileLocation
	 * @param filename
	 * @param content
	 * @param filebeforepprocess
	 * @param fileafterprocsess
	 */
	private boolean appendFile(String fileLocation, String filename, String content,
	                           String encoding, String filebeforepprocess,
	                           String fileafterprocsess, int offset) throws IOException {

		OutputStream out = null;
		InputStream in = null;
		boolean resultStatus = false;

		FileSystemManager manager = VFS.getManager();
		// if the file does not exist, this method creates it
		FileSystemOptions opts = FTPSiteUtils.createDefaultOptions();
		FileObject fileObj = manager.resolveFile(fileLocation + filename, opts);

		if (!filebeforepprocess.equals("")) {
			FileObject fBeforeProcess = manager.resolveFile(filebeforepprocess + filename, opts);
			fBeforeProcess.copyFrom(fileObj, Selectors.SELECT_SELF);
			if (fBeforeProcess != null) {
				fBeforeProcess.close();
			}
		}

		out = fileObj.getContent().getOutputStream(true);

		if (encoding.equals("")) {
			IOUtils.write(content, out, DEFAULT_ENCODING);
		} else {
			IOUtils.write(content, out, encoding);
		}

		if (!fileafterprocsess.equals("")) {
			FileObject fAfterProcess = manager.resolveFile(fileafterprocsess + filename, opts);

			fAfterProcess.copyFrom(fileObj, Selectors.SELECT_SELF);
			if (fAfterProcess != null) {
				fAfterProcess.close();
			}
		}
		if (fileObj != null) {
			fileObj.close();
		}

		resultStatus = true;

		return resultStatus;
	}

}
