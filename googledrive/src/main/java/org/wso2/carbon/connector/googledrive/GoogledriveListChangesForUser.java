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
import com.google.api.services.drive.Drive.Changes;
import com.google.api.services.drive.model.ChangeList;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>list</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/changes/list
 */
public class GoogledriveListChangesForUser extends AbstractConnector implements Connector {
    
    /** Represent the errorCode of the IOException . */
    private static String errorCode;
    
    /** Represent the EMPTY_STRING of optional parameter request . */
    private static final String EMPTY_STRING = "";
    
    /**
     * connect.
     * 
     * @param messageContext ESB messageContext.
     * @throws ConnectException if connection fails.
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        ChangeList changeList;
        OMElement changeListResult;
        
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(GoogleDriveUtils.StringConstants.INCLUDE_DELETED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.INCLUDE_DELETED));
        parameters.put(GoogleDriveUtils.StringConstants.INCLUDE_SUBSCRIBED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.INCLUDE_SUBSCRIBED));
        parameters.put(GoogleDriveUtils.StringConstants.MAX_RESULTS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.MAX_RESULTS));
        parameters.put(GoogleDriveUtils.StringConstants.PAGE_TOKEN,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PAGE_TOKEN));
        parameters.put(GoogleDriveUtils.StringConstants.START_CHANGE_ID,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.START_CHANGE_ID));
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        
        try {
            
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            
            changeList = getChangeList(service, parameters);
            
            if (changeList != null) {
                
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.CHANGE_LIST, changeList.toPrettyString());
                
                changeListResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_LISTCHANGESFORUSER,
                                GoogleDriveUtils.StringConstants.LIST_CHANGES_FOR_USER_RESULT, true,
                                hashMapForResultEnvelope);
            } else {
                
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, errorCode);
                changeListResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_LISTCHANGESFORUSER,
                                GoogleDriveUtils.StringConstants.LIST_CHANGES_FOR_USER_RESULT, false,
                                hashMapForResultEnvelope);
            }
            messageContext.getEnvelope().getBody().addChild(changeListResult);
            
        } catch (Exception e) {
            
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
    }
    
    /**
     * Returns a list of changes to a file
     * 
     * @param service Drive API service instance.
     * @param params HashMap containing parameters for the request
     * @return <strong>List</strong> of changes
     */
    private ChangeList getChangeList(Drive service, HashMap<String, String> params) {
    
        try {
            
            Changes.List request = service.changes().list();
            String temporaryResult = params.get(GoogleDriveUtils.StringConstants.INCLUDE_DELETED);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                request.setIncludeDeleted(Boolean.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = params.get(GoogleDriveUtils.StringConstants.INCLUDE_SUBSCRIBED);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                request.setIncludeSubscribed(Boolean.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = params.get(GoogleDriveUtils.StringConstants.MAX_RESULTS);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                request.setMaxResults(Integer.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = params.get(GoogleDriveUtils.StringConstants.PAGE_TOKEN);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                request.setPageToken(temporaryResult);
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = params.get(GoogleDriveUtils.StringConstants.START_CHANGE_ID);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                request.setStartChangeId(Long.valueOf(temporaryResult));
            }
            
            return request.execute();
            
        } catch (IOException e) {
            
            errorCode = e.getMessage();
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
        }
        
        return null;
    }
}

