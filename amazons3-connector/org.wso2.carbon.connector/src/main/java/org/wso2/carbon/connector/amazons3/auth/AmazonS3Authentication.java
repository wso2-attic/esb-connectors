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
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.wso2.carbon.connector.amazons3.util.AmazonS3Constants;

/**
 * Class AmazonS3Authentication to generate authorization header for Amazon S3 WSO2 ESB Connector.
 * 
 * @see http ://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#ConstructingTheAuthenticationHeader
 */
public class AmazonS3Authentication {
    
    /**
     * AWS access key ID.
     */
    private String accessKeyId;
    
    /**
     * AWS secret access key.
     */
    private String secretAccessKey;
    
    /**
     * Constructor for AmazonS3Authentication class.
     * 
     * @param awsAccessKeyId - accessKeyId passed in the as request parameter.
     * @param awsSecretAccessKey - secretAccessKey passed in the as request parameter.
     */
    public AmazonS3Authentication(final String awsAccessKeyId, final String awsSecretAccessKey) {
    
        this.accessKeyId = awsAccessKeyId;
        this.secretAccessKey = awsSecretAccessKey;
    }
    
    /**
     * getAuthorizationHeaderValue method returns the AmazonS3 Authorization header for a given signing String.
     * 
     * @param signingStr - A String based on select request elements
     * @return generated authorization header as String
     * @throws UnsupportedEncodingException This exception is thrown when the Character Encoding is not supported
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested
     *         but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length,
     *         uninitialized, etc).
     */
    public final String getAuthorizationHeaderValue(final String signingStr) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException {
    
        final Charset defaultCharset = Charset.defaultCharset();
        
        // converts AWSSecretKey into crypto instance.
        final byte[] keyBytes = secretAccessKey.getBytes(defaultCharset);
        final Mac mac = Mac.getInstance(AmazonS3Constants.HMAC_SHA1);
        mac.init(new SecretKeySpec(keyBytes, AmazonS3Constants.HMAC_SHA1));
        
        // Signed String must be BASE64 encoded.
        final byte[] signBytes = mac.doFinal(signingStr.getBytes(defaultCharset));
        final String signatureStr = new String(Base64.encodeBase64(signBytes), defaultCharset);
        String autherizationHeader = null;
        if (!signatureStr.isEmpty()) {
            autherizationHeader = AmazonS3Constants.AWS + accessKeyId + AmazonS3Constants.COLON + signatureStr;
        }
        return autherizationHeader;
    }
}
