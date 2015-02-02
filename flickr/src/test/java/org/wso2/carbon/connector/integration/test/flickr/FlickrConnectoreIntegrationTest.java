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

package org.wso2.carbon.connector.integration.test.flickr;

import java.util.Properties;

import org.apache.axis2.context.ConfigurationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;
import org.json.JSONObject;

import java.net.URL;

import javax.activation.DataHandler;

public class FlickrConnectoreIntegrationTest extends ESBIntegrationTest {

    protected static final String CONNECTOR_NAME = "flickr";

    protected MediationLibraryUploaderStub mediationLibUploadStub = null;

    protected MediationLibraryAdminServiceStub adminServiceStub = null;

    protected ProxyServiceAdminClient proxyAdmin;

    protected String repoLocation = null;

    protected String flickrConnectorFileName = CONNECTOR_NAME + ".zip";

    protected Properties flickrConnectorProperties = null;

    protected String pathToProxiesDirectory = null;

    protected String pathToRequestsDirectory = null;

    // Variables for store results of dependent methods
    protected String addCommentMethodCommentId;
    protected String addTagMethodTagId;



    @BeforeTest(alwaysRun = true)
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

        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, flickrConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(10000);

        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");

        flickrConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

        pathToProxiesDirectory = repoLocation + flickrConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + flickrConnectorProperties.getProperty("requestDirectoryRelativePath");

    }

    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }

    private String addCredentials(String jsonString){
        return String.format(
                jsonString,
                flickrConnectorProperties.getProperty("consumerKey"),
                flickrConnectorProperties.getProperty("consumerKeySecret"),
                flickrConnectorProperties.getProperty("accessToken"),
                flickrConnectorProperties.getProperty("accessTokenSecret"));
    }

    /**
     * Positive test case for echo method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "flickr {echo} integration test with mandatory parameters")
    public void testFlickrEchoWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_echo.txt";
        String methodName = "flickr_echo";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.test.echo&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+
                    "&value=wso2-esb";
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);

            Assert.assertTrue(responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for isLogged method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "flickr {isLogged} integration test with mandatory parameters.")
    public void testFlickrIsLoggedWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_isLogged.txt";
        String methodName = "flickr_isLogged";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST",getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json"+
                    "&method=flickr.test.login" +
                    "&nojsoncallback=1"+
                    "&oauth_consumer_key="+flickrConnectorProperties.getProperty("consumerKey")+
                    "&oauth_nonce=dummynonce"+
                    "&oauth_signature_method=HMAC-SHA1"+
                    "&oauth_timestamp=dummytimestamp"+
                    "&oauth_token="+flickrConnectorProperties.getProperty("accessToken")+
                    "&oauth_version=1.0";
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(true,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);

            Assert.assertTrue(responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for addComment method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "flickr {addComment} integration test with mandatory parameters")
    public void testFlickrAddCommentWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_addComment.txt";
        String methodName = "flickr_addComment";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("photoId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.comments.getList&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            String commentIdConnector = responseConnector.getJSONObject("comment").getString("id");
            addCommentMethodCommentId = commentIdConnector; //keeping the comment id to be used in deleteComment method.
            Assert.assertTrue(responseDirect.toString().contains(commentIdConnector));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for getCommentsList method with mandatory parameters.
     */
//   @Test(priority = 1, groups = { "wso2.esb" }, description = "flickr {getCommentsList} integration test with mandatory parameters")
    public void testFlickrGetCommentsListWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getCommentList.txt";
        String methodName = "flickr_getCommentList";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("photoId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.comments.getList&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseDirect.toString().equals(responseConnector.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Positive test case for editComment method with mandatory parameters.
     */
    @Test(dependsOnMethods ={"testFlickrAddCommentWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "flickr {editComment} integration test with mandatory parameters")
    public void  testFlickrEditCommentWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_editComment.txt";
        String methodName = "flickr_editComment";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",addCommentMethodCommentId);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.comments.getList&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseDirect.toString().contains("edited comment"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for deleteComment method with mandatory parameters.
     */
    @Test(dependsOnMethods ={"testFlickrAddCommentWithMandatoryParameters","testFlickrEditCommentWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "flickr {deleteComment} integration test with mandatory parameters")
    public void  testFlickrDeleteCommentWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_deleteComment.txt";
        String methodName = "flickr_deleteComment";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",addCommentMethodCommentId);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.comments.getList&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(!responseDirect.toString().contains(addCommentMethodCommentId));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for getRecentCommentsForContacts method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "flickr {isLogged} integration test with mandatory parameters.")
    public void testFlickrGetRecentCommentsForContactsWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getRecentCommentsForContacts.txt";
        String methodName = "flickr_getRecentCommentsForContacts";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST",getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json"+
                    "&method=flickr.photos.comments.getRecentForContacts" +
                    "&nojsoncallback=1"+
                    "&oauth_consumer_key="+flickrConnectorProperties.getProperty("consumerKey")+
                    "&oauth_nonce=dummynonce"+
                    "&oauth_signature_method=HMAC-SHA1"+
                    "&oauth_timestamp=dummytimestamp"+
                    "&oauth_token="+flickrConnectorProperties.getProperty("accessToken")+
                    "&oauth_version=1.0";
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(true,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);

            Assert.assertTrue(responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for getInfo method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "flickr {getInfo} integration test with mandatory parameters")
    public void testFlickrGetInfoWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getInfo.txt";
        String methodName = "flickr_getInfo";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("userId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.people.getInfo&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&user_id="+flickrConnectorProperties.getProperty("userId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for getPhotos method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "flickr {getPhotos} integration test with mandatory parameters")
    public void testFlickrGetPhotosWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getPhotos.txt";
        String methodName = "flickr_getPhotos";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("userId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.people.getPhotos&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&user_id="+flickrConnectorProperties.getProperty("userId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for getPhotoInfo method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "flickr {getPhotoInfo} integration test with mandatory parameters")
    public void testFlickrGetPhotoInfoWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getPhotoInfo.txt";
        String methodName = "flickr_getPhotoInfo";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("photoId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.getInfo&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for getExif method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "flickr {getExif} integration test with mandatory parameters")
    public void testFlickrGetExifWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getExif.txt";
        String methodName = "flickr_getExif";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("photoId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.getExif&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for addTags method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "flickr {addTags} integration test with mandatory parameters")
    public void testFlickrAddTagsWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_addTags.txt";
        String methodName = "flickr_addTags";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("photoId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.getInfo&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            addTagMethodTagId = ((JSONObject)responseConnector.getJSONObject("tags").getJSONArray("tag").get(0)).getString("full_tag_id");
            Assert.assertTrue(responseDirect.toString().contains(addTagMethodTagId));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for removeTag method with mandatory parameters.
     */
    @Test(dependsOnMethods ={"testFlickrAddTagsWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "flickr {removeTag} integration test with mandatory parameters")
    public void  testFlickrRemoveTagWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_removeTag.txt";
        String methodName = "flickr_removeTag";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",addTagMethodTagId);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.getInfo&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(!responseDirect.toString().contains(addTagMethodTagId));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }
    
 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
/******************************************************NegetiveTests*************************************************/












    /**
     * Negetive test case for echo method with Negetive parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "flickr {echo} integration test with Negetive parameters")
    public void testFlickrEchoWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_echo.txt";
        String methodName = "flickr_echo";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"/negetive/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.test.echo&api_key="+
                    flickrConnectorProperties.getProperty("invalidConsumerKey")+
                    "&value=wso2-esb";
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);

            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negetive test case for isLogged method with Negetive parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "flickr {isLogged} integration test with Negetive parameters.")
    public void testFlickrIsLoggedWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_isLogged.txt";
        String methodName = "flickr_isLogged";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"/negetive/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST",getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json"+
                    "&method=flickr.test.login" +
                    "&nojsoncallback=1"+
                    "&oauth_consumer_key="+flickrConnectorProperties.getProperty("consumerKey")+
                    "&oauth_nonce=dummynonce"+
                    "&oauth_signature_method=HMAC-SHA1"+
                    "&oauth_timestamp=dummytimestamp"+
                    "&oauth_token="+flickrConnectorProperties.getProperty("invalidAccessToken")+
                    "&oauth_version=1.0";
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(true,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);

            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negetive test case for addComment method with Negetive parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "flickr {addComment} integration test with Negetive parameters")
    public void testFlickrAddCommentWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_addComment.txt";
        String methodName = "flickr_addComment";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("photoId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"/negetive/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.comments.getList&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&
                    !responseDirect.toString().contains(flickrConnectorProperties.getProperty("invalidComment")));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negetive test case for getCommentsList method with Negetive parameters.
     */
   @Test(priority = 2, groups = { "wso2.esb" }, description = "flickr {getCommentsList} integration test with Negetive parameters")
    public void testFlickrGetCommentsListWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getCommentList.txt";
        String methodName = "flickr_getCommentList";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("invalidPhotoId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"/negetive/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.comments.getList&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("invalidPhotoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&responseDirect.toString().equals(responseConnector.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Negetive test case for editComment method with Negetive parameters.
     */
   @Test(dependsOnMethods ={"testFlickrAddCommentWithNegetiveParameters"}, priority = 2, groups = { "wso2.esb" }, description = "flickr {editComment} integration test with Negetive parameters")
    public void  testFlickrEditCommentWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_editComment.txt";
        String methodName = "flickr_editComment";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"/negetive/"+ methodName + ".xml";

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.comments.getList&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&!responseDirect.toString().contains("edited comment"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negetive test case for deleteComment method with Negetive parameters.
     */
    @Test(/*dependsOnMethods ={"testFlickrAddCommentWithNegetiveParameters","testFlickrEditCommentWithNegetiveParameters"},*/ priority = 2, groups = { "wso2.esb" }, description = "flickr {deleteComment} integration test with Negetive parameters")
    public void  testFlickrDeleteCommentWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_deleteComment.txt";
        String methodName = "flickr_deleteComment";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory+"/negetive/" + methodName + ".xml";

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseConnector.getString("stat").equals("fail"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negetive test case for getRecentCommentsForContacts method with Negetive parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "flickr {isLogged} integration test with Negetive parameters.")
    public void testFlickrGetRecentCommentsForContactsWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getRecentCommentsForContacts.txt";
        String methodName = "flickr_getRecentCommentsForContacts";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"/negetive/"+ methodName + ".xml";

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST",getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json"+
                    "&method=flickr.photos.comments.getRecentForContacts" +
                    "&nojsoncallback=1"+
                    "&oauth_consumer_key="+flickrConnectorProperties.getProperty("invalidConsumerKey")+
                    "&oauth_nonce=dummynonce"+
                    "&oauth_signature_method=HMAC-SHA1"+
                    "&oauth_timestamp=dummytimestamp"+
                    "&oauth_token="+flickrConnectorProperties.getProperty("accessToken")+
                    "&oauth_version=1.0";
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(true,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);

            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negetive test case for getInfo method with Negetive parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "flickr {getInfo} integration test with Negetive parameters")
    public void testFlickrGetInfoWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getInfo.txt";
        String methodName = "flickr_getInfo";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("invalidUserId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"/negetive/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.people.getInfo&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&user_id="+flickrConnectorProperties.getProperty("invalidUserId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negetive test case for getPhotos method with Negetive parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "flickr {getPhotos} integration test with Negetive parameters")
    public void testFlickrGetPhotosWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getPhotos.txt";
        String methodName = "flickr_getPhotos";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("invalidUserId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory+"/negetive/" + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.people.getPhotos&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&user_id="+flickrConnectorProperties.getProperty("invalidUserId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negetive test case for getPhotoInfo method with Negetive parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "flickr {getPhotoInfo} integration test with Negetive parameters")
    public void testFlickrGetPhotoInfoWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getPhotoInfo.txt";
        String methodName = "flickr_getPhotoInfo";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("invalidPhotoId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"/negetive/"+ methodName + ".xml";

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.getInfo&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("invalidPhotoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negetive test case for getExif method with Negetive parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "flickr {getExif} integration test with Negetive parameters")
    public void testFlickrGetExifWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getExif.txt";
        String methodName = "flickr_getExif";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("invalidPhotoId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory+"/negetive/" + methodName + ".xml";

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.getExif&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("invalidPhotoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negetive test case for addTags method with Negetive parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "flickr {addTags} integration test with Negetive parameters")
    public void testFlickrAddTagsWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_addTags.txt";
        String methodName = "flickr_addTags";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("invalidPhotoId"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"/negetive/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.getInfo&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("PhotoId");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.getString("stat").equals("fail")&&!responseDirect.toString().contains("negetiveTag"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }


    }


    /**
     * Negetive test case for removeTag method with Negetive parameters.
     */
      @Test(dependsOnMethods ={"testFlickrAddTagsWithNegetiveParameters"}, priority = 2, groups = { "wso2.esb" }, description = "flickr {removeTag} integration test with Negetive parameters")
    public void  testFlickrRemoveTagWithNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_removeTag.txt";
        String methodName = "flickr_removeTag";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"/negetive/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseConnector.getString("stat").equals("fail"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }
    
    
 
    
    
    
    
    
/***************************************************OPTIONAL PARAMETERS***********************************************/










    /**
     * Positive test case for getCommentsList method with Optional parameters.
     */
    @Test(priority = 3, groups = { "wso2.esb" }, description = "flickr {getCommentsList} integration test with Optional parameters")
    public void testFlickrGetCommentsListWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "optional/flickr_getCommentList.txt";
        String methodName = "flickr_getCommentList";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("photoId"));
        rawString = rawString.replace("dummymindate",flickrConnectorProperties.getProperty("minDate"));
        rawString = rawString.replace("dummymaxdate",flickrConnectorProperties.getProperty("maxDate"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"optional/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.comments.getList&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId")
                    +"&min_comment_date="+flickrConnectorProperties.getProperty("minDate")+"&max_comment_date="+flickrConnectorProperties.getProperty("maxDate");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseDirect.toString().equals(responseConnector.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for getPhotos method with Optional parameters.
     */
    @Test(priority = 3, groups = { "wso2.esb" }, description = "flickr {getPhotos} integration test with Optional parameters")
    public void testFlickrGetPhotosWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory +"optional/"+ "flickr_getPhotos.txt";
        String methodName = "flickr_getPhotos";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("userId"));
        rawString = rawString.replace("dummyminuploaddate",flickrConnectorProperties.getProperty("minDate"));
        rawString = rawString.replace("dummymaxuploaddate",flickrConnectorProperties.getProperty("maxDate"));
        rawString = rawString.replace("dummymaxtakendate",flickrConnectorProperties.getProperty("maxDate"));
        rawString = rawString.replace("dummymintakendate",flickrConnectorProperties.getProperty("minDate"));
        rawString = rawString.replace("dummyextra",flickrConnectorProperties.getProperty("extraInfo"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"optional/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.people.getPhotos&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")
                    +"&user_id="+flickrConnectorProperties.getProperty("userId")
                    +"&min_upload_date="+flickrConnectorProperties.getProperty("minDate")
                    +"&max_upload_date="+flickrConnectorProperties.getProperty("maxDate")
                    +"&max_taken_date="+flickrConnectorProperties.getProperty("maxDate")
                    +"&min_taken_date="+flickrConnectorProperties.getProperty("minDate")
                    +"&extras="+flickrConnectorProperties.getProperty("extraInfo");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for getRecentCommentsForContacts method with Optional parameters.
     */
    @Test(priority = 3, groups = { "wso2.esb" }, description = "flickr {isLogged} integration test with Optional parameters.")
    public void testFlickrGetRecentCommentsForContactsWithOptionalParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "flickr_getRecentCommentsForContacts.txt";
        String methodName = "flickr_getRecentCommentsForContacts";

        final String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST",getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json"+
                    "&method=flickr.photos.comments.getRecentForContacts" +
                    "&nojsoncallback=1"+
                    "&oauth_consumer_key="+flickrConnectorProperties.getProperty("consumerKey")+
                    "&oauth_nonce=dummynonce"+
                    "&oauth_signature_method=HMAC-SHA1"+
                    "&oauth_timestamp=dummytimestamp"+
                    "&oauth_token="+flickrConnectorProperties.getProperty("accessToken")+
                    "&oauth_version=1.0";
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(true,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);

            Assert.assertTrue(responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }




    
    
    
    
    
    
    
    

    /***************************************************OPTIONAL NEGATIVE PARAMETERS***********************************************/











    /**
     * Positive test case for getCommentsList method with OptionalNegetive parameters.
     */
    @Test(priority = 4, groups = { "wso2.esb" }, description = "flickr {getCommentsList} integration test with OptionalNegetive parameters")
    public void testFlickrGetCommentsListWithOptionalNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "optional/flickr_getCommentList.txt";
        String methodName = "flickr_getCommentList";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("photoId"));
        rawString = rawString.replace("dummymindate",flickrConnectorProperties.getProperty("invalidMinDate"));
        rawString = rawString.replace("dummymaxdate",flickrConnectorProperties.getProperty("invalidMaxDate"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"optional/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.photos.comments.getList&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")+"&photo_id="+flickrConnectorProperties.getProperty("photoId")
                    +"&min_comment_date="+flickrConnectorProperties.getProperty("invalidMinDate")+"&max_comment_date="+flickrConnectorProperties.getProperty("invalidMaxDate");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(!responseConnector.toString().contains("author")&&responseDirect.toString().equals(responseConnector.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Positive test case for getPhotos method with OptionalNegetive parameters.
     */
    @Test(priority = 4, groups = { "wso2.esb" }, description = "flickr {getPhotos} integration test with OptionalNegetive parameters")
    public void testFlickrGetPhotosWithOptionalNegetiveParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory +"optional/"+ "flickr_getPhotos.txt";
        String methodName = "flickr_getPhotos";

        String rawString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        rawString = rawString.replace("dummyvalue",flickrConnectorProperties.getProperty("userId"));
        rawString = rawString.replace("dummyminuploaddate",flickrConnectorProperties.getProperty("invalidMinDate"));
        rawString = rawString.replace("dummymaxuploaddate",flickrConnectorProperties.getProperty("invalidMaxDate"));
        rawString = rawString.replace("dummymaxtakendate",flickrConnectorProperties.getProperty("invalidMaxDate"));
        rawString = rawString.replace("dummymintakendate",flickrConnectorProperties.getProperty("invalidMinDate"));
        rawString = rawString.replace("dummyextra",flickrConnectorProperties.getProperty("invalidExtraInfo"));
        final String jsonString = addCredentials(rawString);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory +"optional/"+ methodName + ".xml";
        //String modifiedJsonString = String.format(jsonString, accessToken);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), jsonString);

            String httpMethod = "GET";
            String parameters = "format=json&nojsoncallback=1&method=flickr.people.getPhotos&api_key="+
                    flickrConnectorProperties.getProperty("consumerKey")
                    +"&user_id="+flickrConnectorProperties.getProperty("userId")
                    +"&min_upload_date="+flickrConnectorProperties.getProperty("invalidMinDate")
                    +"&max_upload_date="+flickrConnectorProperties.getProperty("invalidMaxDate")
                    +"&max_taken_date="+flickrConnectorProperties.getProperty("invalidMaxDate")
                    +"&min_taken_date="+flickrConnectorProperties.getProperty("invalidMinDate")
                    +"&extras="+flickrConnectorProperties.getProperty("invalidExtraInfo");
            JSONObject responseDirect = ConnectorIntegrationUtil.sendRestRequest(false,
                    httpMethod, parameters, flickrConnectorProperties);

            System.out.println("responseConnector\n"+responseConnector);
            System.out.println("responseDirect\n"+responseDirect);
            Assert.assertTrue(!responseConnector.toString().contains("license")&&responseConnector.toString().equals(responseDirect.toString()));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }
    
    
}
