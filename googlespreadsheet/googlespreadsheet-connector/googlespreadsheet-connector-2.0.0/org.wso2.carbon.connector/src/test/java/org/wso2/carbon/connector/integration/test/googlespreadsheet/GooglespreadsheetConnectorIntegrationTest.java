package org.wso2.carbon.connector.integration.test.googlespreadsheet;

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
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.xpath.*;

import org.apache.axiom.om.OMElement;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.xml.sax.SAXException;

public class GooglespreadsheetConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    private String title;
    private String apiEndpointUrl;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init("googlespreadsheet-connector-2.0.0");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.put("Content-Type", "application/json");
        apiEndpointUrl = "https://www.googleapis.com/oauth2/v3/token?grant_type=refresh_token&client_id=" + connectorProperties.getProperty("clientId") +
                "&client_secret=" + connectorProperties.getProperty("clientSecret") + "&refresh_token=" + connectorProperties.getProperty("refreshToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpointUrl, "POST", apiRequestHeadersMap);
        final String accessToken = apiRestResponse.getBody().getString("access_token");
        connectorProperties.put("accessToken", accessToken);
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
    }

    /**
     * Positive test case for getAllSpreadsheets method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {getAllSpreadsheets} integration test with mandatory parameters.")
    public void testGetAllSpreadsheetsWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:getAllSpreadsheets");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllSpreadsheetsMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/spreadsheets/private/full";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("count(//feed/entry)", esbRestResponse.getBody()),
                getValueByExpression("count(//feed/entry)", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//feed/entry[1]/id", esbRestResponse.getBody()),
                getValueByExpression("//feed/entry[1]/id", apiRestResponse.getBody()));
    }

    /**
     * Positive test case for getSpreadsheetByTilte method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {getSpreadsheetByTilte} integration test with mandatory parameters.")
    public void testGetSpreadsheetByTilteWithMandatoryParameters() throws IOException,
            XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:getSpreadsheetByTilte");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSpreadsheetByTilteMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") +
                "/feeds/spreadsheets/private/full?title=" +
                connectorProperties.getProperty("spreadsheetTitle");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//feed/entry[1]", esbRestResponse.getBody()),
                getValueByExpression("//feed/entry[1]", apiRestResponse.getBody()));
    }

    /**
     * Positive test case for exportCSVFile method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {exportCSVFile} integration test with mandatory parameters.")
    public void testExportCSVFileWithMandatoryParameters() throws IOException,
            XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:exportCSVFile");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "exportCSVFileMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/download/spreadsheets/Export?key=" +
                connectorProperties.getProperty("key") + "&exportFormat=" +
                connectorProperties.getProperty("exportFormat");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 302);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listWorksheets method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {listWorksheets} integration test with mandatory parameters.")
    public void testListWorksheetsWithMandatoryParameters() throws IOException,
            XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:listWorksheets");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "listWorksheetsMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/worksheets/" +
                connectorProperties.getProperty("key") + "/private/full";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for listWorksheets method.
     */
    @Test(priority = 1, description = "googlespreadsheet {listWorksheets} integration test with negative cases.")
    public void testListWorksheetsWithNegativeCase() throws IOException, XMLStreamException {
        esbRequestHeadersMap.put("Action", "urn:listWorksheets");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "listWorksheetsNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/worksheets/" + "" + "/private/full";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for getWorksheetByTitle method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {getWorksheetByTitle} integration test with mandatory parameters.")
    public void testGetWorksheetByTitleWithMandatoryParameters() throws IOException,
            XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:getWorksheetByTitle");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getWorksheetByTitleMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/worksheets/" +
                connectorProperties.getProperty("key") + "/private/full?title=" +
                connectorProperties.getProperty("worksheetTitle");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getWorksheetByTitle method.
     */
    @Test(priority = 1, description = "googlespreadsheet {getWorksheetByTitle} integration test with negative cases.")
    public void testGetWorksheetByTitleWithNegativeCase() throws IOException, XMLStreamException {
        esbRequestHeadersMap.put("Action", "urn:getWorksheetByTitle");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getWorksheetByTitleNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/worksheets/" +
                connectorProperties.getProperty("key") + "/private/full?title=";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for addWorksheet method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {addWorksheet} integration test with mandatory parameters.")
    public void testAddWorksheetWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:addWorksheet");
        RestResponse<OMElement> esbRestResponse = sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "addWorksheetMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/worksheets/" +
                connectorProperties.getProperty("key") + "/private/full?title=" +
                connectorProperties.getProperty("title");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for deleteWorksheet method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {deleteWorksheet} integration test with mandatory parameters.")
    public void testDeleteWorksheetWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:deleteWorksheet");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteWorksheetMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/worksheets/" +
                connectorProperties.getProperty("key") + "/" + "/private/full/" +
                connectorProperties.getProperty("deleteWorksheetId");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for listRows method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {listRows} integration test with mandatory parameters.")
    public void testListRowsWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:listRows");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "listRowsMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/list/" +
                connectorProperties.getProperty("key") + "/" + connectorProperties.getProperty("worksheetId") +
                "/private/full";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for listRows method.
     */
    @Test(priority = 1, description = "googlespreadsheet {listRows} integration test with negative cases.")
    public void testListRowsWithNegativeCase() throws IOException, XMLStreamException {
        esbRequestHeadersMap.put("Action", "urn:listRows");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "listRowsNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/list/" + "/" +
                connectorProperties.getProperty("worksheetId") + "/private/full";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for listCells method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {listCells} integration test with mandatory parameters.")
    public void testListCellsWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:listCells");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "listcellsMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/cells/" +
                connectorProperties.getProperty("key") + "/" + connectorProperties.getProperty("worksheetId") +
                "/private/full";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for listCells method.
     */
    @Test(priority = 1, description = "googlespreadsheet {listCells} integration test with negative cases.")
    public void testListCellsWithNegativeCase() throws IOException, XMLStreamException {
        esbRequestHeadersMap.put("Action", "urn:listCells");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "listCellsNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/cells/" +
                "/" + connectorProperties.getProperty("worksheetId") + "/private/full";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for getRow method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {getRow} integration test with mandatory parameters.")
    public void testGetRowWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:getRow");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getRowMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/list/" +
                connectorProperties.getProperty("key") + "/" + connectorProperties.getProperty("worksheetId") +
                "/private/full/" + connectorProperties.getProperty("rowId");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getRow method.
     */
    @Test(priority = 1, description = "googlespreadsheet {getRow} integration test with negative cases.")
    public void testGetRowWithNegativeCase() throws IOException, SAXException, ParserConfigurationException,
            XPathExpressionException, XMLStreamException {
        esbRequestHeadersMap.put("Action", "urn:getRow");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getRowNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/list/" + "/" +
                connectorProperties.getProperty("worksheetId") + "/private/full" +
                connectorProperties.getProperty("rowId");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for getCell method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {getCell} integration test with mandatory parameters.")
    public void testGetCellWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:getCell");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCellMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/cells/" +
                connectorProperties.getProperty("key") + "/" + connectorProperties.getProperty("worksheetId") +
                "/private/full/" + connectorProperties.getProperty("cellId");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getCell method.
     */
    @Test(priority = 1, description = "googlespreadsheet {getCell} integration test with negative cases.")
    public void testGetCellWithNegativeCase() throws IOException, XMLStreamException {
        esbRequestHeadersMap.put("Action", "urn:getCell");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCellNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/cells/" + "/" +
                connectorProperties.getProperty("worksheetId") + "/private/full/" +
                connectorProperties.getProperty("cellId");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for fetchSpecificRowsOrColumns method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {fetchSpecificRowsOrColumns} integration test with mandatory parameters.")
    public void testFetchSpecificRowsOrColumnsWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:fetchSpecificRowsOrColumns");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "fetchSpecificRowsOrColumnsMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/cells/" +
                connectorProperties.getProperty("key") +
                "/" + connectorProperties.getProperty("worksheetId") + "/private/full?min-row=" +
                connectorProperties.getProperty("minRow") + "&max-row=" +
                connectorProperties.getProperty("maxRow") + "&min-col=" + connectorProperties.getProperty("minCol") +
                "&max-col=" + connectorProperties.getProperty("maxCol");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for fetchSpecificRowsOrColumns method.
     */
    @Test(priority = 1, description = "googlespreadsheet {fetchSpecificRowsOrColumns} integration test with negative cases.")
    public void testFetchSpecificRowsOrColumnsWithNegativeCase() throws IOException, XMLStreamException {
        esbRequestHeadersMap.put("Action", "urn:fetchSpecificRowsOrColumns");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "fetchSpecificRowsOrColumnsNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/cells/" + "/" +
                connectorProperties.getProperty("worksheetId") +
                "/private/full?min-row=" + connectorProperties.getProperty("minRow") +
                "&min-col=" + connectorProperties.getProperty("minCol") + "&max-col=" +
                connectorProperties.getProperty("maxCol");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for deleteRows method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {deleteRows} integration test with mandatory parameters.")
    public void testDeleteRowsWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:deleteRows");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteRowsMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/list/" +
                connectorProperties.getProperty("key") + "/" + connectorProperties.getProperty("worksheetId") +
                "/" + "/private/full/" + connectorProperties.getProperty("deleteRowId");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for queryForRows method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {queryForRows} integration test with mandatory parameters.")
    public void testQueryForRowsWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:queryForRows");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "queryForRowsMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/list/" +
                connectorProperties.getProperty("key") + "/" + connectorProperties.getProperty("worksheetId") +
                "/private/full?sq=" + connectorProperties.getProperty("queryParam");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for queryForRows method.
     */
    @Test(priority = 1, description = "googlespreadsheet {queryForRows} integration test with negative cases.")
    public void testQueryForRowsWithNegativeCase() throws IOException, XMLStreamException {
        esbRequestHeadersMap.put("Action", "urn:queryForRows");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "queryForRowsNegative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/list/" +
                connectorProperties.getProperty("key") + "/" + connectorProperties.getProperty("worksheetId") +
                "/private/full?sq=";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for sortRows method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {sortRows} integration test with mandatory parameters.")
    public void testSortRowsWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:sortRows");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "sortRowsMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/list/" +
                connectorProperties.getProperty("key") + "/" + connectorProperties.getProperty("worksheetId") +
                "/private/full?orderby=" + connectorProperties.getProperty("orderby") + "&reverse=" +
                connectorProperties.getProperty("reverse");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Positive test case for modifyWorksheetTitleAndSize method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {modifyWorksheetTitleAndSize} integration test with mandatory parameters.")
    public void testModifyWorksheetTitleAndSizeWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:modifyWorksheetTitleAndSize");
        connectorProperties.setProperty("title", "updateTitle");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "modifyWorksheetTitleAndSizeMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/worksheets/" +
                connectorProperties.getProperty("key") + "/private/full?title=" +
                connectorProperties.getProperty("title");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for updateCells method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {updateCells} integration test with mandatory parameters.")
    public void testUpdateCellsWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:updateCells");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateCellsMandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/feeds/cells/" +
                connectorProperties.getProperty("key") + "/" + connectorProperties.getProperty("worksheetId") +
                "/private/full/" + connectorProperties.getProperty("cellId");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for importData method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {importData} integration test with mandatory parameters.")
    public void testImportDataWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:importData");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "importDataMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for importTableAndList method with mandatory parameters.
     */
    @Test(priority = 1, description = "googlespreadsheet {importTableAndList} integration test with mandatory parameters.")
    public void testImportTableAndListWithMandatoryParameters() throws IOException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {
        esbRequestHeadersMap.put("Action", "urn:importTableAndList");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "importTableAndListMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }
}

