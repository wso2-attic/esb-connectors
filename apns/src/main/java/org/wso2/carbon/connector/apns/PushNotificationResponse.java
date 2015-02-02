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

/**
 * Represents the response of a push notification dispatch.
 * 
 */
public class PushNotificationResponse {

    /**
     * Device token which the notification sent to.
     */
    private String deviceToken;
    
    /**
     * Alert message of the dispatched payload.
     */
    private String alert;
    
    /**
     * Badge value of the dispatched payload.
     */
    private int badge;
    
    /**
     * Sound clip name of the dispatched payload.
     */
    private String sound;

    /**
     * Returns the device token which the notification sent to.
     * 
     * @return Device token.
     */
    public String getDeviceToken() {
	return deviceToken;
    }

    /**
     * Sets the device token which the notification sent to.
     * 
     * @param deviceToken
     */
    public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
    }

    /**
     * Returns the alert message of the dispatched payload.
     * 
     * @return Alert message.
     */
    public String getAlert() {
	return alert;
    }

    /**
     * Sets the alert message of the dispatched payload.
     * 
     * @param alert Alert message.
     */
    public void setAlert(String alert) {
	this.alert = alert;
    }

    /**
     * Returns the badge value of the dispatched payload.
     * 
     * @return Badge value.
     */
    public int getBadge() {
	return badge;
    }

    /**
     * Sets the badge value of the dispatched payload.
     * 
     * @param badge Badge value.
     */
    public void setBadge(int badge) {
	this.badge = badge;
    }

    /**
     * Returns the sound clip name of the dispatchehd payload.
     * 
     * @return Sound clip name.
     */
    public String getSound() {
	return sound;
    }

    /**
     * Sets the sound clip name of the dispatched payload.
     * 
     * @param sound Sound clip name.
     */
    public void setSound(String sound) {
	this.sound = sound;
    }
}
