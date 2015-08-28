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

    /**
     * Positive test case for getUserInfo method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {getUserInfo} integration test with mandatory parameters.")
    public void testGetUserInfoWithManditoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getUserInfo");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserInfo_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/?access_token=" + connectorProperties.getProperty("accessToken");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("username"), apiRestResponse.getBody().getJSONObject("data").get("username"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("id"), apiRestResponse.getBody().getJSONObject("data").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("full_name"), apiRestResponse.getBody().getJSONObject("data").get("full_name"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for getUserInfo method.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {getUserInfo} integration test with negative case.")
    public void testGetUserInfoWithNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getUserInfo");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserInfo_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/?access_token=" + connectorProperties.getProperty("token");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

	/**
	 * Positive test case for getSelfFeed method with optional parameters.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram {getSelfFeed} integration test with optional parameters.")
	public void testGetSelfFeedWithOptionalParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:getSelfFeed");

		String apiEndPoint = "https://api.instagram.com/v1/users/self/feed?" + "access_token=" + connectorProperties.getProperty("accessToken") + "&count=" + connectorProperties.getProperty("count");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfFeed_optional.json");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").toString(), apiRestResponse.getBody().getJSONObject("pagination").toString());
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
	}

	/**
	 * Negative test case for getSelfFeed method.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram {getSelfFeed} integration test with negative case.")
	public void testGetSelfFeedWithNegativeParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:getSelfFeed");

		String apiEndPoint = "https://api.instagram.com/v1/users/self/feed?" + "access_token=" + connectorProperties.getProperty("token") + "&count=" + connectorProperties.getProperty("count") + "&min_id=" + connectorProperties.getProperty("minId") + "&max_id" + connectorProperties.getProperty("maxId");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfFeed_negative.json");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
	}

   /**
    *  Positive test case for getSelfMediaLiked method with mandatory parameters.
    */
    @Test(groups = {"wso2.esb"}, description = "instagram { getSelfMediaLiked} integration test with mandatory parameters.")
    public void testGetSelfMediaLikedWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getSelfMediaLiked");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfMediaLiked_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/self/media/liked?" + "access_token=" + connectorProperties.getProperty("accessToken");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(apiRestResponse.getBody().getJSONObject("pagination").toString(), esbRestResponse.getBody().getJSONObject("pagination").toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

	/**
	 *  Positive test case for getSelfMediaLiked method with optional parameters.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram { getSelfMediaLiked} integration test with optional parameters.")
	public void testGetSelfMediaLikedWithOptionalParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:getSelfMediaLiked");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfMediaLiked_optional.json");

		String apiEndPoint = "https://api.instagram.com/v1/users/self/media/liked?" + "access_token=" + connectorProperties.getProperty("accessToken")+"&count="+connectorProperties.getProperty("count");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(apiRestResponse.getBody().getJSONObject("pagination").toString(), esbRestResponse.getBody().getJSONObject("pagination").toString());
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
	}

	/**
	 * Negative test case for getSelfMediaLiked method.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram {getSelfMediaLiked} integration test with negative case.")
	public void testGetSelfMediaLikedWithNegativeParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:getSelfFeed");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfMediaLiked_negative.json");

		String apiEndPoint = "https://api.instagram.com/v1/users/self/media/liked?" + "access_token=" + connectorProperties.getProperty("token");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
	}

    /**
     * Positive test case for getUserMediaRecent method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { getUserMediaRecent} integration test with mandatory parameters.")
    public void testGetUserMediaRecentWithmandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getUserMediaRecent");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserMediaRecent_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("accessToken");

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
	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUsermediaRecent_optional.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("accessToken") + "&count=" + connectorProperties.getProperty("count") + "&max_like_id=" + connectorProperties.getProperty("maxLikeId") + "&max_timestamp=" + connectorProperties.getProperty("maxTimestamp") + "&min_timestamp=" + connectorProperties.getProperty("minTimestamp");

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

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserMediaRecent_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("token");

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

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_userSearch_mandatory.json");

		String apiEndPoint = "https://api.instagram.com/v1/users/search?q=" + connectorProperties.getProperty("queryName") + "&access_token=" + connectorProperties.getProperty("accessToken");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString(), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString());
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
	}

	/**
	 * Positive test case for userSearch method with optional parameters.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram { userSearch} integration test with optional parameters.")
	public void tesUserSearchWithOptionalParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:userSearch");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_userSearch_optional.json");

		String apiEndPoint = "https://api.instagram.com/v1/users/search?q=" + connectorProperties.getProperty("queryName") + "&count=" + connectorProperties.getProperty("count") +
				"&access_token=" + connectorProperties.getProperty("accessToken");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString(), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString());
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
	}

	/**
	 * Negative test case for userSearch method.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram { userSearch} integration test with negative case.")
	public void tesUserSearchWithNegativeParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:userSearch");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_userSearch_negative.json");

		String apiEndPoint = "https://api.instagram.com/v1/users/search?q=" + connectorProperties.getProperty("queryName") + "&access_token=" + connectorProperties.getProperty("token");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
	}

	/**
	 *  Positive test case for getUserFollows method with mandatory parameters.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram {getUserFollows} integration test with mandatory parameters.")
	public void testGetUserFollowsWithOptionalParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:getUserFollows");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserFollows_mandatory.json");

		String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/follows?access_token=" + connectorProperties.getProperty("accessToken");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id"));
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
	}

	/**
	 * Negative test case for getUserFollows method with mandatory parameters.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram {getUserFollows} integration test with mandatory parameters.")
	public void testGetUserFollowsWithNegativeParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:getUserFollows");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserFollows_negative.json");

		String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/follows?access_token=" + connectorProperties.getProperty("token");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
	}

    /**
     * Positive test case for getUserFollowedBy method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { getUserFollowedBy} integration test with mandatory parameters.")
    public void testGetUserFollowedByMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getUserFollowedBy");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserFollowedBy_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/followed-by?" + "access_token=" + connectorProperties.getProperty("accessToken");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for getUserFollowedBy method.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { getUserFollowedBy} integration test with negative case.")
    public void testGetUserFollowedByNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getUserFollowedBy");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserFollowedBy_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/followed-by?" + "access_token=" + connectorProperties.getProperty("token");
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

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfRequestedBy_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/self/requested-by?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

	/**
	 * Negative test case for getSelfRequestedBy method.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram { getSelfRequestedBy} integration test with negative case.")
	public void testGetSelfRequestedByNegativeParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:getSelfRequestedBy");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSelfRequestedBy_negative.json");

		String apiEndPoint = "https://api.instagram.com/v1/users/self/requested-by?access_token=" + connectorProperties.getProperty("token");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
	}

	/**
     * Positive test case for getUserRelationship method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {getUserRelationship} integration test with mandatory parameters.")
    public void testGetUserRelationshipWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getUserRelationship");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserRelationship_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/relationship?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("outgoing_status"), apiRestResponse.getBody().getJSONObject("data").get("outgoing_status"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

   /**
    *  Negative test case for getUserRelationship method.
    */
    @Test(groups = {"wso2.esb"}, description = "instagram {getUserRelationship} integration test with negative case.")
    public void testGetUserRelationshipWithNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getUserRelationship");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserRelationship_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/users/" + connectorProperties.getProperty("userId") + "/relationship?access_token=" + connectorProperties.getProperty("token");

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

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaInfo_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("mediaId") + "/?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").get("type"), esbRestResponse.getBody().getJSONObject("data").get("type"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for getMediaInfo method.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaInfo} integration test with negative case.")
    public void tesTagSearchWithNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getMediaInfo");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaInfo_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("mediaId") + "/?access_token=" + connectorProperties.getProperty("token");

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

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaPopular_positive.json");

	    String apiEndPoint = "https://api.instagram.com/v1/media/popular" + "?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("attribution"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("attribution"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for getMediaPopular method.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaPopular} integration test with negative case.")
    public void tesGetMediaPopularNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getMediaPopular");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaPopular_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/media/popular" + "?access_token=" + connectorProperties.getProperty("token");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for getMediaSearch method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaSearch} integration test with mandatory parameters.")
    public void tesGetMediaSearchMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getMediaSearch");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaSearch_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/media/search?lat=" + connectorProperties.getProperty("lat") + "&lng=" + connectorProperties.getProperty("lng") + "&access_token=" + connectorProperties.getProperty("accessToken");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("type"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("type"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

	/**
	 * Positive test case for getMediaSearch method with optional parameters.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram { getMediaSearch} integration test with optional parameters.")
	public void tesGetMediaSearchOptionalParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:getMediaSearch");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaSearch_optional.json");

		String apiEndPoint = "https://api.instagram.com/v1/media/search?lat=" + connectorProperties.getProperty("lat") + "&lng=" + connectorProperties.getProperty("lng") + "&max_timestamp=" + connectorProperties.getProperty("maxTimestamp") + "&min_timestamp=" + connectorProperties.getProperty("minTimestamp") + "&distance=" + connectorProperties.getProperty("distance") + "&access_token=" + connectorProperties.getProperty("accessToken");

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

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaSearch_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/media/search?lat=" + connectorProperties.getProperty("lat") + "&lng=" + connectorProperties.getProperty("lng") + "&access_token=" + connectorProperties.getProperty("token");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for getMediaComment method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {getMediaComment} integration test with mandatory parameters.")
    public void testGetMediaCommentWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getMediaComment");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_media_comment_mandatory.json");

        String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("mediaId") + "/comments?access_token=" + connectorProperties.getProperty("accessToken");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("text"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("text"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for getMediaComment method.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {getMediaComment} integration test with negative case.")
    public void testGetMediaCommentWithNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getMediaComment");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_media_comment_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("mediaId") + "/comments?access_token=" + connectorProperties.getProperty("token");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for getMediaLike method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaLike} integration test with mandatory parameters.")
    public void tesGetMediaLikeWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getMediaLike");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaLike_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("mediaId") + "/likes?" + "access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("username"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for getMediaLike method.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { getMediaLike} integration test with negative case.")
    public void tesGetMediaLikeWithNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getMediaLike");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMediaLike_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/media/" + connectorProperties.getProperty("media_id") + "/likes?" + "access_token=" + connectorProperties.getProperty("token");

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

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagInfo_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tagName") + "?access_token=" + connectorProperties.getProperty("accessToken");

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

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagInfo_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tagName") + "?access_token=" + connectorProperties.getProperty("token");

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

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagRecent_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tagName") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("accessToken");

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

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagRecent_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tagName") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("accessToken") + "&max_id=" + connectorProperties.getProperty("maxId") + "&min_id=" + connectorProperties.getProperty("minId");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("attribution"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("attribution"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for getTagRecent method.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { getTagRecent} integration test with negative case.")
    public void testGetTagRecentWithNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getTagRecent");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagRecent_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/tags/" + connectorProperties.getProperty("tagName") + "/media/recent?" + "access_token=" + connectorProperties.getProperty("token");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for tagSearch method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { tagSearch} integration test with mandatory parameters.")
    public void tesTagSearchWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:tagSearch");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagSearch_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/tags/search?q=" + connectorProperties.getProperty("query") + "&access_token=" + connectorProperties.getProperty("accessToken");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("media_count"), esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("media_count"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for tagSearch method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram { tagSearch} integration test with mandatory parameters.")
    public void tesTagSearchhWithNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:tagSearch");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagSearch_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/tags/search?q=" + connectorProperties.getProperty("query") + "&access_token=" + connectorProperties.getProperty("token");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for getLocationInfo method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {getLocationInfo} integration test with mandatory parameters.")
    public void testGetLocationInfoWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getLocationInfo");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_location_info_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/locations/" + connectorProperties.getProperty("locationId") + "?access_token=" + connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("longitude"), apiRestResponse.getBody().getJSONObject("data").get("longitude"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for getLocationInfo method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {getLocationInfo} integration test with negative parameters.")
    public void testGetLocationInfoWithNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getLocationInfo");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_location_info_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/locations/" + connectorProperties.getProperty("locationId") + "?access_token=" + connectorProperties.getProperty("token");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

	/**
	 * Positive test case for getLocationRecent method with mandatory parameters.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram {getLocationRecent} integration test with mandatory parameters.")
	public void testGetLocationRecentWithMandatoryParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:getLocationRecent");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_location_recent_mandatory.json");

		String apiEndPoint = "https://api.instagram.com/v1/locations/" + connectorProperties.getProperty("locationId") + "/media/recent?access_token=" + connectorProperties.getProperty("accessToken");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").get("next_max_id"), apiRestResponse.getBody().getJSONObject("pagination").get("next_max_id"));
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
	}

    /**
     * Positive test case for getLocationRecent method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {getLocationRecent} integration test with optional parameters.")
    public void testGetLocationRecentWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getLocationRecent");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_location_recent_optional.json");

	    String apiEndPoint = "https://api.instagram.com/v1/locations/" + connectorProperties.getProperty("locationId") + "/media/recent?access_token=" + connectorProperties.getProperty("accessToken") + "&max_timestamp=" + connectorProperties.getProperty("maxTimestamp") + "&min_timestamp=" + connectorProperties.getProperty("minTimestamp");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").get("next_max_id"), apiRestResponse.getBody().getJSONObject("pagination").get("next_max_id"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

	/**
	 * Negative test case for getLocationRecent method with mandatory parameters.
	 */
	@Test(groups = {"wso2.esb"}, description = "instagram {getLocationInfo} integration test with negative parameters.")
	public void testGetLocationRecentWithNegativeParameters() throws Exception {
		esbRequestHeadersMap.put("Action", "urn:getLocationInfo");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_get_location_info_negative.json");

		String apiEndPoint = "https://api.instagram.com/v1/locations/" + connectorProperties.getProperty("locationId") + "/media/recent?access_token=" + connectorProperties.getProperty("token");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
	}

    /**
     * Positive test case for locationSearch method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {locationSearch} integration test with mandatory parameters.")
    public void testLocationSearchWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:locationSearch");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_location_search_mandatory.json");

	    String apiEndPoint = "https://api.instagram.com/v1/locations/search?lat=" + connectorProperties.getProperty("lat") + "&lng=" + connectorProperties.getProperty("lng") + "&access_token=" + connectorProperties.getProperty("accessToken");

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

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_location_search_optional.json");

	    String apiEndPoint = "https://api.instagram.com/v1/locations/search?lat=" + connectorProperties.getProperty("lat") + "&lng=" + connectorProperties.getProperty("lng") + "&access_token=" + connectorProperties.getProperty("accessToken") + "&distance=" + connectorProperties.getProperty("distance");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

	    Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("name"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("name"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for locationSearch method.
     */
    @Test(groups = {"wso2.esb"}, description = "instagram {locationSearch} integration test with negative case.")
    public void testLocationSearchWithNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:locationSearch");

	    RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_location_search_negative.json");

	    String apiEndPoint = "https://api.instagram.com/v1/locations/search?lat=" + connectorProperties.getProperty("lat") + "&lng=" + connectorProperties.getProperty("lng") + "&access_token=" + connectorProperties.getProperty("token");

	    RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("error_message"), apiRestResponse.getBody().getJSONObject("meta").get("error_message"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }
}
