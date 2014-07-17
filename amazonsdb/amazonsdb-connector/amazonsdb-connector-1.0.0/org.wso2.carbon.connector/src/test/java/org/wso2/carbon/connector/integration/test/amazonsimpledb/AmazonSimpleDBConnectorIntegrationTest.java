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

package org.wso2.carbon.connector.integration.test.amazonsimpledb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.axiom.om.OMElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.xml.sax.SAXException;

public class AmazonSimpleDBConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private static int SLEEP_TIME;
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("amazonsimpledb-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/xml");
        
        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        
        //initializes the sleep time
        SLEEP_TIME = Integer.parseInt(connectorProperties.getProperty("sleepTime"));
    }
    
    /**
     * Positive test case for createDomain method with mandatory parameters.
     */
    
    @Test(priority = 2, description = "AmazonSimpleDB {createDomain} integration test with mandatory parameters.")
    public void testCreateDomainWithMandatoryParameters() throws IOException, JSONException, XMLStreamException {
    
        esbRequestHeadersMap.put("Action", "urn:createDomain");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        generateApiRequest("api_listDomain.json");
        RestResponse<OMElement> apiResponseBeforeCreate =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(!(apiResponseBeforeCreate.getBody().toString().contains("<DomainName>"
                + connectorProperties.getProperty("domainName") + "</DomainName>")));
        sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDomain_mandatory.xml");
        RestResponse<OMElement> apiResponseAfterCreate =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(apiResponseAfterCreate.getBody().toString()
                .contains("<DomainName>" + connectorProperties.getProperty("domainName") + "</DomainName>"));
    }
    
    /**
     * Negative test case for createDomain method.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateDomainWithMandatoryParameters" }, description = "AmazonSimpleDB {createDomain} integration test for negative case.")
    public void testCreateDomainWithNegativeCase() throws IOException, JSONException, XPathExpressionException,
            SAXException, ParserConfigurationException, XMLStreamException {
    
        esbRequestHeadersMap.put("Action", "urn:createDomain");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        generateApiRequest("api_createDomain_negative.json");
        
        RestResponse<OMElement> esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDomain_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 400);
        
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        
        Assert.assertEquals(esbResponse.getHttpStatusCode(), apiResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//Code", esbResponse.getBody()),
                getValueByExpression("//Code", apiResponse.getBody()));
    }
    
    /**
     * Positive test case for listDomains method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateDomainWithNegativeCase" }, description = "AmazonSimpleDB {listDomains} integration test with mandatory parameters.")
    public void testListDomainsMandatoryParameters() throws IOException, JSONException, XPathExpressionException,
            SAXException, ParserConfigurationException, XMLStreamException {
    
        esbRequestHeadersMap.put("Action", "urn:listDomains");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDomains_mandatory.xml");
        generateApiRequest("api_listDomains_mandatory.json");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(getValueByExpression("//ListDomainsResult", apiResponse.getBody()).toString(),
                getValueByExpression("//ListDomainsResult", esbRestResponse.getBody()).toString());
        Assert.assertTrue(apiResponse.getBody().toString()
                .contains("<DomainName>" + connectorProperties.getProperty("domainName") + "</DomainName>"));
        
    }
    
    /**
     * Positive test case for listDomains method with optional parameters.
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(priority = 2, dependsOnMethods = { "testListDomainsMandatoryParameters" }, description = "AmazonSimpleDB {listDomains} integration test with optional parameters.")
    public void testListDomainsOptionalParameters() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:listDomains");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDomains_optional.xml");
        generateApiRequest("api_listDomains_optional.json");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(getValueByExpression("//ListDomainsResult", apiResponse.getBody()).toString(),
                getValueByExpression("//ListDomainsResult", esbRestResponse.getBody()).toString());
        Assert.assertTrue(apiResponse.getBody().toString()
                .contains("<DomainName>" + connectorProperties.getProperty("domainName") + "</DomainName>"));
        
    }
    
    /**
     * Negative test case for listDomains method
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(priority = 2, dependsOnMethods = { "testListDomainsOptionalParameters" }, description = "AmazonSimpleDB {listDomains} integration test negative case.")
    public void testListDomainsNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:listDomains");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDomains_negative.xml");
        generateApiRequest("api_listDomains_negative.json");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(getValueByExpression("//Code/text()", apiResponse.getBody()).toString(),
                getValueByExpression("//Code/text()", esbRestResponse.getBody()).toString());
        Assert.assertEquals(getValueByExpression("//Message/text()", apiResponse.getBody()).toString(),
                getValueByExpression("//Message/text()", esbRestResponse.getBody()).toString());
    }
    
    /**
     * Positive test case for putAttributes method with mandatory parameters.
     * 
     * @throws XMLStreamException
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = { "testListDomainsNegativeCase" }, description = "AmazonSimpleDB {putAttributes} integration test with mandatory parameters.")
    public void testPutAttributesWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:putAttributes");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        RestResponse<OMElement> apiResponseBeforeEsbCall =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(!apiResponseBeforeEsbCall.getBody().toString()
                .contains("<Name>" + connectorProperties.getProperty("attributeName") + "</Name>"));
        Assert.assertTrue(!apiResponseBeforeEsbCall.getBody().toString()
                .contains("<Value>" + connectorProperties.getProperty("attributeValue") + "</Value>"));
        sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_putAttributes_mandatory.xml");
        Thread.sleep(SLEEP_TIME);
        generateApiRequest("api_getAttributes.json");
        RestResponse<OMElement> apiResponseAfterEsbCall =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(apiResponseAfterEsbCall.getBody().toString()
                .contains("<Name>" + connectorProperties.getProperty("attributeName") + "</Name>"));
        Assert.assertTrue(apiResponseAfterEsbCall.getBody().toString()
                .contains("<Value>" + connectorProperties.getProperty("attributeValue") + "</Value>"));
        
    }
    
    /**
     * Positive test case for putAttributes method with optional parameters.
     * 
     * @throws XMLStreamException
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = { "testPutAttributesWithMandatoryParameters" }, description = "AmazonSimpleDB {putAttributes} integration test with optional parameters.")
    public void testPutAttributesWithOptionalParameters() throws IOException, JSONException, XMLStreamException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:putAttributes");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        RestResponse<OMElement> apiResponseBeforeEsbCall =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(!apiResponseBeforeEsbCall.getBody().toString()
                .contains("<Name>" + connectorProperties.getProperty("attributeName2") + "</Name>"));
        Assert.assertTrue(!apiResponseBeforeEsbCall.getBody().toString()
                .contains("<Value>" + connectorProperties.getProperty("attributeValue2") + "</Value>"));
        sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_putAttributes_optional.xml");
        Thread.sleep(SLEEP_TIME);
        generateApiRequest("api_getAttributes.json");
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(apiResponse.getBody().toString()
                .contains("<Name>" + connectorProperties.getProperty("attributeName2") + "</Name>"));
        Assert.assertTrue(apiResponse.getBody().toString()
                .contains("<Value>" + connectorProperties.getProperty("attributeValue2") + "</Value>"));
        
    }
    
    /**
     * Negative test case for putAttributes method.
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = { "testPutAttributesWithOptionalParameters" }, description = "AmazonSimpleDB {putAttributes} integration test with optional parameters.")
    public void testPutAttributesNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:putAttributes");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_putAttributes_negative.xml");
        Thread.sleep(SLEEP_TIME);
        generateApiRequest("api_putAttributes_negative.json");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(getValueByExpression("//Code/text()", apiResponse.getBody()).toString(),
                getValueByExpression("//Code/text()", esbRestResponse.getBody()).toString());
        Assert.assertEquals(getValueByExpression("//Message/text()", apiResponse.getBody()).toString(),
                getValueByExpression("//Message/text()", esbRestResponse.getBody()).toString());
        
    }
    
    /**
     * Positive test case for select method with mandatory parameters.
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(priority = 2, dependsOnMethods = { "testPutAttributesNegativeCase" }, description = "AmazonSimpleDB {select} integration test with mandatory parameters.")
    public void testSelectWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:select");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_select_mandatory.xml");
        Assert.assertTrue(esbRestResponse.getBody().toString()
                .contains("<Name>" + connectorProperties.getProperty("itemName") + "</Name>"));
        generateApiRequest("api_select_mandatory.json");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(getValueByExpression("//SelectResult", apiResponse.getBody()).toString(),
                getValueByExpression("//SelectResult", esbRestResponse.getBody()).toString());
    }
    
    /**
     * Positive test case for select method with optional parameters.
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(priority = 2, dependsOnMethods = { "testSelectWithMandatoryParameters" }, description = "AmazonSimpleDB {select} integration test with optional parameters.")
    public void testSelectWithOptionalParameters() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:select");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_select_optional.xml");
        Assert.assertTrue(esbRestResponse.getBody().toString()
                .contains("<Name>" + connectorProperties.getProperty("itemName") + "</Name>"));
        generateApiRequest("api_select_optional.json");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(getValueByExpression("//SelectResult", apiResponse.getBody()).toString(),
                getValueByExpression("//SelectResult", esbRestResponse.getBody()).toString());
        
    }
    
    /**
     * Negative test case for select method
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(priority = 2, dependsOnMethods = { "testSelectWithOptionalParameters" }, description = "AmazonSimpleDB {select} integration test negative case.")
    public void testSelectNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:select");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_select_negative.xml");
        generateApiRequest("api_select_negative.json");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(getValueByExpression("//Code/text()", apiResponse.getBody()).toString(),
                getValueByExpression("//Code/text()", esbRestResponse.getBody()).toString());
        Assert.assertEquals(getValueByExpression("//Message/text()", apiResponse.getBody()).toString(),
                getValueByExpression("//Message/text()", esbRestResponse.getBody()).toString());
        
    }
    
    /**
     * Positive test case for getAttributes method with mandatory parameters.
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(priority = 2, dependsOnMethods = { "testSelectNegativeCase" }, description = "AmazonSimpleDB {getAttributes} integration test with mandatory parameters.")
    public void testGetAttributesWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:getAttributes");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        generateApiRequest("api_getAttributes.json");
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse<OMElement> esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAttributes_mandatory.xml");
        Assert.assertEquals(getValueByExpression("//GetAttributesResult", apiResponse.getBody()).toString(),
                getValueByExpression("//GetAttributesResult", esbResponse.getBody()).toString());
    }
    
    /**
     * Positive test case for getAttributes method with optional parameters.
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(priority = 2, dependsOnMethods = { "testGetAttributesWithMandatoryParameters" }, description = "AmazonSimpleDB {getAttributes} integration test with optional parameters.")
    public void testGetAttributesWithOptionalParameters() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:getAttributes");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        generateApiRequest("api_getAttributes.json");
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse<OMElement> esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAttributes_optional.xml");
        Assert.assertEquals(getValueByExpression("//GetAttributesResult", apiResponse.getBody()).toString(),
                getValueByExpression("//GetAttributesResult", esbResponse.getBody()).toString());
    }
    
    /**
     * Negative test case for getAttributes method.
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    
    @Test(priority = 2, dependsOnMethods = { "testGetAttributesWithOptionalParameters" }, description = "AmazonSimpleDB {getAttributes} integration test for negative case.")
    public void testGetAttributesWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
    

        esbRequestHeadersMap.put("Action", "urn:getAttributes");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        generateApiRequest("api_getAttributes_negative.json");
        RestResponse<OMElement> esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAttributes_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 400);
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), apiResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//Code", esbResponse.getBody()),
                getValueByExpression("//Code", apiResponse.getBody()));
    }
    
    /**
     * Positive test case for deleteAttributes method with optional parameters.
     * 
     * @throws XMLStreamException
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = { "testGetAttributesWithNegativeCase" }, description = "AmazonSimpleDB {deleteAttributes} integration test with optional parameters.")
    public void testDeleteAttributesWithOptionalparameters() throws IOException, JSONException, XMLStreamException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAttributes");
        generateApiRequest("api_getAttributes.json");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        RestResponse<OMElement> apiResponseBeforeDelete =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAttributes_optional.xml");
        String attributeName = connectorProperties.getProperty("attributeName2");
        // Compare attribute lists before and after the ESB call of deleteAttributes
        Assert.assertTrue(apiResponseBeforeDelete.getBody().toString()
                .contains("<Attribute><Name>" + attributeName + "</Name>"));
        Thread.sleep(SLEEP_TIME);
        RestResponse<OMElement> apiResponseAfterDelete =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(!apiResponseAfterDelete.getBody().toString()
                .contains("<Attribute><Name>" + attributeName + "</Name>"));
    }
    
    /**
     * Positive test case for deleteAttributes method with mandatory parameters.
     * 
     * @throws XMLStreamException
     * @throws InterruptedException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(priority = 2, dependsOnMethods = { "testDeleteAttributesWithOptionalparameters" }, description = "AmazonSimpleDB {deleteAttributes} integration test with mandatory parameters.")
    public void testDeleteAttributesWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InterruptedException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAttributes");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        generateApiRequest("api_getAttributes.json");
        RestResponse<OMElement> apiResponseBeforeDelete =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        String attributeName = connectorProperties.getProperty("attributeName");
        Assert.assertTrue(apiResponseBeforeDelete.getBody().toString()
                .contains("<Attribute><Name>" + attributeName + "</Name>"));
        sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAttributes_mandatory.xml");
        // Waits before two quick direct API calls to avoid unexpected behaviors
        Thread.sleep(SLEEP_TIME);
        RestResponse<OMElement> apiResponseAfterDelete =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(!apiResponseAfterDelete.getBody().toString()
                .contains("<Attribute><Name>" + attributeName + "</Name>"));
    }
    
    /**
     * Negative test case for deleteAttributes method.
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(priority = 2, dependsOnMethods = { "testDeleteAttributesWithMandatoryParameters" }, description = "AmazonSimpleDB {deleteAttributes} integration test for negative case.")
    public void testDeleteAttributesWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAttributes");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        generateApiRequest("api_deleteAttributes_negative.json");
        RestResponse<OMElement> esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAttributes_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 400);
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), apiResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//Code", esbResponse.getBody()),
                getValueByExpression("//Code", apiResponse.getBody()));
    }
    
    /**
     * Positive test case for deleteDomain method with mandatory parameters.
     * 
     * @throws XMLStreamException
     */
    
    @Test(priority = 2, dependsOnMethods = { "testDeleteAttributesWithNegativeCase" }, description = "AmazonSimpleDB {deleteDomain} integration test with mandatory parameters.")
    public void testDeleteDomainWithMandatoryParameters() throws IOException, JSONException, XMLStreamException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteDomain");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        generateApiRequest("api_listDomain.json");
        RestResponse<OMElement> apiResponseBeforeDelete =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue((apiResponseBeforeDelete.getBody().toString().contains("<DomainName>"
                + connectorProperties.getProperty("domainName") + "</DomainName>")));
        sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteDomain_mandatory.xml");
        RestResponse<OMElement> apiResponseAfterDelete =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(!(apiResponseAfterDelete.getBody().toString().contains("<DomainName>"
                + connectorProperties.getProperty("domainName") + "</DomainName>")));
    }
    
    /**
     * Negative test case for deleteDomain method.
     * 
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(priority = 2, dependsOnMethods = { "testDeleteDomainWithMandatoryParameters" }, description = "AmazonSimpleDB {deleteDomain} integration test for negative case.")
    public void testDeleteDomainWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteDomain");
        String apiEndPoint = connectorProperties.getProperty("amazonSimpleDBApiUrl");
        generateApiRequest("api_deleteDomain_negative.json");
        RestResponse<OMElement> esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteDomain_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 400);
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), apiResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//Code", esbResponse.getBody()),
                getValueByExpression("//Code", apiResponse.getBody()));
    }
    
    public void generateApiRequest(String signatureRequestFile) throws IOException, JSONException {
    
        String requestData;
        String xFormUrlRequest;
        AmazonSimpleDBAuthConnector authConnector = new AmazonSimpleDBAuthConnector();
        
        String signatureRequestFilePath =
                ProductConstant.SYSTEM_TEST_SETTINGS_LOCATION + File.separator + "artifacts" + File.separator + "ESB"
                        + File.separator + "config" + File.separator + "restRequests" + File.separator
                        + "amazonsimpledb" + File.separator + signatureRequestFile;
        
        requestData = loadRequestFromFile(signatureRequestFilePath);
        JSONObject signatureRequestObject = new JSONObject(requestData);
        xFormUrlRequest = authConnector.getXFormUrl(signatureRequestObject);
        connectorProperties.put("xFormUrl", xFormUrlRequest);
        
    }
    
    private String loadRequestFromFile(String requestFileName) throws IOException {
    
        String requestFilePath;
        String requestData;
        requestFilePath = requestFileName;
        requestData = getFileContent(requestFilePath);
        Properties prop = (Properties) connectorProperties.clone();
        
        Matcher matcher = Pattern.compile("%s\\(([A-Za-z0-9]*)\\)", Pattern.DOTALL).matcher(requestData);
        while (matcher.find()) {
            String key = matcher.group(1);
            requestData = requestData.replaceAll("%s\\(" + key + "\\)", prop.getProperty(key));
        }
        return requestData;
    }
    
    private String getFileContent(String path) throws IOException {
    
        String fileContent = null;
        BufferedInputStream bfist = new BufferedInputStream(new FileInputStream(path));
        
        byte[] buf = new byte[bfist.available()];
        bfist.read(buf);
        fileContent = new String(buf);
        
        if (bfist != null) {
            bfist.close();
        }
        return fileContent;
        
    }
    
}
