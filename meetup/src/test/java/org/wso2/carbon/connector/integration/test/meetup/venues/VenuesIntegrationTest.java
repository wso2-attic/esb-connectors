/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.wso2.carbon.connector.integration.test.meetup.venues;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

/**
 * Created by inshaf on 9/11/14.
 */
public class VenuesIntegrationTest extends MeetupConnectorIntegrationTest {

	@Test(groups = { "wso2.esb" }, description = "meetup {getOpenVenues} integration test")
	public void testGetOpenVenuesRequiredParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "venues_getOpenVenues_mandatory.txt";
		String methodName = "venues_get_open_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("results"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getOpenVenues} integration test")
	public void testGetOpenVenuesWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "venues_getOpenVenues_negative.txt";
		String methodName = "venues_get_open_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 401);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getOpenVenues} integration test")
	public void testGetOpenVenuesWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "venues_getOpenVenues_optional.txt";
		String methodName = "venues_get_open_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("results"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getVenues} integration test")
	public void testGetVenuesRequiredParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "venues_getVenues_mandatory.txt";
		String methodName = "venues_get_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("results"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getVenues} integration test")
	public void testGetVenuesWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "venues_getVenues_negative.txt";
		String methodName = "venues_get_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 401);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getVenues} integration test")
	public void testGetVenuesWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "venues_getVenues_optional.txt";
		String methodName = "venues_get_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("results"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getRecommendedVenues} integration test")
	public void testGetRecommendedVenuesWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "venues_getRecommendedVenues_negative.txt";
		String methodName = "venues_get_recommended_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 401);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getRecommendedVenues} integration test")
	public void testGetRecommendedVenuesWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "venues_getRecommendedVenues_optional.txt";
		String methodName = "venues_get_recommended_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONArray jsonObjectArray = ConnectorIntegrationUtil
					.sendRequestJSONArray(getProxyServiceURL(methodName), modifiedJsonString);

			JSONObject jsonObject = (JSONObject) jsonObjectArray.get(0);
			Assert.assertTrue(jsonObject.has("visibility"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {PostVenues} integration test")
	public void testPostVenuesRequiredParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "venues_postVenues_mandatory.txt";
		String methodName = "venues_post_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
		//System.out.println("modifiedJsonString-"+modifiedJsonString);

		try {

			//JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			//System.out.println("jsonObject-"+jsonObject);

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			System.out.println("responseHeader-" + responseHeader);
			Assert.assertTrue(responseHeader == 201);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {PostVenues} integration test")
	public void testPostVenuesWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "venues_postVenues_negative.txt";
		String methodName = "venues_post_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			System.out.println("responseHeader-" + responseHeader);
			Assert.assertTrue(responseHeader == 401);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {PostVenues} integration test")
	public void testPostVenuesWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "venues_postVenues_optional.txt";
		String methodName = "venues_post_venues";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			System.out.println("responseHeader-" + responseHeader);
			Assert.assertTrue(responseHeader == 201);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

}
