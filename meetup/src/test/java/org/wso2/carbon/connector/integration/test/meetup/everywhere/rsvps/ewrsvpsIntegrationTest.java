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

package org.wso2.carbon.connector.integration.test.meetup.everywhere.rsvps;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.meetup.MeetupConnectorIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

/**
 */
public class ewrsvpsIntegrationTest extends MeetupConnectorIntegrationTest {

	//**************************************       EVERYWHERE-RSVPS      *********************************************************//

	//    //mantatory parameters test for find everywhere rsvps

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_rsvps} integration test")
	public void testgetew_rsvpsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_rsvps-mandatory.txt";
		String methodName = "meetup_getew_rsvps";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("event_id"),
		                                          meetupConnectorProperties.getProperty("member_id")

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

	//    //optional parameters test for find everywhere rsvps

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_rsvps} integration test")
	public void testgetew_rsvpsOptional() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_rsvps_optional.txt";
		String methodName = "meetup_getew_rsvps";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
		                                          meetupConnectorProperties.getProperty("key"),
		                                          meetupConnectorProperties.getProperty("event_id"),
		                                          meetupConnectorProperties.getProperty("member_id")

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

	// ...................................negative test case for  get every where rsvps ...............................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_rsvps}  integration test for negative scenario.")
	public void testgetew_rsvpsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_rsvps_negative.txt";
		String methodName = "meetup_getew_rsvps";

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
			Assert.assertTrue(jsonObject.has("details"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ................................ mantatory parameters test for delete_ewrsvps  ......................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {delete_ewrsvps} integration test")
	public void testdelete_ewrsvpsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "delete_ewrsvps_mandatory.txt";
		String methodName = "meetup_delete_ewrsvps";

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

	// ...................................negative test case for  delete_ewrsvps .......  ........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {delete_ewrsvps}  integration test for negative scenario.")
	public void testdelete_ewrsvpsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "delete_ewrsvps_negative.txt";
		String methodName = "meetup_delete_ewrsvps";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

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

	// ................................ mantatory parameters test for create_rsvps  ......................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {create_rsvps} integration test")
	public void testcreate_rsvpsMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "create_rsvps_mandatory.txt";
		String methodName = "meetup_create_rsvps";

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

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ................................ optional parameters test for create_rsvps  ......................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {create_rsvps} integration test")
	public void testcreate_rsvpsoptional() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "create_rsvps_optional.txt";
		String methodName = "meetup_create_rsvps";

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

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ...................................negative test case for  create_rsvps .......      ........................................

	@Test(enabled = false, groups = { "wso2.esb" },
	      description = "meetup {create_rsvps}  integration test for negative scenario.")
	public void testcreate_rsvpsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "create_rsvps_negative.txt";
		String methodName = "meetup_create_rsvps";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {

			int responseHeader = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
			Assert.assertTrue((responseHeader == 400));

			// JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
			// Assert.assertTrue(jsonObject.has("errors"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	// ................................ mantatory parameters test for getew_rsvps_byid  .... ..................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {create_rsvps} integration test")
	public void testgetew_rsvps_byidMandatory() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_rsvps_byid_mandatory.txt";
		String methodName = "meetup_getew_rsvps_byid";

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

	// ...................................negative test case for  getew_rsvps_byid .......     ........................................

	@Test(enabled = true, groups = { "wso2.esb" },
	      description = "meetup {getew_rsvps_byid}  integration test for negative scenario.")
	public void testgetew_rsvps_byidNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getew_rsvps_byid_negative.txt";
		String methodName = "meetup_getew_rsvps_byid";

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
