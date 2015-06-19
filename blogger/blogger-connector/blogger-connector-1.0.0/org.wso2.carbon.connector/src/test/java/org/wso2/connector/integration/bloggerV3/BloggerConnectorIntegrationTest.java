/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.connector.integration.bloggerV3;

import java.util.Properties;

import org.apache.axis2.context.ConfigurationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.connector.integration.bloggerV3.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import javax.activation.DataHandler;

public class BloggerConnectorIntegrationTest extends ESBIntegrationTest {

    private static final String CONNECTOR_NAME = "blogger";

    private MediationLibraryUploaderStub mediationLibUploadStub = null;

    private MediationLibraryAdminServiceStub adminServiceStub = null;

    private ProxyServiceAdminClient proxyAdmin;

    private String repoLocation = null;

    private String bloggerConnectorFileName = CONNECTOR_NAME + "-connector-1.0.0.zip";

    private Properties bloggerConnectorProperties = null;

    private String pathToProxiesDirectory = null;

    private String pathToRequestsDirectory = null;

    // Variables for store results of dependent methods

    private String apiKey;

    private String accessToken;

    private String userID;

    private String blogID;

    private String postID;

    private String commentID;

    private String commentID2;

    private String bURL;

    private String squery;

    private String postpath;

    private String postID2;

    private long stime;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        ConfigurationContextProvider configurationContextProvider = ConfigurationContextProvider.getInstance();
        ConfigurationContext cc = configurationContextProvider.getConfigurationContext();

        mediationLibUploadStub =
                new MediationLibraryUploaderStub(cc, esbServer.getBackEndUrl() + "MediationLibraryUploader");
        AuthenticateStub.authenticateStub("admin", "admin", mediationLibUploadStub);

        adminServiceStub =
                new MediationLibraryAdminServiceStub(cc, esbServer.getBackEndUrl() + "MediationLibraryAdminService");

        AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            repoLocation = System.getProperty("connector_repo").replace("\\", "/");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }

        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());

        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, bloggerConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);

        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");

        bloggerConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

        pathToProxiesDirectory = repoLocation + bloggerConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + bloggerConnectorProperties.getProperty("requestDirectoryRelativePath");

        accessToken = bloggerConnectorProperties.getProperty("accessToken");
        apiKey = bloggerConnectorProperties.getProperty("apiKey");
        userID = bloggerConnectorProperties.getProperty("userID");
        blogID = bloggerConnectorProperties.getProperty("blogID");
        postID = bloggerConnectorProperties.getProperty("postID");
        commentID = bloggerConnectorProperties.getProperty("commentID");
        bURL = bloggerConnectorProperties.getProperty("blogURL");
        squery = bloggerConnectorProperties.getProperty("search_query");
        postpath = bloggerConnectorProperties.getProperty("post_path");

        stime = 10000;
    }

    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }

    /**
     * Positive test case for getBlog method with mandatory parameters - blogger blog
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "blogger {getBlog} integration test with mandatory parameters - blogID")
    public void getBlogWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getBlog_mandatory.txt";
        String methodName = "getBlog";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for getBlog method with optional parameters - blogger blog.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "blogger {getBlog} integration test with optional parameters")
    public void getBlogWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getBlog_optional.txt";
        String methodName = "getBlog";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            System.out.println(jsonObject.toString());
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for getBlog method.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "blogger {getBlog} integration test with negative case")
    public void getBlogWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getBlog_negative.txt";
        String methodName = "getBlog";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getByURL  method with mandatory parameters - blogger blog
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "blogger {getByURL} integration test with mandatory parameters - blogID")
    public void getByURLWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getByURL_mandatory.txt";
        String methodName = "getByURL";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, bURL);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for getByURL method with optional parameters - blogger blog.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "blogger {getByURL} integration test with optional parameters")
    public void getByURLWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getByURL_optional.txt";
        String methodName = "getByURL";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, bURL);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for getByURL method.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "blogger {getByURL} integration test with negative case")
    public void getByURLWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getByURL_negative.txt";
        String methodName = "getByURL";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for listByUser  method with mandatory parameters - blogger blog
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "blogger {listByUser} integration test with mandatory parameters - blogID")
    public void listByUserWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listByUser_mandatory.txt";
        String methodName = "listByUser";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, userID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.getString("kind").equals("blogger#blogList"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for listByUser method with optional parameters - blogger blog.
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "blogger {listByUser} integration test with optional parameters")
    public void listByUserWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listByUser_optional.txt";
        String methodName = "listByUser";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, userID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for listByUser method.
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "blogger {listByUser} integration test with negative case")
    public void listByUserWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listByUser_negative.txt";
        String methodName = "listByUser";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for listPosts  method with mandatory parameters - blogger post
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "blogger {listPosts} integration test with mandatory parameters - blogID")
    public void listPostsWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listPosts_mandatory.txt";
        String methodName = "listPosts";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.getString("kind").equals("blogger#postList"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for listPosts method with optional parameters - blogger post.
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "blogger {listPosts} integration test with optional parameters")
    public void listPostsWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listPosts_optional.txt";
        String methodName = "listPosts";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for listPosts method.
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "blogger {listPosts} integration test with negative case")
    public void listPostsWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listPosts_negative.txt";
        String methodName = "listPosts";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for getPost  method with mandatory parameters - blogger blog
     */
    @Test(priority = 6, groups = {"wso2.esb"}, description = "blogger {getPost} integration test with mandatory parameters")
    public void getPostWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getPost_mandatory.txt";
        String methodName = "getPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            System.out.println(jsonObject.toString());
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for getPost method with optional parameters - blogger blog.
     */
    @Test(priority = 6, groups = {"wso2.esb"}, description = "blogger {getPost} integration test with optional parameters")
    public void getPostWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getPost_optional.txt";
        String methodName = "getPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for getPost method.
     */
    @Test(priority = 6, groups = {"wso2.esb"}, description = "blogger {getPost} integration test with negative case")
    public void getPostWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getPost_negative.txt";
        String methodName = "getPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for searchPost  method with mandatory parameters - blogger post
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "blogger {searchPost} integration test with mandatory parameters - blogID")
    public void searchPostWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "searchPost_mandatory.txt";
        String methodName = "searchPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, squery);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            int stat = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonReqString);
            boolean assrt = (stat != 400);
            Assert.assertTrue(assrt);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for searchPost method with optional parameters - blogger post.
     */
    @Test(priority = 10, groups = {"wso2.esb"}, description = "blogger {searchPost} integration test with optional parameters")
    public void searchPostWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "searchPost_optional.txt";
        String methodName = "searchPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, squery);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            int stat = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonReqString);
            boolean assrt = (stat != 400);
            Assert.assertTrue(assrt);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for searchPost method.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "blogger {searchPost} integration test with negative case")
    public void searchPostWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "searchPost_negative.txt";
        String methodName = "searchPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            int stat = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonReqString);
            boolean assrt = (stat == 400 | stat == 500);
            Assert.assertTrue(assrt);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for insertPost  method with mandatory parameters - blogger post
     */
    @Test(priority = 8, groups = {"wso2.esb"}, description = "blogger {insertPost} integration test with mandatory parameters")
    public void insertPostWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "insertPost_mandatory.txt";
        String methodName = "insertPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for insertPost method with optional parameters - blogger post.
     */
    @Test(priority = 9, groups = {"wso2.esb"}, description = "blogger {insertPost} integration test with optional parameters")
    public void insertPostWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "insertPost_optional.txt";
        String methodName = "insertPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            System.out.println(jsonObject.toString());
            postID2 = jsonObject.getString("id");
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for insertPost method.
     */
    @Test(priority = 10, groups = {"wso2.esb"}, description = "blogger {insertPost} integration test with negative case")
    public void insertPostWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "insertPost_negative.txt";
        String methodName = "insertPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for getPostByPath  method with mandatory parameters - blogger post
     */
    @Test(priority = 7, groups = {"wso2.esb"}, description = "blogger {getPostByPath} integration test with mandatory parameters ")
    public void getPostByPathWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getPostByPath_mandatory.txt";
        String methodName = "getPostByPath";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postpath);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for getPostByPath method with optional parameters   - blogger post.
     */
    @Test(priority = 7, groups = {"wso2.esb"}, description = "blogger {getPostByPath} integration test with optional parameters")
    public void getPostByPathWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getPostByPath_optional.txt";
        String methodName = "getPostByPath";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postpath);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for getPostByPath method.
     */
    @Test(priority = 7, groups = {"wso2.esb"}, description = "blogger {getPostByPath} integration test with negative case")
    public void getPostByPathWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getPostByPath_negative.txt";
        String methodName = "getPostByPath";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for patchPost  method with mandatory parameters - blogger post
     */
    @Test(priority = 14, groups = {"wso2.esb"}, description = "blogger {patchPost} integration test with mandatory parameters ")
    public void patchPostWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "patchPost_mandatory.txt";
        String methodName = "patchPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {


            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for patchPost method with optional parameters - blogger post.
     */
    @Test(priority = 15, groups = {"wso2.esb"}, description = "blogger {patchPost} integration test with optional parameters")
    public void patchPostWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "patchPost_optional.txt";
        String methodName = "patchPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {


            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);

            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for patchPost method.
     */
    @Test(priority = 16, groups = {"wso2.esb"}, description = "blogger {patchPost} integration test with negative case")
    public void patchPostWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "patchPost_negative.txt";
        String methodName = "patchPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {


            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);

            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for updatePost  method with mandatory parameters - blogger post
     */
    @Test(priority = 11, groups = {"wso2.esb"}, description = "blogger {updatePost} integration test with mandatory parameters ")
    public void updatePostWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "updatePost_mandatory.txt";
        String methodName = "updatePost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for updatePost method with optional parameters - blogger post.
     */
    @Test(priority = 12, groups = {"wso2.esb"}, description = "blogger {updatePost} integration test with optional parameters")
    public void updatePostWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "updatePost_optional.txt";
        String methodName = "updatePost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for updatePost method.
     */
    @Test(priority = 13, groups = {"wso2.esb"}, description = "blogger {updatePost} integration test with negative case")
    public void updatePostWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "updatePost_negative.txt";
        String methodName = "updatePost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for revertPost  method with mandatory parameters - blogger post
     */
    @Test(priority = 17, groups = {"wso2.esb"}, description = "blogger {revertPost} integration test with mandatory parameters ")
    public void revertPostWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "revertPost_mandatory.txt";
        String methodName = "revertPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for revertPost method with optional parameters - blogger post.
     */
    @Test(priority = 18, groups = {"wso2.esb"}, description = "blogger {revertPost} integration test with optional parameters")
    public void revertPostWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "revertPost_optional.txt";
        String methodName = "revertPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for revertPost method.
     */
    @Test(priority = 19, groups = {"wso2.esb"}, description = "blogger {revertPost} integration test with negative case")
    public void revertPostWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "revertPost_negative.txt";
        String methodName = "revertPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for publishPost  method with mandatory parameters - blogger post
     */
    @Test(priority = 20, groups = {"wso2.esb"}, description = "blogger {publishPost} integration test with mandatory parameters ")
    public void publishPostWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "publishPost_mandatory.txt";
        String methodName = "publishPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID2);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for publishPost method with optional parameters - blogger post.
     */
    @Test(priority = 21, groups = {"wso2.esb"}, description = "blogger {publishPost} integration test with optional parameters")
    public void publishPostWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "publishPost_optional.txt";
        String methodName = "publishPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for publishPost method.
     */
    @Test(priority = 22, groups = {"wso2.esb"}, description = "blogger {publishPost} integration test with negative case")
    public void publishPostWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "publishPost_negative.txt";
        String methodName = "publishPost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for listComments  method with mandatory parameters - blogger comment
     */
    @Test(priority = 23, groups = {"wso2.esb"}, description = "blogger {listComments} integration test with mandatory parameters ")
    public void listCommentsWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listComments_mandatory.txt";
        String methodName = "listComments";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.getString("kind").equals("blogger#commentList"));

            JSONArray ja = jsonObject.getJSONArray("items");
            commentID2 = (ja.getJSONObject(ja.length() - 1).getString("id"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for listComments method with optional parameters - blogger comment.
     */
    @Test(priority = 23, groups = {"wso2.esb"}, description = "blogger {listComments} integration test with optional parameters")
    public void listCommentsWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listComments_optional.txt";
        String methodName = "listComments";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
            JSONArray ja = jsonObject.getJSONArray("items");
            commentID2 = (ja.getJSONObject(ja.length() - 1).getString("id"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for listComments method.
     */
    @Test(priority = 23, groups = {"wso2.esb"}, description = "blogger {listComments} integration test with negative case")
    public void listCommentsWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listComments_negative.txt";
        String methodName = "listComments";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for getComment  method with mandatory parameters - blogger comment
     */
    @Test(priority = 24, groups = {"wso2.esb"}, description = "blogger {getComment} integration test with mandatory parameters ")
    public void getCommentWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getComment_mandatory.txt";
        String methodName = "getComment";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID, commentID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for getComment method with optional parameters - blogger comment.
     */
    @Test(priority = 24, groups = {"wso2.esb"}, description = "blogger {getComment} integration test with optional parameters")
    public void getCommentWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getComment_optional.txt";
        String methodName = "getComment";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID, commentID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for getComment method.
     */
    @Test(priority = 24, groups = {"wso2.esb"}, description = "blogger {getComment} integration test with negative case")
    public void getCommentWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getComment_negative.txt";
        String methodName = "getComment";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * listCommentsByBlog
     * Positive test case for listCommentsByBlog  method with mandatory parameters - blogger comment
     */
    @Test(priority = 25, groups = {"wso2.esb"}, description = "blogger {listCommentsByBlog} integration test with mandatory parameters ")
    public void listCommentsByBlogWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsByBlog_mandatory.txt";
        String methodName = "listCommentsByBlog";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.getString("kind").equals("blogger#commentList"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for listCommentsByBlog method with optional parameters - blogger comment.
     */
    @Test(priority = 25, groups = {"wso2.esb"}, description = "blogger {listCommentsByBlog} integration test with optional parameters")
    public void listCommentsByBlogWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsByBlog_optional.txt";
        String methodName = "listCommentsByBlog";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for listCommentsByBlog method.
     */
    @Test(priority = 25, groups = {"wso2.esb"}, description = "blogger {listCommentsByBlog} integration test with negative case")
    public void listCommentsByBlogWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsByBlog_negative.txt";
        String methodName = "listCommentsByBlog";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for markCommentAsSpam  method with mandatory parameters - blogger comment
     */
    @Test(priority = 26, groups = {"wso2.esb"}, description = "blogger {markCommentAsSpam} integration test with mandatory parameters ")
    public void markCommentAsSpamWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "markCommentAsSpam_mandatory.txt";
        String methodName = "markCommentAsSpam";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID, commentID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for markCommentAsSpam method with optional parameters - blogger comment.
     */
    @Test(priority = 26, groups = {"wso2.esb"}, description = "blogger {markCommentAsSpam} integration test with optional parameters")
    public void markCommentAsSpamWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "markCommentAsSpam_optional.txt";
        String methodName = "markCommentAsSpam";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID, commentID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for markCommentAsSpam method.
     */
    @Test(priority = 26, groups = {"wso2.esb"}, description = "blogger {markCommentAsSpam} integration test with negative case")
    public void markCommentAsSpamWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "markCommentAsSpam_negative.txt";
        String methodName = "markCommentAsSpam";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for approveComment  method with mandatory parameters - blogger comment
     */
    @Test(priority = 27, groups = {"wso2.esb"}, description = "blogger {approveComment} integration test with mandatory parameters ")
    public void approveCommentWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "approveComment_mandatory.txt";
        String methodName = "approveComment";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID, commentID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for approveComment method with optional parameters - blogger comment.
     */
    @Test(priority = 27, groups = {"wso2.esb"}, description = "blogger {approveComment} integration test with optional parameters")
    public void approveCommentWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "approveComment_optional.txt";
        String methodName = "approveComment";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID, commentID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for approveComment method.
     */
    @Test(priority = 27, groups = {"wso2.esb"}, description = "blogger {approveComment} integration test with negative case")
    public void approveCommentWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "approveComment_negative.txt";
        String methodName = "approveComment";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for removeCommentContent  method with mandatory parameters - blogger comment
     */
    @Test(priority = 28, groups = {"wso2.esb"}, description = "blogger {removeCommentContent} integration test with mandatory parameters ")
    public void removeCommentContentWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "removeCommentContent_mandatory.txt";
        String methodName = "removeCommentContent";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID, commentID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for removeCommentContent method with optional parameters - blogger comment.
     */
    @Test(priority = 28, groups = {"wso2.esb"}, description = "blogger {removeCommentContent} integration test with optional parameters")
    public void removeCommentContentWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "removeCommentContent_optional.txt";
        String methodName = "removeCommentContent";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID, commentID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for removeCommentContent method.
     */
    @Test(priority = 28, groups = {"wso2.esb"}, description = "blogger {removeCommentContent} integration test with negative case")
    public void removeCommentContentWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "removeCommentContent_negative.txt";
        String methodName = "removeCommentContent";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for deleteComment  method with mandatory parameters - blogger comment
     */
    @Test(priority = 29, groups = {"wso2.esb"}, description = "blogger {deleteComment} integration test with mandatory parameters ")
    public void deleteCommentWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "deleteComment_mandatory.txt";
        String methodName = "deleteComment";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID, commentID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            int stat = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(stat == 204);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negative test case for deleteComment method.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "blogger {deleteComment} integration test with negative case")
    public void deleteCommentWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "deleteComment_negative.txt";
        String methodName = "deleteComment";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            int stat = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(stat == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for deletePost  method with mandatory parameters - blogger post
     */
    @Test(priority = 30, groups = {"wso2.esb"}, description = "blogger {deletePost} integration test with mandatory parameters ")
    public void deletePostWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "deletePost_mandatory.txt";
        String methodName = "deletePost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, blogID, postID2);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            long t = System.currentTimeMillis();
            while (System.currentTimeMillis() - t < stime) {
            }
            int stat = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonReqString);
            System.out.println(stat);
            Assert.assertTrue(stat == 204);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negative test case for deletePost method.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "blogger {deletePost} integration test with negative case")
    public void deletePostWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "deletePost_negative.txt";
        String methodName = "deletePost";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            long t = System.currentTimeMillis();
            while (System.currentTimeMillis() - t < stime) {
            }
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for getUser  method with mandatory parameters - blogger user
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "blogger {getUser} integration test with mandatory parameters ")
    public void getUserWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getUser_mandatory.txt";
        String methodName = "getUser";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, userID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for getUser method with optional parameters - blogger user.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "blogger {getUser} integration test with optional parameters")
    public void getUserWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getUser_optional.txt";
        String methodName = "getUser";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken, userID);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(!jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negative test case for getUser method.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "blogger {getUser} integration test with negative case")
    public void getUserWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getUser_negative.txt";
        String methodName = "getUser";

        final String jsonReqString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonReqString = String.format(jsonReqString, apiKey, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonReqString);
            Assert.assertTrue(jsonObject.has("error"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }
}
