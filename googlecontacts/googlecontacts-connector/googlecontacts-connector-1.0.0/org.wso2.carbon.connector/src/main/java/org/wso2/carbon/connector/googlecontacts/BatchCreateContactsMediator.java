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
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.synapse.MessageContext;

import com.google.gdata.client.batch.BatchInterruptedException;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.contacts.Birthday;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
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
 * The Class BatchCreateContactsMediator handles the batch operations for contacts of Google Contacts
 * connector.
 * 
 * @see https://developers.google.com/google-apps/contacts/v3/#batch_operations_for_contacts
 */
public final class BatchCreateContactsMediator extends AbstractGoogleContactsConnector {
    
    /**
     * Handle batch operations of contacts.
     * 
     * @param messageContext ESB message context.
     */
    public void connect(final MessageContext messageContext) {
    
        final String batchContacts = (String) getParameter(messageContext, Constants.BATCH_CONTACTS);
        
        try {
            
            // Create the batch operation request URL.
            final String batchRequestUrl =
                    getRequestURLBuilder(messageContext, Constants.REQUEST_URL_CONTACTS)
                            .append(Constants.REQUEST_URL_GENERIC_END).append(Constants.FORWARD_SLASH)
                            .append(Constants.BATCH).toString();
            
            // Submit the batch request to the server.
            final ContactsService contactsService = getContactService(messageContext);
            final ContactFeed responseFeed =
                    contactsService.batch(new URL(batchRequestUrl),
                            generateRequestContactFeed(messageContext, batchContacts));
            
            // Build new SOAP envelope to return to client.
            messageContext.getEnvelope().detach();
            messageContext.setEnvelope(getGDataSOAPEnvelope(responseFeed, contactsService));
            
        } catch (ValidationException ve) {
            log.error("Failed to validate contact id: ", ve);
            storeErrorResponseStatus(messageContext, ve, Constants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
            handleException("Failed to validate contact id: ", ve, messageContext);
        } catch (MalformedURLException mue) {
            log.error("Error reading contact entry from built URL: ", mue);
            storeErrorResponseStatus(messageContext, mue, Constants.ERROR_CODE_MALFORMED_URL_EXCEPTION);
            handleException("Error reading contact entry from built URL: ", mue, messageContext);
        } catch (XMLStreamException xmlse) {
            log.error("Error parsing XML stream.", xmlse);
            storeErrorResponseStatus(messageContext, xmlse, Constants.ERROR_CODE_XML_STRM_PARSE_FAILURE);
            handleException("Error parsing XML stream.", xmlse, messageContext);
        } catch (ParseException pe) {
            log.error("Error parsing date.", pe);
            storeErrorResponseStatus(messageContext, pe, Constants.ERROR_CODE_PARSE_EXCEPTION);
            handleException("Error parsing date: ", pe, messageContext);
        } catch (BatchInterruptedException bie) {
            log.error("Batch operation interrupted: ", bie);
            storeErrorResponseStatus(messageContext, bie, Constants.ERROR_CODE_BATCH_INTERRUPTED_EXCEPTION);
            handleException("Batch operation interrupted: ", bie, messageContext);
        } catch (ServiceException se) {
            log.error("Service unavailable: ", se);
            storeErrorResponseStatus(messageContext, se, Constants.ERROR_CODE_SERVICE_EXCEPTION);
            handleException("Service unavailable: ", se, messageContext);
        } catch (IOException io) {
            log.error("Failed to access entry: ", io);
            storeErrorResponseStatus(messageContext, io, Constants.ERROR_CODE_IO_EXCEPTION);
            handleException("Failed to access entry: ", io, messageContext);
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
    
    /**
     * Generate the ContactFeed object containing all the entries for the batch operation.
     * 
     * @param messageContext ESB message context.
     * @param batchContacts String containing the batch contacts details.
     * @return ContactFeed object containing entries of the batch.
     * @throws ValidationException handling errors in the contact ID validation.
     * @throws XMLStreamException unexpected processing conditions.
     * @throws ParseException unexpected issue in parsing.
     */
    private ContactFeed generateRequestContactFeed(final MessageContext messageContext, final String batchContacts)
            throws ValidationException, XMLStreamException, ParseException {
    
        final ContactFeed requestFeed = new ContactFeed();
        
        if (batchContacts != null && !batchContacts.isEmpty()) {
            // Get all the batch entry elements.
            final OMElement batchElement = AXIOMUtil.stringToOM(batchContacts);
            final Iterator< ? > batchElemIterator = batchElement.getChildElements();
            
            final QName typeQName = new QName(Constants.TYPE);
            while (batchElemIterator.hasNext()) {
                final OMElement batchEntry = (OMElement) batchElemIterator.next();
                final OMElement typeElement = batchEntry.getFirstChildWithName(typeQName);
                
                if (typeElement == null) {
                    throw new ValidationException("Batch operation type is not found.");
                } else {
                    
                    final String entryType = typeElement.getText();
                    
                    // Call specific operation method depending on the entry type.
                    if (Constants.QUERY.equals(entryType)) {
                        requestFeed.getEntries().add(getRetrieveContact(messageContext, batchEntry));
                    } else if (Constants.INSERT.equals(entryType)) {
                        requestFeed.getEntries().add(getCreateContact(batchEntry));
                    } else if (Constants.UPDATE.equals(entryType)) {
                        requestFeed.getEntries().add(getUpdateContact(messageContext, batchEntry));
                    } else if (Constants.DELETE.equals(entryType)) {
                        requestFeed.getEntries().add(getDeleteContact(messageContext, batchEntry));
                    } else {
                        throw new ValidationException("Invalid batch operation type.");
                    }
                }
            }
        }
        
        return requestFeed;
    }
    
    /**
     * Method to generate the ContactEntry object for batch retrieval.
     * 
     * @param messageContext ESB message context.
     * @param entryElement OMElement object containing a batch entry.
     * @return ContactEntry object for batch operations.
     */
    private ContactEntry getRetrieveContact(final MessageContext messageContext, final OMElement entryElement) {
    
        final ContactEntry contactEntry = generateContactEntry(messageContext, entryElement);
        
        // Set the batch related details.
        BatchUtils.setBatchOperationType(contactEntry, BatchOperationType.QUERY);
        final OMElement batchIdElem = entryElement.getFirstChildWithName(new QName(Constants.ID));
        if (batchIdElem != null) {
            BatchUtils.setBatchId(contactEntry, batchIdElem.getText());
        }
        
        return contactEntry;
    }
    
    /**
     * Method to generate the ContactEntry object for batch create.
     * 
     * @param entryElement OMElement object containing a batch entry.
     * @return ContactEntry object for batch operations.
     * @throws XMLStreamException unexpected processing conditions.
     * @throws ParseException unexpected issue in parsing.
     */
    private ContactEntry getCreateContact(final OMElement entryElement) throws XMLStreamException, ParseException {
    
        final ContactEntry contactEntry = new ContactEntry();
        setContactDetails(contactEntry, entryElement);
        
        // Set the batch related details.
        BatchUtils.setBatchOperationType(contactEntry, BatchOperationType.INSERT);
        final OMElement batchIdElem = entryElement.getFirstChildWithName(new QName(Constants.ID));
        if (batchIdElem != null) {
            BatchUtils.setBatchId(contactEntry, batchIdElem.getText());
        }
        
        return contactEntry;
    }
    
    /**
     * Method to generate the ContactEntry object for batch update.
     * 
     * @param messageContext ESB message context.
     * @param entryElement OMElement object containing a batch entry.
     * @return ContactEntry object for batch operations.
     * @throws ParseException unexpected issue in parsing.
     * @throws XMLStreamException unexpected processing conditions.
     */
    private ContactEntry getUpdateContact(final MessageContext messageContext, final OMElement entryElement)
            throws ParseException, XMLStreamException {
    
        final ContactEntry contactEntry = generateContactEntry(messageContext, entryElement);
        
        contactEntry.setEtag(Constants.ETAG);
        
        // Set new details.
        setContactDetails(contactEntry, entryElement);
        
        // Set the batch related details.
        BatchUtils.setBatchOperationType(contactEntry, BatchOperationType.UPDATE);
        final OMElement batchIdElem = entryElement.getFirstChildWithName(new QName(Constants.ID));
        if (batchIdElem != null) {
            BatchUtils.setBatchId(contactEntry, batchIdElem.getText());
        }
        
        return contactEntry;
    }
    
    /**
     * Method to generate the ContactEntry object for batch delete.
     * 
     * @param messageContext ESB message context.
     * @param entryElement OMElement object containing a batch entry.
     * @return ContactEntry object for batch operations.
     */
    private ContactEntry getDeleteContact(final MessageContext messageContext, final OMElement entryElement) {
    
        final ContactEntry contactEntry = generateContactEntry(messageContext, entryElement);
        
        contactEntry.setEtag(Constants.ETAG);
        
        // Set the batch related details.
        BatchUtils.setBatchOperationType(contactEntry, BatchOperationType.DELETE);
        final OMElement batchIdElem = entryElement.getFirstChildWithName(new QName(Constants.ID));
        if (batchIdElem != null) {
            BatchUtils.setBatchId(contactEntry, batchIdElem.getText());
        }
        
        return contactEntry;
    }
    
    /**
     * Generate a ContactEntry object for batch processing by setting contact ID.
     * 
     * @param messageContext ESB message context.
     * @param entryElement OMElement object containing a batch entry.
     * @return ContactEntry object for batch operations.
     */
    private ContactEntry generateContactEntry(final MessageContext messageContext, final OMElement entryElement) {
    
        final OMElement contactIdElem = entryElement.getFirstChildWithName(new QName(Constants.CONTACT_ID));
        
        final ContactEntry contactEntry = new ContactEntry();
        if (contactIdElem != null) {
            
            final String requestUrl =
                    getRequestURLBuilder(messageContext, Constants.REQUEST_URL_CONTACTS)
                            .append(Constants.REQUEST_URL_GENERIC_END).append(Constants.FORWARD_SLASH)
                            .append(contactIdElem.getText()).toString();
            
            contactEntry.setId(requestUrl);
        }
        
        return contactEntry;
    }
    
    /**
     * Set the contact details provided with OMElement object to the ContactEntry object.
     * 
     * @param contactEntry object to which the values should be added.
     * @param entryElement OMElement object containing the entry.
     * @throws XMLStreamException unexpected processing conditions.
     * @throws ParseException unexpected issue in parsing.
     */
    private void setContactDetails(final ContactEntry contactEntry, final OMElement entryElement)
            throws XMLStreamException, ParseException {
    
        // Get and Set the birthday.
        final OMElement birthdayElem = entryElement.getFirstChildWithName(new QName(Constants.BIRTHDAY));
        if (birthdayElem != null) {
            final String birthdayValue = birthdayElem.getText();
            if (birthdayValue != null && !birthdayValue.isEmpty()) {
                contactEntry.setBirthday(new Birthday(birthdayValue));
            }
        }
        // Get and Set the nickname.
        final OMElement nicknameElem = entryElement.getFirstChildWithName(new QName(Constants.NICKNAME));
        if (nicknameElem != null) {
            final String nicknameValue = nicknameElem.getText();
            if (nicknameValue != null && !nicknameValue.isEmpty()) {
                contactEntry.setNickname(new Nickname(nicknameValue));
            }
        }
        // Get and Set the file as indexable value.
        final OMElement fileAsElem = entryElement.getFirstChildWithName(new QName(Constants.FILE_AS));
        if (fileAsElem != null) {
            final String fileAsValue = fileAsElem.getText();
            if (fileAsValue != null && !fileAsValue.isEmpty()) {
                contactEntry.setFileAs(new FileAs(fileAsValue));
            }
        }
        // Get and Set the names.
        final OMElement nameElem = entryElement.getFirstChildWithName(new QName(Constants.NAME));
        if (nameElem != null) {
            final Name name = getName(nameElem);
            contactEntry.setName(name);
        }
        
        // Get and Set the email.
        final OMElement emailElem = entryElement.getFirstChildWithName(new QName(Constants.EMAIL));
        if (emailElem != null) {
            final List<Email> emailList = getEmailList(emailElem);
            for (Email emailAddress : emailList) {
                contactEntry.addEmailAddress(emailAddress);
            }
        }
        
        // Get and Set the phone number.
        final OMElement phoneNumElem = entryElement.getFirstChildWithName(new QName(Constants.PHONE_NUMBER));
        if (phoneNumElem != null) {
            final List<PhoneNumber> phoneNumberList = getPhoneNumberList(phoneNumElem);
            for (PhoneNumber phoneNo : phoneNumberList) {
                contactEntry.addPhoneNumber(phoneNo);
            }
        }
        
        // Get and Set the IM Addresses.
        final OMElement imElem = entryElement.getFirstChildWithName(new QName(Constants.IM));
        if (imElem != null) {
            final List<Im> imList = getIMList(imElem);
            for (Im im : imList) {
                contactEntry.addImAddress(im);
            }
        }
        
        // Get and Set the Note.
        final OMElement noteElem = entryElement.getFirstChildWithName(new QName(Constants.NOTE));
        if (noteElem != null) {
            final String noteValue = noteElem.getText();
            if (noteValue != null && !noteValue.isEmpty()) {
                contactEntry.setContent(TextConstruct.plainText(noteValue));
            }
        }
        
        // Get and Set the structured postal addresses.
        final OMElement addressElem =
                entryElement.getFirstChildWithName(new QName(Constants.STRUCTURED_POSTAL_ADDRESS));
        if (addressElem != null) {
            final List<StructuredPostalAddress> addressList = getAddressList(addressElem);
            for (StructuredPostalAddress address : addressList) {
                contactEntry.addStructuredPostalAddress(address);
            }
        }
        
        // Get and Set the URLs.
        final OMElement urlElement = entryElement.getFirstChildWithName(new QName(Constants.URL));
        if (urlElement != null) {
            final List<Website> urlList = getWebSiteList(urlElement);
            for (Website website : urlList) {
                contactEntry.addWebsite(website);
            }
        }
        
        // Get and Set the events.
        final OMElement eventsElement = entryElement.getFirstChildWithName(new QName(Constants.EVENTS));
        if (eventsElement != null) {
            final List<Event> eventList = getEventsList(eventsElement);
            for (Event event : eventList) {
                contactEntry.addEvent(event);
            }
        }
        
        // Get and Set the relationships.
        final OMElement relationshipsElem = entryElement.getFirstChildWithName(new QName(Constants.RELATIONSHIPS));
        if (relationshipsElem != null) {
            final List<Relation> relationList = getRelationList(relationshipsElem);
            for (Relation relation : relationList) {
                contactEntry.addRelation(relation);
            }
        }
    }
    
}
