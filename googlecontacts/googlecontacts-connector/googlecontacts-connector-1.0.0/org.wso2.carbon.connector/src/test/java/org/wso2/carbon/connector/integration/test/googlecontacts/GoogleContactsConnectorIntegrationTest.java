/**
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.connector.integration.test.googlecontacts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.Query;
import com.google.gdata.client.batch.BatchInterruptedException;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.util.ResourceNotFoundException;
import com.google.gdata.util.ServiceException;

/**
 * The Class GoogleContactsConnectorIntegrationTest.
 */
public class GoogleContactsConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    /** The direct invoker. */
    private GoogleContactsDirectInvoker directInvoker;
    
    /** The contact id. */
    private String contactId;
    
    /** Contact Id for batch delete operation. */
    private String batchDeleteContactId;
    
    /** The group id. */
    private String groupId;
    
    /** Group ID for batch delete operation. */
    private String batchDeleteGroupId;
    
    /** The email Address passed in as property value. */
    private String emailAddress;

    Map<String, String> nameSpaceMap = new HashMap<String, String>();
    /**
     * Sets the environment.
     * 
     * @throws Exception the exception
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("googlecontacts-connector-1.0.0");
        directInvoker =
                new GoogleContactsDirectInvoker(connectorProperties.getProperty("accessToken"),
                        connectorProperties.getProperty("appName"));
        emailAddress = connectorProperties.getProperty("userEmail");
        nameSpaceMap.put("atom", "http://www.w3.org/2005/Atom");
        nameSpaceMap.put("gd", "http://schemas.google.com/g/2005");
        nameSpaceMap.put("openSearch", "http://a9.com/-/spec/opensearchrss/1.0/");
        nameSpaceMap.put("gContact", "http://schemas.google.com/contact/2008");
        nameSpaceMap.put("batch", "http://schemas.google.com/gdata/batch");
    }
    
    /**
     * Positive test case for RetrieveContactGroupsByQuery method with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "Google Contacts {retrieveContactGroupsByQuery} integration test with mandatory parameters.")
    public void testRetrieveContactGroupsByQueryWithMandatoryParameters() throws Exception {

        Query contactQuery = new Query(new URL("https://www.google.com/m8/feeds/groups/default/full"));

        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_retrieveContactGroupsByQuery_mandatory.xml");

        ContactGroupFeed feed =
                directInvoker.getDirectInvokerContactsService().query(contactQuery, ContactGroupFeed.class);

        // Assert Total Results.
        Assert.assertEquals(Integer.parseInt((String) xPathEvaluate(esbSoapResponse.getBody()
                .getFirstElement(), "string(//openSearch:totalResults/text())", nameSpaceMap)), feed.getTotalResults());
    }

    /**
     * Positive test case for RetrieveContactGroupsByQuery method with optional parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "Google Contacts {retrieveContactGroupsByQuery} integration test with optional parameters.")
    public void testRetrieveContactGroupsByQueryWithOptionalParameters() throws Exception {

        final Map<String, String> parametersMap = new HashMap<String, String>();

        parametersMap.put("reqUrl", "https://www.google.com/m8/feeds/groups/default/full");

        Query contactQuery = (Query) loadObjectFromFile("api_retrieveContactGroupsByQuery_optional.xml", parametersMap);

        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_retrieveContactGroupsByQuery_optional.xml", parametersMap);

        ContactGroupFeed feed =
                directInvoker.getDirectInvokerContactsService().query(contactQuery, ContactGroupFeed.class);

        // Assert Total Results.
        Assert.assertEquals(Integer.parseInt((String) xPathEvaluate(esbSoapResponse.getBody()
                .getFirstElement(), "string(//openSearch:totalResults/text())", nameSpaceMap)), feed.getTotalResults());

        // Assert Items Per Page.
        Assert.assertEquals(Integer.parseInt((String) xPathEvaluate(esbSoapResponse.getBody()
                .getFirstElement(), "string(//openSearch:itemsPerPage/text())", nameSpaceMap)), feed.getItemsPerPage());

        // Assert Start Index.
        Assert.assertEquals(
                Integer.parseInt((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
                        "string(//openSearch:startIndex/text())", nameSpaceMap)), feed.getStartIndex());
    }

    /**
     * Negative test case for RetrieveContactGroupsByQuery method with negative parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, expectedExceptions = AxisFault.class, groups = { "wso2.esb" }, description = "Google Contacts {retrieveContactGroupsByQuery} integration test with negative parameters.")
    public void testRetrieveContactGroupsByQueryWithNegativeCase() throws Exception {

        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_retrieveContactGroupsByQuery_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
                "string(//faultstring/text())", nameSpaceMap), "");

    }

    /**
     * Positive test case for createContact method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Google Contacts {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_createContact_mandatory.xml");

        Assert.assertTrue(esbSoapResponse.getBody().toString().contains("atom:id"));
        String xPathExp = "string(//atom:id/text())";
        String idUrl = (String) xPathEvaluate(esbSoapResponse, xPathExp, nameSpaceMap);
        batchDeleteContactId = idUrl.substring(idUrl.lastIndexOf("/") + 1, idUrl.length());
        ContactEntry directResponseObject = directInvoker.retrieveSingleContact(batchDeleteContactId);
        Assert.assertEquals(idUrl, directResponseObject.getId());
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse,
                "string(//gd:fullName/text())", nameSpaceMap), directResponseObject
                .getName().getFullName().getValue());

    }

    /**
     * Positive test case for createContact method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Google Contacts {createContact} integration test with optional parameters.")
    public void testCreateContactWithOptionalParameters() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_createContact_optional.xml");

        Assert.assertTrue(esbSoapResponse.getBody().toString().contains("atom:id"));
        String xPathExp = "string(//atom:id/text())";
        String idUrl = (String) xPathEvaluate(esbSoapResponse, xPathExp, nameSpaceMap);
        contactId = idUrl.substring(idUrl.lastIndexOf("/") + 1, idUrl.length());

        ContactEntry directResponseObject = directInvoker.retrieveSingleContact(contactId);
        Assert.assertEquals(idUrl, directResponseObject.getId());
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse, "string(//gd:givenName/text())", nameSpaceMap), directResponseObject
                .getName().getGivenName().getValue());
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse, "string(//gContact:birthday/@when)", nameSpaceMap), directResponseObject
                .getBirthday().getValue());
    }

    /**
     * Positive test case for createContact method negative scenario.
     */
    @Test(expectedExceptions = AxisFault.class, priority = 1, groups = { "wso2.esb" }, description = "Google Contacts {createContact} integration test negative scenario.")
    public void testCreateContactNegativeScenario() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_createContact_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
            "string(//faultstring/text())", nameSpaceMap), "");
 }

    /**
     * Positive test case for createContact method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "Google Contacts {updateContact} integration test with mandatory parameters.")
    public void testUpdateContactWithMandatoryParameters() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("contactId", contactId);
        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_updateContact_mandatory.xml", parametersMap);

        String xPathExp = "string(//atom:id/text())";
        String idUrl = (String) xPathEvaluate(esbSoapResponse, xPathExp, nameSpaceMap);
        String updatedContactId = idUrl.substring(idUrl.lastIndexOf("/") + 1, idUrl.length());

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));
        ContactEntry directResponseObject = directInvoker.retrieveSingleContact(updatedContactId);
        Assert.assertEquals(idUrl, directResponseObject.getId());
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse, "string(//gd:fullName/text())", nameSpaceMap), directResponseObject
                .getName().getFullName().getValue());
    }

    /**
     * Positive test case for createContact method with optional parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "Google Contacts {createContact} integration test with optional parameters.")
    public void testUpdateContactWithOptionalParameters() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("contactId", contactId);
        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_updateContact_optional.xml", parametersMap);

        String xPathExp = "string(//atom:id/text())";
        String idUrl = (String) xPathEvaluate(esbSoapResponse, xPathExp, nameSpaceMap);
        String updatedContactId = idUrl.substring(idUrl.lastIndexOf("/") + 1, idUrl.length());

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));
        ContactEntry directResponseObject = directInvoker.retrieveSingleContact(updatedContactId);
        Assert.assertEquals(idUrl, directResponseObject.getId());
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse, "string(//gd:phoneNumber/text())", nameSpaceMap), directResponseObject
                .getPhoneNumbers().get(0).getPhoneNumber());
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse, "string(//gd:email/@address)", nameSpaceMap), directResponseObject
                .getEmailAddresses().get(0).getAddress());
    }

    /**
     * Negative test case for updateContact method.
     */
    @Test(expectedExceptions = AxisFault.class, priority = 2, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Google Contacts {updateContact} integration test negative scenario.")
    public void testUpdateContactNegativeScenario() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_updateContact_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
                "string(//faultstring/text())", nameSpaceMap), "");

    }

    /**
     * Positive test case for updateContactPhoto method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "Google Contacts {updateContactPhoto} integration test with mandatory parameters.")
    public void testUpdateContactPhotoWithMandatoryParameters() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        Map<String, String> attachmentMap = new HashMap<String, String>();
        attachmentMap.put("photo", "updateContactPhoto_image.png");
        parametersMap.put("contactId", contactId);

        sendSOAPRequest(proxyUrl, "esb_updateContactPhoto_mandatory.xml", parametersMap, attachmentMap);

        ContactEntry directResponseObject = directInvoker.retrieveSingleContact(contactId);
        Assert.assertTrue(directResponseObject.getContactPhotoLink() != null);
    }

    /**
     * Negative test case for updateContactPhoto method.
     */
    @Test(expectedExceptions = AxisFault.class, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, priority = 2, groups = { "wso2.esb" }, description = "Google Contacts {updateContactPhoto} integration test negative scenario.")
    public void testUpdateContactPhotoNegativeScenario() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_updateContactPhoto_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
            "string(//faultstring/text())", nameSpaceMap), "");
    }

    /**
     * Positive test case for retrieveAllContacts method with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Google Contacts {retrieveAllContacts} integration test with mandatory parameters.")
    public void testRetrieveAllContactsWithMandatoryParameters() throws Exception {

        final SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_retrieveAllContacts_mandatory.xml");

        final ContactFeed directResponse = directInvoker.retrieveAllContacts();
        // Assert id
        Assert.assertEquals(directResponse.getEntries().get(0).getName().getFullName().getValue(),
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//gd:fullName/text())", nameSpaceMap));

        // Assert title
        Assert.assertEquals(directResponse.getTitle().getPlainText(),
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:title/text())", nameSpaceMap));

        // Assert totalResults
        Assert.assertEquals(String.valueOf(directResponse.getTotalResults()),
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//openSearch:totalResults/text())", nameSpaceMap));

    }

    /**
     * Negative test case for retrieveAllContacts method with negative parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 1, expectedExceptions = AxisFault.class, groups = { "wso2.esb" }, description = "Google Contacts {retrieveAllContacts} integration test with negative parameters.")
    public void testRetrieveAllContactsWithNegativeCase() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_retrieveAllContacts_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
            "string(//faultstring/text())", nameSpaceMap), "");
    }

    /**
     * Positive test case for changeGroupMembership method with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "Google Contacts {changeGroupMembers} integration test with mandatory parameters.")
    public void testChangeGroupMembershipWithMandatoryParameters() throws Exception {

        final Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("contactId", contactId);
        final SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_changeGroupMembership_mandatory.xml", parametersMap);

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        ContactEntry directResponse = directInvoker.retrieveSingleContact(contactId);

        final int apiResponseSize = directResponse.getGroupMembershipInfos().size();
        final String directApiResponseHref =
                directResponse.getGroupMembershipInfos().get(apiResponseSize - 1).getHref();

        final int esbResponseSize =
                Integer.parseInt((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(count(//atom:groupMembershipInfo))", nameSpaceMap));
        final String directEsbResponseHref =
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:groupMembershipInfo[" + esbResponseSize + "]/@href)", nameSpaceMap);

        // Assert title
        Assert.assertEquals(directResponse.getTitle().getPlainText(),
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:title/text())", nameSpaceMap));

        // Assert Checking whether both responses href are identical
        Assert.assertEquals(directApiResponseHref, directEsbResponseHref);

    }

    /**
     * Negative test case for changeGroupMembership method with negative parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, expectedExceptions = AxisFault.class, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "Google Contacts {changeGroupMembers} integration test with negative parameters.")
    public void testChangeGroupMembershipWithNegativeCase() throws Exception {

        final Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("contactId", contactId);
        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_changeGroupMembership_negative.xml", parametersMap);
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
            "string(//faultstring/text())", nameSpaceMap), "");
    }

    /**
     * Positive test case for retrieveSingleContact method with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" },
            description = "Google Contacts {retrieveSingleContact} integration test with mandatory parameters.")
    public void testRetrieveSingleContactWithMandatoryParameters() throws Exception {

        final Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("contactId", contactId);

        final SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_retrieveSingleContact_mandatory.xml", parametersMap);

        final ContactEntry directResponse = directInvoker.retrieveSingleContact(contactId);

        // Assert contact email.
        Assert.assertEquals(    (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//gd:email/@address)", nameSpaceMap),
                directResponse.getEmailAddresses().get(0).getAddress());
        // Assert the contact's title.
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:title/text())", nameSpaceMap),
                directResponse.getTitle().getPlainText());
    }

    /**
     * Negative test case for retrieveSingleContact method.
     *
     * @throws Exception the exception
     */
    @Test(expectedExceptions = AxisFault.class, priority = 2, groups = { "wso2.esb" },
            description = "Google Contacts {retrieveSingleContact} integration test with negative case.")
    public void testRetrieveSingleContactWithNegativeCase() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_retrieveSingleContact_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
            "string(//faultstring/text())", nameSpaceMap), "");
    }

    /**
     * Positive test case for deleteContactGroup method with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(expectedExceptions = ResourceNotFoundException.class, priority = 3, groups = { "wso2.esb" },
            description = "Google Contacts {deleteContactGroup} integration test with mandatory parameters.")
    public void testDeleteContactGroupWithMandatoryParameters() throws Exception {

        final Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("groupId", groupId);

        sendSOAPRequest(proxyUrl, "esb_deleteContactGroup_mandatory.xml", parametersMap);

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        directInvoker.retrieveSingleContactGroup(groupId);
    }

    /**
     * Negative test case for deleteContactGroup method.
     *
     * @throws Exception the exception
     */
    @Test(expectedExceptions = AxisFault.class, priority = 3, groups = { "wso2.esb" },
            description = "Google Contacts {deleteContactGroup} integration test with negative case.")
    public void testDeleteContactGroupWithNegativeCase() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_deleteContactGroup_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
            "string(//faultstring/text())", nameSpaceMap), "");
    }

    /**
     * Positive test case for createContactGroup method with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Google Contacts {createContactGroup} integration test with mandatory parameters.")
    public void testCreateContactGroupWithMandatoryParameters() throws Exception {

        final SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_createContactGroup_mandatory.xml");

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));
        String xPathExp = "string(//atom:id/text())";
        String idUrl = (String) xPathEvaluate(esbSoapResponse, xPathExp, nameSpaceMap);
        groupId = idUrl.substring(idUrl.lastIndexOf("/") + 1, idUrl.length());
        final ContactGroupEntry directResponse =
                directInvoker.retrieveSingleContactGroupByUrl((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
                        "string(//atom:id/text())", nameSpaceMap));

        // Assert the group title.
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:title/text())", nameSpaceMap),
                directResponse.getTitle().getPlainText());
        // Assert the group's content.
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:content/text())", nameSpaceMap),
                directResponse.getPlainTextContent());
    }

    /**
     * Positive test case for createContactGroup method with optional parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Google Contacts {createContactGroup} integration test with optional parameters.")
    public void testCreateContactGroupWithOptionalParameters() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_createContactGroup_optional.xml");

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        String xPathExp = "string(//atom:id/text())";
        String idUrl = (String) xPathEvaluate(esbSoapResponse, xPathExp, nameSpaceMap);

        batchDeleteGroupId = idUrl.substring(idUrl.lastIndexOf("/") + 1, idUrl.length());
        final ContactGroupEntry directResponse =
                directInvoker.retrieveSingleContactGroupByUrl((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:link/@href)", nameSpaceMap));

        // Assert the group title.
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:title/text())", nameSpaceMap),
                directResponse.getTitle().getPlainText());

        // Assert the value of the extended property.
        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//gd:extendedProperty/@value)", nameSpaceMap),
                directResponse.getExtendedProperties().get(0).getValue());
    }

    /**
     * Negative test case for createContactGroup method.
     *
     * @throws Exception the exception
     */
    @Test(enabled =false, expectedExceptions = AxisFault.class, priority = 1, groups = { "wso2.esb" }, description = "Google Contacts {createContactGroup} integration test negative case.")
    public void testCreateContactGroupNegativeCase() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_createContactGroup_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
            "string(//faultstring/text())", nameSpaceMap), "");
    }

    /**
     * Positive test case for retrieveAllContactGroups method with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 1, groups = { "wso2.esb" },
          description = "Google Contacts {retrieveAllContactGroups} method {mandatory parameters} Integration Tests")
    public void testRetrieveAllContactGroupsMandatoryParameters() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_retrieveAllContactGroups_mandatory.xml");
        ContactGroupFeed groupFeed = directInvoker.retrieveAllContactGroups();
        Assert.assertEquals(String.valueOf(groupFeed.getTotalResults()),
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//openSearch:totalResults/text())", nameSpaceMap));

        Assert.assertEquals(groupFeed.getEntries().get(groupFeed.getEntries().size() - 1).getTitle().getPlainText(),
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[last()]/atom:title/text())", nameSpaceMap));
    }

    /**
     * Negative test case for retrieveAllContactGroups method.
     *
     * @throws Exception the exception
     */
    @Test(expectedExceptions = AxisFault.class, priority = 1, groups = { "wso2.esb" },
          description = "Google Contacts {retrieveAllContactGroups} method {negative scenario} Integration Tests")
    public void testRetrieveAllContactGroupsNegativeScenario() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_retrieveAllContactGroups_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
                "string(//faultstring/text())", nameSpaceMap), "");

    }

    /**
     * Positive test case for deleteContactPhoto method with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" },
          description = "Google Contacts {deleteContactPhoto} method {mandatory parameters} Integration Tests")
    public void testDeleteContactPhotoMandatoryParameters() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();

        parametersMap.put("contactId", contactId);
        // Delete a contact photo for given contact id through a esb call.
        sendSOAPRequest(proxyUrl, "esb_deleteContactPhoto_mandatory.xml", parametersMap);

        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        // Get the contact entry which contact photo was deleted by direct api call.
        ContactEntry contactEntry = directInvoker.retrieveSingleContact(contactId);

        // Assert with retrieved contact photo link's etag attribute is not available.
        // If a contact does not have a photo, then the photo link element has no gd:etag.
        Assert.assertNull(contactEntry.getContactPhotoLink().getEtag());

    }

    /**
     * Negative test case for deleteContactPhoto method.
     *
     * @throws Exception the exception
     */
    @Test(expectedExceptions = AxisFault.class, priority = 2, groups = { "wso2.esb" },
          description = "Google Contacts {deleteContactPhoto} method {negative scenario} Integration Tests")
    public void testDeleteContactPhotoNegativeScenario() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_deleteContactPhoto_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
                "string(//faultstring/text())", nameSpaceMap), "");
    }

    /**
     * Positive test case for retrieveContactsByQuery method with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 1, groups = { "wso2.esb" },
          description = "Google Contacts {retrieveContactsByQuery} method {mandatory parameters} Integration Tests")
    public void testRetrieveContactsByQueryMandatoryParameters() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_retrieveContactsByQuery_mandatory.xml");
        ContactFeed contactFeed = directInvoker.retrieveAllContacts();

        Assert.assertEquals(String.valueOf(contactFeed.getTotalResults()),
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//openSearch:totalResults/text())", nameSpaceMap));

        Assert.assertEquals(
                contactFeed.getEntries().get(contactFeed.getEntries().size() - 1).getTitle().getPlainText(),
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[last()]/atom:title/text())", nameSpaceMap));
    }

    /**
     * Positive test case for retrieveContactsByQuery method with Optional parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 1, groups = { "wso2.esb" },
          description = "Google Contacts {retrieveContactsByQuery} method {Optional parameters} Integration Tests")
    public void testRetrieveContactsByQueryOptionalParameters() throws Exception {

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("requestUrl", "https://www.google.com/m8/feeds/contacts/default/full");

        Query query = (Query) loadObjectFromFile("api_retrieveContactsByQuery_optional.xml", paramMap);

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_retrieveContactsByQuery_optional.xml", paramMap);

        ContactFeed contactFeed = directInvoker.getDirectInvokerContactsService().query(query, ContactFeed.class);

        Assert.assertEquals(String.valueOf(contactFeed.getTotalResults()),
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//openSearch:totalResults/text())", nameSpaceMap));

        Assert.assertEquals(String.valueOf(contactFeed.getStartIndex()),
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//openSearch:startIndex/text())", nameSpaceMap));

    }

    /**
     * Negative test case for retrieveContactsByQuery method.
     *
     * @throws Exception the exception
     */
    @Test(expectedExceptions = AxisFault.class, priority = 1, groups = { "wso2.esb" },
          description = "Google Contacts {retrieveContactsByQuery} method {negative scenario} Integration Tests")
    public void testRetrieveContactsByQueryNegativeScenario() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_retrieveContactsByQuery_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
                "string(//faultstring/text())", nameSpaceMap), "");
    }

    /**
     * Positive test case for updateContactGroup method with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactGroupWithMandatoryParameters" },
            description = "Google Contacts {updateContactGroup} integration test with mandatory parameters.")
    public void testUpdateContactGroupWithMandatoryParameters() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("groupId", groupId);

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_updateContactGroup_mandatory.xml", parametersMap);

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        final ContactGroupEntry directResponse = directInvoker.retrieveSingleContactGroup(groupId);

        // Assert the group title.
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:title/text())", nameSpaceMap),
                directResponse.getTitle().getPlainText());
        // Assert the group's content.
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:content/text())", nameSpaceMap),
                directResponse.getPlainTextContent());
    }

    /**
     * Positive test case for updateContactGroup method with optional parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactGroupWithMandatoryParameters" },
            description = "Google Contacts {updateContactGroup} integration test with optional parameters.")
    public void testUpdateContactGroupWithOptionalParameters() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("groupId", groupId);

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_updateContactGroup_optional.xml", parametersMap);

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        final ContactGroupEntry directResponse =
                directInvoker.retrieveSingleContactGroupByUrl((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:link/@href)", nameSpaceMap));

        // Assert the group title.
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:content/text())", nameSpaceMap),
                directResponse.getPlainTextContent());
        // Assert the value of the extended property.
        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//gd:extendedProperty/@value)", nameSpaceMap),
                directResponse.getExtendedProperties().get(0).getValue());
    }

    /**
     * Negative test case for updateContactGroup method.
     *
     * @throws Exception the exception
     */
    @Test(expectedExceptions = AxisFault.class, priority = 2, groups = { "wso2.esb" }, description = "Google Contacts {updateContactGroup} integration test with negative case.")
    public void testUpdateContactGroupWithNegativeCase() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_updateContactGroup_negative.xml");
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
            "string(//faultstring/text())", nameSpaceMap), "");
    }

    // Retrieve Single contact group Test Scenarios

    /**
     * Positive test case for RetrieveSingleContactGroup method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactGroupWithMandatoryParameters" },
    description = "Google Contacts {retrieveSingleContactGroup integration test with mandatory parameters.")
    public void testRetrieveSingleContactGroup() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("groupId", groupId);

        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_retrieveSingleContactGroup_mandatory.xml", parametersMap);

        ContactGroupEntry directResponse = (ContactGroupEntry) directInvoker.retrieveSingleContactGroup(groupId);

        // Assert id
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:id/text())", nameSpaceMap),
                directResponse.getId());

        // Assert title
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:title/text())", nameSpaceMap),
                directResponse.getTitle().getPlainText());

        // Assert content
        Assert.assertEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:content/text())", nameSpaceMap),
                ((TextContent) directResponse.getContent()).getContent().getPlainText());
    }

    /**
     * Negative test case for retrieve single contact group method.
     */
    @Test(expectedExceptions = AxisFault.class, priority = 2, groups = { "wso2.esb" },
    dependsOnMethods = { "testCreateContactGroupWithMandatoryParameters" },
    description = "Google Contacts {retrieveSingleContactGroup} method {negative scenario} Integration Tests")
    public void testRetrieveSingleContactGroupNegativeScenario() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("groupId", "1111111111");
        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_retrieveSingleContactGroup_negative.xml", parametersMap);
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
                "string(//faultstring/text())", nameSpaceMap), "");
    }

    // Delete contact scenarios

    /**
     * Positive test case for delete contact scenario with mandatory parameters.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = ResourceNotFoundException.class, priority = 3, groups = { "wso2.esb" },
    description = "Google Contacts {deleteContact integration test with mandatory parameters.")
    public void testDeleteContact() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("contactId", contactId);

        sendSOAPRequest(proxyUrl, "esb_deleteContact_mandatory.xml", parametersMap);

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        directInvoker.retrieveSingleContact("e8547510e081c22");

    }

    /**
     * Negative test case for delete contact scenario.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = AxisFault.class, priority = 3, groups = { "wso2.esb" },
    description = "Google Contacts {deleteContact} method {negative scenario} Integration Tests")
    public void testDeleteContactNegativeScenario() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("contactId", "1111111111");

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_deleteContact_negative.xml", parametersMap);
        Assert.assertNotEquals((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
                "string(//faultstring/text())", nameSpaceMap), "");
    }

    /**
     * Positive test case for batchCreateContacts method with optional parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = {
            "testCreateContactWithOptionalParameters", "testCreateContactWithMandatoryParameters" },
            description = "Google Contacts {batchCreateContacts} integration test.")
    public void testBatchCreateContactsWithOptionalParameters() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("contactIdRead", contactId);
        parametersMap.put("contactIdUpdate", batchDeleteContactId);

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_batchCreateContacts_optional.xml", parametersMap);

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        // Assertions for the create.
        final ContactEntry directResponseCreate =
                directInvoker.retrieveSingleContactByUrl(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'insertEntry']/atom:id)", nameSpaceMap));

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'insertEntry']/atom:title/text())", nameSpaceMap),
                        directResponseCreate.getTitle().getPlainText());

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'insertEntry']/atom:content/text())", nameSpaceMap),
                        directResponseCreate.getPlainTextContent());

        // Assertions for the read.
        final ContactEntry directResponseRead = directInvoker.retrieveSingleContact(parametersMap.get("contactIdRead"));

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'retrieveEntry']/atom:title/text())", nameSpaceMap),
                        directResponseRead.getTitle().getPlainText());

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'retrieveEntry']/gd:email/@address)", nameSpaceMap),
                        directResponseRead.getEmailAddresses().get(0).getAddress());

        // Assertions for the update.
        final ContactEntry directResponseUpdate =
                directInvoker.retrieveSingleContact(parametersMap.get("contactIdUpdate"));

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'updateEntry']/atom:title/text())", nameSpaceMap),
                        directResponseUpdate.getTitle().getPlainText());

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'updateEntry']/atom:content/text())", nameSpaceMap),
                        directResponseUpdate.getPlainTextContent());
    }

    /**
     * Positive test case for batchCreateContacts method with delete request.
     *
     * @throws Exception the exception
     */
    @Test(expectedExceptions = ResourceNotFoundException.class, priority = 3, groups = { "wso2.esb" }, dependsOnMethods = {
            "testCreateContactWithOptionalParameters", "testCreateContactWithMandatoryParameters",
            "testBatchCreateContactsWithOptionalParameters"},
            description = "Google Contacts {batchCreateContacts} integration test with contact delete request.")
    public void testBatchCreateContactsWithDeleteRequest() throws Exception {

        final Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("contactIdDelete", batchDeleteContactId);

        sendSOAPRequest(proxyUrl, "esb_batchCreateContacts_delete.xml", parametersMap);

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        // Assertions for the delete.
        directInvoker.retrieveSingleContact(parametersMap.get("contactIdDelete"));
    }

    /**
     * Negative test case for batchCreateContacts method.
     *
     * @throws Exception the exception
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Google Contacts {batchCreateContacts} integration test with negative case.")
    public void testBatchCreateContactsWithNegativeCase() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_batchCreateContacts_negative.xml");

        final ContactFeed directResponse = directInvoker.retrieveBatchContactFeed("aaddd3324dd");

        // Assert for the Error result.
        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'retrieveEntry']/atom:title/text())", nameSpaceMap),
                        directResponse.getEntries().get(0).getTitle().getPlainText());

        // Assert for the error response code.
        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'retrieveEntry']/batch:status/@code)", nameSpaceMap),
                        Integer.toString(directResponse.getEntries().get(0).getBatchStatus().getCode()));
    }

    /**
     * Positive test case for batch create contact groups scenario with optional parameters.
     *
     * @throws Exception the exception
     */
    @Test(priority = 2, groups = { "wso2.esb" },
            dependsOnMethods = {"testCreateContactGroupWithMandatoryParameters", "testCreateContactGroupWithOptionalParameters"},
    description = "Google Contacts {createBatchContactGroups integration test with optional parameters.")
    public void testBatchCreateContactGroupsWithOptionalParameters() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("groupIdRetrieve", groupId);
        parametersMap.put("groupIdUpdate", batchDeleteGroupId);

        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "esb_batchCreateContactGroups_mandatory.xml", parametersMap);

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        // Assert for retrieve contacts
        ContactGroupEntry directResponse =
                directInvoker.retrieveSingleContactGroup(parametersMap.get("groupIdRetrieve"));

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'retrieve']/atom:title/text())", nameSpaceMap),
                        directResponse.getTitle().getPlainText());

        Assert.assertEquals(
               (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'retrieve']/atom:content/text())", nameSpaceMap),
                       directResponse.getPlainTextContent());

        // Assertions for the create.
        final ContactGroupEntry directResponseCreate =
                directInvoker.retrieveSingleContactGroupByUrl((String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(),
                        "string(//atom:entry[batch:id = 'create']/atom:id/text())", nameSpaceMap));

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'create']/atom:title/text())", nameSpaceMap),
                        directResponseCreate.getTitle().getPlainText());

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'create']/atom:content/text())", nameSpaceMap),
                        directResponseCreate.getPlainTextContent());

        // Assertions for the update.
        final ContactGroupEntry directResponseUpdate =
                directInvoker.retrieveSingleContactGroup(parametersMap.get("groupIdUpdate"));

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'update']/atom:title/text())", nameSpaceMap),
                        directResponseUpdate.getTitle().getPlainText());

        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'update']/atom:content/text())", nameSpaceMap),
                        directResponseUpdate.getPlainTextContent());
    }

    /**
     * Positive test case for batch create contact groups scenario with mandatory parameters.
     *
     * @throws Exception the exception
     */
    @Test(expectedExceptions = ResourceNotFoundException.class, priority = 3, groups = { "wso2.esb" },
            dependsOnMethods = {"testCreateContactGroupWithMandatoryParameters", "testCreateContactGroupWithOptionalParameters",
            "testBatchCreateContactGroupsWithOptionalParameters"},
            description = "Google Contacts {createBatchContactGroups integration test with delete request.")
    public void testBatchCreateContactGroupsWithDeleteRequest() throws Exception {

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put("groupIdDelete", batchDeleteGroupId);

        sendSOAPRequest(proxyUrl, "esb_batchCreateContactGroups_delete.xml", parametersMap);

        // Adding a sleep timer to buffer time taken to apply changes on backend.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));

        // Assertions for the delete.
        directInvoker.retrieveSingleContactGroup(parametersMap.get("groupIdDelete"));
    }

    /**
     * Negative test case for batch create contacts group scenario.
     *
     * @throws Exception
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Google Contacts {batchCreateContactGroups} method {negative scenario} Integration Tests")
    public void testBatchCreateContactGroupsNegativeScenario() throws Exception {

        SOAPEnvelope esbSoapResponse = sendSOAPRequest(proxyUrl, "esb_batchCreateContactGroups_negative.xml");
        final ContactGroupFeed directResponse = directInvoker.retrieveBatchContactGroupFeed("aaddd3324dd");
        // Assert for the Error result.
        Assert.assertEquals(
               (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//atom:entry[batch:id = 'retrieve']/atom:title/text())", nameSpaceMap),
                        directResponse.getEntries().get(0).getTitle().getPlainText());

        // Assert for the error response code.
        Assert.assertEquals(
                (String) xPathEvaluate(esbSoapResponse.getBody().getFirstElement(), "string(//batch:status/@code)", nameSpaceMap),
                        Integer.toString(directResponse.getEntries().get(0).getBatchStatus().getCode()));

    }

    // Class GoogleContactsDirectInvoker

    /**
     * Class GoogleContactsDirectInvoker - Inner Class for Direct API Invoke.
     */
    public class GoogleContactsDirectInvoker {

        /**
         * Request URL parameter.
         */
        public static final String REQUEST_URL_GENERIC_BEGIN = "https://www.google.com/m8/feeds/";

        /**
         * Request URL parameter.
         */
        public static final String REQUEST_URL_CONTACTS = "contacts";

        /**
         * Request URL parameter.
         */
        public static final String REQUEST_URL_GROUPS = "groups";

        /**
         * Trailing end of generic request URL.
         */
        public static final String REQUEST_URL_GENERIC_END = "/full";

        /** The direct invoker contacts service. */
        private ContactsService directInvokerContactsService;

        /**
         * Instantiates a new google contacts direct invoker.
         *
         * @param accessToken the access token
         * @param appName the app name
         */
        public GoogleContactsDirectInvoker(final String accessToken, final String appName) {

            final HttpTransport transport = new NetHttpTransport();
            final JsonFactory jsonFactory = new JacksonFactory();
            final GoogleCredential credential =
                    new GoogleCredential.Builder().setTransport(transport).setJsonFactory(jsonFactory).build()
                            .setAccessToken(accessToken);
            directInvokerContactsService = new ContactsService(appName);
            directInvokerContactsService.setOAuth2Credentials(credential);
        }

        /**
         * Gets the direct invoker contacts service.
         *
         * @return the direct invoker contacts service
         */
        public ContactsService getDirectInvokerContactsService() {

            return directInvokerContactsService;
        }

        /**
         * Retrieve all contacts via Direct API call.
         *
         * @return the contact feed
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ServiceException the service exception
         * @throws XMLStreamException the xML stream exception
         */
        public ContactFeed retrieveAllContacts() throws IOException, ServiceException, XMLStreamException {

            final StringBuilder requestUrl =
                    new StringBuilder(REQUEST_URL_GENERIC_BEGIN).append(REQUEST_URL_CONTACTS).append("/")
                            .append(emailAddress);
            requestUrl.append(REQUEST_URL_GENERIC_END);

            // Call Google Contact Service feed method
            return directInvokerContactsService.getFeed(new URL(requestUrl.toString()), ContactFeed.class);
        }

        /**
         * Retrieve all contact groups via Direct API call.
         *
         * @return the ContactGroupFeed.
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ServiceException the service exception.
         */
        public ContactGroupFeed retrieveAllContactGroups() throws IOException, ServiceException {

            final StringBuilder requestUrl = new StringBuilder(REQUEST_URL_GENERIC_BEGIN);
            requestUrl.append(REQUEST_URL_GROUPS).append("/").append(emailAddress).append(REQUEST_URL_GENERIC_END);

            // Get the feed resource referenced by the input URL.
            return directInvokerContactsService.getFeed(new URL(requestUrl.toString()), ContactGroupFeed.class);
        }

        /**
         * Creates the contact.
         *
         * @param entry the entry
         * @return the string
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ServiceException the service exception
         * @throws XMLStreamException the xML stream exception
         */
        public ContactEntry createContact(final ContactEntry entry) throws IOException, ServiceException,
                XMLStreamException {

            final StringBuilder requestUrl = new StringBuilder(REQUEST_URL_CONTACTS);
            requestUrl.append("/").append(emailAddress).append(REQUEST_URL_GENERIC_END);

            // Call Google Contact Service feed method
            return directInvokerContactsService.insert(new URL(requestUrl.toString()), entry);
        }

        /**
         * Retrieve a single contact by contact ID.
         *
         * @param contactId ID of the contact.
         * @return the contact entry.
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ServiceException the service exception.
         */
        public ContactEntry retrieveSingleContact(final String contactId) throws IOException, ServiceException {

            final StringBuilder requestBuilder =
                    new StringBuilder(REQUEST_URL_GENERIC_BEGIN).append(REQUEST_URL_CONTACTS).append("/")
                            .append(emailAddress).append(REQUEST_URL_GENERIC_END).append("/").append(contactId);

            final ContactEntry contactEntry =
                    directInvokerContactsService.getEntry(new URL(requestBuilder.toString()), ContactEntry.class);

            return contactEntry;
        }

        /**
         * Retrieve a Single Contact Group by contact group ID.
         *
         * @param groupId ID of the contact group.
         * @return the contact group.
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ServiceException the service exception.
         */
        public ContactGroupEntry retrieveSingleContactGroup(final String groupId) throws IOException, ServiceException {

            final StringBuilder requestUrlBuilder =
                    new StringBuilder(REQUEST_URL_GENERIC_BEGIN).append(REQUEST_URL_GROUPS).append("/")
                            .append(emailAddress).append(REQUEST_URL_GENERIC_END).append("/").append(groupId);

            final ContactGroupEntry contactGroup =
                    directInvokerContactsService.getEntry(new URL(requestUrlBuilder.toString()),
                            ContactGroupEntry.class);

            return contactGroup;
        }

        /**
         * Retrieve a Single Contact Group by the full contact group ID.
         *
         * @param groupIdUrl the group id url
         * @return the contact group.
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ServiceException the service exception.
         */
        public ContactGroupEntry retrieveSingleContactGroupByUrl(final String groupIdUrl) throws IOException,
                ServiceException {

            final ContactGroupEntry contactGroup =
                    directInvokerContactsService.getEntry(new URL(groupIdUrl.toString()), ContactGroupEntry.class);

            return contactGroup;
        }

        /**
         * Retrieve a single contact by contact ID URL.
         *
         * @param contactIdUrl the contact id url
         * @return the contact entry.
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ServiceException the service exception.
         */
        public ContactEntry retrieveSingleContactByUrl(final String contactIdUrl) throws IOException, ServiceException {

            final ContactEntry contactEntry =
                    directInvokerContactsService.getEntry(new URL(contactIdUrl), ContactEntry.class);

            return contactEntry;
        }

		/**
         * Create a contacts batch result for negative case.
         *
         * @param contactId ID of the contact.
         * @return ContactFeed object containing the batch.
         * @throws ServiceException the service exception.
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws MalformedURLException error in the provided URLs.
         * @throws BatchInterruptedException Batch Interruption failure exception.
         */
        public ContactFeed retrieveBatchContactFeed(final String contactId) throws BatchInterruptedException,
                MalformedURLException, IOException, ServiceException {

            final ContactFeed batchFeed = new ContactFeed();
            final ContactEntry retrieveEntry = new ContactEntry();
            final StringBuilder requestUrlBuilder =
                    new StringBuilder("https://www.google.com/m8/feeds/contacts/").append(emailAddress)
                            .append("/full/").append(contactId);

            retrieveEntry.setId(requestUrlBuilder.toString());
            BatchUtils.setBatchId(retrieveEntry, "retrieveEntry");
            BatchUtils.setBatchOperationType(retrieveEntry, BatchOperationType.QUERY);

            batchFeed.getEntries().add(retrieveEntry);

            return directInvokerContactsService.batch(new URL(
                    "https://www.google.com/m8/feeds/contacts/default/full/batch"), batchFeed);
        }

        /**
         * Create a contacts groups batch result for negative case.
         *
         * @param groupId ID of the contact group.
         * @return ContactFeed object containing the batch.
         * @throws ServiceException the service exception.
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws MalformedURLException error in the provided URLs.
         * @throws BatchInterruptedException Batch Interruption failure exception.
         */
        public ContactGroupFeed retrieveBatchContactGroupFeed(final String groupId) throws BatchInterruptedException,
                MalformedURLException, IOException, ServiceException {

            final ContactGroupFeed batchFeed = new ContactGroupFeed();
            final ContactGroupEntry retrieveEntry = new ContactGroupEntry();
            final StringBuilder requestUrlBuilder =
                    new StringBuilder("https://www.google.com/m8/feeds/groups/").append(emailAddress).append("/full/")
                            .append(groupId);

            retrieveEntry.setId(requestUrlBuilder.toString());
            BatchUtils.setBatchId(retrieveEntry, "retrieve");
            BatchUtils.setBatchOperationType(retrieveEntry, BatchOperationType.QUERY);

            batchFeed.getEntries().add(retrieveEntry);

            return directInvokerContactsService.batch(new URL(
                    "https://www.google.com/m8/feeds/groups/default/full/batch"), batchFeed);
        }
    }
}
