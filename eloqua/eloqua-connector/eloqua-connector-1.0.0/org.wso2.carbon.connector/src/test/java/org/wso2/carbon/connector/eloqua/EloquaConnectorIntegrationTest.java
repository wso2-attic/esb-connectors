package org.wso2.carbon.connector.integration.test.eloqua;
/*
Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.wso2.carbon.connector.integration.test.common.Base64Coder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EloquaConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> mpRequestHeadersMap = new HashMap<String, String>();

    private String multipartProxyUrl;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("eloqua-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);

        String authHeader = connectorProperties.getProperty("siteName") + "\\" + connectorProperties.getProperty("username") + ":" + connectorProperties.getProperty("password");
        String encodedAuthorization = Base64Coder.encodeString(authHeader);

        apiRequestHeadersMap.put("Authorization", "Basic " + encodedAuthorization);
        apiRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.put("Accept", "application/json");
        ;
    }

    /**
     * Positive test case for getContactFields method with mandatory parameters.
     */
    @Test(enabled = true, description = "eloqua {getContactFields} integration test with mandatory parameters.")
    public void testGetContactFieldsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "getContactFields";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/fields";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactFields.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactFields method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactFieldsWithMandatoryParameters"}, description = "eloqua {getContactFields} integration test with optional parameters.")
    public void testGetContactFieldsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getContactFields";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "contacts/fields?totalResults=" + connectorProperties.getProperty("totalResults") + "&q=" + connectorProperties.getProperty("q") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactFieldsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactFieldById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactFieldsWithOptionalParameters"}, description = "eloqua {getContactFieldById} integration test with mandatory parameters.")
    public void testGetContactFieldByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactFieldById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/fields/" + connectorProperties.getProperty("contactFieldId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactFieldById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getContactFieldById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactFieldByIdWithMandatoryParameters"}, description = "eloqua {getContactFieldById} integration test with mandatory parameters.")
    public void testGetContactFieldByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getContactFieldById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactFieldByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getContactFilters method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactFieldByIdWithNegativeCase"}, description = "eloqua {getContactFilters} integration test with mandatory parameters.")
    public void testGetContactFiltersWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactFilters";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/filters";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactFilters.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactFilters method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactFiltersWithMandatoryParameters"}, description = "eloqua {getContactFilters} integration test with optional parameters.")
    public void testGetContactFiltersWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getContactFilters";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "contacts/filters?totalResults=" + connectorProperties.getProperty("totalResults") + "&q=" + connectorProperties.getProperty("q") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactFiltersOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactFilterById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactFiltersWithOptionalParameters"}, description = "eloqua {getContactFilterById} integration test with mandatory parameters.")
    public void testGetContactFilterByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactFilterById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/filters/" + connectorProperties.getProperty("contactFilterId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactFilterById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getContactFilterById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactFilterByIdWithMandatoryParameters"}, description = "eloqua {getContactFilterById} integration test with negative case.")
    public void testGetContactFilterByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getContactFilterById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactFilterByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getContactLists method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactFilterByIdWithNegativeCase"}, description = "eloqua {getContactLists} integration test with mandatory parameters.")
    public void testGetContactListsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactLists";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/lists";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactLists.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactLists method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactListsWithMandatoryParameters"}, description = "eloqua {getContactLists} integration test with optional parameters.")
    public void testGetContactListsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getContactLists";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "contacts/lists?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactListsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactListById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactListsWithOptionalParameters"}, description = "eloqua {getContactListById} integration test with mandatory parameters.")
    public void testGetContactListByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactListById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/lists/" + connectorProperties.getProperty("contactListId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactListById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getContactListById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactListByIdWithMandatoryParameters"}, description = "eloqua {getContactListById} integration test with negative case.")
    public void testGetContactListByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getContactListById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactListByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getContactSegments method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactListByIdWithNegativeCase"}, description = "eloqua {getContactSegments} integration test with mandatory parameters.")
    public void testGetContactSegmentsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactSegments";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/segments";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactSegments.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactSegments method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactSegmentsWithMandatoryParameters"}, description = "eloqua {getContactSegments} integration test with optional parameters.")
    public void testGetContactSegmentsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getContactSegments";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "contacts/segments?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactSegmentsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactSegmentById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactSegmentsWithOptionalParameters"}, description = "eloqua {getContactSegmentById} integration test with mandatory parameters.")
    public void testGetContactSegmentByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactSegmentById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/segments/" + connectorProperties.getProperty("contactSegmentId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactSegmentById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getContactSegmentById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactSegmentByIdWithMandatoryParameters"}, description = "eloqua {getContactSegmentById} integration test with mandatory parameters.")
    public void testGetContactSegmentByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getContactSegmentById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactSegmentByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getContactImports method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactSegmentByIdWithNegativeCase"}, description = "eloqua {getContactImports} integration test with mandatory parameters.")
    public void testGetContactImportsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactImports";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/imports";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactImports.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactImports method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactImportsWithMandatoryParameters"}, description = "eloqua {getContactImports} integration test with optional parameters.")
    public void testGetContactImportsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getContactImports";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "contacts/imports?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactImportsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactExports method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactImportsWithOptionalParameters"}, description = "eloqua {getContactExports} integration test with mandatory parameters.")
    public void testGetContactExportsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactExports";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/exports";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactExports.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getContactExports method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactExportsWithMandatoryParameters"}, description = "eloqua {getContactExports} integration test with optional parameters.")
    public void testGetContactExportsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getContactExports";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "contacts/exports?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactExportsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createContactExport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactExportsWithOptionalParameters"}, description = "eloqua {createContactExport} integration test with mandatory parameter.")
    public void testCreateContactExportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createContactExport";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createContactExport.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);

        String exportUri = esbRestResponse.getBody().get("uri").toString();
        String exportId = exportUri.substring(18, exportUri.length());
        connectorProperties.put("contactExportId", exportId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "contacts/exports/" + connectorProperties.getProperty("contactExportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("contactName"));
    }

    /**
     * Positive test case for updateContactExport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateContactExportWithMandatoryParameters"}, description = "eloqua {updateContactExport} integration test with mandatory parameter.")
    public void testUpdateContactExportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "updateContactExport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "updateContactExport.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "contacts/exports/" + connectorProperties.getProperty("contactExportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("updateContactName"));
    }

    /**
     * Positive test case for getContactExportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testUpdateContactExportWithMandatoryParameters"}, description = "eloqua {getContactExportById} integration test with mandatory parameters.")
    public void testGetContactExportByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactExportById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/exports/" + connectorProperties.getProperty("contactExportId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactExportById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getContactExportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactExportByIdWithMandatoryParameters"}, description = "eloqua {getContactExportById} integration test with negative case.")
    public void testGetContactExportByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getContactExportById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactExportByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getContactExportDataById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactExportByIdWithNegativeCase"}, description = "eloqua {getContactExportDataById} integration test with mandatory parameters.")
    public void testGetContactExportDataByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactExportDataById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/exports/" + connectorProperties.getProperty("contactExportId") + "/data";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactExportDataById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getContactExportDataById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactExportDataByIdWithMandatoryParameters"}, description = "eloqua {getContactExportDataById} integration test with negative case.")
    public void testGetContactExportDataByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getContactExportDataById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactExportDataByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for createContactImport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactExportDataByIdWithNegativeCase"}, description = "eloqua {createContactImport} integration test with mandatory parameter.")
    public void testCreateContactImportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createContactImport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createContactImport.json");

        String importUri = esbRestResponse.getBody().get("uri").toString();
        connectorProperties.put("syncedInstanceUri", importUri);
        String importId = importUri.substring(18, importUri.length());
        connectorProperties.put("contactImportId", importId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "contacts/imports/" + connectorProperties.getProperty("contactImportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("contactName"));
    }

    /**
     * Positive test case for createContactImportData method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateContactImportWithMandatoryParameters"}, description = "eloqua {createContactImportData} integration test with mandatory parameter.")
    public void testCreateContactImportDataWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createContactImportData";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createContactImportData.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }

    /**
     * Positive test case for updateContactImport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateContactImportDataWithMandatoryParameters"}, description = "eloqua {updateContactImport} integration test with mandatory parameter.")
    public void testUpdateContactImportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "updateContactImport";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "updateContactImport.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "contacts/imports/" + connectorProperties.getProperty("contactImportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("updateContactName"));
    }


    /**
     * Positive test case for getContactImportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testUpdateContactImportWithMandatoryParameters"}, description = "eloqua {getContactImportById} integration test with mandatory parameters.")
    public void testGetContactImportByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getContactImportById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "contacts/imports/" + connectorProperties.getProperty("contactImportId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactImportById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getContactImportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactImportByIdWithMandatoryParameters"}, description = "eloqua {getContactImportById} integration test with negative case.")
    public void testGetContactImportByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getContactImportById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getContactImportByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getEmailGroups method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetContactImportByIdWithNegativeCase"}, description = "eloqua {getEmailGroups} integration test with mandatory parameters.")
    public void testGetEmailGroupsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getEmailGroups";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "emailGroups/";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getEmailGroups.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getEmailGroups method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetEmailGroupsWithMandatoryParameters"}, description = "eloqua {getEmailGroups} integration test with optional parameters.")
    public void testGetEmailGroupsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getEmailGroups";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "emailGroups/?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getEmailGroupsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getEmailGroupById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetEmailGroupsWithOptionalParameters"}, description = "eloqua {getEmailGroupById} integration test with mandatory parameters.")
    public void testGetEmailGroupByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getEmailGroupById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "emailGroups/" + connectorProperties.getProperty("emailGroupId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getEmailGroupById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getEmailGroupById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetEmailGroupByIdWithMandatoryParameters"}, description = "eloqua {getEmailGroupById} integration test with negative case.")
    public void testGetEmailGroupByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getEmailGroupById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getEmailGroupByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getExports method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetEmailGroupByIdWithNegativeCase"}, description = "eloqua {getExports} integration test with mandatory parameters.")
    public void testGetExportsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getExports";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "exports/";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getExports.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getExports method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetExportsWithMandatoryParameters"}, description = "eloqua {getExports} integration test with optional parameters.")
    public void testGetExportsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getExports";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "exports/?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getExportsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getExportsData method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetExportsWithOptionalParameters"}, description = "eloqua {getExportsData} integration test with mandatory parameters.")
    public void testGetExportsDataWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getExportsData";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "exports/data";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getExportsData.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getImports method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetExportsDataWithMandatoryParameters"}, description = "eloqua {getImports} integration test with mandatory parameters.")
    public void testGetImportsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getImports";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "imports/";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getImports.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getImports method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetImportsWithMandatoryParameters"}, description = "eloqua {getImports} integration test with optional parameters.")
    public void testGetImportsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getImports";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "imports/?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getImportsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getImportsData method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetImportsWithOptionalParameters"}, description = "eloqua {getImportsData} integration test with mandatory parameters.")
    public void testGetImportsDataWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getImportsData";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "imports/data";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getImportsData.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createSync method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetImportsDataWithMandatoryParameters"}, description = "eloqua {createSync} integration test with mandatory parameter.")
    public void testCreateSyncWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createSync";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createSync.json");
        String syncUri = esbRestResponse.getBody().get("uri").toString();
        String syncId = syncUri.substring(7, syncUri.length());
        connectorProperties.put("syncId", syncId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "syncs/" + connectorProperties.getProperty("syncId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        //Assert.assertEquals(apiRestResponse.getBody().get("uri"), connectorProperties.getProperty("syncUri"));
    }

    /**
     * Positive test case for getSyncs method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateSyncWithMandatoryParameters"}, description = "eloqua {getSyncs} integration test with mandatory parameters.")
    public void testGetSyncsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getSyncs";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "syncs/";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getSyncs.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSyncs method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetSyncsWithMandatoryParameters"}, description = "eloqua {getSyncs} integration test with optional parameters.")
    public void testGetSyncsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getSyncs";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "syncs/?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getSyncsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSyncById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetSyncsWithOptionalParameters"}, description = "eloqua {getSyncById} integration test with mandatory parameters.")
    public void testGetSyncByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getSyncById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "syncs/" + connectorProperties.getProperty("syncId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getSyncById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getSyncById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetSyncByIdWithMandatoryParameters"}, description = "eloqua {getSyncById} integration test with negative case.")
    public void testGetSyncByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getSyncById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getSyncByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getSyncsLogsById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetSyncByIdWithNegativeCase"}, description = "eloqua {getSyncsLogsById} integration test with mandatory parameters.")
    public void testGetSyncsLogsByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getSyncsLogsById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "syncs/" + connectorProperties.getProperty("syncId") + "/logs";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getSyncsLogsById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSyncsLogsById method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetSyncsLogsByIdWithMandatoryParameters"}, description = "eloqua {getSyncsLogsById} integration test with optional parameters.")
    public void testGetSyncsLogsByIdWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getSyncsLogsById";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "syncs/" + connectorProperties.getProperty("syncId") + "/logs?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getSyncsLogsByIdOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSyncsRejectsById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetSyncsLogsByIdWithOptionalParameters"}, description = "eloqua {getSyncsRejectsById} integration test with mandatory parameters.")
    public void testGetSyncsRejectsByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getSyncsRejectsById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "syncs/" + connectorProperties.getProperty("syncId") + "/rejects";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getSyncsRejectsById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSyncsRejectsById method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetSyncsRejectsByIdWithMandatoryParameters"}, description = "eloqua {getSyncsRejectsById} integration test with optional parameters.")
    public void testGetSyncsRejectsByIdWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getSyncsRejectsById";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "syncs/" + connectorProperties.getProperty("syncId") + "/rejects?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getSyncsRejectsByIdOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSyncsDataById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetSyncsRejectsByIdWithOptionalParameters"}, description = "eloqua {getSyncsDataById} integration test with mandatory parameters.")
    public void testGetSyncsDataByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getSyncsDataById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getSyncsDataById.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
    }



    /**
     * Positive test case for createAccountExport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetSyncsDataByIdWithMandatoryParameters"}, description = "eloqua {createAccountExport} integration test with mandatory parameter.")
    public void testCreateAccountExportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createAccountExport";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createAccountExport.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String exportUri = esbRestResponse.getBody().get("uri").toString();
        String exportId = exportUri.substring(18, exportUri.length());
        connectorProperties.put("accountExportId", exportId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "accounts/exports/" + connectorProperties.getProperty("accountExportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("accountName"));
    }

    /**
     * Positive test case for updateAccountExport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateAccountExportWithMandatoryParameters"}, description = "eloqua {updateAccountExport} integration test with mandatory parameter.")
    public void testUpdateAccountExportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "updateAccountExport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "updateAccountExport.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "accounts/exports/" + connectorProperties.getProperty("accountExportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("updateAccountName"));
    }

    /**
     * Positive test case for getAccountExportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testUpdateAccountExportWithMandatoryParameters"}, description = "eloqua {getAccountExportById} integration test with mandatory parameters.")
    public void testGetAccountExportByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getAccountExportById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "accounts/exports/" + connectorProperties.getProperty("accountExportId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountExportById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAccountExportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountExportByIdWithMandatoryParameters"}, description = "eloqua {getAccountExportById} integration test with negative case.")
    public void testGetAccountExportByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getAccountExportById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountExportByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getAccountExportDataById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountExportByIdWithNegativeCase"}, description = "eloqua {getAccountExportDataById} integration test with mandatory parameters.")
    public void testGetAccountExportDataByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getAccountExportDataById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "accounts/exports/" + connectorProperties.getProperty("accountExportId") + "/data";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountExportDataById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAccountExportDataById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountExportDataByIdWithMandatoryParameters"}, description = "eloqua {getAccountExportDataById} integration test with negative case.")
    public void testGetAccountExportDataByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getAccountExportDataById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountExportDataByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for createAccountImport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountExportDataByIdWithNegativeCase"}, description = "eloqua {createAccountImport} integration test with mandatory parameter.")
    public void testCreateAccountImportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createAccountImport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createAccountImport.json");
        String importUri = esbRestResponse.getBody().get("uri").toString();
        String importId = importUri.substring(18, importUri.length());
        connectorProperties.put("accountImportId", importId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "accounts/imports/" + connectorProperties.getProperty("accountImportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("accountName"));
    }

    /**
     * Positive test case for createAccountImportData method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateAccountImportWithMandatoryParameters"}, description = "eloqua {createAccountImportData} integration test with mandatory parameter.")
    public void testCreateAccountImportDataWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createAccountImportData";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createAccountImportData.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }

    /**
     * Positive test case for updateAccountImport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateAccountImportDataWithMandatoryParameters"}, description = "eloqua {updateAccountImport} integration test with mandatory parameter.")
    public void testUpdateAccountImportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "updateAccountImport";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "updateAccountImport.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "accounts/imports/" + connectorProperties.getProperty("accountImportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("updateAccountName"));
    }


    /**
     * Positive test case for getAccountImportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testUpdateAccountImportWithMandatoryParameters"}, description = "eloqua {getAccountImportById} integration test with mandatory parameters.")
    public void testGetAccountImportByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getAccountImportById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "accounts/imports/" + connectorProperties.getProperty("accountImportId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountImportById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAccountImportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountImportByIdWithMandatoryParameters"}, description = "eloqua {getAccountImportById} integration test with negative case.")
    public void testGetAccountImportByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getAccountImportById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountImportByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getAccountFields method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountImportByIdWithNegativeCase"}, description = "eloqua {getAccountFields} integration test with mandatory parameters.")
    public void testGetAccountFieldsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getAccountFields";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "accounts/fields";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountFields.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAccountFields method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountFieldsWithMandatoryParameters"}, description = "eloqua {getAccountFields} integration test with optional parameters.")
    public void testGetAccountFieldsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getAccountFields";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "accounts/fields?totalResults=" + connectorProperties.getProperty("totalResults") + "&q=" + connectorProperties.getProperty("q") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountFieldsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAccountFieldById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountFieldsWithOptionalParameters"}, description = "eloqua {getAccountFieldById} integration test with mandatory parameters.")
    public void testGetAccountFieldByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getAccountFieldById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "accounts/fields/" + connectorProperties.getProperty("accountFieldId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountFieldById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAccountFieldById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountFieldByIdWithMandatoryParameters"}, description = "eloqua {getAccountFieldById} integration test with mandatory parameters.")
    public void testGetAccountFieldByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getAccountFieldById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountFieldByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getAccountLists method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountFieldByIdWithNegativeCase"}, description = "eloqua {getAccountLists} integration test with mandatory parameters.")
    public void testGetAccountListsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getAccountLists";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "accounts/lists";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountLists.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAccountLists method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountListsWithMandatoryParameters"}, description = "eloqua {getAccountLists} integration test with optional parameters.")
    public void testGetAccountListsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getAccountLists";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "accounts/lists?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountListsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAccountListById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountListsWithOptionalParameters"}, description = "eloqua {getAccountListById} integration test with mandatory parameters.")
    public void testGetAccountListByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getAccountListById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "accounts/lists/" + connectorProperties.getProperty("accountListId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountListById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAccountListById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountListByIdWithMandatoryParameters"}, description = "eloqua {getAccountListById} integration test with negative case.")
    public void testGetAccountListByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getAccountListById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountListByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getAccountImports method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountListByIdWithNegativeCase"}, description = "eloqua {getAccountImports} integration test with mandatory parameters.")
    public void testGetAccountImportsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getAccountImports";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "accounts/imports";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountImports.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAccountImports method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountImportsWithMandatoryParameters"}, description = "eloqua {getAccountImports} integration test with optional parameters.")
    public void testGetAccountImportsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getAccountImports";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "accounts/imports?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountImportsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAccountExports method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountImportsWithOptionalParameters"}, description = "eloqua {getAccountExports} integration test with mandatory parameters.")
    public void testGetAccountExportsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getAccountExports";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "accounts/exports";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountExports.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAccountExports method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountExportsWithMandatoryParameters"}, description = "eloqua {getAccountExports} integration test with optional parameters.")
    public void testGetAccountExportsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getAccountExports";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "accounts/exports?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAccountExportsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createActivityExport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetAccountExportsWithOptionalParameters"}, description = "eloqua {createActivityExport} integration test with mandatory parameter.")
    public void testCreateActivityExportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createActivityExport";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createActivityExport.json");
        String exportUri = esbRestResponse.getBody().get("uri").toString();
        String exportId = exportUri.substring(20, exportUri.length());
        connectorProperties.put("activityExportId", exportId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "activities/exports/" + connectorProperties.getProperty("activityExportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("activityName"));
    }

    /**
     * Positive test case for updateActivityExport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateActivityExportWithMandatoryParameters"}, description = "eloqua {updateActivityExport} integration test with mandatory parameter.")
    public void testUpdateActivityExportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "updateActivityExport";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "updateActivityExport.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "activities/exports/" + connectorProperties.getProperty("activityExportId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("updateActivityName"));
    }

    /**
     * Positive test case for getActivityExportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testUpdateActivityExportWithMandatoryParameters"}, description = "eloqua {getActivityExportById} integration test with mandatory parameters.")
    public void testGetActivityExportByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getActivityExportById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "activities/exports/" + connectorProperties.getProperty("activityExportId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityExportById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getActivityExportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityExportByIdWithMandatoryParameters"}, description = "eloqua {getActivityExportById} integration test with negative case.")
    public void testGetActivityExportByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getActivityExportById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityExportByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getActivityExportDataById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityExportByIdWithNegativeCase"}, description = "eloqua {getActivityExportDataById} integration test with mandatory parameters.")
    public void testGetActivityExportDataByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getActivityExportDataById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "activities/exports/" + connectorProperties.getProperty("activityExportId") + "/data";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityExportDataById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getActivityExportDataById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityExportDataByIdWithMandatoryParameters"}, description = "eloqua {getActivityExportDataById} integration test with negative case.")
    public void testGetActivityExportDataByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getActivityExportDataById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityExportDataByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for createActivityImport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityExportDataByIdWithNegativeCase"}, description = "eloqua {createActivityImport} integration test with mandatory parameter.")
    public void testCreateActivityImportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createActivityImport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createActivityImport.json");
        String importUri = esbRestResponse.getBody().get("uri").toString();
        String importId = importUri.substring(20, importUri.length());
        connectorProperties.put("activityImportId", importId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "activities/imports/" + connectorProperties.getProperty("activityImportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("activityName"));
    }

    /**
     * Positive test case for createActivityImportData method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateActivityImportWithMandatoryParameters"}, description = "eloqua {createActivityImportData} integration test with mandatory parameter.")
    public void testCreateActivityImportDataWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createActivityImportData";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createActivityImportData.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }

    /**
     * Positive test case for updateActivityImport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateActivityImportDataWithMandatoryParameters"}, description = "eloqua {updateActivityImport} integration test with mandatory parameter.")
    public void testUpdateActivityImportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "updateActivityImport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "updateActivityImport.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "activities/imports/" + connectorProperties.getProperty("activityImportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("updateActivityName"));
    }


    /**
     * Positive test case for getActivityImportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testUpdateActivityImportWithMandatoryParameters"}, description = "eloqua {getActivityImportById} integration test with mandatory parameters.")
    public void testGetActivityImportByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getActivityImportById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "activities/imports/" + connectorProperties.getProperty("activityImportId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityImportById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getActivityImportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityImportByIdWithMandatoryParameters"}, description = "eloqua {getActivityImportById} integration test with negative case.")
    public void testGetActivityImportByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getActivityImportById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityImportByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getActivityImports method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityImportByIdWithNegativeCase"}, description = "eloqua {getActivityImports} integration test with mandatory parameters.")
    public void testGetActivityImportsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getActivityImports";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "activities/imports";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityImports.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getActivityImports method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityImportsWithMandatoryParameters"}, description = "eloqua {getActivityImports} integration test with optional parameters.")
    public void testGetActivityImportsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getActivityImports";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "activities/imports?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityImportsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println(apiRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getActivityTypes method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityImportsWithOptionalParameters"}, description = "eloqua {getActivityTypes} integration test with mandatory parameters.")
    public void testGetActivityTypesWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getActivityTypes";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "activities/types";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityTypes.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getActivityTypes method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityTypesWithMandatoryParameters"}, description = "eloqua {getActivityTypes} integration test with optional parameters.")
    public void testGetActivityTypesWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getActivityTypes";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "activities/types?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityTypesOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getActivityTypeById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityTypesWithOptionalParameters"}, description = "eloqua {getActivityTypeById} integration test with mandatory parameters.")
    public void testGetActivityTypeByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getActivityTypeById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "activities/types/" + connectorProperties.getProperty("activityTypeId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityTypeById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getActivityTypeById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityTypeByIdWithMandatoryParameters"}, description = "eloqua {getActivityTypeById} integration test with negative case.")
    public void testGetActivityTypeByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getActivityTypeById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getActivityTypeByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for createCustomObjectExport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetActivityTypeByIdWithNegativeCase"}, description = "eloqua {createCustomObjectExport} integration test with mandatory parameter.")
    public void testCreateCustomObjectExportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createCustomObjectExport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createCustomObjectExport.json");
        String exportUri = esbRestResponse.getBody().get("uri").toString();
        String exportId = exportUri.substring(25, exportUri.length());
        connectorProperties.put("customObjectExportId", exportId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/exports/" + connectorProperties.getProperty("customObjectExportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("customObjectName"));
    }

    /**
     * Positive test case for updateCustomObjectExport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateCustomObjectExportWithMandatoryParameters"}, description = "eloqua {updateCustomObjectExport} integration test with mandatory parameter.")
    public void testUpdateCustomObjectExportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "updateCustomObjectExport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "updateCustomObjectExport.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/exports/" + connectorProperties.getProperty("customObjectExportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("updateCustomObjectName"));
    }

    /**
     * Positive test case for getCustomObjectExportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testUpdateCustomObjectExportWithMandatoryParameters"}, description = "eloqua {getCustomObjectExportById} integration test with mandatory parameters.")
    public void testGetCustomObjectExportByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getCustomObjectExportById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/exports/" + connectorProperties.getProperty("customObjectExportId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getCustomObjectExportById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getCustomObjectExportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCustomObjectExportByIdWithMandatoryParameters"}, description = "eloqua {getCustomObjectExportById} integration test with negative case.")
    public void testGetCustomObjectExportByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getCustomObjectExportById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getCustomObjectExportByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getCustomObjectExportDataById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCustomObjectExportByIdWithNegativeCase"}, description = "eloqua {getCustomObjectExportDataById} integration test with mandatory parameters.")
    public void testGetCustomObjectExportDataByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getCustomObjectExportDataById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/exports/" + connectorProperties.getProperty("customObjectExportId") + "/data";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getCustomObjectExportDataById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getCustomObjectExportDataById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCustomObjectExportDataByIdWithMandatoryParameters"}, description = "eloqua {getCustomObjectExportDataById} integration test with negative case.")
    public void testGetCustomObjectExportDataByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getCustomObjectExportDataById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getCustomObjectExportDataByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for createCustomObjectImport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCustomObjectExportDataByIdWithNegativeCase"}, description = "eloqua {createCustomObjectImport} integration test with mandatory parameter.")
    public void testCreateCustomObjectImportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createCustomObjectImport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createCustomObjectImport.json");
        String importUri = esbRestResponse.getBody().get("uri").toString();
        String importId = importUri.substring(25, importUri.length());
        connectorProperties.put("customObjectImportId", importId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/imports/" + connectorProperties.getProperty("customObjectImportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("customObjectName"));
    }

    /**
     * Positive test case for createCustomObjectImportData method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateCustomObjectImportWithMandatoryParameters"}, description = "eloqua {createCustomObjectImportData} integration test with mandatory parameter.")
    public void testCreateCustomObjectImportDataWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "createCustomObjectImportData";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createCustomObjectImportData.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }

    /**
     * Positive test case for updateCustomObjectImport method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateCustomObjectImportDataWithMandatoryParameters"}, description = "eloqua {updateCustomObjectImport} integration test with mandatory parameter.")
    public void testUpdateCustomObjectImportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "updateCustomObjectImport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "updateCustomObjectImport.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/imports/" + connectorProperties.getProperty("customObjectImportId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("updateCustomObjectName"));
    }

    /**
     * Positive test case for getCustomObjectImportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testUpdateCustomObjectImportWithMandatoryParameters"}, description = "eloqua {getCustomObjectImportById} integration test with mandatory parameters.")
    public void testGetCustomObjectImportByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getCustomObjectImportById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/imports/" + connectorProperties.getProperty("customObjectImportId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getCustomObjectImportById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getCustomObjectImportById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCustomObjectImportByIdWithMandatoryParameters"}, description = "eloqua {getCustomObjectImportById} integration test with negative case.")
    public void testGetCustomObjectImportByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getCustomObjectImportById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getCustomObjectImportByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getCustomObjects method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCustomObjectImportByIdWithNegativeCase"}, description = "eloqua {getCustomObjects} integration test with mandatory parameters.")
    public void testGetCustomObjectsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getCustomObjects";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "customObjects";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getCustomObjects.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getCustomObjects method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCustomObjectsWithMandatoryParameters"}, description = "eloqua {getCustomObjects} integration test with optional parameters.")
    public void testGetCustomObjectsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getCustomObjects";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "customObjects?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getCustomObjectsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getCustomObjectById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCustomObjectsWithOptionalParameters"}, description = "eloqua {getCustomObjectById} integration test with mandatory parameters.")
    public void testGetCustomObjectByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getCustomObjectById";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getCustomObjectById.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getCustomObjectById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCustomObjectByIdWithMandatoryParameters"}, description = "eloqua {getCustomObjectById} integration test with negative case.")
    public void testGetCustomObjectByIdWithNegativeCase() throws IOException, JSONException {
        String methodName = "getCustomObjectById";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getCustomObjectByIdNegative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getExportsOfCustomObject method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCustomObjectByIdWithNegativeCase"}, description = "eloqua {getExportsOfCustomObject} integration test with mandatory parameters.")
    public void testGetExportsOfCustomObjectWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getExportsOfCustomObject";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/exports";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getExportsOfCustomObject.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getExportsOfCustomObject method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetExportsOfCustomObjectWithMandatoryParameters"}, description = "eloqua {getExportsOfCustomObject} integration test with optional parameters.")
    public void testGetExportsOfCustomObjectWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getExportsOfCustomObject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/exports?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getExportsOfCustomObjectOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getImportsOfCustomObject method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetExportsOfCustomObjectWithOptionalParameters"}, description = "eloqua {getImportsOfCustomObject} integration test with mandatory parameters.")
    public void testGetImportsOfCustomObjectWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getImportsOfCustomObject";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/imports";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getImportsOfCustomObject.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getImportsOfCustomObject method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetImportsOfCustomObjectWithMandatoryParameters"}, description = "eloqua {getImportsOfCustomObject} integration test with optional parameters.")
    public void testGetImportsOfCustomObjectWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getImportsOfCustomObject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/imports?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getImportsOfCustomObjectOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getFieldsOfCustomObject method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetImportsOfCustomObjectWithOptionalParameters"}, description = "eloqua {getFieldsOfCustomObject} integration test with mandatory parameters.")
    public void testGetFieldsOfCustomObjectWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getFieldsOfCustomObject";
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/fields";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getFieldsOfCustomObject.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getFieldsOfCustomObject method with optional parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetFieldsOfCustomObjectWithMandatoryParameters"}, description = "eloqua {getFieldsOfCustomObject} integration test with optional parameters.")
    public void testGetFieldsOfCustomObjectWithOptionalParameters() throws IOException, JSONException {

        String methodName = "getFieldsOfCustomObject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "customObjects/" + connectorProperties.getProperty("customObjectId") + "/fields?totalResults=" + connectorProperties.getProperty("totalResults") +
                        "&orderBy=" + connectorProperties.getProperty("orderByEncoded") + "&offset=" + connectorProperties.getProperty("offset") + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getFieldsOfCustomObjectOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for deleteContactExportData method with optional parameters.
     */
    @Test(enabled = false, dependsOnMethods = {"testGetFieldsOfCustomObjectWithOptionalParameters"}, description = "eloqua {deleteContactExportData} integration test.")
    public void testDeleteContactExportDataWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "deleteContactExportData";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "deleteContactExportData.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }

    /**
     * Positive test case for deleteContactExport method with optional parameters.
     */
    @Test(enabled = false, dependsOnMethods = {"testDeleteContactExportDataWithMandatoryParameters"}, description = "eloqua {deleteContactExport} integration test.")
    public void testDeleteContactExportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "deleteContactExport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "deleteContactExport.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }

    /**
     * Positive test case for deleteContactImportData method with optional parameters.
     */
    @Test(enabled = false, dependsOnMethods = {"testDeleteContactExportWithMandatoryParameters"}, description = "eloqua {deleteContactImportData} integration test.")
    public void testDeleteContactImportDataWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "deleteContactImportData";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "deleteContactImportData.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }

    /**
     * Positive test case for deleteContactImport method with optional parameters.
     */
    @Test(enabled = false, dependsOnMethods = {"testDeleteContactImportDataWithMandatoryParameters"}, description = "eloqua {deleteContactImport} integration test.")
    public void testDeleteContactImportWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "deleteContactImport";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "deleteContactImport.json");
        System.out.println("\n\n\n\n+++++++++++++++++++++++++++++++++\n\n\n\n");
        System.out.println(esbRestResponse.getHttpStatusCode());
        System.out.println("\n\n\n\n+++++++++++++++++++++++++++++++++\n\n\n\n");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }
}

