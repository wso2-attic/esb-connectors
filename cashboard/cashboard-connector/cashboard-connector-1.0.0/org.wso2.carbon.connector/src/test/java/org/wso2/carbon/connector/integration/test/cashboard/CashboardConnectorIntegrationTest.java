/*
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
package org.wso2.carbon.connector.integration.test.cashboard;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.codec.binary.Base64;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.xml.sax.SAXException;

public class CashboardConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    private Map<String, String> apiRequestHeadersMap;
    private String apiUrl;
    
    Date today;
    String updatedSince;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("cashboard-connector-1.0.0");
               
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/xml");
        esbRequestHeadersMap.put("Accept", "application/xml");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiUrl = connectorProperties.getProperty("apiUrl");
        
        // call generateApiKey() function to get the generated apiKey
        String apiKey = generateApiKey();
        
        // Generates the Base64 encoded apiKey to be used for the authorization
        // of direct API calls via Authorization header
        final String token = connectorProperties.getProperty("subdomain") + ":" + apiKey;
        byte[] encodedToken = Base64.encodeBase64(token.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedToken));
        
        today = new Date();
        updatedSince = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(today);
        connectorProperties.setProperty("updatedSince", updatedSince);
        
        // creates a new client company to resolve dependencies of test cases
        createClientCompany();
    }
    
    /**
     * generateApiKey method is required to set the environment for the integration by retrieving the apiKey
     * of the account
     * 
     * @throws XMLStreamException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    public String generateApiKey() throws IOException, XMLStreamException, XPathExpressionException, SAXException,
            ParserConfigurationException {
        final String apiEndPoint = apiUrl + "/account/auth";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "POST", apiRequestHeadersMap, "api_createApiKey.xml", null, true);
        String generatedApiKey = getValueByExpression("//output", apiRestResponse.getBody());
        return generatedApiKey;
    }
    
    /**
     * createClientCompany method is required to set the environment for the integration
     * 
     * @throws XMLStreamException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    public void createClientCompany() throws IOException, XMLStreamException, XPathExpressionException, SAXException,
            ParserConfigurationException {
        final String apiEndPoint = apiUrl + "/client_companies.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "POST", apiRequestHeadersMap, "api_createClientCompany.xml", null,
                        true);
        String clientCompanyId = getValueByExpression("//id", apiRestResponse.getBody());
        
        connectorProperties.setProperty("clientCompanyId", clientCompanyId);
    }
    
    /**
     * Positive test case for createClientContact method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {createClientContact} integration test with mandatory parameters.")
    public void testCreateClientContactWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createClientContact");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClientContact_mandatory.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String clientContactId = getValueByExpression("//id", esbRestResponse.getBody());
        final String apiEndPoint = apiUrl + "/client_contacts/" + clientContactId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("//first_name", apiRestResponse.getBody()), connectorProperties
                .getProperty("firstName"));
    }
    
    /**
     * Positive test case for createClientContact method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {createClientContact} integration test with optional parameters.")
    public void testCreateClientContactWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createClientContact");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClientContact_optional.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String clientContactId = getValueByExpression("//id", esbRestResponse.getBody());
        connectorProperties.setProperty("clientContactId", clientContactId);
        final String apiEndPoint = apiUrl + "/client_contacts/" + clientContactId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("//first_name", apiRestResponse.getBody()), connectorProperties
                .getProperty("firstName"));
        Assert.assertEquals(getValueByExpression("//address", apiRestResponse.getBody()), connectorProperties
                .getProperty("address"));
        Assert.assertEquals(getValueByExpression("//city", apiRestResponse.getBody()), connectorProperties
                .getProperty("city"));
        Assert.assertEquals(getValueByExpression("//state", apiRestResponse.getBody()), connectorProperties
                .getProperty("state"));
        Assert.assertEquals(getValueByExpression("//zip", apiRestResponse.getBody()), connectorProperties
                .getProperty("zip"));
        Assert.assertEquals(getValueByExpression("//email_address", apiRestResponse.getBody()), connectorProperties
                .getProperty("clientEmailAddress"));
    }
    
    /**
     * Negative test case for createClientContact method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientContactWithOptionalParameters" }, description = "cashboard {createClientContact} integration test with negative case.")
    public void testCreateClientContactWithNegativeCase() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createClientContact");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClientContact_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        final String apiEndPoint = apiUrl + "/client_contacts.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_createClientContact_negative.xml", null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()), getValueByExpression("//error",
                apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getClientContact method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientContactWithOptionalParameters" }, description = "cashboard {getClientContact} integration test with mandatory parameters.")
    public void testGetClientContactWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getClientContact");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getClientContact_mandatory.xml");
        String clientContactId = connectorProperties.getProperty("clientContactId");
        final String apiEndPoint = apiUrl + "/client_contacts/" + clientContactId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("//first_name", esbRestResponse.getBody()), getValueByExpression(
                "//first_name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//address", esbRestResponse.getBody()), getValueByExpression(
                "//address", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//city", esbRestResponse.getBody()), getValueByExpression("//city",
                apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//state", esbRestResponse.getBody()), getValueByExpression("//state",
                apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//zip", esbRestResponse.getBody()), getValueByExpression("//zip",
                apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//email_address", esbRestResponse.getBody()), getValueByExpression(
                "//email_address", apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testGetClientContactWithOptionalParameters. 
     * Status: Skipped. 
     * Reason : There are no any optional parameters for the function invocation.
     */
    
    /**
     * Negative test case for getClientContact method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {getClientContact} integration test with negative case.")
    public void testGetClientContactWithNegativeCase() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getClientContact");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getClientContact_negative.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        final String apiEndPoint = apiUrl + "/client_contacts/INVALID.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for listClientContact method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientContactWithOptionalParameters" }, description = "cashboard {listClientContacts} integration test with mandatory parameters.")
    public void testListClientContactsWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listClientContacts");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClientContacts_mandatory.xml");
        final String apiEndPoint = apiUrl + "/client_contacts.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("count(//client_contact)", esbRestResponse.getBody()),
                getValueByExpression("count(//client_contact)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//client_contact[0]/first_name", esbRestResponse.getBody()),
                getValueByExpression("//client_contact[0]/first_name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//client_contact[0]/id", esbRestResponse.getBody()),
                getValueByExpression("//client_contact[0]/id", apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testListClientContactsWithOptionalParameters.
     * Status: Skipped.
     * Reason : There are no any optional parameters for the function invocation.
     */
    
    /**
     * Test case: testListClientContactsWithNegativeCase. 
     * Status: Skipped. 
     * Reason : The function invocation has zero parameters other than authentication credentials.
     */
    
    /**
     * Positive test case for addClientToCompany method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientContactWithOptionalParameters" }, description = "cashboard {addClientToCompany} integration test with mandatory parameters.")
    public void testAddClientToCompanyWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:addClientToCompany");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addClientToCompany_mandatory.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String membershipId = getValueByExpression("//id", esbRestResponse.getBody());
        final String apiEndPoint = apiUrl + "/company_memberships/" + membershipId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("//person_id", apiRestResponse.getBody()), connectorProperties
                .getProperty("clientContactId"));
        Assert.assertEquals(getValueByExpression("//company_id", apiRestResponse.getBody()), connectorProperties
                .getProperty("clientCompanyId"));
    }
    
    /**
     * Test case: testAddClientToCompanyWithOptionalParameters. 
     * Status: Skipped. 
     * Reason : The function invocation does not have any optional parameters.
     */
    
    /**
     * Negative test case for addClientToCompany method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddClientToCompanyWithMandatoryParameters" }, description = "cashboard {addClientToCompany} integration test with negative case.")
    public void testAddClientToCompanyWithNegativeCase() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:addClientToCompany");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addClientToCompany_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        String contactId = connectorProperties.getProperty("clientContactId");
        final String apiEndPoint = apiUrl + "/client_contacts/" + contactId + "/memberships.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_addClientToCompany_negative.xml", null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()), getValueByExpression("//error",
                apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for createProjectList method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {createProjectList} integration test with mandatory parameters.")
    public void testCreateProjectListWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createProjectList");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectList_mandatory.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String projectListId = getValueByExpression("//id", esbRestResponse.getBody());
        String apiEndPoint = apiUrl + "/project_lists/" + projectListId + ".xml";
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("//project_id", apiRestResponse.getBody()), connectorProperties
                .getProperty("projectId"));
        
        // deleting the projectList that created with an empty title
        apiEndPoint = apiUrl + "/project_lists/" + projectListId + ".xml";
        sendXmlRestRequestHTTPS(apiEndPoint, "DELETE", apiRequestHeadersMap, null, null, true);
    }
    
    /**
     * Positive test case for createProjectList method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientContactWithOptionalParameters" }, description = "cashboard {createProjectList} integration test with optional parameters.")
    public void testCreateProjectListWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createProjectList");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectList_optional.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String projectListId = getValueByExpression("//id", esbRestResponse.getBody());
        connectorProperties.setProperty("projectListId", projectListId);
        final String apiEndPoint = apiUrl + "/project_lists/" + projectListId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("//is_archived", apiRestResponse.getBody()), connectorProperties
                .getProperty("isArchived"));
        Assert.assertEquals(getValueByExpression("//person_id", apiRestResponse.getBody()), connectorProperties
                .getProperty("clientContactId"));
        Assert.assertEquals(getValueByExpression("//project_id", apiRestResponse.getBody()), connectorProperties
                .getProperty("projectId"));
        Assert.assertEquals(getValueByExpression("//rank", apiRestResponse.getBody()), connectorProperties
                .getProperty("rank"));
        Assert.assertEquals(getValueByExpression("//title", apiRestResponse.getBody()), connectorProperties
                .getProperty("title"));
    }
    
    /**
     * Negative test case for createProjectList method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectListWithOptionalParameters" }, description = "cashboard {createProjectList} integration test with negative case.")
    public void testCreateProjectListWithNegativeCase() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createProjectList");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectList_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        final String apiEndPoint = apiUrl + "/project_lists.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_createProjectList_negative.xml", null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()), getValueByExpression("//error",
                apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getProjectList method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectListWithOptionalParameters" }, description = "cashboard {getProjectList} integration test with mandatory parameters.")
    public void testGetProjectListWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getProjectList");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProjectList_mandatory.xml");
        String projectListId = connectorProperties.getProperty("projectListId");
        final String apiEndPoint = apiUrl + "/project_lists/" + projectListId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("//is_archived", esbRestResponse.getBody()), getValueByExpression(
                "//is_archived", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//person_id", esbRestResponse.getBody()), getValueByExpression(
                "//person_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project_id", esbRestResponse.getBody()), getValueByExpression(
                "//project_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//rank", esbRestResponse.getBody()), getValueByExpression("//rank",
                apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//title", esbRestResponse.getBody()), getValueByExpression("//title",
                apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testGetProjectListWithOptionalParameters. 
     * Status: Skipped. 
     * Reason : The function invocation does not have any optional parameters.
     */
    
    /**
     * Negative test case for getProjectList method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {getProjectList} integration test with negative case.")
    public void testGetProjectListWithNegativeCase() throws XPathExpressionException, XMLStreamException, SAXException,
            IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getProjectList");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProjectList_negative.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        final String apiEndPoint = apiUrl + "/project_lists/INVALID.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for listProjectLists method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectListWithOptionalParameters" }, description = "cashboard {listProjectLists} integration test with mandatory parameters.")
    public void testListProjectListsWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listProjectLists");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjectLists_mandatory.xml");
        final String apiEndPoint = apiUrl + "/project_lists.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("count(//project_list)", esbRestResponse.getBody()),
                getValueByExpression("count(//project_list)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project_list[0]/project_id", esbRestResponse.getBody()),
                getValueByExpression("//project_list[0]/project_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project_list[0]/id", esbRestResponse.getBody()),
                getValueByExpression("//project_list[0]/id", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listProjectLists method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectListWithOptionalParameters" }, description = "cashboard {listProjectLists} integration test with mandatory parameters.")
    public void testListProjectListsWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listProjectLists");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjectLists_optional.xml");
        String isArchived = connectorProperties.getProperty("isArchived");
        String clientContactId = connectorProperties.getProperty("clientContactId");
        String projectId = connectorProperties.getProperty("projectId");
        final String apiEndPoint =
                apiUrl + "/project_lists.xml?project_id=" + projectId + "&is_archived=" + isArchived + "&person_id="
                        + clientContactId + "&updated_since=" + updatedSince;
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("count(//project_list)", esbRestResponse.getBody()),
                getValueByExpression("count(//project_list)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project_list[0]/project_id", esbRestResponse.getBody()),
                getValueByExpression("//project_list[0]/project_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project_list[0]/is_archived", esbRestResponse.getBody()),
                getValueByExpression("//project_list[0]/project_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project_list[0]/person_id", esbRestResponse.getBody()),
                getValueByExpression("//project_list[0]/project_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project_list[0]/updated_at", esbRestResponse.getBody()),
                getValueByExpression("//project_list[0]/updated_at", apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testListProjectListsWithNegativeCase. 
     * Status: Skipped. 
     * Reason : Could not generate a negative case since the function call ignores invalid values for the parameters.
     */
    
    /**
     * Positive test case for changeArchiveStatus method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectListWithOptionalParameters" }, description = "cashboard {changeArchiveStatus} integration test with mandatory parameters.")
    public void testChangeArchiveStatusWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:changeArchiveStatus");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_changeArchiveStatus_mandatory.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String projectListId = connectorProperties.getProperty("projectListId");
        final String apiEndPoint = apiUrl + "/project_lists/" + projectListId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("//is_archived", apiRestResponse.getBody()), "true");
    }
    
    /**
     * Test case: testChangeArchiveStatusWithOptionalParameters. 
     * Status: Skipped. 
     * Reason : There are no any optional parameters for the function invocation.
     */
    
    /**
     * Negative test case for changeArchiveStatus method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {changeArchiveStatus} integration test with negative case.")
    public void testChangeArchiveStatusWithNegativeCase() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:changeArchiveStatus");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_changeArchiveStatus_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        final String apiEndPoint = apiUrl + "/project_lists/INVALID.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for createEstimate method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientContactWithOptionalParameters" }, description = "cashboard {createEstimate} integration test with mandatory parameters.")
    public void testCreateEstimateWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        String clientType = "Person";
        connectorProperties.setProperty("clientType", clientType);
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_mandatory.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String estimateId = getValueByExpression("//id", esbRestResponse.getBody());
        final String apiEndPoint = apiUrl + "/estimates/" + estimateId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("//client_id", apiRestResponse.getBody()), connectorProperties
                .getProperty("clientContactId"));
        Assert.assertEquals(getValueByExpression("//client_type", apiRestResponse.getBody()), clientType);
        Assert.assertEquals(getValueByExpression("//name", apiRestResponse.getBody()), connectorProperties
                .getProperty("estimateName"));
    }
    
    /**
     * Positive test case for createEstimate method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientContactWithOptionalParameters" }, description = "cashboard {createEstimate} integration test with optional parameters.")
    public void testCreateEstimateWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_optional.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String estimateId = getValueByExpression("//id", esbRestResponse.getBody());
        String assignedId = getValueByExpression("//assigned_id", esbRestResponse.getBody());
        connectorProperties.setProperty("estimateId", estimateId);
        connectorProperties.setProperty("assignedId", assignedId);
        final String apiEndPoint = apiUrl + "/estimates/" + estimateId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("//is_archived", apiRestResponse.getBody()), connectorProperties
                .getProperty("isArchived"));
        Assert.assertEquals(getValueByExpression("//intro_text", apiRestResponse.getBody()), connectorProperties
                .getProperty("introText"));
        Assert.assertEquals(getValueByExpression("//agreement_text", apiRestResponse.getBody()), connectorProperties
                .getProperty("agreementText"));
        Assert.assertEquals(getValueByExpression("//created_on", apiRestResponse.getBody()), connectorProperties
                .getProperty("updatedSince"));
        Assert.assertEquals(getValueByExpression("//address", apiRestResponse.getBody()), connectorProperties
                .getProperty("address"));
    }
    
    /**
     * Negative test case for createEstimate method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEstimateWithOptionalParameters" }, description = "cashboard {createEstimate} integration test with negative case.")
    public void testCreateEstimateWithNegativeCase() throws XPathExpressionException, XMLStreamException, SAXException,
            IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        final String apiEndPoint = apiUrl + "/estimates.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEstimate_negative.xml",
                        null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()), getValueByExpression("//error",
                apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getEstimate method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEstimateWithOptionalParameters" }, description = "cashboard {getEstimate} integration test with mandatory parameters.")
    public void testGetEstimateWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getEstimate");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEstimate_mandatory.xml");
        String estimateId = connectorProperties.getProperty("estimateId");
        final String apiEndPoint = apiUrl + "/estimates/" + estimateId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("//is_archived", esbRestResponse.getBody()), getValueByExpression(
                "//is_archived", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//intro_text", esbRestResponse.getBody()), getValueByExpression(
                "//intro_text", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//agreement_text", esbRestResponse.getBody()), getValueByExpression(
                "//agreement_text", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//created_on", esbRestResponse.getBody()), getValueByExpression(
                "//created_on", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//address", esbRestResponse.getBody()), getValueByExpression(
                "//address", apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testGetEstimateWithMandatoryParameters. 
     * Status: Skipped. 
     * Reason : There are no any optional parameters for the function invocation.
     */
    
    /**
     * Negative test case for getEstimate method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {getEstimate} integration test with negative case.")
    public void testGetEstimateWithNegativeCase() throws XPathExpressionException, XMLStreamException, SAXException,
            IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getEstimate");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEstimate_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        final String apiEndPoint = apiUrl + "/estimates/INVALID.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for listEstimates method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEstimateWithOptionalParameters" }, description = "cashboard {listEstimates} integration test with mandatory parameters.")
    public void testListEstimatesWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listEstimates");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEstimates_mandatory.xml");
        final String apiEndPoint = apiUrl + "/estimates.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("count(//estimate)", esbRestResponse.getBody()), getValueByExpression(
                "count(//estimate)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//estimate[0]/client_id", esbRestResponse.getBody()),
                getValueByExpression("//estimate[0]/client_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//estimate[0]/client_type", esbRestResponse.getBody()),
                getValueByExpression("//estimate[0]/client_type", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//estimate[0]/name", esbRestResponse.getBody()),
                getValueByExpression("//estimate[0]/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//estimate[0]/id", esbRestResponse.getBody()), getValueByExpression(
                "//estimate[0]/id", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listEstimates method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEstimateWithOptionalParameters" }, description = "cashboard {listEstimates} integration test with mandatory parameters.")
    public void testListEstimatesWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listEstimates");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEstimates_optional.xml");
        String isArchived = connectorProperties.getProperty("isArchived");
        String clientContactId = connectorProperties.getProperty("clientContactId");
        String clientType = connectorProperties.getProperty("clientType");
        final String apiEndPoint =
                apiUrl + "/estimates.xml?client_type=" + clientType + "&is_archived=" + isArchived + "&client_id="
                        + clientContactId + "&updated_since=" + updatedSince;
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("count(//estimate)", esbRestResponse.getBody()), getValueByExpression(
                "count(//estimate)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//estimate[0]/client_id", esbRestResponse.getBody()),
                getValueByExpression("//estimate[0]/client_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//estimate[0]/client_type", esbRestResponse.getBody()),
                getValueByExpression("//estimate[0]/client_type", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//estimate[0]/name", esbRestResponse.getBody()),
                getValueByExpression("//estimate[0]/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//estimate[0]/id", esbRestResponse.getBody()), getValueByExpression(
                "//estimate[0]/id", apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testListEstimatesWithNegativeCase. 
     * Status: Skipped. 
     * Reason : Could not generate a negative case since the function call ignores invalid values for the parameters.
     */
    
    /**
     * Positive test case for createLineItem method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEstimatesWithOptionalParameters" }, description = "cashboard {createLineItem} integration test with mandatory parameters.")
    public void testCreateLineItemWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createLineItem");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLineItem_mandatory.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String lineItemId = getValueByExpression("//id", esbRestResponse.getBody());
        final String apiEndPoint = apiUrl + "/line_items/" + lineItemId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("//title", apiRestResponse.getBody()), connectorProperties
                .getProperty("title"));
    }
    
    /**
     * Positive test case for createLineItem method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEstimatesWithOptionalParameters" }, description = "cashboard {createLineItem} integration test with optional parameters.")
    public void testCreateLineItemWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createLineItem");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLineItem_optional.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String lineItemId = getValueByExpression("//id", esbRestResponse.getBody());
        connectorProperties.setProperty("lineItemIdOptional", lineItemId);
        final String apiEndPoint = apiUrl + "/line_items/" + lineItemId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("//rank", apiRestResponse.getBody()), connectorProperties
                .getProperty("rank"));
        Assert.assertEquals(getValueByExpression("//estimate_id", apiRestResponse.getBody()), connectorProperties
                .getProperty("estimateId"));
        Assert.assertEquals(getValueByExpression("//project_id", apiRestResponse.getBody()), connectorProperties
                .getProperty("projectId"));
        Assert.assertEquals(getValueByExpression("//type_code", apiRestResponse.getBody()), connectorProperties
                .getProperty("typeCode"));
        Assert.assertEquals(getValueByExpression("//project_list_id", apiRestResponse.getBody()), connectorProperties
                .getProperty("projectListId"));
    }
    
    /**
     * Negative test case for createLineItem method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {createLineItem} integration test with negative case.")
    public void testCreateLineItemWithNegativeCase() throws XPathExpressionException, XMLStreamException, SAXException,
            IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createLineItem");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLineItem_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        final String apiEndPoint = apiUrl + "/line_items.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "POST", apiRequestHeadersMap, "api_createLineItem_negative.xml",
                        null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()), getValueByExpression("//error",
                apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listLineItems method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLineItemWithOptionalParameters" }, description = "cashboard {listLineItems} integration test with mandatory parameters.")
    public void testListLineItemsWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listLineItems");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listLineItems_mandatory.xml");
        final String apiEndPoint = apiUrl + "/line_items.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("count(//line_item)", esbRestResponse.getBody()),
                getValueByExpression("count(//line_item)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//line_item[0]/title", esbRestResponse.getBody()),
                getValueByExpression("//line_item[0]/title", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//line_item[0]/created_on", esbRestResponse.getBody()),
                getValueByExpression("//line_item[0]/created_on", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//line_item[0]/id", esbRestResponse.getBody()), getValueByExpression(
                "//line_item[0]/id", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listLineItems method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLineItemWithOptionalParameters" }, description = "cashboard {listLineItems} integration test with mandatory parameters.")
    public void testListLineItemsWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listLineItems");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listLineItems_optional.xml");
        
        String estimateId = connectorProperties.getProperty("estimateId");
        String projectId = connectorProperties.getProperty("projectId");
        String projectListId = connectorProperties.getProperty("projectListId");
        String typeCode = connectorProperties.getProperty("typeCode");
        
        final String apiEndPoint =
                apiUrl + "/line_items.xml?estimate_id=" + estimateId + "&type_code=" + typeCode + "&project_id="
                        + projectId + "&project_list_id=" + projectListId + "&updated_since=" + updatedSince;
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("count(//line_item)", esbRestResponse.getBody()),
                getValueByExpression("count(//line_item)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//line_item[0]/project_id", esbRestResponse.getBody()),
                getValueByExpression("//line_item[0]/project_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//line_item[0]/type_code", esbRestResponse.getBody()),
                getValueByExpression("//line_item[0]/type_code", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//line_item[0]/project_list_id", esbRestResponse.getBody()),
                getValueByExpression("//line_item[0]/project_list_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//line_item[0]/id", esbRestResponse.getBody()), getValueByExpression(
                "//line_item[0]/id", apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testListLineItemsWithNegativeCase. 
     * Status: Skipped. 
     * Reason : Could not generate a negative case since the function call ignores invalid values for the parameters.
     */
    
    /**
     * Positive test case for getLineItem method with mandatory parameters.
     * 
     * @throws XMLStreamException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLineItemWithOptionalParameters" }, description = "cashboard {getLineItem} integration test with mandatory parameters.")
    public void testGetLineItemWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getLineItem");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getLineItem_mandatory.xml");
        String lineItemId = connectorProperties.getProperty("lineItemIdOptional");
        final String apiEndPoint = apiUrl + "/line_items/" + lineItemId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(getValueByExpression("//rank", apiRestResponse.getBody()), getValueByExpression("//rank",
                esbRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//estimate_id", apiRestResponse.getBody()), getValueByExpression(
                "//estimate_id", esbRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project_id", apiRestResponse.getBody()), getValueByExpression(
                "//project_id", esbRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//type_code", apiRestResponse.getBody()), getValueByExpression(
                "//type_code", esbRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project_list_id", apiRestResponse.getBody()), getValueByExpression(
                "//project_list_id", esbRestResponse.getBody()));
    }
    
    /**
     * Test case: testGetLineItemWithOptionalParameters. 
     * Status: Skipped. 
     * Reason : There are no any optional parameters for the function invocation.
     */
    
    /**
     * Negative test case for getLineItem method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {getLineItem} integration test with negative case.")
    public void testGetLineItemWithNegativeCase() throws XPathExpressionException, XMLStreamException, SAXException,
            IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getLineItem");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getLineItem_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        final String apiEndPoint = apiUrl + "/line_items/INVALID.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for createInvoice method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {createInvoice} integration test with mandatory parameters.")
    public void testCreateInvoiceWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_mandatory.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String invoiceId = getValueByExpression("//id", esbRestResponse.getBody());
        
        final String apiEndPoint = apiUrl + "/invoices/" + invoiceId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("//client_id", apiRestResponse.getBody()), connectorProperties
                .getProperty("clientCompanyId"));
        Assert.assertEquals(getValueByExpression("//client_type", apiRestResponse.getBody()), connectorProperties
                .getProperty("clientTypeCompany"));
        Assert.assertEquals(getValueByExpression("//discountPercentage", esbRestResponse.getBody()),
                getValueByExpression("//discountPercentage", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//due_date", esbRestResponse.getBody()), getValueByExpression(
                "//due_date", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//invoice_date", esbRestResponse.getBody()), getValueByExpression(
                "//invoice_date", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for createInvoice method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {createInvoice} integration test with optional parameters.")
    public void testCreateInvoiceWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_optional.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String invoiceId = getValueByExpression("//id", esbRestResponse.getBody());
        connectorProperties.setProperty("invoiceId", invoiceId);
        final String apiEndPoint = apiUrl + "/invoices/" + invoiceId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("//client_id", esbRestResponse.getBody()), getValueByExpression(
                "//client_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//client_type", esbRestResponse.getBody()), getValueByExpression(
                "//client_type", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//address", esbRestResponse.getBody()), getValueByExpression(
                "//address", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//notes", esbRestResponse.getBody()), getValueByExpression("//notes",
                apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//invoice_date", esbRestResponse.getBody()), getValueByExpression(
                "//invoice_date", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for createInvoice method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {createInvoice} integration test with negative case.")
    public void testCreateInvoiceWithNegativeCase() throws XPathExpressionException, XMLStreamException, SAXException,
            IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        final String apiEndPoint = apiUrl + "/invoices.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoice_negative.xml",
                        null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()), getValueByExpression("//error",
                apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getInvoice method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateInvoiceWithOptionalParameters" }, description = "cashboard {getInvoice} integration test with mandatory parameters.")
    public void testGetInvoiceWithMandatoryParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoice_mandatory.xml");
        
        String invoiceId = connectorProperties.getProperty("invoiceId");
        
        final String apiEndPoint = apiUrl + "/invoices/" + invoiceId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("//client_id", esbRestResponse.getBody()), getValueByExpression(
                "//client_id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//client_type", esbRestResponse.getBody()), getValueByExpression(
                "//client_type", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//address", esbRestResponse.getBody()), getValueByExpression(
                "//address", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//notes", esbRestResponse.getBody()), getValueByExpression("//notes",
                apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//invoice_date", esbRestResponse.getBody()), getValueByExpression(
                "//invoice_date", apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testGetInvoiceWithOptionalParameters. 
     * Status: Skipped. 
     * Reason : There are no any optional parameters for the function invocation.
     */
    
    /**
     * Negative test case for getInvoice method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {getInvoice} integration test with negative case.")
    public void testGetInvoiceWithNegativeCase() throws XPathExpressionException, XMLStreamException, SAXException,
            IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoice_negative.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        final String apiEndPoint = apiUrl + "/invoices/INVALID.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Test case: testListInvoicesWithMandatoryParameters. 
     * Status: Skipped. 
     * Reason : There are no any mandatory parameters for the function invocation.
     */
    
    /**
     * Positive test case for listInvoices method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {listInvoices} integration test with optional parameters.")
    public void testListInvoicesWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_optional.xml");
        
        final String apiEndPoint =
                apiUrl + "/invoices.xml?has_been_sent=" + connectorProperties.getProperty("hasBeenSent")
                        + "&has_been_paid=" + connectorProperties.getProperty("hasBeenPaid") + "&client_id="
                        + connectorProperties.getProperty("clientCompanyId") + "&client_type="
                        + connectorProperties.getProperty("clientTypeCompany");
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(getValueByExpression("count(//invoice)", esbRestResponse.getBody()), getValueByExpression(
                "count(//invoice)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//invoice[0]/id", esbRestResponse.getBody()), getValueByExpression(
                "//invoice[0]/id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//invoice[0]/has_been_sent", esbRestResponse.getBody()),
                getValueByExpression("//invoice[0]/has_been_sent", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//invoice[0]/hasBeenPaid", esbRestResponse.getBody()),
                getValueByExpression("//invoice[0]/hasBeenPaid", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//invoice[0]/clientId", esbRestResponse.getBody()),
                getValueByExpression("//invoice[0]/clientId", apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testListInvoicesWithONegativeCase. 
     * Status: Skipped. 
     * Reason : Could not generate a negative case since the function call ignores invalid values sent for the optional parameters.
     */
    
    /**
     * Test case: testUpdateInvoiceWithMandatoryParameters. 
     * Status: Skipped. 
     * Reason : There are no any mandatory parameters for the function invocation.
     */
    
    /**
     * Positive test case for updateInvoice method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateInvoiceWithOptionalParameters" }, description = "cashboard {updateInvoice} integration test with optional parameters.")
    public void testUpdateInvoiceWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:updateInvoice");
        
        String invoiceId = connectorProperties.getProperty("invoiceId");
        
        final String apiEndPoint = apiUrl + "/invoices/" + invoiceId + ".xml";
        final RestResponse<OMElement> apiRestResponseBefore =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInvoice_optional.xml");
        
        final RestResponse<OMElement> apiRestResponseAfter =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertNotEquals(getValueByExpression("//created_on", apiRestResponseBefore.getBody()),
                getValueByExpression("//created_on", apiRestResponseAfter.getBody()));
        Assert.assertNotEquals(getValueByExpression("//due_date", apiRestResponseBefore.getBody()),
                getValueByExpression("//due_date", apiRestResponseAfter.getBody()));
        Assert.assertNotEquals(getValueByExpression("//address", apiRestResponseBefore.getBody()),
                getValueByExpression("//address", apiRestResponseAfter.getBody()));
        Assert.assertNotEquals(getValueByExpression("//notes", apiRestResponseBefore.getBody()), getValueByExpression(
                "//notes", apiRestResponseAfter.getBody()));
        Assert.assertNotEquals(getValueByExpression("//discount_percentage", apiRestResponseBefore.getBody()),
                getValueByExpression("//discount_percentage", apiRestResponseAfter.getBody()));
    }
    
    /**
     * Negative test case for updateInvoice method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {updateInvoice} integration test with negative case.")
    public void testUpdateInvoiceWithNegativeCase() throws XPathExpressionException, XMLStreamException, SAXException,
            IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:updateInvoice");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInvoice_negative.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        final String apiEndPoint = apiUrl + "/invoices/INVALID.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "PUT", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for createEmployee method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {createEmployee} integration test with mandatory parameters.")
    public void testCreateEmployeeWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createEmployee");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEmployee_mandatory.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String employeeId = getValueByExpression("//id", esbRestResponse.getBody());
        connectorProperties.setProperty("employeeId", employeeId);
        final String apiEndPoint = apiUrl + "/employees/" + employeeId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("//first_name", apiRestResponse.getBody()), connectorProperties
                .getProperty("firstName"));
        Assert.assertEquals(getValueByExpression("//email_address", apiRestResponse.getBody()), connectorProperties
                .getProperty("employeeEmail"));
    }
    
    /**
     * Positive test case for createEmployee method with optional parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEmployeeWithMandatoryParameters" }, description = "cashboard {createEmployee} integration test with mandatory parameters.")
    public void testCreateEmployeeWithOptionalParameters() throws XPathExpressionException, XMLStreamException,
            SAXException, IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createEmployee");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEmployee_optional.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String employeeId = getValueByExpression("//id", esbRestResponse.getBody());
        connectorProperties.setProperty("employeeId", employeeId);
        final String apiEndPoint = apiUrl + "/employees/" + employeeId + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("//first_name", apiRestResponse.getBody()), connectorProperties
                .getProperty("firstName"));
        Assert.assertEquals(getValueByExpression("//email_address", apiRestResponse.getBody()), connectorProperties
                .getProperty("emailAddressOptional"));
        Assert.assertEquals(getValueByExpression("//last_name", apiRestResponse.getBody()), connectorProperties
                .getProperty("lastName"));
        Assert.assertEquals(getValueByExpression("//telephone", apiRestResponse.getBody()), connectorProperties
                .getProperty("telephone"));
        Assert.assertEquals(getValueByExpression("//url", apiRestResponse.getBody()), connectorProperties
                .getProperty("url"));
        Assert.assertEquals(getValueByExpression("//address", apiRestResponse.getBody()), connectorProperties
                .getProperty("address"));
        Assert.assertEquals(getValueByExpression("//city", apiRestResponse.getBody()), connectorProperties
                .getProperty("city"));
    }
    
    /**
     * Negative test case for createEmployee method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEmployeeWithMandatoryParameters" }, description = "cashboard {createEmployee} integration test with negative case.")
    public void testCreateEmployeeWithNegativeCase() throws XPathExpressionException, XMLStreamException, SAXException,
            IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:createEmployee");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEmployee_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        final String apiEndPoint = apiUrl + "/employees.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEmployee_negative.xml",
                        null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()), getValueByExpression("//error",
                apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getEmployee method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEmployeeWithMandatoryParameters" }, description = "cashboard {getEmployee} integration test with mandatory parameters.")
    public void testGetEmployeeWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getEmployee");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmployee_mandatory.xml");
        
        final String apiEndPoint = apiUrl + "/employees/" + connectorProperties.getProperty("employeeId") + ".xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("count(//address)", esbRestResponse.getBody()), getValueByExpression(
                "count(//address)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("count(//city)", esbRestResponse.getBody()), getValueByExpression(
                "count(//city)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("count(//email_address)", esbRestResponse.getBody()),
                getValueByExpression("count(//email_address)", apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testGetEmployeeWithOptionalParameters. 
     * Status: Skipped. 
     * Reason : There are no any optional parameters for the function invocation.
     */
    
    /**
     * Negative test case for getEmployee method.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {getEmployee} integration test with negative case.")
    public void testGetEmployeeWithNegativeCase() throws XPathExpressionException, XMLStreamException, SAXException,
            IOException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:getEmployee");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmployee_negative.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        final String apiEndPoint = apiUrl + "/employees/Invalid.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for listEmployees method with mandatory parameters.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    @Test(groups = { "wso2.esb" }, description = "cashboard {listEmployees} integration test with mandatory parameters.")
    public void testListEmployeesWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        
        esbRequestHeadersMap.put("Action", "urn:listEmployees");
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEmployees_mandatory.xml");
        
        final String apiEndPoint = apiUrl + "/employees.xml";
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequestHTTPS(apiEndPoint, "GET", apiRequestHeadersMap, null, null, true);
        
        Assert.assertEquals(getValueByExpression("count(//employees)", esbRestResponse.getBody()),
                getValueByExpression("count(//employees)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//employees[0]/email_address", esbRestResponse.getBody()),
                getValueByExpression("//line_item[0]/email_address", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//employees[0]/first_name", esbRestResponse.getBody()),
                getValueByExpression("//line_item[0]/first_name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//employees[0]/employee_status_code", esbRestResponse.getBody()),
                getValueByExpression("//line_item[0]/employee_status_code", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//employees[0]/id", esbRestResponse.getBody()), getValueByExpression(
                "//line_item[0]/id", apiRestResponse.getBody()));
    }
    
    /**
     * Test case: testListEmployeesWithOptionalParameters. 
     * Status: Skipped. 
     * Reason : There are no any optional parameters for the function invocation.
     */
    
    /**
     * Test case: testListEmployeesWithNegativeCase. 
     * Status: Skipped. 
     * Reason : Could not generate negative case since the function call doesn't have any other parameters except authentication credentials.
     */
}
