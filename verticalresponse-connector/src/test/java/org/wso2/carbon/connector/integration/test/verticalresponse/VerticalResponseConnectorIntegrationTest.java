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

package org.wso2.carbon.connector.integration.test.verticalresponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class VerticalResponseConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    private String listId;
    
    private String listContactId;
    
    private String listIdOptional;
    
    private String contactId;
    
    private String contactIdOptional;
    
    private String emailId;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("verticalresponse");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        
    }
    
    /**
     * Positive test case for createContact method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "VerticalResponse {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws Exception {
    
        String urlRemainder = "/contacts/";
        String apiURL = connectorProperties.getProperty("apiUrl");
        String email = connectorProperties.getProperty("email");
        
        esbRequestHeadersMap.put("Action", "urn:createContact");
        parametersMap.put("email", email);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json",
                        parametersMap);
        String apiEndPoint = esbRestResponse.getBody().get("url").toString();
        contactId = apiEndPoint.split(urlRemainder)[1];
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiURL + urlRemainder + contactId, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("email").toString(), email);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("id").toString(), contactId);
    }
    
    /**
     * Positive test case for createList method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "VerticalResponse {createList} integration test with mandatory parameters.")
    public void testCreateListWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createList");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createList_mandatory.json");
        
        listId = esbRestResponse.getBody().get("url").toString().split("/lists/")[1];
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/lists/" + listId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(listId, apiRestResponse.getBody().getJSONObject("attributes").get("id").toString());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("name").toString(),
                connectorProperties.getProperty("listName"));
    }
    
    /**
     * Positive test case for createList method with optional parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "VerticalResponse {createList} integration test with optional parameters.")
    public void testCreateListWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createList");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createList_optional.json");
        
        listIdOptional = esbRestResponse.getBody().get("url").toString().split("/lists/")[1];
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/lists/" + listIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(listIdOptional, apiRestResponse.getBody().getJSONObject("attributes").get("id").toString());
        Assert.assertEquals(connectorProperties.getProperty("isPublic"),
                apiRestResponse.getBody().getJSONObject("attributes").get("is_public").toString());
    }
    
    /**
     * Negative test case for createList method.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "VerticalResponse {createList} integration test with negative case.")
    public void testCreateListWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createList");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createList_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/lists/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for addContactToList method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
            "testCreateListWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "VerticalResponse {addContactToList} integration test with mandatory parameters.")
    public void testAddContactToListWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:addContactToList");
        parametersMap.put("listId", listId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addContactToList_mandatory.json",
                        parametersMap);
        
        listContactId = esbRestResponse.getBody().get("url").toString().split("/contacts/")[1];
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/lists/" + listId + "/contacts/" + listContactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("email").toString(),
                connectorProperties.getProperty("email"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("id").toString(), listContactId);
    }
    
    /**
     * Negative test case for addContactToList method.
     */
    @Test(priority = 2, dependsOnMethods = { "testAddContactToListWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "VerticalResponse {addContactToList} integration test with negative case.")
    public void testAddContactToListWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:addContactToList");
        parametersMap.put("listId", listId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addContactToList_negative.json",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/lists/" + listId + "/contacts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for updateList method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testAddContactToListWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "VerticalResponse {updateList} integration test with mandatory parameters.")
    public void testUpdateListWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateList");
        parametersMap.put("listId", listId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateList_mandatory.json",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/lists/" + listId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("name").toString(),
                connectorProperties.getProperty("listName"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("id").toString(), listId);
    }
    
    /**
     * Positive test case for updateList method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateListWithOptionalParameters" }, groups = { "wso2.esb" }, description = "VerticalResponse {updateList} integration test with optional parameters.")
    public void testUpdateListWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateList");
        parametersMap.put("listId", listIdOptional);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateList_optional.json",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/lists/" + listIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("listNameOptional"), apiRestResponse.getBody()
                .getJSONObject("attributes").get("name").toString());
        Assert.assertEquals(connectorProperties.getProperty("isPublic"),
                apiRestResponse.getBody().getJSONObject("attributes").get("is_public").toString());
    }
    
    /**
     * Negative test case for updateList method.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "VerticalResponse {updateList} integration test with negative case.")
    public void testUpdateListWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateList");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateList_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/lists/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for getContactMemberList method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "VerticalResponse {getContactMemberList} integration test with mandatory parameters.")
    public void testGetContactMemberListWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getContactMemberList");
        parametersMap.put("contactId", contactId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactMemberList_mandatory.json",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/contacts/" + contactId + "/lists";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0)
                .getJSONObject("attributes").get("id"), apiRestResponse.getBody().getJSONArray("items")
                .getJSONObject(0).getJSONObject("attributes").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0)
                .getJSONObject("attributes").get("name"), apiRestResponse.getBody().getJSONArray("items")
                .getJSONObject(0).getJSONObject("attributes").get("name"));
    }
    
    /**
     * Positive test case for getContactMemberList method with optional parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "VerticalResponse {getContactMemberList} integration test with optional parameters.")
    public void testGetContactMemberListWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getContactMemberList");
        parametersMap.put("contactId", contactIdOptional);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactMemberList_optional.json",
                        parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/contacts/" + contactIdOptional + "/lists/?type=all";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0)
                .getJSONObject("attributes").get("id"), apiRestResponse.getBody().getJSONArray("items")
                .getJSONObject(0).getJSONObject("attributes").get("id"));
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0)
                .getJSONObject("attributes").get("reach"), apiRestResponse.getBody().getJSONArray("items")
                .getJSONObject(0).getJSONObject("attributes").get("reach"));
    }
    
    /**
     * Negative test case for getContactMemberList method.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "VerticalResponse {getContactMemberList} integration test with negative case.")
    public void testGetContactMemberListWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getContactMemberList");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactMemberList_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/contacts/" + "INVALID" + "/lists";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for removeContactFromList method with mandatory parameters.
     */
    @Test(priority = 3, dependsOnMethods = { "testUpdateListWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "VerticalResponse {removeContactFromList} integration test with mandatory parameters.")
    public void testRemoveContactFromListWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:removeContactFromList");
        parametersMap.put("listId", listId);
        parametersMap.put("contactId", listContactId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeContactFromList_mandatory.json",
                        parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/lists/" + listId + "/contacts/" + listContactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Negative test case for removeContactFromList method.
     */
    @Test(priority = 2, dependsOnMethods = { "testRemoveContactFromListWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "VerticalResponse {removeContactFromList} integration test with negative case.")
    public void testRemoveContactFromListWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:removeContactFromList");
        parametersMap.put("listId", listId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeContactFromList_negative.json",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/lists/" + listId + "/contacts/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for createContact method with optional parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateListWithMandatoryParameters" }, description = "VerticalResponse {createContact} integration test with optional parameters.")
    public void testCreateContactWithOptionalParameters() throws Exception {
    
        String urlRemainder = "/contacts/";
        String apiURL = connectorProperties.getProperty("apiUrl");
        String emailOptional = connectorProperties.getProperty("emailOptional");
        String firstName = connectorProperties.getProperty("firstName");
        
        esbRequestHeadersMap.put("Action", "urn:createContact");
        parametersMap.put("email", emailOptional);
        parametersMap.put("listId", listId);
        parametersMap.put("firstName", firstName);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json",
                        parametersMap);
        
        String apiEndPoint = esbRestResponse.getBody().get("url").toString();
        contactIdOptional = apiEndPoint.split(urlRemainder)[1];
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiURL + urlRemainder + contactIdOptional, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("first_name").toString(),
                firstName);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("id").toString(),
                contactIdOptional);
        
    }
    
    /**
     * Negative test case for createContact method.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "VerticalResponse {createContact} integration test for negative case")
    public void testCreateContactWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/contacts", "POST",
                        apiRequestHeadersMap, "api_createContact_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").get("message").toString(), esbRestResponse
                .getBody().getJSONObject("error").get("message").toString());
        
    }
    
    /**
     * Positive test case for updateContact method with optional parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "VerticalResponse {updateContact} integration test with optional parameters.")
    public void testUpdateContactWithOptionalParameters() throws Exception {
    
        String urlRemainder = "/contacts/";
        String apiURL = connectorProperties.getProperty("apiUrl");
        String company = connectorProperties.getProperty("company");
        
        esbRequestHeadersMap.put("Action", "urn:updateContact");
        parametersMap.put("contactId", contactIdOptional);
        parametersMap.put("company", company);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_optional.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiURL + urlRemainder + contactIdOptional + "?type=standard", "GET",
                        apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("company").toString(), company);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("id").toString(),
                contactIdOptional);
        
    }
    
    /**
     * Negative test case for updateContact method.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "VerticalResponse {updateContact} integration test with negative case.")
    public void testUpdateContactWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateContact");
        parametersMap.put("contactId", contactIdOptional);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_negative.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/contacts/" + contactIdOptional,
                        "PUT", apiRequestHeadersMap, "api_updateContact_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").get("message").toString(), esbRestResponse
                .getBody().getJSONObject("error").get("message").toString());
        
    }
    
    /**
     * Positive test case for getContact method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, description = "VerticalResponse {getContact} integration test with mandatory parameters.")
    public void testGetContactWithMandatoryParameters() throws Exception {
    
        String urlRemainder = "/contacts/";
        
        esbRequestHeadersMap.put("Action", "urn:getContact");
        parametersMap.put("contactId", contactId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_mandatory.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + urlRemainder + contactId, "GET",
                        apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attributes").get("email").toString(),
                apiRestResponse.getBody().getJSONObject("attributes").get("email").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attributes").get("id").toString(), apiRestResponse
                .getBody().getJSONObject("attributes").get("id").toString());
        
    }
    
    /**
     * Positive test case for getContact method with optional parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, description = "VerticalResponse {getContact} integration test with optional parameters.")
    public void testGetContactWithOptionalParameters() throws Exception {
    
        String urlRemainder = "/contacts/";
        
        esbRequestHeadersMap.put("Action", "urn:getContact");
        parametersMap.put("contactId", contactId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_optional.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + urlRemainder + contactId
                        + "?type=standard", "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attributes").get("marital_status").toString(),
                apiRestResponse.getBody().getJSONObject("attributes").get("marital_status").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attributes").get("home_phone").toString(),
                apiRestResponse.getBody().getJSONObject("attributes").get("home_phone").toString());
        
    }
    
    /**
     * Negative test case for getContact method with optional parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, description = "VerticalResponse {getContact} integration test with optional parameters.")
    public void testGetContactWithNegativeCase() throws Exception {
    
        String urlRemainder = "/contacts/";
        
        esbRequestHeadersMap.put("Action", "urn:getContact");
        parametersMap.put("contactId", contactId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_negative.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + urlRemainder + contactId
                        + "?type=INVALID", "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").get("message").toString(), esbRestResponse
                .getBody().getJSONObject("error").get("message").toString());
    }
    
    /**
     * Positive test case for sendEmail method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
            "testCreateContactWithOptionalParameters" }, description = "VerticalResponse {sendEmail} integration test with mandatory parameters.")
    public void testSendEmailWithMandatoryParameters() throws Exception {
    
        String urlRemainder = "/messages/emails/";
        String apiURL = connectorProperties.getProperty("apiUrl");
        String email = connectorProperties.getProperty("email");
        String emailOptional = connectorProperties.getProperty("emailOptional");
        String subject = connectorProperties.getProperty("subject");
        
        esbRequestHeadersMap.put("Action", "urn:sendEmail");
        parametersMap.put("email", email);
        parametersMap.put("subject", subject);
        parametersMap.put("emailOptional", emailOptional);
        parametersMap.put("listId", listId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendEmail_mandatory.json",
                        parametersMap);
        String apiEndPoint = esbRestResponse.getBody().get("url").toString();
        emailId = apiEndPoint.split(urlRemainder)[1];
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiURL + urlRemainder + emailId + "?type=basic", "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("subject").toString(), subject);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("id").toString(), emailId);
        
    }
    
    /**
     * Positive test case for sendEmail method with optional parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
            "testCreateContactWithOptionalParameters" }, description = "VerticalResponse {sendEmail} integration test with optional parameters.")
    public void testSendEmailWithOptionalParameters() throws Exception {
    
        String urlRemainder = "/messages/emails/";
        String apiURL = connectorProperties.getProperty("apiUrl");
        String email = connectorProperties.getProperty("email");
        String emailOptional = connectorProperties.getProperty("emailOptional");
        String subject = connectorProperties.getProperty("subject");
        
        // Get next day from calendar:
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(calendar.DAY_OF_MONTH, 1);
        Date tomorrow = calendar.getTime();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String nextDate = fmt.format(tomorrow);
        
        esbRequestHeadersMap.put("Action", "urn:sendEmail");
        parametersMap.put("email", email);
        parametersMap.put("subject", subject);
        parametersMap.put("emailOptional", emailOptional);
        parametersMap.put("listId", listId);
        parametersMap.put("scheduledAt", nextDate);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendEmail_optional.json",
                        parametersMap);
        String apiEndPoint = esbRestResponse.getBody().get("url").toString();
        emailId = apiEndPoint.split(urlRemainder)[1];
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiURL + urlRemainder + emailId + "?type=basic", "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("subject").toString(), subject);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attributes").get("id").toString(), emailId);
    }
    
    /**
     * Negative test case for sendEmail method.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
            "testCreateContactWithOptionalParameters" }, description = "VerticalResponse {sendEmail} integration test with negative test case.")
    public void testSendEmailWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:sendEmail");
        parametersMap.put("email", connectorProperties.getProperty("email"));
        parametersMap.put("emailOptional", connectorProperties.getProperty("emailOptional"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendEmail_negative.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/messages/emails", "POST",
                        apiRequestHeadersMap, "api_sendEmail_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").get("message").toString(), esbRestResponse
                .getBody().getJSONObject("error").get("message").toString());
    }
    
    /**
     * Positive test case for getList method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateListWithMandatoryParameters" }, description = "VerticalResponse {getList} integration test with mandatory parameters.")
    public void testGetListWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getList");
        parametersMap.put("listId", listId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getList_mandatory.json", parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/lists/" + listId, "GET",
                        apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attributes").get("name").toString(),
                apiRestResponse.getBody().getJSONObject("attributes").get("name").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attributes").get("id").toString(), apiRestResponse
                .getBody().getJSONObject("attributes").get("id").toString());
        
    }
    
    /**
     * Positive test case for getList method with optional parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateListWithMandatoryParameters" }, description = "VerticalResponse {getList} integration test with optional parameters.")
    public void testGetListWithOptionalParameters() throws Exception {
    
        String type = connectorProperties.getProperty("type");
        esbRequestHeadersMap.put("Action", "urn:getList");
        parametersMap.put("listId", listId);
        parametersMap.put("type", type);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getList_optional.json", parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/lists/" + listId + "?type=" + type,
                        "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attributes").get("created_at").toString(),
                apiRestResponse.getBody().getJSONObject("attributes").get("created_at").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attributes").get("id").toString(), apiRestResponse
                .getBody().getJSONObject("attributes").get("id").toString());
        
    }
    
    /**
     * Negative test case for getList method.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
            "testCreateContactWithOptionalParameters" }, description = "VerticalResponse {getList} integration test with negative case.")
    public void testGetListWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getList");
        parametersMap.put("listId", listId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getList_negative.json", parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/lists/" + listId + "?type=INVALID",
                        "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").get("message").toString(), esbRestResponse
                .getBody().getJSONObject("error").get("message").toString());
    }
    
    /**
     * Positive test case for listContacts method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateListWithMandatoryParameters" }, description = "VerticalResponse {listContacts} integration test with mandatory parameters.")
    public void testListContactsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        parametersMap.put("listId", listId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/lists/" + listId + "/contacts",
                        "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0)
                .getJSONObject("attributes").get("id"), apiRestResponse.getBody().getJSONArray("items")
                .getJSONObject(0).getJSONObject("attributes").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0)
                .getJSONObject("attributes").get("email"), apiRestResponse.getBody().getJSONArray("items")
                .getJSONObject(0).getJSONObject("attributes").get("email"));
        
    }
    
    /**
     * Positive test case for listContacts method with optional parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateListWithMandatoryParameters" }, description = "VerticalResponse {listContacts} integration test with optional parameters.")
    public void testListContactsWithOptionalParameters() throws Exception {
    
        String type = connectorProperties.getProperty("type");
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        parametersMap.put("listId", listId);
        parametersMap.put("type", type);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/lists/" + listId + "/contacts?type="
                        + type, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0)
                .getJSONObject("attributes").get("id"), apiRestResponse.getBody().getJSONArray("items")
                .getJSONObject(0).getJSONObject("attributes").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0)
                .getJSONObject("attributes").get("email"), apiRestResponse.getBody().getJSONArray("items")
                .getJSONObject(0).getJSONObject("attributes").get("email"));
        
    }
    
    /**
     * Negative test case for listContacts method.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
            "testListContactsWithOptionalParameters" }, description = "VerticalResponse {listContacts} integration test with negative case.")
    public void testListContactsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        parametersMap.put("listId", listId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_negative.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/lists/" + listId
                        + "/contacts?type=INVALID", "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").get("message").toString(), esbRestResponse
                .getBody().getJSONObject("error").get("message").toString());
    }
    
}
