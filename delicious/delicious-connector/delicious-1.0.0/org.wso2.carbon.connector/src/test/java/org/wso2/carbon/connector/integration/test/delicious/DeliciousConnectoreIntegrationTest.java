/**
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.delicious;

import org.apache.axiom.om.OMElement;
import org.apache.commons.codec.binary.Base64;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;

import javax.xml.namespace.QName;

public class DeliciousConnectoreIntegrationTest extends ConnectorIntegrationTestBase {

    protected static final String CONNECTOR_NAME = "delicious-connector-1.0.0";
    private String validAuthorization;
    private String invalidAuthorization;

    @BeforeTest(alwaysRun = true)
    protected void init() throws Exception {
        super.init(CONNECTOR_NAME);

        String concatString = connectorProperties.getProperty("username") + ":" + connectorProperties.getProperty("password");
        validAuthorization = new String(Base64.encodeBase64(concatString.getBytes()));

        String invalidConcatString = connectorProperties.getProperty("invalidusername") + ":" + connectorProperties.getProperty("invalidpassword");
        invalidAuthorization = new String(Base64.encodeBase64(invalidConcatString.getBytes()));
    }


    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }

    private String addCredentials(String jsonString) {
        return String.format(
                jsonString,
                connectorProperties.getProperty("username"),
                connectorProperties.getProperty("password"),
                connectorProperties.getProperty("Apiurl"));
    }

    /**
     * Oauth Positive test case for postGetAll method with mandatory parameters.
     */
    @Test(priority = 1,enabled=false, groups = {"wso2.esb"}, description = "delicious {postGetAll} integration test with mandatory parameters")
    public void testOauthDeliciouspostGetAllWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "postGetAllwithOauth.txt";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("clientId",connectorProperties.getProperty("client_id"));
        rawString = rawString.replace("clientSecret",connectorProperties.getProperty("client_secret"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/all", "", validAuthorization);

        Assert.assertTrue(omElementC.getFirstElement().toString().equals(omElementD.getFirstElement().toString()));
    }

    /**
     * Positive test case for postGetAll method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "delicious {postGetAll} integration test with mandatory parameters")
    public void testDeliciouspostGetAllWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "postGetAll.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/all", "", validAuthorization);

        Assert.assertTrue(omElementC.getFirstElement().toString().equals(omElementD.getFirstElement().toString()));
    }


    /**
     * Positive test case for addNewPost method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "delicious {addNewPost} integration test with mandatory parameters")
    public void testDeliciousaddNewPostWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "addNewPost.txt";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("test_url",connectorProperties.getProperty("inputDescription"));
        rawString = rawString.replace("bookmarkurl",connectorProperties.getProperty("inputurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/recent?&count=1", "", validAuthorization);

        Assert.assertTrue(omElementD.getFirstElement().getAttributeValue(QName.valueOf("description")).equals(connectorProperties.getProperty("inputDescription")));

    }


    /**
     * Positive test case for postAllHashes method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "delicious {postAllHashes} integration test with mandatory parameters")
    public void testDeliciouspostAllHashesWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "postAllHashes.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/all?hashes", "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Positive test case for getDates method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "delicious {getDates} integration test with mandatory parameters")
    public void testDeliciousgetDatesWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "getDates.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/dates", "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Positive test case for deletePost method with mandatory parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {deletePost} integration test with mandatory parameters")
    public void testDeliciousdeletePostWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "deletePost.txt";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("bookmarkurl",connectorProperties.getProperty("inputurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&url="+connectorProperties.getProperty("inputurl");
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/get?"+parameters, "", validAuthorization);

        Assert.assertTrue(omElementD.getAttributeValue(QName.valueOf("code")).equals("no bookmarks"));


    }

    /**
     * Positive test case for getPosts method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "delicious {getPosts} integration test with mandatory parameters")
    public void testDeliciousgetPostsWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "getPosts.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/get", "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Positive test case for getRecentPosts method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "delicious {getRecentPosts} integration test with mandatory parameters")
    public void testDeliciousgetRecentPostsWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "getRecentPosts.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/recent", "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Positive test case for suggestPopularTags method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "delicious {suggestPopularTags} integration test with mandatory parameters")
    public void testDelicioussuggestPopularTagsWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "suggestPopularTags.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&url=www.yahoo.com";
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/suggest?" + parameters, "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Positive test case for lastUpdatedTime method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "delicious {lastUpdatedTime} integration test with mandatory parameters")
    public void testDeliciouslastUpdatedTimeWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "lastUpdatedTime.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/update", "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }




    /**
     * Positive test case for getTags method with mandatory parameters.
     */

    @Test(priority = 2, groups = { "wso2.esb" }, description = "delicious {getTags} integration test with mandatory parameters")
    public void testDeliciousgetTagsWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "getTags.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/tags/get", "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }

    /**
     * Positive test case for allTagsBundles method with mandatory parameters.
     */

    @Test(priority = 2, groups = { "wso2.esb" }, description = "delicious {allTagsBundles} integration test with mandatory parameters")
    public void testDeliciousallTagsBundlesWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "allTagsBundles.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC= responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD=responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl")+"/v1/tags/bundles/all", "",validAuthorization);

        Assert.assertTrue(omElementC.getFirstElement().toString().equals(omElementD.getFirstElement().toString()));

    }


    /**
     * Positive test case for setTagsBundles method with mandatory parameters.
     */


    @Test(priority = 1, groups = { "wso2.esb" }, description = "delicious {setTagsBundles} integration test with mandatory parameters")
    public void testDelicioussetTagsBundlesWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "setTagsBundles.txt";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters ="&bundle="+connectorProperties.getProperty("bundle");
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/tags/bundles/all?" +parameters, "", validAuthorization);

        Assert.assertTrue(omElementD.getFirstElement().getAttributeValue(QName.valueOf("name")).equals(connectorProperties.getProperty("bundle")));

    }

    /**
     * Positive test case for deleteTagsBundles method with mandatory parameters.
     */

    @Test(priority = 5, groups = { "wso2.esb" }, description = "delicious {deleteTagsBundles} integration test with mandatory parameters")
    public void testDeliciousdeleteTagsBundlesWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "deleteTagsBundles.txt";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        Assert.assertTrue(omElementC.toString().equals("<result>done</result>"));
    }









/******************************************************NegetiveTests*************************************************/





    /**
     * Oauth Negetive test case for postGetAll method with Negetive parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {postGetAll} integration test with Negetive parameters")
    public void testOauthDeliciouspostGetAllWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/postGetAllwithOauth.txt";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("clientId",connectorProperties.getProperty("invalidclient_id"));
        rawString = rawString.replace("address",connectorProperties.getProperty("Apiurl"));
        rawString = rawString.replace("clientSecret",connectorProperties.getProperty("invalidclient_secret"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/all", "", invalidAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));
    }



    /**
     * Negetive test case for postGetAll method with Negetive parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {postGetAll} integration test with Negetive parameters")
    public void testDeliciouspostGetAllWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/postGetAll.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("invalidusername",connectorProperties.getProperty("invalidusername"));
        rawString = rawString.replace("invalidpassword",connectorProperties.getProperty("invalidpassword"));
        rawString = rawString.replace("address",connectorProperties.getProperty("Apiurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/all", "", invalidAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Negetive test case for addNewPost method with Negetive parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {addNewPost} integration test with Negetive parameters")
    public void testDeliciousaddNewPostWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/addNewPost.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&url=&description=";
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/add?" + parameters, "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));


    }


    /**
     * Negetive test case for postAllHashes method with Negetive parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {postAllHashes} integration test with Negetive parameters")
    public void testDeliciouspostAllHashesWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/postAllHashes.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("invalidusername",connectorProperties.getProperty("invalidusername"));
        rawString = rawString.replace("invalidpassword",connectorProperties.getProperty("invalidpassword"));
        rawString = rawString.replace("address",connectorProperties.getProperty("Apiurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/all?hashes", "", invalidAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Negetive test case for getDates method with Negetive parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {getDates} integration test with Negetive parameters")
    public void testDeliciousgetDatesWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/getDates.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("invalidusername",connectorProperties.getProperty("invalidusername"));
        rawString = rawString.replace("invalidpassword",connectorProperties.getProperty("invalidpassword"));
        rawString = rawString.replace("address",connectorProperties.getProperty("Apiurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/dates", "", invalidAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Negetive test case for deletePost method with Negetive parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {deletePost} integration test with Negetive parameters")
    public void testDeliciousdeletePostWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/deletePost.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&url=www.dummyurl.com";
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/delete?" + parameters, "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }

    /**
     * Negetive test case for getPosts method with Negetive parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {getPosts} integration test with Negetive parameters")
    public void testDeliciousgetPostsWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/getPosts.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("invalidusername",connectorProperties.getProperty("invalidusername"));
        rawString = rawString.replace("invalidpassword",connectorProperties.getProperty("invalidpassword"));
        rawString = rawString.replace("address",connectorProperties.getProperty("Apiurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/get", "", invalidAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Negetive test case for getRecentPosts method with Negetive parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {getRecentPosts} integration test with Negetive parameters")
    public void testDeliciousgetRecentPostsWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/getRecentPosts.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("invalidusername",connectorProperties.getProperty("invalidusername"));
        rawString = rawString.replace("invalidpassword",connectorProperties.getProperty("invalidpassword"));
        rawString = rawString.replace("address",connectorProperties.getProperty("Apiurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/recent", "", invalidAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Negetive test case for suggestPopularTags method with Negetive parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {suggestPopularTags} integration test with Negetive parameters")
    public void testDelicioussuggestPopularTagsWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/suggestPopularTags.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&url=";
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/suggest?" + parameters, "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Negetive test case for lastUpdatedTime method with Negetive parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "delicious {lastUpdatedTime} integration test with Negetive parameters")
    public void testDeliciouslastUpdatedTimeWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/lastUpdatedTime.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("invalidusername",connectorProperties.getProperty("invalidusername"));
        rawString = rawString.replace("invalidpassword",connectorProperties.getProperty("invalidpassword"));
        rawString = rawString.replace("address",connectorProperties.getProperty("Apiurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/update", "", invalidAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }




    /**
     * Negative test case for getTags method with negative parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "delicious {getTags} integration test with negative parameters")
    public void testDeliciousgetTagsWithNegativeParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/getTags.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("invalidusername",connectorProperties.getProperty("invalidusername"));
        rawString = rawString.replace("invalidpassword",connectorProperties.getProperty("invalidpassword"));
        rawString = rawString.replace("address",connectorProperties.getProperty("Apiurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/tags/get", "", invalidAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }

    /**
     * Negative test case for allTagsBundles method with negative parameters.
     */

    @Test(priority = 2, groups = { "wso2.esb" }, description = "delicious {allTagsBundles} integration test with negative parameters")
    public void testDeliciousallTagsBundlesWithNegativeParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/allTagsBundles.txt";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);

        rawString = rawString.replace("invalidusername",connectorProperties.getProperty("invalidusername"));
        rawString = rawString.replace("invalidpassword",connectorProperties.getProperty("invalidpassword"));
        rawString = rawString.replace("address",connectorProperties.getProperty("Apiurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC= responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD=responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl")+"/v1/tags/bundles/all?", "",invalidAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Negative test case for setTagsBundles method with negative parameters.
     */


    @Test(priority = 2, groups = { "wso2.esb" }, description = "delicious {setTagsBundles} integration test with negative parameters")
    public void testDelicioussetTagsBundlesWithNegativeParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/setTagsBundles.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&bundle=" +""+"&tags="+"";
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/tags/bundles/set?"+parameters, "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Negative test case for deleteTagsBundles method with negative parameters.
     */

    @Test(priority = 2, groups = { "wso2.esb" }, description = "delicious {deleteTagsBundles} integration test with negative parameters")

    public void testDeliciousdeleteTagsBundlesWithNegativeParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "negative/deleteTagsBundles.txt";
        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyBundle",connectorProperties.getProperty("invalidBundle"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&bundle=" +connectorProperties.getProperty("invalidBundle");
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") +"/v1/tags/bundles/delete?" + parameters, "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }








/***************************************************OPTIONAL PARAMETERS***********************************************/








    /**
     * Positive test case for addNewPost method with Optional parameters.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "delicious {addNewPost} integration test with Optional parameters")
    public void testDeliciousaddNewPostWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "optional/addNewPost.txt";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("test_url",connectorProperties.getProperty("inputDescriptionWithOptional"));
        rawString = rawString.replace("bookmarkurl",connectorProperties.getProperty("inputurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();

        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/recent?&count=1", "", validAuthorization);

        Assert.assertTrue(omElementD.getFirstElement().getAttributeValue(QName.valueOf("description")).equals(connectorProperties.getProperty("inputDescriptionWithOptional")));


    }


    /**
     * Positive test case for postGetAll method with Optional parameters.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "delicious {postGetAll} integration test with Optional parameters")
    public void testDeliciouspostGetAllWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "optional/postGetAll.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&tag_separator=comma&results=1";
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/all?" + parameters, "", validAuthorization);

        Assert.assertTrue(omElementC.getFirstElement().toString().equals(omElementD.getFirstElement().toString()));

    }


    /**
     * Positive test case for getDates method with Optional parameters.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "delicious {getDates} integration test with Optional parameters")
    public void testDeliciousgetDatesWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "optional/getDates.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&tag=test";
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/dates?" + parameters, "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Positive test case for getPosts method with Optional parameters.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "delicious {getPosts} integration test with Optional parameters")
    public void testDeliciousgetPostsWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "optional/getPosts.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&tag=test&meta=yes&tag_separator=comma";
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/get?" + parameters, "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Positive test case for getRecentPosts method with Optional parameters.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "delicious {getRecentPosts} integration test with Optional parameters")
    public void testDeliciousgetRecentPostsWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "optional/getRecentPosts.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&tag=test&count=1";
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/recent?" + parameters, "", validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Positive test case for allTagsBundles method with optional parameters.
     */

    @Test(priority = 3, groups = { "wso2.esb" }, description = "delicious {allTagsBundles} integration test with optional parameters")
    public void testDeliciousallTagsBundlesWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "optional/allTagsBundles.txt";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC= responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&bundle=TestWSO2";
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD=responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl")+"/v1/tags/bundles/all?" + parameters, "",validAuthorization);

        Assert.assertTrue(omElementC.toString().equals(omElementD.toString()));

    }


    /**
     * Positive test case for deletePost method with optional parameters.
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "delicious {deletePost} integration test with optional parameters")
    public void testDeliciousdeletePostWithoptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToResourcesDirectory + "optional/deletePost.txt";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("bookmarkurl",connectorProperties.getProperty("inputurl"));
        final String jsonString = addCredentials(rawString);

        ConnectorIntegrationUtil responseConnector = new ConnectorIntegrationUtil();
        OMElement omElementC = responseConnector.getXmlResponse("POST", getProxyServiceURL("delicious"), jsonString);

        String parameters = "&url="+connectorProperties.getProperty("inputurl");
        ConnectorIntegrationUtil responseDirect = new ConnectorIntegrationUtil();
        OMElement omElementD = responseDirect.sendXMLRequestWithBasic(connectorProperties.getProperty("Apiurl") + "/v1/posts/get?"+parameters, "", validAuthorization);

        Assert.assertTrue(omElementD.getAttributeValue(QName.valueOf("code")).equals("no bookmarks"));

    }

}
