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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.sugarcrm.util.SugarCRMUtil;

/**
 * Class mediator which helps to modify the pay load for <strong>name_value_list</strong> item list in
 * SugarCRM SOAP API.
 */
public class NameValueItemListHandler extends AbstractConnector {
    
    /** Represent the name_value_list tag of the pay load in the template. */
    private static final String NAME_VALUE_LIST_TAG = "name_value_list";
    
    /**
     * Modify request body before sending to the end point. Set XML array values to the appropriate target
     * element.
     * 
     * @param messageContext MessageContext - The Synapse message context.
     */
    public final void connect(final MessageContext messageContext) {
    
        SOAPEnvelope envelope = messageContext.getEnvelope();
        SOAPBody body = envelope.getBody();
        
        OMElement mainElement = body.getFirstElement();
        
        Iterator< ? > nameValueListElements = mainElement.getChildrenWithLocalName(NAME_VALUE_LIST_TAG);
        
        SugarCRMUtil.handleItemListElement(nameValueListElements);
        
    }
    
}
