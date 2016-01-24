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

package org.wso2.carbon.connector.integration.test.meetup.feed;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

/**
 */
public class FeedIntegrationTest extends MeetupConnectorIntegrationTest {

	////************************************      FEED            **************************************************************//

	///////////............... test case for mandatory parameters of activty meetup_feed ...................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {activityfeed} integration test")
	public void testactivityfeedWithRequiredParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "activityfeed_mandatory.txt";
		String methodName = "meetup_activityfeed";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key")
				//
		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("results"));
			System.out.println("--------------@@@@@@@@---------");
			//System.out.println(jsonObject);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}
	////////

	///////////test case for optional parameters of activty meetup_feed
	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {activityfeed} integration test")
	public void testactivityfeedWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "activityfeed_optional.txt";
		String methodName = "meetup_activityfeed";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key")
		                                          // meetupConnectorProperties.getProperty("member_id")
		                                          //  meetupConnectorProperties.getProperty("page_start")
		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			//int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
			//Assert.assertTrue(responseHeader == 200);
			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			//Assert.assertTrue(jsonObject.has("results"));
			System.out.println("--------------@@@@@@@@---------");
			//System.out.println(jsonObject);

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);

			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//...................................negative test case for  activty meetup_feed ...............................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {activityfeed}  integration test for negative scenario.")
	public void testactivityfeedNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "activityfeed_negative.txt";
		String methodName = "meetup_activityfeed";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
	    /*String modifiedJsonString = String.format(jsonString,
                meetupConnectorProperties.getProperty("access_token"),
                meetupConnectorProperties.getProperty("urlname")
        );*/
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 401);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("details"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

}
