/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package org.wso2.carbon.connector.authentication;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;
import java.net.URL;

public class AcquiaContextDbGenerateSignature extends AbstractMediator {

    public static final String API_URI = "acquia.contextdb.apiUri.final";
    public static final String SECRET_KEY = "acquia.contextdb.secret.key";
    public static final String ACCESS_KEY = "acquia.contextdb.access.key";
    public static final String HTTP_METHOD = "acquia.contextdb.httpMethod";
    public static final String URL_PARAMETERS = "acquia.contextdb.parameters";
    private static final String UTF8 = "UTF-8";
    private static final String SIGNATURE = "acquia.contextdb.signature";

    public boolean mediate(MessageContext messageContext) {
        try {
            generateSignature(messageContext);
        } catch (Exception e) {
            throw new SynapseException(e);
        }
        return true;
    }

    private void generateSignature(MessageContext msgctx) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException, MalformedURLException, SignatureException {
        String secreteKey = msgctx.getProperty(SECRET_KEY).toString();
        String accessKey = msgctx.getProperty(ACCESS_KEY).toString();
        String httpMethod = msgctx.getProperty(HTTP_METHOD).toString();
        String apiUri = msgctx.getProperty(API_URI).toString();
        String queryParameters = msgctx.getProperty(URL_PARAMETERS).toString();
        Map<String, String> tansportHeaderMap = (Map<String, String>) ((Axis2MessageContext) msgctx).getAxis2MessageContext().getProperty("TRANSPORT_HEADERS");

        String baseString = calculateMessage(httpMethod, tansportHeaderMap, apiUri, queryParameters);
        if (baseString != "" && accessKey != null && accessKey != null) {
            //Create the HMAC signed Message
            String singedMessage = "HMAC " + accessKey + ":" + HMACAuthenticationUtil.calculateRFC2104HMAC(baseString, secreteKey);
            //Add the signature into the synapse property file
            msgctx.setProperty(SIGNATURE, singedMessage.replaceAll("\r", "").replaceAll("\n", ""));
        } else {
            msgctx.setProperty(SIGNATURE, "");
        }
    }

    private String calculateMessage(String httpMethod, Map<String, String> tansportHeaderMap, String apiURL, String queryParameters) throws UnsupportedEncodingException, MalformedURLException {
        if (httpMethod != null && apiURL != null && queryParameters != null) {
            StringBuilder baseString = new StringBuilder();
            URL url = new URL(apiURL);
            Map<String, String> headerMap = new HashMap<String, String>();
            if (tansportHeaderMap != null && tansportHeaderMap.get("Accept") != null) {
                headerMap.put("Accept", tansportHeaderMap.get("Accept").toString());
            }
            headerMap.put("Host", url.getHost().toString());
            headerMap.put("User-Agent", "Synapse-PT-HttpComponents-NIO");
            //Added HTTP method in the first Line
            baseString.append(httpMethod);
            baseString.append("\n");
            //Added the headers
            String[] hashHeaders = {"Accept", "Host", "User-Agent"};
            for (String headerName : hashHeaders) {
                if (headerMap.containsKey(headerName)) {
                    baseString.append(headerName.toLowerCase()).append(":").append(headerMap.get(headerName).toString().trim()).append("\n");
                }
            }
            // add the URI
            baseString.append(url.getPath().toString());
            //Add the Parameters
            if (queryParameters != null && queryParameters.trim().length() > 0) {
                List<String> nameValuePairs = Arrays.<String>asList(queryParameters.split("&"));
                if (nameValuePairs.size() > 0) {
                    Collections.sort(nameValuePairs);
                    baseString.append("?").append(nameValuePairs.get(0));
                    for (int i = 1; i < nameValuePairs.size(); i++) {
                        baseString.append("&").append(nameValuePairs.get(i));
                    }
                }
            }
            return baseString.toString();
        } else {
            return "";
        }
    }
}