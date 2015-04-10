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
package org.wso2.carbon.connector.rm.utils;


import com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.staxutils.StaxUtils;
import org.compass.core.util.reader.StringReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.rm.RMParameters;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class ReliableMessageUtil {

    private static Log log = LogFactory.getLog(ReliableMessageUtil.class);

    public static org.apache.axiom.soap.SOAPEnvelope toOMSOAPEnvelope(String xmlContent) {
        XMLStreamReader reader = StaxUtils.createXMLStreamReader(new ByteArrayInputStream(xmlContent.getBytes()));
        // Get a SOAP OM Builder.  Passing null causes the version to be automatically triggered
        StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(reader, null);
        // Create and return the OM Envelope
        return builder.getSOAPEnvelope();
    }

    public static String getNamespaceForGivenPort(URL wsdl, String portName) throws ConnectException {

        HttpURLConnection connection = null;
        Document document = null;

        try {

            connection = (HttpURLConnection) wsdl.openConnection();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(new InputSource(connection.getInputStream()));

        } catch (IOException e) {
            String message = "Problem occurred while retrieving given WSDL from the location";
            log.error(message);
            throw new ConnectException(e, message);
        } catch (SAXException e) {
            String message = "Problem occurred while parsing WSDL"; //TODO need to add multi-catching exceptions(only in java7 >)
            log.error(message);
            throw new ConnectException(e, message);
        } catch (ParserConfigurationException e) {
            String message = "Problem occurred while parsing WSDL";
            log.error(message);
            throw new ConnectException(e, message);
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }

        NodeList portTagList = document.getElementsByTagName(RMConstants.PORT_TAG_NAME);

        if (portTagList == null || portTagList.getLength() == 0) {
            String message = "Port is not define in the WSDL";
            log.error(message);
            throw new ConnectException(message);
        }

        Node selectedNode = null;

        for (int i = 0; i < portTagList.getLength(); i++) {
            Node node = portTagList.item(i).getAttributes().getNamedItem(RMConstants.PORT_NAME_ATTRIBUTE_NAME);

            if (node != null) {
                if (node.getNodeValue().equals(portName)) {
                    selectedNode = portTagList.item(i).getAttributes().getNamedItem(RMConstants.BINDING_ATTRIBUTE_NAME);
                }
            }

        }

        if (selectedNode == null) {
            String message = "No matching port found in the given WSDL";
            log.error(message);
            throw new ConnectException(message);
        }

        String[] splitTag = selectedNode.getNodeValue().split(":");
        return document.getFirstChild().lookupNamespaceURI(splitTag[0]);
    }


    public static InputStream getSOAPEnvelopAsStreamSource(SOAPEnvelope soapEnvelope) throws ConnectException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        StringWriter stringWriter = null;

        try {

            docBuilder = documentBuilderFactory.newDocumentBuilder();
            stringWriter = new StringWriter();

            String soapEnvelop = soapEnvelope.getBody().toString();
            Document doc = docBuilder.parse(new InputSource(new StringReader(soapEnvelop)));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer trans = transformerFactory.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StreamResult result = new StreamResult(stringWriter);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);

        } catch (Exception e) {
            String message = "Failed to parse SOAPEnvelop request in to StreamSource";
            log.error(message + ":" + e.getMessage());
            throw new ConnectException(e, message);
        } finally {

            try {
                if (stringWriter != null) {
                    stringWriter.close();
                }
            } catch (IOException e) {
                log.warn("Failed to close String writer:" + e.getMessage()); //TODO need to chk handle
            }
        }
        return new ByteArrayInputStream(stringWriter.toString().getBytes());
    }


    public static void validateInputs(RMParameters inputParams) throws ConnectException {

        StringBuffer fields = new StringBuffer();
        boolean valid = true;

        if (inputParams.getWsdlUrl() == null || inputParams.getWsdlUrl().isEmpty()) {
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

        if (!valid) {
            String message = fields + " cannot null";
            log.error(message);
            throw new ConnectException(message);
        }

    }

    public static String getUpdatedFileLocation(RMParameters inputParams) throws ConnectException {

        String baseRetransmissionIntervalQuery = "//beans/*[name()='cxf:bus']/*[name()='cxf:features']/*[name()='wsrm-mgr:reliableMessaging']/*[name()='wsrm-policy:RMAssertion']/*[name()='wsrm-policy:BaseRetransmissionInterval']";
        String acknowledgementIntervalQuery = "//beans/*[name()='cxf:bus']/*[name()='cxf:features']/*[name()='wsrm-mgr:reliableMessaging']/*[name()='wsrm-policy:RMAssertion']/*[name()='wsrm-policy:AcknowledgementInterval']";
        String intraMessageThresholdQuery = "//beans/*[name()='cxf:bus']/*[name()='cxf:features']/*[name()='wsrm-mgr:reliableMessaging']/*[name()='wsrm-mgr:destinationPolicy']/*[name()='wsrm-mgr:acksPolicy']";
        File tempFile;

        try {
            String configurationFilePath = ReliableMessageUtil.class.getResource("/config/client.xml").getFile();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document document = dbf.newDocumentBuilder().parse(new File(configurationFilePath));

            XPathFactory xpf = new XPathFactoryImpl();
            XPath xpath = xpf.newXPath();

            if (inputParams.getAckInterval() != null && !inputParams.getAckInterval().isEmpty()) {
                XPathExpression baseRetransmissionIntervalExpression = xpath.compile(baseRetransmissionIntervalQuery);
                Node baseRetransmissionIntervalNode = (Node) baseRetransmissionIntervalExpression.evaluate(document, XPathConstants.NODE);
                baseRetransmissionIntervalNode.getAttributes().getNamedItem(RMConstants.RE_TRANS_INTERVAL_PROP_NAME).setNodeValue(inputParams.getAckInterval());
            }

            if (inputParams.getRetransmissionInterval() != null && !inputParams.getRetransmissionInterval().isEmpty()) {
                XPathExpression acknowledgementIntervalExpression = xpath.compile(acknowledgementIntervalQuery);
                Node acknowledgementIntervalNode = (Node) acknowledgementIntervalExpression.evaluate(document, XPathConstants.NODE);
                acknowledgementIntervalNode.getAttributes().getNamedItem(RMConstants.ACK_INTERVAL_PROP_NAME).setNodeValue(inputParams.getRetransmissionInterval());
            }

            if (inputParams.getIntraMessageThreshold() != null && !inputParams.getIntraMessageThreshold().isEmpty()) {
                XPathExpression intraMessageThresholdExpression = xpath.compile(intraMessageThresholdQuery);
                Node intraMessageThresholdNode = (Node) intraMessageThresholdExpression.evaluate(document, XPathConstants.NODE);
                intraMessageThresholdNode.getAttributes().getNamedItem(RMConstants.INTRA_MESSAGE_THRESHOLD_PROP_NAME).setNodeValue(inputParams.getIntraMessageThreshold());
            }


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Source xmlSource = new DOMSource(document);
            Result outputTarget = new StreamResult(outputStream);
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
            tempFile = File.createTempFile(RMConstants.TMP_FILE_PREFIX, RMConstants.TMP_FILE_SUFFIX);
            tempFile.deleteOnExit();
            FileOutputStream out = new FileOutputStream(tempFile);
            IOUtils.copy(is, out);

        } catch (Exception e) {
            String message = "Exception occurred while setup the given configurations";
            log.error(message);
            throw new ConnectException(e, message);
        }

        return tempFile.toString();
    }

}
