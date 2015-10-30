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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.synapse.MessageContext;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.util.ServiceException;

/**
 * Change a contact's group membership within a Google Contacts account. Edit the contact's
 * gd:groupMembershipInfo fields to reflect the new group this contact belongs to and send an update request
 * to the API.
 * 
 * @see https ://developers.google.com/google-apps/contacts/v3/#changing_group_membership
 */
public final class ChangeGroupMembershipMediator extends AbstractGoogleContactsConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(final MessageContext messageContext) {
    
        try {
            
            final String contactId = (String) getParameter(messageContext, Constants.CONTACT_ID);
            
            if (contactId != null && !contactId.isEmpty()) {
                
                final String groupMemInfo = (String) getParameter(messageContext, Constants.GROUP_MEMBERSHIP_INFO);
                
                // Authentication Token Setup
                final ContactsService contactsService = getContactService(messageContext);
                
                // Get End point request URL created for contacts
                final StringBuilder requestUrl = getRequestURLBuilder(messageContext, Constants.REQUEST_URL_CONTACTS);
                requestUrl.append(Constants.REQUEST_URL_GENERIC_END).append(Constants.FORWARD_SLASH).append(contactId);
                
                // Fetch Contact Entry details for given contact id
                final ContactEntry contactEntry =
                        contactsService.getEntry(new URL(requestUrl.toString()), ContactEntry.class);
                
                // Set ETAG value for request before update
                contactEntry.setEtag(Constants.ETAG);
                
                // Get and Set GroupMembershipInfo details
                if (groupMemInfo != null && !groupMemInfo.isEmpty()) {
                    final OMElement membershipElement = AXIOMUtil.stringToOM(groupMemInfo);
                    final GroupMembershipInfo membershipInfo =
                            getGroupMembershipInfoList(messageContext, membershipElement);
                    contactEntry.addGroupMembershipInfo(membershipInfo);
                }
                
                // Update group membership info
                final URL editUrl = new URL(contactEntry.getEditLink().getHref());
                final ContactEntry updatedContact = contactsService.update(editUrl, contactEntry);
                messageContext.getEnvelope().detach();
                
                // Get Response in XML format of ContactFeed Object
                messageContext.setEnvelope(getGDataSOAPEnvelope(updatedContact, contactsService));
            } else {
                ValidationException validationException = new ValidationException("Contact ID is not found");
                log.error("Failed to validate : ", validationException);
                storeErrorResponseStatus(messageContext, validationException,
                        Constants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
                handleException("Failed to validate : ", validationException, messageContext);
            }
            
        } catch (ValidationException ve) {
            log.error("Failed to validate id: ", ve);
            storeErrorResponseStatus(messageContext, ve, Constants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
            handleException("Failed to validate id: ", ve, messageContext);
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
