/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.rm.utils.RMConstants;
import org.wso2.carbon.connector.rm.utils.ReliableMessageUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class ReliableMessageInitializer extends AbstractConnector {
    private static Log log = LogFactory.getLog(ReliableMessageInitializer.class);

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        log.debug("Start: Reliable message connector configuration initializing");
        // Input parameters population
        RMParameters inputParams = populateInputParameters(messageContext);
        // Validate and set default values to the parameters
        ReliableMessageUtil.validateInputs(inputParams);
        // SpringBuss initialize
        Bus springBus = initiateSpringBuss(inputParams);
        messageContext.setProperty(RMConstants.RM_PARAMS, inputParams);
        messageContext.setProperty(RMConstants.SPRING_BUS, springBus);
    }

    /**
     * populateInputParameters method used to populate connector inputs.
     *
     * @param messageContext ESB messageContext.
     * @return RMParameters input parameters.
     */
    private RMParameters populateInputParameters(MessageContext messageContext) throws ConnectException {
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
            File cxfServerConfigFile = new File(messageContext.getProperty(RMConstants.CONF_LOCATION).toString());
            try {
                URL busFile = cxfServerConfigFile.toURI().toURL();
                inputParams.setConfigLocation(busFile.toString());
            } catch (MalformedURLException e) {
                String message = "The provided CXF RM configuration file location is invalid";
                log.error(message, e);
                throw new ConnectException(message);
            }
        }
        return inputParams;
    }

    /**
     * This method used to initializing springBuss instance
     *
     * @param inputParams connector input parameters.
     * @throws ConnectException
     */
    private Bus initiateSpringBuss(RMParameters inputParams) throws ConnectException {
        SpringBusFactory bf = new SpringBusFactory();
        Bus springBus = bf.createBus(inputParams.getConfigLocation());
        log.debug("SpringBus initialized");
        bf.setDefaultBus(springBus);
        return springBus;
    }
}
