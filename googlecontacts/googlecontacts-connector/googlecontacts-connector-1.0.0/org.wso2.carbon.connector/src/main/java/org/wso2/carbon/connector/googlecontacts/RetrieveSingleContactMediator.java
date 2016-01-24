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

import javax.xml.stream.XMLStreamException;

import org.apache.synapse.MessageContext;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.util.ServiceException;

/**
 * The Class RetrieveSingleContactMediator handles the Retrieve Single Contact of Google Contacts connector.
 * 
 * @see https://developers.google.com/google-apps/contacts/v3/#retrieving_a_single_contact
 */
public final class RetrieveSingleContactMediator extends AbstractGoogleContactsConnector {
    
    /**
     * This method get a single contact from a provided contact id of a particular user.
     * 
     * @param messageContext ESB message context.
     */
    public void connect(final MessageContext messageContext) {
    
        final String contactId = (String) getParameter(messageContext, Constants.CONTACT_ID);
        
        try {
            // Null or empty check of contact ID since all contacts are returned without contact ID.
            if (contactId != null && !contactId.isEmpty()) {
                
                final ContactsService contactService = getContactService(messageContext);
                
                final String requestUrl =
                        getRequestURLBuilder(messageContext, Constants.REQUEST_URL_CONTACTS)
                                .append(Constants.REQUEST_URL_GENERIC_END).append(Constants.FORWARD_SLASH)
                                .append(contactId).toString();
                
                // Get the contact service entry related to given contact id.
                final ContactEntry contactEntry = contactService.getEntry(new URL(requestUrl), ContactEntry.class);
                messageContext.getEnvelope().detach();
                
                // Build new SOAP envelope to return to client.
                messageContext.setEnvelope(getGDataSOAPEnvelope(contactEntry, contactService));
                
            } else {
                final ValidationException ve = new ValidationException("Contact ID is not found.");
                log.error("Failed to validate contact id: ", ve);
                storeErrorResponseStatus(messageContext, ve, Constants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
                handleException("Failed to validate contact id: ", ve, messageContext);
            }
            
        } catch (MalformedURLException mue) {
            log.error("Error reading contact entry from built URL: ", mue);
            storeErrorResponseStatus(messageContext, mue, Constants.ERROR_CODE_MALFORMED_URL_EXCEPTION);
            handleException("Error reading contact entry from built URL: ", mue, messageContext);
        } catch (ServiceException se) {
            log.error("Service unavailable: ", se);
            storeErrorResponseStatus(messageContext, se, Constants.ERROR_CODE_SERVICE_EXCEPTION);
            handleException("Service unavailable: ", se, messageContext);
        } catch (XMLStreamException xmlse) {
            log.error("Error parsing XML stream.", xmlse);
            storeErrorResponseStatus(messageContext, xmlse, Constants.ERROR_CODE_XML_STRM_PARSE_FAILURE);
            handleException("Error parsing XML stream.", xmlse, messageContext);
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
