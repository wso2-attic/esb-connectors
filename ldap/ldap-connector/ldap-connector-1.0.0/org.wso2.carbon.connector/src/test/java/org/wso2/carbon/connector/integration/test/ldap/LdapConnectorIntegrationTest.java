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

package org.wso2.carbon.connector.integration.test.ldap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.json.JSONObject;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;


import javax.naming.Context;
import javax.naming.directory.*;

public class LdapConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private String userBase = null;
    private String testUserId = null;
    private String providerUrl = null;
    private String securityPrincipal = null;
    private String securityCredentials = null;
    private String baseDN = null;
    private int ldapPort = 0;
    private boolean useEmbeddedLDAP = true;

    private InMemoryDirectoryServer ldapServer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("ldap-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        initializeProperties();
        if (useEmbeddedLDAP) {
            initializeEmbeddedLDAPServer();
        }

    }

    private void initializeProperties() {
        userBase = connectorProperties.getProperty("ldapUserBase");
        testUserId = connectorProperties.getProperty("testUserId");
        providerUrl = connectorProperties.getProperty("providerUrl");
        securityPrincipal = connectorProperties.getProperty("securityPrincipal");
        securityCredentials = connectorProperties.getProperty("securityCredentials");
        baseDN = connectorProperties.getProperty("baseDN");
        ldapPort = Integer.parseInt(connectorProperties.getProperty("ldapPort"));
        useEmbeddedLDAP = Boolean.parseBoolean(connectorProperties.getProperty("useEmbeddedLDAP"));
    }

    private void initializeEmbeddedLDAPServer() throws Exception {
        log.info("Creating Embedded LDAP server");
        InMemoryListenerConfig inMemoryListenerConfig = InMemoryListenerConfig.createLDAPConfig("default", ldapPort);
        InMemoryDirectoryServerConfig directoryServerConfig = new InMemoryDirectoryServerConfig(new DN(baseDN));
        directoryServerConfig.setListenerConfigs(inMemoryListenerConfig);
        directoryServerConfig.addAdditionalBindCredentials(securityPrincipal, securityCredentials);
        ldapServer = new InMemoryDirectoryServer(directoryServerConfig);

        ldapServer.startListening();

        Entry wso2Entry = new Entry(baseDN);
        wso2Entry.addAttribute("objectClass", "dcObject");
        wso2Entry.addAttribute("objectClass", "organizationalUnit");
        wso2Entry.addAttribute("ou", "WSO2");
        wso2Entry.addAttribute("dc", "WSO2");

        ldapServer.add(wso2Entry);

        Entry entry = new Entry(userBase);
        entry.addAttribute("objectClass", "organizationalUnit");
        ldapServer.add(entry);
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        if (ldapServer != null) {
            ldapServer.shutDown(true);
        }
        ldapServer = null;
    }

    //negative test case for logging in to LDAP with invalid credentials
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {logging in} integration test with invalid credentials.")
    public void testLoggingInWithInValidCredentials() throws Exception {
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "wrong_admin_credentials_ldap.json");
        int statusCode = esbRestResponse.getHttpStatusCode();
        Assert.assertEquals(statusCode, 500);
    }

    //positive test case for creating LDAP entry with valid parameters
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {createEntry} integration test with mandatory parameters.")
    public void testCreateEntryWithValidParameters() throws Exception {

        try {
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createEntity_ldap_mandatory.json");

            Assert.assertTrue(esbRestResponse.getBody().has("result"));
            JSONObject result = esbRestResponse.getBody().getJSONObject("result");
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getString("message"), "Success");
        } finally {
            deleteSampleEntry();
        }
    }


    //negative test case for creating LDAP entry with missing objectclass
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {createEntry} integration test with with missing objectclass.")
    public void testCreateEntryWithMissingObjectClass() throws Exception {

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createEntity_ldap_missing_objectclass.json");
        int statusCode = esbRestResponse.getHttpStatusCode();
        Assert.assertEquals(statusCode, 500);

    }

    //negative test case for creating LDAP entry with missing dn
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {createEntry} integration test with missing dn.")
    public void testCreateEntryWithMissingDN() throws Exception {
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createEntity_ldap_missing_dn.json");
        int statusCode = esbRestResponse.getHttpStatusCode();
        Assert.assertEquals(statusCode, 500);
    }

    //negative test case for creating LDAP entry with wrong userbase
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {createEntry} integration test with wrong user base.")
    public void testCreateEntryWithWrongUserBase() throws Exception {

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createEntity_ldap_wrong_userbase.json");
        int statusCode = esbRestResponse.getHttpStatusCode();
        Assert.assertEquals(statusCode, 500);
    }

    //negative test case for creating LDAP entry with wrong objectclass
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {createEntry} integration test with wrong objectclass.")
    public void testCreateEntryWithWrongObjectClass() throws Exception {

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createEntity_ldap_wrong_objectclass.json");
        int statusCode = esbRestResponse.getHttpStatusCode();
        Assert.assertEquals(statusCode, 500);
    }

    //negative test case for creating LDAP entry without mandatory attributes
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {createEntry} integration test without mandatory attributes.")
    public void testCreateEntryWithoutMandatoryAttributes() throws Exception {

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createEntity_ldap_without_mandatory_attributes.json");
        int statusCode = esbRestResponse.getHttpStatusCode();
        Assert.assertEquals(statusCode, 500);
    }

    //positive test case for deleting LDAP entry with valid parameters
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {deleteEntry} integration test with mandatory parameters.")
    public void testDeleteEntryWithValidParameters() throws Exception {
        createSampleEntity();

        //deleting created entry

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteEntity_ldap.json");

        Assert.assertTrue(esbRestResponse.getBody().has("result"));
        JSONObject result = esbRestResponse.getBody().getJSONObject("result");
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString("message"), "Success");
    }


    //positive test case for deleting LDAP entry wrong DN
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {deleteEntry} integration test with wrong DN.")
    public void testDeleteEntryWrongDN() throws Exception {
        createSampleEntity();

        try {
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteEntity_ldap_wrong_dn.json");
            int statusCode = esbRestResponse.getHttpStatusCode();
            Assert.assertEquals(statusCode, 500);
        } finally {
            deleteSampleEntry();
        }
    }


    //positive test case for searching a LDAP entry with valid parameters
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {searchEntry} integration test with valid parameters.")
    public void testSearchEntryWithValidParameters() throws Exception {
        createSampleEntity();

        //searching created entry
        try {

            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchEntry_ldap.json");

            JSONObject result = esbRestResponse.getBody().getJSONObject("result");
            Assert.assertNotNull(result);
            JSONObject entry = result.getJSONObject("entry");
            Assert.assertNotNull(entry);
            Assert.assertEquals(entry.getString("uid"), "testDim20");
        } finally {
            //Finally deleting Entry with correct dn
            deleteSampleEntry();
        }
    }

    //negative test case for searching LDAP entry with wrong parameters
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {searchEntry} integration test with wrong parameters.")
    public void testSearchEntryWithWrongParameters() throws Exception {
        createSampleEntity();

        //searching created entry
        try {
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "searchEntry_ldap_wrong_params.json");
            Object result = esbRestResponse.getBody().get("result");
            Assert.assertFalse(result instanceof JSONObject);
        } finally {
            //Finally deleting Entry with correct dn
            deleteSampleEntry();
        }
    }

    //positive test case for updating LDAP entry with valid parameters
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {updateEntry} integration test with valid parameters.")
    public void testUpdateEntryWithValidParameters() throws Exception {
        createSampleEntity();

        //updating created entry
        try {
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateEntry_ldap_valid_params.json");

            Assert.assertTrue(esbRestResponse.getBody().has("result"));
            JSONObject result = esbRestResponse.getBody().getJSONObject("result");
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getString("message"), "Success");
        } finally {
            //Finally deleting Entry with correct dn
            deleteSampleEntry();
        }
    }

    //negative test case for updating LDAP entry with wrong parameters
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {updateEntry} integration test with wrong parameters.")
    public void testUpdateEntryWithWrongParameters() throws Exception {
        createSampleEntity();

        //updating created entry
        try {
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateEntry_ldap_wrong_params.json");
            int statusCode = esbRestResponse.getHttpStatusCode();
            Assert.assertEquals(statusCode, 500);
        } finally {
            //Finally deleting Entry with correct dn
            deleteSampleEntry();
        }
    }


    //positive test case for success LDAP authentication
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {authenticateEntry} integration test with valid parameters.")
    public void testSuccessAuthentication() throws Exception {

        createSampleEntity();

        try {
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "authenticateUser_ldap.json");

            Assert.assertTrue(esbRestResponse.getBody().has("result"));
            JSONObject result = esbRestResponse.getBody().getJSONObject("result");
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getString("message"), "Success");
        } finally {
            //Finally deleting Entry with correct dn
            deleteSampleEntry();
        }
    }


    //negative test case for fail LDAP authentication
    @Test(priority = 1, groups = {"wso2.esb"}, description = "ldap {authenticateEntry} integration test with wrong parameters.")
    public void testFailAuthentication() throws Exception {

        createSampleEntity();

        try {
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "authenticateUser_ldap_wrong_credentials.json");

            Assert.assertTrue(esbRestResponse.getBody().has("result"));
            JSONObject result = esbRestResponse.getBody().getJSONObject("result");
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getString("message"), "Fail");
        } finally {
            //Finally deleting Entry with correct dn
            deleteSampleEntry();
        }
    }

    public void createSampleEntity() throws Exception {

        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");

        env.put(Context.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, securityCredentials);

        DirContext ctx = new InitialDirContext(env);
        Attributes entry = new BasicAttributes();
        Attribute obClassAttr = new BasicAttribute("objectClass");
        obClassAttr.add("inetOrgPerson");
        entry.put(obClassAttr);

        Attribute mailAttr = new BasicAttribute("mail");
        mailAttr.add(testUserId + "@wso2.com");
        entry.put(mailAttr);

        Attribute passAttr = new BasicAttribute("userPassword");
        passAttr.add("12345");
        entry.put(passAttr);

        Attribute snAttr = new BasicAttribute("sn");
        snAttr.add("dim");
        entry.put(snAttr);

        Attribute cnAttr = new BasicAttribute("cn");
        cnAttr.add("dim");
        entry.put(cnAttr);

        String dn = "uid=" + testUserId + "," + userBase;

        ctx.createSubcontext(dn, entry);

    }

    public void deleteSampleEntry() throws Exception {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");

        env.put(Context.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, securityCredentials);

        DirContext ctx = new InitialDirContext(env);
        String dn = "uid=" + testUserId + "," + userBase;
        ctx.destroySubcontext(dn);
    }

}
