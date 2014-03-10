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

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>trash</strong> method. Adds a file
 * specified by file ID to the Google Drive account's trash. Returns the trashed file as a Google Drive SDK
 * File resource in XML format and attaches to the message context's envelope body, and stores an error
 * message as a property on failure. Maps to the <strong>trashFile</strong> Synapse template within the
 * <strong>Google Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/trash
 */
public class GoogledriveTrashFile extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        String fields = (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS);
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        try {
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            Files.Trash request = service.files().trash(fileId);
            if (fields != null && !fields.isEmpty()) {
                request.setFields(fields);
            }
            File trashedFile = request.execute();
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.FILE, trashedFile.toPrettyString());
            
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_TRASHFILE,
                    GoogleDriveUtils.StringConstants.TRASHED_FILE_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Error trashing file:", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Error trashing file: ", ioe, messageContext);
            
        } catch (GeneralSecurityException gse) {
            
            log.error("Google Drive authentication failure:", gse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, gse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_GENERAL_SECURITY_EXCEPTION);
            handleException("Google Drive authentication failure: ", gse, messageContext);
        }
        
    }
    
}
