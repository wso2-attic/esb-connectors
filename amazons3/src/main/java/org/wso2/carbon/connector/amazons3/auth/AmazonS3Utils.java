
package org.wso2.carbon.connector.amazons3.auth;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class for Amazon S3 WSO2 ESB Connector.
 */
public class AmazonS3Utils {
    
    /**
     * Default constructor for AmazonS3Utils class.
     */
    protected AmazonS3Utils() {
    
    }
    
    // General.
    /**
     * Constant for empty string.
     */
    public static final String EMPTY_STR = "";
    
    /**
     * Constant for colon.
     */
    public static final String COLON = ":";
    
    /**
     * Constant for forward slash.
     */
    public static final String FORWARD_SLASH = "/";
    
    /**
     * Constant for new line character.
     */
    public static final char NEW_LINE = '\n';
    
    /**
     * Constant to regular expression for space.
     */
    public static final String REGEX = "\\s+";
    
    /**
     * Constant for bucketName.
     */
    public static final String BUCKET_NAME = "bucketName";
    
    /**
     * Constant for time zone.
     */
    public static final String TIME_ZONE = "GMT";
    
    // For AmazonS3Authentication Class.
    /**
     * Constant for standard mac algorithm name.
     */
    public static final String HMAC_SHA1 = "HmacSHA1";
    
    /**
     * Constant for charset name.
     */
    public static final String UTF8 = "UTF8";
    
    /**
     * Constant for shorten Amazon Web Services prefix.
     */
    public static final String AWS = "AWS ";
    
    // Request Headers.
    /**
     * Constant for accessKeyId.
     */
    public static final String ACCESS_KEY_ID = "accessKeyId";
    
    /**
     * Constant for secretAccessKey.
     */
    public static final String SECRET_ACCESS_KEY = "secretAccessKey";
    
    /**
     * Constant for methodType.
     */
    public static final String METHOD_TYPE = "methodType";
    
    /**
     * Constant for contentMD5.
     */
    public static final String CONTENT_MD5 = "contentMD5";
    
    /**
     * Constant for contentType.
     */
    public static final String CONTENT_TYPE = "contentType";
    
    /**
     * Constant for uriRemainder.
     */
    public static final String URI_REMAINDER = "uriRemainder";
    
    /**
     * Constant for xAmzDate.
     */
    public static final String XAMZ_DATE = "xAmzDate";
    
    /**
     * Constant for xAmzSecurityToken.
     */
    public static final String XAMZ_SECURITY_TOKEN = "xAmzSecurityToken";
    
    /**
     * Constant for xAmzAcl.
     */
    public static final String XAMZ_ACL = "xAmzAcl";
    
    /**
     * Constant for xAmzGrantRead.
     */
    public static final String XAMZ_GRANT_READ = "xAmzGrantRead";
    
    /**
     * Constant for xAmzGrantWrite.
     */
    public static final String XAMZ_GRANT_WRITE = "xAmzGrantWrite";
    
    /**
     * Constant for xAmzGrantReadAcp.
     */
    public static final String XAMZ_GRANT_READ_ACP = "xAmzGrantReadAcp";
    
    /**
     * Constant for xAmzGrantWriteAcp.
     */
    public static final String XAMZ_GRANT_WRITE_ACP = "xAmzGrantWriteAcp";
    
    /**
     * Constant for xAmzGrantFullControl.
     */
    public static final String XAMZ_GRANT_FULL_CONTROL = "xAmzGrantFullControl";
    
    // CreateObject method related Amz headers.
    /**
     * Constant for xAmzMeta.
     */
    public static final String XAMZ_META = "xAmzMeta";
    
    /**
     * Constant for xAmzServeEncryption.
     */
    public static final String XAMZ_SERVE_ENCRYPTION = "xAmzServeEncryption";
    
    /**
     * Constant for xAmzStorageClass.
     */
    public static final String XAMZ_STORAGE_CLASS = "xAmzStorageClass";
    
    /**
     * Constant for xAmzWebsiteLocation.
     */
    public static final String XAMZ_WEBSITE_LOCATION = "xAmzWebsiteLocation";
    
    /**
     * Constant for xAmzMfa.
     */
    public static final String XAMZ_MFA = "xAmzMfa";
    
    // Headers that are requested for createObjectCopy.
    /**
     * Constant for xAmzCopySource.
     */
    public static final String XAMZ_COPY_SOURCE = "xAmzCopySource";
    
    /**
     * Constant for xAmzMetadataDirective.
     */
    public static final String XAMZ_METADATA_DIRECTIVE = "xAmzMetadataDirective";
    
    /**
     * Constant for xAmzCopySourceIfMatch.
     */
    public static final String XAMZ_COPY_SOURCE_IF_MATCH = "xAmzCopySourceIfMatch";
    
    /**
     * Constant for xAmzCopySourceIfNoneMatch.
     */
    public static final String XAMZ_COPY_SOURCE_IF_NONE_MATCH = "xAmzCopySourceIfNoneMatch";
    
    /**
     * Constant for xAmzCopySourceIfModifiedSince.
     */
    public static final String XAMZ_COPY_SOURCE_IF_MODIFIED_SINCE = "xAmzCopySourceIfModifiedSince";
    
    /**
     * Constant for xAmzCopySourceIfUnmodifiedSince.
     */
    public static final String XAMZ_COPY_SOURCE_IF_UNMODIFIED_SINCE = "xAmzCopySourceIfUnmodifiedSince";
    
    // Common Amz headers.
    /**
     * Constant for x-amz-date.
     */
    public static final String HD_XAMZ_DATE = "x-amz-date";
    
    /**
     * Constant for x-amz-security-token.
     */
    public static final String HD_XAMZ_SECURITY_TOKEN = "x-amz-security-token";
    
    // ACL related Amz real headers names.
    /**
     * Constant for x-amz-acl.
     */
    public static final String HD_XAMZ_ACL = "x-amz-acl";
    
    /**
     * Constant for x-amz-grant-read.
     */
    public static final String HD_XAMZ_GRANT_READ = "x-amz-grant-read";
    
    /**
     * Constant for x-amz-grant-write.
     */
    public static final String HD_XAMZ_GRANT_WRITE = "x-amz-grant-write";
    
    /**
     * Constant for x-amz-grant-read-acp.
     */
    public static final String HD_XAMZ_GRANT_READ_ACP = "x-amz-grant-read-acp";
    
    /**
     * Constant for x-amz-grant-write-acp.
     */
    public static final String HD_XAMZ_GRANT_WRITE_ACP = "x-amz-grant-write-acp";
    
    /**
     * Constant for x-amz-grant-full-control.
     */
    public static final String HD_XAMZ_GRANT_FULL_CONTROL = "x-amz-grant-full-control";
    
    // PutObject method related Amz real headers names.
    /**
     * Constant for x-amz-meta-.
     */
    public static final String HD_XAMZ_META = "x-amz-meta-";
    
    /**
     * Constant for x-amz-server-side-encryption.
     */
    public static final String HD_XAMZ_SERVE_ENCRYPTION = "x-amz-server-side-encryption";
    
    /**
     * Constant for x-amz-storage-class.
     */
    public static final String HD_XAMZ_STORAGE_CLASS = "x-amz-storage-class";
    
    /**
     * Constant for x-amz-website-redirect-location.
     */
    public static final String HD_XAMZ_WEBSITE_LOCATION = "x-amz-website-redirect-location";
    
    /**
     * Constant for x-amz-mfa.
     */
    public static final String HD_XAMZ_MFA = "x-amz-mfa";
    
    // Real header names that are requested for createObjectCopy.
    /**
     * Constant for x-amz-copy-source.
     */
    public static final String HD_XAMZ_COPY_SOURCE = "x-amz-copy-source";
    
    /**
     * Constant for x-amz-metadata-directive.
     */
    public static final String HD_XAMZ_METADATA_DIRECTIVE = "x-amz-metadata-directive";
    
    /**
     * Constant for x-amz-copy-source-if-match.
     */
    public static final String HD_XAMZ_COPY_SOURCE_IF_MATCH = "x-amz-copy-source-if-match";
    
    /**
     * Constant for x-amz-copy-source-if-none-match.
     */
    public static final String HD_XAMZ_COPY_SOURCE_IF_NONE_MATCH = "x-amz-copy-source-if-none-match";
    
    /**
     * Constant for x-amz-copy-source-if-modified-since.
     */
    public static final String HD_XAMZ_COPY_SOURCE_IF_MODIFIED_SINCE = "x-amz-copy-source-if-modified-since";
    
    /**
     * Constant for x-amz-copy-source-if-unmodified-since.
     */
    public static final String HD_XAMZ_COPY_SOURCE_IF_UNMODIFIED_SINCE = "x-amz-copy-source-if-unmodified-since";
    
    // Header names to set.
    /**
     * Constant for authenticationCode.
     */
    public static final String AUTH_CODE = "authenticationCode";
    
    /**
     * Constant for contentMD5Value.
     */
    public static final String CONTENT_MD5_VAL = "contentMD5Value";
    
    /**
     * Constant for contentTypeValue.
     */
    public static final String CONTENT_TYPE_VAL = "contentTypeValue";
    
    /**
     * Constant for date.
     */
    public static final String DATE = "date";
    
    /**
     * Constant for xAmzDateValue.
     */
    public static final String XAMZ_DATE_VAL = "xAmzDateValue";
    
    /**
     * Constant for xAmzAclValue.
     */
    public static final String XAMZ_ACL_VAL = "xAmzAclValue";
    
    /**
     * Constant for xAmzGrantReadValue.
     */
    public static final String XAMZ_GRANT_READ_VAL = "xAmzGrantReadValue";
    
    /**
     * Constant for xAmzGrantWriteValue.
     */
    public static final String XAMZ_GRANT_WRITE_VAL = "xAmzGrantWriteValue";
    
    /**
     * Constant for xAmzGrantReadAcpValue.
     */
    public static final String XAMZ_GRANT_READ_ACP_VAL = "xAmzGrantReadAcpValue";
    
    /**
     * Constant for xAmzGrantWriteAcpValue.
     */
    public static final String XAMZ_GRANT_WRITE_ACP_VAL = "xAmzGrantWriteAcpValue";
    
    /**
     * Constant for xAmzGrantFullControlValue.
     */
    public static final String XAMZ_GRANT_FULL_CONTROL_VAL = "xAmzGrantFullControlValue";
    
    // Header names to set of createObject method.
    /**
     * Constant for xAmzMetaValue.
     */
    public static final String XAMZ_META_VAL = "xAmzMetaValue";
    
    /**
     * Constant for xAmzServeEncryptionValue.
     */
    public static final String XAMZ_SERVE_ENCRYPTION_VAL = "xAmzServeEncryptionValue";
    
    /**
     * Constant for xAmzStorageClassValue.
     */
    public static final String XAMZ_STORAGE_CLASS_VAL = "xAmzStorageClassValue";
    
    /**
     * Constant for xAmzWebsiteLocationValue.
     */
    public static final String XAMZ_WEBSITE_LOCATION_VAL = "xAmzWebsiteLocationValue";
    
    /**
     * Constant for xAmzMfaValue.
     */
    public static final String XAMZ_MFA_VAL = "xAmzMfaValue";
    
    // Header names to set of createObjectCopy method.
    /**
     * Constant for xAmzCopySourceValue.
     */
    public static final String XAMZ_COPY_SOURCE_VAL = "xAmzCopySourceValue";
    
    /**
     * Constant for xAmzMetadataDirectiveValue.
     */
    public static final String XAMZ_METADATA_DIRECTIVE_VAL = "xAmzMetadataDirectiveValue";
    
    /**
     * Constant for xAmzCopySourceIfMatchValue.
     */
    public static final String XAMZ_COPY_SOURCE_IF_MATCH_VAL = "xAmzCopySourceIfMatchValue";
    
    /**
     * Constant for xAmzCopySourceIfNoneMatchValue.
     */
    public static final String XAMZ_COPY_SOURCE_IF_NONE_MATCH_VAL = "xAmzCopySourceIfNoneMatchValue";
    
    /**
     * Constant for xAmzCopySourceIfModifiedSinceValue.
     */
    public static final String XAMZ_COPY_SOURCE_IF_MODIFIED_SINCE_VAL = "xAmzCopySourceIfModifiedSinceValue";
    
    /**
     * Constant for xAmzCopySourceIfUnmodifiedSinceValue.
     */
    public static final String XAMZ_COPY_SOURCE_IF_UNMODIFIED_SINCE_VAL = "xAmzCopySourceIfUnmodifiedSinceValue";
    
    /**
     * Return the stack trace for a <strong>Throwable</strong> as a String.
     * 
     * @param e <strong>Throwable</strong>
     * @return <strong>String</strong> The stack trace as String
     */
    public static String getStackTraceAsString(final Throwable e) {
    
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
}
