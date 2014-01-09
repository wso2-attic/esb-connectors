/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.googledrive;

import java.io.IOException;
import java.util.HashMap;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's
 * <strong>copy</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/copy
 */
public class GoogledriveCopyFile extends AbstractConnector implements Connector {

	/** Represent the errorCode of the IOException . */
	private static final String EMPTY_STRING = "";

	/** Error code to be returned in SOAP envelope */
	private static String errorCode;
	/**
	 * connect.
	 * 
	 * @param messageContext
	 *            ESB messageContext.
	 * @throws ConnectException
	 *             if connection fails.
	 */
	public void connect(MessageContext messageContext) throws ConnectException {


		String fileId = (String) getParameter(messageContext,
				GoogleDriveUtils.StringConstants.FILE_ID);
		HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();

		try {
			OMElement copiedFileResult;
			HttpTransport httpTransport = new NetHttpTransport();
			JsonFactory jsonFactory = new JacksonFactory();

			Drive service = GoogleDriveUtils.getDriveService(messageContext,
					httpTransport, jsonFactory);

			HashMap<String, String> optionalParams = getOptionalParams(messageContext);

			File copiedFile = copyFile(service, fileId, optionalParams);

			if (copiedFile != null) {

				hashMapForResultEnvelope.put(
						GoogleDriveUtils.StringConstants.FILE,
						copiedFile.toPrettyString());

				copiedFileResult = GoogleDriveUtils
						.buildResultEnvelope(
								GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_COPYFILE,
								GoogleDriveUtils.StringConstants.COPIED_FILE_RESULT,
								true, hashMapForResultEnvelope);

			} else {

				hashMapForResultEnvelope.put(
						GoogleDriveUtils.StringConstants.ERROR, errorCode);
				copiedFileResult = GoogleDriveUtils
						.buildResultEnvelope(
								GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_COPYFILE,
								GoogleDriveUtils.StringConstants.COPIED_FILE_RESULT,
								false, hashMapForResultEnvelope);

			}
			messageContext.getEnvelope().getBody().addChild(copiedFileResult);

		}

		catch (Exception e) {
			log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
			throw new ConnectException(e);
		}

	}

	/**
	 * Copy an existing file.
	 * 
	 * @param service
	 *            Drive API service instance.
	 * @param originFileId
	 *            ID of the origin file to copy.
	 * @param copyTitle
	 *            Title of the copy.
	 * @return The copied file if successful, {@code null} otherwise.
	 */
	private File copyFile(Drive service, String originFileId,
			HashMap<String, String> parameters) throws IOException {

		String copyTitle = "";
		File copiedFile = new File();

		if (originFileId != null && !EMPTY_STRING.equals(originFileId)) {
			copyTitle = "Copy of "
					+ (service.files().get(originFileId).execute().getTitle());
		}

		copiedFile.setTitle(copyTitle);

		Files.Copy copyRequest = service.files().copy(originFileId, copiedFile);

		/*
		 * setting optional parameters to the request
		 */

		String temporaryResult = parameters
				.get(GoogleDriveUtils.StringConstants.CONVERT);

		if (!EMPTY_STRING.equals(temporaryResult)) {

			copyRequest.setConvert(Boolean.valueOf(temporaryResult));
		}

		temporaryResult = EMPTY_STRING;
		temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.OCR);

		if (!EMPTY_STRING.equals(temporaryResult)) {

			copyRequest.setOcr(Boolean.valueOf(temporaryResult));
		}

		temporaryResult = EMPTY_STRING;
		temporaryResult = parameters
				.get(GoogleDriveUtils.StringConstants.OCR_LANGUAGE);

		if (!EMPTY_STRING.equals(temporaryResult)) {

			copyRequest.setOcrLanguage((String) temporaryResult);
		}

		temporaryResult = EMPTY_STRING;
		temporaryResult = parameters
				.get(GoogleDriveUtils.StringConstants.PINNED);

		if (!EMPTY_STRING.equals(temporaryResult)) {

			copyRequest.setPinned(Boolean.valueOf(temporaryResult));
		}

		temporaryResult = EMPTY_STRING;
		temporaryResult = parameters
				.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE);

		if (!EMPTY_STRING.equals(temporaryResult)) {

			copyRequest.setTimedTextLanguage((String) temporaryResult);
		}

		temporaryResult = EMPTY_STRING;
		temporaryResult = parameters
				.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME);

		if (!EMPTY_STRING.equals(temporaryResult)) {

			copyRequest.setTimedTextTrackName((String) temporaryResult);
		}

		temporaryResult = EMPTY_STRING;
		temporaryResult = parameters
				.get(GoogleDriveUtils.StringConstants.VISIBILITY);

		if (!EMPTY_STRING.equals(temporaryResult)) {

			copyRequest.setTimedTextTrackName((String) temporaryResult);
		}

		copyRequest.setFields(copyTitle);
		try {
			return service.files().copy(originFileId, copiedFile).execute();
		} catch (Exception e) {
			errorCode = e.getMessage();
			log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
			return null;
		}

	}

	private HashMap<String, String> getOptionalParams(
			MessageContext messageContext) {

		HashMap<String, String> parameters = new HashMap<String, String>();

		parameters.put(
				GoogleDriveUtils.StringConstants.CONVERT,
				(String) getParameter(messageContext,
						GoogleDriveUtils.StringConstants.CONVERT));
		parameters.put(
				GoogleDriveUtils.StringConstants.OCR,
				(String) getParameter(messageContext,
						GoogleDriveUtils.StringConstants.OCR));
		parameters.put(
				GoogleDriveUtils.StringConstants.OCR_LANGUAGE,
				(String) getParameter(messageContext,
						GoogleDriveUtils.StringConstants.OCR_LANGUAGE));
		parameters.put(
				GoogleDriveUtils.StringConstants.PINNED,
				(String) getParameter(messageContext,
						GoogleDriveUtils.StringConstants.PINNED));
		parameters.put(
				GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE,
				(String) getParameter(messageContext,
						GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE));
		parameters.put(
				GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME,
				(String) getParameter(messageContext,
						GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME));
		parameters.put(
				GoogleDriveUtils.StringConstants.VISIBILITY,
				(String) getParameter(messageContext,
						GoogleDriveUtils.StringConstants.VISIBILITY));

		return parameters;
	}
}
