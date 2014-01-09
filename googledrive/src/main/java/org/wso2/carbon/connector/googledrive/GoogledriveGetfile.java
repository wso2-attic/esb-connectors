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
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>get</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/get
 */
public class GoogledriveGetfile extends AbstractConnector implements Connector {
    
    /** Represent the errorCode . */
    private static String errorCode;
    
    /** Represent the getFileResult . */
    private OMElement getFileResult;
    
    /** Represent the emptyString . */
    private static final String EMPTY_STRING = "";
    
    /**
     * Returns body for response SOAP envelope
     * 
     * @param messageContext value for message context.
     * @throws ConnectException if an ConnectException error occurs
     */
    
    public void connect(MessageContext messageContext) throws ConnectException {
    
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
        HashMap<String, String> parameters = new HashMap<String, String>();
        
        parameters.put(GoogleDriveUtils.StringConstants.UPDATE_VIEW_DATE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.UPDATE_VIEW_DATE));
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            
            File fileResult = downloadFile(service, fileId, parameters);
            
            if (fileResult != null) {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.FILE, fileResult.toPrettyString());
                getFileResult =
                        GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_GETFILE,
                                GoogleDriveUtils.StringConstants.GET_FILE_RESULT, true, hashMapForResultEnvelope);
                
            } else {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, errorCode);
                getFileResult =
                        GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_GETFILE,
                                GoogleDriveUtils.StringConstants.GET_FILE_RESULT, false, hashMapForResultEnvelope);
                
            }
            messageContext.getEnvelope().getBody().addChild(getFileResult);
        } catch (Exception e) {
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
    }
    
    private File downloadFile(Drive service, String fileId, HashMap<String, String> parameters) throws Exception {
    
        try {
            Files.Get fileResult = service.files().get(fileId);
            String temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.UPDATE_VIEW_DATE);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                fileResult.setUpdateViewedDate(Boolean.valueOf(temporaryResult));
                
            }
            
            return fileResult.execute();
        } catch (Exception e) {
            errorCode = e.getMessage();
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            throw new ConnectException(e);
            
        }
        
    }
    
}
