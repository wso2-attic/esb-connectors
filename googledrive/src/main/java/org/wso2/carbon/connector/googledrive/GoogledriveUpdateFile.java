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
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.File.IndexableText;
import com.google.api.services.drive.model.File.Labels;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Property;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>update</strong> method. Runs a
 * patch request on a file within Google Drive, specified by a file ID, thereby updating its metadata, or adds
 * new content to an existing file if so required. Returns the updated file as a Google Drive SDK File
 * resource in XML format and attaches to the message context's envelope body, and stores an error message as
 * a property on failure. Maps to the <strong>updateFile</strong> Synapse template within the <strong>Google
 * Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/update
 */
public class GoogledriveUpdateFile extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        String uploadType = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.UPLOAD_TYPE);
        
        Map<String, String> mandatoryParameters = new HashMap<String, String>();
        mandatoryParameters.put(GoogleDriveUtils.StringConstants.FILE_ID, fileId);
        mandatoryParameters.put(GoogleDriveUtils.StringConstants.UPLOAD_TYPE, uploadType);
        
        Map<String, String> optionalParameters = new HashMap<String, String>();
        optionalParameters.put(GoogleDriveUtils.StringConstants.CONVERT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CONVERT));
        optionalParameters.put(GoogleDriveUtils.StringConstants.NEW_REVISION,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.NEW_REVISION));
        optionalParameters.put(GoogleDriveUtils.StringConstants.OCR,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.OCR));
        optionalParameters.put(GoogleDriveUtils.StringConstants.OCR_LANGUAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.OCR_LANGUAGE));
        optionalParameters.put(GoogleDriveUtils.StringConstants.PINNED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PINNED));
        optionalParameters.put(GoogleDriveUtils.StringConstants.SET_MODIFIED_DATE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.SET_MODIFIED_DATE));
        optionalParameters.put(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE));
        optionalParameters.put(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME));
        optionalParameters.put(GoogleDriveUtils.StringConstants.UPDATE_VIEWED_DATE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.UPDATE_VIEWED_DATE));
        optionalParameters.put(GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT));
        optionalParameters.put(GoogleDriveUtils.StringConstants.FILE_RESOURCE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_RESOURCE));
        optionalParameters.put(GoogleDriveUtils.StringConstants.LABELS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.LABELS));
        optionalParameters.put(GoogleDriveUtils.StringConstants.PARENTS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PARENTS));
        optionalParameters.put(GoogleDriveUtils.StringConstants.PROPERTIES,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PROPERTIES));
        optionalParameters.put(GoogleDriveUtils.StringConstants.INDEXABLE_TEXT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.INDEXABLE_TEXT));
        optionalParameters.put(GoogleDriveUtils.StringConstants.FIELDS,
                (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS));
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        try {
            InputStream attachmentInStream = null;
            org.apache.axis2.context.MessageContext axis2mc =
                    ((Axis2MessageContext) messageContext).getAxis2MessageContext();
            
            DataHandler dataHandler = axis2mc.getAttachment(GoogleDriveUtils.StringConstants.FILE);
            
            if (dataHandler != null) {
                
                attachmentInStream = dataHandler.getInputStream();
            }
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            File updatedFile;
            updatedFile = updateFile(service, mandatoryParameters, attachmentInStream, optionalParameters);
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.FILE, updatedFile.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_UPDATEFILE,
                    GoogleDriveUtils.StringConstants.UPDATE_FILE_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Error updating file.", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Error updating file.", ioe, messageContext);
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
        } catch (XMLStreamException xse) {
            log.error("Failed to parse OM Element.", xse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, xse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_XML_STREAM_PARSE_FAILURE);
            handleException("Failed to parse OM Element.", xse, messageContext);
        }
    }
    
    /**
     * Updates a file's metadata or content and returns a file resource.
     * 
     * @param service Google Drive SDK service object
     * @param mandatoryParams Mandatory parameters for the request
     * @param fileContentStream InputStream containing file content
     * @param optionalParams Optional parameters for the request
     * @return Google Drive SDK File resource
     * @throws IOException If an error occur on Google Drive API end.
     * @throws ValidationException If a validation error occurs.
     * @throws TokenResponseException If receiving an error response from the token server.
     * @throws XMLStreamException If an error occurs during parsing string as XML.
     */
    private File updateFile(final Drive service, final Map<String, String> mandatoryParams,
            final InputStream fileContentStream, final Map<String, String> optionalParams) throws IOException,
            ValidationException, TokenResponseException, XMLStreamException {
    
        String fileId = mandatoryParams.get(GoogleDriveUtils.StringConstants.FILE_ID);
        String mimeType = null;
        String uploadType = mandatoryParams.get(GoogleDriveUtils.StringConstants.UPLOAD_TYPE);
        File file = new File();
        String temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.FILE_RESOURCE);
        if (uploadType != null && !uploadType.equals(GoogleDriveUtils.StringConstants.MEDIA)) {
            if (temporaryValue != null && !temporaryValue.isEmpty()) {
                
                Map<String, Object> fileResourceMap = GoogleDriveUtils.getUnkownKeyMap(temporaryValue);
                mimeType = (String) fileResourceMap.get(GoogleDriveUtils.StringConstants.MIME_TYPE);
                file.setUnknownKeys(fileResourceMap);
                
            }
            temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.LABELS);
            
            if (temporaryValue != null && !temporaryValue.isEmpty()) {
                
                Map<String, Object> labelsMap = GoogleDriveUtils.getUnkownKeyMap(temporaryValue);
                Labels labels = new Labels();
                labels.setUnknownKeys(labelsMap);
                file.setLabels(labels);
            }
            
            temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.PARENTS);
            
            if (temporaryValue != null && !temporaryValue.isEmpty()) {
                
                List<ParentReference> parentReferences = GoogleDriveUtils.getParentReferenceList(temporaryValue);
                file.setParents(parentReferences);
                
            }
            
            temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.PROPERTIES);
            
            if (temporaryValue != null && !temporaryValue.isEmpty()) {
                
                List<Property> properties = GoogleDriveUtils.getPropertyList(temporaryValue);
                file.setProperties(properties);
                
            }
            
            temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.INDEXABLE_TEXT);
            
            if (temporaryValue != null && !temporaryValue.isEmpty()) {
                
                IndexableText indexableText = new IndexableText();
                indexableText.setText(temporaryValue);
                file.setIndexableText(indexableText);
                
            }
        }
        File resultFile;
        Files.Update updateRequest = null;
        if (fileContentStream == null) {
            
            updateRequest = service.files().update(fileId, file);
            
        } else {
            // If it is not a patch that is required, we are going to run an
            // upload
            updateRequest =
                    service.files().update(fileId, file, new GoogleDriveFileContent(mimeType, fileContentStream));
            
            if (uploadType != null && !uploadType.equals(GoogleDriveUtils.StringConstants.RESUMABLE)) {
                updateRequest.getMediaHttpUploader().setDirectUploadEnabled(true);
            }
        }
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.CONVERT);
        if (!temporaryValue.isEmpty()) {
            updateRequest.setConvert(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.NEW_REVISION);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            updateRequest.setNewRevision(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.OCR);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            updateRequest.setOcr(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.OCR_LANGUAGE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            updateRequest.setOcrLanguage(temporaryValue);
        }
        
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.PINNED);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            updateRequest.setPinned(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.SET_MODIFIED_DATE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            updateRequest.setSetModifiedDate(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            updateRequest.setTimedTextLanguage(temporaryValue);
        }
        
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            updateRequest.setTimedTextTrackName(temporaryValue);
        }
        
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.UPDATE_VIEWED_DATE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            updateRequest.setUpdateViewedDate(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            updateRequest.setUseContentAsIndexableText(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        temporaryValue = optionalParams.get(GoogleDriveUtils.StringConstants.FIELDS);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            updateRequest.setFields(temporaryValue);
        }
        
        resultFile = updateRequest.execute();
        return resultFile;
        
    }
    
}
