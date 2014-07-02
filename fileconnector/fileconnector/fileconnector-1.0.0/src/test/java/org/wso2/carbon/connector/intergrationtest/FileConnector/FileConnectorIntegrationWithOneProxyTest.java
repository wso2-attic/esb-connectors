/**
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.intergrationtest.FileConnector;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

/**
 * Integration test class for file connector
 */
public class FileConnectorIntegrationWithOneProxyTest extends ConnectorIntegrationTestBase {

	private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

	private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

	private Map<String, String> parametersMap = new HashMap<String, String>();

	/**
	 * Set up the environment.
	 */
	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {

		init("fileconnector");

		esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
		esbRequestHeadersMap.put("Content-Type", "application/xml");

	}

	/**
	 * Positive test case for create file method with mandatory parameters.
	 */
	@Test(groups = { "wso2.esb" }, description = "Fileconnector create file intergration test")
	public void testCreateFile() throws Exception {

		esbRequestHeadersMap.put("Action", "urn:create");
		RestResponse<OMElement> esbRestResponse =
		                                          sendXmlRestRequest(proxyUrl, "POST",
		                                                             esbRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		Assert.assertEquals(true, esbRestResponse.getBody().toString().contains("true"));

	}

	/**
	 * Positive test case for create file method with mandatory parameters.
	 */
	@Test(groups = { "wso2.esb" }, description = "Fileconnector append file intergration test")
	public void testAppendFile() throws Exception {

		esbRequestHeadersMap.put("Action", "urn:append");
		RestResponse<OMElement> esbRestResponse =
		                                          sendXmlRestRequest(proxyUrl, "POST",
		                                                             esbRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		Assert.assertEquals(true, esbRestResponse.getBody().toString().contains("true"));

	}

	/**
	 * Positive test case for rename file method with mandatory parameters.
	 */
	@Test(groups = { "wso2.esb" }, description = "Fileconnector rename file intergration test", dependsOnMethods = { "testAppendFile" })
	public void testRenameFile() throws Exception {

		esbRequestHeadersMap.put("Action", "urn:rename");
		RestResponse<OMElement> esbRestResponse =
		                                          sendXmlRestRequest(proxyUrl, "POST",
		                                                             esbRequestHeadersMap);

		
		Assert.assertEquals(true, esbRestResponse.getBody().toString().contains("true"));

	}

	/**
	 * Positive test case for delete file method with mandatory parameters.
	 */
	@Test(groups = { "wso2.esb" }, description = "Fileconnector delete file intergration test", dependsOnMethods = { "testRenameFile" })
	public void testDeleteFile() throws Exception {

		esbRequestHeadersMap.put("Action", "urn:delete");
		RestResponse<OMElement> esbRestResponse =
		                                          sendXmlRestRequest(proxyUrl, "POST",
		                                                             esbRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		Assert.assertEquals(true, esbRestResponse.getBody().toString().contains("true"));

	}

	/**
	 * Positive test case for copy file method with mandatory parameters.
	 */
	@Test(groups = { "wso2.esb" }, description = "Fileconnector copy file intergration test")
	public void testCopyFile() throws Exception {

		esbRequestHeadersMap.put("Action", "urn:copy");
		RestResponse<OMElement> esbRestResponse =
		                                          sendXmlRestRequest(proxyUrl, "POST",
		                                                             esbRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		Assert.assertEquals(true, esbRestResponse.getBody().toString().contains("true"));

	}

	/**
	 * Positive test case for copy large file method with mandatory parameters.
	 */
	@Test(groups = { "wso2.esb" }, description = "Fileconnector copy large file intergration test")
	public void testCopyLargeFile() throws Exception {

		esbRequestHeadersMap.put("Action", "urn:copylarge");
		RestResponse<OMElement> esbRestResponse =
		                                          sendXmlRestRequest(proxyUrl, "POST",
		                                                             esbRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		Assert.assertEquals(true, esbRestResponse.getBody().toString().contains("true"));

	}

	/**
	 * Positive test case for read file methodwith mandatory parameters.
	 */
	@Test(groups = { "wso2.esb" }, description = "Fileconnector read file intergration test",dependsOnMethods = { "testCreateFile" })
	public void testReadFile() throws Exception {

		esbRequestHeadersMap.put("Action", "urn:read");
		RestResponse<OMElement> esbRestResponse =
		                                          sendXmlRestRequest(proxyUrl, "POST",
		                                                             esbRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		Assert.assertEquals(false, esbRestResponse.getBody().toString().isEmpty());

	}

	/**
	 * Positive test case for archives file method with mandatory parameters.
	 */
	@Test(groups = { "wso2.esb" }, description = "Fileconnector archive file intergration test")
	public void testArchiveFile() throws Exception {

		esbRequestHeadersMap.put("Action", "urn:archive");
		RestResponse<OMElement> esbRestResponse =
		                                          sendXmlRestRequest(proxyUrl, "POST",
		                                                             esbRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		Assert.assertEquals(true, esbRestResponse.getBody().toString().contains("true"));
	}

	/**
	 * Positive test case for isfileexits method with mandatory parameters.
	 */
	@Test(groups = { "wso2.esb" }, description = "Fileconnector ifileexist file intergration test")
	public void testIsFileFile() throws Exception {

		esbRequestHeadersMap.put("Action", "urn:isfileexist");
		RestResponse<OMElement> esbRestResponse =
		                                          sendXmlRestRequest(proxyUrl, "POST",
		                                                             esbRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		Assert.assertEquals(true, esbRestResponse.getBody().toString().contains("true"));
	}

	/**
	 * Positive test case for move method with mandatory parameters.
	 */
	@Test(groups = { "wso2.esb" }, description = "Fileconnector move file intergration test", dependsOnMethods = { "testArchiveFile" })
	public void testMoveFile() throws Exception {

		esbRequestHeadersMap.put("Action", "urn:move");
		RestResponse<OMElement> esbRestResponse =
		                                          sendXmlRestRequest(proxyUrl, "POST",
		                                                             esbRequestHeadersMap);

		System.out.println("Responce::::::::::" + proxyUrl + esbRestResponse.getBody());
		Assert.assertEquals(true, esbRestResponse.getBody().toString().contains("true"));
	}
}
