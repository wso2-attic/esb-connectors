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

package org.wso2.carbon.connector.integration.test.freshbooks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.Base64;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class FreshbooksConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    final private String API_URL_REMAINDER = "/api/2.1/xml-in";
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("freshbooks-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/xml");
        
        // Create base64-encoded auth string from token:arbitraryPass combination
        final String authString =
                connectorProperties.getProperty("authenticationToken") + ":"
                        + connectorProperties.getProperty("arbitraryPassword");
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
    }
    
    /**
     * Positive test case for createExpense method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateClientWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createExpense} integration test with mandatory parameters.")
    public void testCreateExpenseWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExpense_mandatory.xml");
        
        connectorProperties.setProperty("expenseIdMandatory",
                getValueByExpression("//*[local-name()='expense_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createExpense_mandatory.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                Integer.parseInt(getValueByExpression("//*[local-name()='expense_id']/text()",
                        esbRestResponse.getBody())),
                Integer.parseInt(getValueByExpression("//*[local-name()='expense_id']/text()",
                        apiRestResponse.getBody())));
        
    }
    
    /**
     * Positive test case for createExpense method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateClientWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createExpense} integration test with optional parameters.")
    public void testCreateExpenseWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExpense_optional.xml");
        
        connectorProperties.setProperty("expenseIdOptional",
                getValueByExpression("//*[local-name()='expense_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createExpense_optional.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                Integer.parseInt(getValueByExpression("//*[local-name()='expense_id']", esbRestResponse.getBody())),
                Integer.parseInt(getValueByExpression("//*[local-name()='expense_id']", apiRestResponse.getBody())));
        Assert.assertEquals(getValueByExpression("//*[local-name()='client_id']", apiRestResponse.getBody()),
                connectorProperties.getProperty("clientIdOptional"));
        
    }
    
    /**
     * Negative test case for createExpense methods.
     */
    @Test(dependsOnMethods = { "testCreateExpenseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createExpense} negative integration test.")
    public void testCreateExpenseNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExpense_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createExpense_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for updateExpense method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {updateExpense} integration test with optional parameters.")
    public void testUpdateExpenseWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateExpense_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateExpense_optional.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='client_id']", apiRestResponse.getBody()),
                connectorProperties.getProperty("clientIdOptional"));
        
    }
    
    /**
     * Negative test case for updateExpense methods.
     */
    @Test(dependsOnMethods = { "testDeleteExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {updateExpense} negative integration test.")
    public void testUpdateExpenseNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateExpense_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateExpense_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for getExpense method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateExpenseWithMandatoryParameters", "testUpdateExpenseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {getExpense} integration test with mandatory parameters.")
    public void testGetExpenseWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExpense_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getExpense_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='amount']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='amount']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='staff_id']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='staff_id']/text()", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for getExpense methods.
     */
    @Test(dependsOnMethods = { "testDeleteExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {getExpense} negative integration test.")
    public void testGetExpenseNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExpense_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getExpense_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for listExpense method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateExpenseWithMandatoryParameters", "testGetExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listExpense} integration test with mandatory parameters.")
    public void testListExpenseWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listExpense_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listExpense_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("count(//*[local-name()='expense'])", esbRestResponse.getBody()),
                getValueByExpression("count(//*[local-name()='expense'])", apiRestResponse.getBody()));
        Assert.assertEquals(Integer.parseInt(getValueByExpression(
                "//*[local-name()='expense']/*[local-name()='expense_id']/text()", esbRestResponse.getBody())), Integer
                .parseInt(getValueByExpression("//*[local-name()='expense']/*[local-name()='expense_id']/text()",
                        apiRestResponse.getBody())));
        
    }
    
    /**
     * Positive test case for listExpense method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateExpenseWithMandatoryParameters", "testListExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listExpense} integration test with optional parameters.")
    public void testListExpenseWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listExpense");
        parametersMap.put("folder", "active");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listExpense_optional.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listExpense_optional.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("count(//*[local-name()='expense'])", esbRestResponse.getBody()),
                getValueByExpression("count(//*[local-name()='expense'])", apiRestResponse.getBody()));
        Assert.assertEquals(Integer.parseInt(getValueByExpression(
                "//*[local-name()='expense']/*[local-name()='client_id']/text()", esbRestResponse.getBody())), Integer
                .parseInt(getValueByExpression("//*[local-name()='expense']/*[local-name()='client_id']/text()",
                        apiRestResponse.getBody())));
        
    }
    
    /**
     * Negative test case for listExpense methods.
     */
    @Test(dependsOnMethods = { "testCreateExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listExpense} negative integration test.")
    public void testListExpenseNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listExpense_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listExpense_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for deleteExpense method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateExpenseWithMandatoryParameters", "testListExpenseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {deleteExpense} integration test with mandatory parameters.")
    public void testDeleteExpenseWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteExpense_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteExpense_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "fail");
        
    }
    
    /**
     * Negative test case for deleteExpense methods.
     */
    @Test(dependsOnMethods = { "testDeleteExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {deleteExpense} negative integration test.")
    public void testDeleteExpenseNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteExpense_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteExpense_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for createCategory method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Freshbooks {createCategory} integration test with mandatory parameters.")
    public void testCreateCategoryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCategory_mandatory.xml");
        
        connectorProperties.setProperty("categoryId",
                getValueByExpression("//*[local-name()='category_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCategory_mandatory.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='name']/text()", apiRestResponse.getBody()),
                connectorProperties.getProperty("categoryName"));
    }
    
    /**
     * Negative test case for createCategory method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCategoryWithMandatoryParameters" }, description = "Freshbooks {createCategory} integration test with negative.")
    public void testCreateCategoryNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCategory_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCategory_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getCategory method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCategoryNegativeCase" }, description = "Freshbooks {getCategory} integration test with mandatory parameters.")
    public void testGetCategoryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCategory_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getCategory_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='name']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='name']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for getCategory method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCategoryWithMandatoryParameters" }, description = "Freshbooks {getCategory} integration test with negative.")
    public void testGetCategoryNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCategory_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getCategory_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for updateCategory method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCategoryNegativeCase" }, description = "Freshbooks {updateCategory} integration test with optional parameters.")
    public void testUpdateCategoryWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCategory_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateCategory_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='name']/text()", apiRestResponse.getBody()),
                connectorProperties.getProperty("categoryNameUpdated"));
    }
    
    /**
     * Negative test case for updateCategory method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCategoryWithOptionalParameters" }, description = "Freshbooks {updateCategory} integration test with negative.")
    public void testUpdateCategoryNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCategory_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateCategory_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listCategory method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCategoryNegativeCase" }, description = "Freshbooks {listCategory} integration test with mandatory parameters.")
    public void testListCategoryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCategory_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listCategory_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='categories']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='categories']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='category'][1]/*[local-name()='name']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='category'][1]/*[local-name()='name']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listCategory method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCategoryWithMandatoryParameters" }, description = "Freshbooks {listCategory} integration test with optional parameters.")
    public void testListCategoryWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCategory_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listCategory_optional.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='categories']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='categories']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='category'][1]/*[local-name()='name']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='category'][1]/*[local-name()='name']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for listCategory method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCategoryWithOptionalParameters" }, description = "Freshbooks {listCategory} integration test with negative.")
    public void testListCategoryNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCategory_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listCategory_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for deleteCategory method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCategoryNegativeCase" }, description = "Freshbooks {deleteCategory} integration test with mandatory parameters.")
    public void testDeleteCategoryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCategory_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteCategory_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "fail");
        Assert.assertNotNull(getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for deleteCategory method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteCategoryWithMandatoryParameters" }, description = "Freshbooks {deleteCategory} integration test with negative.")
    public void testDeleteCategoryNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteCategory");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCategory_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteCategory_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for createInvoice method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateClientWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createInvoice} integration test with mandatory parameters.")
    public void testCreateInvoiceWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_mandatory.xml");
        
        connectorProperties.setProperty("invoiceIdMandatory",
                getValueByExpression("//*[local-name()='invoice_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoice_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='client_id']/text()", apiRestResponse.getBody()),
                connectorProperties.getProperty("clientIdOptional"));
    }
    
    /**
     * Positive test case for createInvoice method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" }, description = "Freshbooks {createInvoice} integration test with optional parameters.")
    public void testCreateInvoiceWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        parametersMap.put("invoiceNotes", "This is a note.");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_optional.xml",
                        parametersMap);
        
        connectorProperties.setProperty("invoiceIdOptional",
                getValueByExpression("//*[local-name()='invoice_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoice_optional.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='client_id']/text()", apiRestResponse.getBody()),
                connectorProperties.getProperty("clientIdOptional"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='notes']/text()", apiRestResponse.getBody()),
                parametersMap.get("invoiceNotes"));
    }
    
    /**
     * Negative test case for createInvoice method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateInvoiceWithOptionalParameters" }, description = "Freshbooks {createInvoice} integration test with negative.")
    public void testCreateInvoiceNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoice_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getInvoice method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateInvoiceNegativeCase" }, description = "Freshbooks {getInvoice} integration test with mandatory parameters.")
    public void testGetInvoiceWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoice_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getInvoice_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='client_id']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='client_id']/text()", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='notes']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='notes']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for getInvoice method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetInvoiceWithMandatoryParameters" }, description = "Freshbooks {getInvoice} integration test with negative.")
    public void testGetInvoiceNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoice_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getInvoice_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listInvoices method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetInvoiceNegativeCase" }, description = "Freshbooks {listInvoices} integration test with mandatory parameters.")
    public void testListInvoiceWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoice_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listInvoice_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='invoices']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='invoices']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='invoices']/@per_page)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='invoices']/@per_page)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='invoice'][1]/*[local-name()='first_name']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='invoice'][1]/*[local-name()='first_name']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listInvoices method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListInvoiceWithMandatoryParameters" }, description = "Freshbooks {listInvoices} integration test with optional parameters.")
    public void testListInvoiceWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoice_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listInvoice_optional.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='invoices']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='invoices']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='invoices']/@per_page)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='invoices']/@per_page)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='invoice'][1]/*[local-name()='first_name']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='invoice'][1]/*[local-name()='first_name']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for listInvoices method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListInvoiceWithOptionalParameters" }, description = "Freshbooks {listInvoices} integration test with negative.")
    public void testListInvoiceNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoice_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listInvoice_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for updateInvoice method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListInvoiceNegativeCase" }, description = "Freshbooks {updateInvoice} integration test with optional parameters.")
    public void testUpdateInvoiceWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateInvoice");
        parametersMap.put("firstName", "UpdatedFirst");
        parametersMap.put("lastName", "UpdatedLast");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInvoice_optional.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateInvoice_optional.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='first_name']/text()", apiRestResponse.getBody()),
                parametersMap.get("firstName"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='last_name']/text()", apiRestResponse.getBody()),
                parametersMap.get("lastName"));
    }
    
    /**
     * Negative test case for updateInvoice method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteInvoiceWithMandatoryParameters" }, description = "Freshbooks {updateInvoice} integration test with negative.")
    public void testUpdateInvoiceNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateInvoice");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInvoice_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateInvoice_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for sendInvoiceByEmail method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateInvoiceWithOptionalParameters" }, description = "Freshbooks {sendInvoiceByEmail} integration test with mandatory parameters.")
    public void testSendInvoiceByEmailWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:sendInvoiceByEmail");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendInvoiceByEmail_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendInvoiceByEmail_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "ok");
    }
    
    /**
     * Positive test case for sendInvoiceByEmail method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendInvoiceByEmailWithMandatoryParameters" }, description = "Freshbooks {sendInvoiceByEmail} integration test with optional parameters.")
    public void testSendInvoiceByEmailWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:sendInvoiceByEmail");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendInvoiceByEmail_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendInvoiceByEmail_optional.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "ok");
    }
    
    /**
     * Negative test case for sendInvoiceByEmail method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteInvoiceWithMandatoryParameters" }, description = "Freshbooks {sendInvoiceByEmail} integration test with negative.")
    public void testSendInvoiceByEmailNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:sendInvoiceByEmail");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendInvoiceByEmail_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendInvoiceByEmail_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for addInvoiceLine method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendInvoiceByEmailWithOptionalParameters" }, description = "Freshbooks {addInvoiceLine} integration test with mandatory parameters.")
    public void testAddInvoiceLineWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:addInvoiceLine");
        parametersMap.put("invoiceLineName", "NewLineInvoice");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addInvoiceLine_mandatory.xml",
                        parametersMap);
        
        connectorProperties.setProperty("invoiceLineIdMandatory",
                getValueByExpression("//*[local-name()='line_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addInvoiceLine_mandatory.xml",
                        parametersMap);
        
        final QName linesQName = new QName("http://www.freshbooks.com/api/", "lines");
        final QName lineIdQName = new QName("http://www.freshbooks.com/api/", "line_id");
        final QName nameQName = new QName("http://www.freshbooks.com/api/", "name");
        
        final OMElement linesElem = apiRestResponse.getBody().getFirstElement().getFirstChildWithName(linesQName);
        @SuppressWarnings("unchecked")
        final Iterator<OMElement> iterElem = linesElem.getChildElements();
        OMElement selectedLine = null;
        
        while (iterElem.hasNext()) {
            selectedLine = iterElem.next();
            
            if (connectorProperties.getProperty("invoiceLineIdMandatory").equals(
                    selectedLine.getFirstChildWithName(lineIdQName))) {
                break;
            }
        }
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(selectedLine.getFirstChildWithName(nameQName).getText(),
                parametersMap.get("invoiceLineName"));
    }
    
    /**
     * Positive test case for addInvoiceLine method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddInvoiceLineWithMandatoryParameters" }, description = "Freshbooks {addInvoiceLine} integration test with optional parameters.")
    public void testAddInvoiceLineWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:addInvoiceLine");
        parametersMap.put("description", "Description");
        parametersMap.put("quantity", "40");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addInvoiceLine_optional.xml",
                        parametersMap);
        
        connectorProperties.setProperty("invoiceLineIdOptional",
                getValueByExpression("//*[local-name()='line_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addInvoiceLine_optional.xml",
                        parametersMap);
        
        final QName linesQName = new QName("http://www.freshbooks.com/api/", "lines");
        final QName lineIdQName = new QName("http://www.freshbooks.com/api/", "line_id");
        final QName descriptionQName = new QName("http://www.freshbooks.com/api/", "description");
        final QName quantityQName = new QName("http://www.freshbooks.com/api/", "quantity");
        
        final OMElement linesElem = apiRestResponse.getBody().getFirstElement().getFirstChildWithName(linesQName);
        @SuppressWarnings("unchecked")
        final Iterator<OMElement> iterElem = linesElem.getChildElements();
        OMElement selectedLine = null;
        
        while (iterElem.hasNext()) {
            selectedLine = iterElem.next();
            
            if (connectorProperties.getProperty("invoiceLineIdOptional").equals(
                    selectedLine.getFirstChildWithName(lineIdQName))) {
                break;
            }
        }
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(selectedLine.getFirstChildWithName(descriptionQName).getText(),
                parametersMap.get("description"));
        Assert.assertEquals(selectedLine.getFirstChildWithName(quantityQName).getText(), parametersMap.get("quantity"));
    }
    
    /**
     * Negative test case for addInvoiceLine method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteInvoiceWithMandatoryParameters" }, description = "Freshbooks {addInvoiceLine} integration test with negative.")
    public void testAddInvoiceLineNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:addInvoiceLine");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addInvoiceLine_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addInvoiceLine_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for updateInvoiceLine method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddInvoiceLineWithOptionalParameters" }, description = "Freshbooks {updateInvoiceLine} integration test with optional parameters.")
    public void testUpdateInvoiceLineWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateInvoiceLine");
        parametersMap.put("description", "Updated description");
        parametersMap.put("compoundTax", "1");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInvoiceLine_optional.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateInvoiceLine_optional.xml",
                        parametersMap);
        
        final QName linesQName = new QName("http://www.freshbooks.com/api/", "lines");
        final QName lineIdQName = new QName("http://www.freshbooks.com/api/", "line_id");
        final QName descriptionQName = new QName("http://www.freshbooks.com/api/", "description");
        final QName quantityQName = new QName("http://www.freshbooks.com/api/", "compound_tax");
        
        final OMElement linesElem = apiRestResponse.getBody().getFirstElement().getFirstChildWithName(linesQName);
        @SuppressWarnings("unchecked")
        final Iterator<OMElement> iterElem = linesElem.getChildElements();
        OMElement selectedLine = null;
        
        while (iterElem.hasNext()) {
            selectedLine = iterElem.next();
            
            if (connectorProperties.getProperty("invoiceLineIdOptional").equals(
                    selectedLine.getFirstChildWithName(lineIdQName))) {
                break;
            }
        }
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(selectedLine.getFirstChildWithName(descriptionQName).getText(),
                parametersMap.get("description"));
        Assert.assertEquals(selectedLine.getFirstChildWithName(quantityQName).getText(),
                parametersMap.get("compoundTax"));
    }
    
    /**
     * Negative test case for updateInvoiceLine method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteInvoiceWithMandatoryParameters" }, description = "Freshbooks {updateInvoiceLine} integration test with negative.")
    public void testUpdateInvoiceLineNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateInvoiceLine");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInvoiceLine_negative.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateInvoiceLine_negative.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for deleteInvoiceLine method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateInvoiceLineWithOptionalParameters" }, description = "Freshbooks {deleteInvoiceLine} integration test with mandatory parameters.")
    public void testDeleteInvoiceLineWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteInvoiceLine");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteInvoiceLine_mandatory.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteInvoiceLine_mandatory.xml",
                        parametersMap);
        
        final QName linesQName = new QName("http://www.freshbooks.com/api/", "lines");
        final QName lineIdQName = new QName("http://www.freshbooks.com/api/", "line_id");
        
        final OMElement linesElem = apiRestResponse.getBody().getFirstElement().getFirstChildWithName(linesQName);
        @SuppressWarnings("unchecked")
        final Iterator<OMElement> iterElem = linesElem.getChildElements();
        OMElement selectedLine = null;
        boolean found = false;
        
        while (iterElem.hasNext()) {
            selectedLine = iterElem.next();
            
            if (connectorProperties.getProperty("invoiceLineIdOptional").equals(
                    selectedLine.getFirstChildWithName(lineIdQName))) {
                found = true;
            }
        }
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertFalse(found);
    }
    
    /**
     * Negative test case for deleteInvoiceLine method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteInvoiceLineWithMandatoryParameters" }, description = "Freshbooks {deleteInvoiceLine} integration test with negative.")
    public void testDeleteInvoiceLineNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteInvoiceLine");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteInvoiceLine_negative.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteInvoiceLine_negative.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for deleteInvoice method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteInvoiceLineNegativeCase" }, description = "Freshbooks {deleteInvoice} integration test with mandatory parameters.")
    public void testDeleteInvoiceWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteInvoice");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteInvoice_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteInvoice_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "fail");
        Assert.assertNotNull(getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for deleteInvoice method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteInvoiceWithMandatoryParameters" }, description = "Freshbooks {deleteInvoice} integration test with negative.")
    public void testDeleteInvoiceNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteInvoice");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteInvoice_negative.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteInvoice_negative.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for createClient method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Freshbooks {createClient} integration test with mandatory parameters.")
    public void testCreateClientWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_mandatory.xml");
        
        connectorProperties.setProperty("clientIdMandatory",
                getValueByExpression("//*[local-name()='client_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createClient_mandatory.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(Integer.parseInt(getValueByExpression("//*[local-name()='client_id']/text()",
                esbRestResponse.getBody())), Integer.parseInt(getValueByExpression(
                "//*[local-name()='client_id']/text()", apiRestResponse.getBody())));
        
    }
    
    /**
     * Positive test case for createClient method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateClientWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createClient} integration test with optional parameters.")
    public void testCreateClientWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        parametersMap.put("firstName", "Jane");
        parametersMap.put("organization", "ABC Corp");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_optional.xml",
                        parametersMap);
        
        connectorProperties.setProperty("clientIdOptional",
                getValueByExpression("//*[local-name()='client_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createClient_optional.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                Integer.parseInt(getValueByExpression("//*[local-name()='client_id']", esbRestResponse.getBody())),
                Integer.parseInt(getValueByExpression("//*[local-name()='client_id']", apiRestResponse.getBody())));
        Assert.assertEquals(getValueByExpression("//*[local-name()='first_name']", apiRestResponse.getBody()),
                parametersMap.get("firstName"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='organization']", apiRestResponse.getBody()),
                parametersMap.get("organization"));
    }
    
    /**
     * Negative test case for createClient methods.
     */
    @Test(dependsOnMethods = { "testCreateClientWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createClient} negative integration test.")
    public void testCreateClientNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createClient_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='code']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='code']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for updateClient method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateClientWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {updateClient} integration test with optional parameters.")
    public void testUpdateClientWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateClient");
        parametersMap.put("organization", "ABC Corp Updated");
        parametersMap.put("email", "wso2connectorupdated@gmail.com");
        parametersMap.put("homePhone", "(555) 234-5678");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateClient_optional.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateClient_optional.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='organization']", apiRestResponse.getBody()),
                parametersMap.get("organization"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='email']", apiRestResponse.getBody()),
                parametersMap.get("email"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='home_phone']", apiRestResponse.getBody()),
                parametersMap.get("homePhone"));
    }
    
    /**
     * Negative test case for updateClient methods.
     */
    @Test(dependsOnMethods = { "testDeleteClientWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {updateClient} negative integration test.")
    public void testUpdateClientNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateClient");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateClient_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateClient_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='code']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='code']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getClient method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateClientWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {getClient} integration test with mandatory parameters.")
    public void testGetClientWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getClient");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getClient_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getClient_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(Integer.parseInt(getValueByExpression("//*[local-name()='client_id']/text()",
                esbRestResponse.getBody())), Integer.parseInt(getValueByExpression(
                "//*[local-name()='client_id']/text()", apiRestResponse.getBody())));
        Assert.assertEquals(getValueByExpression("//*[local-name()='email']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='email']/text()", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for getClient methods.
     */
    @Test(dependsOnMethods = { "testDeleteClientWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {getClient} negative integration test.")
    public void testGetClientNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getClient");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getClient_negative.xml", parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getClient_negative.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='code']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='code']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for deleteClient method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testGetClientWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {deleteClient} integration test with mandatory parameters.")
    public void testDeleteClientWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteClient");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteClient_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteClient_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "fail");
        Assert.assertNotNull(getValueByExpression("//*[local-name()='error']", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for deleteClient methods.
     */
    @Test(dependsOnMethods = { "testDeleteClientWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {deleteClient} negative integration test.")
    public void testDeleteClientNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteClient");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteClient_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteClient_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listClients method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testUpdateClientWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listClients} integration test with mandatory parameters.")
    public void testListClientsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listClients");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listClients_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='clients']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='clients']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='client'][1]/*[local-name()='client_id']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='client'][1]/*[local-name()='client_id']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listClients method with optional parameters.
     */
    @Test(dependsOnMethods = { "testListClientsWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listClients} integration test with optional parameters.")
    public void testListClientsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listClients");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listClients_optional.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='clients']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='clients']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='client'][1]/*[local-name()='email']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='client'][1]/*[local-name()='email']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for listClients methods.
     */
    @Test(dependsOnMethods = { "testListClientsWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listClients} negative integration test.")
    public void testListClientsNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listClients");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listClients_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for createPayment method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateClientWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createPayment} integration test with mandatory parameters.")
    public void testCreatePaymentWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createPayment");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPayment_mandatory.xml");
        
        connectorProperties.setProperty("paymentIdMandatory",
                getValueByExpression("//*[local-name()='payment_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPayment_mandatory.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                Integer.parseInt(getValueByExpression("//*[local-name()='payment_id']/text()",
                        esbRestResponse.getBody())),
                Integer.parseInt(getValueByExpression("//*[local-name()='payment_id']/text()",
                        apiRestResponse.getBody())));
    }
    
    /**
     * Positive test case for createPayment method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreatePaymentWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createPayment} integration test with optional parameters.")
    public void testCreatePaymentWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createPayment");
        parametersMap.put("amount", "129.88");
        parametersMap.put("currencyCode", "CAD");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPayment_optional.xml",
                        parametersMap);
        
        connectorProperties.setProperty("paymentIdOptional",
                getValueByExpression("//*[local-name()='payment_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPayment_optional.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                Integer.parseInt(getValueByExpression("//*[local-name()='payment_id']", esbRestResponse.getBody())),
                Integer.parseInt(getValueByExpression("//*[local-name()='payment_id']", apiRestResponse.getBody())));
        Assert.assertEquals(new DecimalFormat("#.##").format(new Float(getValueByExpression(
                "//*[local-name()='amount']", apiRestResponse.getBody()))), parametersMap.get("amount"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='currency_code']", apiRestResponse.getBody()),
                parametersMap.get("currencyCode"));
    }
    
    /**
     * Negative test case for createPayment methods.
     */
    @Test(dependsOnMethods = { "testCreatePaymentWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createPayment} negative integration test.")
    public void testCreatePaymentNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createPayment");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPayment_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPayment_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='code']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='code']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for updatePayment method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreatePaymentWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {updatePayment} integration test with optional parameters.")
    public void testUpdatePaymentWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updatePayment");
        parametersMap.put("amount", "150.44");
        parametersMap.put("currencyCode", "USD");
        parametersMap.put("notes", "Optional Payment Note Updated");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePayment_optional.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updatePayment_optional.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(new DecimalFormat("#.##").format(new Float(getValueByExpression(
                "//*[local-name()='amount']", apiRestResponse.getBody()))), parametersMap.get("amount"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='currency_code']", apiRestResponse.getBody()),
                parametersMap.get("currencyCode"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='notes']", apiRestResponse.getBody()),
                parametersMap.get("notes"));
    }
    
    /**
     * Negative test case for updatePayment methods.
     */
    @Test(dependsOnMethods = { "testDeletePaymentWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {updatePayment} negative integration test.")
    public void testUpdatePaymentNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updatePayment");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePayment_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updatePayment_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='code']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='code']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getPayment method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreatePaymentWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {getPayment} integration test with mandatory parameters.")
    public void testGetPaymentWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPayment");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayment_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getPayment_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(
                Integer.parseInt(getValueByExpression("//*[local-name()='payment_id']/text()",
                        esbRestResponse.getBody())),
                Integer.parseInt(getValueByExpression("//*[local-name()='payment_id']/text()",
                        apiRestResponse.getBody())));
        Assert.assertEquals(getValueByExpression("//*[local-name()='date']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='date']/text()", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for getPayment methods.
     */
    @Test(dependsOnMethods = { "testDeletePaymentWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {getPayment} negative integration test.")
    public void testGetPaymentNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getPayment");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayment_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getPayment_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='code']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='code']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for deletePayment method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testGetPaymentWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {deletePayment} integration test with mandatory parameters.")
    public void testDeletePaymentWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deletePayment");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePayment_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deletePayment_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "fail");
        Assert.assertNotNull(getValueByExpression("//*[local-name()='error']", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for deletePayment methods.
     */
    @Test(dependsOnMethods = { "testDeletePaymentWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {deletePayment} negative integration test.")
    public void testDeletePaymentNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deletePayment");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePayment_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deletePayment_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listPayments method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testUpdatePaymentWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listPayments} integration test with mandatory parameters.")
    public void testListPaymentsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listPayments");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPayments_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listPayments_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='payments']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='payments']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='payment'][1]/*[local-name()='payment_id']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='payment'][1]/*[local-name()='payment_id']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listPayments method with optional parameters.
     */
    @Test(dependsOnMethods = { "testListPaymentsWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listPayments} integration test with optional parameters.")
    public void testListPaymentsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listPayments");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPayments_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listPayments_optional.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='payments']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='payments']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='payment'][1]/*[local-name()='client_id']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='payment'][1]/*[local-name()='client_id']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for listPayments methods.
     */
    @Test(dependsOnMethods = { "testListPaymentsWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listPayments} negative integration test.")
    public void testListPaymentsNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listPayments");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPayments_negative.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listPayments_negative.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for createEstimate method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateClientWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createEstimate} integration test with mandatory parameters.")
    public void testCreateEstimateWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_mandatory.xml");
        
        connectorProperties.setProperty("estimateIdMandatory",
                getValueByExpression("//*[local-name()='estimate_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEstimate_mandatory.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                Integer.parseInt(getValueByExpression("//*[local-name()='estimate_id']/text()",
                        esbRestResponse.getBody())),
                Integer.parseInt(getValueByExpression("//*[local-name()='estimate_id']/text()",
                        apiRestResponse.getBody())));
    }
    
    /**
     * Positive test case for createEstimate method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateEstimateWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createEstimate} integration test with optional parameters.")
    public void testCreateEstimateWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        parametersMap.put("status", "draft");
        parametersMap.put("discount", "10");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_optional.xml",
                        parametersMap);
        
        connectorProperties.setProperty("estimateIdOptional",
                getValueByExpression("//*[local-name()='estimate_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEstimate_optional.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                Integer.parseInt(getValueByExpression("//*[local-name()='estimate_id']", esbRestResponse.getBody())),
                Integer.parseInt(getValueByExpression("//*[local-name()='estimate_id']", apiRestResponse.getBody())));
        Assert.assertEquals(getValueByExpression("//*[local-name()='status']", apiRestResponse.getBody()),
                parametersMap.get("status"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='discount']", apiRestResponse.getBody()),
                parametersMap.get("discount"));
    }
    
    /**
     * Negative test case for createEstimate methods.
     */
    @Test(dependsOnMethods = { "testCreateEstimateWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createEstimate} negative integration test.")
    public void testCreateEstimateNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEstimate_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='code']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='code']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for updateEstimate method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateEstimateWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {updateEstimate} integration test with optional parameters.")
    public void testUpdateEstimateWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateEstimate");
        parametersMap.put("discount", "15");
        parametersMap.put("currencyCode", "LKR");
        parametersMap.put("terms", "Payment due in 30 days");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEstimate_optional.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateEstimate_optional.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='discount']", apiRestResponse.getBody()),
                parametersMap.get("discount"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='currency_code']", apiRestResponse.getBody()),
                parametersMap.get("currencyCode"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='terms']", apiRestResponse.getBody()),
                parametersMap.get("terms"));
    }
    
    /**
     * Negative test case for updateEstimate methods.
     */
    @Test(dependsOnMethods = { "testDeleteEstimateWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {updateEstimate} negative integration test.")
    public void testUpdateEstimateNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateEstimate");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEstimate_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateEstimate_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='code']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='code']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getEstimate method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateEstimateWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {getEstimate} integration test with mandatory parameters.")
    public void testGetEstimateWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEstimate");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEstimate_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getEstimate_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(
                Integer.parseInt(getValueByExpression("//*[local-name()='estimate_id']/text()",
                        esbRestResponse.getBody())),
                Integer.parseInt(getValueByExpression("//*[local-name()='estimate_id']/text()",
                        apiRestResponse.getBody())));
        Assert.assertEquals(Integer.parseInt(getValueByExpression("//*[local-name()='client_id']/text()",
                esbRestResponse.getBody())), Integer.parseInt(getValueByExpression(
                "//*[local-name()='client_id']/text()", apiRestResponse.getBody())));
    }
    
    /**
     * Negative test case for getEstimate methods.
     */
    @Test(dependsOnMethods = { "testDeleteEstimateWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {getEstimate} negative integration test.")
    public void testGetEstimateNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEstimate");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEstimate_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getEstimate_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='code']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='code']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for deleteEstimate method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testGetEstimateWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {deleteEstimate} integration test with mandatory parameters.")
    public void testDeleteEstimateWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEstimate");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEstimate_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteEstimate_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "fail");
        Assert.assertNotNull(getValueByExpression("//*[local-name()='error']", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for deleteEstimate methods.
     */
    @Test(dependsOnMethods = { "testDeleteEstimateWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {deleteEstimate} negative integration test.")
    public void testDeleteEstimateNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEstimate");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEstimate_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteEstimate_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listEstimates method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testUpdateEstimateWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listEstimates} integration test with mandatory parameters.")
    public void testListEstimatesWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listEstimates");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEstimates_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listEstimates_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='estimates']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='estimates']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='estimate'][1]/*[local-name()='estimate_id']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='estimate'][1]/*[local-name()='estimate_id']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listEstimates method with optional parameters.
     */
    @Test(dependsOnMethods = { "testListEstimatesWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listEstimates} integration test with optional parameters.")
    public void testListEstimatesWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listEstimates");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEstimates_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listEstimates_optional.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='estimates']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='estimates']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='estimate'][1]/*[local-name()='client_id']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='estimate'][1]/*[local-name()='client_id']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for listEstimates methods.
     */
    @Test(dependsOnMethods = { "testListEstimatesWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {listEstimates} negative integration test.")
    public void testListEstimatesNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listEstimates");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEstimates_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listEstimates_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for sendEstimateByEmail method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testUpdateEstimateWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {sendEstimateByEmail} integration test with mandatory parameters.")
    public void testSendEstimateByEmailWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:sendEstimateByEmail");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendEstimateByEmail_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendEstimateByEmail_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for sendEstimateByEmail method with optional parameters.
     */
    @Test(dependsOnMethods = { "testSendEstimateByEmailWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {sendEstimateByEmail} integration test with optional parameters.")
    public void testSendEstimateByEmailWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:sendEstimateByEmail");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendEstimateByEmail_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendEstimateByEmail_optional.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for sendEstimateByEmail methods.
     */
    @Test(dependsOnMethods = { "testDeleteEstimateWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {sendEstimateByEmail} negative integration test.")
    public void testSendEstimateByEmailNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:sendEstimateByEmail");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendEstimateByEmail_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendEstimateByEmail_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for createTax method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Freshbooks {createTax} integration test with mandatory parameters.")
    public void testCreateTaxWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createTax");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTax_mandatory.xml");
        
        connectorProperties.setProperty("taxIdMandatory",
                getValueByExpression("//*[local-name()='tax_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getTax_mandatory.xml", parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='name']/text()", apiRestResponse.getBody()),
                connectorProperties.getProperty("taxName"));
    }
    
    /**
     * Positive test case for createTax method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaxWithMandatoryParameters" }, description = "Freshbooks {createTax} integration test with optional parameters.")
    public void testCreateTaxWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createTax");
        parametersMap.put("rate", "20");
        parametersMap.put("number", "25000");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTax_optional.xml", parametersMap);
        
        connectorProperties.setProperty("taxIdOptional",
                getValueByExpression("//*[local-name()='tax_id']", esbRestResponse.getBody()));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getTax_optional.xml", parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='rate']", apiRestResponse.getBody()),
                parametersMap.get("rate"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='number']", apiRestResponse.getBody()),
                parametersMap.get("number"));
    }
    
    /**
     * Negative test case for createTax method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaxWithOptionalParameters" }, description = "Freshbooks {createTax} integration test with negative.")
    public void testCreateTaxNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createTax");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTax_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTax_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getTax method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaxWithMandatoryParameters" }, description = "Freshbooks {getTax} integration test with mandatory parameters.")
    public void testGetTaxWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getTax");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTax_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getTax_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='name']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='name']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for getTax methods.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteTaxWithMandatoryParameters" }, description = "Freshbooks {getTax} negative integration test.")
    public void testGetTaxNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getTax");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTax_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getTax_negative.xml");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for updateTax method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaxWithOptionalParameters" }, description = "Freshbooks {updateTax} integration test with optional parameters.")
    public void testUpdateTaxWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateTax");
        parametersMap.put("rate", "50");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTax_optional.xml", parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateTax_optional.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(getValueByExpression("//*[local-name()='name']/text()", apiRestResponse.getBody()),
                connectorProperties.getProperty("taxNameUpdated"));
        Assert.assertEquals(getValueByExpression("//*[local-name()='rate']/text()", apiRestResponse.getBody()),
                parametersMap.get("rate"));
    }
    
    /**
     * Negative test case for updateTax method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteTaxWithMandatoryParameters" }, description = "Freshbooks {updateTax} integration test with negative.")
    public void testUpdateTaxNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateTax");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTax_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateTax_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listTax method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateTaxWithOptionalParameters" }, description = "Freshbooks {listTax} integration test with mandatory parameters.")
    public void testListTaxWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listTaxes");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTax_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listTax_mandatory.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='taxes']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='taxes']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='tax'][1]/*[local-name()='tax_id']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='tax'][1]/*[local-name()='tax_id']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listTax method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListTaxWithMandatoryParameters" }, description = "Freshbooks {listTax} integration test with optional parameters.")
    public void testListTaxWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listTaxes");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTax_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listTax_optional.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='taxes']/@total)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='taxes']/@total)", apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='tax'][1]/*[local-name()='page']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='tax'][1]/*[local-name()='page']/text()",
                        apiRestResponse.getBody()));
        Assert.assertEquals(
                getValueByExpression("//*[local-name()='tax'][1]/*[local-name()='perPage']/text()",
                        esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='tax'][1]/*[local-name()='perPage']/text()",
                        apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for listTax method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListTaxWithOptionalParameters" }, description = "Freshbooks {listTax} integration test with negative.")
    public void testListTaxNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listTaxes");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTax_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listTax_negative.xml");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for deleteTax method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListTaxNegativeCase" }, description = "Freshbooks {deleteTax} integration test with optional parameters.")
    public void testDeleteTaxWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteTax");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTax_mandatory.xml", parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteTax_mandatory.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "fail");
        Assert.assertNotNull(getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for deleteTax method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteTaxWithMandatoryParameters" }, description = "Freshbooks {deleteTax} integration test with negative.")
    public void testDeleteTaxNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteTax");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTax_negative.xml", parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteTax_negative.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for createReceipt method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testUpdateTaxNegativeCase" }, groups = { "wso2.esb" }, description = "Freshbooks {createReceipt} integration test with mandatory parameters.")
    public void testCreateReceiptWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createReceipt");
        parametersMap.put("contentId", "receipt_image");
        String esbRequestUrl =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&authenticationToken="
                        + connectorProperties.getProperty("authenticationToken") + "&arbitraryPassword="
                        + connectorProperties.getProperty("arbitraryPassword");
        
        MultipartFormdataProcessor multiPartProcessor =
                new MultipartFormdataProcessor(esbRequestUrl, esbRequestHeadersMap, MULTIPART_TYPE_RELATED);
        multiPartProcessor.addMetadataToMultipartRelatedRequest("esb_createReceipt_mandatory.xml", "application/xml",
                "UTF-8", parametersMap);
        multiPartProcessor.addFileToMultipartRelatedRequest(connectorProperties.getProperty("uploadFileName"),
                "receipt_image");
        
        RestResponse<OMElement> esbRestResponse = multiPartProcessor.processForXmlResponse();
        
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("uploadFileName"));
        String originalFileHash = getFileHash(new FileInputStream(file));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        InputStream isApi =
                processForInputStream(apiEndPoint, "POST", apiRequestHeadersMap, "api_createReceipt_mandatory.xml",
                        parametersMap);
        String apiFileHash = getFileHash(isApi);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        
        Assert.assertEquals(apiFileHash, originalFileHash);
        
    }
    
    /**
     * Negative test case for createReceipt method.
     */
    @Test(dependsOnMethods = { "testCreateReceiptWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {createReceipt} integration test with negative.")
    public void testCreateReceiptNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createReceipt");
        parametersMap.put("contentId", "receipt_image");
        String esbRequestUrl =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&authenticationToken="
                        + connectorProperties.getProperty("authenticationToken") + "&arbitraryPassword="
                        + connectorProperties.getProperty("arbitraryPassword");
        
        MultipartFormdataProcessor multiPartProcessor =
                new MultipartFormdataProcessor(esbRequestUrl, esbRequestHeadersMap, MULTIPART_TYPE_RELATED);
        multiPartProcessor.addMetadataToMultipartRelatedRequest("esb_createReceipt_negative.xml", "application/xml",
                "UTF-8", parametersMap);
        multiPartProcessor.addFileToMultipartRelatedRequest("clinicalcenter.png", "receipt_image");
        
        RestResponse<OMElement> esbRestResponse = multiPartProcessor.processForXmlResponse();
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "fail");
        
    }
    
    /**
     * Positive test case for updateReceipt method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateReceiptNegativeCase" }, groups = { "wso2.esb" }, description = "Freshbooks {updateReceipt} integration test with mandatory parameters.")
    public void testUpdateReceiptWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateReceipt");
        parametersMap.put("contentId", "receipt_image");
        String esbRequestUrl =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&authenticationToken="
                        + connectorProperties.getProperty("authenticationToken") + "&arbitraryPassword="
                        + connectorProperties.getProperty("arbitraryPassword");
        
        MultipartFormdataProcessor multiPartProcessor =
                new MultipartFormdataProcessor(esbRequestUrl, esbRequestHeadersMap, MULTIPART_TYPE_RELATED);
        multiPartProcessor.addMetadataToMultipartRelatedRequest("esb_updateReceipt_mandatory.xml", "application/xml",
                "UTF-8", parametersMap);
        multiPartProcessor.addFileToMultipartRelatedRequest(connectorProperties.getProperty("updatedFile"),
                "receipt_image");
        
        RestResponse<OMElement> esbRestResponse = multiPartProcessor.processForXmlResponse();
        
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("updatedFile"));
        String originalFileHash = getFileHash(new FileInputStream(file));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        InputStream isApi =
                processForInputStream(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateReceipt_mandatory.xml",
                        parametersMap);
        String apiFileHash = getFileHash(isApi);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        
        Assert.assertEquals(apiFileHash, originalFileHash);
    }
    
    /**
     * Negative test case for updateReceipt method.
     */
    @Test(dependsOnMethods = { "testUpdateReceiptWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {updateReceipt} integration test with negative.")
    public void testUpdateReceiptNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateReceipt");
        parametersMap.put("contentId", "receipt_image");
        String esbRequestUrl =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&authenticationToken="
                        + connectorProperties.getProperty("authenticationToken") + "&arbitraryPassword="
                        + connectorProperties.getProperty("arbitraryPassword");
        
        MultipartFormdataProcessor multiPartProcessor =
                new MultipartFormdataProcessor(esbRequestUrl, esbRequestHeadersMap, MULTIPART_TYPE_RELATED);
        multiPartProcessor.addMetadataToMultipartRelatedRequest("esb_updateReceipt_negative.xml", "application/xml",
                "UTF-8", parametersMap);
        multiPartProcessor.addFileToMultipartRelatedRequest("updated_receipt.png", "receipt_image");
        
        RestResponse<OMElement> esbRestResponse = multiPartProcessor.processForXmlResponse();
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "fail");
        
    }
    
    /**
     * Positive test case for getReceipt method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testUpdateReceiptNegativeCase" }, groups = { "wso2.esb" }, description = "Freshbooks {getReceipt} integration test with mandatory parameters.")
    public void testGetReceiptWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getReceipt");
        esbRequestHeadersMap.put("Content-Type", "application/xml");
        InputStream isEsb =
                processForInputStream(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReceipt_mandatory.xml",
                        parametersMap);
        String esbFileHash = getFileHash(isEsb);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        InputStream isApi =
                processForInputStream(apiEndPoint, "POST", apiRequestHeadersMap, "api_getReceipt_mandatory.xml",
                        parametersMap);
        String apiFileHash = getFileHash(isApi);
        
        Assert.assertEquals(esbFileHash, apiFileHash);
        
    }
    
    /**
     * Negative test case for getReceipt methods.
     */
    @Test(dependsOnMethods = { "testGetReceiptWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Freshbooks {getReceipt} negative integration test.")
    public void testGetReceiptNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getReceipt");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReceipt_negative.xml", parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getReceipt_negative.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='code']", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='code']", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for deleteReceipt method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testGetReceiptNegativeCase" }, groups = { "wso2.esb" }, description = "Freshbooks {deleteReceipt} integration test with mandatory parameters.")
    public void testDeleteReceiptWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteReceipt");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteReceipt_mandatory.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteReceipt_mandatory.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()), "ok");
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()), "fail");
        Assert.assertNotNull(getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for deleteReceipt method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteReceiptWithMandatoryParameters" }, description = "Freshbooks {deleteReceipt} integration test with negative.")
    public void testDeleteReceiptNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteReceipt");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteReceipt_negative.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + API_URL_REMAINDER;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_deleteReceipt_negative.xml",
                        parametersMap);
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='response']/@status)", esbRestResponse.getBody()),
                getValueByExpression("string(//*[local-name()='response']/@status)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='error']/text()", esbRestResponse.getBody()),
                getValueByExpression("//*[local-name()='error']/text()", apiRestResponse.getBody()));
    }
    
    private String getFileHash(InputStream in) throws IOException, NoSuchAlgorithmException {
    
        MessageDigest md = MessageDigest.getInstance("SHA1");
        
        byte[] dataBytes = new byte[1024];
        
        int nread = 0;
        
        while ((nread = in.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };
        
        byte[] mdbytes = md.digest();
        
        // convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    
}
