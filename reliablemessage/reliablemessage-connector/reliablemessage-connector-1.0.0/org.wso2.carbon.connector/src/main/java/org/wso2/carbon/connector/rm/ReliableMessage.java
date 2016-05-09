/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
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
package org.wso2.carbon.connector.rm;


import org.apache.axiom.soap.impl.dom.soap11.SOAP11Factory;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12Factory;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.rm.utils.RMConstants;
import org.wso2.carbon.connector.rm.utils.ReliableMessageUtil;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Reliable message connector
 */
public class ReliableMessage extends AbstractConnector {
    private static Log log = LogFactory.getLog(ReliableMessage.class);

    /**
     * Connect method which is establish connection between ESB with the Reliable backend
     *
     * @param messageContext ESB messageContext.
     * @throws ConnectException
     */
    public void connect(MessageContext messageContext) throws ConnectException {
        log.debug("Start: Reliable message connector");
        // Invoke backend reliable message service
        invokeBackendRMService(messageContext);
    }

    /**
     * This method used to create dispatcher
     *
     * @param inputParams input parameters.
     * @return Dispatch source dispatcher
     * @throws ConnectException
     */
    private Dispatch<Source> createDispatch(RMParameters inputParams, MessageContext messageContext) throws ConnectException {
        String portName = inputParams.getPortName();
        String serviceName = inputParams.getServiceName();
        String nameSpace = inputParams.getNamespace();

        QName serviceQName = new QName(nameSpace, serviceName);
        QName portQName = new QName(nameSpace, portName);
        Service service = Service.create(serviceQName);

        if (service == null) {
            String message = "Service instance cannot initialize";
            log.error(message);
            throw new ConnectException(message);
        }

        if (RMConstants.SOAP_V_11.equals(inputParams.getSoapVersion())) {
            service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING, inputParams.getEndpoint());
        } else {
            service.addPort(portQName, SOAPBinding.SOAP12HTTP_BINDING, inputParams.getEndpoint());
        }

        Dispatch<Source> sourceDispatch = service.createDispatch(portQName, Source.class, Service.Mode.MESSAGE);
        if (messageContext.getSoapAction() != null && !messageContext.getSoapAction().isEmpty()) {
            sourceDispatch.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, messageContext.getSoapAction());
        }
        return sourceDispatch;
    }


    /**
     * This method used to invoke backend reliable message service.
     *
     * @param messageContext ESB messageContext.
     * @throws ConnectException
     */
    private void invokeBackendRMService(MessageContext messageContext)
            throws ConnectException {
        log.debug("Backend service invoking");
        if(messageContext.getProperty(RMConstants.RM_PARAMS) == null || messageContext.getProperty(RMConstants.SPRING_BUS) == null) {
            String message = "Connector configuration not found";
            log.error(message);
            throw new ConnectException(message);
        }

        RMParameters inputParams = (RMParameters) messageContext.getProperty(RMConstants.RM_PARAMS);
        Bus springBus = (Bus) messageContext.getProperty(RMConstants.SPRING_BUS);
        Dispatch<Source> sourceDispatch = createDispatch(inputParams, messageContext);
        Source source = new StreamSource(ReliableMessageUtil.getSOAPEnvelopAsStreamSource(messageContext.getEnvelope()));
        Source response = sourceDispatch.invoke(source);
        setResponse(messageContext, response);
        // shutdown bus
        springBus.shutdown(true);
    }


    /**
     * This method used to set response to the messageContext
     *
     * @param messageContext ESB messageContext.
     * @param response       backend reliable service response
     * @throws ConnectException
     */
    private void setResponse(MessageContext messageContext, Source response) throws ConnectException {
        if (response != null) {
            try {
                String responseString = org.apache.cxf.helpers.XMLUtils.toString(response);
                messageContext.setEnvelope(ReliableMessageUtil.toOMSOAPEnvelope(responseString));
            } catch (Exception e) {
                String message = "Exception occurred while parsing response to the SOAPEnvelop";
                log.error(message);
                throw new ConnectException(e, message);
            }
        } else {
            try {
                if (messageContext.isSOAP11()) {
                    messageContext.setEnvelope(new SOAP11Factory().getDefaultEnvelope());
                } else {
                    messageContext.setEnvelope(new SOAP12Factory().getDefaultEnvelope());
                }
            } catch (AxisFault axisFault) {
                String message = "Exception occurred while setting response to the SOAPEnvelop";
                log.error(axisFault.getMessage());
                throw new ConnectException(message);
            }
        }
        messageContext.setResponse(true);
    }
}
