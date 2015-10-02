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

/**
 * Constants class for Google Contacts Cloud Connector. Contains all String constants and integer type error
 * code constants.
 */
public final class Constants {
    
    /**
     * Private Constructor for google contacts constants, Which is intended to not to instantiate object from
     * the class.
     */
    private Constants() {
    
    }
    
    /**
     * Google Contacts App name.
     */
    public static final String APP_NAME = "appName";
    
    /**
     * Access Token for authentication.
     */
    public static final String ACCESS_TOKEN = "accessToken";
    
    /**
     * Empty String.
     */
    public static final String EMPTY_STR = "";
    
    /**
     * Photo tag for content ID for attachment.
     */
    public static final String PHOTO = "photo";
    
    /**
     * Image content type for contact photo update.
     */
    public static final String IMAGE_CONTENT_TYPE = "image/*";
    
    /**
     * User email.
     */
    public static final String USER_EMAIL = "userEmail";
    
    /**
     * Delete response tag name for deleteContact method.
     */
    public static final String SOAP_DELETE_RESPONSE = "deleteContactResponse";
    
    /**
     * Title of a Group.
     */
    public static final String TITLE = "title";
    
    /**
     * Contact URL post fix appender.
     */
    public static final String URL_POSTFIX = "full";
    
    /**
     * Contact ID.
     */
    public static final String CONTACT_ID = "contactId";
    
    /**
     * ID as a Value.
     */
    public static final String ID = "id";
    
    /**
     * DELETED as a Value.
     */
    public static final String DELETED = "deleted";
    
    /**
     * HERF as a Value.
     */
    public static final String HERF = "href";
    
    /**
     * GROUP_MEMBERSHIP_INFO as an OMElement.
     */
    public static final String GROUP_MEMBERSHIP_INFO = "groupMembershipInfo";
    
    /**
     * DEFAULT as a value.
     */
    public static final String DEFAULT = "default";
    
    /**
     * FORWARD_SLASH.
     */
    public static final String FORWARD_SLASH = "/";
    
    /**
     * ETAG.
     */
    public static final String ETAG = "*";
    
    /**
     * IF_MATCH.
     */
    public static final String IF_MATCH = "IfMatch";
    
    // Google contacts query parameters.
    
    /**
     * Fulltext query on contacts data fields.
     */
    public static final String QUERY = "query";
    
    /**
     * The maximum number of entries to return.
     */
    public static final String MAX_RESULT = "maxResult";
    
    /**
     * The 1-based index of the first result to be retrieved.
     */
    public static final String START_INDEX = "startIndex";
    
    /**
     * The lower bound on entry update dates.
     */
    public static final String UPDATED_MIN = "updatedMin";
    
    /**
     * Sorting criterion.
     */
    public static final String ORDER_BY = "orderBy";
    
    /**
     * Include deleted contacts in the returned contacts feed.
     */
    public static final String SHOW_DELETED = "showDeleted";
    
    /**
     * Dictates the behavior of the server in case it detects that placeholders of some entries deleted.
     */
    public static final String REQUIRE_ALL_DELETED = "requireAllDeleted";
    
    /**
     * Sorting order direction.
     */
    public static final String SORT_ORDER = "sortOrder";
    
    /**
     * The contacts belonging to the group specified.
     */
    public static final String GROUP = "group";
    
    /**
     * The contacts belonging to the group specified.
     */
    public static final String GROUP_ID = "groupId";
    
    /**
     * Parameter name orderby.
     */
    public static final String PARAM_ORDER_BY = "orderby";
    
    /**
     * Parameter name showdeleted.
     */
    public static final String PARAM_SHOW_DELETED = "showdeleted";
    
    /**
     * Parameter name requirealldeleted.
     */
    public static final String PARAM_REQUIRE_ALL_DELETED = "requirealldeleted";
    
    /**
     * Parameter name sortorder.
     */
    public static final String PARAM_SORT_ORDER = "sortorder";
    
    /**
     * The name details of the contact.
     */
    public static final String NAME = "name";
    
    
    /**
     * The nickname of the contact.
     */
    public static final String NICKNAME = "nickname";
    
    /**
     * The index the contact should be filed under.
     */
    public static final String FILE_AS = "fileAs";
    
    /**
     * Name prefix.
     */
    public static final String NAME_PREFIX = "namePrefix";
    
    /**
     * The Contact's given name.
     */
    public static final String GIVEN_NAME = "givenName";
    
    /**
     * The Contact's full name.
     */
    public static final String FULL_NAME = "fullName";
    
    /**
     * The Contact's additional name.
     */
    public static final String ADDITIONAL_NAME = "additionalName";
    
    /**
     * Name suffix.
     */
    public static final String NAME_SUFFIX = "nameSuffix";
    
    /**
     * The contact's family name.
     */
    public static final String FAMILY_NAME = "familyName";
    
    /**
     * The phone number details of the contact.
     */
    public static final String PHONE_NUMBER = "phoneNumber";
    
    /**
     * The email addresses of the contact.
     */
    public static final String EMAIL = "email";
    
    /**
     * The Instant Messaging addresses of the contact.
     */
    public static final String IM = "im";
    
    /**
     * A Contact's event.
     */
    public static final String EVENT = "event";
    
    /**
     * A Contact's events.
     */
    public static final String EVENTS = "events";
    
    /**
     * A Contact's relationships.
     */
    public static final String RELATIONSHIPS = "relations";
    
    
    /**
     * The postal address details of the contact.
     */
    public static final String STRUCTURED_POSTAL_ADDRESS = "structuredPostalAddress";
    
    /**
     * List of structured postal addresses.
     */
    public static final String STRUCTURED_POSTAL_ADDRESSES = "structuredPostalAddresses";
    
    /**
     * Street for address.
     */
    public static final String STREET = "street";
    
    /**
     * City for address.
     */
    public static final String CITY = "city";
    
    /**
     * Region for address.
     */
    public static final String REGION = "region";
    
    /**
     * Post code for address.
     */
    public static final String POST_CODE = "postCode";
    
    /**
     * Country for address.
     */
    public static final String COUNTRY = "country";
    
    /**
     * Country code for the country in the address.
     */
    public static final String COUNTRY_CODE = "countryCode";
    
    /**
     * Formatted address.
     */
    public static final String FORMATTED_ADDRESS = "formattedAddress";
    
    /**
     * Neighborhood for the address.
     */
    public static final String NEIGHBORHOOD = "neighborhood";
    
    /**
     * PO Box for the address.
     */
    public static final String PO_BOX = "poBox";
    
    /**
     * Whether this address is the primary.
     */
    public static final String PRIMARY = "primary";
    
    /**
     * The birthday of the contact.
     */
    public static final String BIRTHDAY = "birthday";
    
    /**
     * Formatter string for dates. 
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * URL list to the user's websites.
     */
    public static final String URL = "url";
    
    /**
     * Represents 'Author' string.
     */
    public static final String AUTHOR = "author";
    
    /**
     * Note on a contact.
     */
    public static final String NOTE = "note";
    
    /**
     * Represents 'displayName' string.
     */
    public static final String DISPLAY_NAME = "displayName";
    
    /**
     * Represents 'rel' string.
     */
    public static final String REL = "rel";
    
    /**
     * Represents 'value' string.
     */
    public static final String VALUE = "value";
    
    /**
     * Contains the prefix for the Extended Property.
     */
    public static final String EXTENDED_PROPERTY = "extendedProperty";
    
    /**
     * Contains the prefix for the Extended Property.
     */
    public static final String EXTENDED_PROPERTIES = "extendedProperties";
    
    /**
     * Contains the prefix for the batch contacts.
     */
    public static final String BATCH_CONTACTS = "batchContacts";
    
    /**
     * Contains the prefix for the batch groups.
     */
    public static final String BATCH_GROUPS = "batchGroups";
    
    /**
     * Contains the prefix for the type.
     */
    public static final String TYPE = "type";
    
    /**
     * Contains the prefix for the insert.
     */
    public static final String INSERT = "insert";
    
    /**
     * Contains the prefix for the update.
     */
    public static final String UPDATE = "update";
    
    /**
     * Contains the prefix for the delete.
     */
    public static final String DELETE = "delete";
    
    /**
     * Contains the prefix for the batch.
     */
    public static final String BATCH = "batch";
    
    /**
     * Request URL parameter.
     */
    public static final String REQUEST_URL_GENERIC_BEGIN = "https://www.google.com/m8/feeds/";
    
    /**
     * Request URL parameter.
     */
    public static final String REQUEST_URL_CONTACTS = "contacts";
    
    /**
     * Request URL parameter for groups.
     */
    public static final String REQUEST_URL_GROUPS = "groups";
    
    /**
     * Trailing end of generic request URL.
     */
    public static final String REQUEST_URL_GENERIC_END = "/full";
    
    /**
     * Represents address string.
     */
    public static final String ADDRESS = "address";
    
    /**
     * Represents content string.
     */
    public static final String CONTENT = "content";
    
    /**
     * Represents IM protocol.
     */
    public static final String PROTOCOL = "protocol";
    
    /**
     * Home type for contact details.
     */
    public static final String HOME = "home";
    
    /**
     * Work type for contact details.
     */
    public static final String WORK = "work";
    
    /**
     * Custom type for contact details.
     */
    public static final String CUSTOM = "custom";
    
    /**
     * Rel value for home type addresses.
     */
    public static final String HOME_REL = "http://schemas.google.com/g/2005#home";
    
    /**
     * Rel value for work type addresses.
     */
    public static final String WORK_REL = "http://schemas.google.com/g/2005#work";
    
    /**
     * Rel value for custom type addresses.
     */
    public static final String CUSTOM_REL = "http://schemas.google.com/g/2005#custom";
    
    /**
     * Rel opening value.
     */
    public static final String REL_OPEN = "http://schemas.google.com/g/2005#";
    
    // Google Contacts Namespace constants.
    
    /**
     * Represent urn for deleteContactPhoto.
     */
    public static final String URN_GOOGLECONTACTS_DELETECONTACTPHOTO =
            "urn:wso2.connector.googlecontacts.deletecontactphoto";
    
    /**
     * Represent urn for DeleteContact.
     */
    public static final String URN_GOOGLECONTACTS_DELETECONTACT = "wso2.connector.googlecontacts.deletecontact";
    
    /**
     * Represent urn for RetrieveContactsByQuery.
     */
    public static final String URN_GOOGLECONTACTS_RETRIEVECONTACTSBYQUERY =
            "urn:wso2.connector.googledrive.retrievecontactsbyquery";
    
    /**
     * URN for createContact.
     */
    public static final String URN_GOOGLECONTACTS_CREATECONTACT = "wso2.connector.googlecontacts.createcontact";
    
    /**
     * URN for updateContact.
     */
    public static final String URN_GOOGLECONTACTS_UPDATECONTACT = "wso2.connector.googlecontacts.updatecontact";
    
    /**
     * URN for updateContactPhoto.
     */
    public static final String URN_GOOGLECONTACTS_UPDATECONTACTPHOTO =
            "wso2.connector.googlecontacts.updatecontactphoto";
    
    /**
     * Represent urn for deleting a contact group.
     */
    public static final String URN_DELETE_CONTACT_GROUP = "urn:wso2.connector.googlecontacts.deletecontactgroup";
    
    // Google Contacts result tag name constants.
    /**
     * Represents the result tag for create contact operation.
     */
    public static final String CREATE_CONTACT_RESULT = "createContactResult";
    
    /**
     * Represent deleteContactPhotoResult.
     */
    public static final String DELETE_CONTACT_PHOTO_RESULT = "deleteContactPhotoResult";
    
    /**
     * Represents update contact photo result.
     */
    public static final String UPDATE_CONTACT_PHOTO_RESULT = "updateContactPhotoResult";
    
    /**
     * Represent ContactsQueryResult.
     */
    public static final String CONTACTS_QUERY_RESULT = "contactsQueryResult";
    
    /**
     * Represent deleteContactResult.
     */
    public static final String DELETE_CONTACT_RESULT = "deleteContactResult";
    
    /**
     * Result tag string for update Contact method.
     */
    public static final String UPDATE_CONTACT_RESULT = "updateContactResult";
    
    /**
     * Represent deleteContactGroupResult.
     */
    public static final String DELETE_CONTACT_GROUP_RESULT = "deleteContactGroupResult";
    
    // Common Contacts Error codes
    
    /**
     * Error code constant for operation failure.
     */
    public static final int ERROR_CODE_IO_EXCEPTION = 700001;
    
    /**
     * Error code constant for authentication failure.
     */
    public static final int ERROR_CODE_GENERAL_SECURITY_EXCEPTION = 700002;
    
    /**
     * Error code constant for XMLStream parsing failure.
     */
    public static final int ERROR_CODE_XML_STRM_PARSE_FAILURE = 700004;
    
    /**
     * Error code constant for XML parsing configuration failure.
     */
    public static final int ERROR_CODE_XML_PARSER_CONFIGURATION_EXCEPTION = 700005;
    
    /**
     * Error code constant for parsing XML request failure.
     */
    public static final int ERROR_CODE_SAX_EXCEPTION = 700006;
    
    /**
     * Error code constant for unknown protocol failure.
     */
    public static final int ERROR_CODE_MALFORMED_URL_EXCEPTION = 700010;
    
    /**
     * Error code constant for Parsing errors.
     */
    public static final int ERROR_CODE_PARSE_EXCEPTION = 700011;
    
    /**
     * Error code constant for number formatting errors.
     */
    public static final int ERROR_CODE_NUMBER_FORMAT_EXCEPTION = 700012;
    
    // Google Contacts Specific Error codes
    
    /**
     * Error code constant for Google JSON response exceptions.
     */
    public static final int ERROR_CODE_GOOGLE_JSON_RESPONSE_EXCEPTION = 800001;
    
    /**
     * Error code constant for Google Http response exceptions.
     */
    public static final int ERROR_CODE_GOOGLE_HTTP_RESPONSE_EXCEPTION = 800002;
    
    /**
     * Error code constant for parameter validation failure.
     */
    public static final int ERROR_CODE_CONNECTOR_VALIDATION_EXCEPTION = 800003;
    
    /**
     * Error code constant for batch operation failure.
     */
    public static final int ERROR_CODE_BATCH_INTERRUPTED_EXCEPTION = 800004;
    
    /**
     * Error code constant for precondition failure.
     */
    public static final int ERROR_CODE_PRECONDITION_FAILED_EXCEPTION = 800005;
    
    /**
     * Error code constant for service exception.
     */
    public static final int ERROR_CODE_SERVICE_EXCEPTION = 800006;
    
    /**
     * Error code constant for run time exception.
     */
    public static final int ERROR_CODE_RUNTIME_EXCEPTION = 900000;
}
