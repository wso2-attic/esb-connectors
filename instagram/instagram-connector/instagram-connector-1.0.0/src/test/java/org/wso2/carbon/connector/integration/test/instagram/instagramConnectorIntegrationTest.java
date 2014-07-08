/**
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

package org.wso2.carbon.connector.integration.test.instagram;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.util.HashMap;
import java.util.Map;


/**
 * Integration test class for instagram connector.
 */
public class instagramConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("instagram-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");

    }


    /*Positive test case for getUserInfo method with mandatory parameters.
    * */
    @Test(groups = {"wso2.esb"}, description = "instagram {getUserInfo} integration test with mandatory parameters.")
    public void testGetUserInfoWithManditoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserInfo");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/?access_token=" + connectorProperties.getProperty("access_token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserInfo_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("username"), apiRestResponse.getBody().getJSONObject("data").get("username"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("id"), apiRestResponse.getBody().getJSONObject("data").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("full_name"), apiRestResponse.getBody().getJSONObject("data").get("full_name"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /**
     * Negative test case for getUserInfo method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {getUserInfo} integration test with mandatory parameters.")
    public void testGetUserInfoWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserInfo");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/?access_token=" + connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserInfo_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }


    /*Positive test case for getUserInfo method with mandatory parameters.
    *
    * */
    @Test(groups = {"wso2.esb"}, description = "instagram {getUserInfo} integration test with mandatory parameters.")
    public void testGetUserInfooWithManditoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserInfo");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/?access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserInfo_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("username"), apiRestResponse.getBody().getJSONObject("data").get("username"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /* Positive test case for getUserFollows method with mandatory parameters.
    *
    * */


    @Test(groups = {"wso2.esb"}, description = "instagram {getUserFollows} integration test with mandatory parameters.")
    public void testGetgetUserFollowsWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserFollows");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/follows?access_token=" + connectorProperties.getProperty("access_token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserFollows_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);


    }


    /**
     * Negative test case for getUserFollows method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {getUserFollows} integration test with mandatory parameters.")
    public void testGetgetUserFollowsWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserFollows");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/follows?access_token=" + connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserFollows_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);


    }


   /* Positive test case for getSelfFeed method with mandatory parameters.
   *
   * */


    @Test(groups = {"wso2.esb"}, description = "instagram { getSelfMediaLiked} integration test with mandatory parameters.")
    public void testGetSelfMediaLikedWithmandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getSelfMediaLiked");
        String apiEndPoint = "https://api.instagram.com/v1/users/self/media/liked?" + "access_token=" + connectorProperties.getProperty("access_token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfMediaLiked_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("pagination").toString(), esbRestResponse.getBody().getJSONObject("pagination").toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /**
     * Negative test case for getSelfFeed method with mandatory parameters.
     */

    @Test(groups = {"wso2.esb"}, description = "instagram { getSelfMediaLiked} integration test with mandatory parameters.")
    public void testGetSelfFeedWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getSelfMediaLiked");
        String apiEndPoint = "https://api.instagram.com/v1/users/self/media/liked?" + "access_token=" + connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfFeed_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

    }


    /*Positive test case for getSelfFeed method with optional parameters.
    *
    * */


    @Test(groups = {"wso2.esb"}, description = "instagram {getSelfFeed} integration test with optional parameters.")
    public void testGetSelfFeedWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getSelfFeed");
        String apiEndPoint = "https://api.instagram.com/v1/users/self/feed?" + "access_token=" + connectorProperties.getProperty("access_token") + "&count=" + connectorProperties.getProperty("count") + "&min_id=" + connectorProperties.getProperty("min_id") + "&max_id" + connectorProperties.getProperty("max_id");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfFeed_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").toString(), apiRestResponse.getBody().getJSONObject("pagination").toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

    }


    /**
     * Positive test case for getUserMediaRecent method with mandatory parameters.
     */

    @Test(groups = {"wso2.esb"}, description = "instagram { getUserMediaRecent} integration test with mandatory parameters.")
    public void testGetUserMediaRecentWithmandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserMediaRecent");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserMediaRecent_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").toString(), apiRestResponse.getBody().getJSONObject("pagination").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("code").toString(), apiRestResponse.getBody().getJSONObject("meta").get("code").toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /**
     * Positive test case for getUserMediaRecent method with optional parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getUserMediaRecent} integration test with optional parameters.")
    public void testGetUserMediaRecentWithoptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserMediaRecent");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("access_token") + "&count=" + connectorProperties.getProperty("count") + "&max_like_id=" + connectorProperties.getProperty("max_like_id") + "&max_timestamp=" + connectorProperties.getProperty("max_timestamp") + "&min_timestamp=" + connectorProperties.getProperty("min_timestamp");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUsermediaRecent_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /**
     * Negative test case for getUserMediaRecent method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getUserMediaRecent} integration test with mandatory parameters.")
    public void testGetUserMediaRecentWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserMediaRecent");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserMediaRecent_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);


    }


    /**
     * Positive test case for getSelfRequestedBy method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getSelfRequestedBy} integration test with mandatory parameters.")
    public void testGetSelfRequestedByWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getSelfRequestedBy");
        String apiEndPoint = "https://api.instagram.com/v1/users/self/requested-by?access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfRequestedBy_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /**
     * Positive test case for getSelfRequestedBy method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getUserFollowedBy} integration test with mandatory parameters.")
    public void testGetUserFollowedByMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserFollowedBy");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/followed-by?" + "access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserFollowedBy_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("bio"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("bio"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("website"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("website"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);


    }


    /**
     * Negative test case for getUserFollowedBy method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getUserFollowedBy} integration test with mandatory parameters.")
    public void testGetUserFollowedByNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserFollowedBy");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/followed-by?" + "access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserFollowedBy_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);


    }


    /**
     * Positive test case for getTagInfo method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getTagInfo} integration test with mandatory parameters.")
    public void testGetTagInfoMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTagInfo");
        String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tag_name") + "?access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagInfo_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("name"), apiRestResponse.getBody().getJSONObject("data").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("media_count"), apiRestResponse.getBody().getJSONObject("data").get("media_count"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

    }


    /**
     * Negative test case for getTagInfo method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getTagInfo} integration test with mandatory parameters.")
    public void testGetTagedInfoNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTagInfo");
        String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tag_name") + "?access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagInfo_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

    }


    /**
     * Positive test case for getTagRecent method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getTagRecent} integration test with mandatory parameters.")
    public void testGetTagRecentMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTagRecent");
        String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tag_name") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagRecent_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("attribution"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("attribution"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /**
     * Positive test case for getTagRecent method with optional parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getTagRecent} integration test with optional parameters.")
    public void testGetTagRecentNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTagRecent");
        String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tag_name") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("access_token") + "&max_id=" + connectorProperties.getProperty("max_id") + "&min_id=" + connectorProperties.getProperty("min_id");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagRecent_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("attribution"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("attribution"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

    }


    /**
     * Negative test case for getTagRecent method with mandatory parameters.
     */

    @Test(groups = {"wso2.esb"}, description = "instagram { getTagRecent} integration test with mandatory parameters.")
    public void testGetTagRecentWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTagRecent");
        String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tag_name") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagRecent_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }


    /**
     * Positive test case for getTagRecent method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { tagSearch} integration test with mandatory parameters.")
    public void tesTagSearchWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:tagSearch");
        String apiEndPoint = "https://api.instagram.com/v1/tags/search?q=" + connectorProperties.getProperty("query") + "&access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagSearch_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("media_count"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("media_count"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /**
     * Negative test case for getTagRecent method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { tagSearch} integration test with mandatory parameters.")
    public void tesTagSearchhWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:tagSearch");
        String apiEndPoint = "https://api.instagram.com/v1/tags/search?q=" + connectorProperties.getProperty("query") + "&access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagSearch_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }


    /**
     * Negative test case for getSelfFeed method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {getSelfFeed} integration test with optional parameters.")
    public void testGetSelfFeeedWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getSelfFeed");
        String apiEndPoint = "https://api.instagram.com/v1/users/self/feed?" + "access_token=" + connectorProperties.getProperty("token") + "&count=" + connectorProperties.getProperty("count") + "&min_id=" + connectorProperties.getProperty("min_id") + "&max_id" + connectorProperties.getProperty("max_id");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfFeed_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);


    }


    /**
     * Negative test case for getSelfMediaLiked method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getSelfMediaLiked} integration test with mandatory parameters.")
    public void testGetSelfMediaLikedWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getSelfMediaLiked");
        String apiEndPoint = "https://api.instagram.com/v1/users/self/media/liked?" + "access_token=" + connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfMediaLiked_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }


    /**
     * Negative test case for getTagInfo method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getTagInfo} integration test with mandatory parameters.")
    public void testGetTagInfoNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTagInfo");
        String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tag_name") + "?access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagInfo_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);


    }


    /**
     * Positive test case for getMediaInfo method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaInfo} integration test with mandatory parameters.")
    public void testMediaInfoWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getMediaInfo");
        String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("media_id") + "/?access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaInfo_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").get("type"), esbRestResponse.getBody().getJSONObject("data").get("type"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);


    }


    /**
     * Negative test case for getMediaInfo method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaInfo} integration test with mandatory parameters.")
    public void tesTagSearchWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getMediaInfo");
        String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("media_id") + "/?access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaInfo_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

    }


    /**
     * Positive test case for getMediaPopular method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaPopular} integration test with mandatory parameters.")
    public void tesGetMediaPopularMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getMediaPopular");
        String apiEndPoint = "https://api.instagram.com/v1/media/popular" + "?access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaPopular_positive.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("attribution"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("attribution"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /**
     * Negative test case for getMediaPopular method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaPopular} integration test with mandatory parameters.")
    public void tesGetMediaPopularNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getMediaPopular");
        String apiEndPoint = "https://api.instagram.com/v1/media/popular" + "?access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaPopular_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }


    /**
     * Positive test case for getMediaSearch method with optional parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaSearch} integration test with optional parameters.")
    public void tesGetMediaSearchOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getMediaSearch");
        String apiEndPoint = "https://api.instagram.com/v1/media/search?lat=" + connectorProperties.getProperty("lat") + "&lng=" + connectorProperties.getProperty("lng") + "&max_timestamp=" + connectorProperties.getProperty("max_timestamp") + "&min_timestamp=" + connectorProperties.getProperty("min_timestamp") + "&distance=" + connectorProperties.getProperty("distance") + "&access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaSearch_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("type"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("type"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

    }


    /**
     * Positive test case for getMediaSearch method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaSearch} integration test with mandatory parameters.")
    public void tesGetMediaSearchMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getMediaSearch");
        String apiEndPoint = "https://api.instagram.com/v1/media/search?lat=" + connectorProperties.getProperty("lat") + "&lng=" + connectorProperties.getProperty("lng") + "&access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaSearch_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("type"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("type"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /**
     * Negative test case for getMediaSearch method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaSearch} integration test with mandatory parameters.")
    public void tesGetMediaSearchWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getMediaSearch");
        String apiEndPoint = "https://api.instagram.com/v1/media/search?lat=" + connectorProperties.getProperty("lat") + "&lng=" + connectorProperties.getProperty("lng") + "&access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaSearch_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }


    /**
     * Positive test case for getMediaLike method with mandatory parameters. LIKES
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaLike} integration test with mandatory parameters.")
    public void tesGetMediaLikeWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getMediaLike");
        String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("media_id") + "/likes?" + "access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaLike_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);


    }


    /**
     * Negative test case for getMediaLike method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaLike} integration test with negative parameters.")
    public void tesGetMediaLikeWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getMediaLike");
        String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("media_id") + "/likes?" + "access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaLike_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }


    /**
     * Positive test case for userSearch method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { userSearch} integration test with mandatory parameters.")
    public void tesUserSearchWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:userSearch");
        String apiEndPoint = "https://api.instagram.com/v1/users/search?q=" + connectorProperties.getProperty("query_name") + "&access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_userSearch_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString(), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);


    }


    /**
     * Negative test case for userSearch method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { userSearch} integration test with mandatory parameters.")
    public void tesUserSearchWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:userSearch");
        String apiEndPoint = "https://api.instagram.com/v1/users/search?q=" + connectorProperties.getProperty("query_name") + "&access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_userSearch_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);


    }


    /**
     * Positive test case for userSearch method with optional parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram { userSearch} integration test with optional parameters.")
    public void tesUserSearchWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:userSearch");
        String apiEndPoint = "https://api.instagram.com/v1/users/search?q=" + connectorProperties.getProperty("query_name") + "&count=" + connectorProperties.getProperty("count") +
                "&access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_userSearch_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString(), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);


    }


    /**
     * Positive test case for getLocationInfo method with optional parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {getLocationInfo} integration test with mandatory parameters.")
    public void testGetLocationInfoWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLocationInfo");

        String apiEndPoint = "https://api.instagram.com/v1/locations/" + connectorProperties.getProperty("location_id") + "?access_token=" + connectorProperties.getProperty("access_token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_location_info_mandatory.txt");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("longitude"), apiRestResponse.getBody().getJSONObject("data").get("longitude"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

    }


    /**
     * Negative test case for getLocationRecent method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {getLocationInfo} integration test with negative parameters.")
    public void testGetLocationInfoWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLocationInfo");

        String apiEndPoint = "https://api.instagram.com/v1/locations/" + connectorProperties.getProperty("location_id") + "?access_token=" + connectorProperties.getProperty("token");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_location_info_negative.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);


    }


    /**
     * Positive test case for getLocationRecent method with optional parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {getLocationRecent} integration test with optional parameters.")
    public void testGetLocationRecentWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getLocationRecent");

        String apiEndPoint = "https://api.instagram.com/v1/locations/" + connectorProperties.getProperty("location_id") + "/media/recent?access_token=" + connectorProperties.getProperty("access_token") + "&amp;max_timestamp=" + connectorProperties.getProperty("max_timestamp") + "&amp;min_timestamp=" + connectorProperties.getProperty("min_timestamp");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_location_recent_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").get("next_max_id"), apiRestResponse.getBody().getJSONObject("pagination").get("next_max_id"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);


    }


    /**
     * Positive test case for getLocationRecent method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {getLocationRecent} integration test with mandatory parameters.")
    public void testGetLocationRecentWithMandatoryParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getLocationRecent");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_location_recent_mandatory.txt");


        String apiEndPoint = "https://api.instagram.com/v1/locations/" + connectorProperties.getProperty("location_id") + "/media/recent?access_token=" + connectorProperties.getProperty("access_token");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").get("next_max_id"), apiRestResponse.getBody().getJSONObject("pagination").get("next_max_id"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);


    }


    /**
     * Positive test case for locationSearch method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {locationSearch} integration test with mandatory parameters.")
    public void testLocationSearchWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:locationSearch");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_location_search_mandatory.txt");


        String apiEndPoint = "https://api.instagram.com/v1/locations/search?lat=" + connectorProperties.getProperty("lat") + "&amp;lng=" + connectorProperties.getProperty("lng") + "&amp;access_token=" + connectorProperties.getProperty("access_token");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("name"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("name"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);


    }


    /**
     * Positive test case for locationSearch method with optional parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {locationSearch} integration test with optional parameters.")
    public void testLocationSearchWithOptionalParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:locationSearch");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_location_search_optional.txt");


        String apiEndPoint = "https://api.instagram.com/v1/locations/search?lat=" + connectorProperties.getProperty("lat") + "&amp;lng=" + connectorProperties.getProperty("lng") + "&amp;access_token=" + connectorProperties.getProperty("access_token") + "&amp;distance=" + connectorProperties.getProperty("distance");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("name"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("name"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);


    }


    /**
     * Negative test case for locationSearch method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {locationSearch} integration test with negative parameters.")
    public void testLocationSearchWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:locationSearch");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_location_search_negative.txt");

        String apiEndPoint = "https://api.instagram.com/v1/locations/search?lat=" + connectorProperties.getProperty("lat") + "&amp;lng=" + connectorProperties.getProperty("lng") + "&amp;access_token=" + connectorProperties.getProperty("token");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

    }


    /**
     * Positive test case for userSearch method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {getMediaComment} integration test with mandatory parameters.")
    public void testGetMediaCommentWithMandatoryParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getMediaComment");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_media_comment_mandatory.txt");

        String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("media_id") + "/comments?access_token=" + connectorProperties.getProperty("access_token");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("text"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("text"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

    }


    /**
     * Negative test case for userSearch method with mandatory parameters.
     */


    @Test(groups = {"wso2.esb"}, description = "instagram {getMediaComment} integration test with mandatory parameters.")
    public void testGetMediaCommentWithNegativeParameters() throws Exception {


        esbRequestHeadersMap.put("Action", "urn:getMediaComment");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_media_comment_negative.txt");

        String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("media_id") + "/comments?access_token=" + connectorProperties.getProperty("token");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

    }





   /* Positive test case for getUserRelationship method with mandatory parameters.
   *
   * */


    @Test(groups = {"wso2.esb"}, description = "instagram {getUserRelationship} integration test with mandatory parameters.")
    public void testGetUserRelationshipWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserRelationship");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/relationship?access_token=" + connectorProperties.getProperty("access_token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserRelationship_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("outgoing_status"), apiRestResponse.getBody().getJSONObject("data").get("outgoing_status"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }



 /* Negative test case for getUserRelationship method with mandatory parameters.
   *
   * */


    @Test(groups = {"wso2.esb"}, description = "instagram {getUserRelationship} integration test with mandatory parameters.")
    public void testGetUserRelationshipWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserRelationship");
        String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("user_id") + "/relationship?access_token=" + connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserRelationship_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);


    }

}
