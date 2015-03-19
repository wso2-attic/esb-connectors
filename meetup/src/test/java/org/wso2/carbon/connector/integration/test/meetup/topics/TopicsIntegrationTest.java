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

package org.wso2.carbon.connector.integration.test.meetup.topics;

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
public class TopicsIntegrationTest extends MeetupConnectorIntegrationTest {

	@Test(groups = { "wso2.esb" }, description = "meetup {getTopics} integration test")
	public void testGetTopicsWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "topics_getTopics_negative.txt";
		String methodName = "topics_get_topics";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getTopics} integration test")
	public void testGetTopicsWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "topics_getTopics_optional.txt";
		String methodName = "topics_get_topics";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getTopicCategories} integration test")
	public void testGetTopicCategoriesWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "topics_getTopicCategories_negative.txt";
		String methodName = "topics_get_topic_categories";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
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

	@Test(groups = { "wso2.esb" }, description = "meetup {getTopicCategories} integration test")
	public void testGetTopicCategoriesWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "topics_getTopicCategories_optional.txt";
		String methodName = "topics_get_topic_categories";

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

	@Test(groups = { "wso2.esb" },
	      description = "meetup {getRecommendGroupTopics} integration test")
	public void testGetRecommendGroupTopicsWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "topics_getRecommendGroupTopics_mandatory.txt";
		String methodName = "topics_get_recommended_group_topics";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONArray jsonObjectArray = ConnectorIntegrationUtil
					.sendRequestJSONArray(getProxyServiceURL(methodName), modifiedJsonString);
			JSONObject jsonObject = (JSONObject) jsonObjectArray.get(0);
			Assert.assertTrue(jsonObject.has("id"));

			//JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			//Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" },
	      description = "meetup {getRecommendGroupTopics} integration test")
	public void testGetRecommendGroupTopicsWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "topics_getRecommendGroupTopics_negative.txt";
		String methodName = "topics_get_recommended_group_topics";

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

	@Test(groups = { "wso2.esb" },
	      description = "meetup {getRecommendGroupTopics} integration test")
	public void testGetRecommendGroupTopicsWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "topics_getRecommendGroupTopics_optional.txt";
		String methodName = "topics_get_recommended_group_topics";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONArray jsonObjectArray = ConnectorIntegrationUtil
					.sendRequestJSONArray(getProxyServiceURL(methodName), modifiedJsonString);
			JSONObject jsonObject = (JSONObject) jsonObjectArray.get(0);
			Assert.assertTrue(jsonObject.has("id"));
			//JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			// Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

}
