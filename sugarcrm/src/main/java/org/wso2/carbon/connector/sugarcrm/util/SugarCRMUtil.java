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

package org.wso2.carbon.connector.sugarcrm.util;

import java.util.Iterator;

import org.apache.axiom.om.OMElement;

/**
 * This class contains utility functions used for SugarCRM connector.
 */
public final class SugarCRMUtil {
    
    /**
     * Default private constructor for utility class SugarCRMUtil.
     */
    private SugarCRMUtil() {
    
    }
    
    /**
     * Remove unnecessary root element of the item list and add it to request root list element of the given
     * OM element.
     * 
     * @param omElementIterator iterator for OM elements.
     */
    public static void handleItemListElement(final Iterator< ? > omElementIterator) {
    
        if (omElementIterator.hasNext()) {
            
            OMElement apiElement = (OMElement) omElementIterator.next();
            
            Iterator< ? > tagInnerIterator = apiElement.getChildElements();
            
            if (tagInnerIterator.hasNext()) {
                OMElement tagInnerElement = (OMElement) tagInnerIterator.next();
                Iterator< ? > itemIterator = tagInnerElement.getChildElements();
                
                // detach unwanted parent element of item list.
                tagInnerElement.detach();
                
                while (itemIterator.hasNext()) {
                    OMElement itemElement = (OMElement) itemIterator.next();
                    apiElement.addChild(itemElement);
                }
                
            }
            
        }
    }
    
}
