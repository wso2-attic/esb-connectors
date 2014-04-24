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

import java.util.UUID;

/**
 * Represent a push notification request to a push notification provider.
 * Contains a push notification (of {@link PushNotification}) and certificate
 * info (of {@link Certificate})
 */
public class PushNotificationRequest {

    /**
     * Constant to represent production destination of the notification service.
     */
    public static final String PRODUCTION_DESTINATION = "production";

    /**
     * Constant to represent sandbox destination of the notification service.
     */
    public static final String SANDBOX_DESTINATION = "sandbox";

    /**
     * Id of the request.
     */
    private String id;

    /**
     * Push notification to be sent.
     */
    private PushNotification pushNotification;

    /**
     * Certificate for the notification service.
     */
    private Certificate certificate;

    /**
     * Destination of the notification service.
     */
    private String destination;

    /**
     * Constructor with push notification, certificate and destination.
     * 
     * @param pushNotification
     *            Push notification to be sent.
     * @param certificate
     *            Certificate for the notification service.
     * @param destination
     *            Destination of the notification service.
     */
    public PushNotificationRequest(PushNotification pushNotification,
	    Certificate certificate, String destination) {
	this.id = UUID.randomUUID().toString();
	this.pushNotification = pushNotification;
	this.certificate = certificate;
	this.destination = destination;
    }

    /**
     * Returns the id of the request.
     * 
     * @return Id of the request.
     */
    public String getId() {
	return id;
    }

    /**
     * Returns the push notification.
     * 
     * @return Push notification.
     */
    public PushNotification getPushNotification() {
	return pushNotification;
    }

    /**
     * Returns the certificate for the notification service.
     * 
     * @return Certificate.
     */
    public Certificate getCertificate() {
	return certificate;
    }

    /**
     * Returns the destination of the notification service.
     * 
     * @return Destination.
     */
    public String getDestination() {
	return destination;
    }

    /**
     * Checks whether the destination is 'production'
     * 
     * @return <code>true</code> if the destination is 'production', <code>false</code> otherwise.
     */
    public boolean isToProductionDestination() {
	return PRODUCTION_DESTINATION.equals(destination);
    }

    /**
     * Checks whether the destination is 'sandbox'
     * 
     * @return <code>true</code> if the destination is 'sandbox', <code>false</code> otherwise.
     */
    public boolean isToSandboxDestination() {
	return SANDBOX_DESTINATION.equals(destination);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "PushNotificationRequest{" + "id='" + id + '\''
		+ ", pushNotification=" + pushNotification + ", certificate="
		+ certificate + ", destination='" + destination + '\'' + '}';
    }
}
