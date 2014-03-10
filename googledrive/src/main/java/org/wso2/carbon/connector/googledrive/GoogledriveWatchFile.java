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
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.Channel;

/**
 * Class mediator which maps to <strong>/files</strong> endpoint's <strong>watch</strong> method. Creates a
 * callback channel to watch modifications on a file within Google Drive, specified by a file ID. Returns the
 * callback channel as a Google Drive SDK Channel resource in XML format and attaches to the message context's
 * envelope body, and stores an error message as a property on failure. Maps to the <strong>watchFile</strong>
 * Synapse template within the <strong>Google Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/files/watch
 */
public class GoogledriveWatchFile extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        Map<String, String> parameters = new HashMap<String, String>();
        
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        
        parameters.put(GoogleDriveUtils.StringConstants.REQUEST_BODY,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.REQUEST_BODY));
        
        parameters.put(GoogleDriveUtils.StringConstants.PARAMS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.PARAMS));
        
        parameters.put(GoogleDriveUtils.StringConstants.FIELDS,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FIELDS));
        
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        
        // Channel object to store result
        Channel createdChannel = null;
        
        try {
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            createdChannel = watchFile(service, fileId, parameters);
            
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_WATCHFILE,
                    GoogleDriveUtils.StringConstants.WATCH_FILE_RESULT, resultEnvelopeMap));
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.FILE, createdChannel.toPrettyString());
            
        } catch (IOException ioe) {
            
            log.error("Error creating callback channel:", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Error creating callback channel: ", ioe, messageContext);
        } catch (GeneralSecurityException gse) {
            
            log.error("Google Drive authentication failure:", gse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, gse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_GENERAL_SECURITY_EXCEPTION);
            handleException("Google Drive authentication failure.: ", gse, messageContext);
        } catch (XMLStreamException xse) {
            log.error("Failed to parse OM Element.", xse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, xse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_XML_STREAM_PARSE_FAILURE);
            handleException("Failed to parse OM Element.", xse, messageContext);
        }
    }
    
    /**
     * Creates a callback channel to watch a file specified by a file ID and returns a Channel resource.
     * 
     * @param service Google Drive SDK service object
     * @param fileId ID of the file to watch
     * @param parameters Map containing optional parameters to create the channel
     * @return Channel callback Channel
     * @throws IOException If an error occur on Google Drive API end.
     * @throws TokenResponseException If receiving an error response from the token server.
     * @throws XMLStreamException If an error occurs during parsing string as XML.
     */
    private Channel watchFile(final Drive service, final String fileId, final Map<String, String> parameters)
            throws IOException, TokenResponseException, XMLStreamException {
    
        Channel channel = new Channel();
        
        String temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.REQUEST_BODY);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            channel.setUnknownKeys(GoogleDriveUtils.getUnkownKeyMap(temporaryValue));
        }
        
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.PARAMS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            Map<String, Object> unknownKeyMap = GoogleDriveUtils.getUnkownKeyMap(temporaryValue);
            Map<String, String> params = new HashMap<String, String>();
            Iterator<Map.Entry<String, Object>> paramIterator = unknownKeyMap.entrySet().iterator();
            while (paramIterator.hasNext()) {
                Map.Entry<String, Object> element = paramIterator.next();
                params.put(element.getKey(), (String) element.getValue());
            }
            channel.setParams(params);
        }
        
        Files.Watch watchRequest = service.files().watch(fileId, channel);
        temporaryValue = parameters.get(GoogleDriveUtils.StringConstants.FIELDS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            
            watchRequest.setFields((String) temporaryValue);
        }
        
        return watchRequest.execute();
        
    }
    
}
