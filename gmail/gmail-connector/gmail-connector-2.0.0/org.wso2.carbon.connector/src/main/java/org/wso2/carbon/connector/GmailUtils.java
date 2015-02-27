package org.wso2.carbon.connector;/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.SynapseException;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import javax.xml.namespace.QName;
import java.util.Map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.context.OperationContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.util.ConnectorUtils;



public class GmailUtils {
    private static Log log = LogFactory
            .getLog(GmailUtils.class);
    public static String lookupFunctionParam(MessageContext ctxt, String paramName) {
        return (String)ConnectorUtils.lookupTemplateParamater(ctxt, paramName);
    }



    public static void storeErrorResponseStatus(MessageContext ctxt, Exception e) {
        ctxt.setProperty(SynapseConstants.ERROR_EXCEPTION, e);
        ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, e.getMessage());

        if(ctxt.getEnvelope().getBody().getFirstElement() != null) {
            ctxt.getEnvelope().getBody().getFirstElement().detach();
        }

        OMFactory factory   = OMAbstractFactory.getOMFactory();
        OMNamespace ns      = factory.createOMNamespace("http://org.wso2.esbconnectors.gmail", "ns");
        OMElement searchResult  = factory.createOMElement("ErrorResponse", ns);
        OMElement errorMessage      = factory.createOMElement("ErrorMessage", ns);
        searchResult.addChild(errorMessage);
        errorMessage.setText(e.getMessage());
        ctxt.getEnvelope().getBody().addChild(searchResult);
    }

}
