/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.googledrive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
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
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Property;

/**
 * Utility class for Google Drive WSO2 ESB Connector.
 */
public final class GoogleDriveUtils {
    
    /**
     * constructor for class Google Drive Utils.
     */
    private GoogleDriveUtils() {
    
    }
    
    /**
     * Takes a String and validates whether it is a proper boolean value.
     * 
     * @param value The String that needs to be validated as boolean
     * @return boolean value if validation passed
     * @throws ValidationException If a validation error occurs.
     */
    public static boolean toBoolean(final String value) throws ValidationException {
    
        if (!value.equalsIgnoreCase("TRUE") && !value.equalsIgnoreCase("FALSE")) {
            
            throw new ValidationException("Invalid value for boolean");
            
        }
        
        return Boolean.parseBoolean(value);
        
    }
    
    /**
     * Takes a String and validates whether it is a proper Integer value.
     * 
     * @param value The String that needs to be validated as integer.
     * @return integer value if validation passed.
     * @throws ValidationException If a validation error occurs.
     */
    public static int toInteger(final String value) throws ValidationException {
    
        if (!value.matches(StringConstants.INTEGER_REGEX)) {
            
            throw new ValidationException("Invalid value for integer");
            
        } else {
            return Integer.parseInt(value);
        }
        
    }
    
    /**
     * Takes a String and validates whether it is a proper Long value.
     * 
     * @param value The String that needs to be validated as long.
     * @return long value if validation passed.
     * @throws ValidationException If a validation error occurs. String errorDetail = "";
     */
    public static long toLong(final String value) throws ValidationException {
    
        if (!value.matches(StringConstants.INTEGER_REGEX)) {
            
            throw new ValidationException("Invalid value for long");
            
        } else {
            return Long.parseLong(value);
        }
        
    }
    
    /**
     * Takes a map containing key value pairs and maps those key-value pairs in to tag-value pairs within an
     * OMElement, and creates a new SOAP envelope using this OMElement.
     * 
     * @param namespace String value for namespace
     * @param resultTagName String tag for result
     * @param elements to be written to the envelope in Map format
     * @return SOAP Envelope to be added to the message context
     * @throws IOException If a failure on parsing JSON
     */
    public static SOAPEnvelope buildResultEnvelope(final String namespace, final String resultTagName,
            final Map<String, String> elements) throws IOException {
    
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(namespace, "urn");
        OMElement resultTag = factory.createOMElement(resultTagName, ns);
        
        if (elements != null) {
            Iterator<Map.Entry<String, String>> elementIterator = elements.entrySet().iterator();
            while (elementIterator.hasNext()) {
                Map.Entry<String, String> element = elementIterator.next();
                OMElement jsonObject =
                        JsonUtil.toXml(new ByteArrayInputStream(element.getValue().getBytes(Charset.defaultCharset())),
                                false);
                Iterator<?> jsonChildrenIterator = jsonObject.getChildElements();
                while (jsonChildrenIterator.hasNext()) {
                    resultTag.addChild((OMElement) jsonChildrenIterator.next());
                }
            }
        }
        
        return TransportUtils.createSOAPEnvelope(resultTag);
    }
    
    /**
     * Creates a Drive Service object from the Google Drive SDK after completing user authentication.
     * 
     * @param messageContext Synapse Message Context
     * @return Credentials object which contain credentials
     * @throws IOException IOException If an error occurs while reading stream
     * @throws GeneralSecurityException If a GeneralSecurity error occurs
     */
    public static Drive getDriveService(final MessageContext messageContext) throws IOException,
            GeneralSecurityException {
    
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        
        String certificatePassword = (String) messageContext.getProperty(StringConstants.CERTIFICATE_PASSWORD);
        if (certificatePassword == null || certificatePassword.isEmpty()) {
            certificatePassword = "notasecret";
        }
        
        String authType = (String) (messageContext.getProperty(GoogleDriveUtils.StringConstants.USE_SERVICE_ACCOUNT));
        if (authType == null || authType.isEmpty()) {
            throw new GeneralSecurityException("Need to specify 'useServiceAccount' parameter");
        }
        if (!authType.equalsIgnoreCase("TRUE") && !authType.equalsIgnoreCase("FALSE")) {
            throw new GeneralSecurityException("'useServiceAccount' should be a boolean value");
        }
        boolean useServiceAccount =
                Boolean.parseBoolean((String) messageContext
                        .getProperty(GoogleDriveUtils.StringConstants.USE_SERVICE_ACCOUNT));
        
        GoogleCredential credential = null;
        if (useServiceAccount) {
            credential =
                    new GoogleCredential.Builder()
                            .setTransport(httpTransport)
                            .setJsonFactory(jsonFactory)
                            .setServiceAccountId(
                                    (String) messageContext
                                            .getProperty(GoogleDriveUtils.StringConstants.SERVICE_ACCOUNT_EMAIL))
                            .setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
                            .setServiceAccountPrivateKey(
                                    extractPrivatekeyFromAttachment(messageContext, certificatePassword)).build();
            
        } else {
            credential =
                    new GoogleCredential.Builder()
                            .setTransport(httpTransport)
                            .setJsonFactory(jsonFactory)
                            .setClientSecrets(
                                    (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.CLIENT_ID),
                                    (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.CLIENT_SECRET))
                            .build()
                            .setAccessToken(
                                    (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.ACCESS_TOKEN))
                            .setRefreshToken(
                                    (String) messageContext.getProperty(GoogleDriveUtils.StringConstants.REFRESH_TOKEN));
        }
        
        return new Drive.Builder(httpTransport, jsonFactory, credential).build();
    }
    
    /**
     * This method can extract private key from PKCS12 format security certificate First it will remove
     * certificate from soap attachment.
     * @param messageContext Synapse Message Context
     * @param certificatePassword this is password of certificate which offer when google provide private key
     *        to save
     * @return java.security.Privatekey The privatekey to use for authentication.
     * @throws GeneralSecurityException If a GeneralSecurity error occurs
     * @throws IOException IOException If an error occurs while reading stream
     */
    private static PrivateKey extractPrivatekeyFromAttachment(final MessageContext messageContext,
            final String certificatePassword) throws GeneralSecurityException, IOException {
    
        char[] passwordChar = certificatePassword.toCharArray();
        
        org.apache.axis2.context.MessageContext axis2mc =
                ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        
        DataHandler dataHandler = axis2mc.getAttachment("certificate");
        
        if (dataHandler == null) {
            throw new GeneralSecurityException(
                    "No certificate found - make sure you have set contentID as 'certificate'");
        }
        
        InputStream certificateInputStream = dataHandler.getInputStream();
        
        KeyStore ks = java.security.KeyStore.getInstance("PKCS12");
        
        ks.load(certificateInputStream, passwordChar);
        
        String alias = ks.aliases().nextElement();
        if (alias != null && !alias.isEmpty()) {
            
            if (ks.isKeyEntry(alias)) {
                return (PrivateKey) ks.getKey(alias, passwordChar);
            } else {
                throw new GeneralSecurityException(
                        "Key alias entry is not a valid key entry to extract the private key");
            }
        } else {
            throw new GeneralSecurityException("could not find alias");
        }
    }
    
    /**
     * Add a <strong>Throwable</strong> to a message context, the message from the throwable is embedded as
     * the Synapse contstant ERROR_MESSAGE.
     * 
     * @param ctxt Synapse Message Context to which the error tags need to be added
     * @param e Throwable that needs to be parsed and added
     * @param errorCode integer type error code to be added to ERROR_CODE Synapse constant
     */
    public static void storeErrorResponseStatus(final MessageContext ctxt, final Throwable e, int errorCode) {
    
        String errorMessage;
        
        if (e instanceof GoogleJsonResponseException) {
            GoogleJsonResponseException gjre = (GoogleJsonResponseException) e;
            errorCode = ErrorCodeConstants.ERROR_CODE_GOOGLE_JSON_RESPONSE_EXCEPTION;
            errorMessage = gjre.getStatusCode() + " : " + gjre.getStatusMessage();
            GoogleJsonError jsonError = gjre.getDetails();
            if (jsonError != null) {
                ctxt.setProperty(SynapseConstants.ERROR_DETAIL, jsonError.getMessage());
            }
            
        } else if (e instanceof HttpResponseException) {
            HttpResponseException ghre = (HttpResponseException) e;
            errorCode = ErrorCodeConstants.ERROR_CODE_GOOGLE_HTTP_RESPONSE_EXCEPTION;
            errorMessage = ghre.getStatusCode() + " " + ghre.getStatusMessage();
        } else {
            errorMessage = e.getMessage();
        }
        ctxt.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, errorMessage);
        
        ctxt.setFaultResponse(true);
    }
    
    /**
     * This method can return a <strong>Map</strong>, containing key value pairs to set values to Google Drive
     * Object.
     * 
     * @param params XML string which containing keys and values
     * @return Map of unknown keys and values
     * @throws XMLStreamException If a failure on parsing XML Stream
     */
    
    public static Map<String, Object> getUnkownKeyMap(final String params) throws XMLStreamException {
    
        Map<String, Object> unknownKeyMap = new HashMap<String, Object>();
        
        OMElement omElement = AXIOMUtil.stringToOM(params);
        
        Iterator<?> omElementIterator = omElement.getChildElements();
        while (omElementIterator.hasNext()) {
            OMElement currentElement = (OMElement) omElementIterator.next();
            unknownKeyMap.put(currentElement.getLocalName(), currentElement.getText());
        }
        return unknownKeyMap;
        
    }
    
    /**
     * This method can return a <strong>List</strong>, of <strong>ParentReference</strong> Objects by parsing
     * XML string.
     * 
     * @param params XML string which containing keys and values
     * @return List of <strong>ParentReference</strong> Objects
     * @throws XMLStreamException If a failure on parsing XML Stream
     */
    
    public static List<ParentReference> getParentReferenceList(final String params) throws XMLStreamException {
    
        List<ParentReference> listOfParentElements = new ArrayList<ParentReference>();
        OMElement omElement = AXIOMUtil.stringToOM(params);
        
        Iterator<?> omElemetIterator = omElement.getChildElements();
        while (omElemetIterator.hasNext()) {
            ParentReference parentReference = new ParentReference();
            OMElement parentElement = (OMElement) omElemetIterator.next();
            
            Iterator<?> parentElementIterator = parentElement.getChildElements();
            Map<String, Object> parametersMap = new HashMap<String, Object>();
            while (parentElementIterator.hasNext()) {
                OMElement currentElemet = (OMElement) parentElementIterator.next();
                parametersMap.put(currentElemet.getLocalName(), currentElemet.getText());
            }
            parentReference.setUnknownKeys(parametersMap);
            listOfParentElements.add(parentReference);
        }
        
        return listOfParentElements;
    }
    
    /**
     * This method can return a <strong>List</strong>, of <strong>Property</strong> Objects by parsing XML
     * string.
     * 
     * @param params XML string which containing keys and values
     * @return List of <strong>Property</strong> Objects
     * @throws XMLStreamException If a failure on parsing XML Stream
     */
    
    public static List<Property> getPropertyList(final String params) throws XMLStreamException {
    
        List<Property> listOfProperties = new ArrayList<Property>();
        OMElement omElement = AXIOMUtil.stringToOM(params);
        
        Iterator<?> omElemetIterator = omElement.getChildElements();
        while (omElemetIterator.hasNext()) {
            Property properties = new Property();
            
            OMElement parentElement = (OMElement) omElemetIterator.next();
            Iterator<?> parentElementIterator = parentElement.getChildElements();
            Map<String, Object> parametersMap = new HashMap<String, Object>();
            while (parentElementIterator.hasNext()) {
                OMElement currentElement = (OMElement) parentElementIterator.next();
                parametersMap.put(currentElement.getLocalName(), currentElement.getText());
            }
            properties.setUnknownKeys(parametersMap);
            listOfProperties.add(properties);
        }
        
        return listOfProperties;
    }
    
    /**
     * Contains Constants for Google Drive Connector strings.
     */
    public static final class StringConstants {
        
        /**
         * Regular expression pattern for Integer.
         */
        public static final String INTEGER_REGEX = "\\d+";
        
        /**
         * Specific to authentication Represent the useServiceAccount.
         */
        public static final String USE_SERVICE_ACCOUNT = "useServiceAccount";
        
        /**
         * Certificate password.
         */
        public static final String CERTIFICATE_PASSWORD = "certificatePassword";
        
        /**
         * Represent the serviceAccountEmail.
         */
        public static final String SERVICE_ACCOUNT_EMAIL = "serviceAccountEmail";
        
        /**
         * Represent the serviceAccountPK.
         */
        public static final String SERVICE_ACCOUNT_PKCS_CONTENT = "serviceAccountPKCSContent";
        
        /**
         * Represent the clientId.
         */
        public static final String CLIENT_ID = "clientId";
        
        /**
         * Represent the clientSecret.
         */
        public static final String CLIENT_SECRET = "clientSecret";
        
        /**
         * Represent the accessToken.
         */
        public static final String ACCESS_TOKEN = "accessToken";
        
        /**
         * Represent the refreshToken.
         */
        public static final String REFRESH_TOKEN = "refreshToken";
        
        /**
         * Represent the fileId.
         */
        public static final String FILE_ID = "fileId";
        
        /**
         * Represent the file.
         */
        public static final String FILE = "file";
        
        /**
         * Represent the File List.
         */
        public static final String FILE_LIST = "fileList";
        
        /**
         * Represent the filerequestBody.
         */
        public static final String REQUEST_BODY = "requestBody";
        
        /**
         * Represent the error.
         */
        public static final String ERROR = "error";
        
        /**
         * Represent the fields.
         */
        public static final String FIELDS = "fields";
        
        /**
         * Represent the maxResult.
         */
        public static final String MAX_RESULTS = "maxResults";
        
        /**
         * Represent the pageToken.
         */
        public static final String PAGE_TOKEN = "pageToken";
        
        /**
         * Represent the query.
         */
        public static final String Q = "q";
        
        /**
         * Represent the convert.
         */
        public static final String CONVERT = "convert";
        
        /**
         * Represent the newRevision.
         */
        public static final String NEW_REVISION = "newRevision";
        
        /**
         * Represent the OCR.
         */
        public static final String OCR = "ocr";
        
        /**
         * Represent the OCRLanguage.
         */
        public static final String OCR_LANGUAGE = "ocrLanguage";
        
        /**
         * Represent the pinned.
         */
        public static final String PINNED = "pinned";
        
        /**
         * Represent the setModifiedDate.
         */
        public static final String SET_MODIFIED_DATE = "setModifiedDate";
        
        /**
         * Represent the timetextLanguage.
         */
        public static final String TIMED_TEXT_LANGUAGE = "timedTextLanguage";
        
        /**
         * Represent the timeTextTrackName.
         */
        public static final String TIMED_TEXT_TRACKNAME = "timedTextTrackName";
        
        /**
         * Represent the UpdateViewDate.
         */
        public static final String UPDATE_VIEWED_DATE = "updateViewedDate";
        
        /**
         * Represent the useContentAsIndexableText.
         */
        public static final String USE_CONTENT_AS_INDEXABLE_TEXT = "useContentAsIndexableText";
        
        /**
         * Represent the uploadType.
         */
        public static final String UPLOAD_TYPE = "uploadType";
        
        /**
         * Represent the mimeType.
         */
        public static final String MIME_TYPE = "mimeType";
        
        /**
         * Represent the default mimeType.
         */
        public static final String DEFAULT_MIME_TYPE = "text/plain";
        
        /**
         * Represent thefileContent.
         */
        public static final String FILE_CONTENT = "fileContent";
        
        /**
         * Represent patchFileResult.
         */
        public static final String PATCH_FILE_RESULT = "patchFileResult";
        
        /**
         * Represent updateFileResult.
         */
        public static final String UPDATE_FILE_RESULT = "updateFileResult";
        
        /**
         * Represent updateViewDate.
         */
        public static final String UPDATE_VIEW_DATE = "updateViewedDate";
        
        /**
         * Represent channelId.
         */
        public static final String CHANNEL_ID = "channelId";
        
        /**
         * Represent channelToken.
         */
        public static final String CHANNEL_TOKEN = "channelToken";
        
        /**
         * Represent channelAddress.
         */
        public static final String CHANNEL_ADDRESS = "channelAddress";
        
        /**
         * Represent channelType.
         */
        public static final String CHANNEL_TYPE = "channelType";
        
        /**
         * Represent getFileResult.
         */
        public static final String GET_FILE_RESULT = "getFileResult";
        
        /**
         * Represent getwatchFileResult.
         */
        public static final String WATCH_FILE_RESULT = "watchFileResult";
        
        /**
         * Represent deleteFileResult.
         */
        public static final String DELETE_FILE_RESULT = "deleteFileResult";
        
        /**
         * Represent listFileResult.
         */
        public static final String LIST_FILE_RESULT = "listFileResult";
        
        /**
         * Represent touchFileResult.
         */
        public static final String TOUCH_FILE_RESULT = "touchFileResult";
        
        /**
         * Represent copiedFileResult.
         */
        public static final String COPIED_FILE_RESULT = "copiedFileResult";
        
        /**
         * Represent insertFileResult.
         */
        public static final String INSERTED_FILE_RESULT = "insertedFileResult";
        
        /**
         * Represent title.
         */
        public static final String TITLE = "title";
        
        /**
         * Represents media upload directive.
         */
        public static final String MEDIA = "media";
        
        /**
         * Represent parentId.
         */
        public static final String PARENT_ID = "parentId";
        
        /**
         * Represent fileName.
         */
        public static final String FILE_NAME = "filename";
        
        /**
         * Represent description.
         */
        public static final String DESCRIPTION = "description";
        
        /**
         * Represent visibility.
         */
        public static final String VISIBILITY = "visibility";
        
        /**
         * Represent tempFolderName.
         */
        public static final String TEMP_FOLDER_NAME = "tempFolderName";
        
        /**
         * Represent tempFileName.
         */
        public static final String TEMP_FILE_NAME = "tempFileName";
        
        /**
         * Represent urn for getFile.
         */
        public static final String URN_GOOGLEDRIVE_GETFILE = "urn:wso2.connector.googledrive.getfile";
        
        /**
         * Represent urn for getwatchFile.
         */
        public static final String URN_GOOGLEDRIVE_WATCHFILE = "urn:wso2.connector.googledrive.watchfile";
        
        /**
         * Represent urn for updateFile.
         */
        public static final String URN_GOOGLEDRIVE_UPDATEFILE = "urn:wso2.connector.googledrive.updatefile";
        
        /**
         * Represent urn for patchFile.
         */
        public static final String URN_GOOGLEDRIVE_PATCHFILE = "urn:wso2.connector.googledrive.patchfile";
        
        /**
         * Represent urn for insertFile.
         */
        public static final String URN_GOOGLEDRIVE_INSERTFILE = "urn:wso2.connector.googledrive.insertfile";
        
        /**
         * Represent urn for copyFile.
         */
        public static final String URN_GOOGLEDRIVE_COPYFILE = "urn:wso2.connector.googledrive.copyfile";
        
        /**
         * Represent urn for trashFile.
         */
        public static final String URN_GOOGLEDRIVE_TRASHFILE = "urn:wso2.connector.googledrive.trashfile";
        
        /**
         * Represent urn for unTrashFile.
         */
        public static final String URN_GOOGLEDRIVE_UNTRASHFILE = "urn:wso2.connector.googledrive.untrashfile";
        
        /**
         * Represent urn for deleteFile.
         */
        public static final String URN_GOOGLEDRIVE_DELETEFILE = "urn:wso2.connector.googledrive.deletefile";
        
        /**
         * Represent urn for listFile.
         */
        public static final String URN_GOOGLEDRIVE_LISTFILE = "urn:wso2.connector.googledrive.listfiles";
        
        /**
         * Represent urn for touchFile.
         */
        public static final String URN_GOOGLEDRIVE_TOUCHFILE = "urn:wso2.connector.googledrive.touchfile";
        
        /**
         * Represent comment.
         */
        public static final String COMMENT = "comment";
        
        /**
         * Represent commentId.
         */
        public static final String COMMENT_ID = "commentId";
        
        /**
         * Represent comment content.
         */
        public static final String COMMENT_CONTENT = "content";
        
        /**
         * Represent includeDeleted.
         */
        public static final String COMMENT_INCLUDE_DELETED = "includeDeleted";
        
        /**
         * Represent insertCommentResult.
         */
        public static final String INSERT_COMMENT_RESULT = "insertCommentResult";
        
        /**
         * Represent trashedFileResult.
         */
        public static final String TRASHED_FILE_RESULT = "trashedFileResult";
        
        /**
         * Represent untrashedFileResult.
         */
        public static final String UNTRASHED_FILE_RESULT = "untrashedFileResult";
        
        /**
         * Represent getCommentResult.
         */
        public static final String GET_COMMENT_RESULT = "getCommentResult";
        
        /**
         * Represent deleteCommentResult.
         */
        public static final String DELETE_COMMENT_RESULT = "deleteCommentResult";
        
        /**
         * Represent urn for insertComment.
         */
        public static final String URN_GOOGLEDRIVE_INSERTCOMMENT = "urn:wso2.connector.googledrive.insertcomment";
        
        /**
         * Represent urn for getCommentById.
         */
        public static final String URN_GOOGLEDRIVE_GETCOMMENTBYID = "urn:wso2.connector.googledrive.getcommentbyid";
        
        /**
         * Represent urn for deleteComment.
         */
        public static final String URN_GOOGLEDRIVE_DELETECOMMENT = "urn:wso2.connector.googledrive.deletecomment";
        
        /**
         * Represent permission.
         */
        public static final String PERMISSION = "permission";
        
        /**
         * Represent emailMessage.
         */
        public static final String EMAIL_MESSAGE = "emailMessage";
        
        /**
         * Represent additional roles.
         */
        public static final String ADDITIONAL_ROLES = "additionalRoles";
        
        /**
         * Represent withLink.
         */
        public static final String WITH_LINK = "withLink";
        
        /**
         * Represent sendNotificationEmails.
         */
        public static final String SEND_NOTIFICATION_EMAILS = "sendNotificationEmails";
        
        /**
         * Represent role.
         */
        public static final String ROLE = "role";
        
        /**
         * Represent resumable.
         */
        public static final String RESUMABLE = "resumable";
        
        /**
         * Represent type.
         */
        public static final String TYPE = "type";
        
        /**
         * Represent value.
         */
        public static final String VALUE = "value";
        
        /**
         * Represent insertPermissionResult.
         */
        public static final String INSERT_PERMISSION_RESULT = "insertPermissionResult";
        
        /**
         * Represent getPermissionsResult.
         */
        public static final String GET_PERMISSIONS_RESULT = "getPermissionsResult";
        
        /**
         * Represent urn for InsertPermission.
         */
        public static final String URN_GOOGLEDRIVE_INSERTPERMISSION = "urn:wso2.connector.googledrive.insertpermission";
        
        /**
         * Represent urn for listFilePermission.
         */
        public static final String URN_GOOGLEDRIVE_GETPERMISSIONLIST =
                "urn:wso2.connector.googledrive.listfilepermissions";
        
        /**
         * Represent childReference.
         */
        public static final String CHILD_REFERENCE = "childReference";
        
        /**
         * Represent folderId.
         */
        public static final String FOLDER_ID = "folderId";
        
        /**
         * Represent the childId.
         */
        public static final String CHILD_ID = "childId";
        
        /**
         * Represent getChildResult.
         */
        public static final String GET_CHILD_RESULT = "getChildResult";
        
        /**
         * Represent insertFileToFolderResult.
         */
        public static final String INSERT_FILE_TO_FOLDER_RESULT = "insertFileToFolderResult";
        
        /**
         * Represent listChildrenResult.
         */
        public static final String LIST_CHILDREN_RESULT = "listChildrenResult";
        
        /**
         * Represent urn for InsertFileToFolder.
         */
        public static final String URN_GOOGLEDRIVE_INSERTFILETOFOLDER =
                "urn:wso2.connector.googledrive.insertfiletofolder";
        
        /**
         * Represent urn for getChild.
         */
        public static final String URN_GOOGLEDRIVE_GETCHILD = "urn:wso2.connector.googledrive.getchild";
        
        /**
         * Represent urn for listfolders.
         */
        public static final String URN_GOOGLEDRIVE_LISTFOLDERS = "urn:wso2.connector.googledrive.listfolders";
        
        /**
         * Represent includeDeleted.
         */
        public static final String INCLUDE_DELETED = "includeDeleted";
        
        /**
         * Represent includeSubscribed.
         */
        public static final String INCLUDE_SUBSCRIBED = "includeSubscribed";
        
        /**
         * Represent startChangeId.
         */
        public static final String START_CHANGE_ID = "startChangeId";
        
        /**
         * Represent change_list.
         */
        public static final String CHANGE_LIST = "change_list";
        
        /**
         * Represent listChangesForUserResult.
         */
        public static final String LIST_CHANGES_FOR_USER_RESULT = "listChangesForUserResult";
        
        /**
         * Represent urn for listchangesforuser.
         */
        public static final String URN_GOOGLEDRIVE_LISTCHANGESFORUSER =
                "urn:wso2.connector.googledrive.listchangesforuser";
        
        /**
         * watchFile params.
         */
        public static final String PARAMS = "params";
        
        /**
         * The life time of the channel.
         */
        public static final String TTL = "ttl";
        
        /**
         * Represent the File Resource.
         */
        public static final String FILE_RESOURCE = "fileResource";
        
        /**
         * Represent the Labels.
         */
        public static final String LABELS = "labels";
        
        /**
         * Represent the Context Type.
         */
        public static final String CONTEXT_TYPE = "contextType";
        
        /**
         * Represent the Context Value.
         */
        public static final String CONTEXT_VALUE = "contextValue";
        
        /**
         * Represent the File Resource properties.
         */
        public static final String PROPERTIES = "properties";
        
        /**
         * Represent the File Resource Parents.
         */
        public static final String PARENTS = "parents";
        
        /**
         * Represent the Indexable Text.
         */
        public static final String INDEXABLE_TEXT = "indexableText";
    }
    
    /**
     * Contains Error Code Constants for Google Drive Connector.
     */
    public static final class ErrorCodeConstants {
        
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
        public static final int ERROR_CODE_XML_STREAM_PARSE_FAILURE = 700004;
        
        /**
         * Error code constant for XML parsing configuration failure.
         */
        public static final int ERROR_CODE_XML_PARSER_CONFIGURATION_EXCEPTION = 700005;
        
        /**
         * Error code constant for parsing XML request failure.
         */
        public static final int ERROR_CODE_SAX_EXCEPTION = 700006;
        
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
        
    }
    
}
