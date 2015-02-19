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

package org.wso2.carbon.connector.integration.test.xero;

import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.wso2.carbon.connector.integration.test.xero.XeroHttpRequest;

public class XeroConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("xero-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/xml");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
    }
    
    /**
     * Positive test case for postEmployees method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "xero {postEmployees} integration test with optional parameters.")
    public void testPostEmployeesWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:postEmployees");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_postEmployees_optional.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        final String employeeId = getValueByExpression("//EmployeeID", esbRestResponse.getBody());
        connectorProperties.setProperty("employeeId", employeeId);
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/employees/" + employeeId;
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("firstName"),
                getValueByExpression("//FirstName", apiRestResponse.getBody()));
        Assert.assertEquals(connectorProperties.getProperty("lastName"),
                getValueByExpression("//LastName", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for postEmployees.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testPostEmployeesWithOptionalParameters" }, description = "xero {postEmployees} integration test with negative case.")
    public void testPostEmployeesWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:postEmployees");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_postEmployees_negative.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/employees";
        final String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_postEmployees_negative.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//Type", esbRestResponse.getBody()),
                getValueByExpression("//Type", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//Message", esbRestResponse.getBody()),
                getValueByExpression("//Message", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getEmployee method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testPostEmployeesWithNegativeCase" }, description = "xero {getEmployee} integration test with mandatory parameters.")
    public void testGetEmployeeWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEmployee");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmployee_mandatory.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/employees";
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("count(//Employee)", esbRestResponse.getBody()),
                getValueByExpression("count(//Employee)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//Employee[1]/FirstName", esbRestResponse.getBody()),
                getValueByExpression("//Employee[1]/FirstName", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getEmployee method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetEmployeeWithMandatoryParameters" }, description = "xero {getEmployee} integration test with optional parameters.")
    public void testGetEmployeeWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEmployee");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmployee_optional.xml");
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/employees/"
                        + connectorProperties.getProperty("employeeId");
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//Employee/FirstName", esbRestResponse.getBody()),
                getValueByExpression("//Employee/FirstName", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//Employee/LastName", esbRestResponse.getBody()),
                getValueByExpression("//Employee/LastName", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for getEmployee.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetEmployeeWithOptionalParameters" }, description = "xero {getEmployee} integration test with negative case.")
    public void testGetEmployeeWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEmployee");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmployee_negative.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/employees/Invalid";
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(getValueByExpression("//output", esbRestResponse.getBody()),
                getValueByExpression("//output", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getPayItems method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetEmployeeWithNegativeCase" }, description = "xero {getPayItems} integration test with mandatory parameters.")
    public void testGetPeyItemsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPayItems");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayItems_mandatory.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/PayItems";
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("count(//EarningsType)", esbRestResponse.getBody()),
                getValueByExpression("count(//EarningsType)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//EarningsType[1]/EarningsType", esbRestResponse.getBody()),
                getValueByExpression("//EarningsType[1]/EarningsType", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getPayItems method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPeyItemsWithMandatoryParameters" }, description = "xero {getPayItems} integration test with optional parameters.")
    public void testGetPayItemsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPayItems");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayItems_optional.xml");
        final String payStubsEarningsTypeID = getValueByExpression("//EarningsType[EarningsType='Regular Hours']/EarningsRateID", esbRestResponse.getBody());
        connectorProperties.setProperty("payStubsEarningsTypeID", payStubsEarningsTypeID);
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/PayItems?page=1";
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("count(//EarningsType)", esbRestResponse.getBody()),
                getValueByExpression("count(//EarningsType)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//EarningsType[1]/EarningsType", esbRestResponse.getBody()),
                getValueByExpression("//EarningsType[1]/EarningsType", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getPaySchedules method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPayItemsWithOptionalParameters" }, description = "xero {getPaySchedules} integration test with mandatory parameters.")
    public void testGetPaySchedulesWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPaySchedules");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPaySchedules_mandatory.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/PaySchedules";
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("count(//PaySchedules)", esbRestResponse.getBody()),
                getValueByExpression("count(//PaySchedules)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//PaySchedule[1]/PayScheduleName", esbRestResponse.getBody()),
                getValueByExpression("//PaySchedule[1]/PayScheduleName", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getPaySchedules method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPaySchedulesWithMandatoryParameters" }, description = "xero {getPaySchedules} integration test with optional parameters.")
    public void testGetPaySchedulesWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPaySchedules");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPaySchedules_optional.xml");
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/PaySchedules/" + connectorProperties.getProperty("payScheduleId");
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//PaySchedule[1]/PayScheduleName", esbRestResponse.getBody()),
                getValueByExpression("//PaySchedule[1]/PayScheduleName", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//PaySchedule[1]/PayScheduleID", esbRestResponse.getBody()),
                connectorProperties.getProperty("payScheduleId"));
    }
    
    /**
     * Negative test case for getPaySchedules.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPaySchedulesWithOptionalParameters" }, description = "xero {getPaySchedules} integration test with negative case.")
    public void testGetPaySchedulesWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPaySchedules");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPaySchedules_negative.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/PaySchedules/Invalid";
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(getValueByExpression("//output", esbRestResponse.getBody()),
                getValueByExpression("//output", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for postPayRuns method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPaySchedulesWithNegativeCase" }, description = "xero {postPayRuns} integration test with optional parameters.")
    public void testPostPayRunsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:postPayRuns");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_postPayRuns_optional.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        final String payRunId = getValueByExpression("//PayRunID", esbRestResponse.getBody());
        connectorProperties.setProperty("payRunId", payRunId);
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/payruns/" + payRunId;
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("payScheduleId"),
                getValueByExpression("//PayScheduleID", apiRestResponse.getBody()));
        Assert.assertTrue(Integer.parseInt(getValueByExpression("count(//Paystub)", apiRestResponse.getBody())) > 0);
    }
    
    /**
     * Positive test case for getPayRuns method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testPostPayRunsWithOptionalParameters" }, description = "xero {getPayRuns} integration test with mandatory parameters.")
    public void testGetPayRunsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPayRuns");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayRuns_mandatory.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/payruns";
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("count(//PayRuns)", esbRestResponse.getBody()),
                getValueByExpression("count(//PayRuns)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//PayRun[1]/PayRunID", esbRestResponse.getBody()),
                getValueByExpression("//PayRun[1]/PayRunID", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getPayRuns method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPayRunsWithMandatoryParameters" }, description = "xero {getPayRuns} integration test with optional parameters.")
    public void testGetPayRunsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPayRuns");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayRuns_optional.xml");
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/payruns/" + connectorProperties.getProperty("payRunId");
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        

        final String employeeId = getValueByExpression("//PayRun[1]/Paystubs/Paystub[1]/EmployeeID", esbRestResponse.getBody());
        connectorProperties.setProperty("payStubEmployeeId", employeeId);
        final String paystubId = getValueByExpression("//PayRun[1]/Paystubs/Paystub[1]/PaystubID", esbRestResponse.getBody());
        connectorProperties.setProperty("payStubsId", paystubId);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//PayRun[1]/Paystubs/Paystub[1]/EmployeeID", esbRestResponse.getBody()),
                getValueByExpression("//PayRun[1]/Paystubs/Paystub[1]/EmployeeID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//PayRun[1]/PayRunID", esbRestResponse.getBody()),
                connectorProperties.getProperty("payRunId"));
    }
    
    /**
     * Negative test case for getPayRuns.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPayRunsWithOptionalParameters" }, description = "xero {getPayRuns} integration test with negative case.")
    public void testGetPayRunsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPayRuns");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayRuns_negative.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/payruns/Invalid";
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(getValueByExpression("//output", esbRestResponse.getBody()),
                getValueByExpression("//output", apiRestResponse.getBody()));
    }
    
 /**
  * Positive test case for postPayStubs method with optional parameters.
  */
 @Test(priority = 1, groups = { "wso2.esb" },dependsOnMethods = { "testGetPayRunsWithOptionalParameters" }, description = "xero {postPayStubs} integration test with optional parameters.")
 public void testPostPayStubsWithOptionalParameters() throws Exception {
 
     esbRequestHeadersMap.put("Action", "urn:postPayStubs");
     
     final RestResponse<OMElement> esbRestResponse =
             sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_postPayStubs_optional.xml");
     
     Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    
     final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/paystubs/" + connectorProperties.getProperty("payStubsId");
     final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
     apiRequestHeadersMap.put("Authorization", OAuthHeader);
     
     final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
     
     Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
     Assert.assertEquals(getValueByExpression("//Paystub/FirstName", esbRestResponse.getBody()),
             getValueByExpression("//Paystub/FirstName", apiRestResponse.getBody()));
     Assert.assertEquals(getValueByExpression("//Paystub/LastName", esbRestResponse.getBody()),
             getValueByExpression("//Paystub/LastName", apiRestResponse.getBody()));

 }
    
    /**
     * Positive test case for getPayStubs method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPayRunsWithNegativeCase" }, description = "xero {getPayStubs} integration test with mandatory parameters.")
    public void testGetPayStubsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPayStubs");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayStubs_mandatory.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/paystubs/" + connectorProperties.getProperty("payStubsId");
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//ProviderName", esbRestResponse.getBody()),
                getValueByExpression("//ProviderName", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//Paystub/EmployeeID", esbRestResponse.getBody()),
                getValueByExpression("//Paystub/EmployeeID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//Paystub/FirstName", esbRestResponse.getBody()),
                getValueByExpression("//Paystub/FirstName", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for getPayStubs.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPayRunsWithOptionalParameters" }, description = "xero {getPayStubs} integration test with negative case.")
    public void testGetPayStubsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPayStubs");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayStubs_negative.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/payroll.xro/1.0/paystubs/Invalid";
        final String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(getValueByExpression("//output", esbRestResponse.getBody()),
                getValueByExpression("//output", apiRestResponse.getBody()));
    }
    
    /**
     * Generate authentication signature.
     * 
     * @param requestMethod
     * @param requestUrl
     * @return Authorization header string
     */
    private String getOAuthHeader(String requestMethod, String requestUrl) {
    
        String OAuthHeader = null;
        
        final String consumerKey = connectorProperties.getProperty("consumerKey");
        final String consumerSecret = connectorProperties.getProperty("consumerSecret");
        final String accessToken = connectorProperties.getProperty("accessToken");
        final String accessTokenSecret = connectorProperties.getProperty("accessTokenSecret");
        
        final XeroHttpRequest request = new XeroHttpRequest();
        request.setRequestUrl(requestUrl);
        request.setMethod(requestMethod);
        
        // Generate the Authorization and get response through signpost.
        final OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(accessToken, accessTokenSecret);
        consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
        HttpRequest response;
        try {
            response = consumer.sign(request);
            OAuthHeader = response.getHeader(OAuth.HTTP_AUTHORIZATION_HEADER);
            
        } catch (OAuthMessageSignerException omse) {
            log.error("Error occured in connector", omse);
        } catch (OAuthExpectationFailedException oefe) {
            log.error("Error occured in connector", oefe);
        } catch (OAuthCommunicationException oce) {
            log.error("Error occured in connector", oce);
        }
        
        return OAuthHeader;
    }
}
