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
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;

/**
 * Retrieve all of a user's contacts within a Google Contacts account. Upon success, the server responds with
 * the created contact entry with some additional elements and properties
 * 
 * @see https ://developers.google.com/google-apps/contacts/v3/#retrieving_all_contacts
 */
public final class RetrieveAllContactsMediator extends AbstractGoogleContactsConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(final MessageContext messageContext) {
    
        try {
            
            // Authentication Token Setup
            final ContactsService contactsService = getContactService(messageContext);
            
            // Get End point request URL created for contacts
            final StringBuilder requestUrl =
                    getRequestURLBuilder(messageContext, Constants.REQUEST_URL_CONTACTS);
            requestUrl.append(Constants.REQUEST_URL_GENERIC_END);
            
            // Call Google Contact Service feed method
            final ContactFeed resultFeed = contactsService.getFeed(new URL(requestUrl.toString()), ContactFeed.class);
            messageContext.getEnvelope().detach();
            
            // Get Response in XML format of ContactFeed Object
            messageContext.setEnvelope(getGDataSOAPEnvelope(resultFeed, contactsService));
            
        } catch (ServiceException se) {
            log.error("Error creating service object.", se);
            storeErrorResponseStatus(messageContext, se, Constants.ERROR_CODE_SERVICE_EXCEPTION);
            handleException("Error creating service object.", se, messageContext);
        } catch (XMLStreamException xmlse) {
            log.error("Error parsing XML stream.", xmlse);
            storeErrorResponseStatus(messageContext, xmlse, Constants.ERROR_CODE_XML_STRM_PARSE_FAILURE);
            handleException("Error parsing XML stream.", xmlse, messageContext);
        } catch (MalformedURLException mue) {
            log.error("Bad query for URL.", mue);
            storeErrorResponseStatus(messageContext, mue, Constants.ERROR_CODE_MALFORMED_URL_EXCEPTION);
            handleException("Bad query for URL.", mue, messageContext);
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
