/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.googledrive;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.codec.binary.Base64;
import org.apache.synapse.MessageContext;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

/**
 * Utility class for Google Drive WSO2 ESB Connector.
 * 
 * 
 */
public class GoogleDriveUtils {

	/**
	 * constructor for class mediator.
	 */
	protected GoogleDriveUtils() {

	}

	/**
	 * Returns body for response SOAP envelope.
	 * 
	 * @param namespace
	 *            String value for namespace.
	 * @param resultTagName
	 *            String tag for result.
	 * @param success
	 *            boolean whether the call was successful.
	 * @param elements
	 *            HashMap(String,String) value.
	 * @return resultTag return result.
	 */
	public static OMElement buildResultEnvelope(final String namespace,
			final String resultTagName, final boolean success,
			final Map<String, String> elements) {

		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace ns = factory.createOMNamespace(namespace, "ns");
		OMElement resultTag = factory.createOMElement(resultTagName, ns);
		OMElement result = factory.createOMElement("result", ns);
		result.setText(String.valueOf(success));
		resultTag.addChild(result);

		if (elements != null) {
			Iterator<Map.Entry<String, String>> elementIterator = elements
					.entrySet().iterator();
			while (elementIterator.hasNext()) {
				Map.Entry<String, String> element = (Map.Entry<String, String>) elementIterator
						.next();
				OMElement tag = factory.createOMElement(element.getKey(), ns);
				tag.setText(element.getValue());
				resultTag.addChild(tag);
			}
		}

		return resultTag;
	}

	/**
	 * Creates a credentials object.
	 * 
	 * @param httpTransport
	 *            NetHttpTransport
	 * @param messageContext
	 *            messageContext
	 * @param jsonFactory
	 *            JsonFactory
	 * @return <strong>GoogleCredential</strong> Credentials object
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws GeneralSecurityException
	 *             if a GeneralSecurity error occurs
	 */
	public static Drive getDriveService(final MessageContext messageContext,
			final HttpTransport httpTransport, final JsonFactory jsonFactory)
			throws IOException, GeneralSecurityException {

		boolean useServiceAccount = Boolean
				.parseBoolean((String) messageContext
						.getProperty(GoogleDriveUtils.StringConstants.USE_SERVICE_ACCOUNT));
		GoogleDriveAuth auth = null;
		if (useServiceAccount) {
			auth = new GoogleDriveUtils.GoogleDriveAuth(
					(String) messageContext
							.getProperty(GoogleDriveUtils.StringConstants.SERVICE_ACCOUNT_EMAIL),
					(String) messageContext
							.getProperty(GoogleDriveUtils.StringConstants.SERVICE_ACCOUNT_PKCS_CONTENT))
					.setAuthType(GoogleDriveUtils.GoogleDriveAuth.AUTH_TYPE_SERVICE_ACCOUNT);

		} else {
			auth = new GoogleDriveUtils.GoogleDriveAuth(
					(String) messageContext
							.getProperty(GoogleDriveUtils.StringConstants.CLIENT_ID),
					(String) messageContext
							.getProperty(GoogleDriveUtils.StringConstants.CLIENT_SECRET),
					(String) messageContext
							.getProperty(GoogleDriveUtils.StringConstants.ACCESS_TOKEN),
					(String) messageContext
							.getProperty(GoogleDriveUtils.StringConstants.REFRESH_TOKEN))
					.setAuthType(GoogleDriveUtils.GoogleDriveAuth.AUTH_TYPE_CLIENT_ACCOUNT);
		}
		GoogleCredential credential = null;
		switch (auth.getAuthType()) {
		case GoogleDriveAuth.AUTH_TYPE_SERVICE_ACCOUNT:
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				
				StringBuffer sb = new StringBuffer();
				byte[] md5Bytes = messageDigest.digest(auth
						.getServicePKCS12FileContent().getBytes());
				for (byte b : md5Bytes) {
					sb.append(Integer.toHexString((int) (b & 0xff)));
				}
				
				credential = new GoogleCredential.Builder()
						.setTransport(httpTransport)
						.setJsonFactory(jsonFactory)
						.setServiceAccountId(auth.getServiceAccountEmail())
						.setServiceAccountScopes(
								Collections.singleton(DriveScopes.DRIVE))
						.setServiceAccountPrivateKeyFromP12File(
								writeToTempFile(sb.toString()+".p12",
										auth.getServicePKCS12FileContent()))
						.build();
			} catch (Exception e) {

				e.printStackTrace();
			}
			break;
		case GoogleDriveAuth.AUTH_TYPE_CLIENT_ACCOUNT:
			credential = new GoogleCredential.Builder()
					.setTransport(httpTransport)
					.setJsonFactory(jsonFactory)
					.setClientSecrets(auth.getClientId(),
							auth.getClientSecret()).build()
					.setAccessToken(auth.getAccessToken())
					.setRefreshToken(auth.getRefreshToken());
			break;
		default:
			credential = null;
		}
		return new Drive.Builder(httpTransport, jsonFactory, null)
				.setHttpRequestInitializer(credential).build();
	}

	/**
	 * Return the stack trace for a <strong>Throwable</strong> as a String.
	 * 
	 * @param e
	 *            <strong>Throwable</strong>
	 * @return <strong>String</strong> The stack trace as String
	 */
	public static String getStackTraceAsString(final Throwable e) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	/**
	 * Writes Base64-encoded content to a temporary file.
	 * 
	 * @param filename
	 *            Name of the file to be stored
	 * @param base64FileContent
	 *            Base64-encoded file content
	 * @return <strong>java.io.File</strong> resource
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 *             throw if an error occur
	 */
	public static java.io.File writeToTempFile(final String filename,
			final String base64FileContent) throws Exception {
		FileOutputStream fout = null;
		byte[] contentBytes = Base64.decodeBase64(base64FileContent);
		java.io.File writtenFileFolder = new java.io.File("tmpfiles");
		java.io.File writtenFile = new java.io.File(
				writtenFileFolder.getAbsolutePath()
						+ System.getProperty("file.separator") + filename);

		System.out.println("Wrote Temporary File: "
				+ writtenFile.getAbsolutePath());
		try {

			if (!writtenFile.exists()) {
				Boolean checkResult = writtenFileFolder.mkdirs();
				if (checkResult) {
					System.out
							.println("Folder "
									+ writtenFileFolder.getAbsolutePath()
									+ " created.");
				}
			}
			fout = new FileOutputStream(writtenFile);
			fout.write(contentBytes);
			fout.flush();
			fout.close();
		} catch (IOException e) {

			throw new IOException(e);
		} catch (Exception e) {
			throw new Exception(e);
		}

		return writtenFile;
	}

	/**
	 * Class to store and manage authentication credentials.
	 * 
	 * 
	 */
	public static class GoogleDriveAuth {

		/** Represent the authTypeServiceAccount . */
		public static final int AUTH_TYPE_SERVICE_ACCOUNT = 12903;

		/** Represent the authTypeClientAccount . */
		public static final int AUTH_TYPE_CLIENT_ACCOUNT = 5435;

		/** Represent the authType. */
		private int authType;

		/** Represent the serviceAccountEmail . */
		private String serviceAccountEmail;

		/** Represent the servicePKCS12FileContent . */
		private String servicePKCS12FileContent;

		/** Represent the clientId . */
		private String clientId;

		/** Represent the clientSecret . */
		private String clientSecret;

		/** Represent the accessToken . */
		private String accessToken;

		/** Represent the refreshToken . */
		private String refreshToken;

		/**
		 * Constructor to build object for service account authentication.
		 * 
		 * @param serviceAccountsEmail
		 *            serviceAccountEmail value
		 * @param serviceFileContent
		 *            serviceFileContent value
		 */
		public GoogleDriveAuth(final String serviceAccountsEmail,
				final String serviceFileContent) {

			this.serviceAccountEmail = serviceAccountsEmail;
			this.servicePKCS12FileContent = serviceFileContent;
		}

		/**
		 * Constructor to build object for client account authentication.
		 * 
		 * @param clientsId
		 *            clientId value
		 * @param refreshTokenId
		 *            refreshToken value
		 * @param clientsSecret
		 *            clientSecret value
		 * @param accesstokenId
		 *            accessToken value
		 */
		public GoogleDriveAuth(final String clientsId,
				final String clientsSecret, final String accesstokenId,
				final String refreshTokenId) {

			this.clientId = clientsId;
			this.clientSecret = clientsSecret;
			this.accessToken = accesstokenId;
			this.refreshToken = refreshTokenId;
		}

		/**
		 * @return toStrigValue
		 */
		@Override
		public final String toString() {

			return "Service email: " + this.getServiceAccountEmail()
					+ " PKCS12 Filepath: " + this.getServicePKCS12FileContent();
		}

		/**
		 * @return the auth_type
		 */
		public final int getAuthType() {

			return authType;
		}

		/**
		 * @param authsType
		 *            the auth_type to set
		 * @return authType return value
		 */
		public final GoogleDriveAuth setAuthType(final int authsType) {

			this.authType = authsType;
			return this;
		}

		/**
		 * @return the serviceAccountEmail
		 */
		public final String getServiceAccountEmail() {

			return serviceAccountEmail;
		}

		/**
		 * @param servicesAccountName
		 *            the service Account Name to set
		 */
		public final void setServiceAccountEmail(
				final String servicesAccountName) {

			this.serviceAccountEmail = servicesAccountName;
		}

		/**
		 * @return the servicePKCS12FileContent
		 */
		public final String getServicePKCS12FileContent() {

			return servicePKCS12FileContent;
		}

		/**
		 * @param servicePKCS12fileContent
		 *            the servicePKCS12FileContent to set
		 */
		public final void setServicePKCS12FileContent(
				final String servicePKCS12fileContent) {

			this.servicePKCS12FileContent = servicePKCS12fileContent;
		}

		/**
		 * @return the clientId
		 */
		public final String getClientId() {

			return clientId;
		}

		/**
		 * @param clientsId
		 *            the client_id to set
		 */
		public final void setClientId(final String clientsId) {

			this.clientId = clientsId;
		}

		/**
		 * @return the clientsId
		 */
		public final String getClientSecret() {

			return clientSecret;
		}

		/**
		 * @param clientsSecret
		 *            the client_secret to set
		 */
		public final void setClientSecret(final String clientsSecret) {

			this.clientSecret = clientsSecret;
		}

		/**
		 * @return the accessToken
		 */
		public final String getAccessToken() {

			return accessToken;
		}

		/**
		 * @param accessTokenId
		 *            the access_token to set
		 */
		public final void setAccessToken(final String accessTokenId) {

			this.accessToken = accessTokenId;
		}

		/**
		 * @return the refreshToken
		 */
		public final String getRefreshToken() {

			return refreshToken;
		}

		/**
		 * @param refreshTokenId
		 *            the refreshToken to set
		 */
		public final void setRefreshToken(final String refreshTokenId) {

			this.refreshToken = refreshTokenId;
		}

	}

	/**
	 * Contains Constants for Google Drive Connector strings.
	 */
	public static final class StringConstants {

		/**
		 * messageContext variable names.
		 */
		// Specific to authentication
		/** Represent the useServiceAccount . */
		public static final String USE_SERVICE_ACCOUNT = "useServiceAccount";

		/** Represent the serviceAccountEmail . */
		public static final String SERVICE_ACCOUNT_EMAIL = "serviceAccountEmail";

		/** Represent the serviceAccountPK . */
		public static final String SERVICE_ACCOUNT_PKCS_CONTENT = "serviceAccountPKCSContent";

		/** Represent the clientId . */
		public static final String CLIENT_ID = "clientId";

		/** Represent the clientSecret . */
		public static final String CLIENT_SECRET = "clientSecret";

		/** Represent the accessToken . */
		public static final String ACCESS_TOKEN = "accessToken";

		/** Represent the refreshToken . */
		public static final String REFRESH_TOKEN = "refreshToken";

		/** Represent the fileId . */
		public static final String FILE_ID = "fileId";

		/** Represent the file . */
		public static final String FILE = "file";

		/** Represent the filerequestBody . */
		public static final String REQUEST_BODY = "requestBody";

		/** Represent the error . */
		public static final String ERROR = "error";

		/** Represent the maxResult . */
		public static final String MAX_RESULTS = "maxResults";

		/** Represent the pageToken . */
		public static final String PAGE_TOKEN = "pageToken";

		/** Represent the q . */
		public static final String Q = "q";

		/** Represent the convert. */
		public static final String CONVERT = "convert";

		/** Represent the newRevision. */
		public static final String NEW_REVISION = "newRevision";

		/** Represent the OCR. */
		public static final String OCR = "ocr";

		/** Represent the OCRLanguage. */
		public static final String OCR_LANGUAGE = "ocrLanguage";

		/** Represent the pinned. */
		public static final String PINNED = "pinned";

		/** Represent the setModifiedDate. */
		public static final String SET_MODIFIED_DATE = "setModifiedDate";

		/** Represent the timetextLanguage. */
		public static final String TIMED_TEXT_LANGUAGE = "timedTextLanguage";

		/** Represent the timeTextTrackName. */
		public static final String TIMED_TEXT_TRACKNAME = "timedTextTrackName";

		/** Represent the UpdateViewDate. */
		public static final String UPDATE_VIEWED_DATE = "updateViewedDate";

		/** Represent the useContentAsIndexableText. */
		public static final String USE_CONTENT_AS_INDEXABLE_TEXT = "useContentAsIndexableText";

		/** Represent the uploadType. */
		public static final String UPLOAD_TYPE = "uploadType";

		/** Represent the mimeType. */
		public static final String MIME_TYPE = "mimeType";

		/** Represent thefileContent. */
		public static final String FILE_CONTENT = "fileContent";

		/** Represent patchFileResult. */
		public static final String PATCH_FILE_RESULT = "patchFileResult";

		/** Represent updateFileResult. */
		public static final String UPDATE_FILE_RESULT = "updateFileResult";

		/** Represent updateViewDate. */
		public static final String UPDATE_VIEW_DATE = "updateViewedDate";

		/** Represent channelId. */
		public static final String CHANNEL_ID = "channelId";

		/** Represent channelAddress. */
		public static final String CHANNEL_ADDRESS = "channelAddress";

		/** Represent channelType. */
		public static final String CHANNEL_TYPE = "channelType";

		/** Represent getFileResult. */
		public static final String GET_FILE_RESULT = "getFileResult";

		/** Represent getwatchFileResult. */
		public static final String WATCH_FILE_RESULT = "watchFileResult";

		/** Represent deleteFileResult. */
		public static final String DELETE_FILE_RESULT = "deleteFileResult";

		/** Represent listFileResult. */
		public static final String LIST_FILE_RESULT = "listFileResult";

		/** Represent touchFileResult. */
		public static final String TOUCH_FILE_RESULT = "touchFileResult";

		/** Represent copiedFileResult. */
		public static final String COPIED_FILE_RESULT = "copiedFileResult";

		/** Represent insertFileResult. */
		public static final String INSERTED_FILE_RESULT = "insertedFileResult";

		/** Represent title. */
		public static final String TITLE = "title";

		/** Represent parentId. */
		public static final String PARENT_ID = "parentId";

		/** Represent fileName. */
		public static final String FILE_NAME = "filename";

		/** Represent description. */
		public static final String DESCRIPTION = "description";

		/** Represent visibility. */
		public static final String VISIBILITY = "visibility";

		/** Represent tempFolderName. */
		public static final String TEMP_FOLDER_NAME = "tempFolderName";

		/** Represent tempFileName. */
		public static final String TEMP_FILE_NAME = "tempFileName";

		/** Represent urn for getFile. */
		public static final String URN_GOOGLEDRIVE_GETFILE = "urn:wso2.connector.googledrive.getfile";

		/** Represent urn for getwatchFile. */
		public static final String URN_GOOGLEDRIVE_WATCHFILE = "urn:wso2.connector.googledrive.watchfile";

		/** Represent urn for updateFile. */
		public static final String URN_GOOGLEDRIVE_UPDATEFILE = "urn:wso2.connector.googledrive.updatefile";

		/** Represent urn for patchFile. */
		public static final String URN_GOOGLEDRIVE_PATCHFILE = "urn:wso2.connector.googledrive.patchfile";

		/** Represent urn for insertFile. */
		public static final String URN_GOOGLEDRIVE_INSERTFILE = "urn:wso2.connector.googledrive.insertfile";

		/** Represent urn for copyFile. */
		public static final String URN_GOOGLEDRIVE_COPYFILE = "urn:wso2.connector.googledrive.copyfile";

		/** Represent urn for trashFile. */
		public static final String URN_GOOGLEDRIVE_TRASHFILE = "urn:wso2.connector.googledrive.trashfile";

		/** Represent urn for unTrashFile. */
		public static final String URN_GOOGLEDRIVE_UNTRASHFILE = "urn:wso2.connector.googledrive.untrashfile";

		/** Represent urn for deleteFile. */
		public static final String URN_GOOGLEDRIVE_DELETEFILE = "urn:wso2.connector.googledrive.deletefile";

		/** Represent urn for listFile. */
		public static final String URN_GOOGLEDRIVE_LISTFILE = "urn:wso2.connector.googledrive.listfiles";

		/** Represent urn for touchFile. */
		public static final String URN_GOOGLEDRIVE_TOUCHFILE = "urn:wso2.connector.googledrive.touchfile";

		/** Represent comment. */
		public static final String COMMENT = "comment";

		/** Represent commentId. */
		public static final String COMMENT_ID = "commentId";

		/** Represent content. */
		public static final String COMMENT_CONTENT = "content";

		/** Represent includeDeleted. */
		public static final String COMMENT_INCLUDE_DELETED = "includeDeleted";

		/** Represent insertCommentResult. */
		public static final String INSERT_COMMENT_RESULT = "insertCommentResult";

		/** Represent trashedFileResult. */
		public static final String TRASHED_FILE_RESULT = "trashedFileResult";

		/** Represent untrashedFileResult. */
		public static final String UNTRASHED_FILE_RESULT = "untrashedFileResult";

		/** Represent getCommentResult. */
		public static final String GET_COMMENT_RESULT = "getCommentResult";

		/** Represent deleteCommentResult. */
		public static final String DELETE_COMMENT_RESULT = "deleteCommentResult";

		/** Represent urn for insertComment. */
		public static final String URN_GOOGLEDRIVE_INSERTCOMMENT = "urn:wso2.connector.googledrive.insertcomment";

		/** Represent urn for getCommentById. */
		public static final String URN_GOOGLEDRIVE_GETCOMMENTBYID = "urn:wso2.connector.googledrive.getcommentbyid";

		/** Represent urn for deleteComment. */
		public static final String URN_GOOGLEDRIVE_DELETECOMMENT = "urn:wso2.connector.googledrive.deletecomment";

		/** Represent permission. */
		public static final String PERMISSION = "permission";

		/** Represent emailMessage. */
		public static final String EMAIL_MESSAGE = "emailMessage";

		/** Represent sendNotificationEmails. */
		public static final String SEND_NOTIFICATION_EMAILS = "sendNotificationEmails";

		/** Represent role. */
		public static final String ROLE = "role";

		/** Represent type. */
		public static final String TYPE = "type";

		/** Represent value. */
		public static final String VALUE = "value";

		/** Represent insertPermissionResult. */
		public static final String INSERT_PERMISSION_RESULT = "insertPermissionResult";

		/** Represent getPermissionsResult. */
		public static final String GET_PERMISSIONS_RESULT = "getPermissionsResult";

		/** Represent urn for InsertPermission. */
		public static final String URN_GOOGLEDRIVE_INSERTPERMISSION = "urn:wso2.connector.googledrive.insertpermission";

		/** Represent urn for listFilePermission. */
		public static final String URN_GOOGLEDRIVE_GETPERMISSIONLIST = "urn:wso2.connector.googledrive.listfilepermissions";

		/** Represent childReference. */
		public static final String CHILD_REFERENCE = "childReference";

		/** Represent folderId. */
		public static final String FOLDER_ID = "folderId";

		/** Represent getCommentResult. */
		public static final String CHILD_ID = "childId";

		/** Represent getCommentResult. */
		public static final String GET_CHILD_RESULT = "getChildResult";

		/** Represent insertFileToFolderResult. */
		public static final String INSERT_FILE_TO_FOLDER_RESULT = "insertFileToFolderResult";

		/** Represent listFoldersResult. */
		public static final String LIST_FOLDERS_RESULT = "listFoldersResult";

		/** Represent urn for InsertFileToFolder. */
		public static final String URN_GOOGLEDRIVE_INSERTFILETOFOLDER = "urn:wso2.connector.googledrive.insertfiletofolder";

		/** Represent urn for getChild. */
		public static final String URN_GOOGLEDRIVE_GETCHILD = "urn:wso2.connector.googledrive.getchild";

		/** Represent urn for listfolders. */
		public static final String URN_GOOGLEDRIVE_LISTFOLDERS = "urn:wso2.connector.googledrive.listfolders";

		/** Represent includeDeleted. */
		public static final String INCLUDE_DELETED = "includeDeleted";

		/** Represent includeSubscribed. */
		public static final String INCLUDE_SUBSCRIBED = "includeSubscribed";

		/** Represent startChangeId. */
		public static final String START_CHANGE_ID = "startChangeId";

		/** Represent change_list. */
		public static final String CHANGE_LIST = "change_list";

		/** Represent listChangesForUserResult. */
		public static final String LIST_CHANGES_FOR_USER_RESULT = "listChangesForUserResult";

		/** Represent urn for listchangesforuser. */
		public static final String URN_GOOGLEDRIVE_LISTCHANGESFORUSER = "urn:wso2.connector.googledrive.listchangesforuser";

	}

}
