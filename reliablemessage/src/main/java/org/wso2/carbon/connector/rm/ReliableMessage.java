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
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Reliable message connector
 */
public class ReliableMessage extends AbstractConnector {

    private static Log log = LogFactory.getLog(ReliableMessage.class);
    private Bus springBus = null;

    /**
     * Connect method which is establish connection between ESB with the Reliable backend
     *
     * @param messageContext ESB messageContext.
     * @throws ConnectException
     */
    public void connect(MessageContext messageContext) throws ConnectException {

        log.debug("Start: Reliable message connector");

        // Input parameters population
        RMParameters inputParams = populateInputParameters(messageContext);
        // Validate and set default values to the parameters
        ReliableMessageUtil.validateInputs(inputParams);
        // SpringBuss initialize
        initiateSpringBuss(inputParams);
        // Invoke backend reliable message service
        Source response = invokeBackendRMService(messageContext, inputParams);
        // Set response back to the message context
        setResponse(messageContext, response);

    }

    /**
     * populateInputParameters method used to populate connector inputs.
     *
     * @param messageContext ESB messageContext.
     * @return RMParameters input parameters.
     */
    private RMParameters populateInputParameters(MessageContext messageContext) {

        RMParameters inputParams = new RMParameters();

        if (messageContext.getProperty(RMConstants.ENDPOINT) != null) {
            inputParams.setEndpoint(messageContext.getProperty(RMConstants.ENDPOINT).toString());
        }
        if (messageContext.getProperty(RMConstants.SERVICE_NAME) != null) {
            inputParams.setServiceName(messageContext.getProperty(RMConstants.SERVICE_NAME).toString());
        }

        if (messageContext.getProperty(RMConstants.NAMESPACE) != null) {
            inputParams.setNamespace(messageContext.getProperty(RMConstants.NAMESPACE).toString());
        }

        if (messageContext.getProperty(RMConstants.PORT_NAME) != null) {
            inputParams.setPortName(messageContext.getProperty(RMConstants.PORT_NAME).toString());
        }

        if (messageContext.getProperty(RMConstants.SOAP_VERSION) != null) {
            inputParams.setSoapVersion(messageContext.getProperty(RMConstants.SOAP_VERSION).toString());
        }

        if (messageContext.getProperty(RMConstants.CONF_LOCATION) != null) {
            inputParams.setConfigLocation(messageContext.getProperty(RMConstants.CONF_LOCATION).toString());
        }

        return inputParams;

    }

    /**
     * This method used to initializing springBuss instance
     *
     * @param inputParams connector input parameters.
     * @throws ConnectException
     */
    private void initiateSpringBuss(RMParameters inputParams) throws ConnectException {

        if (springBus == null) {

            synchronized (this) {

                if (springBus == null) {
                    SpringBusFactory bf = new SpringBusFactory();
                    springBus = bf.createBus(inputParams.getConfigLocation());
                    log.debug("SpringBus initialized");
                    BusFactory.setDefaultBus(springBus);
                }
            }
        }

    }


    /**
     * This method used to create dispatcher
     *
     * @param inputParams input parameters.
     * @return Dispatch source dispatcher
     * @throws ConnectException
     */
    private Dispatch<Source> createDispatch(RMParameters inputParams) throws ConnectException {

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

        return service.createDispatch(portQName, Source.class, Service.Mode.MESSAGE);
    }


    /**
     * This method used to invoke backend reliable message service.
     *
     * @param messageContext ESB messageContext.
     * @param inputParams    input parameters.
     * @return Source backend service response.
     * @throws ConnectException
     */
    private Source invokeBackendRMService(MessageContext messageContext, RMParameters inputParams)
            throws ConnectException {
        log.debug("Backend service invoking");
        Dispatch<Source> sourceDispatch = createDispatch(inputParams);
        Source source = new StreamSource(ReliableMessageUtil.getSOAPEnvelopAsStreamSource(messageContext.getEnvelope()));
        return sourceDispatch.invoke(source);

    }


    /**
     * This method used to set response to the messageContext
     *
     * @param messageContext ESB messageContext.
     * @param response       backend reliable service response
     * @throws ConnectException
     */
    private void setResponse(MessageContext messageContext, Source response)
            throws ConnectException {

        if (response != null) {

            try {
                String responseString = org.apache.cxf.helpers.XMLUtils.toString(response);
                messageContext.setEnvelope(ReliableMessageUtil.toOMSOAPEnvelope(responseString));
                messageContext.setResponse(true);
            } catch (Exception e) {
                String message = "Exception occurred while parsing response to the SOAPEnvelop";
                log.error(message);
                throw new ConnectException(e, message);
            }

        }

    }

}
