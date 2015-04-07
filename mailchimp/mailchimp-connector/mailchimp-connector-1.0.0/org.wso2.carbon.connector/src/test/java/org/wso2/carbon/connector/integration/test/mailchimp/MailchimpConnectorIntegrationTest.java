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
package org.wso2.carbon.connector.integration.test.mailchimp;

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


public class MailchimpConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    private String apiBaseUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {    
        init("mailchimp-connector-1.0.0");        
        esbRequestHeadersMap.put("Content-Type", "application/json");        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiBaseUrl = connectorProperties.getProperty("apiUrl") + "/2.0";        
    }
    
    /**
     * Positive test case for createTemplate method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "mailchimp {createTemplate} integration test with mandatory parameters.")
    public void testCreateTemplateWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTemplate");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createTemplate_mandatory.json");
        final String templateIdMandatory = esbRestResponse.getBody().getString("template_id");
        connectorProperties.setProperty("templateIdMandatory", templateIdMandatory);

        final String apiEndPoint = apiBaseUrl + "/templates/info.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_createTemplate_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        // HTML content is the only attribute of the template that is returned in the GET call.
        Assert.assertEquals(connectorProperties.getProperty("createTemplateHTMLMandatory"),
                apiRestResponse.getBody().getString("source"));
    }
    
    /**
     * Positive test case for createTemplate method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "mailchimp {createTemplate} integration test with optional parameters.")
    public void testCreateTemplateWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTemplate");
        
        // Create a Folder through the ESB call and store the folder_id.
        final String addFolderEndpoint = apiBaseUrl + "/folders/add.json";
        RestResponse<JSONObject> apiCreateFolderResponse = sendJsonRestRequest(addFolderEndpoint, "POST", apiRequestHeadersMap,
                "api_createTemplate_optional_A.json");
        final String folderId = apiCreateFolderResponse.getBody().getString("folder_id");
        connectorProperties.setProperty("folderId", folderId);
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createTemplate_optional.json");

        // List templates put to the folder identified by the folder_id.
        final String apiEndPoint = apiBaseUrl + "/templates/list.json";
        RestResponse<JSONObject> apiListTemplatesResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_createTemplate_optional_B.json");

        // Asserting Template id, name and folderId
        Assert.assertEquals(esbRestResponse.getBody().getString("template_id"),
                apiListTemplatesResponse.getBody().getJSONArray("user").getJSONObject(0).getString("id"));
        Assert.assertEquals(folderId,
                apiListTemplatesResponse.getBody().getJSONArray("user").getJSONObject(0).getString("folder_id"));
        Assert.assertEquals(connectorProperties.getProperty("createTemplateOptionalName"),
                apiListTemplatesResponse.getBody().getJSONArray("user").getJSONObject(0).getString("name"));
    }
    
    /**
     * Negative test case for createTemplate method.
     */
    @Test(groups = { "wso2.esb" }, description = "mailchimp {createTemplate} integration test with negative case.")
    public void testCreateTemplateWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTemplate");        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createTemplate_negative.json");

        final String apiEndPoint = apiBaseUrl + "/templates/add.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_createTemplate_negative.json");

        // Asserting status, code, name, error
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listTemplates method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods= {
            "testCreateTemplateWithMandatoryParameters",
            "testCreateTemplateWithOptionalParameters"},
            description = "mailchimp {listTemplates} integration test with mandatory parameters.")
    public void testListTemplatesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listTemplates");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listTemplates_mandatory.json");

        final String apiEndPoint = apiBaseUrl + "/templates/list.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_listTemplates_mandatory.json");

        // Assert the Length of the Response Array
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("user").length(), apiRestResponse.getBody().getJSONArray("user").length());
        
        // Assert id, name, date_created
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("name"),
                apiRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("date_created"),
                apiRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("date_created"));
    }
    
    /**
     * Positive test case for listTemplates method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods= { "testCreateTemplateWithMandatoryParameters", 
            "testCreateTemplateWithOptionalParameters"},
            description = "mailchimp {listTemplates} integration test with optional parameters.")
    public void testListTemplatesWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listTemplates");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listTemplates_optional.json");

        final String apiEndPoint = apiBaseUrl + "/templates/list.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_listTemplates_optional.json");

        // Assert the Length of the Response Array
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("user").length(),
                apiRestResponse.getBody().getJSONArray("user").length());
        
        // Assert id, folder_id not null and folder_id
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("id"));
        Assert.assertNotNull(esbRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("folder_id"));
        Assert.assertNotNull(apiRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("folder_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("folder_id"),
                apiRestResponse.getBody().getJSONArray("user").getJSONObject(0).getString("folder_id"));
    }
    
    /**
     * Positive test case for createDraftCampaign method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods= {"testListSubscriberListsWithMandatoryParameters"},
            description = "mailchimp {createDraftCampaign} integration test with mandatory parameters.")
    public void testCreateDraftCampaignWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createDraftCampaign");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createDraftCampaign_mandatory.json");
        final String campaignIdMandatory = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("campaignIdMandatory", campaignIdMandatory);
        
        final String apiEndPoint = apiBaseUrl + "/campaigns/list.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_createDraftCampaign_mandatory.json");
        
        // Assert from_name, from_email, subject and to_name.
        Assert.assertEquals(esbRestResponse.getBody().getString("from_name"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("from_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("from_email"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("from_email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subject"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getString("to_name"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("to_name"));
        
        Assert.assertEquals(connectorProperties.getProperty("createCampaignFromName"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("from_name"));
        Assert.assertEquals(connectorProperties.getProperty("createCampaignEmail"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("from_email"));
        Assert.assertEquals(connectorProperties.getProperty("createCampaignSubjectMandatory"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("subject"));
        Assert.assertEquals(connectorProperties.getProperty("createCampaignToName"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("to_name"));
    }
    
    /**
     * Positive test case for createDraftCampaign method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods= { "testCreateTemplateWithMandatoryParameters",
            "testListSubscriberListsWithMandatoryParameters"},
            description = "mailchimp {createDraftCampaign} integration test with optional parameters.")
    public void testCreateDraftCampaignWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createDraftCampaign");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createDraftCampaign_optional.json");
        final String campaignIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("campaignIdOptional", campaignIdOptional);
        
        final String apiEndPoint = apiBaseUrl + "/campaigns/list.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_createDraftCampaign_optional.json");
        
        // Assert title, template_id
        Assert.assertEquals(esbRestResponse.getBody().getString("title"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("template_id"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("template_id"));
        
        Assert.assertEquals(connectorProperties.getProperty("createCampaignTitleOptional"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("templateIdMandatory"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("template_id"));
    }
    
    /**
     * Negative test case for createDraftCampaign method.
     */
    @Test(groups = { "wso2.esb" }, description = "mailchimp {createDraftCampaign} integration test with negative case.")
    public void testCreateDraftCampaignWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createDraftCampaign");        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createDraftCampaign_negative.json");

        final String apiEndPoint = apiBaseUrl + "/campaigns/create.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_createDraftCampaign_negative.json");

        // Asserting status, code, name, error
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listCampaigns method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods= { "testCreateDraftCampaignWithMandatoryParameters",
            "testCreateDraftCampaignWithOptionalParameters"},
            description = "mailchimp {listCampaigns} integration test with mandatory parameters.")
    public void testListCampaignsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCampaigns");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listCampaigns_mandatory.json");

        final String apiEndPoint = apiBaseUrl + "/campaigns/list.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_listCampaigns_mandatory.json");

        // Assert the Length of the Response Array
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").length(), apiRestResponse.getBody().getJSONArray("data").length());
        
        // Assert id, from_name, title
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("from_name"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("from_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("title"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("title"));
    }
    
    /**
     * Positive test case for listCampaigns method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods= { "testCreateDraftCampaignWithMandatoryParameters",
            "testCreateDraftCampaignWithOptionalParameters"},
            description = "mailchimp {listCampaigns} integration test with optional parameters.")
    public void testListCampaignsWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCampaigns");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listCampaigns_optional.json");

        final String apiEndPoint = apiBaseUrl + "/campaigns/list.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_listCampaigns_optional.json");

        // Assert the Length of the Response Array to 1
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").length(), 1);
        
        // Assert id, subject
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("subject"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("subject"));
    }
    
    /**
     * Negative test case for listCampaigns method.
     */
    @Test(groups = { "wso2.esb" }, description = "mailchimp {listCampaigns} integration test with negative case.")
    public void testListCampaignsWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCampaigns");        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listCampaigns_negative.json");

        final String apiEndPoint = apiBaseUrl + "/campaigns/list.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_listCampaigns_negative.json");

        // Asserting filter, value, code, error
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("filter"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("filter"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("value"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("value"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("code"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("error"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("error"));
    }
    
    /**
     * Positive test case for listSubscriberLists method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "mailchimp {listSubscriberLists} integration test with mandatory parameters.")
    public void testListSubscriberListsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listSubscriberLists");        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listSubscriberLists_mandatory.json");
        final String listId = esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id");
        connectorProperties.setProperty("listId", listId);
        
        final String apiEndPoint = apiBaseUrl + "/lists/list.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_listSubscriberLists_mandatory.json");

        // Assert id, name, web_id, date_created
        Assert.assertEquals(listId, apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("name"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("name"));             
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("web_id"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("web_id"));      
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("date_created"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("date_created"));
    }
    
    /**
     * Positive test case for listSubscriberLists method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "mailchimp {listSubscriberLists} integration test with optional parameters.")
    public void testListSubscriberListsWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listSubscriberLists");        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listSubscriberLists_optional.json");
        
        final String apiEndPoint = apiBaseUrl + "/lists/list.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_listSubscriberLists_optional.json");
        
        // Limit has been set to 1.
        //As a prerequisite it will be mentioned that user has to create atleast 2 lists before running the test suite.
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").length(), 1);
        
        // Assert name, web_id
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("name"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("name"));             
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("web_id"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("web_id"));      
    }
    
    /**
     * Negative test case for listSubscriberLists method.
     */
    @Test(groups = { "wso2.esb" }, description = "mailchimp {listSubscriberLists} integration test with negative case.")
    public void testListSubscriberListsWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listSubscriberLists");        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listSubscriberLists_negative.json");
        
        final String apiEndPoint = apiBaseUrl + "/lists/list.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_listSubscriberLists_negative.json");

        // Assert status, code and name.
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }
    
    /**
     * Positive test case for addSubscribersToList method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscriberListsWithMandatoryParameters" },
            description = "mailchimp {addSubscribersToList} integration test with mandatory parameters.")
    public void testAddSubscribersToListWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:addSubscribersToList");        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_addSubscribersToList_mandatory.json");

        final String apiEndPoint = apiBaseUrl + "/lists/member-info.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_addSubscribersToList_mandatory.json");

        // Assert email, euid, leid
        Assert.assertEquals(connectorProperties.getProperty("email"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("adds").getJSONObject(0).getString("euid"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("euid"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("adds").getJSONObject(0).getString("leid"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("leid"));
    }  
    
    /**
     * Positive test case for addSubscribersToList method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddSubscribersToListWithMandatoryParameters" },
            description = "mailchimp {addSubscribersToList} integration test with optional parameters.")
    public void testAddSubscribersToListWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:addSubscribersToList");        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_addSubscribersToList_optional.json");
        
        final String apiEndPoint = apiBaseUrl + "/lists/batch-subscribe.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_addSubscribersToList_optional.json");

        // By setting the 'updateExsiting' optional parameter to 'true', asserting the behavior of both esb call and direct call
        Assert.assertEquals(connectorProperties.getProperty("email"),
                esbRestResponse.getBody().getJSONArray("updates").getJSONObject(0).getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("email"),
                apiRestResponse.getBody().getJSONArray("updates").getJSONObject(0).getString("email"));
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("updates").getJSONObject(0).getString("euid"),
                apiRestResponse.getBody().getJSONArray("updates").getJSONObject(0).getString("euid"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("updates").getJSONObject(0).getString("leid"),
                apiRestResponse.getBody().getJSONArray("updates").getJSONObject(0).getString("leid"));
    }  
    
    /**
     * Negative test case for addSubscribersToList method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscriberListsWithMandatoryParameters" },
            description = "mailchimp {addSubscribersToList} integration test with negative case.")
    public void testAddSubscribersToListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:addSubscribersToList");        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_addSubscribersToList_negative.json");
        
        final String apiEndPoint = apiBaseUrl + "/lists/batch-subscribe.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_addSubscribersToList_negative.json");
        
        Assert.assertEquals(connectorProperties.getProperty("email"),
                esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getJSONObject("email").getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("email"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getJSONObject("email").getString("email"));
        
        // Assert code and error
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("code"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("error"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("error"));

    }
    
    /**
     * Positive test case for removeSubscribersFromList method with mandatory parameters.
     * @throws InterruptedException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendCampaignWithNegativeCase" },
            description = "mailchimp {removeSubscribersFromList} integration test with mandatory parameters.")
    public void testRemoveSubscribersFromListWithMandatoryParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:removeSubscribersFromList");        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeSubscribersFromList_mandatory.json");
        
        final String apiEndPoint = apiBaseUrl + "/lists/member-info.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_removeSubscribersFromList_mandatory.json");
        
        
        // Assert whether the removed email appears in the error section of the response.
        Assert.assertEquals(connectorProperties.getProperty("email"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getJSONObject("merges").getString("EMAIL"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("status"),
                "unsubscribed");
    } 
    
    /**
     * Positive test case for removeSubscribersFromList method with mandatory parameters.
     * @throws InterruptedException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testRemoveSubscribersFromListWithMandatoryParameters" },
            description = "mailchimp {removeSubscribersFromList} integration test with mandatory parameters.")
    public void testRemoveSubscribersFromListWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        // Subscribe an email
        String apiEndPoint = apiBaseUrl + "/lists/batch-subscribe.json";
        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_addSubscribersToList.json");
        
        esbRequestHeadersMap.put("Action", "urn:removeSubscribersFromList");        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeSubscribersFromList_optional.json");
        
        apiEndPoint = apiBaseUrl + "/lists/member-info.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_removeSubscribersFromList_optional.json");
        
        // Assert whether the removed email appears in the error section of the response.
        Assert.assertEquals(connectorProperties.getProperty("emailOptional"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getJSONObject("email").getString("email"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("error"),
                "The email address passed does not exist on this list");
    } 
    
    /**
     * Negative test case for removeSubscribersFromList method.
     */
    @Test(groups = { "wso2.esb" }, description = "mailchimp {removeSubscribersFromList} integration test with negative case.")
    public void testRemoveSubscribersFromListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:removeSubscribersFromList");        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_removeSubscribersFromList_negative.json");
        
        final String apiEndPoint = apiBaseUrl + "/lists/batch-unsubscribe.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_removeSubscribersFromList_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getJSONObject("email").getString("email"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getJSONObject("email").getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("error"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("error"));

    }
    
    /**
     * Positive test case for sendCampaign method with mandatory parameters.
     * @throws InterruptedException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDraftCampaignWithMandatoryParameters" },
            description = "mailchimp {sendCampaign} integration test with mandatory parameters.")
    public void testSendCampaignWithMandatoryParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:sendCampaign");
        final String apiEndPoint = apiBaseUrl + "/campaigns/list.json";
        RestResponse<JSONObject> apiRestResponseBeforeSending = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_sendCampaign_mandatory.json"); 
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_sendCampaign_mandatory.json");

        // Sleep the Thread for some seconds (specified in the property file) till the Campaign is sent.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("sleepTimeoutForSending")));
        
        RestResponse<JSONObject> apiRestResponseAfterSending = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_sendCampaign_mandatory.json");
        
        Assert.assertEquals(apiRestResponseBeforeSending.getBody().getJSONArray("data").getJSONObject(0).getString("status").toLowerCase(),
                connectorProperties.getProperty("saveStatus")); 
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("complete"), true);
        Assert.assertEquals(apiRestResponseAfterSending.getBody().getJSONArray("data").getJSONObject(0).getString("status").toLowerCase(),
                connectorProperties.getProperty("sentStatus"));
    }
    
    /**
     * Negative test case for sendCampaign method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendCampaignWithMandatoryParameters" },
            description = "mailchimp {sendCampaign} integration test with negative case.")
    public void testSendCampaignWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:sendCampaign");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_sendCampaign_negative.json");

        final String apiEndPoint = apiBaseUrl + "/campaigns/send.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_sendCampaign_negative.json");
        
        // Assert name and error
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().getString("error"));
    }
    
}
