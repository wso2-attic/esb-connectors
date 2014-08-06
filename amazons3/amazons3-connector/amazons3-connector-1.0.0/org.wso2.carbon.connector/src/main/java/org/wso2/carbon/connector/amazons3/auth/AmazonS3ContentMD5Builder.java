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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.axiom.soap.SOAPBody;
import org.apache.commons.codec.binary.Base64;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.wso2.carbon.connector.amazons3.util.AmazonS3Constants;
import org.wso2.carbon.connector.core.AbstractConnector;

/**
 * Class AmazonS3ContentMD5Builder which helps to generate Base-64 encoded MD5 checksum for content-MD5 header for
 * Amazon S3 WSO2 ESB Connector.
 */
public class AmazonS3ContentMD5Builder extends AbstractConnector {
    
    /**
     * Connect method which is generating content MD5 header value for given delete configuration for delete multiple
     * objects method.
     * 
     * @param messageContext ESB messageContext.
     */
    public final void connect(final MessageContext messageContext) {
    
        String contentMD5Header = null;
        try {
            final SOAPBody body = messageContext.getEnvelope().getBody();
            contentMD5Header = getContentMD5Header(body.getFirstElement().toString());
            if (contentMD5Header != null) {
                messageContext.setProperty(AmazonS3Constants.CONTENT_MD5, contentMD5Header);
            }
        } catch (IOException ioe) {
            log.error("Error reading MD5 digest: ", ioe);
            storeErrorResponseStatus(messageContext, ioe, AmazonS3Constants.IO_EXCEPTION_ERROR_CODE);
            handleException("Error reading MD5 digest: ", ioe, messageContext);
        } catch (NoSuchAlgorithmException iae) {
            log.error("Invalid Algorithm", iae);
            storeErrorResponseStatus(messageContext, iae, AmazonS3Constants.NOSUCH_ALGORITHM_ERROR_CODE);
            handleException("Invalid Algorithm", iae, messageContext);
        } catch (Exception exc) {
            log.error("Error occured in connector", exc);
            storeErrorResponseStatus(messageContext, exc, AmazonS3Constants.ERROR_CODE_EXCEPTION);
            handleException("Error occured in connector", exc, messageContext);
        }
    }
    
    /**
     * Consume the string type delete configuration and returns its Base-64 encoded MD5 checksum as a string.
     * 
     * @param deleteConfig gets delete configuration as a string.
     * @return String type base-64 encoded MD5 checksum.
     * @throws IOException if an I/O error occurs when reading bytes of data from input stream.
     * @throws NoSuchAlgorithmException if no implementation for the specified algorithm.
     */
    private String getContentMD5Header(final String deleteConfig) throws IOException, NoSuchAlgorithmException {
    
        String contentHeader = null;
        // convert String into InputStream
        final InputStream inputStream = new ByteArrayInputStream(deleteConfig.getBytes(Charset.defaultCharset()));
        final DigestInputStream digestInputStream =
                new DigestInputStream(inputStream, MessageDigest.getInstance(AmazonS3Constants.MD5));
        
        final byte[] buffer = new byte[AmazonS3Constants.BUFFER_SIZE];
        while (digestInputStream.read(buffer) > 0) {
            contentHeader =
                    new String(Base64.encodeBase64(digestInputStream.getMessageDigest().digest()),
                            Charset.defaultCharset());
        }
        return contentHeader;
    }
    
    /**
     * Add a Throwable to a message context, the message from the throwable is embedded as the Synapse Constant
     * ERROR_MESSAGE.
     * 
     * @param ctxt message context to which the error tags need to be added
     * @param throwable Throwable that needs to be parsed and added
     * @param errorCode errorCode mapped to the exception
     */
    public static void storeErrorResponseStatus(final MessageContext ctxt, final Throwable throwable,
            final int errorCode) {
    
        ctxt.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, throwable.getMessage());
        ctxt.setFaultResponse(true);
    }
}
