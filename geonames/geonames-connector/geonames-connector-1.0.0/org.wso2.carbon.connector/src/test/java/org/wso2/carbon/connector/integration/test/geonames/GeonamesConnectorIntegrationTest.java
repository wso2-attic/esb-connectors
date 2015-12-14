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
package org.wso2.carbon.connector.integration.test.geonames;

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

public class GeonamesConnectorIntegrationTest extends ConnectorIntegrationTestBase {

   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

   private String apiUrl;

   private String username;

   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {

      init("geonames-connector-1.0.0");

      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");

      apiUrl = connectorProperties.getProperty("apiUrl");
      username = "?username=" + connectorProperties.getProperty("username");

   }

   /**
    * Positive test case for search method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {search} integration test with mandatory parameters.")
   public void testSearchWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:search");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_search_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String apiEndPoint = apiUrl + "/searchJSON" + username;

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      connectorProperties.setProperty("geonameId",esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("geonameId"));
      Assert.assertEquals(esbRestResponse.getBody().getString("totalResultsCount"), apiRestResponse.getBody()
            .getString("totalResultsCount"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("countryId"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("countryId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("countryName"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("countryName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("name"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("geonameId"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("geonameId"));
   }

   /**
    * Positive test case for search method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {search} integration test with optional parameters.")
   public void testSearchWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:search");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_search_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String query = connectorProperties.getProperty("query");
      String name = connectorProperties.getProperty("name");
      String adminCode1 = connectorProperties.getProperty("adminCode1");
      String country = connectorProperties.getProperty("contryCode");
      String nameStartsWith = connectorProperties.getProperty("nameStartsWith");

      String apiEndPoint = apiUrl + "/searchJSON" + username + "&q=" + query + "&name=" + name + "&adminCode1="
            + adminCode1 + "&country=" + country + "&name_startsWith=" + nameStartsWith;

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getString("totalResultsCount"), apiRestResponse.getBody()
            .getString("totalResultsCount"));
      Assert.assertTrue(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("name").contains(
            name));
      Assert.assertTrue(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("name")
            .startsWith(nameStartsWith));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("adminCode1"),
            adminCode1);
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("countryCode"),
            country);
   }

   /**
    * Negative test case for search method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {search} integration test with negative case.")
   public void testSearchWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:search");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_search_negative.json");

      String apiEndPoint = apiUrl + "/searchJSON" + username + "&featureClass=INVALID";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("status").getString("message"), esbRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("status").getString("value"), esbRestResponse
            .getBody().getJSONObject("status").getString("value"));

   }

   /**
    * Positive test case for getNearByPointsOfInterestOSM method with mandatory
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getNearByPointsOfInterestOSM} integration test with mandatory parameters.")
   public void testGetNearByPointsOfInterestOSMWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getNearByPointsOfInterestOSM");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getNearByPointsOfInterestOSM_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String lat = connectorProperties.getProperty("latitude");
      String lng = connectorProperties.getProperty("longitude");

      String apiEndPoint = apiUrl + "/findNearbyPOIsOSMJSON" + username + "&lat=" + lat + "&lng=" + lng;

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("typeName"),
            apiRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("typeName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("lng"),
            apiRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("lat"),
            apiRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("lat"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("typeClass"),
            apiRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("typeClass"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("distance"),
            apiRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("distance"));
   }

   /**
    * Positive test case for getNearByPointsOfInterestOSM method with optional
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getNearByPointsOfInterestOSM} integration test with optional parameters.")
   public void testGetNearByPointsOfInterestOSMWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getNearByPointsOfInterestOSM");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getNearByPointsOfInterestOSM_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String lat = connectorProperties.getProperty("latitude");
      String lng = connectorProperties.getProperty("longitude");
      String maxRows = connectorProperties.getProperty("maxRows");
      String radius = connectorProperties.getProperty("radius");

      String apiEndPoint = apiUrl + "/findNearbyPOIsOSMJSON" + username + "&lat=" + lat + "&lng=" + lng + "&maxRows="
            + maxRows + "&radius=" + radius;

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").length(), Integer.parseInt(maxRows));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("typeName"),
            apiRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("typeName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("lng"),
            apiRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("lat"),
            apiRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("lat"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("typeClass"),
            apiRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("typeClass"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("distance"),
            apiRestResponse.getBody().getJSONArray("poi").getJSONObject(0).getString("distance"));
   }

   /**
    * Negative test case for getNearByPointsOfInterestOSM method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getNearByPointsOfInterestOSM} integration test with negative case.")
   public void testGetNearByPointsOfInterestOSMWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getNearByPointsOfInterestOSM");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getNearByPointsOfInterestOSM_negative.json");

      String lat = connectorProperties.getProperty("latitude");
      String lng = connectorProperties.getProperty("longitude");

      String apiEndPoint = apiUrl + "/findNearbyPOIsOSMJSON" + username + "&lat=" + lat + "&lng=" + lng
            + "&radius=INVALID";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("status").getString("message"), esbRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("status").getString("value"), esbRestResponse
            .getBody().getJSONObject("status").getString("value"));

   }

   /**
    * Positive test case for listCitiesAndPlaceNames method with mandatory
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listCitiesAndPlaceNames} integration test with mandatory parameters.")
   public void testListCitiesAndPlaceNamesWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listCitiesAndPlaceNames");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listCitiesAndPlaceNames_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String north = connectorProperties.getProperty("north");
      String east = connectorProperties.getProperty("east");
      String south = connectorProperties.getProperty("south");
      String west = connectorProperties.getProperty("west");

      String apiEndPoint = apiUrl + "/citiesJSON" + username + "&north=" + north + "&south=" + south + "&west=" + west
            + "&east=" + east;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("toponymName"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("toponymName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("countrycode"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("countrycode"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fclName"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fclName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("lng"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("lat"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("lat"));

   }

   /**
    * Positive test case for listCitiesAndPlaceNames method with optional
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listCitiesAndPlaceNames} integration test with optional parameters.")
   public void testListCitiesAndPlaceNamesWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listCitiesAndPlaceNames");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listCitiesAndPlaceNames_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      String north = connectorProperties.getProperty("north");
      String east = connectorProperties.getProperty("east");
      String south = connectorProperties.getProperty("south");
      String west = connectorProperties.getProperty("west");
      String language = connectorProperties.getProperty("language");
      String maxRows = connectorProperties.getProperty("maxRows");

      String apiEndPoint = apiUrl + "/citiesJSON" + username + "&north=" + north + "&south=" + south + "&west=" + west
            + "&east=" + east + "&maxRows=" + maxRows + "&lang=" + language;

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").length(), Integer.parseInt(maxRows));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("toponymName"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("toponymName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("countrycode"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("countrycode"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fclName"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fclName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("lng"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("lat"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("lat"));
   }

   /**
    * Negative test case for listCitiesAndPlaceNames method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listCitiesAndPlaceNames} integration test with negative case.")
   public void testListCitiesAndPlaceNamesWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listCitiesAndPlaceNames");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listCitiesAndPlaceNames_negative.json");

      String north = connectorProperties.getProperty("north");
      String east = connectorProperties.getProperty("east");
      String south = connectorProperties.getProperty("south");
      String west = connectorProperties.getProperty("west");

      String apiEndPoint = apiUrl + "/citiesJSON" + username + "&north=" + north + "&south=" + south + "&west=" + west
            + "&east=" + east + "&maxRows=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("status").getString("message"), esbRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("status").getString("value"), esbRestResponse
            .getBody().getJSONObject("status").getString("value"));

   }

   /**
    * Positive test case for getTimezone method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getTimezone} integration test with mandatory parameters.")
   public void testGetTimezoneWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getTimezone");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getTimezone_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/timezoneJSON" + username + "&lat="
            + connectorProperties.getProperty("latitude") + "&lng=" + connectorProperties.getProperty("longitude");

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(connectorProperties.getProperty("latitude"), esbRestResponse.getBody().getString("lat"));
      Assert.assertEquals(connectorProperties.getProperty("longitude"), esbRestResponse.getBody().getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getString("lng"), apiRestResponse.getBody().getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getString("lat"), apiRestResponse.getBody().getString("lat"));
      Assert.assertEquals(esbRestResponse.getBody().getString("sunset"), apiRestResponse.getBody().getString("sunset"));
   }

   /**
    * Positive test case for getTimezone method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getTimezone} integration test with optional parameters.")
   public void testGetTimezoneWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getTimezone");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getTimezone_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/timezoneJSON" + username + "&lat="
            + connectorProperties.getProperty("latitude") + "&lng=" + connectorProperties.getProperty("longitude")
            + "&date=" + connectorProperties.getProperty("date") + "&radius="
            + connectorProperties.getProperty("radius");

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getString("lng"), apiRestResponse.getBody().getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getString("lat"), apiRestResponse.getBody().getString("lat"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("dates").getJSONObject(0).getString("date"),
            apiRestResponse.getBody().getJSONArray("dates").getJSONObject(0).getString("date"));
      Assert.assertEquals(esbRestResponse.getBody().getString("sunset"), apiRestResponse.getBody().getString("sunset"));
   }

   /**
    * Negative test case for getTimezone method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getTimezone} integration test with negative case.")
   public void testGetTimezoneWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getTimezone");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getTimezone_negative.json");

      final String apiEndPoint = apiUrl + "/timezoneJSON" + username + "&lat="
            + connectorProperties.getProperty("latitude") + "&lng=" + connectorProperties.getProperty("longitude")
            + "&date=INVALID";

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));
   }

   /**
    * Positive test case for getNeighbourhood method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getNeighbourhood} integration test with mandatory parameters.")
   public void testGetNeighbourhoodWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getNeighbourhood");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getNeighbourhood_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/neighbourhoodJSON" + username + "&lat="
            + connectorProperties.getProperty("latitude") + "&lng=" + connectorProperties.getProperty("longitude");

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("neighbourhood").getString("adminName2"),
            apiRestResponse.getBody().getJSONObject("neighbourhood").getString("adminName2"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("neighbourhood").getString("name"), apiRestResponse
            .getBody().getJSONObject("neighbourhood").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("neighbourhood").getString("countryCode"),
            apiRestResponse.getBody().getJSONObject("neighbourhood").getString("countryCode"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("neighbourhood").getString("city"), apiRestResponse
            .getBody().getJSONObject("neighbourhood").getString("city"));
   }

   /**
    * Method name: getNeighbourhood 
    * Test scenario: Optional 
    * Reason to skip: No optional parameter to filter the neighbourhood.
    */

   /**
    * Negative test case for getNeighbourhood method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getNeighbourhood} integration test with negative case.")
   public void testGetNeighbourhoodWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getNeighbourhood");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getNeighbourhood_negative.json");

      final String apiEndPoint = apiUrl + "/neighbourhoodJSON" + username + "&lat="
            + connectorProperties.getProperty("latitude") + "&lng=INVALID";

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));
   }

   /**
    * Method name: listNearbyPostalCodes 
    * Test scenario: Mandatory 
    * Reason to skip: No mandatory parameters to retrieve postal codes.
    */

   /**
    * Positive test case for listNearbyPostalCodes method with optional
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listNearbyPostalCodes} integration test with optional parameters.")
   public void testListNearbyPostalCodesWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listNearbyPostalCodes");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listNearbyPostalCodes_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/findNearbyPostalCodesJSON" + username + "&postalcode="
            + connectorProperties.getProperty("postalCode") + "&maxRows=" + connectorProperties.getProperty("maxRows")
            + "&style=MEDIUM" + "&lng=" + connectorProperties.getProperty("longitude") + "&lat="
            + connectorProperties.getProperty("latitude");

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(Integer.parseInt(connectorProperties.getProperty("maxRows")), esbRestResponse.getBody()
            .getJSONArray("postalCodes").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalCodes").length(), apiRestResponse.getBody()
            .getJSONArray("postalCodes").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalCodes").getJSONObject(0).getString(
            "countryCode"), apiRestResponse.getBody().getJSONArray("postalCodes").getJSONObject(0).getString(
            "countryCode"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalCodes").getJSONObject(0).getString("distance"),
            apiRestResponse.getBody().getJSONArray("postalCodes").getJSONObject(0).getString("distance"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalCodes").getJSONObject(0)
            .getString("postalCode"), apiRestResponse.getBody().getJSONArray("postalCodes").getJSONObject(0).getString(
            "postalCode"));
   }

   /**
    * Negative test case for listNearbyPostalCodes method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listNearbyPostalCodes} integration test with negative case.")
   public void testListNearbyPostalCodesWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listNearbyPostalCodes");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listNearbyPostalCodes_negative.json");

      final String apiEndPoint = apiUrl + "/findNearbyPostalCodesJSON" + username + "&postalcode=INVALID";

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));
   }

   /**
    * Method name: listPlacesForPostalCode 
    * Test scenario: Mandatory 
    * Reason to skip: No parameter to assert while running the mandatory case.
    */

   /**
    * Positive test case for listPlacesForPostalCode method with optional
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listPlacesForPostalCode} integration test with optional parameters.")
   public void testListPlacesForPostalCodesWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listPlacesForPostalCode");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listPlacesForPostalCodes_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      final String apiEndPoint = apiUrl + "/postalCodeLookupJSON" + username + "&postalcode="
            + connectorProperties.getProperty("postalCode") + "&maxRows=" + connectorProperties.getProperty("maxRows")
            + "&charset=UTF-8";

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(Integer.parseInt(connectorProperties.getProperty("maxRows")), esbRestResponse.getBody()
            .getJSONArray("postalcodes").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalcodes").length(), apiRestResponse.getBody()
            .getJSONArray("postalcodes").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalcodes").getJSONObject(0).getString(
            "countryCode"), apiRestResponse.getBody().getJSONArray("postalcodes").getJSONObject(0).getString(
            "countryCode"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalcodes").getJSONObject(0)
            .getString("adminName1"), apiRestResponse.getBody().getJSONArray("postalcodes").getJSONObject(0).getString(
            "adminName1"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalcodes").getJSONObject(0)
            .getString("postalcode"), apiRestResponse.getBody().getJSONArray("postalcodes").getJSONObject(0).getString(
            "postalcode"));
   }

   /**
    * Negative test case for listPlacesForPostalCode method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listPlacesForPostalCode} integration test with negative case.")
   public void testListPlacesForPostalCodesWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listPlacesForPostalCode");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listPlacesForPostalCodes_negative.json");

      final String apiEndPoint = apiUrl + "/postalCodeLookupJSON" + username + "&postalcode="
            + connectorProperties.getProperty("postalCode") + "&maxRows=INVALID";

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));
   }

   /**
    * Method name: postalCodeSearch 
    * Test scenario: Mandatory 
    * Reason to skip: No mandatory parameters to retrieve postal codes.
    */

   /**
    * Positive test case for postalCodeSearch method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {postalCodeSearch} integration test with optional parameters.")
   public void testPostalCodeSearchWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:postalCodeSearch");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_postalCodeSearch_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/postalCodeSearchJSON" + username + "&postalcode_startsWith="
            + connectorProperties.getProperty("postalCodeStartsWith") + "&maxRows="
            + connectorProperties.getProperty("maxRows") + "&operator=AND&isReduced=true&country="
            + connectorProperties.getProperty("contryCode");

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(Integer.parseInt(connectorProperties.getProperty("maxRows")), esbRestResponse.getBody()
            .getJSONArray("postalCodes").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalCodes").length(), apiRestResponse.getBody()
            .getJSONArray("postalCodes").length());
      Assert.assertEquals(connectorProperties.getProperty("contryCode"), esbRestResponse.getBody().getJSONArray(
            "postalCodes").getJSONObject(0).getString("countryCode"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalCodes").getJSONObject(0)
            .getString("adminName1"), apiRestResponse.getBody().getJSONArray("postalCodes").getJSONObject(0).getString(
            "adminName1"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("postalCodes").getJSONObject(0).getString("lng"),
            apiRestResponse.getBody().getJSONArray("postalCodes").getJSONObject(0).getString("lng"));
   }

   /**
    * Negative test case for postalCodeSearch method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {postalCodeSearch} integration test with negative case.")
   public void testPostalCodeSearchWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:postalCodeSearch");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listPlacesForPostalCodes_negative.json");

      final String apiEndPoint = apiUrl + "/postalCodeLookupJSON" + username + "&postalcode="
            + connectorProperties.getProperty("postalCode") + "&maxRows=INVALID";

      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));
   }

   /**
    * Positive test case for getChildren method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = {"testListHierarchyOfPlaceNamesWithMandatoryParameters"}, description = "geonames {getChildren} integration test with mandatory parameters.")
   public void testGetChildrenWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getChildren");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getChildren_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/childrenJSON" + username + "&geonameId="
            + connectorProperties.getProperty("hierarchyGeonameId");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("name"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fclName"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fclName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("numberOfChildren"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("numberOfChildren"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("geonameId"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("geonameId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("population"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("population"));

   }

   /**
    * Positive test case for getChildren method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = {"testListHierarchyOfPlaceNamesWithMandatoryParameters"}, description = "geonames {getChildren} integration test with optional parameters.")
   public void testGetChildrenWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getChildren");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getChildren_optional.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/childrenJSON" + username + "&geonameId="
            + connectorProperties.getProperty("hierarchyGeonameId") + "&maxRows=" + connectorProperties.getProperty("maxRows")
            + "&hierarchy=Puglia";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("name"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fclName"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fclName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("numberOfChildren"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("numberOfChildren"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("geonameId"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("geonameId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("population"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("population"));

   }

   /**
    * Negative test case for getChildren method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = {"testListHierarchyOfPlaceNamesWithMandatoryParameters"}, description = "geonames {getChildren} integration test with negative case.")
   public void testGetChildrenWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getChildren");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getChildren_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 503);

      final String apiEndPoint = apiUrl + "/childrenJSON" + username + "&geonameId=INVALID";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));

   }

   /**
    * Positive test case for getWeatherStationWithObservation method with
    * mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getWeatherStationWithObservation} integration test with mandatory parameters.")
   public void testGetWeatherStationWithObservationWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getWeatherStationWithObservation");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getWeatherStationWithObservation_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/findNearByWeatherJSON" + username + "&lat="
            + connectorProperties.getProperty("latitude") + "&lng=" + connectorProperties.getProperty("longitude");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("weatherObservation").getString("countryCode"),
            apiRestResponse.getBody().getJSONObject("weatherObservation").getString("countryCode"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("weatherObservation").getString("lat"),
            apiRestResponse.getBody().getJSONObject("weatherObservation").getString("lat"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("weatherObservation").getString("lng"),
            apiRestResponse.getBody().getJSONObject("weatherObservation").getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("weatherObservation").getString("stationName"),
            apiRestResponse.getBody().getJSONObject("weatherObservation").getString("stationName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("weatherObservation").getString("ICAO"),
            apiRestResponse.getBody().getJSONObject("weatherObservation").getString("ICAO"));

   }

   /**
    * Positive test case for getWeatherStationWithObservation method with
    * optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getWeatherStationWithObservation} integration test with optional parameters.")
   public void testGetWeatherStationWithObservationWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getWeatherStationWithObservation");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getWeatherStationWithObservation_optional.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/findNearByWeatherJSON" + username + "&lat="
            + connectorProperties.getProperty("latitude") + "&lng=" + connectorProperties.getProperty("longitude")
            + "&radius=" + connectorProperties.getProperty("radius");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("weatherObservation").getString("countryCode"),
            apiRestResponse.getBody().getJSONObject("weatherObservation").getString("countryCode"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("weatherObservation").getString("lat"),
            apiRestResponse.getBody().getJSONObject("weatherObservation").getString("lat"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("weatherObservation").getString("lng"),
            apiRestResponse.getBody().getJSONObject("weatherObservation").getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("weatherObservation").getString("stationName"),
            apiRestResponse.getBody().getJSONObject("weatherObservation").getString("stationName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("weatherObservation").getString("ICAO"),
            apiRestResponse.getBody().getJSONObject("weatherObservation").getString("ICAO"));

   }

   /**
    * Negative test case for getWeatherStationWithObservation method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getWeatherStationWithObservation} integration test with negative case.")
   public void testGetWeatherStationWithObservationWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getWeatherStationWithObservation");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getWeatherStationWithObservation_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/findNearByWeatherJSON" + username + "&lat="
            + connectorProperties.getProperty("latitude") + "&lng=INVALID";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));

   }

   /**
    * Positive test case for listHierarchyOfPlaceNames method with mandatory
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = {"testSearchWithMandatoryParameters"}, description = "geonames {listHierarchyOfPlaceNames} integration test with mandatory parameters.")
   public void testListHierarchyOfPlaceNamesWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listHierarchyOfPlaceNames");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listHierarchyOfPlaceNames_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/hierarchyJSON" + username + "&geonameId="
            + connectorProperties.getProperty("geonameId");
      connectorProperties.setProperty("hierarchyGeonameId", esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("geonameId"));
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("toponymName"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("toponymName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("name"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fcodeName"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fcodeName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fcode"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("fcode"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("population"),
            apiRestResponse.getBody().getJSONArray("geonames").getJSONObject(0).getString("population"));
   }

   /**
    * Negative test case for listHierarchyOfPlaceNames method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = {"testSearchWithMandatoryParameters"} , description = "geonames {listHierarchyOfPlaceNames} integration test with negative case.")
   public void testListHierarchyOfPlaceNamesWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listHierarchyOfPlaceNames");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listHierarchyOfPlaceNames_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 503);

      final String apiEndPoint = apiUrl + "/hierarchyJSON" + username + "&geonameId=INVALID";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));

   }

   /**
    * Positive test case for listRecentEarthquakes method with mandatory
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listRecentEarthquakes} integration test with mandatory parameters.")
   public void testListRecentEarthquakesWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listRecentEarthquakes");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listRecentEarthquakes_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/earthquakesJSON" + username + "&north="
            + connectorProperties.getProperty("north") + "&south=" + connectorProperties.getProperty("south")
            + "&east=" + connectorProperties.getProperty("east") + "&west=" + connectorProperties.getProperty("west");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("depth"),
            apiRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("depth"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("lng"),
            apiRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("src"),
            apiRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("src"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("magnitude"),
            apiRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("magnitude"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("lat"),
            apiRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("lat"));
   }

   /**
    * Positive test case for listRecentEarthquakes method with optional
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listRecentEarthquakes} integration test with optional parameters.")
   public void testListRecentEarthquakesWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listRecentEarthquakes");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listrecentearthquakes_optional.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/earthquakesJSON" + username + "&north="
            + connectorProperties.getProperty("north") + "&south=" + connectorProperties.getProperty("south")
            + "&east=" + connectorProperties.getProperty("east") + "&west=" + connectorProperties.getProperty("west")
            + "&date=" + connectorProperties.getProperty("date") + "&minMagnitude=.8&maxRows="
            + connectorProperties.getProperty("maxRows");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("depth"),
            apiRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("depth"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("lng"),
            apiRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("lng"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("src"),
            apiRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("src"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("magnitude"),
            apiRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("magnitude"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("lat"),
            apiRestResponse.getBody().getJSONArray("earthquakes").getJSONObject(0).getString("lat"));

   }

   /**
    * Negative test case for listRecentEarthquakes method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listRecentEarthquakes} integration test with negative case.")
   public void testListRecentEarthquakesWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listRecentEarthquakes");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listrecentearthquakes_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 503);

      final String apiEndPoint = apiUrl + "/earthquakesJSON" + username + "&north="
            + connectorProperties.getProperty("north") + "&south=" + connectorProperties.getProperty("south")
            + "&east=" + connectorProperties.getProperty("east") + "&west=" + connectorProperties.getProperty("west")
            + "&date=INVALID";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));

   }

   /**
    * Positive test case for listWeatherStations method with mandatory
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listWeatherStations} integration test with mandatory parameters.")
   public void testListWeatherStationsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listWeatherStations");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listWeatherStations_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/weatherJSON" + username + "&north="
            + connectorProperties.getProperty("north") + "&south=" + connectorProperties.getProperty("south")
            + "&east=" + connectorProperties.getProperty("east") + "&west=" + connectorProperties.getProperty("west");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "ICAO"), apiRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString("ICAO"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "observation"), apiRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "observation"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "temperature"), apiRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "temperature"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "stationName"), apiRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "stationName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "lat"), apiRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString("lat"));
   }

   /**
    * Positive test case for listWeatherStations method with optional
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listWeatherStations} integration test with optional parameters.")
   public void testListWeatherStationsWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listWeatherStations");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listWeatherStations_optional.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/weatherJSON" + username + "&north="
            + connectorProperties.getProperty("north") + "&south=" + connectorProperties.getProperty("south")
            + "&east=" + connectorProperties.getProperty("east") + "&west=" + connectorProperties.getProperty("west")
            + "&maxRows=" + connectorProperties.getProperty("maxRows");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "ICAO"), apiRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString("ICAO"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "observation"), apiRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "observation"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "temperature"), apiRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "temperature"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "stationName"), apiRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "stationName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString(
            "lat"), apiRestResponse.getBody().getJSONArray("weatherObservations").getJSONObject(0).getString("lat"));

   }

   /**
    * Negative test case for listWeatherStations method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {listWeatherStations} integration test with negative case.")
   public void testListWeatherStationsWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listWeatherStations");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listWeatherStations_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/earthquakesJSON" + username + "&north="
            + connectorProperties.getProperty("north") + "&south=" + connectorProperties.getProperty("south")
            + "&east=" + connectorProperties.getProperty("east") + "&west=" + connectorProperties.getProperty("west")
            + "&maxRows=INVALID";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));

   }

   /**
    * Positive test case for getNearestIntersectionOSM method with mandatory
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getNearestIntersectionOSM} integration test with mandatory parameters.")
   public void testGetNearestIntersectionOSMWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getNearestIntersectionOSM");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getNearestIntersectionOSM_mandatory.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/findNearestIntersectionOSMJSON" + username + "&lng="
            + connectorProperties.getProperty("longitude") + "&lat=" + connectorProperties.getProperty("latitude");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("intersection").getString("distance"),
            apiRestResponse.getBody().getJSONObject("intersection").getString("distance"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("intersection").getString("street1"), apiRestResponse
            .getBody().getJSONObject("intersection").getString("street1"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("intersection").getString("highway2"),
            apiRestResponse.getBody().getJSONObject("intersection").getString("highway2"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("intersection").getString("highway1"),
            apiRestResponse.getBody().getJSONObject("intersection").getString("highway1"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("intersection").getString("lng"), apiRestResponse
            .getBody().getJSONObject("intersection").getString("lng"));

   }

   /**
    * Positive test case for getNearestIntersectionOSM method with optional
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getNearestIntersectionOSM} integration test with optional parameters.")
   public void testGetNearestIntersectionOSMWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getNearestIntersectionOSM");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getNearestIntersectionOSM_optional.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

      final String apiEndPoint = apiUrl + "/findNearestIntersectionOSMJSON" + username + "&lng="
            + connectorProperties.getProperty("longitude") + "&lat=" + connectorProperties.getProperty("latitude")
            + "&maxRows=" + connectorProperties.getProperty("maxRows") + "&radius="
            + connectorProperties.getProperty("radius");

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("intersection").getJSONObject(0).getString("distance"),
            apiRestResponse.getBody().getJSONArray("intersection").getJSONObject(0).getString("distance"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("intersection").getJSONObject(0).getString("street1"),
            apiRestResponse.getBody().getJSONArray("intersection").getJSONObject(0).getString("street1"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("intersection").getJSONObject(0).getString("highway2"),
            apiRestResponse.getBody().getJSONArray("intersection").getJSONObject(0).getString("highway2"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("intersection").getJSONObject(0).getString("highway1"),
            apiRestResponse.getBody().getJSONArray("intersection").getJSONObject(0).getString("highway1"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("intersection").getJSONObject(0).getString("lng"),
            apiRestResponse.getBody().getJSONArray("intersection").getJSONObject(0).getString("lng"));

   }

   /**
    * Negative test case for getNearestIntersectionOSM method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "geonames {getNearestIntersectionOSM} integration test with negative case.")
   public void testGetNearestIntersectionOSMWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getNearestIntersectionOSM");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getNearestIntersectionOSM_negative.json");

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 503);

      final String apiEndPoint = apiUrl + "/findNearestIntersectionOSMJSON" + username + "&lng="
            + connectorProperties.getProperty("longitude") + "&lat=INVALID";

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 503);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("message"), apiRestResponse
            .getBody().getJSONObject("status").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("value"), apiRestResponse
            .getBody().getJSONObject("status").getString("value"));

   }

}
