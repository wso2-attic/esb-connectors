package org.wso2.carbon.connector.integration.test.nimble;

import org.wso2.carbon.esb.ESBIntegrationTest;
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


public class NimbleConnectorIntegrationTest extends ESBIntegrationTest {

    private static final String CONNECTOR_NAME = "nimble";
    private MediationLibraryUploaderStub mediationLibUploadStub = null;
    private MediationLibraryAdminServiceStub adminServiceStub = null;
    private ProxyServiceAdminClient proxyAdmin;
    private String repoLocation = null;
    private String nimbleConnectorFileName = "nimble.zip";
    private Properties nimbleConnectorProperties = null;
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
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, nimbleConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);
        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");
        nimbleConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        pathToProxiesDirectory = repoLocation + nimbleConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + nimbleConnectorProperties.getProperty("requestDirectoryRelativePath");
    }

    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }

    /**
     * Positive test case for getAccessTokenFromRefreshToken method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getAccessTokenFromRefreshToken} integration test with mandatory parameter.")
    public void getAccessTokenFromRefreshToken() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAccessTokenFromRefreshToken.txt";
        String methodName = "getAccessTokenFromRefreshToken";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("access_token"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getAccessTokenFromRefreshToken method with negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getAccessTokenFromRefreshToken} integration test with negative parameter.")
    public void getAccessTokenFromRefreshTokenNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAccessTokenFromRefreshToken_Negative.txt";
        String methodName = "getAccessTokenFromRefreshToken";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getContactNotesList method with optional parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getContactNotesList} integration test with optional parameter.")
    public void getContactNotesListOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactNotesList_Optional.txt";
        String methodName = "getContactNotesList";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("meta"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getContactNotesList method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getContactNotesList} integration test with Negative parameter.")
    public void getContactNotesListNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactNotesList_Negative.txt";
        String methodName = "getContactNotesList";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getContactNotesList method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getContactNotesList} integration test with mandatory parameter.")
    public void getContactNotesList() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactNotesList.txt";
        String methodName = "getContactNotesList";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("meta"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getContactsDetails method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getContactsDetails} integration test with mandatory parameter.")
    public void getContactsDetails() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactsDetails.txt";
        String methodName = "getContactsDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getContactsDetails method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getContactsDetails} integration test with Negative parameter.")
    public void getContactsDetailsNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactsDetails_Negative.txt";
        String methodName = "getContactsDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getContactsDetails method with optional parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getContactsDetails} integration test with optional parameter.")
    public void getContactsDetailsOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactsDetails_Optional.txt";
        String methodName = "getContactsDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("contacts_meta"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getSavedSearch method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getSavedSearch} integration test with mandatory parameter.")
    public void getSavedSearch() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSavedSearch.txt";
        String methodName = "getSavedSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("resources"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSavedSearch method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getSavedSearch} integration test with Negative parameter.")
    public void getSavedSearchNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSavedSearch_Negative.txt";
        String methodName = "getSavedSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getContactsMetadata method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getContactsMetadata} integration test with Negative parameter.")
    public void getContactsMetadataNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactsMetadata_Negative.txt";
        String methodName = "getContactsMetadata";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getContactsMetadata method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getContactsMetadata} integration test with mandatory parameter.")
    public void getContactsMetadata() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getContactsMetadata.txt";
        String methodName = "getContactsMetadata";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("fields"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getListContacts method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getListContacts} integration test with mandatory parameter.")
    public void getListContacts() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getListContacts.txt";
        String methodName = "getListContacts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("meta"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getListContacts method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getListContacts} integration test with Negative parameter.")
    public void getListContactsNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getListContacts_Negative.txt";
        String methodName = "getListContacts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getListContacts method with Optional parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getListContacts} integration test with Optional parameter.")
    public void getListContactsOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getListContacts_Optional.txt";
        String methodName = "getListContacts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("meta"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getListContactsIds method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getListContactsIds} integration test with mandatory parameter.")
    public void getListContactsIds() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getListContactsIds.txt";
        String methodName = "getListContactsIds";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("meta"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getListContactsIds method with Optional parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getListContactsIds} integration test with Optional parameter.")
    public void getListContactsIdsOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getListContactsIds_Optional.txt";
        String methodName = "getListContactsIds";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("meta"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getListContactsIds method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getListContactsIds} integration test with Negative parameter.")
    public void getListContactsIdsNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getListContactsIds_Negative.txt";
        String methodName = "getListContactsIds";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getAdvancedSearch method with optional parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getAdvancedSearch} integration test with optional parameter.")
    public void getAdvancedSearchOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAdvancedSearch_Optional.txt";
        String methodName = "getAdvancedSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("meta"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getAdvancedSearch method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getAdvancedSearch} integration test with mandatory parameter.")
    public void getAdvancedSearch() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAdvancedSearch.txt";
        String methodName = "getAdvancedSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("meta"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getAdvancedSearch method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getAdvancedSearch} integration test with Negative parameter.")
    public void getAdvancedSearchNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAdvancedSearch_Negative.txt";
        String methodName = "getAdvancedSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createContact method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createContact} integration test with mandatory parameter.")
    public void createContact() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createContact.txt";
        String methodName = "createContact";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createContactOptional method with optional parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createContact} integration test with optional parameter.")
    public void createContactOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createContact_Optional.txt";
        String methodName = "createContact";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createNote method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createNote} integration test with mandatory parameter.")
    public void createNote() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createNote.txt";
        String methodName = "createNote";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("note"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for createNote method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createNote} integration test with Negative parameter.")
    public void createNoteNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createNote_Negative.txt";
        String methodName = "createNote";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for createTask method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createTask} integration test with Negative parameter.")
    public void createTaskNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createTask_Negative.txt";
        String methodName = "createTask";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createTask method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createTask} integration test with mandatory parameter.")
    public void createTask() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createTask.txt";
        String methodName = "createTask";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("related_to"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createTask method with optional parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createTask} integration test with mandatory parameter.")
    public void createTaskOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createTask_Optional.txt";
        String methodName = "createTask";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("related_to"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for getSingleNote method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getSingleNote} integration test with mandatory parameter.")
    public void getSingleNote() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSingleNote.txt";
        String methodName = "getSingleNote";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("note"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSingleNote method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {getSingleNote} integration test with Negative parameter.")
    public void getSingleNoteNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSingleNote_Negative.txt";
        String methodName = "getSingleNote";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createField method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createField} integration test with mandatory parameter.")
    public void createField() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createField.txt";
        String methodName = "createField";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("group"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for createField method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createField} integration test with Negative parameter.")
    public void createFieldNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createField_Negative.txt";
        String methodName = "createField";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for createField method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createField} integration test with Negative1 parameter.")
    public void createFieldNegative1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createField_Negative1.txt";
        String methodName = "createField";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for createGroup method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createGroup} integration test with Negative1 parameter.")
    public void createGroupNegative1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createGroup_Negative.txt";
        String methodName = "createGroup";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createGroup method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createGroup} integration test with mandatory parameter.")
    public void createGroup() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createGroup.txt";
        String methodName = "createGroup";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("name"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for createSavedSearch method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createSavedSearch} integration test with mandatory parameter.")
    public void createSavedSearch() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createSavedSearch.txt";
        String methodName = "createSavedSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("query"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for createSavedSearch method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {createSavedSearch} integration test with Negative1 parameter.")
    public void createSavedSearchNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createSavedSearch_Negative.txt";
        String methodName = "createSavedSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for updateSavedSearch method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {updateSavedSearch} integration test with mandatory parameter.")
    public void updateSavedSearch() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "updateSavedSearch.txt";
        String methodName = "updateSavedSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("query"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for updateNote method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {updateNote} integration test with Negative parameter.")
    public void updateNoteNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "updateNote_Negative.txt";
        String methodName = "updateNote";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for updateNote method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {updateNote} integration test with mandatory parameter.")
    public void updateNote() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "updateNote.txt";
        String methodName = "updateNote";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("note_preview"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for deleteNote method with mandatory parameters.
     */
    @Test(enabled=false, groups = {"wso2.esb"}, description = "nimble {deleteNote} integration test with mandatory parameter.")
    public void deleteSingleNote() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "deleteSingleNote.txt";
        String methodName = "deleteSingleNote";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for deleteContact method with mandatory parameters.
     */
    @Test(enabled=false, groups = {"wso2.esb"}, description = "nimble {deleteContact} integration test with mandatory parameter.")
    public void deleteContact() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "deleteContact.txt";
        String methodName = "deleteContact";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for deleteField method with mandatory parameters.
     */
    @Test(enabled=false, groups = {"wso2.esb"}, description = "nimble {deleteField} integration test with mandatory parameter.")
    public void deleteField() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "deleteField.txt";
        String methodName = "deleteField";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for deleteGroup method with mandatory parameters.
     */
    @Test(enabled=false, groups = {"wso2.esb"}, description = "nimble {deleteGroup} integration test with mandatory parameter.")
    public void deleteGroup() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "deleteGroup.txt";
        String methodName = "deleteGroup";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for updateGroup method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {updateGroup} integration test with mandatory parameter.")
    public void updateGroup() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "updateGroup.txt";
        String methodName = "updateGroup";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("name"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for updateGroup method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {updateGroup} integration test with Negative parameter.")
    public void updateGroupNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "updateGroup_Negative.txt";
        String methodName = "updateGroup";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for updateField method with mandatory parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {updateField} integration test with mandatory parameter.")
    public void updateField() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "updateField.txt";
        String methodName = "updateField";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(jsonResponse.has("group"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for updateField method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {updateField} integration test with Negative parameter.")
    public void updateFieldNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "updateField_Negative.txt";
        String methodName = "updateField";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for updateContact method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {updateContact} integration test with Negative parameter.")
    public void updateContactNegative1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "updateContactNegative1.txt";
        String methodName = "updateContact";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for updateContact method with Negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "nimble {updateContact} integration test with Negative parameter.")
    public void updateContactNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "updateContact_Negative.txt";
        String methodName = "updateContact";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 409);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for deleteSavedSearch method with mandatory parameters.
     */
    @Test(enabled=false, groups = {"wso2.esb"}, description = "nimble {deleteSavedSearch} integration test with mandatory parameter.")
    public void deleteSavedSearch() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "deleteSavedSearch.txt";
        String methodName = "deleteSavedSearch";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for advancedDeleteContact method with mandatory parameters.
     */
    @Test(enabled=false, groups = {"wso2.esb"}, description = "nimble {advancedDeleteContact} integration test with mandatory parameter.")
    public void advancedDeleteContact() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "advancedDeleteContact.txt";
        String methodName = "advancedDeleteContact";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

}