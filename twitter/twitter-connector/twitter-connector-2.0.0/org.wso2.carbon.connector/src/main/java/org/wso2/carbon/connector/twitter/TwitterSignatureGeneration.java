/*
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.twitter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.wso2.carbon.connector.core.AbstractConnector;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.lang.Exception;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;

/**
 * generate the authorization header
 */
public class TwitterSignatureGeneration extends AbstractConnector {

    private static Log log = LogFactory.getLog(TwitterSignatureGeneration.class);

    public void connect(MessageContext msgContext) throws SynapseException {
        try {
            log.debug("Starting to generate the header with the signature");
            generateSignature(msgContext.getProperty(TwitterConstants.TWITTER_CONSUMER_KEY).toString(),
                    msgContext.getProperty(TwitterConstants.TWITTER_CONSUMER_SECRET).toString(),
                    msgContext.getProperty(TwitterConstants.TWITTER_ACCESS_TOKEN).toString(),
                    msgContext.getProperty(TwitterConstants.TWITTER_ACCESS_TOKEN_SECRET).toString(), msgContext);
            if (log.isDebugEnabled()) {
                log.debug("Loaded the twitter consumerKey : " + TwitterConstants.TWITTER_CONSUMER_KEY
                        + ",consumerSecret : " + TwitterConstants.TWITTER_CONSUMER_SECRET + ",accessToken : "
                        + TwitterConstants.TWITTER_ACCESS_TOKEN + ",accessSecret : " + TwitterConstants.TWITTER_ACCESS_TOKEN_SECRET);
            }
        } catch (Exception e) {
            handleException("Error while generating the header", e);
        }
    }

    /**
     * encode the value
     *
     * @param value the url param or the endpoint
     * @return the encoded string
     */
    public String encode(String value) {
        if (log.isDebugEnabled()) {
            log.debug("Starting to encode : " + value);
        }
        String encoded;
        try {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("The encoding format is  : " + TwitterConstants.ENC);
                }
                encoded = URLEncoder.encode(value, TwitterConstants.ENC);
            } catch (UnsupportedEncodingException usee) {
                log.error("Unsupported encoding", usee);
                throw new SynapseException("Unsupported encoding", usee);
            }
            StringBuilder buf = null;
            if (encoded != null) {
                buf = new StringBuilder(encoded.length());
                char focus;
                for (int i = 0; i < encoded.length(); i++) {
                    focus = encoded.charAt(i);
                    if (focus == '*') {
                        buf.append("%2A");
                    } else if (focus == '+') {
                        buf.append("%20");
                    } else if (focus == '%' && (i + 1) < encoded.length()
                            && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                        buf.append('~');
                        i += 2;
                    } else {
                        buf.append(focus);
                    }
                }
            }
            if (buf != null) {
                if (log.isDebugEnabled()) {
                    log.debug(value + " is encoded to " + buf.toString());
                }
                return buf.toString();
            }
        } catch (Exception e) {
            handleException("Error while encoding", e);
        }
        return null;
    }

    /**
     * compute the signature for the authorization header
     *
     * @param baseString the signature base
     * @param keyString  the key string
     * @return generated signature with the twitter credential, url params,timestamp and nonce value
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    private static String computeSignature(String baseString, String keyString) throws GeneralSecurityException, UnsupportedEncodingException {
        log.debug("Starting to compute the signature");
        try {
            SecretKey secretKey;
            byte[] keyBytes = keyString.getBytes();
            secretKey = new SecretKeySpec(keyBytes, TwitterConstants.SIGNATURE_METHOD);
            Mac mac = Mac.getInstance(TwitterConstants.SIGNATURE_METHOD);
            mac.init(secretKey);
            byte[] text = baseString.getBytes();
            return new String(Base64.encodeBase64(mac.doFinal(text))).trim();
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    /**
     * generate the authorization header using oauth1.a mechanism
     *
     * @param consumerKey consumer key of twitter account
     * @param consumerSecret consumer secret of twitter account
     * @param accessToken access token of the twitter account
     * @param accessTokenSecret access token secret of twitter account
     * @param msgContext the message context
     */
    public void generateSignature(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret, MessageContext msgContext) {
        log.debug("Starting to generate the signature header");
        try {
            //the HTTP method type of the API method
            String httpMethod = (String) msgContext.getProperty(TwitterConstants.HTTP_METHOD);
            //the API endpoint
            String twitterEndpoint = (String) msgContext.getProperty(TwitterConstants.TWITTER_ENDPOINT);
            //generate the nonce value
            String randomString = UUID.randomUUID().toString();
            randomString = randomString.replaceAll("-", "");
            String oauthNonce = randomString;
            //get the timestamp
            Calendar calendar = Calendar.getInstance();
            long ts = calendar.getTimeInMillis();
            String oauthTimestamp = (new Long(ts / 1000)).toString();
            //get the required URL parameters
            Map<String, String> parametersMap = getParametersMap(msgContext);
            parametersMap.put("uri.var.consumerKey", "oauth_consumer_key=" + consumerKey);
            parametersMap.put("uri.var.oauthToken", "oauth_token=" + accessToken);
            parametersMap.put("uri.var.nonce", "oauth_nonce=" + oauthNonce);
            parametersMap.put("uri.var.timestamp", "oauth_timestamp=" + oauthTimestamp);
            parametersMap.put("uri.var.signatureMethod", "oauth_signature_method=" + TwitterConstants.SIGNATURE_METHOD);
            parametersMap.put("uri.var.oauthVersion", "oauth_version=1.0");

            //sort the parameters alphabetically
            Map<String, String> sortedMap = sortByValue(parametersMap);

            StringBuilder str = new StringBuilder("");
            for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
                if (!(entry.getValue().equals(""))) {
                    str.append(entry.getValue()).append("&");
                }
            }
            str.delete(str.length() - 1, str.length());
            String signatureBase = httpMethod + "&" + encode(twitterEndpoint) + "&" + encode(str.toString());

            //the base string is signed consumer secret and access token secret
            String oauthSignature = "";
            try {
                oauthSignature = computeSignature(signatureBase, consumerSecret + "&" + encode(accessTokenSecret));
            } catch (GeneralSecurityException e) {
                throw new SynapseException(e);
            } catch (UnsupportedEncodingException e) {
                throw new SynapseException(e);
            }

            String authorizationHeader = "OAuth oauth_consumer_key=\"" + consumerKey + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + oauthTimestamp +
                    "\",oauth_nonce=\"" + oauthNonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + encode(oauthSignature) + "\",oauth_token=\"" + encode(accessToken) + "\"";

            if (log.isDebugEnabled()) {
                log.debug("The authorization header is " + authorizationHeader);
            }
            msgContext.setProperty("uri.var.signature", authorizationHeader);
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * get list of URL parameter.
     *
     * @param messageContext the messageContext.
     * @return assigned parameter values as a Map.
     */
    private Map<String, String> getParametersMap(final MessageContext messageContext) {
        log.debug("Starting to collect the url parameters");
        try {
            Object[] keys = messageContext.getPropertyKeySet().toArray();
            Map<String, String> parametersMap = new HashMap<String, String>();
            for (Object key : keys) {
                if ((key).toString().startsWith("uri.var.") && !(key).toString().startsWith("uri.var.uriParams")
                        && !(key).toString().startsWith("uri.var.apiUrl")
                        && !(key).toString().startsWith("uri.var.httpMethod")
                        && !(key).toString().startsWith("uri.var.apiUrl.final")) {
                    String paramValue = (messageContext.getProperty((String) (key)).toString() != null) ? messageContext.getProperty((String) (key)).toString() : TwitterConstants.EMPTY_STR;
                    parametersMap.put((key).toString(), paramValue);
                }
            }
            return parametersMap;
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    /**
     * sort the header parameters alphabetically
     *
     * @param unsortedMap the unsorted parameters
     * @return the sorted parameters
     */
    public static Map sortByValue(Map unsortedMap) {
        log.debug("Starting to sort the header parameters");
        try {
            List list = new LinkedList(unsortedMap.entrySet());
            Collections.sort(list, new Comparator() {
                public int compare(Object object1, Object object2) {
                    return ((Comparable) ((Map.Entry) (object1)).getValue())
                            .compareTo(((Map.Entry) (object2)).getValue());
                }
            });
            Map sortedMap = new LinkedHashMap();
            for (Object aList : list) {
                Map.Entry entry = (Map.Entry) aList;
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            return sortedMap;
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    private static void handleException(String msg, Exception ex) {
        log.error(msg, ex);
        throw new SynapseException(msg, ex);
    }

    private static void handleException(Exception ex) {
        throw new SynapseException(ex);
    }
}

