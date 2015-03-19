package org.wso2.carbon.connector.integration.test.marketo;
/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MarketoConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> mpRequestHeadersMap = new HashMap<String, String>();

    private String multipartProxyUrl;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("marketo-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);

        final String authString = connectorProperties.getProperty("accessToken");

        final String authorizationHeader = "Bearer " + authString;

        apiRequestHeadersMap.put("Authorization", authorizationHeader);

        String multipartPoxyName = connectorProperties.getProperty("multipartProxyName");
        //mpRequestHeadersMap.put("Content-Type",)

        multipartProxyUrl = getProxyServiceURL(multipartPoxyName);
    }

    /**
     * Positive test case for createAndUpdateLeads method with mandatory parameters.
     */
    @Test(priority = 1, description = "Marketo {createAndUpdateLeads} integration test with mandatory parameters.")
    public void testCreateAndUpdateLeadsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createAndUpdateLeads";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createAndUpdateLeads_mandatory.json");
        String leadId = esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("id");
        connectorProperties.put("leadId", leadId);

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/lead/" + connectorProperties.getProperty("leadId") + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("id"), connectorProperties.getProperty("leadId"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("firstName"), connectorProperties.getProperty("leadFirstName"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("lastName"), connectorProperties.getProperty("leadLastName"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("email"), connectorProperties.getProperty("leadEmail"));
    }

    /**
     * Positive test case for createAndUpdateLeads method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithMandatoryParameters"}, description = "Marketo {createAndUpdateLeads} integration test with optional parameters.")
    public void testCreateAndUpdateLeadsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "createAndUpdateLeads";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createAndUpdateLeads_optional.json");
        String OptionalLeadId = esbRestResponse.getBody().getJSONArray("result").getJSONObject(1).getString("id");
        String OptionalLeadId1 = esbRestResponse.getBody().getJSONArray("result").getJSONObject(2).getString("id");
        String OptionalLeadId2 = esbRestResponse.getBody().getJSONArray("result").getJSONObject(3).getString("id");

        connectorProperties.put("lLeadId", OptionalLeadId);
        connectorProperties.put("dLeadId", OptionalLeadId1);
        connectorProperties.put("rLeadId", OptionalLeadId2);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("status"), "updated");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(1).getString("status"), "created");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(2).getString("status"), "created");
    }

    /**
     * Negative test case for createAndUpdateLeads method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithMandatoryParameters"}, description = "Marketo {createAndUpdateLeads} integration test with negative case.")
    public void testCreateAndUpdateLeadsWithNegativeCase() throws IOException, JSONException {

        String methodName = "createAndUpdateLeads";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createAndUpdateLeads_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getJSONArray("reasons").getJSONObject(0).getString("code"), "1005");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getJSONArray("reasons").getJSONObject(0).getString("message"), "Lead already exists");
    }

    /**
     * Positive test case for getLeadById method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getLeadById} integration test with mandatory parameters.")
    public void testGetLeadByIdWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "getLeadById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLeadById_mandatory.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/lead/" + connectorProperties.getProperty("leadId") + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Positive test case for getLeadById method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getLeadById} integration test with optional parameters.")
    public void testGetLeadByIdWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getLeadById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLeadById_optional.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/lead/" + connectorProperties.getProperty("leadId") + ".json?fields=" + connectorProperties.getProperty("fields");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Negative test case for getLeadById method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getLeadById} integration test with negative case.")
    public void testGetLeadByIdWithNegativeCase() throws IOException, JSONException {

        String methodName = "getLeadById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLeadById_Negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/lead/" + connectorProperties.getProperty("invalidLeadId") + ".json?fields=" + connectorProperties.getProperty("fields");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").toString(), apiRestResponse.getBody().getJSONArray("result").toString());
    }

    /**
     * Positive test case for getMultipleLeadsByFilterType method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getMultipleLeadsByFilterType} integration test with mandatory parameters.")
    public void testGetMultipleLeadsByFilterTypeWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "getMultipleLeadsByFilterType";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLeadsByFilterType_mandatory.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/leads.json?filterType=" + connectorProperties.getProperty("filterType") + "&filterValues=" + connectorProperties.getProperty("leadId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Positive test case for getMultipleLeadsByFilterType method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getMultipleLeadsByFilterType} integration test with optional parameters.")
    public void testGetMultipleLeadsByFilterTypeWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getMultipleLeadsByFilterType";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLeadsByFilterType_optional.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/leads.json?filterType=" + connectorProperties.getProperty("filterType") +
                "&filterValues=" + connectorProperties.getProperty("leadId") + "&fields=" + connectorProperties.getProperty("fields") +
                "&batchSize=" + connectorProperties.getProperty("batchSize");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Negative test case for getMultipleLeadsByFilterType method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getMultipleLeadsByFilterType} integration test with negative case.")
    public void testGetMultipleLeadsByFilterTypeWithNegativeCase() throws IOException, JSONException {

        String methodName = "getMultipleLeadsByFilterType";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLeadsByFilterType_Negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/leads.json?filterType=" + connectorProperties.getProperty("invalidFilterType") + "&filterValues=" + connectorProperties.getProperty("leadId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").toString(), apiRestResponse.getBody().getJSONArray("errors").toString());
    }

    /**
     * Positive test case for getMultipleLeadsByListId method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getMultipleLeadsByListId} integration test with mandatory parameters.")
    public void testGetMultipleLeadsByListIdWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "getMultipleLeadsByListId";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLeadsByListId_mandatory.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/list/" + connectorProperties.getProperty("listId") + "/leads.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Positive test case for getMultipleLeadsByListId method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getMultipleLeadsByListId} integration test with optional parameters.")
    public void testGetMultipleLeadsByListIdWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getMultipleLeadsByListId";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLeadsByListId_optional.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/list/" + connectorProperties.getProperty("listId") + "/leads.json?fields=" + connectorProperties.getProperty("fields") +
                "&batchSize=" + connectorProperties.getProperty("batchSize");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Negative test case for getMultipleLeadsByListId method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getMultipleLeadsByListId} integration test with negative case.")
    public void testGetMultipleLeadsByListIdWithNegativeCase() throws IOException, JSONException {

        String methodName = "getMultipleLeadsByListId";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLeadsByListId_Negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/list/" + connectorProperties.getProperty("invalidListId") + "/leads.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").toString(), apiRestResponse.getBody().getJSONArray("errors").toString());
    }

    /**
     * Positive test case for getMultipleLeadsByProgramId method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getLeadByProgramId} integration test with mandatory parameters.")
    public void testGetMultipleLeadsByProgramIdWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "getMultipleLeadsByProgramId";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLeadsByProgramId_mandatory.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/leads/programs/" + connectorProperties.getProperty("programId") + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").toString(), apiRestResponse.getBody().getJSONArray("result").toString());
    }

    /**
     * Positive test case for getMultipleLeadsByProgramId method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {getMultipleLeadsByProgramId} integration test with optional parameters.")
    public void testGetMultipleLeadsByProgramIdWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getMultipleLeadsByProgramId";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLeadsByProgramId_optional.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/leads/programs/" + connectorProperties.getProperty("programId") + ".json?fields=" + connectorProperties.getProperty("fields") + "&batchSize=" + connectorProperties.getProperty("batchSize");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").toString(), apiRestResponse.getBody().getJSONArray("result").toString());
    }

    /**
     * Negative test case for getMultipleLeadsByProgramId method.
     */
    @Test(priority = 1, description = "Marketo {getMultipleLeadsByProgramId} integration test with negative case.")
    public void testGetMultipleLeadsByProgramIdWithNegativeCase() throws IOException, JSONException {

        String methodName = "getMultipleLeadsByProgramId";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLeadsByProgramId_negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/leads/programs/" + connectorProperties.getProperty("invalidProgramId") + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").toString(), apiRestResponse.getBody().getJSONArray("errors").toString());
    }

    /**
     * Positive test case for addLeadsToList method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {addLeadsToList} integration test with positive case.")
    public void testAddLeadsToListWithPositiveCase() throws IOException, JSONException {

        String methodName = "addLeadsToList";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_addLeadsToList_positive.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("status"), "added");

    }

    /**
     * Negative test case for addLeadsToList method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {addLeadsToList} integration test with negative case.")
    public void testAddLeadsToListWithNegativeCase() throws IOException, JSONException {

        String methodName = "addLeadsToList";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_addLeadsToList_Negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getJSONArray("reasons").getJSONObject(0).getString("code"), "1004");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getJSONArray("reasons").getJSONObject(0).getString("message"), "Lead not found");

    }

    /**
     * Positive test case for memberOfList method.
     */
    @Test(priority = 1, dependsOnMethods = {"testAddLeadsToListWithPositiveCase"}, description = "Marketo {memberOfList} integration test with positive case.")
    public void testMemberOfListWithPositiveCase() throws IOException, JSONException {

        String methodName = "memberOfList";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_addLeadsToList_positive.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("status"), "memberof");

    }

    /**
     * Negative test case for memberOfList method.
     */
    @Test(priority = 1, dependsOnMethods = {"testAddLeadsToListWithPositiveCase"}, description = "Marketo {addLeadsToList} integration test with negative case.")
    public void testMemberOfListWithNegativeCase() throws IOException, JSONException {

        String methodName = "addLeadsToList";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_addLeadsToList_Negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getJSONArray("reasons").getJSONObject(0).getString("code"), "1004");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getJSONArray("reasons").getJSONObject(0).getString("message"), "Lead not found");

    }

    /**
     * Positive test case for removeLeadsFromList method.
     */
    @Test(priority = 1, dependsOnMethods = {"testMemberOfListWithPositiveCase"}, description = "Marketo {removeLeadsFromList} integration test with positive case.")
    public void testRemoveLeadsFromListWithPositiveCase() throws IOException, JSONException {

        String methodName = "removeLeadsFromList";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_addLeadsToList_positive.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("status"), "removed");

    }

    /**
     * Negative test case for removeLeadsFromList method.
     */
    @Test(priority = 1, dependsOnMethods = {"testMemberOfListWithPositiveCase"}, description = "Marketo {removeLeadsFromList} integration test with negative case.")
    public void testRemoveLeadsFromListWithNegativeCase() throws IOException, JSONException {

        String methodName = "removeLeadsFromList";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_addLeadsToList_Negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getJSONArray("reasons").getJSONObject(0).getString("code"), "1004");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getJSONArray("reasons").getJSONObject(0).getString("message"), "Lead not found");

    }

    /**
     * Positive test case for associateLead method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {associateLead} integration test with positive case.")
    public void testAssociateLeadWithPositiveCase() throws IOException, JSONException {

        String methodName = "associateLead";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_associateLead_positive.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
    }

    /**
     * Negative test case for associateLead method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {associateLead} integration test with negative case.")
    public void testAssociateLeadWithNegativeCase() throws IOException, JSONException {

        String methodName = "associateLead";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_associateLead_Negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("code"), "1004");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"), "Lead '" + connectorProperties.getProperty("invalidLeadId") + "' not found");

    }

    /**
     * Positive test case for mergeLead method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {mergeLead} integration test with mandatory parameters.")
    public void testMergeLeadWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "mergeLead";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_mergeLead_mandatory.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
    }

    /**
     * Positive test case for mergeLead method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateAndUpdateLeadsWithOptionalParameters"}, description = "Marketo {mergeLead} integration test with optional parameters.")
    public void testMergeLeadWithOptionalParameters() throws IOException, JSONException {

        String methodName = "mergeLead";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_mergeLead_optional.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
    }

    /**
     * Negative test case for mergeLead method.
     */
    @Test(priority = 1, dependsOnMethods = {"testMergeLeadWithOptionalParameters"}, description = "Marketo {mergeLead} integration test with negative case.")
    public void testMergeLeadWithNegativeCase() throws IOException, JSONException {

        String methodName = "mergeLead";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_mergeLead_mandatory.json");

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("code"), "1004");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"), "Lead '" + connectorProperties.getProperty("leadId") + "' not found");
    }

    /**
     * Positive test case for deleteLead method.
     */
    @Test(priority = 1, dependsOnMethods = {"testMergeLeadWithOptionalParameters"}, description = "Marketo {deleteLead} integration test with positive case.")
    public void testDeleteLeadWithPositiveCase() throws IOException, JSONException {

        String methodName = "deleteLead";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteLead_positive.json");

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("status"), "deleted");
    }

    /**
     * Negative test case for deleteLead method.
     */
    @Test(priority = 1, dependsOnMethods = {"testDeleteLeadWithPositiveCase"}, description = "Marketo {deleteLead} integration test with negative case.")
    public void testDeleteLeadWithNegativeCase() throws IOException, JSONException {

        String methodName = "deleteLead";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteLead_positive.json");

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getJSONArray("reasons").getJSONObject(0).getString("code"), "1004");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getJSONArray("reasons").getJSONObject(0).getString("message"), "Lead not found");
    }

    /**
     * Positive test case for getLeadPartitions method.
     */
    @Test(priority = 1, description = "Marketo {getLeadPartitions} integration test with positive case.")
    public void testGetLeadPartitionsWithPositiveCase() throws IOException, JSONException {

        String methodName = "getLeadPartitions";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_init.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/leads/partitions.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Positive test case for describe method.
     */
    @Test(priority = 1, description = "Marketo {describe} integration test with positive case.")
    public void testGetLeadPartitionsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "describe";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_init.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/leads/describe.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Positive test case for getListById method.
     */
    @Test(priority = 1, description = "Marketo {getListById} integration test with positive case.")
    public void testGetListByIdWithPositiveCase() throws IOException, JSONException {

        String methodName = "getListById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getListById_positive.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/lists/" + connectorProperties.getProperty("listId") + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Negative test case for getListById method.
     */
    @Test(priority = 1, description = "Marketo {getListById} integration test with negative case.")
    public void testGetListByIdWithNegativeCase() throws IOException, JSONException {

        String methodName = "getListById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getListById_negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/lists/" + connectorProperties.getProperty("invalidListId") + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).toString());
    }

    /**
     * Positive test case for getMultipleLists method with mandatory parameters.
     */
    @Test(priority = 1, description = "Marketo {getMultipleLists} integration test with mandatory parameters.")
    public void testGetMultipleListsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "getMultipleLists";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_init.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/lists.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").toString(), apiRestResponse.getBody().getJSONArray("result").toString());
//        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("success").toString(), "true}");

    }

    /**
     * Positive test case for getMultipleLists method with optional parameters.
     */
    @Test(priority = 1, description = "Marketo {getMultipleLists} integration test with positive case.")
    public void testGetMultipleListsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getMultipleLists";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLists_optional.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/lists.json?name=" + connectorProperties.getProperty("listName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Negative test case for getMultipleLists method.
     */
    @Test(priority = 1, description = "Marketo {getMultipleLists} integration test with negative case.")
    public void testGetMultipleListsWithNegativeCase() throws IOException, JSONException {

        String methodName = "getMultipleLists";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleLists_negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/lists.json?name=" + connectorProperties.getProperty("listId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").toString(), apiRestResponse.getBody().getJSONArray("result").toString());
    }

    /**
     * Positive test case for getCampaignById method.
     */
    @Test(priority = 1, description = "Marketo {getCampaignById} integration test with positive case.")
    public void testGetCampaignByIdWithPositiveCase() throws IOException, JSONException {

        String methodName = "getCampaignById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getCampaignById_positive.json");
        String campaignName = esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("name");
        connectorProperties.put("campaignName", campaignName);

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/campaigns.json?id=" + connectorProperties.getProperty("campaignId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Negative test case for getCampaignById method.
     */
    @Test(priority = 1, description = "Marketo {getListById} integration test with negative case.")
    public void testGetCampaignByIdWithNegativeCase() throws IOException, JSONException {

        String methodName = "getCampaignById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getCampaignById_negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/campaigns.json?id=" + connectorProperties.getProperty("invalidCampaignId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").toString(), apiRestResponse.getBody().getJSONArray("result").toString());
    }

    /**
     * Positive test case for getMultipleCampaigns method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetCampaignByIdWithPositiveCase"}, description = "Marketo {getMultipleCampaigns} integration test with with mandatory parameters.")
    public void testGetMultipleCampaignsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "getMultipleCampaigns";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_init.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/campaigns.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Positive test case for getMultipleCampaigns method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetCampaignByIdWithPositiveCase"}, description = "Marketo {getMultipleCampaigns} integration test with optional parameters")
    public void testGetMultipleCampaignsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getMultipleCampaigns";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleCampaigns_positive.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/campaigns.json?name=" + connectorProperties.getProperty("campaignName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("result").getJSONObject(0).toString());
    }

    /**
     * Negative test case for getMultipleCampaigns method.
     */
    @Test(priority = 1, description = "Marketo {getMultipleCampaigns} integration test with negative case.")
    public void testGetMultipleCampaignsWithNegativeCase() throws IOException, JSONException {

        String methodName = "getMultipleCampaigns";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMultipleCampaigns_negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/campaigns.json?name=" + connectorProperties.getProperty("invalidCampaignName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").toString(), apiRestResponse.getBody().getJSONArray("result").toString());
    }

    /**
     * Positive test case for scheduleCampaign method with mandatory parameters.
     */
    @Test(priority = 1, description = "Marketo {scheduleCampaign} integration test with mandatory parameters.")
    public void testScheduleCampaignMandatoryParameters() throws IOException, JSONException {

        String methodName = "scheduleCampaign";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_scheduleCampaign_mandatory.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");

    }

    /**
     * Positive test case for scheduleCampaign method with optional parameters.
     */
    @Test(priority = 1, description = "Marketo {scheduleCampaign} integration test with positive case.")
    public void testScheduleCampaignWithOptionalParameters() throws IOException, JSONException {

        String methodName = "scheduleCampaign";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_scheduleCampaign_optional.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");
    }

    /**
     * Negative test case for scheduleCampaign method.
     */
    @Test(priority = 1, description = "Marketo {scheduleCampaign} integration test with negative case.")
    public void testScheduleCampaignWithNegativeCase() throws IOException, JSONException {

        String methodName = "scheduleCampaign";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_scheduleCampaign_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("code"), "1013");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"), "Campaign not found");
    }

    /**
     * Positive test case for requestCampaign method with mandatory parameters.
     */
    @Test(priority = 1, description = "Marketo {requestCampaign} integration test with mandatory parameters.")
    public void testRequestCampaignWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "requestCampaign";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_requestCampaign_positive.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");

    }

    /**
     * Negative test case for requestCampaign method.
     */
    @Test(priority = 1, description = "Marketo {requestCampaign} integration test with negative case.")
    public void testRequestCampaignWithNegativeCase() throws IOException, JSONException {

        String methodName = "requestCampaign";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_scheduleCampaign_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("code"), "1013");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"), "Campaign not found");
    }

    /**
     * Positive test case for getPagingToken method.
     */
    @Test(priority = 1, description = "Marketo {getPagingToken} integration test with positive case.")
    public void testGetPagingTokenWithPositiveCase() throws IOException, JSONException {

        String methodName = "getPagingToken";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getPagingToken_positive.json");
        String nextPageToken = esbRestResponse.getBody().getString("nextPageToken");

        connectorProperties.put("nextPageToken", nextPageToken);
        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");
    }

    /**
     * Positive test case for getActivityTypes method.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetPagingTokenWithPositiveCase"}, description = "Marketo {getActivityTypes} integration test with positive case.")
    public void testGetActivityTypesWithPositiveCase() throws IOException, JSONException {

        String methodName = "getActivityTypes";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_init.json");
        String activityTypeId = esbRestResponse.getBody().getJSONArray("result").getJSONObject(0).getString("id");

        connectorProperties.put("activityTypeId", activityTypeId);
        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");
    }

    /**
     * Positive test case for getLeadActivities method.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetActivityTypesWithPositiveCase"}, description = "Marketo {getLeadActivities} integration test with positive case.")
    public void testGetLeadActivitiesWithPositiveCase() throws IOException, JSONException {

        String methodName = "getLeadActivities";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLeadActivities_mandatory.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");

    }

    /**
     * Negative test case for getLeadActivities method.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetActivityTypesWithPositiveCase"}, description = "Marketo {getLeadActivities} integration test with negative case.")
    public void testGetLeadActivitiesWithNegativeCase() throws IOException, JSONException {

        String methodName = "getLeadActivities";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLeadActivities_negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/activities.json?nextPageToken=" + connectorProperties.getProperty("nextPageToken") + "&activityTypeIds=" + connectorProperties.getProperty("invalidLeadId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").toString(), apiRestResponse.getBody().getJSONArray("errors").toString());
    }

    /**
     * Positive test case for getLeadChanges method.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetActivityTypesWithPositiveCase"}, description = "Marketo {getLeadChanges} integration test with positive case.")
    public void testGetLeadChangesWithPositiveCase() throws IOException, JSONException {

        String methodName = "getLeadChanges";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLeadChanges_mandatory.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");

    }

    /**
     * Negative test case for getLeadChanges method.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetActivityTypesWithPositiveCase"}, description = "Marketo {getLeadChanges} integration test with negative case.")
    public void testGetLeadChangesWithNegativeCase() throws IOException, JSONException {

        String methodName = "getLeadChanges";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLeadChanges_negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/activities/leadchanges.json?nextPageToken=" + connectorProperties.getProperty("nextPageToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"), apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"));
    }
    /**
     * Positive test case for getDeletedLeads method.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetActivityTypesWithPositiveCase"}, description = "Marketo {getDeletedLeads} integration test with positive case.")
    public void testGetDeletedLeadsWithPositiveCase() throws IOException, JSONException {

        String methodName = "getDeletedLeads";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getDeletedLeads_mandatory.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");

    }

    /**
     * Negative test case for getDeletedLeads method.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetActivityTypesWithPositiveCase"}, description = "Marketo {getLeadChanges} integration test with negative case.")
    public void testGetDeletedLeadsWithNegativeCase() throws IOException, JSONException {

        String methodName = "getDeletedLeads";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getDeletedLeads_negative.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/activities/deletedleads.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"), apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"));
    }
    /**
     * Positive test case for getDailyErrors method.
     */
    @Test(priority = 1, description = "Marketo {getDailyErrors} integration test with positive case.")
    public void testGetDailyErrorsWithPositiveCase() throws IOException, JSONException {

        String methodName = "getDailyErrors";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_init.json");
        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/stats/errors.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").toString(), apiRestResponse.getBody().getJSONArray("result").toString());
        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");
    }
    /**
     * Positive test case for getDailyUsage method.
     */
    @Test(priority = 1, description = "Marketo {getDailyUsage} integration test with positive case.")
    public void testGetDailyUsageWithPositiveCase() throws IOException, JSONException {

        String methodName = "getDailyUsage";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_init.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/stats/usage.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");

    }
    /**
     * Positive test case for getLast7DaysErrors method.
     */
    @Test(priority = 1, description = "Marketo {getLast7DaysErrors} integration test with positive case.")
    public void testGetLast7DaysErrorsWithPositiveCase() throws IOException, JSONException {

        String methodName = "getLast7DaysErrors";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_init.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/stats/errors/last7days.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("result").toString(), apiRestResponse.getBody().getJSONArray("result").toString());
        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");

    }

    /**
     * Positive test case for getLast7DaysUsage method.
     */
    @Test(priority = 1, description = "Marketo {getLast7DaysUsage} integration test with positive case.")
    public void testGetLast7DaysUsageWithPositiveCase() throws IOException, JSONException {

        String methodName = "getLast7DaysUsage";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_init.json");

        final String apiUrl = connectorProperties.getProperty("marketoInstanceURL") + "/rest/v1/stats/usage/last7days.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getString("success").toString(), "true");

    }
}
