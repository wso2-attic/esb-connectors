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
package org.wso2.carbon.connector.integration.test.bigquery;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class BigQueryConnectorIntegrationTest extends ConnectorIntegrationTestBase {

   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

   private String apiUrl;

   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {

      init("bigquery-connector-1.0.0");

      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");

      apiUrl = connectorProperties.getProperty("apiUrl") + "/bigquery/v2";

      apiRequestHeadersMap.putAll(esbRequestHeadersMap);

   }

   /**
    * Positive test case for getAccessTokenFromRefreshToken method with
    * mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb.bigquery.oauth" }, description = "BigQuery {getAccessTokenFromRefreshToken} integration test with mandatory parameters.")
   public void testGetAccessTokenFromRefreshToken() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getAccessTokenFromRefreshToken");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getAccessTokenFromRefreshToken.json");

      final String accessToken = esbRestResponse.getBody().getString("access_token");
      connectorProperties.put("accessToken", accessToken);
      apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertNotNull(esbRestResponse.getBody().getString("token_type"));
      Assert.assertNotNull(esbRestResponse.getBody().getString("access_token"));

   }

   /**
    * Positive test case for listProjects method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {listProjects} integration test with mandatory parameters.")
   public void testListProjectsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listProjects");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listProjects_mandatory.json");

      String apiEndpoint = apiUrl + "/projects";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("projects").getJSONObject(0).getString("friendlyName"),
            apiRestResponse.getBody().getJSONArray("projects").getJSONObject(0).getString("friendlyName"));
   }

   /**
    * Positive test case for listProjects method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {listProjects} integration test with optional parameters.")
   public void testListProjectsWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listProjects");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listProjects_optional.json");

      String apiEndpoint = apiUrl
            + "/projects?maxResults=1&pageToken=0&fields=projects(friendlyName,id)&prettyPrint=true&quotaUser=UserProjects";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("projects").getJSONObject(0).getString("friendlyName"),
            apiRestResponse.getBody().getJSONArray("projects").getJSONObject(0).getString("friendlyName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("projects").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("projects").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("projects").length(), 1);
   }

   /**
    * Negative test case for listProjects method.
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {listProjects} integration test with negative case.")
   public void testListProjectsWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listProjects");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listProjects_negative.json");

      String apiEndPoint = apiUrl + "/projects?maxResults=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
   }

   /**
    * Positive test case for listDatasets method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {listDatasets} integration test with mandatory parameters.")
   public void testListDatasetsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listDatasets");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listDatasets_mandatory.json");

      String apiEndpoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/datasets";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("datasets").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("datasets").getJSONObject(0).getString("id"));
   }

   /**
    * Positive test case for listDatasets method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {listDatasets} integration test with optional parameters.")
   public void testListDatasetsWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listDatasets");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listDatasets_optional.json");

      String apiEndpoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId")
            + "/datasets?maxResults=1&all=true&fields=datasets(kind,id)&prettyPrint=true&quotaUser=UserProjects";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("datasets").getJSONObject(0).getString("kind"),
            apiRestResponse.getBody().getJSONArray("datasets").getJSONObject(0).getString("kind"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("datasets").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("datasets").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("datasets").length(), 1);
   }

   /**
    * Negative test case for listDatasets method.
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {listDatasets} integration test with negative case.")
   public void testListDatasetsWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listDatasets");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listDatasets_negative.json");

      String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId")
            + "/datasets?maxResults=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
   }

   /**
    * Positive test case for getDataset method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {getDataset} integration test with mandatory parameters.")
   public void testGetDatasetWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getDataset");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getDataset_mandatory.json");

      String apiEndpoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/datasets/"
            + connectorProperties.getProperty("datasetId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("datasetReference").getString("datasetId"),
            apiRestResponse.getBody().getJSONObject("datasetReference").getString("datasetId"));
   }

   /**
    * Positive test case for getDataset method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {getDataset} integration test with optional parameters.")
   public void testGetDatasetWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getDataset");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getDataset_optional.json");

      String apiEndpoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/datasets/"
            + connectorProperties.getProperty("datasetId")
            + "?fields=kind,id&prettyPrint=true&quotaUser=UserProjects&userIp=192.168.2.2";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
   }

   /**
    * Negative test case for getDataset method.
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {getDataset} integration test with negative case.")
   public void testGetDatasetWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getDataset");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getDataset_negative.json");

      String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/datasets/"
            + connectorProperties.getProperty("datasetId") + "?fields=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
   }

   /**
    * Positive test case for listTabledata method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {listTabledata} integration test with mandatory parameters.")
   public void testListTabledataWithMandatoryParameters() throws IOException, JSONException{

      esbRequestHeadersMap.put("Action", "urn:listTabledata");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTabledata_mandatory.json");
      String apiEndpoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/datasets/"
            + connectorProperties.getProperty("datasetId") + "/tables/" + connectorProperties.getProperty("tableId")
            + "/data";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
      Assert.assertEquals(esbRestResponse.getBody().getString("totalRows"), apiRestResponse.getBody().getString("totalRows"));
   }

   /**
    * Positive test case for listTabledata method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {listTabledata} integration test with optional parameters.")
   public void testListTabledataWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listTabledata");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listTabledata_optional.json");
      String apiEndpoint = apiUrl
            + "/projects/"
            + connectorProperties.getProperty("projectId")
            + "/datasets/"
            + connectorProperties.getProperty("datasetId")
            + "/tables/"
            + connectorProperties.getProperty("tableId")
            + "/data?maxResults=1&startIndex=0&fields=etag,kind,pageToken,totalRows,rows&prettyPrint=true&quotaUser=UserProjects";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
      Assert.assertEquals(esbRestResponse.getBody().getString("totalRows"), apiRestResponse.getBody().getString("totalRows"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("rows").length(), 1);
   }

   /**
    * Negative test case for listTabledata method.
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "BigQuery {listTabledata} integration test with negative case.")
   public void testListTabledataWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listTabledata");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listTabledata_negative.json");

      String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/datasets/"
            + connectorProperties.getProperty("datasetId") + "/tables/" + connectorProperties.getProperty("tabelId")
            + "/data?maxResults=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
   }

   /**
    * Positive test case for getTable method with mandatory parameters.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "bigquery {getTable} integration test with mandatory parameters.")
   public void testGetTableWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getTable");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getTable_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndpoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/datasets/"
            + connectorProperties.getProperty("datasetId") + "/tables/" + connectorProperties.getProperty("tableId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getString("creationTime"), apiRestResponse.getBody().getString(
            "creationTime"));
      Assert.assertEquals(esbRestResponse.getBody().getString("lastModifiedTime"), apiRestResponse.getBody().getString(
            "lastModifiedTime"));
      Assert.assertEquals(esbRestResponse.getBody().getString("numRows"), apiRestResponse.getBody()
            .getString("numRows"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(0)
            .getString("name"), apiRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(
            0).getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(0)
            .getString("type"), apiRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(
            0).getString("type"));

   }

   /**
    * Positive test case for getTable method with optional parameters.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "bigquery {getTable} integration test with optional parameters.")
   public void testGetTableWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getTable");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getTable_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndpoint = apiUrl
            + "/projects/"
            + connectorProperties.getProperty("projectId")
            + "/datasets/"
            + connectorProperties.getProperty("datasetId")
            + "/tables/"
            + connectorProperties.getProperty("tableId")
            + "?fields=schema,numRows,creationTime,lastModifiedTime&quotaUser=1hx46f5g4h5ghx6h41x54gh6f4hx&userIp=192.77.88.12&prettyPrint=true";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getString("creationTime"), apiRestResponse.getBody().getString(
            "creationTime"));
      Assert.assertEquals(esbRestResponse.getBody().getString("lastModifiedTime"), apiRestResponse.getBody().getString(
            "lastModifiedTime"));
      Assert.assertEquals(esbRestResponse.getBody().getString("numRows"), apiRestResponse.getBody()
            .getString("numRows"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(0)
            .getString("name"), apiRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(
            0).getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(0)
            .getString("type"), apiRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(
            0).getString("type"));

   }

   /**
    * Negative test case for getTable method.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "bigquery {getTable} integration test with negative case.")
   public void testGetTableWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getTable");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getTable_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

      final String apiEndpoint = apiUrl + "/projects/INVALID/datasets/" + connectorProperties.getProperty("datasetId")
            + "/tables/" + connectorProperties.getProperty("tableId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
            .getString("domain"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
            .getJSONObject(0).getString("domain"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
            .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
            .getJSONObject(0).getString("reason"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
            .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
            .getJSONObject(0).getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));

   }

   /**
    * Positive test case for listTables method with mandatory parameters.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "bigquery {listTables} integration test with mandatory parameters.")
   public void testListTablesWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listTables");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listTables_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      final String apiEndpoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/datasets/"
            + connectorProperties.getProperty("datasetId") + "/tables";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getString("etag"), apiRestResponse.getBody().getString("etag"));
      Assert.assertEquals(esbRestResponse.getBody().getString("totalItems"), apiRestResponse.getBody().getString(
            "totalItems"));
      Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tables").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("tables").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tables").getJSONObject(0).getJSONObject(
            "tableReference").getString("tableId"), apiRestResponse.getBody().getJSONArray("tables").getJSONObject(0)
            .getJSONObject("tableReference").getString("tableId"));
   }

   /**
    * Positive test case for listTables method with optional parameters.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "bigquery {listTables} integration test with optional parameters.")
   public void testListTablesWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listTables");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listTables_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      final String apiEndpoint = apiUrl
            + "/projects/"
            + connectorProperties.getProperty("projectId")
            + "/datasets/"
            + connectorProperties.getProperty("datasetId")
            + "/tables?fields=kind,etag,tables,totalItems&quotaUser=1hx46f5g4h5ghx6h41x54gh6f4hx&userIp=192.77.88.12&prettyPrint=true&maxResults=5";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getString("etag"), apiRestResponse.getBody().getString("etag"));
      Assert.assertEquals(esbRestResponse.getBody().getString("totalItems"), apiRestResponse.getBody().getString(
            "totalItems"));
      Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tables").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("tables").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tables").getJSONObject(0).getJSONObject(
            "tableReference").getString("tableId"), apiRestResponse.getBody().getJSONArray("tables").getJSONObject(0)
            .getJSONObject("tableReference").getString("tableId"));
   }

   /**
    * Negative test case for listTables method.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "bigquery {listTables} integration test with negative case.")
   public void testListTablesWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listTables");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listTables_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      final String apiEndpoint = apiUrl + "/projects/INVALID/datasets/" + connectorProperties.getProperty("datasetId")
            + "/tables";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
            .getString("domain"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
            .getJSONObject(0).getString("domain"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
            .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
            .getJSONObject(0).getString("reason"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
            .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
            .getJSONObject(0).getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));

   }

   /**
    * Positive test case for runQuery method with mandatory parameters.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "bigquery {runQuery} integration test with mandatory parameters.")
   public void testRunQueryWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:runQuery");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_runQuery_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndpoint = apiUrl + "/projects/" + connectorProperties.getProperty("runQueryProjectId")
            + "/queries";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
            "api_runQuery_mandatory.json");
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(0)
            .getString("name"), apiRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(
            0).getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(0)
            .getString("type"), apiRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(
            0).getString("type"));
      Assert.assertEquals(esbRestResponse.getBody().getString("totalRows"), apiRestResponse.getBody().getString(
            "totalRows"));
      Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("rows").getJSONObject(0).getJSONArray("f")
            .getJSONObject(0).getString("v"), apiRestResponse.getBody().getJSONArray("rows").getJSONObject(0)
            .getJSONArray("f").getJSONObject(0).getString("v"));

   }

   /**
    * Positive test case for runQuery method with optional parameters.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "bigquery {runQuery} integration test with optional parameters.")
   public void testRunQueryWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:runQuery");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_runQuery_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndpoint = apiUrl + "/projects/" + connectorProperties.getProperty("runQueryProjectId")
            + "/queries";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
            "api_runQuery_optional.json");
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(0)
            .getString("name"), apiRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(
            0).getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(0)
            .getString("type"), apiRestResponse.getBody().getJSONObject("schema").getJSONArray("fields").getJSONObject(
            0).getString("type"));
      Assert.assertEquals(esbRestResponse.getBody().getString("totalRows"), apiRestResponse.getBody().getString(
            "totalRows"));
      Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("rows").getJSONObject(0).getJSONArray("f")
            .getJSONObject(0).getString("v"), apiRestResponse.getBody().getJSONArray("rows").getJSONObject(0)
            .getJSONArray("f").getJSONObject(0).getString("v"));

   }

   /**
    * Negative test case for runQuery method.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb.bigquery" }, dependsOnMethods = { "testGetAccessTokenFromRefreshToken" }, description = "bigquery {runQuery} integration test with negative case.")
   public void testRunQueryWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:runQuery");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_runQuery_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      final String apiEndpoint = apiUrl + "/projects/INVALID/queries";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
            "api_runQuery_negative.json");
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
            .getString("domain"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
            .getJSONObject(0).getString("domain"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
            .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
            .getJSONObject(0).getString("reason"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
            .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
            .getJSONObject(0).getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));

   }
}
