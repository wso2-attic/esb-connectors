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
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.synapse.MessageContext;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.util.ServiceException;

/**
 * The Class CreateContactGroupMediator handles the create contact group method of Google Contacts
 * connector.
 * 
 * @see https://developers.google.com/google-apps/contacts/v3/#creating_contact_groups
 */
public final class CreateContactGroupMediator extends AbstractGoogleContactsConnector {
    
    /**
     * Create a new contact group with provided title and the extended properties.
     * 
     * @param messageContext ESB message context.
     */
    public void connect(final MessageContext messageContext) {
    
        final String title = (String) getParameter(messageContext, Constants.TITLE);
        final String extendedProps = (String) getParameter(messageContext, Constants.EXTENDED_PROPERTIES);
        
        try {
            final ContactsService contactService = getContactService(messageContext);
            
            final String requestUrl =
                    getRequestURLBuilder(messageContext, Constants.REQUEST_URL_GROUPS).append(
                            Constants.REQUEST_URL_GENERIC_END).toString();
            
            final ContactGroupEntry newGroup = new ContactGroupEntry();
            newGroup.setTitle(new PlainTextConstruct(title));
            
            final String content = (String) getParameter(messageContext, Constants.CONTENT);
            if (content != null && !content.isEmpty()) {
                newGroup.setContent(new PlainTextConstruct(content));
            }
            
            // Get and set the Extended Properties.
            if (extendedProps != null && !extendedProps.isEmpty()) {
                final OMElement extendedPropElems = AXIOMUtil.stringToOM(extendedProps);
                final List<ExtendedProperty> extendedPropList = getExtendedPropertyList(extendedPropElems);
                for (ExtendedProperty extendedPropertyItem : extendedPropList) {
                    newGroup.addExtendedProperty(extendedPropertyItem);
                }
            }
            
            final ContactGroupEntry createdGroup = contactService.insert(new URL(requestUrl), newGroup);
            messageContext.getEnvelope().detach();
            
            messageContext.setEnvelope(getGDataSOAPEnvelope(createdGroup, contactService));
            
        } catch (MalformedURLException mue) {
            log.error("Error reading contact group entry from built URL: ", mue);
            storeErrorResponseStatus(messageContext, mue, Constants.ERROR_CODE_MALFORMED_URL_EXCEPTION);
            handleException("Error reading contact group entry from built URL: ", mue, messageContext);
        } catch (XMLStreamException xmlse) {
            log.error("Error parsing XML stream.", xmlse);
            storeErrorResponseStatus(messageContext, xmlse, Constants.ERROR_CODE_XML_STRM_PARSE_FAILURE);
            handleException("Error parsing XML stream.", xmlse, messageContext);
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
