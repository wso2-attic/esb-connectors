package org.wso2.carbon.connector.integration.test.reddit;
/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
import java.io.IOException;
import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.json.JSONArray;

public class redditConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    /**
    * Set up the environment.
    */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment()throws Exception {
        init("reddit-connector-1.0.0");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Action", "getAccessTokenFromRefreshToken");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccessToken.json");
        connectorProperties.setProperty("accessToken", esbRestResponse.getBody().getString("access_token"));
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        connectorProperties.setProperty("userRegister", connectorProperties.getProperty("userRegister") +
                System.currentTimeMillis());
    }
    /**
     * Positive test case for getUserFriendInfo method with mandatory parameters or with api endpoint.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "reddit {getUserFriendInfo} integration test with mandatory parameters.")
    public void testgetUserFriendInfoWithMandatoryParameters() throws IOException, JSONException, Exception{
        esbRequestHeadersMap.put("Action", "getUserFriendInfo");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/api/"+ connectorProperties.
                getProperty("apiVersion")+ "/me/friends";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserFriendInfo_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getString("id"), apiRestResponse.getBody().getJSONObject("data").getJSONArray("children").
                        getJSONObject(0).getString("id"));
    }
    /**
      * Positive test case for getCurrentUserInfo method with mandatory parameters or with api endpoint.
      * @throws JSONException
      * @throws IOException
      */
    @Test(priority = 2, description = "reddit {getCurrentUserInfo} integration test with mandatory parameters.")
    public void testgetCurrentUserInfoWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getCurrentUserInfo");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/api/"+connectorProperties.
                getProperty("apiVersion")+ "/me";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCurrentUserInfo_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }
    /**
     * Positive test case for getCurrentUserMultisList method with mandatory parameters.
     * @throws IOException
     */
    @Test(priority = 3, description = "reddit {getCurrentUserMultisList} integration test with mandatory parameters.")
    public void testgetCurrentUserMultisListWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getCurrentUserMultisList");
        Thread.sleep(60000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/api/multi/mine?expand_srs="+connectorProperties.
                getProperty("expandSrs");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCurrentUserMultisList_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Positive test case for getCurrentUserPreferences method with mandatory parameters or with api endpoint.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, description = "reddit {getCurrentUserPreferences} integration test with mandatory parameters.")
    public void testgetCurrentUserPreferencesWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getCurrentUserPreferences");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/api/"+connectorProperties.
                getProperty("apiVersion")+ "/me/prefs";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCurrentUserPreferences_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("private_feeds"), apiRestResponse.getBody().
                getString("private_feeds"));
    }
    /**
     * Positive test case for getCurrentUserTrophies method with mandatory parameters or with api endpoint.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, description = "reddit {getCurrentUserTrophies} integration test with mandatory parameters.")
    public void testgetCurrentUserTrophiesWithMandatoryParameters() throws IOException, JSONException, Exception {
        esbRequestHeadersMap.put("Action", "getCurrentUserTrophies");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/api/"+connectorProperties.
                getProperty("apiVersion")+ "/me/trophies";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCurrentUserTrophies_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("trophies").getJSONObject(0).
                        getString("kind"), apiRestResponse.getBody().getJSONObject("data").getJSONArray("trophies").
                        getJSONObject(0).getString("kind"));
    }
    /**
     * Positive test case for redditSetPassword  method with mandatory parameters.
     * @throws IOException
     */
    @Test(priority = 6, description = "reddit {redditSetPassword} integration test with mandatory parameters.")
    public void testredditSetPasswordWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "redditSetPassword");
        Thread.sleep(60000);
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/password?name="+connectorProperties.
                getProperty("username");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_redditSetPassword.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String searchResponse= esbRestResponse.getBody().toString();
        String find ="an email will be sent to that account's address shortly";
        searchResponse.contains(find);
        boolean foundResponse = searchResponse.contains(find);
        Assert.assertEquals(foundResponse , true);
    }
    /**
     * Negative test case for redditSetPassword.
     * @throws IOException
     */
    @Test(priority = 7, description = "reddit {redditSetPassword} integration test negative case.")
    public void testredditSetPasswordWithNegativeCase() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "redditSetPassword");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/password?name="+connectorProperties.
                getProperty("Negusername");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_redditSetPassword_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String searchResponse= esbRestResponse.getBody().toString();
        String find ="that user doesn't exist";
        searchResponse.contains(find);
        boolean foundResponse = searchResponse.contains(find);
        Assert.assertEquals(foundResponse , true);
    }
    /**
     * Positive test case for getUserSubredditInfo method with mandatory parameters or with api endpoint.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 8, description = "reddit {getUserSubredditInfo} integration test with mandatory parameters.")
    public void testgetUserSubredditInfoWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserSubredditInfo");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth") + "/subreddits/mine";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserSubredditInfo_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject
                (0).getString("kind"), apiRestResponse.getBody().getJSONObject("data").getJSONArray("children").
                getJSONObject(0).getString("kind"));
    }
    /**
     * Positive test case for getSubredditKarma method with mandatory parameters or with api endpoint.
     * If you have any karma points, then only you can get the right response. Otherwise you get some null values(Eg:{"kind":
     * "KarmaList", "data": []})
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 9, description = "reddit {getSubredditKarma} integration test with mandatory parameters.")
    public void testgetSubredditKarmaWithMandatoryParameters() throws IOException, JSONException, Exception {
        esbRequestHeadersMap.put("Action", "getSubredditKarma");
        Thread.sleep(60000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/api/"+connectorProperties.
                getProperty("apiVersion")+ "/me/karma";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubredditKarma_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Positive test case for getAnyUserPublicMultisList method with mandatory parameters.
     * @throws IOException
     */
    @Test(priority = 10, description = "reddit {getAnyUserPublicMultisList} integration test with mandatory parameters.")
    public void testgetAnyUserPublicMultisListWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getAnyUserPublicMultisList");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/api/multi/user/username?username="+
                connectorProperties.getProperty("publicMultiUsername")+ "&expand_srs=" +connectorProperties.
                getProperty("expandSrs");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAnyUserPublicMultisList_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Positive test case for search method with mandatory parameters.
     * @throws IOException
     */
    @Test(priority = 11, description = "reddit {search} integration test with mandatory parameters.")
    public void testsearchWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "search");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/r/subreddit/search?after="+
                connectorProperties.getProperty("searchAfter")+ "&before=" +connectorProperties.
                getProperty("searchBefore")+ "&count=" +connectorProperties.getProperty("searchCount")+
                "&include_facets=" +connectorProperties.getProperty("searchIncludeFacets")+ "&limit=" +connectorProperties.
                getProperty("searchLimit")+ "&q=" +connectorProperties.getProperty("searchStr")+ "&restrict_sr=" +
                connectorProperties.getProperty("searchRestrictSr")+ "&syntax=" +connectorProperties.getProperty("searchSyntax")+
                "&t=" +connectorProperties.getProperty("searchTime")+ "&sort=" +connectorProperties.getProperty("searchSort");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONObject("facets").getJSONArray("subreddits").
                getJSONObject(0).getString("name"), apiRestResponse.getBody().getJSONObject("data").getJSONObject("facets").
                getJSONArray("subreddits").getJSONObject(0).getString("name"));
    }
    /**
     * Positive test case for search method with optional parameters.
     * @throws IOException
     */
    @Test(priority = 12, description = "reddit {search} integration test with Optional parameters.")
    public void testsearchWithOptionalParameters() throws IOException, JSONException, Exception {
        esbRequestHeadersMap.put("Action", "search");
        Thread.sleep(60000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/r/subreddit/search?after="+
                connectorProperties.getProperty("searchAfter")+ "&before=" +connectorProperties.
                getProperty("searchBefore")+ "&count=" +connectorProperties.getProperty("searchCount")+
                "&include_facets=" +connectorProperties.getProperty("searchIncludeFacets")+ "&limit=" +
                connectorProperties.getProperty("searchLimit")+ "&q=" +connectorProperties.getProperty("searchStr")+
                "&restrict_sr=" +connectorProperties.getProperty("searchRestrictSr")+ "&syntax=" +connectorProperties.
                getProperty("searchSyntax")+ "&t=" +connectorProperties.getProperty("searchTime")+
                "&sort=" +connectorProperties.getProperty("searchSort")+ "&show=" +connectorProperties.getProperty("searchShow")+
                "&sr_detail=" +connectorProperties.getProperty("searchSrDetail")+ "&type=" +connectorProperties.getProperty("searchType");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONObject("facets").getJSONArray("subreddits").
                getJSONObject(0).getString("name"), apiRestResponse.getBody().getJSONObject("data").getJSONObject("facets").
                getJSONArray("subreddits").getJSONObject(0).getString("name"));
        }
    /**
     * Positive test case for getUserSentMessagesInfo method with mandatory parameters.
     * @throws IOException
     */
    @Test(priority = 13, description = "reddit {getUserSentMessagesInfo} integration test with mandatory parameters.")
    public void testgetUserSentMessagesInfoWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserSentMessagesInfo");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/message/sent?after="+
                connectorProperties.getProperty("sentAfter")+ "&before=" +connectorProperties.getProperty("sentBefore")+
                "&count=" +connectorProperties.getProperty("sentCount")+ "&limit=" +connectorProperties.
                getProperty("sentLimit")+ "&mark=" +connectorProperties.getProperty("sentMark");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserSentMessagesInfo_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getString("kind"), apiRestResponse.getBody().getJSONObject("data").getJSONArray("children").
                        getJSONObject(0).getString("kind"));
    }
    /**
     * Positive test case for getUserSentMessagesInfo method with optional parameters.
     * @throws IOException
     */
    @Test(priority = 14, description = "reddit {getUserSentMessagesInfo} integration test with Optional parameters.")
    public void testgetUserSentMessagesInfoWithOptionalParameters() throws IOException, JSONException, Exception {
        esbRequestHeadersMap.put("Action", "getUserSentMessagesInfo");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/message/sent?after="+
            connectorProperties.getProperty("sentAfter")+ "&before=" +connectorProperties.getProperty("sentBefore")+
                "&count=" +connectorProperties.getProperty("sentCount")+ "&limit=" +connectorProperties.getProperty("sentLimit")+
                "&mark=" +connectorProperties.getProperty("sentMark")+ "&mid=" +connectorProperties.getProperty("sentMid")+
                "&show=" +connectorProperties.getProperty("sentShow")+ "&sr_detail=" +connectorProperties.getProperty("sentSrDetail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserSentMessagesInfo_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).getString("kind"),
                apiRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).getString("kind"));
    }
    /**
     * Positive test case for getUserInboxMessages method with mandatory parameters.
     * @throws IOException
     */
    @Test(priority = 15, description = "reddit {getUserInboxMessages} integration test with mandatory parameters.")
    public void testgetUserInboxMessagesWithMandatoryParameters() throws IOException, JSONException, Exception {
        esbRequestHeadersMap.put("Action", "getUserInboxMessages");
        Thread.sleep(60000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/message/inbox?after="+
                connectorProperties.getProperty("inboxAfter")+ "&before=" +connectorProperties.getProperty("inboxBefore")+
                "&count=" +connectorProperties.getProperty("inboxCount")+ "&limit=" +connectorProperties.
                getProperty("inboxLimit")+ "&mark=" +connectorProperties.getProperty("inboxMark");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserInboxMessages_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getString("kind"), apiRestResponse.getBody().getJSONObject("data").getJSONArray("children").
                        getJSONObject(0).getString("kind"));
    }
    /**
     * Positive test case for getUserInboxMessages method with optional parameters.
     * @throws IOException
     */
    @Test(priority = 16, description = "reddit {getUserInboxMessages} integration test with Optional parameters.")
    public void testgetUserInboxMessagesWithOptionalParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserInboxMessages");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/message/inbox?after="+
            connectorProperties.getProperty("inboxAfter")+ "&before=" +connectorProperties.getProperty("inboxBefore")+
                "&count=" +connectorProperties.getProperty("inboxCount")+ "&limit=" +connectorProperties.
                getProperty("inboxLimit")+ "&mark=" +connectorProperties.getProperty("inboxMark")+ "&mid=" +
                connectorProperties.getProperty("inboxMid")+ "&show=" +connectorProperties.getProperty("inboxShow")+
                "&sr_detail=" +connectorProperties.getProperty("inboxSrDetail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserInboxMessages_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getString("kind"), apiRestResponse.getBody().getJSONObject("data").getJSONArray("children").
                        getJSONObject(0).getString("kind"));
    }
    /**
     * Positive test case for getUserUpvotedHistory method with mandatory parameters.
     * @throws IOException
     */
    @Test(priority = 17, description = "reddit {getUserUpvotedHistory} integration test with mandatory parameters.")
    public void testgetUserUpvotedHistoryWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserUpvotedHistory");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/upvoted?after="+
                connectorProperties.getProperty("upvotedAfter")+ "&before=" +connectorProperties.getProperty("upvotedBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.
                getProperty("historyLimit")+ "&username=" +connectorProperties.getProperty("historyUsername")+
                "&t=" +connectorProperties.getProperty("historyTime")+ "&show=" +connectorProperties
                .getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserUpvotedHistory_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getJSONObject("data").getString("id"), apiRestResponse.getBody().getJSONObject("data").
                        getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("id"));
    }
    /**
     * Positive test case for getUserUpvotedHistory  method with optional parameters.
     * @throws IOException
     */
    @Test(priority = 18, description = "reddit {getUserUpvotedHistory} integration test with Optional parameters.")
    public void testgetUserUpvotedHistoryWithOptionalParameters() throws IOException, JSONException, Exception {
        esbRequestHeadersMap.put("Action", "getUserUpvotedHistory");
        Thread.sleep(60000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/upvoted?after="+
                connectorProperties.getProperty("upvotedAfter")+ "&before=" +connectorProperties.getProperty("upvotedBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.
                getProperty("historyLimit")+ "&username=" +connectorProperties.getProperty("historyUsername")+
                "&t=" +connectorProperties.getProperty("historyTime")+ "&show=" +connectorProperties.
                getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort")+
                "&sr_detail=" +connectorProperties.getProperty("historySrDetail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserUpvotedHistory_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getJSONObject("data").getString("id"), apiRestResponse.getBody().getJSONObject("data").
                        getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("id"));
    }
    /**
     * Positive test case for getUserDownvotedHistory method with mandatory parameters.
     * @throws IOException
     */
    @Test(priority = 19, description = "reddit {getUserDownvotedHistory} integration test with mandatory parameters.")
    public void testgetUserDownvotedHistoryWithMandatoryParameters() throws IOException, JSONException,Exception{
        esbRequestHeadersMap.put("Action", "getUserDownvotedHistory");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/downvoted?after="+
                connectorProperties.getProperty("downvotedAfter")+ "&before=" +connectorProperties.getProperty("downvotedBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.
                getProperty("historyLimit")+ "&username=" +connectorProperties.getProperty("historyUsername")+
                "&t=" +connectorProperties.getProperty("historyTime")+ "&show=" +connectorProperties.getProperty("historyShow")+
                "&sort=" +connectorProperties.getProperty("historySort");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserDownvotedHistory_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getJSONObject("data").getString("name"), apiRestResponse.getBody().getJSONObject("data").
                        getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("name"));
    }
    /**
     * Positive test case for getUserDownvotedHistory  method with optional parameters.
     * @throws IOException
     */
    @Test(priority = 20, description = "reddit {getUserDownvotedHistory} integration test with Optional parameters.")
    public void testgetUserDownvotedHistoryWithOptionalParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserDownvotedHistory");
        Thread.sleep(40000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/downvoted?after="+
                connectorProperties.getProperty("downvotedAfter")+ "&before=" +connectorProperties.getProperty("downvotedBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.getProperty("historyLimit")+
                "&username=" +connectorProperties.getProperty("historyUsername")+ "&t=" +connectorProperties.getProperty("historyTime")+
                "&show=" +connectorProperties.getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort")+
                "&sr_detail=" +connectorProperties.getProperty("historySrDetail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserDownvotedHistory_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getJSONObject("data").getString("name"), apiRestResponse.getBody().getJSONObject("data").
                        getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("name"));
    }
    /**
     * Positive test case for getUserSavedHistory method with mandatory parameters.
     * @throws IOException
     */
    @Test(priority = 21, description = "reddit {getUserSavedHistory} integration test with mandatory parameters.")
    public void testgetUserSavedHistoryWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserSavedHistory");
        Thread.sleep(60000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/saved?after="+
                connectorProperties.getProperty("savedAfter")+ "&before=" +connectorProperties.getProperty("savedBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.
                getProperty("historyLimit")+ "&username=" +connectorProperties.getProperty("historyUsername")+
                "&t=" +connectorProperties.getProperty("historyTime")+ "&show=" +connectorProperties.
                getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserSavedHistory_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getJSONObject("data").getString("subreddit_id"), apiRestResponse.getBody().getJSONObject("data").
                        getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("subreddit_id"));
    }
    /**
     * Positive test case for getUserSavedHistory  method with optional parameters.
     * @throws IOException
     */
    @Test(priority = 22, description = "reddit {getUserSavedHistory} integration test with Optional parameters.")
    public void testgetUserSavedHistoryWithOptionalParameters() throws IOException, JSONException, Exception {
        esbRequestHeadersMap.put("Action", "getUserSavedHistory");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/saved?after="+
                connectorProperties.getProperty("savedAfter")+ "&before=" +connectorProperties.getProperty("savedBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.getProperty("historyLimit")+
                "&username=" +connectorProperties.getProperty("historyUsername")+ "&t=" +connectorProperties.getProperty("historyTime")+
                "&show=" +connectorProperties.getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort")+
                "&sr_detail=" +connectorProperties.getProperty("historySrDetail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserSavedHistory_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getJSONObject("data").getString("subreddit_id"), apiRestResponse.getBody().getJSONObject("data").
                        getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("subreddit_id"));
    }
    /**
     * Positive test case for getUserModeratorSubreddit  method with mandatory parameters.
     * if you have any moderator subreddit for your account , then only you can have right response for this method
     * @throws IOException
     */
    @Test(priority = 23, description = "reddit {getUserModeratorSubreddit} integration test with mandatory parameters.")
    public void testgetUserModeratorSubredditWithMandatoryParameters() throws IOException, JSONException, Exception{
        esbRequestHeadersMap.put("Action", "getUserModeratorSubreddit");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/subreddits/mine/moderator?after="+
                connectorProperties.getProperty("after")+ "&before=" +connectorProperties.getProperty("before")+
                "&count=" +connectorProperties.getProperty("count")+ "&limit=" +connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserModeratorSubreddit_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Positive test case for getUserModeratorSubreddit  method with optional parameters.
     *if you have any moderator subreddit for your account , then only you can have right response for this method
     * @throws IOException
     */
    @Test(priority = 24, description = "reddit {getUserModeratorSubreddit} integration test with Optional parameters.")
    public void testgetUserModeratorSubredditWithOptionalParameters() throws IOException, JSONException,Exception{
        esbRequestHeadersMap.put("Action", "getUserModeratorSubreddit");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/subreddits/mine/moderator?after="+
                connectorProperties.getProperty("after")+ "&before=" +connectorProperties.getProperty("before")+
                "&count=" +connectorProperties.getProperty("count")+ "&limit=" +connectorProperties.getProperty("limit")+
                "&show=" +connectorProperties.getProperty("show")+ "&sr_detail=" +connectorProperties.getProperty("srDetail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserModeratorSubreddit_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Negative test case for getUserModeratorSubreddit.
     * @throws IOException
     */
        @Test(priority = 25,description = "reddit {getUserModeratorSubreddit} integration test negative case.")
        public void testgetUserModeratorSubredditWithNegativeCase() throws IOException, JSONException,Exception {
            esbRequestHeadersMap.put("Action", "getUserModeratorSubreddit");
            Thread.sleep(60000);
            String apiEndPoint = connectorProperties.getProperty("apiUrlOauth") + "/subreddits/mine/moderator?after=" +
                    connectorProperties.getProperty("NegAfter") + "&before=" + connectorProperties.getProperty("NegBefore") +
                    "&count=" + connectorProperties.getProperty("count") + "&limit=" + connectorProperties.getProperty("limit");
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserModeratorSubreddit_negative.json");
            RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
            Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
        }
    /**
     * Positive test case for getUserNotifications  method with mandatory parameters.
     * if you have any notifications at the moment, then only you can have right response for this method
     * @throws IOException
     */
    @Test(priority = 26, description = "reddit {getUserNotifications} integration test with mandatory parameters.")
    public void testgetUserNotificationsWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserNotifications");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/api/"+connectorProperties.
                getProperty("apiVersion")+ "/me/notifications?count="+
                connectorProperties.getProperty("count")+ "&sort=" +connectorProperties.getProperty("sort");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserNotifications_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Positive test case for getUserNotifications  method with optional parameters.
     * @throws IOException
     */
    @Test(priority = 27, description = "reddit {getUserNotifications} integration test with Optional parameters.")
    public void testgetUserNotificationsWithOptionalParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserNotifications");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/api/"+connectorProperties.
                getProperty("apiVersion")+"/me/notifications?count="+ connectorProperties.getProperty("count")+
                "&sort=" +connectorProperties.getProperty("sort")+ "&end_date=" +connectorProperties.getProperty("endDate")+
                "&start_date=" +connectorProperties.getProperty("startDate");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserNotifications_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Negative test case for getUserNotifications.
     * @throws IOException
     */
        @Test(priority = 28,description = "reddit {getUserNotifications} integration test negative case.")
        public void testgetUserNotificationsWithNegativeCase() throws IOException, JSONException, Exception{
            esbRequestHeadersMap.put("Action", "getUserNotifications");
            Thread.sleep(60000);
            String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/api/"+connectorProperties.
                    getProperty("apiVersion")+"/me/notifications?count="+ connectorProperties.getProperty("count")+
                    "&sort=" +connectorProperties.getProperty("Negsort");
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserNotifications_negative.json");
            RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
            Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
        }
    /**
     * Positive test case for reddit_register  method with mandatory parameters.
     * You need to use the new name or unique one (not existing name which is registered in reddit account).
     * @throws IOException
     */
    @Test(priority = 29, description = "reddit {reddit_register} integration test with mandatory parameters.")
    public void testreddit_registerWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "reddit_register");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/register?user="+connectorProperties.
                getProperty("userRegister")+"&email="+connectorProperties.getProperty("email")+"&passwd="+connectorProperties.
                getProperty("password1")+"&passwd2="+connectorProperties.getProperty("password2")+
                "&dest="+connectorProperties.getProperty("destination")+"&rem="+connectorProperties.
                getProperty("rem")+"&reason="+connectorProperties.getProperty("reason");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_reddit_register_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String searchResponse= esbRestResponse.getBody().toString();
        String find ="that username is already taken";
        searchResponse.contains(find);
        boolean foundResponse = searchResponse.contains(find);
        Assert.assertEquals(foundResponse , false);
    }
    /**
     * Negative test case for reddit_register.
     * Test with existing reddit user name.
     * @throws IOException
     */
    @Test(priority = 30, description = "reddit {reddit_register} integration test negative case.")
    public void testreddit_registerWithNegativeCase() throws IOException, JSONException, Exception {
        esbRequestHeadersMap.put("Action", "reddit_register");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/register?user="+connectorProperties.
                getProperty("NeguserRegister")+"&email="+connectorProperties.getProperty("email")+"&passwd="+connectorProperties.
                getProperty("password1")+"&passwd2="+connectorProperties.getProperty("password2")+
                "&dest="+connectorProperties.getProperty("destination")+"&rem="+connectorProperties.
                getProperty("rem")+"&reason="+connectorProperties.getProperty("reason");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_reddit_register_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String searchResponse= esbRestResponse.getBody().toString();
        String find ="that username is already taken";
        searchResponse.contains(find);
        boolean foundResponse = searchResponse.contains(find);
        Assert.assertEquals(foundResponse , true);
    }
    /**
     * Negative test case for getUserSavedHistory.
     * @throws IOException
     */
    @Test(priority = 31, description = "reddit {getUserSavedHistory} integration test negative case.")
    public void testgetUserSavedHistoryWithNegativeCase() throws IOException, JSONException,Exception{
        esbRequestHeadersMap.put("Action", "getUserSavedHistory");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserSavedHistory_negative.json");
         String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/saved?after="+
                connectorProperties.getProperty("NegAfter")+ "&before=" +connectorProperties.getProperty("NegBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.
                 getProperty("historyLimit")+ "&username=" +connectorProperties.getProperty("historyUsername")+
                "&t=" +connectorProperties.getProperty("historyTime")+ "&show=" +connectorProperties.
                 getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Negative test case for getUserCommentsHistory.
     * @throws IOException
     */
    @Test(priority = 32, description = "reddit {getUserCommentsHistory} integration test negative case.")
    public void testgetUserCommentsHistoryWithNegativeCase() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserCommentsHistory");
        Thread.sleep(60000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/comments?after="+
                connectorProperties.getProperty("NegAfter")+ "&before=" +connectorProperties.getProperty("NegBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.
                getProperty("historyLimit")+ "&username=" +connectorProperties.getProperty("historyUsername")+
                "&t=" +connectorProperties.getProperty("historyTime")+ "&show=" +connectorProperties.
                getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserCommentsHistory_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Negative test case for getUserUpvotedHistory.
     * @throws IOException
     */
    @Test(priority = 33, description = "reddit {getUserUpvotedHistory} integration test negative case.")
    public void testgetUserUpvotedHistoryWithNegativeCase() throws IOException, JSONException,Exception{
        esbRequestHeadersMap.put("Action", "getUserUpvotedHistory");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/upvoted?after="+
                connectorProperties.getProperty("NegAfter")+ "&before=" +connectorProperties.getProperty("NegBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.
                getProperty("historyLimit")+ "&username=" +connectorProperties.getProperty("historyUsername")+
                "&t=" +connectorProperties.getProperty("historyTime")+ "&show=" +connectorProperties.
                getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserUpvotedHistory_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Negative test case for getUserDownvotedHistory.
     * @throws IOException
     */
    @Test(priority = 34, description = "reddit {getUserDownvotedHistory} integration test negative case.")
    public void testgetUserDownvotedHistoryWithNegativeCase() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserDownvotedHistory");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/downvoted?after="+
                connectorProperties.getProperty("NegAfter")+ "&before=" +connectorProperties.getProperty("NegBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.
                getProperty("historyLimit")+ "&username=" +connectorProperties.getProperty("historyUsername")+
                "&t=" +connectorProperties.getProperty("historyTime")+ "&show=" +connectorProperties.
                getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserDownvotedHistory_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Negative test case for getUserSentMessagesInfo.
     * @throws IOException
     */
    @Test(priority = 35, description = "reddit {getUserSentMessagesInfo} integration test negative case.")
    public void testgetUserSentMessagesInfoWithNegativeCase() throws IOException, JSONException,Exception{
        esbRequestHeadersMap.put("Action", "getUserSentMessagesInfo");
        Thread.sleep(60000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/message/sent?after="+
                connectorProperties.getProperty("NegAfter")+ "&before=" +connectorProperties.getProperty("NegBefore")+
                "&count=" +connectorProperties.getProperty("inboxCount")+ "&limit=" +connectorProperties.
                getProperty("inboxLimit")+ "&mark=" +connectorProperties.getProperty("inboxMark");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserSentMessagesInfo_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Negative test case for getUserInboxMessages.
     * @throws IOException
     */
    @Test(priority = 36, description = "reddit {getUserInboxMessages} integration test negative case.")
    public void testgetUserInboxMessagesWithNegativeCase() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserInboxMessages");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/message/inbox?after="+
                connectorProperties.getProperty("NegInboxAfter")+ "&before=" +connectorProperties.getProperty("NegInboxBefore")+
                "&count=" +connectorProperties.getProperty("inboxCount")+ "&limit=" +connectorProperties.
                getProperty("inboxLimit")+ "&mark=" +connectorProperties.getProperty("inboxMark");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserInboxMessages_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Negative test case for search.
     * @throws IOException
     */
    @Test(priority = 37, description = "reddit {search} integration test negative case.")
    public void testsearchWithNegativeCase() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "search");
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/r/subreddit/search?after="+
                connectorProperties.getProperty("NegsearchAfter")+ "&before=" +connectorProperties.
                getProperty("NegsearchBefore")+ "&count=" +connectorProperties.getProperty("searchCount")+
                "&include_facets=" +connectorProperties.getProperty("searchIncludeFacets")+ "&limit=" +connectorProperties.
                getProperty("searchLimit")+ "&q=" +connectorProperties.getProperty("searchStr")+ "&restrict_sr="
                +connectorProperties.getProperty("searchRestrictSr")+ "&syntax=" +connectorProperties.getProperty("Negseacrhsyntax")+
                "&t=" +connectorProperties.getProperty("searchTime")+ "&sort=" +connectorProperties.getProperty("searchSort");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    /**
     * Positive test case for getUserCommentsHistory method with mandatory parameters.
     * @throws IOException
     */
    @Test(priority = 38, description = "reddit {getUserCommentsHistory} integration test with mandatory parameters.")
    public void testgetUserCommentsHistoryWithMandatoryParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserCommentsHistory");
        Thread.sleep(60000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/comments?after="+
                connectorProperties.getProperty("commentAfter")+ "&before=" +connectorProperties.getProperty("commentBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.
                getProperty("historyLimit")+ "&username=" +connectorProperties.getProperty("historyUsername")+
                "&t=" +connectorProperties.getProperty("historyTime")+ "&show=" +connectorProperties.
                getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserCommentsHistory_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getJSONObject("data").getString("subreddit_id"), apiRestResponse.getBody().getJSONObject("data").
                        getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("subreddit_id"));
    }
    /**
     * Positive test case for getUserCommentsHistory  method with optional parameters.
     * @throws IOException
     */
    @Test(priority = 39, description = "reddit {getUserCommentsHistory} integration test with Optional parameters.")
    public void testgetUserCommentsHistoryWithOptionalParameters() throws IOException, JSONException,Exception {
        esbRequestHeadersMap.put("Action", "getUserCommentsHistory");
        Thread.sleep(40000);
        String apiEndPoint = connectorProperties.getProperty("apiUrlOauth")+"/user/username/comments?after="+
                connectorProperties.getProperty("commentAfter")+ "&before=" +connectorProperties.getProperty("commentBefore")+
                "&count=" +connectorProperties.getProperty("historyCount")+ "&limit=" +connectorProperties.
                getProperty("historyLimit")+ "&username=" +connectorProperties.getProperty("historyUsername")+
                "&t=" +connectorProperties.getProperty("historyTime")+ "&show=" +connectorProperties.
                getProperty("historyShow")+ "&sort=" +connectorProperties.getProperty("historySort")+
                "&sr_detail=" +connectorProperties.getProperty("historySrDetail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserCommentsHistory_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("children").getJSONObject(0).
                        getJSONObject("data").getString("subreddit_id"), apiRestResponse.getBody().getJSONObject("data").
                        getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("subreddit_id"));
    }
}