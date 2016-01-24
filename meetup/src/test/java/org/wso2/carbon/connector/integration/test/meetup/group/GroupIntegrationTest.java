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

package org.wso2.carbon.connector.integration.test.meetup.group;

import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;

import javax.activation.DataHandler;
import java.net.URL;

/**
 */
public class GroupIntegrationTest extends MeetupConnectorIntegrationTest {

	//**************************************       GROUP       *********************************************************//

	//............................. optional parameters test for find meetup_groups  ..........  no mandatory..............................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {findgroups} integration test")
	public void testfindfroupsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "findfroup_optional.txt";
		String methodName = "meetup_findgroups";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key")
		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONArray jsonArray = ConnectorIntegrationUtil
					.sendRequestJSONArray(getProxyServiceURL(methodName), modifiedJsonString);
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//...................................negative test case for  findgroups ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {findgroups}  integration test for negative scenario.")
	public void testfindgroupsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "findgroups_negative.txt";
		String methodName = "meetup_findgroups";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 401);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	////........................... mantatory parameters test for get group comments ......................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {get_groupcomments} integration test")
	public void testGetcommentsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "get_groupcomments_mandatory.txt";
		String methodName = "meetup_get_groupcomments";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("group_id"),
		                                          meetupConnectorProperties.getProperty("topic"),
		                                          meetupConnectorProperties.getProperty("groupnum")
		                                          //  meetupConnectorProperties.getProperty("group_urlname")

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

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//...................................negative test case for  group comments ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {get_groupcomments}  integration test for negative scenario.")
	public void testgetcommentsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "get_groupcomments_negative.txt";
		String methodName = "meetup_get_groupcomments";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 400);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("details"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//................................... mantatory parameters test for find recommended-meetup_groups.........................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {find_recomended_group} integration test")
	public void testfind_recomended_groupMandatory() throws Exception {

		String jsonRequestFilePath =
				pathToRequestsDirectory + "find_recomended_group_mandatory.txt";
		String methodName = "meetup_find_recomended_group";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key")
		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONArray jsonArray = ConnectorIntegrationUtil
					.sendRequestJSONArray(getProxyServiceURL(methodName), modifiedJsonString);
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  recommended groups ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {find_recomended_group}  integration test for negative scenario.")
	public void testfind_recomended_groupNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "find_recomended_group_negative.txt";
		String methodName = "meetup_find_recomended_group";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 401);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //....................................   mantatory parameters test for find meetup_groups by url   ......................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {find_group_byurl} integration test")
	public void testfind_group_byurlMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "find_group_byurl-mandatory.txt";
		String methodName = "meetup_find_group_byurl";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("urlname")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);
			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("id"));
			System.out.println("--------------@@@@@@@@---------");

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  meetup_groups by url ..............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {find_group_byurl}  integration test for negative scenario.")
	public void testfind_group_byurlNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "find_group_byurl_negative.txt";
		String methodName = "meetup_find_group_byurl";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 404);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//......................... mantatory parameters test for find similar-meetup_groups..........................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {find_similargroup} integration test")
	public void testfind_similargroupMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "find_similargroup_mantatory.txt";
		String methodName = "meetup_find_similargroup";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("urlname")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);

			JSONArray jsonArray = ConnectorIntegrationUtil
					.sendRequestJSONArray(getProxyServiceURL(methodName), modifiedJsonString);
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  meetup_find_similargroup ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {find_similargroup}  integration test for negative scenario.")
	public void testfind_similargroupNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "find_similargroup_negative.txt";
		String methodName = "meetup_find_similargroup";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 401);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    / ................................ mantatory parameters test for fetch group information  ......................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {fetchgroups} integration test")
	public void testfetchgroupsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "fetchgroups_mandatory.txt";
		String methodName = "meetup_fetchgroups";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("group_id"),

		                                          meetupConnectorProperties
				                                          .getProperty("category_id"),
		                                          meetupConnectorProperties.getProperty("country"),
		                                          meetupConnectorProperties.getProperty("city"),
		                                          meetupConnectorProperties.getProperty("state"),
		                                          meetupConnectorProperties.getProperty("domain"),
		                                          meetupConnectorProperties
				                                          .getProperty("group_urlname"),
		                                          meetupConnectorProperties.getProperty("lat"),
		                                          meetupConnectorProperties.getProperty("lon"),
		                                          meetupConnectorProperties
				                                          .getProperty("member_id"),
		                                          meetupConnectorProperties
				                                          .getProperty("organizer_id"),
		                                          meetupConnectorProperties.getProperty("topic"),
		                                          meetupConnectorProperties.getProperty("groupnum"),
		                                          meetupConnectorProperties.getProperty("zip")

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

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  meetup_fetchgroups ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {fetchgroups}  integration test for negative scenario.")
	public void testfetchgroupsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "fetchgroups_negative.txt";
		String methodName = "meetup_fetchgroups";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue((responseHeader == 400));

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("details"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    / ................................ mantatory parameters test for delete group topics  ......................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {delete_group_topics} integration test")
	public void testdelete_group_topicsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "delete_group_topics_mandatory.txt";
		String methodName = "meetup_delete_group_topics";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("urlname"),

		                                          meetupConnectorProperties.getProperty("topic_id")

		);
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

	// ...................................negative test case for  delete_group_topics ...............................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {delete_group_topics}  integration test for negative scenario.")
	public void testdelete_group_topicsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "delete_group_topics_negative.txt";
		String methodName = "meetup_delete_group_topics";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue((responseHeader == 400));

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    / ................................ mantatory parameters test for add_group_topics  ......................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {add_group_topics} integration test")
	public void testadd_group_topicsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "add_group_topics_mandatory.txt";
		String methodName = "meetup_add_group_topics";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("urlname"),

		                                          meetupConnectorProperties.getProperty("topic_id")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);
			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("id"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  add_group_topics.......  ........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {add_group_topics}  integration test for negative scenario.")
	public void testadd_group_topicsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "add_group_topics_negative.txt";
		String methodName = "meetup_add_group_topics";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue((responseHeader == 400));

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    / ................................ mantatory parameters test for edit_groups  ..............

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {edit_groups} integration test")
	public void testedit_groupsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "edit_groups_mandatory.txt";
		String methodName = "meetup_edit_groups";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("urlname"),

		                                          meetupConnectorProperties
				                                          .getProperty("description")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 200);
			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			//Assert.assertTrue(jsonObject.has("membership_error"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  edit_groups.......  ........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {edit_groups}  integration test for negative scenario.")
	public void testedit_groupsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "edit_groups_negative.txt";
		String methodName = "meetup_edit_groups";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue((responseHeader == 401));

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

}
