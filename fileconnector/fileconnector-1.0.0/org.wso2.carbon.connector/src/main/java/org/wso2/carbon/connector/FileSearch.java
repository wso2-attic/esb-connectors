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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FTPSiteUtils;
import org.wso2.carbon.connector.util.FilePattenMatcher;
import org.wso2.carbon.connector.util.ResultPayloadCreater;

public class FileSearch extends AbstractConnector implements Connector {

	private static Log log = LogFactory.getLog(FileSearch.class);

	public void connect(MessageContext messageContext) throws ConnectException {
		String fileLocation =
		                      getParameter(messageContext, "filelocation") == null ? "" : getParameter(
		                                                                                               messageContext,
		                                                                                               "filelocation").toString();

		String filepattern =
		                     getParameter(messageContext, "filepattern") == null ? "" : getParameter(
		                                                                                             messageContext,
		                                                                                             "filepattern").toString();
		String dirpattern =
		                    getParameter(messageContext, "dirpattern") == null ? "" : getParameter(
		                                                                                           messageContext,
		                                                                                           "dirpattern").toString();

		boolean searchInLocal =
		                        getParameter(messageContext, "searchinlocal") == null ? false : Boolean.getBoolean(getParameter(
		                                                                                                                        messageContext,
		                                                                                                                        "searchinlocal").toString());
		if (log.isDebugEnabled()) {
			log.info("File pattern..." + filepattern.toString());
		}
		if (!searchInLocal) {

			StringBuffer sb = new StringBuffer();

			try {
				readFilesUsingFileSystem(fileLocation, filepattern, dirpattern, sb);
			} catch (FileSystemException e) {
				handleException("Error while searching a file: " + e.getMessage(), e,
						messageContext);
			}

			generateOutput(messageContext, sb);
			if (log.isDebugEnabled()) {
				log.info("File searching completed..." + filepattern.toString());
			}
		} else {

			File inputDirectory = new File(fileLocation.toString());
			final String FILE_PATTERN = filepattern;
			final String DIR_PATTERN = dirpattern;
			final IOFileFilter filesFilter = new IOFileFilter() {

				public boolean accept(File file, String s) {
					return file.isFile();
				}

				public boolean accept(File file) {
					return new FilePattenMatcher(FILE_PATTERN).validate(file.getName()
					                                                        .toLowerCase());
				}

			};

			final IOFileFilter dirsFilter = new IOFileFilter() {

				public boolean accept(File file, String s) {
					return file.isDirectory();
				}

				public boolean accept(File file) {
					return new FilePattenMatcher(DIR_PATTERN).validate(file.getName().toLowerCase());
				}

			};

			Collection fileList = FileUtils.listFiles(inputDirectory, filesFilter, dirsFilter);
			StringBuffer sb = new StringBuffer();
			sb.append("<result>");
			Iterator iterator = fileList.iterator();
			while (iterator.hasNext()) {
				File f = (File) iterator.next();

				sb.append("<file>" + f.getName() + "</file>");

			}
			sb.append("</result>");
			if (log.isDebugEnabled()) {
				log.info(sb.toString());
			}
			generateOutput(messageContext, sb);

		}

	}

	/**
	 * Generate the output
	 * 
	 * @param messageContext
	 * @param sb
	 */
	private void generateOutput(MessageContext messageContext, StringBuffer sb) {
		ResultPayloadCreater resultPayload = new ResultPayloadCreater();

		OMElement element;
		try {
			element = resultPayload.performSearchMessages(sb.toString());
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
	 * Generate the file search
	 * 
	 * @param fileLocation
	 * @param filepattern
	 * @param dirpattern
	 * @param sb
	 * @throws FileSystemException
	 */
	private void readFilesUsingFileSystem(String fileLocation, String filepattern,
	                                      String dirpattern, StringBuffer sb)
	                                                                         throws FileSystemException {

		FileSystemOptions opts = FTPSiteUtils.createDefaultOptions();
		FileSystemManager manager = VFS.getManager();

		FileObject remoteFile = manager.resolveFile(fileLocation, opts);
		FileObject[] children = remoteFile.getChildren();

		final String FILE_PATTERN = filepattern;
		final String DIR_PATTERN = dirpattern;

		sb.append("<result><filelist>");
		for (int i = 0; i < children.length; i++) {
			if (children[i].getType().toString().equals("file") &&
			    new FilePattenMatcher(FILE_PATTERN).validate(children[i].getName().getBaseName()
			                                                            .toLowerCase())) {
				sb.append("<file>" + children[i].getName().getBaseName() + "</file>");
				if (log.isDebugEnabled()) {
					log.info(children[i].getName().getBaseName());
				}
			} else if (children[i].getType().toString().equals("folder") &&
			           new FilePattenMatcher(DIR_PATTERN).validate(children[i].getName()
			                                                                  .getBaseName()
			                                                                  .toLowerCase())) {
				sb.append("<dir>" + children[i].getName().getBaseName() + "</dir>");
				if (log.isDebugEnabled()) {
					log.info(children[i].getName().getBaseName());
				}
			}
		}
		sb.append("</filelist></result>");

	}

}
