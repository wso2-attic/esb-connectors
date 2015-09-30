/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.connector.integration.test.salesforcebulk;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.xml.sax.SAXException;

public class SalesforceBulkConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiUrl;
    
    private String invalidId="INVALID";
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("salesforcebulk-connector-1.0.0");
        String apiVersion = connectorProperties.getProperty("apiVersion");
        apiUrl = connectorProperties.getProperty("apiUrl") + "/services/async/" + apiVersion;
        
        esbRequestHeadersMap.put("Content-Type", "application/xml");
        apiRequestHeadersMap.put("X-SFDC-Session", connectorProperties.getProperty("accessToken"));
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        
    }
    
    /**
     * Positive test case for createJob method with mandatory parameters.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {createTask} integration test with mandatory parameters.")
    public void testCreateJobWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        String operation = "insert";
        String object = "Contact";
        
        esbRequestHeadersMap.put("Action", "urn:createJob");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createJob_mandatory.xml");
        String jobId =
                getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='id']/text()", esbRestResponse
                        .getBody());
        connectorProperties.setProperty("jobIdWithXMLContent", jobId);
        final String apiEndPoint = apiUrl + "/job/" + jobId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='operation']/text()",
                apiRestResponse.getBody()), operation);
        Assert.assertEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='object']/text()",
                apiRestResponse.getBody()), object);
        
    }
    
    /**
     * Positive test case for createJob method with optional parameters.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {createTask} integration test with optional parameters.")
    public void testCreateJobWithOptionalParameters() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        String operation = "insert";
        String object = "Contact";
        String contentType = "CSV";
        
        esbRequestHeadersMap.put("Action", "urn:createJob");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createJob_optional.xml");
        String jobId =
                getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='id']/text()", esbRestResponse
                        .getBody());
        connectorProperties.setProperty("jobIdWithCSVContent", jobId);
        final String apiEndPoint = apiUrl + "/job/" + jobId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='operation']/text()",
                apiRestResponse.getBody()), operation);
        Assert.assertEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='object']/text()",
                apiRestResponse.getBody()), object);
        Assert.assertEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='contentType']/text()",
                apiRestResponse.getBody()), contentType);
        
    }
    
    /**
     * Negative test case for createJob method.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {createTask} integration test with negative case.")
    public void testCreateJobWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createJob");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createJob_negative.xml");
        
        final String apiEndPoint = apiUrl + "/job";
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createJob_negative.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionCode']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionCode']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionMessage']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionMessage']/text()", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for getJob method with mandatory parameters.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateJobWithOptionalParameters" }, description = "SalesforceBulk {getJob} integration test with mandatory parameters.")
    public void testGetJobWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getJob");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getJob_mandatory.xml");
        
        String jobId = connectorProperties.getProperty("jobIdWithCSVContent");
        final String apiEndPoint = apiUrl + "/job/" + jobId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='operation']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='jobInfo']/*[local-name()='operation']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='object']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='jobInfo']/*[local-name()='object']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='contentType']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='jobInfo']/*[local-name()='contentType']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='state']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='jobInfo']/*[local-name()='state']/text()", apiRestResponse.getBody()));
        
    }
    
    /**
     * Test case: testGetJobWithOptionalParameters. Status: Skipped. Reason : There are no any optional
     * parameters for the function invocation.
     */
    
    /**
     * Negative test case for getJob method.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {getJob} integration test with negative case.")
    public void testGetJobWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getJob");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getJob_negative.xml");
        final String apiEndPoint = apiUrl + "/job/" + invalidId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionCode']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionCode']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionMessage']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionMessage']/text()", apiRestResponse.getBody()));
        
    }
    
    /**
     * Test case: testUpdateJobWithMandatoryParameters. Status: Skipped. Reason : There are no any mandatory
     * parameters for the function invocation.
     */
    
    /**
     * Positive test case for updateJob method with optional parameters.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateJobWithOptionalParameters" }, description = "SalesforceBulk {updateJob} integration test with optional parameters.")
    public void testUpdateJobWithOptionalParameters() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:updateJob");
        
        String closed = "Closed";
        connectorProperties.setProperty("closed", closed);
        
        String jobId = connectorProperties.getProperty("jobIdWithCSVContent");
        final String apiEndPoint = apiUrl + "/job/" + jobId;
        
        RestResponse<OMElement> apiRestResponseBefore = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateJob_optional.xml");
        
        RestResponse<OMElement> apiRestResponseAfter = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='state']/text()",
                apiRestResponseAfter.getBody()), closed);
        Assert.assertNotEquals(getValueByExpression("//*[local-name()='jobInfo']/*[local-name()='state']/text()",
                apiRestResponseBefore.getBody()), getValueByExpression(
                "//*[local-name()='jobInfo']/*[local-name()='state']/text()", apiRestResponseAfter.getBody()));
    }
    
    /**
     * Negative test case for updateJob method.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {updateJob} integration test with negative case.")
    public void testUpdateJobWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:updateJob");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateJob_negative.xml");
        
        final String apiEndPoint = apiUrl + "/job/" + invalidId;
        
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateJob_negative.xml");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionCode']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionCode']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionMessage']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionMessage']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getBatchStatus method with mandatory parameters.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddBatchWithMandatoryParameters" }, description = "SalesforceBulk {getBatchStatus} integration test with mandatory parameters.")
    public void testGetBatchStatusWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getBatchStatus");
        
        String completed = "Completed";
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBatchStatus_mandatory.xml");
        
        String jobId = connectorProperties.getProperty("jobIdWithXMLContent");
        String batchId = connectorProperties.getProperty("batchIdWithXMLContent");
        final String apiEndPoint = apiUrl + "/job/" + jobId + "/batch/" + batchId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//*[local-name()='batchInfo']/*[local-name()='state']/text()",
                esbRestResponse.getBody()), completed);
        
        String esbsystemModstamp=getValueByExpression("//*[local-name()='batchInfo']/*[local-name()='systemModstamp']/text()", esbRestResponse.getBody());
        String apisystemModstamp=getValueByExpression("//*[local-name()='batchInfo']/*[local-name()='systemModstamp']/text()", apiRestResponse.getBody());
        
        Assert.assertEquals(esbsystemModstamp.substring(0,esbsystemModstamp.lastIndexOf(":")),apisystemModstamp.substring(0,apisystemModstamp.lastIndexOf(":")));
    }
    
    /**
     * Test case: testGetBatchStatusWithOptionalParameters. Status: Skipped. Reason : There are no any
     * optional parameters for the function invocation.
     */
    
    /**
     * Negative test case for getBatchStatus method.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateJobWithMandatoryParameters" }, description = "SalesforceBulk {getBatchStatus} integration test with negative case.")
    public void testGetBatchStatusWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getBatchStatus");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBatchStatus_negative.xml");
        
        String jobId = connectorProperties.getProperty("jobIdWithXMLContent");
        final String apiEndPoint = apiUrl + "/job/" + jobId + "/batch/" + invalidId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionCode']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionCode']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionMessage']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionMessage']/text()", apiRestResponse.getBody()));
        
    } 
    
    /**
     * Positive test case for getBatchResults method with mandatory parameters.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddBatchWithMandatoryParameters" }, description = "SalesforceBulk {getBatchResults} integration test with mandatory parameters.")
    public void testGetBatchResultsWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getBatchResults");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBatchResults_mandatory.xml");
        
        String jobId = connectorProperties.getProperty("jobIdWithXMLContent");
        String batchId = connectorProperties.getProperty("batchIdWithXMLContent");
        final String apiEndPoint = apiUrl + "/job/" + jobId + "/batch/" + batchId + "/result";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//*[local-name()='results']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='results']", apiRestResponse.getBody()));
        
        int resultsCount =
                Integer.parseInt(getValueByExpression("count(//*[local-name()='results']/*[local-name()='result'])",
                        esbRestResponse.getBody()));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("count(//*[local-name()='results']/*[local-name()='result'])",
                esbRestResponse.getBody()), getValueByExpression(
                "count(//*[local-name()='results']/*[local-name()='result'])", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='results']/*[local-name()='result'][1]",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='results']/*[local-name()='result'][1]", apiRestResponse.getBody()));
        if (resultsCount > 1) {
            Assert.assertEquals(getValueByExpression("//*[local-name()='results']/*[local-name()='result']["
                    + resultsCount + "]", esbRestResponse.getBody()), getValueByExpression(
                    "//*[local-name()='results']/*[local-name()='result'][" + resultsCount + "]", apiRestResponse
                            .getBody()));
            
        }
    }
    
    /**
     * Test case: testGetBatchResultsWithOptionalParameters. Status: Skipped. Reason : There are no any
     * optional parameters for the function invocation.
     */
    
    /**
     * Negative test case for getBatchResults method.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateJobWithMandatoryParameters" }, description = "SalesforceBulk {getBatchResults} integration test with negative case.")
    public void testGetBatchResultsWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getBatchResults");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBatchResults_negative.xml");
        
        String jobId = connectorProperties.getProperty("jobIdWithXMLContent");
        final String apiEndPoint = apiUrl + "/job/" + jobId + "/batch/" + invalidId + "/result";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionCode']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionCode']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionMessage']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionMessage']/text()", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for getBatchRequest method with mandatory parameters.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddBatchWithMandatoryParameters" }, description = "SalesforceBulk {getBatchRequest} integration test with mandatory parameters.")
    public void testGetBatchRequestWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getBatchRequest");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBatchRequest_mandatory.xml");
        
        String jobId = connectorProperties.getProperty("jobIdWithXMLContent");
        String batchId = connectorProperties.getProperty("batchIdWithXMLContent");
        final String apiEndPoint = apiUrl + "/job/" + jobId + "/batch/" + batchId + "/request";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        int sObjectCount =
                Integer.parseInt(getValueByExpression("count(//*[local-name()='sObjects']/*[local-name()='sObject'])",
                        esbRestResponse.getBody()));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("count(//*[local-name()='sObjects']/*[local-name()='sObject'])",
                esbRestResponse.getBody()), getValueByExpression(
                "count(//*[local-name()='sObjects']/*[local-name()='sObject'])", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(
                "//*[local-name()='sObjects']/*[local-name()='sObject'][1]/*[local-name()='description']",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='sObjects']/*[local-name()='sObject'][1]/*[local-name()='description']",
                apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(
                "//*[local-name()='sObjects']/*[local-name()='sObject'][1]/*[local-name()='name']", esbRestResponse
                        .getBody()), getValueByExpression(
                "//*[local-name()='sObjects']/*[local-name()='sObject'][1]/*[local-name()='name']", apiRestResponse
                        .getBody()));
        if (sObjectCount > 1) {
            Assert.assertEquals(getValueByExpression("//*[local-name()='sObjects']/*[local-name()='sObject']["
                    + sObjectCount + "]/*[local-name()='description']", esbRestResponse.getBody()),
                    getValueByExpression("//*[local-name()='sObjects']/*[local-name()='sObject'][" + sObjectCount
                            + "]/*[local-name()='description']", apiRestResponse.getBody()));
            Assert.assertEquals(getValueByExpression("//*[local-name()='sObjects']/*[local-name()='sObject']["
                    + sObjectCount + "]/*[local-name()='name']", esbRestResponse.getBody()), getValueByExpression(
                    "//*[local-name()='sObjects']/*[local-name()='sObject'][" + sObjectCount
                            + "]/*[local-name()='name']", apiRestResponse.getBody()));
            
        }
    }
    
    /**
     * Test case: testGetBatchRequestWithOptionalParameters. Status: Skipped. Reason : There are no any
     * optional parameters for the function invocation.
     */
    
    /**
     * Negative test case for getBatchRequest method.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateJobWithMandatoryParameters" }, description = "SalesforceBulk {getBatchRequest} integration test with negative case.")
    public void testGetBatchRequestWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getBatchRequest");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBatchRequest_negative.xml");
        
        String jobId = connectorProperties.getProperty("jobIdWithXMLContent");
        final String apiEndPoint = apiUrl + "/job/" + jobId + "/batch/" + invalidId + "/request";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionCode']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionCode']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionMessage']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionMessage']/text()", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for listBatches method with mandatory parameters.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateJobWithMandatoryParameters" }, description = "SalesforceBulk {listBatches} integration test with mandatory parameters.")
    public void testListBatchesWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listBatches");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listBatches_mandatory.xml");
        
        String jobId = connectorProperties.getProperty("jobIdWithXMLContent");
        final String apiEndPoint = apiUrl + "/job/" + jobId + "/batch/";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        int batchCount =
                Integer.parseInt(getValueByExpression(
                        "count(//*[local-name()='batchInfoList']/*[local-name()='batchInfo'])", esbRestResponse
                                .getBody()));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression(
                "count(//*[local-name()='batchInfoList']/*[local-name()='batchInfo'])", esbRestResponse.getBody()),
                getValueByExpression("count(//*[local-name()='batchInfoList']/*[local-name()='batchInfo'])",
                        apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(
                "//*[local-name()='batchInfoList']/*[local-name()='batchInfo'][1]/*[local-name()='jobId']",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='batchInfoList']/*[local-name()='batchInfo'][1]/*[local-name()='jobId']",
                apiRestResponse.getBody()));
        
        if (batchCount > 1) {
            Assert.assertEquals(getValueByExpression("//*[local-name()='batchInfoList']/*[local-name()='batchInfo']["
                    + batchCount + "]/*[local-name()='jobId']", esbRestResponse.getBody()), getValueByExpression(
                    "//*[local-name()='batchInfoList']/*[local-name()='batchInfo'][" + batchCount
                            + "]/*[local-name()='jobId']", apiRestResponse.getBody()));
            
        }
        
    }
    
    /**
     * Test case: testListBatchesWithOptionalParameters. Status: Skipped. Reason : There are no any optional
     * parameters for the function invocation.
     */
    
    /**
     * Negative test case for listBatches method.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {listBatches} integration test with negative case.")
    public void testListBatchesWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listBatches");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listBatches_negative.xml");
        final String apiEndPoint = apiUrl + "/job/" + invalidId + "/batch";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionCode']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionCode']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionMessage']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionMessage']/text()", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for createJobToUploadBatchFile method with mandatory parameters.
     * 
     * @throws XMLStreamException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {createJobToUploadBatchFile} integration test with mandatory parameters.")
    public void testCreateJobToUploadBatchFileWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createJobToUploadBatchFile");
        
        final String jobFileName = connectorProperties.getProperty("jobFileName");
        final String jobContentType = connectorProperties.getProperty("jobContentType");
        
        esbRequestHeadersMap.put("Content-Type", jobContentType);
        
        final String responseString =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&accessToken="
                        + connectorProperties.getProperty("accessToken") + "&apiVersion="
                        + connectorProperties.getProperty("apiVersion");
        
        RestResponse<OMElement> esbRestResponse =
                sendBinaryContentForXmlResponse(responseString, "POST", esbRequestHeadersMap, jobFileName);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        final String jobId = getValueByExpression("//*[local-name()='id']", esbRestResponse.getBody());
        connectorProperties.setProperty("jobIdToUploadBatchFile", jobId);
        
        final String apiEndPoint = apiUrl + "/job/" + jobId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(getValueByExpression("//*[local-name()='operation']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='operation']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='object']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='object']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='contentType']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='contentType']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='createdById']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='createdById']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='state']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='state']/text()", apiRestResponse.getBody()));
        
        esbRequestHeadersMap.put("Content-Type", "application/xml");
    }
    
    /**
     * Test case: testCreateJobToUploadBatchFileWithWithOptionalParameters. Status: Skipped. Reason : There are no optional
     * parameters since method contains only binary attachment.
     */
    
    /**
     * Negative test case for createJobToUploadBatchFile method.
     * 
     * @throws XMLStreamException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {createJobToUploadBatchFile} integration test with negative case.")
    public void testCreateJobToUploadBatchFileWithNegativeCase() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createJobToUploadBatchFile");
        
        final String jobFileName = "empty.txt";
        final String jobContentType = "text/csv";
        
        esbRequestHeadersMap.put("Content-Type", jobContentType);
        
        final String responseString =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&accessToken="
                        + connectorProperties.getProperty("accessToken") + "&apiVersion="
                        + connectorProperties.getProperty("apiVersion");
        
        RestResponse<OMElement> esbRestResponse =
                sendBinaryContentForXmlResponse(responseString, "POST", esbRequestHeadersMap, jobFileName);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        final String apiEndPoint = apiUrl + "/job";
        apiRequestHeadersMap.put("Content-Type", jobContentType);
        RestResponse<OMElement> apiRestResponse =
                sendBinaryContentForXmlResponse(apiEndPoint, "POST", apiRequestHeadersMap, jobFileName);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='exceptionCode']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='exceptionCode']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='exceptionMessage']/text()", esbRestResponse
                .getBody()), getValueByExpression("//*[local-name()='exceptionMessage']/text()", apiRestResponse
                .getBody()));
        
        esbRequestHeadersMap.put("Content-Type", "application/xml");
    }
    
    /**
     * Positive test case for uploadBatchFile method with mandatory parameters.
     * 
     * @throws XMLStreamException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {uploadBatchFile} integration test with mandatory parameters.")
    public void testUploadBatchFileWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException, InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:uploadBatchFile");
        
        String queued = "Queued";
        String completed = "Completed";
        
        final String bathcFileName = connectorProperties.getProperty("batchFileName");
        final String batchContentType = connectorProperties.getProperty("jobContentType");
        
        esbRequestHeadersMap.put("Content-Type", batchContentType);
        
        final String responseString =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&jobId="
                        + connectorProperties.getProperty("CSVJobId") + "&accessToken="
                        + connectorProperties.getProperty("accessToken") + "&apiVersion="
                        + connectorProperties.getProperty("apiVersion");
        
        RestResponse<OMElement> esbRestResponse =
                sendBinaryContentForXmlResponse(responseString, "POST", esbRequestHeadersMap, bathcFileName);
        
        Long timeOut = Long.parseLong(connectorProperties.getProperty("timeOut"));
        Thread.sleep(timeOut);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        final String jobId = connectorProperties.getProperty("CSVJobId");
        final String batchId = getValueByExpression("//*[local-name()='id']", esbRestResponse.getBody());
        
        final String apiEndPoint = apiUrl + "/job/" + jobId + "/batch/" + batchId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(getValueByExpression("//*[local-name()='state']/text()", esbRestResponse.getBody()), queued);
        Assert.assertEquals(getValueByExpression("//*[local-name()='state']/text()", apiRestResponse.getBody()),
                completed);
        
        String esbsystemModstamp=getValueByExpression("//*[local-name()='batchInfo']/*[local-name()='systemModstamp']/text()", esbRestResponse.getBody());
        String apisystemModstamp=getValueByExpression("//*[local-name()='batchInfo']/*[local-name()='systemModstamp']/text()", apiRestResponse.getBody());
        
        Assert.assertEquals(esbsystemModstamp.substring(0,esbsystemModstamp.lastIndexOf(":")),apisystemModstamp.substring(0,apisystemModstamp.lastIndexOf(":")));
 
        esbRequestHeadersMap.put("Content-Type", "application/xml");
        
    }
    
    /**
     * Test case: testUploadBatchFileWithOptionalParameters. Status: Skipped. Reason : There are no optional
     * parameters since method contains only binary attachment.
     */
    
    /**
     * Negative test case for createJobToUploadBatchFile method.
     * 
     * @throws XMLStreamException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {uploadBatchFile} integration test with negative case.")
    public void testUploadBatchFileWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:uploadBatchFile");
        
        final String bathcFileName = connectorProperties.getProperty("batchFileName");
        final String batchContentType = connectorProperties.getProperty("jobContentType");
        
        esbRequestHeadersMap.put("Content-Type", batchContentType);
        
        final String responseString =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&jobId="
                        + invalidId + "&accessToken="
                        + connectorProperties.getProperty("accessToken") + "&apiVersion="
                        + connectorProperties.getProperty("apiVersion");
        
        RestResponse<OMElement> esbRestResponse =
                sendBinaryContentForXmlResponse(responseString, "POST", esbRequestHeadersMap, bathcFileName);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        final String apiEndPoint = apiUrl + "/job/" + invalidId + "/batch";
        apiRequestHeadersMap.put("Content-Type", batchContentType);
        RestResponse<OMElement> apiRestResponse =
                sendBinaryContentForXmlResponse(apiEndPoint, "POST", apiRequestHeadersMap, bathcFileName);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='exceptionCode']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='exceptionCode']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='exceptionMessage']/text()", esbRestResponse
                .getBody()), getValueByExpression("//*[local-name()='exceptionMessage']/text()", apiRestResponse
                .getBody()));
        
        esbRequestHeadersMap.put("Content-Type", "application/xml");
    }
    
    /**
     * Positive test case for addBatch method with mandatory parameters.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateJobWithMandatoryParameters" }, description = "SalesforceBulk {addBatch} integration test with mandatory parameters.")
    public void testAddBatchWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException, InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:addBatch");
        
        String queued = "Queued";
        String completed = "Completed";
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addBatch_mandatory.xml");
        
        String jobId = connectorProperties.getProperty("jobIdWithXMLContent");
        String batchId =
                getValueByExpression("//*[local-name()='batchInfo']/*[local-name()='id']/text()", esbRestResponse
                        .getBody());
        
        connectorProperties.setProperty("batchIdWithXMLContent", batchId);
        final String apiEndPoint = apiUrl + "/job/" + jobId + "/batch/" + batchId;
        
        Long timeOut = Long.parseLong(connectorProperties.getProperty("timeOut"));
        Thread.sleep(timeOut);
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        Assert.assertEquals(getValueByExpression("//*[local-name()='state']/text()", esbRestResponse.getBody()), queued);
        Assert.assertEquals(getValueByExpression("//*[local-name()='state']/text()", apiRestResponse.getBody()),
                completed);
        
        String esbsystemModstamp=getValueByExpression("//*[local-name()='batchInfo']/*[local-name()='systemModstamp']/text()", esbRestResponse.getBody());
        String apisystemModstamp=getValueByExpression("//*[local-name()='batchInfo']/*[local-name()='systemModstamp']/text()", apiRestResponse.getBody());
        
        Assert.assertEquals(esbsystemModstamp.substring(0,esbsystemModstamp.lastIndexOf(":")),apisystemModstamp.substring(0,apisystemModstamp.lastIndexOf(":")));
 
        
    }
    
    /**
     * Test case: testAddBatchWithOptionalParameters. Status: Skipped. Reason : There are no any optional
     * parameters for the function invocation.
     */
    
    /**
     * Negative test case for addBatch method.
     * 
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceBulk {addBatch} integration test with negative case.")
    public void testAddBatchWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:addBatch");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addBatch_negative.xml");
        
        final String apiEndPoint = apiUrl + "/job/" + invalidId + "/batch";
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addBatch_negative.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionCode']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionCode']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/*[local-name()='exceptionMessage']/text()",
                esbRestResponse.getBody()), getValueByExpression(
                "//*[local-name()='error']/*[local-name()='exceptionMessage']/text()", apiRestResponse.getBody()));
        
    }
    
}
