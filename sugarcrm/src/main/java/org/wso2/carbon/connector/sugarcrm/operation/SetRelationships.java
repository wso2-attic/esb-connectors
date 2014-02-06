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
 * Class mediator which helps to modify the pay load for <strong>set_relationships</strong> method in SugarCRM
 * SOAP API.
 */
public class SetRelationships extends AbstractConnector implements Connector {
    
    /** Represent the set_relationships tag of the pay load in the template. */
    private static final String SET_RELATIONSHIPS_TAG = "set_relationships";
    
    /**
     * Represent the set_relationships_list tag of the pay load in the template.
     */
    private static final String SET_RELATIONSHIP_LIST_TAG = "set_relationship_list";
    
    /** Represent the template parameter relationshipLists. */
    private static final String RELATIONSHIP_LISTS_TAG = "relationshipLists";
    
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
            
            Iterator<OMElement> setRelationshipsElements = soapBody.getChildrenWithLocalName(SET_RELATIONSHIPS_TAG);
            
            if (setRelationshipsElements.hasNext()) {
                
                OMElement setRelationshipsElement = setRelationshipsElements.next();
                Iterator<OMElement> childElements =
                        setRelationshipsElement.getChildrenWithLocalName(SET_RELATIONSHIP_LIST_TAG);
                
                if (childElements.hasNext()) {
                    
                    OMElement childElement = childElements.next();
                    String relationshipsListString =
                            (String) ConnectorUtils.lookupTemplateParamater(messageContext, RELATIONSHIP_LISTS_TAG);
                    OMElement omElementList = AXIOMUtil.stringToOM(relationshipsListString);
                    Iterator<OMElement> omElementIterator = omElementList.getChildElements();
                    
                    while (omElementIterator.hasNext()) {
                        OMElement newElement = omElementIterator.next();
                        childElement.addChild(newElement);
                    }
                    
                }
                
            }
            
        } catch (Exception e) {
            log.error(SugarCRMUtil.EXCEPTION + SugarCRMUtil.getStackTraceAsString(e));
            throw new ConnectException(e);
        }
        
    }
    
}
