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

package org.wso2.carbon.connector.integration.test.amazonses;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.OMTextImpl;
import org.apache.axiom.om.util.AXIOMUtil;
import org.jaxen.JaxenException;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.xml.sax.SAXException;

/**
 * The Class AmazonSESConnectorIntegrationTest.
 */
public class AmazonSESConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    /**
     * All required maps.
     */
    private Map<String, String> esbRequestHeadersMap, apiRequestHeadersMap, commonParametersMap, apiParametersMap;

    /**
     * define api params map.
     */
    private void loadApiParamsMap() {

        apiParametersMap = new HashMap<String, String>();

        apiParametersMap.put("dkimEnabled", "false");
        apiParametersMap.put("forwardingEnabled", "true");
        apiParametersMap.put("maxItems", "3");
        apiParametersMap.put("messageBody", "This is the Message Body: Integration Test Optional 02");
        apiParametersMap.put("messageSubject", "This is the Message Subject: Integration Test Optional 02");
        apiParametersMap.put("rawMessageInvalid", "Invalid Raw Message");

    }

    /**
     * Methods to build the Payload and Generate the Signature
     *
     * @throws ParserConfigurationException
     */
    private void buildAPIRequest(Map<String, String> singleValuedParamsMap, Map<String, String> multiValuedParamsMap)
            throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException,
            ParserConfigurationException {

        final SignatureGenerator generator =
                new SignatureGenerator(singleValuedParamsMap, multiValuedParamsMap, commonParametersMap);

        apiRequestHeadersMap.put(AmazonSESConstants.API_X_AMZ_DATE_HEADER_SET, generator.getFormattedDate());
        apiRequestHeadersMap.put(AmazonSESConstants.API_X_AMZN_AUTHORIZATION_HEADER_SET, generator.generateSignature(
                (String) connectorProperties.get("accessKeyId"), (String) connectorProperties.get("secretAccessKey")));
        connectorProperties.put("payload", generator.buildPayload());

    }

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        commonParametersMap = new HashMap<String, String>();

        init("amazonses-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        commonParametersMap.put("AWSAccessKeyId", connectorProperties.getProperty("accessKeyId"));
        commonParametersMap.put("SignatureMethod", connectorProperties.getProperty("signatureMethod"));
        commonParametersMap.put("SignatureVersion", connectorProperties.getProperty("signatureVersion"));
        commonParametersMap.put("Version", connectorProperties.getProperty("version"));

        apiRequestHeadersMap.put("Host", "email.us-west-2.amazonaws.com");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");

        // load all api parameter values
        loadApiParamsMap();
    }

    /**
     * Positive test case for setIdentityDkimEnabled method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "AmazonSES {setIdentityDkimEnabled} integration test with mandatory parameters.")
    public void testSetIdentityDkimEnabledWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:setIdentityDkimEnabled");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setIdentityDkimEnabled_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("SetIdentityDkimEnabledResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SetIdentityDkimEnabledResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }

    /**
     * Negative test case for setIdentityDkimEnabled method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testSetIdentityDkimEnabledWithMandatoryParameters"},
            description = "AmazonSES {setIdentityDkimEnabled} integration test with negative case.")
    public void testSetIdentityDkimEnabledWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:setIdentityDkimEnabled");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setIdentityDkimEnabled_negative.json");

        commonParametersMap.put("Action", "SetIdentityDkimEnabled");

        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();

        apiRequestSingleValuedParameterMap
                .put(AmazonSESConstants.API_DKIM_ENABLED, apiParametersMap.get("dkimEnabled"));
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_IDENTITY, "INVALID");
        buildAPIRequest(apiRequestSingleValuedParameterMap, null);

        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for listIdentityDkimAttributes method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws JaxenException
     */
    @Test(priority = 1, dependsOnMethods = {"testSetIdentityDkimEnabledWithNegativeCase"},
            description = "AmazonSES {listIdentityDkimAttributes} integration test with mandatory parameters.")
    public void testListIdentityDkimAttributesWithMandatoryParameters() throws IOException, JSONException,
            XMLStreamException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
            ParserConfigurationException, SAXException, JaxenException {

        esbRequestHeadersMap.put("Action", "urn:listIdentityDkimAttributes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listIdentityDkimAttributes_mandatory.json");

        commonParametersMap.put("Action", "GetIdentityDkimAttributes");

        Map<String, String> apiRequestMultiValuedParameterMap = new HashMap<String, String>();
        apiRequestMultiValuedParameterMap.put(AmazonSESConstants.API_IDENTITIES,
                connectorProperties.getProperty("identities"));
        buildAPIRequest(null, apiRequestMultiValuedParameterMap);

        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");
        String modifiedXml =
                apiRestResponse.getBody().toString()
                        .replaceFirst(" xmlns=\"http://ses.amazonaws.com/doc/2010-12-01/\"", "");
        List<OMTextImpl> result = (List) xPathEvaluate(AXIOMUtil.stringToOM(modifiedXml), "//key/text()", null);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        for (int i = 0; i < result.size(); i++) {
            Assert.assertEquals(
                    result.get(i).getText(),
                    esbRestResponse.getBody().getJSONObject("GetIdentityDkimAttributesResponse")
                            .getJSONObject("GetIdentityDkimAttributesResult").getJSONObject("DkimAttributes")
                            .getJSONArray("entry").getJSONObject(i).getString("key"));
        }

    }

    /**
     * Negative test case for listIdentityDkimAttributes method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentityDkimAttributesWithMandatoryParameters"},
            description = "AmazonSES {listIdentityDkimAttributes} integration test with negative case.")
    public void testListIdentityDkimAttributesWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:listIdentityDkimAttributes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listIdentityDkimAttributes_negative.json");

        commonParametersMap.put("Action", "GetIdentityDkimAttributes");

        // Sending no identities to api
        buildAPIRequest(null, null);

        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for listDomainDkim method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws JaxenException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentityDkimAttributesWithNegativeCase"},
            description = "AmazonSES {listDomainDkim} integration test with mandatory parameters.")
    public void testListDomainDkimWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException, JaxenException {

        esbRequestHeadersMap.put("Action", "urn:listDomainDkim");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDomainDkim_mandatory.json");

        commonParametersMap.put("Action", "VerifyDomainDkim");
        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap
                .put(AmazonSESConstants.API_DOMAIN, connectorProperties.getProperty("domain"));
        buildAPIRequest(apiRequestSingleValuedParameterMap, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");
        String modifiedXml =
                apiRestResponse.getBody().toString()
                        .replaceFirst(" xmlns=\"http://ses.amazonaws.com/doc/2010-12-01/\"", "");
        List<OMTextImpl> result =
                (List) xPathEvaluate(AXIOMUtil.stringToOM(modifiedXml),
                        "/VerifyDomainDkimResponse/VerifyDomainDkimResult/DkimTokens/member/text()", null);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        for (int i = 0; i < result.size(); i++) {
            Assert.assertEquals(
                    result.get(i).getText(),
                    esbRestResponse.getBody().getJSONObject("VerifyDomainDkimResponse")
                            .getJSONObject("VerifyDomainDkimResult").getJSONObject("DkimTokens").getJSONArray("member")
                            .getString(i));
        }

    }

    /**
     * Negative test case for listDomainDkim method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testListDomainDkimWithMandatoryParameters"},
            description = "AmazonSES {listDomainDkim} integration test with negative case.")
    public void testListDomainDkimWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:listDomainDkim");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDomainDkim_negative.json");

        commonParametersMap.put("Action", "VerifyDomainDkim");

        // Not sending any domain to api
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for verifyDomainIdentity method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testListDomainDkimWithNegativeCase"},
            description = "AmazonSES {verifyDomainIdentity} integration test with mandatory parameters.")
    public void testVerifyDomainIdentityWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:verifyDomainIdentity");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_verifyDomainIdentity_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("VerifyDomainIdentityResponse")
                        .getJSONObject("VerifyDomainIdentityResult").getString("VerificationToken"), null);
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("VerifyDomainIdentityResponse")
                        .getJSONObject("VerifyDomainIdentityResult").getString("VerificationToken"), "");

    }

    /**
     * Negative test case for verifyDomainIdentity method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testVerifyDomainIdentityWithMandatoryParameters"},
            description = "AmazonSES {verifyDomainIdentity} integration test with negative case.")
    public void testVerifyDomainIdentityWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:verifyDomainIdentity");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_verifyDomainIdentity_negative.json");

        commonParametersMap.put("Action", "VerifyDomainIdentity");
        // Not sending any domain
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for setIdentityFeedbackForwardingEnabled method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testVerifyDomainIdentityWithNegativeCase"},
            description = "AmazonSES {setIdentityFeedbackForwardingEnabled} integration test with mandatory parameters.")
    public void testSetIdentityFeedbackForwardingEnabledWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:setIdentityFeedbackForwardingEnabled");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setIdentityFeedback_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("SetIdentityFeedbackForwardingEnabledResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(esbRestResponse.getBody().getJSONObject("SetIdentityFeedbackForwardingEnabledResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"), "");

    }

    /**
     * Negative test case for setIdentityFeedbackForwardingEnabled method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testSetIdentityFeedbackForwardingEnabledWithMandatoryParameters"},
            description = "AmazonSES {setIdentityFeedbackForwardingEnabled} integration test with negative case.")
    public void testSetIdentityFeedbackForwardingEnabledWithNegativeCase() throws IOException, JSONException,
            XMLStreamException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
            ParserConfigurationException, SAXException {

        esbRequestHeadersMap.put("Action", "urn:setIdentityFeedbackForwardingEnabled");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setIdentityFeedback_negative.json");

        commonParametersMap.put("Action", "SetIdentityFeedbackForwardingEnabled");
        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_IDENTITY, "INVALID");
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_FORWARDING_ENABLED,
                apiParametersMap.get("forwardingEnabled"));
        buildAPIRequest(apiRequestSingleValuedParameterMap, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");
        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for verifyEmailIdentity method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testSetIdentityFeedbackForwardingEnabledWithNegativeCase"},
            description = "AmazonSES {verifyEmailIdentity} integration test with mandatory parameters.")
    public void testVerifyEmailIdentityWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:verifyEmailIdentity");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_verifyEmailIdentity_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("VerifyEmailIdentityResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("VerifyEmailIdentityResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");

    }

    /**
     * Negative test case for verifyEmailIdentity method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testVerifyEmailIdentityWithMandatoryParameters"},
            description = "AmazonSES {verifyEmailIdentity} integration test with optional parameters.")
    public void testVerifyEmailIdentityWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:verifyEmailIdentity");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_verifyEmailIdentity_negative.json");

        commonParametersMap.put("Action", "VerifyEmailIdentity");
        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_EMAIL_ADDRESS, "INVALID");
        buildAPIRequest(apiRequestSingleValuedParameterMap, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for setIdentityNotificationTopic method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testVerifyEmailIdentityWithNegativeCase"},
            description = "AmazonSES {setIdentityNotificationTopic} integration test with mandatory parameters.")
    public void testSetIdentityNotificationTopicWithOptionalParameters() throws IOException, JSONException,
            XMLStreamException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
            ParserConfigurationException, SAXException {

        esbRequestHeadersMap.put("Action", "urn:setIdentityNotificationTopic");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setIdentityNotification_optional.json");

        commonParametersMap.put("Action", "GetIdentityNotificationAttributes");
        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_IDENTITIES + ".1",
                connectorProperties.getProperty("identity"));
        buildAPIRequest(apiRequestSingleValuedParameterMap, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String key = getValueByExpression("//*[local-name()='key']/text()", apiRestResponse.getBody());
        String deliveryTopic = getValueByExpression("//*[local-name()='DeliveryTopic']/text()", apiRestResponse.getBody());
        String forwardingEnabled = getValueByExpression("//*[local-name()='ForwardingEnabled']/text()", apiRestResponse.getBody());

        Assert.assertEquals(key, connectorProperties.getProperty("identity"));
        Assert.assertEquals(deliveryTopic, connectorProperties.getProperty("snsTopic"));
        Assert.assertTrue(Boolean.parseBoolean(forwardingEnabled));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("SetIdentityNotificationTopicResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(esbRestResponse.getBody().getJSONObject("SetIdentityNotificationTopicResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"), "");


    }

    /**
     * Positive test case for setIdentityNotificationTopic method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testSetIdentityNotificationTopicWithOptionalParameters"},
            description = "AmazonSES {setIdentityNotificationTopic} integration test with mandatory parameters.")
    public void testSetIdentityNotificationTopicWithMandatoryParameters() throws IOException, JSONException,
            XMLStreamException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
            ParserConfigurationException, SAXException {

        esbRequestHeadersMap.put("Action", "urn:setIdentityNotificationTopic");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_setIdentityNotification_mandatory.json");

        commonParametersMap.put("Action", "GetIdentityNotificationAttributes");
        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_IDENTITIES + ".1",
                connectorProperties.getProperty("identity"));
        buildAPIRequest(apiRequestSingleValuedParameterMap, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String key = getValueByExpression("//*[local-name()='key']/text()", apiRestResponse.getBody());
        String deliveryTopic = getValueByExpression("//*[local-name()='DeliveryTopic']/text()", apiRestResponse.getBody());
        String forwardingEnabled = getValueByExpression("//*[local-name()='ForwardingEnabled']/text()", apiRestResponse.getBody());

        Assert.assertEquals(key, connectorProperties.getProperty("identity"));
        Assert.assertEquals(deliveryTopic, "");
        Assert.assertTrue(Boolean.parseBoolean(forwardingEnabled));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("SetIdentityNotificationTopicResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(esbRestResponse.getBody().getJSONObject("SetIdentityNotificationTopicResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"), "");

    }


    /**
     * Negative test case for setIdentityNotificationTopic method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testSetIdentityNotificationTopicWithMandatoryParameters"},
            description = "AmazonSES {setIdentityNotificationTopic} integration test with negative case.")
    public void testSetIdentityNotificationTopicWithNegativeCase() throws IOException, JSONException,
            XMLStreamException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
            ParserConfigurationException, SAXException {

        esbRequestHeadersMap.put("Action", "urn:setIdentityNotificationTopic");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setIdentityNotifTopic_negative.json");

        commonParametersMap.put("Action", "SetIdentityNotificationTopic");
        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_IDENTITY,
                connectorProperties.getProperty("identity"));
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_NOTIFICATION_TYPE, "INVALID");
        buildAPIRequest(apiRequestSingleValuedParameterMap, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for listIdentityNotificationAttributes method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws JaxenException
     */
    @Test(priority = 1, dependsOnMethods = {"testSetIdentityNotificationTopicWithNegativeCase"},
            description = "AmazonSES {listIdentityNotificationAttributes} integration test with mandatory parameters.")
    public void testListIdentityNotificationAttributesWithMandatoryParameters() throws IOException, JSONException,
            XMLStreamException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
            ParserConfigurationException, SAXException, JaxenException {

        esbRequestHeadersMap.put("Action", "urn:listIdentityNotificationAttributes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listIdentityNotification_mandatory.json");

        commonParametersMap.put("Action", "GetIdentityNotificationAttributes");

        Map<String, String> apiRequestMultiValuedParameterMap = new HashMap<String, String>();
        apiRequestMultiValuedParameterMap.put(AmazonSESConstants.API_IDENTITIES,
                connectorProperties.getProperty("identities"));
        buildAPIRequest(null, apiRequestMultiValuedParameterMap);

        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");
        String modifiedXml =
                apiRestResponse.getBody().toString()
                        .replaceFirst(" xmlns=\"http://ses.amazonaws.com/doc/2010-12-01/\"", "");
        List<OMTextImpl> result = (List) xPathEvaluate(AXIOMUtil.stringToOM(modifiedXml), "//key/text()", null);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        for (int i = 0; i < result.size(); i++) {
            Assert.assertEquals(
                    result.get(i).getText(),
                    esbRestResponse.getBody().getJSONObject("GetIdentityNotificationAttributesResponse")
                            .getJSONObject("GetIdentityNotificationAttributesResult")
                            .getJSONObject("NotificationAttributes").getJSONArray("entry").getJSONObject(i)
                            .getString("key"));
        }

    }

    /**
     * Negative test case for listIdentityNotificationAttributes method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentityNotificationAttributesWithMandatoryParameters"},
            description = "AmazonSES {listIdentityNotificationAttributes} integration test with negative case.")
    public void testListIdentityNotificationAttributesWithNegativeCase() throws IOException, JSONException,
            XMLStreamException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
            ParserConfigurationException, SAXException {

        esbRequestHeadersMap.put("Action", "urn:listIdentityNotificationAttributes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listIdentityNotification_negative.json");

        commonParametersMap.put("Action", "GetIdentityNotificationAttributes");
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for listIdentityVerificationAttributes method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws JaxenException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentityNotificationAttributesWithNegativeCase"},
            description = "AmazonSES {listIdentityVerificationAttributes} integration test with mandatory parameters.")
    public void testListIdentityVerificationAttributesWithMandatoryParameters() throws IOException, JSONException,
            XMLStreamException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
            ParserConfigurationException, SAXException, JaxenException {

        esbRequestHeadersMap.put("Action", "urn:listIdentityVerificationAttributes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listIdentityVerification_mandatory.json");

        commonParametersMap.put("Action", "GetIdentityVerificationAttributes");

        Map<String, String> apiRequestMultiValuedParameterMap = new HashMap<String, String>();
        apiRequestMultiValuedParameterMap.put(AmazonSESConstants.API_IDENTITIES,
                connectorProperties.getProperty("identities"));
        buildAPIRequest(null, apiRequestMultiValuedParameterMap);

        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");
        String modifiedXml =
                apiRestResponse.getBody().toString()
                        .replaceFirst(" xmlns=\"http://ses.amazonaws.com/doc/2010-12-01/\"", "");
        List<OMTextImpl> result = (List) xPathEvaluate(AXIOMUtil.stringToOM(modifiedXml), "//key/text()", null);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        for (int i = 0; i < result.size(); i++) {
            Assert.assertEquals(
                    result.get(i).getText(),
                    esbRestResponse.getBody().getJSONObject("GetIdentityVerificationAttributesResponse")
                            .getJSONObject("GetIdentityVerificationAttributesResult")
                            .getJSONObject("VerificationAttributes").getJSONArray("entry").getJSONObject(i)
                            .getString("key"));
        }

    }

    /**
     * Negative test case for listIdentityVerificationAttributes method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentityVerificationAttributesWithMandatoryParameters"},
            description = "AmazonSES {listIdentityVerificationAttributes} integration test with negative case.")
    public void testListIdentityVerificationAttributesWithNegativeCase() throws IOException, JSONException,
            XMLStreamException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
            ParserConfigurationException, SAXException {

        esbRequestHeadersMap.put("Action", "urn:listIdentityVerificationAttributes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listIdentityVerification_negative.json");

        commonParametersMap.put("Action", "GetIdentityVerificationAttributes");
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for listIdentities method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws JaxenException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentityVerificationAttributesWithNegativeCase"},
            description = "AmazonSES {listIdentities} integration test with mandatory parameters.")
    public void testListIdentitiesWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException, JaxenException {

        esbRequestHeadersMap.put("Action", "urn:listIdentities");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIdentities_mandatory.json");

        commonParametersMap.put("Action", "ListIdentities");

        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_IDENTITY_TYPE,
                connectorProperties.getProperty("identityType"));
        buildAPIRequest(apiRequestSingleValuedParameterMap, null);

        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");
        String modifiedXml =
                apiRestResponse.getBody().toString()
                        .replaceFirst(" xmlns=\"http://ses.amazonaws.com/doc/2010-12-01/\"", "");
        List<OMTextImpl> result =
                (List) xPathEvaluate(AXIOMUtil.stringToOM(modifiedXml),
                        "/ListIdentitiesResponse/ListIdentitiesResult/Identities/member/text()", null);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        for (int i = 0; i < result.size(); i++) {
            Assert.assertEquals(
                    result.get(i).getText(),
                    esbRestResponse.getBody().getJSONObject("ListIdentitiesResponse")
                            .getJSONObject("ListIdentitiesResult").getJSONObject("Identities").getJSONArray("member")
                            .getString(i));
        }

    }

    /**
     * Positive test case for listIdentities method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws JaxenException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentitiesWithMandatoryParameters"},
            description = "AmazonSES {listIdentities} integration test with optional parameters.")
    public void testListIdentitiesWithOptionalParameters() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException, JaxenException {

        esbRequestHeadersMap.put("Action", "urn:listIdentities");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIdentities_optional.json");

        commonParametersMap.put("Action", "ListIdentities");

        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_IDENTITY_TYPE,
                connectorProperties.getProperty("identityType"));
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_MAX_ITEMS, apiParametersMap.get("maxItems"));
        buildAPIRequest(apiRequestSingleValuedParameterMap, null);

        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");
        String modifiedXml =
                apiRestResponse.getBody().toString()
                        .replaceFirst(" xmlns=\"http://ses.amazonaws.com/doc/2010-12-01/\"", "");
        List<OMTextImpl> result =
                (List) xPathEvaluate(AXIOMUtil.stringToOM(modifiedXml),
                        "/ListIdentitiesResponse/ListIdentitiesResult/Identities/member/text()", null);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        for (int i = 0; i < result.size(); i++) {
            Assert.assertEquals(
                    result.get(i).getText(),
                    esbRestResponse.getBody().getJSONObject("ListIdentitiesResponse")
                            .getJSONObject("ListIdentitiesResult").getJSONObject("Identities").getJSONArray("member")
                            .getString(i));
        }
        Assert.assertEquals(result.size(),
                Integer.parseInt(apiRequestSingleValuedParameterMap.get(AmazonSESConstants.API_MAX_ITEMS)));

    }

    /**
     * Negative test case for listIdentities method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentitiesWithOptionalParameters"},
            description = "AmazonSES {listIdentities} integration test with optional parameters.")
    public void testListIdentitiesWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:listIdentities");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIdentities_negative.json");

        commonParametersMap.put("Action", "ListIdentities");
        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_IDENTITY_TYPE, "INVALID");
        buildAPIRequest(apiRequestSingleValuedParameterMap, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for deleteIdentity method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentitiesWithNegativeCase"},
            description = "AmazonSES {deleteIdentity} integration test with mandatory parameters.")
    public void testDeleteIdentityWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteIdentity");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteIdentity_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("DeleteIdentityResponse").getJSONObject("ResponseMetadata")
                        .getString("RequestId"), null);
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("DeleteIdentityResponse").getJSONObject("ResponseMetadata")
                        .getString("RequestId"), "");
    }

    /**
     * Negative test case for deleteIdentity method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testDeleteIdentityWithMandatoryParameters"},
            description = "AmazonSES {deleteIdentity} integration test with negative case.")
    public void testDeleteIdentityWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:deleteIdentity");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteIdentity_negative.json");

        commonParametersMap.put("Action", "DeleteIdentity");
        // Not sending identity name
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));

    }

    /**
     * Positive test case for sendEmail method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testDeleteIdentityWithNegativeCase"},
            description = "AmazonSES {sendEmail} integration test with mandatory parameters.")
    public void testSendEmailWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:sendEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendEmail_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("SendEmailResponse")
                .getJSONObject("SendEmailResult").getString("MessageId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SendEmailResponse").getJSONObject("SendEmailResult")
                        .getString("MessageId"), "");
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("SendEmailResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SendEmailResponse").getJSONObject("ResponseMetadata")
                        .getString("RequestId"), "");
    }

    /**
     * Positive test case for sendEmail method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testSendEmailWithMandatoryParameters"},
            description = "AmazonSES {sendEmail} integration test with optional parameters.")
    public void testSendEmailWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:sendEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendEmail_optional.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("SendEmailResponse")
                .getJSONObject("SendEmailResult").getString("MessageId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SendEmailResponse").getJSONObject("SendEmailResult")
                        .getString("MessageId"), "");
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("SendEmailResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SendEmailResponse").getJSONObject("ResponseMetadata")
                        .getString("RequestId"), "");

    }

    /**
     * Negative test case for sendEmail method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testSendEmailWithOptionalParameters"},
            description = "AmazonSES {sendEmail} integration test with negative case.")
    public void testSendEmailNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:sendEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendEmail_negative.json");

        commonParametersMap.put("Action", "SendEmail");

        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_MESSAGE_SUBJECT,
                apiParametersMap.get("messageSubject"));
        apiRequestSingleValuedParameterMap
                .put(AmazonSESConstants.API_MESSAGE_BODY, apiParametersMap.get("messageBody"));
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_SOURCE_ADDRESS, "INVALID@INVALID");

        Map<String, String> apiRequestMultiValuedParameterMap = new HashMap<String, String>();
        apiRequestMultiValuedParameterMap.put(AmazonSESConstants.API_TO_ADDRESSES,
                connectorProperties.getProperty("toAddresses"));

        buildAPIRequest(apiRequestSingleValuedParameterMap, apiRequestMultiValuedParameterMap);

        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for sendRawEmail method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testSendEmailNegativeCase"},
            description = "AmazonSES {sendRawEmail} integration test with mandatory parameters.")
    public void testSendRawEmailWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:sendRawEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendRawEmail_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SendRawEmailResponse").getJSONObject("SendRawEmailResult")
                        .getString("MessageId"), "");
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SendRawEmailResponse").getJSONObject("ResponseMetadata")
                        .getString("RequestId"), "");


    }

    /**
     * Positive test case for sendRawEmail method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testSendRawEmailWithMandatoryParameters"},
            description = "AmazonSES {sendRawEmail} integration test with optional parameters.")
    public void testSendRawEmailWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:sendRawEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendRawEmail_optional.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SendRawEmailResponse").getJSONObject("SendRawEmailResult")
                        .getString("MessageId"), "");
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SendRawEmailResponse").getJSONObject("ResponseMetadata")
                        .getString("RequestId"), "");

    }

    /**
     * Negative test case for sendRawEmail method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testSendRawEmailWithOptionalParameters"},
            description = "AmazonSES {sendRawEmail} integration test with negative case.")
    public void testSendRawEmailNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:sendRawEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendRawEmail_negative.json");

        commonParametersMap.put("Action", "SendRawEmail");

        Map<String, String> apiRequestSingleValuedParameterMap = new HashMap<String, String>();
        apiRequestSingleValuedParameterMap.put(AmazonSESConstants.API_RAW_MESSAGE,
                apiParametersMap.get("rawMessageInvalid"));

        Map<String, String> apiRequestMultiValuedParameterMap = new HashMap<String, String>();
        apiRequestMultiValuedParameterMap.put(AmazonSESConstants.API_DESTINATIONS,
                connectorProperties.getProperty("destinations"));
        buildAPIRequest(apiRequestSingleValuedParameterMap, apiRequestMultiValuedParameterMap);

        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }


    /**
     * Positive test case for listIdentityPolicies method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testSendRawEmailNegativeCase"},
            description = "AmazonSES {listIdentityPolicies} integration test with mandatory parameters.")
    public void testListIdentityPoliciesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listIdentityPolicies");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIdentityPolicies_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("ListIdentityPoliciesResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("ListIdentityPoliciesResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }

    /**
     * Negative test case for listIdentityPolicies method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentityPoliciesWithMandatoryParameters"},
            description = "AmazonSES {listIdentityPolicies} integration test with negative case.")
    public void testListIdentityPoliciesWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:listIdentityPolicies");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIdentityPolicies_negative.json");

        commonParametersMap.put("Action", "ListIdentityPolicies");

        // Not sending any domain to api
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }


    /**
     * Positive test case for getIdentityPolicies method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testListIdentityPoliciesWithNegativeCase"},
            description = "AmazonSES {getIdentityPolicies} integration test with mandatory parameters.")
    public void testGetIdentityPoliciesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getIdentityPolicies");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIdentityPolicies_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("GetIdentityPoliciesResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("GetIdentityPoliciesResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }

    /**
     * Negative test case for getIdentityPolicies method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testGetIdentityPoliciesWithMandatoryParameters"},
            description = "AmazonSES {getIdentityPolicies} integration test with negative case.")
    public void testGetIdentityPoliciesWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:getIdentityPolicies");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIdentityPolicies_negative.json");

        commonParametersMap.put("Action", "GetIdentityPolicies");

        // Not sending any domain to api
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }


    /**
     * Positive test case for deleteIdentityPolicy method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testGetIdentityPoliciesWithNegativeCase"},
            description = "AmazonSES {deleteIdentityPolicy} integration test with mandatory parameters.")
    public void testDeleteIdentityPolicyWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteIdentityPolicy");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteIdentityPolicy_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("DeleteIdentityPolicyResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("DeleteIdentityPolicyResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }

    /**
     * Negative test case for deleteIdentityPolicy method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testDeleteIdentityPolicyWithMandatoryParameters"},
            description = "AmazonSES {deleteIdentityPolicy} integration test with negative case.")
    public void testDeleteIdentityPolicyWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:deleteIdentityPolicy");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteIdentityPolicy_negative.json");

        commonParametersMap.put("Action", "DeleteIdentityPolicy");

        // Not sending any domain to api
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for describeActiveReceiptRuleSet method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testDeleteIdentityPolicyWithNegativeCase"},
            description = "AmazonSES {describeActiveReceiptRuleSet} integration test with mandatory parameters.")
    public void testDescribeActiveReceiptRuleSetWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:describeActiveReceiptRuleSet");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_describeActiveReceiptRuleSet_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("DescribeActiveReceiptRuleSetResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("DescribeActiveReceiptRuleSetResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }


    /**
     * Positive test case for createReceiptRuleSet method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testDescribeActiveReceiptRuleSetWithMandatoryParameters"},
            description = "AmazonSES {createReceiptRuleSet} integration test with mandatory parameters.")
    public void testCreateReceiptRuleSetWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createReceiptRuleSet");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReceiptRuleSet_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("CreateReceiptRuleSetResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("CreateReceiptRuleSetResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }

    /**
     * Negative test case for createReceiptRuleSet method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateReceiptRuleSetWithMandatoryParameters"},
            description = "AmazonSES {createReceiptRuleSet} integration test with negative case.")
    public void testCreateReceiptRuleSetWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:createReceiptRuleSet");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReceiptRuleSet_negative.json");

        commonParametersMap.put("Action", "CreateReceiptRuleSet");

        // Not sending any domain to api
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for deleteReceiptRuleSet method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateReceiptRuleSetWithNegativeCase"},
            description = "AmazonSES {deleteReceiptRuleSet} integration test with mandatory parameters.")
    public void testDeleteReceiptRuleSetWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteReceiptRuleSet");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteReceiptRuleSet_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("DeleteReceiptRuleSetResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("DeleteReceiptRuleSetResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }

    /**
     * Negative test case for deleteReceiptRuleSet method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testDeleteReceiptRuleSetWithMandatoryParameters"},
            description = "AmazonSES {createReceiptRuleSet} integration test with negative case.")
    public void testDeleteReceiptRuleSetWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:deleteReceiptRuleSet");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteReceiptRuleSet_negative.json");

        commonParametersMap.put("Action", "DeleteReceiptRuleSet");

        // Not sending any domain to api
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }


    /**
     * Positive test case for deleteReceiptRule method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testDeleteReceiptRuleSetWithNegativeCase"},
            description = "AmazonSES {deleteReceiptRule} integration test with mandatory parameters.")
    public void testDeleteReceiptRuleWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteReceiptRule");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteReceiptRule_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("DeleteReceiptRuleResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("DeleteReceiptRuleResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }

    /**
     * Negative test case for deleteReceiptRule method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testDeleteReceiptRuleWithMandatoryParameters"},
            description = "AmazonSES {deleteReceiptRule} integration test with negative case.")
    public void testDeleteReceiptRuleWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:deleteReceiptRule");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteReceiptRule_negative.json");

        commonParametersMap.put("Action", "DeleteReceiptRule");

        // Not sending any domain to api
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for describeReceiptRuleSet method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testDeleteReceiptRuleWithNegativeCase"},
            description = "AmazonSES {describeReceiptRuleSet} integration test with mandatory parameters.")
    public void testDescribeReceiptRuleSetWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:describeReceiptRuleSet");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_describeReceiptRuleSet_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("DescribeReceiptRuleSetResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("DescribeReceiptRuleSetResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }

    /**
     * Negative test case for describeReceiptRuleSet method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testDescribeReceiptRuleSetWithMandatoryParameters"},
            description = "AmazonSES {describeReceiptRuleSet} integration test with negative case.")
    public void testDescribeReceiptRuleSetWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:describeReceiptRuleSet");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_describeReceiptRuleSet_negative.json");

        commonParametersMap.put("Action", "DescribeReceiptRuleSet");

        // Not sending any domain to api
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }

    /**
     * Positive test case for describeReceiptRule method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testDescribeReceiptRuleSetWithNegativeCase"},
            description = "AmazonSES {describeReceiptRule} integration test with mandatory parameters.")
    public void testDescribeReceiptRuleWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:describeReceiptRule");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_describeReceiptRule_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("DescribeReceiptRuleResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("DescribeReceiptRuleResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }

    /**
     * Negative test case for describeReceiptRule method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testDescribeReceiptRuleWithMandatoryParameters"},
            description = "AmazonSES {describeReceiptRule} integration test with negative case.")
    public void testDescribeReceiptRuleWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:describeReceiptRule");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_describeReceiptRule_negative.json");

        commonParametersMap.put("Action", "DescribeReceiptRule");

        // Not sending any domain to api
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }


    /**
     * Positive test case for listReceiptRuleSets method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, dependsOnMethods = {"testDescribeReceiptRuleWithNegativeCase"},
            description = "AmazonSES {listReceiptRuleSets} integration test with mandatory parameters.")
    public void testListReceiptRuleSetsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listReceiptRuleSets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReceiptRuleSets_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("ListReceiptRuleSetsResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("ListReceiptRuleSetsResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }


    /**
     * Positive test case for setActiveReceiptRuleSet method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, dependsOnMethods = {"testListReceiptRuleSetsWithMandatoryParameters"},
            description = "AmazonSES {setActiveReceiptRuleSet} integration test with mandatory parameters.")
    public void testSetActiveReceiptRuleSetWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:setActiveReceiptRuleSet");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setActiveReceiptRuleSet_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("SetActiveReceiptRuleSetResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SetActiveReceiptRuleSetResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }


    /**
     * Positive test case for setReceiptRulePosition method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, dependsOnMethods = {"testSetActiveReceiptRuleSetWithMandatoryParameters"},
            description = "AmazonSES {setReceiptRulePosition} integration test with mandatory parameters.")
    public void testSetReceiptRulePositionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:setReceiptRulePosition");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setReceiptRulePosition_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("SetReceiptRulePositionResponse")
                .getJSONObject("ResponseMetadata").getString("RequestId"));
        Assert.assertNotEquals(
                esbRestResponse.getBody().getJSONObject("SetReceiptRulePositionResponse")
                        .getJSONObject("ResponseMetadata").getString("RequestId"), "");
    }

    /**
     * Negative test case for setReceiptRulePosition method.
     *
     * @throws JSONException
     * @throws IOException
     * @throws XMLStreamException
     * @throws XPathExpressionException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test(priority = 1, dependsOnMethods = {"testSetReceiptRulePositionWithMandatoryParameters"},
            description = "AmazonSES {setReceiptRulePosition} integration test with negative case.")
    public void testSetReceiptRulePositionWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException,
            SAXException {

        esbRequestHeadersMap.put("Action", "urn:setReceiptRulePosition");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setReceiptRulePosition_negative.json");

        commonParametersMap.put("Action", "SetReceiptRulePosition");

        // Not sending any domain to api
        buildAPIRequest(null, null);
        RestResponse<OMElement> apiRestResponse =
                sendXmlRestRequest(connectorProperties.getProperty("apiUrl"), "POST", apiRequestHeadersMap,
                        "api_common");

        String apiErrorCode = getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody());
        String apiErrorMessage = getValueByExpression("//*[local-name()='Message']/text()", apiRestResponse.getBody());
        String apiErrorType = getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody());

        // Asserting Error Code, Type and Message
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorType,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Type"));
        Assert.assertEquals(apiErrorCode,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Code"));
        Assert.assertEquals(apiErrorMessage,
                esbRestResponse.getBody().getJSONObject("ErrorResponse").getJSONObject("Error").getString("Message"));
    }
}
