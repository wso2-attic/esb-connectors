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
 * Class mediator which helps to modify the pay load for <strong>set_entries</strong> method in SugarCRM SOAP
 * API.
 */
public class SetEntries extends AbstractConnector implements Connector {
    
    /** Represent the set_entries tag of the pay load in the template. */
    private static final String SET_ENTRIES_TAG = "set_entries";
    
    /** Represent the template parameter nameValueLists. */
    private static final String NAME_VALUE_LISTS_PARAMETER = "nameValueLists";
    
    /** Represent the new tag name_value_lists for modify the existing pay load. */
    private static final String NAME_VALUE_LISTS_TAG = "name_value_lists";
    
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
            Iterator<OMElement> setEntriesElements = soapBody.getChildrenWithLocalName(SET_ENTRIES_TAG);
            
            if (setEntriesElements.hasNext()) {
                
                OMElement setEntriesElement = setEntriesElements.next();
                Iterator<OMElement> childElements = setEntriesElement.getChildrenWithLocalName(NAME_VALUE_LISTS_TAG);
                
                if (childElements.hasNext()) {
                    
                    OMElement childElement = childElements.next();
                    String nameValueListsString =
                            (String) ConnectorUtils.lookupTemplateParamater(messageContext, NAME_VALUE_LISTS_PARAMETER);
                    OMElement omElementList = AXIOMUtil.stringToOM(nameValueListsString);
                    Iterator<OMElement> omElementIterator = omElementList.getChildElements();
                    
                    while (omElementIterator.hasNext()) {
                        OMElement nameValueListsElement = omElementIterator.next();
                        childElement.addChild(nameValueListsElement);
                    }
                    
                }
                
            }
            
        } catch (Exception e) {
            log.error(SugarCRMUtil.EXCEPTION + SugarCRMUtil.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
        
    }
    
}
