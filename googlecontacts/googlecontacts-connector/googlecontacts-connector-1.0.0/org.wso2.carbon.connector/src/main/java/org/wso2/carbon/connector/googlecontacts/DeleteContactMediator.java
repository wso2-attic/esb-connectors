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
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.util.ServiceException;

/**
 * This GooglecontactsDeleteContact is used to delete user contact details for the given contact id.
 * 
 * @see https://developers.google.com/google-apps/contacts/v3/#deleting_contacts
 */
public final class DeleteContactMediator extends AbstractGoogleContactsConnector {
    
    /**
     * This method delete a particular contact related to provided contact id of a particular user.
     * 
     * @param messageContext The message context for the connector request.
     */
    public void connect(final MessageContext messageContext) {
    
        // Get contact id from message context
        final String contactId = (String) getParameter(messageContext, Constants.CONTACT_ID);
        
        try {
            
            // Check contact ID is available
            if (contactId != null && !contactId.isEmpty()) {
                
                // append parameters for request URL
                final String requestUrl =
                        getRequestURLBuilder(messageContext, Constants.REQUEST_URL_CONTACTS)
                                .append(Constants.FORWARD_SLASH).append(Constants.URL_POSTFIX)
                                .append(Constants.FORWARD_SLASH).append(contactId).toString();
                
                // Add authentication specific parameters for message context
                final ContactsService contactsService = getContactService(messageContext);
                
                // Delete the contact service entry related to given id
                final ContactEntry contactEntry =
                        contactsService.getEntry(new URL(requestUrl), ContactEntry.class, contactId);
                
                // Set ETAG value for request before delete
                contactEntry.setEtag(Constants.ETAG);
                contactEntry.delete();
                messageContext.getEnvelope().detach();
                
                // build new SOAP envelope to return to client
                messageContext.setEnvelope(buildResultEnvelope(Constants.URN_GOOGLECONTACTS_DELETECONTACT,
                        Constants.SOAP_DELETE_RESPONSE, null));
            } else {
                ValidationException validationException = new ValidationException("Contact ID is not found.");
                log.error("Request validation failed: ", validationException);
                storeErrorResponseStatus(messageContext, validationException,
                        Constants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
                handleException("Request validation failed: ", validationException, messageContext);
            }
            
        } catch (MalformedURLException mue) {
            log.error("No protocol is specified, or an unknown protocol is found: ", mue);
            storeErrorResponseStatus(messageContext, mue, Constants.ERROR_CODE_MALFORMED_URL_EXCEPTION);
            handleException("No protocol is specified, or an unknown protocol is found: ", mue, messageContext);
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
