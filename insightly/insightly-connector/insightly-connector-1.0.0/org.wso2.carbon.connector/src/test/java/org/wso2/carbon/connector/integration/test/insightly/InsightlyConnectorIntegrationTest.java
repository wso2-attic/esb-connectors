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
package org.wso2.carbon.connector.integration.test.insightly;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class InsightlyConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiRequestUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("insightly-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        final String authorizationString = connectorProperties.getProperty("apiKey") + ":";
        apiRequestHeadersMap.put("Authorization", "Basic "
                        + new String(Base64.encodeBase64(authorizationString.getBytes())));
        
        apiRequestUrl = connectorProperties.getProperty("apiUrl") + "/v2.1";
        
        // Retrieve the ID of the user to be used in test cases.
        final String apiEndPoint = apiRequestUrl + "/Users";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray userArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        connectorProperties.setProperty("ownerUserId", userArray.getJSONObject(0).getString("USER_ID"));
        
    }
    
    /**
     * Positive test case for createContact method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createContact");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/Contacts/" + esbRestResponse.getBody().getString("CONTACT_ID");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("IMAGE_URL"), apiRestResponse.getBody().getString(
                        "IMAGE_URL"));
        Assert.assertEquals(esbRestResponse.getBody().getString("OWNER_USER_ID"), apiRestResponse.getBody().getString(
                        "OWNER_USER_ID"));
        Assert.assertEquals(esbRestResponse.getBody().getString("DATE_CREATED_UTC"), apiRestResponse.getBody()
                        .getString("DATE_CREATED_UTC"));
        Assert.assertEquals(esbRestResponse.getBody().getString("DATE_UPDATED_UTC"), apiRestResponse.getBody()
                        .getString("DATE_UPDATED_UTC"));
    }
    
    /**
     * Positive test case for createContact method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createContact} integration test with optional parameters.")
    public void testCreateContactWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createContact");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
        
        final String contactIdOptional = esbRestResponse.getBody().getString("CONTACT_ID");
        connectorProperties.setProperty("contactIdOptional", contactIdOptional);
        
        final String apiEndPoint = apiRequestUrl + "/Contacts/" + contactIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("firstName"), apiRestResponse.getBody().getString(
                        "FIRST_NAME"));
        Assert.assertEquals(connectorProperties.getProperty("lastName"), apiRestResponse.getBody().getString(
                        "LAST_NAME"));
        Assert.assertEquals(connectorProperties.getProperty("salutation"), apiRestResponse.getBody().getString(
                        "SALUTATION"));
        Assert.assertEquals(connectorProperties.getProperty("visibleTo").toLowerCase(), apiRestResponse.getBody()
                        .getString("VISIBLE_TO").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("background"), apiRestResponse.getBody().getString(
                        "BACKGROUND"));
        Assert.assertEquals(connectorProperties.getProperty("tagName"), apiRestResponse.getBody().getJSONArray("TAGS")
                        .getJSONObject(0).getString("TAG_NAME"));
    }
    
    /**
     * Negative test case for createContact method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createContact} integration test with negative case.")
    public void testCreateContactWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createContact");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Contacts";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                                        "api_createContact_negative.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Message"), apiResponseArray.getJSONObject(0).get(
                        "Message"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Name"), apiResponseArray.getJSONObject(0)
                        .get("Name"));
    }
    
    /**
     * Positive test case for updateContact method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {updateContact} integration test with optional parameters.", dependsOnMethods = { "testCreateContactWithOptionalParameters" })
    public void testUpdateContactWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateContact");
        
        final String apiEndPoint = apiRequestUrl + "/Contacts/" + connectorProperties.getProperty("contactIdOptional");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("FIRST_NAME"), apiRestResponseAfter.getBody()
                        .getString("FIRST_NAME"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("LAST_NAME"), apiRestResponseAfter.getBody()
                        .getString("LAST_NAME"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("SALUTATION"), apiRestResponseAfter.getBody()
                        .getString("SALUTATION"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("BACKGROUND"), apiRestResponseAfter.getBody()
                        .getString("BACKGROUND"));
        
        Assert.assertEquals(connectorProperties.getProperty("firstNameUpdated"), apiRestResponseAfter.getBody()
                        .getString("FIRST_NAME"));
        Assert.assertEquals(connectorProperties.getProperty("lastNameUpdated"), apiRestResponseAfter.getBody()
                        .getString("LAST_NAME"));
        Assert.assertEquals(connectorProperties.getProperty("salutationUpdated"), apiRestResponseAfter.getBody()
                        .getString("SALUTATION"));
        Assert.assertEquals(connectorProperties.getProperty("backgroundUpdated"), apiRestResponseAfter.getBody()
                        .getString("BACKGROUND"));
        Assert.assertEquals(connectorProperties.getProperty("tagNameUpdated"), apiRestResponseAfter.getBody()
                        .getJSONArray("TAGS").getJSONObject(0).getString("TAG_NAME"));
    }
    
    /**
     * Negative test case for updateContact method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {updateContact} integration test with negative case.")
    public void testUpdateContactWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateContact");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_negative.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Contacts";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateContact_negative.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Message"), apiResponseArray.getJSONObject(0).get(
                        "Message"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Name"), apiResponseArray.getJSONObject(0)
                        .get("Name"));
    }
    
    /**
     * Positive test case for getContact method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {getContact} integration test with mandatory parameters.", dependsOnMethods = { "testCreateContactWithOptionalParameters" })
    public void testGetContactWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getContact");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/Contacts/" + connectorProperties.getProperty("contactIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("FIRST_NAME"), apiRestResponse.getBody().getString(
                        "FIRST_NAME"));
        Assert.assertEquals(esbRestResponse.getBody().getString("LAST_NAME"), apiRestResponse.getBody().getString(
                        "LAST_NAME"));
        Assert.assertEquals(esbRestResponse.getBody().getString("SALUTATION"), apiRestResponse.getBody().getString(
                        "SALUTATION"));
        Assert.assertEquals(esbRestResponse.getBody().getString("BACKGROUND"), apiRestResponse.getBody().getString(
                        "BACKGROUND"));
    }
    
    /**
     * Positive test case for listContacts method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listContacts} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateContactWithOptionalParameters", "testCreateContactWithMandatoryParameters" })
    public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Contacts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("FIRST_NAME"), apiResponseArray
                        .getJSONObject(0).getString("FIRST_NAME"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("LAST_NAME"), apiResponseArray.getJSONObject(0)
                        .getString("LAST_NAME"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("SALUTATION"), apiResponseArray
                        .getJSONObject(0).getString("SALUTATION"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("BACKGROUND"), apiResponseArray
                        .getJSONObject(0).getString("BACKGROUND"));
    }
    
    /**
     * Positive test case for listContacts method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listContacts} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateContactWithOptionalParameters", "testCreateContactWithMandatoryParameters" })
    public void testListContactsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint =
                        apiRequestUrl + "/Contacts?ids=" + connectorProperties.getProperty("contactIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), 1);
        Assert.assertEquals(apiResponseArray.length(), 1);
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("FIRST_NAME"), apiResponseArray
                        .getJSONObject(0).getString("FIRST_NAME"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("LAST_NAME"), apiResponseArray.getJSONObject(0)
                        .getString("LAST_NAME"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("SALUTATION"), apiResponseArray
                        .getJSONObject(0).getString("SALUTATION"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("BACKGROUND"), apiResponseArray
                        .getJSONObject(0).getString("BACKGROUND"));
    }
    
    /**
     * Negative test case for listContacts method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listContacts} integration test with negative case.")
    public void testListContactsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/Contacts?ids=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for createOpportunity method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createOpportunity} integration test with mandatory parameters.")
    public void testCreateOpportunityWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createOpportunity");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createOpportunity_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/Opportunities/" + esbRestResponse.getBody().getString("OPPORTUNITY_ID");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("IMAGE_URL"), apiRestResponse.getBody().getString(
                        "IMAGE_URL"));
        Assert.assertEquals(esbRestResponse.getBody().getString("OWNER_USER_ID"), apiRestResponse.getBody().getString(
                        "OWNER_USER_ID"));
        Assert.assertEquals(esbRestResponse.getBody().getString("DATE_CREATED_UTC"), apiRestResponse.getBody()
                        .getString("DATE_CREATED_UTC"));
        Assert.assertEquals(esbRestResponse.getBody().getString("OPPORTUNITY_STATE"), apiRestResponse.getBody()
                        .getString("OPPORTUNITY_STATE"));
    }
    
    /**
     * Positive test case for createOpportunity method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createOpportunity} integration test with optional parameters.")
    public void testCreateOpportunityWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createOpportunity");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createOpportunity_optional.json");
        
        final String opportunityIdOptional = esbRestResponse.getBody().getString("OPPORTUNITY_ID");
        connectorProperties.setProperty("opportunityIdOptional", opportunityIdOptional);
        
        final String apiEndPoint = apiRequestUrl + "/Opportunities/" + opportunityIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("opportunityName"), apiRestResponse.getBody().getString(
                        "OPPORTUNITY_NAME"));
        Assert.assertEquals(connectorProperties.getProperty("opportunityDetails"), apiRestResponse.getBody().getString(
                        "OPPORTUNITY_DETAILS"));
        Assert.assertEquals(Integer.parseInt(connectorProperties.getProperty("probability")), apiRestResponse.getBody()
                        .getInt("PROBABILITY"));
        Assert.assertEquals(connectorProperties.getProperty("bidCurrency"), apiRestResponse.getBody().getString(
                        "BID_CURRENCY"));
        Assert.assertEquals(Integer.parseInt(connectorProperties.getProperty("bidAmount")), apiRestResponse.getBody()
                        .getInt("BID_AMOUNT"));
        Assert.assertEquals(connectorProperties.getProperty("bidType").toLowerCase(), apiRestResponse.getBody()
                        .getString("BID_TYPE").toLowerCase());
        Assert.assertEquals(Integer.parseInt(connectorProperties.getProperty("bidDuration")), apiRestResponse.getBody()
                        .getInt("BID_DURATION"));
        Assert.assertEquals(connectorProperties.getProperty("opportunityState").toLowerCase(), apiRestResponse
                        .getBody().getString("OPPORTUNITY_STATE").toLowerCase());
    }
    
    /**
     * Negative test case for createOpportunity method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createOpportunity} integration test with negative case.")
    public void testCreateOpportunityWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createOpportunity");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createOpportunity_negative.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Opportunities";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                                        "api_createOpportunity_negative.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Message"), apiResponseArray.getJSONObject(0).get(
                        "Message"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Name"), apiResponseArray.getJSONObject(0)
                        .get("Name"));
    }
    
    /**
     * Positive test case for updateOpportunity method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {updateOpportunity} integration test with optional parameters.", dependsOnMethods = { "testCreateOpportunityWithOptionalParameters" })
    public void testUpdateOpportunityWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateOpportunity");
        
        final String apiEndPoint =
                        apiRequestUrl + "/Opportunities/" + connectorProperties.getProperty("opportunityIdOptional");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateOpportunity_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("OPPORTUNITY_NAME"), apiRestResponseAfter
                        .getBody().getString("OPPORTUNITY_NAME"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("OPPORTUNITY_DETAILS"), apiRestResponseAfter
                        .getBody().getString("OPPORTUNITY_DETAILS"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getInt("PROBABILITY"), apiRestResponseAfter.getBody()
                        .getInt("PROBABILITY"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("BID_CURRENCY"), apiRestResponseAfter
                        .getBody().getString("BID_CURRENCY"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getInt("BID_AMOUNT"), apiRestResponseAfter.getBody()
                        .getInt("BID_AMOUNT"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("BID_TYPE").toLowerCase(),
                        apiRestResponseAfter.getBody().getString("BID_TYPE").toLowerCase());
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getInt("BID_DURATION"), apiRestResponseAfter.getBody()
                        .getInt("BID_DURATION"));
        
        Assert.assertEquals(connectorProperties.getProperty("opportunityNameUpdated"), apiRestResponseAfter.getBody()
                        .getString("OPPORTUNITY_NAME"));
        Assert.assertEquals(connectorProperties.getProperty("opportunityDetailsUpdated"), apiRestResponseAfter
                        .getBody().getString("OPPORTUNITY_DETAILS"));
        Assert.assertEquals(Integer.parseInt(connectorProperties.getProperty("probabilityUpdated")),
                        apiRestResponseAfter.getBody().getInt("PROBABILITY"));
        Assert.assertEquals(connectorProperties.getProperty("bidCurrencyUpdated"), apiRestResponseAfter.getBody()
                        .getString("BID_CURRENCY"));
        Assert.assertEquals(Integer.parseInt(connectorProperties.getProperty("bidAmountUpdated")), apiRestResponseAfter
                        .getBody().getInt("BID_AMOUNT"));
        Assert.assertEquals(connectorProperties.getProperty("bidTypeUpdated").toLowerCase(), apiRestResponseAfter
                        .getBody().getString("BID_TYPE").toLowerCase());
        Assert.assertEquals(Integer.parseInt(connectorProperties.getProperty("bidDurationUpdated")),
                        apiRestResponseAfter.getBody().getInt("BID_DURATION"));
    }
    
    /**
     * Negative test case for updateOpportunity method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {updateOpportunity} integration test with negative case.")
    public void testUpdateOpportunityWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateOpportunity");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_updateOpportunity_negative.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Opportunities";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap,
                                        "api_updateOpportunity_negative.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Message"), apiResponseArray.getJSONObject(0).get(
                        "Message"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Name"), apiResponseArray.getJSONObject(0)
                        .get("Name"));
    }
    
    /**
     * Positive test case for getOpportunity method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {getOpportunity} integration test with mandatory parameters.", dependsOnMethods = { "testCreateOpportunityWithOptionalParameters" })
    public void testGetOpportunityWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getOpportunity");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getOpportunity_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/Opportunities/" + connectorProperties.getProperty("opportunityIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("OPPORTUNITY_NAME"), apiRestResponse.getBody()
                        .getString("OPPORTUNITY_NAME"));
        Assert.assertEquals(esbRestResponse.getBody().getString("OPPORTUNITY_DETAILS"), apiRestResponse.getBody()
                        .getString("OPPORTUNITY_DETAILS"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("PROBABILITY"), apiRestResponse.getBody().getInt(
                        "PROBABILITY"));
        Assert.assertEquals(esbRestResponse.getBody().getString("BID_CURRENCY"), apiRestResponse.getBody().getString(
                        "BID_CURRENCY"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("BID_AMOUNT"), apiRestResponse.getBody().getInt(
                        "BID_AMOUNT"));
        Assert.assertEquals(esbRestResponse.getBody().getString("BID_TYPE").toLowerCase(), apiRestResponse.getBody()
                        .getString("BID_TYPE").toLowerCase());
        Assert.assertEquals(esbRestResponse.getBody().getInt("BID_DURATION"), apiRestResponse.getBody().getInt(
                        "BID_DURATION"));
        Assert.assertEquals(esbRestResponse.getBody().getString("OPPORTUNITY_STATE").toLowerCase(), apiRestResponse
                        .getBody().getString("OPPORTUNITY_STATE").toLowerCase());
    }
    
    /**
     * Positive test case for listOpportunities method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listOpportunities} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateOpportunityWithMandatoryParameters", "testCreateOpportunityWithOptionalParameters" })
    public void testListOpportunitiesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listOpportunities");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listOpportunities_mandatory.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Opportunities";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("OPPORTUNITY_DETAILS"), apiResponseArray
                        .getJSONObject(0).getString("OPPORTUNITY_DETAILS"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("IMAGE_URL"), apiResponseArray.getJSONObject(0)
                        .getString("IMAGE_URL"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("DATE_CREATED_UTC"), apiResponseArray
                        .getJSONObject(0).getString("DATE_CREATED_UTC"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("OPPORTUNITY_ID"), apiResponseArray
                        .getJSONObject(0).getString("OPPORTUNITY_ID"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("OPPORTUNITY_STATE"), apiResponseArray
                        .getJSONObject(0).getString("OPPORTUNITY_STATE"));
    }
    
    /**
     * Positive test case for listOpportunities method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listOpportunities} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateOpportunityWithMandatoryParameters", "testCreateOpportunityWithOptionalParameters" })
    public void testListOpportunitiesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listOpportunities");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listOpportunities_optional.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint =
                        apiRequestUrl + "/Opportunities?ids="
                                        + connectorProperties.getProperty("opportunityIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), 1);
        Assert.assertEquals(apiResponseArray.length(), 1);
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("OPPORTUNITY_DETAILS"), apiResponseArray
                        .getJSONObject(0).getString("OPPORTUNITY_DETAILS"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("IMAGE_URL"), apiResponseArray.getJSONObject(0)
                        .getString("IMAGE_URL"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("DATE_CREATED_UTC"), apiResponseArray
                        .getJSONObject(0).getString("DATE_CREATED_UTC"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("OPPORTUNITY_ID"), apiResponseArray
                        .getJSONObject(0).getString("OPPORTUNITY_ID"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("OPPORTUNITY_STATE"), apiResponseArray
                        .getJSONObject(0).getString("OPPORTUNITY_STATE"));
    }
    
    /**
     * Negative test case for listOpportunities method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listOpportunities} integration test with negative case.")
    public void testListOpportunitiesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listOpportunities");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listOpportunities_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/Opportunities?ids=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for createProject method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createProject} integration test with mandatory parameters.")
    public void testCreateProjectWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createProject");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/Projects/" + esbRestResponse.getBody().getString("PROJECT_ID");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("projectNameMandatory"), apiRestResponse.getBody()
                        .getString("PROJECT_NAME"));
        Assert.assertEquals(connectorProperties.getProperty("status"), apiRestResponse.getBody().getString("STATUS"));
        Assert.assertEquals(esbRestResponse.getBody().getString("DATE_UPDATED_UTC"), apiRestResponse.getBody()
                        .getString("DATE_UPDATED_UTC"));
        Assert.assertEquals(esbRestResponse.getBody().getString("VISIBLE_TO"), apiRestResponse.getBody().getString(
                        "VISIBLE_TO"));
    }
    
    /**
     * Positive test case for createProject method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createProject} integration test with optional parameters.", dependsOnMethods = { "testCreateOpportunityWithOptionalParameters" })
    public void testCreateProjectWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createProject");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_optional.json");
        
        final String projectIdOptional = esbRestResponse.getBody().getString("PROJECT_ID");
        connectorProperties.setProperty("projectIdOptional", projectIdOptional);
        
        final String apiEndPoint = apiRequestUrl + "/Projects/" + projectIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("projectDetails"), apiRestResponse.getBody().getString(
                        "PROJECT_DETAILS"));
        
        Assert.assertEquals(connectorProperties.getProperty("completedDate"), apiRestResponse.getBody().getString(
                        "COMPLETED_DATE"));
        Assert.assertEquals(connectorProperties.getProperty("visibleTo").toLowerCase(), apiRestResponse.getBody()
                        .getString("VISIBLE_TO").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("opportunityIdOptional"), apiRestResponse.getBody()
                        .getString("OPPORTUNITY_ID"));
    }
    
    /**
     * Negative test case for createProject method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createProject} integration test with negative case.")
    public void testCreateProjectWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createProject");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/Projects";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                                        "api_createProject_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for updateProject method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {updateProject} integration test with optional parameters.", dependsOnMethods = { "testCreateProjectWithOptionalParameters" })
    public void testUpdateProjectWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateProject");
        
        final String apiEndPoint = apiRequestUrl + "/Projects/" + connectorProperties.getProperty("projectIdOptional");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProject_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("PROJECT_NAME"), apiRestResponseAfter
                        .getBody().getString("PROJECT_NAME"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("PROJECT_DETAILS"), apiRestResponseAfter
                        .getBody().getString("PROJECT_DETAILS"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("STATUS"), apiRestResponseAfter.getBody()
                        .getString("STATUS"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("COMPLETED_DATE"), apiRestResponseAfter
                        .getBody().getString("COMPLETED_DATE"));
        
        Assert.assertEquals(connectorProperties.getProperty("projectNameUpdated"), apiRestResponseAfter.getBody()
                        .getString("PROJECT_NAME"));
        Assert.assertEquals(connectorProperties.getProperty("projectDetailsUpdated"), apiRestResponseAfter.getBody()
                        .getString("PROJECT_DETAILS"));
        Assert.assertEquals(connectorProperties.getProperty("statusUpdated"), apiRestResponseAfter.getBody().getString(
                        "STATUS"));
        Assert.assertEquals(connectorProperties.getProperty("completedDateUpdated"), apiRestResponseAfter.getBody()
                        .getString("COMPLETED_DATE"));
    }
    
    /**
     * Negative test case for updateProject method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {updateProject} integration test with negative case.")
    public void testUpdateProjectWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateProject");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProject_negative.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Projects";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateProject_negative.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Message"), apiResponseArray.getJSONObject(0).get(
                        "Message"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Name"), apiResponseArray.getJSONObject(0)
                        .get("Name"));
    }
    
    /**
     * Positive test case for getProject method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {getProject} integration test with mandatory parameters.", dependsOnMethods = { "testCreateProjectWithOptionalParameters" })
    public void testGetProjectWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getProject");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/Projects/" + connectorProperties.getProperty("projectIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("PROJECT_NAME"), apiRestResponse.getBody().getString(
                        "PROJECT_NAME"));
        Assert.assertEquals(esbRestResponse.getBody().getString("PROJECT_DETAILS"), apiRestResponse.getBody()
                        .getString("PROJECT_DETAILS"));
        Assert.assertEquals(esbRestResponse.getBody().getString("STATUS"), apiRestResponse.getBody()
                        .getString("STATUS"));
        Assert.assertEquals(esbRestResponse.getBody().getString("COMPLETED_DATE"), apiRestResponse.getBody().getString(
                        "COMPLETED_DATE"));
        Assert.assertEquals(esbRestResponse.getBody().getString("VISIBLE_TO"), apiRestResponse.getBody().getString(
                        "VISIBLE_TO"));
        Assert.assertEquals(esbRestResponse.getBody().getString("DATE_CREATED_UTC").toLowerCase(), apiRestResponse
                        .getBody().getString("DATE_CREATED_UTC").toLowerCase());
        Assert.assertEquals(esbRestResponse.getBody().getString("DATE_UPDATED_UTC"), apiRestResponse.getBody()
                        .getString("DATE_UPDATED_UTC"));
    }
    
    /**
     * Positive test case for listProjects method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listProjects} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateProjectWithMandatoryParameters", "testCreateProjectWithOptionalParameters" })
    public void testListProjectsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_mandatory.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Projects";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("PROJECT_ID"), apiResponseArray
                        .getJSONObject(0).getString("PROJECT_ID"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("PROJECT_NAME"), apiResponseArray
                        .getJSONObject(0).getString("PROJECT_NAME"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("VISIBLE_TO"), apiResponseArray
                        .getJSONObject(0).getString("VISIBLE_TO"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("COMPLETED_DATE"), apiResponseArray
                        .getJSONObject(0).getString("COMPLETED_DATE"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("DATE_CREATED_UTC"), apiResponseArray
                        .getJSONObject(0).getString("DATE_CREATED_UTC"));
    }
    
    /**
     * Positive test case for listProjects method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listProjects} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateProjectWithMandatoryParameters", "testCreateProjectWithOptionalParameters" })
    public void testListProjectsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_optional.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint =
                        apiRequestUrl + "/Projects?ids=" + connectorProperties.getProperty("projectIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), 1);
        Assert.assertEquals(apiResponseArray.length(), 1);
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("PROJECT_ID"), apiResponseArray
                        .getJSONObject(0).getString("PROJECT_ID"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("PROJECT_NAME"), apiResponseArray
                        .getJSONObject(0).getString("PROJECT_NAME"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("VISIBLE_TO"), apiResponseArray
                        .getJSONObject(0).getString("VISIBLE_TO"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("COMPLETED_DATE"), apiResponseArray
                        .getJSONObject(0).getString("COMPLETED_DATE"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("DATE_CREATED_UTC"), apiResponseArray
                        .getJSONObject(0).getString("DATE_CREATED_UTC"));
    }
    
    /**
     * Negative test case for listProjects method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listProjects} integration test with negative case.")
    public void testListProjectsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/Projects?ids=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for createNote method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createNote} integration test with mandatory parameters.")
    public void testCreateNoteWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/Notes/" + esbRestResponse.getBody().getString("NOTE_ID");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("titleMandatory"), apiRestResponse.getBody().getString(
                        "TITLE"));
        Assert.assertEquals(connectorProperties.getProperty("opportunityIdOptional"), apiRestResponse.getBody()
                        .getString("LINK_SUBJECT_ID"));
        Assert.assertEquals(connectorProperties.getProperty("linkSubjectType").toLowerCase(), apiRestResponse.getBody()
                        .getString("LINK_SUBJECT_TYPE").toLowerCase());
    }
    
    /**
     * Positive test case for createNote method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createNote} integration test with optional parameters.", dependsOnMethods = { "testCreateOpportunityWithOptionalParameters" })
    public void testCreateNoteWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_optional.json");
        
        final String noteIdOptional = esbRestResponse.getBody().getString("NOTE_ID");
        connectorProperties.setProperty("noteIdOptional", noteIdOptional);
        
        final String apiEndPoint = apiRequestUrl + "/Notes/" + noteIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("titleOptional"), apiRestResponse.getBody().getString(
                        "TITLE"));
        Assert.assertEquals(connectorProperties.getProperty("opportunityIdOptional"), apiRestResponse.getBody()
                        .getString("LINK_SUBJECT_ID"));
        Assert.assertEquals(connectorProperties.getProperty("linkSubjectType").toLowerCase(), apiRestResponse.getBody()
                        .getString("LINK_SUBJECT_TYPE").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("visibleTo").toLowerCase(), apiRestResponse.getBody()
                        .getString("VISIBLE_TO").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("ownerUserId"), apiRestResponse.getBody().getString(
                        "OWNER_USER_ID"));
        Assert.assertEquals(connectorProperties.getProperty("noteBodyOptional"), apiRestResponse.getBody().getString(
                        "BODY"));
    }
    
    /**
     * Negative test case for createNote method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createNote} integration test with negative case.")
    public void testCreateNoteWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/Notes";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                                        "api_createProject_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for updateNote method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {updateNote} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateNoteWithOptionalParameters", "testCreateContactWithOptionalParameters" })
    public void testUpdateNoteWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateNote");
        
        final String apiEndPoint = apiRequestUrl + "/Notes/" + connectorProperties.getProperty("noteIdOptional");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateNote_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("TITLE"), apiRestResponseAfter.getBody()
                        .getString("TITLE"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("BODY"), apiRestResponseAfter.getBody()
                        .getString("BODY"));
        
        Assert.assertEquals(connectorProperties.getProperty("titleUpdated"), apiRestResponseAfter.getBody().getString(
                        "TITLE"));
        Assert.assertEquals(connectorProperties.getProperty("noteBodyUpdated"), apiRestResponseAfter.getBody()
                        .getString("BODY"));
    }
    
    /**
     * Negative test case for updateNote method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {updateNote} integration test with negative case.")
    public void testUpdateNoteWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateNote");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateNote_negative.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Notes";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateNote_negative.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Message"), apiResponseArray.getJSONObject(0).get(
                        "Message"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Name"), apiResponseArray.getJSONObject(0)
                        .get("Name"));
    }
    
    /**
     * Positive test case for getNote method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {getNote} integration test with mandatory parameters.", dependsOnMethods = { "testCreateNoteWithOptionalParameters" })
    public void testGetNoteWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getNote");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNote_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/Notes/" + connectorProperties.getProperty("noteIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("TITLE"), apiRestResponse.getBody().getString("TITLE"));
        Assert.assertEquals(esbRestResponse.getBody().getString("LINK_SUBJECT_ID"), apiRestResponse.getBody()
                        .getString("LINK_SUBJECT_ID"));
        Assert.assertEquals(esbRestResponse.getBody().getString("LINK_SUBJECT_TYPE").toLowerCase(), apiRestResponse
                        .getBody().getString("LINK_SUBJECT_TYPE").toLowerCase());
        Assert.assertEquals(esbRestResponse.getBody().getString("VISIBLE_TO").toLowerCase(), apiRestResponse.getBody()
                        .getString("VISIBLE_TO").toLowerCase());
        Assert.assertEquals(esbRestResponse.getBody().getString("BODY"), apiRestResponse.getBody().getString("BODY"));
    }
    
    /**
     * Positive test case for listNotes method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listNotes} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateNoteWithMandatoryParameters", "testCreateNoteWithOptionalParameters" })
    public void testListNotesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listNotes");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotes_mandatory.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Notes";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("TITLE"), apiResponseArray.getJSONObject(0)
                        .getString("TITLE"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("VISIBLE_TO"), apiResponseArray
                        .getJSONObject(0).getString("VISIBLE_TO"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("BODY"), apiResponseArray.getJSONObject(0)
                        .getString("BODY"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("DATE_CREATED_UTC"), apiResponseArray
                        .getJSONObject(0).getString("DATE_CREATED_UTC"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("DATE_UPDATED_UTC"), apiResponseArray
                        .getJSONObject(0).getString("DATE_UPDATED_UTC"));
    }
    
    /**
     * Positive test case for createTask method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createTask} integration test with mandatory parameters.")
    public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/Tasks/" + esbRestResponse.getBody().getString("TASK_ID");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("titleMandatory"), apiRestResponse.getBody().getString(
                        "Title"));
        Assert.assertEquals(connectorProperties.getProperty("publiclyVisible").toLowerCase(), apiRestResponse.getBody()
                        .getString("PUBLICLY_VISIBLE").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("completed").toLowerCase(), apiRestResponse.getBody()
                        .getString("COMPLETED").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("ownerUserId"), apiRestResponse.getBody().getString(
                        "OWNER_USER_ID"));
        Assert.assertEquals(connectorProperties.getProperty("ownerUserId"), apiRestResponse.getBody().getString(
                        "RESPONSIBLE_USER_ID"));
    }
    
    /**
     * Positive test case for createTask method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createTask} integration test with optional parameters.", dependsOnMethods = { "testCreateProjectWithOptionalParameters" })
    public void testCreateTaskWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");
        
        final String taskIdOptional = esbRestResponse.getBody().getString("TASK_ID");
        connectorProperties.setProperty("taskIdOptional", taskIdOptional);
        
        final String apiEndPoint = apiRequestUrl + "/Tasks/" + taskIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("ownerVisible").toLowerCase(), apiRestResponse.getBody()
                        .getString("OWNER_VISIBLE").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("startDate"), apiRestResponse.getBody().getString(
                        "START_DATE"));
        Assert.assertEquals(connectorProperties.getProperty("dueDate").toLowerCase(), apiRestResponse.getBody()
                        .getString("DUE_DATE").toLowerCase());
        // Assert.assertEquals(connectorProperties.getProperty("percentComplete"),
        // apiRestResponse.getBody().getString("PERCENT_COMPLETE"));
        Assert.assertEquals(connectorProperties.getProperty("priority").toLowerCase(), apiRestResponse.getBody()
                        .getString("PRIORITY").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("status").toLowerCase(), apiRestResponse.getBody()
                        .getString("STATUS").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("details"), apiRestResponse.getBody().getString("DETAILS"));
        Assert.assertEquals(connectorProperties.getProperty("projectIdOptional"), apiRestResponse.getBody().getString(
                        "PROJECT_ID"));
    }
    
    /**
     * Negative test case for createTask method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {createTask} integration test with negative case.")
    public void testCreateTaskWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/Tasks";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for updateTask method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {updateTask} integration test with optional parameters.", dependsOnMethods = { "testCreateTaskWithOptionalParameters" })
    public void testUpdateTaskWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        
        final String apiEndPoint = apiRequestUrl + "/Tasks/" + connectorProperties.getProperty("taskIdOptional");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("Title"), apiRestResponseAfter.getBody()
                        .getString("Title"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("DETAILS"), apiRestResponseAfter.getBody()
                        .getString("DETAILS"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("START_DATE"), apiRestResponseAfter.getBody()
                        .getString("START_DATE"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("DUE_DATE"), apiRestResponseAfter.getBody()
                        .getString("DUE_DATE"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("STATUS").toLowerCase(), apiRestResponseAfter
                        .getBody().getString("STATUS").toLowerCase());
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("PRIORITY").toLowerCase(),
                        apiRestResponseAfter.getBody().getString("PRIORITY").toLowerCase());
        
        Assert.assertEquals(connectorProperties.getProperty("taskTitleUpdated"), apiRestResponseAfter.getBody()
                        .getString("Title"));
        Assert.assertEquals(connectorProperties.getProperty("detailsUpdated"), apiRestResponseAfter.getBody()
                        .getString("DETAILS"));
        Assert.assertEquals(connectorProperties.getProperty("startDateUpdated"), apiRestResponseAfter.getBody()
                        .getString("START_DATE"));
        Assert.assertEquals(connectorProperties.getProperty("dueDateUpdated"), apiRestResponseAfter.getBody()
                        .getString("DUE_DATE"));
        Assert.assertEquals(connectorProperties.getProperty("statusUpdated").toLowerCase(), apiRestResponseAfter
                        .getBody().getString("STATUS").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("priorityUpdated").toLowerCase(), apiRestResponseAfter
                        .getBody().getString("PRIORITY").toLowerCase());
    }
    
    /**
     * Negative test case for updateTask method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {updateTask} integration test with negative case.")
    public void testUpdateTaskWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/Tasks";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for getTask method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {getTask} integration test with mandatory parameters.", dependsOnMethods = { "testCreateTaskWithOptionalParameters" })
    public void testGetTaskWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/Tasks/" + connectorProperties.getProperty("taskIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("Title"), apiRestResponse.getBody().getString("Title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("DETAILS"), apiRestResponse.getBody().getString(
                        "DETAILS"));
        Assert.assertEquals(esbRestResponse.getBody().getString("START_DATE"), apiRestResponse.getBody().getString(
                        "START_DATE"));
        Assert.assertEquals(esbRestResponse.getBody().getString("DUE_DATE"), apiRestResponse.getBody().getString(
                        "DUE_DATE"));
        Assert.assertEquals(esbRestResponse.getBody().getString("STATUS").toLowerCase(), apiRestResponse.getBody()
                        .getString("STATUS").toLowerCase());
        Assert.assertEquals(esbRestResponse.getBody().getString("PRIORITY").toLowerCase(), apiRestResponse.getBody()
                        .getString("PRIORITY").toLowerCase());
    }
    
    /**
     * Positive test case for listTasks method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listTasks} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateTaskWithMandatoryParameters", "testCreateTaskWithOptionalParameters" })
    public void testListTasksWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_mandatory.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Tasks";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("Title"), apiResponseArray.getJSONObject(0)
                        .getString("Title"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("DETAILS"), apiResponseArray.getJSONObject(0)
                        .getString("DETAILS"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("START_DATE"), apiResponseArray
                        .getJSONObject(0).getString("START_DATE"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("DUE_DATE"), apiResponseArray.getJSONObject(0)
                        .getString("DUE_DATE"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("STATUS"), apiResponseArray.getJSONObject(0)
                        .getString("STATUS"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("PRIORITY"), apiResponseArray.getJSONObject(0)
                        .getString("PRIORITY"));
    }
    
    /**
     * Positive test case for listTasks method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listTasks} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateTaskWithMandatoryParameters", "testCreateTaskWithOptionalParameters" })
    public void testListTasksWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_optional.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/Tasks?ids=" + connectorProperties.getProperty("taskIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), 1);
        Assert.assertEquals(apiResponseArray.length(), 1);
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("Title"), apiResponseArray.getJSONObject(0)
                        .getString("Title"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("DETAILS"), apiResponseArray.getJSONObject(0)
                        .getString("DETAILS"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("START_DATE"), apiResponseArray
                        .getJSONObject(0).getString("START_DATE"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("DUE_DATE"), apiResponseArray.getJSONObject(0)
                        .getString("DUE_DATE"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("STATUS"), apiResponseArray.getJSONObject(0)
                        .getString("STATUS"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("PRIORITY"), apiResponseArray.getJSONObject(0)
                        .getString("PRIORITY"));
    }
    
    /**
     * Negative test case for listTasks method.
     */
    @Test(groups = { "wso2.esb" }, description = "insightly {listTasks} integration test with negative case.")
    public void testListTasksWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/Tasks?ids=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
}
