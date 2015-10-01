/*
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.amazonsqs.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

public class AmazonSQSContext extends AbstractConnector {

    private static Log log = LogFactory.getLog(AmazonSQSContext.class);

    public void connect(MessageContext msgContext) throws SynapseException {
        try {
            removeContext(msgContext);
        } catch (Exception e) {
            log.error("Error while removing the properties");
            throw new SynapseException("Error while removing the properties", e);
        }
    }

    /**
     * Remove the previous amazonsqs method's context
     *
     * @param messageContext
     */
    private void removeContext(final MessageContext messageContext) {
        log.debug("Removing the unneeded properties of the already run methods");
        Object[] keys = messageContext.getPropertyKeySet().toArray();
        for (Object key : keys) {
            if ((key).toString().startsWith("uri.var.") && !(key).toString().startsWith("uri.var.apiUrl")
                    && !(key).toString().startsWith("uri.var.accessKeyId") && !(key).toString().startsWith("uri.var.secretAccessKey")
                    && !(key).toString().startsWith("uri.var.contentType") && !(key).toString().startsWith("uri.var.service")
                    && !(key).toString().startsWith("uri.var.signatureVersion") && !(key).toString().startsWith("uri.var.signatureMethod")
                    && !(key).toString().startsWith("uri.var.version") && !(key).toString().startsWith("uri.var.terminationString")
                    && !(key).toString().startsWith("uri.var.region") && !(key).toString().startsWith("uri.var.hostName")
                    && !(key).toString().startsWith("uri.var.httpMethod")) {
                messageContext.getPropertyKeySet().remove(key);
            }
        }
    }
}


