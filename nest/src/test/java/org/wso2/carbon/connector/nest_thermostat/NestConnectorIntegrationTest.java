/*
*  Copyright (c) 2014-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.connector.nest_thermostat;

import org.apache.axis2.context.ConfigurationContext;
import org.jruby.RubyProcess;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;

import javax.activation.DataHandler;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.System;
import java.net.URL;
import java.util.Properties;

public class NestConnectorIntegrationTest extends ESBIntegrationTest {
    private static final String CONNECTOR_NAME = "nest";

    private MediationLibraryUploaderStub mediationLibUploadStub = null;

    private MediationLibraryAdminServiceStub adminServiceStub = null;

    private ProxyServiceAdminClient proxyAdmin;

    private String repoLocation = null;

    private String nestConnectorFileName = "nest.zip";

    private Properties nestConnectorProperties = null;

    private String propertiesFilePath = null;

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
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, nestConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);

        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");
        nestConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        propertiesFilePath = repoLocation + nestConnectorProperties.getProperty("propertiesFilePath");
        pathToProxiesDirectory = repoLocation + nestConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + nestConnectorProperties.getProperty("requestDirectoryRelativePath");

        JSONObject apiRestResponse = new JSONObject(ConnectorIntegrationUtil.sendRestRequest("GET", ""));
        JSONObject structures = apiRestResponse.getJSONObject("structures");
        String smokeCOAlarmId = "";
        String thermostatId = "";
        String structureId = "";
        String key = structures.toString().split(":")[0].replace("\"", "").replace("{", "");
        JSONObject jObject = structures.getJSONObject(key);
        smokeCOAlarmId = jObject.get("smoke_co_alarms").toString().replace("[", "").replace("]", "").split(",")[0].replace("\"", "");
        thermostatId = jObject.get("thermostats").toString().replace("[", "").replace("]", "").split(",")[0].replace("\"", "");
        structureId = key;
        nestConnectorProperties.setProperty("smokeCOAlarmDeviceId", smokeCOAlarmId);
        nestConnectorProperties.setProperty("deviceIdThermostat", thermostatId);
        nestConnectorProperties.setProperty("structureId", structureId);
    }

    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }

    /**
     * Test case for getAccessToken method.
     * For this method, authorization code must be changed in nest.properties file every time.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, priority = 1, description = "nest {getAccessToken} integration test.")
    public void testGetAccessToken() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAccessToken.txt";
        String methodName = "nest_getAccessToken";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("clientId"), nestConnectorProperties.getProperty("clientSecret"), nestConnectorProperties.getProperty("code"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), modifiedJsonString);
            if (responseConnector.has("access_token") && !responseConnector.getString("access_token").equals("")) {
                FileInputStream in = new FileInputStream(propertiesFilePath + "nest.properties");
                Properties props = new Properties();
                props.load(in);
                in.close();
                FileOutputStream out = new FileOutputStream(propertiesFilePath + "nest.properties");
                props.setProperty("accessToken", responseConnector.getString("access_token"));
                props.store(out, null);
                out.close();
            }
            Assert.assertTrue(responseConnector.has("access_token"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewCurrentTemperatureFahrenheit) method.
     */
    @Test(groups = {"wso2.esb"}, priority = 2, description = "nest {getServices} integration test.")
    public void testViewCurrentTemperatureFahrenheit() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForThermostats.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceThermostats"), nestConnectorProperties.getProperty("deviceIdThermostat"), "viewCurrentTemperatureFahrenheit", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/thermostats/" + jo.getString("deviceId") + "/ambient_temperature_f";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewTargetTemperatureFahrenheit) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewTargetTemperatureFahrenheit() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForThermostats.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceThermostats"), nestConnectorProperties.getProperty("deviceIdThermostat"), "viewTargetTemperatureFahrenheit", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/thermostats/" + jo.getString("deviceId") + "/target_temperature_f";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Test case for getServices(viewHumidity) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewHumidity() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForThermostats.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceThermostats"), nestConnectorProperties.getProperty("deviceIdThermostat"), "viewHumidity", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/thermostats/" + jo.getString("deviceId") + "/humidity";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewTemperatureMode) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewTemperatureMode() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForThermostats.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceThermostats"), nestConnectorProperties.getProperty("deviceIdThermostat"), "viewTemperatureMode", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/thermostats/" + jo.getString("deviceId") + "/hvac_mode";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getServices for thermostats.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test with negative case.")
    public void testGetServicesForThermostatsNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForThermostats_Negative.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceThermostats"), nestConnectorProperties.getProperty("deviceIdThermostatInvalid"), "viewCurrentTemperatureFahrenheit", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewCOAlarmState) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewCOAlarmState() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForSmoke_COAlarms.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceSmokeCOAlarms"), nestConnectorProperties.getProperty("smokeCOAlarmDeviceId"), "viewCOAlarmState", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/smoke_co_alarms/" + jo.getString("deviceId") + "/co_alarm_state";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewSmokeAlarmState) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewSmokeAlarmState() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForSmoke_COAlarms.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceSmokeCOAlarms"), nestConnectorProperties.getProperty("smokeCOAlarmDeviceId"), "viewSmokeAlarmState", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/smoke_co_alarms/" + jo.getString("deviceId") + "/smoke_alarm_state";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewBatteryHealth) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewBatteryHealth() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForSmoke_COAlarms.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceSmokeCOAlarms"), nestConnectorProperties.getProperty("smokeCOAlarmDeviceId"), "viewBatteryHealth", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/smoke_co_alarms/" + jo.getString("deviceId") + "/battery_health";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewManualTestState) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewManualTestState() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForSmoke_COAlarms.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceSmokeCOAlarms"), nestConnectorProperties.getProperty("smokeCOAlarmDeviceId"), "viewManualTestState", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/smoke_co_alarms/" + jo.getString("deviceId") + "/is_manual_test_active";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewLastManualTestStatus) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewLastManualTestStatus() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForSmoke_COAlarms.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceSmokeCOAlarms"), nestConnectorProperties.getProperty("smokeCOAlarmDeviceId"), "viewLastManualTestStatus", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/smoke_co_alarms/" + jo.getString("deviceId") + "/ui_color_state";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewLastManualTestTimestamp) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewLastManualTestTimestamp() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForSmoke_COAlarms.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceSmokeCOAlarms"), nestConnectorProperties.getProperty("smokeCOAlarmDeviceId"), "viewLastManualTestTimestamp", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/smoke_co_alarms/" + jo.getString("deviceId") + "/last_manual_test_time";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewOnlineStatus) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewOnlineStatus() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForSmoke_COAlarms.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceSmokeCOAlarms"), nestConnectorProperties.getProperty("smokeCOAlarmDeviceId"), "viewOnlineStatus", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/smoke_co_alarms/" + jo.getString("deviceId") + "/is_online";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewLastConnectionInformation) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewLastConnectionInformation() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForSmoke_COAlarms.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceSmokeCOAlarms"), nestConnectorProperties.getProperty("smokeCOAlarmDeviceId"), "viewLastConnectionInformation", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "devices/smoke_co_alarms/" + jo.getString("deviceId") + "/last_connection";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getServices for smoke_co_alarms.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test with negative case.")
    public void testGetServicesForSmokeCOAlarmsNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForSmoke_COAlarms_Negative.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("deviceSmokeCOAlarms"), nestConnectorProperties.getProperty("smokeCOAlarmDeviceIdInvalid"), "viewOnlineStatus", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewThermostats) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewAllThermostats() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForStructures.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("structureId"), "viewThermostats", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "structures/" + jo.getString("structureId") + "/thermostats";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewSmokeCOAlarms) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewAllSmokeCOAlarms() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForStructures.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("structureId"), "viewSmokeCOAlarms", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "structures/" + jo.getString("structureId") + "/smoke_co_alarms";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewEnergyEventPeekStart) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewEnergyEventPeekstart() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForStructures.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("structureId"), "viewEnergyEventPeekStart", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "structures/" + jo.getString("structureId") + "/peak_period_start_time";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewAwayState) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewAwayState() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForStructures.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("structureId"), "viewAwayState", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "structures/" + jo.getString("structureId") + "/away";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getServices(viewPostalCode) method.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test.")
    public void testViewPostalcode() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForStructures.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("structureId"), "viewPostalCode", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String responseConnector = ConnectorIntegrationUtil.sendRequestString("POST", getProxyServiceURL(methodName), modifiedJsonString);
            String httpMethod = "GET";
            JSONObject jo = new JSONObject(modifiedJsonString);
            String parameters = "structures/" + jo.getString("structureId") + "/postal_code";
            String responseDirect = ConnectorIntegrationUtil.sendRestRequest(httpMethod, parameters);
            log.info("responseConnector\n" + responseConnector);
            log.info("responseDirect\n" + responseDirect);
            Assert.assertTrue(responseConnector.equals(responseDirect));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getServices for structures.
     */
    @Test(groups = {"wso2.esb"}, description = "nest {getServices} integration test with negative case.")
    public void testGetServicesForStructuresNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getServicesForStructures_Negative.txt";
        String methodName = "nest";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("structureIdInvalid"), "viewAwayState", nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Test case for setFanTimer method.
     */
    @Test(groups = {"wso2.esb"}, priority = 2, description = "nest {setFanTimer} integration test.")
    public void setFanTimer() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "setFanTimer.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "nest";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("fanTimerState"), nestConnectorProperties.getProperty("deviceIdThermostat"), nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jo = new JSONObject(modifiedJsonString);
            Assert.assertTrue((responseConnector.has("error") && responseConnector.getString("error").equals("Cannot change fan_timer_active while structure is away")) || (!responseConnector.has("error") && responseConnector.getString("fan_timer_active").equals(jo.getString("fanTimerState").toString())));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for setFanTimer method.
     */
    @Test(groups = {"wso2.esb"}, priority = 2, description = "nest {setFanTimer} negative integration test.")
    public void setFanTimerNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "setFanTimer_Negative.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "nest";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("fanTimerStateInvalid"), nestConnectorProperties.getProperty("deviceIdThermostat"), nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for setTargetTemperature method.
     */
    @Test(groups = {"wso2.esb"}, priority = 2, description = "nest {setTargetTemperature} integration test.")
    public void setTargetTemperature() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "setTargetTemperature.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "nest";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("targetTemperature"), nestConnectorProperties.getProperty("scale"), nestConnectorProperties.getProperty("deviceIdThermostat"), nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jo = new JSONObject(modifiedJsonString);
            if (jo.getString("scale").toString().toLowerCase().equals("f")) {
                Assert.assertTrue((!responseConnector.has("error") && responseConnector.getString("target_temperature_f").equals(jo.getString("targetTemperature").toString())) || (responseConnector.has("error") && responseConnector.getString("error").equals("Cannot change target temperature while structure is away")));
            } else if (jo.getString("scale").toString().toLowerCase().equals("c")) {
                Assert.assertTrue((!responseConnector.has("error") && responseConnector.getString("target_temperature_c").equals(jo.getString("targetTemperature").toString())) || (responseConnector.has("error") && responseConnector.getString("error").equals("Cannot change target temperature while structure is away")));
            }
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for setTargetTemperature method.
     */
    @Test(groups = {"wso2.esb"}, priority = 2, description = "nest {setTargetTemperature} negative integration test.")
    public void setTargetTemperatureNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "setTargetTemperature_Negative.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "nest";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("targetTemperatureInvalid"), nestConnectorProperties.getProperty("scale"), nestConnectorProperties.getProperty("deviceIdThermostat"), nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for setTemperatureMode method.
     */
    @Test(groups = {"wso2.esb"}, priority = 2, description = "nest {setTemperatureMode} integration test.")
    public void setTemperatureMode() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "setTemperatureMode.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "nest";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("temperatureMode"), nestConnectorProperties.getProperty("deviceIdThermostat"), nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jo = new JSONObject(modifiedJsonString);
            Assert.assertTrue(responseConnector.getString("hvac_mode").equals(jo.getString("temperatureMode").toString()));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for setTemperatureMode method.
     */
    @Test(groups = {"wso2.esb"}, priority = 2, description = "nest {setTemperatureMode} negative integration test.")
    public void setTemperatureModeNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "setTemperatureMode_Negative.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "nest";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("temperatureModeInvalid"), nestConnectorProperties.getProperty("deviceIdThermostat"), nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for setAwayState method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"setFanTimer", "setTargetTemperature"}, priority = 2, description = "nest {setAwayState} integration test.")
    public void setAwayState() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "setAwayState.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "nest";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("awayState"), nestConnectorProperties.getProperty("structureId"), nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jo = new JSONObject(modifiedJsonString);
            Assert.assertTrue(responseConnector.getString("away").equals(jo.getString("awayState").toString()));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for setAwayState method.
     */
    @Test(groups = {"wso2.esb"}, priority = 2, description = "nest {setAwayState} negative integration test.")
    public void setAwayStateNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "setAwayState_Negative.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "nest";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("awayStateInvalid"), nestConnectorProperties.getProperty("structureId"), nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for setETA method.
     */
    @Test(groups = {"wso2.esb"}, priority = 2, dependsOnMethods = {"setAwayState"}, description = "nest {setETA} integration test.")
    public void setETA() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "setETA.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "nest";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("tripId"), nestConnectorProperties.getProperty("begin"), nestConnectorProperties.getProperty("end"), nestConnectorProperties.getProperty("structureId"), nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            JSONObject responseConnector = ConnectorIntegrationUtil.sendRequest("POST", getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jo = new JSONObject(modifiedJsonString);
            Assert.assertTrue((responseConnector.has("error") && responseConnector.getString("error").equals("Not in away mode")) || (!responseConnector.has("error") && (responseConnector.getString("trip_id").equals(jo.getString("tripId").toString()) && responseConnector.getString("estimated_arrival_window_begin").equals(jo.getString("begin").toString()) && responseConnector.getString("estimated_arrival_window_end").equals(jo.getString("end").toString()))));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for setETA method.
     */
    @Test(groups = {"wso2.esb"}, priority = 2, description = "nest {setETA} negative integration test.")
    public void setETANegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "setETA_Negative.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "nest";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, nestConnectorProperties.getProperty("tripId"), nestConnectorProperties.getProperty("beginInvalid"), nestConnectorProperties.getProperty("end"), nestConnectorProperties.getProperty("structureId"), nestConnectorProperties.getProperty("accessToken"), nestConnectorProperties.getProperty("apiRedirectUrl"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
}