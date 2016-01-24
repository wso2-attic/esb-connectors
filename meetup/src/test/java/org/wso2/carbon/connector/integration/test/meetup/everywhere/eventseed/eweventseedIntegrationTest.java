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

package org.wso2.carbon.connector.integration.test.meetup.everywhere.eventseed;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

/**
 */
public class eweventseedIntegrationTest extends MeetupConnectorIntegrationTest {

	//***********************************************EVERYWHERE EVENT SEED ******************************************************

	// ................................ mantatory parameters test for get_ewseed_event  ....  500 INTERNAL SERVER ERROR ..................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {get_ewseed_event} integration test")
	public void testget_ewseed_eventMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "get_ewseed_event_mandatory.txt";
		String methodName = "meetup_get_ewseed_event";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),

		                                          meetupConnectorProperties.getProperty("id")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);
			// JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			//Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ................................ optional parameters test for get_ewseed_event   ..................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {create_rsvps} integration test")
	public void testget_ewseed_eventOptional() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "get_ewseed_event_optional.txt";
		String methodName = "meetup_get_ewseed_event";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),

		                                          meetupConnectorProperties.getProperty("id")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);
			// JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			//Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  get_ewseed_event .......     ........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {get_ewseed_event}  integration test for negative scenario.")
	public void testget_ewseed_eventNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "get_ewseed_event_negative.txt";
		String methodName = "meetup_get_ewseed_event";

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
			Assert.assertTrue((responseHeader == 401));

			// JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

}
