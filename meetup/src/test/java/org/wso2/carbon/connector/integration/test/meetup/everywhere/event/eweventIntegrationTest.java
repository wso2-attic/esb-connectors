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

package org.wso2.carbon.connector.integration.test.meetup.everywhere.event;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

/**
 */
public class eweventIntegrationTest extends MeetupConnectorIntegrationTest {

	//**************************************       EVERYWHERE-EVENTS      *********************************************************//

	//    //mantatory parameters test for find everywhere events

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_events} integration test")
	public void testgetew_eventsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_events-mandatory.txt";
		String methodName = "meetup_getew_events";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties
				                                          .getProperty("community_id"),
		                                          meetupConnectorProperties.getProperty("event_id"),
		                                          meetupConnectorProperties
				                                          .getProperty("container_id"),
		                                          meetupConnectorProperties.getProperty("urlname")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);

			Assert.assertTrue(jsonObject.has("results"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //optional parameters test for find everywhere events

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_events} integration test")
	public void testgetew_eventsoptional() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_events_optional.txt";
		String methodName = "meetup_getew_events";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties
				                                          .getProperty("community_id"),
		                                          meetupConnectorProperties.getProperty("event_id"),
		                                          meetupConnectorProperties
				                                          .getProperty("container_id"),
		                                          meetupConnectorProperties.getProperty("urlname")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);

			Assert.assertTrue(jsonObject.has("results"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  get every where events ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_events}  integration test for negative scenario.")
	public void testgetew_eventsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_events_negative.txt";
		String methodName = "meetup_getew_events";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 400);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //......................mantatory parameters test for getew_event_byid  ....................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_event_byid} integration test")
	public void testgetew_event_byidMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_event_byid_mandatory.txt";
		String methodName = "meetup_getew_event_byid";

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

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);

			Assert.assertTrue(jsonObject.has("id"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //......................optional parameters test for getew_event_byid  ....................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_event_byid} integration test")
	public void testgetew_event_byidoptional() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_event_byid_optional.txt";
		String methodName = "meetup_getew_event_byid";

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

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);

			Assert.assertTrue(jsonObject.has("id"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  getew_event_byid ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_event_byid}  integration test for negative scenario.")
	public void testgetew_event_byidNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_event_byid_negative.txt";
		String methodName = "meetup_getew_event_byid";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(
					(responseHeader == 401) || (responseHeader == 405) || (responseHeader == 404));

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //...................... parameters test for edit_ewevent  ..........  all are optional..each time type new details...................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {edit_ewevent} integration test")
	public void testedit_eweventMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "edit_ewevent_mandatory.txt";
		String methodName = "meetup_edit_ewevent";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("id"),
		                                          meetupConnectorProperties
				                                          .getProperty("description"),
		                                          meetupConnectorProperties
				                                          .getProperty("venue_name")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);

			Assert.assertTrue(jsonObject.has("id"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  edit_ewevent ...............................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {edit_ewevent}  integration test for negative scenario.")
	public void testedit_eweventNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "edit_ewevent_negative.txt";
		String methodName = "meetup_edit_ewevent";

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
			Assert.assertTrue(responseHeader == 400);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //......................mantatory parameters test for post_ewevent  ..................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {post_ewevent} integration test")
	public void testpost_eweventMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "post_ewevent_mandatory.txt";
		String methodName = "meetup_post_ewevent";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("id"),
		                                          meetupConnectorProperties
				                                          .getProperty("description"),
		                                          meetupConnectorProperties.getProperty("country")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 201);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);

			Assert.assertTrue(jsonObject.has("lon"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  post_ewevent .......................server internal error 500........................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {post_ewevent}  integration test for negative scenario.")
	public void testpost_eweventNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "post_ewevent_negative.txt";
		String methodName = "meetup_post_ewevent";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 500);

			//JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

}
