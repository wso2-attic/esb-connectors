/**
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.googlecontacts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.activation.DataHandler;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;

import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

/**
 * Class which maps to the update/add photo method of Google Contacts. Take a new image file as a SOAP
 * attachment and streams it to update the photo's contact.
 * 
 * @see https://developers.google.com/google-apps/contacts/v3/?hl=en#addingupdating_a_photo_for_a_contact
 */
public final class UpdateContactPhotoMediator extends AbstractGoogleContactsConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(final MessageContext messageContext) {
    
        String contactId = (String) getParameter(messageContext, Constants.CONTACT_ID);
        InputStream photoFileInputStream = null;
        try {
            // Check contact ID is available
            if (contactId != null && !contactId.isEmpty()) {
             // Get End point request URL created for contacts
                StringBuilder requestUrl = getRequestURLBuilder(messageContext, Constants.REQUEST_URL_CONTACTS);
                requestUrl.append(Constants.REQUEST_URL_GENERIC_END).append(Constants.FORWARD_SLASH).append(contactId);
                // Retrieve attachment from message context
                org.apache.axis2.context.MessageContext axis2mc =
                        ((Axis2MessageContext) messageContext).getAxis2MessageContext();
                DataHandler dataHandler = axis2mc.getAttachment(Constants.PHOTO);
                // Check whether the attachment was sent
                if (dataHandler != null) {
                    photoFileInputStream = dataHandler.getInputStream();
                } else {
                    IOException ioe = new IOException("Photo attachment with content ID 'photo' not found.");
                    log.error("Photo attachment with content ID 'photo' not found.", ioe);
                    storeErrorResponseStatus(messageContext, ioe, Constants.ERROR_CODE_IO_EXCEPTION);
                    handleException("Photo attachment with content ID 'photo' not found.", ioe, messageContext);
                }
                // Build contacts service object
                final ContactsService contactsService = getContactService(messageContext);
                final ContactEntry entryToUpdate =
                        contactsService.getEntry(new URL(requestUrl.toString()), ContactEntry.class);
                Link photoLink = entryToUpdate.getContactPhotoLink();
                URL photoUrl = new URL(photoLink.getHref());
                // Build request to upload photo
                GDataRequest request =
                        contactsService.createRequest(GDataRequest.RequestType.UPDATE, photoUrl, new ContentType(
                                Constants.IMAGE_CONTENT_TYPE));
                request.setEtag(photoLink.getEtag());
                
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                // Create a byte array from the image data
                int nRead;
                final byte[] data = new byte[photoFileInputStream.available()];
                
                while ((nRead = photoFileInputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                
                request.getRequestStream().write(buffer.toByteArray());
                request.execute();
                
                messageContext.getEnvelope().getBody().getFirstElement().detach();
                
                // Build new SOAP envelope to return to client.
                messageContext.setEnvelope(buildResultEnvelope(Constants.URN_GOOGLECONTACTS_UPDATECONTACTPHOTO,
                        Constants.UPDATE_CONTACT_PHOTO_RESULT));
            } else {
                ValidationException validationException = new ValidationException("Contact ID is not found");
                log.error("Failed to validate : ", validationException);
                storeErrorResponseStatus(messageContext, validationException,
                        Constants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
                handleException("Failed to validate : ", validationException, messageContext);
            }
        } catch (MalformedURLException mue) {
            log.error("Bad query for URL.", mue);
            storeErrorResponseStatus(messageContext, mue, Constants.ERROR_CODE_MALFORMED_URL_EXCEPTION);
            handleException("Bad query for URL.", mue, messageContext);
        } catch (ServiceException se) {
            log.error("Error creating service object.", se);
            storeErrorResponseStatus(messageContext, se, Constants.ERROR_CODE_SERVICE_EXCEPTION);
            handleException("Error creating service object.", se, messageContext);
        } catch (IOException ioe) {
            log.error("Error sending request.", ioe);
            storeErrorResponseStatus(messageContext, ioe, Constants.ERROR_CODE_IO_EXCEPTION);
            handleException("Error sending request.", ioe, messageContext);
        } catch (RuntimeException re) {
            log.error("Error occured in connector: ", re);
            storeErrorResponseStatus(messageContext, re, Constants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException("Error occured in connector: ", re, messageContext);
        }
    }
}
