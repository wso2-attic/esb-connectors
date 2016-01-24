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

package org.wso2.carbon.connector.integration.test.meetup.everywhere.community;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

/**
 */
public class EwcommunityIntegrationTest extends MeetupConnectorIntegrationTest {

	//**************************************       EVERYWHERE-COMMUNITY      *********************************************************//

	//    //.................   mantatory parameters test for get everywhere community  .....................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getcommunity} integration test")
	public void testgetcommunityMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getcommunity_mandatory.txt";
		String methodName = "meetup_getcommunity";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties
				                                          .getProperty("community_id"),
		                                          meetupConnectorProperties
				                                          .getProperty("community_urlname"),
		                                          meetupConnectorProperties
				                                          .getProperty("container_id"),
		                                          meetupConnectorProperties.getProperty("country"),
		                                          meetupConnectorProperties.getProperty("city"),
		                                          meetupConnectorProperties.getProperty("state"),
		                                          meetupConnectorProperties.getProperty("fields"),
		                                          meetupConnectorProperties.getProperty("lat"),
		                                          meetupConnectorProperties.getProperty("lon"),
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
			// Assert.assertTrue(jsonObject.has("result"));
			System.out.println("--------------@@@@@@@@---------");

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  getcommunity ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getcommunity}  integration test for negative scenario.")
	public void testgetcommunityNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getcommunity_negative.txt";
		String methodName = "meetup_getcommunity";

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
			Assert.assertTrue(
					(responseHeader == 401) || (responseHeader == 400) || (responseHeader == 404));

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //...............   mantatory parameters test for find community by id ............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getcommunity_byid} integration test")
	public void testgetcommunity_byidMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getcommunity_byid_mandatory.txt";
		String methodName = "meetup_getcommunity_byid";

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
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("zip"));
			System.out.println("--------------@@@@@@@@---------");

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//...............   optional parameters test for find community by id ........... .................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getcommunity_byid} integration test")
	public void testgetcommunity_byidoptional() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getcommunity_byid_optional.txt";
		String methodName = "meetup_getcommunity_byid";

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
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("zip"));
			System.out.println("--------------@@@@@@@@---------");

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //...............    negative test for find community by id ...........................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getcommunity_byid}  integration test for negative scenario.")
	public void testgetcommunity_byidNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getcommunity_byid_negative.txt";
		String methodName = "meetup_getcommunity_byid";

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

	//    //mantatory parameters test for find everywhere followers

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_followers} integration test")
	public void testgetew_followerssMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_followers_mandatory.txt";
		String methodName = "meetup_getew_followers";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties
				                                          .getProperty("community_id"),
		                                          meetupConnectorProperties
				                                          .getProperty("community_urlname"),
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

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  get every where followers ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew-followers}  integration test for negative scenario.")
	public void testgetew_followersNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_followers_negative.txt";
		String methodName = "meetup_getew_followers";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(
					(responseHeader == 401) || (responseHeader == 400) || (responseHeader == 404));

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //mantatory parameters test for get everywhere follows

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_follows} integration test")
	public void testgetew_followsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_follows_mandatory.txt";
		String methodName = "meetup_getew_follows";

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
			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("results"));
			System.out.println("--------------@@@@@@@@---------");

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................optional test case for  get every where follows ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_follows}  integration test for negative scenario.")
	public void testgetew_followsoptional() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_follows_optional.txt";
		String methodName = "meetup_getew_follows";

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
			Assert.assertTrue(responseHeader == 200);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("results"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  get every where follows ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_follows}  integration test for negative scenario.")
	public void testgetew_followsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_follows_negative.txt";
		String methodName = "meetup_getew_follows";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 401);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //mantatory parameters test for getew_follows_byid

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_follows_byid} integration test")
	public void testgetew_follows_byidMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_follows_byid_mandatory.txt";
		String methodName = "meetup_getew_follows_byid";

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
			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("id"));
			System.out.println("--------------@@@@@@@@---------");

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  getew_follows_byid ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_follows_byid}  integration test for negative scenario.")
	public void testgetew_follows_byidNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_follows_byid_negative.txt";
		String methodName = "meetup_getew_follows_byid";

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
			Assert.assertTrue(responseHeader == 404);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("problem"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    / ................................ mantatory parameters test for delete_ewcommunity  ......................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {delete_ewcommunity} integration test")
	public void testdelete_ewcommunityMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "delete_ewcommunity_mandatory.txt";
		String methodName = "meetup_delete_ewcommunity";

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

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  delete_ewcommunity .......  ........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {delete_ewcommunity}  integration test for negative scenario.")
	public void testdelete_ewcommunityNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "delete_ewcommunity_negative.txt";
		String methodName = "meetup_delete_ewcommunity";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue((responseHeader == 404));

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("problem"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    / ................................ mantatory parameters test for delete_ewfollow  ......................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {delete_ewfollow} integration test")
	public void testdelete_ewfollowMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "delete_ewfollow_mandatory.txt";
		String methodName = "meetup_delete_ewfollow";

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
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("problem"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  delete_ewfollow ....... ........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {delete_ewfollow}  integration test for negative scenario.")
	public void testdelete_ewfollowNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "delete_ewfollow_negative.txt";
		String methodName = "meetup_delete_ewfollow";

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
			Assert.assertTrue((responseHeader == 404));

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("problem"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//........................................ mandatory parameter for post_follow  .........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {post_ewfollow} integration test")
	public void testpost_followMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "post_ewfollow_mandatory.txt";
		String methodName = "meetup_post_ewfollow";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties
				                                          .getProperty("community_id")

		);
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
					                             modifiedJsonString);
			Assert.assertTrue(responseHeader == 201);
			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("id"));
			System.out.println("--------------@@@@@@@@---------");

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //...............    negative test for  post_follow  .............................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {post_ewfollow}  integration test for negative scenario.")
	public void testpost_followNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "post_ewfollow_negative.txt";
		String methodName = "meetup_post_ewfollow";

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
			Assert.assertTrue(jsonObject.has("problem"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//........................................ mandatory parameter for edit_ewcommunity  ...  every times data should be changed......................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {edit_ewcommunity} integration test")
	public void testedit_ewcommunityMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "edit_ewcommunity_mandatory.txt";
		String methodName = "meetup_edit_ewcommunity";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("id"),
		                                          meetupConnectorProperties.getProperty("name"),
		                                          meetupConnectorProperties.getProperty("country")

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

	//    //...............    negative test for  edit_ewcommunity  ................. to be checked.............................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {edit_ewcommunity}  integration test for negative scenario.")
	public void testedit_ewcommunityNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "edit_ewcommunity_negative.txt";
		String methodName = "meetup_edit_ewcommunity";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 404);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("problem"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//...............   mantatory parameters test for create_ewcommunity....... same community name cannot be added again.....................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {create_ewcommunity} integration test")
	public void testcreate_ewcommunityMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "create_ewcommunity_mandatory.txt";
		String methodName = "meetup_create_ewcommunity";

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
			Assert.assertTrue(responseHeader == 201);
			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(jsonObject.has("id"));
			System.out.println("--------------@@@@@@@@---------");

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //...............    negative test for create_ewcommunity...........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {create_ewcommunity}  integration test for negative scenario.")
	public void testcreate_ewcommunityNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "create_ewcommunity_negative.txt";
		String methodName = "meetup_create_ewcommunity";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(responseHeader == 400);

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("problem"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

}
