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
package org.wso2.carbon.connector.integration.test.confluence;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ConfluenceConnectorIntegrationTest extends ConnectorIntegrationTestBase {

	private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

	private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

	private String apiUrl;

	/**
	 * Set up the environment.
	 */
	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {

		init("confluence-connector-2.0.0");

		esbRequestHeadersMap.put("Content-Type", "application/json");

		// Create base64-encoded auth string using username and password
		final String authString = connectorProperties.getProperty("username") + ":"
				+ connectorProperties.getProperty("password");
		final String base64AuthString = Base64.encode(authString.getBytes());

		apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);

		apiRequestHeadersMap.putAll(esbRequestHeadersMap);

		connectorProperties.setProperty("responseType", "json");

		apiUrl = connectorProperties.getProperty("apiUrl") + "/rest/api";

		connectorProperties.setProperty("pageLimit", "1");
		connectorProperties.setProperty("pageStart", "0");

	}

	/**
	 * Positive test case for listContent method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {listContent} integration test with mandatory parameters.")
	public void testListContentWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listContent");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listContent_mandatory.json");
		final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("results");
		final String contentId = esbResponseArray.getJSONObject(0).getString("id");
		connectorProperties.setProperty("contentId", contentId);

		final String apiEndPoint = apiUrl + "/content";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("results");

		Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"),
				apiResponseArray.getJSONObject(0).getString("id"));
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("title"),
				apiResponseArray.getJSONObject(0).getString("title"));
		Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length() - 1).getString("id"),
				apiResponseArray.getJSONObject(esbResponseArray.length() - 1).getString("id"));
		Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length() - 1).getString("title"),
				apiResponseArray.getJSONObject(apiResponseArray.length() - 1).getString("title"));
	}

	/**
	 * Positive test case for listContent method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {listContent} integration test with optional parameters.")
	public void testListContentWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listContent");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listContent_optional.json");
		final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("results");

		final String title = connectorProperties.getProperty("contentTitleOpt");
		final String type = connectorProperties.getProperty("contentTypeOpt");
		final String spaceKey = connectorProperties.getProperty("spaceKeyMand");
		final String limit = connectorProperties.getProperty("pageLimit");
		final String start = connectorProperties.getProperty("pageStart");
		final String apiEndPoint = apiUrl + "/content?title=" + URLEncoder.encode(title, "UTF-8") + "&type=" + type
				+ "&spaceKey=" + spaceKey + "&limit=" + limit + "&start=" + start;
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("results");

		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), limit);
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), apiRestResponse.getBody().getString("limit"));
		Assert.assertEquals(esbRestResponse.getBody().getString("start"), start);
		Assert.assertEquals(esbRestResponse.getBody().getString("start"), apiRestResponse.getBody().getString("start"));
		Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("title"),
				apiResponseArray.getJSONObject(0).getString("title"));
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("type"),
				apiResponseArray.getJSONObject(0).getString("type"));
	}

	/**
	 * Negative test case for listContent method.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {listContent} integration test negative case.")
	public void testListContentWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listContent");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listContent_negative.json");

		final String apiEndPoint = apiUrl + "/content?type=INVALID";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getString("statusCode"),
				apiRestResponse.getBody().getString("statusCode"));
		Assert.assertEquals(esbRestResponse.getBody().getString("message"),
				apiRestResponse.getBody().getString("message"));
	}

	/**
	 * Positive test case for getContent method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListContentWithMandatoryParameters" }, description = "confluence {getContent} integration test with mandatory parameters.")
	public void testGetContentWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContent");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContent_mandatory.json");

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId");
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
		Assert.assertEquals(esbRestResponse.getBody().getString("status"),
				apiRestResponse.getBody().getString("status"));
		Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("history").getString("createdDate"),
				apiRestResponse.getBody().getJSONObject("history").getString("createdDate"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("space").getString("name"),
				apiRestResponse.getBody().getJSONObject("space").getString("name"));

	}

	/**
	 * Positive test case for getContent method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListContentWithMandatoryParameters" }, description = "confluence {getContent} integration test with optional parameters.")
	public void testGetContentWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContent");

		final String contentStatus = "any";
		final String contentExpand = "version";
		connectorProperties.setProperty("contentStatus", contentStatus);
		connectorProperties.setProperty("contentExpand", contentExpand);

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContent_optional.json");

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "?status="
				+ contentStatus + "&expand=" + contentExpand;
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getString("status"),
				apiRestResponse.getBody().getString("status"));
		Assert.assertEquals(esbRestResponse.getBody().has("version"), apiRestResponse.getBody().has("version"));

	}

	/**
	 * Negative test case for getContent method.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListContentWithMandatoryParameters" }, description = "confluence {getContent} integration test negative case.")
	public void testGetContentWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContent");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContent_negative.json");

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId")
				+ "?status=INVALID";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getString("statusCode"),
				apiRestResponse.getBody().getString("statusCode"));
		Assert.assertEquals(esbRestResponse.getBody().getString("message"),
				apiRestResponse.getBody().getString("message"));
	}

	/**
	 * Test case: testUpdateContentWithMandatoryParameters. 
	 * Status: Skipped.
	 * Reason : There are no mandatory parameters to assert.
	 */

	/**
	 * Positive test case for updateContent method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = {
			"wso2.esb" }, description = "confluence {updateContent} integration test with optional parameters.")
	public void testUpdateContentWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:updateContent");

		// Creating a unique content title by appending the date string
		connectorProperties.put("contentTitleUpdate", "Content Title: " + new Date().toString());

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentIdToUpdate");
		RestResponse<JSONObject> apiResponseBeforeUpdate = sendJsonRestRequest(apiEndPoint, "GET",
				apiRequestHeadersMap);

		final Integer nextContentVersion = apiResponseBeforeUpdate.getBody().getJSONObject("version").getInt("number")
				+ 1;
		connectorProperties.setProperty("nextContentVersion", nextContentVersion.toString());
		final String contentType = apiResponseBeforeUpdate.getBody().getString("type");
		connectorProperties.setProperty("contentType", contentType);

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_updateContent_mandatory.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		RestResponse<JSONObject> apiResponseAfterUpdate = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertNotEquals(apiResponseBeforeUpdate.getBody().getString("title"),
				apiResponseAfterUpdate.getBody().getString("title"));
		Assert.assertNotEquals(apiResponseBeforeUpdate.getBody().getJSONObject("version").getInt("number"),
				apiResponseAfterUpdate.getBody().getJSONObject("version").getInt("number"));
		Assert.assertNotEquals(apiResponseBeforeUpdate.getBody().getJSONObject("version").getString("when"),
				apiResponseAfterUpdate.getBody().getJSONObject("version").getString("when"));
		Assert.assertEquals(apiResponseAfterUpdate.getBody().getString("title"),
				connectorProperties.get("contentTitleUpdate"));

	}

	/**
	 * Negative test case for updateContent method.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {updateContent} integration test negative case.")
	public void testUpdateContentWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:updateContent");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_updateContent_negative.json");

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentIdToUpdate");
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap,
				"api_updateContent_negative.json");

		Assert.assertEquals(esbRestResponse.getBody().getString("statusCode"),
				apiRestResponse.getBody().getString("statusCode"));
		Assert.assertEquals(esbRestResponse.getBody().getString("message"),
				apiRestResponse.getBody().getString("message"));
	}

	/**
	 * Positive test case for returnContentHistory method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListContentWithMandatoryParameters" }, description = "confluence {returnContentHistory} integration test with mandatory parameters.")
	public void testReturnContentHistoryMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:returnContentHistory");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_returnContentHistory_mandatory.json");

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "/history";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getString("createdDate"),
				apiRestResponse.getBody().getString("createdDate"));
		Assert.assertEquals(esbRestResponse.getBody().getBoolean("latest"),
				apiRestResponse.getBody().getBoolean("latest"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("createdBy").getString("type"),
				apiRestResponse.getBody().getJSONObject("createdBy").getString("type"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("lastUpdated").getString("when"),
				apiRestResponse.getBody().getJSONObject("lastUpdated").getString("when"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("lastUpdated").getString("number"),
				apiRestResponse.getBody().getJSONObject("lastUpdated").getString("number"));

	}

	/**
	 * Positive test case for returnContentHistory method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListContentWithMandatoryParameters" }, description = "confluence {returnContentHistory} integration test with optional parameters.")
	public void testReturnContentHistoryOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:returnContentHistory");
		final String contentExpand = "nextVersion";
		connectorProperties.setProperty("contentExpand", contentExpand);

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_returnContentHistory_optional.json");

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId")
				+ "/history?expand=" + contentExpand;
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_expandable").length(), 2);
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_expandable").length(),
				apiRestResponse.getBody().getJSONObject("_expandable").length());
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_expandable").has(contentExpand), false);
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_expandable").has(contentExpand),
				apiRestResponse.getBody().getJSONObject("_expandable").has(contentExpand));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_expandable").has("previousVersion"), true);
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_expandable").has("previousVersion"),
				apiRestResponse.getBody().getJSONObject("_expandable").has("previousVersion"));

	}

	/**
	 * Test case: testReturnContentHistoryWithNegativeCase. 
	 * Status: Skipped.
	 * Reason : There is no negative case to assert.
	 */

	/**
	 * Positive test case for search method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {search} integration test with mandatory parameters.")
	public void testSearchWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:search");
		final String cqlString = "type=" + connectorProperties.getProperty("contentTypeOpt");
		connectorProperties.setProperty("cqlString", cqlString);

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_search_mandatory.json");
		final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("results");

		String apiEndPoint = apiUrl + "/content/search?cql=" + cqlString;
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("results");

		Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"),
				apiResponseArray.getJSONObject(0).getString("id"));
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("title"),
				apiResponseArray.getJSONObject(0).getString("title"));
		Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length() - 1).getString("id"),
				apiResponseArray.getJSONObject(esbResponseArray.length() - 1).getString("id"));
		Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length() - 1).getString("title"),
				apiResponseArray.getJSONObject(apiResponseArray.length() - 1).getString("title"));
	}

	/**
	 * Positive test case for search method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {search} integration test with optional parameters.")
	public void testSearchWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:search");
		final String cqlString = "type=" + connectorProperties.getProperty("contentTypeOpt");
		connectorProperties.setProperty("cqlString", cqlString);

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_search_optional.json");
		final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("results");

		final String limit = connectorProperties.getProperty("pageLimit");
		final String start = connectorProperties.getProperty("pageStart");
		final String apiEndPoint = apiUrl + "/content/search?cql=" + cqlString + "&limit=" + limit + "&start=" + start;
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("results");

		Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), limit);
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), apiRestResponse.getBody().getString("limit"));
		Assert.assertEquals(esbRestResponse.getBody().getString("start"), start);
		Assert.assertEquals(esbRestResponse.getBody().getString("start"), apiRestResponse.getBody().getString("start"));
	}

	/**
	 * Negative test case for search method.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {search} integration test negative case.")
	public void testSearchWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:search");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_search_negative.json");

		final String apiEndPoint = apiUrl + "/content/search?cql=INVALID";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getString("statusCode"),
				apiRestResponse.getBody().getString("statusCode"));
		Assert.assertEquals(esbRestResponse.getBody().getString("message"),
				apiRestResponse.getBody().getString("message"));
	}

	/**
	 * Positive test case for getContentChildren method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = {
			"wso2.esb" }, description = "confluence {getContentChildren} integration test with mandatory parameters.")
	public void testGetContentChildrenWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContentChildren");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContentChildren_mandatory.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentIdWithChildContents")
				+ "/child";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_links").getString("base"),
				apiRestResponse.getBody().getJSONObject("_links").getString("base"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_links").getString("self"),
				apiRestResponse.getBody().getJSONObject("_links").getString("self"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_expandable").getString("attachment"),
				apiRestResponse.getBody().getJSONObject("_expandable").getString("attachment"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_expandable").getString("page"),
				apiRestResponse.getBody().getJSONObject("_expandable").getString("page"));
	}

	/**
	 * Positive test case for getContentChildren method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = {
			"wso2.esb" }, description = "confluence {getContentChildren} integration test with optional parameters.")
	public void testGetContentChildrenWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContentChildren");
		final String childContentExpand = "page";
		connectorProperties.setProperty("childContentExpand", childContentExpand);

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContentChildren_optional.json");
		final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("page").getJSONArray("results");

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentIdWithChildContents")
				+ "/child?expand=" + childContentExpand;
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("page").getJSONArray("results");

		Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("title"),
				apiResponseArray.getJSONObject(0).getString("title"));
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("type"),
				apiResponseArray.getJSONObject(0).getString("type"));
	}

	/**
	 * Test case: testGetContentChildrenWithNegativeCase. 
	 * Status: Skipped.
	 * Reason : There is no negative case to assert.
	 */

	/**
	 * Positive test case for listContentComments method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = {
			"wso2.esb" }, description = "confluence {listContentComments} integration test with mandatory parameters.")
	public void testListContentCommentsWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listContentComments");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listContentComments_mandatory.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentIdWithComments")
				+ "/child/comment";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("title"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("title"));
		Assert.assertEquals(
				esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("location"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("location"));
		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), apiRestResponse.getBody().getString("limit"));
	}

	/**
	 * Positive test case for listContentComments method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = {
			"wso2.esb" }, description = "confluence {listContentComments} integration test with optional parameters.")
	public void testListContentCommentsWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listContentComments");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listContentComments_optional.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentIdWithComments")
				+ "/child/comment?expand=extensions.resolution&start=" + connectorProperties.getProperty("pageStart")
				+ "&limit=" + connectorProperties.getProperty("pageLimit") + "&parentVersion=0&depth=all";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("title"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("title"));
		Assert.assertEquals(
				esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("location"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("location"));
		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), connectorProperties.getProperty("pageLimit"));
		Assert.assertEquals(esbRestResponse.getBody().getString("start"), connectorProperties.getProperty("pageStart"));
	}

	/**
	 * Test case: testListContentCommentsWithNegativeCase. 
	 * Status: Skipped.
	 * Reason : There is no negative case to assert.
	 */

	/**
	 * Positive test case for getAttachment method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = {
			"wso2.esb" }, description = "confluence {getAttachment} integration test with mandatory parameters.")
	public void testGetAttachmentWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getAttachment");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getAttachment_mandatory.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		final String attachmentName = esbRestResponse.getBody().getJSONArray("results").getJSONObject(0)
				.getString("title");
		connectorProperties.setProperty("attachmentName", attachmentName);
		final String attachmentMediaType = esbRestResponse.getBody().getJSONArray("results").getJSONObject(0)
				.getJSONObject("metadata").getString("mediaType");
		connectorProperties.setProperty("attachmentMediaType", attachmentMediaType);

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentIdWithAttachments")
				+ "/child/attachment";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("title"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("title"));
		Assert.assertEquals(
				esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("fileSize"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("fileSize"));
		Assert.assertEquals(
				esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("mediaType"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("mediaType"));
		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), apiRestResponse.getBody().getString("limit"));
	}

	/**
	 * Positive test case for getAttachment method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testGetAttachmentWithMandatoryParameters" }, description = "confluence {getAttachment} integration test with optional parameters.")
	public void testGetAttachmentWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getAttachment");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getAttachment_optional.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentIdWithAttachments")
				+ "/child/attachment?start=" + connectorProperties.getProperty("pageStart") + "&limit="
				+ connectorProperties.getProperty("pageLimit") + "&expand=version,container&filename="
				+ URLEncoder.encode(connectorProperties.getProperty("attachmentName"), "UTF-8") + "&mediaType="
				+ URLEncoder.encode(connectorProperties.getProperty("attachmentMediaType"), "UTF-8");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("title"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("title"));
		Assert.assertEquals(
				esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("fileSize"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("fileSize"));
		Assert.assertEquals(
				esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("mediaType"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("extensions")
						.getString("mediaType"));
		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), connectorProperties.getProperty("pageLimit"));
		Assert.assertEquals(esbRestResponse.getBody().getString("start"), connectorProperties.getProperty("pageStart"));
	}

	/**
	 * Test case: testGetAttachmentWithWithNegativeCase. 
	 * Status: Skipped. 
	 * Reason: There is no negative case to assert.
	 */

	/**
	 * Positive test case for addContentLabels method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListContentWithMandatoryParameters" }, description = "confluence {addContentLabels} integration test with mandatory parameters.")
	public void testAddContentLabelsWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:addContentLabels");

		final String contentLabel = "label" + new Date().getTime();
		connectorProperties.setProperty("contentLabel", contentLabel);

		final String requestUrl = proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&password="
				+ connectorProperties.getProperty("password") + "&id=" + connectorProperties.getProperty("contentId")
				+ "&username=" + connectorProperties.getProperty("username");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(requestUrl, "POST", esbRequestHeadersMap,
				"esb_addContentLabels_mandatory.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "/label";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		JSONArray labelsArray = apiRestResponse.getBody().getJSONArray("results");
		boolean hasLabel = false;
		for (int i = 0; i < labelsArray.length(); i++) {
			if (contentLabel.equals(labelsArray.getJSONObject(i).getString("name"))) {
				hasLabel = true;
				break;
			}
		}
		Assert.assertEquals(hasLabel, true);
	}

	/**
	 * Test case: testAddContentLabelsWithOptionalParameters. 
	 * Status: Skipped.
	 * Reason : There are no optional parameters to assert.
	 */

	/**
	 * Negative test case for addContentLabels method.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListContentWithMandatoryParameters" }, description = "confluence {addContentLabels} integration test negative case.")
	public void testAddContentLabelsWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:addContentLabels");

		final String requestUrl = proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&password="
				+ connectorProperties.getProperty("password") + "&id=" + connectorProperties.getProperty("contentId")
				+ "&username=" + connectorProperties.getProperty("username");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(requestUrl, "POST", esbRequestHeadersMap,
				"esb_addContentLabels_negative.json");

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "/label";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
				"api_addContentLabels_negative.json");

		Assert.assertEquals(esbRestResponse.getBody().getString("statusCode"),
				apiRestResponse.getBody().getString("statusCode"));
		Assert.assertEquals(esbRestResponse.getBody().getString("message"),
				apiRestResponse.getBody().getString("message"));
	}

	/**
	 * Positive test case for getContentLabels method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testAddContentLabelsWithMandatoryParameters" }, description = "confluence {getContentLabels} integration test with mandatory parameters.")
	public void testGetContentLabelsWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContentLabels");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContentLabels_mandatory.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "/label";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("prefix"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("prefix"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("name"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("name"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"));
		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), apiRestResponse.getBody().getString("limit"));
	}

	/**
	 * Positive test case for getContentLabels method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testAddContentLabelsWithMandatoryParameters" }, description = "confluence {getContentLabels} integration test with optional parameters.")
	public void testGetContentLabelsWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContentLabels");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContentLabels_optional.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId")
				+ "/label?prefix=global&start=" + connectorProperties.getProperty("pageStart") + "&limit="
				+ connectorProperties.getProperty("pageLimit");
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("prefix"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("prefix"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("name"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("name"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"));
		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), connectorProperties.getProperty("pageLimit"));
		Assert.assertEquals(esbRestResponse.getBody().getString("start"), connectorProperties.getProperty("pageStart"));
	}

	/**
	 * Negative test case for getContentLabels method.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testAddContentLabelsWithMandatoryParameters" }, description = "confluence {getContentLabels} integration test with negative case.")
	public void testGetContentLabelsWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContentLabels");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContentLabels_negative.json");

		String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId")
				+ "/label?prefix=INVALID";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(apiRestResponse.getBody().getString("message"),
				esbRestResponse.getBody().getString("message"));
	}

	/**
	 * Positive test case for removeContentLabel method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testGetContentLabelsWithNegativeCase" }, description = "confluence {removeContentLabel} integration test with mandatory parameters.")
	public void testRemoveContentLabelWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:removeContentLabel");

		final String contentLabel = connectorProperties.getProperty("contentLabel");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_removeContentLabel_mandatory.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);

		String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "/label";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		JSONArray labelsArray = apiRestResponse.getBody().getJSONArray("results");
		boolean hasLabel = false;
		for (int i = 0; i < labelsArray.length(); i++) {
			if (contentLabel.equals(labelsArray.getJSONObject(i).getString("name"))) {
				hasLabel = true;
				break;
			}
		}
		Assert.assertEquals(hasLabel, false);
	}

	/**
	 * Negative test case for removeContentLabel method.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testRemoveContentLabelWithMandatoryParameters" }, description = "confluence {removeContentLabel} integration test with negative case.")
	public void testRemoveContentLabelWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:removeContentLabel");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_removeContentLabel_negative.json");

		String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "/label?name="
				+ connectorProperties.getProperty("contentLabel");
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(apiRestResponse.getBody().getString("message"),
				esbRestResponse.getBody().getString("message"));
	}

	/**
	 * Positive test case for createNewContentProperty method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListContentWithOptionalParameters" }, description = "confluence {createNewContentProperty} integration test with mandatory parameters.")
	public void testCreateNewContentPropertyWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:createNewContentProperty");

		final String propertyKey = "PROPKEY" + new Date().getTime();
		final String propertyValueKey = "propertykey" + new Date().getTime();
		final String propertyValue = "propertyvalue" + new Date().getTime();
		connectorProperties.setProperty("propertyKey", propertyKey);
		connectorProperties.setProperty("propertyValue", propertyValue);
		connectorProperties.setProperty("propertyValueKey", propertyValueKey);

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_createNewContentProperty_mandatory.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "/property/"
				+ connectorProperties.getProperty("propertyKey");
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
		Assert.assertEquals(apiRestResponse.getBody().getJSONObject("value").names().getString(0),
				connectorProperties.getProperty("propertyValueKey"));
	}

	/**
	 * Test case: testCreateNewContentPropertyWithOptionalParameters. 
	 * Status: Skipped. 
	 * Reason : There are no optional parameters to assert.
	 */

	/**
	 * Test case: testCreateNewContentPropertyWithNegativeCase. 
	 * Status: Skipped.
	 * Reason : No valid JSON response returned.
	 */

	/**
	 * Positive test case for listContentProperties method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testCreateNewContentPropertyWithMandatoryParameters" }, description = "confluence {listContentProperties} integration test with mandatory parameters.")
	public void testListContentPropertiesWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listContentProperties");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listContentProperties_mandatory.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "/property";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("key"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("key"));
		Assert.assertEquals(
				esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("version")
						.getString("when"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("version")
						.getString("when"));
		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), apiRestResponse.getBody().getString("limit"));
	}

	/**
	 * Positive test case for listContentProperties method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testCreateNewContentPropertyWithMandatoryParameters" }, description = "confluence {listContentProperties} integration test with optional parameters.")
	public void testListContentPropertiesWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listContentProperties");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listContentProperties_optional.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

		String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId")
				+ "/property?expand=content,version&start=" + connectorProperties.getProperty("pageStart") + "&limit="
				+ connectorProperties.getProperty("pageLimit");
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("key"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("key"));
		Assert.assertEquals(
				esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("version")
						.getString("when"),
				apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getJSONObject("version")
						.getString("when"));
		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), connectorProperties.getProperty("pageLimit"));
		Assert.assertEquals(esbRestResponse.getBody().getString("start"), connectorProperties.getProperty("pageStart"));
	}

	/**
	 * Test case: testListContentPropertiesWithNegativeCase. 
	 * Status: Skipped.
	 * Reason : No error message is returned.
	 */

	/**
	 * Positive test case for getContentProperty method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testCreateNewContentPropertyWithMandatoryParameters" }, description = "confluence {getContentProperty} integration test with mandatory parameters.")
	public void testGetContentPropertyWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContentProperty");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContentProperty_mandatory.json");
		final JSONObject esbRestResponseObject = esbRestResponse.getBody();

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "/property/"
				+ connectorProperties.getProperty("propertyKey");
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		final JSONObject apiRestResponseObject = apiRestResponse.getBody();

		Assert.assertEquals(esbRestResponseObject.getJSONObject("value").toString(),
				apiRestResponseObject.getJSONObject("value").toString());
		Assert.assertEquals(esbRestResponseObject.getJSONObject("version").getString("when"),
				apiRestResponseObject.getJSONObject("version").getString("when"));
	}

	/**
	 * Positive test case for getContentProperty method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testCreateNewContentPropertyWithMandatoryParameters" }, description = "confluence {getContentProperty} integration test with optional parameters.")
	public void testGetContentPropertyWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContentProperty");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContentProperty_optional.json");
		final JSONObject esbContentObject = esbRestResponse.getBody();

		Assert.assertEquals(esbContentObject.has("content"), true);

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId") + "/property/"
				+ connectorProperties.getProperty("propertyKey") + "?expand=content";

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		final JSONObject apiContentObject = apiRestResponse.getBody();

		Assert.assertEquals(esbContentObject.has("content"), true);
		Assert.assertEquals(esbContentObject.has("content"), apiContentObject.has("content"));
		Assert.assertEquals(esbContentObject.getJSONObject("content").getString("id"),
				apiContentObject.getJSONObject("content").getString("id"));
		Assert.assertEquals(esbContentObject.getJSONObject("content").getString("title"),
				apiContentObject.getJSONObject("content").getString("title"));
	}

	/**
	 * Negative test case for getContentProperty method.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testCreateNewContentPropertyWithMandatoryParameters" }, description = "confluence {getContentProperty} integration test with optional parameters.")
	public void testGetContentPropertyWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getContentProperty");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getContentProperty_negative.json");

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId")
				+ "/property/INVALID";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
		Assert.assertEquals(esbRestResponse.getBody().getString("message"),
				apiRestResponse.getBody().getString("message"));
	}

	/**
	 * Positive test case for createSpace method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {createSpace} integration test with mandatory parameters.")
	public void testCreateSpaceWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:createSpace");

		final String spaceKeyForMand = "keyMand" + new Date().getTime();
		connectorProperties.setProperty("spaceKeyForMand", spaceKeyForMand);

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_createSpace_mandatory.json");

		final String apiEndPoint = apiUrl + "/space?spaceKey=" + connectorProperties.getProperty("spaceKeyForMand");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("results");

		Assert.assertEquals(esbRestResponse.getBody().getString("id"),
				apiResponseArray.getJSONObject(0).getString("id"));
		Assert.assertEquals(esbRestResponse.getBody().getString("name"),
				connectorProperties.getProperty("spaceNameMand"));
	}

	/**
	 * Positive test case for createSpace method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {createSpace} integration test with optional parameters.")
	public void testCreateSpaceWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:createSpace");

		final String spaceKeyForOpt = "keyOpt" + new Date().getTime();
		connectorProperties.setProperty("spaceKeyForOpt", spaceKeyForOpt);

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_createSpace_optional.json");
		String apiEndPoint = apiUrl + "/space?spaceKey=" + connectorProperties.getProperty("spaceKeyForOpt");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("results");

		Assert.assertEquals(esbRestResponse.getBody().getString("id"),
				apiResponseArray.getJSONObject(0).getString("id"));
		Assert.assertEquals(
				esbRestResponse.getBody().getJSONObject("description").getJSONObject("plain").getString("value"),
				connectorProperties.getProperty("spaceDescription"));
	}

	/**
	 * Negative test case for createSpace method. Trying to create a space by providing the same key.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testCreateSpaceWithMandatoryParameters" }, description = "confluence {createSpace} integration test with negative case.")
	public void testCreateSpaceWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:createSpace");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_createSpace_negative.json");

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

		final String apiEndPoint = apiUrl + "/space";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
				"api_createSpace_negative.json");

		Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
		Assert.assertEquals(esbRestResponse.getBody().getString("message"),
				apiRestResponse.getBody().getString("message"));
	}

	/**
	 * Positive test case for getSpaceInfo method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testCreateSpaceWithMandatoryParameters" }, description = "confluence {getSpaceInfo} integration test with mandatory parameters.")
	public void testGetSpaceInfoWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getSpaceInfo");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getSpaceInfo_mandatory.json");
		final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("results");

		final String apiEndPoint = apiUrl + "/space?spaceKey=" + connectorProperties.getProperty("spaceKeyForMand");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("results");

		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"),
				apiResponseArray.getJSONObject(0).getString("id"));
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"),
				apiResponseArray.getJSONObject(0).getString("name"));

	}

	/**
	 * Positive test case for getSpaceInfo method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testCreateSpaceWithMandatoryParameters" }, description = "confluence {getSpaceInfo} integration test with optional parameters.")
	public void testGetSpaceInfoWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getSpaceInfo");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getSpaceInfo_optional.json");

		final String apiEndPoint = apiUrl + "/space?spaceKey=" + connectorProperties.getProperty("spaceKeyForMand")
				+ "&limit=" + connectorProperties.getProperty("pageLimit") + "&start="
				+ connectorProperties.getProperty("pageStart");
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getString("start"), apiRestResponse.getBody().getString("start"));
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), apiRestResponse.getBody().getString("limit"));
		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
	}

	/**
	 * Negative test case for getSpaceInfo method.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {getSpaceInfo} integration test with negative case.")
	public void testGetSpaceInfoWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getSpaceInfo");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getSpaceInfo_negative.json");

		final String apiEndPoint = apiUrl + "/space?spaceKey=INVALID";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getInt("size"), 0);
		Assert.assertEquals(apiRestResponse.getBody().getInt("size"), 0);
	}

	/**
	 * Positive test case for listLongRunningTasks method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = {
			"wso2.esb" }, description = "confluence {listLongRunningTasks} integration test with mandatory parameters.")
	public void testListLongRunningTasksWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listLongRunningTasks");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listLongRunningTasks_mandatory.json");
		final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("results");

		final String taskId = esbResponseArray.getJSONObject(0).getString("id");
		connectorProperties.setProperty("taskId", taskId);

		final String apiEndPoint = apiUrl + "/longtask";

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("results");

		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"),
				apiResponseArray.getJSONObject(0).getString("id"));
		Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("name").getString("translation"),
				apiResponseArray.getJSONObject(0).getJSONObject("name").getString("translation"));
	}

	/**
	 * Positive test case for listLongRunningTasks method with optional parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = {
			"wso2.esb" }, description = "confluence {listLongRunningTasks} integration test with optional parameters.")
	public void testListLongRunningTasksWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listLongRunningTasks");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listLongRunningTasks_optional.json");

		final String apiEndPoint = apiUrl + "/longtask?limit=" + connectorProperties.getProperty("pageLimit")
				+ "&start=" + connectorProperties.getProperty("pageStart");

		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getString("start"), apiRestResponse.getBody().getString("start"));
		Assert.assertEquals(esbRestResponse.getBody().getString("limit"), apiRestResponse.getBody().getString("limit"));
		Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
	}

	/**
	 * Test case: testListLongRunningTasksWithNegativeCase. 
	 * Status: Skipped.
	 * Reason : No valid JSON response is returned.
	 */

	/**
	 * Positive test case for getLongRunningTask method with mandatory parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListLongRunningTasksWithMandatoryParameters" }, description = "confluence {getLongRunningTask} integration test with mandatory parameters.")
	public void testGetLongRunningTaskWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getLongRunningTask");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getLongRunningTask_mandatory.json");

		final String apiEndPoint = apiUrl + "/longtask/" + connectorProperties.getProperty("taskId");
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("name").getString("translation"),
				apiRestResponse.getBody().getJSONObject("name").getString("translation"));
		Assert.assertEquals(esbRestResponse.getBody().getString("elapsedTime"),
				apiRestResponse.getBody().getString("elapsedTime"));
		Assert.assertEquals(esbRestResponse.getBody().getString("percentageComplete"),
				apiRestResponse.getBody().getString("percentageComplete"));
		Assert.assertEquals(esbRestResponse.getBody().getString("successful"),
				apiRestResponse.getBody().getString("successful"));

	}

	/**
	 * Test case: testGetLongRunningTaskWithOptionalParameters. 
	 * Status: Skipped.
	 * Reason : Cannot assert optional parameter 'expand'. Response does not return expandable parameters.
	 */

	/**
	 * Negative test case for getLongRunningTask method.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, description = "confluence {getLongRunningTask} integration test with negative case.")
	public void testGetLongRunningTaskWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getLongRunningTask");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_getLongRunningTask_negative.json");

		String apiEndPoint = apiUrl + "/longtask/INVALID";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(apiRestResponse.getBody().getString("message"),
				esbRestResponse.getBody().getString("message"));
	}

	/**
	 * Positive test case for listOperationRestrictions method with mandatory
	 * parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListContentWithMandatoryParameters" }, description = "confluence {listOperationRestrictions} integration test with mandatory parameters.")
	public void testListOperationRestrictionsWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listOperationRestrictions");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listOperationRestrictions_mandatory.json");
		JSONObject esbReadRestrictions = esbRestResponse.getBody().getJSONObject("read").getJSONObject("restrictions");
		JSONObject esbUpdateRestrictions = esbRestResponse.getBody().getJSONObject("update")
				.getJSONObject("restrictions");

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId")
				+ "/restriction/byOperation";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		JSONObject apiReadRestrictions = apiRestResponse.getBody().getJSONObject("read").getJSONObject("restrictions");
		JSONObject apiUpdateRestrictions = apiRestResponse.getBody().getJSONObject("update")
				.getJSONObject("restrictions");

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("read").getString("operation"),
				apiRestResponse.getBody().getJSONObject("read").getString("operation"));
		Assert.assertEquals(esbReadRestrictions.getJSONObject("user").getString("results"),
				apiReadRestrictions.getJSONObject("user").getString("results"));
		Assert.assertEquals(esbReadRestrictions.getJSONObject("user").getString("limit"),
				apiReadRestrictions.getJSONObject("user").getString("limit"));
		Assert.assertEquals(esbUpdateRestrictions.getJSONObject("user").getString("results"),
				apiUpdateRestrictions.getJSONObject("user").getString("results"));
		Assert.assertEquals(esbUpdateRestrictions.getJSONObject("user").getString("limit"),
				apiUpdateRestrictions.getJSONObject("user").getString("limit"));
	}

	/**
	 * Positive test case for listOperationRestrictions method with optional
	 * parameters.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = {
			"testListContentWithMandatoryParameters" }, description = "confluence {listOperationRestrictions} integration test with optional parameters.")
	public void testListOperationRestrictionsWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listOperationRestrictions");

		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
				"esb_listOperationRestrictions_optional.json");

		final String apiEndPoint = apiUrl + "/content/" + connectorProperties.getProperty("contentId")
				+ "/restriction/byOperation?expand=update";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().length(), apiRestResponse.getBody().length());
		Assert.assertEquals(esbRestResponse.getBody().has("read"), false);
		Assert.assertEquals(esbRestResponse.getBody().has("read"), apiRestResponse.getBody().has("read"));
		Assert.assertEquals(esbRestResponse.getBody().has("update"), true);
		Assert.assertEquals(esbRestResponse.getBody().has("update"), apiRestResponse.getBody().has("update"));

	}

	/**
	 * Test case: testListOperationRestrictionsWithNegativeCase. 
	 * Status: Skipped. 
	 * Reason : No valid JSON response is returned.
	 */

}
