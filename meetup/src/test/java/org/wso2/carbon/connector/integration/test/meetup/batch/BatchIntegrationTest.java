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

package org.wso2.carbon.connector.integration.test.meetup.batch;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

// TODO: Auto-generated Javadoc

/**
 * The Class BatchIntegrationTest.
 */
public class BatchIntegrationTest extends MeetupConnectorIntegrationTest {
	/**
	 * Test Batch request API operation for Mandatory fields.
	 * Expecting Response header 200 and status code 200 in returned JSONArray.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "Meetup batchRequests integration test for Mandatory fields.")
	public void testBatchRequestsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "batchRequests_mandatory.txt";
		String methodName = "batch_batchRequests";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString =
				String.format(jsonString,
				              meetupConnectorProperties.getProperty("access_token"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader =
					ConnectorIntegrationUtil.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName),
							modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONArray jsonArray =
					ConnectorIntegrationUtil
							.sendRequestJSONArray(getProxyServiceURL(methodName),
							                      modifiedJsonString);
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			Assert.assertTrue(jsonObject.has("status"));
			Assert.assertTrue(jsonObject.getInt("status") == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Test Batch request API operation for negative scenario.
	 * Expecting 401 request header and errors element in returned JSONObject.
	 *
	 * @throws Exception if test fails.
	 */
	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "Meetup batchRequests integration test for negative scenario.")
	public void testBatchRequestsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "batchRequests_negative.txt";
		String methodName = "batch_batchRequests";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		/*
		 * String modifiedJsonString = String.format(jsonString,
		 * meetupConnectorProperties.getProperty("access_token"),
		 * meetupConnectorProperties.getProperty("requests")
		 * );
		 */
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
			Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}
}
