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
import com.google.api.services.drive.model.ChildReference;

/**
 * Class mediator which maps to <strong>/children</strong> endpoint's <strong>insert</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/children/insert
 */
public class GoogledriveInsertFileToFolder extends AbstractConnector implements Connector {
    
    /** Represent the error_code . */
    private static String errorCode;
    
    /**
     * Returns body for response SOAP envelope
     * 
     * @param messageContext value for message context.
     * @throws ConnectException if an ConnectException error occurs
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        OMElement insertFileToFolderResult;
        ChildReference insertedChild;
        
        String folderId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FOLDER_ID);
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            insertedChild = insertFileIntoFolder(service, folderId, fileId);
            if (insertedChild != null) {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.CHILD_REFERENCE,
                        insertedChild.toPrettyString());
                insertFileToFolderResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTFILETOFOLDER,
                                GoogleDriveUtils.StringConstants.INSERT_FILE_TO_FOLDER_RESULT, true,
                                hashMapForResultEnvelope);
            } else {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, errorCode);
                insertFileToFolderResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTFILETOFOLDER,
                                GoogleDriveUtils.StringConstants.INSERT_FILE_TO_FOLDER_RESULT, false,
                                hashMapForResultEnvelope);
            }
            messageContext.getEnvelope().getBody().addChild(insertFileToFolderResult);
        } catch (Exception e) {
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
    }
    
    /**
     * Insert a file into a folder.
     * 
     * @param service Drive API service instance.
     * @param folderId ID of the folder to insert the file into
     * @param fileId ID of the file to insert.
     * @return The inserted child if successful, {@code null} otherwise.
     */
    private ChildReference insertFileIntoFolder(Drive service, String folderId, String fileId) throws Exception {
    
        ChildReference newChild = new ChildReference();
        newChild.setId(fileId);
        try {
            return service.children().insert(folderId, newChild).execute();
            
        } catch (Exception e) {
            errorCode = e.getMessage();
            log.error(GoogleDriveUtils.getStackTraceAsString(e));
        }
        return null;
    }
    
}
