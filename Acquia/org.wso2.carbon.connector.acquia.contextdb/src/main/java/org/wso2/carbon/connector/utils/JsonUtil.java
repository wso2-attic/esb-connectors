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


import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class JsonUtil extends AbstractMediator {
    public static final String PAYLOAD = "acquia.contextdb.payload";
    public boolean mediate(MessageContext messageContext) {
        String payload = messageContext.getProperty(PAYLOAD).toString();
        StringBuilder basestring = null;
        try {
            if (payload!=null) {
                if (payload.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(payload);
                    for (int i = 0, size = jsonArray.length(); i < size; i++) {
                        JSONObject objectInArray = jsonArray.getJSONObject(i);
                        if (basestring == null) {
                            basestring = new StringBuilder(objectInArray.toString());
                        } else {
                            basestring.append("\n");
                            basestring.append(objectInArray.toString());
                        }
                    }
                } else {
                    JSONObject jsonObject = new JSONObject(payload);
                    basestring = new StringBuilder(jsonObject.toString());
                }
            }
        } catch (Exception e) {
            throw new SynapseException(e);
        }
        if (basestring!=null) {
            messageContext.setProperty(PAYLOAD,basestring.toString());
        }
        return true;
    }
}