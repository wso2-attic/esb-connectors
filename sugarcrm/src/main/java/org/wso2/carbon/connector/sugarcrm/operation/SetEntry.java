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
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.connector.sugarcrm.util.SugarCRMUtil;

/**
 * Class mediator which helps to modify the pay load for <strong>set_entry</strong> method in SugarCRM SOAP
 * API.
 */
public class SetEntry extends AbstractConnector implements Connector {
    
    /** Represent the set_entry tag of the pay load in the template. */
    private static final String SET_ENTRY_TAG = "set_entry";
    
    /** Represent the name_value_list tag of the pay load in the template. */
    private static final String NAME_VALUE_LIST_TAG = "name_value_list";
    
    /** Represent the template parameter nameValueList. */
    private static final String NAME_VALUE_LIST_PARAMETER = "nameValueList";
    
    /**
     * Modify request body before sending to the end point.
     * 
     * @param messageContext MessageContext - The message context.
     * @throws ConnectException if connection is failed.
     */
    @SuppressWarnings("unchecked")
    public void connect(MessageContext messageContext) throws ConnectException {
    
        try {
            
            SOAPEnvelope envelope = messageContext.getEnvelope();
            SOAPBody soapBody = envelope.getBody();
            Iterator<OMElement> setEntryElements = soapBody.getChildrenWithLocalName(SET_ENTRY_TAG);
            
            if (setEntryElements.hasNext()) {
                
                OMElement setEntryElement = setEntryElements.next();
                Iterator<OMElement> nameValueListElements =
                        setEntryElement.getChildrenWithLocalName(NAME_VALUE_LIST_TAG);
                
                if (nameValueListElements.hasNext()) {
                    
                    String nameValueListString =
                            (String) ConnectorUtils.lookupTemplateParamater(messageContext, NAME_VALUE_LIST_PARAMETER);
                    OMElement omElementList = AXIOMUtil.stringToOM(nameValueListString);
                    Iterator<OMElement> omElementIterator = omElementList.getChildElements();
                    
                    // Append children to request body
                    if (omElementIterator.hasNext()) {
                        OMElement nameValueListElement = nameValueListElements.next();
                        nameValueListElement.addChild(omElementIterator.next());
                    }
                    
                }
                
            }
            
        } catch (Exception e) {
            log.error(SugarCRMUtil.EXCEPTION + SugarCRMUtil.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
        
    }
    
}
