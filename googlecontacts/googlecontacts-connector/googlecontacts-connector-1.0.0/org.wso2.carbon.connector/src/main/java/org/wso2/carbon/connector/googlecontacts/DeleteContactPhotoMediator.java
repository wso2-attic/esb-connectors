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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.synapse.MessageContext;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.util.ServiceException;

/**
 * Class to delete a contact's photo by sending an authorized DELETE request to the contact's photo URL. The
 * photo URL retrieved from the contact entry returned by the API providing contact Id.
 * 
 * @see https://developers.google.com/google-apps/contacts/v3/?hl=ja#deleting_a_contacts_photo
 */
public final class DeleteContactPhotoMediator extends AbstractGoogleContactsConnector {
    
    /**
     * This method delete a particular contact photo related to provided contact id of a particular user.
     * 
     * @param messageContext ESB message context.
     */
    public void connect(final MessageContext messageContext) {
    
        // Get contact id from message context.
        final String contactId = (String) getParameter(messageContext, Constants.CONTACT_ID);
        
        try {
            
            // Check contact ID is available or not.
            if (contactId != null && !contactId.isEmpty()) {
                
                // Authenticate with google contacts API and get returning contacts Service.
                final ContactsService contactsService = getContactService(messageContext);
                
                // Build the end point request URL created for contacts.
                final StringBuilder requestUrl = getRequestURLBuilder(messageContext, Constants.REQUEST_URL_CONTACTS);
                requestUrl.append(Constants.REQUEST_URL_GENERIC_END).append(Constants.FORWARD_SLASH).append(contactId);
                
                // Get the contact service entry related to given contact id.
                final ContactEntry contactEntry =
                        contactsService.getEntry(new URL(requestUrl.toString()), ContactEntry.class);
                
                // Set If-Match header value '*'.
                contactEntry.setEtag(Constants.ETAG);
                
                // Get the contact photo link from the returned contact entry.
                final Link photoLink = contactEntry.getContactPhotoLink();
                final URL photoUrl = new URL(photoLink.getHref());
                
                // Delete the contact photo related to given contact id.
                contactsService.delete(photoUrl, photoLink.getEtag());
                
                // Build new SOAP envelope to return to client.
                messageContext.getEnvelope().detach();
                messageContext.setEnvelope(buildResultEnvelope(Constants.URN_GOOGLECONTACTS_DELETECONTACTPHOTO,
                        Constants.DELETE_CONTACT_PHOTO_RESULT));
            } else {
                
                final ValidationException validateException = new ValidationException("Contact ID is not found");
                log.error("Failed to validate id: ", validateException);
                storeErrorResponseStatus(messageContext, validateException,
                        Constants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
                handleException("Failed to validate id: ", validateException, messageContext);
                
            }
            
        } catch (MalformedURLException mue) {
            log.error("Error reading contact entry from built URL: ", mue);
            storeErrorResponseStatus(messageContext, mue, Constants.ERROR_CODE_MALFORMED_URL_EXCEPTION);
            handleException("Error reading contact entry from built URL: ", mue, messageContext);
        } catch (ServiceException se) {
            log.error("Service unavailable: ", se);
            storeErrorResponseStatus(messageContext, se, Constants.ERROR_CODE_SERVICE_EXCEPTION);
            handleException("Service unavailable: ", se, messageContext);
        } catch (IOException io) {
            log.error("Failed to access entry: ", io);
            storeErrorResponseStatus(messageContext, io, Constants.ERROR_CODE_IO_EXCEPTION);
            handleException("Failed to access entry: ", io, messageContext);
        } catch (RuntimeException re) {
            log.error("Error occured in connector: ", re);
            storeErrorResponseStatus(messageContext, re, Constants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException("Error occured in connector: ", re, messageContext);
        }
    }
}
