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

package org.wso2.carbon.connector.flickr;

import org.apache.commons.codec.binary.Base64;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * This class generates the signature of the uri.
 */
public class GenerateSignature extends AbstractMediator {

    public static final String API_URI = "flickr.apiUri";
    public static final String CONSUMER_SECRET = "flickr.oauth.consumerKeySecret";
    public static final String ACCESS_TOKEN_SECRET = "flickr.oauth.accessTokenSecret";
    public static final String HTTP_METHOD = "flickr.httpMethod";
    public static final String URL_PARAMETERS = "flickr.parameters";

    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String ENC = "UTF-8";

    public void generateSignature(MessageContext msgctx) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException {

        StringBuilder baseString = new StringBuilder();

        baseString.append(msgctx.getProperty(HTTP_METHOD).toString());
        baseString.append("&");
        baseString.append(URLEncoder.encode(msgctx.getProperty(API_URI).toString(),ENC));

       /* generating the timestamp and nonce then replace the
        dummy oauth_nonce and oauth_timestamp with generated values.*/

        String parameters = msgctx.getProperty(URL_PARAMETERS).toString();
        //parameters = parameters.replace(" ","%20"); // URL encode the spaces in url.
        parameters = parameters.replace("dummynonce", Long.toString((long) (Math.random() * 100000000)));
        parameters = parameters.replace("dummytimestamp", String.valueOf((System.currentTimeMillis() / 1000)));
        baseString.append("&");
        baseString.append(URLEncoder.encode(parameters,ENC));

        msgctx.setProperty(URL_PARAMETERS,parameters);

        byte[] keyBytes = (msgctx.getProperty(CONSUMER_SECRET).toString()
                + "&" + msgctx.getProperty(ACCESS_TOKEN_SECRET).toString()).getBytes(ENC);

        SecretKey key = new SecretKeySpec(keyBytes, HMAC_SHA1);

        Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(key);
        Base64 base64 = new Base64();
        // encode it, base64 it, change it to string.
        String signature = "";
        signature = new String(base64.encode(mac.doFinal(baseString.toString().getBytes(ENC))), ENC).trim();
        while (signature.contains("+")) {
            signature = new String(base64.encode(mac.doFinal(baseString.toString().getBytes(ENC))), ENC).trim();
        }

        msgctx.setProperty("flickr.oauth.signature", URLEncoder.encode(signature, ENC));

    }

    public boolean mediate(MessageContext msgctx) {
        try {
            generateSignature(msgctx);
        } catch (Exception e) {
            throw new SynapseException(e);
        }
        return true;
    }

}
