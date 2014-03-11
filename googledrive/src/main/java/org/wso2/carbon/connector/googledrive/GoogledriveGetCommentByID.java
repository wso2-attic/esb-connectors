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
import com.google.api.services.drive.Drive.Comments;
import com.google.api.services.drive.model.Comment;

/**
 * Class which maps to <strong>/comments</strong> endpoint's <strong>get</strong> method. Gets a comment on a
 * file specified by a comment ID, on a file specified by a file ID. Returns a Google Drive SDK Comment
 * resource in XML format if successful, and stores an error message as a property on failure. Maps to the
 * <strong>getCommentByID</strong> Synapse template within the <strong>Google Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/comments/get
 */
public class GoogledriveGetCommentByID extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        String commentId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.COMMENT_ID);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(GoogleDriveUtils.StringConstants.COMMENT_INCLUDE_DELETED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.COMMENT_INCLUDE_DELETED));
        parameters.put(GoogleDriveUtils.StringConstants.FIELDS,
                (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS));
        
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        try {
            
            Comment returnedComment;
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            returnedComment = getCommentById(service, fileId, commentId, parameters);
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.COMMENT, returnedComment.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_GETCOMMENTBYID,
                    GoogleDriveUtils.StringConstants.GET_COMMENT_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Failed to get Comment: " + ioe.getMessage(), ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Failed to get Comment: ", ioe, messageContext);
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
     * Returns the specified document comment corresponding to the specified ID as a Comment resource.
     * 
     * @param service Drive service object
     * @param fileId Id of the file the comment is in
     * @param commentId ID of the comment to be returned
     * @param params Parameters for comment return
     * @return <strong>Comment</strong> object
     * @throws IOException If an error occur on Google Drive API end.
     * @throws ValidationException If a validation error occurs.
     * @throws TokenResponseException If receiving an error response from the token server.
     */
    private Comment getCommentById(final Drive service, final String fileId, final String commentId,
            final Map<String, String> params) throws IOException, ValidationException, TokenResponseException {
    
        Comments.Get request = service.comments().get(fileId, commentId);
        String temporaryValue = params.get(GoogleDriveUtils.StringConstants.COMMENT_INCLUDE_DELETED);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setIncludeDeleted(GoogleDriveUtils.toBoolean(temporaryValue));
        }
        temporaryValue = params.get(GoogleDriveUtils.StringConstants.FIELDS);
        
        if (temporaryValue != null && !temporaryValue.isEmpty()) {
            request.setFields(temporaryValue);
        }
        return request.execute();
        
    }
}
