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
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>patch</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/patch
 */
public class GoogledrivePatchFile extends AbstractConnector implements Connector {
    
    /** Represent the EMPTY_STRING of optional parameter request . */
    private static final String EMPTY_STRING = "";
    
    /**
     * connect.
     * 
     * @param messageContext ESB messageContext.
     * @throws ConnectException if connection fails.
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        File patchedFile;
        OMElement patchFileResult;
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
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
        
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            
            patchedFile = patchFile(service, fileId, parameters);
            
            if (patchedFile != null) {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.FILE, patchedFile.toPrettyString());
                patchFileResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_PATCHFILE,
                                GoogleDriveUtils.StringConstants.PATCH_FILE_RESULT, true, hashMapForResultEnvelope);
                
                messageContext.getEnvelope().getBody().addChild(patchFileResult);
                
            }
            
        } catch (Exception e) {
            
            hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, e.getMessage());
            patchFileResult =
                    GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_PATCHFILE,
                            GoogleDriveUtils.StringConstants.PATCH_FILE_RESULT, false, hashMapForResultEnvelope);
            
            messageContext.getEnvelope().getBody().addChild(patchFileResult);
            
            log.error(GoogleDriveUtils.getStackTraceAsString(e));
        }
    }
    
    /**
     * Patch a file
     * 
     * @param service Google Drive service object
     * @param fileId Id of the file to be patched
     * @param parameters HashMap containing parameters to be patched
     * @return File type resource
     * @throws Exception
     */
    private File patchFile(Drive service, String fileId, HashMap<String, String> parameters) throws IOException {
    
        File file = new File();
        
        Files.Patch patchRequest = service.files().patch(fileId, file);
        
        String temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.CONVERT);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            patchRequest.setConvert(Boolean.valueOf(temporaryResult));
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.OCR);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            patchRequest.setOcr(Boolean.valueOf(temporaryResult));
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.NEW_REVISION);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            patchRequest.setNewRevision(Boolean.valueOf(temporaryResult));
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.OCR_LANGUAGE);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            patchRequest.setOcrLanguage(temporaryResult);
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.PINNED);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            patchRequest.setPinned(Boolean.valueOf(temporaryResult));
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.SET_MODIFIED_DATE);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            patchRequest.setSetModifiedDate(Boolean.valueOf(temporaryResult));
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            patchRequest.setTimedTextLanguage(temporaryResult);
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            patchRequest.setTimedTextTrackName(temporaryResult);
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.UPDATE_VIEWED_DATE);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            patchRequest.setUpdateViewedDate(Boolean.valueOf(temporaryResult));
        }
        
        temporaryResult = EMPTY_STRING;
        temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            patchRequest.setUseContentAsIndexableText(Boolean.valueOf(temporaryResult));
        }
        
        return patchRequest.execute();
        
    }
}
