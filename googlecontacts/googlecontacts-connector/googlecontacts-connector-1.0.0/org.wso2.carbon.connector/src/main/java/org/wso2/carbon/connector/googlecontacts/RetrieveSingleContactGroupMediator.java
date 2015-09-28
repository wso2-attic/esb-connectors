/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.util.ServiceException;

/**
 * The Class RetrieveSingleContactGroupMediator retrieve contact details for given contact group. This class
 * extends {@link AbstractGoogleContactsConnector}.
 * 
 * @see https://developers.google.com/google-apps/contacts/v3/# retrieving_a_single_contact_group
 */
public final class RetrieveSingleContactGroupMediator extends AbstractGoogleContactsConnector {
    
    /**
     * This method retrieve contact group for single contact group ID.
     * 
     * @param messageContext The message context for the connector request.
     */
    public void connect(final MessageContext messageContext) {
    
        // Get group id from message context
        final String groupId = (String) getParameter(messageContext, Constants.GROUP_ID);
        
        try {
            // Check group ID is available
            if (groupId != null && !groupId.isEmpty()) {
                
                // append parameters for request URL
                final String requestUrl =
                        getRequestURLBuilder(messageContext, Constants.REQUEST_URL_GROUPS)
                                .append(Constants.FORWARD_SLASH).append(Constants.URL_POSTFIX)
                                .append(Constants.FORWARD_SLASH).append(groupId).toString();
                messageContext.getEnvelope().getBody().getFirstElement().detach();
                
                // Add authentication specific parameters for message context
                final ContactsService contactsService = getContactService(messageContext);
                
                // Retrieve contact group entry for the given group id
                final ContactGroupEntry contactGroupEntry =
                        contactsService.getEntry(new URL(requestUrl), ContactGroupEntry.class);
                
                // Get XML format of ContactGroupEntry Object and set to messageContext
                messageContext.setEnvelope(getGDataSOAPEnvelope(contactGroupEntry, contactsService));
            } else {
                ValidationException validationException = new ValidationException("Group ID is not found.");
                log.error("Request validation failed: ", validationException);
                storeErrorResponseStatus(messageContext, validationException,
                        Constants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
                handleException("Request validation failed: ", validationException, messageContext);
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
