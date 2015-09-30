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

package org.wso2.carbon.connector.integration.test.sendloop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class SendloopConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("sendloop-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        apiRequestHeadersMap.put("Accept", "application/json");
        
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/api/v3";
        
    }
    
    /**
     * Positive test case for createEmailCampaign method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {createEmailCampaign} integration test with mandatory parameters.", dependsOnMethods = { "testListSubscribersWithOptionalParameters" })
    public void testCreateEmailCampaignWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createEmailCampaign");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createEmailCampaign_mandatory.json");
        
        final String campaignIdMandatory = esbRestResponse.getBody().getString("CampaignID");
        connectorProperties.put("campaignIdMandatory", campaignIdMandatory);
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createEmailCampaign_mandatory.json");
        
        Assert.assertEquals(connectorProperties.getProperty("mandatoryEmailCampaignName"), apiRestResponse.getBody()
                        .getJSONObject("Campaign").getString("CampaignName"));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryEmailCampaignFromName"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("FromName"));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryEmailCampaignFromEmail"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("FromEmail"));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryEmailCampaignReplyToName"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("ReplyToName"));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryEmailCampaignReplyToEmail"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("ReplyToEmail"));
        Assert.assertEquals(connectorProperties.getProperty("subscriberListIdMandatory"), apiRestResponse.getBody()
                        .getJSONObject("Campaign").getJSONArray("Lists").getString(0));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryEmailCampaignSubject"), apiRestResponse.getBody()
                        .getJSONObject("Campaign").getString("Subject"));
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), true);
        
    }
    
    /**
     * Positive test case for createEmailCampaign method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {createEmailCampaign} integration test with optional parameters.", dependsOnMethods = { "testCreateEmailCampaignWithMandatoryParameters" })
    public void testCreateEmailCampaignWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createEmailCampaign");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createEmailCampaign_optional.json");
        
        final String campaignIdOptional = esbRestResponse.getBody().getString("CampaignID");
        connectorProperties.put("campaignIdOptional", campaignIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createEmailCampaign_optional.json");
        
        Assert.assertEquals(connectorProperties.getProperty("optionalEmailCampaignName"), apiRestResponse.getBody()
                        .getJSONObject("Campaign").getString("CampaignName"));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryEmailCampaignFromName"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("FromName"));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryEmailCampaignFromEmail"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("FromEmail"));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryEmailCampaignReplyToName"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("ReplyToName"));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryEmailCampaignReplyToEmail"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("ReplyToEmail"));
        Assert.assertEquals(connectorProperties.getProperty("subscriberListIdMandatory"), apiRestResponse.getBody()
                        .getJSONObject("Campaign").getJSONArray("Lists").getString(0));
        Assert.assertEquals(connectorProperties.getProperty("optionalEmailCampaignSubject"), apiRestResponse.getBody()
                        .getJSONObject("Campaign").getString("Subject"));
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), true);
        Assert.assertEquals(connectorProperties.getProperty("optionalEmailCampaignPlainContent"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("PlainContent"));
        
    }
    
    /**
     * Negative test case for createEmailCampaign method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {createEmailCampaign} integration test with negative case.")
    public void testCreateEmailCampaignWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createEmailCampaign");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createEmailCampaign_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.Create/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createEmailCampaign_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Method name: updateEmailCampaign
     * Test scenario: Mandatory
     * Reason to skip: There are no mandatory
     * parameters in the method except for emailCampaingId which alone doesn't make any sense when used in the
     * method.
     */
    
    /**
     * Positive test case for updateEmailCampaign method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {updateEmailCampaign} integration test with optional parameters.", dependsOnMethods = { "testCreateEmailCampaignWithOptionalParameters" })
    public void testUpdateEmailCampaignWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateEmailCampaign");
        final String apiEndpoint = apiEndpointUrl + "/Campaign.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_updateEmailCampaign_optional.json");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_updateEmailCampaign_optional.json");
        
        RestResponse<JSONObject> apiRestResponse2 =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_updateEmailCampaign_optional.json");
        
        Assert.assertNotEquals(apiRestResponse.getBody().getJSONObject("Campaign").getString("CampaignName"),
                        apiRestResponse2.getBody().getJSONObject("Campaign").getString("CampaignName"));
        Assert.assertNotEquals(apiRestResponse.getBody().getJSONObject("Campaign").getString("FromName"),
                        apiRestResponse2.getBody().getJSONObject("Campaign").getString("FromName"));
        Assert.assertNotEquals(apiRestResponse.getBody().getJSONObject("Campaign").getString("FromEmail"),
                        apiRestResponse2.getBody().getJSONObject("Campaign").getString("FromEmail"));
        Assert.assertNotEquals(apiRestResponse.getBody().getJSONObject("Campaign").getString("ReplyToName"),
                        apiRestResponse2.getBody().getJSONObject("Campaign").getString("ReplyToName"));
        Assert.assertNotEquals(apiRestResponse.getBody().getJSONObject("Campaign").getString("ReplyToEmail"),
                        apiRestResponse2.getBody().getJSONObject("Campaign").getString("ReplyToEmail"));
        Assert.assertNotEquals(apiRestResponse.getBody().getJSONObject("Campaign").getString("Subject"),
                        apiRestResponse2.getBody().getJSONObject("Campaign").getString("Subject"));
        Assert.assertEquals(connectorProperties.getProperty("campaignCampaignNameUpdated"), apiRestResponse2.getBody()
                        .getJSONObject("Campaign").getString("CampaignName"));
        Assert.assertEquals(connectorProperties.getProperty("campaignFromNameUpdated"), apiRestResponse2.getBody()
                        .getJSONObject("Campaign").getString("FromName"));
        Assert.assertEquals(connectorProperties.getProperty("campaignFromEmailUpdated"), apiRestResponse2.getBody()
                        .getJSONObject("Campaign").getString("FromEmail"));
        Assert.assertEquals(connectorProperties.getProperty("campaignReplyToNameUpdated"), apiRestResponse2.getBody()
                        .getJSONObject("Campaign").getString("ReplyToName"));
        Assert.assertEquals(connectorProperties.getProperty("campaignReplyToEmailUpdated"), apiRestResponse2.getBody()
                        .getJSONObject("Campaign").getString("ReplyToEmail"));
        Assert.assertEquals(connectorProperties.getProperty("campaignSubjectUpdated"), apiRestResponse2.getBody()
                        .getJSONObject("Campaign").getString("Subject"));
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), true);
        
    }
    
    /**
     * Negative test case for updateEmailCampaign method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {updateEmailCampaign} integration test with negative case.", dependsOnMethods = { "testUpdateEmailCampaignWithOptionalParameters" })
    public void testUpdateEmailCampaignWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateEmailCampaign");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_updateEmailCampaign_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.Update/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_updateEmailCampaign_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for getEmailCampaign method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getEmailCampaign} integration test with mandatory parameters.", dependsOnMethods = { "testUpdateEmailCampaignWithNegativeCase" })
    public void testGetEmailCampaignWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getEmailCampaign");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getEmailCampaign_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getEmailCampaign_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Campaign").getString("CampaignName"),
                        apiRestResponse.getBody().getJSONObject("Campaign").getString("CampaignName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Campaign").getString("FromName"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("FromName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Campaign").getString("FromEmail"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("FromEmail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Campaign").getString("ReplyToName"),
                        apiRestResponse.getBody().getJSONObject("Campaign").getString("ReplyToName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Campaign").getString("ReplyToEmail"),
                        apiRestResponse.getBody().getJSONObject("Campaign").getString("ReplyToEmail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Campaign").getJSONArray("Lists").getString(0),
                        apiRestResponse.getBody().getJSONObject("Campaign").getJSONArray("Lists").getString(0));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Campaign").getString("Subject"), apiRestResponse
                        .getBody().getJSONObject("Campaign").getString("Subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Campaign").getString("PlainContent"),
                        apiRestResponse.getBody().getJSONObject("Campaign").getString("PlainContent"));
        
    }
    
    /**
     * Method name: getEmailCampaign
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters
     * to be tested in method.
     */
    
    /**
     * Negative test case for getEmailCampaign method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getEmailCampaign} integration test with negative case.")
    public void testGetEmailCampaignWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getEmailCampaign");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getEmailCampaign_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getEmailCampaign_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for listEmailCampaigns method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listEmailCampaigns} integration test with mandatory parameters.", dependsOnMethods = { "testGetEmailCampaignWithMandatoryParameters" })
    public void testListEmailCampaignsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listEmailCampaigns_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.GetList/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listEmailCampaigns_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").length(), apiRestResponse.getBody()
                        .getJSONArray("Campaigns").length());
        
    }
    
    /**
     * Positive test case for listEmailCampaigns method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listEmailCampaigns} integration test with optional parameters.", dependsOnMethods = { "testListEmailCampaignsWithMandatoryParameters" })
    public void testListEmailCampaignsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listEmailCampaigns_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.GetList/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listEmailCampaigns_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").length(), apiRestResponse.getBody()
                        .getJSONArray("Campaigns").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ReplyToEmail"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("ReplyToEmail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("Subject"),
                        apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("Subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignID"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "CampaignStatus"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignStatus"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "CampaignName"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromName"),
                        apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromName"));
        Assert.assertEquals(
                        esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromEmail"),
                        apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromEmail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ReplyToName"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ReplyToName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ScheduleType"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("ScheduleType"));
        
    }
    
    /**
     * Negative test case for listEmailCampaigns method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listEmailCampaigns} integration test with negative case.")
    public void testListEmailCampaignsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listEmailCampaigns_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.GetList/INVALID";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listEmailCampaigns_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for listEmailCampaignsByStatus method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listEmailCampaignsByStatus} integration test with mandatory parameters.", dependsOnMethods = { "testListEmailCampaignsWithOptionalParameters" })
    public void testListEmailCampaignsByStatusWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaignsByStatus");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listEmailCampaignsByStatus_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.GetListByStatus/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listEmailCampaignsByStatus_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").length(), apiRestResponse.getBody()
                        .getJSONArray("Campaigns").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ReplyToEmail"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("ReplyToEmail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("Subject"),
                        apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("Subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignID"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "CampaignName"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromName"),
                        apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromName"));
        Assert.assertEquals(
                        esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromEmail"),
                        apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromEmail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ReplyToName"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ReplyToName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ScheduleType"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("ScheduleType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "CampaignStatus"), "Draft");
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "CampaignStatus"), "Draft");
        
    }
    
    /**
     * Positive test case for listEmailCampaignsByStatus method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listEmailCampaignsByStatus} integration test with optional parameters.", dependsOnMethods = { "testListEmailCampaignsByStatusWithMandatoryParameters" })
    public void testListEmailCampaignsByStatusWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaignsByStatus");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listEmailCampaignsByStatus_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.GetListByStatus/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listEmailCampaignsByStatus_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ReplyToEmail"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("ReplyToEmail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("Subject"),
                        apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("Subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignID"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "CampaignStatus"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignStatus"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "CampaignName"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("CampaignName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromName"),
                        apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromName"));
        Assert.assertEquals(
                        esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromEmail"),
                        apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString("FromEmail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ReplyToName"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ReplyToName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "ScheduleType"), apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0)
                        .getString("ScheduleType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("Campaigns").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "CampaignStatus"), "Draft");
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("Campaigns").getJSONObject(0).getString(
                        "CampaignStatus"), "Draft");
        
    }
    
    /**
     * Negative test case for listEmailCampaignsByStatus method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listEmailCampaignsByStatus} integration test with negative case.")
    public void testListEmailCampaignsByStatusWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaignsByStatus");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listEmailCampaignsByStatus_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.GetListByStatus/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listEmailCampaignsByStatus_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        
    }
    
    /**
     * Positive test case for sendEmailCampaign method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException 
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {sendEmailCampaign} integration test with mandatory parameters.", dependsOnMethods = { "testListEmailCampaignsByStatusWithOptionalParameters" })
    public void testSendEmailCampaignWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:sendEmailCampaign");
        final String apiEndpoint = apiEndpointUrl + "/Campaign.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_sendEmailCampaign_mandatory.json");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_sendEmailCampaign_mandatory.json");
        
        // Sleeping for 10s until the campaign is sent.
        Thread.sleep(10000);
        
        RestResponse<JSONObject> apiRestResponse2 =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_sendEmailCampaign_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), true);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("Campaign").getString("CampaignStatus"), "Draft");
        Assert.assertEquals(apiRestResponse2.getBody().getJSONObject("Campaign").getString("CampaignStatus"), "Sent");
        
    }
    
    /**
     * Method name: sendEmailCampaign
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters
     * to be tested in method.
     */
    
    /**
     * Negative test case for sendEmailCampaign method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {sendEmailCampaign} integration test with negative case.", dependsOnMethods = { "testSendEmailCampaignWithMandatoryParameters" })
    public void testSendEmailCampaignWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:sendEmailCampaign");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_sendEmailCampaign_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Campaign.Send/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_sendEmailCampaign_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for createSubscriberList method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {createSubscriberList} integration test with mandatory parameters.")
    public void testCreateSubscriberListWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createSubscriberList");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createSubscriberList_mandatory.json");
        
        final String subscriberListIdMandatory = esbRestResponse.getBody().getString("ListID");
        connectorProperties.put("subscriberListIdMandatory", subscriberListIdMandatory);
        
        final String apiEndpoint = apiEndpointUrl + "/List.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createSubscriberList_mandatory.json");
        
        Assert.assertEquals(connectorProperties.getProperty("subscriberListName"), apiRestResponse.getBody()
                        .getJSONObject("List").getString("Name"));
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), true);
        Assert.assertEquals(apiRestResponse.getBody().getBoolean("Success"), true);
        
    }
    
    /**
     * Method name: createSubscriberList
     * Test scenario: Optional
     * Reason to skip: There are no optional
     * parameters to be tested in createSubscriberList method.
     */
    
    /**
     * Negative test case for createSubscriberList method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {createSubscriberList} integration test with negative case.")
    public void testCreateSubscriberListWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createSubscriberList");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createSubscriberList_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/List.Create/INVALID";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createSubscriberList_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Method name: updateSubscriberList
     * Test scenario: Mandatory
     * Reason to skip: There are no mandatory
     * parameters in the method except for subscriberListId which alone doesn't make any sense when used in
     * the method.
     */
    
    /**
     * Positive test case for updateSubscriberList method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {updateSubscriberList} integration test with optional parameters.", dependsOnMethods = { "testCreateSubscriberListWithMandatoryParameters" })
    public void testUpdateSubscriberListWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateSubscriberList");
        final String apiEndpoint = apiEndpointUrl + "/List.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_updateSubscriberList_optional.json");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_updateSubscriberList_optional.json");
        
        RestResponse<JSONObject> apiRestResponse2 =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_updateSubscriberList_optional.json");
        
        Assert.assertNotEquals(apiRestResponse.getBody().getJSONObject("List").getString("Name"), apiRestResponse2
                        .getBody().getJSONObject("List").getString("Name"));
        Assert.assertEquals(connectorProperties.getProperty("subscriberListNameUpdated"), apiRestResponse2.getBody()
                        .getJSONObject("List").getString("Name"));
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), true);
        
    }
    
    /**
     * Negative test case for updateSubscriberList method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {updateSubscriberList} integration test with negative case.")
    public void testUpdateSubscriberListWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateSubscriberList");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_updateSubscriberList_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/List.Update/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_updateSubscriberList_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for getSubscriberList method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getSubscriberList} integration test with mandatory parameters.", dependsOnMethods = { "testUpdateSubscriberListWithOptionalParameters" })
    public void testGetSubscriberListWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getSubscriberList");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getSubscriberList_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/List.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getSubscriberList_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("List").getString("Name"), apiRestResponse
                        .getBody().getJSONObject("List").getString("Name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("List").getString("CreatedOn"), apiRestResponse
                        .getBody().getJSONObject("List").getString("CreatedOn"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("List").getString("SubscriptionFormURL"),
                        apiRestResponse.getBody().getJSONObject("List").getString("SubscriptionFormURL"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("List").getString("UnsubscriptionFormURL"),
                        apiRestResponse.getBody().getJSONObject("List").getString("UnsubscriptionFormURL"));
        
    }
    
    /**
     * Positive test case for getSubscriberList method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getSubscriberList} integration test with optional parameters.", dependsOnMethods = { "testGetSubscriberListWithMandatoryParameters" })
    public void testGetSubscriberListWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getSubscriberList");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getSubscriberList_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/List.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getSubscriberList_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("List").getString("Name"), apiRestResponse
                        .getBody().getJSONObject("List").getString("Name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("List").getString("CreatedOn"), apiRestResponse
                        .getBody().getJSONObject("List").getString("CreatedOn"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("List").getString("SubscriptionFormURL"),
                        apiRestResponse.getBody().getJSONObject("List").getString("SubscriptionFormURL"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("List").getString("UnsubscriptionFormURL"),
                        apiRestResponse.getBody().getJSONObject("List").getString("UnsubscriptionFormURL"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("CustomFields").getJSONObject(0).getString(
                        "CustomFieldID"), apiRestResponse.getBody().getJSONArray("CustomFields").getJSONObject(0)
                        .getString("CustomFieldID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("CustomFields").getJSONObject(0).getString(
                        "FieldDataType"), apiRestResponse.getBody().getJSONArray("CustomFields").getJSONObject(0)
                        .getString("FieldDataType"));
        
    }
    
    /**
     * Negative test case for getSubscriberList method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getSubscriberList} integration test with negative case.")
    public void testGetSubscriberListWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getSubscriberList");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getSubscriberList_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/List.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getSubscriberList_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for listSubscriberLists method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listSubscriberLists} integration test with mandatory parameters.", dependsOnMethods = { "testGetSubscriberListWithOptionalParameters" })
    public void testListSubscriberListsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listSubscriberLists");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listSubscriberLists_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/List.GetList/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listSubscriberLists_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Lists").getJSONObject(0).getString("ListID"),
                        apiRestResponse.getBody().getJSONArray("Lists").getJSONObject(0).getString("ListID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Lists").getJSONObject(0).getString("Name"),
                        apiRestResponse.getBody().getJSONArray("Lists").getJSONObject(0).getString("Name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Lists").getJSONObject(0).getString("CreatedOn"),
                        apiRestResponse.getBody().getJSONArray("Lists").getJSONObject(0).getString("CreatedOn"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Lists").getJSONObject(0).getJSONObject(
                        "SubscriptionActivity").length(), apiRestResponse.getBody().getJSONArray("Lists")
                        .getJSONObject(0).getJSONObject("SubscriptionActivity").length());
        
    }
    
    /**
     * Method name: listSubscriberLists
     * Test scenario: Optional
     * Reason to skip: There are no optional
     * parameters to be tested in method.
     */
    
    /**
     * Negative test case for listSubscriberLists method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listSubscriberLists} integration test with negative case.")
    public void testListSubscriberListsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listSubscriberLists");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listSubscriberLists_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/List.GetList/INVALID";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listSubscriberLists_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for subscribe method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {subscribe} integration test with mandatory parameters.", dependsOnMethods = { "testListSubscriberListsWithMandatoryParameters" })
    public void testSubscribeWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:subscribe");
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Browse/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_subscribe_mandatory.json");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_subscribe_mandatory.json");
        
        final String subscriberIdMandatory = esbRestResponse.getBody().getString("SubscriberID");
        connectorProperties.put("subscriberIdMandatory", subscriberIdMandatory);
        
        RestResponse<JSONObject> apiRestResponse2 =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_subscribe_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("Subscribers").length(), 0);
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), true);
        Assert.assertEquals(esbRestResponse.getBody().getString("SubscriptionStatus"), "Subscribed");
        Assert.assertEquals(apiRestResponse2.getBody().getJSONArray("Subscribers").length(), 1);
        
    }
    
    /**
     * Positive test case for subscribe method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {subscribe} integration test with optional parameters.", dependsOnMethods = { "testSubscribeWithMandatoryParameters" })
    public void testSubscribeWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:subscribe");
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Browse/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_subscribe_optional.json");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_subscribe_optional.json");
        
        RestResponse<JSONObject> apiRestResponse2 =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_subscribe_optional.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("Subscribers").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), true);
        Assert.assertEquals(esbRestResponse.getBody().getString("SubscriptionStatus"), "Subscribed");
        Assert.assertEquals(apiRestResponse2.getBody().getJSONArray("Subscribers").length(), 2);
        
    }
    
    /**
     * Negative test case for subscribe method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {subscribe} integration test with negative case.", dependsOnMethods = { "testSubscribeWithOptionalParameters" })
    public void testSubscribeWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:subscribe");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_subscribe_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Subscribe/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_subscribe_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for unsubscribe method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {unsubscribe} integration test with mandatory parameters.", dependsOnMethods = { "testSendEmailCampaignWithNegativeCase" })
    public void testUnsubscribeWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:unsubscribe");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unsubscribe_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_unsubscribe_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), true);
        Assert.assertNotNull(esbRestResponse.getBody().getString("RedirectURL"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionStatus"),
                        "Unsubscribed");
        
    }
    
    /**
     * Method name: unsubscribe Test scenario: Optional Reason to skip: There are no optional parameters to be
     * tested in method.
     */
    
    /**
     * Negative test case for unsubscribe method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {unsubscribe} integration test with negative case.", dependsOnMethods = { "testUnsubscribeWithMandatoryParameters" })
    public void testUnsubscribeWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:unsubscribe");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unsubscribe_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Unsubscribe/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_unsubscribe_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Method name: updateSubscriber
     * Test scenario: Mandatory
     * Reason to skip: There are no mandatory
     * parameters in the method except for subscriberId which alone doesn't make any sense when used in the
     * method.
     */
    
    /**
     * Positive test case for updateSubscriber method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {updateSubscriber} integration test with optional parameters.", dependsOnMethods = { "testSearchSubscriberWithMandatoryParameters" })
    public void testUpdateSubscriberWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateSubscriber");
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_updateSubscriber_optional.json");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_updateSubscriber_optional.json");
        
        RestResponse<JSONObject> apiRestResponse2 =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_updateSubscriber_optional.json");
        
        Assert.assertNotEquals(apiRestResponse.getBody().getJSONObject("Subscriber").getString("EmailAddress"),
                        apiRestResponse2.getBody().getJSONObject("Subscriber").getString("EmailAddress"));
        Assert.assertEquals(connectorProperties.getProperty("subcriberEmailAddressUpdated"), apiRestResponse2.getBody()
                        .getJSONObject("Subscriber").getString("EmailAddress"));
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), true);
        
    }
    
    /**
     * Negative test case for updateSubscriber method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {updateSubscriber} integration test with negative case.", dependsOnMethods = { "testUpdateSubscriberWithOptionalParameters" })
    public void testUpdateSubscriberWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateSubscriber");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_updateSubscriber_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Update/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_updateSubscriber_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for searchSubscriber method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {searchSubscriber} integration test with mandatory parameters.", dependsOnMethods = { "testSubscribeWithNegativeCase" })
    public void testSearchSubscriberWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchSubscriber");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_searchSubscriber_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Search/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_searchSubscriber_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("SearchResults").getJSONObject(0).getInt("ListID"),
                        apiRestResponse.getBody().getJSONArray("SearchResults").getJSONObject(0).getInt("ListID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("SearchResults").getJSONObject(0).getString(
                        "ListName"), apiRestResponse.getBody().getJSONArray("SearchResults").getJSONObject(0)
                        .getString("ListName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("SearchResults").getJSONObject(0).getJSONObject(
                        "Subscribers").length(), apiRestResponse.getBody().getJSONArray("SearchResults").getJSONObject(
                        0).getJSONObject("Subscribers").length());
        
    }
    
    /**
     * Method name: searchSubscriber
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters
     * to be tested in method.
     */
    
    /**
     * Negative test case for searchSubscriber method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {searchSubscriber} integration test with negative case.")
    public void testSearchSubscriberWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchSubscriber");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_searchSubscriber_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Search/INVALID";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_searchSubscriber_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for getSubscriber method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getSubscriber} integration test with mandatory parameters.", dependsOnMethods = { "testUpdateSubscriberWithNegativeCase" })
    public void testGetSubscriberWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getSubscriber");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubscriber_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getSubscriber_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Subscriber").getString("EmailAddress"),
                        apiRestResponse.getBody().getJSONObject("Subscriber").getString("EmailAddress"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionIP"),
                        apiRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionIP"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Subscriber").getString("BounceType"),
                        apiRestResponse.getBody().getJSONObject("Subscriber").getString("BounceType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionDate"),
                        apiRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionDate"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionStatus"),
                        apiRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionStatus"));
        
    }
    
    /**
     * Positive test case for getSubscriber method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getSubscriber} integration test with optional parameters.", dependsOnMethods = { "testGetSubscriberWithMandatoryParameters" })
    public void testGetSubscriberWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getSubscriber");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubscriber_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getSubscriber_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Subscriber").getString("EmailAddress"),
                        apiRestResponse.getBody().getJSONObject("Subscriber").getString("EmailAddress"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionIP"),
                        apiRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionIP"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Subscriber").getString("BounceType"),
                        apiRestResponse.getBody().getJSONObject("Subscriber").getString("BounceType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionDate"),
                        apiRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionDate"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionStatus"),
                        apiRestResponse.getBody().getJSONObject("Subscriber").getString("SubscriptionStatus"));
        
    }
    
    /**
     * Negative test case for getSubscriber method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getSubscriber} integration test with negative case.", dependsOnMethods = { "testGetSubscriberWithOptionalParameters" })
    public void testGetSubscriberWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getSubscriber");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubscriber_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getSubscriber_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for listSubscribers method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listSubscribers} integration test with mandatory parameters.", dependsOnMethods = { "testGetSubscriberWithNegativeCase" })
    public void testListSubscribersWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listSubscribers");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listSubscribers_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Browse/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listSubscribers_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").length(), apiRestResponse.getBody()
                        .getJSONArray("Subscribers").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0).getString(
                        "SubscriberID"), apiRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0)
                        .getString("SubscriberID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0).getString(
                        "SubscriptionDate"), apiRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0)
                        .getString("SubscriptionDate"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0).getString(
                        "SubscriptionIP"), apiRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0)
                        .getString("SubscriptionIP"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0).getString(
                        "BounceType"), apiRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0)
                        .getString("BounceType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0).getString(
                        "EmailAddress"), apiRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0)
                        .getString("EmailAddress"));
        
    }
    
    /**
     * Positive test case for listSubscribers method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listSubscribers} integration test with optional parameters.", dependsOnMethods = { "testListSubscribersWithMandatoryParameters" })
    public void testListSubscribersWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listSubscribers");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubscribers_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Browse/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listSubscribers_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").length(), apiRestResponse.getBody()
                        .getJSONArray("Subscribers").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0).getString(
                        "SubscriberID"), apiRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0)
                        .getString("SubscriberID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0).getString(
                        "SubscriptionDate"), apiRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0)
                        .getString("SubscriptionDate"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0).getString(
                        "SubscriptionIP"), apiRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0)
                        .getString("SubscriptionIP"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0).getString(
                        "BounceType"), apiRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0)
                        .getString("BounceType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0).getString(
                        "EmailAddress"), apiRestResponse.getBody().getJSONArray("Subscribers").getJSONObject(0)
                        .getString("EmailAddress"));
        
    }
    
    /**
     * Negative test case for listSubscribers method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {listSubscribers} integration test with negative case.", dependsOnMethods = { "testListSubscribersWithOptionalParameters" })
    public void testListSubscribersWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listSubscribers");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubscribers_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Subscriber.Browse/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_listSubscribers_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for getCampaignOpeners method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getCampaignOpeners} integration test with mandatory parameters.")
    public void testGetCampaignOpenersWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getCampaignOpeners");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getCampaignOpeners_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Data.Campaign.EmailOpens/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getCampaignOpeners_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").length(), apiRestResponse.getBody()
                        .getJSONArray("Data").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("SubscriberID"),
                        apiRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("SubscriberID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("OpenDate"),
                        apiRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("OpenDate"));
        
    }
    
    /**
     * Positive test case for getCampaignOpeners method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getCampaignOpeners} integration test with optional parameters.")
    public void testGetCampaignOpenersWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getCampaignOpeners");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getCampaignOpeners_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Data.Campaign.EmailOpens/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getCampaignOpeners_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("SubscriberID"),
                        apiRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("SubscriberID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("OpenDate"),
                        apiRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("OpenDate"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("Data").length(), 1);
        
    }
    
    /**
     * Negative test case for getCampaignOpeners method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getCampaignOpeners} integration test with negative case.")
    public void testGetCampaignOpenersWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getCampaignOpeners");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getCampaignOpeners_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Data.Campaign.EmailOpens/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getCampaignOpeners_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for getCampaignLinkClickers method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getCampaignLinkClickers} integration test with mandatory parameters.")
    public void testGetCampaignLinkClickersWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getCampaignLinkClickers");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getCampaignLinkClickers_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Data.Campaign.LinkClicks/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getCampaignLinkClickers_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").length(), apiRestResponse.getBody()
                        .getJSONArray("Data").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("SubscriberID"),
                        apiRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("SubscriberID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("ClickDate"),
                        apiRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("ClickDate"));
        
    }
    
    /**
     * Positive test case for getCampaignLinkClickers method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getCampaignLinkClickers} integration test with optional parameters.")
    public void testGetCampaignLinkClickersWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getCampaignLinkClickers");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getCampaignLinkClickers_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Data.Campaign.LinkClicks/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getCampaignLinkClickers_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("SubscriberID"),
                        apiRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("SubscriberID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("ClickDate"),
                        apiRestResponse.getBody().getJSONArray("Data").getJSONObject(0).getString("ClickDate"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Data").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("Data").length(), 1);
        
    }
    
    /**
     * Negative test case for getCampaignLinkClickers method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getCampaignLinkClickers} integration test with negative case.")
    public void testGetCampaignLinkClickersWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getCampaignLinkClickers");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getCampaignLinkClickers_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Data.Campaign.LinkClicks/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getCampaignLinkClickers_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("Success"), apiRestResponse.getBody().getBoolean(
                        "Success"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("ErrorCode"), apiRestResponse.getBody()
                        .getInt("ErrorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ErrorMessage"), apiRestResponse.getBody().getString(
                        "ErrorMessage"));
        
    }
    
    /**
     * Positive test case for getAccountDetails method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "sendloop {getAccountDetails} integration test with mandatory parameters.")
    public void testGetAccountDetailsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getAccountDetails");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getAccountDetails_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/Account.Info.Get/json";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_getAccountDetails_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("AccountInfo").getString("Username"),
                        apiRestResponse.getBody().getJSONObject("AccountInfo").getString("Username"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("AccountInfo").getString("Subdomain"),
                        apiRestResponse.getBody().getJSONObject("AccountInfo").getString("Subdomain"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("AccountInfo").getString("MemberSince"),
                        apiRestResponse.getBody().getJSONObject("AccountInfo").getString("MemberSince"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("AccountInfo").getString("FirstName"),
                        apiRestResponse.getBody().getJSONObject("AccountInfo").getString("FirstName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("AccountInfo").getString("LastName"),
                        apiRestResponse.getBody().getJSONObject("AccountInfo").getString("LastName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("AccountInfo").getString("Country"),
                        apiRestResponse.getBody().getJSONObject("AccountInfo").getString("Country"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("AccountInfo").getString("CompanyName"),
                        apiRestResponse.getBody().getJSONObject("AccountInfo").getString("CompanyName"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("AccountInfo").getString("Website"),
                        apiRestResponse.getBody().getJSONObject("AccountInfo").getString("Website"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("AccountInfo").getString("SignUpIP"),
                        apiRestResponse.getBody().getJSONObject("AccountInfo").getString("SignUpIP"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("AccountInfo").getString("TimeZone"),
                        apiRestResponse.getBody().getJSONObject("AccountInfo").getString("TimeZone"));
        
    }
    
    /**
     * Method name: getAccountDetails
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters
     * to be tested in method.
     */
    
    /**
     * Method name: getAccountDetails
     * Test scenario: Negative
     * Reason to skip: The method doesn't have any
     * parameters to test the negative case.
     */
    
}
