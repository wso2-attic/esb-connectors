/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.transport.nhttp.NhttpConstants;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

public class AddEntry extends AbstractConnector {
    public static final String OBJECT_CLASS = "objectClass";
    public static final String ATTRIBUTES = "attributes";
    public static final String DN = "dn";

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        String objectClass = (String)getParameter(messageContext, OBJECT_CLASS);
        String attributesString = (String)getParameter(messageContext, ATTRIBUTES);
        String dn = (String)getParameter(messageContext, DN);

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(LDAPConstants.CONNECTOR_NAMESPACE, "ns");
        OMElement result = factory.createOMElement("result", ns);
        OMElement message = factory.createOMElement("message", ns);

        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext)messageContext).getAxis2MessageContext();

        try {

            DirContext context = LDAPUtils.getDirectoryContext(messageContext);

            String classes[] = objectClass.split(",");
            Attributes entry = new BasicAttributes();
            Attribute obClassAttr = new BasicAttribute("objectClass");
            for (int i = 0; i < classes.length; i++) {
                obClassAttr.add(classes[i]);
            }
            entry.put(obClassAttr);
            if (attributesString != null) {
                String attrSet[] = attributesString.split(",");
                for (int i = 0; i < attrSet.length; i++) {
                    String keyVals[] = attrSet[i].split("=");
                    Attribute newAttr = new BasicAttribute(keyVals[0]);
                    newAttr.add(keyVals[1]);
                    entry.put(newAttr);
                }
            }
            try {
                context.createSubcontext(dn, entry);
                message.setText("Success");
                result.addChild(message);
                LDAPUtils.preparePayload(messageContext,result);
            } catch (NamingException e) {
                log.error("Failed to create ldap entry with dn = " + dn,e);
                LDAPUtils.handleErrorResponse(messageContext, LDAPConstants.ErrorConstants.ADD_ENTRY_ERROR, e);
                throw new SynapseException(e);
            }
        } catch (NamingException e) {
            LDAPUtils.handleErrorResponse(messageContext,LDAPConstants.ErrorConstants.INVALID_LDAP_CREDENTIALS,e);
            throw new SynapseException(e);
        }
    }


}
