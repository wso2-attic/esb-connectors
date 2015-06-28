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

package org.wso2.carbon.connector.integration.test.freeagent;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class FreeagentConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("freeagent-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        apiRequestHeadersMap.put("Accept", "application/json");
        
    }
    
    /**
     * Positive test case for createContact method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "freeagent {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("contact");

        String url = esbResponseObject.getString("url");
        String contactId = getEntity(url);
        connectorProperties.setProperty("contactId", contactId);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/contacts/" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("contact");

        Assert.assertEquals(apiResponseObject.getString("first_name"), connectorProperties.get("firstName"));
        Assert.assertEquals(apiResponseObject.getString("last_name"), connectorProperties.get("lastName"));

    }

    /**
     * Positive test case for createContact method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "freeagent {createContact} integration test with optional parameters.")
    public void testCreateContactWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("contact");

        String url = esbResponseObject.getString("url");

        String contactId = getEntity(url);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/contacts/" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("contact");

        Assert.assertEquals(apiResponseObject.getString("first_name"), connectorProperties.get("firstName"));
        Assert.assertEquals(apiResponseObject.getString("last_name"), connectorProperties.get("lastName"));
        Assert.assertEquals(apiResponseObject.getString("organisation_name"),
                connectorProperties.get("organisationName"));
        Assert.assertEquals(apiResponseObject.getString("email"), connectorProperties.get("email"));

    }

    /**
     * Negative test case for createContact method .
     */
    @Test(groups = { "wso2.esb" }, description = "freeagent {createContact} integration test with negative case.")
    public void testCreateContactWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");

        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("errors");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/contacts/";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createContact_negative.json");

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("errors");

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());

        for (int i = 0; i < esbResponseArray.length(); i++) {
            Assert.assertEquals(apiResponseArray.getJSONObject(i).getString("message"),
                    esbResponseArray.getJSONObject(i).getString("message"));
            if(i==1){
                break;
            }
        }

    }

    /**
     * Positive test case for getContactById method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {getContactById} integration test with mandatory parameters.")
    public void testGetContactByIdWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getContactById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactById_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("contact");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/contacts/"
                        + connectorProperties.getProperty("contactId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("contact");

        Assert.assertEquals(apiResponseObject.getString("first_name"), esbResponseObject.getString("first_name"));
        Assert.assertEquals(apiResponseObject.getString("last_name"), esbResponseObject.getString("last_name"));
        Assert.assertEquals(apiResponseObject.getString("created_at"), esbResponseObject.getString("created_at"));
        Assert.assertEquals(apiResponseObject.getString("status"), esbResponseObject.getString("status"));

    }

    /**
     * Positive test case for listContacts method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateContactWithMandatoryParameters", "testCreateContactWithOptionalParameters" }, groups = { "wso2.esb" }, description = "freeagent {listContacts} integration test with mandatory parameters.")
    public void testListContactsWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");

        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("contacts");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/contacts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("contacts");

        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("url"), esbResponseArray.getJSONObject(0)
                .getString("url"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("country"), esbResponseArray.getJSONObject(0)
                .getString("country"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("status"), esbResponseArray.getJSONObject(0)
                .getString("status"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("created_at"), esbResponseArray
                .getJSONObject(0).getString("created_at"));

    }

    /**
     * Positive test case for listContacts method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateContactWithMandatoryParameters", "testCreateContactWithOptionalParameters" }, groups = { "wso2.esb" }, description = "freeagent {listContacts} integration test with optional parameters.")
    public void testListContactsWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json");

        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("contacts");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/contacts?view=active&sort=created_at";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("contacts");

        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("url"), esbResponseArray.getJSONObject(0)
                .getString("url"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("country"), esbResponseArray.getJSONObject(0)
                .getString("country"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("status"), esbResponseArray.getJSONObject(0)
                .getString("status"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("created_at"), esbResponseArray
                .getJSONObject(0).getString("created_at"));

    }

    /**
     * Negative test case for listContacts method.
     */
    @Test(groups = { "wso2.esb" }, description = "freeagent {listContacts} integration test with negative case.")
    public void testListContactsWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_negative.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("errors");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/contacts?view=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("errors");

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseObject.getJSONObject("error").getString("message"), esbResponseObject
                .getJSONObject("error").getString("message"));

    }

    /**
     * Positive test case for createProject method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {createProject} integration test with mandatory parameters.")
    public void testCreateProjectWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("project");

        String url = esbResponseObject.get("url").toString();
        String projectId = getEntity(url);
        connectorProperties.setProperty("projectId", projectId);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/projects/" + projectId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("project");

        Assert.assertEquals(apiResponseObject.get("name").toString(), connectorProperties.get("projectName"));
        Assert.assertEquals(apiResponseObject.get("budget_units").toString(),
                connectorProperties.get("ProjectBudgetUnit"));
        Assert.assertEquals(apiResponseObject.get("currency").toString(), connectorProperties.get("currency"));

    }

    /**
     * Positive test case for createProject method with Optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {createProject} integration test with optional parameters.")
    public void testCreateProjectWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_optional.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("project");

        String url = esbResponseObject.get("url").toString();
        String projectId = getEntity(url);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/projects/" + projectId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("project");

        Assert.assertEquals(apiResponseObject.get("name").toString(), connectorProperties.get("projectNameOptional"));
        Assert.assertEquals(apiResponseObject.get("budget").toString(), connectorProperties.get("projectBudget"));
        Assert.assertEquals(apiResponseObject.get("normal_billing_rate").toString(),
                connectorProperties.get("ProjectNormalBillingRate"));
        Assert.assertEquals(apiResponseObject.get("hours_per_day").toString(),
                connectorProperties.get("projectHoursPerDay"));

    }

    /**
     * Test createProject method with Negative Case.
     */
    @Test(description = "freeagent {createProject} integration test with  negative case.")
    public void testCreateProjectWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_negative.json");

        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/v2/projects";

        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createProject_negative.json");

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"),
                esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"));

    }

    /**
     * Positive test case for getProjectById method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {getProjectById} integration test with mandatory parameters.")
    public void testGetProjectByIdWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getProjectById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProjectById_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("project");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/projects/"
                        + connectorProperties.getProperty("projectId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("project");

        Assert.assertEquals(esbResponseObject.get("url").toString(), apiResponseObject.get("url").toString());
        Assert.assertEquals(esbResponseObject.get("name").toString(), apiResponseObject.get("name").toString());
        Assert.assertEquals(esbResponseObject.get("created_at").toString(), apiResponseObject.get("created_at")
                .toString());

    }

    /**
     * Test listProjects method with Mandatory Parameters.
     */
    @Test(dependsOnMethods = { "testCreateProjectWithMandatoryParameters", "testCreateProjectWithOptionalParameters" }, description = "freeagent {listProjects} integration test with mandatory parameters.")
    public void testListProjectsWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("projects");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/projects";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("projects");

        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("url"), esbResponseArray.getJSONObject(0)
                .getString("url"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("name"), esbResponseArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("created_at"), esbResponseArray
                .getJSONObject(0).getString("created_at"));

    }

    /**
     * Test listProjects method with Optional Parameters.
     */
    @Test(dependsOnMethods = { "testCreateProjectWithMandatoryParameters", "testCreateProjectWithOptionalParameters" }, description = "freeagent {listProjects} integration test with optional parameters.")
    public void testListProjectsWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_optional.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("projects");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/projects?contact="
                        + connectorProperties.getProperty("contactId") + "&view="
                        + connectorProperties.getProperty("projectView");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("projects");

        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("url"), esbResponseArray.getJSONObject(0)
                .getString("url"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("status"), esbResponseArray.getJSONObject(0)
                .getString("status"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("contact"), esbResponseArray.getJSONObject(0)
                .getString("contact"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("created_at"), esbResponseArray
                .getJSONObject(0).getString("created_at"));

    }

    /**
     * Test listProjects method with Negative Case.
     */
    @Test(description = "freeagent {listProjects} integration test with  negative case.")
    public void testListProjectsWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProject_negative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/projects?view=INVALID";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONObject("errors").getJSONObject("error").getString("message"),
                esbRestResponse.getBody().getJSONObject("errors").getJSONObject("error").getString("message"));

    }

    /**
     * Positive test case for createInvoice method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {createInvoice} integration test with mandatory parameters.")
    public void testCreateInvoiceWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("invoice");

        String url = esbResponseObject.get("url").toString();
        String invoiceId = getEntity(url);
        connectorProperties.setProperty("invoiceId", invoiceId);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/invoices/" + invoiceId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("invoice");

        Assert.assertEquals(apiResponseObject.get("dated_on").toString(), connectorProperties.get("invoiceDatedOn"));
        Assert.assertEquals(apiResponseObject.get("created_at").toString(), esbResponseObject.get("created_at"));
        Assert.assertEquals(apiResponseObject.get("payment_terms_in_days").toString(),
                connectorProperties.get("invoicePaymentTermsInDays"));

    }

    /**
     * Positive test case for createInvoice method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {createInvoice} integration test with optional parameters.")
    public void testCreateInvoiceWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_optional.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("invoice");

        String url = esbResponseObject.get("url").toString();
        String invoiceId = getEntity(url);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/invoices/" + invoiceId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("invoice");

        Assert.assertEquals(apiResponseObject.get("currency").toString(), connectorProperties.get("currency"));
        Assert.assertEquals(apiResponseObject.get("discount_percent").toString(),
                connectorProperties.get("invoiceDiscountPercent"));

    }

    /**
     * Test createInvoice method with Negative Case.
     */
    @Test(description = "freeagent {createInvoice} integration test with  negative case.")
    public void testCreateInvoiceWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_negative.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/invoices";

        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoice_negative.json");

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"),
                esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"));

    }

    /**
     * Test listInvoices method with Mandatory Parameters.
     */
    @Test(dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters", "testCreateInvoiceWithOptionalParameters" }, description = "freeagent {listInvoices} integration test with mandatory parameters.")
    public void testListInvoicesWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("invoices");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/invoices";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("invoices");

        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("url"), esbResponseArray.getJSONObject(0)
                .getString("url"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("contact"), esbResponseArray.getJSONObject(0)
                .getString("contact"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("created_at"), esbResponseArray
                .getJSONObject(0).getString("created_at"));

    }

    /**
     * Test listInvoices method with Optional Parameters.
     */
    @Test(dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters", "testCreateInvoiceWithOptionalParameters" }, description = "freeagent {listInvoices} integration test with optional parameters.")
    public void testListInvoicesWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_optional.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("invoices");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/invoices?sort="
                        + connectorProperties.getProperty("invoiceSort") + "&view="
                        + connectorProperties.getProperty("invoiceView");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("invoices");

        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("url"), esbResponseArray.getJSONObject(0)
                .getString("url"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("contact"), esbResponseArray.getJSONObject(0)
                .getString("contact"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("created_at"), esbResponseArray
                .getJSONObject(0).getString("created_at"));

    }

    /**
     * Test listInvoices method with Negative Case.
     */
    @Test(description = "freeagent {listInvoices} integration test with  negative case.")
    public void testListInvoicesWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_negative.json");

        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/v2/invoices?sort=INVALID";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONObject("errors").getJSONObject("error").getString("message"),
                esbRestResponse.getBody().getJSONObject("errors").getJSONObject("error").getString("message"));

    }

    /**
     * Positive test case for getInvoiceById method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {getInvoiceById} integration test with mandatory parameters.")
    public void testGetInvoiceByIdWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getInvoiceById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoiceById_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("invoice");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/invoices/"
                        + connectorProperties.getProperty("invoiceId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("invoice");

        Assert.assertEquals(esbResponseObject.get("url").toString(), apiResponseObject.get("url").toString());
        Assert.assertEquals(esbResponseObject.get("dated_on").toString(), apiResponseObject.get("dated_on").toString());
        Assert.assertEquals(esbResponseObject.get("created_at").toString(), apiResponseObject.get("created_at")
                .toString());

    }

    /**
     * Positive test case for createTask method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {createTask} integration test with mandatory parameters.")
    public void testCreateTaskWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("task");

        String url = esbResponseObject.getString("url");
        String taskId = getEntity(url);
        connectorProperties.setProperty("taskId", taskId);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/tasks/" + taskId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("task");

        Assert.assertEquals(apiResponseObject.getString("name"), esbResponseObject.getString("name"));
        Assert.assertEquals(apiResponseObject.get("url").toString(), esbResponseObject.getString("url"));
        Assert.assertEquals(apiResponseObject.get("is_billable").toString(), esbResponseObject.getString("is_billable"));

    }

    /**
     * Positive test case for createTask method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {createTask} integration test with optional parameters.")
    public void testCreateTaskWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("task");

        String url = esbResponseObject.getString("url");
        String taskId = getEntity(url);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/tasks/" + taskId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("task");

        Assert.assertEquals(apiResponseObject.getString("name"), esbResponseObject.getString("name"));
        Assert.assertEquals(apiResponseObject.get("url").toString(), esbResponseObject.getString("url"));
        Assert.assertEquals(apiResponseObject.get("is_billable").toString(), esbResponseObject.getString("is_billable"));

    }
    
    /**
     * Negative test case for createTask method.
     */
    @Test(groups = { "wso2.esb" }, description = "freeagent {createTask} integration test with negative case.")
    public void testCreateTaskWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/tasks?project="
                        + connectorProperties.getProperty("projectId");

        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTask_negative.json");

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"),
                esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"));

    }
    
    /**
     * Positive test case for listTasks method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithOptionalParameters",
            "testCreateTaskWithOptionalParameters" }, description = "freeagent {listTasks} integration test with mandatory parameters.")
    public void testListTasksWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_mandatory.json");

        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("tasks");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/tasks?project="
                        + connectorProperties.getProperty("projectId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("tasks");

        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("url"), apiResponseArray.getJSONObject(0)
                .getString("url"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("created_at"), apiResponseArray
                .getJSONObject(0).getString("created_at"));

    }

    /**
     * Positive test case for getTaskById method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithMandatoryParameters" }, description = "freeagent {getTaskById} integration test with mandatory parameters.")
    public void testGetTaskByIdWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTaskById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTaskById_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("task");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/tasks/" + connectorProperties.getProperty("taskId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("task");

        Assert.assertEquals(esbResponseObject.getString("url"), apiResponseObject.getString("url"));
        Assert.assertEquals(esbResponseObject.getString("name"), apiResponseObject.getString("name"));
        Assert.assertEquals(esbResponseObject.getString("created_at"), apiResponseObject.getString("created_at"));

    }

    /**
     * Positive test case for listUsers method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "freeagent {urn:listUsers} integration test with mandatory parameters.")
    public void testListUsersWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_mandatory.json");

        JSONArray esbArray = esbRestResponse.getBody().getJSONArray("users");

        String url = esbArray.getJSONObject(0).getString("url");
        String userId = getEntity(url);
        connectorProperties.setProperty("userId", userId);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/users";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiArray = apiRestResponse.getBody().getJSONArray("users");

        Assert.assertEquals(esbArray.getJSONObject(0).getString("url"), apiArray.getJSONObject(0).getString("url"));
        Assert.assertEquals(esbArray.getJSONObject(0).getString("first_name"),
                apiArray.getJSONObject(0).getString("first_name"));
        Assert.assertEquals(esbArray.getJSONObject(0).getString("created_at"),
                apiArray.getJSONObject(0).getString("created_at"));

    }

    /**
     * Positive test case for getUserById method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListUsersWithMandatoryParameters" }, description = "freeagent {urn:getUserById} integration test with mandatory parameters.")
    public void testGetUserByIdWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getUserById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserById_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("user");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/users/" + connectorProperties.getProperty("userId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("user");

        Assert.assertEquals(esbResponseObject.getString("url"), apiResponseObject.getString("url"));
        Assert.assertEquals(esbResponseObject.getString("first_name"), apiResponseObject.getString("first_name"));
        Assert.assertEquals(esbResponseObject.getString("created_at"), apiResponseObject.getString("created_at"));

    }

    /**
     * Positive test case for createTimeSlip method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateProjectWithMandatoryParameters", "testCreateTaskWithMandatoryParameters",
            "testListUsersWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {createTimeSlip} integration test with mandatory parameters.")
    public void testCreateTimeSlipWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createTimeSlip");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTimeSlip_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("timeslip");

        String url = esbResponseObject.getString("url");

        String timeSlipId = getEntity(url);

        connectorProperties.setProperty("timeSlipId", timeSlipId);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/timeslips/" + timeSlipId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("timeslip");

        Assert.assertEquals(getEntity(apiResponseObject.getString("project")), connectorProperties.get("projectId"));
        Assert.assertEquals(getEntity(apiResponseObject.getString("task")), connectorProperties.get("taskId"));
        Assert.assertEquals(getEntity(apiResponseObject.getString("user")), connectorProperties.get("userId"));
        Assert.assertEquals(apiResponseObject.getString("dated_on"), connectorProperties.get("datedOn"));

    }

    /**
     * Negative test case for createTimeSlip method.
     */
    @Test(groups = { "wso2.esb" }, description = "freeagent {createTimeSlip} integration test with negative case.")
    public void testCreateTimeSlipWithNegative() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createTimeSlip");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTimeSlip_negative.json");

        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("errors");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/timeslips";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTimeSlip_negative.json");

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("errors");

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("message"), esbResponseArray.getJSONObject(0)
                .getString("message"));

    }

    /**
     * Positive test case for getTimeSlipById method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateTimeSlipWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {getTimeSlipById} integration test with mandatory parameters.")
    public void testGetTimeSlipByIdWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTimeSlipById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTimeSlipById_mandatory.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("timeslip");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/timeslips/"
                        + connectorProperties.getProperty("timeSlipId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("timeslip");

        Assert.assertEquals(apiResponseObject.getString("project"), esbResponseObject.getString("project"));
        Assert.assertEquals(apiResponseObject.getString("task"), esbResponseObject.getString("task"));
        Assert.assertEquals(apiResponseObject.getString("user"), esbResponseObject.getString("user"));
        Assert.assertEquals(apiResponseObject.getString("dated_on"), esbResponseObject.getString("dated_on"));

    }

    /**
     * Positive test case for listTimeSlips method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateTimeSlipWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {listTimeSlips} integration test with mandatory parameters.")
    public void testListTimeSlipsWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listTimeSlips");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeSlips_mandatory.json");

        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("timeslips");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/timeslips";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("timeslips");

        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("project"), esbResponseArray.getJSONObject(0)
                .getString("project"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("task"), esbResponseArray.getJSONObject(0)
                .getString("task"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("user"), esbResponseArray.getJSONObject(0)
                .getString("user"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("dated_on"), esbResponseArray.getJSONObject(0)
                .getString("dated_on"));

    }

    /**
     * Positive test case for listTimeSlips method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateTimeSlipWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "freeagent {listTimeSlips} integration test with optional parameters.")
    public void testListTimeSlipsWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listTimeSlips");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeSlips_optional.json");

        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("timeslips");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/timeslips?task="
                        + connectorProperties.getProperty("taskId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("timeslips");

        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("project"), esbResponseArray.getJSONObject(0)
                .getString("project"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("task"), esbResponseArray.getJSONObject(0)
                .getString("task"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("user"), esbResponseArray.getJSONObject(0)
                .getString("user"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("dated_on"), esbResponseArray.getJSONObject(0)
                .getString("dated_on"));

    }

    /**
     * Negative test case for listTimeSlips method.
     */
    @Test(groups = { "wso2.esb" }, description = "freeagent {listTimeSlips} integration test with  negative case.")
    public void testListTimeSlipsWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:listTimeSlips");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeSlips_negative.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("errors");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/timeslips?from_date=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("errors");

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseObject.getJSONObject("error").getString("message"), esbResponseObject
                .getJSONObject("error").getString("message"));

    }
    
    /**
     * This method is used to get the entity id by passing url.
     * 
     * @param url This is the entity url.
     * @return String This returns the url entity id.
     */
    private String getEntity(final String url) {
    
        String[] bits = url.split("/");
        String id = bits[bits.length - 1];
        
        return id;
    }
    
}
