/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.jenkins;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.Base64;
import org.json.JSONException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class JenkinsConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private String apiEndpointUrl;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("jenkins-connector-1.0.0");
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/xml");

        // Create base64-encoded auth string using username and password
        final String authString =
                connectorProperties.getProperty("username") + ":" + connectorProperties.getProperty("password");
        final String base64AuthString = Base64.encode(authString.getBytes());

        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + ":" + connectorProperties.getProperty("port");
        connectorProperties.setProperty("jobName", System.currentTimeMillis() + connectorProperties
                .getProperty("jobName"));
        connectorProperties.setProperty("uploadJobName", System.currentTimeMillis() + connectorProperties
                .getProperty("uploadJobName"));

    }

    /**
     * Positive test case for createJob method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {createJob} integration test with mandatory parameters.")
    public void testCreateJobWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createJob");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createJob_mandatory.xml");

        final String apiEndpoint = apiEndpointUrl + "/job/" + connectorProperties.getProperty("jobName") + "/api/xml";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("jobDescription"),
                getValueByExpression("//freeStyleProject/description", apiRestResponse.getBody()));
        Assert.assertEquals(connectorProperties.getProperty("keepDependencies"),
                getValueByExpression("//freeStyleProject/keepDependencies", apiRestResponse.getBody()));
        Assert.assertEquals(connectorProperties.getProperty("jobName"),
                getValueByExpression("//freeStyleProject/name", apiRestResponse.getBody()));
    }

    /**
     * Method name: createJob 
     * Test scenario: Optional 
     * Reason to skip: No optional parameters to create a job.
     */

    /**
     * Negative test case for createJob method .
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {createJob} integration test with negative case.", dependsOnMethods = {"testCreateJobWithMandatoryParameters"})
    public void testCreateJobWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createJob");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createJob_negative.xml");

        final String apiEndpoint = apiEndpointUrl + "/createItem?name=" + connectorProperties.getProperty("jobName");
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createJob_negative.xml");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for createJobWithConfigFile method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {createJobWithConfigFile} integration test with mandatory parameters.")
    public void testCreateJobWithConfigFileMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createJobWithConfigFile");
        final String jobFileName = connectorProperties.getProperty("jobFileName");

        final String responseString =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&username="
                        + connectorProperties.getProperty("username") + "&password="
                        + connectorProperties.getProperty("password") + "&name="
                        + connectorProperties.getProperty("uploadJobName");

        RestResponse<OMElement> esbRestResponse =
                sendBinaryContentForXmlResponse(responseString, "POST", esbRequestHeadersMap, jobFileName);

        final String apiEndpoint =
                apiEndpointUrl + "/job/" + connectorProperties.getProperty("uploadJobName") + "/api/xml";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Method name: createJobWithConfigFile 
     * Test scenario: Optional 
     * Reason to skip: No optional parameters to create the job.
     */

    /**
     * Negative test case for createJobWithConfigFile method .
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {createJobWithConfigFile} integration test with negative case.", dependsOnMethods = {"testCreateJobWithConfigFileMandatoryParameters"})
    public void testCreateJobWithConfigurationFileWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createJobWithConfigFile");
        final String jobFileName = connectorProperties.getProperty("jobFileName");

        final String responseString =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&username="
                        + connectorProperties.getProperty("username") + "&password="
                        + connectorProperties.getProperty("password") + "&name="
                        + connectorProperties.getProperty("uploadJobName");

        RestResponse<OMElement> esbRestResponse =
                sendBinaryContentForXmlResponse(responseString, "POST", esbRequestHeadersMap, jobFileName);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for updateJobConfiguration method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {updateJobConfiguration} integration test with mandatory parameters.", dependsOnMethods = {"testCreateJobWithMandatoryParameters"})
    public void testUpdateJobWithMandatoryParameters() throws Exception {

        final String apiEndpoint = apiEndpointUrl + "/job/" + connectorProperties.getProperty("jobName") + "/api/xml";
        RestResponse<OMElement> apiRestResponseBefore = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        esbRequestHeadersMap.put("Action", "urn:updateJobConfiguration");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateJobConfiguration_mandatory.xml");

        RestResponse<OMElement> apiRestResponseAfter = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotEquals(getValueByExpression("//freeStyleProject/description", apiRestResponseBefore.getBody()),
                getValueByExpression("//freeStyleProject/description", apiRestResponseAfter.getBody()));
        Assert.assertNotEquals(
                getValueByExpression("//freeStyleProject/keepDependencies", apiRestResponseBefore.getBody()),
                getValueByExpression("//freeStyleProject/keepDependencies", apiRestResponseAfter.getBody()));

        Assert.assertEquals(connectorProperties.getProperty("updatedJobDescription"),
                getValueByExpression("//freeStyleProject/description", apiRestResponseAfter.getBody()));
        Assert.assertEquals(connectorProperties.getProperty("updatedKeepDependencies"),
                getValueByExpression("//freeStyleProject/keepDependencies", apiRestResponseAfter.getBody()));

    }

    /**
     * Method name: updateJobConfiguration 
     * Test scenario: Optional 
     * Reason to skip: No optional parameters to be updated.
     */

    /**
     * Negative test case for updateJobConfiguration method.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {updateJobConfiguration} integration test with negative case.", dependsOnMethods = {"testCreateJobWithMandatoryParameters"})
    public void testUpdateJobWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:updateJobConfiguration");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateJobConfiguration_negative.xml");

        final String apiEndpoint = apiEndpointUrl + "/job/invalid/config.xml";
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_updateJobConfiguration_negative.xml");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

    }

    /**
     * Positive test case for updateJobConfigurationWithConfigFile method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {updateJobConfigurationWithConfigFile} integration test with mandatory parameters.", dependsOnMethods = {"testCreateJobWithConfigFileMandatoryParameters"})
    public void testUpdatedJobConfigurationWithConfigFileMandatoryParameters() throws Exception {

        final String apiEndpoint =
                apiEndpointUrl + "/job/" + connectorProperties.getProperty("uploadJobName") + "/api/xml";
        RestResponse<OMElement> apiRestResponseBefore = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        esbRequestHeadersMap.put("Action", "urn:updateJobConfigurationWithConfigFile");
        final String jobFileName = connectorProperties.getProperty("updatedJobFileName");

        final String responseString =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&username="
                        + connectorProperties.getProperty("username") + "&password="
                        + connectorProperties.getProperty("password") + "&jobName="
                        + connectorProperties.getProperty("uploadJobName");

        RestResponse<OMElement> esbRestResponse =
                sendBinaryContentForXmlResponse(responseString, "POST", esbRequestHeadersMap, jobFileName);

        RestResponse<OMElement> apiRestResponseAfter = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotEquals(apiRestResponseBefore.getBody(), apiRestResponseAfter.getBody());
    }

    /**
     * Method name: updateJobConfigurationWithConfigFile 
     * Test scenario: Optional 
     * Reason to skip: No optional parameters to be updated.
     */

    /**
     * Negative test case for updateJobConfigurationWithConfigFile method .
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {updateJobConfigurationWithConfigFile} integration test with negative case.", dependsOnMethods = {"testUpdatedJobConfigurationWithConfigFileMandatoryParameters"})
    public void testUpdatedJobConfigurationWithConfigurationFileWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:updateJobConfigurationWithConfigFile");
        final String jobFileName = connectorProperties.getProperty("updatedJobFileName");

        final String responseString =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&username="
                        + connectorProperties.getProperty("username") + "&password="
                        + connectorProperties.getProperty("password") + "&jobName=invalid";

        RestResponse<OMElement> esbRestResponse =
                sendBinaryContentForXmlResponse(responseString, "POST", esbRequestHeadersMap, jobFileName);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getJobConfiguration method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {getJobConfiguration} integration test with mandatory parameters.", dependsOnMethods = {"testCreateJobWithMandatoryParameters"})
    public void testGetJobConfigurationWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getJobConfiguration");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getJobConfiguration_mandatory.xml");

        final String apiEndpoint =
                apiEndpointUrl + "/job/" + connectorProperties.getProperty("jobName") + "/config.xml";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//project/description", esbRestResponse.getBody()),
                getValueByExpression("//project/description", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project/builders", esbRestResponse.getBody()),
                getValueByExpression("//project/builders", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project/keepDependencies", esbRestResponse.getBody()),
                getValueByExpression("//project/keepDependencies", apiRestResponse.getBody()));
    }

    /**
     * Method name: getJobConfiguration 
     * Test scenario: Optional 
     * Reason to skip: No optional parameters to be retrieved.
     */

    /**
     * Negative test case for getJobConfiguration method .
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {getJobConfiguration} integration test with negative case.")
    public void testGetJobConfigurationWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getJobConfiguration");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getJobConfiguration_negative.xml");

        final String apiEndpoint = apiEndpointUrl + "/job/invalid/config.xml";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for listJobs method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {listJobs} integration test with mandatory parameters.", dependsOnMethods = {"testCreateJobWithMandatoryParameters"})
    public void testListJobsWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listJobs");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listJobs_mandatory.xml");

        final String apiEndpoint = apiEndpointUrl + "/api/xml";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//hudson/mode", esbRestResponse.getBody()),
                getValueByExpression("//hudson/mode", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//hudson/numExecutors", esbRestResponse.getBody()),
                getValueByExpression("//hudson/numExecutors", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//hudson/quietingDown", esbRestResponse.getBody()),
                getValueByExpression("//hudson/quietingDown", apiRestResponse.getBody()));
    }

    /**
     * Method name: listJobs 
     * Test scenario: Optional 
     * Reason to skip: No optional parameters to be retrieved.
     */

    /**
     * Method name: listJobs 
     * Test scenario: Negative 
     * Reason to skip: No negative parameters to fail the list jobs method.
     */

    /**
     * Positive test case for getJob method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {getJob} integration test with mandatory parameters.", dependsOnMethods = {"testCreateJobWithMandatoryParameters"})
    public void testGetJobWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getJob");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getJob_mandatory.xml");

        final String apiEndpoint = apiEndpointUrl + "/job/" + connectorProperties.getProperty("jobName") + "/api/xml";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//project/displayName", esbRestResponse.getBody()),
                getValueByExpression("//project/displayName", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project/description", esbRestResponse.getBody()),
                getValueByExpression("//project/description", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project/name", esbRestResponse.getBody()),
                getValueByExpression("//project/name", apiRestResponse.getBody()));
    }

    /**
     * Method name: getJob 
     * Test scenario: Optional 
     * Reason to skip: No optional parameters to retrieve jobs.
     */

    /**
     * Negative test case for getJob method.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {getJob} integration test with negative case.", dependsOnMethods = {"testCreateJobWithMandatoryParameters"})
    public void testGetJobWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getJob");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getJob_negative.xml");

        final String apiEndpoint = apiEndpointUrl + "/job/invalid/api/xml";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

    }

    /**
     * Positive test case for createBuild method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {createBuild} integration test with mandatory parameters.", dependsOnMethods = {"testCreateJobWithMandatoryParameters"})
    public void testCreateBuildWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createBuild");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBuild_mandatory.xml");

        final String apiEndpoint = apiEndpointUrl + "/job/" + connectorProperties.getProperty("jobName") + "/build";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 201);

    }

    /**
     * Positive test case for createBuild method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {createBuild} integration test with optional parameters.", dependsOnMethods = {"testCreateJobWithConfigFileMandatoryParameters"})
    public void testCreateBuildWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createBuild");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBuild_optional.xml");

        final String apiEndpoint =
                apiEndpointUrl + "/job/" + connectorProperties.getProperty("uploadJobName") + "/buildWithParameters?"
                        + connectorProperties.getProperty("buildParameter") + "="
                        + connectorProperties.getProperty("buildParameterValue");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 201);

    }

    /**
     * Negative test case for createBuild method.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {createBuild} integration test with negative case.")
    public void testCreateBuildWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createBuild");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBuild_negative.xml");

        final String apiEndpoint = apiEndpointUrl + "/job/invalid/build";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

    }

    /**
     * Positive test case for getBuildDetails method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {getBuildDetails} integration test with mandatory parameters.")
    public void testGetBuildDetailsWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBuildDetails");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBuildDetails_mandatory.xml");

        final String apiEndpoint =
                apiEndpointUrl + "/job/" + connectorProperties.getProperty("buildJobName") + "/" + connectorProperties.getProperty("buildValue") + "/api/xml";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//project/displayName", esbRestResponse.getBody()),
                getValueByExpression("//project/displayName", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project/duration", esbRestResponse.getBody()),
                getValueByExpression("//project/duration", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project/keepLog", esbRestResponse.getBody()),
                getValueByExpression("//project/keepLog", apiRestResponse.getBody()));
    }

    /**
     * Positive test case for getBuildDetails method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {getBuildDetails} integration test with optional parameters.")
    public void testGetBuildDetailsWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBuildDetails");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBuildDetails_optional.xml");

        final String apiEndpoint =
                apiEndpointUrl + "/job/" + connectorProperties.getProperty("buildJobName") + "/"
                        + connectorProperties.getProperty("buildValue") + "/api/xml?tree"
                        + connectorProperties.getProperty("tree");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//project/displayName", esbRestResponse.getBody()),
                getValueByExpression("//project/displayName", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project/duration", esbRestResponse.getBody()),
                getValueByExpression("//project/duration", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//project/keepLog", esbRestResponse.getBody()),
                getValueByExpression("//project/keepLog", apiRestResponse.getBody()));
    }

    /**
     * Negative test case for getBuildDetails method.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "jenkins {getBuildDetails}  integration test with negative case.")
    public void testGetBuildDetailsWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBuildDetails");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBuildDetails_negative.xml");

        final String apiEndpoint =
                apiEndpointUrl + "/job/invalid/" + connectorProperties.getProperty("buildValue") + "/api/xml";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

    }
}
