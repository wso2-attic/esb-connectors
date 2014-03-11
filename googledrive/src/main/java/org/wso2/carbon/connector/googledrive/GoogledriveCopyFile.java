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
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.synapse.MessageContext;
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
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>copy</strong> method. Creates a
 * copy of a file within Google Drive specified by a given file ID, with the name
 * <strong>"Copy of {original file name}"</strong>. Returns the newly created copy of the file as a Google
 * Drive SDK File resource in XML format and attaches to the message context's envelope body, and stores an
 * error message as a property on failure.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/copy
 */
public class GoogledriveCopyFile extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        try {
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            Map<String, String> optionalParams = getOptionalParams(messageContext);
            
            File copiedFile = copyFile(service, fileId, optionalParams);
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.FILE, copiedFile.toPrettyString());
            
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_COPYFILE,
                    GoogleDriveUtils.StringConstants.COPIED_FILE_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Failed to copy file:", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Failed to copy file: ", ioe, messageContext);
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
     * Create a copy of an existing file within Google Drive, specified by file ID with the name <strong>Copy
     * of</strong> {original file name} and returns a Google Drive SDK file resource corresponding to the
     * newly created file.
     * 
     * @param service Drive API service instance
     * @param originFileId ID of the origin file to copy
     * @param parameters collection of optional parameters
     * @return The copied file if successful
     * @throws IOException If an error occur on Google Drive API end.
     * @throws ValidationException If a validation error occurs.
     * @throws TokenResponseException If receiving an error response from the token server.
     * @throws XMLStreamException
     */
    private File copyFile(final Drive service, final String originFileId, final Map<String, String> parameters)
            throws IOException, ValidationException, TokenResponseException, XMLStreamException {
    
        File copiedFile = new File();
        
        String temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.FILE_RESOURCE);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            Map<String, Object> fileResourceMap = GoogleDriveUtils.getUnkownKeyMap(temporaryValue);
            copiedFile.setUnknownKeys(fileResourceMap);
            
            String copyTitle = (String) fileResourceMap.get(GoogleDriveUtils.StringConstants.TITLE);
            if ((copyTitle == null || copyTitle.isEmpty()) && (originFileId != null && !originFileId.isEmpty())) {
                copyTitle = service.files().get(originFileId).execute().getTitle();
                copiedFile.setTitle(copyTitle);
            }
            
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.LABELS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            Map<String, Object> labelsMap = GoogleDriveUtils.getUnkownKeyMap(temporaryValue);
            Labels labels = new Labels();
            labels.setUnknownKeys(labelsMap);
            copiedFile.setLabels(labels);
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.PARENTS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            List<ParentReference> parentReferences = GoogleDriveUtils.getParentReferenceList(temporaryValue);
            copiedFile.setParents(parentReferences);
            
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.PROPERTIES);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            List<Property> properties = GoogleDriveUtils.getPropertyList(temporaryValue);
            copiedFile.setProperties(properties);
            
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.INDEXABLE_TEXT);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            IndexableText indexableText = new IndexableText();
            indexableText.setText(temporaryValue);
            copiedFile.setIndexableText(indexableText);
            
        }
        Files.Copy copyRequest = service.files().copy(originFileId, copiedFile);
        
        // setting optional parameters to the request
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.CONVERT);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            copyRequest.setConvert(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.OCR);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            copyRequest.setOcr(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.OCR_LANGUAGE);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            copyRequest.setOcrLanguage((String) temporaryValue);
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.PINNED);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            copyRequest.setPinned(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_LANGUAGE);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            copyRequest.setTimedTextLanguage((String) temporaryValue);
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.TIMED_TEXT_TRACKNAME);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            copyRequest.setTimedTextTrackName((String) temporaryValue);
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.VISIBILITY);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            copyRequest.setVisibility((String) temporaryValue);
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.FIELDS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            copyRequest.setFields((String) temporaryValue);
        }
        
        
        
        return copyRequest.execute();
        
    }
    
    /**
     * Adds a set of parameters stored in a message context to a Map for ease of use.
     * 
     * @param messageContext Synapse Message Context
     * @return A map containing key value pairs corresponding to the optional parameters available in the
     *         message context.
     */
    private Map<String, String> getOptionalParams(final MessageContext messageContext) {
    
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
    
}
