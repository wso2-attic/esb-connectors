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

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;

import com.google.api.services.drive.Drive;

/**
 * Class which maps to <strong>/comments</strong> endpoint's <strong>delete</strong> method. Deletes a comment
 * specified by a comment ID, on a file specified by a file ID. Returns the success of the deletion operation
 * by adding a true tag to the message context's soap envelope, and stores an error message as a property on
 * failure. Maps to the <strong>deleteComment</strong> Synapse template within the <strong>Google
 * Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/comments/delete
 */
public class GoogledriveDeleteComment extends AbstractConnector {
    
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
        try {
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            service.comments().delete(fileId, commentId).execute();
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_DELETECOMMENT,
                    GoogleDriveUtils.StringConstants.DELETE_COMMENT_RESULT, null));
            
        } catch (IOException ioe) {
            log.error("Failed to delete Comment.", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Failed to delete Comment.", ioe, messageContext);
        } catch (GeneralSecurityException gse) {
            log.error("Google Drive authentication failure.", gse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, gse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_GENERAL_SECURITY_EXCEPTION);
            handleException("Google Drive authentication failure.", gse, messageContext);
        }
    }
}
