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
import com.google.api.services.drive.model.PermissionList;

/**
 * Class mediator which maps to <strong>/permissions</strong> endpoint's <strong>list</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/permissions/list
 */
public class GoogledriveListFilePermissions extends AbstractConnector implements Connector {
    
    /** Represent the errorCode of the IOException . */
    private String errorCode;
    
    /**
     * Modify request body before sending to the end point.
     * 
     * @param messageContext MessageContext - The message context.
     * @throws ConnectException if connection is failed.
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        PermissionList permissionList;
        OMElement permissionListResult;
        
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            
            permissionList = retrievePermissions(service, fileId);
            
            OMElement temporaryResultGetPermissionList =
                    GoogleDriveUtils.buildResultEnvelope(
                            GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_GETPERMISSIONLIST,
                            GoogleDriveUtils.StringConstants.GET_PERMISSIONS_RESULT, false, hashMapForResultEnvelope);
            
            if (permissionList != null) {
                
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.PERMISSION,
                        permissionList.toPrettyString());
                permissionListResult = temporaryResultGetPermissionList;
                
            } else {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, errorCode);
                permissionListResult = temporaryResultGetPermissionList;
            }
            
            messageContext.getEnvelope().getBody().addChild(permissionListResult);
            
        } catch (Exception e) {
            
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
    }
    
    /**
     * Retrieve a list of permissions.
     * 
     * @param service Drive API service instance.
     * @param fileId ID of the file to retrieve permissions for.
     * @return List of permissions.
     */
    private PermissionList retrievePermissions(Drive service, String fileId) {
    
        try {
            
            return service.permissions().list(fileId).execute();
            
        } catch (Exception e) {
            
            errorCode = e.getMessage();
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            return null;
        }
        
    }
    
}
