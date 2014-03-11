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

import javax.xml.stream.XMLStreamException;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>patch</strong> method. Runs a patch
 * request on a file within Google Drive, specified by a file ID, thereby updating its metadata Returns the
 * patched file as a Google Drive SDK File resource in XML format and attaches to the message context's
 * envelope body, and stores an error message as a property on failure. Maps to the <strong>patchFile</strong>
 * Synapse template within the <strong>Google Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/patch
 */
public class GoogledrivePatchFile extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        // Represent the patched files.
        File patchedFile;
        
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(GoogleDriveUtils.StringConstants.CONVERT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CONVERT));
        parameters.put(GoogleDriveUtils.StringConstants.NEW_REVISION,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.NEW_REVISION));
        parameters.put(GoogleDriveUtils.StringConstants.OCR,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.OCR));
        parameters.put(GoogleDriveUtils.StringConstants.OCR_LANGUAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.OCR_LANGUAGE));
        parameters.put(GoogleDriveUtils.StringConstants.PINNED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PINNED));
        parameters.put(GoogleDriveUtils.StringConstants.SET_MODIFIED_DATE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.SET_MODIFIED_DATE));
        parameters.put(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE));
        parameters.put(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME));
        parameters.put(GoogleDriveUtils.StringConstants.UPDATE_VIEWED_DATE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.UPDATE_VIEWED_DATE));
        parameters.put(GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT));
        parameters.put(GoogleDriveUtils.StringConstants.FIELDS,
                (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS));
        parameters.put(GoogleDriveUtils.StringConstants.FILE_RESOURCE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_RESOURCE));
        
        try {
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            patchedFile = patchFile(service, fileId, parameters);
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.FILE, patchedFile.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_PATCHFILE,
                    GoogleDriveUtils.StringConstants.PATCH_FILE_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Failed to patch file:", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Failed to patch file: ", ioe, messageContext);
        } catch (GeneralSecurityException gse) {
            
            log.error("Google Drive authentication failure:", gse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, gse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_GENERAL_SECURITY_EXCEPTION);
            handleException("Google Drive authentication failure: ", gse, messageContext);
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
     * Patch a file, thereby updating its metadata, and return the file itself as a File resource.
     * 
     * @param service Google Drive service object
     * @param fileId Id of the file to be patched
     * @param parameters HashMap containing parameters to be patched
     * @return File type resource
     * @throws IOException If an error occur on Google Drive API end.
     * @throws ValidationException If a validation error occurs.
     * @throws TokenResponseException If receiving an error response from
     *         the token server.
     * @throws XMLStreamException thrown when errors are encountered in building OM Element from String.
     */
    private File patchFile(final Drive service, final String fileId, final Map<String, String> parameters)
            throws IOException, ValidationException, TokenResponseException, XMLStreamException {
    
        File file = new File();
        String temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.FILE_RESOURCE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            Map<String, Object> fileResourceMap = GoogleDriveUtils.getUnkownKeyMap(temporaryValue);
            file.setUnknownKeys(fileResourceMap);
            
        }
        Files.Patch patchRequest = service.files().patch(fileId, file);
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.CONVERT);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setConvert(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.OCR);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setOcr(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.NEW_REVISION);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setNewRevision(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.OCR_LANGUAGE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setOcrLanguage(temporaryValue);
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.PINNED);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setPinned(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.SET_MODIFIED_DATE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setSetModifiedDate(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setTimedTextLanguage(temporaryValue);
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setTimedTextTrackName(temporaryValue);
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.UPDATE_VIEWED_DATE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setUpdateViewedDate(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setUseContentAsIndexableText(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.FIELDS);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            patchRequest.setFields(temporaryValue);
        }
        
        return patchRequest.execute();
        
    }
}
