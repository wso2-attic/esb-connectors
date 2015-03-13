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

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SignatureException;


public class HMACAuthenticationUtil {
    private static final String UTF8 = "UTF-8";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    /**
     * This method signs the message  HmacSHA1
     *
     * @param data
     * @param key
     * @return
     * @throws SignatureException
     */
    public static String calculateRFC2104HMAC(String data, String key) throws SignatureException {
        String result;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(UTF8), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = Base64.encodeBase64String(rawHmac);
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }
}
