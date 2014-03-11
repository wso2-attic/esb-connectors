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

import java.io.FileNotFoundException;
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
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>insert</strong> method. Inserts a
 * new file to Google Drive. File contents need to be passed as a Base64-encoded string. Returns the newly
 * created file as a Google Drive SDK File resource in XML format and attaches to the message context's
 * envelope body, and stores an error message as a property on failure. Maps to the
 * <strong>insertFile</strong> Synapse template within the <strong>Google Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/insert
 */
public class GoogledriveInsertFile extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        String uploadType = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.UPLOAD_TYPE);
        
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        
        InputStream attachmentInStream = null;
        try {
            org.apache.axis2.context.MessageContext axis2mc =
                    ((Axis2MessageContext) messageContext).getAxis2MessageContext();
            DataHandler dataHandler = axis2mc.getAttachment(GoogleDriveUtils.StringConstants.FILE);
            
            if ( dataHandler != null ) {
                attachmentInStream = dataHandler.getInputStream();
            }
            
            Map<String, String> optionalParametersMap = getOptionalParamMap(messageContext);
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            File insertedFile = insertFile(service, uploadType, attachmentInStream, optionalParametersMap);
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.FILE, insertedFile.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTFILE,
                    GoogleDriveUtils.StringConstants.INSERTED_FILE_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Error inserting file.", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Error inserting file.", ioe, messageContext);
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
     * Assign optional parameters stored in the message context to a Map.
     * 
     * @param messageContext Synapse Message Context
     * @return return optional parameters map
     */
    private Map<String, String> getOptionalParamMap(final MessageContext messageContext) {
    
        Map<String, String> parameters = new HashMap<String, String>();
        
        parameters.put(GoogleDriveUtils.StringConstants.CONVERT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CONVERT));
        parameters.put(GoogleDriveUtils.StringConstants.OCR,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.OCR));
        parameters.put(GoogleDriveUtils.StringConstants.OCR_LANGUAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.OCR_LANGUAGE));
        parameters.put(GoogleDriveUtils.StringConstants.PINNED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PINNED));
        parameters.put(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE));
        parameters.put(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME));
        parameters.put(GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT));
        parameters.put(GoogleDriveUtils.StringConstants.VISIBILITY,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.VISIBILITY));
        parameters.put(GoogleDriveUtils.StringConstants.FILE_RESOURCE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_RESOURCE));
        parameters.put(GoogleDriveUtils.StringConstants.LABELS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.LABELS));
        parameters.put(GoogleDriveUtils.StringConstants.PARENTS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PARENTS));
        parameters.put(GoogleDriveUtils.StringConstants.PROPERTIES,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PROPERTIES));
        parameters.put(GoogleDriveUtils.StringConstants.INDEXABLE_TEXT,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.INDEXABLE_TEXT));
        parameters.put(GoogleDriveUtils.StringConstants.FIELDS,
                (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS));
        
        return parameters;
    }
    
    /**
     * Insert a new file and return it as a Google Drive SDK file resource.
     * 
     * @param service Google Drive SDK service object
     * @param uploadType The type of the upload: resumable or media
     * @param fileContentStream InputStream for file content to be inserted
     * @param optionalParametersMap Optional parameters for the request
     * @return Google Drive SDK File resource related to the inserted file
     * @throws IOException If an error occur on Google Drive API end.
     * @throws ValidationException If a validation error occurs.
     * @throws TokenResponseException If receiving an error response from the token server.
     * @throws XMLStreamException If an error occurs during parsing string as XML.
     */
    private File insertFile(final Drive service, final String uploadType, final InputStream fileContentStream,
            final Map<String, String> optionalParametersMap) throws IOException, ValidationException,
            TokenResponseException, XMLStreamException {
    
        // Mime Type is set to null if not specified
        String mimeType = null;
        // File's metadata.
        File metaDatafile = new File();
        
        String temporaryValue;
        if (uploadType != null && !uploadType.equals(GoogleDriveUtils.StringConstants.MEDIA)) {
            temporaryValue = optionalParametersMap.get(GoogleDriveUtils.StringConstants.FILE_RESOURCE);
            
            if (temporaryValue != null && !temporaryValue.isEmpty()) {
                
                Map<String, Object> fileResourceMap = GoogleDriveUtils.getUnkownKeyMap(temporaryValue);
                mimeType = (String) fileResourceMap.get(GoogleDriveUtils.StringConstants.MIME_TYPE);
                metaDatafile.setUnknownKeys(fileResourceMap);
                
            }
            
            temporaryValue = optionalParametersMap.get(GoogleDriveUtils.StringConstants.LABELS);
            
            if (temporaryValue != null && !temporaryValue.isEmpty()) {
                
                Map<String, Object> labelsMap = GoogleDriveUtils.getUnkownKeyMap(temporaryValue);
                Labels labels = new Labels();
                labels.setUnknownKeys(labelsMap);
                metaDatafile.setLabels(labels);
            }
            
            temporaryValue = optionalParametersMap.get(GoogleDriveUtils.StringConstants.PARENTS);
            
            if (temporaryValue != null && !temporaryValue.isEmpty()) {
                
                List<ParentReference> parentReferences = GoogleDriveUtils.getParentReferenceList(temporaryValue);
                metaDatafile.setParents(parentReferences);
                
            }
            
            temporaryValue = optionalParametersMap.get(GoogleDriveUtils.StringConstants.PROPERTIES);
            
            if (temporaryValue != null && !temporaryValue.isEmpty()) {
                
                List<Property> properties = GoogleDriveUtils.getPropertyList(temporaryValue);
                metaDatafile.setProperties(properties);
                
            }
            
            temporaryValue = optionalParametersMap.get(GoogleDriveUtils.StringConstants.INDEXABLE_TEXT);
            
            if (temporaryValue != null && !temporaryValue.isEmpty()) {
                
                IndexableText indexableText = new IndexableText();
                indexableText.setText(temporaryValue);
                metaDatafile.setIndexableText(indexableText);
                
            }
        }
        Files.Insert insertRequest = null;
        if ( fileContentStream != null ) {
            // File's content.
            insertRequest =
                    service.files().insert(metaDatafile, new GoogleDriveFileContent(mimeType, fileContentStream));
            if (uploadType != null && !uploadType.equals(GoogleDriveUtils.StringConstants.RESUMABLE)) {
                insertRequest.getMediaHttpUploader().setDirectUploadEnabled(true);
            } 
        } else {
            insertRequest = service.files().insert(metaDatafile);
        }
        // Setting optional parameters to the request
        setOptionalParameters(optionalParametersMap, insertRequest);
        
        return insertRequest.execute();
    }
    
    /**
     * Set optional parameters for the request.
     * 
     * @param insertRequest Google insert request.
     * @param parametersMap list of parameter values.
     * @throws ValidationException If a validation error occurs.
     */
    private void setOptionalParameters(final Map<String, String> parametersMap, final Files.Insert insertRequest)
            throws ValidationException {
    
        String temporaryValue;
        
        temporaryValue = parametersMap.get(GoogleDriveUtils.StringConstants.CONVERT);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            insertRequest.setConvert(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parametersMap.get(GoogleDriveUtils.StringConstants.OCR);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            insertRequest.setOcr(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parametersMap.get(GoogleDriveUtils.StringConstants.OCR_LANGUAGE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            insertRequest.setOcrLanguage((String) temporaryValue);
        }
        
        temporaryValue = parametersMap.get(GoogleDriveUtils.StringConstants.PINNED);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            insertRequest.setPinned(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parametersMap.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            insertRequest.setTimedTextLanguage(temporaryValue);
        }
        
        temporaryValue = parametersMap.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            insertRequest.setTimedTextTrackName(temporaryValue);
        }
        
        temporaryValue = parametersMap.get(GoogleDriveUtils.StringConstants.USE_CONTENT_AS_INDEXABLE_TEXT);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            insertRequest.setUseContentAsIndexableText(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parametersMap.get(GoogleDriveUtils.StringConstants.VISIBILITY);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            insertRequest.setVisibility(temporaryValue);
        }
        temporaryValue = parametersMap.get(GoogleDriveUtils.StringConstants.FIELDS);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            insertRequest.setFields(temporaryValue);
        }
        
    }
}
