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
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>get</strong> method. Gets a file
 * within Google Drive, as specified by a file ID. Returns the file as a Google Drive SDK File resource in XML
 * format and attaches to the message context's envelope body, and stores an error message as a property on
 * failure. Maps to the <strong>getFile</strong> Synapse template within the <strong>Google Drive</strong>
 * connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/get
 */
public class GoogledriveGetfile extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
        Map<String, String> parameters = new HashMap<String, String>();
        
        parameters.put(GoogleDriveUtils.StringConstants.UPDATE_VIEW_DATE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.UPDATE_VIEW_DATE));
        parameters.put(GoogleDriveUtils.StringConstants.FIELDS,
                (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS));
        
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        
        try {
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            File fileResult = getFileById(service, fileId, parameters);
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.FILE, fileResult.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_GETFILE,
                    GoogleDriveUtils.StringConstants.GET_FILE_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Error retrieving file.", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Error retrieving file.", ioe, messageContext);
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
     * Retrieve a File according to fileId passed and return it as a File resource.
     * 
     * @param service Drive API service instance
     * @param fileId ID of the file to insert comment for
     * @param parameters collection of optional parameters
     * @return File resource
     * @throws IOException If an error occur on Google Drive API end.
     * @throws ValidationException If a validation error occurs.
     * @throws TokenResponseException If receiving an error response from
     *         the token server.
     */
    private File getFileById(final Drive service, final String fileId, final Map<String, String> parameters)
            throws IOException, ValidationException, TokenResponseException {
    
        Files.Get fileResult = service.files().get(fileId);
        String temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.UPDATE_VIEW_DATE);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            fileResult.setUpdateViewedDate(GoogleDriveUtils.toBoolean(temporaryValue));
            
        }
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.FIELDS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            fileResult.setFields(temporaryValue);
            
        }
        
        return fileResult.execute();
    }
    
}
