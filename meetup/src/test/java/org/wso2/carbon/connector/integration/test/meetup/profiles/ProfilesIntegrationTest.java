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

package org.wso2.carbon.connector.integration.test.meetup.profiles;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

public class ProfilesIntegrationTest extends MeetupConnectorIntegrationTest {

	/**
	 * Test getProfiles API operation for Mandatory fields.
	 * Expecting Response header '200' and 'results' JSONObject.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "Testing {getProfiles} API method")
	public void testGetProfilesMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "profiles_getProfiles_mandatory.txt";
		String methodName = "profiles_getProfilesComments";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);

			Assert.assertTrue(jsonObject.has("results"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Test CreateProfile API operation for Mandatory fields.
	 * Expecting Response header '201'.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "Testing {createProfile} API method")
	public void testCreateProfileMandatory() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "profiles_createProfile_mandatory.txt";
		String methodName = "profiles_createProfile";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties
				                                          .getProperty("access_token"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 201);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Test editProfile API operation for Mandatory fields.
	 * Expecting Response header '201'.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "Testing {editProfile} API method")
	public void testEditProfileMandatory() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "profiles_editProfile_mandatory.txt";
		String methodName = "profiles_editProfile";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties
				                                          .getProperty("access_token"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			JSONObject toCompare = new JSONObject(modifiedJsonString);
			Assert.assertTrue(jsonObject.get("bio").equals(toCompare.get("intro")));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Test getProfile API operation for Mandatory fields.
	 * Expecting Response header '201'.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "Testing {getProfile} API method")
	public void testGetProfileMandatory() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "profiles_getProfile_mandatory.txt";
		String methodName = "profiles_getProfile";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties
				                                          .getProperty("access_token"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);

			Assert.assertTrue(jsonObject.has("member_id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Test delete Profile API operation for Mandatory fields.
	 * Expecting Response header '201'.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "Testing {getProfile} API method")
	public void testDeleteProfileMandatory() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "profiles_deleteProfile_mandatory.txt";
		String methodName = "profiles_deleteProfile";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties
				                                          .getProperty("access_token"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Test Profile approvals API operation for Mandatory fields.
	 * Expecting Response header '201'.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "Testing {getProfile} API method")
	public void testApprovalsMandatory() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "profiles_approvals_mandatory.txt";
		String methodName = "profiles_approvals";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties
				                                          .getProperty("access_token"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Test Profile decline API operation for Mandatory fields.
	 * Expecting Response header '201'.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "Testing {decline} API method")
	public void testDeclineMandatory() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "profiles_decline_mandatory.txt";
		String methodName = "profiles_decline";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties
				                                          .getProperty("access_token"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}
}