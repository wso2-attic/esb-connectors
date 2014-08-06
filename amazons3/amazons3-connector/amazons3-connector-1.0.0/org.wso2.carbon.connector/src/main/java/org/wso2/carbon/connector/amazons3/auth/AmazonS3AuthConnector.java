/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved. WSO2 Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.connector.amazons3.auth;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.wso2.carbon.connector.amazons3.util.AmazonS3Constants;
import org.wso2.carbon.connector.core.AbstractConnector;

/**
 * Class AmazonS3AuthConnector which helps to generate authentication header for Amazon S3 WSO2 ESB Connector.
 */
public class AmazonS3AuthConnector extends AbstractConnector {
    
    /**
     * Connect method which is generating authentication of the connector for each request.
     * 
     * @param messageContext ESB messageContext.
     */
    public final void connect(final MessageContext messageContext) {
    
        final StringBuilder builder = new StringBuilder();
        final Map<String, String> parametersMap = getParametersMap(messageContext);
        final Locale defaultLocale = Locale.getDefault();
        
        final SimpleDateFormat dateFormat = new SimpleDateFormat(AmazonS3Constants.CURR_DATE_FORMAT, defaultLocale);
        dateFormat.setTimeZone(TimeZone.getTimeZone(AmazonS3Constants.TIME_ZONE));
        final String currentDate = dateFormat.format(new Date());
        
        builder.append(parametersMap.get(AmazonS3Constants.METHOD_TYPE)).append(AmazonS3Constants.NEW_LINE);
        builder.append(parametersMap.get(AmazonS3Constants.CONTENT_MD5)).append(AmazonS3Constants.NEW_LINE);
        builder.append(parametersMap.get(AmazonS3Constants.CONTENT_TYPE)).append(AmazonS3Constants.NEW_LINE);
        
        final String dateTrimmed = currentDate.trim();
        final Map<String, String> amzHeadersMap = new HashMap<String, String>();
        
        if (Boolean.parseBoolean(parametersMap.get(AmazonS3Constants.IS_XAMZ_DATE))) {
            builder.append(AmazonS3Constants.NEW_LINE);
            amzHeadersMap.put(AmazonS3Constants.HD_XAMZ_DATE, dateTrimmed);
            messageContext.setProperty(AmazonS3Constants.IS_XAMZ_DATE_VAL, dateTrimmed);
        } else {
            builder.append(dateTrimmed).append(AmazonS3Constants.NEW_LINE);
        }
        
        final Map<String, String> amzHeaderKeysMap = getAmzHeaderKeysMap();
        for (Map.Entry<String, String> entry : amzHeaderKeysMap.entrySet()) {
            String key = entry.getKey();
            String tempParam = parametersMap.get(key);
            if (!tempParam.isEmpty()) {
                amzHeadersMap.put(amzHeaderKeysMap.get(key),
                        tempParam.replaceAll(AmazonS3Constants.REGEX, AmazonS3Constants.EMPTY_STR));
            }
        }
        
        final SortedSet<String> keys = new TreeSet<String>(amzHeadersMap.keySet());
        for (String key : keys) {
            String headerValues = amzHeadersMap.get(key);
            builder.append(key.toLowerCase(defaultLocale)).append(AmazonS3Constants.COLON).append(headerValues)
                    .append(AmazonS3Constants.NEW_LINE);
        }
        
        // Setting the canonicalized resource.
        builder.append(AmazonS3Constants.FORWARD_SLASH).append(parametersMap.get(AmazonS3Constants.BUCKET_NAME));
        String urlRemainder = (String) messageContext.getProperty(AmazonS3Constants.URI_REMAINDER);
        if (urlRemainder != null && !urlRemainder.isEmpty()) {
            builder.append(urlRemainder);
        }
        
        // Sign the created string.
        final AmazonS3Authentication amazonS3Authentication =
                new AmazonS3Authentication(parametersMap.get(AmazonS3Constants.ACCESS_KEY_ID),
                        parametersMap.get(AmazonS3Constants.SECRET_ACCESS_KEY));
        try {
            final String authenticationHeaderValue =
                    amazonS3Authentication.getAuthorizationHeaderValue(builder.toString());
            if (authenticationHeaderValue != null) {
                messageContext.setProperty(AmazonS3Constants.AUTH_CODE, authenticationHeaderValue);
            }
        } catch (InvalidKeyException ike) {
            log.error("Invalid key", ike);
            storeErrorResponseStatus(messageContext, ike, AmazonS3Constants.INVALID_KEY_ERROR_CODE);
            handleException("Invalid key", ike, messageContext);
        } catch (NoSuchAlgorithmException iae) {
            log.error("Invalid Algorithm", iae);
            storeErrorResponseStatus(messageContext, iae, AmazonS3Constants.NOSUCH_ALGORITHM_ERROR_CODE);
            handleException("Invalid Algorithm", iae, messageContext);
        } catch (UnsupportedEncodingException uee) {
            log.error("Encoding Not Supported", uee);
            storeErrorResponseStatus(messageContext, uee, AmazonS3Constants.UNSUPPORTED_ENCORDING_ERROR_CODE);
            handleException("Encoding Not Supported", uee, messageContext);
        } catch (Exception exc) {
            log.error("Error occured in connector", exc);
            storeErrorResponseStatus(messageContext, exc, AmazonS3Constants.ERROR_CODE_EXCEPTION);
            handleException("Error occured in connector", exc, messageContext);
        }
        
        // Set Message Headers.
        messageContext.setProperty(AmazonS3Constants.DATE, currentDate);
    }
    
    /**
     * getKeys method used to return list of predefined parameter keys.
     * 
     * @return list of parameter key value.
     */
    private String[] getKeys() {
    
        return new String[] { AmazonS3Constants.ACCESS_KEY_ID, AmazonS3Constants.SECRET_ACCESS_KEY,
                AmazonS3Constants.METHOD_TYPE, AmazonS3Constants.CONTENT_MD5, AmazonS3Constants.CONTENT_TYPE,
                AmazonS3Constants.BUCKET_NAME, AmazonS3Constants.IS_XAMZ_DATE, AmazonS3Constants.XAMZ_SECURITY_TOKEN,
                AmazonS3Constants.XAMZ_ACL, AmazonS3Constants.XAMZ_GRANT_READ, AmazonS3Constants.XAMZ_GRANT_WRITE,
                AmazonS3Constants.XAMZ_GRANT_READ_ACP, AmazonS3Constants.XAMZ_GRANT_WRITE_ACP,
                AmazonS3Constants.XAMZ_GRANT_FULL_CONTROL, AmazonS3Constants.XAMZ_META,
                AmazonS3Constants.XAMZ_SERVE_ENCRYPTION, AmazonS3Constants.XAMZ_STORAGE_CLASS,
                AmazonS3Constants.XAMZ_WEBSITE_LOCATION, AmazonS3Constants.XAMZ_MFA,
                AmazonS3Constants.XAMZ_COPY_SOURCE, AmazonS3Constants.XAMZ_METADATA_DIRECTIVE,
                AmazonS3Constants.XAMZ_COPY_SOURCE_IF_MATCH, AmazonS3Constants.XAMZ_COPY_SOURCE_IF_NONE_MATCH,
                AmazonS3Constants.XAMZ_COPY_SOURCE_IF_UNMODIFIED_SINCE,
                AmazonS3Constants.XAMZ_COPY_SOURCE_IF_MODIFIED_SINCE };
    }
    
    /**
     * getParametersMap method used to return list of parameter values passed in via proxy.
     * 
     * @param messageContext ESB messageContext.
     * @return assigned parameter values as a HashMap.
     */
    private Map<String, String> getParametersMap(final MessageContext messageContext) {
    
        String[] keys = getKeys();
        Map<String, String> parametersMap = new HashMap<String, String>();
        for (byte index = 0; index < keys.length; index++) {
            String paramValue =
                    (messageContext.getProperty(keys[index]) != null) ? (String) messageContext
                            .getProperty(keys[index]) : AmazonS3Constants.EMPTY_STR;
            parametersMap.put(keys[index], paramValue);
        }
        return parametersMap;
    }
    
    /**
     * getAmzHeaderKeysMap method used to return list of predefined XAMZ keys with values.
     * 
     * @return list of Amz header keys and values Map.
     */
    private Map<String, String> getAmzHeaderKeysMap() {
    
        Map<String, String> amzHeaderKeysMap = new HashMap<String, String>();
        
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_SECURITY_TOKEN, AmazonS3Constants.HD_XAMZ_SECURITY_TOKEN);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_ACL, AmazonS3Constants.HD_XAMZ_ACL);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_GRANT_READ, AmazonS3Constants.HD_XAMZ_GRANT_READ);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_GRANT_WRITE, AmazonS3Constants.HD_XAMZ_GRANT_WRITE);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_GRANT_READ_ACP, AmazonS3Constants.HD_XAMZ_GRANT_READ_ACP);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_GRANT_WRITE_ACP, AmazonS3Constants.HD_XAMZ_GRANT_WRITE_ACP);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_GRANT_FULL_CONTROL, AmazonS3Constants.HD_XAMZ_GRANT_FULL_CONTROL);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_META, AmazonS3Constants.HD_XAMZ_META);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_SERVE_ENCRYPTION, AmazonS3Constants.HD_XAMZ_SERVE_ENCRYPTION);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_STORAGE_CLASS, AmazonS3Constants.HD_XAMZ_STORAGE_CLASS);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_WEBSITE_LOCATION, AmazonS3Constants.HD_XAMZ_WEBSITE_LOCATION);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_MFA, AmazonS3Constants.HD_XAMZ_MFA);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_COPY_SOURCE, AmazonS3Constants.HD_XAMZ_COPY_SOURCE);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_METADATA_DIRECTIVE, AmazonS3Constants.HD_XAMZ_METADATA_DIRECTIVE);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_COPY_SOURCE_IF_MATCH,
                AmazonS3Constants.HD_XAMZ_COPY_SOURCE_IF_MATCH);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_COPY_SOURCE_IF_NONE_MATCH,
                AmazonS3Constants.HD_XAMZ_COPY_SOURCE_IF_NONE_MATCH);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_COPY_SOURCE_IF_UNMODIFIED_SINCE,
                AmazonS3Constants.HD_XAMZ_COPY_SOURCE_IF_UNMODIFIED_SINCE);
        amzHeaderKeysMap.put(AmazonS3Constants.XAMZ_COPY_SOURCE_IF_MODIFIED_SINCE,
                AmazonS3Constants.HD_XAMZ_COPY_SOURCE_IF_MODIFIED_SINCE);
        
        return amzHeaderKeysMap;
    }
    
    /**
     * Add a Throwable to a message context, the message from the throwable is embedded as the Synapse Constant
     * ERROR_MESSAGE.
     * 
     * @param ctxt message context to which the error tags need to be added
     * @param throwable Throwable that needs to be parsed and added
     * @param errorCode errorCode mapped to the exception
     */
    public void storeErrorResponseStatus(final MessageContext ctxt, final Throwable throwable, final int errorCode) {
    
        ctxt.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, throwable.getMessage());
        ctxt.setFaultResponse(true);
    }
    
}
