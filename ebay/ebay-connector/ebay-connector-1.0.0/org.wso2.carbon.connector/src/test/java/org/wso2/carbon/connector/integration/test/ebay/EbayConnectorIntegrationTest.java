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

package org.wso2.carbon.connector.integration.test.ebay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class EbayConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> shoppingApiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    Map<String, String> nameSpaceMap = new HashMap<String, String>();
    
    private final String SOAP_HEADER_XPATH_EXP = "/soapenv:Envelope/soapenv:Header/*";
    
    private final String SOAP_BODY_XPATH_EXP = "/soapenv:Envelope/soapenv:Body/*";
    
    private String tradingApiEndpoint;
    
    private String shoppingApiEndpoint;
	
	private long timeOut;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("ebay-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        nameSpaceMap.put("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        nameSpaceMap.put("ebl", "urn:ebay:apis:eBLBaseComponents");
        
        tradingApiEndpoint =
                connectorProperties.getProperty("tradingApiUrl") + "?siteid="
                        + connectorProperties.getProperty("siteId") + "&" + "appid="
                        + connectorProperties.getProperty("appId") + "&" + "routing="
                        + connectorProperties.getProperty("routing");
        
        shoppingApiEndpoint = connectorProperties.getProperty("shoppingApiUrl");
		timeOut=Long.parseLong(connectorProperties.getProperty("timeOut"));
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
    }
    
    /**
     * Positive test case for setStoreCategories method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Ebay {setStoreCategories} integration test with mandatory parameters.")
    public void testSetStoreCategoriesWithMandatoryParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setStoreCategories_mandatory.xml", null, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:SetStoreCategoriesResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp =
                "string(//ebl:SetStoreCategoriesResponse/ebl:CustomCategory/ebl:CustomCategory/ebl:CategoryID/text())";
        String esbCategoryID = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        connectorProperties.setProperty("customCategoryId", esbCategoryID);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetStore", "api_setStoreCategories_mandatory.xml",
                        null, "GetStore", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp =
                "string(//ebl:GetStoreResponse/ebl:Store/ebl:CustomCategories/ebl:CustomCategory/ebl:CategoryID/text())";
        String apiCategoryID = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbCategoryID, apiCategoryID);
        
        xPathExp = "string(//ebl:SetStoreCategoriesResponse/ebl:CustomCategory/ebl:CustomCategory/ebl:Name/text())";
        String esbCategoryName = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Store/ebl:CustomCategories/ebl:CustomCategory/ebl:Name/text())";
        String apiCategoryName = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbCategoryName, apiCategoryName);
    }
    
    /**
     * Positive test case for setStoreCategories method with optional parameters.
     */
    @Test(dependsOnMethods = { "testSetStoreCategoriesWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Ebay {setStoreCategories} integration test with optional parameters.")
    public void testSetStoreCategoriesWithOptionalParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setStoreCategories_optional.xml", null, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:SetStoreCategoriesResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp = "string(//ebl:SetStoreCategoriesResponse/ebl:Status/text())";
        String esbStatus = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbStatus, "Complete");
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetStore", "api_setStoreCategories_optional.xml", null,
                        "GetStore", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Store/ebl:CustomCategories/ebl:CustomCategory/ebl:Name/text())";
        String apiCategoryName = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(connectorProperties.getProperty("nameOptional"), apiCategoryName);
    }
    
    /**
     * Negative test case for setStoreCategories method.
     */
    @Test(dependsOnMethods = { "testSetStoreCategoriesWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Ebay {setStoreCategories} integration test negative case.")
    public void testSetStoreCategoriesNegativeCase() throws Exception {
		
		Thread.sleep(timeOut);
		
        String apiFaultString = "apiFaultString";
        String apiErrorCode = "apiErrorCode";
        String esbFaultString = "esbFaultString";
        String esbErrorCode = "esbErrorCode";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_setStoreCategories_negative.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                    SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultString = af.getMessage();
            OMElement esbErrorDetailElement = af.getDetail();
            esbErrorCode = (String) xPathEvaluate(esbErrorDetailElement, "string(//ErrorCode/text())", nameSpaceMap);
        }
        
        try {
            sendSOAPRequest(tradingApiEndpoint + "&callname=SetStoreCategories", "api_setStoreCategories_negative.xml",
                    parametersMap, "SetStoreCategories", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultString = af.getMessage();
            OMElement apiErrorDetailElement = af.getDetail();
            apiErrorCode = (String) xPathEvaluate(apiErrorDetailElement, "string(//ErrorCode/text())", nameSpaceMap);
        }
        Assert.assertEquals(esbFaultString, apiFaultString);
        Assert.assertEquals(esbErrorCode, apiErrorCode);
    }
    
    /**
     * Positive test case for getStores method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testSetStoreCategoriesNegativeCase" }, groups = { "wso2.esb" }, description = "eBay {getStores} integration test with mandatory parameters.")
    public void testGetStoresWithMandatoryParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getStores_mandatory.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:GetStoreResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Store/ebl:Name/text())";
        String esbStoreName = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetStore", "api_getStores_mandatory.xml", null,
                        "GetStore", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Ack/text())";
        
        String apiSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiSuccess, "Success");
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Store/ebl:Name/text())";
        String apiStoreName = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbStoreName, apiStoreName);
    }
    
    /**
     * Positive test case for getStores method with optional parameters.
     */
    @Test(dependsOnMethods = { "testGetStoresWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "eBay {getStores} integration test with optional parameters.")
    public void testGetStoresWithOptionalParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getStores_optional.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:GetStoreResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Store/ebl:Name/text())";
        String esbStoreName = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetStore", "api_getStores_optional.xml", null,
                        "GetStore", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Ack/text())";
        
        String apiSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiSuccess, "Success");
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Store/ebl:Name/text())";
        String apiStoreName = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbStoreName, apiStoreName);
    }
    
    /**
     * Negative test case for getStores method.
     */
    @Test(dependsOnMethods = { "testGetStoresWithOptionalParameters" }, groups = { "wso2.esb" }, description = "eBay {getStores} integration test with negative case.")
    public void testGetStoresNegativeCase() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getStores_negative.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:GetStoreResponse/ebl:Ack/text())";
        String esbAck = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Errors/ebl:ShortMessage/text())";
        String esbShortMessage = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetStore", "api_getStores_negative.xml", null,
                        "GetStore", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Ack/text())";
        
        String apiAck = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//ebl:GetStoreResponse/ebl:Errors/ebl:ShortMessage/text())";
        String apiShortMessage = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbAck, apiAck);
        Assert.assertEquals(esbShortMessage, apiShortMessage);
    }
    
    /**
     * Positive test case for addItem method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testGetStoresNegativeCase", "testSetPromotionalSaleWithMandatoryParameters"}, 
    		groups = { "wso2.esb" }, description = "eBay {addItem} integration test with mandatory parameters.")
    public void testAddItemWithMandatoryParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        String uuid = buildItemUUID();
        
        parametersMap.put("uuid", uuid);
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addItem_mandatory.xml", parametersMap, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp = "string(//ebl:ItemID/text())";
        
        String itemID = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        parametersMap.put("itemID", itemID);
        connectorProperties.setProperty("itemIdMandatory", itemID);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetItem", "api_addItem_mandatory.xml", parametersMap,
                        "AddItem", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:Ack/text())";
        
        String apiSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiSuccess, "Success");
    }
    
    /**
     * Positive test case for addItem method with optional parameters.
     */
    @Test(dependsOnMethods = { "testAddItemWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "eBay {addItem} integration test with optional parameters.")
    public void testAddItemWithOptionalParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        String uuid = buildItemUUID();
        
        parametersMap.put("uuid", uuid);
        parametersMap.put("refundDescription", "Test Optional Description");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addItem_optional.xml", parametersMap, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp = "string(//ebl:ItemID/text())";
        
        String itemID = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        parametersMap.put("itemID", itemID);
        connectorProperties.setProperty("leadItemId", itemID);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetItem", "api_addItem_optional.xml", parametersMap,
                        "AddItem", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:Ack/text())";
        
        String apiSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiSuccess, "Success");
        
        xPathExp = "string(//ebl:ListingType/text())";
        
        String apiListingType = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiListingType, "LeadGeneration");
    }
    
    /**
     * Negative test case for addItem method.
     */
    @Test(dependsOnMethods = { "testAddItemWithOptionalParameters" }, groups = { "wso2.esb" }, description = "eBay {addItem} integration test negative case.")
    public void testAddItemNegativeCase() throws Exception {
		
		Thread.sleep(timeOut);
		
        String uuid = buildItemUUID() + "inv";
        
        parametersMap.put("uuid", uuid);
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addItem_negative.xml", parametersMap, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:AddItemResponse/ebl:Ack/text())";
        String esbAck = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//ebl:AddItemResponse/ebl:Errors/ebl:ShortMessage/text())";
        String esbShortMessage = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=AddItem", "api_addItem_negative.xml", parametersMap,
                        "AddItem", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:AddItemResponse/ebl:Ack/text())";
        
        String apiAck = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//ebl:AddItemResponse/ebl:Errors/ebl:ShortMessage/text())";
        String apiShortMessage = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbAck, apiAck);
        Assert.assertEquals(esbShortMessage, apiShortMessage);
    }
    
    /**
     * Positive test case for setPromotionalSale method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "Ebay {setPromotionalSale} integration test with mandatory parameters.")
    public void testSetPromotionalSaleWithMandatoryParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        
        String startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(cal.getTime());
        
        parametersMap.put("PromotionalSaleStartTime", startDate);
        
        cal.add(Calendar.DATE, 10);
        
        String endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(cal.getTime());
        parametersMap.put("PromotionalSaleEndTime", endDate);
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setPromotionalSale_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:SetPromotionalSaleResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp = "string(//ebl:SetPromotionalSaleResponse/ebl:PromotionalSaleID/text())";
        String esbPromotionalSaleId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        connectorProperties.setProperty("promotionalSaleId", esbPromotionalSaleId);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetPromotionalSaleDetails",
                        "api_setPromotionalSale_mandatory.xml", null, "GetPromotionalSaleDetails",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp =
                "string(//ebl:PromotionalSaleDetails/ebl:PromotionalSale[ebl:PromotionalSaleID='"
                        + esbPromotionalSaleId + "']/ebl:PromotionalSaleID/text())";
        String apiPromotionalSaleId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbPromotionalSaleId, apiPromotionalSaleId);
    }
    
    /**
     * Negative test case for setPromotionalSale method.
     */
    @Test(dependsOnMethods = { "testSetPromotionalSaleWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Ebay {setPromotionalSale} integration test negative case.")
    public void testSetPromotionalSaleNegativeCase() throws Exception {
		
		Thread.sleep(timeOut);
		
        String apiFaultString = "apiFaultString";
        String apiErrorCode = "apiErrorCode";
        String esbFaultString = "esbFaultString";
        String esbErrorCode = "esbErrorCode";
        
        Date currentDate = new Date();
        String startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(currentDate);
        
        parametersMap.put("PromotionalSaleStartTime", startDate);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, 5);
        
        String endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(cal.getTime());
        parametersMap.put("PromotionalSaleEndTime", endDate);
        
        try {
            sendSOAPRequest(proxyUrl, "esb_setPromotionalSale_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultString = af.getMessage();
            OMElement esbErrorDetailElement = af.getDetail();
            esbErrorCode = (String) xPathEvaluate(esbErrorDetailElement, "string(//ErrorCode/text())", nameSpaceMap);
        }
        
        try {
            sendSOAPRequest(tradingApiEndpoint + "&callname=SetPromotionalSale", "api_setPromotionalSale_negative.xml",
                    parametersMap, "SetPromotionalSale", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultString = af.getMessage();
            OMElement apiErrorDetailElement = af.getDetail();
            apiErrorCode = (String) xPathEvaluate(apiErrorDetailElement, "string(//ErrorCode/text())", nameSpaceMap);
        }
        
        Assert.assertEquals(esbFaultString, apiFaultString);
        Assert.assertEquals(esbErrorCode, apiErrorCode);
    }
    
    /**
     * Positive test case for setPromotionalSaleListings method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testSetPromotionalSaleNegativeCase","testAddItemWithMandatoryParameters" }, 
    		groups = { "wso2.esb" }, description = "Ebay {setPromotionalSaleListings} integration test with mandatory parameters.")
    public void testSetPromotionalSaleListingsWithMandatoryParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setPromotionalSaleListings_mandatory.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:SetPromotionalSaleListingsResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp = "string(//ebl:SetPromotionalSaleListingsResponse/ebl:Status/text())";
        String esbStatus = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbStatus, "Scheduled");
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetPromotionalSaleDetails",
                        "api_setPromotionalSaleListings_mandatory.xml", null, "GetPromotionalSaleDetails",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp =
                "string(//ebl:PromotionalSaleDetails/ebl:PromotionalSale/ebl:PromotionalSaleItemIDArray/ebl:ItemID/text())";
        String apiItemId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiItemId, connectorProperties.getProperty("itemIdMandatory"));

    }
    
    /**
     * Positive test case for setPromotionalSaleListings method with optional parameters.
     */
    @Test(dependsOnMethods = { "testSetPromotionalSaleListingsWithMandatoryParameters" }, 
    		groups = { "wso2.esb" }, description = "Ebay {setPromotionalSaleListings} integration test with optional parameters.")
    public void testSetPromotionalSaleListingsWithOptionalParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
    	String uuid = buildItemUUID();
    	
    	
    	parametersMap.put("uuidOptional", uuid);
    	
    	//Create item to set as promotional item.
    	SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_addItem_promotional.xml", parametersMap, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp = "string(//ebl:ItemID/text())";
        
        String itemID = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        parametersMap.put("itemID", itemID);
        connectorProperties.setProperty("itemIdSecondary", itemID);
    	
         esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_setPromotionalSaleListings_optional.xml", parametersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
         esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:SetPromotionalSaleListingsResponse/ebl:Ack/text())";
         esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp = "string(//ebl:SetPromotionalSaleListingsResponse/ebl:Status/text())";
        String esbStatus = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbStatus, "Scheduled");
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetPromotionalSaleDetails",
                        "api_setPromotionalSaleListings_optional.xml", null, "GetPromotionalSaleDetails",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "count(//ebl:PromotionalSaleDetails/ebl:PromotionalSale/ebl:PromotionalSaleItemIDArray/ebl:ItemID)";
        double apiItemIdCount = (Double) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        boolean isAllAuctionItems = false;
        
        if (apiItemIdCount > 1.0) {
            isAllAuctionItems = true;
        }
        
        Assert.assertTrue(isAllAuctionItems);
    }
    
    /**
     * Negative test case for setPromotionalSaleListings method.
     */
    @Test(dependsOnMethods = { "testSetPromotionalSaleListingsWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Ebay {setPromotionalSaleListings} integration test negative case.")
    public void testSetPromotionalSaleListingsNegativeCase() throws Exception {
		
		Thread.sleep(timeOut);
		
        String apiFaultString = "apiFaultString";
        String apiErrorCode = "apiErrorCode";
        String esbFaultString = "esbFaultString";
        String esbErrorCode = "esbErrorCode";
        
        try {
            sendSOAPRequest(proxyUrl, "esb_setPromotionalSaleListings_negative.xml", parametersMap, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultString = af.getMessage();
            OMElement esbErrorDetailElement = af.getDetail();
            esbErrorCode = (String) xPathEvaluate(esbErrorDetailElement, "string(//ErrorCode/text())", nameSpaceMap);
        }
        
        try {
            sendSOAPRequest(tradingApiEndpoint + "&callname=SetPromotionalSaleListings",
                    "api_setPromotionalSaleListings_negative.xml", parametersMap, "SetPromotionalSale",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultString = af.getMessage();
            OMElement apiErrorDetailElement = af.getDetail();
            apiErrorCode = (String) xPathEvaluate(apiErrorDetailElement, "string(//ErrorCode/text())", nameSpaceMap);
        }
        
        Assert.assertEquals(esbFaultString, apiFaultString);
        Assert.assertEquals(esbErrorCode, apiErrorCode);
    }
    
    /**
     * Positive test case for getAdFormatLeads method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testSetPromotionalSaleListingsNegativeCase" }, groups = { "wso2.esb" }, description = "eBay {getAdFormatLeads} integration test with mandatory parameters.")
    public void testGetAdFormatLeadsWithMandatoryParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getAdFormatLeads_mandatory.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:GetAdFormatLeadsResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetAdFormatLeads",
                        "api_getAdFormatLeads_mandatory.xml", null, "GetAdFormatLeads", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:GetAdFormatLeadsResponse/ebl:Ack/text())";
        
        String apiSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiSuccess, "Success");
    }
    
    /**
     * Positive test case for getAdFormatLeads method with optional parameters.
     */
    @Test(dependsOnMethods = { "testGetAdFormatLeadsWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "eBay {getAdFormatLeads} integration test with optional parameters.")
    public void testGetAdFormatLeadsWithOptionalParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getAdFormatLeads_optional.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:GetAdFormatLeadsResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetAdFormatLeads", "api_getAdFormatLeads_optional.xml",
                        null, "GetAdFormatLeads", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:GetAdFormatLeadsResponse/ebl:Ack/text())";
        
        String apiSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiSuccess, "Success");
        
    }
    
    /**
     * Negative test case for getAdFormatLeads method.
     */
    @Test(dependsOnMethods = { "testGetAdFormatLeadsWithOptionalParameters" }, groups = { "wso2.esb" }, description = "eBay {getAdFormatLeads} integration test with negative case.")
    public void testGetAdFormatLeadsNegativeCase() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getAdFormatLeads_negative.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:GetAdFormatLeadsResponse/ebl:Ack/text())";
        String esbAck = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//ebl:GetAdFormatLeadsResponse/ebl:Errors/ebl:ShortMessage/text())";
        String esbShortMessage = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetAdFormatLeads", "api_getAdFormatLeads_negative.xml",
                        null, "GetAdFormatLeads", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:GetAdFormatLeadsResponse/ebl:Ack/text())";
        
        String apiAck = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//ebl:GetAdFormatLeadsResponse/ebl:Errors/ebl:ShortMessage/text())";
        String apiShortMessage = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbAck, apiAck);
        Assert.assertEquals(esbShortMessage, apiShortMessage);
    }
    
    /**
     * Positive test case for getMyeBaySelling method with optional parameters.
     */
    @Test(dependsOnMethods = { "testGetAdFormatLeadsNegativeCase" }, groups = { "wso2.esb" }, description = "eBay {getMyeBaySelling} integration test with optional parameters.")
    public void testGetMyeBaySellingWithOptionalParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getMyeBaySelling_optional.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetMyeBaySelling", "api_getMyeBaySelling_optional.xml",
                        null, "GetMyeBaySelling", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:ActiveList/ebl:ItemArray/ebl:Item[0]/ebl:ItemID/text())";
        
        String esbActiveItemId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiActiveItemId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbActiveItemId, apiActiveItemId);
        
        xPathExp = "string(//ebl:SoldList/ebl:ItemArray/ebl:Item[0]/ebl:ItemID/text())";
        
        String esbSoldItemId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiSoldItemId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSoldItemId, apiSoldItemId);
    }
    
    /**
     * Negative test case for getMyeBaySelling method.
     */
    @Test(dependsOnMethods = { "testGetMyeBaySellingWithOptionalParameters" }, groups = { "wso2.esb" }, description = "eBay {getMyeBaySelling} negative test case.")
    public void testGetMyeBaySellingNegativeCase() throws Exception {
		
		Thread.sleep(timeOut);
		
        String apiFaultString = "apiFaultString";
        String apiErrorCode = "apiErrorCode";
        String esbFaultString = "esbFaultString";
        String esbErrorCode = "esbErrorCode";
        try {
            sendSOAPRequest(proxyUrl, "esb_getMyeBaySelling_negative.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                    SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultString = af.getMessage();
            OMElement esbErrorDetailElement = af.getDetail();
            esbErrorCode = (String) xPathEvaluate(esbErrorDetailElement, "string(//ErrorCode/text())", nameSpaceMap);
        }
        
        try {
            sendSOAPRequest(tradingApiEndpoint + "&callname=GetMyeBaySelling", "api_getMyeBaySelling_negative.xml",
                    null, "GetMyeBaySelling", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultString = af.getMessage();
            OMElement apiErrorDetailElement = af.getDetail();
            apiErrorCode = (String) xPathEvaluate(apiErrorDetailElement, "string(//ErrorCode/text())", nameSpaceMap);
        }
        
        Assert.assertEquals(esbFaultString, apiFaultString);
        Assert.assertEquals(esbErrorCode, apiErrorCode);
    }
    
    /**
     * Positive test case for getItem method with mandatory parameters.
     */
    @Test(dependsOnMethods={"testAddItemWithMandatoryParameters"}, groups = { "wso2.esb" }, description = "eBay {getItem} integration test with mandatory parameters.")
    public void testGetItemWithMandatoryParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getItem_mandatory.xml", parametersMap, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:GetItemResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetItem", "api_getItem_mandatory.xml", parametersMap,
                        "GetItem", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:GetItemResponse/ebl:Ack/text())";
        
        String apiSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiSuccess, "Success");
        
        xPathExp = "string(//ebl:Description/text())";
        
        String esbDescription = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiDescription = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbDescription, apiDescription);
    }
    
    /**
     * Positive test case for getItem method with optional parameters.
     */
    @Test(dependsOnMethods = { "testAddItemWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "eBay {getItem} integration test with optional parameters.")
    public void testGetItemWithOptionalParameters() throws Exception {
		
		Thread.sleep(timeOut);
			
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getItem_optional.xml", parametersMap, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:GetItemResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetItem", "api_getItem_optional.xml", parametersMap,
                        "GetItem", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:GetItemResponse/ebl:Ack/text())";
        
        String apiSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(apiSuccess, "Success");
        
        xPathExp = "string(//ebl:WatchCount/text())";
        
        String esbWatchCount = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiWatchCount = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbWatchCount, apiWatchCount);
        
    }
    
    /**
     * Negative test case for getItem method.
     */
    @Test(dependsOnMethods = { "testAddItemWithMandatoryParameters" }, groups = { "wso2.esb" }, 
    		description = "eBay {getItem} integration test with negative case.")
    public void testGetItemNegativeCase() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_getItem_negative.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:GetItemResponse/ebl:Ack/text())";
        String esbAck = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//ebl:GetItemResponse/ebl:Errors/ebl:ShortMessage/text())";
        String esbShortMessage = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        SOAPEnvelope apiSoapResponse =
                sendSOAPRequest(tradingApiEndpoint + "&callname=GetItem", "api_getItem_negative.xml", null, "GetItem",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        
        xPathExp = "string(//ebl:GetItemResponse/ebl:Ack/text())";
        
        String apiAck = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        
        xPathExp = "string(//ebl:GetItemResponse/ebl:Errors/ebl:ShortMessage/text())";
        String apiShortMessage = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbAck, apiAck);
        Assert.assertEquals(esbShortMessage, apiShortMessage);
    }
    
    /**
     * Positive test case for findProducts method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testGetMyeBaySellingNegativeCase" }, groups = { "wso2.esb" }, description = "eBay {findProducts} integration test with mandatory parameters.")
    public void testFindProductsWithMandatoryParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        connectorProperties.setProperty("query", "apple");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_findProducts_mandatory.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//ebl:FindProductsResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        shoppingApiRequestHeadersMap.put("X-EBAY-API-APP-ID", connectorProperties.getProperty("appId"));
        shoppingApiRequestHeadersMap.put("X-EBAY-API-VERSION", connectorProperties.getProperty("version"));
        shoppingApiRequestHeadersMap.put("X-EBAY-API-SITE-ID", connectorProperties.getProperty("siteId"));
        shoppingApiRequestHeadersMap.put("X-EBAY-API-CALL-NAME", "FindProducts");
        shoppingApiRequestHeadersMap.put("X-EBAY-API-REQUEST-ENCODING", "XML");
        shoppingApiRequestHeadersMap.put("Content-Type", "text/xml;charset=UTF-8");
        
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(shoppingApiEndpoint, "POST", shoppingApiRequestHeadersMap,
                        "api_findProducts_mandatory.xml", null);
        
        Assert.assertEquals(getValueByExpression("string(//*[local-name()='Ack']/text())", apiResponse.getBody()),
                "Success");
    }
    
    /**
     * Positive test case for findProducts method with optional parameters.
     */
    @Test(dependsOnMethods = { "testFindProductsWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "eBay {findProducts} integration test with optional parameters.")
    public void testFindProductsWithOptionalParameters() throws Exception {
		
		Thread.sleep(timeOut);
		
        connectorProperties.setProperty("productSort", "Title");
        connectorProperties.setProperty("availableItemOnly", "true");
        
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_findProducts_optional.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//ebl:FindProductsResponse/ebl:Ack/text())";
        String esbSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbSuccess, "Success");
        
        xPathExp = "string(//ebl:FindProductsResponse/ebl:MoreResults/text())";
        String esbTotalProducts = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        shoppingApiRequestHeadersMap.put("X-EBAY-API-APP-ID", connectorProperties.getProperty("appId"));
        shoppingApiRequestHeadersMap.put("X-EBAY-API-VERSION", connectorProperties.getProperty("version"));
        shoppingApiRequestHeadersMap.put("X-EBAY-API-SITE-ID", connectorProperties.getProperty("siteId"));
        shoppingApiRequestHeadersMap.put("X-EBAY-API-CALL-NAME", "FindProducts");
        shoppingApiRequestHeadersMap.put("X-EBAY-API-REQUEST-ENCODING", "XML");
        shoppingApiRequestHeadersMap.put("Content-Type", "text/xml;charset=UTF-8");
        
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(shoppingApiEndpoint, "POST", shoppingApiRequestHeadersMap,
                        "api_findProducts_optional.xml", null);
        
        Assert.assertEquals(getValueByExpression("string(//*[local-name()='Ack']/text())", apiResponse.getBody()),
                "Success");
        
        Assert.assertEquals(
                getValueByExpression("string(//*[local-name()='MoreResults']/text())", apiResponse.getBody()),
                esbTotalProducts);
    }
    
    /**
     * Negative test case for findProducts method.
     */
    @Test(dependsOnMethods = { "testFindProductsWithOptionalParameters" }, groups = { "wso2.esb" }, description = "eBay {findProducts} integration test with negative case.")
    public void testFindProductsNegativeCase() throws Exception {
		
		Thread.sleep(timeOut);
		
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_findProducts_negative.xml", null, "mediate", SOAP_HEADER_XPATH_EXP,
                        SOAP_BODY_XPATH_EXP);
        
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        
        String xPathExp = "string(//ebl:FindProductsResponse/ebl:Ack/text())";
        String esbAck = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        
        Assert.assertEquals(esbAck, "Failure");
        
        shoppingApiRequestHeadersMap.put("X-EBAY-API-APP-ID", connectorProperties.getProperty("appId"));
        shoppingApiRequestHeadersMap.put("X-EBAY-API-VERSION", connectorProperties.getProperty("version"));
        shoppingApiRequestHeadersMap.put("X-EBAY-API-SITE-ID", connectorProperties.getProperty("siteId"));
        shoppingApiRequestHeadersMap.put("X-EBAY-API-CALL-NAME", "FindProducts");
        shoppingApiRequestHeadersMap.put("X-EBAY-API-REQUEST-ENCODING", "XML");
        shoppingApiRequestHeadersMap.put("Content-Type", "text/xml;charset=UTF-8");
        
        RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(shoppingApiEndpoint, "POST", shoppingApiRequestHeadersMap,
                        "api_findProducts_negative.xml", null);
        
        Assert.assertEquals(getValueByExpression("string(//*[local-name()='Ack']/text())", apiResponse.getBody()),
                "Failure");
    }
    
    /**
     * Builds a 32-character UUID String for addItem method.
     * 
     * @return String the randomly-generated UUID
     */
    public static String buildItemUUID() {
    
        char[] chars = "abcdef0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
        
    }
    
}
