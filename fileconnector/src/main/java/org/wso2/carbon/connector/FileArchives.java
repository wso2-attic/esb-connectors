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
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.ArchiveType;
import org.wso2.carbon.connector.util.FileCompressUtil;
import org.wso2.carbon.connector.util.FilePattenMatcher;
import org.wso2.carbon.connector.util.ResultPayloadCreater;

public class FileArchives extends AbstractConnector implements Connector {

	private static String FILE_PATTERN = "[A-Za-z][a-z]+";
	private static String DIR_PATTERN = "[A-Za-z][a-z]+";
	private static Log log = LogFactory.getLog(FileArchives.class);

	public void connect(MessageContext messageContext) throws ConnectException {
		String fileLocation =
		                      getParameter(messageContext, "filelocation") == null ? "" : getParameter(
		                                                                                               messageContext,
		                                                                                               "filelocation").toString();
		String filename =
		                  getParameter(messageContext, "file") == null ? "" : getParameter(
		                                                                                   messageContext,
		                                                                                   "file").toString();
		String suffixs =
		                 getParameter(messageContext, "suffixs") == null ? "" : getParameter(
		                                                                                     messageContext,
		                                                                                     "suffixs").toString();
		String newFileName =
		                     getParameter(messageContext, "archivefilename") == null ? "" : getParameter(
		                                                                                                 messageContext,
		                                                                                                 "archivefilename").toString();

		String archiveType =
		                     getParameter(messageContext, "archivetype") == null ? "" : getParameter(
		                                                                                             messageContext,
		                                                                                             "archivetype").toString();

		String filepattern =
		                     getParameter(messageContext, "filepattern") == null ? "" : getParameter(
		                                                                                             messageContext,
		                                                                                             "filepattern").toString();
		String dirpattern =
		                    getParameter(messageContext, "dirpattern") == null ? "" : getParameter(
		                                                                                           messageContext,
		                                                                                           "dirpattern").toString();
		boolean archivedirectory =
		                           getParameter(messageContext, "archivedirectory") == null ? false : Boolean.parseBoolean(getParameter(
		                                                                                                                                messageContext,
		                                                                                                                                "archivedirectory").toString());
		if (log.isDebugEnabled()) {
			log.info("File creation started..." + filename.toString());
			log.info("File Location..." + fileLocation.toString());
			log.info("File content..." + suffixs.toString());
		}

		boolean resultStatus = false;

		File file = new File(newFileName.toString());

		File inputDirectory = new File(fileLocation.toString());

		File[] subdirs = inputDirectory.listFiles();
		Collection<File> fileList = new ArrayList<File>();
		if (suffixs.equals("")) {
			if (archivedirectory) {
				for (File f : subdirs) {
					fileList.add(f);
				}
			} else {
				fileList =
				           FileUtils.listFiles(inputDirectory, TrueFileFilter.INSTANCE,
				                               TrueFileFilter.INSTANCE);
			}

		} else {
			final String[] SUFFIX = suffixs.split(",".toString());// { "xls" };
			fileList = FileUtils.listFiles(inputDirectory, SUFFIX, true);
		}

		Collection<File> filteredList = new ArrayList<File>();
		if (filepattern.equals("") && dirpattern.equals("")) {
			filteredList = fileList;
		} else {
			if (!filepattern.equals("")) {
				FILE_PATTERN = filepattern;
			}
			if (!dirpattern.equals("")) {
				DIR_PATTERN = dirpattern;
			}
			for (File filterFile : fileList) {
				if (new FilePattenMatcher(FILE_PATTERN).validate(filterFile.getName())) {
					filteredList.add(filterFile);
				} else if (filterFile.isDirectory() &&
				           new FilePattenMatcher(DIR_PATTERN).validate(filterFile.getName())) {
					filteredList.add(filterFile);
				}
			}
		}
		try {
			if (archiveType.equals(ArchiveType.TAR_GZIP.toString())) {
				new FileCompressUtil().compressFiles(filteredList, file, ArchiveType.TAR_GZIP);
			} else {
				new FileCompressUtil().compressFiles(filteredList, file, ArchiveType.ZIP);
			}
			resultStatus = true;
		} catch (IOException e) {

			handleException(e.getMessage(), messageContext);
			log.error(e.getMessage());
			resultStatus = false;
		}
		generateResults(messageContext, resultStatus);
		if (log.isDebugEnabled()) {
			log.info("File archived......");
		}

	}

	/**
	 * Generate the results
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
			handleException(e.getMessage(), messageContext);
		}
	}
}
