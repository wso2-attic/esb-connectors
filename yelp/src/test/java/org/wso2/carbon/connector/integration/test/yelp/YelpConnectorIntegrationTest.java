/**
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


package org.wso2.carbon.connector.integration.test.yelp;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.GenerateApiUrl;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * Integration test class for Yelp connector.
 */
public class YelpConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    private String httpMethod = "GET";
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init("yelp");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
    }

    /**
     * Positive test case for search method with location mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "yelp {search by location} integration test with mandatory parameters.")
    public void testYelpSearchByLocationWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/search_location_mandatory.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using location with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with optional parameters.")
    public void testYelpSearchByLocationWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/search_location_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="category_filter="+connectorProperties.getProperty("categoryFilter")+
                "&cc="+connectorProperties.getProperty("countryCode")+
                "&deals_filter="+connectorProperties.getProperty("dealsFilter")+
                "&lang="+connectorProperties.getProperty("language")+
                "&limit="+connectorProperties.getProperty("limit")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&offset="+connectorProperties.getProperty("offset")+
                "&radius_filter="+connectorProperties.getProperty("radiusFilter")+
                "&sort="+connectorProperties.getProperty("sort")+
                "&term="+connectorProperties.getProperty("term");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using location with category filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with category filter optional parameters.")
    public void testYelpSearchByLocationWithCategoryFilterOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/optional/search_categoryFilter_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="category_filter="+connectorProperties.getProperty("categoryFilter")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }



    /**
     * Positive test case for search method using location with country code optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with country code optional parameters.")
    public void testYelpSearchByLocationWithCountryCodeOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/optional/search_countryCode_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="cc="+connectorProperties.getProperty("countryCode")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using location with deals filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with deals filter optional parameters.")
    public void testYelpSearchByLocationWithDealsFilterOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/optional/search_dealsFilter_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="deals_filter="+connectorProperties.getProperty("dealsFilter")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using location with language optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with language optional parameters.")
    public void testYelpSearchByLocationWithLanguageOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/optional/search_language_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="lang="+connectorProperties.getProperty("language")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using location with limit optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with limit optional parameters.")
    public void testYelpSearchByLocationWithLimitOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/optional/search_limit_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="limit="+connectorProperties.getProperty("limit")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using location with offset optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with offset optional parameters.")
    public void testYelpSearchByLocationWithOffsetOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/optional/search_offset_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&offset="+connectorProperties.getProperty("offset");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using location with radius filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with radius filter optional parameters.")
    public void testYelpSearchByLocationWithRadiusFilterOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/optional/search_radiusFilter_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&radius_filter="+connectorProperties.getProperty("radiusFilter");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using location with sort optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with sort optional parameters.")
    public void testYelpSearchByLocationWithSortOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/optional/search_sort_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&sort="+connectorProperties.getProperty("sort");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using location with term optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with term optional parameters.")
    public void testYelpSearchByLocationWithTermOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/optional/search_term_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&term="+connectorProperties.getProperty("term");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * ***************************************************Location Negative*********************************************
     */


    /**
     * Negative test case for search method with location mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with negative parameters.")
    public void testYelpSearchByLocationWithMandatoryParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/search_location_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="location="+connectorProperties.getProperty("invalidLocation")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using location with categoy filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with categoy filter optional negative parameters.")
    public void testYelpSearchByLocationWithCategoryFilterOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/negative/search_categoryFilter_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="category_filter="+connectorProperties.getProperty("invalidCategoryFilter")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using location with country code optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with country code optional negative parameters.")
    public void testYelpSearchByLocationWithCountryCodeOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/negative/search_countryCode_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="cc="+connectorProperties.getProperty("invalidCountryCode")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Negative test case for search method using location with deals filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with deals filter optional negative parameters.")
    public void testYelpSearchByLocationWithDealsFilterOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/negative/search_dealsFilter_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="deals_filter="+connectorProperties.getProperty("invalidDealsFilter")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }



    /**
     * Negative test case for search method using location with language optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with language optional negative parameters.")
    public void testYelpSearchByLocationWithLanguageOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/negative/search_language_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="lang="+connectorProperties.getProperty("invalidLanguage")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Negative test case for search method using location with limit optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with limit optional negative parameters.")
    public void testYelpSearchByLocationWithLimitOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/negative/search_limit_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="limit="+connectorProperties.getProperty("invalidLimit")+
                "&location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using location with offset optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with offset optional negative parameters.")
    public void testYelpSearchByLocationWithOffsetOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/negative/search_offset_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&offset="+connectorProperties.getProperty("invalidOffset");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using location with radius filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with radius filter optional negative parameters.")
    public void testYelpSearchByLocationWithRadiusFilterOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/negative/search_radiusFilter_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&radius_filter="+connectorProperties.getProperty("invalidRadiusFilter");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using location with sort optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test sort optional negative parameters.")
    public void testYelpSearchByLocationWithSortOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/negative/search_sort_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&sort="+connectorProperties.getProperty("invalidSort");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using location with term optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with term optional negative parameters.")
    public void testYelpSearchByLocationWithTermOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/location/negative/search_term_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="location="+connectorProperties.getProperty("location")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&term="+connectorProperties.getProperty("invalidTerm");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * *************************************************Bounding Box****************************************************
     */

    /**
     * Positive test case for search method using bounding box with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with mandatory parameters.")
    public void testYelpSearchByBoundingBoxWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/search_boundingbox_mandatory.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }



    /**
     * Positive test case for search method using bounding box with category filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with category filter optional parameters.")
    public void testYelpSearchByBoundingBoxWithCategoryFilterOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/optional/search_categoryFilter_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&category_filter="+connectorProperties.getProperty("categoryFilter")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using bounding box with country code optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with country code optional parameters.")
    public void testYelpSearchByBoundingBoxWithCountryCodeOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/optional/search_countryCode_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&cc="+connectorProperties.getProperty("countryCode")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using bounding box with deals filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with deals filter optional parameters.")
    public void testYelpSearchByBoundingBoxWithDealsFilterOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/optional/search_dealsFilter_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&deals_filter="+connectorProperties.getProperty("dealsFilter")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using bounding box with language optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with language optional parameters.")
    public void testYelpSearchByBoundingBoxWithLanguageOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/optional/search_language_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&lang="+connectorProperties.getProperty("language")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using bounding box with limit optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with limit optional parameters.")
    public void testYelpSearchByBoundingBoxWithLimitOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/optional/search_limit_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&limit="+connectorProperties.getProperty("limit")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using bounding box with offset optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with offset optional parameters.")
    public void testYelpSearchByBoundingBoxWithOffsetOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/optional/search_offset_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&offset="+connectorProperties.getProperty("offset");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using bounding box with radius filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with radius filter optional parameters.")
    public void testYelpSearchByBoundingBoxWithRadiusFilterOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/optional/search_radiusFilter_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&radius_filter="+connectorProperties.getProperty("radiusFilter");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using bounding box with sort optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with sort optional parameters.")
    public void testYelpSearchByBoundingBoxWithSortOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/optional/search_sort_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&sort="+connectorProperties.getProperty("sort");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using bounding box with term optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with term optional parameters.")
    public void testYelpSearchByBoundingBoxWithTermOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/optional/search_term_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&term="+connectorProperties.getProperty("term");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * ****************************************************Negative Bounding Box*************************************************
     */


    /**
     * Negative test case for search method using bounding box with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with mandatory negative parameters.")
    public void testYelpSearchByBoundingBoxWithMandatoryParametersNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/search_boundingbox_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("invalidNeLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using bounding box with category filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with category filter optional negative parameters.")
    public void testYelpSearchByBoundingBoxWithCategoryFilterOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/negative/search_categoryFilter_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&category_filter="+connectorProperties.getProperty("invalidCategoryFilter")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using bounding box with country code optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with country code optional negative parameters.")
    public void testYelpSearchByBoundingBoxWithCountryCodeOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/negative/search_countryCode_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&cc="+connectorProperties.getProperty("invalidCountryCode")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        System.out.println("responseESB\n"+esbRestResponse.getBody().toString());
        System.out.println("responseDirect\n"+apiRestResponse.getBody().toString());
        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Negative test case for search method using bounding box with deals filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with deals filter optional negative parameters.")
    public void testYelpSearchByBoundingBoxWithDealsFilterOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/negative/search_dealsFilter_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&deals_filter="+connectorProperties.getProperty("invalidDealsFilter")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using bounding box with language optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with language optional negative parameters.")
    public void testYelpSearchByBoundingBoxWithLanguageOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/negative/search_language_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&lang="+connectorProperties.getProperty("invalidLanguage")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Negative test case for search method using bounding box with limit optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with limit optional negative parameters.")
    public void testYelpSearchByBoundingBoxWithLimitOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/negative/search_limit_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&limit="+connectorProperties.getProperty("invalidLimit")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using bounding box with offset optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with offset optional negative parameters.")
    public void testYelpSearchByBoundingBoxWithOffsetOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/negative/search_offset_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&offset="+connectorProperties.getProperty("invalidOffset");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using bounding box with radius filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with radius filter optional negative parameters.")
    public void testYelpSearchByBoundingBoxWithRadiusFilterOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/negative/search_radiusFilter_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&radius_filter="+connectorProperties.getProperty("invalidRadiusFilter");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using bounding box with sort optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with sort optional negative parameters.")
    public void testYelpSearchByBoundingBoxWithSortOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/negative/search_sort_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&sort="+connectorProperties.getProperty("invalidSort");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using bounding box with term optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with term optional negative parameters.")
    public void testYelpSearchByBoundingBoxWithTermOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/boundingBox/negative/search_term_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="bounds="+connectorProperties.getProperty("swLatitude")+
                "%2C"+connectorProperties.getProperty("swLongitude")+
                "%7C"+connectorProperties.getProperty("neLatitude")+
                "%2C"+connectorProperties.getProperty("neLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&term="+connectorProperties.getProperty("invalidTerm");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * ****************************************************Geographic Location******************************************
     */


    /**
     * Positive test case for search method using geographic location with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with mandatory parameters.")
    public void testYelpSearchByGeographicLocationWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/search_geographicLocation_mandatory.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }

    /**
     * Positive test case for search method using geographic location with mandatory's optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with mandatory's optional parameters.")
    public void testYelpSearchByGeographicLocationWithMandatoryOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/search_geographicLocation_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+
                "%2C"+connectorProperties.getProperty("longitude")+
                "%2C"+connectorProperties.getProperty("accuracy")+
                "%2C"+connectorProperties.getProperty("altitude")+
                "%2C"+connectorProperties.getProperty("altitudeAccuracy")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using geographic location with category filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with category filter optional parameters.")
    public void testYelpSearchByGeographicLocationWithCategoryFilterOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/optional//search_categoryFilter_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="category_filter="+connectorProperties.getProperty("categoryFilter")+
                "&ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using geographic location with country code optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with country code optional parameters.")
    public void testYelpSearchByGeographicLocationWithCountryCodeOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/optional/search_countryCode_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="cc="+connectorProperties.getProperty("countryCode")+
                "&ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using geographic location with deals filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with deals filter optional parameters.")
    public void testYelpSearchByGeographicLocationWithDealsFilterOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/optional/search_dealsFilter_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="deals_filter="+connectorProperties.getProperty("dealsFilter")+
                "&ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using geographic location with language optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with language optional parameters.")
    public void testYelpSearchByGeographicLocationWithLanguageOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/optional/search_language_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="lang="+connectorProperties.getProperty("language")+
                "&ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using geographic location with limit optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with limit optional parameters.")
    public void testYelpSearchByGeographicLocationWithLimitOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/optional/search_limit_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="limit="+connectorProperties.getProperty("limit")+
                "&ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using geographic location with offset optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with offset optional parameters.")
    public void testYelpSearchByGeographicLocationWithOffsetOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/optional/search_offset_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&offset="+connectorProperties.getProperty("offset");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using geographic location with radius filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with radius filter optional parameters.")
    public void testYelpSearchByGeographicLocationWithRadiusFilterOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/optional/search_radiusFilter_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&radius_filter="+connectorProperties.getProperty("radiusFilter");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using geographic location with sort optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with sort optional parameters.")
    public void testYelpSearchByGeographicLocationWithSortOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/optional/search_sort_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&sort="+connectorProperties.getProperty("sort");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Positive test case for search method using geographic location with term optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with term optional parameters.")
    public void testYelpSearchByGeographicLocationWithTermOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/optional/search_term_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&term="+connectorProperties.getProperty("term");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * *****************************************Negative Geographic Location*****************************************************
     */


    /**
     * Negative test case for search method using geographic location with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with mandatory negative parameters.")
    public void testYelpSearchByGeographicLocationWithMandatoryParametersNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/search_geographicLocation_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("invalidLongitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }

    /**
     *  Negative test case for search method using geographic location with mandatory's optional parameters.
     */

    @Test(groups = {"wso2.esb"}, description = "yelp {search by bounding box} integration test with mandatory's optional negative parameters.")
    public void testYelpSearchByGeographicLocationWithMandatoryOptionalParametersNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/search_geographicLocation_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+
                "%2C"+connectorProperties.getProperty("longitude")+
                "%2C"+connectorProperties.getProperty("invalidAccuracy")+
                "%2C"+connectorProperties.getProperty("invalidAltitude")+
                "%2C"+connectorProperties.getProperty("invalidAltitudeAccuracy")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("region").toString(), apiRestResponse.getBody().getJSONObject("region").toString());
    }


    /**
     * Negative test case for search method using geographic location with category filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with category filter optional negative parameters.")
    public void testYelpSearchByGeographicLocationWithCategoryFilterOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/negative/search_categoryFilter_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="category_filter="+connectorProperties.getProperty("invalidCategoryFilter")+
                "&ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using geographic location with country code optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with country code optional negative parameters.")
    public void testYelpSearchByGeographicLocationWithCountryCodeOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/negative/search_countryCode_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="cc="+connectorProperties.getProperty("invalidCountryCode")+
                "&ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Negative test case for search method using geographic location with deals filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with deals filter optional negative parameters.")
    public void testYelpSearchByGeographicLocationWithDealsFilterOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/negative/search_dealsFilter_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="deals_filter="+connectorProperties.getProperty("invalidDealsFilter")+
                "&ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using geographic location with language optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with language optional negative parameters.")
    public void testYelpSearchByGeographicLocationWithLanguageOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/negative/search_language_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="lang="+connectorProperties.getProperty("invalidLanguage")+
                "&ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Negative test case for search method using geographic location with limit optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with limit optional negative parameters.")
    public void testYelpSearchByGeographicLocationWithLimitOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/negative/search_limit_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="limit="+connectorProperties.getProperty("invalidLimit")+
                "&ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using geographic location with offset optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with offset optional negative parameters.")
    public void testYelpSearchByGeographicLocationWithOffsetOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/negative/search_offset_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&offset="+connectorProperties.getProperty("invalidOffset");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using geographic location with radius filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with radius filter optional negative parameters.")
    public void testYelpSearchByGeographicLocationWithRadiusFilterOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/negative/search_radiusFilter_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&radius_filter="+connectorProperties.getProperty("invalidRadiusFilter");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using geographic location with sort optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with sort optional negative parameters.")
    public void testYelpSearchByGeographicLocationWithSortOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/negative/search_sort_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&sort="+connectorProperties.getProperty("invalidSort");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("id"), apiRestResponse.getBody().getJSONObject("error").get("id"));
    }


    /**
     * Negative test case for search method using geographic location with term optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by geographic location} integration test with term optional negative parameters.")
    public void testYelpSearchByGeographicLocationWithTermOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "search/geographicLocation/negative/search_term_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"search";
        String parameters ="ll="+connectorProperties.getProperty("latitude")+"%2C"+connectorProperties.getProperty("longitude")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0"+
                "&term="+connectorProperties.getProperty("invalidTerm");
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }

    /**
     * *******************************************************Business**************************************************
     */



    /**
     * Positive test case for business method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {business} integration test with mandatory parameters.")
    public void testYelpBusinessWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:business");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "business/business_mandatory.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"business/"+connectorProperties.getProperty("id");
        String parameters ="oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Positive test case for business method optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {business} integration test with optional parameters.")
    public void testYelpBusinessWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:business");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "business/business_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"business/"+connectorProperties.getProperty("id");
        String parameters ="cc="+connectorProperties.getProperty("countryCode")+
                "&lang="+connectorProperties.getProperty("language")+
                "&lang_filter="+connectorProperties.getProperty("langFilter")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Positive test case for business method using country code optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {business} integration test with country code optional parameters.")
    public void testYelpBusinessWithCountryCodeOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:business");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "business/optional/business_countryCode_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"business/"+connectorProperties.getProperty("id");
        String parameters ="cc="+connectorProperties.getProperty("countryCode")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Positive test case for business method using language optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {business} integration test with language optional parameters.")
    public void testYelpBusinessWithLanguageOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:business");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "business/optional/business_language_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"business/"+connectorProperties.getProperty("id");
        String parameters ="lang="+connectorProperties.getProperty("language")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Positive test case for business method using language filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {business} integration test with language filter optional  parameters.")
    public void testYelpBusinessWithLangFilterOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:business");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "business/optional/business_langFilter_optional.txt");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "business/" + connectorProperties.getProperty("id");
        String parameters = "lang_filter=" + connectorProperties.getProperty("langFilter") +
                "&oauth_consumer_key=" + connectorProperties.getProperty("consumerKey") +
                "&oauth_nonce=dummynonce" +
                "&oauth_signature_method=HMAC-SHA1" +
                "&oauth_timestamp=dummytimestamp" +
                "&oauth_token=" + connectorProperties.getProperty("accessToken") +
                "&oauth_version=1.0";
        String apiUrl = GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, httpMethod, apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }

    /**
     * ****************************************************Business Negative********************************************
     */

    /**
     * Negative test case for business method optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {business} integration test with optional negative parameters.")
    public void testYelpBusinessWithOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:business");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "business/business_optional.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"business/"+connectorProperties.getProperty("id");
        String parameters ="cc="+connectorProperties.getProperty("invalidCountryCode")+
                "&lang="+connectorProperties.getProperty("invalidLanguage")+
                "&lang_filter="+connectorProperties.getProperty("invalidLangFilter")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Negative test case for business method mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {business} integration test with mandatory Negative parameters.")
    public void testYelpBusinessWithMandatoryParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:business");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "business/business_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"business/"+connectorProperties.getProperty("id");
        String parameters ="oauth_consumer_key="+connectorProperties.getProperty("invalidConsumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }



    /**
     * Negative test case for business method using country code optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {business} integration test with country code optional negative parameters.")
    public void testYelpBusinessWithCountryCodeOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:business");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "business/negative/business_countryCode_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"business/"+connectorProperties.getProperty("id");
        String parameters ="cc="+connectorProperties.getProperty("invalidCountryCode")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Negative test case for business method using language optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {business} integration test with language optional negative parameters.")
    public void testYelpBusinessWithLanguageOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:business");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "business/negative/business_language_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"business/"+connectorProperties.getProperty("id");
        String parameters ="lang="+connectorProperties.getProperty("invalidLanguage")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }


    /**
     * Negative test case for business method using language filter optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "yelp {search by location} integration test with mandatory negative parameters.")
    public void testYelpBusinessWithLangFilterOptionalParametersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:business");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "business/negative/business_langFilter_optional_negative.txt");
        String apiEndPoint= connectorProperties.getProperty("apiUrl")+"business/"+connectorProperties.getProperty("id");
        String parameters ="lang_filter="+connectorProperties.getProperty("invalidLangFilter")+
                "&oauth_consumer_key="+connectorProperties.getProperty("consumerKey")+
                "&oauth_nonce=dummynonce"+
                "&oauth_signature_method=HMAC-SHA1"+
                "&oauth_timestamp=dummytimestamp"+
                "&oauth_token="+connectorProperties.getProperty("accessToken")+
                "&oauth_version=1.0";
        String apiUrl= GenerateApiUrl.getApiUrl(httpMethod, apiEndPoint, parameters, connectorProperties);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl,httpMethod,apiRequestHeadersMap);

        Assert.assertTrue(esbRestResponse.getBody().toString().equals(apiRestResponse.getBody().toString()));
    }
}