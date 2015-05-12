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
package org.wso2.carbon.connector.rm.utils;


import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.staxutils.StaxUtils;
import org.compass.core.util.reader.StringReader;
import org.w3c.dom.Document;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.rm.RMParameters;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Reliable message utility methods
 */
public class ReliableMessageUtil {

    private static Log log = LogFactory.getLog(ReliableMessageUtil.class);

    /**
     * Convert string xml content to axiom SoapEnvelop
     *
     * @param xmlContent String soapEnvelop content
     * @return SOAPEnvelope backend reliable service response
     */
    public static org.apache.axiom.soap.SOAPEnvelope toOMSOAPEnvelope(String xmlContent) {
        XMLStreamReader reader = StaxUtils.createXMLStreamReader(new ByteArrayInputStream(xmlContent.getBytes()));
        // Get a SOAP OM Builder.  Passing null causes the version to be automatically triggered
        StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(reader, null);
        // Create and return the OM Envelope
        return builder.getSOAPEnvelope();
    }

    /**
     * Convert SOAPEnvelope to inputStream
     *
     * @param soapEnvelope soapEnvelop.
     * @throws ConnectException
     * @retuen InputStream converted inputStream.
     */
    public static InputStream getSOAPEnvelopAsStreamSource(SOAPEnvelope soapEnvelope)
            throws ConnectException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        StringWriter stringWriter = null;

        try {

            docBuilder = documentBuilderFactory.newDocumentBuilder();
            stringWriter = new StringWriter();

            String soapEnvelop = "";
            if(soapEnvelope != null && soapEnvelope.getBody() != null) {
                soapEnvelop = soapEnvelope.getBody().toString();
            }
            Document doc = docBuilder.parse(new InputSource(new StringReader(soapEnvelop)));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer trans = transformerFactory.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StreamResult result = new StreamResult(stringWriter);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);

        } catch (Exception e) {
            String message = "Failed to parse SOAPEnvelop request in to StreamSource";
            throwException(message, e);
        } finally {

            try {
                if (stringWriter != null) {
                    stringWriter.close();
                }
            } catch (IOException e) {
                log.warn("Failed to close String writer:" + e.getMessage());
            }
        }
        return new ByteArrayInputStream(stringWriter.toString().getBytes());
    }

    /**
     * Validate input parameters.
     *
     * @param inputParams input parameters.
     * @throws ConnectException
     */
    public static void validateInputs(RMParameters inputParams) throws ConnectException {
        validateMandatoryInputs(inputParams);
        validateSoapVersion(inputParams);
    }

    /**
     * Validate mandatory input parameters.
     *
     * @param inputParams input parameters.
     * @throws ConnectException
     */
    private static void validateMandatoryInputs(RMParameters inputParams) throws ConnectException {

        StringBuffer fields = new StringBuffer();
        boolean valid = true;

        if (inputParams.getEndpoint() == null || inputParams.getEndpoint().isEmpty()) {
            fields.append("wsdlURL ");
            valid = false;
        }

        if (inputParams.getServiceName() == null || inputParams.getServiceName().isEmpty()) {
            fields.append("serviceName ");
            valid = false;
        }

        if (inputParams.getPortName() == null || inputParams.getPortName().isEmpty()) {
            fields.append("portName ");
            valid = false;
        }

        if (inputParams.getNamespace() == null || inputParams.getNamespace().isEmpty()) {
            fields.append("namespace ");
            valid = false;
        }

        if (inputParams.getConfigLocation() == null || inputParams.getConfigLocation().isEmpty()) {
            fields.append("configLocation ");
            valid = false;
        }

        if (!valid) {
            String message = fields + " cannot be null";
            throwException(message);
        }

    }

    /**
     * Validate soap version.
     *
     * @param inputParams input parameters.
     * @throws ConnectException
     */
    private static void validateSoapVersion(RMParameters inputParams) throws ConnectException {

        if (inputParams.getSoapVersion() == null || inputParams.getSoapVersion().isEmpty()) {
            inputParams.setSoapVersion(RMConstants.SOAP_V_11); //set default soap version(1.1)
        }

        if (!RMConstants.SOAP_V_11.equals(inputParams.getSoapVersion()) && !RMConstants.SOAP_V_12.equals(inputParams.getSoapVersion())) {
            String message = "Invalid soap version defined";
            throwException(message);
        }

    }

    private static void throwException(String message, Exception e) throws ConnectException {
        log.error(message);
        throw new ConnectException(e, message);
    }

    private static void throwException(String message) throws ConnectException {
        log.error(message);
        throw new ConnectException(message);
    }


}
