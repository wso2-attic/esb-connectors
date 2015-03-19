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

package org.wso2.carbon.connector.integration.test.meetup.dashboard;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

public class DashboardIntegrationTest extends MeetupConnectorIntegrationTest {
	/**
	 * Test getDashboard API operation.
	 * Expecting Response header '200' and 'last_event' JSONObject in returned
	 * JSONArray. A dashboard of aggregated Meetup information for the
	 * authorized member.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = true, groups = { "wso2.esb" }, description = "Test getDashboard API operation.")
	public void testGetDashboardMandatory() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory +
				"dashboard_getDashboard_mandatory.txt";
		String methodName = "dashboard_getDashboard";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString =
				String.format(jsonString,
				              meetupConnectorProperties.getProperty("access_token"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
		System.out.println(modifiedJsonString +
		                   "********************************************************************");
		try {

			int responseHeader =
					ConnectorIntegrationUtil.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName),
							modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONObject jsonObject =
					ConnectorIntegrationUtil
							.sendRequest(getProxyServiceURL(methodName),
							             modifiedJsonString);
			Assert.assertTrue(jsonObject.has("last_event"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Test getDashboard API operation for negative scenario.
	 * Expecting Response header '401' and 'errors' JSONObject in returned
	 * JSONArray. This operation does not require Authentication token.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "Test getDashboard API operation for negative scenario.")
	public void testGetDashboardNegative() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory +
				"dashboard_getDashboard_negative.txt";
		String methodName = "dashboard_getDashboard";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader =
					ConnectorIntegrationUtil.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName),
							jsonString);
			Assert.assertTrue(responseHeader == 401);

			JSONObject jsonObject =
					ConnectorIntegrationUtil
							.sendRequest(getProxyServiceURL(methodName),
							             jsonString);
			Assert.assertTrue(jsonObject.has("details"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}
}
