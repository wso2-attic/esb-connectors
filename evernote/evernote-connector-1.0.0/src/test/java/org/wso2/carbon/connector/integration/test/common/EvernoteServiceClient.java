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


package org.wso2.carbon.connector.integration.test.common;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;

import javax.xml.namespace.QName;


public class EvernoteServiceClient extends ServiceClient {

    public EvernoteServiceClient() throws AxisFault {
        super();
    }

    private static final Log log = LogFactory.getLog(EvernoteServiceClient.class);

    public OMElement sendReceive(OMElement payload, String endPointReference, String operation)
            throws AxisFault {

        Options options;
        OMElement response = null;
        if (log.isDebugEnabled()) {
            log.debug("Service Endpoint : " + endPointReference);
            log.debug("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setTimeOutInMilliSeconds(45000);
            options.setAction("urn:" + operation);
            setOptions(options);

            response = sendReceive(payload);
            if (log.isDebugEnabled()) {
                log.debug("Response Message : " + response);
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
        Assert.assertNotNull(response);
        return response;
    }

    @Override
    public OMElement sendReceive(QName operationQName, OMElement xmlPayload)
            throws AxisFault {
        MessageContext messageContext = new MessageContext();
        fillSOAPEnvelope(messageContext, xmlPayload);
        OperationClient operationClient = createClient(operationQName);
        operationClient.addMessageContext(messageContext);
        operationClient.execute(true);
        MessageContext response = operationClient
                .getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        if (super.getOptions().isCallTransportCleanup()) {
            response.getEnvelope().build();
            cleanupTransport();
        }
        return response.getEnvelope().getBody();
    }

    private void fillSOAPEnvelope(MessageContext messageContext, OMElement xmlPayload)
            throws AxisFault {
        messageContext.setServiceContext(getServiceContext());
        SOAPFactory soapFactory = getSOAPFactory();
        SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
        if (xmlPayload != null) {
            envelope.getBody().addChild(xmlPayload);
        }
        addHeadersToEnvelope(envelope);
        messageContext.setEnvelope(envelope);
    }

    private SOAPFactory getSOAPFactory() {
        String soapVersionURI = getOptions().getSoapVersionURI();
        if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(soapVersionURI)) {
            return OMAbstractFactory.getSOAP12Factory();
        } else {
            // make the SOAP 1.1 the default SOAP version
            return OMAbstractFactory.getSOAP11Factory();
        }
    }



}
