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
 * Abstract class for push notification provider implementations. This class is
 * responsible for calling the relevant dispatch method based on the destination
 * of the push notification request.
 */
public abstract class AbstractPushNotificationProvider {

    private static final Log log = LogFactory
	    .getLog(AbstractPushNotificationProvider.class);

    /**
     * Sends the given push notification request to the given destination.
     * 
     * @param request
     *            Push notification request to be sent.
     * @throws PushNotificationException
     *             When the notification cannot be delivered.
     */
    public final PushNotificationResponse send(PushNotificationRequest request)
	    throws PushNotificationException {

	if (request.isToProductionDestination()) {

	    if (log.isDebugEnabled()) {
		log.debug(String
			.format("<apsn:%s> Sending the push notification to production destination.",
				request.getId()));
	    }
	    return sendToProductionDestination(request);

	} else if (request.isToSandboxDestination()) {

	    if (log.isDebugEnabled()) {
		log.debug(String
			.format("<apns:%s> Sending the push notification to sandbox destination.",
				request.getId()));
	    }
	    return sendToSandboxDestination(request);

	} else {
	    String errorMessage = String.format(
		    "<apns:%s> Destination '%s' is not valid", request.getId(),
		    request.getDestination());
	    throw new PushNotificationException(errorMessage,
		    Utils.Errors.ERROR_CODE_INVALID_APNS_DESTINATION);
	}

    }

    /**
     * Abstract method to be implemented by the providers to send the
     * notification to the 'sandbox' destination.
     * 
     * @param request
     *            Request to be sent to the 'sandbox' destination.
     * @throws PushNotificationException
     *             When the notification cannot be delivered to the 'sandbox'
     *             destination.
     */
    protected abstract PushNotificationResponse sendToSandboxDestination(
	    PushNotificationRequest request) throws PushNotificationException;

    /**
     * Abstract method to be implemented by the providers to send the
     * notification to the 'production' destination.
     * 
     * @param request
     *            Request to be sent to the 'production' destination.
     * @throws PushNotificationException
     *             if the notification cannot be delivered to the 'production'
     *             destination.
     */
    protected abstract PushNotificationResponse sendToProductionDestination(
	    PushNotificationRequest request) throws PushNotificationException;

}
