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
import com.google.api.services.drive.model.ChildReference;

/**
 * Class mediator which maps to <strong>/children</strong> endpoint's <strong>get</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/children/get
 */
public class GoogledriveGetChild extends AbstractConnector implements Connector {
    
    /**
     * Connect method for class mediator.
     * 
     * @param messageContext the context of the OMElement
     * @throws ConnectException if connection fails.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        String folderId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FOLDER_ID);
        String childId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CHILD_ID);
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        OMElement getChildResult;
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            ChildReference returnedChild;
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            returnedChild = getChild(service, folderId, childId);
            if (returnedChild != null) {
                hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.CHILD_REFERENCE,
                        returnedChild.toPrettyString());
                getChildResult =
                        GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_GETCHILD,
                                GoogleDriveUtils.StringConstants.GET_CHILD_RESULT, true, hashMapForResultEnvelope);
                messageContext.getEnvelope().getBody().addChild(getChildResult);
            }
            
        } catch (Exception e) {
            hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, e.getMessage());
            getChildResult =
                    GoogleDriveUtils.buildResultEnvelope(GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_GETCHILD,
                            GoogleDriveUtils.StringConstants.GET_CHILD_RESULT, false, hashMapForResultEnvelope);
            messageContext.getEnvelope().getBody().addChild(getChildResult);
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
        }
    }
    
    /**
     * Check if a file is in a specific folder.
     * 
     * @param service Drive API service instance.
     * @param folderId ID of the folder.
     * @param childId ID of the file.
     * @return A reference to the Child instance.
     */
    private ChildReference getChild(Drive service, String folderId, String childId) throws IOException {
    
        return service.children().get(folderId, childId).execute();
        
    }
}
