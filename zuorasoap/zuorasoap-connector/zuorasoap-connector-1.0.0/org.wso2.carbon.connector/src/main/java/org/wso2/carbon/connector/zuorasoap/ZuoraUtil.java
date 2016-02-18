/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.zuorasoap;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseLog;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

import javax.xml.namespace.QName;
import java.util.Iterator;

public final class ZuoraUtil {

    public static final String ZUORA_ZOBJECTS = "zobjects";
    public static final String ZUORA_CREATE_ZOBJECTTYPE = "type";

    public static synchronized ZuoraUtil getZuoraUtil() {
        return new ZuoraUtil();
    }

    public void addZobjects(String strOperation, String strParamName, MessageContext synCtx,
                            SynapseLog synLog, String strExternalId) {
        SOAPEnvelope envelope = synCtx.getEnvelope();
        OMFactory fac = OMAbstractFactory.getOMFactory();
        SOAPBody body = envelope.getBody();

        Iterator<OMElement> bodyChildElements = body.getChildrenWithLocalName(strOperation);
        if (bodyChildElements.hasNext()) {
            try {
                OMElement bodyElement = bodyChildElements.next();
                if (strExternalId != null) {
                    OMNamespace omNs = fac.createOMNamespace("http://api.zuora.com/", "urn");
                    OMElement value = fac.createOMElement("externalIDFieldName", omNs);
                    value.addChild(fac.createOMText(strExternalId));
                    bodyElement.addChild(value);
                }
                String strZobject = (String) ConnectorUtils.lookupTemplateParamater(synCtx, strParamName);
                OMElement zObjects = AXIOMUtil.stringToOM(strZobject);
                Iterator<OMElement> zObject = zObjects.getChildElements();
                String strType = zObjects.getAttributeValue(new QName(ZuoraUtil.ZUORA_CREATE_ZOBJECTTYPE));

                OMElement tmpElement = null;
                OMNamespace omNsurn = fac.createOMNamespace("http://api.zuora.com/", "urn");
                OMNamespace omNsurn1 = fac.createOMNamespace("http://object.api.zuora.com/",
                        "urn1");
                // Loops zObject
                while (zObject.hasNext()) {
                    OMElement currentElement = zObject.next();
                    OMElement newElement = fac.createOMElement("zObjects", omNsurn);
                    // Add Object type
                    if (strType != null) {
                        tmpElement = fac.createOMElement("type", omNsurn1);
                        tmpElement.addChild(fac.createOMText(strType));
                        newElement.addChild(tmpElement);
                    }
                    // Add the fields
                    Iterator<OMElement> zObjectFields = currentElement.getChildElements();
                    while (zObjectFields.hasNext()) {
                        OMElement zObjectField = zObjectFields.next();
                        tmpElement = fac.createOMElement(zObjectField.getLocalName(), omNsurn1);
                        Iterator<OMElement> zNestedObjects = zObjectField.getChildElements();
                        // Support nested zObjects
                        if (zNestedObjects.hasNext()) {
                            while (zNestedObjects.hasNext()) {
                                OMElement sNestedObjectField = zNestedObjects.next();
                                OMElement newChildElement = fac.createOMElement(sNestedObjectField.getLocalName(),
                                        omNsurn1);
                                newChildElement.addChild(fac.createOMText(sNestedObjectField.getText()));
                                tmpElement.addChild(newChildElement);
                            }
                        } else {
                            tmpElement.addChild(fac.createOMText(zObjectField.getText()));
                        }
                        newElement.addChild(tmpElement);
                    }
                    bodyElement.addChild(newElement);
                }
            } catch (Exception e) {
                synLog.error("Zuora adaptor - error injecting zObjects to payload : " + e);
            }
        }
    }

    public void addIds(String strOperation, String strParamName, MessageContext synCtx,
                       SynapseLog synLog) {
        SOAPEnvelope envelope = synCtx.getEnvelope();
        OMFactory fac = OMAbstractFactory.getOMFactory();
        SOAPBody body = envelope.getBody();
        Iterator<OMElement> bodyChildElements = body.getChildrenWithLocalName(strOperation);
        if (bodyChildElements.hasNext()) {
            try {
                OMElement bodyElement = bodyChildElements.next();
                Iterator<OMElement> cElements = bodyElement.getChildElements();
                if (cElements != null && cElements.hasNext()) {
                    cElements.next();
                }
                String strZobject = (String) ConnectorUtils.lookupTemplateParamater(synCtx, strParamName);
                OMElement zObjects = AXIOMUtil.stringToOM(strZobject);
                Iterator<OMElement> zObject = zObjects.getChildElements();
                OMNamespace omNsurn = fac.createOMNamespace("http://api.zuora.com/", "urn");
                // Loops zObject
                while (zObject.hasNext()) {
                    OMElement currentElement = zObject.next();
                    OMElement newElement = fac.createOMElement("ids", omNsurn);
                    // Add the fields
                    newElement.addChild(fac.createOMText(currentElement.getText()));
                    bodyElement.addChild(newElement);
                }
            } catch (Exception e) {
                synLog.error("Zuora adaptor - error injecting zObjects to payload : " + e);
            }
        }
    }
}