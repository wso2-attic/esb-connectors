/*
 *  Copyright (c) 2005-2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.connector.integration.test.netsuite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.jaxen.JaxenException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.testng.Assert;

public class NetsuiteConnectorIntegrationTest extends
        ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private final String SOAP_HEADER_XPATH_EXP = "/soapenv:Envelope/soapenv:Header/*";

    private final String SOAP_BODY_XPATH_EXP = "/soapenv:Envelope/soapenv:Body/*";

    private String apiEndPoint;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("netsuite-connector-1.0.0");

        apiEndPoint = connectorProperties.getProperty("apiUrl");
    }

    /**
     * Positive test case for addList method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {addList} integration test with mandatory parameters.")
    public void testAddListWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("listRel", "urn:relationships_2014_1.lists.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_addList_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(/soapenv:Body/ns:addListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));

        String internalId;
        if (isSuccess.booleanValue()) {
            xPathExp = "string(/soapenv:Body/ns:addListResponse/ns:writeResponseList/ns:writeResponse/ns:baseRef/@internalId)";
            internalId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
            connectorProperties.put("addListMandatoryInternalId", internalId);
        }

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_addList_mandatory.xml", null, "getList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:entityId/text())";
        String apiEntityId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:companyName/text())";
        String apicompanyName = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:subsidiary/@internalId)";
        String subsidiary = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(apiEntityId, connectorProperties.getProperty("customerNameMandatory"));
        Assert.assertEquals(apicompanyName, connectorProperties.getProperty("companyName"));
        Assert.assertEquals(subsidiary, connectorProperties.getProperty("subsidiaryId"));
    }

    /**
     * Positive test case for addList method with optional parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {addList} integration test with optional parameters.")
    public void testAddListWithOptionalParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("listRel", "urn:relationships_2014_1.lists.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_addList_optional.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(/soapenv:Body/ns:addListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));

        String internalId;
        if (isSuccess.booleanValue()) {
            xPathExp = "string(/soapenv:Body/ns:addListResponse/ns:writeResponseList/ns:writeResponse/ns:baseRef/@internalId)";
            internalId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
            connectorProperties.put("addListOptionalInternalId", internalId);
        }

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_addList_optional.xml", null, "getList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:email/text())";
        String email = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:url/text())";
        String url = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:comments/text())";
        String comment = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(email, connectorProperties.getProperty("customerEmail"));
        Assert.assertEquals(url, connectorProperties.getProperty("customerUrl"));
        Assert.assertEquals(comment, connectorProperties.getProperty("customerComment"));
    }

    /**
     * Negative test case for addList method with Negative case.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testAddListWithMandatoryParameters"}, description = "Netsuite {addList} integration test with negative case.")
    public void testAddListWithNegativeCase() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_addList_negative.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_addList_negative.xml", null, "addList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xPathExp = "string(/soapenv:Body/ns:addListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        String esbIsSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiIsSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:addListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/platformCore:statusDetail/@type)";
        String esbType = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiType = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:addListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/platformCore:statusDetail/platformCore:code/text())";
        String esbCode = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiCode = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(esbIsSuccess, apiIsSuccess);
        Assert.assertEquals(esbType, apiType);
        Assert.assertEquals(esbCode, apiCode);
    }

    /**
     * Positive test case for getList method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testAddListWithMandatoryParameters"}, description = "Netsuite {getList} integration test with mandatory parameters.")
    public void testGetListWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("listRel", "urn:relationships_2014_1.lists.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_getList_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_getList_mandatory.xml", null, "getList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:entityId/text())";
        String esbEntityId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiEntityId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:companyName/text())";
        String esbCompanyName = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiCompanyName = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:subsidiary/@internalId)";
        String esbSubsidiary = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiSubsidiary = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:customForm/@internalId)";
        String esbCustomFormId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiCustomFormId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:customForm/platformCore:name/text())";
        String esbCustomFormName = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiCustomFormName = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(esbEntityId, apiEntityId);
        Assert.assertEquals(esbCompanyName, apiCompanyName);
        Assert.assertEquals(esbSubsidiary, apiSubsidiary);
        Assert.assertEquals(esbCustomFormId, apiCustomFormId);
        Assert.assertEquals(esbCustomFormName, apiCustomFormName);
    }

    /**
     * Negative test case for getList method.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testAddListWithMandatoryParameters"}, description = "Netsuite {getList} integration test with negative case.")
    public void testGetListWithNegativeCase() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_getList_negative.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_getList_negative.xml", null, "getList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/platformCore:status/@isSuccess)";
        String esbIsSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiIsSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/platformCore:status/platformCore:statusDetail/@type)";
        String esbType = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiType = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/platformCore:status/platformCore:statusDetail/platformCore:code/text())";
        String esbCode = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiCode = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(esbIsSuccess, apiIsSuccess);
        Assert.assertEquals(esbType, apiType);
        Assert.assertEquals(esbCode, apiCode);
    }

    /**
     * Positive test case for updateList method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testAddListWithMandatoryParameters"}, description = "Netsuite {updateList} integration test with mandatory parameters.")
    public void testUpdateListWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("listRel", "urn:relationships_2014_1.lists.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_updateList_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());

        String xPathExp = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isEsbSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));

        Assert.assertTrue(isEsbSuccess.booleanValue());

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_updateList_mandatory.xml", null, "getList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:entityId/text())";
        String apiEntityId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/ns:record/listRel:companyName/text())";
        String apicompanyName = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(apiEntityId, connectorProperties.getProperty("customerNameUpdated"));
        Assert.assertEquals(apicompanyName, connectorProperties.getProperty("companyNameUpdated"));
    }

    /**
     * Negative test case for updateList method.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testAddListWithOptionalParameters"}, description = "Netsuite {updateList} integration test with negative case.")
    public void testUpdateListWithNegative() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("listRel", "urn:relationships_2014_1.lists.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_updateList_negative.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_updateList_negative.xml", null, "updateList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xPathExp = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        String esbIsSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiIsSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/platformCore:statusDetail/@type)";
        String esbType = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiType = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/platformCore:statusDetail/platformCore:code/text())";
        String esbCode = (String) xPathEvaluate(esbResponseElement, xPathExp,
                nameSpaceMap);
        String apiCode = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(esbIsSuccess, apiIsSuccess);
        Assert.assertEquals(esbType, apiType);
        Assert.assertEquals(esbCode, apiCode);
    }

    /**
     * Positive test case for deleteList method with mandatory parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "Netsuite {deleteList} integration test with mandatory parameters.")
    public void testDeleteListWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("listRel", "urn:relationships_2014_1.lists.webservices.netsuite.com");

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_deleteList_mandatory.xml", null, "getList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/platformCore:status/@isSuccess)";
        Boolean isApiSuccess = Boolean.valueOf((String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap));

        // Test List is existing or not via direct call
        Assert.assertTrue(isApiSuccess.booleanValue());

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_deleteList_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());

        xPathExp = "string(/soapenv:Body/ns:deleteListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isEsbSuccess = Boolean.valueOf((String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap));

        // Test List is deleted via ESB
        Assert.assertTrue(isEsbSuccess.booleanValue());

        apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_deleteList_mandatory.xml", null, "getList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        xPathExp = "string(/soapenv:Body/ns:getListResponse/ns:readResponseList/ns:readResponse/platformCore:status/@isSuccess)";
        Boolean isItemExists = Boolean.valueOf((String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap));

        // Checking the availability of the deleted List
        Assert.assertFalse(isItemExists.booleanValue());
    }

    /**
     * Negative test case for deleteList method.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "Netsuite {deleteList} integration test with mandatory parameters.")
    public void testDeleteListWithNegative() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("listRel", "urn:relationships_2014_1.lists.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_deleteList_negative.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_deleteList_negative.xml", null, "deleteList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xPathExp = "string(/soapenv:Body/ns:deleteListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        String esbIsSuccess = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiIsSuccess = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:deleteListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/platformCore:statusDetail/@type)";
        String esbType = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiType = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        xPathExp = "string(/soapenv:Body/ns:deleteListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/platformCore:statusDetail/platformCore:code/text())";
        String esbCode = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        String apiCode = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(esbIsSuccess, apiIsSuccess);
        Assert.assertEquals(esbType, apiType);
        Assert.assertEquals(esbCode, apiCode);
    }

    /**
     * Positive test case for getCustomizationId method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {getCustomizationId} integration test with mandatory parameters.")
    public void testGetCustomizationIdWithMandatoryParameters()
            throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_getCustomizationId_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_getCustomizationId_mandatory.xml", null,
                "getCustomizationId", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbRequestElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiRequestElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isEsbSuccess = Boolean.valueOf((String) xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap));
        Boolean isApiSuccess = Boolean.valueOf((String) xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap));

        xpathString = "string(/soapenv:Body/ns:getCustomizationIdResponse/platformCore:getCustomizationIdResult/platformCore:totalRecords/text())";
        String esbTotalRecords = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiTotalRecords = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:getCustomizationIdResponse/platformCore:getCustomizationIdResult/platformCore:customizationRefList/platformCore:customizationRef[1]/platformCore:name/text())";
        String esbName = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiName = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(isEsbSuccess, isApiSuccess);
        Assert.assertEquals(esbTotalRecords, apiTotalRecords);
        Assert.assertEquals(esbName, apiName);
    }

    /**
     * Negative test case for getCustomizationId method.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetCustomizationIdWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "Netsuite {getCustomizationId} integration test with mandatory parameters.")
    public void testGetCustomizationIdNegativeCase() throws Exception {

        String apiFaultCode = "";
        String apiFaultCodeElement = "";
        String esbFaultCode = "";
        String esbFaultCodeElement = "";

        try {
            SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_getCustomizationId_negative.xml", null, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        try {
            SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_getCustomizationId_negative.xml", null,
                    "getCustomizationId", SOAP_HEADER_XPATH_EXP,
                    SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }

        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }

    /**
     * Positive test case for attach method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {attach} integration test with mandatory parameters.")
    public void testAttachWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_attach_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_attach_mandatory.xml", null, "attach",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbRequestElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiRequestElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isEsbSuccess = Boolean.valueOf((String) xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap));
        Boolean isApiSuccess = Boolean.valueOf((String) xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap));

        xpathString = "string(/soapenv:Body/attachResponse/writeResponse/platformCore:status/baseRef/@internalId)";
        String esbAttachInternalId = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiAttachInternalId = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/attachResponse/writeResponse/platformCore:status/baseRef/platformCore:name/text())";
        String esbName = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiName = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(isEsbSuccess, isApiSuccess);
        Assert.assertEquals(esbAttachInternalId, apiAttachInternalId);
        Assert.assertEquals(esbName, apiName);
    }

    /**
     * Negative test case for attach method.
     */
    @Test(priority = 1, dependsOnMethods = {"testAttachWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "Netsuite {attach} integration test with mandatory parameters.")
    public void testAttachNegativeCase() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_attach_negative.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_attach_negative.xml", null, "attach",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbRequestElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiRequestElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isEsbSuccess = Boolean.valueOf((String) xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap));
        Boolean isApiSuccess = Boolean.valueOf((String) xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap));

        xpathString = "string(/soapenv:Body/attachResponse/writeResponse/platformCore:status/platformCore:statusDetail/@type)";
        String esbStatusDetail = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiStatusDetail = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/attachResponse/writeResponse/platformCore:status/platformCore:statusDetail/platformCore:code/text())";
        String esbCode = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiCode = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(isEsbSuccess, isApiSuccess);
        Assert.assertEquals(esbStatusDetail.toString(), apiStatusDetail);
        Assert.assertEquals(esbCode, apiCode);
    }

    /**
     * Positive test case for detach method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {detach} integration test with mandatory parameters.")
    public void testDetachWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_detach_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_detach_mandatory.xml", null, "detach",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbRequestElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiRequestElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isEsbSuccess = Boolean.valueOf((String) xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap));
        Boolean isApiSuccess = Boolean.valueOf((String) xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap));

        xpathString = "string(/soapenv:Body/attachResponse/writeResponse/platformCore:status/baseRef/@internalId)";
        String esbDetachInternalId = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiDetachInternalId = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/attachResponse/writeResponse/platformCore:status/baseRef/@type)";
        String esbType = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiType = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(isEsbSuccess, isApiSuccess);
        Assert.assertEquals(esbDetachInternalId, apiDetachInternalId);
        Assert.assertEquals(esbType, apiType);
    }

    /**
     * Negative test case for detach method.
     */
    @Test(priority = 1, dependsOnMethods = {"testDetachWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "Netsuite {detach} integration test with mandatory parameters.")
    public void testDetachNegativeCase() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_detach_negative.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_detach_negative.xml", null, "detach",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbRequestElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiRequestElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isEsbSuccess = Boolean.valueOf((String) xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap));
        Boolean isApiSuccess = Boolean.valueOf((String) xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap));

        xpathString = "string(/soapenv:Body/attachResponse/writeResponse/platformCore:status/platformCore:statusDetail/@type)";
        String esbStatusDetail = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiStatusDetail = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/attachResponse/writeResponse/platformCore:status/platformCore:statusDetail/platformCore:code/text())";
        String esbCode = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiCode = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(isEsbSuccess, isApiSuccess);
        Assert.assertEquals(esbStatusDetail.toString(), apiStatusDetail);
        Assert.assertEquals(esbCode, apiCode);
    }

    /**
     * Positive test case for getDataCenterUrls method with mandatory
     * parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {getDataCenterUrls} integration test with mandatory parameters.")
    public void testGetDataCenterUrlsWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_getDataCenterUrls_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_getDataCenterUrls_mandatory.xml", null,
                "getDataCenterUrls", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbRequestElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiRequestElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isEsbSuccess = Boolean.valueOf((String) xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap));
        Boolean isApiSuccess = Boolean.valueOf((String) xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap));

        xpathString = "string(/soapenv:Body/getDataCenterUrlsResponse/platformCore:getDataCenterUrlsResult/platformCore:dataCenterUrls/platformCore:restDomain/text())";
        String esbRestDomain = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiRestDomain = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/getDataCenterUrlsResponse/platformCore:getDataCenterUrlsResult/platformCore:dataCenterUrls/platformCore:webservicesDomain/text())";
        String esbWebservicesDomain = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiWebservicesDomain = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(isEsbSuccess, isApiSuccess);
        Assert.assertEquals(esbRestDomain, apiRestDomain);
        Assert.assertEquals(esbWebservicesDomain, apiWebservicesDomain);
    }

    /**
     * Negative test case for getDataCenterUrls method.
     */
    @Test(priority = 1, dependsOnMethods = {"testGetCustomizationIdWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "Netsuite {getDataCenterUrls} integration test with mandatory parameters.")
    public void testGetDataCenterUrlsNegativeCase() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_getDataCenterUrls_negative.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_getDataCenterUrls_negative.xml", null,
                "getDataCenterUrls", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbRequestElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiRequestElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Body/ns:updateListResponse/ns:writeResponseList/ns:writeResponse/platformCore:status/@isSuccess)";
        Boolean isEsbSuccess = Boolean.valueOf((String) xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap));
        Boolean isApiSuccess = Boolean.valueOf((String) xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap));

        xpathString = "string(/soapenv:Body/attachResponse/writeResponse/platformCore:status/platformCore:statusDetail/@type)";
        String esbStatusDetail = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiStatusDetail = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/attachResponse/writeResponse/platformCore:status/platformCore:statusDetail/platformCore:code/text())";
        String esbCode = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiCode = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(isEsbSuccess, isApiSuccess);
        Assert.assertEquals(esbStatusDetail.toString(), apiStatusDetail);
        Assert.assertEquals(esbCode, apiCode);
    }

    /**
     * Positive test case for search method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {search} integration test with mandatory parameters.")
    public void testSearchWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("listEmp", "urn:employees_2014_1.lists.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_search_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_search_mandatory.xml", null, "search",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbRequestElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        OMElement apiRequestElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Body/ns:searchResponse/platformCore:searchResult/platformCore:status/@isSuccess)";
        Boolean isEsbSuccess = Boolean.valueOf((String) xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap));
        Boolean isApiSuccess = Boolean.valueOf((String) xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap));

        xpathString = "string(/soapenv:Body/ns:searchResponse/platformCore:searchResult/platformCore:recordList[1]/platformCore:record/listEmp:entityId/text())";
        String esbEntityId = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiEntityId = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:searchResponse/platformCore:searchResult/platformCore:totalRecords/text())";
        String esbTotalRecords = xPathEvaluate(esbRequestElement, xpathString, nameSpaceMap).toString();
        String apiTotalRecords = xPathEvaluate(apiRequestElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(isEsbSuccess, isApiSuccess);
        Assert.assertEquals(esbEntityId, apiEntityId);
        Assert.assertEquals(esbTotalRecords, apiTotalRecords);
    }

    /**
     * Negative test case for search method.
     */
    @Test(priority = 1, dependsOnMethods = {"testSearchWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "Netsuite {search} integration test with mandatory parameters.")
    public void testSearchNegativeCase() throws Exception {

        String apiFaultCode = "";
        String apiFaultCodeElement = "";
        String esbFaultCode = "";
        String esbFaultCodeElement = "";

        try {
            SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl,
                    "esb_search_negative.xml", null, "mediate",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }

        String apiEndPoint = connectorProperties.getProperty("apiUrl");
        try {
            SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_search_negative.xml", null, "search",
                    SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }

        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }

    /**
     * Positive test case for getAll method with mandatory parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "Netsuite {getAll} integration test with mandatory parameters.")
    public void testGetAllWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        nameSpaceMap.put("listAcct", "urn:accounting_2014_1.lists.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_getAll_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_getAll_mandatory.xml", null, "getAll",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Body/ns:getAllResponse/platformCore:getAllResult/platformCore:status/@isSuccess)";
        String esbIsSuccess = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiIsSuccess = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:getAllResponse/platformCore:getAllResult/platformCore:totalRecords)";
        String esbTotalRecords = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiTotalRecords = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:getAllResponse/platformCore:getAllResult/platformCore:recordList/platformCore:record[1]/@xsi:type)";
        String esbRecordType = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiRecordType = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:getAllResponse/platformCore:getAllResult/platformCore:recordList/platformCore:record[1]/listAcct:country)";
        String esbCountry = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiCountry = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(apiIsSuccess, esbIsSuccess);
        Assert.assertEquals(apiTotalRecords, esbTotalRecords);
        Assert.assertEquals(apiRecordType, esbRecordType);
        Assert.assertEquals(apiCountry, esbCountry);
    }

    /**
     * Negative test case for getAll method.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "Netsuite {getAll} integration test with negative case.")
    public void testGetAllWithNegativeCase() throws Exception {

        String apiFaultCode = "";
        String esbFaultCode = "";
        String apiFaultCodeElement = "";
        String esbFaultCodeElement = "";

        try {
            sendSOAPRequest(proxyUrl, "esb_getAll_negative.xml", null,
                    "mediate", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }

        try {
            sendSOAPRequest(apiEndPoint, "api_getAll_negative.xml", null,
                    "getAll", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }

        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }

    /**
     * Positive test case for getItemAvailability method with mandatory
     * parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {getItemAvailability} integration test with mandatory parameters.")
    public void testGetItemAvailabilityWithMandatoryParameters()
            throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl,
                "esb_getItemAvailability_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse
                .getBody().toString());

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_getItemAvailability_mandatory.xml", null,
                "getItemAvailability", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Envelope/soapenv:Body/ns:getItemAvailabilityResponse/platformCore:getItemAvailabilityResult/platformCore:status/@isSuccess)";
        String esbIsSuccess = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiIsSuccess = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Envelope/soapenv:Body/ns:getItemAvailabilityResponse/platformCore:getItemAvailabilityResult/platformCore:itemAvailabilityList/platformCore:itemAvailability/platformCore:item/platformCore:name)";
        String esbItemName = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiItemName = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "number(/soapenv:Envelope/soapenv:Body/ns:getItemAvailabilityResponse/platformCore:getItemAvailabilityResult/platformCore:itemAvailabilityList/platformCore:itemAvailability/platformCore:quantityOnHand)"
                .toString();
        String esbQuantityOnHand = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiQuantityOnHand = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "number(/soapenv:Envelope/soapenv:Body/ns:getItemAvailabilityResponse/platformCore:getItemAvailabilityResult/platformCore:itemAvailabilityList/platformCore:itemAvailability/platformCore:quantityOnOrder)"
                .toString();
        String esbQuantityOnOrder = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiQuantityOnOrder = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(apiIsSuccess, esbIsSuccess);
        Assert.assertEquals(apiItemName, esbItemName);
        Assert.assertEquals(apiQuantityOnHand, esbQuantityOnHand);
        Assert.assertEquals(apiQuantityOnOrder, esbQuantityOnOrder);
    }

    /**
     * Positive test case for getItemAvailability method with optional
     * parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {getItemAvailability} integration test with optional parameters.")
    public void testGetItemAvailabilityWithOptionalParameters()
            throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_getItemAvailability_optional.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_getItemAvailability_optional.xml", null,
                "getItemAvailability", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "string(/soapenv:Body/ns:getItemAvailabilityResponse/platformCore:getItemAvailabilityResult/platformCore:status/@isSuccess)";
        String esbIsSuccess = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiIsSuccess = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:getItemAvailabilityResponse/platformCore:getItemAvailabilityResult/platformCore:itemAvailabilityList/platformCore:itemAvailability/platformCore:item/platformCore:name)";
        String esbItemName = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiItemName = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "number(/soapenv:Body/ns:getItemAvailabilityResponse/platformCore:getItemAvailabilityResult/platformCore:itemAvailabilityList/platformCore:itemAvailability/platformCore:quantityOnHand)"
                .toString();
        String esbQuantityOnHand = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiQuantityOnHand = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:getItemAvailabilityResponse/platformCore:getItemAvailabilityResult/platformCore:itemAvailabilityList/platformCore:itemAvailability/platformCore:lastQtyAvailableChange)";
        String esbLastQtyAvailableChange = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiLastQtyAvailableChange = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(apiIsSuccess, esbIsSuccess);
        Assert.assertEquals(apiItemName, esbItemName);
        Assert.assertEquals(apiQuantityOnHand, esbQuantityOnHand);
        Assert.assertEquals(apiLastQtyAvailableChange, esbLastQtyAvailableChange);
    }

    /**
     * Negative test case for getItemAvailability method.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "Netsuite {getItemAvailability} integration test with negative case.")
    public void testGetItemAvailabilityWithNegativeCase() throws Exception {

        String apiFaultCode = "";
        String esbFaultCode = "";
        String apiFaultCodeElement = "";
        String esbFaultCodeElement = "";

        try {
            sendSOAPRequest(proxyUrl, "esb_getItemAvailability_negative.xml",
                    null, "mediate", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }

        try {
            sendSOAPRequest(apiEndPoint, "api_getItemAvailability_negative.xml", null,
                    "getItemAvailability", SOAP_HEADER_XPATH_EXP,
                    SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }

        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }

    /**
     * Positive test case for getSelectValue method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {getSelectValue} integration test with mandatory parameters.")
    public void testGetSelectValueWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl,
                "esb_getSelectValue_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint,
                "api_getSelectValue_mandatory.xml", null, "getSelectValue",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "boolean(/soapenv:Body/ns:getSelectValueResponse/platformCore:getSelectValueResult/platformCore:status/@isSuccess)";
        String esbIsSuccess = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiIsSuccess = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:getSelectValueResponse/platformCore:getSelectValueResult/platformCore:baseRefList/platformCore:baseRef[1]/@internalId)"
                .toString();
        String esbInternalId = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiInternalId = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "number(/soapenv:Body/ns:getSelectValueResponse/platformCore:getSelectValueResult/platformCore:totalRecords)"
                .toString();
        String esbTotalRecords = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiTotalRecords = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "number(/soapenv:Body/ns:getSelectValueResponse/platformCore:getSelectValueResult/totalPages)"
                .toString();
        String esbTotalPages = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiTotalPages = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(apiIsSuccess, esbIsSuccess);
        Assert.assertEquals(apiInternalId, esbInternalId);
        Assert.assertEquals(apiTotalRecords, esbTotalRecords);
        Assert.assertEquals(apiTotalPages, esbTotalPages);
    }

    /**
     * Positive test case for getSelectValue method with optional parameters.
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "Netsuite {getSelectValue} integration test with optional parameters.")
    public void testGetSelectValueWithOptionalParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_getSelectValue_optional.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_getSelectValue_optional.xml", null, "getSelectValue",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "boolean(/soapenv:Body/ns:getSelectValueResponse/platformCore:getSelectValueResult/platformCore:status/@isSuccess)";
        String esbIsSuccess = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiIsSuccess = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:getSelectValueResponse/platformCore:getSelectValueResult/platformCore:baseRefList/platformCore:baseRef[1]/@internalId)"
                .toString();
        String esbInternalId = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiInternalId = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "number(/soapenv:Body/ns:getSelectValueResponse/platformCore:getSelectValueResult/platformCore:totalRecords)"
                .toString();
        String esbTotalRecords = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiTotalRecords = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "number(/soapenv:Body/ns:getSelectValueResponse/platformCore:getSelectValueResult/totalPages)".toString();
        String esbTotalPages = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiTotalPages = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(apiIsSuccess, esbIsSuccess);
        Assert.assertEquals(apiInternalId, esbInternalId);
        Assert.assertEquals(apiTotalRecords, esbTotalRecords);
        Assert.assertEquals(apiTotalPages, esbTotalPages);
    }

    /**
     * Negative test case for getSelectValue method.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Netsuite {getSelectValue} integration test with negative case.")
    public void testGetSelectValueWithNegativeCase() throws Exception {

        String apiFaultCode = "";
        String esbFaultCode = "";
        String apiFaultCodeElement = "";
        String esbFaultCodeElement = "";

        try {
            sendSOAPRequest(proxyUrl, "esb_getSelectValue_negative.xml", null,
                    "mediate", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }

        try {
            sendSOAPRequest(apiEndPoint, "api_getSelectValue_negative.xml",
                    null, "getSelectValue", SOAP_HEADER_XPATH_EXP,
                    SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }
        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }

    /**
     * Positive test case for initializeList method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testAddListWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "Netsuite {initializeList} integration test with mandatory parameters.")
    public void testInitializeListWithMandatoryParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns", "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore", "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        nameSpaceMap.put("tranCust", "urn:customers_2014_1.transactions.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_initializeList_mandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "api_initializeList_mandatory.xml", null, "initializeList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());

        String xpathString = "boolean(/soapenv:Body/ns:initializeListResponse/readResponseList/readResponse/platformCore:status/@isSuccess)";
        String esbIsSuccess = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiIsSuccess = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:initializeListResponse/readResponseList/readResponse/record/@xsi:type)";
        String esbRecordType = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiRecordType = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:initializeListResponse/readResponseList/readResponse/record/tranCust:customForm[1]/platformCore:name)";
        String esbRecordName = xPathEvaluate(esbResponseElement, xpathString, nameSpaceMap).toString();
        String apiRecordName = xPathEvaluate(apiResponseElement, xpathString, nameSpaceMap).toString();

        Assert.assertEquals(apiIsSuccess, esbIsSuccess);
        Assert.assertEquals(apiRecordType, esbRecordType);
        Assert.assertEquals(apiRecordName, esbRecordName);
    }

    /**
     * Positive test case for initializeList method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testAddListWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "Netsuite {initializeList} integration test with optional parameters.")
    public void testInitializeListWithOptionalParameters() throws Exception {

        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("ns",
                "urn:messages_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("platformCore",
                "urn:core_2014_1.platform.webservices.netsuite.com");
        nameSpaceMap.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        nameSpaceMap.put("tranCust",
                "urn:customers_2014_1.transactions.webservices.netsuite.com");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl,
                "esb_initializeList_optional.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse
                .getBody().toString());

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint,
                "api_initializeList_optional.xml", null, "initializeList",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse
                .getBody().toString());

        String xpathString = "boolean(/soapenv:Body/ns:initializeListResponse/readResponseList/readResponse/platformCore:status/@isSuccess)";
        String esbIsSuccess = xPathEvaluate(esbResponseElement, xpathString,
                nameSpaceMap).toString();
        String apiIsSuccess = xPathEvaluate(apiResponseElement, xpathString,
                nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:initializeListResponse/readResponseList/readResponse/record/@xsi:type)";
        String esbRecordType = xPathEvaluate(esbResponseElement, xpathString,
                nameSpaceMap).toString();
        String apiRecordType = xPathEvaluate(apiResponseElement, xpathString,
                nameSpaceMap).toString();

        xpathString = "string(/soapenv:Body/ns:initializeListResponse/readResponseList/readResponse/record/tranCust:customForm[1]/platformCore:name)";
        String esbRecordName = xPathEvaluate(esbResponseElement, xpathString,
                nameSpaceMap).toString();
        String apiRecordName = xPathEvaluate(apiResponseElement, xpathString,
                nameSpaceMap).toString();

        Assert.assertEquals(apiIsSuccess, esbIsSuccess);
        Assert.assertEquals(apiRecordType, esbRecordType);
        Assert.assertEquals(apiRecordName, esbRecordName);
    }

    /**
     * Negative test case for initializeList method.
     */
    @Test(priority = 2, dependsOnMethods = {"testAddListWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "Netsuite {initializeList} integration test with negative case.")
    public void testInitializeListWithNegativeCase() throws Exception {

        String apiFaultCode = "";
        String esbFaultCode = "";
        String apiFaultCodeElement = "";
        String esbFaultCodeElement = "";

        try {
            sendSOAPRequest(proxyUrl, "esb_initializeList_negative.xml", null,
                    "mediate", SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            esbFaultCode = af.getMessage();
            esbFaultCodeElement = af.getFaultCodeElement().getText();
        }

        try {
            sendSOAPRequest(apiEndPoint, "api_initializeList_negative.xml", null, "initializeList", SOAP_HEADER_XPATH_EXP,
                    SOAP_BODY_XPATH_EXP);
        } catch (AxisFault af) {
            apiFaultCode = af.getMessage();
            apiFaultCodeElement = af.getFaultCodeElement().getText();
        }

        Assert.assertEquals(apiFaultCode, esbFaultCode);
        Assert.assertEquals(apiFaultCodeElement, esbFaultCodeElement);
    }
}