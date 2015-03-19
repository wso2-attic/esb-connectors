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

package org.wso2.carbon.connector.integration.test.meetup.rsvps;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

public class RSVPsIntegrationTest extends MeetupConnectorIntegrationTest {

	/**
	 * Test getRSVPs API operation for Mandatory fields.
	 * Expecting Response header '200' and 'results' JSONObject.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "Testing {getRSVPs} API method")
	public void testGetRSVPsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "rsvps_getRSVPs_mandatory.txt";
		String methodName = "rsvps_getRSVPs";

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

			/*JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);

			Assert.assertTrue(jsonObject.has("results"));*/

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Test createRSVP API operation for Mandatory fields.
	 * Expecting Response header '201' JSONObject.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "Testing {createRSVP} API method")
	public void testCreateRSVPMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "rsvps_createRSVP_mandatory.txt";
		String methodName = "rsvps_createRSVP";

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
	 * Test getRSVP API operation for Mandatory fields.
	 * Expecting Response header '200' and 'member' JSONObject.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "Testing {getRSVP} API method")
	public void testGetRSVPMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "rsvps_getRSVP_mandatory.txt";
		String methodName = "rsvps_getRSVP";

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

			Assert.assertTrue(jsonObject.has("member"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}
}