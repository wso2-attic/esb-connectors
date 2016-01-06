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

package org.wso2.carbon.connector.integration.test.powerbi;

import java.io.IOException;
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

public class PowerbiConnectorIntegrationTest extends ConnectorIntegrationTestBase {

   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

   private String apiUrl;

   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {

      init("powerbi-connector-1.0.0");

      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");

      apiUrl = connectorProperties.getProperty("apiUrl") + "/v1.0/myorg";

      apiRequestHeadersMap.putAll(esbRequestHeadersMap);

   }

   /**
    * Positive test case for getAccessTokenFromAuthorizationCode method with
    * mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Power BI {getAccessTokenFromAuthorizationCode} integration test with mandatory parameters.")
   public void testGetAccessTokenFromAuthorizationCode() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getAccessTokenFromAuthorizationCode");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getAccessTokenFromAuthorizationCode_m.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("map").getString("token_type"));
      Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("map").getString("access_token"));

   }

   /**
    * Positive test case for getAccessTokenFromRefreshToken method with
    * mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAccessTokenFromAuthorizationCode" }, description = "Power BI {getAccessTokenFromRefreshToken} integration test with mandatory parameters.")
   public void testGetAccessTokenFromRefreshToken() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getAccessTokenFromRefreshToken");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getAccessTokenFromRefreshToken_m.json");

      final String accessToken = esbRestResponse.getBody().getJSONObject("map").getString("access_token");
      connectorProperties.put("accessToken", accessToken);
      apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("map").getString("token_type"));
      Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("map").getString("access_token"));

   }

   /**
    * Positive test case for listGroups method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "Power BI {listGroups} integration test with mandatory parameters.")
   public void testListGroupsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listGroups");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listGroups_mandatory.json");

      final String apiEndpoint = apiUrl + "/groups";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").length(), apiRestResponse.getBody()
            .getJSONArray("value").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("name"),
            apiRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("name"));
      connectorProperties.put("groupId", esbRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString(
            "id"));

   }

   /**
    * Method Name: listGroups Skipped Case: optional case Reason: No optional
    * parameter(s) to assert.
    */

   /**
    * Method Name: listGroups Skipped Case: negative case Reason: No
    * parameter(s) to cause the negative scenario.
    */

   /**
    * Positive test case for createDataset method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "Power BI {createDataset} integration test with mandatory parameters.")
   public void testCreateDatasetWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:createDataset");
      final String retentionPolicyDefaultValue = "None";
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_createDataset_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      String datasetId = esbRestResponse.getBody().getString("id");
      String defaultRetentionPolicy = esbRestResponse.getBody().getString("defaultRetentionPolicy");

      Assert.assertEquals(defaultRetentionPolicy, retentionPolicyDefaultValue);
      connectorProperties.put("datasetId", datasetId);
      final String apiEndpoint = apiUrl + "/datasets";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      JSONArray apiDatasets = apiRestResponse.getBody().getJSONArray("value");

      JSONObject dataset = null;
      for (int i = 0; i < apiDatasets.length(); i++) {
         if (apiDatasets.getJSONObject(i).getString("id").equals(datasetId)) {
            dataset = apiDatasets.getJSONObject(i);
            break;
         }
      }

      Assert.assertNotNull(dataset);
      Assert.assertEquals(connectorProperties.getProperty("datasetName"), dataset.getString("name"));

   }

   /**
    * Positive test case for createDataset method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "Power BI {createDataset} integration test with mandatory parameters.")
   public void testCreateDatasetWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:createDataset");
      final String retentionPolicyDefaultValue = "BasicFIFO";
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_createDataset_optional.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);

      String datasetId = esbRestResponse.getBody().getString("id");
      String defaultRetentionPolicy = esbRestResponse.getBody().getString("defaultRetentionPolicy");

      Assert.assertEquals(defaultRetentionPolicy, retentionPolicyDefaultValue);

      final String apiEndpoint = apiUrl + "/datasets";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      JSONArray apiDatasets = apiRestResponse.getBody().getJSONArray("value");
      JSONObject dataset = null;
      for (int i = 0; i < apiDatasets.length(); i++) {
         if (apiDatasets.getJSONObject(i).getString("id").equals(datasetId)) {
            dataset = apiDatasets.getJSONObject(i);
            break;
         }
      }

      Assert.assertNotNull(dataset);
      Assert.assertEquals(connectorProperties.getProperty("datasetName"), dataset.getString("name"));

   }

   /**
    * Positive test case for createDataset method with negative case.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "Power BI {createDataset} integration test with negative case.")
   public void testCreateDatasetWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:createDataset");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_createDataset_negative.json");

      final String apiEndpoint = apiUrl + "/datasets";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
            "api_createDataset_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("details").getJSONObject(0)
            .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("details")
            .getJSONObject(0).getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("details").getJSONObject(0)
            .getString("target"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("details")
            .getJSONObject(0).getString("target"));

   }

   /**
    * Positive test case for listDatasets method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDatasetWithMandatoryParameters" }, description = "Power BI {listDatasets} integration test with mandatory parameters.")
   public void testListDatasetsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listDatasets");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listDatasets_mandatory.json");

      final String apiEndpoint = apiUrl + "/datasets";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").length(), apiRestResponse.getBody()
            .getJSONArray("value").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("name"),
            apiRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("name"));

   }

   /**
    * Method Name: listDatasets Skipped Case: optional case Reason: No optional
    * parameter(s) to assert.
    */

   /**
    * Method Name: listDatasets Skipped Case: negative case Reason: No
    * parameter(s) to cause the negative scenario.
    */

   /**
    * Positive test case for listTables method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDatasetWithMandatoryParameters" }, description = "Power BI {listTables} integration test with mandatory parameters.")
   public void testListTablesWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listTables");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listTables_mandatory.json");

      final String apiEndpoint = apiUrl + "/datasets/" + connectorProperties.getProperty("datasetId") + "/tables";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").length(), apiRestResponse.getBody()
            .getJSONArray("value").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("name"),
            apiRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("name"));

   }

   /**
    * Method Name: listTables Skipped Case: optional case Reason: No optional
    * parameter(s) to assert.
    */

   /**
    * Negative test case for listTables method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDatasetWithMandatoryParameters" }, description = "Power BI {listTables} integration test with negative case.")
   public void testListTablesWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listTables");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listTables_negative.json");

      final String apiEndpoint = apiUrl + "/datasets/INVALID/tables";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));

   }

   /**
    * Positive test case for addRows method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDatasetWithMandatoryParameters" }, description = "Power BI {addRows} integration test with mandatory parameters.")
   public void testAddRowsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:addRows");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_addRows_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

   }

   /**
    * Method Name: addRows Skipped Case: optional case Reason: No optional
    * parameter(s) to assert.
    */

   /**
    * Negative test case for addRows method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDatasetWithMandatoryParameters" }, description = "Power BI {addRows} integration test with negative case.")
   public void testAddRowsWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:addRows");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_addRows_negative.json");

      final String apiEndpoint = apiUrl + "/datasets/INVALID/tables/Product/rows";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
            "api_addRows_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));

   }

   /**
    * Positive test case for deleteRows method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddRowsWithMandatoryParameters" }, description = "Power BI {deleteRows} integration test with mandatory parameters.")
   public void testDeleteRowsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:deleteRows");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_deleteRows_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

   }

   /**
    * Method Name: deleteRows Skipped Case: optional case Reason: No optional
    * parameter(s) to assert.
    */

   /**
    * Negative test case for deleteRows method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddRowsWithMandatoryParameters" }, description = "Power BI {deleteRows} integration test with negative case.")
   public void testDeleteRowsWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:deleteRows");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_deleteRows_negative.json");

      final String apiEndpoint = apiUrl + "/datasets/INVALID/tables/Product/rows";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "DELETE", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));

   }

   /**
    * Positive test case for updateTableSchema method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteRowsWithMandatoryParameters" }, description = "Power BI {updateTableSchema} integration test with mandatory parameters.")
   public void testUpdateTableSchemaWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:updateTableSchema");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_updateTableSchema_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

   }

   /**
    * Method Name: updateTableSchema Skipped Case: optional case Reason: No
    * optional parameter(s) to assert.
    */

   /**
    * Negative test case for updateTableSchema method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteRowsWithMandatoryParameters" }, description = "Power BI {updateTableSchema} integration test with negative case.")
   public void testUpdateTableSchemaWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:updateTableSchema");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_updateTableSchema_negative.json");

      final String apiEndpoint = apiUrl + "/datasets/INVALID/tables/Product";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap,
            "api_updateTableSchema_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));

   }

   /**
    * Positive test case for createGroupDataset method with mandatory
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListGroupsWithMandatoryParameters" }, description = "Power BI {createGroupDataset} integration test with mandatory parameters.")
   public void testCreateGroupDatasetWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:createGroupDataset");
      final String retentionPolicyDefaultValue = "None";
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_createGroupDataset_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      String datasetId = esbRestResponse.getBody().getString("id");
      String defaultRetentionPolicy = esbRestResponse.getBody().getString("defaultRetentionPolicy");

      Assert.assertEquals(defaultRetentionPolicy, retentionPolicyDefaultValue);
      connectorProperties.put("groupDatasetId", datasetId);
      final String apiEndpoint = apiUrl + "/groups/" + connectorProperties.getProperty("groupId") + "/datasets";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      JSONArray apiDatasets = apiRestResponse.getBody().getJSONArray("value");

      JSONObject dataset = null;
      for (int i = 0; i < apiDatasets.length(); i++) {
         if (apiDatasets.getJSONObject(i).getString("id").equals(datasetId)) {
            dataset = apiDatasets.getJSONObject(i);
            break;
         }
      }

      Assert.assertNotNull(dataset);
      Assert.assertEquals(connectorProperties.getProperty("datasetName"), dataset.getString("name"));

   }

   /**
    * Positive test case for createGroupDataset method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListGroupsWithMandatoryParameters" }, description = "Power BI {createGroupDataset} integration test with optional parameters.")
   public void testCreateGroupDatasetWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:createGroupDataset");
      final String retentionPolicyDefaultValue = "BasicFIFO";
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_createGroupDataset_optional.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);

      String datasetId = esbRestResponse.getBody().getString("id");
      String defaultRetentionPolicy = esbRestResponse.getBody().getString("defaultRetentionPolicy");

      Assert.assertEquals(defaultRetentionPolicy, retentionPolicyDefaultValue);

      final String apiEndpoint = apiUrl + "/groups/" + connectorProperties.getProperty("groupId") + "/datasets";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      JSONArray apiDatasets = apiRestResponse.getBody().getJSONArray("value");
      JSONObject dataset = null;
      for (int i = 0; i < apiDatasets.length(); i++) {
         if (apiDatasets.getJSONObject(i).getString("id").equals(datasetId)) {
            dataset = apiDatasets.getJSONObject(i);
            break;
         }
      }

      Assert.assertNotNull(dataset);
      Assert.assertEquals(connectorProperties.getProperty("datasetName"), dataset.getString("name"));

   }

   /**
    * Negative test case for createGroupDataset method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListGroupsWithMandatoryParameters" }, description = "Power BI {createGroupDataset} integration test with negative case.")
   public void testCreateGroupDatasetWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:createGroupDataset");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_createGroupDataset_negative.json");

      final String apiEndpoint = apiUrl + "/groups/" + connectorProperties.getProperty("groupId") + "/datasets";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
            "api_createGroupDataset_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("details").getJSONObject(0)
            .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("details")
            .getJSONObject(0).getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("details").getJSONObject(0)
            .getString("target"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("details")
            .getJSONObject(0).getString("target"));

   }

   /**
    * Positive test case for listGroupDatasets method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateGroupDatasetWithMandatoryParameters" }, description = "Power BI {listGroupDatasets} integration test with mandatory parameters.")
   public void testListGroupDatasetsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listGroupDatasets");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listGroupDatasets_mandatory.json");

      final String apiEndpoint = apiUrl + "/groups/" + connectorProperties.getProperty("groupId") + "/datasets";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").length(), apiRestResponse.getBody()
            .getJSONArray("value").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("name"),
            apiRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("name"));

   }

   /**
    * Method Name: listGroupDatasets Skipped Case: optional case Reason: No
    * optional parameter(s) to assert.
    */

   /**
    * Method Name: listGroupDatasets Skipped Case: negative case Reason: No
    * parameter(s) to cause the negative scenario.
    */

   /**
    * Positive test case for listGroupTables method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateGroupDatasetWithMandatoryParameters" }, description = "Power BI {listGroupTables} integration test with mandatory parameters.")
   public void testListGroupTablesWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listGroupTables");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listGroupTables_mandatory.json");

      final String apiEndpoint = apiUrl + "/groups/" + connectorProperties.getProperty("groupId") + "/datasets/"
            + connectorProperties.getProperty("groupDatasetId") + "/tables";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").length(), apiRestResponse.getBody()
            .getJSONArray("value").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("name"),
            apiRestResponse.getBody().getJSONArray("value").getJSONObject(0).getString("name"));

   }

   /**
    * Method Name: listGroupTables Skipped Case: optional case Reason: No
    * optional parameter(s) to assert.
    */

   /**
    * Negative test case for listGroupTables method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateGroupDatasetWithMandatoryParameters" }, description = "Power BI {listGroupTables} integration test with negative case.")
   public void testListGroupTablesWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listGroupTables");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listGroupTables_negative.json");

      final String apiEndpoint = apiUrl + "/groups/" + connectorProperties.getProperty("groupId")
            + "/datasets/INVALID/tables";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));

   }

   /**
    * Positive test case for addGroupRows method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateGroupDatasetWithMandatoryParameters" }, description = "Power BI {addRows} integration test with mandatory parameters.")
   public void testAddGroupRowsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:addGroupRows");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_addGroupRows_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

   }

   /**
    * Method Name: addGroupRows Skipped Case: optional case Reason: No optional
    * parameter(s) to assert.
    */

   /**
    * Negative test case for addGroupRows method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateGroupDatasetWithMandatoryParameters" }, description = "Power BI {addGroupRows} integration test with negative case.")
   public void testAddGroupRowsWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:addGroupRows");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_addGroupRows_negative.json");

      final String apiEndpoint = apiUrl + "/groups/" + connectorProperties.getProperty("groupId")
            + "/datasets/INVALID/tables/Product/rows";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
            "api_addGroupRows_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));

   }

   /**
    * Positive test case for deleteGroupRows method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddGroupRowsWithMandatoryParameters" }, description = "Power BI {deleteGroupRows} integration test with mandatory parameters.")
   public void testDeleteGroupRowsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:deleteGroupRows");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_deleteGroupRows_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

   }

   /**
    * Method Name: deleteGroupRows Skipped Case: optional case Reason: No
    * optional parameter(s) to assert.
    */

   /**
    * Negative test case for deleteGroupRows method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddGroupRowsWithMandatoryParameters" }, description = "Power BI {deleteGroupRows} integration test with negative case.")
   public void testDeleteGroupRowsWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:deleteGroupRows");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_deleteGroupRows_negative.json");

      final String apiEndpoint = apiUrl + "/groups/" + connectorProperties.getProperty("groupId")
            + "/datasets/INVALID/tables/Product/rows";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "DELETE", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));

   }

   /**
    * Positive test case for updateGroupTableSchema method with mandatory
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteGroupRowsWithMandatoryParameters" }, description = "Power BI {updateGroupTableSchema} integration test with mandatory parameters.")
   public void testUpdateGroupTableSchemaWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:updateGroupTableSchema");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_updateGroupTableSchema_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

   }

   /**
    * Method Name: updateGroupTableSchema Skipped Case: optional case Reason: No
    * optional parameter(s) to assert.
    */

   /**
    * Negative test case for updateGroupTableSchema method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteGroupRowsWithMandatoryParameters" }, description = "Power BI {updateGroupTableSchema} integration test with negative case.")
   public void testUpdateGroupTableSchemaWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:updateGroupTableSchema");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_updateGroupTableSchema_negative.json");

      final String apiEndpoint = apiUrl + "/groups/" + connectorProperties.getProperty("groupId")
            + "/datasets/INVALID/tables/Product";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap,
            "api_updateGroupTableSchema_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));

   }

}
