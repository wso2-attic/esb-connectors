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
import com.google.api.services.drive.model.ChildReference;

/**
 * Class mediator which maps to <strong>/children</strong> endpoint's <strong>insert</strong> method. Inserts
 * an existing file, specified by a File ID, into a folder in Google Drive specified by a folder ID. Returns
 * the inserted reference as a Google Drive SDK ChildReference resource in XML format and attaches to the
 * message context's envelope body, and stores an error message as a property on failure. Maps to the
 * <strong>insertFileToFolder</strong> Synapse template within the <strong>Google Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/children/insert
 */
public class GoogledriveInsertFileToFolder extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        ChildReference insertedChild;
        
        String folderId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FOLDER_ID);
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        String fields = (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS);
        
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        try {
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            insertedChild = insertFileIntoFolder(service, folderId, fileId, fields);
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.CHILD_REFERENCE, insertedChild.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTFILETOFOLDER,
                    GoogleDriveUtils.StringConstants.INSERT_FILE_TO_FOLDER_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Failed insert file to folder.", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Failed insert file to folder.", ioe, messageContext);
        } catch (GeneralSecurityException gse) {
            log.error("Google Drive authentication failure.", gse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, gse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_GENERAL_SECURITY_EXCEPTION);
            handleException("Google Drive authentication failure.", gse, messageContext);
        }
    }
    
    /**
     * Insert a file into a folder, and return inserted file as a ChildReference resource.
     * 
     * @param service Drive API service instance.
     * @param folderId ID of the folder to insert the file into
     * @param fileId ID of the file to insert.
     * @param fields Field selectors for the response.
     * @return The inserted child if successful, {@code null} otherwise.
     * @throws IOException If an error occur on Google Drive API end.
     * @throws TokenResponseException If receiving an error response from the token server.
     */
    private ChildReference insertFileIntoFolder(final Drive service, final String folderId, final String fileId,
            final String fields) throws IOException, TokenResponseException {
    
        ChildReference newChild = new ChildReference();
        newChild.setId(fileId);
        Children.Insert insertChildRequest = service.children().insert(folderId, newChild);
        if ( fields != null && !fields.isEmpty() ) {
            insertChildRequest.setFields(fields);
        }
        return insertChildRequest.execute();
        
    }
    
}
