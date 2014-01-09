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
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.model.ChildList;

/**
 * Class mediator which maps to <strong>/children</strong> endpoint's <strong>list</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/children/list
 */
public class GoogledriveListFolders extends AbstractConnector implements Connector {
    
    /** Error Code. */
    private String errorCode;
    
    /** Empty String. */
    private static final String EMPTY_STRING = "";
    
    /**
     * Connect method for class mediator.
     * 
     * @param messageContext the context of the OMElement
     * @throws ConnectException if connection fails.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        String folderId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FOLDER_ID);
        HashMap<String, String> optParam = new HashMap<String, String>();
        optParam.put(GoogleDriveUtils.StringConstants.MAX_RESULTS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.MAX_RESULTS));
        optParam.put(GoogleDriveUtils.StringConstants.PAGE_TOKEN,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PAGE_TOKEN));
        optParam.put(GoogleDriveUtils.StringConstants.Q,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.Q));
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            OMElement listFoldersResult;
            ChildList childrenList;
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            childrenList = retrieveListOfChildren(service, folderId, optParam);
            
            if (childrenList != null) {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.CHILD_REFERENCE,
                        childrenList.toPrettyString());
                listFoldersResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_LISTFOLDERS,
                                GoogleDriveUtils.StringConstants.LIST_FOLDERS_RESULT, false, hashMapForResultEnvelope);
            } else {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, errorCode);
                listFoldersResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_LISTFOLDERS,
                                GoogleDriveUtils.StringConstants.LIST_FOLDERS_RESULT, false, hashMapForResultEnvelope);
            }
            messageContext.getEnvelope().getBody().addChild(listFoldersResult);
            
        } catch (Exception e) {
            log.error(GoogleDriveUtils.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
    }
    
    /**
     * Retrieves a list of children for a given folder.
     * 
     * @param service Drive service object
     * @param folderId ID of the folder of which the children should be returned
     * @param optParam optional parameter hashmap
     * @return List of Child objects
     */
    private ChildList retrieveListOfChildren(Drive service, String folderId, HashMap<String, String> optParam) {
    
        try {
            Children.List request = service.children().list(folderId);
            String temporaryResult = optParam.get(GoogleDriveUtils.StringConstants.MAX_RESULTS);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                request.setMaxResults(Integer.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = optParam.get(GoogleDriveUtils.StringConstants.PAGE_TOKEN);
            if (!EMPTY_STRING.equals(temporaryResult)) {
                request.setPageToken(temporaryResult);
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = optParam.get(GoogleDriveUtils.StringConstants.Q);
            if (!EMPTY_STRING.equals(temporaryResult)) {
                request.setQ(temporaryResult);
            }
            
            return request.execute();
            
        } catch (Exception e) {
            errorCode = e.getMessage();
            log.error("Error: " + errorCode);
            return null;
        }
        
    }
    
}
