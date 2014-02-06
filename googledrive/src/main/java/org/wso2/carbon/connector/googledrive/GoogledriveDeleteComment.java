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

/**
 * Class which maps to <strong>/comments</strong> endpoint's <strong>delete</strong> method.
 * 
 * @see https://developers.google.com/drive/v2/reference/comments/delete
 */
public class GoogledriveDeleteComment extends AbstractConnector implements Connector {
    
    /**
     * Connect method for class mediator.
     * 
     * @param messageContext the context of the OMElement
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     * @throws ConnectException if connection fails.
     */
    public void connect(MessageContext messageContext) throws ConnectException {
    
        String fileId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FILE_ID);
        String commentId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.COMMENT_ID);
        OMElement deleteCommentResult;
        try {
            
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext, httpTransport, jsonFactory);
            
            deleteComment(service, fileId, commentId);
            
            deleteCommentResult =
                    GoogleDriveUtils.buildResultEnvelope(
                            GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_DELETECOMMENT,
                            GoogleDriveUtils.StringConstants.DELETE_COMMENT_RESULT, true, null);
            messageContext.getEnvelope().getBody().addChild(deleteCommentResult);
            
        } catch (Exception e) {
            HashMap<String, String> hashMapForResultEnvelope = new HashMap<String, String>();
            hashMapForResultEnvelope.put("error", e.getMessage());
            deleteCommentResult =
                    GoogleDriveUtils.buildResultEnvelope(
                            GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_DELETECOMMENT,
                            GoogleDriveUtils.StringConstants.DELETE_COMMENT_RESULT, false, hashMapForResultEnvelope);
            messageContext.getEnvelope().getBody().addChild(deleteCommentResult);
            log.error("Error: " + GoogleDriveUtils.getStackTraceAsString(e));
        }
    }
    
    /**
     * Remove a comment.
     * 
     * @param service Drive API service instance.
     * @param fileId ID of the file to remove the comment for.
     * @param commentId ID of the comment to remove.
     */
    private void deleteComment(Drive service, String fileId, String commentId) throws IOException {
    
        service.comments().delete(fileId, commentId).execute();
        
    }
}
