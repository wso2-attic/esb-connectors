/*
 *  Copyright (c) 2005-2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.amazonses.auth;
import java.nio.charset.Charset;
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
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.wso2.carbon.connector.amazonses.constants.AmazonSESConstants;
import org.wso2.carbon.connector.core.AbstractConnector;

/**
 * AmazonSESMediatorBase class generates and create authorization code.
 */
public abstract class AmazonSESMediatorBase extends AbstractConnector {

    /**
     * The header date format.
     */
    private SimpleDateFormat headerDateFormat;

    /**
     * The iso8601 date format.
     */
    private SimpleDateFormat iso8601DateFormat;

    /**
     * Axis implementation of the javax SOAPMessageContext class.
     */
    private MessageContext messageContext;

    /**
     * The parameter map.
     */
    private Map<String, String> parameterMap;

    /**
     * Connect method which is generating authentication of the connector for each request.
     *
     * @param context ESB messageContext.
     */
    public final void connect(final MessageContext context) {

        this.messageContext = context;

        headerDateFormat = new SimpleDateFormat(AmazonSESConstants.HEADER_DATE_FORMATTER);
        headerDateFormat.setTimeZone(TimeZone.getTimeZone(AmazonSESConstants.GMT));
        iso8601DateFormat = new SimpleDateFormat(AmazonSESConstants.ISO_8601_DATE_FORMATTER);
        iso8601DateFormat.setTimeZone(TimeZone.getTimeZone(AmazonSESConstants.GMT));
        parameterMap = new TreeMap<String, String>();

        populateParameterMap();
        generateSignature();
        buildPayload();

    }

    /**
     * Populate parameter map by adding Mandatory Parameters from the MessageContext.
     */
    private void populateParameterMap() {

        parameterMap.put(AmazonSESConstants.API_ACTION,
                (String) messageContext.getProperty(AmazonSESConstants.ESB_ACTION));
        parameterMap.put(AmazonSESConstants.API_AWS_ACCESS_KEY_ID,
                (String) messageContext.getProperty(AmazonSESConstants.ESB_AWS_ACCESS_KEY_ID));
        parameterMap.put(AmazonSESConstants.API_SIGNATURE_METHOD,
                (String) messageContext.getProperty(AmazonSESConstants.ESB_SIGNATURE_METHOD));
        parameterMap.put(AmazonSESConstants.API_SIGNATURE_VERSION,
                (String) messageContext.getProperty(AmazonSESConstants.ESB_SIGNATURE_VERSION));
        parameterMap.put(AmazonSESConstants.API_VERSION,
                (String) messageContext.getProperty(AmazonSESConstants.ESB_VERSION));

        // Adding method specific Single-valued Parameters specified by the User
        Map<String, String> singleValuedParameterMap = getSingleValuedParametersMap();
        if (singleValuedParameterMap != null) {
        	Charset charset = Charset.defaultCharset();
            for (Entry<String, String> entry : singleValuedParameterMap.entrySet()) {
                String key = entry.getKey();
                String property = messageContext.getProperty(entry.getValue()).toString();
                if (!AmazonSESConstants.EMPTY.equals(property)) {
                	// Only RawMessage.Data parameter has to be Base64Encoded in the Request
                    if (AmazonSESConstants.API_RAW_MESSAGE.equals(key))
                        parameterMap.put(key, new String(Base64.encodeBase64(property.getBytes(charset)), charset));
                    else
                    	parameterMap.put(key, property);
                }
            }
        }

        // Adding method specific Multi-valued Parameters specified by the User
        Map<String, String> multiValuedParameterMap = getMultiValuedParametersMap();
        if (multiValuedParameterMap != null) {
            for (Entry<String, String> entry : multiValuedParameterMap.entrySet()) {
                String property = messageContext.getProperty(entry.getValue()).toString();
                if (!AmazonSESConstants.EMPTY.equals(property)) {
                    int i = 1;
                    for (String individualValue : property.split(AmazonSESConstants.SEPERATOR)) {
                        String suffix = AmazonSESConstants.SUFFIX_JOINER + i++;
                        parameterMap.put(entry.getKey() + suffix, individualValue);
                    }
                }
            }
        }
    }

    /**
     * Builds the payload.
     */
    private void buildPayload() {
        StringBuilder payload = new StringBuilder();
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {

            // Generating json Payload.
            payload.append('"');
            payload.append(entry.getKey());
            payload.append('"');
            payload.append(':');
            payload.append('"');
            payload.append(entry.getValue());
            payload.append('"');
            payload.append(',');

        }

        // Adds authorization header to message context, removes additionally appended comma at the end
        if (payload.length() > 0) {
            messageContext.setProperty(AmazonSESConstants.ESB_REQUEST_PAYLOAD_SET,
                    "{" + payload.substring(0, payload.length()-1) + "}");
        }
    }

    /**
     * Generating the Hmac sha using value and key.
     *
     * @param value the value
     * @param key the key
     * @return the byte[]
     */
    private byte[] hmacSHA1(final String value, final String key) {

        Charset charset = Charset.defaultCharset();
        byte[] keyBytes = key.getBytes(charset);
        final SecretKeySpec signingKey = new SecretKeySpec(keyBytes, AmazonSESConstants.HMAC_ALGORITHM);
        Mac mac = null;
        try {
            mac = Mac.getInstance(AmazonSESConstants.HMAC_ALGORITHM);
            mac.init(signingKey);
        } catch (NoSuchAlgorithmException nsa) {
            log.error(AmazonSESConstants.CONNECTOR_ERROR, nsa);
            storeErrorResponseStatus(messageContext, nsa, AmazonSESConstants.NOSUCH_ALGORITHM_ERROR_CODE);
            handleException(AmazonSESConstants.CONNECTOR_ERROR, nsa, messageContext);
        } catch (InvalidKeyException ike) {
            log.error(AmazonSESConstants.CONNECTOR_ERROR, ike);
            storeErrorResponseStatus(messageContext, ike, AmazonSESConstants.INVALID_KEY_ERROR_CODE);
            handleException(AmazonSESConstants.CONNECTOR_ERROR, ike, messageContext);
        }

        return mac.doFinal(value.getBytes(charset));
    }

    /**
     * Generates the signature.
     */
    private void generateSignature() {

        final Date currentDate = new Date();
        final String headerDate = headerDateFormat.format(currentDate);
        messageContext.setProperty(AmazonSESConstants.ESB_X_AMZ_DATE_HEADER_SET, headerDate);

        String iso8601Date = iso8601DateFormat.format(currentDate);
        parameterMap.put(AmazonSESConstants.BODY_PARAMETER_TIMESTAMP, iso8601Date);

        byte[] digest =
                hmacSHA1(headerDate, (String) messageContext.getProperty(AmazonSESConstants.ESB_SECRET_ACCESS_KEY));
        byte[] base64EncodedDigest = Base64.encodeBase64(digest);

        StringBuilder authorizationHeader = new StringBuilder();
        Charset charset = Charset.defaultCharset();
        authorizationHeader.append(AmazonSESConstants.API_AUTHORIZATION_HEADER_PREFIX).append(AmazonSESConstants.SPACE)
                .append(AmazonSESConstants.API_AWS_ACCESS_KEY_ID).append(AmazonSESConstants.ASSIGN)
                .append(messageContext.getProperty(AmazonSESConstants.ESB_AWS_ACCESS_KEY_ID).toString())
                .append(AmazonSESConstants.SEPERATOR).append(AmazonSESConstants.SPACE)
                .append(AmazonSESConstants.ALGORITHM).append(AmazonSESConstants.ASSIGN)
                .append(AmazonSESConstants.HMAC_ALGORITHM).append(AmazonSESConstants.SEPERATOR)
                .append(AmazonSESConstants.SPACE).append(AmazonSESConstants.API_SIGNATURE)
                .append(AmazonSESConstants.ASSIGN).append(new String(base64EncodedDigest, charset));

        messageContext.setProperty(AmazonSESConstants.ESB_X_AMZN_AUTHORIZATION_HEADER_SET,
                authorizationHeader.toString());
        parameterMap.put(AmazonSESConstants.API_SIGNATURE, new String(base64EncodedDigest, charset));
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
     * Gets the single valued parameters map.
     *
     * @return the single valued parameters map
     */
    protected Map<String, String> getSingleValuedParametersMap() {

        return null;
    }

    /**
     * Gets the multi valued parameters map.
     *
     * @return the multi valued parameters map
     */
    protected Map<String, String> getMultiValuedParametersMap() {

        return null;
    }
}
