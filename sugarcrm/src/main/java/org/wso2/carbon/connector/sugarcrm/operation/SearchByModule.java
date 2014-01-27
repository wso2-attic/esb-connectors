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

package org.wso2.carbon.connector.sugarcrm.operation;

import java.util.Iterator;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.connector.sugarcrm.util.SugarCRMUtil;

/**
 * Class mediator which helps to modify the pay load for <strong>search_by_module</strong> method in SugarCRM
 * SOAP API.
 */
public class SearchByModule extends AbstractConnector implements Connector {
    
    /** Represent the search_by_module tag of the pay load in the template. */
    private static final String SEARCH_BY_MODULE_TAG = "search_by_module";
    
    /** Represent the modules name tag. */
    private static final String MODULES_TAG = "modules";
    
    /**
     * Modify request body before sending to the end point.
     * 
     * @param messageContext MessageContext - The message context.
     * @throws ConnectException if connection is failed.
     */
    @SuppressWarnings("unchecked")
    public void connect(MessageContext messageContext) throws ConnectException {
    
        SOAPEnvelope envelope = messageContext.getEnvelope();
        SOAPBody body = envelope.getBody();
        Iterator<OMElement> nameValueListElements = body.getChildrenWithLocalName(SEARCH_BY_MODULE_TAG);
        
        try {
            
            OMElement nameValueListElement = nameValueListElements.next();
            Iterator<OMElement> omElementIterator = nameValueListElement.getChildrenWithLocalName(MODULES_TAG);
            
            if (omElementIterator.hasNext()) {
                OMFactory omFactory = OMAbstractFactory.getOMFactory();
                OMElement item = omElementIterator.next();
                String moduleListString = (String) ConnectorUtils.lookupTemplateParamater(messageContext, MODULES_TAG);
                SugarCRMUtil.getItemElement(omFactory, messageContext, item, moduleListString);
            }
            
        } catch (Exception e) {
            log.error(SugarCRMUtil.EXCEPTION + SugarCRMUtil.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
        
    }
    
}
