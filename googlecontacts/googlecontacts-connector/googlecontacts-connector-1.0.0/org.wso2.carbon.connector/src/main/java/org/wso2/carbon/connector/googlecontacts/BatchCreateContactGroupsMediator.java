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
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.synapse.MessageContext;

import com.google.gdata.client.batch.BatchInterruptedException;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.util.ServiceException;

/**
 * Class to send a batch request for operations on contact groups, send an authorized POST request to the
 * contact groups batch feed URL with the batch feed data in the request body.
 * 
 * @see https://developers.google.com/google-apps/contacts/v3/?hl=ja#batch_operations_for_contact_groups
 */
public final class BatchCreateContactGroupsMediator extends AbstractGoogleContactsConnector {
    
    /**
     * This method performs all the batch operations(query, insert, update, delete) on listed contact group
     * entries of a particular user.
     * 
     * @param messageContext ESB message context.
     */
    
    public void connect(final MessageContext messageContext) {
    
        // Get all the batch operation entries from message context.
        final String batchGroupEntries = (String) getParameter(messageContext, Constants.BATCH_GROUPS);
        
        try {
            
            // Check batch entries are available.
            if (batchGroupEntries != null && !batchGroupEntries.isEmpty()) {
                
                // Holds all the batch request entries returned.
                final ContactGroupFeed requestFeed = generateRequestGroupFeed(messageContext, batchGroupEntries);
                
                // Authenticate with google contacts API and get returning contacts Service.
                final ContactsService contactsService = getContactService(messageContext);
                
                // Build the batch end point request URL.
                final String batchUrl =
                        getRequestURLBuilder(messageContext, Constants.REQUEST_URL_GROUPS)
                                .append(Constants.REQUEST_URL_GENERIC_END).append(Constants.FORWARD_SLASH)
                                .append(Constants.BATCH).toString();
                
                // Submit the batch request to the server.
                final ContactGroupFeed responseFeed = contactsService.batch(new URL(batchUrl), requestFeed);
                
                // Build the new soap envelope using response feed.
                messageContext.getEnvelope().detach();
                messageContext.setEnvelope(getGDataSOAPEnvelope(responseFeed, contactsService));
            }
            
        } catch (ValidationException ve) {
            log.error("Failed to validate id: ", ve);
            storeErrorResponseStatus(messageContext, ve, Constants.ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION);
            handleException("Failed to validate id: ", ve, messageContext);
        } catch (MalformedURLException mue) {
            log.error("Error reading contact group entry from built URL: ", mue);
            storeErrorResponseStatus(messageContext, mue, Constants.ERROR_CODE_MALFORMED_URL_EXCEPTION);
            handleException("Error reading contact group entry from built URL: ", mue, messageContext);
        } catch (XMLStreamException xmlse) {
            log.error("Error parsing XML stream.", xmlse);
            storeErrorResponseStatus(messageContext, xmlse, Constants.ERROR_CODE_XML_STRM_PARSE_FAILURE);
            handleException("Error parsing XML stream.", xmlse, messageContext);
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
        } catch (RuntimeException re) {
            log.error("Error occured in connector: ", re);
            storeErrorResponseStatus(messageContext, re, Constants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException("Error occured in connector: ", re, messageContext);
        }
    }
    
    /**
     * This method iterate all the contact group entries according to batch operation type and generate the
     * whole request contacts group feed.
     * 
     * @param messageContext ESB message context
     * @param batchGroupEntries all the batch operation group entries as string.
     * @return the constructed request contact group feed.
     * @throws XMLStreamException If unexpected XML processing errors.
     * @throws ValidationException If validation errors has occurred.
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ServiceException If an error occurs while processing a GDataRequest.
     */
    
    private ContactGroupFeed generateRequestGroupFeed(final MessageContext messageContext,
            final String batchGroupEntries) throws XMLStreamException, ValidationException, IOException,
            ServiceException {
    
        // Feed that holds all the batch request entries.
        final ContactGroupFeed requestFeed = new ContactGroupFeed();
        
        final OMElement entryElements = AXIOMUtil.stringToOM(batchGroupEntries);
        final Iterator< ? > entryIterator = entryElements.getChildElements();
        final QName qName = new QName(Constants.TYPE);
        
        // Iterate each and every batch entry to process separately.
        while (entryIterator.hasNext()) {
            
            // Get an entry and its operation type OMElement.
            final OMElement entryElement = (OMElement) entryIterator.next();
            final OMElement entryTypeElem = entryElement.getFirstChildWithName(qName);
            
            if (entryTypeElem == null) {
                throw new ValidationException("Batch operation type is not found");
            } else {
                
                final String entryType = entryTypeElem.getText();
                
                if (Constants.QUERY.equals(entryType)) {
                    // Insert the query type entry to the batch feed.
                    requestFeed.getEntries().add(retreiveContactGroup(messageContext, entryElement));
                } else if (Constants.INSERT.equals(entryType)) {
                    // Insert the insert type entry to the batch feed.
                    requestFeed.getEntries().add(createContactGroup(entryElement));
                } else if (Constants.UPDATE.equals(entryType)) {
                    // Insert the update type entry to the batch feed.
                    requestFeed.getEntries().add(updateContactGroup(messageContext, entryElement));
                } else if (Constants.DELETE.equals(entryType)) {
                    // Insert the delete type entry to the batch feed.
                    requestFeed.getEntries().add(deleteContactGroup(messageContext, entryElement));
                } else {
                    throw new ValidationException("Batch operation type is not found");
                }
            }
        }
        
        return requestFeed;
        
    }
    
    /**
     * This method returns the contact group entry for requested group id to perform query batch request.
     * 
     * @param messageContext ESB message context.
     * @param entryElement OMElement type entry.
     * @return the contact group entry for requested group id.
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ServiceException If an error occurs while processing a GDataRequest.
     */
    
    private ContactGroupEntry retreiveContactGroup(final MessageContext messageContext, final OMElement entryElement)
            throws IOException, ServiceException {
    
        // Get group id OMElement from entryElement.
        final OMElement groupIdElem = entryElement.getFirstChildWithName(new QName(Constants.GROUP_ID));
        
        // Create a ContactEntry for the retrieve request.
        final ContactGroupEntry queryGroup = generateContactGroupEntry(messageContext, groupIdElem);
        
        // Get the batch request Id from entry OMElement.
        final OMElement entryIdElem = entryElement.getFirstChildWithName(new QName(Constants.ID));
        
        // Sets batch id only if not null.
        if (entryIdElem != null) {
            BatchUtils.setBatchId(queryGroup, entryIdElem.getText());
        }
        
        BatchUtils.setBatchOperationType(queryGroup, BatchOperationType.QUERY);
        
        return queryGroup;
        
    }
    
    /**
     * This method returns the contact group entry for requested group id to perform create batch request.
     * 
     * @param entryElement OMElement type entry.
     * @return the contact group entry.
     */
    
    private ContactGroupEntry createContactGroup(final OMElement entryElement) {
    
        // Get the batch request Id from entry OMElement.
        final OMElement entryIdElem = entryElement.getFirstChildWithName(new QName(Constants.ID));
        
        // Create a ContactGroupEntry for the create request.
        final ContactGroupEntry createGroup = new ContactGroupEntry();
        
        // Get and set the contact group title.
        final OMElement titleElem = entryElement.getFirstChildWithName(new QName(Constants.TITLE));
        if (titleElem != null) {
            createGroup.setTitle(new PlainTextConstruct(titleElem.getText()));
        }
        
        // Get and set the Extended Properties to contact group.
        final OMElement extendedProps = entryElement.getFirstChildWithName(new QName(Constants.EXTENDED_PROPERTIES));
        if (extendedProps != null) {
            final List<ExtendedProperty> extendedPropList = getExtendedPropertyList(extendedProps);
            for (ExtendedProperty extendedPropertyItem : extendedPropList) {
                createGroup.addExtendedProperty(extendedPropertyItem);
            }
        }
        
        // Process the create type batch request.
        if (entryIdElem != null) {
            // Sets batch id only if not null.
            BatchUtils.setBatchId(createGroup, entryIdElem.getText());
        }
        
        BatchUtils.setBatchOperationType(createGroup, BatchOperationType.INSERT);
        
        return createGroup;
        
    }
    
    /**
     * This method returns the contact group entry for requested group id to perform update batch request.
     * 
     * @param messageContext ESB message context.
     * @param entryElement OMElement type entry.
     * @return the contact group entry for requested group id to update.
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ServiceException If an error occurs while processing a GDataRequest.
     */
    
    private ContactGroupEntry updateContactGroup(final MessageContext messageContext, final OMElement entryElement)
            throws IOException, ServiceException {
    
        // Get group id OMElement from entryElement.
        final OMElement groupIdElem = entryElement.getFirstChildWithName(new QName(Constants.GROUP_ID));
        
        // Retrieve the ContactGroupEntry to update.
        final ContactGroupEntry updateGroup = generateContactGroupEntry(messageContext, groupIdElem);
        
        // Get and set the title to contact group.
        final OMElement titleElem = entryElement.getFirstChildWithName(new QName(Constants.TITLE));
        if (titleElem != null) {
            updateGroup.setTitle(new PlainTextConstruct(titleElem.getText()));
        }
        
        // Get and set the content to contact group.
        final OMElement contentElem = entryElement.getFirstChildWithName(new QName(Constants.CONTENT));
        if (contentElem != null) {
            updateGroup.setContent(new PlainTextConstruct(contentElem.getText()));
        }
        
        // Get and set the Extended Properties to contact group.
        updateGroup.getExtendedProperties().clear();
        final OMElement extendedProps = entryElement.getFirstChildWithName(new QName(Constants.EXTENDED_PROPERTIES));
        
        if (extendedProps != null) {
            final List<ExtendedProperty> extendedPropList = getExtendedPropertyList(extendedProps);
            for (ExtendedProperty extendedPropertyItem : extendedPropList) {
                updateGroup.addExtendedProperty(extendedPropertyItem);
            }
        }
        
        // Get the batch request Id from entry OMElement.
        final OMElement entryIdElem = entryElement.getFirstChildWithName(new QName(Constants.ID));
        
        // Process the update type batch request.
        if (entryIdElem != null) {
            // Sets batch id only if not null.
            BatchUtils.setBatchId(updateGroup, entryIdElem.getText());
        }
        
        BatchUtils.setBatchOperationType(updateGroup, BatchOperationType.UPDATE);
        
        return updateGroup;
        
    }
    
    /**
     * This method returns the contact group entry for requested group id to perform delete batch request.
     * 
     * @param messageContext ESB message context.
     * @param entryElement OMElement type entry.
     * @return the contact group entry for requested group id to delete.
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ServiceException If an error occurs while processing a GDataRequest.
     */
    
    private ContactGroupEntry deleteContactGroup(final MessageContext messageContext, final OMElement entryElement)
            throws IOException, ServiceException {
    
        // Get group id OMElement from entryElement.
        final OMElement groupIdElem = entryElement.getFirstChildWithName(new QName(Constants.GROUP_ID));
        
        // Retrieve the ContactGroupEntry to delete.
        final ContactGroupEntry delContactGroup = generateContactGroupEntry(messageContext, groupIdElem);
        
        // Get the batch request Id from entry OMElement.
        final OMElement entryIdElem = entryElement.getFirstChildWithName(new QName(Constants.ID));
        
        // Process the delete type batch request.
        if (entryIdElem != null) {
            
            // Sets batch id only if not null.
            BatchUtils.setBatchId(delContactGroup, entryIdElem.getText());
        }
        
        BatchUtils.setBatchOperationType(delContactGroup, BatchOperationType.DELETE);
        
        return delContactGroup;
        
    }
    
    /**
     * This method create a new contact group entry and sets Id and Etag and then returns.
     * 
     * @param msgContext ESB message context.
     * @param groupIdElem OMElement for group id.
     * @return the contact group entry for requested group id.
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ServiceException If an error occurs while processing a GDataRequest.
     */
    
    private ContactGroupEntry generateContactGroupEntry(final MessageContext msgContext, final OMElement groupIdElem)
            throws IOException, ServiceException {
    
        // Create a ContactGroupEntry.
        final ContactGroupEntry contactGroupEntry = new ContactGroupEntry();
        
        if (groupIdElem != null) {
            
            // Build the end point request URL created for contact groups.
            final String requestUrl =
                    getRequestURLBuilder(msgContext, Constants.REQUEST_URL_GROUPS)
                            .append(Constants.REQUEST_URL_GENERIC_END).append(Constants.FORWARD_SLASH)
                            .append(groupIdElem.getText()).toString();
            // Set constructed end point url as contact entry id.
            contactGroupEntry.setId(requestUrl);
            
        }
        
        // Set Etag value as '*'.
        contactGroupEntry.setEtag(Constants.ETAG);
        
        return contactGroupEntry;
        
    }
    
}
