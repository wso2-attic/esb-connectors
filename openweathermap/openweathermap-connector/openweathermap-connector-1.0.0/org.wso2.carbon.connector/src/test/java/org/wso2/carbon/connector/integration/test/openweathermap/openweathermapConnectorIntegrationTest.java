package org.wso2.carbon.connector.integration.test.openweathermap;

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
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

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

public class openweathermapConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("openweathermap-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
    }

    /**
     * Positive test case for getCurrentWeatherForOneLocByCityID method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForOneLocByCityID} integration test with mandatory parameters.")
    public void testGetCurrentWeatherForOneLocByCityIDWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForOneLocByCityID");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForOneLocByCityIDMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/weather/?id=" + connectorProperties.getProperty("cityID") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
    }

    /**
     * Negative test case for getCurrentWeatherForOneLocByCityID method.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForOneLocByCityID} integration test with negative cases.")
    public void testGetCurrentWeatherForOneLocByCityIDWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForOneLocByCityID");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForOneLocByCityIDNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/weather/?id=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }

    /**
     * Positive test case for getCurrentWeatherForOneLocByCityName method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForOneLocByCityName} integration test with mandatory parameters.")
    public void testGetCurrentWeatherForOneLocByCityNameWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForOneLocByCityName");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForOneLocByCityNameMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/weather/?q=" +
                connectorProperties.getProperty("cityName") + "," + connectorProperties.getProperty("countryCode") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("weather"), apiRestResponse.getBody().getString("weather"));
    }

    /**
     * Negative test case for getCurrentWeatherForOneLocByCityName method.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForOneLocByCityName} integration test with negative cases.")
    public void testGetCurrentWeatherForOneLocByCityNameWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForOneLocByCityName");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForOneLocByCityNameNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/weather/?q=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));

    }

    /**
     * Positive test case for getCurrentWeatherForOneLocByGeoCoordinates method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForOneLocByGeoCoordinates} integration test with mandatory parameters.")
    public void testGetCurrentWeatherForOneLocByGeoCoordinatesWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForOneLocByGeoCoordinates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForOneLocByGeoCoorMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/weather/?lat=" +
                connectorProperties.getProperty("lat") + "&lon=" + connectorProperties.getProperty("lon") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
    }

    /**
     * Negative test case for getCurrentWeatherForOneLocByGeoCoordinates method.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForOneLocByGeoCoordinates} integration test with negative cases.")
    public void testGetCurrentWeatherForOneLocByGeoCoordinatesWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForOneLocByGeoCoordinates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForOneLocByGeoCoorNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/weather/?lat= " + "&lon= " + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }

    /**
     * Positive test case for getCurrentWeatherForOneLocByZipCode method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForOneLocByZipCode} integration test with mandatory parameters.")
    public void testGetCurrentWeatherForOneLocByZipCodeWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForOneLocByZipCode");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForOneLocByZipCodeMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/weather/?zip=" +
                connectorProperties.getProperty("zipCode") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }

    /**
     * Positive test case for getCurrentWeatherForOneLocByZipCode method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForOneLocByZipCode} integration test with mandatory parameters.")
    public void testGetCurrentWeatherForOneLocByZipCodeWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForOneLocByZipCode");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForOneLocByZipCodeOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/weather/?zip=" +
                connectorProperties.getProperty("zipCode") + "," + connectorProperties.getProperty("countryCode") +
                "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("weather").getJSONObject(0).getString("main"),
                apiRestResponse.getBody().getJSONArray("weather").getJSONObject(0).getString("main"));
    }

    /**
     * Negative test case for getCurrentWeatherForOneLocByZipCode method.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForOneLocByZipCode} integration test with negative cases.")
    public void testGetCurrentWeatherForOneLocByZipCodeWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForOneLocByZipCode");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForOneLocByZipCodeNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/weather/?zip=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }

    /**
     * Positive test case for getCurrentWeatherForSeveralCitiesWithinCycle method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForSeveralCitiesWithinCycle} integration test with mandatory parameters.")
    public void testGetCurrentWeatherForSeveralCitiesWithinCycleWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForSeveralCitiesWithinCycle");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForSeveralCitiesWithinCycleMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/find?lat=" +
                connectorProperties.getProperty("lat") + "&lon=" + connectorProperties.getProperty("lon") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString("message"));
    }

    /**
     * Positive test case for getCurrentWeatherForSeveralCitiesWithinCycle method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForSeveralCitiesWithinCycle} integration test with Optional parameters.")
    public void testGetCurrentWeatherForSeveralCitiesWithinCycleWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForSeveralCitiesWithinCycle");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForSeveralCitiesWithinCycleOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/find?lat=" +
                connectorProperties.getProperty("lat") + "&lon=" + connectorProperties.getProperty("lon") + "&cnt=" + connectorProperties.getProperty("cnt")
                + "&cluster=" + connectorProperties.getProperty("cluster") + "&lang=" + connectorProperties.getProperty("lang") + "&callback=" +
                connectorProperties.getProperty("callback") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody().getString("output"));
    }

    /*
     * Negative test case for getCurrentWeatherForSeveralCitiesWithinCycle method.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForSeveralCitiesWithinCycle} integration test with negative cases.")
    public void testGetCurrentWeatherForSeveralCitiesWithinCycleWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForSeveralCitiesWithinCycle");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForSeveralCitiesWithinCycleNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/find?lat= " + "&lon= " + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }

    /**
     * Positive test case for getCurrentWeatherForSeveralCitiesWithinRectangle method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForSeveralCitiesWithinRectangle} integration test with mandatory parameters.")
    public void testGetCurrentWeatherForSeveralCitiesWithinRectangleWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForSeveralCitiesWithinRectangle");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForSeveralCitiesWithinRectangleMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/box/city?bbox=" +
                connectorProperties.getProperty("bbox") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getString("weather"),
                apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getString("weather"));
    }

    /**
     * Positive test case for getCurrentWeatherForSeveralCitiesWithinRectangle method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForSeveralCitiesWithinRectangle} integration test with Optional parameters.")
    public void testGetCurrentWeatherForSeveralCitiesWithinRectangleWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForSeveralCitiesWithinRectangle");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForSeveralCitiesWithinRectangleOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/box/city?bbox=" +
                connectorProperties.getProperty("bbox") + "&cluster=" + connectorProperties.getProperty("cluster")
                + "&lang=" + connectorProperties.getProperty("lang") + "&callback=" + connectorProperties.getProperty("callback")
                + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody().getString("output"));
    }

    /*
    * Negative test case for getCurrentWeatherForSeveralCitiesWithinRectangle method.
    */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForSeveralCitiesWithinRectangle} integration test with negative cases.")
    public void testGetCurrentWeatherForSeveralCitiesWithinRectangleWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForSeveralCitiesWithinRectangle");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForSeveralCitiesWithinRectangleNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/box/city?bbox=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }

    /**
     * Positive test case for getCurrentWeatherForSeveralCityIDs method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForSeveralCityIDs} integration test with mandatory parameters.")
    public void testCetCurrentWeatherForSeveralCityIDsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForSeveralCityIDs");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForSeveralCityIDsMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/group?id=" +
                connectorProperties.getProperty("cityIDs") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getString("id"));
    }

    /**
     * Positive test case for getCurrentWeatherForSeveralCityIDs method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForSeveralCityIDs} integration test with Optional parameters.")
    public void testGetCurrentWeatherForSeveralCityIDsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForSeveralCityIDs");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForSeveralCityIDsOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/group?id=" +
                connectorProperties.getProperty("cityIDs") + "&units=" + connectorProperties.getProperty("units") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getString("id"));
    }

    /*
     * Negative test case for getCurrentWeatherForSeveralCityIDs method.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherForSeveralCityIDs} integration test with negative cases.")
    public void testGetCurrentWeatherForSeveralCityIDsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherForSeveralCityIDs");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherForSeveralCityIDsNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/group?id=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cnt"), apiRestResponse.getBody().getString("cnt"));
    }


    /**
     * Positive test case for search method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {search} integration test with mandatory parameters.")
    public void testSearchWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/find?q=" +
                connectorProperties.getProperty("cityName") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString("message"));
    }


    /**
     * Positive test case for search method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {search} integration test with Optional parameters.")
    public void testSearchWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/find?q=" +
                connectorProperties.getProperty("cityName") + "&type=" + connectorProperties.getProperty("type") + "&mode=" +
                connectorProperties.getProperty("mode") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString("message"));
    }


    /*
    * Negative test case for search method.
    */
    @Test(priority = 1, description = "openweathermap {search} integration test with negative cases.")
    public void testSearchWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/find?q=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }


    /**
     * Positive test case for searchWeatherForcastFor5daysByCityID method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor5daysByCityID} integration test with mandatory parameters.")
    public void testSearchWeatherForcastFor5daysByCityIDWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor5daysByCityID");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor5daysByCityIDMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/forecast/?id=" +
                connectorProperties.getProperty("cityID") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("city").getString("id"), apiRestResponse.getBody().getJSONObject("city").getString("id"));
    }


    /**
     * Negative test case for searchWeatherForcastFor5daysByCityID method.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor5daysByCityID} integration test with negative cases.")
    public void testSearchWeatherForcastFor5daysByCityIDWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor5daysByCityID");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor5daysByCityIDNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/forecast/?id=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }


    /**
     * Positive test case for searchWeatherForcastFor5daysByCityName method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor5daysByCityName} integration test with mandatory parameters.")
    public void testSearchWeatherForcastFor5daysByCityNameWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor5daysByCityName");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor5daysByCityNameMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/forecast/?q="
                + connectorProperties.getProperty("cityName") + "," + connectorProperties.getProperty("countryCode") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("city").getString("name"), apiRestResponse.getBody().getJSONObject("city").getString("name"));
    }


    /**
     * Negative test case for searchWeatherForcastFor5daysByCityName method.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor5daysByCityName} integration test with negative cases.")
    public void testSearchWeatherForcastFor5daysByCityNameWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor5daysByCityName");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor5daysByCityNameNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/forecast/?q=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }


    /**
     * Positive test case for searchWeatherForcastFor5daysByGeoCoordinates method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor5daysByGeoCoordinates} integration test with mandatory parameters.")
    public void testSearchWeatherForcastFor5daysByGeoCoordinatesWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor5daysByGeoCoordinates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor5daysByGeoCoorMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/forecast/?lat=" +
                connectorProperties.getProperty("lat") + "&lon=" + connectorProperties.getProperty("lon") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("city").getString("id"), apiRestResponse.getBody().getJSONObject("city").getString("id"));
    }


    /**
     * Negative test case for searchWeatherForcastFor5daysByGeoCoordinates method.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor5daysByGeoCoordinates} integration test with negative cases.")
    public void testSearchWeatherForcastFor5daysByGeoCoordinatesWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor5daysByGeoCoordinates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor5daysByGeoCoorNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/forecast/?lat= " + "&lon= " + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }


    /**
     * Positive test case for searchWeatherForcastFor16daysByCityID method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor16daysByCityID} integration test with mandatory parameters.")
    public void testSearchWeatherForcastFor16daysByCityIDWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor16daysByCityID");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor16daysByCityIDMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/forecast/daily?id=" +
                connectorProperties.getProperty("cityID") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("city").getString("id"), apiRestResponse.getBody().getJSONObject("city").getString("id"));
    }


    /**
     * Negative test case for searchWeatherForcastFor16daysByCityID method.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor16daysByCityID} integration test with negative cases.")
    public void testSearchWeatherForcastFor16daysByCityIDWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor16daysByCityID");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor16daysByCityIDNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/forecast/daily?id=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }


    /**
     * Positive test case for searchWeatherForcastFor16daysByCityName method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor16daysByCityName} integration test with mandatory parameters.")
    public void testSearchWeatherForcastFor16daysByCityNameWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor16daysByCityName");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor16daysByCityNameMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/forecast/daily?q=" +
                connectorProperties.getProperty("cityName") + "," + connectorProperties.getProperty("countryCode") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("city").getString("id"), apiRestResponse.getBody().getJSONObject("city").getString("id"));
    }


    /**
     * Negative test case for searchWeatherForcastFor16daysByCityName method.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor16daysByCityName} integration test with negative cases.")
    public void testSearchWeatherForcastFor16daysByCityNameWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor16daysByCityName");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor16daysByCityNameNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/forecast/daily?q=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }


    /**
     * Positive test case for searchWeatherForcastFor16daysByGeoCoordinates method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor16daysByGeoCoordinates} integration test with mandatory parameters.")
    public void testSearchWeatherForcastFor16daysByGeoCoordinatesWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor16daysByGeoCoordinates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor16daysByGeoCoorMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/forecast/daily/?lat=" +
                connectorProperties.getProperty("lat") + "&lon=" + connectorProperties.getProperty("lon") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("city").getString("id"), apiRestResponse.getBody().getJSONObject("city").getString("id"));
    }


    /**
     * Positive test case for searchWeatherForcastFor16daysByGeoCoordinates method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor16daysByGeoCoordinates} integration test with optional parameters.")
    public void testSearchWeatherForcastFor16daysByGeoCoordinatesWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor16daysByGeoCoordinates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor16daysByGeoCoorOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/forecast/?lat=" +
                connectorProperties.getProperty("lat") + "&lon=" + connectorProperties.getProperty("lon") + "&cnt=" +
                connectorProperties.getProperty("count") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("city").getString("id"), apiRestResponse.getBody().getJSONObject("city").getString("id"));
    }


    /**
     * Negative test case for searchWeatherForcastFor16daysByGeoCoordinates method.
     */
    @Test(priority = 1, description = "openweathermap {searchWeatherForcastFor16daysByGeoCoordinates} integration test with negative cases.")
    public void testSearchWeatherForcastFor16daysByGeoCoordinatesWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:searchWeatherForcastFor16daysByGeoCoordinates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchWeatherForcastFor16daysByGeoCoorNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/forecast/?lat= " + "&lon= " + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }

    /**
     * Positive test case for getHoulryHistoricalDataByCityID method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getHoulryHistoricalDataByCityID} integration test with mandatory parameters.")
    public void testGetHoulryHistoricalDataByCityIDWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHoulryHistoricalDataByCityID");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHoulryHistoricalDataByCityIDMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/history/city?id=" +
                connectorProperties.getProperty("cityID") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("city_id"), apiRestResponse.getBody().getString("city_id"));
    }


    /**
     * Positive test case for getHoulryHistoricalDataByCityID method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {getHoulryHistoricalDataByCityID} integration test with Optional parameters.")
    public void testGetHoulryHistoricalDataByCityIDWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHoulryHistoricalDataByCityID");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHoulryHistoricalDataByCityIDOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/history/city?id=" +
                connectorProperties.getProperty("cityID") + "&type=" + connectorProperties.getProperty("types") + "&start" + connectorProperties.getProperty("start") +
                "&end" + connectorProperties.getProperty("end") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("city_id"), apiRestResponse.getBody().getString("city_id"));
    }


    /**
     * Negative test case for getHoulryHistoricalDataByCityID method.
     */
    @Test(priority = 1, description = "openweathermap {getHoulryHistoricalDataByCityID} integration test with negative cases.")
    public void testGetHoulryHistoricalDataByCityIDWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHoulryHistoricalDataByCityID");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHoulryHistoricalDataByCityIDNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/weather/?id=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }

    /**
     * Positive test case for getHoulryHistoricalDataByCityName method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getHoulryHistoricalDataByCityName} integration test with mandatory parameters.")
    public void testGetHoulryHistoricalDataByCityNameWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHoulryHistoricalDataByCityName");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHoulryHistoricalDataByCityNameMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/history/city?q=" +
                connectorProperties.getProperty("cityName") + "," + connectorProperties.getProperty("countryCode") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("city_id"), apiRestResponse.getBody().getString("city_id"));
    }


    /**
     * Positive test case for getHoulryHistoricalDataByCityName method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {getHoulryHistoricalDataByCityName} integration test with Optional parameters.")
    public void testGetHoulryHistoricalDataByCityNameWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHoulryHistoricalDataByCityName");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHoulryHistoricalDataByCityNameOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/history/city?q=" +
                connectorProperties.getProperty("cityName") + "," + connectorProperties.getProperty("countryCode") + "&type=" + connectorProperties.getProperty("types") + "&start" +
                connectorProperties.getProperty("start") + "&end" + connectorProperties.getProperty("end") + "&cnt=" +
                connectorProperties.getProperty("cnt") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("city_id"), apiRestResponse.getBody().getString("city_id"));
    }


    /**
     * Negative test case for getHoulryHistoricalDataByCityName method.
     */
    @Test(priority = 1, description = "openweathermap {getHoulryHistoricalDataByCityID} integration test with negative cases.")
    public void testGetHoulryHistoricalDataByCityNameWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHoulryHistoricalDataByCityName");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHoulryHistoricalDataByCityNameNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/weather/?q=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }


    /**
     * Positive test case for getHoulryHistoricalDataByGeoCoordinates method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getHoulryHistoricalDataByGeoCoordinates} integration test with mandatory parameters.")
    public void testGetHoulryHistoricalDataByGeoCoordinatesWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHoulryHistoricalDataByGeoCoordinates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHoulryHistoricalDataByGeoCoorMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/history/city?lat=" +
                connectorProperties.getProperty("lat") + "&lon=" + connectorProperties.getProperty("lon") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("city_id"), apiRestResponse.getBody().getString("city_id"));
    }

    /**
     * Positive test case for getHoulryHistoricalDataByGeoCoordinates method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {getHoulryHistoricalDataByGeoCoordinates} integration test with Optional parameters.")
    public void testGetHoulryHistoricalDataByGeoCoordinatesWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHoulryHistoricalDataByGeoCoordinates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHoulryHistoricalDataByGeoCoorOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/history/city?lat=" +
                connectorProperties.getProperty("lat") + "&lon=" + connectorProperties.getProperty("lon") + "&type=" + connectorProperties.getProperty("types") +
                "&start" + connectorProperties.getProperty("start") + "&end" + connectorProperties.getProperty("end") + "&cnt=" +
                connectorProperties.getProperty("cnt") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("city_id"), apiRestResponse.getBody().getString("city_id"));
    }


    /**
     * Negative test case for getHoulryHistoricalDataByGeoCoordinates method.
     */
    @Test(priority = 1, description = "openweathermap {getHoulryHistoricalDataByGeoCoordinates} integration test with negative cases.")
    public void testGetHoulryHistoricalDataByGeoCoordinatesWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHoulryHistoricalDataByGeoCoordinates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHoulryHistoricalDataByGeoCoorNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/history/city?lat= " + "&lon= " + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }


    /**
     * Positive test case for getHistoricalDataFromWeatherStation method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getHistoricalDataFromWeatherStation} integration test with mandatory parameters.")
    public void testGetHistoricalDataFromWeatherStationWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHistoricalDataFromWeatherStation");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHistoricalDataFromWeatherStationMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/history/station?id=" +
                connectorProperties.getProperty("stationID") + "&type=" + connectorProperties.getProperty("frequencyType") + "&APPID=" +
                connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("station_id"), apiRestResponse.getBody().getString("station_id"));
    }


    /**
     * Negative test case for getHistoricalDataFromWeatherStation method.
     */
    @Test(priority = 1, description = "openweathermap {getHistoricalDataFromWeatherStation} integration test with negative cases.")
    public void testGetHistoricalDataFromWeatherStationWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getHistoricalDataFromWeatherStation");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHistoricalDataFromWeatherStationNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/history/station?id=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }


    /**
     * Positive test case for getCurrentWeatherFromOneStation method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherFromOneStation} integration test with mandatory parameters.")
    public void testGetCurrentWeatherFromOneStationWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherFromOneStation");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherFromOneStationMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/station?id=" +
                connectorProperties.getProperty("stationID") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("station"), apiRestResponse.getBody().getString("station"));
    }


    /**
     * Negative test case for getCurrentWeatherFromOneStation method.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherFromOneStation} integration test with negative cases.")
    public void testGetCurrentWeatherFromOneStationWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherFromOneStation");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherFromOneStationNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/station?id=" + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }


    /**
     * Positive test case for getCurrentWeatherFromSeveralStationArroundGeoPoint method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherFromSeveralStationArroundGeoPoint} integration test with mandatory parameters.")
    public void testGetCurrentWeatherFromSeveralStationArroundGeoPointWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherFromSeveralStationArroundGeoPoint");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherFromSeveralStationArroundGeoPointMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/station/find?lat=" +
                connectorProperties.getProperty("lat") + "&lon=" + connectorProperties.getProperty("lon") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody().getString("output"));
    }


    /**
     * Positive test case for getCurrentWeatherFromSeveralStationArroundGeoPoint method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherFromSeveralStationArroundGeoPoint} integration test with Optional parameters.")
    public void testGetCurrentWeatherFromSeveralStationArroundGeoPointWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherFromSeveralStationArroundGeoPoint");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherFromSeveralStationArroundGeoPointOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/station/find?lat=" +
                connectorProperties.getProperty("lat") + "&lon=" + connectorProperties.getProperty("lon") + "&cnt=" +
                connectorProperties.getProperty("cnt") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody().getString("output"));
    }


    /**
     * Negative test case for getCurrentWeatherFromSeveralStationArroundGeoPoint method.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherFromSeveralStationArroundGeoPoint} integration test with negative cases.")
    public void testGetCurrentWeatherFromSeveralStationArroundGeoPointWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherFromSeveralStationArroundGeoPoint");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherFromSeveralStationArroundGeoPointNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/station/find?lat= " + "&lon= " + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody().getString("output"));
    }


    /**
     * Positive test case for getCurrentWeatherFromSeveralStationWithinRecZone method with mandatory parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherFromSeveralStationWithinRecZone} integration test with mandatory parameters.")
    public void testGetCurrentWeatherFromSeveralStationWithinRecZoneWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherFromSeveralStationWithinRecZone");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherFromSeveralStationWithinRecZoneMandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/box/station?bbox=" +
                connectorProperties.getProperty("bbox") + "&APPID=" + connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getString("id"));
    }

    /**
     * Positive test case for getCurrentWeatherFromSeveralStationWithinRecZone method with optional parameters.
     */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherFromSeveralStationWithinRecZone} integration test with Optional parameters.")
    public void testGetCurrentWeatherFromSeveralStationWithinRecZoneWithinRecZoneWithinRectangleWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherFromSeveralStationWithinRecZone");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherFromSeveralStationWithinRecZoneOptional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") + "/box/station?bbox=" +
                connectorProperties.getProperty("bbox") + "&cluster=" + connectorProperties.getProperty("cluster")
                + "&lang=" + connectorProperties.getProperty("lang") + "&callback=" + connectorProperties.getProperty("callback") + "&APPID=" +
                connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody().getString("output"));
    }

    /*
    * Negative test case for getCurrentWeatherFromSeveralStationWithinRecZone method.
    */
    @Test(priority = 1, description = "openweathermap {getCurrentWeatherFromSeveralStationWithinRecZone} integration test with negative cases.")
    public void testGetCurrentWeatherFromSeveralStationWithinRecZoneWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCurrentWeatherFromSeveralStationWithinRecZone");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentWeatherFromSeveralStationWithinRecZoneNegative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/data/" + connectorProperties.getProperty("apiVersion") +
                "/box/city?bbox=" + "&APPID=" + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("cod"), apiRestResponse.getBody().getString("cod"));
    }
}























