/*
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

package org.wso2.carbon.connector.integration.test.magento;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;

public class MagentoConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    Map<String, String> nameSpaceMap = new HashMap<String, String>();
    
    private final String SOAP_HEADER_XPATH_EXP = "/soapenv:Envelope/soapenv:Header/*";
    
    private final String SOAP_BODY_XPATH_EXP = "/soapenv:Envelope/soapenv:Body/*";
    
    private final String MAGENTO_ACTION = "urn:Mage_Api_Model_Server_V2_HandlerAction";
    
    private String apiEndPoint;
    
    private String sessionId;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("magento-connector-1.0.0");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        nameSpaceMap.put("ns1", "urn:Magento");
        nameSpaceMap.put("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        
        apiEndPoint = connectorProperties.getProperty("apiUrl");
        
        // Login and get session id for API calls.
        SOAPEnvelope apiLoginResponse =
                sendSOAPRequest(apiEndPoint, "api_login.xml", null, MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        String xPathExp = "string(//soapenv:Body/ns1:loginResponse/loginReturn/text())";
        sessionId = (String) xPathEvaluate(apiLoginResponse, xPathExp, nameSpaceMap);
        
        parametersMap.put("sessionId", sessionId);
    }
    
    /**
     * Positive test case for moveProductFromQuoteToCart method with mandatory parameters. For this test to work you
     * need: 1. A shopping cart that has not been converted to an order (quote) 2. A customer order which has not been
     * converted to an invoice 3. Shipping Address set for customer 4. Customer added to cart 5. Products added to cart
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {moveProductFromQuoteToCart} integration test with mandatory parameters.")
    public void testMoveProductFromQuoteToCartWithMandatoryParameters() throws Exception {
    
        // Create the quote for the operation
        SOAPEnvelope apiCreateCartSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_mandatory_createCart.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        String xPathExp = "string(//quoteId/text())";
        String createdQuoteIdSecond = (String) xPathEvaluate(apiCreateCartSoapResponse, xPathExp, nameSpaceMap);
        connectorProperties.setProperty("moveProductFromQuoteToCartQuoteId", createdQuoteIdSecond);
        
        sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_mandatory_addCartProduct.xml", parametersMap,
                MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_mandatory_shoppingCartCustomerAdd.xml",
                parametersMap, MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_mandatory_setCartCustomerAddress.xml",
                parametersMap, MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        // Call the moveProductFromQuoteToCart methods through ESB.
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_moveProductFromQuoteToCart_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductMoveToCustomerQuoteResponse/result/text())";
        Boolean success = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(success);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_mandatory.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        Assert.assertTrue(apiResponseElement.toString().contains("shoppingCartItemEntity[0]"));
    }
    
    /**
     * Positive test case for moveProductFromQuoteToCart method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testMoveProductFromQuoteToCartWithMandatoryParameters" }, description = "Magento {moveProductFromQuoteToCart} integration test with optional parameters.")
    public void testMoveProductFromQuoteToCartWithOptionalParameters() throws Exception {
    
        // Create the quote for the operation
        SOAPEnvelope apiCreateCartSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_mandatory_createCart.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        String xPathExp = "string(//quoteId/text())";
        String createdQuoteIdSecond = (String) xPathEvaluate(apiCreateCartSoapResponse, xPathExp, nameSpaceMap);
        connectorProperties.setProperty("moveProductFromQuoteToCartQuoteId", createdQuoteIdSecond);
        
        sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_mandatory_addCartProduct.xml", parametersMap,
                MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_mandatory_shoppingCartCustomerAdd.xml",
                parametersMap, MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_mandatory_setCartCustomerAddress.xml",
                parametersMap, MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        // Call the moveProductFromQuoteToCart methods through ESB.
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_moveProductFromQuoteToCart_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductMoveToCustomerQuoteResponse/result/text())";
        Boolean success = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(success);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_mandatory.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        Assert.assertTrue(apiResponseElement.toString().contains("shoppingCartItemEntity[0]"));
    }
    
    /**
     * Negative test case for moveProductFromQuoteToCart method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testMoveProductFromQuoteToCartWithOptionalParameters" }, description = "Magento {moveProductFromQuoteToCart} negative case integration test.")
    public void testMoveProductFromQuoteToCartNegativeCase() throws Exception {
    
        String esbFaultCode = "esbFaultCode";
        String esbFaultMessage = "esbFaultMessage";
        String apiFaultCode = "apiFaultCode";
        String apiFaultMessage = "apiFaultMessage";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_moveProductFromQuoteToCart_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultMessage = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_moveProductFromQuoteToCart_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultMessage = af.getFaultCodeElement().getText();
        }
        Assert.assertEquals(esbFaultCode, apiFaultCode);
        Assert.assertEquals(esbFaultMessage, apiFaultMessage);
    }
    
    /**
     * Positive test case for cancelOrder method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testMoveProductFromQuoteToCartNegativeCase" }, groups = { "wso2.esb" }, description = "Magento {cancelOrder} integration test with mandatory parameters.")
    public void testCancelOrderWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_cancelOrder_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderCancelResponse/result/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_cancelOrder_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderInfoResponse/result/status/text())";
        String updatedStatus = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderInfoResponse/result/state/text())";
        String updatedState = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(updatedStatus, "canceled");
        Assert.assertEquals(updatedState, "canceled");
    }
    
    /**
     * Negative test case for cancelOrder method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCancelOrderWithMandatoryParameters" }, description = "Magento {cancelOrder} integration test with negative case.")
    public void testCancelOrderWithNegativeCase() throws Exception {
    
        String apiFaultString = "apiFaultString";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultString = "esbFaultString";
        String esbFaultCodeElement = "esbFaultCodeElement";
        try {
            sendSOAPRequest(proxyUrl, "esb_cancelOrder_negative.xml", parametersMap, "mediate", SOAP_HEADER_XPATH_EXP,
                    SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultString = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_cancelOrder_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultString = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultString, esbFaultString);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for updateCatalogProductTierPrice method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {updateCatalogProductTierPrice} integration test with optional parameters.")
    public void testUpdateCatalogProductTierPriceWithOptionalParameters() throws Exception {
    
        parametersMap.put("customerGroupId", "1");
        parametersMap.put("customerGroupQty", "10");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_updateCatalogProductTierPrice_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:catalogProductAttributeTierPriceUpdateResponse/result/text())";
        String isSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(isSuccess, "1");
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_updateCatalogProductTierPrice_optional.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp =
                "string(//soapenv:Body/ns1:catalogProductAttributeTierPriceInfoResponse/result/item[customer_group_id=1]/qty/text())";
        String apiResultQty = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiResultQty, parametersMap.get("customerGroupQty"));
    }
    
    /**
     * Negative test case for updateCatalogProductTierPrice method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCatalogProductTierPriceWithOptionalParameters" }, description = "Magento {updateCatalogProductTierPrice} integration test negative case.")
    public void testUpdateCatalogProductTierPriceNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_updateCatalogProductTierPrice_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_updateCatalogProductTierPrice_negative.xml", parametersMap,
                    MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for getCatalogProductTierPriceInfo method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCatalogProductTierPriceNegativeCase" }, description = "Magento {getCatalogProductTierPriceInfo} integration test with mandatory parameters.")
    public void testGetCatalogProductTierPriceInfoWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getCatalogProductTierPriceInfo_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "count(//soapenv:Body/ns1:catalogProductAttributeTierPriceInfoResponse/result/item)";
        Double esbResultCount = (Double) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogProductAttributeTierPriceInfoResponse/result/item[1]/qty/text())";
        String esbResultQty = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCatalogProductTierPriceInfo_mandatory.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "count(//soapenv:Body/ns1:catalogProductAttributeTierPriceInfoResponse/result/item)";
        Double apiResultCount = (Double) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogProductAttributeTierPriceInfoResponse/result/item[1]/qty/text())";
        String apiResultQty = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbResultCount, apiResultCount);
        Assert.assertEquals(esbResultQty, apiResultQty);
    }
    
    /**
     * Positive test case for getCatalogProductTierPriceInfo method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCatalogProductTierPriceInfoWithMandatoryParameters" }, description = "Magento {getCatalogProductTierPriceInfo} integration test with optional parameters.")
    public void testGetCatalogProductTierPriceInfoWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getCatalogProductTierPriceInfo_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "count(//soapenv:Body/ns1:catalogProductAttributeTierPriceInfoResponse/result/item)";
        Double esbResultCount = (Double) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogProductAttributeTierPriceInfoResponse/result/item[1]/qty/text())";
        String esbResultQty = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCatalogProductTierPriceInfo_optional.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "count(//soapenv:Body/ns1:catalogProductAttributeTierPriceInfoResponse/result/item)";
        Double apiResultCount = (Double) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogProductAttributeTierPriceInfoResponse/result/item[1]/qty/text())";
        String apiResultQty = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbResultCount, apiResultCount);
        Assert.assertEquals(esbResultQty, apiResultQty);
    }
    
    /**
     * Negative test case for getCatalogProductTierPriceInfo method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCatalogProductTierPriceInfoWithOptionalParameters" }, description = "Magento {getCatalogProductTierPriceInfo} integration test negative case.")
    public void testGetCatalogProductTierPriceInfoNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_getCatalogProductTierPriceInfo_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_getCatalogProductTierPriceInfo_negative.xml", parametersMap,
                    MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for updateStockData method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCatalogProductTierPriceInfoWithOptionalParameters" }, description = "Magento {updateStockData} integration test with optional parameters.")
    public void testUpdateStockDataWithOptionalParameters() throws Exception {
    
        parametersMap.put("updateQty", "100");
        parametersMap.put("isInStock", "2");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_updateStockData_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:catalogInventoryStockItemUpdateResponse/result/text())";
        String isSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(isSuccess, "1");
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_updateStockData_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:catalogInventoryStockItemListResponse/result/item[1]/qty/text())";
        String apiQty = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogInventoryStockItemListResponse/result/item[1]/is_in_stock/text())";
        String apiIsInStock = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiQty.split("\\.")[0], parametersMap.get("updateQty"));
        Assert.assertEquals(apiIsInStock, parametersMap.get("isInStock"));
    }
    
    /**
     * Negative test case for updateStockData method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {updateStockData} integration test negative case.")
    public void testUpdateStockDataNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_updateStockData_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_updateStockData_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for listStockData method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateStockDataWithOptionalParameters" }, description = "Magento {listStockData} integration test with mandatory parameters.")
    public void testListStockDataWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_listStockData_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_listStockData_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String xPathExp = "count(//result/item)";
        Double esbResultCount = (Double) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        Double apiResultCount = (Double) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogInventoryStockItemListResponse/result/item[1]/product_id/text())";
        String esbProductId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiProductId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogInventoryStockItemListResponse/result/item[1]/sku/text())";
        String esbSKU = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiSKU = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogInventoryStockItemListResponse/result/item[1]/qty/text())";
        String esbQty = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiQty = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogInventoryStockItemListResponse/result/item[1]/is_in_stock/text())";
        String esbIsInStock = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiIsInStock = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbResultCount, apiResultCount);
        Assert.assertEquals(esbProductId, apiProductId);
        Assert.assertEquals(esbSKU, apiSKU);
        Assert.assertEquals(esbQty, apiQty);
        Assert.assertEquals(esbIsInStock, apiIsInStock);
    }
    
    /**
     * Positive test case for createCustomer method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {createCustomer} integration test with mandatory parameters.")
    public void testCreateCustomerWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createCustomer_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:customerCustomerCreateResponse/result/text())";
        
        String esbResultCustomerId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        connectorProperties.setProperty("customerId", esbResultCustomerId);
        
        xPathExp = "string(//soapenv:Body/ns1:customerCustomerInfoResponse/customerInfo/customer_id/text())";
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createCustomer_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String apiResultCustomerId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbResultCustomerId, apiResultCustomerId);
    }
    
    /**
     * Positive test case for createCustomer method with optional parameters.
     */
    @Test(dependsOnMethods = { "testGetCustomerInfoNegativeCase" }, groups = { "wso2.esb" }, description = "Magento {createCustomer} integration test with optional parameters.")
    public void testCreateCustomerWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createCustomer_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:customerCustomerCreateResponse/result/text())";
        
        String esbResultCustomerId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        connectorProperties.setProperty("customerId", esbResultCustomerId);
        
        xPathExp = "string(//soapenv:Body/ns1:customerCustomerInfoResponse/customerInfo/customer_id/text())";
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createCustomer_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String apiResultCustomerId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        xPathExp = "string(//soapenv:Body/ns1:customerCustomerInfoResponse/customerInfo/email/text())";
        String apiResultCustomerEmail = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbResultCustomerId, apiResultCustomerId);
        Assert.assertEquals(connectorProperties.getProperty("emailOptional"), apiResultCustomerEmail);
    }
    
    /**
     * Negative test case for createCustomer method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomerWithOptionalParameters" }, description = "Magento {createCustomer} integration test negative case.")
    public void testCreateCustomerNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_createCustomer_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_createCustomer_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for getCustomerInfo method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateCustomerWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Magento {getCustomerInfo} integration test with mandatory parameters.")
    public void testGetCustomerInfoWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getCustomerInfo_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String esbResultCustomerId = getValueByExpression("//*[local-name()='customer_id']/text()", esbResponseElement);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCustomerInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        String apiResultCustomerId = getValueByExpression("//*[local-name()='customer_id']/text()", apiResponseElement);
        
        Assert.assertEquals(esbResultCustomerId, apiResultCustomerId);
    }
    
    /**
     * Positive test case for getCustomerInfo method with optional parameters.
     */
    @Test(dependsOnMethods = { "testGetCustomerInfoWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Magento {getCustomerInfo} integration test with optional parameters.")
    public void testGetCustomerInfoWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getCustomerInfo_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String esbResultCustomerId = getValueByExpression("//*[local-name()='customer_id']/text()", esbResponseElement);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCustomerInfo_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        String apiResultCustomerId = getValueByExpression("//*[local-name()='customer_id']/text()", apiResponseElement);
        
        String apiResultEmail = getValueByExpression("//*[local-name()='email']/text()", apiResponseElement);
        
        Assert.assertEquals(esbResultCustomerId, apiResultCustomerId);
        Assert.assertEquals(apiResultEmail, connectorProperties.getProperty("email"));
    }
    
    /**
     * Negative test case for getCustomerInfo method.
     */
    @Test(dependsOnMethods = { "testGetCustomerInfoWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Magento {getCustomerInfo} integration test negative case.")
    public void testGetCustomerInfoNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_getCustomerInfo_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_getCustomerInfo_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for updateCustomer method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateCustomerNegativeCase" }, groups = { "wso2.esb" }, description = "Magento {updateCustomer} integration test with optional parameters.")
    public void testUpdateCustomerWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_updateCustomer_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:customerCustomerUpdateResponse/result/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_updateCustomer_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:customerCustomerInfoResponse/customerInfo/customer_id/text())";
        String apiCustomerId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:customerCustomerInfoResponse/customerInfo/email/text())";
        String apiCustomerEmail = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:customerCustomerInfoResponse/customerInfo/firstname/text())";
        String apiCustomerFirstName = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiCustomerId, connectorProperties.getProperty("customerId"));
        Assert.assertEquals(apiCustomerEmail, connectorProperties.getProperty("updateEmail"));
        Assert.assertEquals(apiCustomerFirstName, connectorProperties.getProperty("updateFirstName"));
    }
    
    /**
     * Negative test case for updateCustomer method.
     */
    @Test(dependsOnMethods = { "testUpdateCustomerWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Magento {updateCustomer} integration test negative case.")
    public void testUpdateCustomerNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_updateCustomer_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_updateCustomer_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for createCustomerAddress method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCustomerNegativeCase" }, description = "Magento {createCustomerAddress} integration test with mandatory parameters.")
    public void testCreateCustomerAddressWithMandatoryParameters() throws Exception {
    
        parametersMap.put("company", "companyTest");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createCustomerAddress_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String addressId =
                (String) xPathEvaluate(esbResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressCreateResponse/result/text())", nameSpaceMap);
        
        connectorProperties.setProperty("addressId", addressId);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCustomerAddressInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String apiCustomerAddressId =
                (String) xPathEvaluate(apiResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/customer_address_id/text())",
                        nameSpaceMap);
        
        String apiCompany =
                (String) xPathEvaluate(apiResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/company/text())", nameSpaceMap);
        
        Assert.assertEquals(addressId, apiCustomerAddressId);
        Assert.assertEquals(parametersMap.get("company"), apiCompany);
    }
    
    /**
     * Positive test case for createCustomerAddress method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomerAddressWithMandatoryParameters" }, description = "Magento {createCustomerAddress} integration test with optional parameters.")
    public void testCreateCustomerAddressWithOptionalParameters() throws Exception {
    
        parametersMap.put("company", "companyTest");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createCustomerAddress_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String addressIdOptional =
                (String) xPathEvaluate(esbResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressCreateResponse/result/text())", nameSpaceMap);
        
        connectorProperties.setProperty("addressId", addressIdOptional);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCustomerAddressInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String apiCustomerAddressId =
                (String) xPathEvaluate(apiResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/customer_address_id/text())",
                        nameSpaceMap);
        
        boolean isBilling =
                Boolean.valueOf((String) xPathEvaluate(apiResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/is_default_billing/text())",
                        nameSpaceMap));
        
        Assert.assertTrue(isBilling);
        Assert.assertEquals(addressIdOptional, apiCustomerAddressId);
    }
    
    /**
     * Negative test case for createCustomerAddress.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {createCustomerAddress} integration test with negative case.")
    public void testCreateCustomerAddressNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        try {
            sendSOAPRequest(proxyUrl, "esb_createCustomerAddress_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        try {
            sendSOAPRequest(apiEndPoint, "api_createCustomerAddress_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
        
    }
    
    /**
     * Positive test case for getCustomerAddressInfo method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomerAddressWithOptionalParameters" }, description = "Magento {getCustomerAddressInfo} integration test with mandatory parameters.")
    public void testGetCustomerAddressInfoWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getCustomerAddressInfo_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCustomerAddressInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String esbCustomerAddressId =
                (String) xPathEvaluate(esbResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/customer_address_id/text())",
                        nameSpaceMap);
        String apiCustomerAddressId =
                (String) xPathEvaluate(apiResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/customer_address_id/text())",
                        nameSpaceMap);
        String esbCreatedAt =
                (String) xPathEvaluate(esbResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/created_at/text())", nameSpaceMap);
        String apiCreatedAt =
                (String) xPathEvaluate(apiResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/created_at/text())", nameSpaceMap);
        
        Assert.assertEquals(esbCustomerAddressId, apiCustomerAddressId);
        Assert.assertEquals(esbCreatedAt, apiCreatedAt);
    }
    
    /**
     * Negative test case for getCustomerAddressInfo.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {getCustomerAddressInfo} integration test with negative case.")
    public void testGetCustomerAddressInfoNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        try {
            sendSOAPRequest(proxyUrl, "esb_getCustomerAddressInfo_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_getCustomerAddressInfo_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
        
    }
    
    /**
     * Positive test case for updateCustomerAddress method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCustomerAddressInfoWithMandatoryParameters" }, description = "Magento {updateCustomerAddress} integration test with mandatory parameters.")
    public void testUpdateCustomerAddressWithMandatoryParameters() throws Exception {
    
        parametersMap.put("city", "updateCity");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_updateCustomerAddress_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCustomerAddressInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        boolean isUpdated =
                Boolean.valueOf((String) xPathEvaluate(esbResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressUpdateResponse/info/text())", nameSpaceMap));
        String apiUpdatedCity =
                (String) xPathEvaluate(apiResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/city/text())", nameSpaceMap);
        
        Assert.assertTrue(isUpdated);
        Assert.assertEquals(parametersMap.get("city"), apiUpdatedCity);
    }
    
    /**
     * Positive test case for updateCustomerAddress method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCustomerAddressWithMandatoryParameters" }, description = "Magento {updateCustomerAddress} integration test with optional parameters.")
    public void testUpdateCustomerAddressWithOptionalParameters() throws Exception {
    
        parametersMap.put("company", "updatedCompany");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_updateCustomerAddress_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCustomerAddressInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        boolean isUpdated =
                Boolean.valueOf((String) xPathEvaluate(esbResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressUpdateResponse/info/text())", nameSpaceMap));
        
        String apiUpdatedCity =
                (String) xPathEvaluate(apiResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/company/text())", nameSpaceMap);
        
        boolean isBilling =
                Boolean.valueOf((String) xPathEvaluate(apiResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressInfoResponse/info/is_default_billing/text())",
                        nameSpaceMap));
        
        Assert.assertTrue(isUpdated);
        Assert.assertTrue(isBilling);
        Assert.assertEquals(parametersMap.get("company"), apiUpdatedCity);
    }
    
    /**
     * Negative test case for updateCustomerAddress.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {updateCustomerAddress} integration test with negative case.")
    public void testUpdateCustomerAddressNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        try {
            sendSOAPRequest(proxyUrl, "esb_updateCustomerAddress_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        try {
            sendSOAPRequest(apiEndPoint, "api_updateCustomerAddress_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for deleteCustomerAddress method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCustomerAddressWithOptionalParameters" }, description = "Magento {deleteCustomerAddress} integration test with mandatory parameters.")
    public void testDeleteCustomerAddressWithMandatoryParameters() throws Exception {
    
        String apiFaultCodeElement = "";
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_deleteCustomerAddress_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        boolean isDeleted =
                Boolean.valueOf((String) xPathEvaluate(esbResponseElement,
                        "string(//soapenv:Body/ns1:customerAddressDeleteResponse/info/text())", nameSpaceMap));
        
        try {
            sendSOAPRequest(apiEndPoint, "api_getCustomerAddressInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertTrue(isDeleted);
        Assert.assertEquals(Integer.parseInt(apiFaultCodeElement), 102);
    }
    
    /**
     * Negative test case for deleteCustomerAddress.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {deleteCustomerAddress} integration test with negative case.")
    public void testDeleteCustomerAddressNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        try {
            sendSOAPRequest(proxyUrl, "esb_deleteCustomerAddress_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        try {
            sendSOAPRequest(apiEndPoint, "api_deleteCustomerAddress_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for deleteCustomer method with mandatory parameters.
     */
    @Test(expectedExceptions = AxisFault.class, dependsOnMethods = { "testDeleteCustomerAddressWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Magento {deleteCustomer} integration test with mandatory parameters.")
    public void testDeleteCustomerWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_deleteCustomer_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:customerCustomerDeleteResponse/result/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        sendSOAPRequest(apiEndPoint, "api_deleteCustomer_mandatory.xml", parametersMap, MAGENTO_ACTION,
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
    }
    
    /**
     * Negative test case for deleteCustomer method.
     */
    @Test(dependsOnMethods = { "testDeleteCustomerWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Magento {deleteCustomer} integration test negative case.")
    public void testDeleteCustomerNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_deleteCustomer_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_deleteCustomer_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for createShoppingCart method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {createShoppingCart} integration test with mandatory parameters.")
    public void testCreateShoppingCartWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createShoppingCart_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartCreateResponse/quoteId/text())";
        String esbQuoteId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        connectorProperties.setProperty("quoteId", esbQuoteId);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createShoppingCart_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/quote_id/text())";
        String apiQuoteId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiQuoteId, esbQuoteId);
    }
    
    /**
     * Positive test case for createShoppingCart method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {createShoppingCart} integration test with optional parameters.")
    public void testCreateShoppingCartWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createShoppingCart_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartCreateResponse/quoteId/text())";
        String esbQuoteId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        connectorProperties.setProperty("quoteIdOptional", esbQuoteId);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createShoppingCart_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/quote_id/text())";
        String apiQuoteId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiQuoteId, esbQuoteId);
    }
    
    /**
     * Negative test case for createShoppingCart method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {createShoppingCart} integration test negative case.")
    public void testCreateShoppingCartNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_createShoppingCart_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_createShoppingCart_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for getShoppingCartInfo method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateShoppingCartWithMandatoryParameters" }, description = "Magento {getShoppingCartInfo} integration test with mandatory parameters.")
    public void testGetShoppingCartInfoWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getShoppingCartInfo_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/quote_id/text())";
        String esbQuoteId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/store_id/text())";
        String esbStoreId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/items_count/text())";
        String esbItemsCount = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getShoppingCartInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/quote_id/text())";
        String apiQuoteId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/store_id/text())";
        String apiStoreId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/items_count/text())";
        String apiItemsCount = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiQuoteId, esbQuoteId);
        Assert.assertEquals(apiStoreId, esbStoreId);
        Assert.assertEquals(apiItemsCount, esbItemsCount);
    }
    
    /**
     * Positive test case for getShoppingCartInfo method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateShoppingCartWithOptionalParameters" }, description = "Magento {getShoppingCartInfo} integration test with optional parameters.")
    public void testGetShoppingCartInfoWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getShoppingCartInfo_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/quote_id/text())";
        String esbQuoteId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/store_id/text())";
        String esbStoreId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/items_count/text())";
        String esbItemsCount = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getShoppingCartInfo_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/quote_id/text())";
        String apiQuoteId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/store_id/text())";
        String apiStoreId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/items_count/text())";
        String apiItemsCount = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiQuoteId, esbQuoteId);
        Assert.assertEquals(apiStoreId, esbStoreId);
        Assert.assertEquals(apiItemsCount, esbItemsCount);
    }
    
    /**
     * Negative test case for getShoppingCartInfo method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {getShoppingCartInfo} integration test negative case.")
    public void testGetShoppingCartInfoNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_getShoppingCartInfo_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_getShoppingCartInfo_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for getShoppingCartTotals method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetShoppingCartInfoWithMandatoryParameters" }, description = "Magento {getShoppingCartTotals} integration test with mandatory parameters.")
    public void testGetShoppingCartTotalsWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getShoppingCartTotals_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "count(//soapenv:Body/ns1:shoppingCartTotalsResponse/result/item)";
        Double esbItemCount = (Double) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartTotalsResponse/result/item[1]/title/text())";
        String esbResultTitle = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getShoppingCartTotals_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "count(//soapenv:Body/ns1:shoppingCartTotalsResponse/result/item)";
        Double apiItemCount = (Double) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartTotalsResponse/result/item[1]/title/text())";
        String apiResultTitle = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiItemCount, esbItemCount);
        Assert.assertEquals(apiResultTitle, esbResultTitle);
    }
    
    /**
     * Positive test case for getShoppingCartTotals method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetShoppingCartInfoWithOptionalParameters" }, description = "Magento {getShoppingCartTotals} integration test with optional parameters.")
    public void testGetShoppingCartTotalsWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getShoppingCartTotals_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "count(//soapenv:Body/ns1:shoppingCartTotalsResponse/result/item)";
        Double esbItemCount = (Double) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartTotalsResponse/result/item[1]/title/text())";
        String esbResultTitle = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getShoppingCartTotals_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "count(//soapenv:Body/ns1:shoppingCartTotalsResponse/result/item)";
        Double apiItemCount = (Double) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartTotalsResponse/result/item[1]/title/text())";
        String apiResultTitle = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiItemCount, esbItemCount);
        Assert.assertEquals(apiResultTitle, esbResultTitle);
    }
    
    /**
     * Negative test case for getShoppingCartTotals method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {getShoppingCartTotals} integration test negative case.")
    public void testGetShoppingCartTotalsNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_getShoppingCartTotals_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_getShoppingCartTotals_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for setCustomerAddress method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetShoppingCartTotalsWithMandatoryParameters" }, description = "Magento {setCustomerAddress} integration test with mandatory parameters.")
    public void testSetCustomerAddressWithMandatoryParameters() throws Exception {
    
        parametersMap.put("setCustAddFirstName", "firstName");
        parametersMap.put("setCustAddLastName", "lastName");
        parametersMap.put("setCustAddFax", "24233553");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setCustomerAddresses_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartCustomerAddressesResponse/result/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_shoppingCartInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/billing_address/";
        
        String firstName = (String) xPathEvaluate(apiResponseElement, xPathExp + "firstname/text())", nameSpaceMap);
        String lastName = (String) xPathEvaluate(apiResponseElement, xPathExp + "lastname/text())", nameSpaceMap);
        String fax = (String) xPathEvaluate(apiResponseElement, xPathExp + "fax/text())", nameSpaceMap);
        
        Assert.assertEquals(firstName, parametersMap.get("setCustAddFirstName"));
        Assert.assertEquals(lastName, parametersMap.get("setCustAddLastName"));
        Assert.assertEquals(fax, parametersMap.get("setCustAddFax"));
    }
    
    /**
     * Positive test case for setCustomerAddress method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetShoppingCartTotalsWithOptionalParameters" }, description = "Magento {setCustomerAddress} integration test with optional parameters.")
    public void testSetCustomerAddressWithOptionalParameters() throws Exception {
    
        parametersMap.put("setCustAddFirstName", "firstName");
        parametersMap.put("setCustAddLastName", "lastName");
        parametersMap.put("setCustAddFax", "24233553");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setCustomerAddresses_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartCustomerAddressesResponse/result/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_shoppingCartInfo_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/billing_address/";
        
        String firstName = (String) xPathEvaluate(apiResponseElement, xPathExp + "firstname/text())", nameSpaceMap);
        String lastName = (String) xPathEvaluate(apiResponseElement, xPathExp + "lastname/text())", nameSpaceMap);
        String fax = (String) xPathEvaluate(apiResponseElement, xPathExp + "fax/text())", nameSpaceMap);
        
        Assert.assertEquals(firstName, parametersMap.get("setCustAddFirstName"));
        Assert.assertEquals(lastName, parametersMap.get("setCustAddLastName"));
        Assert.assertEquals(fax, parametersMap.get("setCustAddFax"));
    }
    
    /**
     * Negative test case for setCustomerAddress method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCustomerAddressWithMandatoryParameters" }, description = "Magento {setCustomerAddress} integration test with negative case.")
    public void testSetCustomerAddressWithNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_setCustomerAddresses_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_setCustomerAddresses_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for setCustomerInformation method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCustomerAddressWithNegativeCase" }, description = "Magento {setCustomerInformation} integration test with mandatory parameters.")
    public void testSetCustomerInformationWithMandatoryParameters() throws Exception {
    
        parametersMap.put("setCustInfoFirstName", "FIRST_NAME");
        parametersMap.put("setCustInfoLastName", "LAST_NAME");
        parametersMap.put("setCustInfoEmail", "abc@gmail.com");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setCustomerInformation_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartCustomerSetResponse/result/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_shoppingCartInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/";
        
        String firstName =
                (String) xPathEvaluate(apiResponseElement, xPathExp + "customer_firstname/text())", nameSpaceMap);
        String lastName =
                (String) xPathEvaluate(apiResponseElement, xPathExp + "customer_lastname/text())", nameSpaceMap);
        String email = (String) xPathEvaluate(apiResponseElement, xPathExp + "customer_email/text())", nameSpaceMap);
        
        Assert.assertEquals(firstName, parametersMap.get("setCustInfoFirstName"));
        Assert.assertEquals(lastName, parametersMap.get("setCustInfoLastName"));
        Assert.assertEquals(email, parametersMap.get("setCustInfoEmail"));
    }
    
    /**
     * Positive test case for setCustomerInformation method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCustomerAddressWithOptionalParameters" }, description = "Magento {setCustomerInformation} integration test with optional parameters.")
    public void testSetCustomerInformationWithOptionalParameters() throws Exception {
    
        parametersMap.put("setCustInfoFirstName", "FIRST_NAME");
        parametersMap.put("setCustInfoLastName", "LAST_NAME");
        parametersMap.put("setCustInfoEmail", "abc@gmail.com");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setCustomerInformation_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartCustomerSetResponse/result/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_shoppingCartInfo_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/";
        
        String firstName =
                (String) xPathEvaluate(apiResponseElement, xPathExp + "customer_firstname/text())", nameSpaceMap);
        String lastName =
                (String) xPathEvaluate(apiResponseElement, xPathExp + "customer_lastname/text())", nameSpaceMap);
        String email = (String) xPathEvaluate(apiResponseElement, xPathExp + "customer_email/text())", nameSpaceMap);
        
        Assert.assertEquals(firstName, parametersMap.get("setCustInfoFirstName"));
        Assert.assertEquals(lastName, parametersMap.get("setCustInfoLastName"));
        Assert.assertEquals(email, parametersMap.get("setCustInfoEmail"));
    }
    
    /**
     * Negative test case for setCustomerInformation method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCustomerInformationWithMandatoryParameters" }, description = "Magento {setCustomerInformation} integration test with negative case.")
    public void testSetCustomerInformationWithNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_setCustomerInformation_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
            
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_setCustomerInformation_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for AddCartProduct method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCustomerInformationWithNegativeCase" }, description = "Magento {addCartProduct} integration test with mandatory parameters.")
    public void testAddCartProductWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addCartProduct_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductAddResponse/result/text())";
        
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_addCartProduct_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductListResponse/result/item[1]/product_id/text())";
        String apiProductId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertTrue(apiSoapResponse.getBody().toString()
                .contains(connectorProperties.getProperty("productSKUMandatory")));
        Assert.assertTrue(apiSoapResponse.getBody().toString()
                .contains(connectorProperties.getProperty("productIdMandatory")));
        Assert.assertEquals(apiProductId, connectorProperties.getProperty("productIdMandatory"));
    }
    
    /**
     * Positive test case for addCartProduct method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCustomerInformationWithOptionalParameters" }, description = "Magento {addCartProduct} integration test with optional parameters.")
    public void testAddCartProductWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addCartProduct_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductAddResponse/result/text())";
        
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        /*
         * The listCartProducts method does not return the <quantity> attributes of the products it lists. There isn't a
         * getCartProduct method as well. Therefore the optional value cannot be retrieved through a direct API call and
         * subsequently asserted.
         */
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_addCartProduct_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        Assert.assertTrue(apiSoapResponse.getBody().toString()
                .contains(connectorProperties.getProperty("productSKUOptional")));
        Assert.assertTrue(apiSoapResponse.getBody().toString()
                .contains(connectorProperties.getProperty("productIdOptional")));
    }
    
    /**
     * Negative test case for addCartProduct method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddCartProductWithMandatoryParameters" }, description = "Magento {addCartProduct} integration test with negative case.")
    public void testAddCartProductWithNegativeParameters() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_addCartProduct_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_addCartProduct_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for updateCartProduct method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddCartProductWithNegativeParameters" }, description = "Magento {updateCartProduct} integration test with mandatory parameters.")
    public void testUpdateCartProductWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_updateCartProduct_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductUpdateResponse/result/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        /*
         * The listCartProducts method does not return the <quantity> attributes of the products it lists. There isn't a
         * getCartProduct method as well. Therefore the updated value cannot be retrieved through a direct API call and
         * subsequently asserted. Hence only the ESB response, returned as part of updateCartProduct is asserted to
         * true!
         */
    }
    
    /**
     * Positive test case for updateCartProduct method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddCartProductWithOptionalParameters" }, description = "Magento {updateCartProduct} integration test with optional parameters.")
    public void testUpdateCartProductWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_updateCartProduct_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductUpdateResponse/result/text())";
        
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        /*
         * The listCartProducts method does not return the <quantity> attributes of the products it lists. There isn't a
         * getCartProduct method as well. Therefore the updated value cannot be retrieved through a direct API call and
         * subsequently asserted. Hence only the ESB response, returned as part of updateCartProduct is asserted to
         * true!
         */
        
    }
    
    /**
     * Negative test case for updateCartProduct method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCartProductWithMandatoryParameters" }, description = "Magento {updateCartProduct} integration test with negative case.")
    public void testUpdateCartProductWithNegativeParameters() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_updateCartProduct_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getFaultCode().toString();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        try {
            sendSOAPRequest(apiEndPoint, "api_updateCartProduct_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultCode = af.getFaultCode().toString();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for removeCartProduct method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCartProductWithNegativeParameters" }, description = "Magento {removeCartProduct} integration test with mandatory parameters.")
    public void testRemoveCartProductWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_removeCartProduct_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductRemoveResponse/result/text())";
        
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_removeCartProduct_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        xPathExp = "string(//product_id)";
        String apiProductList = (String) xPathEvaluate(apiSoapResponse, xPathExp, nameSpaceMap);
        
        Assert.assertFalse(apiProductList.contains(connectorProperties.getProperty("productIdMandatory")));
    }
    
    /**
     * Positive test case for removeCartProduct method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCartProductWithOptionalParameters" }, description = "Magento {removeCartProduct} integration test with optional parameters.")
    public void testRemoveCartProductWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_removeCartProduct_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductRemoveResponse/result/text())";
        
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_removeCartProduct_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        xPathExp = "string(//product_id)";
        String apiProductList = (String) xPathEvaluate(apiSoapResponse, xPathExp, nameSpaceMap);
        
        Assert.assertFalse(apiProductList.contains(connectorProperties.getProperty("productIdMandatory")));
    }
    
    /**
     * Negative test case for removeCartProduct method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testRemoveCartProductWithMandatoryParameters" }, description = "Magento {removeCartProduct} integration test with negative case.")
    public void testRemoveCartProductWithNegativeParameters() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_removeCartProduct_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        try {
            sendSOAPRequest(apiEndPoint, "api_removeCartProduct_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for listCartProducts method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testRemoveCartProductWithNegativeParameters" }, description = "Magento {listCartProducts} integration test with mandatory parameters.")
    public void testListCartProductsWithMandatoryParameters() throws Exception {
    
        String xpathExpCount = "count(//result/item)";
        String xPathExpName = "string(//soapenv:Body/ns1:shoppingCartProductListResponse/result/item[1]/name/text())";
        String xPathExpId =
                "string(//soapenv:Body/ns1:shoppingCartProductListResponse/result/item[1]/product_id/text())";
        String xPathExpSku = "string(//soapenv:Body/ns1:shoppingCartProductListResponse/result/item[1]/sku/text())";
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_listCartProducts_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        Double esbResponseCount = (Double) xPathEvaluate(esbResponseElement, xpathExpCount, nameSpaceMap);
        String esbResponseName = (String) xPathEvaluate(esbResponseElement, xPathExpName, nameSpaceMap);
        String esbResponseId = (String) xPathEvaluate(esbResponseElement, xPathExpId, nameSpaceMap);
        String esbResponseSku = (String) xPathEvaluate(esbResponseElement, xPathExpSku, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_listCartProducts_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        Double apiResponseCount = (Double) xPathEvaluate(apiResponseElement, xpathExpCount, nameSpaceMap);
        String apiResponseName = (String) xPathEvaluate(apiResponseElement, xPathExpName, nameSpaceMap);
        String apiResponseId = (String) xPathEvaluate(apiResponseElement, xPathExpId, nameSpaceMap);
        String apiResponseSku = (String) xPathEvaluate(apiResponseElement, xPathExpSku, nameSpaceMap);
        
        Assert.assertEquals(esbResponseCount, apiResponseCount);
        Assert.assertEquals(esbResponseName, apiResponseName);
        Assert.assertEquals(esbResponseId, apiResponseId);
        Assert.assertEquals(esbResponseSku, apiResponseSku);
    }
    
    /**
     * Positive test case for listCartProducts method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testRemoveCartProductWithOptionalParameters" }, description = "Magento {listCartProducts} integration test with optional parameters.")
    public void testListCartProductsWithOptionalParameters() throws Exception {
    
        String xPathExpCount = "count(//result/item)";
        String xPathExpName = "string(//soapenv:Body/ns1:shoppingCartProductListResponse/result/item[1]/name/text())";
        String xPathExpId =
                "string(//soapenv:Body/ns1:shoppingCartProductListResponse/result/item[1]/product_id/text())";
        String xPathExpSku = "string(//soapenv:Body/ns1:shoppingCartProductListResponse/result/item[1]/sku/text())";
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_listCartProducts_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        Double esbResponseCount = (Double) xPathEvaluate(esbResponseElement, xPathExpCount, nameSpaceMap);
        String esbResponseName = (String) xPathEvaluate(esbResponseElement, xPathExpName, nameSpaceMap);
        String esbResponseId = (String) xPathEvaluate(esbResponseElement, xPathExpId, nameSpaceMap);
        String esbResponseSku = (String) xPathEvaluate(esbResponseElement, xPathExpSku, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_listCartProducts_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        Double apiResponseCount = (Double) xPathEvaluate(apiResponseElement, xPathExpCount, nameSpaceMap);
        String apiResponseName = (String) xPathEvaluate(apiResponseElement, xPathExpName, nameSpaceMap);
        String apiResponseId = (String) xPathEvaluate(apiResponseElement, xPathExpId, nameSpaceMap);
        String apiResponseSku = (String) xPathEvaluate(apiResponseElement, xPathExpSku, nameSpaceMap);
        
        Assert.assertEquals(esbResponseCount, apiResponseCount);
        Assert.assertEquals(esbResponseName, apiResponseName);
        Assert.assertEquals(esbResponseId, apiResponseId);
        Assert.assertEquals(esbResponseSku, apiResponseSku);
    }
    
    /**
     * Negative test case for listCartProducts method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {listCartProducts} integration test with negative case.")
    public void testlistCartProductsWithNegativeParameters() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_listCartProducts_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        try {
            sendSOAPRequest(apiEndPoint, "api_listCartProducts_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for setCartShippingMethod method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCartProductsWithMandatoryParameters" }, description = "Magento {setCartShippingMethod} integration test with mandatory parameters.")
    public void testSetCartShippingMethodWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setCartShippingMethod_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_setCartShippingMethod_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartShippingMethodResponse/result/text())";
        String esbResultValue = (String) xPathEvaluate(esbSoapResponse, xPathExp, nameSpaceMap);
        xPathExp = "string(//shipping_method/text())";
        String apiResultValue = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbResultValue, "true");
        Assert.assertEquals(apiResultValue, connectorProperties.getProperty("cartShippingMethod"));
    }
    
    /**
     * Positive test case for setCartShippingMethod method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCartProductsWithOptionalParameters" }, description = "Magento {setCartShippingMethod} integration test with optional parameters.")
    public void testSetCartShippingMethodWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setCartShippingMethod_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_setCartShippingMethod_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartShippingMethodResponse/result/text())";
        String esbResultValue = (String) xPathEvaluate(esbSoapResponse, xPathExp, nameSpaceMap);
        xPathExp = "string(//shipping_method/text())";
        String apiResultValue = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbResultValue, "true");
        Assert.assertEquals(apiResultValue, connectorProperties.getProperty("cartShippingMethod"));
    }
    
    /**
     * Negative test case for setCartShippingMethod method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCartShippingMethodWithMandatoryParameters" }, description = "Magento {setCartShippingMethod} integration test with negative case.")
    public void testSetCartShippingMethodWithNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_setCartShippingMethod_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_setCartShippingMethod_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for listAvailableCartShippingMethods method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCartShippingMethodWithNegativeCase" }, description = "Magento {listAvailableCartShippingMethods} integration test with mandatory parameters.")
    public void testListAvailableCartShippingMethodsWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_listAvailableCartShippingMethods_mandatory.xml", parametersMap,
                        "mediate", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "count(//soapenv:Body/ns1:shoppingCartShippingListResponse/result/item)";
        Double esbItemCount = (Double) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartShippingListResponse/result/item[1]/code/text())";
        String esbResultCode = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_listAvailableCartShippingMethods_mandatory.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "count(//soapenv:Body/ns1:shoppingCartShippingListResponse/result/item)";
        Double apiItemCount = (Double) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartShippingListResponse/result/item[1]/code/text())";
        String apiResultCode = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiItemCount, esbItemCount);
        Assert.assertEquals(apiResultCode, esbResultCode);
    }
    
    /**
     * Positive test case for listAvailableCartShippingMethods method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCartShippingMethodWithOptionalParameters" }, description = "Magento {listAvailableCartShippingMethods} integration test with optional parameters.")
    public void testListAvailableCartShippingMethodsWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_listAvailableCartShippingMethods_optional.xml", parametersMap,
                        "mediate", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "count(//soapenv:Body/ns1:shoppingCartShippingListResponse/result/item)";
        Double esbItemCount = (Double) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartShippingListResponse/result/item[1]/code/text())";
        String esbResultCode = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_listAvailableCartShippingMethods_optional.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "count(//soapenv:Body/ns1:shoppingCartShippingListResponse/result/item)";
        Double apiItemCount = (Double) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartShippingListResponse/result/item[1]/code/text())";
        String apiResultCode = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiItemCount, esbItemCount);
        Assert.assertEquals(apiResultCode, esbResultCode);
        
    }
    
    /**
     * Negative test case for listAvailableCartShippingMethods method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {listAvailableCartShippingMethods} integration test negative case.")
    public void testListAvailableCartShippingMethodsNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_listAvailableCartShippingMethods_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_listAvailableCartShippingMethods_negative.xml", parametersMap,
                    MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for setCartPaymentMethod method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListAvailableCartShippingMethodsWithMandatoryParameters" }, description = "Magento {setCartPaymentMethod} integration test with mandatory parameters.")
    public void testSetCartPaymentMethodWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setCartPaymentMethod_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_listAvailableCartPaymentMethods_mandatory.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartPaymentMethodResponse/result/text())";
        String esbResultValue = (String) xPathEvaluate(esbSoapResponse, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartPaymentListResponse/result/item/code/text())";
        String apiItemCode = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbResultValue, "true");
        Assert.assertEquals(apiItemCode, connectorProperties.getProperty("paymentDataMethod"));
    }
    
    /**
     * Positive test case for setCartPaymentMethod method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListAvailableCartShippingMethodsWithOptionalParameters" }, description = "Magento {setCartPaymentMethod} integration test with optional parameters.")
    public void testSetCartPaymentMethodWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setCartPaymentMethod_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_listAvailableCartPaymentMethods_optional.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartPaymentMethodResponse/result/text())";
        String esbResultValue = (String) xPathEvaluate(esbSoapResponse, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartPaymentListResponse/result/item/code/text())";
        String apiItemCode = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbResultValue, "true");
        Assert.assertEquals(apiItemCode, connectorProperties.getProperty("paymentDataMethod"));
    }
    
    /**
     * Negative test case for setCartPaymentMethod method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCartPaymentMethodWithMandatoryParameters" }, description = "Magento {setCartPaymentMethod} integration test with negative case.")
    public void testSetCartPaymentMethodWithNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_setCartPaymentMethod_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_setCartPaymentMethod_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for listAvailableCartPaymentMethods method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCartPaymentMethodWithNegativeCase" }, description = "Magento {listAvailableCartPaymentMethods} integration test with mandatory parameters.")
    public void testListAvailableCartPaymentMethodsWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_listAvailableCartPaymentMethods_mandatory.xml", parametersMap,
                        "mediate", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "count(//soapenv:Body/ns1:shoppingCartPaymentListResponse/result/item)";
        Double esbResultCount = (Double) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_listAvailableCartPaymentMethods_mandatory.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "count(//soapenv:Body/ns1:shoppingCartPaymentListResponse/result/item)";
        Double apiResultCount = (Double) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiResultCount, esbResultCount);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductListResponse/result/item[0]/title/text())";
        String apiItemTitle = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        String esbItemTitle = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        Assert.assertEquals(apiItemTitle, esbItemTitle);
    }
    
    /**
     * Positive test case for listAvailableCartPaymentMethods method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCartPaymentMethodWithOptionalParameters" }, description = "Magento {listAvailableCartPaymentMethods} integration test with optional parameters.")
    public void testListAvailableCartPaymentMethodsWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_listAvailableCartPaymentMethods_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "count(//soapenv:Body/ns1:shoppingCartPaymentListResponse/result/item)";
        Double esbResultCount = (Double) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_listAvailableCartPaymentMethods_optional.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "count(//soapenv:Body/ns1:shoppingCartPaymentListResponse/result/item)";
        Double apiResultCount = (Double) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiResultCount, esbResultCount);
        
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartProductListResponse/result/item[0]/title/text())";
        String apiItemTitle = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        String esbItemTitle = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        Assert.assertEquals(apiItemTitle, esbItemTitle);
    }
    
    /**
     * Negative test case for listAvailableCartPaymentMethods method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {listAvailableCartPaymentMethods} integration test negative case.")
    public void testListAvailableCartPaymentMethodsNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_listAvailableCartPaymentMethods_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_listAvailableCartPaymentMethods_negative.xml", parametersMap,
                    MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for createOrderFromShoppingCart method with mandatory parameters. Prerequisites of this test
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListAvailableCartPaymentMethodsWithMandatoryParameters" }, description = "Magento {createOrderFromShoppingCart} integration test with mandatory parameters.")
    public void testCreateOrderFromShoppingCartWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createOrderFromShoppingCart_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartOrderResponse/result/text())";
        String esbResultOrderId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        connectorProperties.setProperty("orderIdCreated", esbResultOrderId);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createOrderFromShoppingCart_mandatory.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/reserved_order_id/text())";
        String apiResultOrderId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiResultOrderId, esbResultOrderId);
    }
    
    /**
     * Positive test case for createOrderFromShoppingCart method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListAvailableCartPaymentMethodsWithOptionalParameters" }, description = "Magento {createOrderFromShoppingCart} integration test with optional parameters.")
    public void testCreateOrderFromShoppingCartWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createOrderFromShoppingCart_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:shoppingCartOrderResponse/result/text())";
        String esbResultOrderId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        connectorProperties.setProperty("orderIdCreated", esbResultOrderId);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createOrderFromShoppingCart_optional.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:shoppingCartInfoResponse/result/reserved_order_id/text())";
        String apiResultOrderId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiResultOrderId, esbResultOrderId);
    }
    
    /**
     * Negative test case for createOrderFromShoppingCart method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {createOrderFromShoppingCart} integration test negative case.")
    public void testCreateOrderFromShoppingCartNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_createOrderFromShoppingCart_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_createOrderFromShoppingCart_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for getOrderInfo method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {getOrderInfo} integration test with mandatory parameters.")
    public void testGetOrderInfoWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getOrderInfo_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getOrderInfo_mandatory.xml", parametersMap, "getOrderInfo",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderInfoResponse/result/increment_id/text())";
        String esbSalesOrderIncrementId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiSalesOrderIncrementId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderInfoResponse/result/store_id/text())";
        String apiStoreId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        String esbStoreId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSalesOrderIncrementId, connectorProperties.getProperty("orderIncrementId"));
        Assert.assertEquals(esbSalesOrderIncrementId, apiSalesOrderIncrementId);
        Assert.assertEquals(apiStoreId, esbStoreId);
        
    }
    
    /**
     * Negative test case for getOrderInfo method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {getOrderInfo} integration test with Negative parameters.")
    public void testGetOrderInfoWithNegativeParameters() throws Exception {
    
        String apiFaultCode = "";
        String esbFaultCode = "";
        String apiFaultCodeElement = "";
        String esbFaultCodeElement = "";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_getOrderInfo_negative.xml", parametersMap, "mediate", SOAP_HEADER_XPATH_EXP,
                    SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_getOrderInfo_negative.xml", parametersMap, "getOrderInfo",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for addCommentToOrder method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetOrderInfoWithMandatoryParameters" }, description = "Magento {addCommentToOrder} integration test with mandatory parameters.")
    public void testAddCommentToOrderWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addCommentToOrder_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderAddCommentResponse/result/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_addCommentToOrder_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderInfoResponse/result/status_history/item[1]/";
        String status = (String) xPathEvaluate(apiResponseElement, xPathExp + "status/text())", nameSpaceMap);
        
        Assert.assertEquals(status, connectorProperties.getProperty("status"));
    }
    
    /**
     * Positive test case for addCommentToOrder method with Optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddCommentToOrderWithMandatoryParameters" }, description = "Magento {addCommentToOrder} integration test with Optional parameters.")
    public void testAddCommentToOrderWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addCommentToOrder_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderAddCommentResponse/result/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_addCommentToOrder_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderInfoResponse/result/status_history/item[1]/";
        String status = (String) xPathEvaluate(apiResponseElement, xPathExp + "status/text())", nameSpaceMap);
        String comment = (String) xPathEvaluate(apiResponseElement, xPathExp + "comment/text())", nameSpaceMap);
        
        Assert.assertEquals(status, connectorProperties.getProperty("status"));
        Assert.assertEquals(comment, connectorProperties.getProperty("comment"));
    }
    
    /**
     * Negative test case for addCommentToOrder method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {addCommentToOrder} integration test with negative case.")
    public void testAddCommentToOrderWithNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_addCommentToOrder_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_addCommentToOrder_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for createInvoice method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddCommentToOrderWithOptionalParameters" }, description = "Magento {createInvoice} integration test with mandatory parameters.")
    public void testCreateInvoiceWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createInvoice_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        Assert.assertTrue(esbResponseElement.toString().contains("salesOrderInvoiceCreateResponse"));
        
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderInvoiceCreateResponse/result/text())";
        String invoiceIncrementId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        connectorProperties.setProperty("invoiceIncrementId", invoiceIncrementId);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createInvoice_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:salesOrderInvoiceInfoResponse/result/order_increment_id/text())";
        String apiOrderIncrementId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiOrderIncrementId, connectorProperties.getProperty("orderIncrementId"));
    }
    
    /**
     * Positive test case for createInvoice method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {createInvoice} integration test with optional parameters.")
    public void testCreateInvoiceWithOptionalParameters() throws Exception {
    
        SOAPEnvelope apiCreateCartSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createInvoice_optional_createCart.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        String xPathExp = "string(//quoteId/text())";
        String createdQuoteId = (String) xPathEvaluate(apiCreateCartSoapResponse, xPathExp, nameSpaceMap);
        connectorProperties.setProperty("createInvoiceOptionalQuoteId", createdQuoteId);
        
        sendSOAPRequest(apiEndPoint, "api_createInvoice_optional_addCartProduct.xml", parametersMap, MAGENTO_ACTION,
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        sendSOAPRequest(apiEndPoint, "api_createInvoice_optional_shoppingCartCustomerAdd.xml", parametersMap,
                MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        sendSOAPRequest(apiEndPoint, "api_createInvoice_optional_setCartCustomerAddress.xml", parametersMap,
                MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        sendSOAPRequest(apiEndPoint, "api_createInvoice_optional_setCartPaymentMethod.xml", parametersMap,
                MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        sendSOAPRequest(apiEndPoint, "api_createInvoice_optional_setCartShippingMethod.xml", parametersMap,
                MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        SOAPEnvelope apiCreateOrderFromCartSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createInvoice_optional_orderCreate.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        xPathExp = "string(//result/text())";
        String createInvoiceOptionalOrderIncrementId =
                (String) xPathEvaluate(apiCreateOrderFromCartSoapResponse, xPathExp, nameSpaceMap);
        connectorProperties.setProperty("createInvoiceOptOrderIncId", createInvoiceOptionalOrderIncrementId);
        
        SOAPEnvelope apiOrderInfoSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createInvoice_optional_getOrderInfo.xml", parametersMap,
                        MAGENTO_ACTION, SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        xPathExp = "string(//order_item_id/text())";
        String orderItemId = (String) xPathEvaluate(apiOrderInfoSoapResponse, xPathExp, nameSpaceMap);
        connectorProperties.setProperty("orderItemId", orderItemId);
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createInvoice_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        Assert.assertTrue(esbResponseElement.toString().contains("salesOrderInvoiceCreateResponse"));
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderInvoiceCreateResponse/result/text())";
        String optionalInvoiceIncrementId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        parametersMap.put("optInvoiceIncId", optionalInvoiceIncrementId);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createInvoice_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//soapenv:Body/ns1:salesOrderInvoiceInfoResponse/result/order_increment_id/text())";
        String apiOptionalOrderIncrementId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiOptionalOrderIncrementId, connectorProperties.getProperty("createInvoiceOptOrderIncId"));
    }
    
    /**
     * Negative test case for createInvoice method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {createInvoice} negative case integration test.")
    public void testCreateInvoiceNegativeCase() throws Exception {
    
        String esbFaultCode = "esbFaultCode";
        String esbFaultMessage = "esbFaultMessage";
        String apiFaultCode = "apiFaultCode";
        String apiFaultMessage = "apiFaultMessage";
        try {
            sendSOAPRequest(proxyUrl, "esb_createInvoice_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultMessage = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_createInvoice_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultMessage = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(esbFaultCode, apiFaultCode);
        Assert.assertEquals(esbFaultMessage, apiFaultMessage);
    }
    
    /**
     * Positive test case for getInvoiceInfo method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateInvoiceWithOptionalParameters" }, description = "Magento {getInvoiceInfo} integration test with mandatory parameters.")
    public void testGetInvoiceInfoWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getInvoiceInfo_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getInvoiceInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderInvoiceInfoResponse/result/store_id/text())";
        String esbStoreId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiStoreId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderInvoiceInfoResponse/result/invoice_id/text())";
        String esbInvoiceId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiInvoiceId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogInventoryStockItemListResponse/result/grand_total/text())";
        String esbGrandTotal = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiGrandTotal = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:catalogInventoryStockItemListResponse/result/shipping_amount/text())";
        String esbShippingAmount = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiShippingAmount = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbStoreId, apiStoreId);
        Assert.assertEquals(esbInvoiceId, apiInvoiceId);
        Assert.assertEquals(esbGrandTotal, apiGrandTotal);
        Assert.assertEquals(esbShippingAmount, apiShippingAmount);
    }
    
    /**
     * Negative test case for getInvoiceInfo method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {getInvoiceInfo} integration test with negative case.")
    public void testGetInvoiceInfoWithNegativeCase() throws Exception {
    
        String apiFaultString = "apiFaultString";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultString = "esbFaultString";
        String esbFaultCodeElement = "esbFaultCodeElement";
        try {
            sendSOAPRequest(proxyUrl, "esb_getInvoiceInfo_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultString = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_getInvoiceInfo_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultString = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultString, esbFaultString);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for createShipment method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetInvoiceInfoWithMandatoryParameters" }, description = "Magento {createShipment} integration test with mandatory parameters.")
    public void testCreateShipmentWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createShipment_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentCreateResponse/shipmentIncrementId/text())";
        String esbShipmentIncrementId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        connectorProperties.put("shipmentIncrementId", esbShipmentIncrementId);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createShipment_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentInfoResponse/result/increment_id/text())";
        String apiShipmentIncrementId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentInfoResponse/result/created_at/text())";
        String apiShipmentCreatedDate = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        String shipmentCreatedDate = apiShipmentCreatedDate.split(" ")[0];
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf1.format(new java.util.Date());
        
        Assert.assertEquals(connectorProperties.getProperty("shipmentIncrementId"), apiShipmentIncrementId);
        Assert.assertEquals(shipmentCreatedDate, today);
    }
    
    /**
     * Positive test case for createShipment method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateInvoiceWithOptionalParameters" }, description = "Magento {createShipment} integration test with optional parameters.")
    public void testCreateShipmentWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createShipment_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentCreateResponse/shipmentIncrementId/text())";
        String esbShipmentIncrementId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        connectorProperties.put("shipmentIncrementId", esbShipmentIncrementId);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_createShipment_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentInfoResponse/result/increment_id/text())";
        String apiShipmentIncrementId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentInfoResponse/result/created_at/text())";
        String apiShipmentCreatedDate = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        String shipmentCreatedDate = apiShipmentCreatedDate.split(" ")[0];
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf1.format(new java.util.Date());
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentInfoResponse/result/comments/item[1]/comment/text())";
        String apiShipmentComment = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(connectorProperties.getProperty("shipmentIncrementId"), apiShipmentIncrementId);
        Assert.assertEquals(shipmentCreatedDate, today);
        Assert.assertEquals(connectorProperties.getProperty("comment"), apiShipmentComment);
    }
    
    /**
     * Negative test case for createShipment method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {createShipment} integration test negative case.")
    public void testCreateShipmentNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_createShipment_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_createShipment_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for getShipmentInfo method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateShipmentWithMandatoryParameters" }, description = "Magento {getShipmentInfo} integration test with mandatory parameters.")
    public void testGetShipmentInfoWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getShipmentInfo_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getShipmentInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentInfoResponse/result/increment_id/text())";
        String apiShipmentIncrementId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentInfoResponse/result/store_id/text())";
        String esbStoreId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiStoreId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentInfoResponse/result/shipping_address_id/text())";
        String esbShippingAddressId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiShippingAddressId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiShipmentIncrementId, connectorProperties.getProperty("shipmentIncrementId"));
        Assert.assertEquals(apiStoreId, esbStoreId);
        Assert.assertEquals(apiShippingAddressId, esbShippingAddressId);
    }
    
    /**
     * Negative test case for getShipmentInfo method.
     */
    @Test(groups = { "wso2.esb" }, description = "Netsuite {getShipmentInfo} integration test with mandatory parameters.")
    public void testGetShipmentInfoNegativeCase() throws Exception {
    
        String apiFaultCode = "";
        String apiFaultCodeElement = "";
        String esbFaultCode = "";
        String esbFaultCodeElement = "";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_getShipmentInfo_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        try {
            sendSOAPRequest(apiEndPoint, "api_getShipmentInfo_negative.xml", parametersMap, "getShipmentInfo",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
            
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for addCommentToShipment method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetShipmentInfoWithMandatoryParameters" }, description = "Magento {addCommentToShipment} integration test with optional parameters.")
    public void testAddCommentToShipmentWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addCommentToShipment_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentAddCommentResponse/shipmentIncrementId/text())";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));
        Assert.assertTrue(isSuccess);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_addCommentToShipment_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//soapenv:Body/ns1:salesOrderShipmentInfoResponse/result/comments/item[1]/comment/text())";
        String apiShipmentComment = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(connectorProperties.getProperty("addComment"), apiShipmentComment);
    }
    
    /**
     * Negative test case for addCommentToShipment method.
     */
    @Test(groups = { "wso2.esb" }, description = "Magento {addCommentToShipment} integration test negative case.")
    public void testAddCommentToShipmentNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_addCommentToShipment_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_addCommentToShipment_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for createCreditMemo method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddCommentToShipmentWithOptionalParameters" }, description = "Magento {createCreditMemo} integration test with mandatory parameters.")
    public void testCreateCreditMemoWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createCreditMemo_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderCreditmemoCreateResponse/result/text())";
        String creditMemoIncrementID = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        connectorProperties.setProperty("incrementId", creditMemoIncrementID);
        
        parametersMap.put("incrementId", creditMemoIncrementID);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCreditMemoInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        String xPathExpIncId = "string(//soapenv:Body/ns1:salesOrderCreditmemoInfoResponse/result/increment_id/text())";
        String xPathExpCurrCode =
                "string(//soapenv:Body/ns1:salesOrderCreditmemoInfoResponse/result/global_currency_code/text())";
        
        Assert.assertEquals(creditMemoIncrementID,
                (String) xPathEvaluate(apiResponseElement, xPathExpIncId, nameSpaceMap));
        Assert.assertNotNull((String) xPathEvaluate(apiResponseElement, xPathExpCurrCode, nameSpaceMap));
    }
    
    /**
     * Positive test case for createCreditMemo method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCreditMemoWithMandatoryParameters" }, description = "Magento {createCreditMemo} integration test with optional parameters.")
    public void testCreateCreditMemoWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createCreditMemo_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//soapenv:Body/ns1:salesOrderCreditmemoCreateResponse/result/text())";
        String incrementIDOptional = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        connectorProperties.setProperty("incrementIdOptional", incrementIDOptional);
        
        parametersMap.put("incrementIdOptional", incrementIDOptional);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCreditMemoInfo_optional.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        String xPathExpIncId = "string(//soapenv:Body/ns1:salesOrderCreditmemoInfoResponse/result/increment_id/text())";
        String xPathExpComment =
                "string(//soapenv:Body/ns1:salesOrderCreditmemoInfoResponse/result/comments/item/comment/text())";
        
        Assert.assertEquals(incrementIDOptional,
                (String) xPathEvaluate(apiResponseElement, xPathExpIncId, nameSpaceMap));
        Assert.assertEquals(connectorProperties.getProperty("comment"),
                (String) xPathEvaluate(apiResponseElement, xPathExpComment, nameSpaceMap));
    }
    
    /**
     * Negative test case for createCreditMemo method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {createCreditMemo} integration test with negative case.")
    public void testCreateCreditMemoWithNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_createCreditMemo_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_createCreditMemo_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for getCreditMemoInfo method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCreditMemoWithOptionalParameters" }, description = "Magento {getCreditMemoInfo} integration test with mandatory parameters.")
    public void testGetCreditMemoInfoWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getCreditMemoInfo_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCreditMemoInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String xPathExpIncId = "string(//soapenv:Body/ns1:salesOrderCreditmemoInfoResponse/result/increment_id/text())";
        String xPathExpGCC =
                "string(//soapenv:Body/ns1:salesOrderCreditmemoInfoResponse/result/global_currency_code/text())";
        
        Assert.assertEquals((String) xPathEvaluate(esbResponseElement, xPathExpIncId, nameSpaceMap),
                (String) xPathEvaluate(apiResponseElement, xPathExpIncId, nameSpaceMap));
        Assert.assertEquals((String) xPathEvaluate(esbResponseElement, xPathExpGCC, nameSpaceMap),
                (String) xPathEvaluate(apiResponseElement, xPathExpGCC, nameSpaceMap));
    }
    
    /**
     * Negative test case for getCreditMemoInfo method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {getCreditMemoInfo} integration test with negative case.")
    public void testGetCreditMemoInfoWithNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_getCreditMemoInfo_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_getCreditMemoInfo_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
    /**
     * Positive test case for addCommentToCreditMemo method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCreditMemoInfoWithMandatoryParameters" }, description = "Magento {addCommentToCreditMemo} integration test with mandatory parameters.")
    public void testAddCommentToCreditMemoWithMandatoryParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addCommentToCreditMemo_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCreditMemoInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String xPathExpIncId = "string(//soapenv:Body/ns1:salesOrderCreditmemoInfoResponse/result/increment_id/text())";
        String xPathExpCurrCode =
                "string(//soapenv:Body/ns1:salesOrderCreditmemoInfoResponse/result/global_currency_code/text())";
        String xPathExpResult = "string(//soapenv:Body/ns1:salesOrderCreditmemoAddCommentResponse/result/text())";
        
        Assert.assertTrue(Boolean
                .parseBoolean((String) xPathEvaluate(esbResponseElement, xPathExpResult, nameSpaceMap)));
        Assert.assertEquals(connectorProperties.getProperty("incrementId"),
                (String) xPathEvaluate(apiResponseElement, xPathExpIncId, nameSpaceMap));
        Assert.assertNotNull((String) xPathEvaluate(apiResponseElement, xPathExpCurrCode, nameSpaceMap));
    }
    
    /**
     * Positive test case for addCommentToCreditMemo method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddCommentToCreditMemoWithMandatoryParameters" }, description = "Magento {addCommentToCreditMemo} integration test with optional parameters.")
    public void testAddCommentToCreditMemoWithOptionalParameters() throws Exception {
    
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addCommentToCreditMemo_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(apiEndPoint, "api_getCreditMemoInfo_mandatory.xml", parametersMap, MAGENTO_ACTION,
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        String xPathExpIncId = "string(//soapenv:Body/ns1:salesOrderCreditmemoInfoResponse/result/increment_id/text())";
        String xPathExpResult = "string(//soapenv:Body/ns1:salesOrderCreditmemoAddCommentResponse/result/text())";
        String xPathExpComment =
                "string(//soapenv:Body/ns1:salesOrderCreditmemoInfoResponse/result/comments/item/comment/text())";
        
        Assert.assertTrue(Boolean
                .parseBoolean((String) xPathEvaluate(esbResponseElement, xPathExpResult, nameSpaceMap)));
        Assert.assertEquals(connectorProperties.getProperty("incrementId"),
                (String) xPathEvaluate(apiResponseElement, xPathExpIncId, nameSpaceMap));
        Assert.assertEquals(connectorProperties.getProperty("commentOptional"),
                (String) xPathEvaluate(apiResponseElement, xPathExpComment, nameSpaceMap));
        
    }
    
    /**
     * Negative test case for addCommentToCreditMemo method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Magento {addCommentToCreditMemo} integration test with negative case.")
    public void testAddCommentToCreditMemoWithNegativeCase() throws Exception {
    
        String apiFaultCode = "apiFaultCode";
        String apiFaultCodeElement = "apiFaultCodeElement";
        String esbFaultCode = "esbFaultCode";
        String esbFaultCodeElement = "esbFaultCodeElement";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_addCommentToCreditMemo_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        try {
            sendSOAPRequest(apiEndPoint, "api_addCommentToCreditMemo_negative.xml", parametersMap, MAGENTO_ACTION,
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
    
}
