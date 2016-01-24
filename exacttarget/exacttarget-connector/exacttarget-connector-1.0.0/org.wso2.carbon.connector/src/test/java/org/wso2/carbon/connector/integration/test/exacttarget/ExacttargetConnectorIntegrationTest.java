/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *   
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.wso2.carbon.connector.integration.test.exacttarget;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.jaxen.JaxenException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;

public class ExacttargetConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
   
   Map<String, String> nameSpaceMap = new HashMap<String, String>();
   
   private final String SOAP_HEADER_XPATH_EXP = "/soapenv:Envelope/soapenv:Header/*";
   
   private final String SOAP_BODY_XPATH_EXP = "/soapenv:Envelope/soapenv:Body/*";
   
   private String apiEndPoint = null;
   
   private String currentTimeString = null;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("exacttarget-connector-1.0.0");
      
      apiEndPoint = connectorProperties.getProperty("apiUrl");
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      nameSpaceMap.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
      nameSpaceMap.put("xmlns", "http://exacttarget.com/wsdl/partnerAPI");
      nameSpaceMap.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      
      currentTimeString = String.valueOf(System.currentTimeMillis());
      connectorProperties.setProperty("currentTimeString", currentTimeString);
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      
   }
   
   /**
    * Positive test case for listEmails method with mandatory parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "ExactTarget {listEmails} integration test with mandatory parameters.")
   public void testListEmailsWithMandatoryParameters() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listEmails_mandatory.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForId = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:ID/text())";
      String esbEmailIdString = (String) xPathEvaluate(esbResponseElement, xPathExpForId, nameSpaceMap);
      connectorProperties.setProperty("emailId", esbEmailIdString);
      
      String xPathExpForCreatedDate =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:CreatedDate/text())";
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      
      String esbCreatedDateString = (String) xPathEvaluate(esbResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listEmails_mandatory.xml", null, "Retrieve", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiEmailIdString = (String) xPathEvaluate(apiResponseElement, xPathExpForId, nameSpaceMap);
      String apiCreatedDateString = (String) xPathEvaluate(apiResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      String apiResultsCount = (String) xPathEvaluate(apiResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertEquals(esbEmailIdString, apiEmailIdString);
      Assert.assertEquals(esbCreatedDateString, apiCreatedDateString);
      Assert.assertEquals(esbResultsCount, apiResultsCount);
   }
   
   /**
    * Positive test case for listEmails method with optional parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEmailsWithMandatoryParameters" }, description = "ExactTarget {listEmails} integration test with optional parameters.")
   public void testListEmailsWithOptionalParameters() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listEmails_optional.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForId = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:ID/text())";
      String xPathExpForCreatedDate =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:CreatedDate/text())";
      
      String esbIdString = (String) xPathEvaluate(esbResponseElement, xPathExpForId, nameSpaceMap);
      String esbCreatedDateString = (String) xPathEvaluate(esbResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listEmails_optional.xml", null, "Retrieve", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiCreatedDateString = (String) xPathEvaluate(apiResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      String apiResultsCount = (String) xPathEvaluate(apiResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertEquals(esbIdString, connectorProperties.getProperty("emailId"));
      Assert.assertEquals(esbCreatedDateString, apiCreatedDateString);
      Assert.assertTrue("1".equals(esbResultsCount));
      Assert.assertEquals(esbResultsCount, apiResultsCount);
   }
   
   /**
    * Negative test case for listEmails method, provides an invalid property name to be retrieved.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEmailsWithOptionalParameters" }, description = "ExactTarget {listEmails} integration test with negative case.")
   public void testListEmailsNegativeCase() throws OMException, XMLStreamException, JaxenException, IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listEmails_negative.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatus = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:OverallStatus/text())";
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      
      String esbStatusString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatus, nameSpaceMap);
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertFalse("OK".equals(esbResultsCount));
      Assert.assertTrue(esbStatusString.startsWith("Error:"));
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listEmails_negative.xml", null, "Retrieve", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatus, nameSpaceMap);
      
      Assert.assertEquals(esbStatusString, apiStatusString);
   }
   
   /**
    * Positive test case for listSubscribers method with mandatory parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEmailsNegativeCase" }, description = "ExactTarget {listSubscribers} integration test with mandatory parameters.")
   public void testListSubscribersWithMandatoryParameters() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listSubscribers_mandatory.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathResultsCount, nameSpaceMap);
      
      String xPathExpForSubscriberId = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:ID/text())";
      
      String esbSubscriberIdString = (String) xPathEvaluate(esbResponseElement, xPathExpForSubscriberId, nameSpaceMap);
      
      connectorProperties.setProperty("subscriberId", esbSubscriberIdString);
      
      String xPathExpForSubscriberKey =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:SubscriberKey/text())";
      
      String esbSubscriberKeyString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForSubscriberKey, nameSpaceMap);
      
      connectorProperties.setProperty("subscriberKey", esbSubscriberKeyString);
      
      String xPathExpForCreatedDate =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:CreatedDate/text())";
      String esbCreatedDateString = (String) xPathEvaluate(esbResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listSubscribers_mandatory.xml", null, "Retrieve", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiResultsCount = (String) xPathEvaluate(apiResponseElement, xPathResultsCount, nameSpaceMap);
      String apiListIdString = (String) xPathEvaluate(apiResponseElement, xPathExpForSubscriberId, nameSpaceMap);
      String apiCreatedDateString = (String) xPathEvaluate(apiResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      String apiSubscriberKeyString =
            (String) xPathEvaluate(apiResponseElement, xPathExpForSubscriberKey, nameSpaceMap);
      
      Assert.assertEquals(esbResultsCount, apiResultsCount);
      Assert.assertEquals(esbSubscriberIdString, apiListIdString);
      Assert.assertEquals(esbCreatedDateString, apiCreatedDateString);
      Assert.assertEquals(esbSubscriberKeyString, apiSubscriberKeyString);
      
   }
   
   /**
    * Positive test case for listSubscribers method with optional parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscribersWithMandatoryParameters" }, description = "ExactTarget {listSubscribers} integration test with optional parameters.")
   public void testListSubscribersWithOptionalParameters() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listSubscribers_optional.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathResultsCount, nameSpaceMap);
      
      String xPathExpForSubscriberId = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:ID/text())";
      String esbSubscriberIdString = (String) xPathEvaluate(esbResponseElement, xPathExpForSubscriberId, nameSpaceMap);
      
      String xPathExpForCreatedDate =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:CreatedDate/text())";
      String esbCreatedDateString = (String) xPathEvaluate(esbResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      
      String xPathExpForStatus = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:Status/text())";
      String esbStatusString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatus, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listSubscribers_optional.xml", null, "Retrieve", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiResultsCount = (String) xPathEvaluate(apiResponseElement, xPathResultsCount, nameSpaceMap);
      String apiIdString = (String) xPathEvaluate(apiResponseElement, xPathExpForSubscriberId, nameSpaceMap);
      String apiModifiedDateString = (String) xPathEvaluate(apiResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      String apiStatusString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatus, nameSpaceMap);
      
      Assert.assertEquals(esbResultsCount, apiResultsCount);
      Assert.assertEquals(esbSubscriberIdString, apiIdString);
      Assert.assertEquals(esbCreatedDateString, apiModifiedDateString);
      Assert.assertEquals(esbStatusString, apiStatusString);
      
   }
   
   /**
    * Negative test case for listSubscribers method, provides an invalid property name to be retrieved.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscribersWithOptionalParameters" }, description = "ExactTarget {listSubscribers} integration test negative case.")
   public void testListSubscribersNegativeCase() throws OMException, XMLStreamException, JaxenException, IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listSubscribers_negative.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathOverallStatus = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:OverallStatus/text())";
      String esbOverallStatusString = (String) xPathEvaluate(esbResponseElement, xPathOverallStatus, nameSpaceMap);
      
      Assert.assertNotEquals(esbOverallStatusString, "OK");
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listSubscribers_negative.xml", null, "Retrieve", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiOverallStatusString = (String) xPathEvaluate(apiResponseElement, xPathOverallStatus, nameSpaceMap);
      
      Assert.assertNotEquals(apiOverallStatusString, "OK");
      
      Assert.assertEquals(esbOverallStatusString, apiOverallStatusString);
      
   }
   
   /**
    * Positive test case for createTriggeredSendDefinition method with mandatory parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscribersNegativeCase" }, description = "ExactTarget {createTriggeredSendDefinition} integration test with mandatory parameters.")
   public void testCreateTriggeredSendDefinitionWithMandatoryParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      connectorProperties.setProperty("esbCreateTriggeredSendDefinition", "esbTrgdSendDef_" + currentTimeString);
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_createTriggeredSendDefinition_mandatory.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String esbXPathExpForObjectId =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:Object/xmlns:ObjectID/text())";
      
      String esbObjectIdString = (String) xPathEvaluate(esbResponseElement, esbXPathExpForObjectId, nameSpaceMap);
      
      connectorProperties.setProperty("objectId", esbObjectIdString);
      
      String esbXPathExpForCustomerKey =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:Object/xmlns:CustomerKey/text())";
      String esbCustomerKeyString = (String) xPathEvaluate(esbResponseElement, esbXPathExpForCustomerKey, nameSpaceMap);
      
      connectorProperties.setProperty("triggeredSendDefinitionCustomerKey", esbCustomerKeyString);
      
      String esbXPathExpForName =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:Object/xmlns:Name/text())";
      String esbNameString = (String) xPathEvaluate(esbResponseElement, esbXPathExpForName, nameSpaceMap);
      
      String esbXPathExpForDescription =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:Object/xmlns:Description/text())";
      String esbDescriptionString = (String) xPathEvaluate(esbResponseElement, esbXPathExpForDescription, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_createTriggeredSendDefinition_mandatory.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiXPathExpForCustomerKey =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results/xmlns:CustomerKey/text())";
      String apiCustomerKeyString = (String) xPathEvaluate(apiResponseElement, apiXPathExpForCustomerKey, nameSpaceMap);
      
      String apiXPathExpForName = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results/xmlns:Name/text())";
      String apiNameString = (String) xPathEvaluate(apiResponseElement, apiXPathExpForName, nameSpaceMap);
      
      String apiXPathExpForDescription =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results/xmlns:Description/text())";
      String apiDescriptionString = (String) xPathEvaluate(apiResponseElement, apiXPathExpForDescription, nameSpaceMap);
      
      Assert.assertEquals(esbCustomerKeyString, apiCustomerKeyString);
      Assert.assertEquals(esbNameString, apiNameString);
      Assert.assertEquals(esbDescriptionString, apiDescriptionString);
      
   }
   
   /**
    * Positive test case for createTriggeredSendDefinition method with optional parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTriggeredSendDefinitionWithMandatoryParameters" }, description = "ExactTarget {createTriggeredSendDefinition} integration test with optional parameters.")
   public void testCreateTriggeredSendDefinitionWithOptionalParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      connectorProperties.setProperty("esbCreateTriggeredSendDefinitionOpt", "esbTrgdSendDef_Opt_" + currentTimeString);
      
      connectorProperties.setProperty("apiCreateTriggeredSendDefinitionOpt", "apiTrgdSendDef_Opt_" + currentTimeString);
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_createTriggeredSendDefinition_optional.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatusCode = "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String esbStatusCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatusCode, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, "OK");
      
      String xPathExpForStatusMessage =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusMessage/text())";
      String esbStatusMessageString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals(esbStatusMessageString, "Queued");
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_createTriggeredSendDefinition_optional.xml", null, "Create",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String apiStatusMessageString =
            (String) xPathEvaluate(apiResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, apiStatusCodeString);
      Assert.assertEquals(esbStatusMessageString, apiStatusMessageString);
      
   }
   
   /**
    * Negative test case for createTriggeredSendDefinition method, creating TriggeredSendDefinition with same
    * CustomerKey and Name.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTriggeredSendDefinitionWithOptionalParameters" }, description = "ExactTarget {createTriggeredSendDefinition} integration test negative case.")
   public void testCreateTriggeredSendDefinitionNegativeCase() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      connectorProperties.setProperty("esbCreateTriggeredSendDefinition", "esbTrgdSendDef_" + currentTimeString);
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_createTriggeredSendDefinition_negative.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatusCode = "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String esbStatusCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatusCode, nameSpaceMap);
      
      String xPathExpForStatusMessage =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusMessage/text())";
      String esbStatusMessageString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      String xPathExpForErrorCode = "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:ErrorCode/text())";
      String esbErrorCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForErrorCode, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_createTriggeredSendDefinition_negative.xml", null, "Create",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String apiStatusMessageString =
            (String) xPathEvaluate(apiResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      String apiErrorCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForErrorCode, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, apiStatusCodeString);
      Assert.assertEquals(esbStatusMessageString, apiStatusMessageString);
      Assert.assertEquals(esbErrorCodeString, apiErrorCodeString);
      
   }
   
   /**
    * Positive test case for updateTriggeredSendDefinition method with mandatory parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTriggeredSendDefinitionNegativeCase" }, description = "ExactTarget {updateTriggeredSendDefinition} integration test with mandatory parameters.")
   public void testUpdateTriggeredSendDefinitionWithMandatoryParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      SOAPEnvelope apiSoapResponseBeforeUpdate =
            sendSOAPRequest(apiEndPoint, "api_retrieveTriggeredSendDefinition.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElementBeforeUpdate = AXIOMUtil.stringToOM(apiSoapResponseBeforeUpdate.getBody().toString());
      
      String apiXPathExpForStatus =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results/xmlns:TriggeredSendStatus/text())";
      String apiXPathExpForCustomerKey =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results/xmlns:CustomerKey/text())";
      String esbXPathExpForStatus =
            "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:Object/xmlns:TriggeredSendStatus/text())";
      String esbXPathExpForCustomerKey =
            "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:Object/xmlns:CustomerKey/text())";
      
      String apiStatusCodeStringBeforeUpdate =
            (String) xPathEvaluate(apiResponseElementBeforeUpdate, apiXPathExpForStatus, nameSpaceMap);
      
      String apiCustomerKeyString =
            (String) xPathEvaluate(apiResponseElementBeforeUpdate, apiXPathExpForCustomerKey, nameSpaceMap);
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_updateTriggeredSendDefinition_mandatory.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String esbStatusString = (String) xPathEvaluate(esbResponseElement, esbXPathExpForStatus, nameSpaceMap);
      String esbCustomerKeyString = (String) xPathEvaluate(esbResponseElement, esbXPathExpForCustomerKey, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponseAfterUpdate =
            sendSOAPRequest(apiEndPoint, "api_retrieveTriggeredSendDefinition.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElementAfterUpdate = AXIOMUtil.stringToOM(apiSoapResponseAfterUpdate.getBody().toString());
      
      String apiStatusCodeStringAfterUpdate =
            (String) xPathEvaluate(apiResponseElementAfterUpdate, apiXPathExpForStatus, nameSpaceMap);
      
      Assert.assertNotEquals(apiStatusCodeStringBeforeUpdate, apiStatusCodeStringAfterUpdate);
      Assert.assertEquals(apiStatusCodeStringAfterUpdate, "Active");
      Assert.assertEquals(esbStatusString, apiStatusCodeStringAfterUpdate);
      Assert.assertEquals(esbCustomerKeyString, apiCustomerKeyString);
   }
   
   /**
    * Positive test case for updateTriggeredSendDefinition method with optional parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateTriggeredSendDefinitionWithMandatoryParameters" }, description = "ExactTarget {updateTriggeredSendDefinition} integration test with optional parameters.")
   public void testUpdateTriggeredSendDefinitionWithOptionalParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      connectorProperties.setProperty("esbUpdateTriggeredSendDefinitionConversationID",
            "esbSendTriggeredSendDefinition_" + currentTimeString);
      connectorProperties.setProperty("apiUpdateTriggeredSendDefinitionConversationID",
            "apiSendTriggeredSendDefinition_" + currentTimeString);
      
      String xPathExpForStatus = "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String xPathExpForStatusMessage =
            "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:StatusMessage/text())";
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_updateTriggeredSendDefinition_optional.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String esbStatusString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatus, nameSpaceMap);
      String esbStatusMessageString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals("OK", esbStatusString);
      Assert.assertEquals("Queued", esbStatusMessageString);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_updateTriggeredSendDefinition_optional.xml", null, "Update",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatus, nameSpaceMap);
      String apiStatusMessageString =
            (String) xPathEvaluate(apiResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals(esbStatusString, apiStatusString);
      Assert.assertEquals(esbStatusMessageString, apiStatusMessageString);
   }
   
   /**
    * Negative test case for updateTriggeredSendDefinition method, provides an invalid Customer Key.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateTriggeredSendDefinitionWithOptionalParameters" }, description = "ExactTarget {updateTriggeredSendDefinition} integration test with negative case.")
   public void testUpdateTriggeredSendDefinitionNegativeCase() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      String xPathExpForStatus = "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String xPathExpForErrorCode = "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:ErrorCode/text())";
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_updateTriggeredSendDefinition_negative.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String esbStatusString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatus, nameSpaceMap);
      String esbErrorCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForErrorCode, nameSpaceMap);
      
      Assert.assertEquals("Error", esbStatusString);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_updateTriggeredSendDefinition_negative.xml", null, "Update",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatus, nameSpaceMap);
      String apiErrorCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForErrorCode, nameSpaceMap);
      
      Assert.assertEquals(esbStatusString, apiStatusString);
      Assert.assertEquals(esbErrorCodeString, apiErrorCodeString);
   }
   
   /**
    * Positive test case for updateSubscriberAttributes method with mandatory parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateTriggeredSendDefinitionNegativeCase" }, description = "ExactTarget {updateSubscriberAttributes} integration test with mandatory parameters.")
   public void testUpdateSubscriberAttributesWithMandatoryParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      connectorProperties.setProperty("esbAttributeValue1", "esbAttributeValue1_" + currentTimeString);
      connectorProperties.setProperty("esbAttributeValue2", "esbAttributeValue2_" + currentTimeString);
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_updateSubscriberAttributes_mandatory.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String esbXPathExpForObjectId =
            "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:Object/xmlns:ID/text())";
      
      String esbSubscriberObjIdString =
            (String) xPathEvaluate(esbResponseElement, esbXPathExpForObjectId, nameSpaceMap);
      
      connectorProperties.setProperty("SubscriberObjId", esbSubscriberObjIdString);
      
      String esbXPathExpForSubscriberAttr1 =
            "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:Object/xmlns:Attributes[xmlns:Name = '"
                  + connectorProperties.getProperty("subscriberAtr1") + "']/xmlns:Value/text())";
      String esbSubscriberAttr1String =
            (String) xPathEvaluate(esbResponseElement, esbXPathExpForSubscriberAttr1, nameSpaceMap);
      
      String esbXPathExpForSubscriberAttr2 =
            "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:Object/xmlns:Attributes[xmlns:Name = '"
                  + connectorProperties.getProperty("subscriberAtr2") + "']/xmlns:Value/text())";
      String esbSubscriberAttr2String =
            (String) xPathEvaluate(esbResponseElement, esbXPathExpForSubscriberAttr2, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_updateSubscriberAttributes_mandatory.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiXPathExpForId = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results/xmlns:ID/text())";
      
      String apiSubscriberObjIdString = (String) xPathEvaluate(apiResponseElement, apiXPathExpForId, nameSpaceMap);
      
      String apiXPathExpForSubscriberAttr1 =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results/xmlns:Attributes[xmlns:Name = '"
                  + connectorProperties.getProperty("subscriberAtr1") + "']/xmlns:Value/text())";
      
      String apiSubscriberAttr1String =
            (String) xPathEvaluate(apiResponseElement, apiXPathExpForSubscriberAttr1, nameSpaceMap);
      
      String apiXPathExpForSubscriberAttr2 =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results/xmlns:Attributes[xmlns:Name = '"
                  + connectorProperties.getProperty("subscriberAtr2") + "']/xmlns:Value/text())";
      
      String apiSubscriberAttr2String =
            (String) xPathEvaluate(apiResponseElement, apiXPathExpForSubscriberAttr2, nameSpaceMap);
      
      Assert.assertEquals(esbSubscriberObjIdString, apiSubscriberObjIdString);
      Assert.assertEquals(esbSubscriberAttr1String, apiSubscriberAttr1String);
      Assert.assertEquals(esbSubscriberAttr2String, apiSubscriberAttr2String);
      
   }
   
   /**
    * Positive test case for updateSubscriberAttributes method with optional parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateSubscriberAttributesWithMandatoryParameters" }, description = "ExactTarget {updateSubscriberAttributes} integration test with optional parameters.")
   public void testUpdateSubscriberAttributesWithOptionalParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      connectorProperties.setProperty("esbUpdateSubscriberAttributes", "esbUpdateSubscriberAttributes_"
            + currentTimeString);
      connectorProperties.setProperty("apiUpdateSubscriberAttributes", "apiUpdateSubscriberAttributes_"
            + currentTimeString);
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_updateSubscriberAttributes_optional.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatusCode = "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String esbStatusCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatusCode, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, "OK");
      
      String xPathExpForStatusMessage =
            "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:StatusMessage/text())";
      String esbStatusMessageString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals(esbStatusMessageString, "Queued");
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_updateSubscriberAttributes_optional.xml", null, "Update",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String apiStatusMessageString =
            (String) xPathEvaluate(apiResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, apiStatusCodeString);
      Assert.assertEquals(esbStatusMessageString, apiStatusMessageString);
      
   }
   
   /**
    * Negative test case for updateSubscriberAttributes method, provides an invalid SubscriberKey.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateSubscriberAttributesWithOptionalParameters" }, description = "ExactTarget {updateSubscriberAttributes} integration test negative case.")
   public void testUpdateSubscriberAttributesNegativeCase() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_updateSubscriberAttributes_negative.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatusCode = "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String esbStatusCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatusCode, nameSpaceMap);
      
      String xPathExpForStatusMessage =
            "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:StatusMessage/text())";
      String esbStatusMessageString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      String xPathExpForErrorCode = "string(//soap:Body/xmlns:UpdateResponse/xmlns:Results/xmlns:ErrorCode/text())";
      String esbErrorCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForErrorCode, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_updateSubscriberAttributes_negative.xml", null, "Update",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String apiStatusMessageString =
            (String) xPathEvaluate(apiResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      String apiErrorCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForErrorCode, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, apiStatusCodeString);
      Assert.assertEquals(esbStatusMessageString, apiStatusMessageString);
      Assert.assertEquals(esbErrorCodeString, apiErrorCodeString);
      
   }
   
   /**
    * Positive test case for sendTriggeredEmail method with mandatory parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateSubscriberAttributesNegativeCase" }, description = "ExactTarget {sendTriggeredEmail} integration test with mandatory parameters.")
   public void testSendTriggeredEmailWithMandatoryParameters() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_sendTriggeredEmail_mandatory.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatusCode = "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String xPathExpForStatusMessage =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusMessage/text())";
      
      String esbStatusCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String esbStatusMessageString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals("OK", esbStatusCodeString);
      Assert.assertEquals("Created TriggeredSend", esbStatusMessageString);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_sendTriggeredEmail_mandatory.xml", null, "Create", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String apiStatusMessageString =
            (String) xPathEvaluate(apiResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, apiStatusCodeString);
      Assert.assertEquals(esbStatusMessageString, apiStatusMessageString);
   }
   
   /**
    * Positive test case for sendTriggeredEmail method with optional parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendTriggeredEmailWithMandatoryParameters" }, description = "ExactTarget {sendTriggeredEmail} integration test with optional parameters.")
   public void testSendTriggeredEmailOptionalParameters() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      connectorProperties.setProperty("esbSendTriggeredEmailConversationID", "esbSendTriggeredEmail_"
            + currentTimeString);
      connectorProperties.setProperty("apiSendTriggeredEmailConversationID", "apiSendTriggeredEmail_"
            + currentTimeString);
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_sendTriggeredEmail_optional.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatusCode = "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String xPathExpForStatusMessage =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusMessage/text())";
      
      String esbStatusCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String esbStatusMessageString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals("OK", esbStatusCodeString);
      Assert.assertEquals("Queued", esbStatusMessageString);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_sendTriggeredEmail_optional.xml", null, "Create", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String apiStatusMessageString =
            (String) xPathEvaluate(apiResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, apiStatusCodeString);
      Assert.assertEquals(esbStatusMessageString, apiStatusMessageString);
      
   }
   
   /**
    * Negative test case for sendTriggeredEmail method, provides an invalid Customer Key.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendTriggeredEmailOptionalParameters" }, description = "ExactTarget {sendTriggeredEmail} integration test with negative case.")
   public void testSendTriggeredEmailNegativeCase() throws OMException, XMLStreamException, JaxenException, IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_sendTriggeredEmail_negative.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatusCode = "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String xPathExpForErrorCode = "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:ErrorCode/text())";
      
      String esbStatusCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String esbErrorCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForErrorCode, nameSpaceMap);
      
      Assert.assertEquals("Error", esbStatusCodeString);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_sendTriggeredEmail_negative.xml", null, "Create", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String apiErrorCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForErrorCode, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, apiStatusCodeString);
      Assert.assertEquals(esbErrorCodeString, apiErrorCodeString);
   }
   
   /**
    * Positive test case for insertDataToDataExtension method with mandatory parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendTriggeredEmailNegativeCase" }, description = "ExactTarget {insertDataToDataExtension} integration test with mandatory parameters.")
   public void testInsertDataToDataExtensionWithMandatoryParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      connectorProperties.setProperty("esbEmailIdString", "esbEmailIdString_" + currentTimeString);
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_insertDataToDataExtension_mandatory.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatusCode = "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String xPathExpForStatusMessage =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusMessage/text())";
      
      String esbXpathExpForResultsFieldOne =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:Object/xmlns:Properties/xmlns:Property[xmlns:Name='"
                  + connectorProperties.getProperty("dataExtField1") + "']/xmlns:Value/text())";
      String esbXpathExpForResultsFieldTwo =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:Object/xmlns:Properties/xmlns:Property[xmlns:Name='"
                  + connectorProperties.getProperty("dataExtField2") + "']/xmlns:Value/text())";
      
      String esbFieldOneValueString =
            (String) xPathEvaluate(esbResponseElement, esbXpathExpForResultsFieldOne, nameSpaceMap);
      String esbFieldTwoValueString =
            (String) xPathEvaluate(esbResponseElement, esbXpathExpForResultsFieldTwo, nameSpaceMap);
      
      String esbStatusCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String esbStatusMessageString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      connectorProperties.setProperty("fieldOneVlaue", esbFieldOneValueString);
      
      Assert.assertEquals("OK", esbStatusCodeString);
      Assert.assertEquals("Created DataExtensionObject", esbStatusMessageString);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listDataExtensionObject_mandatory.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiXpathExpForResultsFieldOne =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results)]/xmlns:Properties/xmlns:Property[1]/xmlns:Value)";
      String apiXPathExpForResultsFieldTwo =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results)]/xmlns:Properties/xmlns:Property[2]/xmlns:Value)";
      
      String apiResultsFieldOneValue =
            (String) xPathEvaluate(apiResponseElement, apiXpathExpForResultsFieldOne, nameSpaceMap);
      String apiResultsFieldTwoValue =
            (String) xPathEvaluate(apiResponseElement, apiXPathExpForResultsFieldTwo, nameSpaceMap);
      
      Assert.assertEquals(esbFieldOneValueString, apiResultsFieldOneValue);
      Assert.assertEquals(esbFieldTwoValueString, apiResultsFieldTwoValue);
   }
   
   /**
    * Positive test case for insertDataToDataExtension method with optional parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testInsertDataToDataExtensionWithMandatoryParameters" }, description = "ExactTarget {insertDataToDataExtension} integration test with optional parameters.")
   public void testInsertDataToDataExtensionWithOptionalParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      connectorProperties.setProperty("esbDataExtensionConversationId", "esbDataExtensionConversationId_"
            + currentTimeString);
      
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_insertDataToDataExtension_optional.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatusCode = "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String xPathExpForStatusMessage =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusMessage/text())";
      
      String esbStatusCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String esbStatusMessageString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_insertDataToDataExtension_optional.xml", null, "Create",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String apiStatusMessageString =
            (String) xPathEvaluate(apiResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, apiStatusCodeString);
      Assert.assertEquals(esbStatusMessageString, apiStatusMessageString);
   }
   
   /**
    * Negative test case for insertDataToDataExtension method, provides an invalid property name.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testInsertDataToDataExtensionWithOptionalParameters" }, description = "ExactTarget {insertDataToDataExtension} integration test with negative case.")
   public void testInsertDataToDataExtensionWithNegativeCase() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_insertDataToDataExtension_negative.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatusCode = "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusCode/text())";
      String xPathExpForStatusMessage =
            "string(//soap:Body/xmlns:CreateResponse/xmlns:Results/xmlns:StatusMessage/text())";
      
      String esbStatusCodeString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String esbStatusMessageString =
            (String) xPathEvaluate(esbResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_insertDataToDataExtension_negative.xml", null, "Create",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusCodeString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatusCode, nameSpaceMap);
      String apiStatusMessageString =
            (String) xPathEvaluate(apiResponseElement, xPathExpForStatusMessage, nameSpaceMap);
      
      Assert.assertEquals(esbStatusCodeString, apiStatusCodeString);
      Assert.assertEquals(esbStatusMessageString, apiStatusMessageString);
   }
   
   /**
    * Positive test case for listDataExtensionObjects method with mandatory parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testInsertDataToDataExtensionWithNegativeCase" }, description = "ExactTarget {listDataExtensionObjects} integration test with mandatory parameters.")
   public void testListDataExtensionObjectsWithMandatoryParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listDataExtentionObjects_mandatory.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForFieldOne =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:Properties/xmlns:Property[ xmlns:Name='"
                  + connectorProperties.getProperty("dataExtField1") + "']/xmlns:Value/text())";
      String esbFieldOneValue = (String) xPathEvaluate(esbResponseElement, xPathExpForFieldOne, nameSpaceMap);
      
      String xPathExpForFieldTwo =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:Properties/xmlns:Property[ xmlns:Name='"
                  + connectorProperties.getProperty("dataExtField2") + "']/xmlns:Value/text())";
      String esbFieldTwoValue = (String) xPathEvaluate(esbResponseElement, xPathExpForFieldTwo, nameSpaceMap);
      
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listDataExtensionObjects_mandatory.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiFieldOneValue = (String) xPathEvaluate(apiResponseElement, xPathExpForFieldOne, nameSpaceMap);
      String apiFieldTwoValue = (String) xPathEvaluate(esbResponseElement, xPathExpForFieldTwo, nameSpaceMap);
      String apiResultsCount = (String) xPathEvaluate(apiResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertEquals(esbFieldOneValue, apiFieldOneValue);
      Assert.assertEquals(esbFieldTwoValue, apiFieldTwoValue);
      Assert.assertEquals(esbResultsCount, apiResultsCount);
   }
   
   /**
    * Positive test case for listDataExtensionObjects method with optional parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDataExtensionObjectsWithMandatoryParameters" }, description = "ExactTarget {listDataExtensionObjects} integration test with optional parameters.")
   public void testListDataExtensionObjectsWithOptionalParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listDataExtensionObjects_optional.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForFieldOne =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:Properties/xmlns:Property[ xmlns:Name='"
                  + connectorProperties.getProperty("dataExtField1") + "']/xmlns:Value/text())";
      String esbfieldOneValue = (String) xPathEvaluate(esbResponseElement, xPathExpForFieldOne, nameSpaceMap);
      
      String xPathExpForFieldTwo =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:Properties/xmlns:Property[ xmlns:Name='"
                  + connectorProperties.getProperty("dataExtField2") + "']/xmlns:Value/text())";
      String esbFieldTwoValue = (String) xPathEvaluate(esbResponseElement, xPathExpForFieldTwo, nameSpaceMap);
      
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listDataExtensionObjects_optional.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiFieldOneValue = (String) xPathEvaluate(apiResponseElement, xPathExpForFieldOne, nameSpaceMap);
      String apiFieldTwoValue = (String) xPathEvaluate(esbResponseElement, xPathExpForFieldTwo, nameSpaceMap);
      String apiResultsCount = (String) xPathEvaluate(apiResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertEquals(esbfieldOneValue, apiFieldOneValue);
      Assert.assertEquals(esbFieldTwoValue, apiFieldTwoValue);
      Assert.assertEquals(esbResultsCount, apiResultsCount);
   }
   
   /**
    * Negative test case for listDataExtensionObjects method, provides an invalid property name to be
    * retrieved.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDataExtensionObjectsWithOptionalParameters" }, description = "ExactTarget {listDataExtensionObjects} integration test with negative case.")
   public void testListDataExtensionObjectsWithNegativeCase() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listDataExtensionObjects_negative.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatus = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:OverallStatus/text())";
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      
      String esbStatusString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatus, nameSpaceMap);
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertFalse("OK".equals(esbResultsCount));
      Assert.assertTrue(esbStatusString.startsWith("Error:"));
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listDataExtensionObjects_negative.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatus, nameSpaceMap);
      
      Assert.assertEquals(esbStatusString, apiStatusString);
   }
   
   /**
    * Positive test case for listOpenEvents method with mandatory parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDataExtensionObjectsWithNegativeCase" }, description = "ExactTarget {listOpenEvents} integration test with mandatory parameters.")
   public void testListOpenEventsWithMandatoryParameters() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listOpenEvents_mandatory.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForId = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:ID/text())";
      String xpathExpForCreatedDate =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:CreatedDate/text())";
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      
      String esbIdString = (String) xPathEvaluate(esbResponseElement, xPathExpForId, nameSpaceMap);
      
      connectorProperties.setProperty("ObjIdString", esbIdString);
      
      String esbCreatedDateString = (String) xPathEvaluate(esbResponseElement, xpathExpForCreatedDate, nameSpaceMap);
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listOpenEvents_mandatory.xml", null, "Retrieve", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiIdString = (String) xPathEvaluate(apiResponseElement, xPathExpForId, nameSpaceMap);
      String apiCreatedDateString = (String) xPathEvaluate(apiResponseElement, xpathExpForCreatedDate, nameSpaceMap);
      String apiResultsCount = (String) xPathEvaluate(apiResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertEquals(esbIdString, apiIdString);
      Assert.assertEquals(esbCreatedDateString, apiCreatedDateString);
      Assert.assertEquals(esbResultsCount, apiResultsCount);
   }
   
   /**
    * Positive test case for listOpenEvents method with optional parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListOpenEventsWithMandatoryParameters" }, description = "ExactTarget {listOpenEvents} integration test with optional parameters.")
   public void testListOpenEventsWithOptionalParameters() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listOpenEvents_optional.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForId = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:ID/text())";
      String xpathExpForCreatedDate =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:CreatedDate/text())";
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      
      String esbIdString = (String) xPathEvaluate(esbResponseElement, xPathExpForId, nameSpaceMap);
      String esbCreatedDateString = (String) xPathEvaluate(esbResponseElement, xpathExpForCreatedDate, nameSpaceMap);
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listOpenEvents_optional.xml", null, "Retrieve", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiCreatedDateString = (String) xPathEvaluate(apiResponseElement, xpathExpForCreatedDate, nameSpaceMap);
      String apiResultsCount = (String) xPathEvaluate(apiResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertEquals(esbIdString, connectorProperties.getProperty("openEventId"));
      Assert.assertEquals(esbCreatedDateString, apiCreatedDateString);
      Assert.assertTrue("1".equals(esbResultsCount));
      Assert.assertEquals(esbResultsCount, apiResultsCount);
   }
   
   /**
    * Negative test case for listOpenEvents method, provides an invalid property name to be retrieved.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListOpenEventsWithOptionalParameters" }, description = "ExactTarget {listOpenEvents} integration test with negative case.")
   public void testListOpenEventsWithNegativeCase() throws OMException, XMLStreamException, JaxenException, IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listOpenEvents_negative.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatus = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:OverallStatus/text())";
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      
      String esbStatusString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatus, nameSpaceMap);
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertFalse("OK".equals(esbResultsCount));
      Assert.assertTrue(esbStatusString.startsWith("Error:"));
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listOpenEvents_negative.xml", null, "Retrieve", SOAP_HEADER_XPATH_EXP,
                  SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatus, nameSpaceMap);
      
      Assert.assertEquals(esbStatusString, apiStatusString);
   }
   
   /**
    * Positive test case for listSendClassifications method with mandatory parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListOpenEventsWithNegativeCase" }, description = "ExactTarget {listSendClassifications} integration test with mandatory parameters.")
   public void testListSendClassificationsWithMandatoryParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listSendClassifications_mandatory.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForCustomerKey =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:CustomerKey/text())";
      String esbCustomerKeyString = (String) xPathEvaluate(esbResponseElement, xPathExpForCustomerKey, nameSpaceMap);
      
      connectorProperties.setProperty("customerKeyString", esbCustomerKeyString);
      
      String xPathExpForCreatedDate =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:CreatedDate/text())";
      
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      
      String esbCreatedDateString = (String) xPathEvaluate(esbResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listSendClassifications_mandatory.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiCustomerKeyString = (String) xPathEvaluate(apiResponseElement, xPathExpForCustomerKey, nameSpaceMap);
      String apiCreatedDateString = (String) xPathEvaluate(apiResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      String apiResultsCount = (String) xPathEvaluate(apiResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertEquals(esbCustomerKeyString, apiCustomerKeyString);
      Assert.assertEquals(esbCreatedDateString, apiCreatedDateString);
      Assert.assertEquals(esbResultsCount, apiResultsCount);
   }
   
   /**
    * Positive test case for listSendClassifications method with optional parameters.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSendClassificationsWithMandatoryParameters" }, description = "ExactTarget {listSendClassifications} integration test with optional parameters.")
   public void testListSendClassificationsWithOptionalParameters() throws OMException, XMLStreamException,
         JaxenException, IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listSendClassifications_optional.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForCustomerKey =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:CustomerKey/text())";
      
      String esbCustomerKeyString = (String) xPathEvaluate(esbResponseElement, xPathExpForCustomerKey, nameSpaceMap);
      
      String xPathExpForCreatedDate =
            "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results[1]/xmlns:CreatedDate/text())";
      
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      
      String esbCreatedDateString = (String) xPathEvaluate(esbResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listSendClassifications_optional.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiCreatedDateString = (String) xPathEvaluate(apiResponseElement, xPathExpForCreatedDate, nameSpaceMap);
      String apiResultsCount = (String) xPathEvaluate(apiResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertEquals(esbCustomerKeyString, connectorProperties.getProperty("customerKeyString"));
      Assert.assertEquals(esbCreatedDateString, apiCreatedDateString);
      Assert.assertTrue("1".equals(esbResultsCount));
      Assert.assertEquals(esbResultsCount, apiResultsCount);
   }
   
   /**
    * Negative test case for listSendClassifications method, provides an invalid property name to be
    * retrieved.
    * 
    * @throws OMException throws if OM Exception occurred
    * @throws XMLStreamException throws if xml is not well-formed as well as unexpected processing conditions
    * @throws JaxenException throws if Jaxen Exception occurred
    * @throws IOException throws if failed or interrupted I/O operations
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSendClassificationsWithOptionalParameters" }, description = "ExactTarget {listSendClassifications} integration test with negative case.")
   public void testListSendClassificationsWithNegativeCase() throws OMException, XMLStreamException, JaxenException,
         IOException {
   
      SOAPEnvelope esbSoapResponse =
            sendSOAPRequest(proxyUrl, "esb_listSendClassifications_negative.xml", null, "mediate",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
      
      String xPathExpForStatus = "string(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:OverallStatus/text())";
      String xPathExpForResultsCount = "string(count(//soap:Body/xmlns:RetrieveResponseMsg/xmlns:Results))";
      
      String esbStatusString = (String) xPathEvaluate(esbResponseElement, xPathExpForStatus, nameSpaceMap);
      String esbResultsCount = (String) xPathEvaluate(esbResponseElement, xPathExpForResultsCount, nameSpaceMap);
      
      Assert.assertFalse("OK".equals(esbResultsCount));
      Assert.assertTrue(esbStatusString.startsWith("Error:"));
      
      SOAPEnvelope apiSoapResponse =
            sendSOAPRequest(apiEndPoint, "api_listSendClassifications_negative.xml", null, "Retrieve",
                  SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
      OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
      
      String apiStatusString = (String) xPathEvaluate(apiResponseElement, xPathExpForStatus, nameSpaceMap);
      
      Assert.assertEquals(esbStatusString, apiStatusString);
   }
}
