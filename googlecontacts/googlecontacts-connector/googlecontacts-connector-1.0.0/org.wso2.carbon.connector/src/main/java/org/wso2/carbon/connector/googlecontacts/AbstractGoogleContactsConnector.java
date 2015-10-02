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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.transport.TransportUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.wso2.carbon.connector.core.AbstractConnector;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.contacts.Event;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.contacts.Relation;
import com.google.gdata.data.contacts.Website;
import com.google.gdata.data.contacts.Website.Rel;
import com.google.gdata.data.extensions.AdditionalName;
import com.google.gdata.data.extensions.City;
import com.google.gdata.data.extensions.Country;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.FamilyName;
import com.google.gdata.data.extensions.FormattedAddress;
import com.google.gdata.data.extensions.FullName;
import com.google.gdata.data.extensions.GivenName;
import com.google.gdata.data.extensions.Im;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.NamePrefix;
import com.google.gdata.data.extensions.NameSuffix;
import com.google.gdata.data.extensions.Neighborhood;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.PoBox;
import com.google.gdata.data.extensions.PostCode;
import com.google.gdata.data.extensions.Region;
import com.google.gdata.data.extensions.Street;
import com.google.gdata.data.extensions.StructuredPostalAddress;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.XmlBlob;
import com.google.gdata.util.common.xml.XmlWriter;

/**
 * Parent class for Google Contacts Cloud connector implementation. Contains authentication, error handling,
 * and response building methods.
 */
public abstract class AbstractGoogleContactsConnector extends AbstractConnector {
    
    /**
     * Creates a Google Contacts Service object which can be used to make requests via the SDK. This uses
     * Oauth2 access tokens and refresh tokens.
     * 
     * @param messageContext The message context for the connector request.
     * @return A Contacts Service object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected final ContactsService getContactService(final MessageContext messageContext) throws IOException {
    
        final HttpTransport transport = new NetHttpTransport();
        final JsonFactory jsonFactory = new JacksonFactory();
        final GoogleCredential credential =
                new GoogleCredential.Builder().setTransport(transport).setJsonFactory(jsonFactory).build()
                        .setAccessToken((String) messageContext.getProperty(Constants.ACCESS_TOKEN));
        final ContactsService contactsService =
                new ContactsService((String) messageContext.getProperty(Constants.APP_NAME));
        contactsService.setOAuth2Credentials(credential);
        return contactsService;
    }
    
    /**
     * Add a <strong>Throwable</strong> to a message context, the message from the throwable is embedded as
     * the Synapse contstant ERROR_MESSAGE.
     * 
     * @param ctxt Synapse Message Context to which the error tags need to be added
     * @param throwable Throwable that needs to be parsed and added
     * @param errorCode integer type error code to be added to ERROR_CODE Synapse constant
     */
    protected final void storeErrorResponseStatus(final MessageContext ctxt, final Throwable throwable,
            final int errorCode) {
    
        if (throwable instanceof ServiceException) {
            ctxt.setProperty(SynapseConstants.ERROR_DETAIL, ((ServiceException) throwable).getResponseBody());
        }
        
        ctxt.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, throwable.getMessage());
        ctxt.setFaultResponse(true);
    }
    
    /**
     * Takes an array of keys containing the names of optional parameters for a method, and builds an optional
     * parameter map for ease of use.
     * 
     * @param keys String array containing the parameter names for the method.
     * @param messageContext The message context sent to the connector.
     * @return A map containing the key value pairs for the connector method parameters.
     */
    protected final Map<String, String> buildParameterMap(final String[] keys, final MessageContext messageContext) {
    
        Map<String, String> parameterMap = new HashMap<String, String>();
        for (byte index = 0; index < keys.length; index++) {
            String paramValue =
                    (messageContext.getProperty(keys[index]) != null) ? (String) messageContext
                            .getProperty(keys[index]) : Constants.EMPTY_STR;
            parameterMap.put(keys[index], paramValue);
        }
        return parameterMap;
    }
    
    /**
     * Generates a SOAP envelope based on XML Serialization provided by GData(BaseEntry) API generate method.
     * 
     * @param baseEntry the object to be serialized.
     * @param contactsService Contacts Service object.
     * @return A SOAP Envelope containing the XML generated from the object.
     * @throws IOException On failure to initialize XML Writer.
     * @throws XMLStreamException On failure to convert XML String to OM Element.
     */
    protected final SOAPEnvelope getGDataSOAPEnvelope(final BaseEntry< ? > baseEntry,
            final ContactsService contactsService) throws IOException, XMLStreamException {
    
        Writer stringWriter = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(stringWriter);
        
        baseEntry.generate(xmlWriter, contactsService.getExtensionProfile());
        
        return TransportUtils.createSOAPEnvelope(AXIOMUtil.stringToOM(stringWriter.toString()));
    }
    
    /**
     * Generates a SOAP envelope based on XML Serialization provided by GData(BaseFeed) API generate method.
     * 
     * @param baseFeed The object to be serialized.
     * @param contactsService Contact Service object.
     * @return a SOAP envelope containing the serialized XML.
     * @throws IOException Thrown on failure to initialize the XML writer.
     * @throws XMLStreamException On failure to convert the XML String to OMElement.
     */
    public final SOAPEnvelope getGDataSOAPEnvelope(final BaseFeed< ? , ? > baseFeed,
            final ContactsService contactsService) throws IOException, XMLStreamException {
    
        Writer stringWriter = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(stringWriter);
        
        baseFeed.generate(xmlWriter, contactsService.getExtensionProfile());
        
        return TransportUtils.createSOAPEnvelope(AXIOMUtil.stringToOM(stringWriter.toString()));
    }
    
    /**
     * Builds an empty result envelope for methods which do not need to return a large XML body. e.g.
     * deleteContacts, deleteGroup, updateContactPhoto
     * 
     * @param namespace String value for namespace
     * @param resultTagName String tag for result
     * @return SOAP Envelope to be added to the message context
     * @throws IOException If a failure on parsing JSON
     */
    protected final SOAPEnvelope buildResultEnvelope(final String namespace, final String resultTagName)
            throws IOException {
    
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(namespace, "urn");
        OMElement resultTag = factory.createOMElement(resultTagName, ns);
        return TransportUtils.createSOAPEnvelope(resultTag);
    }
    
    /**
     * Builds an empty result envelope for methods which do not need to return a large XML body. e.g.
     * deleteContacts, deleteGroup, updateContactPhoto
     * 
     * @param namespace String value for namespace
     * @param resultTagName String tag for result
     * @param elements to be written to the envelope in Map format
     * @return SOAP Envelope to be added to the message context
     * @throws IOException If a failure on parsing JSON
     */
    protected final SOAPEnvelope buildResultEnvelope(final String namespace, final String resultTagName,
            final Map<String, String> elements) throws IOException {
    
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(namespace, "urn");
        OMElement resultTag = factory.createOMElement(resultTagName, ns);
        
        if (elements != null) {
            for (Entry<String, String> element : elements.entrySet()) {
                OMElement childElement = factory.createOMElement(new QName(element.getKey()));
                childElement.setText(element.getValue());
                resultTag.addChild(childElement);
            }
        }
        
        return TransportUtils.createSOAPEnvelope(resultTag);
    }
    
    /**
     * Builds the mapping for Website.Rel values. Assigns a set of Website.Rel values to string keys for ease
     * of use.
     * @return A map containing the Website Rel values mapped to keys.
     */
    private Map<String, Rel> buildWebsiteRelMap() {
    
        Map<String, Rel> websiteRelMap = new HashMap<String, Rel>();
        Rel[] relArray = Rel.values();
        for (int i = 0; i < relArray.length; i++) {
            Rel rel = relArray[i];
            websiteRelMap.put(Enum.valueOf(Rel.class, rel.name()).toValue(), rel);
        }
        return websiteRelMap;
    }
    
    /**
     * Builds the mapping for Relation.Rel values. Assigns a set of Relation.Rel values to string keys for
     * ease of use.
     * @return A map containing the Relation Rel values mapped to keys.
     */
    private Map<String, Relation.Rel> buildRelationRelMap() {
    
        Map<String, Relation.Rel> relationRelMap = new HashMap<String, Relation.Rel>();
        Relation.Rel[] relArray = Relation.Rel.values();
        for (int i = 0; i < relArray.length; i++) {
            Relation.Rel rel = relArray[i];
            relationRelMap.put(Enum.valueOf(Relation.Rel.class, rel.name()).toValue(), rel);
        }
        return relationRelMap;
    }
    
    /**
     * Returns a list containing Email GData extension objects. This method takes a formatted OM Element and
     * converts it to a list of email objects.
     * 
     * @param emailElement The parent element containing the individual elements containing email details.
     * @return a list containing GData extension Email objects.
     */
    protected final List<Email> getEmailList(final OMElement emailElement) {
    
        List<Email> emailList = new ArrayList<Email>();
        Iterator< ? > emailChildrenItearator = emailElement.getChildElements();
        while (emailChildrenItearator.hasNext()) {
            OMElement singleEmailElement = (OMElement) emailChildrenItearator.next();
            Email email = new Email();
            email.setAddress(singleEmailElement.getText());
            email.setRel(Constants.REL_OPEN + singleEmailElement.getLocalName());
            if (singleEmailElement.getAttributeValue(new QName(Constants.PRIMARY)) != null) {
                email.setPrimary(Boolean.parseBoolean(singleEmailElement
                        .getAttributeValue(new QName(Constants.PRIMARY))));
            }
            emailList.add(email);
        }
        return emailList;
    }
    
    /**
     * Returns a list containing Phone Number GData extension objects. This method takes a formatted OM
     * Element and converts it to a list of phone number objects.
     * 
     * @param phoneNumberElement The parent element containing the individual elements containing phone number
     *        details.
     * @return a list containing GData extension PhoneNumber objects.
     */
    protected final List<PhoneNumber> getPhoneNumberList(final OMElement phoneNumberElement) {
    
        List<PhoneNumber> phoneNoList = new ArrayList<PhoneNumber>();
        Iterator< ? > phoneNumberIterator = phoneNumberElement.getChildElements();
        while (phoneNumberIterator.hasNext()) {
            OMElement singlePhoneNumberElement = (OMElement) phoneNumberIterator.next();
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setPhoneNumber(singlePhoneNumberElement.getText());
            phoneNumber.setRel(Constants.REL_OPEN + singlePhoneNumberElement.getLocalName());
            if (singlePhoneNumberElement.getAttributeValue(new QName(Constants.PRIMARY)) != null) {
                phoneNumber.setPrimary(Boolean.parseBoolean(singlePhoneNumberElement.getAttributeValue(new QName(
                        Constants.PRIMARY))));
            }
            phoneNoList.add(phoneNumber);
        }
        return phoneNoList;
    }
    
    /**
     * Returns a list containing Im GData extension objects. This method takes a formatted OM Element and
     * converts it to a list of instant messaging data objects.
     * 
     * @param iMElement The parent element containing the individual elements containing instant messaging
     *        details.
     * @return a list containing GData extension Im objects.
     */
    protected final List<Im> getIMList(final OMElement iMElement) {
    
        List<Im> imList = new ArrayList<Im>();
        
        Iterator< ? > iMIterator = iMElement.getChildElements();
        while (iMIterator.hasNext()) {
            OMElement singleIMElement = (OMElement) iMIterator.next();
            Im im = new Im();
            OMElement tempElement = singleIMElement.getFirstChildWithName(new QName(Constants.ADDRESS));
            if (tempElement != null) {
                im.setAddress(tempElement.getText());
            }
            tempElement = singleIMElement.getFirstChildWithName(new QName(Constants.PROTOCOL));
            if (tempElement != null) {
                im.setProtocol(Constants.REL_OPEN + tempElement.getText());
            }
            im.setRel(Constants.REL_OPEN + singleIMElement.getLocalName());
            if (singleIMElement.getAttributeValue(new QName(Constants.PRIMARY)) != null) {
                im.setPrimary(Boolean.valueOf(singleIMElement.getAttributeValue(new QName(Constants.PRIMARY))));
            }
            imList.add(im);
        }
        
        return imList;
    }
    
    /**
     * Returns a list containing Structured Postal Address GData extension objects. This method takes a
     * formatted OM Element and converts it to a list of structured postal address objects.
     * 
     * @param addressElement The parent element containing the individual elements containing postal address
     *        details.
     * @return a list containing GData extension StructuredPostalAddress objects.
     */
    protected final List<StructuredPostalAddress> getAddressList(final OMElement addressElement) {
    
        List<StructuredPostalAddress> addressList = new ArrayList<StructuredPostalAddress>();
        
        Iterator< ? > addressChildrenIterator = addressElement.getChildElements();
        while (addressChildrenIterator.hasNext()) {
            OMElement singleAddress = (OMElement) addressChildrenIterator.next();
            StructuredPostalAddress structuredAddress = new StructuredPostalAddress();
            OMElement tempAddressElement = singleAddress.getFirstChildWithName(new QName(Constants.CITY));
            if (tempAddressElement != null) {
                structuredAddress.setCity(new City(tempAddressElement.getText()));
            }
            tempAddressElement = singleAddress.getFirstChildWithName(new QName(Constants.REGION));
            if (tempAddressElement != null) {
                structuredAddress.setRegion(new Region(tempAddressElement.getText()));
            }
            tempAddressElement = singleAddress.getFirstChildWithName(new QName(Constants.STREET));
            if (tempAddressElement != null) {
                structuredAddress.setStreet(new Street(tempAddressElement.getText()));
            }
            tempAddressElement = singleAddress.getFirstChildWithName(new QName(Constants.COUNTRY));
            if (tempAddressElement != null) {
                structuredAddress.setCountry(new Country(tempAddressElement.getAttributeValue(new QName(
                        Constants.COUNTRY_CODE)), tempAddressElement.getText()));
            }
            tempAddressElement = singleAddress.getFirstChildWithName(new QName(Constants.FORMATTED_ADDRESS));
            if (tempAddressElement != null) {
                structuredAddress.setFormattedAddress(new FormattedAddress(tempAddressElement.getText()));
            }
            tempAddressElement = singleAddress.getFirstChildWithName(new QName(Constants.NEIGHBORHOOD));
            if (tempAddressElement != null) {
                structuredAddress.setNeighborhood(new Neighborhood(tempAddressElement.getText()));
            }
            tempAddressElement = singleAddress.getFirstChildWithName(new QName(Constants.PO_BOX));
            if (tempAddressElement != null) {
                structuredAddress.setPobox(new PoBox(tempAddressElement.getText()));
            }
            tempAddressElement = singleAddress.getFirstChildWithName(new QName(Constants.POST_CODE));
            if (tempAddressElement != null) {
                structuredAddress.setPostcode(new PostCode(tempAddressElement.getText()));
            }
            if (singleAddress.getAttributeValue(new QName(Constants.PRIMARY)) != null) {
                structuredAddress.setPrimary(Boolean.valueOf(singleAddress.getAttributeValue(new QName(
                        Constants.PRIMARY))));
            }
            structuredAddress.setRel(Constants.REL_OPEN + singleAddress.getLocalName());
            
            addressList.add(structuredAddress);
            
        }
        return addressList;
    }
    
    /**
     * Returns a list containing Website GData extension objects. This method takes a formatted OM Element and
     * converts it to a list of website objects.
     * 
     * @param urlElement The parent element containing the individual elements
     * @return a list containing GData extension Website objects.
     */
    protected final List<Website> getWebSiteList(final OMElement urlElement) {
    
        Map<String, Rel> websiteRelMap = buildWebsiteRelMap();
        List<Website> webSiteList = new ArrayList<Website>();
        
        Iterator< ? > urlIterator = urlElement.getChildElements();
        while (urlIterator.hasNext()) {
            OMElement singleUrlElement = (OMElement) urlIterator.next();
            String element = singleUrlElement.getText();
            if (element != null && !element.isEmpty()) {
                Website website = new Website();
                website.setHref(element);
                website.setRel(websiteRelMap.get(singleUrlElement.getLocalName()));
                if (singleUrlElement.getAttributeValue(new QName(Constants.PRIMARY)) != null) {
                    website.setPrimary(Boolean.valueOf(singleUrlElement.getAttributeValue(
                            new QName(Constants.PRIMARY))));
                }
                webSiteList.add(website);
            }
        }
        return webSiteList;
    }
    
    /**
     * Returns a list containing Event GData extension objects. This method takes a formatted OM Element and
     * converts it to a list of event objects.
     * 
     * @param eventsElement The parent element containing the individual elements containing event details.
     * @return a list containing GData extension Event objects.
     */
    protected final List<Event> getEventsList(final OMElement eventsElement) {
    
        List<Event> eventList = new ArrayList<Event>();
        Iterator< ? > eventsIterator = eventsElement.getChildElements();
        while (eventsIterator.hasNext()) {
            OMElement singleEventElement = (OMElement) eventsIterator.next();
            Event event = new Event();
            String eventType = singleEventElement.getLocalName();
            if (Event.Rel.ANNIVERSARY.equals(eventType)) {
                event.setRel(Event.Rel.ANNIVERSARY);
            } else {
                event.setRel(Event.Rel.OTHER);
            }
            When when = new When();
            String dateString = singleEventElement.getText();
            when.setStartTime(DateTime.parseDate(dateString));
            
            event.setWhen(when);
            eventList.add(event);
        }
        return eventList;
    }
    
    /**
     * Returns a list containing Relation GData extension objects. This method takes a formatted OM Element
     * and converts it to a list of event objects.
     * 
     * @param relationsElement The parent element containing the individual elements.
     * @return a list containing GData extension Relation objects.
     */
    protected final List<Relation> getRelationList(final OMElement relationsElement) {
    
        Map<String, Relation.Rel> relationRelMap = buildRelationRelMap();
        List<Relation> relationList = new ArrayList<Relation>();
        Iterator< ? > relationIterator = relationsElement.getChildElements();
        while (relationIterator.hasNext()) {
            OMElement singleRelationElement = (OMElement) relationIterator.next();
            Relation relation = new Relation();
            relation.setRel(relationRelMap.get(singleRelationElement.getLocalName()));
            relation.setValue(singleRelationElement.getText());
            relationList.add(relation);
            
        }
        return relationList;
    }
    
    /**
     * Returns a list containing Name type GData extension objects. This method takes a formatted OM Element
     * and converts it to a list of name objects.
     * 
     * @param nameElement the name element
     * @return a list containing GData extension Event objects.
     * @throws XMLStreamException the xML stream exception
     */
    protected final Name getName(final OMElement nameElement) throws XMLStreamException {
    
        Iterator< ? > nameIterator = nameElement.getChildElements();
        Name name = new Name();
        while (nameIterator.hasNext()) {
            OMElement singleNameElement = (OMElement) nameIterator.next();
            String elementValue = singleNameElement.getText();
            if (elementValue != null && !elementValue.isEmpty()) {
                if (Constants.NAME_PREFIX.equalsIgnoreCase(singleNameElement.getLocalName())) {
                    name.setNamePrefix(new NamePrefix(elementValue));
                } else if (Constants.FULL_NAME.equalsIgnoreCase(singleNameElement.getLocalName())) {
                    name.setFullName(new FullName(elementValue, null));
                } else if (Constants.GIVEN_NAME.equalsIgnoreCase(singleNameElement.getLocalName())) {
                    name.setGivenName(new GivenName(elementValue, null));
                } else if (Constants.FAMILY_NAME.equalsIgnoreCase(singleNameElement.getLocalName())) {
                    name.setFamilyName(new FamilyName(elementValue, null));
                } else if (Constants.ADDITIONAL_NAME.equalsIgnoreCase(singleNameElement.getLocalName())) {
                    name.setAdditionalName(new AdditionalName(elementValue, null));
                } else if (Constants.NAME_SUFFIX.equalsIgnoreCase(singleNameElement.getLocalName())) {
                    name.setNameSuffix(new NameSuffix(elementValue));
                } 
            }
        }
        return name;
    }
    
    /**
     * Generate a list of extended properties from the given OMElement.
     * 
     * @param extendedPropertyElements OMElement containing the extended properties.
     * @return a list of ExtendedProperty objects.
     */
    protected final List<ExtendedProperty> getExtendedPropertyList(final OMElement extendedPropertyElements) {
    
        List<ExtendedProperty> extendedPropertyList = new ArrayList<ExtendedProperty>();
        Iterator< ? > extendedPropertyIterator = extendedPropertyElements.getChildElements();
        while (extendedPropertyIterator.hasNext()) {
            OMElement extendedPropertyElement = (OMElement) extendedPropertyIterator.next();
            ExtendedProperty extendedProperty = new ExtendedProperty();
            String nameAttribute = extendedPropertyElement.getAttributeValue(new QName(Constants.NAME));
            if (nameAttribute != null) {
                extendedProperty.setName(nameAttribute);
            }
            Iterator< ? > extendedPropertyChildIterator = extendedPropertyElement.getChildElements();
            // In case where the value is set inside <info> tags or else as an
            // attribute.
            if (extendedPropertyChildIterator.hasNext()) {
                OMElement infoElement = (OMElement) extendedPropertyChildIterator.next();
                XmlBlob xmlBlob = new XmlBlob();
                xmlBlob.setBlob(infoElement.toString());
                extendedProperty.setXmlBlob(xmlBlob);
            } else {
                String valueAttribute = extendedPropertyElement.getAttributeValue(new QName(Constants.VALUE));
                if (valueAttribute != null) {
                    extendedProperty.setValue(valueAttribute);
                }
            }
            extendedPropertyList.add(extendedProperty);
        }
        return extendedPropertyList;
    }
    
    /**
     * Returns an Object of Group Membership Info GData contacts object. This method takes a formatted OM
     * Element and converts to GroupMembershipInfo Object.
     * 
     * @param messageContext Synapse Message Context.
     * @param membershipElement The parent element containing the individual elements.
     * @return an object of Group Membership Info.
     * @throws ValidationException validationException is thrown if herf attribute is missing.
     */
    protected final GroupMembershipInfo getGroupMembershipInfoList(final MessageContext messageContext,
            final OMElement membershipElement) throws ValidationException {
    
        GroupMembershipInfo membershipInfo = new GroupMembershipInfo();
        String herf = membershipElement.getAttributeValue(new QName(Constants.HERF));
        
        if (herf != null) {
            membershipInfo
                    .setDeleted(Boolean.valueOf(membershipElement.getAttributeValue(new QName(Constants.DELETED))));
            membershipInfo.setHref(herf);
        } else {
            throw new ValidationException("Missing attribute: href");
        }
        
        return membershipInfo;
    }
    
    /**
     * Gets the request url.
     * 
     * @param messageContext the message context
     * @param requestAppendURL the request append url
     * @return the request url
     */
    protected final StringBuilder getRequestURLBuilder(final MessageContext messageContext,
            final String requestAppendURL) {
    
        final String userEmail = (String) messageContext.getProperty(Constants.USER_EMAIL);
        final StringBuilder requestUrlBuilder = new StringBuilder(Constants.REQUEST_URL_GENERIC_BEGIN);
        requestUrlBuilder.append(requestAppendURL).append(Constants.FORWARD_SLASH);
        if (userEmail == null || userEmail.isEmpty()) {
            requestUrlBuilder.append(Constants.DEFAULT);
        } else {
            requestUrlBuilder.append(userEmail);
        }
        return requestUrlBuilder;
    }
}
