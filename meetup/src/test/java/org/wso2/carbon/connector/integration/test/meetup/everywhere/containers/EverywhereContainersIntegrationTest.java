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

package org.wso2.carbon.connector.integration.test.meetup.everywhere.containers;

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
public class EverywhereContainersIntegrationTest extends MeetupConnectorIntegrationTest {

	@Test(groups = { "wso2.esb" }, description = "meetup {getContainer} integration test")
	public void testGetContainerRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_getContainer_mandatory.txt";
		String methodName = "everywhere_containers_get_container";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getContainer} integration test")
	public void testGetContainerWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_getContainer_negative.txt";
		String methodName = "everywhere_containers_get_container";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getContainer} integration test")
	public void testGetContainerWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_getContainer_optional.txt";
		String methodName = "everywhere_containers_get_container";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getContainerById} integration test")
	public void testGetContainerByIdWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_getContainerById_negative.txt";
		String methodName = "everywhere_containers_get_container_by_id";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getContainerById} integration test")
	public void testGetContainerByIdWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_getContainerById_optional.txt";
		String methodName = "everywhere_containers_get_container_by_id";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getContainerIdAlert} integration test")
	public void testGetContainerAlertByIdWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_getContainerAlert_negative.txt";
		String methodName = "everywhere_containers_get_container_alerts";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getContainerIdAlert} integration test")
	public void testGetContainerAlertByIdWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_getContainerAlert_optional.txt";
		String methodName = "everywhere_containers_get_container_alerts";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 404);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {postContainer} integration test")
	public void testPostContainerRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_postContainer_mandatory.txt";
		String methodName = "everywhere_containers_post_container";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			System.out.println("jsonObject-" + jsonObject);
			Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {postContainer} integration test")
	public void testPostContainerWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_postContainer_negative.txt";
		String methodName = "everywhere_containers_post_container";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {postContainer} integration test")
	public void testPostContainerWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_postContainer_optional.txt";
		String methodName = "everywhere_containers_post_container";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			System.out.println("jsonObject-" + jsonObject);
			Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {postContainerById} integration test")
	public void testPostContainerByIdWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_postContainerById_negative.txt";
		String methodName = "everywhere_containers_post_container_by_id";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {postContainerById} integration test")
	public void testPostContainerByIdWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_postContainerById_optional.txt";
		String methodName = "everywhere_containers_post_container_by_id";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			System.out.println("jsonObject-" + jsonObject);
			Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {postContainerAlertById} integration test")
	public void testPostContainerAlertByIdWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_postContainerAlert_negative.txt";
		String methodName = "everywhere_containers_post_container_alerts";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {postContainerAlertById} integration test")
	public void testPostContainerAlertByIdWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "everywhere_container_postContainerAlert_optional.txt";
		String methodName = "everywhere_containers_post_container_alerts";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			System.out.println("jsonObject-" + jsonObject);
			Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

}
