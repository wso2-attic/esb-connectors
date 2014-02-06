
package org.wso2.carbon.connector.amazons3.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Authentication class to generate authorization header for Amazon S3 WSO2 ESB Connector.
 * 
 * @see http 
 *      ://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#ConstructingTheAuthenticationHeader
 */
public class AmazonS3Authentication {
    
    /**
     * Message Authentication Code Variable declaration.
     */
    private Mac mac;
    
    /**
     * AWS access key ID.
     */
    private String accessKeyId;
    
    /**
     * AWS secret access key.
     */
    private String secretAccessKey;
    
    /**
     * This is constructor for AmazonS3Authentication class.
     * 
     * @param awsAccessKeyId - accessKeyId common header
     * @param awsSecretAccessKey - secretAccessKey common header
     */
    public AmazonS3Authentication(final String awsAccessKeyId, final String awsSecretAccessKey) {
    
        this.accessKeyId = awsAccessKeyId;
        this.secretAccessKey = awsSecretAccessKey;
    }
    
    /**
     * This method converts AWSSecretKey into crypto instance.
     * 
     * @throws Exception - General Exception
     */
    public final void setKey() throws Exception {
    
        mac = Mac.getInstance(AmazonS3Utils.HMAC_SHA1);
        byte[] keyBytes = secretAccessKey.getBytes(AmazonS3Utils.UTF8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AmazonS3Utils.HMAC_SHA1);
        mac.init(secretKeySpec);
        
    }
    
    /**
     * This method return the signature for a given String.
     * 
     * @param data - String type
     * @return base64 encoded signature as String
     * @throws Exception - General Exception
     */
    private String getSignature(final String data) throws Exception {
    
        if (mac != null) {
            // Signed String must be BASE64 encoded.
            byte[] signBytes = mac.doFinal(data.getBytes(AmazonS3Utils.UTF8));
            return new String(Base64.encodeBase64(signBytes), AmazonS3Utils.UTF8);
        }
        
        return null;
        
    }
    
    /**
     * This method returns the AmazonS3 Authorization header.
     * 
     * @param data - String type
     * @return generated authorization header as String
     * @throws Exception - General Exception
     */
    public final String getAuthorizationHeaderValue(final String data) throws Exception {
    
        String signature = getSignature(data);
        
        if (signature != null) {
            return (AmazonS3Utils.AWS + accessKeyId + AmazonS3Utils.COLON + signature);
        }
        
        return null;
        
    }
    
}
