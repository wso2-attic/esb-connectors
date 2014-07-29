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

package org.wso2.carbon.connector.integration.test.amazonses;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * The Class SignatureGenerator used to generating Amazon Signature Version 3.
 */
public class SignatureGenerator {
    
    /**
     * Maps for storing headers, parameters and parameters.
     */
    private final Map<String, String> parameterMap, commonParametersMap, singleParamMap, multiParamMap;
    
    /**
     * Instantiates a new signature generator with properties map.
     * 
     * @param parametersMap the parameters map
     */
    public SignatureGenerator(final Map<String, String> singleParamsMap, final Map<String, String> multiParamsMap,
            final Map<String, String> commonParameterMap) {
    
        parameterMap = new TreeMap<String, String>();
        this.singleParamMap = singleParamsMap;
        this.multiParamMap = multiParamsMap;
        this.commonParametersMap = commonParameterMap;
    }
    
    /**
     * Populate parameter map.
     */
    private final void populateParameterMap() {
    
        // Adding Mandatory Parameters which MUST be there in the MessageContext
        parameterMap.put(AmazonSESConstants.API_ACTION, commonParametersMap.get(AmazonSESConstants.API_ACTION));
        parameterMap.put(AmazonSESConstants.API_AWS_ACCESS_KEY_ID,
                commonParametersMap.get(AmazonSESConstants.API_AWS_ACCESS_KEY_ID));
        parameterMap.put(AmazonSESConstants.API_SIGNATURE_METHOD,
                commonParametersMap.get(AmazonSESConstants.API_SIGNATURE_METHOD));
        parameterMap.put(AmazonSESConstants.API_SIGNATURE_VERSION,
                commonParametersMap.get(AmazonSESConstants.API_SIGNATURE_VERSION));
        parameterMap.put(AmazonSESConstants.API_VERSION, commonParametersMap.get(AmazonSESConstants.API_VERSION));
        
        // Adding method specific Single-valued Parameters specified by the User
        if (singleParamMap != null) {
            parameterMap.putAll(singleParamMap);
            if (singleParamMap.get(AmazonSESConstants.API_RAW_MESSAGE) != null) {
                parameterMap.put(
                        AmazonSESConstants.API_RAW_MESSAGE,
                        new String(Base64.encodeBase64(singleParamMap.get(AmazonSESConstants.API_RAW_MESSAGE)
                                .getBytes())));
            }
        }
        
        // Adding method specific Multi-valued Parameters specified by the User
        if (multiParamMap != null) {
            for (Entry<String, String> entry : multiParamMap.entrySet()) {
                int i = 1;
                for (String individualValue : entry.getValue().split(AmazonSESConstants.SEPERATOR)) {
                    String suffix = AmazonSESConstants.SUFFIX_JOINER + (i++);
                    parameterMap.put(entry.getKey() + suffix, individualValue);
                }
            }
        }
    }
    
    /**
     * Builds the payload.
     * 
     * @return the string built payload
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public final String buildPayload() throws UnsupportedEncodingException {
    
        final StringBuilder payload = new StringBuilder();
        for (Entry<String, String> entry : parameterMap.entrySet()) {
            payload.append(entry.getKey()).append(AmazonSESConstants.ASSIGN)
                    .append(URLEncoder.encode(entry.getValue(), AmazonSESConstants.ENCODING_STYLE))
                    .append(AmazonSESConstants.AMPERSTAND);
        }
        payload.deleteCharAt(payload.length() - 1);
        return payload.toString();
    }
    
    /**
     * Generate signature.
     * 
     * @param accessKeyId the access key id
     * @param secretAccessKey the secret access key
     * @return the string generated signature
     * @throws InvalidKeyException the invalid key exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public final String generateSignature(final String accessKeyId, final String secretAccessKey)
            throws InvalidKeyException, NoSuchAlgorithmException {
    
        // populate the parameter map
        populateParameterMap();
        
        final SimpleDateFormat iso8601DateFormat = new SimpleDateFormat(AmazonSESConstants.ISO_8601_DATE_FORMATTER);
        iso8601DateFormat.setTimeZone(TimeZone.getTimeZone(AmazonSESConstants.GMT));
        String iso8601Date = iso8601DateFormat.format(new Date());
        
        parameterMap.put(AmazonSESConstants.BODY_PARAMETER_TIMESTAMP, iso8601Date);
        
        final byte[] digest = hmacSHA1(getFormattedDate(), secretAccessKey);
        final byte[] base64EncodedDigest = Base64.encodeBase64(digest);
        
        final StringBuilder authorizationHeader = new StringBuilder();
        authorizationHeader.append(AmazonSESConstants.API_AUTHORIZATION_HEADER_PREFIX).append(AmazonSESConstants.SPACE)
                .append(AmazonSESConstants.API_AWS_ACCESS_KEY_ID).append(AmazonSESConstants.ASSIGN).append(accessKeyId)
                .append(AmazonSESConstants.SEPERATOR).append(AmazonSESConstants.SPACE)
                .append(AmazonSESConstants.ALGORITHM).append(AmazonSESConstants.ASSIGN)
                .append(AmazonSESConstants.HMAC_ALGORITHM).append(AmazonSESConstants.SEPERATOR)
                .append(AmazonSESConstants.SPACE).append(AmazonSESConstants.API_SIGNATURE)
                .append(AmazonSESConstants.ASSIGN).append(new String(base64EncodedDigest));
        
        parameterMap.put(AmazonSESConstants.API_SIGNATURE, new String(base64EncodedDigest));
        
        return authorizationHeader.toString();
    }
    
    /**
     * Hmac sha1.
     * 
     * @param value the value
     * @param key the key
     * @return the byte[] hmacSha value
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws InvalidKeyException the invalid key exception
     */
    private final byte[] hmacSHA1(final String value, final String key) throws NoSuchAlgorithmException,
            InvalidKeyException {
    
        final byte[] keyBytes = key.getBytes();
        final SecretKeySpec signingKey = new SecretKeySpec(keyBytes, AmazonSESConstants.HMAC_ALGORITHM);
        final Mac mac = Mac.getInstance(AmazonSESConstants.HMAC_ALGORITHM);
        mac.init(signingKey);
        return mac.doFinal(value.getBytes());
    }
    
    /**
     * Gets the formatted date.
     * 
     * @return the formatted date
     */
    public final String getFormattedDate() {
    
        final SimpleDateFormat headerDateFormat = new SimpleDateFormat(AmazonSESConstants.HEADER_DATE_FORMATTER);
        headerDateFormat.setTimeZone(TimeZone.getTimeZone(AmazonSESConstants.GMT));
        final Date currentDate = new Date();
        return headerDateFormat.format(currentDate);
    }
}
