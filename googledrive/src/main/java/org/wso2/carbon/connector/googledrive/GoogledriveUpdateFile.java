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
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>update</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/update
 */
public class GoogledriveUpdateFile extends AbstractConnector implements Connector {
    
    /** Empty String. */
    private static final String EMPTY_STRING = "";
    
    /**
     * Connect method for class mediator.
     * 
     * @param messageContext the context of the OMElement
     * @throws ConnectException if connection fails.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        /** Is Patch. */
        boolean isPatch = false;
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        String uploadType = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.UPLOAD_TYPE);
        String fileContentBase64 = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_CONTENT);
        
        String mimeType = "text/plain";
        if (EMPTY_STRING.equals(fileContentBase64)) {
            isPatch = true;
            mimeType = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.MIME_TYPE);
        }
        
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(GoogleDriveUtils.StringConstants.CONVERT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CONVERT));
        parameters.put(GoogleDriveUtils.StringConstants.NEW_REVISION,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.NEW_REVISION));
        parameters.put(GoogleDriveUtils.StringConstants.OCR,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.OCR));
        parameters.put(GoogleDriveUtils.StringConstants.OCR_LANGUAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.OCR_LANGUAGE));
        parameters.put(GoogleDriveUtils.StringConstants.PINNED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PINNED));
        parameters.put(GoogleDriveUtils.StringConstants.SET_MODIFIED_DATE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.SET_MODIFIED_DATE));
        parameters.put(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE));
        parameters.put(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME));
        parameters.put(GoogleDriveUtils.StringConstants.UPDATE_VIEWED_DATE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.UPDATE_VIEWED_DATE));
        parameters.put(GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT));
        parameters.put(GoogleDriveUtils.StringConstants.REQUEST_BODY,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.REQUEST_BODY));
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        OMElement updateFileResult;
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            
            java.io.File writtenFile = null;
            if (!isPatch) {
                writtenFile = GoogleDriveUtils.writeToTempFile("tempfile", fileContentBase64);
            }
            
            File updatedFile;
            updatedFile = updateFile(service, fileId, uploadType, mimeType, writtenFile, parameters, isPatch);
            
            if (updatedFile != null) {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.FILE, updatedFile.toPrettyString());
                updateFileResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_UPDATEFILE,
                                GoogleDriveUtils.StringConstants.UPDATE_FILE_RESULT, true, hashMapForResultEnvelope);
                messageContext.getEnvelope().getBody().addChild(updateFileResult);
                
            }
            
        } catch (Exception e) {
            hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, e.getMessage());
            updateFileResult =
                    GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_UPDATEFILE,
                            GoogleDriveUtils.StringConstants.UPDATE_FILE_RESULT, false, hashMapForResultEnvelope);
            messageContext.getEnvelope().getBody().addChild(updateFileResult);
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
        }
    }
    
    /**
     * Update an existing file's metadata and content.
     * 
     * @param service Drive API service instance.
     * @param fileId ID of the file to update.
     * @param newTitle New title for the file.
     * @param newDescription New description for the file.
     * @param newMimeType New MIME type for the file.
     * @param newFilename Filename of the new content to upload.
     * @param newRevision Whether or not to create a new revision for this file.
     * @return Updated file metadata if successful, {@code null} otherwise.
     */
    private File updateFile(Drive service, String fileId, String uploadType, String mimeType, java.io.File content,
            HashMap<String, String> parameters, boolean isPatch) throws IOException {
    
        Files files = service.files();
        File file = files.get(fileId).execute();
        if (isPatch) {
            // If it is a patch that is required (metadata update), we are going
            // to run a patch
            Files.Patch patchRequest = files.patch(fileId, file);
            
            String temporaryResult = parameters.get("convert");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                patchRequest.setConvert(Boolean.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get("newRevision");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                patchRequest.setNewRevision(Boolean.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get("ocr");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                patchRequest.setOcr(Boolean.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get("ocrLanguage");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                patchRequest.setOcrLanguage(temporaryResult);
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get("pinned");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                patchRequest.setPinned(Boolean.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get("setModifiedDate");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                patchRequest.setSetModifiedDate(Boolean.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get("timedTextLanguage");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                patchRequest.setTimedTextLanguage(temporaryResult);
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get("timedTextTrackName");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                patchRequest.setTimedTextTrackName(temporaryResult);
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get("updateViewedDate");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                patchRequest.setUpdateViewedDate(Boolean.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get("useContentAsIndexableText");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                patchRequest.setUseContentAsIndexableText(Boolean.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get("requestBody");
            if (!EMPTY_STRING.equals(temporaryResult)) {
                Gson gson = new Gson();
                Type hashmapCollectionType = new TypeToken<HashMap<String, String>>() {}.getType();
                Map<String, Object> requestMap = gson.fromJson(temporaryResult, hashmapCollectionType);
                patchRequest.setUnknownKeys(requestMap);
                
            }
            return patchRequest.execute();
            
        } else {
            // If it is not a patch that is required, we are going to run an
            // upload
            return service.files().update(fileId, file, new FileContent(mimeType, content)).execute();
        }
        
    }
    
}
