
package org.wso2.carbon.connector.amazons3.auth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;

/**
 * Class mediator which helps to generate authentication header for Amazon S3 WSO2 ESB Connector.
 */
public class AuthenticationHelper extends AbstractConnector implements Connector {
    
    /**
     * connect method which is generating authentication of the connector for each request.
     * 
     * @param messageContext ESB messageContext.
     * @throws ConnectException if connection fails.
     */
    
    public void connect(MessageContext messageContext) throws ConnectException {
    
        try {
            
            /*
             * Get the data from the message context, which needed to generate authorization header.
             */
            
            String accessKeyId =
                    (getParameter(messageContext, AmazonS3Utils.ACCESS_KEY_ID) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.ACCESS_KEY_ID) : AmazonS3Utils.EMPTY_STR;
            
            String secretAccessKey =
                    (getParameter(messageContext, AmazonS3Utils.SECRET_ACCESS_KEY) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.SECRET_ACCESS_KEY) : AmazonS3Utils.EMPTY_STR;
            
            String httpMethod =
                    (getParameter(messageContext, AmazonS3Utils.METHOD_TYPE) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.METHOD_TYPE) : AmazonS3Utils.EMPTY_STR;
            
            String contentMD5 =
                    (getParameter(messageContext, AmazonS3Utils.CONTENT_MD5) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.CONTENT_MD5) : AmazonS3Utils.EMPTY_STR;
            
            String contentType =
                    (getParameter(messageContext, AmazonS3Utils.CONTENT_TYPE) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.CONTENT_TYPE) : AmazonS3Utils.EMPTY_STR;
            
            String bucketName =
                    (getParameter(messageContext, AmazonS3Utils.BUCKET_NAME) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.BUCKET_NAME) : AmazonS3Utils.EMPTY_STR;
            
            String uriRemainder =
                    (getParameter(messageContext, AmazonS3Utils.URI_REMAINDER) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.URI_REMAINDER) : AmazonS3Utils.EMPTY_STR;
            
            boolean xAmzDate =
                    ((getParameter(messageContext, AmazonS3Utils.XAMZ_DATE) != null) && (((String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_DATE)).equalsIgnoreCase(Boolean.TRUE.toString())))
                            ? true : false;
            
            String xAmzSecurityToken =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_SECURITY_TOKEN) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_SECURITY_TOKEN) : AmazonS3Utils.EMPTY_STR;
            
            String xAmzAcl =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_ACL) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_ACL) : AmazonS3Utils.EMPTY_STR;
            
            String xAmzGrantRead =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_GRANT_READ) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_GRANT_READ) : AmazonS3Utils.EMPTY_STR;
            
            String xAmzGrantWrite =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_GRANT_WRITE) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_GRANT_WRITE) : AmazonS3Utils.EMPTY_STR;
            
            String xAmzGrantReadAcp =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_GRANT_READ_ACP) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_GRANT_READ_ACP) : AmazonS3Utils.EMPTY_STR;
            
            String xAmzGrantWriteAcp =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_GRANT_WRITE_ACP) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_GRANT_WRITE_ACP) : AmazonS3Utils.EMPTY_STR;
            
            String xAmzGrantFullControl =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_GRANT_FULL_CONTROL) != null)
                            ? (String) getParameter(messageContext, AmazonS3Utils.XAMZ_GRANT_FULL_CONTROL)
                            : AmazonS3Utils.EMPTY_STR;
            
            // CreateObject method related Amz headers.
            String xAmzMeta =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_META) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_META) : AmazonS3Utils.EMPTY_STR;
            
            String xAmzServeEncryption =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_SERVE_ENCRYPTION) != null)
                            ? (String) getParameter(messageContext, AmazonS3Utils.XAMZ_SERVE_ENCRYPTION)
                            : AmazonS3Utils.EMPTY_STR;
            
            String xAmzStorageClass =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_STORAGE_CLASS) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_STORAGE_CLASS) : AmazonS3Utils.EMPTY_STR;
            
            String xAmzWebsiteLocation =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_WEBSITE_LOCATION) != null)
                            ? (String) getParameter(messageContext, AmazonS3Utils.XAMZ_WEBSITE_LOCATION)
                            : AmazonS3Utils.EMPTY_STR;
            
            String xAmzMfa =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_MFA) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_MFA) : AmazonS3Utils.EMPTY_STR;
            
            // Headers that are requested for createObjectCopy.
            String xAmzCopySource =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_COPY_SOURCE) != null) ? (String) getParameter(
                            messageContext, AmazonS3Utils.XAMZ_COPY_SOURCE) : AmazonS3Utils.EMPTY_STR;
            
            String xAmzMetadataDirective =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_METADATA_DIRECTIVE) != null)
                            ? (String) getParameter(messageContext, AmazonS3Utils.XAMZ_METADATA_DIRECTIVE)
                            : AmazonS3Utils.EMPTY_STR;
            
            String xAmzCopySourceIfMatch =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_COPY_SOURCE_IF_MATCH) != null)
                            ? (String) getParameter(messageContext, AmazonS3Utils.XAMZ_COPY_SOURCE_IF_MATCH)
                            : AmazonS3Utils.EMPTY_STR;
            
            String xAmzCopySourceIfNoneMatch =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_COPY_SOURCE_IF_NONE_MATCH) != null)
                            ? (String) getParameter(messageContext, AmazonS3Utils.XAMZ_COPY_SOURCE_IF_NONE_MATCH)
                            : AmazonS3Utils.EMPTY_STR;
            
            String xAmzCopySourceIfUnmodifiedSince =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_COPY_SOURCE_IF_UNMODIFIED_SINCE) != null)
                            ? (String) getParameter(messageContext, AmazonS3Utils.XAMZ_COPY_SOURCE_IF_UNMODIFIED_SINCE)
                            : AmazonS3Utils.EMPTY_STR;
            
            String xAmzCopySourceIfModifiedSince =
                    (getParameter(messageContext, AmazonS3Utils.XAMZ_COPY_SOURCE_IF_MODIFIED_SINCE) != null)
                            ? (String) getParameter(messageContext, AmazonS3Utils.XAMZ_COPY_SOURCE_IF_MODIFIED_SINCE)
                            : AmazonS3Utils.EMPTY_STR;
            
            // AmazonS3 time stamp pattern.
            String format = "EEE, dd MMM yyyy HH:mm:ss ";
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone(AmazonS3Utils.TIME_ZONE));
            String date = dateFormat.format(new Date()) + AmazonS3Utils.TIME_ZONE;
            
            // Create the string to be signed.
            StringBuilder builder = new StringBuilder();
            builder.append(httpMethod).append(AmazonS3Utils.NEW_LINE);
            builder.append(contentMD5).append(AmazonS3Utils.NEW_LINE);
            builder.append(contentType).append(AmazonS3Utils.NEW_LINE);
            
            // Set date value.
            if (xAmzDate) {
                builder.append(AmazonS3Utils.NEW_LINE);
            } else {
                builder.append(date).append(AmazonS3Utils.NEW_LINE);
            }
            
            // Amz header processing.
            Map<String, String> amzHeadersMap = new HashMap<String, String>();
            
            // Common Amz headers.
            if (xAmzDate) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_DATE, date.trim());
            }
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzSecurityToken.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_SECURITY_TOKEN,
                        xAmzSecurityToken.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            // ACL related Amz headers.
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzAcl.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_ACL,
                        xAmzAcl.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzGrantRead.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_GRANT_READ,
                        xAmzGrantRead.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzGrantWrite.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_GRANT_WRITE,
                        xAmzGrantWrite.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzGrantReadAcp.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_GRANT_READ_ACP,
                        xAmzGrantReadAcp.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzGrantWriteAcp.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_GRANT_WRITE_ACP,
                        xAmzGrantWriteAcp.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzGrantFullControl.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_GRANT_FULL_CONTROL,
                        xAmzGrantFullControl.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            // createObject method related Amz headers.
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzMeta.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_META,
                        xAmzMeta.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzServeEncryption.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_SERVE_ENCRYPTION,
                        xAmzServeEncryption.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzStorageClass.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_STORAGE_CLASS,
                        xAmzStorageClass.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzWebsiteLocation.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_WEBSITE_LOCATION,
                        xAmzWebsiteLocation.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzMfa.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_MFA,
                        xAmzMfa.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            // Headers that are requested for createObjectCopy.
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzCopySource.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_COPY_SOURCE,
                        xAmzCopySource.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzMetadataDirective.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_METADATA_DIRECTIVE,
                        xAmzMetadataDirective.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzCopySourceIfMatch.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_COPY_SOURCE_IF_MATCH,
                        xAmzCopySourceIfMatch.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzCopySourceIfNoneMatch.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_COPY_SOURCE_IF_NONE_MATCH,
                        xAmzCopySourceIfNoneMatch.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzCopySourceIfUnmodifiedSince.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_COPY_SOURCE_IF_UNMODIFIED_SINCE,
                        xAmzCopySourceIfUnmodifiedSince.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            if (!AmazonS3Utils.EMPTY_STR.equals(xAmzCopySourceIfModifiedSince.trim())) {
                amzHeadersMap.put(AmazonS3Utils.HD_XAMZ_COPY_SOURCE_IF_MODIFIED_SINCE,
                        xAmzCopySourceIfModifiedSince.replaceAll(AmazonS3Utils.REGEX, AmazonS3Utils.EMPTY_STR));
            }
            
            SortedSet<String> keys = new TreeSet<String>(amzHeadersMap.keySet());
            
            for (String key : keys) {
                String headerValues = amzHeadersMap.get(key);
                builder.append(key.toLowerCase(Locale.US)).append(AmazonS3Utils.COLON).append(headerValues)
                        .append(AmazonS3Utils.NEW_LINE);
            }
            
            // Set the canonicalized resource.
            builder.append(AmazonS3Utils.FORWARD_SLASH).append(bucketName);
            builder.append(uriRemainder);
            
            log.debug("------------------------------------------------");
            log.debug("AmazonS3 Connector Log");
            log.debug("------------------------------------------------");
            // Log message.
            log.debug("String to sign : \n" + builder.toString());
            
            // Sign the created string.
            AmazonS3Authentication amazonS3Authentication = new AmazonS3Authentication(accessKeyId, secretAccessKey);
            
            if (! secretAccessKey.equals(AmazonS3Utils.EMPTY_STR)) {
                
                amazonS3Authentication.setKey();
                String authenticationHeaderValue = amazonS3Authentication.getAuthorizationHeaderValue(builder.toString());
                
                log.debug("------------------------------------------------");
                // Log message.
                log.debug("authenticationHeaderValue : " + authenticationHeaderValue);
                
                if (authenticationHeaderValue != null) {
                    messageContext.setProperty(AmazonS3Utils.AUTH_CODE, authenticationHeaderValue);
                }
                
            }
            
            // Set headers.
            messageContext.setProperty(AmazonS3Utils.CONTENT_MD5_VAL, contentMD5);
            messageContext.setProperty(AmazonS3Utils.CONTENT_TYPE_VAL, contentType);
            
            messageContext.setProperty(AmazonS3Utils.DATE, date);
            if (xAmzDate) {
                messageContext.setProperty(AmazonS3Utils.XAMZ_DATE_VAL, date);
            }
            messageContext.setProperty(AmazonS3Utils.XAMZ_SECURITY_TOKEN, xAmzSecurityToken);
            messageContext.setProperty(AmazonS3Utils.XAMZ_ACL_VAL, xAmzAcl);
            messageContext.setProperty(AmazonS3Utils.XAMZ_GRANT_READ_VAL, xAmzGrantRead);
            messageContext.setProperty(AmazonS3Utils.XAMZ_GRANT_WRITE_VAL, xAmzGrantWrite);
            messageContext.setProperty(AmazonS3Utils.XAMZ_GRANT_READ_ACP_VAL, xAmzGrantReadAcp);
            messageContext.setProperty(AmazonS3Utils.XAMZ_GRANT_WRITE_ACP_VAL, xAmzGrantWriteAcp);
            messageContext.setProperty(AmazonS3Utils.XAMZ_GRANT_FULL_CONTROL_VAL, xAmzGrantFullControl);
            
            // Set headers createObject method.
            messageContext.setProperty(AmazonS3Utils.XAMZ_META_VAL, xAmzMeta);
            messageContext.setProperty(AmazonS3Utils.XAMZ_SERVE_ENCRYPTION_VAL, xAmzServeEncryption);
            messageContext.setProperty(AmazonS3Utils.XAMZ_STORAGE_CLASS_VAL, xAmzStorageClass);
            messageContext.setProperty(AmazonS3Utils.XAMZ_WEBSITE_LOCATION_VAL, xAmzWebsiteLocation);
            messageContext.setProperty(AmazonS3Utils.XAMZ_MFA_VAL, xAmzMfa);
            
            // Headers that are requested for createObjectCopy.
            messageContext.setProperty(AmazonS3Utils.XAMZ_COPY_SOURCE_VAL, xAmzCopySource);
            messageContext.setProperty(AmazonS3Utils.XAMZ_METADATA_DIRECTIVE_VAL, xAmzMetadataDirective);
            messageContext.setProperty(AmazonS3Utils.XAMZ_COPY_SOURCE_IF_MATCH_VAL, xAmzCopySourceIfMatch);
            messageContext.setProperty(AmazonS3Utils.XAMZ_COPY_SOURCE_IF_NONE_MATCH_VAL, xAmzCopySourceIfNoneMatch);
            messageContext.setProperty(AmazonS3Utils.XAMZ_COPY_SOURCE_IF_UNMODIFIED_SINCE_VAL,
                    xAmzCopySourceIfUnmodifiedSince);
            messageContext.setProperty(AmazonS3Utils.XAMZ_COPY_SOURCE_IF_MODIFIED_SINCE_VAL,
                    xAmzCopySourceIfModifiedSince);
            
        } catch (Exception e) {
            // Log message.
            log.error(AmazonS3Utils.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
        
    }
}
