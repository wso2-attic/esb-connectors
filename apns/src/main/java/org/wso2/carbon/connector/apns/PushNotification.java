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

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a push notification. A push notification contains a payload and a
 * set of device tokens.
 */
public class PushNotification {

    /**
     * Payload of the push notification.
     */
    private Payload payload;

    /**
     * Set of the device tokens which the payload should be sent to. NOTE : Only
     * one device token is considered as of now.
     */
    private Set<String> deviceTokens;

    /**
     * Default constructor.
     */
    public PushNotification() {
	payload = new Payload();
	deviceTokens = new HashSet<String>();
    }

    /**
     * Adds the given device token to the list.
     * 
     * @param deviceToken
     *            Device token to be added.
     */
    public void addDeviceToken(String deviceToken) {
	deviceTokens.add(deviceToken);
    }

    /**
     * Sets the alert message to the payload.
     * 
     * @param alert
     *            Alert message of the payload.
     */
    public void setAlert(String alert) {
	payload.setAlert(alert);
    }

    /**
     * Sets the badge value (of {@link String} ) to the payload.
     * 
     * @param badge
     *            Badge value of the payload.
     */
    public void setBadge(String badge) {
	payload.setBadge(badge);
    }

    /**
     * Sets the badge value to the payload.
     * 
     * @param badge
     *            Badge value of the payload.
     */
    public void setBadge(int badge) {
	payload.setBadge(badge);
    }

    /**
     * Sets the sound clip name to the payload.
     * 
     * @param sound
     *            Sound clip name of the payload.
     */
    public void setSound(String sound) {
	payload.setSound(sound);
    }

    /**
     * Returns the alert message of the payload.
     * 
     * @return Alert message of the payload.
     */
    public String getAlert() {
	return payload.getAlert();
    }

    /**
     * Returns the sound clip name of the payload.
     * 
     * @return Sound clip name of the payload.
     */
    public String getSound() {
	return payload.getSound();
    }

    /**
     * Returns the badge value of the payload.
     * 
     * @return Badge value of the payload.
     */
    public int getBadge() {
	return payload.getBadge();
    }

    /**
     * Returns device tokens.
     * 
     * @return Device tokens
     */
    public Set<String> getDeviceTokens() {
	return deviceTokens;
    }

    /**
     * Returns a random device token from the device tokens.
     * 
     * @return A random device token if there are more then zero device tokens,
     *         null otherwise.
     */
    public String getSingleDeviceToken() {

	if (!deviceTokens.isEmpty()) {
	    return (String) deviceTokens.toArray()[0];
	} else {
	    return null;
	}

    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "PushNotification{" + "payload=" + payload + ", deviceTokens="
		+ deviceTokens + '}';
    }
}
