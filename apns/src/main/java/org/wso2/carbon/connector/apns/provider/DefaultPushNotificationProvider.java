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
package org.wso2.carbon.connector.apns.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.connector.apns.AbstractPushNotificationProvider;
import org.wso2.carbon.connector.apns.Certificate;
import org.wso2.carbon.connector.apns.Payload;
import org.wso2.carbon.connector.apns.PushNotification;
import org.wso2.carbon.connector.apns.PushNotificationException;
import org.wso2.carbon.connector.apns.PushNotificationRequest;
import org.wso2.carbon.connector.apns.PushNotificationResponse;
import org.wso2.carbon.connector.apns.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.notnoop.apns.PayloadBuilder;
import com.notnoop.apns.internal.Utilities;
import com.notnoop.exceptions.InvalidSSLConfig;
import com.notnoop.exceptions.NetworkIOException;

/**
 * The default push notification provider. T his provider uses
 * https://github.com/notnoop/java-apns library.
 */
public class DefaultPushNotificationProvider extends
	AbstractPushNotificationProvider {

    private static final Log log = LogFactory
	    .getLog(DefaultPushNotificationProvider.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.wso2.carbon.connector.apns.AbstractPushNotificationProvider#
     * sendToSandboxDestination
     * (org.wso2.carbon.connector.apns.PushNotificationRequest)
     */
    @Override
    protected PushNotificationResponse sendToSandboxDestination(
	    PushNotificationRequest request) throws PushNotificationException {
	ApnsServiceBuilder builder = APNS.newService().withSandboxDestination();
	return doSend(builder, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.wso2.carbon.connector.apns.AbstractPushNotificationProvider#
     * sendToProductionDestination
     * (org.wso2.carbon.connector.apns.PushNotificationRequest)
     */
    @Override
    protected PushNotificationResponse sendToProductionDestination(
	    PushNotificationRequest request) throws PushNotificationException {
	ApnsServiceBuilder builder = APNS.newService()
		.withProductionDestination();
	return doSend(builder, request);
    }

    /**
     * Send the push notification to the push notification service.
     * 
     * @param builder
     *            Apns service build with relevant destination.
     * @param request
     *            Push notification request
     * @return Push notification response
     * @throws PushNotificationException
     *             When there are certificate errors or when APNs gives an
     *             error.
     */
    private PushNotificationResponse doSend(ApnsServiceBuilder builder,
	    PushNotificationRequest request) throws PushNotificationException {

	ApnsService apnsService = null;
	Certificate certificate = request.getCertificate();

	// Build app service with certificate info
	try {
	    apnsService = builder.withCert(certificate.getContent(),
		    certificate.getPassword()).build();
	} catch (InvalidSSLConfig e) {
	    String errorMessage = String.format(
		    "<apns:%s> Cannot decrypt the PCSK12 file in %s",
		    request.getId(), certificate.getName());
	    throw new PushNotificationException(errorMessage,
		    Utils.Errors.ERROR_CODE_INVALID_CERTIFICATE_INFO, e);
	}

	PushNotification pushNotification = request.getPushNotification();

	// Build the payload
	String payload = getPayload(pushNotification);

	if (log.isDebugEnabled()) {
	    log.debug(String.format("<apns:%s>Payload : %s", request.getId(),
		    payload));
	}

	String deviceToken = pushNotification.getSingleDeviceToken();

	ApnsNotification result = null;
	if (deviceToken != null) {

	    if (log.isDebugEnabled()) {
		log.debug(String
			.format("<apns:%s> Sending push notification to device token : <%s>",
				request.getId(), deviceToken));

	    }

	    try {
		result = apnsService.push(deviceToken, payload);
		return buildResponse(result.getDeviceToken(),
			result.getPayload());
	    } catch (NetworkIOException e) {
		throw new PushNotificationException("Cannot connect to APNs",
			Utils.Errors.ERROR_CODE_APNS_IO_FAILURE, e);
	    }

	} else {
	    log.error("No device tokens in the push notification request");
	    return null;
	}

    }

    /**
     * Builds and returns the payload to be sent from the push notification.
     * 
     * @param pushNotification
     *            Push notification to be sent.
     * @return The payload string.
     * @throws PushNotificationException
     *             When the payload is too long.
     */
    private String getPayload(PushNotification pushNotification)
	    throws PushNotificationException {

	PayloadBuilder payloadBuilder = APNS.newPayload();

	if (pushNotification.getAlert() != null) {
	    payloadBuilder.alertBody(pushNotification.getAlert());
	}

	if (pushNotification.getSound() != null) {
	    payloadBuilder.sound(pushNotification.getSound());
	}

	if (pushNotification.getBadge() != Payload.NO_BADGE) {
	    payloadBuilder.badge(pushNotification.getBadge());
	} else {
	    payloadBuilder.clearBadge();
	}

	if (payloadBuilder.isTooLong()) {
	    throw new PushNotificationException(String.format(
		    "Payload is too long. ( %s bytes )",
		    payloadBuilder.length()),
		    Utils.Errors.ERROR_CODE_PAYLOAD_ERROR);
	}

	return payloadBuilder.build();

    }

    /**
     * Build and returns push notification response from the response of APNs
     * 
     * @param deviceToken
     *            Device token which the push notification sent to.
     * @param payload
     *            Dispatched payload to APNs
     * @return Push notification response.
     * @throws PushNotificationException
     *             When the reponse form APNs cannot be parsed.
     */
    private PushNotificationResponse buildResponse(byte[] deviceToken,
	    byte[] payload) throws PushNotificationException {

	PushNotificationResponse response = new PushNotificationResponse();

	response.setDeviceToken(Utilities.encodeHex(deviceToken));

	try {
	    @SuppressWarnings("unchecked")
	    Map<String, Object> root = new ObjectMapper().readValue(payload,
		    HashMap.class);

	    @SuppressWarnings("unchecked")
	    Map<String, Object> aps = (Map<String, Object>) root.get("aps");

	    Object element = aps.get("alert");
	    if (element != null) {
		response.setAlert((String) element);
	    }

	    element = aps.get("badge");
	    if (element != null) {
		response.setBadge((Integer) element);
	    }

	    element = aps.get("sound");
	    if (element != null) {
		response.setSound((String) element);
	    }

	} catch (Exception e) {
	    throw new PushNotificationException(
		    "Cannot parse response from APNs",
		    Utils.Errors.ERROR_CODE_RESPONSE_BUILDING_FAILURE, e);
	}

	return response;
    }
}
