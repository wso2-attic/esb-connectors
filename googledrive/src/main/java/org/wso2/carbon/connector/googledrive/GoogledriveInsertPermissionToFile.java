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
import com.google.api.services.drive.Drive.Permissions;
import com.google.api.services.drive.model.Permission;

/**
 * Class mediator which maps to <strong>/permissions</strong> endpoint's <strong>insert</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/permissions/insert
 */
public class GoogledriveInsertPermissionToFile extends AbstractConnector implements Connector {
    
    /** Represent the errorCode . */
    private static String errorCode;
    
    /** Represent the emptyString . */
    private static final String EMPTY_STRING = "";
    
    /**
     * Returns body for response SOAP envelope
     * 
     * @param messageContext value for message context.
     * @throws ConnectException if an ConnectException error occurs
     */
    
    public void connect(MessageContext messageContext) throws ConnectException {
    
        Permission insertedPermission;
        OMElement insertPermissionResult;
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
        HashMap<String, String> parameters = new HashMap<String, String>();
        
        parameters.put(GoogleDriveUtils.StringConstants.EMAIL_MESSAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.EMAIL_MESSAGE));
        parameters.put(GoogleDriveUtils.StringConstants.SEND_NOTIFICATION_EMAILS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.SEND_NOTIFICATION_EMAILS));
        String role = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.ROLE);
        String type = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TYPE);
        String value = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.VALUE);
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            
            insertedPermission = insertPermission(service, fileId, value, type, role, parameters);
            if (insertedPermission != null) {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.PERMISSION,
                        insertedPermission.toPrettyString());
                insertPermissionResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTPERMISSION,
                                GoogleDriveUtils.StringConstants.INSERT_PERMISSION_RESULT, true,
                                hashMapForResultEnvelope);
            } else {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, errorCode);
                insertPermissionResult =
                        GoogleDriveUtils
                                .buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTCOMMENT,
                                        GoogleDriveUtils.StringConstants.INSERT_COMMENT_RESULT, false,
                                        hashMapForResultEnvelope);
            }
            messageContext.getEnvelope().getBody().addChild(insertPermissionResult);
        } catch (Exception e) {
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
    }
    
    /**
     * Insert a new permission.
     * 
     * @param service Drive API service instance.
     * @param fileId ID of the file to insert permission for.
     * @param value User or group e-mail address, domain name or {@code null} "default" type.
     * @param type The value "user", "group", "domain" or "default".
     * @param role The value "owner", "writer" or "reader".
     * @return The inserted permission if successful, {@code null} otherwise.
     */
    private Permission insertPermission(Drive service, String fileId, String value, String type, String role,
            HashMap<String, String> params) throws Exception {
    
        Permission newPermission = new Permission();
        
        newPermission.setValue(value);
        newPermission.setType(type);
        newPermission.setRole(role);
        
        try {
            Permissions.Insert request = service.permissions().insert(fileId, newPermission);
            
            String temporaryResult = params.get(GoogleDriveUtils.StringConstants.EMAIL_MESSAGE);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                request.setEmailMessage((String) temporaryResult);
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = params.get(GoogleDriveUtils.StringConstants.SEND_NOTIFICATION_EMAILS);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                request.setSendNotificationEmails(Boolean.valueOf(temporaryResult));
            }
            
            return request.execute();
        }
        
        catch (Exception e) {
        	errorCode = e.getMessage();
            log.error(GoogleDriveUtils.getStackTraceAsString(e));
        }
        return null;
    }
    
}
