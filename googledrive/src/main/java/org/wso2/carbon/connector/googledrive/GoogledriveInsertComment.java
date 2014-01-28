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
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Class mediator which maps to <strong>/comments</strong> endpoint's <strong>insert</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/comments/insert
 */
public class GoogledriveInsertComment extends AbstractConnector implements Connector {
    
    /** Represent the EMPTY_STRING of optional parameter request . */
    private static final String EMPTY_STRING = "";
    
    /**
     * connect.
     * 
     * @param messageContext ESB messageContext.
     * @throws ConnectException if connection fails.
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        OMElement insertCommentResult;
        Comment insertedComment;
        
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        String content = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.COMMENT_CONTENT);
        
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(GoogleDriveUtils.StringConstants.REQUEST_BODY,
                (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.REQUEST_BODY));
        
        HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            insertedComment = insertComment(service, fileId, content, parameters);
            if (insertedComment != null) {
                hashMapForResultEnvelope
                        .put(GoogleDriveUtils.StringConstants.COMMENT, insertedComment.toPrettyString());
                insertCommentResult =
                        GoogleDriveUtils.buildResultEnvelope(
                                GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTCOMMENT,
                                GoogleDriveUtils.StringConstants.INSERT_COMMENT_RESULT, true, hashMapForResultEnvelope);
                messageContext.getEnvelope().getBody().addChild(insertCommentResult);
            }
            
        } catch (Exception e) {
            hashMapForResultEnvelope.put(GoogleDriveUtils.StringConstants.ERROR, e.getMessage());
            insertCommentResult =
                    GoogleDriveUtils.buildResultEnvelope(
                            GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_INSERTCOMMENT,
                            GoogleDriveUtils.StringConstants.INSERT_COMMENT_RESULT, false, hashMapForResultEnvelope);
            
            messageContext.getEnvelope().getBody().addChild(insertCommentResult);
            
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
    }
    
    /**
     * Insert a new document-level comment.
     * 
     * @param service Drive API service instance.
     * @param fileId ID of the file to insert comment for.
     * @param content Text content of the comment.
     * @return The inserted comment if successful, {@code null} otherwise.
     */
    private Comment insertComment(Drive service, String fileId, String content, HashMap<String, String> params)
            throws IOException {
    
        Comment newComment = new Comment();
        newComment.setContent(content);
        
        Comments.Insert request = service.comments().insert(fileId, newComment);
        
        String temporaryResult = params.get(GoogleDriveUtils.StringConstants.REQUEST_BODY);
        if (!EMPTY_STRING.equals(temporaryResult)) {
            
            String json = temporaryResult;
            Gson gson = new Gson();
            Type hashmapCollectionType = new TypeToken<HashMap<String, String>>() {}.getType();
            Map<String, Object> requestMap = gson.fromJson(json, hashmapCollectionType);
            request.setUnknownKeys(requestMap);
            
        }
        return request.execute();
        
    }
}
