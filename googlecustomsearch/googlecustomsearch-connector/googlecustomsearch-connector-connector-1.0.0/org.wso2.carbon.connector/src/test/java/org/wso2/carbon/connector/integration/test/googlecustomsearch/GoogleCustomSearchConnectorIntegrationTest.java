/**
 *  Copyright (c) 2014-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


package org.wso2.carbon.connector.integration.test.googlecustomsearch;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.util.HashMap;
import java.util.Map;
import java.lang.*;

/**
 * Integration test class for Google Custom Search connector.
 */
public class GoogleCustomSearchConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("googlecustomsearch-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
    }

    /**
     * Positive test case for Search method with required parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with required parameters.")
    public void testSearchWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with required parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with negative case for required parameters.")
    public void testSearchWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("invalidApiKey")
                + "&cx=" + connectorProperties.getProperty("invalidCseID")
                + "&q=" + connectorProperties.getProperty("query");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for search type.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for search type.")
    public void testSearchWithOptionalParameterSearchType() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:searchType");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchtype_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&searchType=" + connectorProperties.getProperty("searchType");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for search type.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative search type.")
    public void testSearchWithOptionalParameterNegativeSearchType() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:searchType");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchtype_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&searchType=" + connectorProperties.getProperty("invalidSearchType");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for exact terms.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for exact terms.")
    public void testSearchWithOptionalParameterExactTerms() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:exactTerms");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_exactTerms_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&exactTerms=" + connectorProperties.getProperty("exactTerms");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }


    /**
     * Positive test case for Search method with optional parameter for dateRestrict.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for dateRestrict.")
    public void testSearchWithOptionalParameterDateRestrict() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:dateRestrict");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_dateRestrict_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&dateRestrict=" + connectorProperties.getProperty("dateRestrict");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for exclude terms.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for exclude terms.")
    public void testSearchWithOptionalParameterExcludeTerms() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:excludeTerms");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_excludeTerms_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&excludeTerms=" + connectorProperties.getProperty("excludeTerms");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for filter.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for filter.")
    public void testSearchWithOptionalParameterFilter() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:filter");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_filter_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&filter=" + connectorProperties.getProperty("filter");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for negative filter.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative filter.")
    public void testSearchWithOptionalParameterNegativeFilter() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:filter");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_filter_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&filter=" + connectorProperties.getProperty("invalidFilter");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for country code.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for country code.")
    public void testSearchWithOptionalParameterCountryCode() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:countryCode");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_gl_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&gl=" + connectorProperties.getProperty("countryCode");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for interface language.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for interface language.")
    public void testSearchWithOptionalParameterInterfaceLanguage() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:interfaceLanguage");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_hl_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&hl=" + connectorProperties.getProperty("interfaceLanguage");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for appends query.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for appends query.")
    public void testSearchWithOptionalParameterAppendsQuery() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:appendsQuery");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_hq_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&hq=" + connectorProperties.getProperty("appendsQuery");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for imgColorType.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for imgColorType.")
    public void testSearchWithOptionalParameterImgColorType() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:imgColorType");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_imgColorType_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&searchType=" + connectorProperties.getProperty("searchType")
                + "&imgColorType=" + connectorProperties.getProperty("imgColorType");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for negative imgColorType.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative imgColorType.")
    public void testSearchWithOptionalParameterNegativeImgColorType() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:imgColorType");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_imgColorType_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&searchType=" + connectorProperties.getProperty("searchType")
                + "&imgColorType=" + connectorProperties.getProperty("invalidImgColorType");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);//

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for imgDominantColor.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for imgDominantColor.")
    public void testSearchWithOptionalParameterImgDominantColor() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:imgDominantColor");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_imgDominantColor_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&searchType=" + connectorProperties.getProperty("searchType")
                + "&imgDominantColor=" + connectorProperties.getProperty("imgDominantColor");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for negative imgDominantColor.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative imgDominantColor.")
    public void testSearchWithOptionalParameterNegativeImgDominantColor() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:imgDominantColor");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_imgDominantColor_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&searchType=" + connectorProperties.getProperty("searchType")
                + "&imgDominantColor=" + connectorProperties.getProperty("invalidImgDominantColor");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for imgType.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for imgType.")
    public void testSearchWithOptionalParameterImgType() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:imgType");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_imgType_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&searchType=" + connectorProperties.getProperty("searchType")
                + "&imgType=" + connectorProperties.getProperty("imgType");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for negative imgType.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative imgType.")
    public void testSearchWithOptionalParameterNegativeImgType() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:imgType");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_imgType_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&searchType=" + connectorProperties.getProperty("searchType")
                + "&imgType=" + connectorProperties.getProperty("invalidImgType");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for imgSize.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for imgSize.")
    public void testSearchWithOptionalParameterImgSize() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:imgSize");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_imgSize_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&searchType=" + connectorProperties.getProperty("searchType")
                + "&imgSize=" + connectorProperties.getProperty("imgSize");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for negative imgSize.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative imgSize.")
    public void testSearchWithOptionalParameterNegativeImgSize() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:imgSize");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_imgSize_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&searchType=" + connectorProperties.getProperty("searchType")
                + "&imgSize=" + connectorProperties.getProperty("invalidImgSize");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for start.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for start.")
    public void testSearchWithOptionalParameterStart() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:start");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_start_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&start=" + connectorProperties.getProperty("start");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for start.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative start.")
    public void testSearchWithOptionalParameterNegativeStart() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:start");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_start_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&start=" + connectorProperties.getProperty("invalidStart");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for sort.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for sort.")
    public void testSearchWithOptionalParameterSort() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:sort");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sort_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&sort=" + connectorProperties.getProperty("sortP");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for safe.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for safe.")
    public void testSearchWithOptionalParameterSafe() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:safe");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_safe_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&safe=" + connectorProperties.getProperty("safe");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for safe.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative safe.")
    public void testSearchWithOptionalParameterNegativeSafe() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:safe");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_safe_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url") + "?key="
                + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&safe=" + connectorProperties.getProperty("invalidSafe");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for num.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for num.")
    public void testSearchWithOptionalParameterNum() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:num");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_num_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&num=" + connectorProperties.getProperty("num");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for num.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative num.")
    public void testSearchWithOptionalParameterNegativeNum() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:num");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_num_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url") + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&num=" + connectorProperties.getProperty("invalidNum");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for language restrict.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for language restrict.")
    public void testSearchWithOptionalParameterLanguageRestrict() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:languageRestrict");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_languageRestrict_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&lr=" + connectorProperties.getProperty("languageRestrict");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for languageRestrict.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative language restrict.")
    public void testSearchWithOptionalParameterNegativeLanguageRestrict() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:languageRestrict");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_languageRestrict_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&lr=" + connectorProperties.getProperty("invalidLanguageRestrict");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for lowRange.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for lowRange.")
    public void testSearchWithOptionalParameterLowRange() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:lowRange");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_lowRange_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&lowRange=" + connectorProperties.getProperty("lowRange");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for siteSearchFilter.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for siteSearchFilter.")
    public void testSearchWithOptionalParameterSiteSearchFilter() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:siteSearchFilter");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_siteSearchFilter_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&siteSearchFilter=" + connectorProperties.getProperty("siteSearchFilter");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for siteSearchFilter.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative siteSearchFilter.")
    public void testSearchWithOptionalParameterNegativeSiteSearchFilter() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:siteSearchFilter");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_siteSearchFilter_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url") + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&siteSearchFilter=" + connectorProperties.getProperty("invalidSiteSearchFilter");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for relatedSite.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for siteSearchFilter.")
    public void testSearchWithOptionalParameterRelatedSite() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:relatedSite");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_relatedSite_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&relatedSite=" + connectorProperties.getProperty("relatedSite");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for linkSite.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for linkSite.")
    public void testSearchWithOptionalParameterLinkSite() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:linkSite");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_linkSite_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&linkSite=" + connectorProperties.getProperty("linkSite");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for alternative.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for alternative.")
    public void testSearchWithOptionalParameterAlternative() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:alt");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_alt_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&alt=" + connectorProperties.getProperty("alt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for Search method with optional parameter for alternative.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative alternative.")
    public void testSearchWithOptionalParameterNegativeAlternative() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:alt");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_alt_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&alt=" + connectorProperties.getProperty("invalidAlt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

    /**
     * Positive test case for Search method with optional parameter for countryRestriction.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for country restriction .")
    public void testSearchWithOptionalParameterCountryRestriction() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:countryRest");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_countryRestrict_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&cr=" + connectorProperties.getProperty("countryRestrict");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for orterms.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for orTerms .")
    public void testSearchWithOptionalParameterOrTerms() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:orTerms");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_orTerms_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&orTerms=" + connectorProperties.getProperty("orTerms");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("context").toString(), esbRestResponse.getBody().get("context").toString());
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for rights.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for rights .")
    public void testSearchWithOptionalParameterRights() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:rights");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_rights_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&rights=" + connectorProperties.getProperty("rights");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Positive test case for Search method with optional parameter for Linked Custom search engine reference.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for Linked Custom search engine reference .")
    public void testSearchWithOptionalParameterSearchcref() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_cref_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cref=" + connectorProperties.getProperty("cref")
                + "&q=" + connectorProperties.getProperty("query");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().get("url").toString(), esbRestResponse.getBody().get("url").toString());
    }

    /**
     * Negative test case for Search method with optional parameter for Linked Custom search engine reference.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter for negative Linked Custom search engine reference.")
    public void testSearchWithOptionalParameterNegativeCref() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_cref_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&q=" + connectorProperties.getProperty("query")
                + "&cref=" + connectorProperties.getProperty("invalidCref");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }

     /**
     * Positive test case for Search method with optional parameter for fields.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with optional parameter fields.")
    public void testSearchWithMandatoryParametersFields() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:fields");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_fields_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&fields=" + connectorProperties.getProperty("fields");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       }

    /**
     * Negative test case for Search method with optional parameter fields.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "googlecustomsearch {search} integration test with negative case for optional parameter fields.")
    public void testSearchWithNegativeCaseFields() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:fields");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_fields_optional_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("url")
                + "?key=" + connectorProperties.getProperty("apiKey")
                + "&cx=" + connectorProperties.getProperty("cseID")
                + "&q=" + connectorProperties.getProperty("query")
                + "&fields=" + connectorProperties.getProperty("invalidFields");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
    }
}
