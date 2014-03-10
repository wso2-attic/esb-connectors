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
import com.google.api.services.drive.Drive.Changes;
import com.google.api.services.drive.model.ChangeList;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>list</strong> method. Gets a list
 * of changes by the user to a file in Google Drive specified by a file ID. Returns the retrieved list of
 * changes as a Google Drive SDK ChangeList resource in XML format and attaches to the message context's
 * envelope body, and stores an error message as a property on failure. Maps to the
 * <strong>listChangesForUser</strong> Synapse template within the <strong>Google Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/changes/list
 */
public class GoogledriveListChangesForUser extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        ChangeList changeList;
        
        Map<String, String> parameterMap = new HashMap<String, String>();
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        
        // Adding parameters to parameterMap
        parameterMap.put(GoogleDriveUtils.StringConstants.INCLUDE_DELETED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.INCLUDE_DELETED));
        parameterMap.put(GoogleDriveUtils.StringConstants.INCLUDE_SUBSCRIBED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.INCLUDE_SUBSCRIBED));
        parameterMap.put(GoogleDriveUtils.StringConstants.MAX_RESULTS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.MAX_RESULTS));
        parameterMap.put(GoogleDriveUtils.StringConstants.PAGE_TOKEN,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PAGE_TOKEN));
        parameterMap.put(GoogleDriveUtils.StringConstants.START_CHANGE_ID,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.START_CHANGE_ID));
        parameterMap.put(GoogleDriveUtils.StringConstants.FIELDS,
                (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS));
        
        try {
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            changeList = getChangeList(service, parameterMap);
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.CHANGE_LIST, changeList.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_LISTCHANGESFORUSER,
                    GoogleDriveUtils.StringConstants.LIST_CHANGES_FOR_USER_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Failed to retrieve changes.", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Failed to retrieve changes.", ioe, messageContext);
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
        }
    }
    
    /**
     * Returns a list of changes to a file as a ChangeList resource.
     * 
     * @param service Drive API service instance.
     * @param params HashMap containing parameters for the request
     * @return <strong>ChangeList</strong> of changes
     * @throws IOException If an error occur on Google Drive API end.
     * @throws ValidationException If a validation error occurs.
     * @throws TokenResponseException If receiving an error response from
     *         the token server.
     */
    private ChangeList getChangeList(final Drive service, final Map<String, String> params) throws IOException,
            ValidationException, TokenResponseException {
    
        // Get list of changes
        Changes.List request = service.changes().list();
        String temporaryValue;
        
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.INCLUDE_DELETED);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setIncludeDeleted(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.INCLUDE_SUBSCRIBED);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setIncludeSubscribed(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.MAX_RESULTS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setMaxResults(GoogleDriveUtils.toInteger(temporaryValue));
        }
        
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.PAGE_TOKEN);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setPageToken(temporaryValue);
        }
        
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.START_CHANGE_ID);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setStartChangeId(GoogleDriveUtils.toLong(temporaryValue));
        }
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.FIELDS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setFields(temporaryValue);
        }
        
        return request.execute();
        
    }
}
