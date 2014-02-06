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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.synapse.MessageContext;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

/**
 * This class contains utility functions used for SugarCRM connector.
 */
public class SugarCRMUtil {
    
    /** Represent the prefix for exception logs. */
    public static final String EXCEPTION = "EXCEPTION: ";
    
    /** Represent the name space needed for the pay load. */
    public static final String NAME_SPACE = "http://www.sugarcrm.com/sugarcrm";
    
    /** Represent the name space alias. */
    public static final String NAME_SPACE_ALIAS = "sug";
    
    /** Represent the new tag name item. */
    private static final String ITEM_TAG = "item";
    
    /** Represent an empty string. */
    private static final String EMPTY_STRING = "";
    
    /** Represent the enter key string (\n). */
    private static final String ENTER_KEY_STRING = "\n";
    
    /**
     * Add the child elements to the given OM element.
     * 
     * @param omFactory OMFactory
     * @param messageContext MessageContext
     * @param childElement OMElement
     * @param strObject String
     * @return OMElement
     */
    public static final OMElement getItemElement(final OMFactory omFactory, final MessageContext messageContext,
            final OMElement childElement, final String strObject) {
    
        String[] strArray = strObject.split(ENTER_KEY_STRING);
        List<String> itemList = new ArrayList<String>();
        
        for (int i = 0; i < strArray.length; i++) {
            if (!EMPTY_STRING.equals(strArray[i].trim())) {
                itemList.add(strArray[i].trim());
            }
        }
        
        // assume the child element name is item and soap request need to be
        // sent as item
        Iterator<String> iterator = itemList.iterator();
        while (iterator.hasNext()) {
            OMElement itemElement = omFactory.createOMElement(ITEM_TAG, null);
            itemElement.addChild(omFactory.createOMText(iterator.next()));
            childElement.addChild(itemElement);
        }
        
        return childElement;
    }
    
    /**
     * Return the stack trace for a <strong>Throwable</strong> as a String.
     * 
     * @param e <strong>Throwable</strong>
     * @return <strong>String</strong> The stack trace as String
     */
    public static String getStackTraceAsString(final Throwable e) {
    
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
}
