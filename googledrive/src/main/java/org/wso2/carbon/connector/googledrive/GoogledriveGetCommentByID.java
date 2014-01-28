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
import java.util.HashMap;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Comments;
import com.google.api.services.drive.model.Comment;

/**
 * Class which maps to <strong>/comments</strong> endpoint's <strong>get</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/comments/get
 */
public class GoogledriveGetCommentByID extends AbstractConnector implements Connector {
    
    /** Empty String. */
    private static final String EMPTY_STRING = "";
    
    /**
     * Connect method for class mediator.
     * 
     * @param messageContext the context of the OMElement
     * @throws ConnectException if connection fails.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        String commentId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.COMMENT_ID);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(GoogleDriveUtils.StringConstants.COMMENT_INCLUDE_DELETED,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.COMMENT_INCLUDE_DELETED));
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        OMElement getCommentResult;
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            Comment returnedComment;
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            returnedComment = getCommentById(service, fileId, commentId, parameters);
            if (returnedComment != null) {
                hashMapForResultEnvelope
                        .put(GoogleDriveUtils.StringConstants.COMMENT, returnedComment.toPrettyString());
                getCommentResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_GETCOMMENTBYID,
                                GoogleDriveUtils.StringConstants.GET_COMMENT_RESULT, true, hashMapForResultEnvelope);
                messageContext.getEnvelope().getBody().addChild(getCommentResult);
            }
            
        } catch (Exception e) {
            hashMapForResultEnvelope.put("error", e.getMessage());
            getCommentResult =
                    GoogleDriveUtils.buildResultEnvelope(
                            GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_GETCOMMENTBYID,
                            GoogleDriveUtils.StringConstants.GET_COMMENT_RESULT, false, hashMapForResultEnvelope);
            
            messageContext.getEnvelope().getBody().addChild(getCommentResult);
            
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            
        }
    }
    
    /**
     * Returns the specified document comment.
     * 
     * @param service Drive service object
     * @param fileId Id of the file the comment is in
     * @param commentId ID of the comment to be returned
     * @param params Parameters for comment return
     * @return <strong>Comment</strong> object
     */
    private Comment getCommentById(Drive service, String fileId, String commentId, HashMap<String, String> params)
            throws IOException {
    
        Comments.Get request = service.comments().get(fileId, commentId);
        String temporaryResult = params.get(GoogleDriveUtils.StringConstants.COMMENT_INCLUDE_DELETED);
        
        if (!EMPTY_STRING.equals(temporaryResult)) {
            request.setIncludeDeleted(Boolean.valueOf(temporaryResult));
        }
        return request.execute();
        
    }
}
