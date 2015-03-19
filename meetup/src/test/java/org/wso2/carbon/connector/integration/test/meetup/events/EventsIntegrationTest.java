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

package org.wso2.carbon.connector.integration.test.meetup.events;

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
public class EventsIntegrationTest extends MeetupConnectorIntegrationTest {

	@Test(groups = { "wso2.esb" }, description = "meetup {getOpenEvents} integration test")
	public void testGetOpenEventsWithRequiredParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getOpenEvents_mandatory.txt";
		String methodName = "events_get_open_events";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getOpenEvents} integration test")
	public void testGetOpenEventsWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getOpenEvents_negative.txt";
		String methodName = "events_get_open_events";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getOpenEvents} integration test")
	public void testGetOpenEventsWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getOpenEvents_optional.txt";
		String methodName = "events_get_open_events";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getConcierge} integration test")
	public void testGetConciergeWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getConcierge_negative.txt";
		String methodName = "events_get_concierge";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 400);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getConcierge} integration test")
	public void testGetConciergeWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getConcierge_optional.txt";
		String methodName = "events_get_concierge";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEvents} integration test")
	public void testGetEventsWithRequiredParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getEvents_mandatory.txt";
		String methodName = "events_get_events";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEvents} integration test")
	public void testGetEventsWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getEvents_negative.txt";
		String methodName = "events_get_events";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEvents} integration test")
	public void testGetEventsWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getEvents_optional.txt";
		String methodName = "events_get_events";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventsByID} integration test")
	public void testGetEventsByIDWithRequiredParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getEventsByID_mandatory.txt";
		String methodName = "events_get_event_by_id";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("visibility"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventsByID} integration test")
	public void testGetEventsByIDWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getEventsByID_negative.txt";
		String methodName = "events_get_event_by_id";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventsByID} integration test")
	public void testGetEventsByIDWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getEventsByID_optional.txt";
		String methodName = "events_get_event_by_id";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("visibility"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventComments} integration test")
	public void testGetEventCommentsWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventsComments_mandatory.txt";
		String methodName = "events_get_event_comments";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventComments} integration test")
	public void testGetEventCommentsWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventsComments_negative.txt";
		String methodName = "events_get_event_comments";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventComments} integration test")
	public void testGetEventCommentsWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventsComments_optional.txt";
		String methodName = "events_get_event_comments";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventCommentsByID} integration test")
	public void testGetEventCommentsByIDWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventCommentByID_mandatory.txt";
		String methodName = "events_get_event_comment_by_id";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("time"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventCommentsByID} integration test")
	public void testGetEventCommentsByIDWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventCommentByID_negative.txt";
		String methodName = "events_get_event_comment_by_id";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventCommentsByID} integration test")
	public void testGetEventCommentsByIDWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventCommentByID_optional.txt";
		String methodName = "events_get_event_comment_by_id";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("time"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventCommentLikes} integration test")
	public void testGetEventCommentLikesWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventsCommentLikes_mandatory.txt";
		String methodName = "events_get_event_comment_likes";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			//System.out.println(jsonObject);
			Assert.assertTrue(jsonObject.has("results"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventCommentLikes} integration test")
	public void testGetEventCommentLikesWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventsCommentLikes_negative.txt";
		String methodName = "events_get_event_comment_likes";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventRating} integration test")
	public void testGetEventRatingWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventRating_mandatory.txt";
		String methodName = "events_get_event_rating";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventRating} integration test")
	public void testGetEventRatingWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_getEventRating_negative.txt";
		String methodName = "events_get_event_rating";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventRating} integration test")
	public void testGetEventRatingWithOptionalParameters() throws Exception {

		//System.out.println("*************0");
		String jsonRequestFilePath = pathToRequestsDirectory + "events_getEventRating_optional.txt";
		String methodName = "events_get_event_rating";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			System.out.println("*************1");
			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			System.out.println("*************2");

			Assert.assertTrue(jsonObject.has("results"));
			System.out.println("*************3");

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventComments} integration test")
	public void testPostEventCommentsWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventsComments_mandatory.txt";
		String methodName = "events_post_event_comments";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 201);
			System.out.println("responseHeader-" + responseHeader);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventComments} integration test")
	public void testPostEventCommentsWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventsComments_negative.txt";
		String methodName = "events_post_event_comments";

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
	      description = "meetup {postEventComments} integration test")
	public void testPostEventCommentsWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventsComments_optional.txt";
		String methodName = "events_post_event_comments";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 201);
			System.out.println("responseHeader-" + responseHeader);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {deleteEventCommentsByID} integration test")
	public void testDeleteEventCommentsByIDWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_deleteEventCommentsByID_mandatory.txt";
		String methodName = "events_delete_event_comments_by_id";

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

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {deleteEventCommentsByID} integration test")
	public void testDeleteEventCommentsByIDWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_deleteEventCommentsByID_negative.txt";
		String methodName = "events_delete_event_comments_by_id";

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
	      description = "meetup {postEvents} integration test")
	public void testPostEventsWithRequiredParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_postEvents_mandatory.txt";
		String methodName = "events_post_events";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 201);
			System.out.println("responseHeader-" + responseHeader);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEvents} integration test")
	public void testPostEventsWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_postEvents_negative.txt";
		String methodName = "events_post_events";

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
			System.out.println("responseHeader-" + responseHeader);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEvents} integration test")
	public void testPostEventsWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_postEvents_optional.txt";
		String methodName = "events_post_events";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		System.out.println("modifiedJsonString-" + modifiedJsonString);

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			System.out.println("jsonObject-" + jsonObject);

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 201);
			System.out.println("responseHeader-" + responseHeader);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventsByID} integration test")
	public void testPostEventsByIDWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_postEventsById_negative.txt";
		String methodName = "events_post_events_by_id";

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
			System.out.println("responseHeader-" + responseHeader);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventsByID} integration test")
	public void testPostEventsByIDWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "events_postEventsById_optional.txt";
		String methodName = "events_post_events_by_id";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			System.out.println("jsonObject-" + jsonObject);
			Assert.assertTrue(jsonObject.has("status"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {deleteEvents} integration test")
	public void testDeleteEventWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_deleteEventByID_mandatory.txt";
		String methodName = "events_delete_event_by_id";

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
			System.out.println("responseHeader-" + responseHeader);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {deleteEventsByID} integration test")
	public void testDeleteEventWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_deleteEventByID_negative.txt";
		String methodName = "events_delete_event_by_id";

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
			System.out.println("responseHeader-" + responseHeader);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventCommentsFlag} integration test")
	public void testPostEventCommentsFlagWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventsCommentsFlag_mandatory.txt";
		String methodName = "events_post_event_comments_flag";

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
			Assert.assertTrue(responseHeader == 401);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventCommentsFlag} integration test")
	public void testPostEventCommentsFlagWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventsCommentsFlag_negative.txt";
		String methodName = "events_post_event_comments_flag";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventCommentsFlag} integration test")
	public void testPostEventCommentsFlagWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventsCommentsFlag_optional.txt";
		String methodName = "events_post_event_comments_flag";

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
			Assert.assertTrue(responseHeader == 401);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventCommentSubscribe} integration test")
	public void testPostEventCommentSubscribeWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventsCommentSubscribe_mandatory.txt";
		String methodName = "events_post_event_comment_subscribe";

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
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventCommentSubscribe} integration test")
	public void testPostEventCommentSubscribeWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventsCommentSubscribe_negative.txt";
		String methodName = "events_post_event_comment_subscribe";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {deleteEventCommentSubscribe} integration test")
	public void testDeleteEventCommentSubscribeWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_deleteEventsCommentSubscribe_mandatory.txt";
		String methodName = "events_delete_event_comment_subscribe";

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
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {deleteEventCommentSubscribe} integration test")
	public void testDeleteEventCommentSubscribeWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_deleteEventsCommentSubscribe_negative.txt";
		String methodName = "events_delete_event_comment_subscribe";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventCommentLike} integration test")
	public void testPostEventCommentLikeWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventsCommentLike_mandatory.txt";
		String methodName = "events_post_event_comment_like";

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
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventCommentLike} integration test")
	public void testPostEventCommentLikeWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventsCommentLike_negative.txt";
		String methodName = "events_post_event_comment_like";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {deleteEventCommentLike} integration test")
	public void testDeleteEventCommentLikeWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_deleteEventsCommentLike_mandatory.txt";
		String methodName = "events_delete_event_comment_like";

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
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {deleteEventCommentLike} integration test")
	public void testDeleteEventCommentLikeWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_deleteEventsCommentLike_negative.txt";
		String methodName = "events_delete_event_comment_like";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventRating} integration test")
	public void testPostEventRatingWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventRating_mandatory.txt";
		String methodName = "events_post_event_rating";

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
			Assert.assertTrue(responseHeader == 400);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventRating} integration test")
	public void testPostEventRatingWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventRating_negative.txt";
		String methodName = "events_post_event_rating";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postRating} integration test")
	public void testPostEventRatingWithOptionalParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventRating_optional.txt";
		String methodName = "events_post_event_rating";

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
			Assert.assertTrue(responseHeader == 400);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventAttendance} integration test")
	public void testGetEventAttendanceWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventsAttendance_mandatory.txt";
		String methodName = "events_get_event_attendance";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONArray jsonObjectArray = ConnectorIntegrationUtil
					.sendRequestJSONArray(getProxyServiceURL(methodName), modifiedJsonString);
			JSONObject jsonObject = (JSONObject) jsonObjectArray.get(0);
			System.out.println(jsonObject);
			Assert.assertTrue(jsonObject.has("status"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventAttendance} integration test")
	public void testGetEventAttendanceWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventsAttendance_negative.txt";
		String methodName = "events_get_event_attendance";

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

	@Test(groups = { "wso2.esb" }, description = "meetup {getEventAttendance} integration test")
	public void testGetEventAttendanceWithOptionalParameters() throws Exception {

		//System.out.println("*************0");
		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_getEventsAttendance_optional.txt";
		String methodName = "events_get_event_attendance";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONArray jsonObjectArray = ConnectorIntegrationUtil
					.sendRequestJSONArray(getProxyServiceURL(methodName), modifiedJsonString);
			JSONObject jsonObject = (JSONObject) jsonObjectArray.get(0);
			System.out.println(jsonObject);
			Assert.assertTrue(jsonObject.has("status"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventAttendance} integration test")
	public void testPostEventAttendanceWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventAttendance_mandatory.txt";
		String methodName = "events_post_event_attendance";

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
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventAttendance} integration test")
	public void testPostAttendanceWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventAttendance_negative.txt";
		String methodName = "events_post_event_attendance";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventAttendance} integration test")
	public void testPostEventAttendanceWithOptionalParameters() throws Exception {

		//System.out.println("*************0");
		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventAttendance_optional.txt";
		String methodName = "events_post_event_attendance";

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
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventPayments} integration test")
	public void testPostEventPaymentsWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventPayments_mandatory.txt";
		String methodName = "events_post_event_payments";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          System.currentTimeMillis());
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			System.out.println("responseHeader-" + responseHeader);
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventPayments} integration test")
	public void testPostEventPaymentsWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventPayments_negative.txt";
		String methodName = "events_post_event_payments";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventPayments} integration test")
	public void testPostEventPaymentsWithOptionalParameters() throws Exception {

		//System.out.println("*************0");
		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventPayments_optional.txt";
		String methodName = "events_post_event_payments";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          System.currentTimeMillis());
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			System.out.println("jsonObject-" + jsonObject);
			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			System.out.println("responseHeader-" + responseHeader);
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventWatchlist} integration test")
	public void testPostEventWatchlistWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventWatchlist_mandatory.txt";
		String methodName = "events_post_event_watchlist";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          System.currentTimeMillis());
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			System.out.println("responseHeader-" + responseHeader);
			Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {postEventWatchlist} integration test")
	public void testPostWatchlistWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_postEventWatchlist_negative.txt";
		String methodName = "events_post_event_watchlist";

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

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {deleteEventWatchlist} integration test")
	public void testDeleteEventWatchlistWithRequiredParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_deleteEventWatchlist_mandatory.txt";
		String methodName = "events_delete_event_watchlist";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          System.currentTimeMillis());
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("status"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {deleteEventWatchlist} integration test")
	public void testDeleteWatchlistWithNegativeParameters() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "events_deleteEventWatchlist_negative.txt";
		String methodName = "events_delete_event_watchlist";

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

}
