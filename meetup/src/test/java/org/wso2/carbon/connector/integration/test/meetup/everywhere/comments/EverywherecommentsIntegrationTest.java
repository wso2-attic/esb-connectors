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

package org.wso2.carbon.connector.integration.test.meetup.everywhere.comments;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

/**
 * Created by sriashalyasrivathsan on 10/16/14.
 */
public class EverywherecommentsIntegrationTest extends MeetupConnectorIntegrationTest {

	//**************************************       EVERYWHERE-COMMENTS      *********************************************************//

	//    //mantatory parameters test for get everywhere comments by id-------........................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getcomments-byid} integration test")
	public void testgetewcomments_byidMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getcomments-byid-mandatory.txt";
		String methodName = "meetup_getcomments-byid";

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
			Assert.assertTrue(jsonObject.has("id"));
			System.out.println("--------------@@@@@@@@---------");
			//System.out.println(jsonObject);

			// int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);

			// Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  get every where comments_byid ............test ..................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getcomments-byid}  integration test for negative scenario.")
	public void testgetewcomments_byidNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getcomments-byid_negative.txt";
		String methodName = "meetup_getcomments-byid";

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
			Assert.assertTrue(jsonObject.has("details"));//changed

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    //..................    mantatory parameters test for get everywhere comments   ...............................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew-comments} integration test")
	public void testgetew_commentsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_comments_mandatory.txt";
		String methodName = "meetup_getew_comments";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties
				                                          .getProperty("comment_id"),
		                                          meetupConnectorProperties
				                                          .getProperty("community_id"),
		                                          meetupConnectorProperties
				                                          .getProperty("community_urlname"),
		                                          meetupConnectorProperties
				                                          .getProperty("container_id"),
		                                          meetupConnectorProperties.getProperty("event_id"),
		                                          meetupConnectorProperties
				                                          .getProperty("event_status"),
		                                          meetupConnectorProperties.getProperty("fields"),
		                                          meetupConnectorProperties
				                                          .getProperty("member_id"),
		                                          meetupConnectorProperties
				                                          .getProperty("parent_comment_id"),
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
			Assert.assertTrue(jsonObject.has("results"));
			System.out.println("--------------@@@@@@@@---------");
			//System.out.println(jsonObject);

			// int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);

			// Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  getew-comments ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew-comments}  integration test for negative scenario.")
	public void testgetew_commentsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_comments_negative.txt";
		String methodName = "meetup_getew_comments";

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

	//    / ................................ mantatory parameters test for delete_ewcomments  ......................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {delete_ewcomments} integration test")
	public void testdelete_ewcommentsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "delete_ewcomments_mandatory.txt";
		String methodName = "meetup_delete_ewcomments";

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
			//JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
			//Assert.assertTrue(jsonObject.has("problem"));
			// System.out.println("--------------@@@@@@@@---------");
			//System.out.println(jsonObject);

			// int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);

			// Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  delete_ewcomments .......     ........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {delete_ewcomments}  integration test for negative scenario.")
	public void testdelete_ewcommentsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "delete_ewcomments_negative.txt";
		String methodName = "meetup_delete_ewcomments";

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
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    / ................................ mantatory parameters test for post_ewcomments  ......................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {post_ew_comments} integration test")
	public void testpost_ewcommentsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "post_ew_comments_mandatory.txt";
		String methodName = "meetup_post_ew_comments";

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
			// System.out.println("--------------@@@@@@@@---------");
			//System.out.println(jsonObject);

			// int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);

			// Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	//    / ................................ optional parameters test for post_ewcomments  .....  values should be given .................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {post_ew_comments} integration test")
	public void testpost_ewcommentsoptional() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "post_ew_comments_optional.txt";
		String methodName = "meetup_post_ew_comments";

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
			// System.out.println("--------------@@@@@@@@---------");
			//System.out.println(jsonObject);

			// int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);

			// Assert.assertTrue(responseHeader == 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  post_ewcomments .......     ........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {post_ew_comments}  integration test for negative scenario.")
	public void testpost_ewcommentsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "post_ew_comments_negative.txt";
		String methodName = "meetup_post_ew_comments";

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
			Assert.assertTrue((responseHeader == 400));

			JSONObject jsonObject = ConnectorIntegrationUtil
					.sendRequest(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue(jsonObject.has("problem"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

}
