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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents the payload of a push notification.
 */
public class Payload {

    /**
     * Constant to represent badge value 0.
     */
    public static final int NO_BADGE = 0;

    private static final Log log = LogFactory.getLog(Payload.class);

    /**
     * Alert message.
     */
    private String alert;

    /**
     * Name of the sound clip.
     */
    private String sound;

    /**
     * Value of the badge.
     */
    private int badge;

    /**
     * Default constructor.
     */
    public Payload() {

    }

    /**
     * Constructor with alert, sound and badge.
     * 
     * @param alert
     *            Alert message.
     * @param sound
     *            Name of the sound clip.
     * @param badge
     *            Value of the badge.
     */
    public Payload(String alert, String sound, int badge) {
	this.alert = alert;
	this.sound = sound;
	this.badge = badge;
    }

    /**
     * Returns alert message.
     * 
     * @return Alert message.
     */
    public String getAlert() {
	return alert;
    }

    /**
     * Sets alert message if it is not empty.
     * 
     * @param alert
     *            Alert message.
     */
    public void setAlert(String alert) {
	if (alert.isEmpty()) {
	    log.debug("Alert is empty. Setting to null");
	    return;
	}
	this.alert = alert;
    }

    /**
     * Returns sound clip name.
     * 
     * @return Sound clip name.
     */
    public String getSound() {
	return sound;
    }

    /**
     * Sets the name of the sound clip
     * 
     * @param sound
     *            Name of the sound clip.
     */
    public void setSound(String sound) {
	if (sound.isEmpty()) {
	    log.debug("Sound is empty. Setting to null");
	    return;
	}
	this.sound = sound;
    }

    /**
     * Return the value of the badge.
     * 
     * @return Value of the badge.
     */
    public int getBadge() {
	return badge;
    }

    /**
     * Sets the value of the badge.
     * 
     * @param badge
     *            Value of the badge.
     */
    public void setBadge(int badge) {
	this.badge = badge;
    }

    /**
     * Sets the value of the badge after parsing. If there are parsing errors
     * then the value is set to 0.
     * 
     * @param badge Value of the badge.
     */
    public void setBadge(String badge) {

	if (badge != null) {

	    try {
		int badgeValue = Integer.parseInt(badge);
		this.badge = badgeValue;
	    } catch (NumberFormatException e) {
		log.warn("Badge value is not a integer. Setting to default value (0)");
		this.badge = NO_BADGE;
	    }

	} else {
	    log.warn("Badge value is not a integer. Setting to default value (0)");
	    this.badge = NO_BADGE;
	}

    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Payload{" + "alert='" + alert + '\'' + ", sound='" + sound
		+ '\'' + ", badge='" + badge + '\'' + '}';
    }
}
