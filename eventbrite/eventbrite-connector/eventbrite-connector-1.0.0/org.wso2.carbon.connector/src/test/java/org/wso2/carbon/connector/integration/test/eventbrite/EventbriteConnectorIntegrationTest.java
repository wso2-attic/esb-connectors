/*
*  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.connector.integration.test.eventbrite;

import java.lang.String;
import java.net.URL;
import java.util.Properties;
import javax.activation.DataHandler;

import org.apache.axis2.context.ConfigurationContext;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;

public class EventbriteConnectorIntegrationTest extends ESBIntegrationTest {

    private static final String CONNECTOR_NAME = "eventbrite";
    private MediationLibraryUploaderStub mediationLibUploadStub = null;
    private MediationLibraryAdminServiceStub adminServiceStub = null;
    private ProxyServiceAdminClient proxyAdmin;
    private String repoLocation = null;
    private String eventbriteConnectorFileName = CONNECTOR_NAME + "-connector-1.0.0.zip";
    private Properties eventbriteConnectorProperties = null;
    private String pathToProxiesDirectory = null;
    private String pathToRequestsDirectory = null;

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
            repoLocation = System.getProperty("connector_repo").replace("/", "\\");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, eventbriteConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);
        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");
        eventbriteConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        pathToProxiesDirectory = repoLocation + eventbriteConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + eventbriteConnectorProperties.getProperty("requestDirectoryRelativePath");
    }

    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }

    /**
     * Positive test case for getMe method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getMe} integration test with mandatory parameters.")
    public void testGetMe() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getMe.txt";
        String methodName = "getMe";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            String userId = jsonResponse.getString("id");
            eventbriteConnectorProperties.setProperty("userId",userId);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
}
    /**
     * Positive test case for getUserDetails method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe"}, description = "eventbrite{getuserdetails} integration test with mandatory parameters.")
    public void testGetUserDetailsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getuserdetails_mandatory.txt";
        String methodName = "getuserdetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for  getUserDetails method .
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getuserdetails} integration test with negative.")
    public void testGetUserDetailsWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getuserdetails_negative.txt";
        String methodName = "getuserdetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getUserOrder method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods={"testGetMe"}, description = "eventbrite{getUserOrder} integration test with mandatory parameters.")
    public void testgetUserOrderWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserOrder_mandatory.txt";
        String methodName = "getUserOrder";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("orders"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for  getUserOrder method .
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getUserOrder} integration test with negative.")
    public void testgetUserOrderWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserOrder_negative.txt";
        String methodName = "getUserOrder";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getUserOwnedEvents method with optinal parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe"}, description = "eventbrite{getUserOwnedEvents} integration test with optinal parameters.")
    public void testgetUserOwnedEventsWithOptinalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserOwnedEvents_optinal.txt";
        String methodName = "getUserOwnedEvents";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("events"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for  getUserOwnedEvents method .
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getUserOwnedEvents} integration test with negative.")
    public void testgetUserOwnedEventsWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getUserOwnedEvents_negative.txt";
        String methodName = "getUserOwnedEvents";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
            log.info(responseHeader);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getUserOwnedEventsOrders method with optinal parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe"}, description = "eventbrite{getUserOwnedEventsOrders} integration test with optinal parameters.")
    public void testgetUserOwnedEventsOrdersWithOptinalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserOwnedEventsOrders_optinal.txt";
        String methodName = "getUserOwnedEventsOrders";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("orders"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for  getUserOwnedEventsOrders method .
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getUserOwnedEventsOrders} integration test with negative.")
    public void testgetUserOwnedEventsOrdersWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserOwnedEventsOrders_negative.txt";
        String methodName = "getUserOwnedEventsOrders";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getUserOwnedEventAttendees method with optinal parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe"}, description = "eventbrite{getUserOwnedEventAttendees} integration test with optinal parameters.")
    public void testgetUserOwnedEventAttendeesWithOptinalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserOwnedEventAttendees_optinal.txt";
        String methodName = "getUserOwnedEventAttendees";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("attendees"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for eventSearch method with optinal parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getUserOwnedEventsOrders} integration test with optinal parameters.")
    public void testEventSearchWithOptinalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventSearch_Optional.txt";
        String methodName = "getEventSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.getJSONArray("events").getJSONObject(0).has("venue"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for  getUserOwnedEventAttendees method .
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getUserOwnedEventAttendees} integration test with negative.")
    public void testGetUserOwnedEventAttendeesWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserOwnedEventAttendees_negative.txt";
        String methodName = "getUserOwnedEventAttendees";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getUserVenues method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe"}, description = "eventbrite{getUserVenues} integration test with mandatory parameters.")
    public void testGetUserVenuesWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserVenues_mandatory.txt";
        String methodName = "getUserVenues";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("venues"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for  getUserVenues method .
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getUserVenues} integration test with negative.")
    public void testGetUserVenuesWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserVenues_negative.txt";
        String methodName = "getUserVenues";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getUserOrganizers method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe"}, description = "eventbrite{getUserOrganizers} integration test with mandatory parameters.")
    public void testGetUserOrganizersWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserOrganizers_mandatory.txt";
        String methodName = "getUserOrganizers";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("organizers"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for  getUserOrganizers method .
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getUserOrganizers} integration test with negative.")
    public void testGetUserOrganizersWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserOrganizers_negative.txt";
        String methodName = "getUserOrganizers";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**oid
     * Positive test case for getOrderDetails  method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateOrganizerParameters"}, description = "eventbrite{getOrderDetails} integration test with mandatory parameters.")
    public void testGetOrderDetailsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getOrderDetails_mandatory.txt";
        String methodName = "getOrderDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("orderId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("costs"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for  getOrderDetails method .
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getOrderDetails} integration test with negative.")
    public void testGetOrderDetailsWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getOrderDetails_negative.txt";
        String methodName = "getOrderDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("orderId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 500);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getContactLists method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe"}, description = "eventbrite{getContactLists} integration test with mandatory parameters.")
    public void testGetContactListsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactLists_mandatory.txt";
        String methodName = "getContactLists";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("contact_lists"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getContactLists  method .
     */

    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getContactLists} integration test with negative.")
    public void testGetContactListsWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactLists_negative.txt";
        String methodName = "getContactLists";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getContactListDetails method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe", "testCreateContactListDetailsWithMandatoryParameters"}, description = "eventbrite{getContactListDetails} integration test with mandatory parameters.")
    public void testGetContactListDetailsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactListDetails_mandatory.txt";
        String methodName = "getContactListDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"), eventbriteConnectorProperties.getProperty("contactListId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("resource_uri"));
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getContactListDetails  method .
     */

    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getContactListDetails} integration test with negative.")
    public void testGetContactListDetailsWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactListDetails_negative.txt";
        String methodName = "getContactListDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"), eventbriteConnectorProperties.getProperty("contactListId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getSpecificContactListDetails method with mandatory parameters.
     */

    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe", "testCreateContactListDetailsWithMandatoryParameters"}, description = "eventbrite{getSpecificContactListDetails} integration test with mandatory parameters.")
    public void testGetSpecificContactListDetailsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSpecificContactListDetails_mandatory.txt";
        String methodName = "getSpecificContactListDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"), eventbriteConnectorProperties.getProperty("contactListId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("contacts"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSpecificContactListDetails  method .
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getSpecificContactListDetails} integration test with negative.")
    public void testGetSpecificContactListDetailsWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSpecificContactListDetails_negative.txt";
        String methodName = "getSpecificContactListDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"), eventbriteConnectorProperties.getProperty("contactListId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**eid
     * Positive test case for testEventTeams method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters"}, description = "eventbrite{eventTeams} integration test.")
    public void testEventTeamsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTeams_Mandatory.txt";
        String methodName = "getEventTeams";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("teams"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for testEventOrder method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventOrders} integration test.")
    public void testEventOrdersWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventOrders_Mandatory.txt";
        String methodName = "getEventOrders";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("orders"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**eid
     * Positive test case for testEventDetails method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters"}, description = "eventbrite{eventDetails} integration test.")
    public void testEventDetailsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventDetails_Mandatory.txt";
        String methodName = "getEventDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("resource_uri"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**eid
     * Positive test case for testEventAttendees method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters"}, description = "eventbrite{eventAttendees} integration test.")
    public void testEventAttendeesWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventAttendees_Mandatory.txt";
        String methodName = "getEventAttendees";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("attendees"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**eid, aid
     * Positive test case for testEventAttendeesDetails method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters", ""}, description = "eventbrite{eventAttendeesDetails} integration test.")
    public void testEventAttendeesDetailsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventAttendeesDetails_Mandatory.txt";
        String methodName = "getEventAttendeesDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"), eventbriteConnectorProperties.getProperty("attendeesId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("team"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    ====================
    /**
     * Positive test case for testEventCatagories method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventCategories} integration test.")
    public void testEventCatagoriesWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventCategories_Mandatory.txt";
        String methodName = "getEventCategories";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("categories"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
===========================
    /**
     * Positive test case for testEventSearch method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{EventSearch} integration test.")
    public void testEventSearchWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventSearch_Mandatory.txt";
        String methodName = "getEventSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("events"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**eid, tid
     * Positive test case for testEventTeamsAttendees method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters", ""}, description = "eventbrite{eventTeamsAttendees} integration test.")
    public void testEventTeamsAttendeesWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTeamsAttendees_Mandatory.txt";
        String methodName = "getEventTeamsAttendees";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"), eventbriteConnectorProperties.getProperty("teamId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("attendees"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**eid, tid
     * Positive test case for testEventTeams method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters", ""}, description = "eventbrite{eventTeamsDetails} integration test.")
    public void testEventTeamsDetailsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTeamsDetails_Mandatory.txt";
        String methodName = "getEventTeamsDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"), eventbriteConnectorProperties.getProperty("teamId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("event"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**eid
     * Positive test case for testEventTransferDetails method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters"}, description = "eventbrite{eventTransfers} integration test.")
    public void testEventTransferDetailsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTransfers_Mandatory.txt";
        String methodName = "getEventTransfers";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("transfers"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**eid
     * Positive test case for testGetEventAccessCodes method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters"}, description = "eventbrite{getEventAccessCodes} integration test.")
    public void testGetEventAccessCodesWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getEventAccessCodes_Mandatory.txt";
        String methodName = "getEventAccessCodes";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("access_codes"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**eid
     * Positive test case for testGetEventDiscounts method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters"}, description = "eventbrite{getEventDiscounts} integration test.")
    public void testGetEventDiscountsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getEventDiscounts_Mandatory.txt";
        String methodName = "getEventDiscounts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("discounts"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSpecificContactListDetails method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventTeams} integration Negative test.")
    public void testEventTeamsWithMandatoryNegativeParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTeams_Negative.txt";
        String methodName = "getEventTeams";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);

            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventOrders method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventOrders} integration Negative test.")
    public void testEventOrdersWithMandatoryNegativePParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventOrders_Negative.txt";
        String methodName = "getEventOrders";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventAttendees method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventAttendees} integration Negative test.")
    public void testEventAttendeesWithMandatoryNegativeParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventAttendees_Negative.txt";
        String methodName = "getEventAttendees";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventAttendeesDetails method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventAttendeesDetails} integration Negative test.")
    public void testEventAttendeesDetailsWithMandatoryNegativeParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventAttendeesDetails_Negative.txt";
        String methodName = "getEventAttendeesDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"), eventbriteConnectorProperties.getProperty("attendeesId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventAttendeesDetails method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventAttendeesDetails} integration Negative2 test.")
    public void testEventAttendeesDetailsWithMandatoryNegative2Parameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventAttendeesDetails_Negative2.txt";
        String methodName = "getEventAttendeesDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"), eventbriteConnectorProperties.getProperty("attendeesId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventCategories method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventCategories} integration  Negative test.")
    public void testEventCatagoriesWithMandatoryNegativeParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventCategories_Negative.txt";
        String methodName = "getEventCategories";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventTeamsAttendees method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventTeamsAttendees} integration Negative test.")
    public void testEventTeamsAttendeesWithMandatoryNegativeParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTeamsAttendees_Negative.txt";
        String methodName = "getEventTeamsAttendees";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"), eventbriteConnectorProperties.getProperty("teamId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventTeamsAttendees method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventTeamsAttendees} integration Negative1 test.")
    public void testEventTeamsAttendeesWithMandatoryNegative1Parameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTeamsAttendees_Negative1.txt";
        String methodName = "getEventTeamsAttendees";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"), eventbriteConnectorProperties.getProperty("teamId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventTeamsAttendees method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventTeamsAttendees} integration Negative2 test.")
    public void testEventTeamsAttendeesWithMandatoryNegative2Parameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTeamsAttendees_Negative2.txt";
        String methodName = "getEventTeamsAttendees";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"), eventbriteConnectorProperties.getProperty("teamId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 500);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventTeamsDetails method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventTeamsDetails} integration Negative test.")
    public void testEventTeamsDetailsWithMandatoryNegativeParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTeamsDetails_Negative.txt";
        String methodName = "getEventTeamsDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"), eventbriteConnectorProperties.getProperty("teamId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventTeamsDetails method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventTeamsDetails} integration Negative 2 test.")
    public void testEventTeamsDetailsWithMandatoryNegative2Parameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTeamsDetails_Negative.txt";
        String methodName = "getEventTeamsDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"), eventbriteConnectorProperties.getProperty("teamId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for eventTransfers method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{eventTransfers} integration Negative test.")
    public void testEventTransferDetailsWithMandatoryNegativeParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "eventTransfers_Negative.txt";
        String methodName = "getEventTransfers";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getEventAccessCodes method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getEventAccessCodes} integration Negative test.")
    public void testGetEventAccessCodesWithMandatoryNegativeParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getEventAccessCodes_Negative.txt";
        String methodName = "getEventAccessCodes";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getEventDiscounts method with Negative parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{getEventDiscounts} integration Negative test.")
    public void testGetEventDiscountsWithMandatoryNegativeParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getEventDiscounts_Negative.txt";
        String methodName = "getEventDiscounts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for testCreateContactListDetails method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe"}, description = "eventbrite{testCreateContactListDetailsWithMandatoryParameters} integration test with mandatory parameters.")
    public void testCreateContactListDetailsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createContactLists.txt";
        String methodName = "createContact";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            String contactListId = jsonResponse.getString("id");
            eventbriteConnectorProperties.setProperty("contactListId",contactListId);
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createSpecificContactListDetails method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe", "testCreateContactListDetailsWithMandatoryParameters"}, description = "eventbrite{createSpecificContactListDetails} integration test with mandatory parameters.")
    public void testcreateSpecificContactListDetailsMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createSpecificContactListDetails.txt";
        String methodName = "createSpecificContactListDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"), eventbriteConnectorProperties.getProperty("contactListId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for testcreateSpecificContactListDetailsOptionalParameters  method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe", "testCreateContactListDetailsWithMandatoryParameters"}, description = "eventbrite{testcreateSpecificContactListDetailsOptionalParameters} integration test with optional parameters.")
    public void testcreateSpecificContactListDetailsOptionalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createSpecificContactListDetails_Optional.txt";
        String methodName = "createSpecificContactListDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"), eventbriteConnectorProperties.getProperty("contactListId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createContactListDetails method with mandatory  parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetMe", "testCreateContactListDetailsWithMandatoryParameters"}, description = "eventbrite{createContactListDetails} integration test with mandatory parameters.")
    public void testcreateContactListDetailsMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createContactListDetails.txt";
        String methodName = "createContactListDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("userId"), eventbriteConnectorProperties.getProperty("contactListId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createEventAccessCodes method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters"}, description = "eventbrite{createEventAccessCodes} integration test with mandatory parameters.")
    public void testcreateEventAccessCodesMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createEventAccessCodes_Mandatory.txt";
        String methodName = "createEventAccessCodes";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createEventAccessCodes method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters"}, description = "eventbrite{createEventAccessCodes} integration test with optional parameters.")
    public void testcreateEventAccessCodesOptionalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createEventAccessCodes_Optional.txt";
        String methodName = "createEventAccessCodes";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createEventDiscount method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters"}, description = "eventbrite{createEventDiscount} integration test with mandatory parameters.")
    public void testcreateEventDiscountMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createEventDiscounts_Mandatory.txt";
        String methodName = "createEventDiscounts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createEventDiscount method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateEventParameters"}, description = "eventbrite{createEventDiscount} integration test with optional parameters.")
    public void testcreateEventDiscountOptionalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createEventDiscounts_Optional.txt";
        String methodName = "createEventDiscounts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("eventId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    /**
     * Positive test case for createOrganizer method with optional  parameters.Ok
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{createOrganizer} integration test with optional parameters.")
    public void testcreateOrganizerParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createOrganizer_Optional.txt";
        String methodName = "createOrganizer";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            String organizerId = jsonResponse.getString("id");
            eventbriteConnectorProperties.setProperty("organizerId",organizerId);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    /**
     * Positive test case for createUserVenues method with optional  parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "eventbrite{createUserVenues} integration test with optional parameters.")
    public void testcreateUserVenuesOptionalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createUserVenues_Optional.txt";
        String methodName = "createUserVenues";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse = null;
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            String venueId = jsonResponse.getString("id");
            eventbriteConnectorProperties.setProperty("venueId",venueId);
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);

            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    /**
     * Positive test case for createEvent method with mandatory  parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testcreateOrganizerParameters", "testcreateUserVenuesOptionalParameters"}, description = "eventbrite{createEvent} integration test with mandatory parameters.")
    public void testcreateEventParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createEvent.txt";
        String methodName = "createEvent";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, eventbriteConnectorProperties.getProperty("apiUrl"), eventbriteConnectorProperties.getProperty("accessToken"), eventbriteConnectorProperties.getProperty("organizerId"), eventbriteConnectorProperties.getProperty("venueId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            String eventId = jsonResponse.getString("id");
            eventbriteConnectorProperties.setProperty("eventId",eventId);
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);

            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
}
