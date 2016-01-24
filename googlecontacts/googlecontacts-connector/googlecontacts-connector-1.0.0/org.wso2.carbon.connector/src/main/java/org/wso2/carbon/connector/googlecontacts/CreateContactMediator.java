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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.synapse.MessageContext;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.contacts.Birthday;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.Event;
import com.google.gdata.data.contacts.FileAs;
import com.google.gdata.data.contacts.Nickname;
import com.google.gdata.data.contacts.Relation;
import com.google.gdata.data.contacts.Website;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.Im;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.StructuredPostalAddress;
import com.google.gdata.util.ServiceException;

/**
 * Creates a new contact within a Google Contacts account. Allows to add name, multiple addresses, multiple
 * phone numbers, multiple email addresses, multiple IM addresses, Birthday and multiple websites. Note that
 * each type of entry in the multiple category must include a relation name, either 'home', 'work', or
 * 'custom'. IM Addresses require protocol definitions, e.g. GOOGLE_TALK. Website definitions require
 * relations in one of the types; home, home_page, work, blog, profile or other.
 * 
 * @see https://developers.google.com/google-apps/contacts/v3/?hl=en#creating_contacts
 */
public final class CreateContactMediator extends AbstractGoogleContactsConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(final MessageContext messageContext) {
    
        final Map<String, String> createContactParameterMap =
                buildParameterMap(new String[] {Constants.NAME, Constants.FILE_AS,
                        Constants.NICKNAME, Constants.EMAIL, Constants.PHONE_NUMBER, Constants.IM,
                        Constants.STRUCTURED_POSTAL_ADDRESS, Constants.BIRTHDAY, Constants.URL, Constants.EVENTS,
                        Constants.USER_EMAIL, Constants.RELATIONSHIPS, Constants.NOTE}, messageContext);
        
        ContactEntry contactEntry = new ContactEntry();
        
        try {
            // Get and Set birthday details
            String birthdayValue = createContactParameterMap.get(Constants.BIRTHDAY);
            if (birthdayValue != null && !birthdayValue.isEmpty()) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT);
                dateFormatter.parse(birthdayValue);
                contactEntry.setBirthday(new Birthday(birthdayValue));
            }
            String tempElementString = createContactParameterMap.get(Constants.NAME);
            if (!tempElementString.isEmpty()) {
                // Get and Set name details
                OMElement nameElement = AXIOMUtil.stringToOM(tempElementString);
                Name name = getName(nameElement);
                contactEntry.setName(name);
            }
            tempElementString = createContactParameterMap.get(Constants.NICKNAME);
            if (!tempElementString.isEmpty()) {
                // Set nickname
                contactEntry.setNickname(new Nickname(tempElementString));
            }
            tempElementString = createContactParameterMap.get(Constants.FILE_AS);
            if (!tempElementString.isEmpty()) {
                // Set index to file the contact under
                contactEntry.setFileAs(new FileAs(tempElementString));
            }
            tempElementString = createContactParameterMap.get(Constants.EMAIL);
            if (!tempElementString.isEmpty()) {
                // Get and Set email details
                OMElement emailElement = AXIOMUtil.stringToOM(tempElementString);
                List<Email> emailList = getEmailList(emailElement);
                for (Email emailAddress : emailList) {
                    contactEntry.addEmailAddress(emailAddress);
                }
            }
            tempElementString = createContactParameterMap.get(Constants.PHONE_NUMBER);
            if (!tempElementString.isEmpty()) {
                // Get and Set phoneNo details
                OMElement phoneNumberElement = AXIOMUtil.stringToOM(tempElementString);
                List<PhoneNumber> phoneNumberList = getPhoneNumberList(phoneNumberElement);
                for (PhoneNumber phoneNo : phoneNumberList) {
                    contactEntry.addPhoneNumber(phoneNo);
                }
            }
            tempElementString = createContactParameterMap.get(Constants.IM);
            if (!tempElementString.isEmpty()) {
                // Get and Set im details
                OMElement iMElement = AXIOMUtil.stringToOM(tempElementString);
                List<Im> imList = getIMList(iMElement);
                for (Im im : imList) {
                    contactEntry.addImAddress(im);
                }
            }
            // Get and Set Notes
            String tempNoteString = createContactParameterMap.get(Constants.NOTE);
            if (tempNoteString != null && !tempNoteString.isEmpty()) {
                contactEntry.setContent(TextConstruct.plainText(tempNoteString));
            }
            tempElementString = createContactParameterMap.get(Constants.STRUCTURED_POSTAL_ADDRESS);
            if (!tempElementString.isEmpty()) {
                // Get and Set StructuredPostalAddress details
                OMElement addressElement = AXIOMUtil.stringToOM(tempElementString);
                List<StructuredPostalAddress> addressList = getAddressList(addressElement);
                for (StructuredPostalAddress address : addressList) {
                    contactEntry.addStructuredPostalAddress(address);
                }
            }
            tempElementString = createContactParameterMap.get(Constants.URL);
            if (!tempElementString.isEmpty()) {
                // Get and Set web site details
                OMElement urlElement = AXIOMUtil.stringToOM(tempElementString);
                List<Website> urlList = getWebSiteList(urlElement);
                for (Website website : urlList) {
                    contactEntry.addWebsite(website);
                }
            }
            tempElementString = createContactParameterMap.get(Constants.EVENTS);
            if (!tempElementString.isEmpty()) {
                // Get and set event details
                OMElement eventsElement = AXIOMUtil.stringToOM(tempElementString);
                List<Event> eventList = getEventsList(eventsElement);
                for (Event event : eventList) {
                    contactEntry.addEvent(event);
                }
            }
            tempElementString = createContactParameterMap.get(Constants.RELATIONSHIPS);
            if (!tempElementString.isEmpty()) {
                // Get and set relationship details
                OMElement relationshipsElement = AXIOMUtil.stringToOM(tempElementString);
                List<Relation> relationList = getRelationList(relationshipsElement);
                for (Relation relation : relationList) {
                    contactEntry.addRelation(relation);
                }
            }
            // Authentication Token Setup
            ContactsService contactsService = getContactService(messageContext);
            // Get End point request URL created for contacts
            StringBuilder requestUrl = getRequestURLBuilder(messageContext, Constants.REQUEST_URL_CONTACTS);
            requestUrl.append(Constants.REQUEST_URL_GENERIC_END);
            // Call Google Contact Service insert method
            ContactEntry createdContact = contactsService.insert(new URL(requestUrl.toString()), contactEntry);
            messageContext.getEnvelope().getBody().getFirstElement().detach();
            // Get XML format of ContactEntry Object and set to messageContext
            messageContext.setEnvelope(getGDataSOAPEnvelope(createdContact, contactsService));
            
        } catch (XMLStreamException xmlse) {
            log.error("Error parsing XML stream.", xmlse);
            storeErrorResponseStatus(messageContext, xmlse, Constants.ERROR_CODE_XML_STRM_PARSE_FAILURE);
            handleException("Error parsing XML stream.", xmlse, messageContext);
        } catch (MalformedURLException mue) {
            log.error("Query URL is not well-formed.", mue);
            storeErrorResponseStatus(messageContext, mue, Constants.ERROR_CODE_MALFORMED_URL_EXCEPTION);
            handleException("Query URL is not well-formed", mue, messageContext);
        } catch (ServiceException se) {
            log.error("Error creating service object.", se);
            storeErrorResponseStatus(messageContext, se, Constants.ERROR_CODE_SERVICE_EXCEPTION);
            handleException("Error creating service object.", se, messageContext);
        } catch (IOException ioe) {
            log.error("Error sending request.", ioe);
            storeErrorResponseStatus(messageContext, ioe, Constants.ERROR_CODE_IO_EXCEPTION);
            handleException("Error sending request.", ioe, messageContext);
        } catch (ParseException pe) {
            log.error("The format of the date you have provided is invalid.", pe);
            storeErrorResponseStatus(messageContext, pe, Constants.ERROR_CODE_PARSE_EXCEPTION);
            handleException("The format of the date you have provided is invalid.", pe, messageContext);
        } catch (NumberFormatException nfe) {
            log.error("The format of the date you have provided is invalid.", nfe);
            storeErrorResponseStatus(messageContext, nfe, Constants.ERROR_CODE_PARSE_EXCEPTION);
            handleException("The format of the date you have provided is invalid.", nfe, messageContext);
        } catch (RuntimeException re) {
            log.error("Error occured in connector: ", re);
            storeErrorResponseStatus(messageContext, re, Constants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException("Error occured in connector: ", re, messageContext);
        }
    }
}
