/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved. WSO2 Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.connector.integration.test.amazonsimpledb;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Class AmazonSimpleDBAuthentication to generate signature for Amazon simple DB WSO2 ESB Connector.
 * 
 * @see http://docs.aws.amazon.com/AmazonSimpleDB/latest/DeveloperGuide/HMACAuth.html#RequiredAuthInfo
 */
public class AmazonSimpleDBAuthentication {

    /**
     * AWS secret access key.
     */
    private String secretAccessKey;

    /**
     * Constructor for AmazonSimpleDBAuthentication class.
     *
     * @param awsSecAccesKey - secretAccessKey passed in the as request parameter.
     */
    public AmazonSimpleDBAuthentication(final String awsSecAccesKey) {

        this.secretAccessKey = awsSecAccesKey;
    }

    /**
     * getAuthorizationHeaderValue method returns the AmazonSimpleDB signature for a given signing String.
     *
     * @param signingStr - A String based on select request elements
     * @return generated authorization header as String
     * @throws UnsupportedEncodingException This exception is thrown when the Character Encoding is not
     *         supported
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is
     *         requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length,
     *         uninitialized, etc).
     */
    public final String getAuthorizationSignature(final String signingStr) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException {

        final Charset defaultCharset = Charset.defaultCharset();

        // converts AWSSecretKey into crypto instance.
        final byte[] keyBytes = secretAccessKey.getBytes(defaultCharset);
        final Mac mac = Mac.getInstance(AmazonSimpleDBConstants.HMAC_SHA1);
        mac.init(new SecretKeySpec(keyBytes, AmazonSimpleDBConstants.HMAC_SHA1));

        // compute the hmac on input data bytes
        final byte[] signBytes = mac.doFinal(signingStr.getBytes(defaultCharset));

        // Returned Signed Base64 encoded signed String.
        return new String(Base64.encodeBase64(signBytes) , defaultCharset);
    }
}
