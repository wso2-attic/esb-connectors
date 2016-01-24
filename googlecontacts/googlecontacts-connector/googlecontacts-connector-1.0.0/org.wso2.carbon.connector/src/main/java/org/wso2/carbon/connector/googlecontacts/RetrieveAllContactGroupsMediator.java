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
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.util.ServiceException;

/**
 * Class to retrieve user's all contact groups by sending an authorized GET request.
 * 
 * @see https https://developers.google.com/google-apps/contacts/v3/?hl=ja#retrieving_all_contact_groups
 */
public final class RetrieveAllContactGroupsMediator extends AbstractGoogleContactsConnector {
    
    /**
     * This method retrieving all contact groups of a particular user.
     * 
     * @param messageContext ESB message context.
     */
    public void connect(final MessageContext messageContext) {
    
        // Build the end point request URL created for contact groups.
        final StringBuilder requestUrl = getRequestURLBuilder(messageContext, Constants.REQUEST_URL_GROUPS);
        requestUrl.append(Constants.REQUEST_URL_GENERIC_END);
        
        try {
            
            // Authenticate with google contacts API and get returning contacts Service.
            final ContactsService contactsService = getContactService(messageContext);
            
            // Create feed URL.
            final URL feedUrl = new URL(requestUrl.toString());
            
            // Request the group feed.
            final ContactGroupFeed resultGroupFeed = contactsService.getFeed(feedUrl, ContactGroupFeed.class);
            
            // Build the result Envelope.
            messageContext.getEnvelope().detach();
            messageContext.setEnvelope(getGDataSOAPEnvelope(resultGroupFeed, contactsService));
            
        } catch (XMLStreamException xmlse) {
            log.error("Error parsing XML stream: ", xmlse);
            storeErrorResponseStatus(messageContext, xmlse, Constants.ERROR_CODE_XML_STRM_PARSE_FAILURE);
            handleException("Error parsing XML stream: ", xmlse, messageContext);
        } catch (MalformedURLException mue) {
            log.error("Error reading contact group entry from built URL: ", mue);
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
