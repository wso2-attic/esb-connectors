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

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.model.ChildReference;

/**
 * Class mediator which maps to <strong>/children</strong> endpoint's <strong>get</strong> method. Gets a
 * child element of a folder within Google Drive, as specified by a folder ID and child ID (file ID). Returns
 * the child as a Google Drive SDK ChildReference resource in XML format and attaches to the message context's
 * envelope body, and stores an error message as a property on failure. Maps to the <strong>getChild</strong>
 * Synapse template within the <strong>Google Drive</strong> connector.
 * 
 * @see https://developers.google.com/drive/v2/reference/children/get
 */
public class GoogledriveGetChild extends AbstractConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public final void connect(final MessageContext messageContext) {
    
        String folderId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.FOLDER_ID);
        String childId = (String) getParameter(messageContext, GoogleDriveUtils.StringConstants.CHILD_ID);
        String fields = (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.FIELDS);
        
        Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
        try {
            
            ChildReference returnedChild;
            
            Drive service = GoogleDriveUtils.getDriveService(messageContext);
            Children.Get getChildRequest = service.children().get(folderId, childId);
            if (fields != null && !fields.isEmpty()) {
                getChildRequest.setFields(fields);
            }
            returnedChild = getChildRequest.execute();
            
            resultEnvelopeMap.put(GoogleDriveUtils.StringConstants.CHILD_REFERENCE, returnedChild.toPrettyString());
            messageContext.getEnvelope().detach();
            // build new SOAP envelope to return to client
            messageContext.setEnvelope(GoogleDriveUtils.buildResultEnvelope(
                    GoogleDriveUtils.StringConstants.URN_GOOGLEDRIVE_GETCHILD,
                    GoogleDriveUtils.StringConstants.GET_CHILD_RESULT, resultEnvelopeMap));
            
        } catch (IOException ioe) {
            log.error("Error getting child reference.", ioe);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, ioe,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_IO_EXCEPTION);
            handleException("Error getting child reference.", ioe, messageContext);
        } catch (GeneralSecurityException gse) {
            log.error("Google Drive authentication failure.", gse);
            GoogleDriveUtils.storeErrorResponseStatus(messageContext, gse,
                    GoogleDriveUtils.ErrorCodeConstants.ERROR_CODE_GENERAL_SECURITY_EXCEPTION);
            handleException("Google Drive authentication failure.", gse, messageContext);
        }
    }
}
