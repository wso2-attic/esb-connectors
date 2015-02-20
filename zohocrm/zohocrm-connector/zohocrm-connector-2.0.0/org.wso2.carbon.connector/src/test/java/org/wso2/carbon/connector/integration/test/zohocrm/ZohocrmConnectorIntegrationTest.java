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

package org.wso2.carbon.connector.integration.test.zohocrm;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ZohocrmConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("zohocrm-connector-2.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        createResources();
    }
    
    /**
     * This method Creates the resources within the Zoho account to be used in API calls to follow.
     * 
     * @throws IOException
     * @throws JSONException
     */
    private void createResources() throws IOException, JSONException {
    
        String apiEndPointCreateMandatoryLead =
                connectorProperties.getProperty("apiUrl")
                        + "/crm/private/json/Leads/insertRecords?authtoken="
                        + connectorProperties.getProperty("accessToken")
                        + "&scope="
                        + connectorProperties.getProperty("scope")
                        + "&xmlData="
                        + URLEncoder
                                .encode("<Leads> <row no=\"1\"> <FL val=\"First Name\"></FL> <FL val=\"Last Name\">sample lead mandatory</FL> </row> </Leads>",
                                        Charset.defaultCharset().name());
        
        String apiEndPointCreateOptionalLead =
                connectorProperties.getProperty("apiUrl")
                        + "/crm/private/json/Leads/insertRecords?authtoken="
                        + connectorProperties.getProperty("accessToken")
                        + "&scope="
                        + connectorProperties.getProperty("scope")
                        + "&xmlData="
                        + URLEncoder
                                .encode("<Leads> <row no=\"1\"> <FL val=\"First Name\"></FL> <FL val=\"Last Name\">sample lead optional</FL> </row> </Leads>",
                                        Charset.defaultCharset().name());
        
        RestResponse<JSONObject> createLeadMandatoryApiRestResponse =
                sendJsonRestRequest(apiEndPointCreateMandatoryLead, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> createLeadOptionalApiRestResponse =
                sendJsonRestRequest(apiEndPointCreateOptionalLead, "GET", apiRequestHeadersMap);
        
        connectorProperties.put("leadIdMandatory",
                createLeadMandatoryApiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("recorddetail").getJSONArray("FL").getJSONObject(0).get("content").toString());
        connectorProperties.put("leadIdOptional",
                createLeadOptionalApiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("recorddetail").getJSONArray("FL").getJSONObject(0).get("content").toString());
    }
    
    /**
     * Positive test case for insertRecords method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "zohocrm {insertRecords} integration test with mandatory parameters")
    public void testInsertRecordsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:insertRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertRecords_mandatory.json");
        String recordId =
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("recorddetail").getJSONArray("FL").getJSONObject(0).get("content").toString();
        connectorProperties.put("recordId", recordId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/getRecordById?id="
                        + connectorProperties.getProperty("recordId") + "&authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertFalse(apiRestResponse.getBody().getJSONObject("response").has("nodata"));
    }
    
    /**
     * Positive test case for insertRecords method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testInsertRecordsWithMandatoryParameters" }, description = "zohocrm {insertRecords} integration test with optional parameters")
    public void testInsertRecordsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:insertRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertRecords_optional.json");
        String recordId =
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("recorddetail").getJSONArray("FL").getJSONObject(0).get("content").toString();
        connectorProperties.put("recordId", recordId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/getRecordById?id="
                        + connectorProperties.getProperty("recordId") + "&authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&newFormat=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertFalse(apiRestResponse.getBody().getJSONObject("response").has("nodata"));
    }
    
    /**
     * Negative test case for insertRecords method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testInsertRecordsWithOptionalParameters" }, description = "zohocrm {insertRecords} integration test negative case")
    public void testInsertRecordsNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:insertRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertRecords_negative.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/crm/private/json/Leads/insertRecords?authtoken="
                        + connectorProperties.getProperty("accessToken")
                        + "&scope="
                        + connectorProperties.getProperty("scope")
                        + "&newFormat=1&xmlData="
                        + URLEncoder
                                .encode("<Campaigns> <row no=\"1\"> <FL val=\"Campaign Name\">First Campaign</FL> </row> </Campaigns>",
                                        Charset.defaultCharset().name());
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString());
    }
    
    /**
     * Positive test case for convertLead method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testInsertRecordsNegativeCase" }, description = "zohocrm {convertLead} integration test with mandatory parameters")
    public void testConvertLeadWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:convertLead");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_convertLead_mandatory.json");
        String contactIdMandatory =
                esbRestResponse.getBody().getJSONObject("success").getJSONObject("Contact").get("content").toString();
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Contacts/getRecordById?id="
                        + contactIdMandatory + "&authtoken=" + connectorProperties.getProperty("accessToken")
                        + "&scope=" + connectorProperties.getProperty("scope");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertFalse(apiRestResponse.getBody().getJSONObject("response").has("nodata"));
    }
    
    /**
     * Positive test case for convertLead method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testConvertLeadWithMandatoryParameters" }, description = "zohocrm {convertLead} integration test with optional parameters")
    public void testConvertLeadWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:convertLead");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_convertLead_optional.json");
        String contactIdOptional =
                esbRestResponse.getBody().getJSONObject("success").getJSONObject("Contact").get("content").toString();
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Contacts/getRecordById?id="
                        + contactIdOptional + "&authtoken=" + connectorProperties.getProperty("accessToken")
                        + "&scope=" + connectorProperties.getProperty("scope");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertFalse(apiRestResponse.getBody().getJSONObject("response").has("nodata"));
    }
    
    /**
     * Negative test case for convertLead method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testConvertLeadWithOptionalParameters" }, description = "zohocrm {convertLead} integration test negative case")
    public void testConvertLeadNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:convertLead");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_convertLead_negative.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/crm/private/json/Leads/convertLead?leadId=1234"
                        + "&authtoken="
                        + connectorProperties.getProperty("accessToken")
                        + "&scope="
                        + connectorProperties.getProperty("scope")
                        + "&xmlData="
                        + URLEncoder
                                .encode("<Potentials> <row no=\"1\"> <option val=\"createPotential\">true</option> <option val=\"assignTo\">sample@zoho.com</option> <option val=\"notifyLeadOwner\">true</option> <option val=\"notifyNewEntityOwner\">true</option> </row> <row no=\"2\"> <FL val=\"Potential Name\">Samplepotential</FL> <FL val=\"Closing Date\">12/21/2009</FL><FL val=\"Potential Stage\">Closed Won</FL> <FL val=\"Contact Role\">Purchasing</FL> <FL val=\"Amount\">3432.23</FL> <FL val=\"Probability\">100</FL> </row> </Potentials>",
                                        Charset.defaultCharset().name());
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString());
    }
    
    /**
     * Positive test case for getRecordsById method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testConvertLeadNegativeCase" }, description = "zohocrm {getRecordsById} integration test with mandatory parameters")
    public void testGetRecordsByIdWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRecordsById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecordsById_mandatory.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/getRecordById?id="
                        + connectorProperties.getProperty("recordId") + "&authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns")
                        .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("Campaigns").toString());
    }
    
    /**
     * Positive test case for getRecordsById method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordsByIdWithMandatoryParameters" }, description = "zohocrm {getRecordsById} integration test with optional parameters")
    public void testGetRecordsByIdWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRecordsById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecordsById_optional.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/getRecordById?newFormat=1&id="
                        + connectorProperties.getProperty("recordId") + "&authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns")
                        .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("Campaigns").toString());
    }
    
    /**
     * Negative test case for getRecordsById.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordsByIdWithOptionalParameters" }, description = "zohocrm {getRecordsById} integration test with optional parameters")
    public void testGetRecordsByIdNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRecordsById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecordsById_negative.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/getRecordById?version=0&id="
                        + connectorProperties.getProperty("recordId") + "&authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString());
    }
    
    /**
     * Positive test case for updateRecords method with mandatory parameters.
     */
    
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordsByIdNegativeCase" }, description = "zohocrm {updateRecords} integration test with mandatory parameters")
    public void testUpdateRecordsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRecords_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/getRecordById?id="
                        + connectorProperties.getProperty("recordId") + "&authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray jsonAry =
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns")
                        .getJSONObject("row").getJSONArray("FL");
        // loop was iterated backwardly since expected results tend to be at latter parts
        for (int i = jsonAry.length() - 1; i > -1; i--) {
            JSONObject flJson = new JSONObject(jsonAry.get(i).toString());
            if ("Description".equals(flJson.get("val"))
                    && connectorProperties.getProperty("updateDescription").equals(flJson.get("content"))) {
                Assert.assertTrue(true);
                return;
            }
            Assert.assertFalse(true);
        }
    }
    
    /**
     * Positive test case for updateRecords method with optional parameters.
     */
    
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateRecordsWithMandatoryParameters" }, description = "zohocrm {updateRecords} integration test with optional parameters")
    public void testUpdateRecordsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRecords_optional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/getRecordById?id="
                        + connectorProperties.getProperty("recordId") + "&authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray jsonAry =
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns")
                        .getJSONObject("row").getJSONArray("FL");
        // loop was iterated backwardly since expected results tend to be at latter parts
        for (int i = jsonAry.length() - 1; i > -1; i--) {
            JSONObject flJson = new JSONObject(jsonAry.get(i).toString());
            if ("Description".equals(flJson.get("val"))
                    && connectorProperties.getProperty("updateDescriptionOptional").equals(flJson.get("content"))) {
                Assert.assertTrue(true);
                return;
            }
            Assert.assertFalse(true);
        }
    }
    
    /**
     * Negative test case for updateRecords method.
     */
    
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateRecordsWithOptionalParameters" }, description = "zohocrm {updateRecords} integration test with negative case")
    public void testUpdateRecordsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRecords_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/updateRecords?id=-"
                        + "&authtoken=" + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&xmlData=%3CInvalid%3E%3C%2FInvalid%3E";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiJsonObj = apiRestResponse.getBody().getJSONObject("response").getJSONObject("error");
        JSONObject esbJsonObj = esbRestResponse.getBody().getJSONObject("response").getJSONObject("error");
        Assert.assertEquals(esbJsonObj.get("message").toString(), apiJsonObj.get("message").toString());
        Assert.assertEquals(esbJsonObj.get("code").toString(), apiJsonObj.get("code").toString());
    }
    
    /**
     * Positive test case for getRecords method with mandatory parameters.
     */
    
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateRecordsWithNegativeCase" }, description = "zohocrm {getRecords} integration test with mandatory parameters")
    public void testGetRecordsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecords_mandatory.json");
        JSONArray esbJsonAry =
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns")
                        .getJSONArray("row").getJSONObject(0).getJSONArray("FL");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/getRecords?authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiJsonAry =
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns")
                        .getJSONArray("row").getJSONObject(0).getJSONArray("FL");
        Assert.assertEquals(esbJsonAry.length(), apiJsonAry.length());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("content"), apiJsonAry.getJSONObject(0).get("content"));
    }
    
    /**
     * Positive test case for getRecords method with optional parameters.
     */
    
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordsWithMandatoryParameters" }, description = "zohocrm {getRecords} integration test with optional parameters")
    public void testGetRecordsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecords_optional.json");
        JSONArray esbJsonAry =
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns")
                        .getJSONArray("row").getJSONObject(0).getJSONArray("FL");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/getRecords?authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&newFormat=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiJsonAry =
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns")
                        .getJSONArray("row").getJSONObject(0).getJSONArray("FL");
        Assert.assertEquals(esbJsonAry.length(), apiJsonAry.length());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("content"), apiJsonAry.getJSONObject(0).get("content"));
    }
    
    /**
     * Negative test case for getRecords method.
     */
    
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordsWithOptionalParameters" }, description = "zohocrm {getRecords} integration test with negative case")
    public void testGetRecordsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecords_negative.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/getRecords?authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&version=0";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiJsonObj = apiRestResponse.getBody().getJSONObject("response").getJSONObject("error");
        JSONObject esbJsonObj = esbRestResponse.getBody().getJSONObject("response").getJSONObject("error");
        Assert.assertEquals(esbJsonObj.get("message"), apiJsonObj.get("message"));
        Assert.assertEquals(esbJsonObj.get("code"), apiJsonObj.get("code"));
    }
    
    /**
     * Positive test case for getRelatedRecords method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordsWithNegativeCase" }, description = "zohocrm {getRelatedRecords} integration test with mandatory parameters")
    public void testGetRelatedRecordsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRelatedRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRelatedRecords_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Leads/getRelatedRecords?id="
                        + connectorProperties.getProperty("parentModuleId") + "&authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&parentModule=Campaigns";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String leadIdESB = "";
        String leadIdAPI = "";
        String leadOwnerIdESB = "";
        String leadOwnerIdAPI = "";
        
        // JSON object structure changes when the result contains multiple rows
        if (esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                .get("row") instanceof JSONObject) {
            leadIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONObject("row").getJSONArray("FL").getJSONObject(0).get("content").toString();
            leadIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONObject("row").getJSONArray("FL").getJSONObject(0).get("content").toString();
            leadOwnerIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONObject("row").getJSONArray("FL").getJSONObject(1).get("content").toString();
            leadOwnerIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONObject("row").getJSONArray("FL").getJSONObject(1).get("content").toString();
        } else {
            leadIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONArray("row").getJSONObject(0).getJSONArray("FL").getJSONObject(0).get("content")
                            .toString();
            leadIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONArray("row").getJSONObject(0).getJSONArray("FL").getJSONObject(0).get("content")
                            .toString();
            leadOwnerIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONArray("row").getJSONObject(0).getJSONArray("FL").getJSONObject(1).get("content")
                            .toString();
            leadOwnerIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONArray("row").getJSONObject(0).getJSONArray("FL").getJSONObject(1).get("content")
                            .toString();
        }
        
        Assert.assertEquals(leadIdESB, leadIdAPI);
        Assert.assertEquals(leadOwnerIdESB, leadOwnerIdAPI);
    }
    
    /**
     * Positive test case for getRelatedRecords method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRelatedRecordsWithMandatoryParameters" }, description = "zohocrm {getRelatedRecords} integration test with optional parameters")
    public void testGetRelatedRecordsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRelatedRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRelatedRecords_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Leads/getRelatedRecords?id="
                        + connectorProperties.getProperty("parentModuleId") + "&authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&parentModule=Campaigns&" + "newFormat=1";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String leadIdESB = "";
        String leadIdAPI = "";
        String leadOwnerIdESB = "";
        String leadOwnerIdAPI = "";
        
        // JSON object structure changes when the result contains multiple rows
        if (esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                .get("row") instanceof JSONObject) {
            leadIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONObject("row").getJSONArray("FL").getJSONObject(0).get("content").toString();
            leadIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONObject("row").getJSONArray("FL").getJSONObject(0).get("content").toString();
            leadOwnerIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONObject("row").getJSONArray("FL").getJSONObject(1).get("content").toString();
            leadOwnerIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONObject("row").getJSONArray("FL").getJSONObject(1).get("content").toString();
        } else {
            leadIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONArray("row").getJSONObject(0).getJSONArray("FL").getJSONObject(0).get("content")
                            .toString();
            leadIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONArray("row").getJSONObject(0).getJSONArray("FL").getJSONObject(0).get("content")
                            .toString();
            leadOwnerIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONArray("row").getJSONObject(0).getJSONArray("FL").getJSONObject(1).get("content")
                            .toString();
            leadOwnerIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Leads")
                            .getJSONArray("row").getJSONObject(0).getJSONArray("FL").getJSONObject(1).get("content")
                            .toString();
        }
        
        Assert.assertEquals(leadIdESB, leadIdAPI);
        Assert.assertEquals(leadOwnerIdESB, leadOwnerIdAPI);
    }
    
    /**
     * Negative test case for getRelatedRecords method .
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRelatedRecordsWithOptionalParameters" }, description = "zohocrm {getRelatedRecords} integration test negative case")
    public void testGetRelatedRecordNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRelatedRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRelatedRecords_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Leads/getRelatedRecords?id=-"
                        + "&authtoken=" + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&parentModule=Campaigns";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString());
    }
    
    /**
     * Positive test case for getSearchRecords method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRelatedRecordNegativeCase" }, description = "zohocrm {getSearchRecords} integration test with mandatory parameters")
    public void testGetSearchRecordsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getSearchRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchRecords_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/crm/private/json/Campaigns/getSearchRecords?selectColumns="
                        + URLEncoder.encode("Campaigns(Campaign Name,Status)", Charset.defaultCharset().toString())
                        + "&authtoken=" + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&searchCondition="
                        + URLEncoder.encode("(Campaign Name|contains|*Campaign*)", Charset.defaultCharset().toString());
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        String campaignIdESB = "";
        String campaignIdAPI = "";
        String campaignStatusESB = "";
        String campaignStatusAPI = "";
        if (esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns")
                .get("row") instanceof JSONObject) {
            campaignIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONObject("row").getJSONArray("FL").getJSONObject(0)
                            .get("content").toString();
            campaignIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONObject("row").getJSONArray("FL").getJSONObject(0)
                            .get("content").toString();
            campaignStatusESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONObject("row").getJSONArray("FL").getJSONObject(1)
                            .get("content").toString();
            campaignStatusAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONObject("row").getJSONArray("FL").getJSONObject(1)
                            .get("content").toString();
        } else {
            campaignIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                            .getJSONObject(0).get("content").toString();
            campaignIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                            .getJSONObject(0).get("content").toString();
            campaignStatusESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                            .getJSONObject(1).get("content").toString();
            campaignStatusAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                            .getJSONObject(1).get("content").toString();
        }
        
        Assert.assertEquals(campaignIdESB, campaignIdAPI);
        Assert.assertEquals(campaignStatusESB, campaignStatusAPI);
    }
    
    /**
     * Positive test case for getSearchRecords method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSearchRecordsWithMandatoryParameters" }, description = "zohocrm {getSearchRecords} integration test with Optional parameters")
    public void testGetSearchRecordsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getSearchRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchRecords_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/crm/private/json/Campaigns/getSearchRecords?selectColumns="
                        + URLEncoder.encode("Campaigns(Campaign Name,Status)", Charset.defaultCharset().toString())
                        + "&authtoken=" + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&searchCondition="
                        + URLEncoder.encode("(Campaign Name|contains|*Campaign*)", Charset.defaultCharset().toString())
                        + "&newFormat=1";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        String campaignIdESB = "";
        String campaignIdAPI = "";
        String campaignStatusESB = "";
        String campaignStatusAPI = "";
        if (esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns")
                .get("row") instanceof JSONObject) {
            campaignIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONObject("row").getJSONArray("FL").getJSONObject(0)
                            .get("content").toString();
            campaignIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONObject("row").getJSONArray("FL").getJSONObject(0)
                            .get("content").toString();
            campaignStatusESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONObject("row").getJSONArray("FL").getJSONObject(1)
                            .get("content").toString();
            campaignStatusAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONObject("row").getJSONArray("FL").getJSONObject(1)
                            .get("content").toString();
        } else {
            campaignIdESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                            .getJSONObject(0).get("content").toString();
            campaignIdAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                            .getJSONObject(0).get("content").toString();
            campaignStatusESB =
                    esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                            .getJSONObject(1).get("content").toString();
            campaignStatusAPI =
                    apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                            .getJSONObject("Campaigns").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                            .getJSONObject(1).get("content").toString();
        }
        
        Assert.assertEquals(campaignIdESB, campaignIdAPI);
        Assert.assertEquals(campaignStatusESB, campaignStatusAPI);
    }
    
    /**
     * Negative test case for getSearchRecords method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSearchRecordsWithOptionalParameters" }, description = "zohocrm {getSearchRecords} integration negative test case")
    public void testGetSearchRecordsNegativeTestCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getSearchRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchRecords_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/crm/private/json/Campaigns/getSearchRecords?selectColumns="
                        + URLEncoder.encode("Campaigns(Campaign Name,Status)", Charset.defaultCharset().toString())
                        + "&authtoken=" + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&searchCondition=-";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString());
    }
    
    /**
     * Positive test case for updatRelatedRecords method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSearchRecordsNegativeTestCase" }, description = "zohocrm {updatRelatedRecords} integration test with mandatory parameters")
    public void testUpdatRelatedRecordsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateRelatedRecords");
        // Direct call is not used in this test case since the updated info cannot be retrieved via an API
        // call.
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRelatedRecords_mandatory.json");
        
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("status")
                        .get("code").toString(), "200");
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("success")
                        .get("code").toString(), "4800");
        
    }
    
    /**
     * Negative test case for updatRelatedRecords method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdatRelatedRecordsWithMandatoryParameters" }, description = "zohocrm {updatRelatedRecords} integration test negative test case")
    public void testUpdatRelatedRecordsWithNegativecase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateRelatedRecords");
        
        // Direct call is not used in this test case since the updated info cannot be retrieved via an API
        // call.
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRelatedRecords_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/crm/private/json/Campaigns/updateRelatedRecords?relatedModule=-&id="
                        + URLEncoder.encode(connectorProperties.getProperty("parentModuleId"), Charset.defaultCharset()
                                .toString())
                        + "&authtoken="
                        + connectorProperties.getProperty("accessToken")
                        + "&scope="
                        + connectorProperties.getProperty("scope")
                        + "&xmlData="
                        + URLEncoder
                                .encode("<Leads> <row no=\"1\"> <FL val=\"LEADID\">%s(relatedModuleId)</FL> <FL val=\"member_status\">Sent</FL> </row> </Leads>",
                                        Charset.defaultCharset().toString());
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("code")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").get("message")
                .toString());
    }
    
    /**
     * Positive test case for searchRecords method with mandatory parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdatRelatedRecordsWithNegativecase" }, description = "zohocrm {searchRecords} integration test with mandatory parameters")
    public void testSearchRecordsWithMandatoryParameters() throws NumberFormatException, InterruptedException,
            IOException, JSONException {
    
        // Sleeps the test case to avoid request failures.
        Thread.sleep(Integer.parseInt(connectorProperties.getProperty("sleepTime")));
        
        esbRequestHeadersMap.put("Action", "urn:searchRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchRecords_mandatory.json");
        
        String urlEncodedCriteria =
                URLEncoder.encode("(Campaign Name:" + connectorProperties.getProperty("campaignName1") + ")", Charset
                        .defaultCharset().toString());
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/searchRecords?authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&criteria=" + urlEncodedCriteria;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbCampaignsObj =
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns");
        JSONObject apiCampaignsObj =
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns");
        
        JSONArray esbCampaignDetailsArray = null;
        JSONArray apiCampaignDetailsArray = null;
        
        // If there is only one Campaign is adhering to the search criteria, it will return an object,
        // otherwise an array. Following code handles the response accordingly.
        if (esbCampaignsObj.get("row") instanceof JSONObject) {
            Assert.assertTrue(apiCampaignsObj.get("row") instanceof JSONObject);
            esbCampaignDetailsArray = esbCampaignsObj.getJSONObject("row").getJSONArray("FL");
            apiCampaignDetailsArray = apiCampaignsObj.getJSONObject("row").getJSONArray("FL");
        } else {
            Assert.assertTrue(apiCampaignsObj.get("row") instanceof JSONArray);
            esbCampaignDetailsArray = esbCampaignsObj.getJSONArray("row").getJSONObject(0).getJSONArray("FL");
            apiCampaignDetailsArray = apiCampaignsObj.getJSONArray("row").getJSONObject(0).getJSONArray("FL");
        }
        
        Assert.assertEquals(esbCampaignDetailsArray.getJSONObject(0).getString("content"), apiCampaignDetailsArray
                .getJSONObject(0).getString("content"));
        Assert.assertEquals(esbCampaignDetailsArray.getJSONObject(0).getString("val"), apiCampaignDetailsArray
                .getJSONObject(0).getString("val"));
        Assert.assertEquals(esbCampaignDetailsArray.getJSONObject(1).getString("content"), apiCampaignDetailsArray
                .getJSONObject(1).getString("content"));
        Assert.assertEquals(esbCampaignDetailsArray.getJSONObject(1).getString("val"), apiCampaignDetailsArray
                .getJSONObject(1).getString("val"));
    }
    
    /**
     * Positive test case for searchRecords method with optional parameters.
     **/
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchRecordsWithMandatoryParameters" }, description = "zohocrm {searchRecords} integration test with optional parameters")
    public void testSearchRecordsWithOptionalParameters() throws NumberFormatException, InterruptedException,
            IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchRecords_optional.json");
        
        String urlEncodedCriteria =
                URLEncoder.encode("(Campaign Name:" + connectorProperties.getProperty("campaignName2") + ")", Charset
                        .defaultCharset().toString());
        
        String urlEncodedSelectColumns =
                URLEncoder.encode("Campaigns(Campaign Owner)", Charset.defaultCharset().toString());
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/searchRecords?authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&criteria=" + urlEncodedCriteria
                        + "&selectColumns=" + urlEncodedSelectColumns;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbCampaignsObj =
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns");
        JSONObject apiCampaignsObj =
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Campaigns");
        
        JSONArray esbCampaignDetailsArray = null;
        JSONArray apiCampaignDetailsArray = null;
        
        // If there is only one Campaign adhering to the search criteria returns an row object, otherwise
        // an array. Here we retrieve the FL array accordingly.
        if (esbCampaignsObj.get("row") instanceof JSONObject) {
            Assert.assertTrue(apiCampaignsObj.get("row") instanceof JSONObject);
            esbCampaignDetailsArray = esbCampaignsObj.getJSONObject("row").getJSONArray("FL");
            apiCampaignDetailsArray = apiCampaignsObj.getJSONObject("row").getJSONArray("FL");
        } else {
            Assert.assertTrue(apiCampaignsObj.get("row") instanceof JSONArray);
            esbCampaignDetailsArray = esbCampaignsObj.getJSONArray("row").getJSONObject(0).getJSONArray("FL");
            apiCampaignDetailsArray = apiCampaignsObj.getJSONArray("row").getJSONObject(0).getJSONArray("FL");
        }
        
        // Parameter to check the availability of 'Campaign Owner' in the response. Initially sets to false.
        boolean isCampaignOwnerPresent = false;
        for (int i = 0; i < esbCampaignDetailsArray.length(); i++) {
            
            String value = esbCampaignDetailsArray.getJSONObject(i).getString("val");
            // Here, as per the request it should only return 'Campaign Owner' value apart from the ID's
            // returned.
            // 'Campaign Name' should not be present in response.
            if ("Campaign Name".equals(value)) {
                Assert.assertTrue(false);
            }
            if ("Campaign Owner".equals(value)) {
                Assert.assertEquals(esbCampaignDetailsArray.getJSONObject(i).getString("content"),
                        esbCampaignDetailsArray.getJSONObject(i).getString("content"));
                isCampaignOwnerPresent = true;
            }
        }
        
        // Checks weather the Campaign owner was present in response. Only passes if it was found in response.
        Assert.assertTrue(isCampaignOwnerPresent);
        // Checks the equality of the ID's normally returned in default.
        Assert.assertEquals(esbCampaignDetailsArray.getJSONObject(0).getString("content"), apiCampaignDetailsArray
                .getJSONObject(0).getString("content"));
        
    }
    
    /**
     * Negative test case for searchRecords method. Uses an invalid criteria.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchRecordsWithOptionalParameters" }, description = "zohocrm {searchRecords} integration test with negative case")
    public void testSearchRecordsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchRecords_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/searchRecords?authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&criteria=INVALID";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("error")
                .getString("code"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("error")
                .getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getString("uri"), apiRestResponse
                .getBody().getJSONObject("response").getString("uri"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").getString("message"),
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for uploadFile method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchRecordsNegativeCase" }, description = "zohocrm {uploadFile} integration test with mandatory parameters")
    public void testUploadFileWithMandatoryParameters() throws Exception {
    
        String fileUploadProxyUrl = getProxyServiceURL("zohocrm_uploadFile");
        fileUploadProxyUrl +=
                "?accessToken=" + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&moduleType=Campaigns";
        
        String fileName = connectorProperties.getProperty("uploadFileName");
        
        esbRequestHeadersMap.remove("Content-Type");
        esbRequestHeadersMap.remove("Action");
        esbRequestHeadersMap.remove("Accept-Charset");
        
        final MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(fileUploadProxyUrl, esbRequestHeadersMap);
        
        final File file = new File(pathToResourcesDirectory + fileName);
        
        multipartProcessor.addFileToRequest("content", file, "text/plain");
        multipartProcessor.addFormDataToRequest("id", connectorProperties.getProperty("recordId"));
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String attachmentId =
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("recorddetail").getJSONArray("FL").getJSONObject(0).get("content").toString();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/crm/private/json/Campaigns/downloadFile?authtoken="
                        + connectorProperties.getProperty("accessToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&id=" + attachmentId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }
    
}
