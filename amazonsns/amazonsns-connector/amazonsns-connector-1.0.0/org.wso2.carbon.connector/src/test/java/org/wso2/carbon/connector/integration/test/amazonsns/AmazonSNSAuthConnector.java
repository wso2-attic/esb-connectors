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

package org.wso2.carbon.connector.integration.test.amazonsns;

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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class AmazonSNSAuthConnector which helps to generate authentication signature for Amazon SNS WSO2 ESB Connector.
 */

public class AmazonSNSAuthConnector {
    
    /**
     * Connect method which is generating authentication of the connector for each request.
     * 
     * @param messageContext ESB messageContext.
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws JSONException
     */
    public final Map<String, String> getRequestPayload(final JSONObject signatureRequestObject)
            throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException,
            JSONException {
    
        final StringBuilder canonicalRequest = new StringBuilder();
        final StringBuilder stringToSign = new StringBuilder();
        final StringBuilder payloadBuilder = new StringBuilder();
        final StringBuilder payloadStrBuilder = new StringBuilder();
        final StringBuilder authHeader = new StringBuilder();
        init(signatureRequestObject);
        
        // Generate time-stamp which will be sent to API and to be used in Signature
        final Date date = new Date();
        final TimeZone timeZone = TimeZone.getTimeZone(AmazonSNSConstants.GMT);
        final DateFormat dateFormat = new SimpleDateFormat(AmazonSNSConstants.ISO8601_BASIC_DATE_FORMAT);
        dateFormat.setTimeZone(timeZone);
        final String amzDate = dateFormat.format(date);
        
        final DateFormat shortDateFormat = new SimpleDateFormat(AmazonSNSConstants.SHORT_DATE_FORMAT);
        shortDateFormat.setTimeZone(timeZone);
        final String shortDate = shortDateFormat.format(date);
        
        signatureRequestObject.put(AmazonSNSConstants.AMZ_DATE, amzDate);
        final Map<String, String> parameterNamesMap = getParameterNamesMap();
        final Map<String, String> parametersMap = getSortedParametersMap(signatureRequestObject, parameterNamesMap);
        
        canonicalRequest.append(signatureRequestObject.get(AmazonSNSConstants.HTTP_METHOD));
        canonicalRequest.append(AmazonSNSConstants.NEW_LINE);
        canonicalRequest.append(AmazonSNSConstants.FORWARD_SLASH);
        canonicalRequest.append(AmazonSNSConstants.NEW_LINE);
        
        final String charSet = Charset.defaultCharset().toString();
        final Set<String> keySet = parametersMap.keySet();
        for (String key : keySet) {
            payloadBuilder.append(URLEncoder.encode(key, charSet));
            payloadBuilder.append(AmazonSNSConstants.EQUAL);
            payloadBuilder.append(URLEncoder.encode(parametersMap.get(key), charSet));
            payloadBuilder.append(AmazonSNSConstants.AMPERSAND);
            payloadStrBuilder.append(AmazonSNSConstants.QUOTE);
            payloadStrBuilder.append(key);
            payloadStrBuilder.append(AmazonSNSConstants.QUOTE);
            payloadStrBuilder.append(AmazonSNSConstants.COLON);
            payloadStrBuilder.append(AmazonSNSConstants.QUOTE);
            payloadStrBuilder.append(parametersMap.get(key));
            payloadStrBuilder.append(AmazonSNSConstants.QUOTE);
            payloadStrBuilder.append(AmazonSNSConstants.COMMA);
            
        }
        // Adds authorization header to message context, removes additionally appended comma at the end
        if (payloadStrBuilder.length() > 0) {
            signatureRequestObject.put(AmazonSNSConstants.REQUEST_PAYLOAD,
                    payloadStrBuilder.substring(0, payloadStrBuilder.length() - 1));
        }
        // Appends empty string since no URL parameters are used in POST API requests
        canonicalRequest.append("");
        canonicalRequest.append(AmazonSNSConstants.NEW_LINE);
        final Map<String, String> headersMap = getSortedHeadersMap(signatureRequestObject, parameterNamesMap);
        final StringBuilder canonicalHeaders = new StringBuilder();
        final StringBuilder signedHeader = new StringBuilder();
        
        for (Entry<String, String> entry : headersMap.entrySet()) {
            canonicalHeaders.append(entry.getKey());
            canonicalHeaders.append(AmazonSNSConstants.COLON);
            canonicalHeaders.append(entry.getValue());
            canonicalHeaders.append(AmazonSNSConstants.NEW_LINE);
            signedHeader.append(entry.getKey());
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
        // HashedPayload = HexEncode(Hash(requestPayload))
        String requestPayload = "";
        if (payloadBuilder.length() > 0) {
            /*
             * First removes the additional ampersand appended to the end of the payloadBuilder, then o further
             * modifications to preserve unreserved characters as per the API guide
             * (http://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html)
             */
            requestPayload =
                    payloadBuilder.substring(0, payloadBuilder.length() - 1)
                            .replace(AmazonSNSConstants.PLUS, AmazonSNSConstants.URL_ENCODED_PLUS)
                            .replace(AmazonSNSConstants.URL_ENCODED_TILT, AmazonSNSConstants.TILT)
                            .replace(AmazonSNSConstants.ASTERISK, AmazonSNSConstants.URL_ENCODED_ASTERISK);
        }
        canonicalRequest.append(bytesToHex(hash(requestPayload)).toLowerCase());
        stringToSign.append(AmazonSNSConstants.AWS4_HMAC_SHA_256);
        stringToSign.append(AmazonSNSConstants.NEW_LINE);
        stringToSign.append(amzDate);
        stringToSign.append(AmazonSNSConstants.NEW_LINE);
        stringToSign.append(shortDate);
        stringToSign.append(AmazonSNSConstants.FORWARD_SLASH);
        stringToSign.append(signatureRequestObject.get(AmazonSNSConstants.REGION));
        stringToSign.append(AmazonSNSConstants.FORWARD_SLASH);
        stringToSign.append(signatureRequestObject.get(AmazonSNSConstants.SERVICE));
        stringToSign.append(AmazonSNSConstants.FORWARD_SLASH);
        stringToSign.append(signatureRequestObject.get(AmazonSNSConstants.TERMINATION_STRING));
        stringToSign.append(AmazonSNSConstants.NEW_LINE);
        stringToSign.append(bytesToHex(hash(canonicalRequest.toString())).toLowerCase());
        final byte[] signingKey =
                getSignatureKey(signatureRequestObject, signatureRequestObject
                        .get(AmazonSNSConstants.SECRET_ACCESS_KEY).toString(), shortDate,
                        signatureRequestObject.get(AmazonSNSConstants.REGION).toString(),
                        signatureRequestObject.get(AmazonSNSConstants.SERVICE).toString());
        
        // Construction of authorization header value to be in cluded in API request
        authHeader.append(AmazonSNSConstants.AWS4_HMAC_SHA_256);
        authHeader.append(AmazonSNSConstants.COMMA);
        authHeader.append(AmazonSNSConstants.CREDENTIAL);
        authHeader.append(AmazonSNSConstants.EQUAL);
        authHeader.append(signatureRequestObject.get(AmazonSNSConstants.ACCESS_KEY_ID));
        authHeader.append(AmazonSNSConstants.FORWARD_SLASH);
        authHeader.append(shortDate);
        authHeader.append(AmazonSNSConstants.FORWARD_SLASH);
        authHeader.append(signatureRequestObject.get(AmazonSNSConstants.REGION));
        authHeader.append(AmazonSNSConstants.FORWARD_SLASH);
        authHeader.append(signatureRequestObject.get(AmazonSNSConstants.SERVICE));
        authHeader.append(AmazonSNSConstants.FORWARD_SLASH);
        authHeader.append(signatureRequestObject.get(AmazonSNSConstants.TERMINATION_STRING));
        authHeader.append(AmazonSNSConstants.COMMA);
        authHeader.append(AmazonSNSConstants.SIGNED_HEADERS);
        authHeader.append(AmazonSNSConstants.EQUAL);
        authHeader.append(signedHeaders);
        authHeader.append(AmazonSNSConstants.COMMA);
        authHeader.append(AmazonSNSConstants.API_SIGNATURE);
        authHeader.append(AmazonSNSConstants.EQUAL);
        authHeader.append(bytesToHex(hmacSHA256(signingKey, stringToSign.toString())).toLowerCase());
        // Adds authorization header to message context
        signatureRequestObject.put(AmazonSNSConstants.AUTHORIZATION_HEADER, authHeader.toString());
        
        Map<String, String> responseMap = new HashMap<String, String>();
        responseMap.put(AmazonSNSConstants.AUTHORIZATION_HEADER, authHeader.toString());
        responseMap.put(AmazonSNSConstants.AMZ_DATE, amzDate);
        responseMap.put(AmazonSNSConstants.REQUEST_PAYLOAD, requestPayload);
        return responseMap;
    }
    
    /**
     * @param signatureRequestObject
     * @throws JSONException
     */
    private void init(JSONObject signatureRequestObject) throws JSONException {
    
        signatureRequestObject.put(AmazonSNSConstants.SERVICE, "sns");
        signatureRequestObject.put(AmazonSNSConstants.SIGNATURE_METHOD, "HmacSHA256");
        signatureRequestObject.put(AmazonSNSConstants.SIGNATURE_VERSION, "4");
        signatureRequestObject.put(AmazonSNSConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");
        signatureRequestObject.put(AmazonSNSConstants.HTTP_METHOD, "POST");
        signatureRequestObject.put(AmazonSNSConstants.TERMINATION_STRING, "aws4_request");
        signatureRequestObject.put(AmazonSNSConstants.HOST,
                "sns." + signatureRequestObject.get(AmazonSNSConstants.REGION) + ".amazonaws.com");
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
                AmazonSNSConstants.ENDPOINT, AmazonSNSConstants.PLATFORM_APPLICATION_ARN,
                AmazonSNSConstants.SUBSCRIPTION_ARN, AmazonSNSConstants.TOKEN,
                AmazonSNSConstants.AUTHENTICATE_ON_UNSUBSCRIBE, AmazonSNSConstants.NAME, AmazonSNSConstants.MESSAGE,
                AmazonSNSConstants.MESSAGE_STRUCTURE, AmazonSNSConstants.SUBJECT, AmazonSNSConstants.TARGET_ARN,
                AmazonSNSConstants.NEXT_TOKEN };
    }
    
    /**
     * getKeys method returns a list of header keys.
     * 
     * @return list of header key value.
     */
    private String[] getHeaderKeys() {
    
        return new String[] { AmazonSNSConstants.HOST, AmazonSNSConstants.CONTENT_TYPE, AmazonSNSConstants.AMZ_DATE, };
    }
    
    /**
     * getCollectionParameterKeys method returns a list of predefined parameter keys which users will be used. to send
     * collection of values in each parameter.
     * 
     * @return list of parameter key value.
     */
    private String[] getMultivaluedParameterKeys() {
    
        return new String[] { AmazonSNSConstants.AWS_ACCOUNT_NUMBERS, AmazonSNSConstants.ACTION_NAMES,
                AmazonSNSConstants.REQUEST_ENTRIES, AmazonSNSConstants.ATTRIBUTE_ENTRIES,
                AmazonSNSConstants.ATTRIBUTES, AmazonSNSConstants.MESSAGE_ATTRIBUTE_NAMES,
                AmazonSNSConstants.MESSAGE_ATTRIBUTES };
    }
    
    /**
     * getParametersMap method used to return list of parameter values sorted by expected API parameter names.
     * 
     * @param signatureRequestObject ESB messageContext.
     * @param namesMap contains a map of esb parameter names and matching API parameter names
     * @return assigned parameter values as a HashMap.
     * @throws JSONException
     */
    private Map<String, String> getSortedParametersMap(final JSONObject signatureRequestObject,
            final Map<String, String> namesMap) throws JSONException {
    
        final String[] singleValuedKeys = getParameterKeys();
        final Map<String, String> parametersMap = new TreeMap<String, String>();
        // Stores sorted, single valued API parameters
        for (byte index = 0; index < singleValuedKeys.length; index++) {
            final String key = singleValuedKeys[index];
            // builds the parameter map only if provided by the user
            if (signatureRequestObject.has(key) && !("").equals((String) signatureRequestObject.get(key))) {
                parametersMap.put(namesMap.get(key), (String) signatureRequestObject.get(key));
            }
        }
        final String[] multiValuedKeys = getMultivaluedParameterKeys();
        // Stores sorted, multi-valued API parameters
        for (byte index = 0; index < multiValuedKeys.length; index++) {
            final String key = multiValuedKeys[index];
            // builds the parameter map only if provided by the user
            if (signatureRequestObject.has(key) && !("").equals((String) signatureRequestObject.get(key))) {
                final String collectionParam = (String) signatureRequestObject.get(key);
                // Splits the collection parameter to retrieve parameters separately
                final String[] keyValuepairs = collectionParam.split(AmazonSNSConstants.AMPERSAND);
                for (String keyValue : keyValuepairs) {
                    if (keyValue.contains(AmazonSNSConstants.EQUAL)
                            && keyValue.split(AmazonSNSConstants.EQUAL).length == AmazonSNSConstants.TWO) {
                        // Split the key and value of parameters to be sent to API
                        parametersMap.put(keyValue.split(AmazonSNSConstants.EQUAL)[0],
                                keyValue.split(AmazonSNSConstants.EQUAL)[1]);
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
        return parametersMap;
    }
    
    /**
     * getSortedHeadersMap method used to return list of header values sorted by expected API parameter names.
     * 
     * @param signatureRequestObject ESB messageContext.
     * @param namesMap contains a map of esb parameter names and matching API parameter names
     * @return assigned header values as a HashMap.
     * @throws JSONException
     */
    private Map<String, String> getSortedHeadersMap(final JSONObject signatureRequestObject,
            final Map<String, String> namesMap) throws JSONException {
    
        final String[] headerKeys = getHeaderKeys();
        final Map<String, String> parametersMap = new TreeMap<String, String>();
        // Stores sorted, single valued API parameters
        for (byte index = 0; index < headerKeys.length; index++) {
            final String key = headerKeys[index];
            // builds the parameter map only if provided by the user
            if (signatureRequestObject.has(key) && !("").equals((String) signatureRequestObject.get(key))) {
                parametersMap.put(namesMap.get(key).toLowerCase(), signatureRequestObject.get(key).toString().trim()
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
        
        map.put(AmazonSNSConstants.TOPIC_ARN, AmazonSNSConstants.API_TOPIC_ARN);
        map.put(AmazonSNSConstants.ENDPOINT, AmazonSNSConstants.API_ENDPOINT);
        map.put(AmazonSNSConstants.PROTOCOL, AmazonSNSConstants.API_PROTOCOL);
        map.put(AmazonSNSConstants.SUBSCRIPTION_ARN, AmazonSNSConstants.API_SUBSCRIPTION_ARN);
        map.put(AmazonSNSConstants.TOKEN, AmazonSNSConstants.API_TOKEN);
        map.put(AmazonSNSConstants.AUTHENTICATE_ON_UNSUBSCRIBE, AmazonSNSConstants.API_AUTHENTICATE_ON_UNSUBSCRIBE);
        map.put(AmazonSNSConstants.ENDPOINT_ARN, AmazonSNSConstants.API_ENDPOINT_ARN);
        map.put(AmazonSNSConstants.PLATFORM_APPLICATION_ARN, AmazonSNSConstants.API_PLATFORM_APPLICATION_ARN);
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
     * Add a Throwable to a message context, the message from the throwable is embedded as the Synapse. Constant
     * ERROR_MESSAGE.
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
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public final byte[] hash(final String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    
        MessageDigest messageDigest = null;
        messageDigest = MessageDigest.getInstance(AmazonSNSConstants.SHA_256);
        messageDigest.update(text.getBytes(AmazonSNSConstants.UTF_8));
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
     * Returns the encoded signature key to be used for further encodings as per API doc.
     * 
     * @param signatureRequestObject message context of the connector
     * @param key key to be used for signing
     * @param dateStamp current date stamp
     * @param regionName region name given to the connector
     * @param serviceName Name of the service being addressed
     * @return Signature key
     * @throws UnsupportedEncodingException Unsupported Encoding Exception
     * @throws IllegalStateException Illegal Argument Exception
     * @throws NoSuchAlgorithmException No Such Algorithm Exception
     * @throws InvalidKeyException Invalid Key Exception
     * @throws JSONException
     */
    private byte[] getSignatureKey(final JSONObject signatureRequestObject, final String key, final String dateStamp,
            final String regionName, final String serviceName) throws UnsupportedEncodingException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, JSONException {
    
        final byte[] kSecret = (AmazonSNSConstants.AWS4 + key).getBytes(AmazonSNSConstants.UTF8);
        final byte[] kDate = hmacSHA256(kSecret, dateStamp);
        final byte[] kRegion = hmacSHA256(kDate, regionName);
        final byte[] kService = hmacSHA256(kRegion, serviceName);
        return hmacSHA256(kService, signatureRequestObject.get(AmazonSNSConstants.TERMINATION_STRING).toString());
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
    private byte[] hmacSHA256(final byte[] key, final String data) throws NoSuchAlgorithmException,
            InvalidKeyException, IllegalStateException, UnsupportedEncodingException {
    
        final String algorithm = AmazonSNSConstants.HAMC_SHA_256;
        final Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes(AmazonSNSConstants.UTF8));
    }
}
