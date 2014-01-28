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

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>delete</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/delete
 */
public class GoogledriveDeleteFile extends AbstractConnector implements Connector {
    
    /**
     * connect.
     * 
     * @param messageContext ESB messageContext.
     * @throws ConnectException if connection fails.
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        OMElement deleteFileResult;
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            deleteFile(service, fileId);
            
            deleteFileResult =
                    GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_DELETEFILE,
                            GoogleDriveUtils.StringConstants.DELETE_FILE_RESULT, true, null);
            
            messageContext.getEnvelope().getBody().addChild(deleteFileResult);
            // All exceptions are being caught to pass to the ESB, so that the
            // ESB is notified of any
            // exception
        } catch (Exception e) {
            HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
            hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, e.getMessage());
            deleteFileResult =
                    GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_DELETEFILE,
                            GoogleDriveUtils.StringConstants.DELETE_FILE_RESULT, false, hashMapForResultEnvelope);
            messageContext.getEnvelope().getBody().addChild(deleteFileResult);
            log.error(GoogleDriveUtils.getStackTraceAsString(e));
            
        }
    }
    
    /**
     * Permanently delete a file, skipping the trash.
     * 
     * @param service Drive API service instance.
     * @param fileId ID of the file to delete.
     */
    private void deleteFile(Drive service, String fileId) throws IOException {
    
        service.files().delete(fileId).execute();
        
    }
}
