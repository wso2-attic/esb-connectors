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
import com.google.api.services.drive.model.Channel;
/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>watch</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/watch
 */
public class GoogledriveWatchFile extends AbstractConnector implements Connector {
    
    /** Represent the watchResult . */
    private Channel watchResult = null;
    
    /** Represent the errorCode . */
    private static String errorCode;
    
    /** Represent the watchFileResult . */
    private OMElement watchFileResult;
    
    /** Represent the emptyString . */
    private static final String EMPTY_STRING = "";
    
    /**
     * Returns body for response SOAP envelope
     * 
     * @param messageContext value for message context.
     * @throws ConnectException if an ConnectException error occurs
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        HashMap<String, String> parameters = new HashMap<String, String>();
        
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
        parameters.put(GoogleDriveUtils.StringConstants.CHANNEL_ID,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CHANNEL_ID));
        
        parameters.put(GoogleDriveUtils.StringConstants.CHANNEL_ADDRESS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CHANNEL_ADDRESS));
        
        parameters.put(GoogleDriveUtils.StringConstants.CHANNEL_TYPE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CHANNEL_TYPE));
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            watchResult = watchFile(service, fileId, parameters);
            
            if (watchResult != null) {
                
                watchFileResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_WATCHFILE,
                                GoogleDriveUtils.StringConstants.WATCH_FILE_RESULT, true, hashMapForResultEnvelope);
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.FILE, watchResult.toPrettyString());
                
            } else {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, errorCode);
                watchFileResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_WATCHFILE,
                                GoogleDriveUtils.StringConstants.WATCH_FILE_RESULT, false, hashMapForResultEnvelope);
                
            }
            messageContext.getEnvelope().getBody().addChild(watchFileResult);
        } catch (Exception e) {
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
    }
    
    private Channel watchFile(Drive service, String fileId, HashMap<String, String> parameters) {
    
        Channel channel = new Channel();
        
        try {
            
            Files.Watch watchRequest = service.files().watch(fileId, channel);
            
            String temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.CHANNEL_ID);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                channel.setId((String) temporaryResult);
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.CHANNEL_ADDRESS);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                channel.setAddress((String) temporaryResult);
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = parameters.get(GoogleDriveUtils.StringConstants.CHANNEL_TYPE);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                channel.setType((String) temporaryResult);
            }
            
            return watchRequest.execute();
            
        } catch (Exception e) {
            errorCode = e.getMessage();
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            return null;
            
        }
        
    }
    
}
