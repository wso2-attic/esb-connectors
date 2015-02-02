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
package org.wso2.carbon.connector.apns;

import java.io.IOException;
import java.io.InputStream;

import org.apache.synapse.MessageContext;
import org.apache.synapse.registry.Registry;
import org.wso2.carbon.connector.apns.Utils.Errors;
import org.wso2.carbon.connector.apns.Utils.PropertyNames;
import org.wso2.carbon.connector.apns.Utils.PropertyOptions;
import org.wso2.carbon.connector.apns.provider.ProviderRegistry;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.mediation.registry.WSO2Registry;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;

/**
 * Connector which sends a push notification request to Apple Push Notification
 * service (APNs) for the device token in the connector configuration.
 */
public class DispatchToDevice extends AbstractConnector {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse
     * .MessageContext)
     */
    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
	sendPushNotification(messageContext);
    }

    /**
     * Extracts push notification informations in the message context and send
     * the notification to APNs.
     * 
     * @param messageContext
     *            Synapse message context.
     */
    private void sendPushNotification(MessageContext messageContext) {

	PushNotificationRequest pushNotificationRequest = null;

	try {

	    // Extract the push notification request.
	    pushNotificationRequest = getPushNotificationRequest(messageContext);

	    log.info(String.format("<message:%s> Processing APNs request \n%s",
		    messageContext.getMessageID(),
		    pushNotificationRequest.toString()));

	    // Get the notification provider.
	    AbstractPushNotificationProvider pushNotificationProvider = ProviderRegistry
		    .getProvider();

	    if (log.isDebugEnabled()) {
		log.debug(String.format(
			"<apns:%s> Push notification provider : <%s>",
			pushNotificationRequest.getId(),
			pushNotificationProvider.getClass().getName()));

	    }

	    // Send the notification.
	    PushNotificationResponse response = pushNotificationProvider
		    .send(pushNotificationRequest);

	    log.info(String.format("<apns:%s> Push notification sent.",
		    pushNotificationRequest.getId()));

	    // Build and set a SOAP envelope in the message context.
	    try {
		Utils.setResultEnvelope(messageContext, response);
	    } catch (IOException e) {
		String errorMessage = "Error building the response envelope";
		throw new PushNotificationException(errorMessage,
			Utils.Errors.ERROR_CODE_RESPONSE_BUILDING_FAILURE);
	    }

	} catch (PushNotificationException pne) {

	    String errorMessage = String.format(
		    "Error in sending push notification. Error Code : <%s>",
		    pne.getErrorCode());
	    Utils.setError(messageContext, pne);
	    this.handleException(errorMessage, pne, messageContext);
	} catch (Exception e) {

	    PushNotificationException pne = new PushNotificationException(
		    "Unknown error", Errors.ERROR_CODE_UNKNOWN_ERROR, e);

	    String errorMessage = String.format(
		    "Error in sending push notification. Error Code : <%s>",
		    pne.getErrorCode());
	    Utils.setError(messageContext, pne);
	    this.handleException(errorMessage, pne, messageContext);
	}

    }

    /**
     * Extracts the push notification request from the message context.
     * 
     * @param messageContext
     *            Synapse message context.
     * @return Extracted push notification request.
     * @throws PushNotificationException
     *             When mandatory parameters are missing.
     */
    private PushNotificationRequest getPushNotificationRequest(
	    MessageContext messageContext) throws PushNotificationException {

	// Get push notification.
	PushNotification pushNotification = getPushNotification(messageContext);

	// Get environment.
	String environment = Utils.getMandatoryPropertyAsString(messageContext,
		Utils.PropertyNames.DESTINATION);

	// Get certificate.
	Certificate certificate = getCertificate(messageContext);

	// Construct and return the push notification request.
	return new PushNotificationRequest(pushNotification, certificate,
		environment);
    }

    /**
     * Extracts certificate info from the message context.
     * 
     * @param messageContext
     *            Synapse message context.
     * @return Extracted certificate.
     * @throws PushNotificationException
     *             When the certificate is missing.
     */
    private Certificate getCertificate(MessageContext messageContext)
	    throws PushNotificationException {

	// Get the certificate fetch method.
	String certificateFetchMethod = Utils.getMandatoryPropertyAsString(
		messageContext, Utils.PropertyNames.CERTIFICATE_FETCH_METHOD);

	// Get certificate info.
	Certificate certificate = null;

	if (Utils.PropertyOptions.CERTIFICATE_FETCH_METHOD_ATTACHMENT
		.equals(certificateFetchMethod)) {

	    certificate = getCertificateFromAttachment(messageContext);

	    if (log.isDebugEnabled()) {
		log.debug("Fetched certificate from attachment.");
	    }
	} else if (PropertyOptions.CERTIFICATE_FETCH_METHOD_REGISTYR
		.equals(certificateFetchMethod)) {

	    certificate = getCertificateFromRegistry(messageContext);

	    if (log.isDebugEnabled()) {
		log.debug("Fetched certificate from registry.");
	    }
	} else {
	    throw new PushNotificationException(String.format(
		    "Certificate fetch method '%s' is not allowed.",
		    certificateFetchMethod),
		    Errors.ERROR_CODE_ILLEGAL_PARAMETER);
	}

	return certificate;
    }

    /**
     * Extracts and returns the certificate from ESB registry.
     * 
     * @param messageContext
     *            Synapse message context.
     * @return Certificate.
     * @throws PushNotificationException
     *             When the certificate cannot be extracted from the attachment.
     */
    private Certificate getCertificateFromRegistry(MessageContext messageContext)
	    throws PushNotificationException {

	// Get the registry path.
	String certificateRegistryPath = Utils.getMandatoryPropertyAsString(
		messageContext, PropertyNames.CERTIFICATE_REGISTRY_PATH);

	// Get content from the registry resource.
	Registry registry = messageContext.getConfiguration().getRegistry();
	WSO2Registry wso2Registry = (WSO2Registry) registry;
	Resource resource = wso2Registry.getResource(certificateRegistryPath);

	if (resource == null) {
	    throw new PushNotificationException(String.format(
		    "No certificate in registry path %s",
		    certificateRegistryPath),
		    Errors.ERROR_CODE_INVALID_CERTIFICATE_INFO);
	}

	InputStream content = null;
	try {
	    content = resource.getContentStream();
	} catch (RegistryException e) {
	    throw new PushNotificationException(String.format(
		    "Certificate content in registry, <%s> cannot be read",
		    certificateRegistryPath),
		    Errors.ERROR_CODE_INVALID_CERTIFICATE_INFO);
	}

	// Get the password.
	String password = Utils.getMandatoryPropertyAsString(messageContext,
		Utils.PropertyNames.PASSWORD);

	return new Certificate(certificateRegistryPath, content, password);
    }

    /**
     * Extracts and returns the certificate from SOAP attachment.
     * 
     * @param messageContext
     *            Synapse message context.
     * @return Certificate.
     * @throws PushNotificationException
     *             When the certificate cannot be extracted from the attachment.
     */
    private Certificate getCertificateFromAttachment(
	    MessageContext messageContext) throws PushNotificationException {

	// Get certificate info.
	String certificateName = Utils
		.getMandatoryPropertyAsString(messageContext,
			Utils.PropertyNames.CERTIFICATE_ATTACHMENT_NAME);

	// Extract certificate content.
	InputStream content = Utils.extractAttachment(messageContext,
		certificateName);

	if (content == null) {
	    String errorMessage = String.format(
		    "Cannot extract certificate attachment for the name %s",
		    certificateName);
	    throw new PushNotificationException(errorMessage,
		    Utils.Errors.ERROR_CODE_INVALID_CERTIFICATE_INFO);
	}

	// Get the password.
	String password = Utils.getMandatoryPropertyAsString(messageContext,
		Utils.PropertyNames.PASSWORD);

	return new Certificate(certificateName, content, password);
    }

    /**
     * Extracts the push notification from the message context.
     * 
     * @param messageContext
     *            Synapse messahe context.
     * @return Extracted push notification.
     * @throws PushNotificationException
     *             When device token is missing or empty.
     */
    private PushNotification getPushNotification(MessageContext messageContext)
	    throws PushNotificationException {

	PushNotification pushNotification = new PushNotification();

	// Set payload.
	String alert = Utils.getOptionalPropertyAsString(messageContext,
		Utils.PropertyNames.ALERT);
	pushNotification.setAlert(alert);

	String sound = Utils.getOptionalPropertyAsString(messageContext,
		Utils.PropertyNames.SOUND);
	pushNotification.setSound(sound);

	String badge = Utils.getOptionalPropertyAsString(messageContext,
		Utils.PropertyNames.BADGE);
	pushNotification.setBadge(badge);

	// Set device token.
	String deviceToken = Utils.getMandatoryPropertyAsString(messageContext,
		Utils.PropertyNames.DEVICE_TOKEN);
	if (!deviceToken.trim().isEmpty()) {
	    pushNotification.addDeviceToken(deviceToken);
	} else {
	    throw new PushNotificationException("Device token is empty",
		    Utils.Errors.ERROR_CODE_ILLEGAL_PARAMETER);
	}

	return pushNotification;
    }

}
