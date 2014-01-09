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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>list</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/list
 */
public class GoogledriveListFiles extends AbstractConnector implements Connector {
    
    /** Represent the errorCode of the IOException . */
    private static String errorCode;
    
    /** Represent the listFileResult of google drive utils . */
    private OMElement listFileResult;
    
    /** Represent the EMPTY_STRING of optional parameter request . */
    private static final String EMPTY_STRING = "";
    
    /**
     * connect.
     * 
     * @param messageContext ESB messageContext.
     * @throws ConnectException if connection fails.
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
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
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            List<File> files = retrieveListOfFiles(service, optParam);
            
            if (files != null) {
                
                for (int i = 0; i < files.size(); i++) {
                    
                    if (files.get(i) != null) {
                        
                        hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.FILE + i, files.get(i)
                                .toPrettyString());
                        listFileResult =
                                GoogleDriveUtils.buildResultEnvelope(
                                        GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_LISTFILE,
                                        GoogleDriveUtils.StringConstants.LIST_FILE_RESULT, true,
                                        hashMapForResultEnvelope);
                    }
                }
            } else {
                
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, errorCode);
                listFileResult =
                        GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_LISTFILE,
                                GoogleDriveUtils.StringConstants.LIST_FILE_RESULT, false, hashMapForResultEnvelope);
            }
            
            messageContext.getEnvelope().getBody().addChild(listFileResult);
            
        } catch (Exception e) {
            
            log.error(GoogleDriveUtils.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
    }
    
    /**
     * Retrieve a list of File resources according to optional parameters passed.
     * 
     * @param service Drive API service instance.
     * @param optParam
     * @return List of File resources.
     */
    private List<File> retrieveListOfFiles(Drive service, HashMap<String, String> optParam) {
    
        List<File> result = new ArrayList<File>();
        
        try {
            
            Files.List request = service.files().list();
            
            String temporaryResult = optParam.get(GoogleDriveUtils.StringConstants.MAX_RESULTS);
            
            if (!EMPTY_STRING.equals(temporaryResult)) {
                
                request.setMaxResults(Integer.valueOf(temporaryResult));
            }
            
            temporaryResult = EMPTY_STRING;
            temporaryResult = optParam.get(GoogleDriveUtils.StringConstants.MAX_RESULTS);
            
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
            
            FileList files = request.execute();
            result.addAll(files.getItems());
            
        } catch (Exception e) {
            
            errorCode = e.getMessage();
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            return null;
        }
        
        return result;
    }
    
}
