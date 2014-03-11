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
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Permissions;
import com.google.api.services.drive.model.Permission;

/**
 * Class mediator which maps to <strong>/permissions</strong> endpoint's <strong>insert</strong> method. This
 * method will set the permission to the files in google drive. also this can configure to send notification
 * email if wants. permission type , user and value should be send as json array. Returns the newly created
 * permission as a Google Drive SDK Permission resource in XML format and attaches to the message context's
 * envelope body, and stores an error message as a property on failure. Maps to the
 * <strong>insertPermissionToFile</strong> Synapse template within the <strong>Google Drive</strong>
 * connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/permissions/insert
 */
public class GoogledriveInsertPermissionToFile extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        Permission insertedPermission;
        
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
        Map<String, String> parameters = new HashMap<String, String>();
        
        parameters.put(GoogleDriveUtils.StringConstants.EMAIL_MESSAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.EMAIL_MESSAGE));
        parameters.put(GoogleDriveUtils.StringConstants.SEND_NOTIFICATION_EMAILS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.SEND_NOTIFICATION_EMAILS));
        parameters.put(GoogleDriveUtils.StringConstants.ADDITIONAL_ROLES,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.ADDITIONAL_ROLES));
        parameters.put(GoogleDriveUtils.StringConstants.REQUEST_BODY,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.REQUEST_BODY));
        parameters.put(GoogleDriveUtils.StringConstants.FIELDS,
                (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS));
        
        Map<String, String> resultsEnvelopeMap = new HashMap<String, String>();
        try {
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            insertedPermission = insertPermission(service, fileId, parameters);
            
            resultsEnvelopeMap.put(GoogleDriveUtils.StringConstants.PERMISSION, insertedPermission.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTPERMISSION,
                    GoogleDriveUtils.StringConstants.INSERT_PERMISSION_RESULT, resultsEnvelopeMap));
        } catch (IOException ioe) {
            log.error("Error on insert permission.", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Error on insert permission.", ioe, messageContext);
        } catch (GeneralSecurityException gse) {
            log.error("Google Drive authentication failure.", gse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, gse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_GENERAL_SECURITY_EXCEPTION);
            handleException("Google Drive authentication failure.", gse, messageContext);
        } catch (ValidationException ve) {
            log.error("Failed to validate boolean parameter.", ve);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ve,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
            handleException("Failed to validate boolean parameter.", ve, messageContext);
        } catch (XMLStreamException xse) {
            log.error("Failed to parse OM Element.", xse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, xse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_XML_STREAM_PARSE_FAILURE);
            handleException("Failed to parse OM Element.", xse, messageContext);
        }
    }
    
    /**
     * Insert a new permission to a file specified by a file ID.
     * 
     * @param service Drive API service instance.
     * @param fileId ID of the file to insert permission for.
     * @param params Collection of optional parameters.
     * @return The inserted permission if successful.
     * @throws IOException If an error occur on Google Drive API end.
     * @throws ValidationException If a validation error occurs.
     * @throws TokenResponseException If receiving an error response from the token server.
     * @throws XMLStreamException thrown when errors are encountered in building OM Element from String.
     */
    private Permission insertPermission(final Drive service, final String fileId, final Map<String, String> params)
            throws IOException, ValidationException, TokenResponseException, XMLStreamException {
    
        Permission newPermission = new Permission();
        String temporaryValue = params.get(GoogleDriveUtils.StringConstants.REQUEST_BODY);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            Map<String, Object> requestBodyMap = GoogleDriveUtils.getUnkownKeyMap(temporaryValue);
            newPermission.setRole((String) requestBodyMap.get(GoogleDriveUtils.StringConstants.ROLE));
            newPermission.setType((String) requestBodyMap.get(GoogleDriveUtils.StringConstants.TYPE));
            newPermission.setValue((String) requestBodyMap.get(GoogleDriveUtils.StringConstants.VALUE));
        }
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.ADDITIONAL_ROLES);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            List<String> additionalRoleList = getRoleList(temporaryValue);
            
            newPermission.setAdditionalRoles(additionalRoleList);
            
        }
        Permissions.Insert request = service.permissions().insert(fileId, newPermission);
        
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.EMAIL_MESSAGE);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            request.setEmailMessage((String) temporaryValue);
        }
        
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.SEND_NOTIFICATION_EMAILS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            request.setSendNotificationEmails(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.WITH_LINK);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            newPermission.setWithLink(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.FIELDS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            request.setFields(temporaryValue);
        }
        return request.execute();
        
    }
    
    /**
     * Compiles a XML String in to a List structure.
     * 
     * @params String containing role data to set as role information.
     * @return Converted List Object.
     * @throws XMLStreamException thrown when errors are encountered in building OM Element from String.
     */
    private List<String> getRoleList(String params) throws XMLStreamException {
    
        List<String> list = new ArrayList<String>();
        OMElement roles = AXIOMUtil.stringToOM(params);
        Iterator<?> roleIterator = roles.getChildElements();
        while (roleIterator.hasNext()) {
            OMElement element = (OMElement) roleIterator.next();
            list.add(element.getText());
        }
        
        return list;
    }
    
}
