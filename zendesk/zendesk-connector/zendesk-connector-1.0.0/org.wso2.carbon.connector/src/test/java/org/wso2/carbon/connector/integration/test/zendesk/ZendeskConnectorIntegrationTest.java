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

package org.wso2.carbon.connector.integration.test.zendesk;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ZendeskConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> parametersMap = new HashMap<String, String>();
   
   private String ticketId, token, attachmentId, attachmentIdOptional, externalId, organizationId, groupId,
         requesterId;
   
   private StringBuffer ticketIds = new StringBuffer("");
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("zendesk-connector-1.0.0");
      
      esbRequestHeadersMap.put("Content-Type", "application/json");
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      
      String token = connectorProperties.getProperty("username") + ":" + connectorProperties.getProperty("password");
      
      // encoding token into base 64
      byte[] encodedToken = Base64.encodeBase64(token.getBytes());
      apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedToken));
   }
   
   /**
    * Positive test case for listTicketAudits method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {listTicketAudits} integration test with mandatory parameters.")
   public void testListTicketAuditsWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTicketAudits");
      parametersMap.put("ticketId", ticketId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketAudits_mandatory.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + ticketId
                  + "/audits.json", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getJSONArray("audits").getJSONObject(0).getString("id"),
            esbRestResponse.getBody().getJSONArray("audits").getJSONObject(0).getString("id"));
      Assert.assertEquals(ticketId,
            esbRestResponse.getBody().getJSONArray("audits").getJSONObject(0).getString("ticket_id"));
      
   }
   
   /**
    * Negative test case for listTicketAudits method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {listTicketAudits} integration test for negative case.")
   public void testListTicketAuditsNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTicketAudits");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketAudits_negative.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/INVALID/audits.json",
                  "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("title"), esbRestResponse
            .getBody().getJSONObject("error").getString("title"));
      
   }
   
   /**
    * Positive test case for uploadFiles method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {uploadFiles} integration test with mandatory parameters.")
   public void testUploadFilesWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:uploadFiles");
      
      final Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
      
      attachmentHeadersMap.putAll(esbRequestHeadersMap);
      attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("fileContentType"));
      
      String fileName = connectorProperties.getProperty("fileName");
      String requestString =
            proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&username="
                  + connectorProperties.getProperty("username") + "&password="
                  + connectorProperties.getProperty("password") + "&fileName=" + fileName;
      
      final MultipartFormdataProcessor fileRequestProcessor =
            new MultipartFormdataProcessor(requestString, attachmentHeadersMap);
      
      File file = new File(pathToResourcesDirectory + fileName);
      fileRequestProcessor.addFiletoRequestBody(file);
      
      RestResponse<JSONObject> esbRestResponse = fileRequestProcessor.processAttachmentForJsonResponse();
      
      token = esbRestResponse.getBody().getJSONObject("upload").getString("token");
      
      attachmentId =
            esbRestResponse.getBody().getJSONObject("upload").getJSONArray("attachments").getJSONObject(0)
                  .getString("id");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/attachments/" + attachmentId
                  + ".json", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("upload").getJSONArray("attachments")
            .getJSONObject(0).getString("file_name"), fileName);
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attachment").getString("file_name"), fileName);
      Assert.assertNotSame(apiRestResponse.getBody().getJSONObject("attachment").getString("size"), 0);
      
   }
   
   /**
    * Positive test case for uploadFiles method with optional parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testUploadFilesWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Zendesk {uploadFiles} integration test with optional parameters.")
   public void testUploadFilesWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:uploadFiles");
      
      final Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
      
      attachmentHeadersMap.putAll(esbRequestHeadersMap);
      attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("fileContentTypeOptional"));
      
      String fileNameOptional = connectorProperties.getProperty("fileNameOptional");
      String requestString =
            proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&username="
                  + connectorProperties.getProperty("username") + "&password="
                  + connectorProperties.getProperty("password") + "&fileName=" + fileNameOptional + "&token=" + token;
      
      final MultipartFormdataProcessor fileRequestProcessor =
            new MultipartFormdataProcessor(requestString, attachmentHeadersMap);
      
      File file = new File(pathToResourcesDirectory + fileNameOptional);
      fileRequestProcessor.addFiletoRequestBody(file);
      
      RestResponse<JSONObject> esbRestResponse = fileRequestProcessor.processAttachmentForJsonResponse();
      
      attachmentIdOptional =
            esbRestResponse.getBody().getJSONObject("upload").getJSONObject("attachment").getString("id");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/attachments/"
                  + attachmentIdOptional + ".json", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("upload").getJSONObject("attachment").getString("file_name"),
            fileNameOptional);
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("attachment").getString("file_name"),
            fileNameOptional);
      Assert.assertNotSame(apiRestResponse.getBody().getJSONObject("attachment").getString("size"), 0);
      
   }
   
   /**
    * Negative test case for uploadFiles method.
    */
   @Test(priority = 1, dependsOnMethods = { "testUploadFilesWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {uploadFiles} integration test with negative case.")
   public void testUploadFilesWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:uploadFiles");
      
      final Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
      
      attachmentHeadersMap.putAll(esbRequestHeadersMap);
      attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("fileContentTypeOptional"));
      
      String fileNameOptional = connectorProperties.getProperty("fileNameOptional");
      String requestString =
            proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&username="
                  + connectorProperties.getProperty("username") + "&password="
                  + connectorProperties.getProperty("password") + "&fileNameInvalid=INVALID";
      
      final MultipartFormdataProcessor fileRequestProcessor =
            new MultipartFormdataProcessor(requestString, attachmentHeadersMap);
      
      File file = new File(pathToResourcesDirectory + fileNameOptional);
      fileRequestProcessor.addFiletoRequestBody(file);
      
      RestResponse<JSONObject> esbRestResponse = fileRequestProcessor.processAttachmentForJsonResponse();
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), "RecordInvalid");
   }
   
   /**
    * Positive test case for listTickets method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {listTickets} integration test with mandatory parameters.")
   public void testListTicketsWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_mandatory.json");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets.json", "GET",
                  apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getString("count").toString(),
            apiRestResponse.getBody().getString("count").toString());
      
   }
   
   /**
    * Positive test case for listTickets method with optional parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {listTickets} integration test with optional parameters.")
   public void testListTicketsWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      parametersMap.put("externalId", externalId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_optional.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets.json?external_id="
                  + externalId, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("external_id"),
            apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("external_id"));
      Assert.assertEquals(esbRestResponse.getBody().getString("count").toString(),
            apiRestResponse.getBody().getString("count").toString());
   }
   
   /**
    * Negative test case for listTickets method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {listTickets} integration test for negative case.")
   public void testListTicketsNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_negative.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("invalidApiUrl") + "/api/v2/tickets.json", "GET",
                  apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("title"), esbRestResponse
            .getBody().getJSONObject("error").getString("title"));
      
   }
   
   /**
    * Positive test case for listTicketsForOrganizationId method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {listTicketsForOrganizationId} integration test with mandatory parameters.")
   public void testListTicketsForOrganizationIdWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      parametersMap.put("organizationId", organizationId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_listTicketsForOrganizationId_mandatory.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/organizations/" + organizationId
                  + "/tickets.json", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("organization_id"),
            apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("organization_id"));
      Assert.assertEquals(esbRestResponse.getBody().getString("count").toString(),
            apiRestResponse.getBody().getString("count").toString());
   }
   
   /**
    * Negative test case for listTicketsForOrganizationId method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {listTicketsForOrganizationId} integration test for negative case.")
   public void testListTicketsForOrganizationIdNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_listTicketsForOrganizationId_negative.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl")
                  + "/api/v2/organizations/INVALID/tickets.json", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("title"), esbRestResponse
            .getBody().getJSONObject("error").getString("title"));
   }
   
   /**
    * Positive test case for listTicketsForUserId method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {listTicketsForUserId} integration test with mandatory parameters.")
   public void testListTicketsForUserIdWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      parametersMap.put("userId", requesterId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketsForUserId_mandatory.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/users/" + requesterId
                  + "/tickets/requested.json", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("requester_id"),
            apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("requester_id"));
      Assert.assertEquals(esbRestResponse.getBody().getString("count").toString(),
            apiRestResponse.getBody().getString("count").toString());
   }
   
   /**
    * Negative test case for listTicketsForUserId method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {listTicketsForUserId} integration test for negative case.")
   public void testListTicketsForUserIdNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketsForUserId_negative.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl")
                  + "/api/v2/users/INVALID/tickets/requested.json", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("title"), esbRestResponse
            .getBody().getJSONObject("error").getString("title"));
   }
   
   /**
    * Positive test case for listTicketsForCcd method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {listTicketsForCcd} integration test with mandatory parameters.")
   public void testListTicketsForCcdWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      parametersMap.put("userId", requesterId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketsForCcd_mandatory.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/users/" + requesterId
                  + "/tickets/ccd.json", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("collaborator_ids"),
            apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("collaborator_ids"));
      Assert.assertEquals(esbRestResponse.getBody().getString("count").toString(),
            apiRestResponse.getBody().getString("count").toString());
   }
   
   /**
    * Negative test case for listTicketsForCcd method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {listTicketsForCcd} integration test for negative case.")
   public void testListTicketsForCcdNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketsForCcd_negative.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/users/INVALID/tickets/ccd.json",
                  "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("title"), esbRestResponse
            .getBody().getJSONObject("error").getString("title"));
   }
   
   /**
    * Positive test case for listTicketsForRecent method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {listTicketsForRecent} integration test with mandatory parameters.")
   public void testListTicketsForRecentWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      parametersMap.put("userId", requesterId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketsForRecent_mandatory.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/recent.json", "GET",
                  apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getString("next_page"),
            apiRestResponse.getBody().getString("next_page"));
      Assert.assertEquals(esbRestResponse.getBody().getString("count").toString(),
            apiRestResponse.getBody().getString("count").toString());
   }
   
   /**
    * Negative test case for listTicketsForRecent method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {listTicketsForRecent} integration test for negative case.")
   public void testListTicketsForRecentNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketsForRecent_negative.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("invalidApiUrl") + "/api/v2/tickets/recent.json",
                  "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("title"), esbRestResponse
            .getBody().getJSONObject("error").getString("title"));
   }
   
   /**
    * Positive test case for getAttachment method with mandatory parameters.
    */
   
   @Test(priority = 1, dependsOnMethods = { "testUploadFilesWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Zendesk {getAttachment} integration test with mandatory parameters.")
   public void testGetAttachmentWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getAttachment");
      parametersMap.put("attachmentId", attachmentId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAttachment_mandatory.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/attachments/" + attachmentId
                  + ".json", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attachment").getString("id"), apiRestResponse
            .getBody().getJSONObject("attachment").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attachment").getString("file_name"), apiRestResponse
            .getBody().getJSONObject("attachment").getString("file_name"));
      
   }
   
   /**
    * Negative test case for getAttachment method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {getAttachment} integration test for negative case.")
   public void testGetAttachmentNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getAttachment");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAttachment_negative.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/attachments/INVALID.json", "GET",
                  apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("title"), esbRestResponse
            .getBody().getJSONObject("error").getString("title"));
      
   }
   
   /**
    * Positive test case for search method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {search} integration test with mandatory parameters.")
   public void testSearchWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:search");
      parametersMap.put("query", connectorProperties.getProperty("query"));
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_mandatory.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/search.json?query="
                  + connectorProperties.getProperty("query"), "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getString("count").toString(),
            apiRestResponse.getBody().getString("count").toString());
      
   }
   
   /**
    * Positive test case for search method with optional parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {search} integration test with optional parameters.")
   public void testSearchWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:search");
      parametersMap.put("query", connectorProperties.getProperty("query"));
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_optional.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/search.json?query="
                  + connectorProperties.getProperty("query") + "&sort_by=created_at", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("created_at"),
            apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("created_at"));
   }
   
   /**
    * Negative test case for search method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {search} integration test for negative case.")
   public void testSearchNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:search");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_negative.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/search.json?query=" + "\""
                  + connectorProperties.getProperty("query") + "\"" + "&sort_order=INVALID", "GET",
                  apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("title"), esbRestResponse
            .getBody().getJSONObject("error").getString("title"));
   }
   
   /**
    * Positive test case for DeleteTicket method with mandatory parameters.
    */
   @Test(priority = 2, dependsOnMethods = { "testUpdateTicketWithNegativeCase" }, groups = { "wso2.esb" }, description = "Zendesk {DeleteTicket} integration test with mandatory parameters.")
   public void testDeleteTicketWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:deleteTicket");
      parametersMap.put("ticketId", ticketId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTicket_mandatory.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + ticketId + ".json",
                  "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      
   }
   
   /**
    * Negative test case for DeleteTicket method.
    */
   @Test(priority = 2, groups = { "wso2.esb" }, description = "Zendesk {DeleteTicket} integration test for negative case.")
   public void testDeleteTicketNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:deleteTicket");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTicket_negative.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/INVALID.json", "GET",
                  apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("title"), esbRestResponse
            .getBody().getJSONObject("error").getString("title"));
   }
   
   /**
    * Positive test case for showMultipleTickets method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {showMultipleTickets} integration test with mandatory parameters.")
   public void testShowMultipleTicketsWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:showMultipleTickets");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showMultipleTickets_mandatory.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/show_many.json", "POST",
                  apiRequestHeadersMap);
      
      // Count element should be 0 when no value is passed for ids. Asserting it to 0
      Assert.assertEquals(apiRestResponse.getBody().getString("count"), esbRestResponse.getBody().getString("count"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONArray("tickets").length(), esbRestResponse.getBody()
            .getJSONArray("tickets").length());
      
   }
   
   /**
    * Positive test case for showMultipleTickets method with optional parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {showMultipleTickets} integration test with optional parameters.")
   public void testShowMultipleTicketsWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:showMultipleTickets");
      parametersMap.put("ticketIds", ticketIds.toString());
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showMultipleTickets_optional.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/show_many.json?ids="
                  + ticketIds.toString(), "POST", apiRequestHeadersMap);
      
      final int esbTicketListLength = esbRestResponse.getBody().getJSONArray("tickets").length();
      final int apiTicketListLength = apiRestResponse.getBody().getJSONArray("tickets").length();
      
      Assert.assertEquals(apiRestResponse.getBody().getString("count"), esbRestResponse.getBody().getString("count"));
      Assert.assertEquals(apiTicketListLength, esbTicketListLength);
      
      for (int i = 0; i < esbTicketListLength; i++) {
         Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(i).getString("id"),
               apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(i).getString("id"));
         Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(i).getString("subject"),
               apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(i).getString("subject"));
         Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(i)
               .getString("description"),
               apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(i).getString("description"));
      }
      
   }
   
   /**
    * Negative test case for showMultipleTickets method with invalid parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {showMultipleTickets} integration test with invalid parameters.")
   public void testShowMultipleTicketsWithInvlalidParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:showMultipleTickets");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showMultipleTickets_invalid.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/show_many.json?ids="
                  + connectorProperties.getProperty("apiShowMuiltipleTickets"), "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("message"), esbRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("title"), esbRestResponse
            .getBody().getJSONObject("error").getString("title"));
      
   }
   
   /**
    * Positive test case for createTicket method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testUploadFilesWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Zendesk {createTicket} integration test with mandatory parameters.")
   public void testCreateTicketWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createTicket");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_Mandatory.json",
                  parametersMap);
      
      ticketId = esbRestResponse.getBody().getJSONObject("ticket").getString("id");
      organizationId = esbRestResponse.getBody().getJSONObject("ticket").getString("organization_id");
      groupId = esbRestResponse.getBody().getJSONObject("ticket").getString("group_id");
      externalId = esbRestResponse.getBody().getJSONObject("ticket").getString("external_id");
      requesterId = esbRestResponse.getBody().getJSONObject("ticket").getString("requester_id");
      ticketIds.append(ticketId);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + ticketId + ".json",
                  "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(ticketId, apiRestResponse.getBody().getJSONObject("ticket").getString("id"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("subject"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("subject"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("description"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("description"));
      
   }
   
   /**
    * Positive test case for createTicket method with optional parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Zendesk {createTicket} integration test with optional parameters.")
   public void testCreateTicketWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createTicket");
      parametersMap.put("organizationId", organizationId);
      parametersMap.put("collaboratorId", requesterId);
      parametersMap.put("groupId", groupId);
      parametersMap.put("attachment", token);
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_optional.json", parametersMap);
      
      String id = esbRestResponse.getBody().getJSONObject("ticket").getString("id");
      
      ticketIds.append(",");
      ticketIds.append(id);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + id + ".json", "GET",
                  apiRequestHeadersMap);
      
      Assert.assertEquals(id, apiRestResponse.getBody().getJSONObject("ticket").getString("id"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("subject"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("subject"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("description"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("description"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("priority"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("priority"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("type"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("type"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("status"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("status"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("organization_id"),
            apiRestResponse.getBody().getJSONObject("ticket").getString("organization_id"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("group_id"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("group_id"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("due_at"), apiRestResponse.getBody()
            .getJSONObject("ticket").getString("due_at"));
      
   }
   
   /**
    * Negative test case for createTicket method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {createTicket} integration test with negative case.")
   public void testCreateTicketWithNegativeCase() throws Exception {
   
      final Map<String, String> apiParametersMap = new HashMap<String, String>();
      
      esbRequestHeadersMap.put("Action", "urn:createTicket");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_invalid.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets.json", "POST",
                  apiRequestHeadersMap, "api_createTicket_invalid.json", apiParametersMap);
      
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("details").getJSONArray("base").getJSONObject(0).get("description"),
            apiRestResponse.getBody().getJSONObject("details").getJSONArray("base").getJSONObject(0).get("description"));
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      
      Assert.assertEquals(esbRestResponse.getBody().getString("description"),
            apiRestResponse.getBody().getString("description"));
      
   }
   
   /**
    * Positive test case for updateTicket method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {updateTicket} integration test with mandatory parameters.")
   public void testUpdateTicketWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:updateTicket");
      
      parametersMap.put("requesterId", requesterId);
      parametersMap.put("ticketId", ticketId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTicket_mandatory.json",
                  parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + ticketId + ".json",
                  "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("subject"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("subject"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("requester_id"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("requester_id"));
      
   }
   
   /**
    * Positive test case for updateTicket method with optional parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testUploadFilesWithOptionalParameters",
         "testCreateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {updateTicket} integration test with optional parameters.")
   public void testUpdateTicketWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:updateTicket");
      parametersMap.put("ticketId", ticketId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTicket_optional.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + ticketId + ".json",
                  "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("priority"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("priority"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("type"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("type"));
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("due_at"), apiRestResponse
            .getBody().getJSONObject("ticket").getString("due_at"));
   }
   
   /**
    * Negative test case for updateTicket method.
    */
   @Test(priority = 1, dependsOnMethods = { "testUpdateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {updateTicket} integration test with negative case.")
   public void testUpdateTicketWithNegativeCase() throws Exception {
   
      final Map<String, String> apiParametersMap = new HashMap<String, String>();
      
      esbRequestHeadersMap.put("Action", "urn:updateTicket");
      parametersMap.put("ticketId", ticketId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTicket_invalid.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + ticketId + ".json",
                  "PUT", apiRequestHeadersMap, "api_updateTicket_invalid.json", apiParametersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      
      Assert.assertEquals(esbRestResponse.getBody().getString("description"),
            apiRestResponse.getBody().getString("description"));
      
   }
   
   /**
    * Positive test case for deleteUpload method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testUpdateTicketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Zendesk {deleteUpload} integration test with mandatory parameters.")
   public void testDeleteUploadWithMandatoryParameters() throws Exception {
   
      // generate new token and attachmentId for deletion
      testUploadFilesWithMandatoryParameters();
      
      esbRequestHeadersMap.put("Action", "urn:deleteUpload");
      parametersMap.put("token", token);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteUpload.json", parametersMap);
      
      Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/attachments/" + attachmentId
                  + ".json", "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      
   }
   
   /**
    * Negative test case for deleteUpload method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "Zendesk {deleteUpload} integration test with negative case.")
   public void testDeleteUploadWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:deleteUpload");
      parametersMap.put("token", "INVALID");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteUpload.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/uploads/INVALID.json", "DELETE",
                  apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getString("error"), esbRestResponse.getBody().getString("error"));
      Assert.assertEquals(apiRestResponse.getBody().getString("description"),
            esbRestResponse.getBody().getString("description"));
   }
   
   /**
    * Positive test case for addTags method with optional parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testUpdateTicketWithNegativeCase" }, groups = { "wso2.esb" }, description = "Zendesk {addTags} integration test with optional parameters.")
   public void testAddTagsWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:addTags");
      parametersMap.put("ticketId", ticketId);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addTags_optional.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + ticketId + ".json",
                  "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("tags"), apiRestResponse.getBody()
            .getJSONObject("ticket").getString("tags"));
      
   }
   
   /**
    * Positive test case for addTags method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testAddTagsWithOptionalParameters" }, description = "Zendesk {addTags} integration test with mandatory parameters.")
   public void testAddTagsWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:addTags");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addTags_mandatory.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + ticketId + ".json",
                  "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("tags"), apiRestResponse.getBody()
            .getJSONObject("ticket").getString("tags"));
      
   }
   
   /**
    * Negative test case for addTags method.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testAddTagsWithMandatoryParameters" }, description = "Zendesk {addTags} integration test with negative case.")
   public void testAddTagsWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:addTags");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addTags_invalid.json", parametersMap);
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/INVALID/tags.json", "PUT",
                  apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("title"), apiRestResponse
            .getBody().getJSONObject("error").getString("title"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
   }
   
}
