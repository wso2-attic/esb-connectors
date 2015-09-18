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

package org.wso2.carbon.connector.integration.test.beetrack;

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

public class BeetrackConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap;
   
   private Map<String, String> apiRequestHeadersMap;
   
   private String apiEndpointUrl;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("beetrack-connector-1.0.0");
      
      esbRequestHeadersMap = new HashMap<String, String>();
      apiRequestHeadersMap = new HashMap<String, String>();
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      esbRequestHeadersMap.put("Accept", "application/json");
      
      apiRequestHeadersMap.put("X-AUTH-TOKEN", connectorProperties.getProperty("accessToken"));
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      
      apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/api/external/v1";
      
   }
   
   /**
    * Positive test case for createVehicle method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {createVehicle} integration test with mandatory parameters.")
   public void testCreateVehicleWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createVehicle");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createVehicle_mandatory.json");
      
      final String apiEndpoint = apiEndpointUrl + "/trucks/" + connectorProperties.getProperty("vehicleId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      
   }
   
   /**
    * Method name: createVehicle 
    * Test scenario: Optional 
    * Reason to skip: There are no optional parameters in this method.
    */
   
   /**
    * Negative test case for createVehicle method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {createVehicle} integration test with negative case.", dependsOnMethods = { "testCreateVehicleWithMandatoryParameters" })
   public void testCreateVehicleWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createVehicle");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createVehicle_negative.json");
      
      final String apiEndpoint = apiEndpointUrl + "/trucks";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createVehicle_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getString("response"),
            apiRestResponse.getBody().getString("response"));
      
   }
   
   /**
    * Positive test case for getVehicle method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {getVehicle} integration test with mandatory parameters.", dependsOnMethods = { "testCreateVehicleWithNegativeCase" })
   public void testGetVehicleWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getVehicle");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVehicle_mandatory.json");
      
      final String apiEndpoint = apiEndpointUrl + "/trucks/" + connectorProperties.getProperty("vehicleId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getString("truck"), apiRestResponse
            .getBody().getJSONObject("response").getString("truck"));
      
   }
   
   /**
    * Method name: getVehicle 
    * Test scenario: Optional 
    * Reason to skip: There are no optional parameters in this method.
    */
   
   /**
    * Negative test case for getVehicle method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {getVehicle} integration test with negative case.", dependsOnMethods = { "testGetVehicleWithMandatoryParameters" })
   public void testGetVehicleWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getVehicle");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVehicle_negative.json");
      
      final String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/trucks/xyz";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      // Response gives an empty JSON, This is the only value to assert
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
   }
   
   /**
    * Positive test case for listVehicles method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {listVehicles} integration test with mandatory parameters.", dependsOnMethods = { "testGetVehicleWithMandatoryParameters" })
   public void testListVehiclesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listVehicles");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listVehicles_mandatory.json");
      
      final String apiEndpoint = apiEndpointUrl + "/trucks";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("trucks").length(),
            apiRestResponse.getBody().getJSONObject("response").getJSONArray("trucks").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("trucks").get(0),
            esbRestResponse.getBody().getJSONObject("response").getJSONArray("trucks").get(0));
      
   }
   
   /**
    * Method name: lisVehicles 
    * Test scenario: Optional 
    * Reason to skip: There are no optional parameters in this method.
    */
   
   /**
    * Method name: lisVehicles 
    * Test scenario: Negative 
    * Reason to skip: There are no parameters to make negative in this method.
    */
   
   /**
    * Positive test case for createRoute method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {createRoute} integration test with mandatory parameters.", dependsOnMethods = { "testListVehiclesWithMandatoryParameters" })
   public void testCreateRouteWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createRoute");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRoute_mandatory.json");
      
      final String routeId = esbRestResponse.getBody().getJSONObject("response").getString("route_id");
      connectorProperties.put("routeId", routeId);
      
      final String apiEndpoint = apiEndpointUrl + "/routes/" + routeId;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(connectorProperties.getProperty("vehicleId"),
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("truck_identifier"));
      Assert.assertEquals(connectorProperties.getProperty("routeDate"),
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("dispatch_date"));
      Assert.assertEquals(connectorProperties.getProperty("routeDispatches"),
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("route").getJSONArray("dispatches")
                  .getJSONObject(0).getString("identifier"));
      
   }
   
   /**
    * Positive test case for createRoute method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {createRoute} integration test with optional parameters.", dependsOnMethods = { "testCreateRouteWithMandatoryParameters" })
   public void testCreateRouteWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createRoute");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRoute_optional.json");
      
      final String routeIdOptional = esbRestResponse.getBody().getJSONObject("response").getString("route_id");
      connectorProperties.put("routeIdOptional", routeIdOptional);
      
      final String apiEndpoint = apiEndpointUrl + "/routes/" + routeIdOptional;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(connectorProperties.getProperty("vehicleId"),
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("truck_identifier"));
      Assert.assertEquals(connectorProperties.getProperty("routeDate"),
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("dispatch_date"));
      Assert.assertEquals(connectorProperties.getProperty("routeDriverIdentifier"), apiRestResponse.getBody()
            .getJSONObject("response").getJSONObject("route").getString("driver_identifier"));
      Assert.assertEquals(connectorProperties.getProperty("routeDispatches"),
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("route").getJSONArray("dispatches")
                  .getJSONObject(0).getString("identifier"));
      
   }
   
   /**
    * Negative test case for createRoute method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {createRoute} integration test with negative case.", dependsOnMethods = { "testCreateRouteWithMandatoryParameters" })
   public void testCreateRouteWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createRoute");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRoute_negative.json");
      
      final String apiEndpoint = apiEndpointUrl + "/routes";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createRoute_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getString("response"),
            apiRestResponse.getBody().getString("response"));
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      
   }
   
   /**
    * Positive test case for getRoute method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {getRoute} integration test with mandatory parameters.", dependsOnMethods = { "testCreateRouteWithNegativeCase" })
   public void testGetRouteWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getRoute");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRoute_mandatory.json");
      
      final String apiEndpoint = apiEndpointUrl + "/routes/" + connectorProperties.getProperty("routeId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("dispatch_date"),
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("dispatch_date"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("truck_identifier"),
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("truck_identifier"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("driver_identifier"),
            esbRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("driver_identifier"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("start_time"),
            esbRestResponse.getBody().getJSONObject("response").getJSONObject("route").getString("start_time"));
      
   }
   
   /**
    * Method name: getRoute 
    * Test scenario: Optional 
    * Reason to skip: There are no optional parameters in this method.
    */
   
   /**
    * Negative test case for getRoute method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {getRoute} integration test with negative case.", dependsOnMethods = { "testGetRouteWithMandatoryParameters" })
   public void testGetRouteWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getRoute");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRoute_negative.json");
      
      final String apiEndpoint = apiEndpointUrl + "/routes/invalid";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getString("response"),
            apiRestResponse.getBody().getString("response"));
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      
   }
   
   /**
    * Positive test case for listRoutes method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {listRoutes} integration test with mandatory parameters.", dependsOnMethods = { "testGetRouteWithNegativeCase" })
   public void testListRoutesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listRoutes");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRoutes_mandatory.json");
      
      final String apiEndpoint = apiEndpointUrl + "/routes";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").length(),
            apiRestResponse.getBody().getJSONObject("response").getJSONArray("routes").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(0)
            .getString("id"),
            esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(0)
            .getString("truck_identifier"), esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes")
            .getJSONObject(0).getString("truck_identifier"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(1)
            .getString("id"),
            esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(1).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(1)
            .getString("truck_identifier"), esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes")
            .getJSONObject(1).getString("truck_identifier"));
      
   }
   
   /**
    * Positive test case for listRoutes method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {listRoutes} integration test with optional parameters.", dependsOnMethods = { "testListRoutesWithMandatoryParameters" })
   public void testListRoutesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listRoutes");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRoutes_optional.json");
      
      final String apiEndpoint = apiEndpointUrl + "/routes?date=" + connectorProperties.getProperty("routeDate");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").length(),
            apiRestResponse.getBody().getJSONObject("response").getJSONArray("routes").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(0)
            .getString("id"),
            esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(0)
            .getString("truck_identifier"), esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes")
            .getJSONObject(0).getString("truck_identifier"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(1)
            .getString("id"),
            esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(1).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes").getJSONObject(1)
            .getString("truck_identifier"), esbRestResponse.getBody().getJSONObject("response").getJSONArray("routes")
            .getJSONObject(1).getString("truck_identifier"));
      
   }
   
   /**
    * Method name: listRoutes 
    * Test scenario: Negative 
    * Reason to skip: There are no negative parameters in this method.
    */
   
   /**
    * Positive test case for updateRoute method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {updateRoute} integration test with mandatory parameters.", dependsOnMethods = { "testListRoutesWithOptionalParameters" })
   public void testUpdateRouteWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateRoute");
      final String apiEndpoint = apiEndpointUrl + "/routes/" + connectorProperties.getProperty("routeId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRoute_mandatory.json");
      
      RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertNotEquals(
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("route").getJSONArray("dispatches")
                  .length(),
            apiRestResponse2.getBody().getJSONObject("response").getJSONObject("route").getJSONArray("dispatches")
                  .length());
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "ok");
      Assert.assertEquals(connectorProperties.getProperty("routeDispatchesUpdated"),
            apiRestResponse2.getBody().getJSONObject("response").getJSONObject("route").getJSONArray("dispatches")
                  .getJSONObject(1).getString("identifier"));
      
   }
   
   /**
    * Positive test case for updateRoute method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {updateRoute} integration test with optional parameters.", dependsOnMethods = { "testListRoutesWithOptionalParameters" })
   public void testUpdateRouteWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateRoute");
      final String apiEndpoint = apiEndpointUrl + "/routes/" + connectorProperties.getProperty("routeIdOptional");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRoute_optional.json");
      
      RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      String startDateAPI =
            apiRestResponse2.getBody().getJSONObject("response").getJSONObject("route").getString("start_time");
      String[] startDateWithoutTimeAPI = startDateAPI.split("T");
      String endDateAPI =
            apiRestResponse2.getBody().getJSONObject("response").getJSONObject("route").getString("end_time");
      String[] endDateWithoutTimeAPI = endDateAPI.split("T");
      
      Assert.assertNotEquals(
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("route").getJSONArray("dispatches")
                  .length(),
            apiRestResponse2.getBody().getJSONObject("response").getJSONObject("route").getJSONArray("dispatches")
                  .length());
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "ok");
      Assert.assertEquals(connectorProperties.getProperty("routeDispatchesUpdated"),
            apiRestResponse2.getBody().getJSONObject("response").getJSONObject("route").getJSONArray("dispatches")
                  .getJSONObject(1).getString("identifier"));
      Assert.assertEquals(connectorProperties.getProperty("routeStartTimeUpdate"), startDateWithoutTimeAPI[0]);
      Assert.assertEquals(connectorProperties.getProperty("routeEndTimeUpdate"), endDateWithoutTimeAPI[0]);
      
   }
   
   /**
    * Negative test case for updateRoute method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "beetrack {updateRoute} integration test with negative case.", dependsOnMethods = { "testUpdateRouteWithMandatoryParameters" })
   public void testUpdateRouteWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateRoute");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRoute_negative.json");
      
      final String apiEndpoint = apiEndpointUrl + "/routes/invalid";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateRoute_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getString("response"),
            apiRestResponse.getBody().getString("response"));
      
   }
   
}
