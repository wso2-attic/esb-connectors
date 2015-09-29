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
package org.wso2.carbon.connector.integration.test.elasticsearch;

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

public class ElasticsearchConnectorIntegrationTest extends ConnectorIntegrationTestBase {

   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

   private String apiUrl;
   
   private long currentTimeString;

   /**
    * Set up the environment.
    * @throws Exception 
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {

      init("elasticsearch-connector-1.0.0");

      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);

      apiUrl = connectorProperties.getProperty("apiUrl");
      currentTimeString = System.currentTimeMillis();

   }

   /**
    * Positive test case for searchByQuery method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {searchByQuery} integration test with mandatory parameters.")
   public void testSearchByQueryWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:searchByQuery");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_searchByQuery_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String apiEndPoint = apiUrl + "/_search";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_id"), apiRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_id"));
      Assert.assertEquals(esbRestResponse.getBody().getString("timed_out"),
            apiRestResponse.getBody().getString("timed_out"));
   }

   /**
    * Positive test case for searchByQuery method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {searchByQuery} integration test with optional parameters.")
   public void testSearchByQueryWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:searchByQuery");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_searchByQuery_optional.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String apiEndPoint = apiUrl + "/_search?terminate_after=" + connectorProperties.getProperty("terminateAfter")
            + "&scroll=" + connectorProperties.getProperty("scroll");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
            "api_searchByQuery_optional.json");
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").length(), 1);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getString("max_score"), apiRestResponse
            .getBody().getJSONObject("hits").getString("max_score"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_version"),
            apiRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getString("_version"));
      Assert.assertEquals(esbRestResponse.getBody().getString("timed_out"),
            apiRestResponse.getBody().getString("timed_out"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getString("total"), apiRestResponse.getBody()
            .getJSONObject("hits").getString("total"));
   }

   /**
    * Negative test case for searchByQuery method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {searchByQuery} integration test with negative case.")
   public void testSearchByQueryWithNegativeCase() throws IOException, JSONException{

      esbRequestHeadersMap.put("Action", "urn:searchByQuery");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_searchByQuery_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      String apiEndPoint = apiUrl + "/_search?terminate_after=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }

   /**
    * Positive test case for searchDocumentByIndex method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {searchDocumentByIndex} integration test with mandatory parameters.")
   public void testSearchDocumentByIndexWithMandatoryParameters() throws IOException, JSONException{

      esbRequestHeadersMap.put("Action", "urn:searchDocumentByIndex");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_searchDocumentByIndex_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String apiEndPoint = apiUrl + "/" + connectorProperties.getProperty("indexName") + "/_search";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_index"), connectorProperties.getProperty("indexName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_id"), apiRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_id"));
   }

   /**
    * Positive test case for searchDocumentByIndex method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {searchDocumentByIndex} integration test with optional parameters.")
   public void testSearchDocumentByIndexWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:searchDocumentByIndex");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_searchDocumentByIndex_optional.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String apiEndPoint = apiUrl + "/" + connectorProperties.getProperty("indexName") + "/_search?terminate_after="
            + connectorProperties.getProperty("terminateAfter") + "&scroll="
            + connectorProperties.getProperty("scroll");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
            "api_searchByQuery_optional.json");
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_index"), connectorProperties.getProperty("indexName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").length(), 1);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getString("max_score"), apiRestResponse
            .getBody().getJSONObject("hits").getString("max_score"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_version"),
            apiRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getString("_version"));
      Assert.assertEquals(esbRestResponse.getBody().getString("timed_out"),
            apiRestResponse.getBody().getString("timed_out"));
   }

   /**
    * Negative test case for searchDocumentByIndex method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {searchDocumentByIndex} integration test with negative case.")
   public void testSearchDocumentByIndexWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:searchDocumentByIndex");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_searchDocumentByIndex_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      String apiEndPoint = apiUrl + "/" + connectorProperties.getProperty("indexName")
            + "/_search?terminate_after=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }

   /**
    * Positive test case for searchDocumentByTypes method with mandatory
    * parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {searchDocumentByTypes} integration test with mandatory parameters.")
   public void testSearchDocumentByTypesWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:searchDocumentByTypes");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_searchDocumentByTypes_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String apiEndPoint = apiUrl + "/" + connectorProperties.getProperty("indexName") + "/"
            + connectorProperties.getProperty("type") + "/_search";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_index"), connectorProperties.getProperty("indexName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_id"), apiRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_id"));
   }

   /**
    * Positive test case for searchDocumentByTypes method with optional
    * parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {searchDocumentByTypes} integration test with optional parameters.")
   public void testSearchDocumentByTypesWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:searchDocumentByTypes");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_searchDocumentByTypes_optional.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String apiEndPoint = apiUrl + "/" + connectorProperties.getProperty("indexName") + "/"
            + connectorProperties.getProperty("type") + "/_search?terminate_after="
            + connectorProperties.getProperty("terminateAfter") + "&scroll="
            + connectorProperties.getProperty("scroll");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
            "api_searchByQuery_optional.json");
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_index"), connectorProperties.getProperty("indexName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").length(), 1);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getString("max_score"), apiRestResponse
            .getBody().getJSONObject("hits").getString("max_score"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
            .getString("_version"),
            apiRestResponse.getBody().getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getString("_version"));
      Assert.assertEquals(esbRestResponse.getBody().getString("timed_out"),
            apiRestResponse.getBody().getString("timed_out"));
   }

   /**
    * Negative test case for searchDocumentByTypes method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {searchDocumentByTypes} integration test with negative case.")
   public void testSearchDocumentByTypesWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:searchDocumentByTypes");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_searchDocumentByTypes_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      String apiEndPoint = apiUrl + "/" + connectorProperties.getProperty("indexName") + "/"
            + connectorProperties.getProperty("type") + "/_search?terminate_after=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }
   
   /**
    * Positive test case for deleteDocument method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateDocumentWithMandatoryParameters" }, description = "elasticSearch {deleteDocument} integration test with mandatory parameters.")
   public void testDeleteDocumentWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:deleteDocument");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteDocument_mandatory.json");
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/" + connectorProperties.getProperty("documentIdMandatory");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
   }
   
   /**
    * Positive test case for deleteDocument method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateDocumentWithOptionalParameters" }, description = "elasticSearch {deleteDocument} integration test with optional parameters.")
   public void testDeleteDocumentWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:deleteDocument");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteDocument_optional.json");
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/" + connectorProperties.getProperty("documentIdOptional");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
   }
   
   /**
    * Negative test case for deleteDocument method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {deleteDocument} integration test with negative case.")
   public void testDeleteDocumentWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:deleteDocument");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteDocument_negative.json");
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/INVALID";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("found"), "false");
      Assert.assertEquals(apiRestResponse.getBody().getString("found"), esbRestResponse.getBody().getString("found"));
   }
   
   /**
    * Positive test case for updateDocument method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAutomaticIdWithMandatoryParameters" }, description = "elasticSearch {updateDocument} integration test with mandatory parameters.")
   public void testUpdateDocumentWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateDocument");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateDocument_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/" + connectorProperties.getProperty("documentIdMandatory");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("_source").getString("age"),
            connectorProperties.getProperty("updateDocValueMandatory"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_version"),
            apiRestResponse.getBody().getString("_version"));
      
   }
   
   /**
    * Positive test case for updateDocument method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAutomaticIdWithOptionalParameters" }, description = "elasticSearch {updateDocument} integration test with optional parameters.")
   public void testUpdateDocumentWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateDocument");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateDocument_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/" + connectorProperties.getProperty("documentIdOptional");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("_source").getString("age"),
            connectorProperties.getProperty("updateDocValueOptional"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_version"),
            apiRestResponse.getBody().getString("_version"));
      
   }
   
   /**
    * Negative test case for updateDocument method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {updateDocument} integration test with negative case.")
   public void testUpdateDocumentWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateDocument");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateDocument_negative.json");
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/INVALID/_update";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateDocument_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getString("error"), esbRestResponse.getBody().getString("error"));
      
   }
   
   /**
    * Positive test case for routeDocument method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {routeDocument} integration test with mandatory parameters.")
   public void testRouteDocumentWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:routeDocument");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_routeDocument_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      String documentId = esbRestResponse.getBody().getString("_id");
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/" + documentId + "?routing=" + connectorProperties.getProperty("routing");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(connectorProperties.getProperty("routingMessageMandatory"), apiRestResponse.getBody()
            .getJSONObject("_source").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_version"),
            apiRestResponse.getBody().getString("_version"));
      
   }
   
   /**
    * Positive test case for routeDocument method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {routeDocument} integration test with optional parameters.")
   public void testRouteDocumentWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:routeDocument");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_routeDocument_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      String documentId = esbRestResponse.getBody().getString("_id");
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/" + documentId + "?routing=" + connectorProperties.getProperty("routing");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(connectorProperties.getProperty("routingMessageOptional"), apiRestResponse.getBody()
            .getJSONObject("_source").getString("message"));
      Assert.assertEquals(connectorProperties.getProperty("userName"), apiRestResponse.getBody().getJSONObject("_source")
            .getString("user"));
      Assert.assertEquals(connectorProperties.getProperty("postDate"),
            apiRestResponse.getBody().getJSONObject("_source").getString("post_date"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_version"),
            apiRestResponse.getBody().getString("_version"));
      
   }
   
   /**
    * Negative test case for routeDocument method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {routeDocument} integration test with negative case.")
   public void testRouteDocumentWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:routeDocument");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_routeDocument_negative.json");
      String apiEndPoint =
            apiUrl + "/INVALID/" + connectorProperties.getProperty("type") + "?routing="
                  + connectorProperties.getProperty("routing");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_routeDocument_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getString("error"), esbRestResponse.getBody().getString("error"));
      
   }
   
   /**
    * Positive test case for createAutomaticId method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {createAutomaticId} integration test with mandatory parameters.")
   public void testCreateAutomaticIdWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createAutomaticId");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAutomaticId_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      String documentId = esbRestResponse.getBody().getString("_id");
      connectorProperties.put("documentIdMandatory", documentId);
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/" + documentId ;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(connectorProperties.getProperty("messageMandatory"), apiRestResponse.getBody()
            .getJSONObject("_source").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_version"),
            apiRestResponse.getBody().getString("_version"));
      
   }
   
   /**
    * Positive test case for createAutomaticId method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {createAutomaticId} integration test with optional parameters.")
   public void testCreateAutomaticIdWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createAutomaticId");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAutomaticId_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      String documentId = esbRestResponse.getBody().getString("_id");
      connectorProperties.put("documentIdOptional", documentId);
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/" + documentId ;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(connectorProperties.getProperty("messageOptional"), apiRestResponse.getBody()
            .getJSONObject("_source").getString("message"));
      Assert.assertEquals(connectorProperties.getProperty("userName"), apiRestResponse.getBody().getJSONObject("_source")
            .getString("user"));
      Assert.assertEquals(connectorProperties.getProperty("postDate"),
            apiRestResponse.getBody().getJSONObject("_source").getString("post_date"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_version"),
            apiRestResponse.getBody().getString("_version"));
      
   }
   
   /**
    * Negative test case for createAutomaticId method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {createAutomaticId} integration test with negative case.")
   public void testCreateAutomaticIdWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createAutomaticId");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAutomaticId_negative.json");
      String apiEndPoint =
            apiUrl + "/INVALID/" + connectorProperties.getProperty("type");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAutomaticId_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getString("error"), esbRestResponse.getBody().getString("error"));
      
   }
   
   /**
    * Positive test case for performBulkOperations method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {performBulkOperations} integration test with mandatory parameters.")
   public void testPerformBulkOperationsWithMandatoryParameters() throws IOException, JSONException {
   
      final Map<String, String> bulkOperationsRequestHeadersMap = new HashMap<String, String>();
      bulkOperationsRequestHeadersMap.putAll(apiRequestHeadersMap);
      bulkOperationsRequestHeadersMap.put("Action", "urn:performBulkOperations");
      bulkOperationsRequestHeadersMap.put("Content-Type", "text/plain");
      
      final String proxyUrlWithQueryParams = proxyUrl + "?apiUrl=" + apiUrl;
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrlWithQueryParams, "POST", bulkOperationsRequestHeadersMap,
                  "esb_bulkOperations_mandatory.json");

      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      final String id =
            esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getJSONObject("create").getString("_id");
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/" + connectorProperties.getProperty("type")
                  + "/" + id;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getString("_index"), connectorProperties.getProperty("indexName"));
      Assert.assertEquals(apiRestResponse.getBody().getString("_type"), connectorProperties.getProperty("type"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("_source").getString("value1"),
            connectorProperties.getProperty("bulkOperationValue1"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("_source").getString("value2"),
            connectorProperties.getProperty("bulkOperationValue2"));
      
   }
   
   /**
    * Negative test case for performBulkOperations.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {performBulkOperations} integration test with negative case.")
   public void testPerformBulkOperationsWithNegative() throws IOException, JSONException {
   
      final Map<String, String> bulkOperationsRequestHeadersMap = new HashMap<String, String>();
      bulkOperationsRequestHeadersMap.putAll(apiRequestHeadersMap);
      bulkOperationsRequestHeadersMap.put("Action", "urn:performBulkOperations");
      bulkOperationsRequestHeadersMap.put("Content-Type", "text/plain");
      
      final String proxyUrlWithQueryParams = proxyUrl + "?apiUrl=" + apiUrl;
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrlWithQueryParams, "POST", bulkOperationsRequestHeadersMap,
                  "esb_bulkOperations_negative.json");
      
      String apiEndPoint = apiUrl + "/_bulk";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", bulkOperationsRequestHeadersMap,
                  "api_bulkOperations_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      
   }
   
   /**
    * Positive test case for multiSearch method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" },dependsOnMethods={"testPerformBulkOperationsWithMandatoryParameters"}, description = "elasticSearch {multiSearch} integration test with mandatory parameters.")
   public void testMultiSearchWithMandatoryParameters() throws IOException, JSONException {
   
      final Map<String, String> bulkOperationsRequestHeadersMap = new HashMap<String, String>();
      bulkOperationsRequestHeadersMap.putAll(apiRequestHeadersMap);
      bulkOperationsRequestHeadersMap.put("Action", "urn:multiSearch");
      bulkOperationsRequestHeadersMap.put("Content-Type", "text/plain");
      
      final String proxyUrlWithQueryParams = proxyUrl + "?apiUrl=" + apiUrl;
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrlWithQueryParams, "POST", bulkOperationsRequestHeadersMap,
                  "esb_multiSearch_mandatory.json");
      JSONObject esbShards =
            esbRestResponse.getBody().getJSONArray("responses").getJSONObject(0).getJSONObject("_shards");
      JSONObject esbHits = esbRestResponse.getBody().getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
      
      String apiEndPoint = apiUrl + "/_msearch";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", bulkOperationsRequestHeadersMap, "api_multiSearch_mandatory.json");
      
      JSONObject apiShards =
            apiRestResponse.getBody().getJSONArray("responses").getJSONObject(0).getJSONObject("_shards");
      JSONObject apiHits = apiRestResponse.getBody().getJSONArray("responses").getJSONObject(0).getJSONObject("hits");
      
      Assert.assertEquals(esbShards.getString("total"), apiShards.getString("total"));
      Assert.assertEquals(esbShards.getString("successful"), apiShards.getString("successful"));
      Assert.assertEquals(esbHits.getString("total"), apiHits.getString("total"));
      Assert.assertEquals(esbHits.getJSONArray("hits").getJSONObject(0).getString("_id"), apiHits.getJSONArray("hits")
            .getJSONObject(0).getString("_id"));
      
   }
   
   /**
    * Negative test case for multiSearch.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "elasticSearch {multiSearch} integration test with negative case.")
   public void testMultiSearchWithNegative() throws IOException, JSONException {
   
      final Map<String, String> bulkOperationsRequestHeadersMap = new HashMap<String, String>();
      bulkOperationsRequestHeadersMap.putAll(apiRequestHeadersMap);
      bulkOperationsRequestHeadersMap.put("Action", "urn:multiSearch");
      bulkOperationsRequestHeadersMap.put("Content-Type", "text/plain");
      
      final String proxyUrlWithQueryParams = proxyUrl + "?apiUrl=" + apiUrl;
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrlWithQueryParams, "POST", bulkOperationsRequestHeadersMap,
                  "esb_multiSearch_negative.json");
      
      String apiEndPoint = apiUrl + "/_msearch";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", bulkOperationsRequestHeadersMap, "api_multiSearch_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      
   }
   
   /**
    * Positive test case for createAutomaticIndex method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {createAutomaticIndex} integration test with mandatory parameters.")
   public void testCreateAutomaticIndexWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:createAutomaticIndex");
      final String indexName=connectorProperties.getProperty("indexNameMand");
      
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAutomaticIndex_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200); 
      final String isIndexAcknowledged=esbRestResponse.getBody().getString("acknowledged");
      Assert.assertEquals(isIndexAcknowledged, "true");

      String apiEndPoint = apiUrl + "/"+indexName;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getBody().has(indexName), true); 
   }
   
   /**
    * Positive test case for createAutomaticIndex method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {createAutomaticIndex} integration test with optional parameters.")
   public void testCreateAutomaticIndexWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:createAutomaticIndex");
      final String indexName=connectorProperties.getProperty("indexNameOpt");
      
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAutomaticIndex_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200); 

      String apiEndPoint = apiUrl + "/"+indexName;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getBody().has(indexName), true);
      
      final JSONObject mappingObject=apiRestResponse.getBody().getJSONObject(indexName).getJSONObject("settings").getJSONObject("index").getJSONObject("mapping");
      final String indexMappingAllowType=mappingObject.getString("allow_type_wrapper");
      
      Assert.assertEquals(indexMappingAllowType, connectorProperties.getProperty("indexMappingAllowType"));
      
   }
   
   /**
    * Negative test case for createAutomaticIndex method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {createAutomaticIndex} integration test with negative case.")
   public void testCreateAutomaticIndexWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:createAutomaticIndex");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_createAutomaticIndex_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      String apiEndPoint = apiUrl + "/INVALIDINDEX";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }

   /**
    * Positive test case for indexChildDocument method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateAutomaticIndexWithMandatoryParameters","testCreateDocumentWithIndexWithMandatoryParameters"}, description = "elasticSearch {indexChildDocument} integration test with mandatory parameters.")
   public void testIndexChildDocumentWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:indexChildDocument");
      final String indexName=connectorProperties.getProperty("indexNameMand");
      final String type=connectorProperties.getProperty("indexChildDocumentTypeMand");
      final String childId=connectorProperties.getProperty("indexChildDocumentChildId");
      final String parentId=connectorProperties.getProperty("documentIdMandatory");
      
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_indexChildDocument_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201); 

      String apiEndPoint = apiUrl + "/"+indexName+"/"+type+"/"+childId+"?parent="+parentId;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      Assert.assertEquals(apiRestResponse.getBody().getString("found"),"true");
      Assert.assertEquals(apiRestResponse.getBody().getString("_index"), indexName);
      Assert.assertEquals(apiRestResponse.getBody().getString("_type"), type);
      Assert.assertEquals(esbRestResponse.getBody().getString("_id"), childId);
   }
   
   /**
    * Positive test case for indexChildDocument method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateAutomaticIndexWithOptionalParameters", "testCreateDocumentWithIndexWithMandatoryParameters"}, description = "elasticSearch {indexChildDocument} integration test with optional parameters.")
   public void testIndexChildDocumentWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:indexChildDocument");
      final String indexName=connectorProperties.getProperty("indexNameOpt");
      final String type=connectorProperties.getProperty("indexChildDocumentTypeOpt");
      final String childId=connectorProperties.getProperty("indexChildDocumentChildId");
      final String parentId=connectorProperties.getProperty("documentIdMandatory");
      final String tagValue=connectorProperties.getProperty("tagValue");
      
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_indexChildDocument_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201); 

      String apiEndPoint = apiUrl + "/"+indexName+"/"+type+"/"+childId+"?parent="+parentId;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      Assert.assertEquals(apiRestResponse.getBody().getString("found"),"true");
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("_source").getString("tag"), tagValue);
     
   }
   
   /**
    * Negative test case for indexChildDocument method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateAutomaticIndexWithMandatoryParameters", "testCreateDocumentWithIndexWithMandatoryParameters"}, description = "elasticSearch {indexChildDocument} integration test with negative case.")
   public void testIndexChildDocumentWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:indexChildDocument");
      final String indexName=connectorProperties.getProperty("indexNameMand");
      final String type=connectorProperties.getProperty("indexChildDocumentTypeMand");
      final String childId=connectorProperties.getProperty("indexChildDocumentChildId");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_indexChildDocument_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      //Mapping the index child type with parent type
      String apiEndPoint = apiUrl + "/"+indexName+"/"+type+"/_mapping";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_indexChildDocument_mapping_negative.json");
      
      final boolean isApiMappingAcknowleged=apiRestResponse.getBody().getBoolean("acknowledged");
      
      if(isApiMappingAcknowleged){
    	  apiEndPoint = apiUrl + "/"+indexName+"/"+type+"/"+childId+"?parent=";
    	  apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_indexChildDocument_negative.json");
    	  Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    	  Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    	  Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      }
      else {
         Assert.fail("Mapping index child type with parent type failed.");
      }

   }
   
   /**
    * Positive test case for createDocumentWithIndex method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {createDocumentWithIndex} integration test with mandatory parameters.")
   public void testCreateDocumentWithIndexWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createDocumentWithIndex");
      
      String documentIdMandatory = "documentIdMandatory_" + currentTimeString;
      connectorProperties.setProperty("documentIdMandatory", documentIdMandatory);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDocumentWithIndex_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      
      String esbIndexName = esbRestResponse.getBody().getString("_index");
      String esbIndexType = esbRestResponse.getBody().getString("_type");
      String esbDocumentId = esbRestResponse.getBody().getString("_id");
      
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/"
                  + connectorProperties.getProperty("type") + "/"
                  + connectorProperties.getProperty("documentIdMandatory");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbIndexName, connectorProperties.getProperty("indexName"));
      Assert.assertEquals(esbIndexType, connectorProperties.getProperty("type"));
      Assert.assertEquals(esbDocumentId, connectorProperties.getProperty("documentIdMandatory"));
      
      Assert.assertEquals(esbIndexName, apiRestResponse.getBody().getString("_index"));
      Assert.assertEquals(esbIndexType, apiRestResponse.getBody().getString("_type"));
      Assert.assertEquals(esbDocumentId, apiRestResponse.getBody().getString("_id"));
   }
   
   /**
    * Positive test case for createDocumentWithIndex method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {createDocumentWithIndex} integration test with optional parameters.")
   public void testCreateDocumentWithIndexWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createDocumentWithIndex");
      
      String documentIdOptional = "documentIdOptional_" + currentTimeString;
      connectorProperties.setProperty("documentIdOptional", documentIdOptional);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDocumentWithIndex_optional.json");
      
      JSONObject esbResponseJsonObject = esbRestResponse.getBody();
      
      String esbIndexName = esbResponseJsonObject.getString("_index");
      String esbIndexType = esbResponseJsonObject.getString("_type");
      String esbDocumentId = esbResponseJsonObject.getString("_id");
      String esbVersion = esbResponseJsonObject.getString("_version");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/"
                  + connectorProperties.getProperty("type") + "/"
                  + connectorProperties.getProperty("documentIdOptional");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONObject apiResponseSource = apiRestResponse.getBody().getJSONObject("_source");
      
      Assert.assertEquals(esbIndexName, connectorProperties.getProperty("indexName"));
      Assert.assertEquals(esbIndexType, connectorProperties.getProperty("type"));
      Assert.assertEquals(esbDocumentId, connectorProperties.getProperty("documentIdOptional"));
      Assert.assertEquals(esbVersion, connectorProperties.getProperty("version"));
      
      Assert.assertEquals(esbIndexName, apiRestResponse.getBody().getString("_index"));
      Assert.assertEquals(esbIndexType, apiRestResponse.getBody().getString("_type"));
      Assert.assertEquals(esbDocumentId, apiRestResponse.getBody().getString("_id"));
      Assert.assertEquals(apiResponseSource.getString("post_date"), connectorProperties.getProperty("postDate"));
      Assert.assertEquals(apiResponseSource.getString("user"), connectorProperties.getProperty("userName"));
      Assert.assertEquals(apiResponseSource.getString("message"), connectorProperties.getProperty("message"));
   }
   
   /**
    * Negative test case for createDocumentWithIndex method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {createDocumentWithIndex} integration test with negative case.")
   public void testCreateDocumentWithIndexWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createDocumentWithIndex");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDocumentWithIndex_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/"
                  + connectorProperties.getProperty("type") + "/" + 400 + "?version=INVALID";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_createDocumentWithIndex_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }
   
   /**
    * Positive test case for getDocument method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateDocumentWithIndexWithOptionalParameters"}, description = "elasticSearch {getDocument} integration test with mandatory parameters.")
   public void testGetDocumentWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getDocument");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDocument_mandatory.json");
      
      JSONObject esbResponseSource = esbRestResponse.getBody().getJSONObject("_source");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/"
                  + connectorProperties.getProperty("type") + "/"
                  + connectorProperties.getProperty("documentIdOptional");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONObject apiResponseSource = apiRestResponse.getBody().getJSONObject("_source");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("_index"), apiRestResponse.getBody().getString("_index"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_type"), apiRestResponse.getBody().getString("_type"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_id"), apiRestResponse.getBody().getString("_id"));
      Assert.assertEquals(esbResponseSource.getString("post_date"), apiResponseSource.getString("post_date"));
      Assert.assertEquals(esbResponseSource.getString("user"), apiResponseSource.getString("user"));
      Assert.assertEquals(esbResponseSource.getString("message"), apiResponseSource.getString("message"));
   }
   
   /**
    * Positive test case for getDocument method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateDocumentWithIndexWithOptionalParameters"}, description = "elasticSearch {getDocument} integration test with optional parameters.")
   public void testGetDocumentWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getDocument");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDocument_optional.json");
      
      JSONObject esbResponseSource = esbRestResponse.getBody().getJSONObject("_source");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/"
                  + connectorProperties.getProperty("type") + "/"
                  + connectorProperties.getProperty("documentIdOptional") + "?version="
                  + connectorProperties.getProperty("version") + "&_source=message,user";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONObject apiResponseSource = apiRestResponse.getBody().getJSONObject("_source");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("_index"), apiRestResponse.getBody().getString("_index"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_type"), apiRestResponse.getBody().getString("_type"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_id"), apiRestResponse.getBody().getString("_id"));
      Assert.assertEquals(esbRestResponse.getBody().getString("_version"),
            apiRestResponse.getBody().getString("_version"));
      Assert.assertEquals(esbResponseSource.getString("message"), apiResponseSource.getString("message"));
      Assert.assertEquals(esbResponseSource.getString("user"), apiResponseSource.getString("user"));
   }
   
   /**
    * Negative test case for getDocument method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateDocumentWithIndexWithOptionalParameters"}, description = "elasticSearch {getDocument} integration test with negative case.")
   public void testGetDocumentWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getDocument");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDocument_negative.json");
      
      String apiEndPoint =
            apiUrl + "/" + connectorProperties.getProperty("indexName") + "/"
                  + connectorProperties.getProperty("type") + "/" + 400 + "?version=INVALID";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }
   
   /**
    * Positive test case for listDocuments method with mandatory parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateDocumentWithIndexWithMandatoryParameters", "testCreateDocumentWithIndexWithOptionalParameters"}, description = "elasticSearch {listDocuments} integration test with mandatory parameters.")
   public void testListDocumentsWithMandatoryParameters() throws IOException, JSONException {
       
       esbRequestHeadersMap.put("Action", "urn:listDocuments");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDocuments_mandatory.json");
       
       String apiEndPoint = apiUrl + "/" + connectorProperties.getProperty("indexName") + "/_mget";
       
       RestResponse<JSONObject> apiRestResponse =
               sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listDocuments_mandatory.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").length(), apiRestResponse.getBody().getJSONArray("docs").length());
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").getJSONObject(0).getString("_index"),
               apiRestResponse.getBody().getJSONArray("docs").getJSONObject(0).getString("_index"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").getJSONObject(0).getString("_type"),
               apiRestResponse.getBody().getJSONArray("docs").getJSONObject(0).getString("_type"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").getJSONObject(0).getString("_source"),
               apiRestResponse.getBody().getJSONArray("docs").getJSONObject(0).getString("_source"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").getJSONObject(0).getString("_version"),
               apiRestResponse.getBody().getJSONArray("docs").getJSONObject(0).getString("_version"));
       
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getString("_index"),
               apiRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getString("_index"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getString("_type"),
               apiRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getString("_type"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getString("_source"),
               apiRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getString("_source"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getString("_version"),
               apiRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getString("_version"));
       
   }
   
   /**
    * Positive test case for listDocuments method with optional parameters.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateDocumentWithIndexWithMandatoryParameters", "testCreateDocumentWithIndexWithOptionalParameters"}, description = "elasticSearch {listDocuments} integration test with optional parameters.")
   public void testListDocumentsWithOptionalParameters() throws IOException, JSONException {
       
       esbRequestHeadersMap.put("Action", "urn:listDocuments");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDocuments_optional.json");
       
       String apiEndPoint = apiUrl + "/" + connectorProperties.getProperty("indexName") + "/_mget";
       
       RestResponse<JSONObject> apiRestResponse =
               sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listDocuments_optional.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getJSONObject("fields")
    		   .getString("user"), apiRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getJSONObject("fields").getString("user"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getJSONObject("fields")
    		   .getString("post_date"), apiRestResponse.getBody().getJSONArray("docs").getJSONObject(1).getJSONObject("fields").getString("post_date"));
       
   }
   
   /**
    * Negative test case for listDocuments method.
    * @throws JSONException 
    * @throws IOException 
    */
   @Test(groups = { "wso2.esb" }, description = "elasticSearch {listDocuments} integration test with negative case.")
   public void testListDocumentsWithNegativeCase() throws IOException, JSONException {
       
       esbRequestHeadersMap.put("Action", "urn:listDocuments");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDocuments_negative.json");
       
       String apiEndPoint = apiUrl + "/" + connectorProperties.getProperty("indexName") + "/_mget";
       
       RestResponse<JSONObject> apiRestResponse =
               sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_listDocuments_negative.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
       Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
       Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
               .getString("status"));
   }
   

}
