/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package org.wso2.carbon.connector.utils;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class AcquiaJsonUtil extends AbstractMediator {

    /**
     * Log instance.
     */
    private static Log log = LogFactory.getLog(AcquiaJsonUtil.class);

    /**
     * Constants.
     */
    public static final String PAYLOAD = "acquia.contextdb.payload";
    public static final String EVENT_IMPORT_IDENTITY = "identity";
    public static final String EVENT_IMPORT_IDENTITY_SOURCE = "identity_source";
    public static final String EVENT_IMPORT_EVENT_NAME = "event_name";
    public static final String EVENT_IMPORT_EVENT_SOURCE = "event_source";

    public boolean mediate(MessageContext messageContext) {
        String payload = messageContext.getProperty(PAYLOAD).toString();
        StringBuilder basestring = null;
        JSONObject objectInArray = null;
        try {
            if (payload != null) {
                if (payload.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(payload);

                    for (int i = 0, size = jsonArray.length(); i < size; i++) {
                        objectInArray = jsonArray.getJSONObject(i);
                        if (objectInArray.has(EVENT_IMPORT_IDENTITY) && objectInArray.has(EVENT_IMPORT_IDENTITY_SOURCE)
                                && objectInArray.has(EVENT_IMPORT_EVENT_NAME) && objectInArray.has(EVENT_IMPORT_EVENT_SOURCE)) {
                            if (basestring == null) {
                                basestring = new StringBuilder(objectInArray.toString());
                            } else {
                                basestring.append("\n");
                                basestring.append(objectInArray.toString());
                            }
                        } else {
                            log.error("Acquia lift ContextDB connector - Given payload does not contain the required fields");
                            throw new SynapseException("Given payload does not contain the required fields");
                        }
                    }
                } else {
                    JSONObject jsonObject = new JSONObject(payload);
                    if (jsonObject!=null && jsonObject.has(EVENT_IMPORT_IDENTITY) && jsonObject.has(EVENT_IMPORT_IDENTITY_SOURCE)
                            && jsonObject.has(EVENT_IMPORT_EVENT_NAME) && jsonObject.has(EVENT_IMPORT_EVENT_SOURCE)) {
                        basestring = new StringBuilder(jsonObject.toString());
                    } else {
                        log.error("Acquia lift ContextDB connector - Given payload does not contain the required fields");
                        throw new SynapseException("Given payload does not contain the required fields");
                    }
                }
            }
        } catch (Exception e) {
            throw new SynapseException(e);
        }
        if (basestring != null) {
            messageContext.setProperty(PAYLOAD, basestring.toString());
        }
        return true;
    }
}
