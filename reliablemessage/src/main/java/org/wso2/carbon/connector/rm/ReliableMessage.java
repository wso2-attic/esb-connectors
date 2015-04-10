/*
 *
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
 * /
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
import java.net.MalformedURLException;
import java.net.URL;


public class ReliableMessage extends AbstractConnector {

    private static Log log = LogFactory.getLog(ReliableMessage.class);
    private Bus springBus = null;

    public void connect(MessageContext messageContext) throws ConnectException {

        log.info("Start: Reliable message connector");

        //Input parameters initialize
        RMParameters inputParams = populateInputParameters(messageContext);
        //Validate and set default values to the parameters
        ReliableMessageUtil.validateInputs(inputParams);
        //update configuration file with the given configurations
        String configurationFileLocation = ReliableMessageUtil.getUpdatedFileLocation(inputParams);
        //SpringBuss initialize
        initiateSpringBuss(configurationFileLocation, inputParams.isDynamicParam());
        //Invoke backend reliable message service
        Source response = invokeBackendRMService(messageContext, inputParams);
        //Set response back to the message context
        setResponse(messageContext, response);
        //SpringBuss shutdown
        shutdownSpringBuss(inputParams.isDynamicParam());

    }

    private RMParameters populateInputParameters(MessageContext messageContext) {

        RMParameters inputParams = new RMParameters();

        if (messageContext.getProperty(RMConstants.WSDL_URL) != null) {
            inputParams.setWsdlUrl(messageContext.getProperty(RMConstants.WSDL_URL).toString());
        }
        if (messageContext.getProperty(RMConstants.SERVICE_NAME) != null) {
            inputParams.setServiceName(messageContext.getProperty(RMConstants.SERVICE_NAME).toString());
        }

        if (messageContext.getProperty(RMConstants.PORT_NAME) != null) {
            inputParams.setPortName(messageContext.getProperty(RMConstants.PORT_NAME).toString());
        }

        if (messageContext.getProperty(RMConstants.ACK_INTERVAL) != null) {
            inputParams.setAckInterval(messageContext.getProperty(RMConstants.ACK_INTERVAL).toString());
        }

        if (messageContext.getProperty(RMConstants.RE_TRANS_INTERVAL) != null) {
            inputParams.setRetransmissionInterval(messageContext.getProperty(RMConstants.RE_TRANS_INTERVAL).toString());
        }

        if (messageContext.getProperty(RMConstants.INTRA_MESSAGE_THRESHOLD) != null) {
            inputParams.setIntraMessageThreshold(messageContext.getProperty(RMConstants.INTRA_MESSAGE_THRESHOLD).toString());
        }

        if (messageContext.getProperty(RMConstants.DYNAMIC_PARAM) != null) {

            if ("true".equals(messageContext.getProperty(RMConstants.DYNAMIC_PARAM).toString())) {
                inputParams.setDynamicParam(true);
            }
        }

        return inputParams;

    }

    private void initiateSpringBuss(String fileLocation, boolean dynamicParamFlag) {

        if(springBus == null || dynamicParamFlag) {
            SpringBusFactory bf = new SpringBusFactory();
            springBus = bf.createBus(fileLocation.toString());
            log.debug("SpringBus initialized");
            BusFactory.setDefaultBus(springBus);
        }



    }

    private void shutdownSpringBuss(boolean dynamicParamFlag) {
        if(dynamicParamFlag) {
            springBus.shutdown(true);
            log.debug("Shutdown SpringBus");
        }
    }

    private void setResponse(MessageContext messageContext, Source response) throws ConnectException {

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

    private Source invokeBackendRMService(MessageContext messageContext, RMParameters inputParams) throws ConnectException {
        log.info("Backend service invoking");
        Dispatch<Source> sourceDispatch = createDispatch(inputParams.getWsdlUrl(), inputParams.getServiceName(), inputParams.getPortName());
        Source source = new StreamSource(ReliableMessageUtil.getSOAPEnvelopAsStreamSource(messageContext.getEnvelope()));
        return sourceDispatch.invoke(source);

    }

    private Dispatch<Source> createDispatch(String wsdlUrl, String serviceName, String portName) throws ConnectException {

        URL wsdl;
        try {
            wsdl = new URL(wsdlUrl);
        } catch (MalformedURLException e) {
            String message = "Malformed WSDL URL has occurred";
            log.error(message);
            throw new ConnectException(e, message);
        }
        String nameSpace = ReliableMessageUtil.getNamespaceForGivenPort(wsdl, portName);
        QName serviceQName = new QName(nameSpace, serviceName);
        QName portQName = new QName(nameSpace, portName);
        Service service = Service.create(wsdl, serviceQName);
        return service.createDispatch(portQName, Source.class, Service.Mode.MESSAGE);
    }


}
