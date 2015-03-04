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
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AcquiaContextDbGenerateSignature extends AbstractMediator {

    public static final String API_URI = "acquia.contextdb.apiUri";
    public static final String SECRET_KEY = "acquia.contextdb.secret.key";
    public static final String ACCESS_KEY = "acquia.contextdb.access.key";
    public static final String HTTP_METHOD = "acquia.contextdb.httpMethod";
    public static final String URL_PARAMETERS = "acquia.contextdb.parameters";
    private static final String UTF8 = "UTF-8";
    private static final String SIGNATURE = "acquia.contextdb.signature";

    public void generateSignature(MessageContext msgctx) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException {

        String secreteKey = msgctx.getProperty(SECRET_KEY).toString();
        String accessKey = msgctx.getProperty(ACCESS_KEY).toString();
        String httpMethod = msgctx.getProperty(HTTP_METHOD).toString();
        String apiUri = msgctx.getProperty(API_URI).toString();
        String queryParameters = msgctx.getProperty(URL_PARAMETERS).toString();
        Map<String,String> headerMap =(Map<String,String>) ((Axis2MessageContext)msgctx).getAxis2MessageContext().getProperty("TRANSPORT_HEADERS");


        String baseString = calculateMessage(httpMethod, headerMap, apiUri, queryParameters);

        if (baseString != "" && accessKey != null && accessKey != null) {
            //Create the HMAC signed Message
            String singedMessage = "HMAC " + accessKey + ":" + HMACAuthenticationUtil.calculateRFC2104HMAC(baseString, secreteKey);
            //Add the signature into the synapse property file
            msgctx.setProperty(SIGNATURE, singedMessage);
        } else {
            msgctx.setProperty(SIGNATURE, "");
        }


    }

    public boolean mediate(MessageContext messageContext) {
        try {
            generateSignature(messageContext);
        } catch (Exception e) {
            throw new SynapseException(e);
        }
        return true;
    }


    private String calculateMessage(String httpMethod,  Map<String,String> headers, String apiUri, String queryParameters) throws UnsupportedEncodingException {


        if (httpMethod != null && headers != null && apiUri != null && queryParameters != null) {
            StringBuilder baseString = new StringBuilder();

            //Added HTTP method in the first Line
            baseString.append(httpMethod);
            baseString.append("\n");

            //Added the headers
            String[] hashHeaders = { "Accept", "Host", "User-Agent" };
            for (String headerName : hashHeaders) {
                if (headers.containsKey(headerName)) {
                    baseString.append(headerName.toLowerCase()).append(":").append(headers.get(headerName).toString().trim()).append("\n");
                }
            }

            // add the URI
            baseString.append(URLEncoder.encode(apiUri, UTF8));

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
