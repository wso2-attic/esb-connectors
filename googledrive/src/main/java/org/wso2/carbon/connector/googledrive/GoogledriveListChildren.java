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
import java.util.HashMap;
import java.util.Map;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.model.ChildList;

/**
 * Class mediator which maps to <strong>/children</strong> endpoint's <strong>list</strong> method. Gets a
 * list of folders within the user's Drive or within a folder specified by a folder ID. Returns the retrieved
 * list of Folders as a Google Drive SDK ChildList resource in XML format and attaches to the message
 * context's envelope body, and stores an error message as a property on failure. Maps to the
 * <strong>listFolders</strong> Synapse template within the <strong>Google Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/children/list
 */
public class GoogledriveListChildren extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        String folderId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FOLDER_ID);
        Map<String, String> optParam = new HashMap<String, String>();
        optParam.put(GoogleDriveUtils.StringConstants.MAX_RESULTS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.MAX_RESULTS));
        optParam.put(GoogleDriveUtils.StringConstants.PAGE_TOKEN,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PAGE_TOKEN));
        optParam.put(GoogleDriveUtils.StringConstants.Q,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.Q));
        optParam.put(GoogleDriveUtils.StringConstants.FIELDS,
                (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS));
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        try {
            
            ChildList childrenList;
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            childrenList = retrieveListOfChildren(service, folderId, optParam);
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.CHILD_REFERENCE, childrenList.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_LISTFOLDERS,
                    GoogleDriveUtils.StringConstants.LIST_CHILDREN_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Error listing folders:", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Error listing folders: ", ioe, messageContext);
        } catch (GeneralSecurityException gse) {
            
            log.error("Google Drive authentication failure:", gse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, gse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_GENERAL_SECURITY_EXCEPTION);
            handleException("Google Drive authentication failure.: ", gse, messageContext);
        } catch (ValidationException ve) {
            log.error("Failed to validate integer parameter.", ve);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ve,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
            handleException("Failed to validate integer parameter.", ve, messageContext);
        }
    }
    
    /**
     * Retrieves a list of children for a given folder and returns them as a ChildList resource.
     * 
     * @param service Drive service object
     * @param folderId ID of the folder of which the children should be returned
     * @param optParam optional parameter hashmap
     * @return List of Child objects
     * @throws IOException If an error occur on Google Drive API end.
     * @throws TokenResponseException If receiving an error response from
     *         the token server.
     */
    private ChildList retrieveListOfChildren(final Drive service, final String folderId,
            final Map<String, String> optParam) throws IOException, ValidationException, TokenResponseException {
    
        Children.List request = service.children().list(folderId);
        String temporaryValue = optParam.get(GoogleDriveUtils.StringConstants.MAX_RESULTS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setMaxResults(GoogleDriveUtils.toInteger(temporaryValue));
        }
        
        temporaryValue = optParam.get(GoogleDriveUtils.StringConstants.PAGE_TOKEN);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setPageToken(temporaryValue);
        }
        
        temporaryValue = optParam.get(GoogleDriveUtils.StringConstants.Q);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setQ(temporaryValue);
        }
        temporaryValue = optParam.get(GoogleDriveUtils.StringConstants.FIELDS);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setFields(temporaryValue);
        }
        
        return request.execute();
        
    }
    
}
