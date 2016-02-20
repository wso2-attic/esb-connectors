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

package org.wso2.carbon.connector.integration.test.amazonsqs;

import junit.framework.Assert;
import org.apache.axiom.om.OMElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonsqsConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map< String, String > esbRequestHeadersMap = new HashMap< String, String >();

    private Map< String, String > apiRequestHeadersMap = new HashMap< String, String >();

    private static String apiEndPoint, apiUrl;

    final static String charSet = Charset.defaultCharset().toString();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("amazonsqs-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "");

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/xml");

        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");

        apiEndPoint = "http://sqs." + connectorProperties.getProperty("region") + ".amazonaws.com";

    }

    /**
     * Positive test case for createQueue method with mandatory parameters.
     */
    @Test(priority = 2, description = "AmazonSQS {createQueue} integration test with mandatory parameters.")
    public void testCreateQueueMandatoryParameters() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            IllegalStateException, JSONException, XMLStreamException, XPathExpressionException, SAXException,
            ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:createQueue");
        generateApiRequest("api_createQueue_mandatory.json");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQueue_mandatory.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        String qId = getValueByExpression("//*[local-name()='QueueUrl']/text()", esbResponse.getBody()).split("/")[3];
        String apiQId = getValueByExpression("//*[local-name()='QueueUrl']/text()", esbResponse.getBody()).split("/")[3];
        connectorProperties.put("queueId", qId);
        connectorProperties.put("apiQueueId", apiQId);
        Assert.assertNotNull(getValueByExpression("//*[local-name()='QueueUrl']/text()", esbResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='QueueUrl']/text()", esbResponse.getBody()).equals(""));
        Assert.assertNotNull(getValueByExpression("//*[local-name()='QueueUrl']/text()", apiRestResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='QueueUrl']/text()", apiRestResponse.getBody()).equals(""));
    }

    /**
     * Positive test case for createQueue method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateQueueMandatoryParameters"}, description = "AmazonSQS {createQueue} integration test with optional parameters.")
    public void testCreateQueueOptionalParameters() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            IllegalStateException, JSONException, XMLStreamException, XPathExpressionException, SAXException,
            ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:createQueue");
        generateApiRequest("api_createQueue_optional.json");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQueue_optional.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        String qId = getValueByExpression("//*[local-name()='QueueUrl']/text()", esbResponse.getBody()).split("/")[3];
        String apiQId = getValueByExpression("//*[local-name()='QueueUrl']/text()", esbResponse.getBody()).split("/")[3];
        connectorProperties.put("optionalQueueId", qId);
        connectorProperties.put("optionalAPIQueueId", apiQId);
        Assert.assertNotNull(getValueByExpression("//*[local-name()='QueueUrl']/text()", esbResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='QueueUrl']/text()", esbResponse.getBody()).equals(""));
        Assert.assertNotNull(getValueByExpression("//*[local-name()='QueueUrl']/text()", apiRestResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='QueueUrl']/text()", apiRestResponse.getBody()).equals(""));
    }

    /**
     * Negative test case for createQueue method.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateQueueOptionalParameters"}, description = "AmazonSQS {createQueue} integration test negative case.")
    public void testCreateQueueNegativeCase() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            IllegalStateException, JSONException, XMLStreamException, XPathExpressionException, SAXException,
            ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:createQueue");
        generateApiRequest("api_createQueue_negative.json");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQueue_negative.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody()),
                getValueByExpression("//*[local-name()='Code']/text()", esbResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='Type']/text()", apiRestResponse.getBody()),
                getValueByExpression("//*[local-name()='Type']/text()", esbResponse.getBody()));

    }

    /**
     * Positive test case for listQueues method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateQueueNegativeCase"}, description = "AmazonSQS {listQueues} integration test with mandatory parameters.")
    public void testListQueuesMandatoryParameters() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            IllegalStateException, JSONException, XMLStreamException, XPathExpressionException, SAXException,
            ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:listQueues");
        generateApiRequest("api_listQueues_mandatory.json");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listQueues_mandatory.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("count(//*[local-name()='QueueUrl'])", apiRestResponse.getBody()),
                getValueByExpression("count(//*[local-name()='QueueUrl'])", esbResponse.getBody()));

    }

    /**
     * Positive test case for listQueues method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testListQueuesMandatoryParameters"}, description = "AmazonSQS {listQueues} integration test with optional parameters")
    public void testListQueuesWithOptionalParameters() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:listQueues");
        generateApiRequest("api_listQueues_optional.json");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listQueues_optional.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");

        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(getValueByExpression("count(//*[local-name()='QueueUrl'])", apiRestResponse.getBody()),
                getValueByExpression("count(//*[local-name()='QueueUrl'])", esbResponse.getBody()));

    }

    /**
     * Positive test case for sendMessage method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testListQueuesWithOptionalParameters"}, description = "AmazonSQS {sendMessage} integration test with mandatory parameters")
    public void testSendMessageWithMandatoryParameters() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:sendMessage");
        generateApiRequest("api_sendMessage_mandatory.json");

        String endPoint = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("apiQueueName"), charSet) + "/";
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendMessage_mandatory.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(endPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(getValueByExpression("//*[local-name()='MessageId']/text()", esbResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='MessageId']/text()", esbResponse.getBody()).equals(""));
        Assert.assertNotNull(getValueByExpression("//*[local-name()='MessageId']/text()", apiRestResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='MessageId']/text()", apiRestResponse.getBody()).equals(""));

    }

    /**
     * Positive test case for sendMessage method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testSendMessageWithMandatoryParameters"}, description = "AmazonSQS {sendMessage} integration test with optional parameters")
    public void testSendMessageWithOptionalParameters() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:sendMessage");
        generateApiRequest("api_sendMessage_optional.json");

        String endPoint = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("apiQueueName"), charSet) + "/";
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendMessage_optional.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(endPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(getValueByExpression("//*[local-name()='MessageId']/text()", esbResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='MessageId']/text()", esbResponse.getBody()).equals(""));
        Assert.assertNotNull(getValueByExpression("//*[local-name()='MessageId']/text()", apiRestResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='MessageId']/text()", apiRestResponse.getBody()).equals(""));
    }

    /**
     * Negative test case for sendMessage method.
     */
    @Test(priority = 2, dependsOnMethods = {"testSendMessageWithOptionalParameters"}, description = "AmazonSQS {sendMessage} integration test with optional parameters")
    public void testSendMessageNegativeCase() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            IllegalStateException, JSONException, XMLStreamException, XPathExpressionException, SAXException,
            ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:sendMessage");
        generateApiRequest("api_sendMessage_negative.json");

        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendMessage_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
    }

    /**
     * Positive test case for receiveMessage method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testSendMessageNegativeCase"}, description = "AmazonSQS {receiveMessage} integration test with mandatory parameters")
    public void testReceiveMessageWithMandatoryParameters() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:receiveMessage");
        generateApiRequest("api_receiveMessage_mandatory.json");

        String endPoint = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("apiQueueName"), charSet) + "/";
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_receiveMessage_mandatory.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(endPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        connectorProperties.put("esbReceiptHandle",
                getValueByExpression("//*[local-name()='ReceiptHandle'][1]", esbResponse.getBody()));
        connectorProperties.put("apiReceiptHandle",
                getValueByExpression("//*[local-name()='ReceiptHandle'][1]", apiRestResponse.getBody()));
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);

        Assert.assertNotNull(getValueByExpression("//*[local-name()='Body']/text()", esbResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='Body']/text()", esbResponse.getBody()).equals(""));
        Assert.assertNotNull(getValueByExpression("//*[local-name()='Body']/text()", apiRestResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='Body']/text()", apiRestResponse.getBody()).equals(""));
    }

    /**
     * Positive test case for receiveMessage method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testReceiveMessageWithMandatoryParameters"}, description = "AmazonSQS {receiveMessage} integration test with optional parameters")
    public void testReceiveMessageWithOptionalParameters() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:receiveMessage");
        generateApiRequest("api_receiveMessage_optional.json");

        String endPoint = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("apiQueueName"), charSet) + "/";
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_receiveMessage_mandatory.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(endPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(getValueByExpression("//*[local-name()='Body']/text()", esbResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='Body']/text()", esbResponse.getBody()).equals(""));
        Assert.assertNotNull(getValueByExpression("//*[local-name()='Body']/text()", apiRestResponse.getBody()));
        Assert.assertFalse(getValueByExpression("//*[local-name()='Body']/text()", apiRestResponse.getBody()).equals(""));
    }

    /**
     * Negative test case for receiveMessage method.
     */
    @Test(priority = 2, dependsOnMethods = {"testReceiveMessageWithOptionalParameters"}, description = "AmazonSQS {receiveMessage} integration test with optional parameters")
    public void testReceiveMessageNegativeCase() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            IllegalStateException, JSONException, XMLStreamException, XPathExpressionException, SAXException,
            ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:receiveMessage");
        generateApiRequest("api_receiveMessage_negative.json");

        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_receiveMessage_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
    }

    /**
     * Positive test case for changeMessageVisibility method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testReceiveMessageNegativeCase"}, description = "AmazonSQS {changeMessageVisibility} integration test with mandatory parameters")
    public void testChangeMessageVisibilityWithMandatoryParameters() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:changeMessageVisibility");
        generateApiRequest("api_changeMessageVisibility_mandatory.json");

        String endPoint = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("apiQueueName"), charSet) + "/";
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_changeMessageVisibility_mandatory.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(endPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertTrue(getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody()).equals(""));
        Assert.assertTrue(getValueByExpression("//*[local-name()='Code']/text()", esbResponse.getBody()).equals(""));
    }

    /**
     * Negative test case for changeMessageVisibility method with negative parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testChangeMessageVisibilityWithMandatoryParameters"}, description = "AmazonSQS {changeMessageVisibility} integration test with mandatory parameters")
    public void testChangeMessageVisibilityNegativeCase() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:changeMessageVisibility");
        generateApiRequest("api_changeMessageVisibility_negative.json");

        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_changeMessageVisibility_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
    }

    /**
     * Positive test case for changeMessageVisibilityBatch method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testReceiveMessageNegativeCase"}, description = "AmazonSQS {changeMessageVisibilityBatch} integration test with mandatory parameters")
    public void testChangeMessageVisibilityBatchWithMandatoryParameters() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:changeMessageVisibilityBatch");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_changeMessageVisibilityBatch_mandatory.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for changeMessageVisibilityBatch method with negative parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testChangeMessageVisibilityWithMandatoryParameters"}, description = "AmazonSQS {changeMessageVisibilityBatch} integration test with mandatory parameters")
    public void testChangeMessageVisibilityBatchNegativeCase() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:changeMessageVisibilityBatch");

        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_changeMessageVisibilityBatch_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
    }

    /**
     * Positive test case for deleteMessage method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testChangeMessageVisibilityNegativeCase"}, description = "AmazonSQS {deleteMessage} integration test with mandatory parameters")
    public void testDeleteMessageWithMandatoryParameters() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException, JSONException, XMLStreamException,
            XPathExpressionException, SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:deleteMessage");
        generateApiRequest("api_deleteMessage_mandatory.json");

        String endPoint = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("apiQueueName"), charSet) + "/";
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteMessage_mandatory.xml");
        RestResponse< OMElement > apiRestResponse =
                sendXmlRestRequest(endPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertTrue(getValueByExpression("//*[local-name()='Code']/text()", apiRestResponse.getBody()).equals(""));
        Assert.assertTrue(getValueByExpression("//*[local-name()='Code']/text()", esbResponse.getBody()).equals(""));
    }

    /**
     * Negative test case for deleteMessage method.
     */
    @Test(priority = 2, dependsOnMethods = {"testDeleteMessageWithMandatoryParameters"}, description = "AmazonSQS {deleteMessage} integration test negative case")
    public void testDeleteMessageNegativeCase() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            IllegalStateException, JSONException, XMLStreamException, XPathExpressionException, SAXException,
            ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:deleteMessage");
        generateApiRequest("api_deleteMessage_negative.json");

        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteMessage_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
    }

    /**
     * Positive test case for setQueueAttributes method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testDeleteMessageNegativeCase"}, description = "AmazonSQS {setQueueAttributes} integration test with mandatory parameters.")
    public void testSetQueueAttributesWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:setQueueAttributes");
        generateApiRequest("api_getQueueAttributes.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("queueName"), charSet) + "/";
        RestResponse< OMElement > apiResponseBeforeSet =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertFalse(("2").equals(getValueByExpression("//*[local-name()='Value']/text()",
                apiResponseBeforeSet.getBody())));
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setQueueAttributes_mandatory.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        RestResponse< OMElement > apiResponseAfterSet =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(getValueByExpression("//*[local-name()='Value']/text()", apiResponseAfterSet.getBody()),
                "2");
    }

    /**
     * Negative test case for setQueueAttributes method.
     *
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = {"testSetQueueAttributesWithMandatoryParameters"}, description = "AmazonSQS {setQueueAttributes} integration test for Negative case.")
    public void testSetQueueAttributesWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:setQueueAttributes");
        generateApiRequest("api_setQueueAttributes_negative.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("negativeQueueName"), charSet) + "/";
        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(apiResponse.getHttpStatusCode(), 400);
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setQueueAttributes_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
    }

    /**
     * Positive test case for getQueueAttributes method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testSetQueueAttributesWithNegativeCase"}, description = "AmazonSQS {getQueueAttributes} integration test with mandatory parameters.")
    public void testGetQueueAttributesWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:getQueueAttributes");
        generateApiRequest("api_getQueueAttributes_mandatory.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("queueName"), charSet) + "/";
        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQueueAttributes_mandatory.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getHttpStatusCode(), apiResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getQueueAttributes method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testGetQueueAttributesWithMandatoryParameters"}, description = "AmazonSQS {getQueueAttributes} integration test with optional parameters.")
    public void testGetQueueAttributesWithOptionalParameters() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:getQueueAttributes");
        generateApiRequest("api_getQueueAttributes_optional.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("queueName"), charSet) + "/";
        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQueueAttributes_optional.xml");

        Assert.assertEquals(getValueByExpression("//*[local-name()='Name']/text()", esbResponse.getBody()),
                getValueByExpression("//*[local-name()='Name']/text()", apiResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='Value']/text()", esbResponse.getBody()),
                getValueByExpression("//*[local-name()='Value']/text()", apiResponse.getBody()));
    }

    /**
     * Negative test case for getQueueAttributes method.
     *
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = {"testGetQueueAttributesWithOptionalParameters"}, description = "AmazonSQS {getQueueAttributes} integration test for Negative case.")
    public void testGetQueueAttributesWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:getQueueAttributes");
        generateApiRequest("api_getQueueAttributes_negative.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("negativeQueueName"), charSet) + "/";
        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(apiResponse.getHttpStatusCode(), 400);
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQueueAttributes_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
    }

    /**
     * Positive test case for listDeadLetterSourceQueues method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testGetQueueAttributesWithNegativeCase"}, description = "AmazonSQS {listDeadLetterSourceQueues} integration test with optional parameters.")
    public void testListDeadLetterSourceQueuesWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:listDeadLetterSourceQueues");
        generateApiRequest("api_listDeadLetterSourceQueues_optional.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("queueName"), charSet) + "/";
        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeadLetterSourceQueues_mandatory.xml");

        Assert.assertEquals(getValueByExpression("//*[local-name()='Name']/text()", esbResponse.getBody()),
                getValueByExpression("//*[local-name()='Name']/text()", apiResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='Value']/text()", esbResponse.getBody()),
                getValueByExpression("//*[local-name()='Value']/text()", apiResponse.getBody()));
    }

    /**
     * Negative test case for listDeadLetterSourceQueues method.
     *
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = {"testListDeadLetterSourceQueuesWithMandatoryParameters"}, description = "AmazonSQS {listDeadLetterSourceQueues} integration test for Negative case.")
    public void testListDeadLetterSourceQueuesWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:listDeadLetterSourceQueues");
        generateApiRequest("api_listDeadLetterSourceQueues_negative.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("negativeQueueName"), charSet) + "/";
        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(apiResponse.getHttpStatusCode(), 400);
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeadLetterSourceQueues_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
    }
    /**
     * Positive test case for getQueueUrl method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testGetQueueAttributesWithNegativeCase"}, description = "AmazonSQS {getQueueUrl} integration test with mandatory parameters.")
    public void testGetQueueUrlWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:getQueueUrl");
        generateApiRequest("api_getQueueUrl_mandatory.json");

        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQueueUrl_mandatory.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getHttpStatusCode(), apiResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getQueueUrl method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testGetQueueUrlWithMandatoryParameters"}, description = "AmazonSQS {getQueueUrl} integration test with optional parameters.")
    public void testGetQueueUrlWithOptionalParameters() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:getQueueUrl");
        generateApiRequest("api_getQueueUrl_optional.json");

        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQueueUrl_optional.xml");

        Assert.assertEquals(getValueByExpression("//*[local-name()='Name']/text()", esbResponse.getBody()),
                getValueByExpression("//*[local-name()='Name']/text()", apiResponse.getBody()));
        Assert.assertEquals(getValueByExpression("//*[local-name()='Value']/text()", esbResponse.getBody()),
                getValueByExpression("//*[local-name()='Value']/text()", apiResponse.getBody()));
    }

    /**
     * Negative test case for getQueueUrl method.
     *
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = {"testGetQueueUrlWithOptionalParameters"}, description = "AmazonSQS {getQueueUrl} integration test for Negative case.")
    public void testGetQueueUrlWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:getQueueUrl");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQueueUrl_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for addPermission method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testGetQueueAttributesWithNegativeCase"}, description = "AmazonSQS {addPermission} integration test with mandatory parameters.")
    public void testAddPermissionWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:addPermission");
        generateApiRequest("api_addPermission_mandatory.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("apiQueueName"), charSet) + "/";
        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addPermission_mandatory.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for addPermission method.
     */
    @Test(priority = 2, dependsOnMethods = {"testAddPermissionWithMandatoryParameters"}, description = "AmazonSQS {addPermission} adding permission to a respective queue.")
    public void testAddPermissionWithNegativecase() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:addPermission");
        generateApiRequest("api_addPermission_negative.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("negativeQueueName"), charSet) + "/";
        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addPermission_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(apiResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for removePermission method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testAddPermissionWithNegativecase"}, description = "AmazonSQS {removePermission} Removing permission form a respective queue.")
    public void testRemovePermissionWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:removePermission");
        generateApiRequest("api_addPermission_mandatory.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("apiQueueName"), charSet) + "/";
        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removePermission_mandatory.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for removePermission method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testRemovePermissionWithMandatoryParameters"}, description = "AmazonSQS {removePermission} removing permission to a respective queue.")
    public void testRemovePermissionWithNegativecase() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException {

        esbRequestHeadersMap.put("Action", "urn:removePermission");
        generateApiRequest("api_removePermission_negative.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("negativeQueueName"), charSet) + "/";
        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removePermission_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(apiResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for deleteQueue method with mandatory parameters.
     *
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = {"testRemovePermissionWithNegativecase"}, description = "AmazonSQS {deleteQueue} integration test with mandatory parameters.")
    public void testDeleteQueueWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:deleteQueue");
        generateApiRequest("api_listQueues.json");
        RestResponse< OMElement > apiResponseBeforeDelete =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(apiResponseBeforeDelete.getBody().toString().contains(
                "<QueueUrl>" + apiEndPoint + "/" + connectorProperties.getProperty("queueId") + "/"
                        + connectorProperties.getProperty("queueName") + "</QueueUrl>"));

        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteQueue_mandatory.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);

        RestResponse< OMElement > esbReDeleteResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteQueue_mandatory.xml");
        Assert.assertEquals(esbReDeleteResponse.getHttpStatusCode(), 400);
    }

    /**
     * Negative test case for deleteQueue method.
     *
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = {"testDeleteQueueWithMandatoryParameters"}, description = "AmazonSQS {deleteQueue} integration test case for Negativce case.")
    public void testDeleteQueueWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:deleteQueue");
        generateApiRequest("api_deleteQueue_negative.json");

        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("negativeQueueName"), charSet) + "/";

        RestResponse< OMElement > apiResponse =
                sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertEquals(apiResponse.getHttpStatusCode(), 400);
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteQueue_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
    }

    /**
     * Positive test case for purgeQueue method with mandatory parameters.
     *
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = {"testDeleteQueueWithNegativeCase"}, description = "AmazonSQS {purgeQueue} integration test with mandatory parameters.")
    public void testPurgeQueueWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:purgeQueue");
        generateApiRequest("api_listQueues.json");
        RestResponse< OMElement > apiResponseBeforeDelete =
                sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "common_api_request.txt");
        Assert.assertTrue(apiResponseBeforeDelete.getBody().toString().contains(
                "<QueueUrl>" + apiEndPoint + "/" + connectorProperties.getProperty("queueId") + "/"
                        + connectorProperties.getProperty("apiQueueName") + "</QueueUrl>"));

        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_purgeQueue_mandatory.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 200);

        RestResponse< OMElement > esbReDeleteResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_purgeQueue_mandatory.xml");
        Assert.assertEquals(esbReDeleteResponse.getHttpStatusCode(), 403);
    }

    /**
     * Negative test case for purgeQueue method.
     *
     * @throws InterruptedException
     */
    @Test(priority = 2, dependsOnMethods = {"testPurgeQueueWithMandatoryParameters"}, description = "AmazonSQS {purgeQueue} integration test case for Negativce case.")
    public void testPurgeQueueWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, XPathExpressionException,
            SAXException, ParserConfigurationException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:purgeQueue");
        RestResponse< OMElement > esbResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_purgeQueue_negative.xml");
        Assert.assertEquals(esbResponse.getHttpStatusCode(), 403);
    }

    @AfterClass(alwaysRun = true)
    public void clearQueues() throws Exception {

        // Clears queues created during test case execution
        generateApiRequest("api_deleteOptionalQueue.json");
        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("optionalQueueName"), charSet) + "/";
        sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        generateApiRequest("api_deleteApiQueue.json");
        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("apiQueueName"), charSet) + "/";
        sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
        generateApiRequest("api_deleteApiOptionalQueue.json");
        apiUrl = apiEndPoint + "/" + URLEncoder.encode(connectorProperties.getProperty("queueId"), charSet) + "/"
                + URLEncoder.encode(connectorProperties.getProperty("apiOptionalQueueName"), charSet) + "/";
        sendXmlRestRequest(apiUrl, "POST", apiRequestHeadersMap, "common_api_request.txt");
    }

    public void generateApiRequest(String signatureRequestFile) throws IOException, JSONException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException {

        String requestData;
        Map< String, String > responseMap;
        AmazonSQSAuthConnector authConnector = new AmazonSQSAuthConnector();

        String signatureRequestFilePath =
                ProductConstant.SYSTEM_TEST_SETTINGS_LOCATION + File.separator + "artifacts" + File.separator + "ESB"
                        + File.separator + "config" + File.separator + "restRequests" + File.separator + "amazonsqs"
                        + File.separator + signatureRequestFile;

        requestData = loadRequestFromFile(signatureRequestFilePath);
        JSONObject signatureRequestObject = new JSONObject(requestData);
        responseMap = authConnector.getRequestPayload(signatureRequestObject);
        apiRequestHeadersMap.put("Authorization", responseMap.get(AmazonSQSConstants.AUTHORIZATION_HEADER));
        apiRequestHeadersMap.put("x-amz-date", responseMap.get(AmazonSQSConstants.AMZ_DATE));
        connectorProperties.put("xFormUrl", responseMap.get(AmazonSQSConstants.REQUEST_PAYLOAD));
    }

    private String loadRequestFromFile(String requestFileName) throws IOException {
        String requestFilePath;
        String requestData;
        requestFilePath = requestFileName;
        requestData = getFileContent(requestFilePath);
        Properties prop = (Properties) connectorProperties.clone();

        Matcher matcher = Pattern.compile("%s\\(([A-Za-z0-9]*)\\)", Pattern.DOTALL).matcher(requestData);
        while (matcher.find()) {
            String key = matcher.group(1);
            requestData = requestData.replaceAll("%s\\(" + key + "\\)", prop.getProperty(key));
        }
        return requestData;
    }

    private String getFileContent(String path) throws IOException {
        String fileContent;
        BufferedInputStream bfist = new BufferedInputStream(new FileInputStream(path));

        byte[] buf = new byte[bfist.available()];
        bfist.read(buf);
        fileContent = new String(buf);

        if (bfist != null) {
            bfist.close();
        }
        return fileContent;
    }
}
