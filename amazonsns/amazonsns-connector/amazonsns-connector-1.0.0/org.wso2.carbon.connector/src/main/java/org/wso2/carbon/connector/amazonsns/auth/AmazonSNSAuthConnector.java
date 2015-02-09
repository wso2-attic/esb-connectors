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

package org.wso2.carbon.connector.amazonsns.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.wso2.carbon.connector.amazonsns.constants.AmazonSNSConstants;
import org.wso2.carbon.connector.core.AbstractConnector;

/**
 * Class AmazonSNSAuthConnector which helps to generate authentication signature for Amazon SNS WSO2 ESB
 * Connector.
 */

public class AmazonSNSAuthConnector extends AbstractConnector {
    
    /**
     * Connect method which is generating authentication of the connector for each request.
     * 
     * @param messageContext ESB messageContext.
     */
    public final void connect(final MessageContext messageContext) {
    
        final StringBuilder canonicalRequest = new StringBuilder();
        final StringBuilder stringToSign = new StringBuilder();
        final StringBuilder payloadBuilder = new StringBuilder();
        final StringBuilder payloadStrBuilder = new StringBuilder();
        final StringBuilder authHeader = new StringBuilder();
        
        // Generate time-stamp which will be sent to API and to be used in Signature
        final Date date = new Date();
        final TimeZone timeZone = TimeZone.getTimeZone(AmazonSNSConstants.GMT);
        final DateFormat dateFormat = new SimpleDateFormat(AmazonSNSConstants.ISO8601_BASIC_DATE_FORMAT);
        dateFormat.setTimeZone(timeZone);
        final String amzDate = dateFormat.format(date);
        
        final DateFormat shortDateFormat = new SimpleDateFormat(AmazonSNSConstants.SHORT_DATE_FORMAT);
        shortDateFormat.setTimeZone(timeZone);
        final String shortDate = shortDateFormat.format(date);
        
        messageContext.setProperty(AmazonSNSConstants.AMZ_DATE, amzDate);
        final Map<String, String> parameterNamesMap = getParameterNamesMap();
        final Map<String, String> parametersMap = getSortedParametersMap(messageContext, parameterNamesMap);
        try {
            canonicalRequest.append(messageContext.getProperty(AmazonSNSConstants.HTTP_METHOD));
            canonicalRequest.append(AmazonSNSConstants.NEW_LINE);
            canonicalRequest.append(messageContext.getProperty(AmazonSNSConstants.HTTP_REQUEST_URI));
            canonicalRequest.append(AmazonSNSConstants.NEW_LINE);
            
            final String charSet = Charset.defaultCharset().toString();
            
            for (Map.Entry<String, String> entry : parametersMap.entrySet()) {
                
                payloadBuilder.append(URLEncoder.encode(entry.getKey(), charSet));
                payloadBuilder.append(AmazonSNSConstants.EQUAL);
                payloadBuilder.append(URLEncoder.encode(entry.getValue(), charSet));
                payloadBuilder.append(AmazonSNSConstants.AMPERSAND);
                
                // Generating XML Payload.
                payloadStrBuilder.append(AmazonSNSConstants.LESS_THAN);
                payloadStrBuilder.append(entry.getKey());
                payloadStrBuilder.append(AmazonSNSConstants.GREATER_THAN);
                payloadStrBuilder.append(entry.getValue());
                payloadStrBuilder.append(AmazonSNSConstants.LESS_THAN);
                payloadStrBuilder.append(AmazonSNSConstants.FORWARD_SLASH);
                payloadStrBuilder.append(entry.getKey());
                payloadStrBuilder.append(AmazonSNSConstants.GREATER_THAN);
                
            }
            
            // Adds authorization header to message context, removes additionally appended comma at the end
            if (payloadStrBuilder.length() > 0) {
                messageContext.setProperty(AmazonSNSConstants.REQUEST_PAYLOAD,
                        payloadStrBuilder.substring(0, payloadStrBuilder.length()));
            }
            
            // Appends empty string since no url parameters are used in POST API requests
            canonicalRequest.append("");
            canonicalRequest.append(AmazonSNSConstants.NEW_LINE);
            
            final Map<String, String> headersMap = getSortedHeadersMap(messageContext, parameterNamesMap);
            final StringBuilder canonicalHeaders = new StringBuilder();
            final StringBuilder signedHeader = new StringBuilder();
            
            final Set<String> keysSet = headersMap.keySet();
            for (String key : keysSet) {
                
                canonicalHeaders.append(key);
                canonicalHeaders.append(AmazonSNSConstants.COLON);
                canonicalHeaders.append(headersMap.get(key));
                canonicalHeaders.append(AmazonSNSConstants.NEW_LINE);
                signedHeader.append(key);
                signedHeader.append(AmazonSNSConstants.SEMI_COLON);
            }
            
            canonicalRequest.append(canonicalHeaders.toString());
            canonicalRequest.append(AmazonSNSConstants.NEW_LINE);
            
            // Remove unwanted semi-colon at the end of the signedHeader string
            String signedHeaders = "";
            if (signedHeader.length() > 0) {
                signedHeaders = signedHeader.substring(0, signedHeader.length() - 1);
            }
            
            canonicalRequest.append(signedHeaders);
            canonicalRequest.append(AmazonSNSConstants.NEW_LINE);
            
            String requestPayload = "";
            if (payloadBuilder.length() > 0) {
                /*
                 * First removes the additional ampersand appended to the end of the payloadBuilder, then o
                 * further modifications to preserve unreserved characters as per the API guide
                 * (http://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html)
                 */
                requestPayload =
                        payloadBuilder.substring(0, payloadBuilder.length() - 1)
                                .replace(AmazonSNSConstants.PLUS, AmazonSNSConstants.URL_ENCODED_PLUS)
                                .replace(AmazonSNSConstants.URL_ENCODED_TILT, AmazonSNSConstants.TILT)
                                .replace(AmazonSNSConstants.ASTERISK, AmazonSNSConstants.URL_ENCODED_ASTERISK);
            }
            
            canonicalRequest.append(bytesToHex(hash(messageContext, requestPayload)).toLowerCase());
            
            stringToSign.append(AmazonSNSConstants.AWS4_HMAC_SHA_256);
            stringToSign.append(AmazonSNSConstants.NEW_LINE);
            stringToSign.append(amzDate);
            stringToSign.append(AmazonSNSConstants.NEW_LINE);
            
            stringToSign.append(shortDate);
            stringToSign.append(AmazonSNSConstants.FORWARD_SLASH);
            stringToSign.append(messageContext.getProperty(AmazonSNSConstants.REGION));
            stringToSign.append(AmazonSNSConstants.FORWARD_SLASH);
            stringToSign.append(messageContext.getProperty(AmazonSNSConstants.SERVICE));
            stringToSign.append(AmazonSNSConstants.FORWARD_SLASH);
            stringToSign.append(messageContext.getProperty(AmazonSNSConstants.TERMINATION_STRING));
            
            stringToSign.append(AmazonSNSConstants.NEW_LINE);
            stringToSign.append(bytesToHex(hash(messageContext, canonicalRequest.toString())).toLowerCase());
            
            final byte[] signingKey =
                    getSignatureKey(messageContext, messageContext.getProperty(AmazonSNSConstants.SECRET_ACCESS_KEY)
                            .toString(), shortDate, messageContext.getProperty(AmazonSNSConstants.REGION).toString(),
                            messageContext.getProperty(AmazonSNSConstants.SERVICE).toString());
            
            // Construction of authorization header value to be in cluded in API request
            authHeader.append(AmazonSNSConstants.AWS4_HMAC_SHA_256);
            authHeader.append(AmazonSNSConstants.COMMA);
            authHeader.append(AmazonSNSConstants.CREDENTIAL);
            authHeader.append(AmazonSNSConstants.EQUAL);
            authHeader.append(messageContext.getProperty(AmazonSNSConstants.ACCESS_KEY_ID));
            authHeader.append(AmazonSNSConstants.FORWARD_SLASH);
            authHeader.append(shortDate);
            authHeader.append(AmazonSNSConstants.FORWARD_SLASH);
            authHeader.append(messageContext.getProperty(AmazonSNSConstants.REGION));
            authHeader.append(AmazonSNSConstants.FORWARD_SLASH);
            authHeader.append(messageContext.getProperty(AmazonSNSConstants.SERVICE));
            authHeader.append(AmazonSNSConstants.FORWARD_SLASH);
            authHeader.append(messageContext.getProperty(AmazonSNSConstants.TERMINATION_STRING));
            authHeader.append(AmazonSNSConstants.COMMA);
            authHeader.append(AmazonSNSConstants.SIGNED_HEADERS);
            authHeader.append(AmazonSNSConstants.EQUAL);
            authHeader.append(signedHeaders);
            authHeader.append(AmazonSNSConstants.COMMA);
            authHeader.append(AmazonSNSConstants.API_SIGNATURE);
            authHeader.append(AmazonSNSConstants.EQUAL);
            authHeader.append(bytesToHex(hmacSHA256(signingKey, stringToSign.toString())).toLowerCase());
            
            // Adds authorization header to message context
            messageContext.setProperty(AmazonSNSConstants.AUTHORIZATION_HEADER, authHeader.toString());
            
        } catch (InvalidKeyException exc) {
            log.error(AmazonSNSConstants.INVALID_KEY_ERROR, exc);
            storeErrorResponseStatus(messageContext, exc, AmazonSNSConstants.INVALID_KEY_ERROR_CODE);
            handleException(AmazonSNSConstants.INVALID_KEY_ERROR, exc, messageContext);
        } catch (NoSuchAlgorithmException exc) {
            log.error(AmazonSNSConstants.NO_SUCH_ALGORITHM_ERROR, exc);
            storeErrorResponseStatus(messageContext, exc, AmazonSNSConstants.NO_SUCH_ALGORITHM_ERROR_CODE);
            handleException(AmazonSNSConstants.NO_SUCH_ALGORITHM_ERROR, exc, messageContext);
        } catch (IllegalStateException exc) {
            log.error(AmazonSNSConstants.ILLEGAL_STATE_ERROR, exc);
            storeErrorResponseStatus(messageContext, exc, AmazonSNSConstants.ILLEGAL_STATE_ERROR_CODE);
            handleException(AmazonSNSConstants.CONNECTOR_ERROR, exc, messageContext);
        } catch (UnsupportedEncodingException exc) {
            log.error(AmazonSNSConstants.UNSUPPORTED_ENCORDING_ERROR, exc);
            storeErrorResponseStatus(messageContext, exc, AmazonSNSConstants.UNSUPPORTED_ENCORDING_ERROR_CODE);
            handleException(AmazonSNSConstants.CONNECTOR_ERROR, exc, messageContext);
        }
    }
    
    /**
     * getKeys method returns a list of parameter keys.
     * 
     * @return list of parameter key value.
     */
    private String[] getParameterKeys() {
    
        return new String[] { AmazonSNSConstants.ACTION, AmazonSNSConstants.EXPIRES, AmazonSNSConstants.SECURITY_TOKEN,
                AmazonSNSConstants.SIGNATURE, AmazonSNSConstants.SIGNATURE_METHOD,
                AmazonSNSConstants.SIGNATURE_VERSION, AmazonSNSConstants.TIMESTAMP, AmazonSNSConstants.VERSION,
                AmazonSNSConstants.ACCESS_KEY_ID, AmazonSNSConstants.TOPIC_ARN, AmazonSNSConstants.PROTOCOL,
                AmazonSNSConstants.ENDPOINT, AmazonSNSConstants.SUBSCRIPTION_ARN, AmazonSNSConstants.TOKEN,
                AmazonSNSConstants.AUTHENTICATE_ON_UNSUBSCRIBE, AmazonSNSConstants.NAME, AmazonSNSConstants.MESSAGE,
                AmazonSNSConstants.MESSAGE_STRUCTURE, AmazonSNSConstants.SUBJECT, AmazonSNSConstants.TARGET_ARN,
                AmazonSNSConstants.NEXT_TOKEN, AmazonSNSConstants.PLATFORM_APPLICATION_ARN,
                AmazonSNSConstants.ENDPOINT_ARN, AmazonSNSConstants.CUSTOM_USER_DATA };
    }
    
    /**
     * getKeys method returns a list of parameter keys.
     * 
     * @return list of parameter key value.
     */
    private String[] getHeaderKeys() {
    
        return new String[] { AmazonSNSConstants.HOST, AmazonSNSConstants.CONTENT_TYPE, AmazonSNSConstants.AMZ_DATE, };
    }
    
    /**
     * getCollectionParameterKeys method returns a list of predefined parameter keys which users will be used.
     * to send collection of values in each parameter.
     * 
     * @return list of parameter key value.
     */
    private String[] getMultivaluedParameterKeys() {
    
        return new String[] { AmazonSNSConstants.AWS_ACCOUNT_NUMBERS, AmazonSNSConstants.ACTION_NAMES,
                AmazonSNSConstants.REQUEST_ENTRIES, AmazonSNSConstants.ATTRIBUTE_ENTRIES };
    }
    
    /**
     * getParametersMap method used to return list of parameter values sorted by expected API parameter names.
     * 
     * @param messageContext ESB messageContext.
     * @param namesMap contains a map of esb parameter names and matching API parameter names
     * @return assigned parameter values as a HashMap.
     */
    private Map<String, String> getSortedParametersMap(final MessageContext messageContext,
            final Map<String, String> namesMap) {
    
        final String[] singleValuedKeys = getParameterKeys();
        final Map<String, String> parametersMap = new TreeMap<String, String>();
        // Stores sorted, single valued API parameters
        for (byte index = 0; index < singleValuedKeys.length; index++) {
            final String key = singleValuedKeys[index];
            // builds the parameter map only if provided by the user
            if (messageContext.getProperty(key) != null && !("").equals((String) messageContext.getProperty(key))) {
                parametersMap.put(namesMap.get(key), (String) messageContext.getProperty(key));
            }
        }
        
        final String[] multiValuedKeys = getMultivaluedParameterKeys();
        // Stores sorted, multi-valued API parameters
        for (byte index = 0; index < multiValuedKeys.length; index++) {
            final String key = multiValuedKeys[index];
            // builds the parameter map only if provided by the user
            if (messageContext.getProperty(key) != null && !("").equals((String) messageContext.getProperty(key))) {
                final String collectionParam = (String) messageContext.getProperty(key);
                // Splits the collection parameter to retrieve parameters separately
                final String[] keyValuepairs = collectionParam.split(AmazonSNSConstants.AMPERSAND);
                for (String keyValue : keyValuepairs) {
                    if (keyValue.contains(AmazonSNSConstants.EQUAL)
                            && keyValue.split(AmazonSNSConstants.EQUAL).length == AmazonSNSConstants.TWO) {
                        // Split the key and value of parameters to be sent to API
                        parametersMap.put(keyValue.split(AmazonSNSConstants.EQUAL)[0],
                                keyValue.split(AmazonSNSConstants.EQUAL)[1]);
                    } else {
                        storeErrorResponseStatus(messageContext, AmazonSNSConstants.INVALID_PARAMETERS,
                                AmazonSNSConstants.ILLEGAL_ARGUMENT_ERROR_CODE);
                        handleException("Invalid key", new IllegalArgumentException(), messageContext);
                    }
                }
            }
            
        }
        return parametersMap;
    }
    
    /**
     * getSortedHeadersMap method used to return list of header values sorted by expected API parameter names.
     * 
     * @param messageContext ESB messageContext.
     * @param namesMap contains a map of esb parameter names and matching API parameter names
     * @return assigned header values as a HashMap.
     */
    private Map<String, String> getSortedHeadersMap(final MessageContext messageContext,
            final Map<String, String> namesMap) {
    
        final String[] headerKeys = getHeaderKeys();
        final Map<String, String> parametersMap = new TreeMap<String, String>();
        // Stores sorted, single valued API parameters
        for (byte index = 0; index < headerKeys.length; index++) {
            final String key = headerKeys[index];
            // builds the parameter map only if provided by the user
            if (messageContext.getProperty(key) != null && !("").equals((String) messageContext.getProperty(key))) {
                parametersMap.put(namesMap.get(key).toLowerCase(), messageContext.getProperty(key).toString().trim()
                        .replaceAll(AmazonSNSConstants.TRIM_SPACE_REGEX, AmazonSNSConstants.SPACE));
            }
        }
        return parametersMap;
    }
    
    /**
     * getparameterNamesMap returns a map of esb parameter names and corresponding API parameter names.
     * 
     * @return generated map.
     */
    private Map<String, String> getParameterNamesMap() {
    
        final Map<String, String> map = new HashMap<String, String>();
        map.put(AmazonSNSConstants.ACTION, AmazonSNSConstants.API_ACTION);
        map.put(AmazonSNSConstants.EXPIRES, AmazonSNSConstants.API_EXPIRES);
        map.put(AmazonSNSConstants.SECURITY_TOKEN, AmazonSNSConstants.API_SECURITY_TOKEN);
        map.put(AmazonSNSConstants.SIGNATURE, AmazonSNSConstants.API_SIGNATURE);
        map.put(AmazonSNSConstants.SIGNATURE_METHOD, AmazonSNSConstants.API_SIGNATURE_METHOD);
        map.put(AmazonSNSConstants.SIGNATURE_VERSION, AmazonSNSConstants.API_SIGNATURE_VERSION);
        map.put(AmazonSNSConstants.TIMESTAMP, AmazonSNSConstants.API_TIMESTAMP);
        map.put(AmazonSNSConstants.VERSION, AmazonSNSConstants.API_VERSION);
        map.put(AmazonSNSConstants.ACCESS_KEY_ID, AmazonSNSConstants.AWS_ACCESS_KEY_ID);
        
        // AmazonSNS related parameters
        map.put(AmazonSNSConstants.TOPIC_ARN, AmazonSNSConstants.API_TOPIC_ARN);
        map.put(AmazonSNSConstants.ENDPOINT, AmazonSNSConstants.API_ENDPOINT);
        map.put(AmazonSNSConstants.PROTOCOL, AmazonSNSConstants.API_PROTOCOL);
        map.put(AmazonSNSConstants.SUBSCRIPTION_ARN, AmazonSNSConstants.API_SUBSCRIPTION_ARN);
        map.put(AmazonSNSConstants.TOKEN, AmazonSNSConstants.API_TOKEN);
        map.put(AmazonSNSConstants.AUTHENTICATE_ON_UNSUBSCRIBE, AmazonSNSConstants.API_AUTHENTICATE_ON_UNSUBSCRIBE);
        map.put(AmazonSNSConstants.ENDPOINT_ARN, AmazonSNSConstants.API_ENDPOINT_ARN);
        map.put(AmazonSNSConstants.PLATFORM_APPLICATION_ARN, AmazonSNSConstants.API_PLATFORM_APPLICATION_ARN);
        map.put(AmazonSNSConstants.CUSTOM_USER_DATA, AmazonSNSConstants.API_CUSTOM_USER_DATA);
        map.put(AmazonSNSConstants.NAME, AmazonSNSConstants.API_NAME);
        map.put(AmazonSNSConstants.SUBJECT, AmazonSNSConstants.API_SUBJECT);
        map.put(AmazonSNSConstants.TARGET_ARN, AmazonSNSConstants.API_TARGET_ARN);
        map.put(AmazonSNSConstants.MESSAGE, AmazonSNSConstants.API_MESSAGE);
        map.put(AmazonSNSConstants.MESSAGE_STRUCTURE, AmazonSNSConstants.API_MESSAGE_STRUCTURE);
        map.put(AmazonSNSConstants.NEXT_TOKEN, AmazonSNSConstants.API_NEXT_TOKEN);
        
        // Header parameters
        map.put(AmazonSNSConstants.HOST, AmazonSNSConstants.API_HOST);
        map.put(AmazonSNSConstants.CONTENT_TYPE, AmazonSNSConstants.API_CONTENT_TYPE);
        map.put(AmazonSNSConstants.AMZ_DATE, AmazonSNSConstants.API_AMZ_DATE);
        
        return map;
    }
    
    /**
     * Add a Throwable to a message context, the message from the throwable is embedded as the Synapse.
     * Constant ERROR_MESSAGE.
     * 
     * @param ctxt message context to which the error tags need to be added
     * @param throwable Throwable that needs to be parsed and added
     * @param errorCode errorCode mapped to the exception
     */
    public final void storeErrorResponseStatus(final MessageContext ctxt, final Throwable throwable, final int errorCode) {
    
        ctxt.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, throwable.getMessage());
        ctxt.setFaultResponse(true);
    }
    
    /**
     * Add a message to message context, the message from the throwable is embedded as the Synapse Constant
     * ERROR_MESSAGE.
     * 
     * @param ctxt message context to which the error tags need to be added
     * @param message message to be returned to the user
     * @param errorCode errorCode mapped to the exception
     */
    public final void storeErrorResponseStatus(final MessageContext ctxt, final String message, final int errorCode) {
    
        ctxt.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, message);
        ctxt.setFaultResponse(true);
    }
    
    /**
     * Hashes the string contents (assumed to be UTF-8) using the SHA-256 algorithm.
     * 
     * @param messageContext of the connector
     * @param text text to be hashed
     * @return SHA-256 hashed text
     */
    public final byte[] hash(final MessageContext messageContext, final String text) {
    
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(AmazonSNSConstants.SHA_256);
            messageDigest.update(text.getBytes(AmazonSNSConstants.UTF_8));
        } catch (Exception exc) {
            log.error(AmazonSNSConstants.CONNECTOR_ERROR, exc);
            storeErrorResponseStatus(messageContext, exc, AmazonSNSConstants.ERROR_CODE_EXCEPTION);
            handleException(AmazonSNSConstants.CONNECTOR_ERROR, exc, messageContext);
        }
        if (messageDigest == null) {
            log.error(AmazonSNSConstants.CONNECTOR_ERROR);
            storeErrorResponseStatus(messageContext, AmazonSNSConstants.CONNECTOR_ERROR,
                    AmazonSNSConstants.ERROR_CODE_EXCEPTION);
            handleException(AmazonSNSConstants.CONNECTOR_ERROR, messageContext);
        }
        return messageDigest.digest();
    }
    
    /**
     * bytesToHex method HexEncoded the received byte array.
     * 
     * @param bytes bytes to be hex encoded
     * @return hex encoded String of the given byte array
     */
    public static String bytesToHex(final byte[] bytes) {
    
        final char[] hexArray = AmazonSNSConstants.HEX_ARRAY_STRING.toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        
        for (int j = 0; j < bytes.length; j++) {
            final int byteVal = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[byteVal >>> 4];
            hexChars[j * 2 + 1] = hexArray[byteVal & 0x0F];
        }
        
        return new String(hexChars);
    }
    
    /**
     * Provides the HMAC SHA 256 encoded value(using the provided key) of the given data.
     * 
     * @param key to use for encoding
     * @param data to be encoded
     * @return HMAC SHA 256 encoded byte array
     * @throws NoSuchAlgorithmException No such algorithm Exception
     * @throws InvalidKeyException Invalid key Exception
     * @throws UnsupportedEncodingException Unsupported Encoding Exception
     * @throws IllegalStateException Illegal State Exception
     */
    private static byte[] hmacSHA256(final byte[] key, final String data) throws NoSuchAlgorithmException,
            InvalidKeyException, IllegalStateException, UnsupportedEncodingException {
    
        final String algorithm = AmazonSNSConstants.HAMC_SHA_256;
        final Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes(AmazonSNSConstants.UTF8));
    }
    
    /**
     * Returns the encoded signature key to be used for further encodings as per API doc.
     * 
     * @param ctx message context of the connector
     * @param key key to be used for signing
     * @param dateStamp current date stamp
     * @param regionName region name given to the connector
     * @param serviceName Name of the service being addressed
     * @return Signature key
     * @throws UnsupportedEncodingException Unsupported Encoding Exception
     * @throws IllegalStateException Illegal Argument Exception
     * @throws NoSuchAlgorithmException No Such Algorithm Exception
     * @throws InvalidKeyException Invalid Key Exception
     */
    private static byte[] getSignatureKey(final MessageContext ctx, final String key, final String dateStamp,
            final String regionName, final String serviceName) throws UnsupportedEncodingException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException {
    
        final byte[] kSecret = (AmazonSNSConstants.AWS4 + key).getBytes(AmazonSNSConstants.UTF8);
        final byte[] kDate = hmacSHA256(kSecret, dateStamp);
        final byte[] kRegion = hmacSHA256(kDate, regionName);
        final byte[] kService = hmacSHA256(kRegion, serviceName);
        return hmacSHA256(kService, ctx.getProperty(AmazonSNSConstants.TERMINATION_STRING).toString());
    }
}
