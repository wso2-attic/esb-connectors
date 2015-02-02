/*
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.amazonses.constants;

/**
 * The Class AmazonSESConstants used to define constants.
 */
public final class AmazonSESConstants {
    
    /**
     * Constant for Header Date Formatter.
     */
    public static final String HEADER_DATE_FORMATTER = "EEE, dd MMM yyyy HH:mm:ss zzz";
    
    /**
     * Constant for ISO8601 Date Formatter.
     */
    public static final String ISO_8601_DATE_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    /**
     * Constant for expected TimeZone.
     */
    public static final String GMT = "GMT";
    
    /**
     * Constant for ESB Action.
     */
    public static final String ESB_ACTION = "uri.var.action";
    
    /**
     * Constant for API Action.
     */
    public static final String API_ACTION = "Action";
    
    /**
     * Constant for ESB AWS Access Key Id.
     */
    public static final String ESB_AWS_ACCESS_KEY_ID = "uri.var.accessKeyId";
    
    /**
     * Constant for API AWS Access Key Id.
     */
    public static final String API_AWS_ACCESS_KEY_ID = "AWSAccessKeyId";
    
    /**
     * Constant for ESB Signature Method.
     */
    public static final String ESB_SIGNATURE_METHOD = "uri.var.signatureMethod";
    
    /**
     * Constant for API Signature Method.
     */
    public static final String API_SIGNATURE_METHOD = "SignatureMethod";
    
    /**
     * Constant for ESB Signature Version.
     */
    public static final String ESB_SIGNATURE_VERSION = "uri.var.signatureVersion";
    
    /**
     * Constant for API Signature Version.
     */
    public static final String API_SIGNATURE_VERSION = "SignatureVersion";
    
    /**
     * Constant for ESB Version.
     */
    public static final String ESB_VERSION = "uri.var.version";
    
    /**
     * Constant for API Version.
     */
    public static final String API_VERSION = "Version";
    
    /**
     * Constant for ESB Secret Access Key.
     */
    public static final String ESB_SECRET_ACCESS_KEY = "uri.var.secretAccessKey";
    
    /**
     * Constant for API Signature.
     */
    public static final String API_SIGNATURE = "Signature";
    
    /**
     * Constant for API Authorization Header Prefix.
     */
    public static final String API_AUTHORIZATION_HEADER_PREFIX = "AWS3-HTTPS";
    
    /**
     * Constant for Empty.
     */
    public static final String EMPTY = "";
    
    /**
     * Constant for Seperator.
     */
    public static final String SEPERATOR = ",";
    
    /**
     * Constant for Suffix Joiner for List values.
     */
    public static final String SUFFIX_JOINER = ".";
    
    /**
     * Constant for Assign Operator.
     */
    public static final String ASSIGN = "=";
    
    /**
     * Constant for Encoding Style.
     */
    public static final String ENCODING_STYLE = "UTF-8";
    
    /**
     * Constant for Amperstand.
     */
    public static final String AMPERSTAND = "&";
    
    /**
     * Constant for Space.
     */
    public static final String SPACE = " ";
    
    /**
     * Constant for the key to set the Payload into MessageContext.
     */
    public static final String ESB_REQUEST_PAYLOAD_SET = "uri.var.requestPayload";
    
    /**
     * Constant for the key to set the X-Amzn-Authorization Header value into MessageContext.
     */
    public static final String ESB_X_AMZN_AUTHORIZATION_HEADER_SET = "uri.var.xAmznAuthorization";
    
    /**
     * Constant for the key to set the x-amz-date into MessageContext.
     */
    public static final String ESB_X_AMZ_DATE_HEADER_SET = "uri.var.xAmzDate";
    
    /**
     * Constant for Timestamp to go in the request body.
     */
    public static final String BODY_PARAMETER_TIMESTAMP = "Timestamp";
    
    /**
     * Constant for HmacSHA1.
     */
    public static final String HMAC_ALGORITHM = "HmacSHA1";
    
    /**
     * Constant for Algorithm.
     */
    public static final String ALGORITHM = "Algorithm";
    
    /**
     * Constant for Connector Error.
     */
    public static final String CONNECTOR_ERROR = "Error occured in connector";
    
    /**
     * Constant errorCode for IOException.
     */
    public static final int IO_EXCEPTION_ERROR_CODE = 700001;
    
    /**
     * Constant errorCode for InvalidKeyException.
     */
    public static final int INVALID_KEY_ERROR_CODE = 700007;
    
    /**
     * Constant errorCode for NoSuchAlgorithmException.
     */
    public static final int NOSUCH_ALGORITHM_ERROR_CODE = 700008;
    
    /**
     * Constant errorCode for IllegalArgumentException.
     */
    public static final int ILLEGAL_ARGUMENT_ERROR_CODE = 700013;
    
    /**
     * Constant errorCode for UnsupportedEncodingException.
     */
    public static final int UNSUPPORTED_ENCORDING_ERROR_CODE = 700009;
    
    /**
     * ESB and API constants for Message Body.
     */
    public static final String ESB_MESSAGE_BODY = "uri.var.messageBody";
    
    /**
     * The Constant API_MESSAGE_BODY.
     */
    public static final String API_MESSAGE_BODY = "Message.Body.Text.Data";
    
    /**
     * ESB and API constants for Message Subject.
     */
    public static final String ESB_MESSAGE_SUBJECT = "uri.var.messageSubject";
    
    /**
     * The Constant API_MESSAGE_SUBJECT.
     */
    public static final String API_MESSAGE_SUBJECT = "Message.Subject.Data";
    
    /**
     * ESB and API constants for Return Path.
     */
    public static final String ESB_RETURN_PATH = "uri.var.returnPath";
    
    /**
     * The Constant API_RETURN_PATH.
     */
    public static final String API_RETURN_PATH = "ReturnPath";
    
    /**
     * ESB and API constants for Source Address.
     */
    public static final String ESB_SOURCE_ADDRESS = "uri.var.source";
    
    /**
     * The Constant API_SOURCE_ADDRESS.
     */
    public static final String API_SOURCE_ADDRESS = "Source";
    
    /**
     * ESB and API constants for To Addresses.
     */
    public static final String ESB_TO_ADDRESSES = "uri.var.toAddresses";
    
    /**
     * The Constant API_TO_ADDRESSES.
     */
    public static final String API_TO_ADDRESSES = "Destination.ToAddresses.member";
    
    /**
     * ESB and API constants for CC Addresses.
     */
    public static final String ESB_CC_ADDRESSES = "uri.var.ccAddresses";
    
    /**
     * The Constant API_CC_ADDRESSES.
     */
    public static final String API_CC_ADDRESSES = "Destination.CcAddresses.member";
    
    /**
     * ESB and API constants for BCC Addresses.
     */
    public static final String ESB_BCC_ADDRESSES = "uri.var.bccAddresses";
    
    /**
     * The Constant API_BCC_ADDRESSES.
     */
    public static final String API_BCC_ADDRESSES = "Destination.BccAddresses.member";
    
    /**
     * ESB and API constants for Reply To Addresses.
     */
    public static final String ESB_REPLY_TO_ADDRESSES = "uri.var.replyToAddresses";
    
    /**
     * The Constant API_REPLY_TO_ADDRESSES.
     */
    public static final String API_REPLY_TO_ADDRESSES = "ReplyToAddresses.member";
    
    /**
     * ESB and API constants for Raw Message.
     */
    public static final String ESB_RAW_MESSAGE = "uri.var.rawMessage";
    
    /**
     * The Constant API_RAW_MESSAGE.
     */
    public static final String API_RAW_MESSAGE = "RawMessage.Data";
    
    /**
     * ESB and API constants for Destinations.
     */
    public static final String ESB_DESTINATIONS = "uri.var.destinations";
    
    /**
     * The Constant API_DESTINATIONS.
     */
    public static final String API_DESTINATIONS = "Destinations.member";
    
    /**
     * ESB and API constants for Identity.
     */
    public static final String ESB_IDENTITY = "uri.var.identity";
    
    /**
     * The Constant API_IDENTITY.
     */
    public static final String API_IDENTITY = "Identity";
    
    /**
     * ESB and API constants for Identities.
     */
    public static final String ESB_IDENTITIES = "uri.var.identities";
    
    /**
     * The Constant API_IDENTITIES.
     */
    public static final String API_IDENTITIES = "Identities.member";
    
    /**
     * ESB and API constants for DKIM Enabled.
     */
    public static final String ESB_DKIM_ENABLED = "uri.var.dkimEnabled";
    
    /**
     * The Constant API_DKIM_ENABLED.
     */
    public static final String API_DKIM_ENABLED = "DkimEnabled";
    
    /**
     * ESB and API constants for Forwarding Enabled.
     */
    public static final String ESB_FORWARDING_ENABLED = "uri.var.forwardingEnabled";
    
    /**
     * The Constant API_FORWARDING_ENABLED.
     */
    public static final String API_FORWARDING_ENABLED = "ForwardingEnabled";
    
    /**
     * ESB and API constants for Notification Type.
     */
    public static final String ESB_NOTIFICATION_TYPE = "uri.var.notificationType";
    
    /**
     * The Constant API_NOTIFICATION_TYPE.
     */
    public static final String API_NOTIFICATION_TYPE = "NotificationType";
    
    /**
     * ESB and API constants for SNS Topic.
     */
    public static final String ESB_SNS_TOPIC = "uri.var.snsTopic";
    
    /**
     * The Constant API_SNS_TOPIC.
     */
    public static final String API_SNS_TOPIC = "SnsTopic";
    
    /**
     * ESB and API constants for Email Address.
     */
    public static final String ESB_EMAIL_ADDRESS = "uri.var.emailAddress";
    
    /**
     * The Constant API_EMAIL_ADDRESS.
     */
    public static final String API_EMAIL_ADDRESS = "EmailAddress";
    
    /**
     * ESB and API constants for Domain.
     */
    public static final String ESB_DOMAIN = "uri.var.domain";
    
    /**
     * The Constant API_DOMAIN.
     */
    public static final String API_DOMAIN = "Domain";
    
    /**
     * ESB and API constants for Identity Type.
     */
    public static final String ESB_IDENTITY_TYPE = "uri.var.identityType";
    
    /**
     * The Constant API_IDENTITY_TYPE.
     */
    public static final String API_IDENTITY_TYPE = "IdentityType";
    
    /**
     * ESB and API constants for Max Items.
     */
    public static final String ESB_MAX_ITEMS = "uri.var.maxItems";
    
    /**
     * The Constant API_MAX_ITEMS.
     */
    public static final String API_MAX_ITEMS = "MaxItems";
    
    /**
     * ESB and API constants for Next Token.
     */
    public static final String ESB_NEXT_TOKEN = "uri.var.nextToken";
    
    /**
     * The Constant API_NEXT_TOKEN.
     */
    public static final String API_NEXT_TOKEN = "NextToken";
    
}
