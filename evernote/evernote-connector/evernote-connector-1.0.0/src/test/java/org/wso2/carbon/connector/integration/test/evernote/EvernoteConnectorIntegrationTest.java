/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.evernote;


import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.EvernoteServiceClient;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;

import javax.xml.namespace.QName;

public class EvernoteConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private static final String CONNECTOR_NAME = "evernote-connector-1.0.0";
    private String noteGuid = null;
    private String noteGuid2 = null;
    private String notebookGuid = null;
    private String tagGuid = null;
    private String searchGuid = null;
    String shareNotebookId = null;

    private static final Log log = LogFactory.getLog(EvernoteConnectorIntegrationTest.class);

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init(CONNECTOR_NAME);

    }

    /************************************************Mandatory Test Cases*************************************************************************/

    /**
     * mandatory parameter test case for createNote method.
     */


    @Test(priority = 1, groups = {"wso2.esb"}, description = "Evernote {createNote} Mandatory Parameters integration test.")
    public void testCreateNoteMandatoryParams() throws Exception {

        final String methodName = "createNote";
        final String noteTitle = connectorProperties.getProperty("noteTitle");

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:title>" + noteTitle + "</evr:title>\n"
                        + "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            noteGuid = ((OMElement) response.getChildrenWithLocalName("note").next()).getAttributeValue(new QName("guid"));
            Assert.assertEquals(childElement.getText(), "Note created successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }


    }


    /**
     * mandatory parameter test case for createTag method.
     */
    @Test(priority = 0, groups = {"wso2.esb"}, description = "Evernote {createTag} Mandatory Parameters integration test.")
    public void testCreateTagMandatoryParams() throws Exception {

        final String methodName = "createTag";
        final String tagName = connectorProperties.getProperty("tagName1");

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagName>" + tagName + "</evr:tagName>\n"
                        + "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            tagGuid = ((OMElement) response.getChildrenWithLocalName("tag").next()).getAttributeValue(new QName("guid"));
            Assert.assertEquals(childElement.getText(), "Create tag successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }


    }

    /**
     * mandatory parameter test case for expungeTag method.
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "Evernote {expungeTag} Mandatory Parameters integration test.")
    public void testExpungeTagMandatoryParams() throws Exception {

        final String methodName = "expungeTag";


        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagGuid>" + tagGuid + "</evr:tagGuid>\n"
                        + "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Tag completely removed");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }


    }

    /**
     * mandatory parameter test case for getTag method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {getTag} Mandatory Parameters integration test.")
    public void testGetTagMandatoryParams() throws Exception {

        final String methodName = "getTag";

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagGuid>" + tagGuid + "</evr:tagGuid>\n"
                        + "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();
        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Tag retrieved successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }


    }

    /**
     * mandatory parameter test case for listTags method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {listTags} Mandatory Parameters integration test.")
    public void testListTagsMandatoryParams() throws Exception {

        final String methodName = "listTags";

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();
        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "List all tags successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for untagAll method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {untagAll} Mandatory Parameters integration test.")
    public void testUntagAllMandatoryParams() throws Exception {

        final String methodName = "untagAll";

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagGuid>" + tagGuid + "</evr:tagGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();
        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Untag all successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for updateTag method.
     */

    @Test(priority = 1, groups = {"wso2.esb"}, description = "Evernote {untagAll} Mandatory Parameters integration test.")
    public void testUpdateTagMandatoryParams() throws Exception {

        final String methodName = "updateTag";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagGuid>" + tagGuid + "</evr:tagGuid>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();
        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Tag updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /**
     * mandatory parameter test case for createSearch method.
     */

    @Test(priority = 1, groups = {"wso2.esb"}, description = "Evernote {untagAll} Mandatory Parameters integration test.")
    public void testCreateSearchMandatoryParams() throws Exception {

        final String methodName = "createSearch";
        String searchName = connectorProperties.getProperty("searchName");
        String query = connectorProperties.getProperty("query");
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:searchName>" + searchName + "</evr:searchName>\n" +
                        "<evr:query>" + query + "</evr:query>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            searchGuid = ((OMElement) response.getChildrenWithLocalName("search-guid").next()).getText();
            Assert.assertEquals(childElement.getText(), "Create search successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for getSearch method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {getSearch} Mandatory Parameters integration test.")
    public void testGetSearchMandatoryParams() throws Exception {

        final String methodName = "getSearch";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:searchGuid>" + searchGuid + "</evr:searchGuid>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Search retrieved successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for expungeSearch method.
     */

    @Test(priority = 4, groups = {"wso2.esb"}, description = "Evernote {expungeSearch} Mandatory Parameters integration test.")
    public void testExpungeSearchMandatoryParams() throws Exception {

        final String methodName = "expungeSearch";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:searchGuid>" + searchGuid + "</evr:searchGuid>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Delete search successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for updateSearch method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {updateSearch} Mandatory Parameters integration test.")
    public void testUpdateSearchMandatoryParams() throws Exception {

        final String methodName = "updateSearch";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:searchGuid>" + searchGuid + "</evr:searchGuid>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Search updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for deleteNote method.
     */

    @Test(priority = 4, groups = {"wso2.esb"}, description = "Evernote {deleteNote} Mandatory Parameters integration test.")
    public void testDeleteNoteMandatoryParams() throws Exception {

        final String methodName = "deleteNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>" + noteGuid + "</evr:noteGuid>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Note deleted successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for expungeNote method.
     */

    @Test(priority = 5, groups = {"wso2.esb"}, description = "Evernote {expungeNote} Mandatory Parameters integration test.")
    public void testExpungeNoteMandatoryParams() throws Exception {

        final String methodName = "expungeNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>" + noteGuid + "</evr:noteGuid>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Note completely removed");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for shareNote method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {shareNote} Mandatory Parameters integration test.")
    public void testShareNoteMandatoryParams() throws Exception {

        final String methodName = "shareNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>" + noteGuid + "</evr:noteGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Note shared successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for stopSharingNote method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {stopSharingNote} Mandatory Parameters integration test.")
    public void testStopSharingNoteMandatoryParams() throws Exception {

        final String methodName = "stopSharingNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>" + noteGuid + "</evr:noteGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Note sharing stopped successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for updateNote method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {updateNote} Mandatory Parameters integration test.")
    public void testUpdateNoteMandatoryParams() throws Exception {

        final String methodName = "updateNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>" + noteGuid + "</evr:noteGuid>\n" +
                        "<evr:title>" + "Test Update" + "</evr:title>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Note updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /**
     * mandatory parameter test case for findNotesMetaData method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {findNotesMetaData} Mandatory Parameters integration test.")
    public void testFindNotesMetaDataMandatoryParams() throws Exception {

        final String methodName = "findNotesMetaData";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:ascending>" + true + "</evr:ascending>\n" +
                        "<evr:inactive>false</evr:inactive>\n" +
                        "<evr:offset>0</evr:offset>\n" +
                        "<evr:maxNotes>100</evr:maxNotes>\n" +
                        "<evr:includeTitle>true</evr:includeTitle>\n" +
                        "<evr:includeContentLength>false</evr:includeContentLength>\n" +
                        "<evr:includeCreated>true</evr:includeCreated>\n" +
                        "<evr:includeUpdated>true</evr:includeUpdated>\n" +
                        "<evr:includeDeleted>false</evr:includeDeleted>\n" +
                        "<evr:includeUpdateSequenceNum>true</evr:includeUpdateSequenceNum>\n" +
                        "<evr:includeNotebookGuid>false</evr:includeNotebookGuid>\n" +
                        "<evr:includeTagGuids>true</evr:includeTagGuids>\n" +
                        "<evr:includeAttributes>true</evr:includeAttributes>\n" +
                        "<evr:includeLargestResourceMime>true</evr:includeLargestResourceMime>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Find notes metadata successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for createNotebook method.
     */

    @Test(priority = 1, groups = {"wso2.esb"}, description = "Evernote {createNotebook} Mandatory Parameters integration test.")
    public void testCreateNotebookMandatoryParams() throws Exception {

        final String methodName = "createNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookName>" + connectorProperties.getProperty("notebookName") + "</evr:notebookName>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            notebookGuid = ((OMElement) response.getChildrenWithLocalName("notebook").next()).getAttributeValue(new QName("guid"));
            Assert.assertEquals(childElement.getText(), "Notebook created successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for expungeNotebook method.
     */

    @Test(priority = 4, groups = {"wso2.esb"}, description = "Evernote {expungeNotebook} Mandatory Parameters integration test.")
    public void testExpungeNotebookMandatoryParams() throws Exception {

        final String methodName = "expungeNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookGuid>" + notebookGuid + "</evr:notebookGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Notebook deleted successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for getNotebook method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {getNotebook} Mandatory Parameters integration test.")
    public void testGetNotebookMandatoryParams() throws Exception {

        final String methodName = "getNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookGuid>" + notebookGuid + "</evr:notebookGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Notebook retrieved successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for listNotebooks method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {listNotebooks} Mandatory Parameters integration test.")
    public void testListNotebooksMandatoryParams() throws Exception {

        final String methodName = "listNotebooks";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Notebooks retrieved successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for updateNotebook method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {updateNotebook} Mandatory Parameters integration test.")
    public void testUpdateNotebookMandatoryParams() throws Exception {

        final String methodName = "updateNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookGuid>" + notebookGuid + "</evr:notebookGuid>\n" +
                        "<evr:notebookName>" + connectorProperties.getProperty("notebookName") + "Updated" + "</evr:notebookName>\n" +
                        "<evr:defaultNotebook>" + connectorProperties.getProperty("notebookName") + "</evr:defaultNotebook>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Notebook updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /**
     * mandatory parameter test case for createSharedNotebook method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {createSharedNotebook} Mandatory Parameters integration test.")
    public void testCreateSharedNotebookMandatoryParams() throws Exception {

        final String methodName = "createSharedNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookGuid>" + notebookGuid + "</evr:notebookGuid>\n" +
                        "<evr:email>" + connectorProperties.getProperty("email") + "</evr:email>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            shareNotebookId = ((OMElement) response.getChildrenWithLocalName("sharednotebook").next()).getAttributeValue(new QName("id"));
            Assert.assertEquals(childElement.getText(), "Shared Notebook created successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /**
     * mandatory parameter test case for updateSharedNotebook method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {updateSharedNotebook} Mandatory Parameters integration test.")
    public void testUpdateSharedNotebookMandatoryParams() throws Exception {

        final String methodName = "updateSharedNotebook";

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:id>" + shareNotebookId + "</evr:id>\n" +
                        "<evr:email>" + connectorProperties.getProperty("email") + "</evr:email>\n" +
                        "<evr:allowPreview>true</evr:allowPreview>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Shared Notebook updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * mandatory parameter test case for getNote method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {getNote} Mandatory Parameters integration test.")
    public void testGetNoteMandatoryParams() throws Exception {

        final String methodName = "getNote";

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>" + noteGuid + "</evr:noteGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Note retrieved successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /************************************************Optional Test Cases*************************************************************************/

    /**
     * optional parameter test case for createNote method.
     */


    @Test(priority = 1, groups = {"wso2.esb"}, description = "Evernote {createNote} Optional Parameters integration test.")
    public void testCreateNoteOptionalParams() throws Exception {

        final String methodName = "createNote";
        final String noteTitle = connectorProperties.getProperty("noteTitle");

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:title>" + noteTitle + "</evr:title>\n" +
                        "<evr:content>" + "Test Note" + "</evr:content>\n" +
                        "<evr:fileName>" + "Attachment1" + "</evr:fileName>\n" +
                        "<evr:sourceURL>" + connectorProperties.getProperty("sourceURL") + "</evr:sourceURL>\n" +
                        "<evr:mime>" + connectorProperties.getProperty("mime") + "</evr:mime>\n" +
                        "<evr:tagName>" + connectorProperties.getProperty("tagName1") + "</evr:tagName>\n"
                        + "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            noteGuid2 = ((OMElement) response.getChildrenWithLocalName("note").next()).getAttributeValue(new QName("guid"));
            Assert.assertEquals(childElement.getText(), "Note created successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }

    }


    /**
     * optional parameter test case for createTag method.
     */


    @Test(priority = 1, groups = {"wso2.esb"}, description = "Evernote {createTag} Optional Parameters integration test.")
    public void testCreateTagOptionalParams() throws Exception {

        final String methodName = "createTag";
        final String tagName = connectorProperties.getProperty("tagName2");

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagName>" + tagName + "</evr:tagName>\n" +
                        "<evr:parentGuid>" + tagGuid + "</evr:parentGuid>"
                        + "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Create tag successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }


    }

    /**
     * optional parameter test case for updateTag method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {untagAll} Mandatory Parameters integration test.")
    public void testupdateTagOptionalParams() throws Exception {

        final String methodName = "updateTag";
        final String tagName = connectorProperties.getProperty("updateTagName");

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagGuid>" + tagGuid + "</evr:tagGuid>\n" +
                        "<evr:tagName>" + tagName + "</evr:tagName>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();
        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Tag updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * optional parameter test case for createSearch method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {creteSearch} Optional Parameters integration test.")
    public void testCreateSearchOptionalParams() throws Exception {

        final String methodName = "createSearch";
        String searchName = connectorProperties.getProperty("searchName");
        String query = connectorProperties.getProperty("query");
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:searchName>" + searchName + "optional" + "</evr:searchName>\n" +
                        "<evr:query>" + query + "</evr:query>\n" +
                        "<evr:includeAccount>" + true + "</evr:includeAccount>\n" +
                        "<evr:includePersonalLinkedNotebooks>" + true + "</evr:includePersonalLinkedNotebooks>\n" +
                        "<evr:includeBusinessLinkedNotebooks>" + true + "</evr:includeBusinessLinkedNotebooks>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Create search successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * optional parameter test case for updateNote method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {updateNote} Optional Parameters integration test.")
    public void testUpdateNoteOptionalParams() throws Exception {

        final String methodName = "updateNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:title>" + "Test Update" + "</evr:title>\n" +
                        "<evr:noteGuid>" + noteGuid2 + "</evr:noteGuid>\n" +
                        "<evr:sourceURL>" + connectorProperties.getProperty("sourceURL") + "</evr:sourceURL>\n" +
                        "<evr:mime>" + connectorProperties.getProperty("mime") + "</evr:mime>\n" +
                        "<evr:tagName>" + "wso2" + "</evr:tagName>\n" +
                        "<evr:fileName>" + "xsl-pdf-1" + "</evr:fileName>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Note updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /**
     * optional parameter test case for findNotesMetaData method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {findNotesMetaData} Optional Parameters integration test.")
    public void testFindNotesMetaDataOptionalParams() throws Exception {

        final String methodName = "findNotesMetaData";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:ascending>" + true + "</evr:ascending>\n" +
                        "<evr:words>tag:wso2</evr:words>\n" +
                        "<evr:inactive>false</evr:inactive>\n" +
                        "<evr:emphasized>tag:wso2</evr:emphasized>\n" +
                        "<evr:offset>0</evr:offset>\n" +
                        "<evr:maxNotes>100</evr:maxNotes>\n" +
                        "<evr:includeTitle>true</evr:includeTitle>\n" +
                        "<evr:includeContentLength>false</evr:includeContentLength>\n" +
                        "<evr:includeCreated>true</evr:includeCreated>\n" +
                        "<evr:includeUpdated>true</evr:includeUpdated>\n" +
                        "<evr:includeDeleted>false</evr:includeDeleted>\n" +
                        "<evr:includeUpdateSequenceNum>true</evr:includeUpdateSequenceNum>\n" +
                        "<evr:includeNotebookGuid>false</evr:includeNotebookGuid>\n" +
                        "<evr:includeTagGuids>true</evr:includeTagGuids>\n" +
                        "<evr:includeAttributes>true</evr:includeAttributes>\n" +
                        "<evr:includeLargestResourceMime>true</evr:includeLargestResourceMime>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Find notes metadata successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * optional parameter test case for updateSearch method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {updateSearch} Optional Parameters integration test.")
    public void testUpdateSearchOptionalParams() throws Exception {

        final String methodName = "updateSearch";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:searchGuid>" + searchGuid + "</evr:searchGuid>\n" +
                        "<evr:searchName>" + connectorProperties.getProperty("mysrch28") + "updated" + "</evr:searchName>\n" +
                        "<evr:query>tag:wso2</evr:query>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Search updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * optional parameter test case for createNotebook method.
     */

    @Test(priority = 1, groups = {"wso2.esb"}, description = "Evernote {createNotebook} Optional Parameters integration test.")
    public void testCreateNotebookOptionalParams() throws Exception {

        final String methodName = "createNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookName>" + connectorProperties.getProperty("notebookName") + "optional</evr:notebookName>\n" +
                        "<evr:defaultNotebook>false</evr:defaultNotebook>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Notebook created successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * optional parameter test case for updateNotebook method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {updateNotebook} Optional Parameters integration test.")
    public void testUpdateNotebookOptionalParams() throws Exception {

        final String methodName = "updateNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookGuid>" + notebookGuid + "</evr:notebookGuid>\n" +
                        "<evr:notebookName>" + connectorProperties.getProperty("notebookName") + "UpdatedOptional" + "</evr:notebookName>\n" +
                        "<evr:defaultNotebook>" + "false" + "</evr:defaultNotebook>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            Assert.assertEquals(childElement.getText(), "Notebook updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /************************************************Negative Test Cases*************************************************************************/


    /**
     * Negative parameter test case for createNote method.
     */


    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {createNote} Negative Parameters integration test.")
    public void testCreateNoteNegativeParams() throws Exception {

        final String methodName = "createNote";
        final String noteTitle = connectorProperties.getProperty("noteTitle");

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:title>" + noteTitle + "</evr:title>\n" +
                        "<evr:content>" + "Test Note" + "</evr:content>\n" +
                        "<evr:tagName>" + "wso2" + "</evr:tagName>\n" +
                        "<evr:notebookGuid>" + "872863723872" + "</evr:notebookGuid>"
                        + "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Note created successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }

    }


    /**
     * Negative parameter test case for createTag method.
     */


    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {createTag} Negative Parameters integration test.")
    public void testCreateTagNegativeParams() throws Exception {

        final String methodName = "createTag";
        final String tagName = connectorProperties.getProperty("tagName2");

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +

                        "<evr:tagName>" + tagName + "</evr:tagName>\n" +
                        "<evr:parentGuid>" + tagGuid + "</evr:parentGuid>" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Create tag successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }


    }


    /**
     * Negative parameter test case for ExpungeTag method.
     */


    @Test(priority = 4, groups = {"wso2.esb"}, description = "Evernote {expungeTag} Negative Parameters integration test.")
    public void testExpungeTagNegativeParams() throws Exception {

        final String methodName = "expungeTag";


        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagGuid>" + "98986769878900" + "</evr:tagGuid>\n"
                        + "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Tag completely removed");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {

            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }


    }


    /**
     * Negative parameter test case for getTag method.
     */


    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {getTag} Negative Parameters integration test.")
    public void testGetTagNegativeParams() throws Exception {

        final String methodName = "getTag";

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagGuid>" + "8685cxs6868" + "</evr:tagGuid>\n"
                        + "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Tag retrieved successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * Negative parameter test case for getTag method.
     */


    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {getTag} Negative Parameters integration test.")
    public void testunTagAllNegativeParams() throws Exception {

        final String methodName = "untagAll";

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagGuid>" + "8685cxs6868" + "</evr:tagGuid>\n"
                        + "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Untag All successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {

            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for updateTag method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {untagAll} Mandatory Parameters integration test.")
    public void testupdateTagNegativeParams() throws Exception {

        final String methodName = "updateTag";
        final String tagName = connectorProperties.getProperty("updateTagName");

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:tagGuid>" + tagGuid + "</evr:tagGuid>\n" +
                        "<evr:tagName>" + tagName + "</evr:tagName>\n" +
                        "<evr:parentGuid>" + "9868756786j" + "</evr:parentGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Tag updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {

            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for createSearch method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {untagAll} Mandatory Parameters integration test.")
    public void testCreateSearchNegativeParams() throws Exception {

        final String methodName = "createSearch";
        String searchName = connectorProperties.getProperty("searchName");
        String query = connectorProperties.getProperty("query");
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:searchName>" + searchName + "</evr:searchName>\n" +
                        "<evr:query>" + query + "</evr:query>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Create search successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {

            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /**
     * Negative parameter test case for getSearch method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {getSearch} Negative Parameters integration test.")
    public void getSearchNegativeParams() throws Exception {

        final String methodName = "getSearch";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:searchGuid>" + "8273822121" + "</evr:searchGuid>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Search retrieved successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for expungeSearch method.
     */

    @Test(priority = 4, groups = {"wso2.esb"}, description = "Evernote {expungeSearch} Negative Parameters integration test.")
    public void testExpungeSearchNegativeParams() throws Exception {

        final String methodName = "expungeSearch";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:searchGuid>" + "28wuud62sggsua" + "</evr:searchGuid>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Delete search successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for deleteNote method.
     */

    @Test(priority = 5, groups = {"wso2.esb"}, description = "Evernote {deleteNote} Negative Parameters integration test.")
    public void testDeleteNoteNegativeParams() throws Exception {

        final String methodName = "deleteNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>" + "977568756788" + "</evr:noteGuid>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Note deleted successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /**
     * negative parameter test case for expungeNote method.
     */

    @Test(priority = 5, groups = {"wso2.esb"}, description = "Evernote {expungeNote} Negative Parameters integration test.")
    public void testExpungeNoteNegativeParams() throws Exception {

        final String methodName = "expungeNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>" + "128172156x" + "</evr:noteGuid>\n" +

                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Note completely removed");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /**
     * negative parameter test case for shareNote method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {shareNote} Negative Parameters integration test.")
    public void testShareNoteNegativeParams() throws Exception {

        final String methodName = "shareNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>" + "sjdsjdtegd98" + "</evr:noteGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Note shared successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for stopSharingNote method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {stopSharingNote} Negative Parameters integration test.")
    public void testStopSharingNoteNegativeParams() throws Exception {

        final String methodName = "stopSharingNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>" + "98bbjvjvgjv" + "</evr:noteGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Note sharing stopped successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for updateNote method.
     */

    @Test(priority = 4, groups = {"wso2.esb"}, description = "Evernote {NegativeNote} Optional Parameters integration test.")
    public void testUpdateNoteNegativeParams() throws Exception {

        final String methodName = "updateNote";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:title>" + "Test Update" + "</evr:title>\n" +
                        "<evr:noteGuid>" + noteGuid2 + "0090" + "</evr:noteGuid>\n" +
                        "<evr:sourceURL>" + connectorProperties.getProperty("sourceURL") + "</evr:sourceURL>\n" +
                        "<evr:mime>" + connectorProperties.getProperty("mime") + "</evr:mime>\n" +
                        "<evr:tagName>" + "wso2" + "</evr:tagName>\n" +
                        "<evr:fileName>" + "xsl-pdf-1" + "</evr:fileName>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Note updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * Negative parameter test case for findNotesMetaData method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {findNotesMetaData} Negative Parameters integration test.")
    public void testFindNotesMetaDataNegativeParams() throws Exception {

        final String methodName = "findNotesMetaData";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:ascending>" + true + "</evr:ascending>\n" +
                        "<evr:words>tag:wso2</evr:words>\n" +
                        "<evr:inactive>false</evr:inactive>\n" +
                        "<evr:emphasized>tag:wso2</evr:emphasized>\n" +
                        "<evr:offset>0</evr:offset>\n" +
                        "<evr:maxNotes>jhjas</evr:maxNotes>\n" +
                        "<evr:includeTitle>true</evr:includeTitle>\n" +
                        "<evr:includeContentLength>false</evr:includeContentLength>\n" +
                        "<evr:includeCreated>true</evr:includeCreated>\n" +
                        "<evr:includeUpdated>true</evr:includeUpdated>\n" +
                        "<evr:includeDeleted>false</evr:includeDeleted>\n" +
                        "<evr:includeUpdateSequenceNum>true</evr:includeUpdateSequenceNum>\n" +
                        "<evr:includeNotebookGuid>false</evr:includeNotebookGuid>\n" +
                        "<evr:includeTagGuids>true</evr:includeTagGuids>\n" +
                        "<evr:includeAttributes>true</evr:includeAttributes>\n" +
                        "<evr:includeLargestResourceMime>true</evr:includeLargestResourceMime>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();
        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Find notes metadata successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for updateSearch method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {updateSearch} Negative Parameters integration test.")
    public void testUpdateSearchNegativeParams() throws Exception {

        final String methodName = "updateSearch";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:searchGuid>" + "gdjhagdsagdas" + "</evr:searchGuid>\n" +
                        "<evr:searchName>" + connectorProperties.getProperty("mysrch28") + "updated" + "</evr:searchName>\n" +
                        "<evr:query>tag:wso2</evr:query>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "           </root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Search updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for createNotebook method.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {createNotebook} Negative Parameters integration test.")
    public void testCreateNotebookNegativeParams() throws Exception {

        final String methodName = "createNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + "989788shahsja"
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookName>" + connectorProperties.getProperty("notebookName") + "</evr:notebookName>\n" +
                        "<evr:defaultNotebook>false</evr:defaultNotebook>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Notebook created successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for expungeNotebook method.
     */

    @Test(priority = 4, groups = {"wso2.esb"}, description = "Evernote {expungeNotebook} Mandatory Parameters integration test.")
    public void testExpungeNotebookNegativeParams() throws Exception {

        final String methodName = "expungeNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookGuid>" + notebookGuid + "njjh</evr:notebookGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Notebook deleted successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for getNotebook method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {getNotebook} Negative Parameters integration test.")
    public void testGetNotebookNegativeParams() throws Exception {

        final String methodName = "getNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookGuid>890989jksd</evr:notebookGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Notebook retrieved successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for listNotebooks method.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {listNotebooks} negative Parameters integration test.")
    public void testListNotebooksNegativeParams() throws Exception {

        final String methodName = "listNotebooks";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + "sdsdsdsdsdsdsd"
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Notebooks retrieved successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for updateNotebook method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {negativeNotebook} Optional Parameters integration test.")
    public void testUpdateNotebookNegativeParams() throws Exception {

        final String methodName = "updateNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookGuid>" + notebookGuid + "</evr:notebookGuid>\n" +
                        "<evr:notebookName>" + connectorProperties.getProperty("notebookName") + "optional" + "</evr:notebookName>\n" +
                        "<evr:defaultNotebook>" + "false" + "</evr:defaultNotebook>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Notebook updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }

    /**
     * negative parameter test case for createSharedNotebook method.
     */

    @Test(priority = 3, groups = {"wso2.esb"}, description = "Evernote {createSharedNotebook} Negative Parameters integration test.")
    public void testCreateSharedNotebookNegativeParams() throws Exception {

        final String methodName = "createSharedNotebook";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:notebookGuid>uhsduydsdyuhy</evr:notebookGuid>\n" +
                        "<evr:email>" + connectorProperties.getProperty("email") + "</evr:email>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Shared Notebook created successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /**
     * negative parameter test case for updateSharedNotebook method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {updateSharedNotebook} Negative Parameters integration test.")
    public void testUpdateSharedNotebookNegativeParams() throws Exception {

        final String methodName = "updateSharedNotebook";

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:id>" + shareNotebookId+"121" + "</evr:id>\n" +
                        "<evr:email>" + connectorProperties.getProperty("email") + "</evr:email>\n" +
                        "<evr:allowPreview>true</evr:allowPreview>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Shared Notebook updated successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }


    /**
     * negative parameter test case for getNote method.
     */

    @Test(priority = 2, groups = {"wso2.esb"}, description = "Evernote {getNote} Negative Parameters integration test.")
    public void testGetNoteNegativeParams() throws Exception {

        final String methodName = "getNote";

        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:evr=\"wso2.connector.evernote\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <evr:devToken>"
                        + connectorProperties.getProperty("devToken")
                        + "</evr:devToken>\n"
                        + "<evr:noteStoreUrl>"
                        + connectorProperties.getProperty("noteStoreUrl")
                        + "</evr:noteStoreUrl>\n" +
                        "<evr:devTokenType>"
                        + connectorProperties.getProperty("devTokenType")
                        + "</evr:devTokenType>\n" +
                        "<evr:noteGuid>987679889</evr:noteGuid>\n" +
                        "<evr:method>"
                        + methodName
                        + "</evr:method>\n"
                        + "</root>\n" + "</soapenv:Body>\n" + " </soapenv:Envelope>";

        EvernoteServiceClient evernoteServiceClient = new EvernoteServiceClient();
        OMElement paramRequest = AXIOMUtil.stringToOM(omString);
        OMElement response = evernoteServiceClient.sendReceive(paramRequest, getProxyServiceURL("evernote"), "mediate");
        OMElement childElement = response.getFirstElement();

        if (childElement.getLocalName().equalsIgnoreCase("message")) {
            log.error("ERROR");
            Assert.assertNotEquals(childElement.getText(), "Note retrieved successfully");
        } else if (childElement.getLocalName().equalsIgnoreCase("errorMessage")) {
            Assert.assertNotEquals(childElement.getText(), null);
        } else {
            log.error("ERROR");
            Assert.assertEquals(childElement.getText(), "1");
        }
    }



}