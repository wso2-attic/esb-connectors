/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.connector.integration.test.zuorasoap;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.testng.Assert;

public class ZuorasoapConnectorIntegrationTest extends
        ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private final String SOAP_HEADER_XPATH_EXP = "/soapenv:Envelope/soapenv:Header/*";

    private final String SOAP_BODY_XPATH_EXP = "/soapenv:Envelope/soapenv:Body/*";

    Map<String, String> nameSpaceMap = new HashMap<String, String>();

    private String apiEndPoint;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("zuorasoap-connector-1.0.0");

        apiEndPoint = connectorProperties.getProperty("apiUrl");

        nameSpaceMap.put("ns", "http://api.zuora.com/");
        nameSpaceMap.put("ns1", "http://object.api.zuora.com/");
    }

    /**
     * Positive test case for getUserInfo method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "Zuora {getUserInfo} integration test with mandatory parameters.")
    public void testGetUserInfoWithMandatoryParameters() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esbGetUserInfoMandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//ns:UserEmail)";
        String esbUserEmail = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        xPathExp = "string(//ns:UserId)";
        String esbUserId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "apiLogin.xml", null, "login",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//ns:Session)";
        String session = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        connectorProperties.put("session", session);


        apiSoapResponse = sendSOAPRequest(apiEndPoint, "apiGetUserInfoMandatory.xml", null, "getUserInfo",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//ns:UserEmail)";
        String apiUserEmail = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        xPathExp = "string(//ns:UserId)";
        String apiUserId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(esbUserEmail, apiUserEmail);
        Assert.assertEquals(esbUserId, apiUserId);

    }

    /**
     * Positive test case for create method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, dependsOnMethods = { "testGetUserInfoWithMandatoryParameters" }, description = "Zuora {create} integration test with mandatory parameters.")
    public void testCreateWithMandatoryParameters() throws Exception {
        connectorProperties.put("accountNumber", connectorProperties.getProperty("accountNumber")+System.currentTimeMillis());
        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esbCreateMandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa     "+esbSoapResponse.getBody().toString());
        String xPathExp = "string(//ns:Id)";
        String id = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        connectorProperties.put("id", id);
        //xPathExp = "string(//ns:UserId)";
        //String esbUserId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaid     "+id);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa     "+System.currentTimeMillis());

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "apiQueryForCreateMandatory.xml", null, "query",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//ns1:Name)";
        String name = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa     "+apiSoapResponse.getBody().toString());
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa     "+name);
        //xPathExp = "string(//ns:UserId)";
        //String apiUserId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(connectorProperties.getProperty("name"), name);
//        Assert.assertEquals(esbUserId, apiUserId);

    }

    /**
     * Positive test case for update method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, dependsOnMethods = { "testCreateWithMandatoryParameters" }, description = "Zuora {update} integration test with mandatory parameters.")
    public void testUpdateWithMandatoryParameters() throws Exception {
        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esbUpdateMandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa     " + esbSoapResponse.getBody().toString());
        String xPathExp = "string(//ns:Success)";
        String status = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);

        //xPathExp = "string(//ns:UserId)";
        //String esbUserId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaastatus     " + status);


        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "apiQueryForCreateMandatory.xml", null, "query",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//ns1:Name)";
        String name = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa     " + apiSoapResponse.getBody().toString());
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa     " + name);
        //xPathExp = "string(//ns:UserId)";
        //String apiUserId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(status, "true");
        Assert.assertEquals(connectorProperties.getProperty("updateName"), name);
//        Assert.assertEquals(esbUserId, apiUserId);

    }

    /**
     * Positive test case for delete method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, dependsOnMethods = { "testUpdateWithMandatoryParameters" }, description = "Zuora {delete} integration test with mandatory parameters.")
    public void testDeleteWithMandatoryParameters() throws Exception {
        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esbDeleteMandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa     " + esbSoapResponse.getBody().toString());
        String xPathExp = "string(//ns:success)";
        String status = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);

        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaastatus     " + status);

        Assert.assertEquals(status, "true");
    }


    /**
     * Positive test case for query method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, dependsOnMethods = { "testDeleteWithMandatoryParameters" }, description = "Zuora {query} integration test with mandatory parameters.")
    public void testQueryWithMandatoryParameters() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esbQueryMandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//ns:result/*[not(name()='queryLocator')])";
        String esbResults = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        xPathExp = "string(//ns:queryLocator)";
        String queryLocator = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        connectorProperties.put("queryLocator", queryLocator);

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "apiQueryMandatory.xml", null, "query",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//ns:result/*[not(name()='queryLocator')])";
        String apiResults = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        //xPathExp = "string(//ns:UserId)";
        //String apiUserId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(esbResults, apiResults);
//        Assert.assertEquals(esbUserId, apiUserId);

    }

    /**
     * Positive test case for queryMore method with mandatory parameters.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, dependsOnMethods = { "testQueryWithMandatoryParameters" }, description = "Zuora {queryMore} integration test with mandatory parameters.")
    public void testQueryMoreWithMandatoryParameters() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esbQueryMoreMandatory.xml", null, "mediate",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);

        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String xPathExp = "string(//ns:result/*[not(name()='queryLocator')])";
        String esbResults = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);
        //xPathExp = "string(//ns:UserId)";
        //String esbUserId = (String) xPathEvaluate(esbResponseElement, xPathExp, nameSpaceMap);

        SOAPEnvelope apiSoapResponse = sendSOAPRequest(apiEndPoint, "apiQueryMoreMandatory.xml", null, "queryMore",
                SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement apiResponseElement = AXIOMUtil.stringToOM(apiSoapResponse.getBody().toString());
        xPathExp = "string(//ns:result/*[not(name()='queryLocator')])";
        String apiResults = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);
        //xPathExp = "string(//ns:UserId)";
        //String apiUserId = (String) xPathEvaluate(apiResponseElement, xPathExp, nameSpaceMap);

        Assert.assertEquals(esbResults, apiResults);
//        Assert.assertEquals(esbUserId, apiUserId);

    }
}
