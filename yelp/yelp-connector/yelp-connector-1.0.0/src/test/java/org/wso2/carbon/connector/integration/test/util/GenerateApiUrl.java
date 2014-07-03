/**
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.util;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class GenerateApiUrl {

    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String ENC = "UTF-8";

    public static String getApiUrl(String httpMethod,String apiEndPoint,String parameters, Properties connectorProperties) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String processParameters=processParameters(parameters);
        return apiEndPoint+"?"+processParameters+"&oauth_signature="+generateSignature(httpMethod,apiEndPoint,processParameters,connectorProperties);
    }

    public static String processParameters(String parameters){

        parameters = parameters.replace("dummynonce", Long.toString((long) (Math.random() * 1000000)));
        parameters = parameters.replace("dummytimestamp", String.valueOf((System.currentTimeMillis() / 1000)));
        return parameters;
    }

    public static String generateSignature(String httpMethod,String apiEndPoint,String processedParameters, Properties connectorProperties)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        StringBuilder baseString = new StringBuilder();
        baseString.append(httpMethod);
        baseString.append("&");
        baseString.append(URLEncoder.encode(apiEndPoint, ENC));
        processedParameters= processedParameters.replace(" ","%20"); // URL encode the spaces in url.
        baseString.append("&");
        baseString.append(URLEncoder.encode(processedParameters,ENC));

        byte[] keyBytes = (connectorProperties.getProperty("consumerKeySecret")
                + "&" + connectorProperties.getProperty("accessTokenSecret")).getBytes(ENC);

        SecretKey key = new SecretKeySpec(keyBytes, HMAC_SHA1);
        Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(key);
        Base64 base64 = new Base64();
        String signature =
                new String(base64.encode(mac.doFinal(baseString.toString().getBytes(ENC))), ENC).trim();
        return URLEncoder.encode(signature,ENC);
    }
}
