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

package org.wso2.carbon.connector.integration.test.soundcloud;

import org.apache.axis2.context.ConfigurationContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;

import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;


import javax.activation.DataHandler;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SoundcloudConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> parametersMap = new HashMap<String, String>();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("soundcloud-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");


        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/json");

    }



    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getUser")
    public void testSoundcloudGetUserPositive() throws Exception {

        //http://api.soundcloud.com/users/99675972.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUser");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("userId")+
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));

    }


    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getUserComments")
    public void testSoundcloudGetUserCommentsPositive() throws Exception {

        //http://api.soundcloud.com/users/99675972/comments.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserComments");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("userId")+ "/comments" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }






    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getUserFavorites")
    public void testSoundcloudGetUserFavoritesPositive() throws Exception {

        //http://api.soundcloud.com/users/99675972/favorites.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserFavorites");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("userId")+ "/favorites" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }




    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getUserFollowers")
    public void testSoundcloudGetUserFollowersPositive() throws Exception {

        //http://api.soundcloud.com/users/99675972/followers.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserFollowers");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("userId")+ "/followers" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }

      @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getUserFollowings")
    public void testSoundcloudGetUserFollowingsPositive() throws Exception {

        //http://api.soundcloud.com/users/99675972/followings.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserFollowings");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("userId")+ "/followings" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

          RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }




    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getUserGroups")
    public void testSoundcloudGetUserGroupsPositive() throws Exception {


        //http://api.soundcloud.com/users/99675972/groups.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserGroups");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("userId")+ "/groups" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }




    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getUserPlaylists")
    public void testSoundcloudGetUserPlaylistsPositive() throws Exception {

        //http://api.soundcloud.com/users/99675972/playlists.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserPlaylists");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("userId")+ "/playlists" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }





    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getUserTracks")
    public void testSoundcloudGetUserTracksPositive() throws Exception {

        //http://api.soundcloud.com/users/99675972/tracks.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserTracks");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("userId")+ "/tracks" +
                ".json?client_id="+connectorProperties.getProperty("clientId");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }




    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getUserWebProfile")
    public void testSoundcloudGetUserWebProfilesPositive() throws Exception {

        //http://api.soundcloud.com/users/99675972/web-profiles.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserWebProfile");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("userId")+ "/web-profiles" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }





    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud searchUsers")
    public void testSoundcloudSearchUserPositive() throws Exception {

        //http://api.soundcloud.com/users.json?client_id=21fded24c32c2d9b0316971643d2f75f&q=manilsl

        esbRequestHeadersMap.put("Action", "urn:searchUsers");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users" +
                ".json?client_id="+connectorProperties.getProperty("clientId")+
                "&q="+connectorProperties.getProperty("searchUser");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_searchusers_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }





    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getMe")
    public void testSoundcloudGetMePositive() throws Exception {

        //https://api.soundcloud.com/me.json?oauth_token=1-84493-99675972-bb6c61a90fa2b08

        esbRequestHeadersMap.put("Action", "urn:getMe");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me" +
                ".json?oauth_token="+connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));

    }



    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getConnections")
    public void testSoundcloudGetConnectionsPositive() throws Exception {

        //https://api.soundcloud.com/me/connections.json?oauth_token=1-84493-99675972-bb6c61a90fa2b08

        esbRequestHeadersMap.put("Action", "urn:getConnections");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/connections" +
                ".json?oauth_token="+connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));


    }


    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getConnectionById")
    public void testSoundcloudGetConnectionByIdPositive() throws Exception {

        //https://api.soundcloud.com/me/connections/61945938.json?oauth_token=1-84493-99675972-bb6c61a90fa2b08

        esbRequestHeadersMap.put("Action", "urn:getConnectionById");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/connections/" +
                connectorProperties.getProperty("connectionId") +
                ".json?oauth_token="+connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_connectionId_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));

    }


    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud setConnections")
    public void testSoundcloudSetConnectionsPositive() throws Exception {

        //https://api.soundcloud.com/me/connections.json?oauth_token=1-84493-99675972-bb6c61a90fa2b08

        esbRequestHeadersMap.put("Action", "urn:setConnections");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/connections" +
                ".json?oauth_token="+connectorProperties.getProperty("token")+
                "&service="+connectorProperties.getProperty("serviceType")+"&redirect_uri="  + connectorProperties.getProperty("redirectUrl");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"mandatory_esb_set_connection.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("authorize_url"), apiRestResponse.getBody().get("authorize_url"));

    }


    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getActivities")
    public void testSoundcloudActivitiesPositive() throws Exception {

        //https://api.soundcloud.com/me/activities.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getActivities");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities" +
                ".json?oauth_token="+connectorProperties.getProperty("token") + "&limit=";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("future_href"), apiRestResponse.getBody().get("future_href"));

    }


    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getAllActivities")
    public void testSoundcloudAllActivitiesPositive() throws Exception {

        //https://api.soundcloud.com/me/activities/all.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getAllActivities");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/all" +
                ".json?oauth_token="+connectorProperties.getProperty("token")+ "&limit=";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("future_href"), apiRestResponse.getBody().get("future_href"));

    }

    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getAllOwnActivities")
    public void testSoundcloudAllOwnActivitiesPositive() throws Exception {

        //https://api.soundcloud.com/me/activities/all/own.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getAllOwnActivities");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/all/own" +
                ".json?oauth_token="+connectorProperties.getProperty("token")+ "&limit=";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("future_href"), apiRestResponse.getBody().get("future_href"));

    }


    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getActivitiesTracksAffiliated")
    public void testSoundcloudActivitiesAffiliatedPositive() throws Exception {

        //https://api.soundcloud.com/me/activities/tracks/affiliated.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getActivitiesTracksAffiliated");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/tracks/affiliated" +
                ".json?oauth_token="+connectorProperties.getProperty("token")+ "&limit=";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("future_href"), apiRestResponse.getBody().get("future_href"));

    }

    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getActivitiesTracksExclusive")
    public void testSoundcloudActivitiesExclusivePositive() throws Exception {

        //https://api.soundcloud.com/me/activities/tracks/exclusive.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getActivitiesTracksExclusive");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/tracks/exclusive" +
                ".json?oauth_token="+connectorProperties.getProperty("token")+ "&limit=";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("future_href"), apiRestResponse.getBody().get("future_href"));

    }


    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getComments")
    public void testSoundcloudCommentsPositive() throws Exception {

        //https://api.soundcloud.com/comments/13685794.json?oauth_token=1-84493-99675972-393f092639c561b

        esbRequestHeadersMap.put("Action", "urn:getComments");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/comments/" +
                connectorProperties.getProperty("commentId") +
                ".json?oauth_token="+connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"mandatory_esb_commentid.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));

    }


    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getAllComments")
    public void testSoundcloudAllCommentsPositive() throws Exception {

        //https://api.soundcloud.com/comments/13685794.json?oauth_token=1-84493-99675972-393f092639c561b





        esbRequestHeadersMap.put("Action", "urn:getAllComments");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/comments" +
                    ".json?oauth_token="+connectorProperties.getProperty("token");
        System.out.println("proxyUrl ---" + proxyUrl);


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().toString().contains("kind\\\":\\\"comment"), apiRestResponse.getBody().toString().contains("kind\\\":\\\"comment"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

    }


    /////////-----------------------------NEGATIVE-----------------------------///////////////////


    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getUser")
    public void testSoundcloudGetUserNegative() throws Exception {

        //http://api.soundcloud.com/users/99675972.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUser");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("negUserId")+
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }


    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getUserComments")
    public void testSoundcloudGetUserCommentsNegative() throws Exception {

        //http://api.soundcloud.com/users/999999999000000000/comments.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserComments");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("negUserId")+ "/comments" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        System.out.println("proxyUrl ---" + proxyUrl);
        System.out.println("apiEndPoint ---" + apiEndPoint);



        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);



        System.out.println("-------------------------------");
        System.out.println("-------------------------------");
        System.out.println("esbRestResponse-------" + esbRestResponse.getBody().toString().length());

        System.out.println("apiRestResponse-------" + apiRestResponse.getBody().toString().length());

        System.out.println();
        System.out.println();
        System.out.println("esbRestResponse-------" + esbRestResponse.getBody().toString());

        System.out.println("apiRestResponse-------" + apiRestResponse.getBody().toString());




        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }






    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getUserFavorites")
    public void testSoundcloudGetUserFavoritesNegative() throws Exception {

        //http://api.soundcloud.com/users/99675972/favorites.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserFavorites");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("negUserId")+ "/favorites" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }




    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getUserFollowers")
    public void testSoundcloudGetUserFollowersNegative() throws Exception {

        //http://api.soundcloud.com/users/99675972/followers.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserFollowers");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("negUserId")+ "/followers" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }

    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getUserFollowings")
    public void testSoundcloudGetUserFollowingsNegative() throws Exception {

        //http://api.soundcloud.com/users/99675972/followings.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserFollowings");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("negUserId")+ "/followings" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }




    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getUserGroups")
    public void testSoundcloudGetUserGroupsNegative() throws Exception {


        //http://api.soundcloud.com/users/99675972/groups.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserGroups");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("negUserId")+ "/groups" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }




    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getUserPlaylists")
    public void testSoundcloudGetUserPlaylistsNegative() throws Exception {

        //http://api.soundcloud.com/users/99675972/playlists.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserPlaylists");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("negUserId")+ "/playlists" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }





    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getUserTracks")
    public void testSoundcloudGetUserTracksNegative() throws Exception {

        //http://api.soundcloud.com/users/99675972/tracks.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserTracks");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("negUserId")+ "/tracks" +
                ".json?client_id="+connectorProperties.getProperty("clientId");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }




    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getUserWebProfile")
    public void testSoundcloudGetUserWebProfilesNegative() throws Exception {

        //http://api.soundcloud.com/users/99675972/web-profiles.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getUserWebProfile");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users/" +
                connectorProperties.getProperty("negUserId")+ "/web-profiles" +
                ".json?client_id="+connectorProperties.getProperty("clientId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_userid_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }





    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud searchUsers")
    public void testSoundcloudSearchUserNegative() throws Exception {

        //http://api.soundcloud.com/users.json?client_id=21fded24c32c2d9b0316971643d2f75f&q=manilsl

        esbRequestHeadersMap.put("Action", "urn:searchUsers");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users" +
                ".json?client_id="+connectorProperties.getProperty("clientId")+
                "&q="+connectorProperties.getProperty("negSearchUser");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_searchusers_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }





    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getMe")
    public void testSoundcloudGetMeNegative() throws Exception {

        //https://api.soundcloud.com/me.json?oauth_token=1-84493-99675972-bb6c61a90fa2b08

        esbRequestHeadersMap.put("Action", "urn:getMe");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me" +
                ".json?oauth_token="+connectorProperties.getProperty("negToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);



        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getHttpStatusCode());

    }



    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getConnections")
    public void testSoundcloudGetConnectionsNegative() throws Exception {

        //https://api.soundcloud.com/me/connections.json?oauth_token=1-84493-99675972-bb6c61a90fa2b08

        esbRequestHeadersMap.put("Action", "urn:getConnections");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/connections" +
                ".json?oauth_token="+connectorProperties.getProperty("negToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);



        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getHttpStatusCode());


    }


    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getConnectionById")
    public void testSoundcloudGetConnectionByIdNegative() throws Exception {

        //https://api.soundcloud.com/me/connections/61945938.json?oauth_token=1-84493-99675972-bb6c61a90fa2b08

        esbRequestHeadersMap.put("Action", "urn:getConnectionById");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/connections/" +
                connectorProperties.getProperty("negConnectionId") +
                ".json?oauth_token="+connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_connectionId_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }

    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud setConnections")
    public void testSoundcloudSetConnectionsNegative() throws Exception {

        //https://api.soundcloud.com/me/connections.json?oauth_token=1-84493-99675972-bb6c61a90fa2b08

        esbRequestHeadersMap.put("Action", "urn:setConnections");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/connections" +
                ".json?oauth_token="+connectorProperties.getProperty("token")+
                "&service="+connectorProperties.getProperty("negServiceType")+"&redirect_uri="  + connectorProperties.getProperty("redirectUrl");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_set_connection.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }





    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getActivities")
    public void testSoundcloudActivitiesNegative() throws Exception {

        //https://api.soundcloud.com/me/activities.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getActivities");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities" +
                ".json?oauth_token="+connectorProperties.getProperty("negToken") + "&limit=";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getHttpStatusCode());

    }


    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getAllActivities")
    public void testSoundcloudAllActivitiesNegative() throws Exception {

        //https://api.soundcloud.com/me/activities/all.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getAllActivities");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/all" +
                ".json?oauth_token="+connectorProperties.getProperty("negToken")+ "&limit=";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getHttpStatusCode());

    }

    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getAllOwnActivities")
    public void testSoundcloudAllOwnActivitiesNegative() throws Exception {

        //https://api.soundcloud.com/me/activities/all/own.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getAllOwnActivities");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/all/own" +
                ".json?oauth_token="+connectorProperties.getProperty("negToken")+ "&limit=";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getHttpStatusCode());

    }


    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getActivitiesTracksAffiliated")
    public void testSoundcloudActivitiesAffiliatedNegative() throws Exception {

        //https://api.soundcloud.com/me/activities/tracks/affiliated.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getActivitiesTracksAffiliated");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/tracks/affiliated" +
                ".json?oauth_token="+connectorProperties.getProperty("negToken")+ "&limit=";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getHttpStatusCode());

    }

    @Test(priority = 2, groups = { "wso2.esb" }, description = "Soundcloud getActivitiesTracksExclusive")
    public void testSoundcloudActivitiesExclusiveNegative() throws Exception {

        //https://api.soundcloud.com/me/activities/tracks/exclusive.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getActivitiesTracksExclusive");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/tracks/exclusive" +
                ".json?oauth_token="+connectorProperties.getProperty("negToken")+ "&limit=";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getHttpStatusCode());

    }

    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getComments")
    public void testSoundcloudCommentsNegative() throws Exception {

        //https://api.soundcloud.com/comments/13685794.json?oauth_token=1-84493-99675972-393f092639c561b

        esbRequestHeadersMap.put("Action", "urn:getComments");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/comments/" +
                connectorProperties.getProperty("negCommentId") +
                ".json?oauth_token="+connectorProperties.getProperty("token");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_commentid.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors").toString());

    }

    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getAllComments")
    public void testSoundcloudgetAllCommentsNegative() throws Exception {

        //https://api.soundcloud.com/comments/13685794.json?oauth_token=1-84493-99675972-393f092639c561b

        esbRequestHeadersMap.put("Action", "urn:getAllComments");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/comments" +
               ".json?oauth_token="+connectorProperties.getProperty("negToken");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"neg_esb_securedtoken_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);



        Assert.assertEquals(Integer.parseInt(esbRestResponse.getBody().get("error").toString()), apiRestResponse.getHttpStatusCode());

    }


    /////////////////////////////////////////OPTIONAL PARAMETERS////////////////////////////////////////////////////


    @Test(priority = 3, groups = { "wso2.esb" }, description = "soundcloud searchUsers integration test with Optional parameters")
    public void testSoundcloudSearchUsersOptional() throws Exception {

        //http://api.soundcloud.com/users.json?client_id=21fded24c32c2d9b0316971643d2f75f&q=manil&limit=2

        esbRequestHeadersMap.put("Action", "urn:searchUsers");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttp") + "/users" +
                ".json?client_id="+connectorProperties.getProperty("clientId")+
                "&q="+connectorProperties.getProperty("optSearch") + "&limit="+connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"optional_esb_searchusers.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }

@Test(priority = 3, groups = { "wso2.esb" }, description = "Soundcloud getActivities")
public void testSoundcloudActivitiesOptional() throws Exception {

    //https://api.soundcloud.com/me/activities.json?oauth_token=1-84493-99675972-9cbaa3ccb534805&limit=2

    esbRequestHeadersMap.put("Action", "urn:getActivities");
    String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities" +
            ".json?oauth_token="+connectorProperties.getProperty("token")+"&limit="+connectorProperties.getProperty("limit");

    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"optional_esb_securedtoken_limit.json");
    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

    Assert.assertEquals(esbRestResponse.getBody().get("future_href"), apiRestResponse.getBody().get("future_href"));

}

    @Test(priority = 3, groups = { "wso2.esb" }, description = "Soundcloud getAllActivities")
    public void testSoundcloudAllActivitiesOptional() throws Exception {

        //https://api.soundcloud.com/me/activities/all.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getAllActivities");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/all" +
                ".json?oauth_token="+connectorProperties.getProperty("token")+"&limit="+connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"optional_esb_securedtoken_limit.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("future_href"), apiRestResponse.getBody().get("future_href"));

    }

    @Test(priority = 3, groups = { "wso2.esb" }, description = "Soundcloud getAllOwnActivities")
    public void testSoundcloudAllOwnActivitiesOptional() throws Exception {

        //https://api.soundcloud.com/me/activities/all/own.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getAllOwnActivities");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/all/own" +
                ".json?oauth_token="+connectorProperties.getProperty("token")+ "&limit="+connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"optional_esb_securedtoken_limit.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("future_href"), apiRestResponse.getBody().get("future_href"));

    }


    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getActivitiesTracksAffiliated")
    public void testSoundcloudActivitiesAffiliatedOptional() throws Exception {


        //https://api.soundcloud.com/me/activities/tracks/affiliated.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getActivitiesTracksAffiliated");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/tracks/affiliated" +
                ".json?oauth_token="+connectorProperties.getProperty("token")+ "&limit="+connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"optional_esb_securedtoken_limit.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("future_href"), apiRestResponse.getBody().get("future_href"));

    }

    @Test(priority = 1, groups = { "wso2.esb" }, description = "Soundcloud getActivitiesTracksExclusive")
    public void testSoundcloudActivitiesExclusiveOptional() throws Exception {

        //https://api.soundcloud.com/me/activities/tracks/exclusive.json?oauth_token=1-84493-99675972-9cbaa3ccb534805

        esbRequestHeadersMap.put("Action", "urn:getActivitiesTracksExclusive");
        String apiEndPoint =  connectorProperties.getProperty("apiUrlHttps") + "/me/activities/tracks/exclusive" +
                ".json?oauth_token="+connectorProperties.getProperty("token")+ "&limit="+connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,"optional_esb_securedtoken_limit.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("future_href"), apiRestResponse.getBody().get("future_href"));

    }



    /////////////////////////------------NEXT STAGE --------------////////////////


    @Test(priority = 1, description = "soundcloud {resolveUser} integration test with mandatory parameters.")
    public void testResolveUserWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/aganimarathunga&client_id=7e33f0ffe0c83fe50c4635f5df19f80d


        esbRequestHeadersMap.put("Action", "urn:resolveUser");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/resolve.json?url="+ connectorProperties.getProperty("urlSite") +"/"+ connectorProperties.getProperty("permalinkOfUser")+"&client_id="+connectorProperties.getProperty("consumerKey");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveUser_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("username"), apiRestResponse.getBody().get("username"));

    }





    @Test(priority = 1, description = "soundcloud {resolveTrack} integration test with mandatory parameters.")
    public void testResolveTrackWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/aganimarathunga/bach-air-on-the-g-ltring&client_id=7e33f0ffe0c83fe50c4635f5df19f80d


        esbRequestHeadersMap.put("Action", "urn:resolveTrack");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/resolve.json?url="+ connectorProperties.getProperty("urlSite") +"/"+ connectorProperties.getProperty("permalinkOfUser")+ "/" +connectorProperties.getProperty("permalinkOfTrack")+"&client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveTrack_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("duration"), apiRestResponse.getBody().get("duration"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));


    }







    @Test(priority = 1, description = "soundcloud {resolveAllTracksOfUser} integration test with mandatory parameters.")
    public void testResolveAllTracksOfUserWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/aganimarathunga/tracks&client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:resolveAllTracksOfUser");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/resolve.json?url="+ connectorProperties.getProperty("urlSite") +"/"+ connectorProperties.getProperty("permalinkOfUser")+"/tracks&client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveAllTracksOfUser_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));


    }








    @Test(priority = 1, description = "soundcloud {resolveAllGroupsOfUser} integration test with mandatory parameters.")
    public void testResolveAllGroupsOfUserWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/aganimarathunga/groups&client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:resolveAllGroupsOfUser");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/resolve.json?url="+ connectorProperties.getProperty("urlSite") +"/"+ connectorProperties.getProperty("permalinkOfUser")+"/groups&client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveAllGroupsOfUser_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());


    }










    @Test(priority = 1, description = "soundcloud {resolveApp} integration test with mandatory parameters.")
    public void testResolveAppWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/apps/testapp-331&client_id=7e33f0ffe0c83fe50c4635f5df19f80d
        esbRequestHeadersMap.put("Action", "urn:resolveApp");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/resolve.json?url="+ connectorProperties.getProperty("urlSite") +"/apps/"+ connectorProperties.getProperty("permalinkOfApp")+"&client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveApp_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("creator"), apiRestResponse.getBody().get("creator"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));


    }


    @Test(priority = 1, description = "soundcloud {getEmbedContent} integration test with mandatory parameters.")
    public void testGetEmbedContentWithMandatoryParameters() throws IOException, JSONException {

        //http://soundcloud.com/oembed?format=json&url=http://soundcloud.com/forss/flickermood
        esbRequestHeadersMap.put("Action", "urn:getEmbedContent");
        String apiEndPoint =
                connectorProperties.getProperty("urlSite") + "/oembed?format=json&url="+ connectorProperties.getProperty("url");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmbedContent_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("version"), apiRestResponse.getBody().get("version"));
        Assert.assertEquals(esbRestResponse.getBody().get("type"), apiRestResponse.getBody().get("type"));
        Assert.assertEquals(esbRestResponse.getBody().get("provider_name"), apiRestResponse.getBody().get("provider_name"));
    }

    @Test(priority = 1, description = "soundcloud {getTrackById} integration test with mandatory parameters.")
    public void testGetTrackByIdWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks/155208102.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getTrackById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks/" +connectorProperties.getProperty("trackId") + ".json?client_id="+connectorProperties.getProperty("consumerKey");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTrackById_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);



        Assert.assertEquals(esbRestResponse.getBody().get("genre"), apiRestResponse.getBody().get("genre"));
        Assert.assertEquals(esbRestResponse.getBody().get("created_at"), apiRestResponse.getBody().get("created_at"));

    }







    @Test(priority = 1, description = "soundcloud {getCommentsByIdOfTrack} integration test with mandatory parameters.")
    public void testGetCommentsByIdOfTrackWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks/156637660/comments.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getCommentsByIdOfTrack");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks/" +connectorProperties.getProperty("trackId") + "/comments.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommentsOfTrack_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));


    }





    @Test(priority = 1, description = "soundcloud {getFavoritersByIdOfTrack} integration test with mandatory parameters.")
    public void testGetFavoritersByIdOfTrackWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks/13158665/favoriters.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d


        esbRequestHeadersMap.put("Action", "urn:getFavoritersByIdOfTrack");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks/" +connectorProperties.getProperty("trackIdOfFavoriters") + "/favoriters.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFavoritersByIdOfTrack_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);



        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }




    @Test(priority = 1, description = "soundcloud {getCommentOfTrack} integration test with mandatory parameters.")
    public void testGetCommentOfTrackWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks/155208102.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getCommentOfTrack");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks/" +connectorProperties.getProperty("trackId") + "/comments/"+ connectorProperties.getProperty("commentId") +".json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommentOfTrack_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);



        Assert.assertEquals(esbRestResponse.getBody().get("timestamp"), apiRestResponse.getBody().get("timestamp"));
        Assert.assertEquals(esbRestResponse.getBody().get("body"), apiRestResponse.getBody().get("body"));
        Assert.assertEquals(esbRestResponse.getBody().get("user_id"), apiRestResponse.getBody().get("user_id"));

    }





    @Test(priority = 1, description = "soundcloud {getTracks} integration test with mandatory parameters.")
    public void testGetTracksWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d
        esbRequestHeadersMap.put("Action", "urn:getTracks");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks.json?client_id="+connectorProperties.getProperty("consumerKey");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTracks_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().toString().contains("kind\\\":\\\"track"),apiRestResponse.getBody().toString().contains("kind\\\":\\\"track"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(),apiRestResponse.getHttpStatusCode());

    }


    @Test(priority = 1, description = "soundcloud {getAppById} integration test with mandatory parameters.")
    public void testGetAppByIdWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/apps/124.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getAppById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/apps/" +connectorProperties.getProperty("appId") + ".json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAppById_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
    }






    @Test(priority = 1, description = "soundcloud {getApps} integration test with mandatory parameters.")
    public void testGetAppsWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/apps.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getApps");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/apps.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getApps_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }





    @Test(priority = 1, description = "soundcloud {getTracksOfApp} integration test with mandatory parameters.")
    public void testGetTracksOfAppWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/apps/124/tracks.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getTracksOfApp");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/apps/" +connectorProperties.getProperty("appId") + "/tracks.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTracksOfApp_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        // Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }

    @Test(priority = 1, description = "soundcloud {getGroups} integration test with mandatory parameters.")
    public void testGetGroupsWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/groups.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getGroups");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getGroups_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        //Assert.assertEquals(esbRestResponse.getBody().get("output").toString(), apiRestResponse.getBody().get("output").toString());
    }







    @Test(priority = 1, description = "soundcloud {getGroupById} integration test with mandatory parameters.")
    public void testGetGroupByIdWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/40545942.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("groupId") + ".json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getGroupById_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("track_count"), apiRestResponse.getBody().get("track_count"));
        Assert.assertEquals(esbRestResponse.getBody().get("members_count"), apiRestResponse.getBody().get("members_count"));

    }







    @Test(priority = 1, description = "soundcloud {getModeratorsOfGroupById} integration test with mandatory parameters.")
    public void testGetModeratorsOfGroupByIdWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/3/moderators.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getModeratorsOfGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("groupId") + "/moderators.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getModeratorsOfGroupById_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));
        //Assert.assertEquals(esbRestResponse.getBody().get("members_count"), apiRestResponse.getBody().get("members_count"));

    }





    @Test(priority = 1, description = "soundcloud {getMembersOfGroupById} integration test with mandatory parameters.")
    public void testGetMembersOfGroupByIdWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/3/members.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getMembersOfGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("groupId") + "/members.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMembersOfGroupById_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        // Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));
        //Assert.assertEquals(esbRestResponse.getBody().get("members_count"), apiRestResponse.getBody().get("members_count"));

    }




    @Test(priority = 1, description = "soundcloud {getContributorsOfGroupById} integration test with mandatory parameters.")
    public void testGetContributorsOfGroupByIdWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/3/contributors.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getContributorsOfGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("groupId") + "/contributors.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContributorsOfGroupById_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));


    }





    @Test(priority = 1, description = "soundcloud {getUsersOfGroupById} integration test with mandatory parameters.")
    public void testGetUsersOfGroupByIdWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/3/users.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getUsersOfGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("groupId") + "/users.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUsersOfGroupById_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        //Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));


    }





    @Test(priority = 1, description = "soundcloud {getTracksOfGroupById} integration test with mandatory parameters.")
    public void testGetTracksOfGroupByIdWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/3/tracks.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getTracksOfGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("groupId") + "/tracks.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTracksOfGroupById_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        //Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));


    }

    @Test(priority = 1, description = "soundcloud {getPlaylistById} integration test with mandatory parameters.")
    public void testGetPlaylistByIdWithMandatoryParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/playlists/40545942.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getPlaylistById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/playlists/" +connectorProperties.getProperty("playlistId") + ".json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPlaylistById_mandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);



        Assert.assertEquals(esbRestResponse.getBody().get("track_count"), apiRestResponse.getBody().get("track_count"));
        Assert.assertEquals(esbRestResponse.getBody().get("genre"), apiRestResponse.getBody().get("genre"));
        Assert.assertEquals(esbRestResponse.getBody().get("sharing"), apiRestResponse.getBody().get("sharing"));
    }








    ///////////////////NEGATIVE/////////////////

    @Test(priority = 2, description = "soundcloud {resolveUser} integration test with negative case.")
    public void testResolveUserWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/1231233213123&client_id=7e33f0ffe0c83fe50c4635f5df19f80d


        esbRequestHeadersMap.put("Action", "urn:resolveUser");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/resolve.json?url="+ connectorProperties.getProperty("urlSite") +"/"+ connectorProperties.getProperty("invalidUser")+"&client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveUser_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
        //{"errors":[{"error_message":"404 - Not Found"}]}
    }

    @Test(priority = 2, description = "soundcloud {resolveTrack} integration test with negative case.")
    public void testResolveTrackWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/invaliduser/bach-air-on-the-g-ltring&client_id=7e33f0ffe0c83fe50c4635f5df19f80d


        esbRequestHeadersMap.put("Action", "urn:resolveTrack");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/resolve.json?url="+ connectorProperties.getProperty("urlSite") +"/"+ connectorProperties.getProperty("invalidUser")+ "/" +connectorProperties.getProperty("permalinkOfTrack")+"&client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveTrack_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());


    }


    @Test(priority = 2, description = "soundcloud {resolveAllTracksOfUser} integration test with negative case.")
    public void testResolveAllTracksOfUserWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/invaliduser/tracks&client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:resolveAllTracksOfUser");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/resolve.json?url="+ connectorProperties.getProperty("urlSite") +"/"+ connectorProperties.getProperty("invalidUser")+"/tracks&client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveAllTracksOfUser_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
    }


    @Test(priority = 2, description = "soundcloud {resolveAllGroupsOfUser} integration test with negative case.")
    public void testResolveAllGroupsOfUserWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/invaliduser/groups&client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:resolveAllGroupsOfUser");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/resolve.json?url="+ connectorProperties.getProperty("urlSite") +"/"+ connectorProperties.getProperty("invalidUser")+"/groups&client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveAllGroupsOfUser_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
    }

    @Test(priority = 2, description = "soundcloud {resolveApp} integration test with negative case.")
    public void testResolveAppWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/apps/testapp-331&client_id=7e33f0ffe0c83fe50c4635f5df19f80d
        esbRequestHeadersMap.put("Action", "urn:resolveApp");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/resolve.json?url="+ connectorProperties.getProperty("urlSite") +"/apps/"+ connectorProperties.getProperty("invalidApp")+"&client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveApp_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
    }

    @Test(priority = 2, description = "soundcloud {getEmbedContent} integration test with negative case.")
    public void testGetEmbedContentWithNegativeCase() throws IOException, JSONException {

        //http://soundcloud.com/oembed?format=json&url=http://soundcloud.com/forss/flickermood
        esbRequestHeadersMap.put("Action", "urn:getEmbedContent");
        String apiEndPoint =
                connectorProperties.getProperty("urlSite") + "/oembed?format=json&url="+ connectorProperties.getProperty("invalidUrl");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmbedContent_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        //    Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
    }

    @Test(priority = 2, description = "soundcloud {getTrackById} integration test with negative case.")
    public void testGetTrackByIdWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks/155208102.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getTrackById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks/" +connectorProperties.getProperty("invalidTrackId") + ".json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTrackById_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());

        //{"errors":[{"error_message":"404 - Not Found"}]}
    }

    @Test(priority = 2, description = "soundcloud {getCommentsByIdOfTrack} integration test with negative case.")
    public void testGetCommentsByIdOfTrackWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks/156637660/comments.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getCommentsByIdOfTrack");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks/" +connectorProperties.getProperty("invalidTrackId") + "/comments.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommentsOfTrack_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());


    }

    @Test(priority = 2, description = "soundcloud {getCommentOfTrack} integration test with negative case.")
    public void testGetCommentOfTrackWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks/156637660/comments/1111111111.json?client_id=21fded24c32c2d9b0316971643d2f75f

        esbRequestHeadersMap.put("Action", "urn:getCommentOfTrack");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks/" +connectorProperties.getProperty("trackId") + "/comments/"+ connectorProperties.getProperty("invalidCommentId") +".json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommentOfTrack_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
        //{"errors":[{"error_message":"404 - Not Found"}]}

    }

    @Test(priority = 2, description = "soundcloud {getFavoritersByIdOfTrack} integration test with negative case.")
    public void testGetFavoritersByIdOfTrackWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks/11111111111111/favoriters.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d


        esbRequestHeadersMap.put("Action", "urn:getFavoritersByIdOfTrack");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks/" +connectorProperties.getProperty("invalidTrackId") + "/favoriters.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFavoritersByIdOfTrack_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
    }

    @Test(priority = 2, description = "soundcloud {getTracks} integration test with negative case.")
    public void testGetTracksWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks.json?client_id=1111111111111111111111111
        esbRequestHeadersMap.put("Action", "urn:getTracks");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks.json?client_id="+connectorProperties.getProperty("invalidConsumerKey");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTracks_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());

    }


    @Test(priority = 2, description = "soundcloud {updateTrack} integration test with negative case.")
    public void testUpdateTrackWithNegativeCase() throws IOException, JSONException {

        //https://api.soundcloud.com/tracks/156951295.json?oauth_token=1-84496-99677846-6dc227453c69b00&track[title]=newtitle1&track[description]=testingdesc&track[sharing]=public
        esbRequestHeadersMap.put("Action", "urn:updateTrack");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttps") + "/tracks/"+ connectorProperties.getProperty("forbiddenTrackId") +".json?oauth_token="+ connectorProperties.getProperty("accessToken")+"&track[title]="+connectorProperties.getProperty("title")+"&track[description]=" + connectorProperties.getProperty("description") + "&track[sharing]=" + connectorProperties.getProperty("sharing");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTrack_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);



        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());


    }

    @Test(priority = 1, description = "soundcloud {deleteCommentOfTrack} integration test with negative case.")
    public void testDeleteCommentOfTrackWithNegativeCase() throws IOException, JSONException {

        //https://api.soundcloud.com/tracks/156637660/comments/190244345.json?oauth_token=1-84496-99677846-6dc227453c69b00
        esbRequestHeadersMap.put("Action", "urn:deleteCommentOfTrack");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCommentOfTrack_negative.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);

    }

        @Test(priority = 2, description = "soundcloud {getAppById} integration test with negative case.")
    public void testGetAppByIdWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/apps/11111111.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getAppById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/apps/" +connectorProperties.getProperty("invalidAppId") + ".json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAppById_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
        //{"errors":[{"error_message":"404 - Not Found"}]}
    }


    @Test(priority = 2, description = "soundcloud {getApps} integration test with negatvie case.")
    public void testGetAppsWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/apps.json?client_id=1111111111111111111111111

        esbRequestHeadersMap.put("Action", "urn:getApps");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/apps.json?client_id="+connectorProperties.getProperty("invalidConsumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getApps_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
        //{"errors":[{"error_message":"401 - Unauthorized"}]}

    }


    @Test(priority = 2, description = "soundcloud {getTracksOfApp} integration test with negative case.")
    public void testGetTracksOfAppWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/apps/11111111111/tracks.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getTracksOfApp");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/apps/" +connectorProperties.getProperty("invalidAppId") + "/tracks.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTracksOfApp_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);



        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
        //{"errors":[{"error_message":"404 - Not Found"}]}
    }


    @Test(priority = 2, description = "soundcloud {getGroupById} integration test with negative case.")
    public void testGetGroupByIdWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/40545942.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("invalidGroupId") + ".json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getGroupById_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());

    }

    @Test(priority = 2, description = "soundcloud {getModeratorsOfGroupById} integration test with negative case.")
    public void testGetModeratorsOfGroupByIdWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/11111111111111/moderators.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getModeratorsOfGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("invalidGroupId") + "/moderators.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getModeratorsOfGroupById_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
    }

    @Test(priority = 2, description = "soundcloud {getMembersOfGroupById} integration test with negative case.")
    public void testGetMembersOfGroupByIdWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/11111111111111/members.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getMembersOfGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("invalidGroupId") + "/members.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMembersOfGroupById_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
    }

    @Test(priority = 2, description = "soundcloud {getContributorsOfGroupById} integration test with negative case.")
    public void testGetContributorsOfGroupByIdWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/3/contributors.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getContributorsOfGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("invalidGroupId") + "/contributors.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContributorsOfGroupById_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());


    }

    @Test(priority = 2, description = "soundcloud {getUsersOfGroupById} integration test with negative case.")
    public void testGetUsersOfGroupByIdWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/11111111111111/users.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getUsersOfGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("invalidGroupId") + "/users.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUsersOfGroupById_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());


    }


    @Test(priority = 2, description = "soundcloud {getTracksOfGroupById} integration test with negative case.")
    public void testGetTracksOfGroupByIdWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/groups/11111111111111/tracks.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getTracksOfGroupById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups/" +connectorProperties.getProperty("invalidGroupId") + "/tracks.json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTracksOfGroupById_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());


    }

    @Test(priority = 2, description = "soundcloud {getGroups} integration test with negative case.")
    public void testGetGroupsWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/groups.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getGroups");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups.json?client_id="+connectorProperties.getProperty("invalidConsumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getGroups_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);


    }


    @Test(priority = 2, description = "soundcloud {getPlaylistById} integration test with negative case.")
    public void testGetPlaylistByIdWithNegativeCase() throws IOException, JSONException {

        //http://api.soundcloud.com/playlists/11111111111111.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d

        esbRequestHeadersMap.put("Action", "urn:getPlaylistById");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/playlists/" +connectorProperties.getProperty("invalidPlaylistId") + ".json?client_id="+connectorProperties.getProperty("consumerKey");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPlaylistById_negative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").length(), apiRestResponse.getBody().getJSONArray("errors").length());
    }


    @Test(priority = 2, description = "soundcloud {deleteTrack} integration test with negative case.")
    public void testDeleteTrackWithNegativeCase() throws IOException, JSONException {

        //https://api.soundcloud.com/tracks/11111111111111.json?oauth_token=1-84496-99677846-5551deafe5c1f83
        esbRequestHeadersMap.put("Action", "urn:deleteTrack");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTrack_negative.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }




    //////////////OPTIONAL////////////


    @Test(priority = 3, description = "soundcloud {getTracks} integration test with optional parameters.")
    public void testGetTracksWithOptionalParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/tracks.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d&q=bach&limit=3
        esbRequestHeadersMap.put("Action", "urn:getTracks");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/tracks.json?client_id="+connectorProperties.getProperty("consumerKey")+"&q=" + connectorProperties.getProperty("searchInTracks") + "&limit=" + connectorProperties.getProperty("limit");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTracks_optional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }






    @Test(priority = 3, description = "soundcloud {updateTrack} integration test with optional parameters.")
    public void testUpdateTrackWithOptionalParameters() throws IOException, JSONException {

        //https://api.soundcloud.com/tracks/156951295.json?oauth_token=1-84496-99677846-6dc227453c69b00&track[title]=newtitle1&track[description]=testingdesc&track[sharing]=public
        esbRequestHeadersMap.put("Action", "urn:updateTrack");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttps") + "/tracks/"+ connectorProperties.getProperty("updateTrackId") +".json?oauth_token="+ connectorProperties.getProperty("accessToken")+"&track[title]="+connectorProperties.getProperty("title")+"&track[description]=" + connectorProperties.getProperty("description") + "&track[sharing]=" + connectorProperties.getProperty("sharing");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTrack_optional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
        Assert.assertEquals(esbRestResponse.getBody().get("sharing"), apiRestResponse.getBody().get("sharing"));


    }

    @Test(priority = 3, description = "soundcloud {getTracksOfApp} integration test with optional parameters.")
    public void testGetTracksOfAppWithOptionalParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/apps/124/tracks.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d&limit=3

        esbRequestHeadersMap.put("Action", "urn:getTracksOfApp");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/apps/" +connectorProperties.getProperty("appId") + "/tracks.json?client_id="+connectorProperties.getProperty("consumerKey") +"&limit=" + connectorProperties.getProperty("limit");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTracksOfApp_optional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("output"), apiRestResponse.getBody().get("output"));

    }

    @Test(priority = 3, description = "soundcloud {getGroups} integration test with optional parameters.")
    public void testGetGroupsWithOptionalParameters() throws IOException, JSONException {

        //http://api.soundcloud.com/groups.json?client_id=7e33f0ffe0c83fe50c4635f5df19f80d&q=make

        esbRequestHeadersMap.put("Action", "urn:getGroups");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrlHttp") + "/groups.json?client_id="+connectorProperties.getProperty("consumerKey")+"&q=" +connectorProperties.getProperty("searchInGroups");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getGroups_optional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        // Assert.assertEquals(esbRestResponse.getBody().get("output").toString(), apiRestResponse.getBody().get("output").toString());


    }





    ////UNCOMMENT BELOW ON DEMO


/*    @Test(priority = 3, description = "soundcloud {deleteCommentOfTrack} integration test with optional parameters.")
    public void testDeleteCommentOfTrackWithOptionalParameters() throws IOException, JSONException {

        //https://api.soundcloud.com/tracks/156637660/comments/190244345.json?oauth_token=1-84496-99677846-6dc227453c69b00

        esbRequestHeadersMap.put("Action", "urn:deleteCommentOfTrack");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCommentOfTrack_mandatory.json");


        Assert.assertEquals(esbRestResponse.getBody().get("status"),"200 - OK");


    }*/





   /*@Test(priority = 3, description = "soundcloud {deleteTrack} integration test with optional parameters.")
    public void testDeleteTrackWithOptionalParameters() throws IOException, JSONException {

        //https://api.soundcloud.com/tracks/155207823.json?oauth_token=1-84496-99677846-5551deafe5c1f83
        esbRequestHeadersMap.put("Action", "urn:deleteTrack");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTrack_mandatory.json");

        Assert.assertEquals(esbRestResponse.getBody().get("status"),"200 - OK");
    }
*/







}

