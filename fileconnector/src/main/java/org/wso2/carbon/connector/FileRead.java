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

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.VFS;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FTPSiteUtils;
import org.wso2.carbon.connector.util.ResultPayloadCreater;

public class FileRead extends AbstractConnector implements Connector {

	private static final String CONTENT_STOP_TAG = "</content>";
	private static final String CONTENT_TAG = "<content>";
	private static final String END_TAG = "</result>";
	private static final String START_TAG = "<result>";

	public void connect(MessageContext messageContext) throws ConnectException {
		String filename =
		                  getParameter(messageContext, "file") == null ? "" : getParameter(
		                                                                                   messageContext,
		                                                                                   "file").toString();
		String content =
		                 getParameter(messageContext, "content") == null ? "" : getParameter(
		                                                                                     messageContext,
		                                                                                     "content").toString();
		String fileLocation =
		                      getParameter(messageContext, "filelocation") == null ? "" : getParameter(
		                                                                                               messageContext,
		                                                                                               "filelocation").toString();

		String encoding =
		                  getParameter(messageContext, "encoding") == null ? "" : getParameter(
		                                                                                       messageContext,
		                                                                                       "encoding").toString();

		if (log.isDebugEnabled()) {
			log.info("File read start with" + filename.toString());
		}

		StringBuilder sb = new StringBuilder();
		try {
			sb = readFile(filename, fileLocation, encoding);
		} catch (IOException e) {
			handleException(e.getMessage(), messageContext);
		}

		generateResults(messageContext, sb);

	}

	/**
	 * Generate the results
	 * 
	 * @param messageContext
	 * @param sb
	 */
	private void generateResults(MessageContext messageContext, StringBuilder sb) {
		ResultPayloadCreater resultPayload = new ResultPayloadCreater();

		OMElement element;
		try {
			element = resultPayload.performSearchMessages(sb.toString());
			resultPayload.preparePayload(messageContext, element);
		} catch (XMLStreamException | IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Read the file content
	 * 
	 * @param filename
	 * @param fileLocation
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	private StringBuilder readFile(String filename, String fileLocation, String encoding)
	                                                                                     throws IOException {

		InputStream in = null;
		StringBuilder sb = new StringBuilder();

		FileSystemManager manager = VFS.getManager();
		if (manager != null) {

			FileSystemOptions opts = FTPSiteUtils.createDefaultOptions();
			FileObject fileObj = manager.resolveFile(fileLocation + filename, opts);

			in = fileObj.getContent().getInputStream();

			sb.append(START_TAG);
			sb.append(CONTENT_TAG);
			if (!encoding.equals("")) {
				sb.append(IOUtils.toString(in, encoding));
			} else {
				sb.append(IOUtils.toString(in));
			}
			sb.append(CONTENT_STOP_TAG);
			sb.append(END_TAG);

			int length;
			while ((length = in.read()) != -1) {

				if (log.isDebugEnabled()) {
					log.info((char) length);
				}
			}

			if (fileObj != null) {
				fileObj.close();
			}

		}

		return sb;
	}
}
