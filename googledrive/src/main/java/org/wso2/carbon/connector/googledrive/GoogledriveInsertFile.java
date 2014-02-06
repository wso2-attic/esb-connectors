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

import java.util.Arrays;
import java.util.HashMap;

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
import com.google.api.services.drive.model.ParentReference;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>insert</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/insert
 */
public class GoogledriveInsertFile extends AbstractConnector implements Connector {
    
    /** Represent the EMPTY_STRING of optional parameter request . */
    private static final String EMPTY_STRING = "";
    
    /**
     * connect.
     * 
     * @param messageContext ESB messageContext.
     * @throws ConnectException if connection fails.
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        OMElement insertedFileResult = null;
        
        String fileContentBaseSixtyFour =
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_CONTENT);
        String title = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TITLE);
        String description = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.DESCRIPTION);
        String parentId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PARENT_ID);
        String mimeType = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.MIME_TYPE);
        String fileName = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_NAME);
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        
        try {
            
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            
            HashMap<String, String> parameters = getOptionalParams(messageContext);
            
            com.google.api.services.drive.model.File insertedFile =
                    insertFile(service, title, description, parentId, mimeType, fileName, fileContentBaseSixtyFour,
                            parameters);
            
            if (insertedFile != null) {
                
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.FILE, insertedFile.toPrettyString());
                
                insertedFileResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTFILE,
                                GoogleDriveUtils.StringConstants.INSERTED_FILE_RESULT, true, hashMapForResultEnvelope);
                messageContext.getEnvelope().getBody().addChild(insertedFileResult);
                
            }
            
        }
        
        catch (Exception e) {
            hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, e.getMessage());
            insertedFileResult =
                    GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTFILE,
                            GoogleDriveUtils.StringConstants.INSERTED_FILE_RESULT, false, hashMapForResultEnvelope);
            
            messageContext.getEnvelope().getBody().addChild(insertedFileResult);
            
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            
        }
    }
    
    /**
     * Insert new file.
     * 
     * @param service Drive API service instance.
     * @param title Title of the file to insert, including the extension.
     * @param description Description of the file to insert.
     * @param parentId Optional parent folder's ID.
     * @param mimeType MIME type of the file to insert.
     * @param filename Filename of the file to insert.
     * @return Inserted file metadata if successful, {@code null} otherwise.
     */
    private com.google.api.services.drive.model.File insertFile(Drive service, String title, String description,
            String parentId, String mimeType, String filename, String fileContentBaseSixtyFour,
            HashMap<String, String> parameters) throws Exception {
    
        java.io.File writtenFile =
                GoogleDriveUtils.writeToTempFile(GoogleDriveUtils.StringConstants.TEMP_FILE_NAME,
                        fileContentBaseSixtyFour);
        
        // File's metadata.
        com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
        body.setTitle(title);
        body.setDescription(description);
        body.setMimeType(mimeType);
        
        // Set the parent folder.
        if (parentId != null && parentId.length() > 0) {
            body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
        }
        
        // File's content.
        java.io.File fileContent = writtenFile;
        FileContent mediaContent = new FileContent(mimeType, fileContent);
        
        Files.Insert insertRequest = service.files().insert(body, mediaContent);
        
        /*
         * setting optional parameters to the request
         */
        String temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.CONVERT);
        
        if (!EMPTY_STRING.equals(temporaryResult)) {
            
            insertRequest.setConvert(Boolean.valueOf(temporaryResult));
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.OCR);
        
        if (!EMPTY_STRING.equals(temporaryResult)) {
            
            insertRequest.setOcr(Boolean.valueOf(temporaryResult));
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.OCR_LANGUAGE);
        
        if (!EMPTY_STRING.equals(temporaryResult)) {
            
            insertRequest.setOcrLanguage((String) temporaryResult);
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.PINNED);
        
        if (!EMPTY_STRING.equals(temporaryResult)) {
            
            insertRequest.setPinned(Boolean.valueOf(temporaryResult));
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE);
        
        if (!EMPTY_STRING.equals(temporaryResult)) {
            
            insertRequest.setTimedTextLanguage((String) temporaryResult);
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME);
        
        if (!EMPTY_STRING.equals(temporaryResult)) {
            
            insertRequest.setTimedTextTrackName((String) temporaryResult);
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT);
        
        if (!EMPTY_STRING.equals(temporaryResult)) {
            
            insertRequest.setOcr(Boolean.valueOf(temporaryResult));
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.VISIBILITY);
        
        if (!EMPTY_STRING.equals(temporaryResult)) {
            
            insertRequest.setTimedTextTrackName((String) temporaryResult);
        }
        
        return insertRequest.execute();
        
    }
    
    private HashMap<String, String> getOptionalParams(MessageContext messageContext) {
    
        HashMap<String, String> parameters = new HashMap<String, String>();
        
        parameters.put(GoogleDriveUtils.StringConstants.CONVERT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CONVERT));
        parameters.put(GoogleDriveUtils.StringConstants.OCR,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.OCR));
        parameters.put(GoogleDriveUtils.StringConstants.OCR_LANGUAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.OCR_LANGUAGE));
        parameters.put(GoogleDriveUtils.StringConstants.PINNED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PINNED));
        parameters.put(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE));
        parameters.put(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME));
        parameters.put(GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT));
        parameters.put(GoogleDriveUtils.StringConstants.VISIBILITY,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.VISIBILITY));
        
        return parameters;
    }
}
