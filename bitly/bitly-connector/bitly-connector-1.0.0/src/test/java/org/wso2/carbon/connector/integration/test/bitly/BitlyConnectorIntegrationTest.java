package org.wso2.carbon.connector.integration.test.bitly;



/*Copyright 2005-2011 WSO2, Inc. (http://wso2.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/


import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.util.HashMap;
import java.util.Map;


public class BitlyConnectorIntegrationTest extends ConnectorIntegrationTestBase {


    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> parametersMap = new HashMap<String, String>();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("bitly-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
    }

    //    User

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUOauthApp} integration test with mandatory parameters.")
    public void testGetOAuthAppMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getOauthApp");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/oauth/app?access_token=" + connectorProperties.getProperty("accessToken") + "&client_id=" + connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getOAuthApp.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUOauthApp} integration test with negative parameters.")
    public void testGetOAuthAppNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getOauthApp");
//
        String apiEndPoint = "https://api-ssl.bitly.com/v3/oauth/app?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&client_id=" + connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getOAuthApp_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserInfo} integration test with mandatory parameters.")
    public void testGetUserInfoMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserInfo");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/info?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserInfo} integration test with optional parameters.")
    public void testGetUserInfoOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserInfo");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/info?access_token=" + connectorProperties.getProperty("accessToken") + "&full_name=" + connectorProperties.getProperty("fullName");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getUserInfo_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"), "PASSED INOKA");
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserInfo} integration test with negative parameters.")
    public void testGetUserInfoNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserInfo");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/info?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserLinkHistory} integration test with mandatory parameters.")
    public void testGetUserLinkHistoryMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserLinkHistory");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/link_history?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserLinkHistory} integration test with optional parameters.")
    public void testGetUserLinkHistoryOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserLinkHistory");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/link_history?access_token=" + connectorProperties.getProperty("accessToken") + "&limit=" + connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getUserLinkHistory_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserLinkHistory} integration test with negative parameters.")
    public void testGetUserLinkHistoryNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserLinkHistory");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/link_history?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserNetworkHistory} integration test with mandatory parameters.")
    public void testGetUserNetworkHistoryMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserNetworkHistory");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/network_history?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserNetworkHistory} integration test with optional parameters.")
    public void testGetUserNetworkHistoryOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserNetworkHistory");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/network_history?access_token=" + connectorProperties.getProperty("accessToken") + "&expand_user=" + connectorProperties.getProperty("expandUser") + "&limit=" + connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getUserNetworkHistory_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserNetworkHistory} integration test with negative parameters.")
    public void testGetUserNetworkHistoryNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserNetworkHistory");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/network_history?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getTrackingDomainList} integration test with mandatory parameters.")
    public void testGetTrackingDomainListMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTrackingDomainList");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/tracking_domain_list?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getTrackingDomainList} integration test with negative parameters.")
    public void testGetTrackingDomainListNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTrackingDomainList");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/tracking_domain_list?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


//    User Metrics

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserClicks} integration test with mandatory parameters.")
    public void testGetUserClicksMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserClicks");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/clicks?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserClicks} integration test with negative parameters.")
    public void testGetUserClicksNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserClicks");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/clicks?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getCountries} integration test with mandatory parameters.")
    public void testGetCountriesMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getCountries");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/countries?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getCountries} integration test with negative parameters.")
    public void testGetCountriesNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getCountries");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/countries?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getPopularLinks} integration test with mandatory parameters.")
    public void testGetPopularLinksMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPopularLinks");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/popular_links?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getPopularLinks} integration test with optional parameters.")
    public void testGetPopularLinksOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPopularLinks");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/popular_links?access_token=" + connectorProperties.getProperty("accessToken") + "&limit=" + connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getPopularLinks_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getPopularLinks} integration test with negative parameters.")
    public void testGetPopularLinksNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPopularLinks");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/popular_links?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getReferrers} integration test with mandatory parameters.")
    public void testGetReferrersMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getReferrers");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/referrers?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getReferrers} integration test with optional parameters.")
    public void testGetReferrersOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getReferrers");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/referrers?access_token=" + connectorProperties.getProperty("accessToken") + "&limit=" + connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getReferrers_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getReferrers} integration test with negative parameters.")
    public void testGetReferrersNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getReferrers");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/referrers?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getReferringDomains} integration test with mandatory parameters.")
    public void testGetReferringDomainsMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getReferringDomains");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/referring_domains?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getReferringDomains} integration test with optional parameters.")
    public void testGetReferringDomainsOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getReferringDomains");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/referring_domains?access_token=" + connectorProperties.getProperty("accessToken") + "&limit=" + connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getReferringDomains_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getReferringDomains} integration test with negative parameters.")
    public void testGetReferringDomainsNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getReferringDomains");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/referring_domains?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getShareCounts} integration test with mandatory parameters.")
    public void testGetShareCountsMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getShareCounts");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/share_counts?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getShareCounts} integration test with negative parameters.")
    public void testGetShareCountsNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getShareCounts");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/share_counts?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


//    Bundle

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {deleteBundle} integration test with mandatory parameters.")
    public void testDeleteBundleMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:deleteBundle");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/archive?access_token=" + connectorProperties.getProperty("accessToken") + "&bundle_link=" + connectorProperties.getProperty("deleteBundleLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteBundle_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

//        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {deleteBundle} integration test with negative parameters.")
    public void testDeleteBundleNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:deleteBundle");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/archive?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&bundle_link=" + connectorProperties.getProperty("deleteBundleLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteBundle_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));

//        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {addLinkToBundle} integration test with mandatory parameters.")
    public void testAddLinkToBundleMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:addLinkToBundle");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/link_add?access_token=" + connectorProperties.getProperty("accessToken") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink") + "&link=" + connectorProperties.getProperty("addBitlyLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "addLinkToBundle_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

//        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
        Assert.assertEquals(apiRestResponse.getBody().get("status_code"), 500);
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {addLinkToBundle} integration test with negative parameters.")
    public void testAddLinkToBundleNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:addLinkToBundle");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/link_add?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink") + "&link=" + connectorProperties.getProperty("addBitlyLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "addLinkToBundle_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
//        Assert.assertEquals(apiRestResponse.getBody().get("status_code"),500);
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {createBundle} integration test with mandatory parameters.")
    public void testCreateBundleMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createBundle");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/create?access_token=" + connectorProperties.getProperty("accessToken") + "&title=" + connectorProperties.getProperty("crtBundleTitle");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createBundle_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {createBundle} integration test with optional parameters.")
    public void testCreateBundleOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createBundle");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/create?access_token=" + connectorProperties.getProperty("accessToken") + "&title=" + connectorProperties.getProperty("crtBundleTitle") + "&description=" + connectorProperties.getProperty("bndlDescription");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createBundle_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {createBundle} integration test with negative parameters.")
    public void testCreateBundleNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createBundle");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/create?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&title=" + connectorProperties.getProperty("crtBundleTitle");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createBundle_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {editBundle} integration test with mandatory parameters.")
    public void testEditBundleMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editBundle");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/edit?access_token=" + connectorProperties.getProperty("accessToken") + "&title=" + connectorProperties.getProperty("newBundleTitle") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "editBundle_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {editBundle} integration test with optional parameters.")
    public void testEditBundleOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editBundle");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/edit?access_token=" + connectorProperties.getProperty("accessToken") + "&title=" + connectorProperties.getProperty("newBundleTitle") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink") + "&description=" + connectorProperties.getProperty("bndlDescription");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "editBundle_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {editBundle} integration test with negative parameters.")
    public void testEditBundleNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editBundle");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/edit?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&title=" + connectorProperties.getProperty("newBundleTitle") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "editBundle_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getBundleContents} integration test with mandatory parameters.")
    public void testGetBundleContentsMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBundleContents");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/contents?access_token=" + connectorProperties.getProperty("accessToken") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getBundleContents_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getBundleContents} integration test with optional parameters.")
    public void testGetBundleContentsOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBundleContents");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/contents?access_token=" + connectorProperties.getProperty("accessToken") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink") + "&expand_user=" + connectorProperties.getProperty("expandUser");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getBundleContents_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getBundleContents} integration test with negative parameters.")
    public void testGetBundleContentsNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBundleContents");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/contents?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getBundleContents_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getBundleLinkRemove} integration test with mandatory parameters.")
    public void testGetBundleLinkRemoveMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBundleLinkRemove");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/link_remove?access_token=" + connectorProperties.getProperty("accessToken") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink") + "&link=" + connectorProperties.getProperty("addBitlyLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "bundleLinkRemove_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getBundleLinkRemove} integration test with negative parameters.")
    public void testGetBundleLinkRemoveNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBundleLinkRemove");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/link_remove?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink") + "&link=" + connectorProperties.getProperty("addBitlyLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "bundleLinkRemove_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getBundlesByUser} integration test with mandatory parameters.")
    public void testGetBundlesByUserMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBundlesByUser");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/bundles_by_user?access_token=" + connectorProperties.getProperty("accessToken") + "&user=" + connectorProperties.getProperty("bitlyUser");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getBundlesByUser_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getBundlesByUser} integration test with optional parameters.")
    public void testGetBundlesByUserOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBundlesByUser");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/bundles_by_user?access_token=" + connectorProperties.getProperty("accessToken") + "&user=" + connectorProperties.getProperty("bitlyUser") + "&expand_user=" + connectorProperties.getProperty("expandUser");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getBundlesByUser_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getBundlesByUser} integration test with negative parameters.")
    public void testGetBundlesByUserNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBundlesByUser");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/bundles_by_user?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&user=" + connectorProperties.getProperty("bitlyUser");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getBundlesByUser_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getBundlEviewCount} integration test with mandatory parameters.")
    public void testGetBundlEviewCountMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBundlEviewCount");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/view_count?access_token=" + connectorProperties.getProperty("accessToken") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getBundlEviewCount_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getBundlEviewCount} integration test with negative parameters.")
    public void testGetBundlEviewCountNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBundlEviewCount");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bundle/view_count?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&bundle_link=" + connectorProperties.getProperty("toBundleLink");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getBundlEviewCount_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserBundleHistory} integration test with mandatory parameters.")
    public void testGetUserBundleHistoryMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserBundleHistory");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/bundle_history?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserBundleHistory} integration test with optional parameters.")
    public void testGetUserBundleHistoryOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserBundleHistory");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/bundle_history?access_token=" + connectorProperties.getProperty("accessToken") + "&expand_user=" + connectorProperties.getProperty("expandUser");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getUserBundleHistory_Optional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getUserBundleHistory} integration test with negative parameters.")
    public void testGetUserBundleHistoryNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserBundleHistory");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/bundle_history?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


//    Domain

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getProDomain} integration test with mandatory parameters.")
    public void testGetProDomainMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getProDomain");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bitly_pro_domain?access_token=" + connectorProperties.getProperty("accessToken") + "&domain=" + connectorProperties.getProperty("proDomain");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getProDomain_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getProDomain} integration test with negative parameters.")
    public void testGetProDomainNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getProDomain");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/bitly_pro_domain?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&domain=" + connectorProperties.getProperty("proDomain");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getProDomain_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getTrackingDomainClicks} integration test with mandatory parameters.")
    public void testGetTrackingDomainClicksMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTrackingDomainClicks");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/tracking_domain_clicks?access_token=" + connectorProperties.getProperty("accessToken") + "&domain=" + connectorProperties.getProperty("proDomain");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getTrackingDomainClicks_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getTrackingDomainClicks} integration test with negative parameters.")
    public void testGetTrackingDomainClicksNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTrackingDomainClicks");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/tracking_domain_clicks?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&domain=" + connectorProperties.getProperty("proDomain");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getTrackingDomainClicks_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getTrackingDomainShortenCounts} integration test with mandatory parameters.")
    public void testGetTrackingDomainShortenCountsMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTrackingDomainShortenCounts");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/tracking_domain_shorten_counts?access_token=" + connectorProperties.getProperty("accessToken") + "&domain=" + connectorProperties.getProperty("proDomain");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getTrackingDomainShortenCounts_Mandatory.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly {getTrackingDomainShortenCounts} integration test with negative parameters.")
    public void testGetTrackingDomainShortenCountsNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTrackingDomainShortenCounts");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/tracking_domain_shorten_counts?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&domain=" + connectorProperties.getProperty("proDomain");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getTrackingDomainShortenCounts_Negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getHighvalue} integration test with mandatory parameters.")
    public void testGetHighValueWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getHighvalue");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHighvalue_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/highvalue?access_token=" + connectorProperties.getProperty("accessToken") + "&limit=" + connectorProperties.getProperty("noOfLinks");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getHighvalue} integration test with negative parameters.")
    public void testGetHighValueWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getHighvalue");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getHighvalue_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/highvalue?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&limit=" + connectorProperties.getProperty("NoOfLinks");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));

    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkInfo} integration test with mandatory parameters.")
    public void testGetLinkInfoWithMandatoryParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getLinkInfo");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkInfo_mandatory.txt");


        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/info?access_token=" + connectorProperties.getProperty("accessToken") + "&link=" + connectorProperties.getProperty("bitlyLink");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getLinkInfo_mandatory.txt");


        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkInfo} integration test with negative parameters.")
    public void testGetLinkInfoWithNegativeParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getLinkInfo");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkInfo_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/info?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&link=" + connectorProperties.getProperty("bitlyLink");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkContent} integration test with mandatory parameters.")
    public void testGetLinkContentWithMandatoryParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getLinkContent");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkContent_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/content?access_token=" + connectorProperties.getProperty("accessToken") + "&link=" + connectorProperties.getProperty("bitlyLinkforContent");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getLinkContent_mandatory.txt");


        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkContent} integration test with mandatory parameters.")
    public void testGetLinkContentWithOptionalParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getLinkContent");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkContent_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/content?access_token=" + connectorProperties.getProperty("accessToken") + "&link=" + connectorProperties.getProperty("bitlyLinkforContent") + "&content_type=" + connectorProperties.getProperty("contentType");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getLinkContent_mandatory.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkContent} integration test with negative parameters.")
    public void testGetLinkContentWithNegativeParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getLinkContent");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkContent_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/content?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&link=" + connectorProperties.getProperty("bitlyLinkforContent");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkCatagory} integration test with mandatory parameters.")
    public void testGetLinkCatagoryWithMandatoryParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getLinkCatagory");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkCatagory_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/category?access_token=" + connectorProperties.getProperty("accessToken") + "&link=" + connectorProperties.getProperty("bitlyLinkforCategory");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getLinkCatagory_mandatory.txt");


        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkCatagory} integration test with negative parameters.")
    public void testGetLinkCatagoryWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkCatagory");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkCatagory_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/category?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&link=" + connectorProperties.getProperty("bitlyLinkforCategory");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getSearch} integration test with mandatory parameters.")
    public void testGetSearchWithMandatoryParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getSearch");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSearch_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/search?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getSearch_mandatory.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getSearch} integration test with mandatory parameters.")
    public void testGetSearchWithOptionalParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getSearch");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSearch_optional.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/search?access_token=" + connectorProperties.getProperty("accessToken") + "&limit=" + connectorProperties.getProperty("noOfLinks")
                + "query=" + connectorProperties.getProperty("query");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getSearch_optional.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getSearch} integration test with negative parameters.")
    public void testGetSearchWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getSearch");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSearch_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/search?access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getInfo} integration test with mandatory parameters.")
    public void testGetInfoWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getInfo");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getInfo_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/info?access_token=" + connectorProperties.getProperty("accessToken") + "&shortUrl=" + connectorProperties.getProperty("shortUrlforInfo");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getInfo_mandatory.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getInfo} integration test with optional parameters.")
    public void testGetInfoWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getInfo");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getInfo_optional.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/info?access_token=" + connectorProperties.getProperty("accessToken") + "&shortUrl=" + connectorProperties.getProperty("shortUrlforInfo") + "&expand_user=" + connectorProperties.getProperty("expandUserinfo");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getInfo_optional.txt");


        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getInfo} integration test with nagative parameters.")
    public void testGetInfoWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getInfo");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getInfo_negative.txt");


        String apiEndPoint = "https://api-ssl.bitly.com/v3/info?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&shortUrl=" + connectorProperties.getProperty("shortUrlforInfo");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getExpand} integration test with mandatory parameters.")
    public void testGetExpandWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getExpand");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getExpand_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/expand?access_token=" + connectorProperties.getProperty("accessToken") + "&shortUrl=" + connectorProperties.getProperty("shortUrlforExpand");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getExpand_mandatory.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getExpand} integration test with negative parameters.")
    public void testGetExpandWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getExpand");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getExpand_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/expand?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&shortUrl=" + connectorProperties.getProperty("shortUrlforExpand");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 500);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkSave} integration test with mandatory parameters.")
    public void testGetLinkSaveWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkSave");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkSave_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/link_save?access_token=" + connectorProperties.getProperty("accessToken") + "&longUrl=" + connectorProperties.getProperty("longtUrlforLinkSave");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getLinkSave_mandatory.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkSave} integration test with optional parameters.")
    public void testGetLinkSaveWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkSave");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkSave_optional.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/link_save?access_token=" + connectorProperties.getProperty("accessToken") + "&longUrl=" + connectorProperties.getProperty("longtUrlforLinkSave") + "&titl=" + connectorProperties.getProperty("titleforLink") + "&note=" + connectorProperties.getProperty("noteforLink") + "&private=" + connectorProperties.getProperty("privateLink");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getLinkSave_optional.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkSave} integration test with negative parameters.")
    public void testGetLinkSaveWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkSave");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkSave_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/user/link_save?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&longUrl=" + connectorProperties.getProperty("longtUrlforLinkSave");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLookup} integration test with mandatory parameters.")
    public void testGetLookUpWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLookup");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLookup_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/lookup?url=" + connectorProperties.getProperty("urlforLookup") + "&access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getLookup_mandatory.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLookup} integration test with negative parameters.")
    public void testGetLookUpWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLookup");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLookup_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/lookup?url=" + connectorProperties.getProperty("urlforLookup") + "&access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getShorten} integration test with mandatory parameters.")
    public void testGetShortnWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getShorten");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getShorten_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/shorten?longUrl=" + connectorProperties.getProperty("longtUrlforShorten") + "&access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getShorten_mandatory.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getShorten} integration test with optional parameters.")
    public void testGetShortnWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getShorten");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getShorten_optional.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/shorten?longUrl=" + connectorProperties.getProperty("longtUrlforShorten") + "&access_token=" + connectorProperties.getProperty("accessToken") + "&domain=" + connectorProperties.getProperty("bitlyShortDomain");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getShorten_optional.txt");


        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getShorten} integration test with negative parameters.")
    public void testGetShortnWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getShorten");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getShorten_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/shorten?longUrl=" + connectorProperties.getProperty("longtUrlforShorten") + "&access_token=" + connectorProperties.getProperty("accessTokenNegative");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 500);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkClicks} integration test with mandatory parameters.")
    public void testGetLinkClicksWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkClicks");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkClicks_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/clicks?access_token=" + connectorProperties.getProperty("accessToken") + "&link=" + connectorProperties.getProperty("urlforLinkCkick");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getLinkClicks_mandatory.txt");


        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkClicks} integration test with negative parameters.")
    public void testGetLinkClicksWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkClicks");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkClicks_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/clicks?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&link=" + connectorProperties.getProperty("urlforLinkCkick");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkCountries} integration test with mandatory parameters.")
    public void testGetLinkCountriesWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkCountries");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkCountries_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/countries?access_token=" + connectorProperties.getProperty("accessToken") + "&link=" + connectorProperties.getProperty("urlforLinkCountry");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getLinkCountries_mandatory.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkCountries} integration test with negative parameters.")
    public void testGetLinkCountriesWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkCountries");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkCountries_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/countries?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&link=" + connectorProperties.getProperty("urlforLinkCountry");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }


    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkShares} integration test with mandatory parameters.")
    public void testGetLinkSharesWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkShares");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkShares_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/shares?access_token=" + connectorProperties.getProperty("accessToken") + "&link=" + connectorProperties.getProperty("urlforLinkShares");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "getLinkShares_mandatory.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkShares} integration test with negative parameters.")
    public void testGetLinkSharesWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkShares");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkShares_negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/shares?access_token=" + connectorProperties.getProperty("accessTokenNegative") + "&link=" + connectorProperties.getProperty("urlforLinkShares");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), 403);
        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkReferringDomains} integration test with mandatory parameters.")
    public void testGetLinkReferringDomainsWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkReferringDomains");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkReferringDomains_mandatory.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/referring_domains?access_token=" + connectorProperties.getProperty("accessToken") + "&link=" + connectorProperties.getProperty("urlforReferringDomain");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));
    }

    @Test(priority = 1, groups = {"wso2.esb"}, description = "bitly{getLinkReferringDomains} integration test with Negative parameters.")
    public void testGetLinkReferringDomainsWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLinkReferringDomains");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getLinkReferringDomains_Negative.txt");

        String apiEndPoint = "https://api-ssl.bitly.com/v3/link/referring_domains?access_token=dasd5t67asdsafd6678sdasfsaf&link=" + connectorProperties.getProperty("urlforReferringDomain");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("status_code"), apiRestResponse.getBody().get("status_code"));

    }


}