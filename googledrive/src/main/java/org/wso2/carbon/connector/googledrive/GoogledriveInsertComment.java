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

import javax.xml.parsers.ParserConfigurationException;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.xml.sax.SAXException;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Comments;
import com.google.api.services.drive.model.Comment;
import com.google.api.services.drive.model.Comment.Context;

/**
 * Class mediator which maps to <strong>/comments</strong> endpoint's <strong>insert</strong> method. Inserts
 * a new comment to a file in Google Drive specified by a file ID. Returns the newly created comment as a
 * Google Drive SDK Comment resource in XML format and attaches to the message context's envelope body, and
 * stores an error message as a property on failure. Maps to the <strong>insertComment</strong> Synapse
 * template within the <strong>Google Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/comments/insert
 */
public class GoogledriveInsertComment extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        Comment comment;
        
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        String commentContent = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.COMMENT_CONTENT);
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(GoogleDriveUtils.StringConstants.CONTEXT_TYPE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CONTEXT_TYPE));
        parameters.put(GoogleDriveUtils.StringConstants.CONTEXT_VALUE,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CONTEXT_VALUE));
        parameters.put(GoogleDriveUtils.StringConstants.FIELDS,
                (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS));
        
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        
        try {
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            
            comment = insertComment(service, fileId, commentContent, parameters);
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.COMMENT, comment.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTCOMMENT,
                    GoogleDriveUtils.StringConstants.INSERT_COMMENT_RESULT, resultEnvelopeMap));
            
        } catch (ParserConfigurationException pce) {
            log.error("Error in XML parsing configuration.", pce);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, pce,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_XML_PARSER_CONFIGURATION_EXCEPTION);
            handleException("Error in XML parsing configuration.", pce, messageContext);
        } catch (SAXException saxe) {
            log.error("Error in parsing XML request.", saxe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, saxe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_SAX_EXCEPTION);
            handleException("Error in parsing XML request.", saxe, messageContext);
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
        }
    }
    
    /**
     * Insert a new document-level comment, and return the newly created comment as a Comment resource.
     * 
     * @param service Drive API service instance
     * @param fileId ID of the file to insert comment for
     * @param commentContent Text content of the comment
     * @param params Optional parameters
     * @return The inserted comment if successful
     * @throws IOException If an error occur on Google Drive API end.
     * @throws ParserConfigurationException If an error occurs in the parser.
     * @throws SAXException If a parser error occurs.
     * @throws TokenResponseException If receiving an error response from
     *         the token server.
     */
    private Comment insertComment(final Drive service, final String fileId, final String commentContent,
            final Map<String, String> params) throws IOException, ParserConfigurationException, SAXException,
            TokenResponseException {
    
        Comment newComment = new Comment();
        newComment.setContent(commentContent);
        Context context = new Context();
        
        
        String temporaryValue = params.get(GoogleDriveUtils.StringConstants.CONTEXT_TYPE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            context.setType(temporaryValue);
        }
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.CONTEXT_VALUE);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            context.setValue(temporaryValue);
        }
        newComment.setContext(context);
        Comments.Insert request = service.comments().insert(fileId, newComment);
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.FIELDS);
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setFields(temporaryValue);
        }
        return request.execute();
    }
}
