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

package org.wso2.carbon.connector.integration.test.concur;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ConcurConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    private String entryId;
    
    private String paymentTypeId;
    
    private String reportId;
    
    private String entryIdOptional;
    
    private String multipartProxyUrl;
    
    private String receiptImageId;
    
    private String receiptImageIdOptional;
    
    private String expenseId;
    
    private String expenseIdOptional;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("concur");
        
        multipartProxyUrl = getProxyServiceURL("concur_multipart");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/xml");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "OAuth " + connectorProperties.getProperty("accessToken"));
        
    }
    
    /**
     * Positive test case for getAllAttendeeTypes method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Concur {getAllAttendeeTypes} integration test with mandatory parameters.")
    public void testGetAllAttendeeTypesWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAllAttendeeTypes");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAllAttendeeTypes_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/attendeetypes";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//ID", esbRestResponse.getBody()),
                getValueByExpression("//ID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//Name", esbRestResponse.getBody()),
                getValueByExpression("//Name", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for getAllAttendeeTypes method with optional parameters.
     */
    @Test(dependsOnMethods = { "testGetAllAttendeeTypesWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {getAllAttendeeTypes} integration test with optional parameters.")
    public void testGetAllAttendeeTypesWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAllAttendeeTypes");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAllAttendeeTypes_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/attendeetypes?limit=2";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//ID", esbRestResponse.getBody()),
                getValueByExpression("//ID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//Name", esbRestResponse.getBody()),
                getValueByExpression("//Name", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for getAllAttendeeTypes method.
     */
    @Test(dependsOnMethods = { "testGetAllAttendeeTypesWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getAllAttendeeTypes} integration test negative case.")
    public void testGetAllAttendeeTypesNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAllAttendeeTypes");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAllAttendeeTypes_negative.xml");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/attendeetypes?offset=invalidOffset";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//Message", esbRestResponse.getBody()),
                getValueByExpression("//Message", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for getExchangeRates method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Concur {getExchangeRates} integration test with mandatory parameters.")
    public void testGetExchangeRatesWithMandatoryParameters() throws Exception {
    
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(new Date());
        parametersMap.put("forDate", date);
        
        esbRequestHeadersMap.put("Action", "urn:getExchangeRates");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExchangeRates_mandatory.xml",
                        parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/api/v3.0/expense/exchangerates/?fromCurrency=USD&toCurrency=GBP&forDate=" + date;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//decimal", esbRestResponse.getBody()),
                getValueByExpression("//decimal", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for getExchangeRates method.
     */
    @Test(dependsOnMethods = { "testGetExchangeRatesWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {getExchangeRates} integration test negative case.")
    public void testGetExchangeRatesNegativeCase() throws Exception {
    
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(new Date());
        parametersMap.put("forDate", date);
        
        esbRequestHeadersMap.put("Action", "urn:getExchangeRates");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExchangeRates_negative.xml",
                        parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/exchangerates/?fromcurrency=USD"
                        + "&forDate=" + date;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
    }
    
    /**
     * Positive test case for getAllQuickExpenses method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateQuickExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {getAllQuickExpenses} integration test with mandatory parameters.")
    public void testGetAllQuickExpensesWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAllQuickExpenses");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAllQuickExpenses_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//ID", esbRestResponse.getBody()),
                getValueByExpression("//ID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//PaymentTypeCode", esbRestResponse.getBody()),
                getValueByExpression("//PaymentTypeCode", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for getAllQuickExpenses method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateQuickExpenseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getAllQuickExpenses} integration test with optional parameters.")
    public void testGetAllQuickExpensesWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAllQuickExpenses");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAllQuickExpenses_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses?limit=2";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//ID", esbRestResponse.getBody()),
                getValueByExpression("//ID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//PaymentTypeCode", esbRestResponse.getBody()),
                getValueByExpression("//PaymentTypeCode", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for getAllQuickExpenses method.
     */
    @Test(dependsOnMethods = { "testGetAllQuickExpensesWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getAllQuickExpenses} integration test negative case.")
    public void testGetAllQuickExpensesNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAllQuickExpenses");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAllQuickExpenses_negative.xml");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses?offset=invalidOffset";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//Message", esbRestResponse.getBody()),
                getValueByExpression("//Message", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for getQuickExpense method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateQuickExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {getQuickExpense} integration test with mandatory parameters.")
    public void testGetQuickExpenseWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getQuickExpense");
        parametersMap.put("expenseId", expenseId);
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQuickExpense_mandatory.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/" + expenseId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//ID", esbRestResponse.getBody()),
                getValueByExpression("//ID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//TransactionAmount", esbRestResponse.getBody()),
                getValueByExpression("//TransactionAmount", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//OwnerName", esbRestResponse.getBody()),
                getValueByExpression("//OwnerName", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for getQuickExpense method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateQuickExpenseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getQuickExpense} integration test with optional parameters.")
    public void testGetQuickExpenseWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getQuickExpense");
        parametersMap.put("expenseIdOptional", expenseIdOptional);
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQuickExpense_optional.xml",
                        parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/" + expenseIdOptional
                        + "?user=" + connectorProperties.getProperty("user");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//ID", esbRestResponse.getBody()),
                getValueByExpression("//ID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//TransactionAmount", esbRestResponse.getBody()),
                getValueByExpression("//TransactionAmount", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//TransactionDate", esbRestResponse.getBody()),
                getValueByExpression("//TransactionDate", apiRestResponse.getBody()));
        
    }
    
    /**
     * Negative test case for getQuickExpense method.
     */
    @Test(dependsOnMethods = { "testGetQuickExpenseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getQuickExpense} integration test negative case.")
    public void testGetQuickExpenseNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getQuickExpense");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQuickExpense_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/invalid_id";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(getValueByExpression("//Message", esbRestResponse.getBody()),
                getValueByExpression("//Message", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for createReceiptImage method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Concur {createReceiptImage} integration test with mandatory parameters.")
    public void testCreateReceiptImageWithMandatoryParameters() throws Exception {
    
        final Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
        
        attachmentHeadersMap.putAll(esbRequestHeadersMap);
        attachmentHeadersMap.put("Authorization", "OAuth " + connectorProperties.getProperty("accessToken"));
        attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("imageContentType"));
        
        final String endPointUrl = multipartProxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl");
        
        final MultipartFormdataProcessor fileRequestProcessor =
                new MultipartFormdataProcessor(endPointUrl, attachmentHeadersMap);
        
        final File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("imageFileName"));
        
        fileRequestProcessor.addFiletoRequestBody(file);
        final RestResponse<OMElement> esbRestResponse = fileRequestProcessor.processAttachmentForXmlResponse();
        
        receiptImageId = getValueByExpression("//ReceiptImage/ID", esbRestResponse.getBody());
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(receiptImageId);
    }
    
    /**
     * Positive test case for createReceiptImage method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateReceiptImageWithMandatoryParameters" }, description = "Concur {createReceiptImage} integration test with mandatory parameters.")
    public void testCreateReceiptImageWithOptionalParameters() throws Exception {
    
        final Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
        
        attachmentHeadersMap.putAll(esbRequestHeadersMap);
        attachmentHeadersMap.put("Authorization", "OAuth " + connectorProperties.getProperty("accessToken"));
        attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("imageContentType"));
        
        final String endPointUrl =
                multipartProxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&user="
                        + connectorProperties.getProperty("user");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final MultipartFormdataProcessor fileRequestProcessor =
                new MultipartFormdataProcessor(endPointUrl, attachmentHeadersMap);
        
        final File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("imageFileName"));
        
        fileRequestProcessor.addFiletoRequestBody(file);
        final RestResponse<OMElement> esbRestResponse = fileRequestProcessor.processAttachmentForXmlResponse();
        
        receiptImageIdOptional = getValueByExpression("//ReceiptImage/ID", esbRestResponse.getBody());
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(receiptImageIdOptional);
    }
    
    /**
     * Negative test case for createReceiptImage method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateReceiptImageWithOptionalParameters" }, description = "Concur {createReceiptImage} integration test negative case.")
    public void testCreateReceiptImageNegativeCase() throws Exception {
    
        Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
        
        attachmentHeadersMap.putAll(esbRequestHeadersMap);
        attachmentHeadersMap.put("Authorization", "OAuth " + connectorProperties.getProperty("accessToken") + "1");
        attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("imageContentType"));
        
        final String endPointUrl = multipartProxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final MultipartFormdataProcessor fileRequestProcessor =
                new MultipartFormdataProcessor(endPointUrl, attachmentHeadersMap);
        
        final File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("imageFileName"));
        
        fileRequestProcessor.addFiletoRequestBody(file);
        final RestResponse<JSONObject> esbRestResponse = fileRequestProcessor.processAttachmentForJsonResponse();
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
    }
    
    /**
     * Positive test case for getReceiptIdsForUser method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateReceiptImageWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {getReceiptIdsForUser} integration test with mandatory parameters.")
    public void testGetReceiptIdsForUserWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getReceiptIdsForUser");
        List<OMElement> esbResponseReceiptImageList = new ArrayList<OMElement>();
        List<OMElement> apiResponseReceiptImageList = new ArrayList<OMElement>();
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReceiptIdsForUser_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/receiptimages";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Iterator<?> esbResponseIterator =
                esbRestResponse.getBody().getFirstChildWithName(new QName("Items"))
                        .getChildrenWithLocalName("ReceiptImage");
        Iterator<?> apiResponseIterator =
                apiRestResponse.getBody().getFirstChildWithName(new QName("Items"))
                        .getChildrenWithLocalName("ReceiptImage");
        
        while (esbResponseIterator.hasNext()) {
            esbResponseReceiptImageList.add((OMElement) esbResponseIterator.next());
        }
        
        while (apiResponseIterator.hasNext()) {
            apiResponseReceiptImageList.add((OMElement) apiResponseIterator.next());
        }
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponseReceiptImageList.size(), apiResponseReceiptImageList.size());
        Assert.assertEquals(esbResponseReceiptImageList.get(0).getFirstChildWithName(new QName("ID")).getText(),
                apiResponseReceiptImageList.get(0).getFirstChildWithName(new QName("ID")).getText());
        
    }
    
    /**
     * Positive test case for getReceiptIdsForUser method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateReceiptImageWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getReceiptIdsForUser} integration test with optional parameters.")
    public void testGetReceiptIdsForUserWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getReceiptIdsForUser");
        List<OMElement> esbResponseReceiptImageList = new ArrayList<OMElement>();
        List<OMElement> apiResponseReceiptImageList = new ArrayList<OMElement>();
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReceiptIdsForUser_optional.xml");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/receiptimages?limit=2&user="
                        + connectorProperties.getProperty("user");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Iterator<?> esbResponseIterator =
                esbRestResponse.getBody().getFirstChildWithName(new QName("Items"))
                        .getChildrenWithLocalName("ReceiptImage");
        Iterator<?> apiResponseIterator =
                apiRestResponse.getBody().getFirstChildWithName(new QName("Items"))
                        .getChildrenWithLocalName("ReceiptImage");
        
        while (esbResponseIterator.hasNext()) {
            esbResponseReceiptImageList.add((OMElement) esbResponseIterator.next());
        }
        
        while (apiResponseIterator.hasNext()) {
            apiResponseReceiptImageList.add((OMElement) apiResponseIterator.next());
        }
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponseReceiptImageList.size(), apiResponseReceiptImageList.size());
        Assert.assertEquals(esbResponseReceiptImageList.get(0).getFirstChildWithName(new QName("ID")).getText(),
                apiResponseReceiptImageList.get(0).getFirstChildWithName(new QName("ID")).getText());
        
    }
    
    /**
     * Negative test case for getReceiptIdsForUser method.
     */
    @Test(dependsOnMethods = { "testGetReceiptIdsForUserWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getReceiptIdsForUser} integration test negative case.")
    public void testGetReceiptIdsForUserNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getReceiptIdsForUser");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReceiptIdsForUser_negative.xml");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/receiptimages?offset=invalidOffset";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getFirstChildWithName(new QName("Message")).getText(),
                apiRestResponse.getBody().getFirstChildWithName(new QName("Message")).getText());
        
    }
    
    /**
     * Positive test case for getReceiptImageURL method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateReceiptImageWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {getReceiptImageURL} integration test with mandatory parameters.")
    public void testGetReceiptImageURLWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getReceiptImageURL");
        parametersMap.put("receiptImageId", receiptImageId);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReceiptImageURL_mandatory.xml",
                        parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/receiptimages/" + receiptImageId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponseURI = esbRestResponse.getBody().getFirstChildWithName(new QName("URI")).getText();
        String apiResponseURI = apiRestResponse.getBody().getFirstChildWithName(new QName("URI")).getText();
        
        esbResponseURI = esbResponseURI.substring(1, esbResponseURI.lastIndexOf("/"));
        apiResponseURI = apiResponseURI.substring(1, apiResponseURI.lastIndexOf("/"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponseURI, apiResponseURI);
        
    }
    
    /**
     * Positive test case for getReceiptImageURL method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateReceiptImageWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getReceiptImageURL} integration test with optional parameters.")
    public void testGetReceiptImageURLWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getReceiptImageURL");
        parametersMap.put("receiptImageIdOptional", receiptImageIdOptional);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReceiptImageURL_optional.xml",
                        parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/receiptimages/" + receiptImageIdOptional
                        + "?user=" + connectorProperties.getProperty("user");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponseURI = esbRestResponse.getBody().getFirstChildWithName(new QName("URI")).getText();
        String apiResponseURI = apiRestResponse.getBody().getFirstChildWithName(new QName("URI")).getText();
        
        esbResponseURI = esbResponseURI.substring(1, esbResponseURI.lastIndexOf("/"));
        apiResponseURI = apiResponseURI.substring(1, apiResponseURI.lastIndexOf("/"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponseURI, apiResponseURI);
        
    }
    
    /**
     * Negative test case for getReceiptImageURL method.
     */
    @Test(dependsOnMethods = { "testGetReceiptImageURLWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getReceiptImageURL} integration test negative case.")
    public void testGetReceiptImageURLNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getReceiptImageURL");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReceiptImageURL_negative.xml");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/receiptimages/InvalidId?user="
                        + connectorProperties.getProperty("user");
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getFirstChildWithName(new QName("Message")).getText(),
                apiRestResponse.getBody().getFirstChildWithName(new QName("Message")).getText());
        
    }
    
    /**
     * Positive test case for deleteReceiptImage method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetReceiptImageURLWithMandatoryParameters",
            "testGetReceiptIdsForUserWithMandatoryParameters" }, description = "Concur {deleteReceiptImage} integration test with mandatory parameters.")
    public void testDeleteReceiptImageWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteReceiptImage");
        parametersMap.put("receiptImageId", receiptImageId);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteReceiptImage_mandatory.xml",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/receiptimages/" + receiptImageId;
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 403);
    }
    
    /**
     * Positive test case for deleteReceiptImage method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetReceiptImageURLWithOptionalParameters",
            "testGetReceiptIdsForUserWithOptionalParameters" }, description = "Concur {deleteReceiptImage} integration test with optional parameters.")
    public void testDeleteReceiptImageWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteReceiptImage");
        
        connectorProperties.setProperty("receiptImageId", receiptImageIdOptional);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteReceiptImage_optional.xml");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/receiptimages/" + receiptImageIdOptional;
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 403);
    }
    
    /**
     * Negative test case for deleteReceiptImage method.
     */
    @Test(dependsOnMethods = { "testDeleteReceiptImageWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {deleteReceiptImage} integration test negative case.")
    public void testDeleteReceiptImageNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteReceiptImage");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteReceiptImage_negative.xml");
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/receiptimages/InvalidReceiptId";
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for getAllExpenseEntries method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Concur {getAllExpenseEntries} integration test with mandatory parameters.")
    public void testGetAllExpenseEntriesWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAllExpenseEntries");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAllExpenseEntries_mandatory.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//ID", esbRestResponse.getBody()),
                getValueByExpression("//ID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//ReportID", esbRestResponse.getBody()),
                getValueByExpression("//ReportID", apiRestResponse.getBody()));
        
        paymentTypeId = getValueByExpression("//PaymentTypeID", esbRestResponse.getBody());
        reportId = getValueByExpression("//ReportID", esbRestResponse.getBody());
        
    }
    
    /**
     * Positive test case for getAllExpenseEntries method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateExpenseEntryWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getAllExpenseEntries} integration test with optional parameters.")
    public void testGetAllExpenseEntriesWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAllExpenseEntries");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAllExpenseEntries_optional.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries?limit=1";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//ID", esbRestResponse.getBody()),
                getValueByExpression("//ID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//ReportID", esbRestResponse.getBody()),
                getValueByExpression("//ReportID", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for getAllExpenseEntries method.
     */
    @Test(dependsOnMethods = { "testGetAllExpenseEntriesWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getAllExpenseEntries} integration test negative test case.")
    public void testGetAllExpenseEntriesWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAllExpenseEntries");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAllExpenseEntries_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries?offset=error";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getFirstChildWithName(new QName("Message")).getText(),
                apiRestResponse.getBody().getFirstChildWithName(new QName("Message")).getText());
    }
    
    /**
     * Positive test case for getExpenseEntry method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateExpenseEntryWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {getExpenseEntry} integration test with mandatory parameters.")
    public void testGetExpenseEntryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getExpenseEntry");
        parametersMap.put("entryId", entryId);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExpenseEntry_mandatory.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries/" + entryId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//Entry/ID", esbRestResponse.getBody()),
                getValueByExpression("//Entry/ID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//Entry/URI", esbRestResponse.getBody()),
                getValueByExpression("//Entry/URI", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getExpenseEntry method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateExpenseEntryWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getExpenseEntry} integration test with optional parameters.")
    public void testGetExpenseEntryWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getExpenseEntry");
        parametersMap.put("entryId", entryIdOptional);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExpenseEntry_optional.xml",
                        parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries/" + entryIdOptional + "?user="
                        + connectorProperties.getProperty("user");
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//Entry/ID", esbRestResponse.getBody()),
                getValueByExpression("//Entry/ID", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//Entry/ReportOwnerID", esbRestResponse.getBody()),
                getValueByExpression("//Entry/ReportOwnerID", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for getExpenseEntry method.
     */
    @Test(dependsOnMethods = { "testGetExpenseEntryWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {getExpenseEntry} integration test negative test case.")
    public void testGetExpenseEntryWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getExpenseEntry");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExpenseEntry_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries/invalid";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getFirstChildWithName(new QName("Message")).getText(),
                apiRestResponse.getBody().getFirstChildWithName(new QName("Message")).getText());
    }
    
    /**
     * Positive test case for createExpenseEntry method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetAllExpenseEntriesWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {createExpenseEntry} integration test with mandatory parameters.")
    public void testCreateExpenseEntryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createExpenseEntry");
        parametersMap.put("paymentTypeId", paymentTypeId);
        parametersMap.put("reportId", reportId);
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExpenseEntry_mandatory.xml",
                        parametersMap);
        entryId = getValueByExpression("//Response/ID", esbRestResponse.getBody());
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries/" + entryId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//Response/ID", esbRestResponse.getBody()),
                getValueByExpression("//Entry/ID", apiRestResponse.getBody()));
        
    }
    
    /**
     * Positive test case for createExpenseEntry method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetAllExpenseEntriesWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {createExpenseEntry} integration test with optional parameters.")
    public void testCreateExpenseEntryWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createExpenseEntry");
        parametersMap.put("paymentTypeId", paymentTypeId);
        parametersMap.put("reportId", reportId);
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExpenseEntry_optional.xml",
                        parametersMap);
        
        entryIdOptional = getValueByExpression("//Response/ID", esbRestResponse.getBody());
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries/" + entryIdOptional;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("//Response/ID", esbRestResponse.getBody()),
                getValueByExpression("//Entry/ID", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for createExpenseEntry method.
     */
    @Test(dependsOnMethods = { "testCreateExpenseEntryWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {createExpenseEntry} integration test negative test case.")
    public void testCreateExpenseEntryWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createExpenseEntry");
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExpenseEntry_negative.xml");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries";
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createExpenseEntry_negative.xml");
        
        String esbMessage = getValueByExpression("//Message", esbRestResponse.getBody());
        String apiMessage = getValueByExpression("//Message", apiRestResponse.getBody());
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbMessage, apiMessage);
    }
    
    /**
     * Positive test case for deleteExpenseEntry method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testGetExpenseEntryWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {deleteExpenseEntry} integration test with mandatory parameters.")
    public void testDeleteExpenseEntryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteExpenseEntry");
        parametersMap.put("entryId", entryId);
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteExpenseEntry_mandatory.xml",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries/" + entryId;
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for deleteExpenseEntry method with optional parameters.
     */
    @Test(dependsOnMethods = { "testGetExpenseEntryWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {deleteExpenseEntry} integration test with optional parameters.")
    public void testDeleteExpenseEntryWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteExpenseEntry");
        parametersMap.put("entryIdOptional", entryIdOptional);
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteExpenseEntry_optional.xml",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries/" + entryIdOptional;
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Negative test case for deleteExpenseEntry method.
     */
    @Test(dependsOnMethods = { "testDeleteExpenseEntryWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {deleteExpenseEntry} integration test negative case.")
    public void testDeleteExpenseEntryNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteExpenseEntry");
        
        final RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteExpenseEntry_negative.xml");
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/entries/InvalidReceiptId";
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for createQuickExpense method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Concur {createQuickExpense} integration test with mandatory parameters.")
    public void testCreateQuickExpenseWithMandatoryParameters() throws Exception {
    
        String esbTransactionAmount = connectorProperties.getProperty("transactionAmount");
        esbRequestHeadersMap.put("Action", "urn:createQuickExpense");
        parametersMap.put("transactionAmount", esbTransactionAmount);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQuickExpense_mandatory.xml",
                        parametersMap);
        
        expenseId = getValueByExpression("//Response/ID", esbRestResponse.getBody());
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/" + expenseId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiTransactionAmount =
                getValueByExpression("//QuickExpense/TransactionAmount", apiRestResponse.getBody());
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbTransactionAmount, apiTransactionAmount);
        
    }
    
    /**
     * Positive test case for createQuickExpense method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Concur {createQuickExpense} integration test with optional parameters.")
    public void testCreateQuickExpenseWithOptionalParameters() throws Exception {
    
        String esbVendorDescription = connectorProperties.getProperty("vendorDescription");
        esbRequestHeadersMap.put("Action", "urn:createQuickExpense");
        parametersMap.put("vendorDescription", esbVendorDescription);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQuickExpense_optional.xml",
                        parametersMap);
        
        expenseIdOptional = getValueByExpression("//Response/ID", esbRestResponse.getBody());
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/" + expenseIdOptional;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiVendorDescription =
                getValueByExpression("//QuickExpense/VendorDescription", apiRestResponse.getBody());
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbVendorDescription, apiVendorDescription);
        
    }
    
    /**
     * Negative test case for createQuickExpense method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateQuickExpenseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {createQuickExpense} integration test with mandatory parameters.")
    public void testCreateQuickExpenseWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createQuickExpense");
        parametersMap.put("transactionAmount", connectorProperties.getProperty("transactionAmount"));
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQuickExpense_negative.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses";
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createQuickExpense_negative.xml",
                        parametersMap);
        
        String esbMessage = getValueByExpression("//Message", esbRestResponse.getBody());
        String apiMessage = getValueByExpression("//Message", apiRestResponse.getBody());
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbMessage, apiMessage);
        
    }
    
    /**
     * Positive test case for updateQuickExpense method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testGetQuickExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {updateQuickExpense} integration test with mandatory parameters.")
    public void testUpdateQuickExpenseWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateQuickExpense");
        parametersMap.put("expenseId", expenseId);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateQuickExpense_mandatory.xml",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/" + expenseId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiExpenseId = getValueByExpression("//QuickExpense/ID", apiRestResponse.getBody());
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(expenseId, apiExpenseId);
    }
    
    /**
     * Positive test case for updateQuickExpense method with optional parameters.
     */
    @Test(dependsOnMethods = { "testGetQuickExpenseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {updateQuickExpense} integration test with optional parameters.")
    public void testUpdateQuickExpenseWithOptionalParameters() throws Exception {
    
        String esbVendorDescription = connectorProperties.getProperty("updatedVendorDescription");
        esbRequestHeadersMap.put("Action", "urn:updateQuickExpense");
        parametersMap.put("expenseIdOptional", expenseIdOptional);
        parametersMap.put("updatedVendorDescription", esbVendorDescription);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateQuickExpense_optional.xml",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/" + expenseIdOptional;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiVendorDescription =
                getValueByExpression("//QuickExpense/VendorDescription", apiRestResponse.getBody());
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(esbVendorDescription, apiVendorDescription);
        
    }
    
    /**
     * Negative test case for updateQuickExpense method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testUpdateQuickExpenseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {updateQuickExpense} integration test with mandatory parameters.")
    public void testUpdateQuickExpenseWithNegativeCase() throws Exception {
    
        String esbTransactionAmount = connectorProperties.getProperty("transactionAmount");
        esbRequestHeadersMap.put("Action", "urn:updateQuickExpense");
        parametersMap.put("expenseIdOptional", expenseIdOptional);
        parametersMap.put("transactionAmount", esbTransactionAmount);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateQuickExpense_negative.xml",
                        parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/" + expenseIdOptional;
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateQuickExpense_negative.xml",
                        parametersMap);
        
        String esbMessage = getValueByExpression("//Error/Message", esbRestResponse.getBody());
        String apiMessage = getValueByExpression("//Error/Message", apiRestResponse.getBody());
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbMessage, apiMessage);
        
    }
    
    /**
     * Positive test case for deleteQuickExpense method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testUpdateQuickExpenseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Concur {urn:deleteQuickExpense} integration test with mandatory parameters.")
    public void testDeleteQuickExpenseWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteQuickExpense");
        parametersMap.put("expenseId", expenseId);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteQuickExpense_mandatory.xml",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/" + expenseId;
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for deleteQuickExpense method with optional parameters.
     */
    @Test(dependsOnMethods = { "testUpdateQuickExpenseWithNegativeCase" }, groups = { "wso2.esb" }, description = "Concur {urn:deleteQuickExpense} integration test with optional parameters.")
    public void testDeleteQuickExpenseWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteQuickExpense");
        parametersMap.put("expenseIdOptional", expenseIdOptional);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteQuickExpense_optional.xml",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/" + expenseIdOptional
                        + "?user=" + connectorProperties.getProperty("user");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Negative test case for deleteQuickExpense method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testDeleteQuickExpenseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Concur {deleteQuickExpense} integration test with mandatory parameters.")
    public void testDeleteQuickExpenseWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteQuickExpense");
        parametersMap.put("expenseIdOptional", expenseIdOptional);
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteQuickExpense_negative.xml",
                        parametersMap);
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.0/expense/quickexpenses/" + expenseIdOptional
                        + "?user=Invalid";
        final RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
    }
}
