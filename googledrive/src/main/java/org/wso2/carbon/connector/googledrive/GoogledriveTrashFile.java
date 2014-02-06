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
import com.google.api.services.drive.model.File;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>trash</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/trash
 */
public class GoogledriveTrashFile extends AbstractConnector implements Connector {
    
    /**
     * Modify request body before sending to the end point.
     * 
     * @param messageContext MessageContext - The message context.
     * @throws ConnectException if connection is failed.
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        OMElement trashedFileResult = null;
        try {
            
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            File trashedFile = trashFile(service, fileId);
            
            if (trashedFile != null) {
                
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.FILE, trashedFile.toPrettyString());
                
                trashedFileResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_TRASHFILE,
                                GoogleDriveUtils.StringConstants.TRASHED_FILE_RESULT, true, hashMapForResultEnvelope);
                messageContext.getEnvelope().getBody().addChild(trashedFileResult);
                
            }
            
        }
        
        catch (Exception e) {
            hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, e.getMessage());
            trashedFileResult =
                    GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_TRASHFILE,
                            GoogleDriveUtils.StringConstants.TRASHED_FILE_RESULT, false, hashMapForResultEnvelope);
            messageContext.getEnvelope().getBody().addChild(trashedFileResult);
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            
        }
        
    }
    
    /**
     * Move a file to the trash.
     * 
     * @param service Drive API service instance.
     * @param fileId ID of the file to trash.
     * @return The updated file if successful, {@code null} otherwise.
     */
    private File trashFile(Drive service, String fileId) throws IOException {
    
        return service.files().trash(fileId).execute();
        
    }
    
}
