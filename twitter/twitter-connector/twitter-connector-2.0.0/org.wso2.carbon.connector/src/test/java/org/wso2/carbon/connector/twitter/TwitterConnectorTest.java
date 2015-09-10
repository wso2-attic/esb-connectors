/*
Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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
package org.wso2.carbon.connector.twitter;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TwitterConnectorTest extends ConnectorIntegrationTestBase {
	private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

	private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

	/**
	 * Set up the environment.
	 */
	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {

		init("twitter-connector-2.0.0");

		esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
		esbRequestHeadersMap.put("Content-Type", "application/json");

		apiRequestHeadersMap.putAll(esbRequestHeadersMap);

		apiRequestHeadersMap.put("Content-Type", "application/json");
		apiRequestHeadersMap.put("Accept", "application/json");
	}

	/**
	 * Positive test case for getContactFields method with mandatory parameters.
	 */
	@Test(enabled = true, description = "twitter {getContactFields} integration test with mandatory parameters.")
	public void testGetContactFieldsWithMandatoryParameters() throws IOException, JSONException {

		String methodName = "getAccountSettings";
		String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/1.1/account/settings.json";
		RestResponse<JSONObject> esbRestResponse =
				sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountSettings.json");
		//RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
	}


}