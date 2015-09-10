package org.wso2.carbon.connector.integration.test.sendgrid;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class SendGridConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap;

    private Map<String, String> apiRequestHeadersMap;

    private String apiEndpointUrl;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        esbRequestHeadersMap = new HashMap<String, String>();

        apiRequestHeadersMap = new HashMap<String, String>();

        init("sendgrid-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);

        apiEndpointUrl = connectorProperties.getProperty("apiUrl");

    }

    /**
     * Positive test case for getList method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, dependsOnMethods = { "testAddListWithMandatoryParameters" },description = "SendGrid {getList} integration test with mandatory parameters.")
    public void testGetListWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getList");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getList_mandatory.json");

        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");

        final JSONArray apiResponseArray=new JSONArray(responseArrayString);

        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("list"), apiResponseArray.getJSONObject(0).get("list"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("id"), apiResponseArray.getJSONObject(0).get("id"));

    }


    /**
     * Positive test case for getList method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getList} integration test with optional parameters.")
    public void testGetListWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getList");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getList_optional.json");

        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("getListName");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");

        final JSONArray apiResponseArray=new JSONArray(responseArrayString);

        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("list"), apiResponseArray.getJSONObject(0).get("list"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("id"), apiResponseArray.getJSONObject(0).get("id"));

    }


    /**
     * Positive test case for listCategory method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, dependsOnMethods = { "testAddCategoryWithMandatoryParameters" }, description = "SendGrid {listCategory} integration test with mandatory parameters.")
    public void testListCategoryWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCategory");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCategory_mandatory.json");

        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/category/list.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");

        final JSONArray apiResponseArray=new JSONArray(responseArrayString);

        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("category"), apiResponseArray.getJSONObject(0).get("category"));

    }



    /**Not working in the API
     * Positive test case for listCategory method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {listCategory} integration test with optional parameters.")
    public void testListCategoryWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCategory");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCategory_optional.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/category/list.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"category="+connectorProperties.getProperty("categoryList");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        //  Assert.assertEquals(esbRestResponse.getBody().getString("category"), apiRestResponse.getBody().getString("category"));

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Positive test case for listMarketingEmail method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, dependsOnMethods = { "testAddMarketingEmailWithMandatoryParameters" }, description = "SendGrid {listMarketingEmail} integration test with mandatory parameters.")
    public void testListMarketingEmailWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listMarketingEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMarketingEmail_mandatory.json");

        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/list.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");

        final JSONArray apiResponseArray=new JSONArray(responseArrayString);

        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("name"), apiResponseArray.getJSONObject(0).get("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("newsletter_id"), apiResponseArray.getJSONObject(0).get("newsletter_id"));

        //Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Positive test case for listMarketingEmail method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {listMarketingEmail} integration test with optional parameters.")
    public void testListMarketingEmailWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listMarketingEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMarketingEmail_optional.json");

        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/list.json?"+"api_user="+connectorProperties.getProperty("apiUser")+"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("nameListMarketingEmail");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");

        final JSONArray apiResponseArray=new JSONArray(responseArrayString);

        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("name"), apiResponseArray.getJSONObject(0).get("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("newsletter_id"), apiResponseArray.getJSONObject(0).get("newsletter_id"));

        //Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Positive test case for createCategory method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {createCategory} integration test with mandatory parameters.")
    public void testCreateCategoryWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:createCategory");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCategory_mandatory.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/category/create.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"category="+connectorProperties.getProperty("createCategoryName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }

    /**
     * Positive test case for addCategory method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {addCategory} integration test with mandatory parameters.")
    public void testAddCategoryWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addCategory");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addCategory_mandatory.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/category/create.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"category="+connectorProperties.getProperty("addCategoryName")+"&"
                +"name="+connectorProperties.getProperty("marktingEmailName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");

    }


    /**
     * Positive test case for removeCategory method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     * here name is marketing email name which is you are going to remove from the category
     */
    @Test(priority = 1, description = "SendGrid {removeCategory} integration test with mandatory parameters.")
    public void testRemoveCategoryWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:removeCategory");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeCategory_mandatory.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/category/remove.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("removeCategoryName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");

    }



    /**
     * Positive test case for removeCategory method with optional parameters.
     * @throws JSONException
     * @throws IOException
     * here name is marketing email name which is you are going to remove from the category
     */
    @Test(priority = 1, description = "SendGrid {removeCategory} integration test with optional parameters.")
    public void testRemoveCategoryWithOptionalParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:removeCategory");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeCategory_optional.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/category/remove.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("removeCategoryName")
                +"&"+"category="+connectorProperties.getProperty("removeCategory");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");

    }


    /**
     * Positive test case for getEmail method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getEmail} integration test with mandatory parameters.")
    public void testGetEmailWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmail_mandatory.json");

        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/email/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("getEmailListName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");

        final JSONArray apiResponseArray=new JSONArray(responseArrayString);

        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("email"), apiResponseArray.getJSONObject(0).get("email"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("name"), apiResponseArray.getJSONObject(0).get("name"));
        //Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }



    /**
     * Positive test case for getEmail method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getEmail} integration test with optional parameters.")
    public void testGetEmailWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmail_optional.json");

        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/email/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("getEmailListName")
                +"&"+"email="+connectorProperties.getProperty("getEmailName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");

        final JSONArray apiResponseArray=new JSONArray(responseArrayString);

        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("email"), apiResponseArray.getJSONObject(0).get("email"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("name"), apiResponseArray.getJSONObject(0).get("name"));
        //Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }

    /**
     * Positive test case for getMarketingEmail method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getMarketingEmail} integration test with mandatory parameters.")
    public void testGetMarketingEmailWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getMarketingEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMarketingEmail_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("getMarketingEmailListName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getString("content_preview"), apiRestResponse.getBody().getString("content_preview"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subject"), apiRestResponse.getBody().getString("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getString("html"), apiRestResponse.getBody().getString("html"));

        //Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }



    /**
     * Positive test case for getRecipient method with mandatory parameters.
     * here name is marketing email name
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getRecipient} integration test with mandatory parameters.")
    public void testGetRecipientWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getRecipient");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecipient_mandatory.json");

        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/recipients/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("getRecipientListName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");

        final JSONArray apiResponseArray=new JSONArray(responseArrayString);

        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("list"), apiResponseArray.getJSONObject(0).get("list"));
        //Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Positive test case for listSenderaddress method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {listSenderaddress} integration test with mandatory parameters.")
    public void testListSenderAddressWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listSenderaddress");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSenderaddress_mandatory.json");

        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/list.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");

        final JSONArray apiResponseArray=new JSONArray(responseArrayString);

        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("identity"), apiResponseArray.getJSONObject(0).get("identity"));
        //Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Positive test case for listSenderaddress method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {listSenderaddress} integration test with optional parameters.")
    public void testListSenderAddressWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listSenderaddress");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSenderaddress_optional.json");

        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/list.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("listIdentityName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");

        final JSONArray apiResponseArray=new JSONArray(responseArrayString);

        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("identity"), apiResponseArray.getJSONObject(0).get("identity"));
        //Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Positive test case for addEmail method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {addEmail} integration test with mandatory parameters.")
    public void testAddEmailWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addEmail_mandatory.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/email/add.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("addEmailListName")+"&"
                +"data="+connectorProperties.getProperty("marktingEmailName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("inserted"), "1");

    }


    /**
     * Positive test case for addList method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {addList} integration test with mandatory parameters.")
    public void testAddListWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addList_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/add.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("addListName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }


    /**
     * Positive test case for addList method with optional parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {addList} integration test with optional parameters.")
    public void testAddListWithOptionalParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addList_optional.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/add.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("addlistNameOptional")+"&"+"name="+connectorProperties.getProperty("nameOfAddList");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }


    /**
     * Positive test case for getSchedule method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getSchedule} integration test with mandatory parameters.")
    public void testGetScheduleWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getSchedule");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSchedule_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/schedule/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("nameMarketingEmail");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("date"), apiRestResponse.getBody().getString("date"));

        // Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Positive test case for getSenderaddress method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getSenderaddress} integration test with mandatory parameters.")
    public void testGetSenderaddressWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getSenderaddress");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSenderaddress_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("getIdentityName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getString("city"), apiRestResponse.getBody().getString("city"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("zip"), apiRestResponse.getBody().getString("zip"));
        Assert.assertEquals(esbRestResponse.getBody().getString("replyto"), apiRestResponse.getBody().getString("replyto"));
        Assert.assertEquals(esbRestResponse.getBody().getString("country"), apiRestResponse.getBody().getString("country"));
        Assert.assertEquals(esbRestResponse.getBody().getString("state"), apiRestResponse.getBody().getString("state"));
        Assert.assertEquals(esbRestResponse.getBody().getString("address"), apiRestResponse.getBody().getString("address"));
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("identity"), apiRestResponse.getBody().getString("identity"));

        // Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Positive test case for countEmail method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {countEmail} integration test with mandatory parameters.")
    public void testCountEmailWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:countEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_countEmail_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/email/count.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("countListName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getString("count"), apiRestResponse.getBody().getString("count"));
        // Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }

    /**
     * Positive test case for addMarketingEmail method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {addMarketingEmail} integration test with mandatory parameters.")
    public void testAddMarketingEmailWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addMarketingEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addMarketingEmail_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/add.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("addMarketingEmailIdentity")
                +"&"+"name="+connectorProperties.getProperty("addMarketingEmailName")+"&"+"subject="+connectorProperties.getProperty("addMarketingEmailSubject")
                +"&"+"text="+connectorProperties.getProperty("addMarketingEmailText")+"&"+"html="+connectorProperties.getProperty("addMarketingEmailHtml");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }

    /**
     * Positive test case for addRecipient method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {addRecipient} integration test with mandatory parameters.")
    public void testAddRecipientWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addRecipient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addRecipient_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/recipients/add.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("addRecipientList")
                +"&"+"name="+connectorProperties.getProperty("addRecipientName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }

    /**
     * Positive test case for addSchedule method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {addSchedule} integration test with mandatory parameters.")
    public void testAddScheduleWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addSchedule");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addSchedule_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/schedule/add.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("addScheduleName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }


    /**
     * Positive test case for addSchedule method with optional parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {addSchedule} integration test with optional parameters.")
    public void testAddScheduleWithoOtionalParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addSchedule");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addSchedule_optional.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/schedule/add.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("addScheduleName0")
                +"&"+"at="+connectorProperties.getProperty("addScheduleAt");

        /*final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/recipients/add.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("addScheduleName0")
                +"&"+"after="+connectorProperties.getProperty("addScheduleAfter");*/

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }

    /**
     * Positive test case for addSenderaddress method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {addSenderaddress} integration test with mandatory parameters.")
    public void testAddSenderaddressWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addSenderaddress");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addSenderaddress_mandatory.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/add.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("addSenderaddressIdentity")
                +"&"+"name="+connectorProperties.getProperty("addSenderaddressName")+"&"+"email="+connectorProperties.getProperty("addSenderaddressEmail")
                +"&"+"address="+connectorProperties.getProperty("addSenderaddressAddress")+"&"+"city="+connectorProperties.getProperty("addSenderaddressCity")
                +"&"+"state="+connectorProperties.getProperty("addSenderaddressState")
                +"&"+"zip="+connectorProperties.getProperty("addSenderaddressZip")
                +"&"+"country="+connectorProperties.getProperty("addSenderaddressCountry");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }


    /**
     * Positive test case for addSenderaddress method with optional parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {addSenderaddress} integration test with optional parameters.")
    public void testAddSenderaddressWithOptionalParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addSenderaddress");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addSenderaddress_optional.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/add.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("addSenderaddressIdentity")
                +"&"+"name="+connectorProperties.getProperty("addSenderaddressName")+"&"+"email="+connectorProperties.getProperty("addSenderaddressEmail")
                +"&"+"address="+connectorProperties.getProperty("addSenderaddressAddress")+"&"+"city="+connectorProperties.getProperty("addSenderaddressCity")
                +"&"+"state="+connectorProperties.getProperty("addSenderaddressState")
                +"&"+"zip="+connectorProperties.getProperty("addSenderaddressZip")
                +"&"+"country="+connectorProperties.getProperty("addSenderaddressCountry")
                +"&"+"replyto="+connectorProperties.getProperty("addSenderaddressReplyTo");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }


    /**
     * Positive test case for editList method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {editList} integration test with mandatory parameters.")
    public void testEditListWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:editList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editList_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/edit.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("editListName")
                +"&"+"newlist="+connectorProperties.getProperty("editListNewName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }

    /**
     * Positive test case for editMarketingEmail method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {editMarketingEmail} integration test with mandatory parameters.")
    public void testEditMarketingEmailWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:editMarketingEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editMarketingEmail_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/edit.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("editMarketingEmailIdentity")
                +"&"+"name="+connectorProperties.getProperty("editMarketingEmailName")+"&"+"subject="+connectorProperties.getProperty("editMarketingEmailSubject")
                +"&"+"text="+connectorProperties.getProperty("editMarketingEmailText")+"&"+"html="+connectorProperties.getProperty("editMarketingEmailHtml");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }

    /**
     * Positive test case for editMarketingEmail method with optional parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {editMarketingEmail} integration test with optional parameters.")
    public void testEditMarketingEmailWithOptionalParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:editMarketingEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editMarketingEmail_optional.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/edit.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("editMarketingEmailIdentity")
                +"&"+"name="+connectorProperties.getProperty("editMarketingEmailName")+"&"+"subject="+connectorProperties.getProperty("editMarketingEmailSubject")
                +"&"+"text="+connectorProperties.getProperty("editMarketingEmailText")+"&"+"html="+connectorProperties.getProperty("editMarketingEmailHtml")
                +"&"+"newname="+connectorProperties.getProperty("editMarketingEmailNewName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }



    /**
     * Positive test case for editSenderaddress method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {editSenderaddress} integration test with mandatory parameters.")
    public void testeditSenderaddressWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:editSenderaddress");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editSenderaddress_mandatory.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/edit.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("editSenderaddressIdentity")
                +"&"+"email="+connectorProperties.getProperty("editSenderaddressEmail");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }


    /**
     * Positive test case for editSenderaddress method with optional parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {editSenderaddress} integration test with optional parameters.")
    public void testeditSenderaddressWithOptionalParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:editSenderaddress");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editSenderaddress_optional.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/edit.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("editSenderaddressIdentity")
                +"&"+"name="+connectorProperties.getProperty("editSenderaddressName")+"&"+"email="+connectorProperties.getProperty("editSenderaddressEmail")
                +"&"+"address="+connectorProperties.getProperty("editSenderaddressAddress")+"&"+"newidentity="+connectorProperties.getProperty("editSenderaddressNewidentity")
                +"&"+"replyto="+connectorProperties.getProperty("editSenderaddressReplyto");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }



    /**
     * Positive test case for deleteEmail method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {deleteEmail} integration test with mandatory parameters.")
    public void testDeleteEmailWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEmail_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/email/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("deleteEmailList")
                +"&"+"email="+connectorProperties.getProperty("deleteEmailName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("removed"), "1");

    }


    /**
     * Positive test case for deleteList method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {deleteList} integration test with mandatory parameters.")
    public void testDeleteListWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:deleteList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteList_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("deleteListName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }



    /**
     * Positive test case for deleteMarketingEmail method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {deleteMarketingEmail} integration test with mandatory parameters.")
    public void testDeleteMarketingEmailWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:deleteMarketingEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteMarketingEmail_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("deleteMarketingEmailName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }



    /**
     * Positive test case for deleteRecipient method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {deleteRecipient} integration test with mandatory parameters.")
    public void testDeleteRecipientWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:deleteRecipient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteRecipient_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/recipients/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("deleteRecipientList")
                +"&"+"name="+connectorProperties.getProperty("deleteRecipientName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }



    /**
     * Positive test case for deleteSchedule method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {deleteSchedule} integration test with mandatory parameters.")
    public void testDeleteScheduleWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:deleteSchedule");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteSchedule_mandatory.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/schedule/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("deleteScheduleName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }



    /**
     * Positive test case for deleteSenderaddress method with mandatory parameters.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {deleteSenderaddress} integration test with mandatory parameters.")
    public void testDeleteSenderaddressWithMandatoryParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:deleteSenderaddress");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteSenderaddress_mandatory.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("deleteSenderaddressIdentity");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
    }



    /**
     * Negative test case for addCategory method
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {addCategory} integration test with Negative parameters.")
    public void testAddCategoryWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:addCategory");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addCategory_negative.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/category/create.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"category="+connectorProperties.getProperty("addCategoryNegativeName")+"&"
                +"name="+connectorProperties.getProperty("marktingEmailNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);

    }


    /**
     * Negative test case for createCategory method
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {createCategory} integration test with Negative parameters.")
    public void testCreateCategoryWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:createCategory");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCategory_negative.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/category/create.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"category="+connectorProperties.getProperty("createCategoryNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);
    }



    /**
     * Negative test case for removeCategory method.
     * @throws JSONException
     * @throws IOException
     * here name is marketing email name which is you are going to remove from the category
     */
    @Test(priority = 1, description = "SendGrid {removeCategory} integration test with Negative parameters.")
    public void testRemoveCategoryWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:removeCategory");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeCategory_negative.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/category/remove.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("removeCategoryNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);

    }



    /**
     * Negative test case for countEmail method
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {countEmail} integration test with Negative parameters.")
    public void testCountEmailWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:countEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_countEmail_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/email/count.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("countListNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);

    }



    /**
     * Negative test case for deleteEmail method.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {deleteEmail} integration test with Negative parameters.")
    public void testDeleteEmailWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEmail_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/email/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("deleteEmailNegativeList")
                +"&"+"email="+connectorProperties.getProperty("deleteEmailNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);

    }


    /**
     * Negative test case for getEmail method.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getEmail} integration test with Negative parameters.")
    public void testGetEmailWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmail_negative.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/email/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("getEmailListNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);

    }


    /**
     * Negative test case for deleteList.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {deleteList} integration test with Negative parameters.")
    public void testDeleteListWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:deleteList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteList_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("deleteListNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }



    /**
     * Negative test case for editList method.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {editList} integration test with Negative parameters.")
    public void testEditListWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:editList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editList_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/edit.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("editListNegativeName")
                +"&"+"newlist="+connectorProperties.getProperty("editListNegativeNewName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }



    /**
     * Negative test case for getList method.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getList} integration test with Negative parameters.")
    public void testGetListWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getList");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getList_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/lists/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("getListNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }



    /**
     * Negative test case for deleteMarketingEmail method.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {deleteMarketingEmail} integration test with Negative parameters.")
    public void testDeleteMarketingEmailWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:deleteMarketingEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteMarketingEmail_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("deleteMarketingEmailNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }



    /**
     * Negative test case for editMarketingEmail method.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {editMarketingEmail} integration test with Negative parameters.")
    public void testEditMarketingEmailWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:editMarketingEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editMarketingEmail_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/edit.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("editMarketingEmailNegativeIdentity")
                +"&"+"name="+connectorProperties.getProperty("editMarketingEmailName")+"&"+"subject="+connectorProperties.getProperty("editMarketingEmailSubject")
                +"&"+"text="+connectorProperties.getProperty("editMarketingEmailText")+"&"+"html="+connectorProperties.getProperty("editMarketingEmailHtml");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }



    /**
     * Negative test case for getMarketingEmail method.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getMarketingEmail} integration test with Negative parameters.")
    public void testGetMarketingEmailWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getMarketingEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMarketingEmail_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("getMarketingEmailNegativeListName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }



    /**
     * Negative test case for listMarketingEmail method.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {listMarketingEmail} integration test with Negative parameters.")
    public void testListMarketingEmailWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listMarketingEmail");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMarketingEmail_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/list.json?"+"api_user="+connectorProperties.getProperty("apiUser")+"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("listMarketingNegativeEmailName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Negative test case for deleteRecipient method.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {deleteRecipient} integration test with Negative parameters.")
    public void testDeleteRecipientWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:deleteRecipient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteRecipient_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/recipients/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"list="+connectorProperties.getProperty("deleteRecipientNegativeList")
                +"&"+"name="+connectorProperties.getProperty("deleteRecipientNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }



    /**
     * Negative test case for getRecipient method.
     * here name is marketing email name
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getRecipient} integration test with Negative parameters.")
    public void testGetRecipientWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getRecipient");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecipient_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/recipients/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("getRecipientNegativeListName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Negative test case for deleteSchedule method.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {deleteSchedule} integration test with Negative parameters.")
    public void testDeleteScheduleWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:deleteSchedule");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteSchedule_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/schedule/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("deleteScheduleNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }



    /**
     * Negative test case for getSchedule method.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getSchedule} integration test with Negative parameters.")
    public void testGetScheduleWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getSchedule");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSchedule_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/schedule/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"name="+connectorProperties.getProperty("nameMarketingEmailNegative");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }



    /**
     * Negative test case for deleteSenderaddress method.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {deleteSenderaddress} integration test with Negative parameters.")
    public void testDeleteSenderaddressWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:deleteSenderaddress");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteSenderaddress_negative.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/delete.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("deleteSenderaddressNegativeIdentity");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }


    /**
     * Negative test case for editSenderaddress method.
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "SendGrid {editSenderaddress} integration test with Negative parameters.")
    public void testeditSenderaddressWithNegativeParameters() throws IOException, JSONException{

        esbRequestHeadersMap.put("Action", "urn:editSenderaddress");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editSenderaddress_negative.json");


        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/edit.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("editSenderaddressNegativeIdentity")
                +"&"+"email="+connectorProperties.getProperty("editSenderaddressNegativeEmail");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


    /**
     * Negative test case for getSenderaddress method.
     *
     * @throws JSONException
     * @throws IOException
     */

    @Test(priority = 1, description = "SendGrid {getSenderaddress} integration test with Negative parameters.")
    public void testGetSenderaddressWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getSenderaddress");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSenderaddress_negative.json");

        final String apiEndpoint = connectorProperties.getProperty("apiUrl") +"/identity/get.json?"+"api_user="+connectorProperties.getProperty("apiUser")
                +"&"+"api_key="+connectorProperties.getProperty("apiKey")+"&"+"identity="+connectorProperties.getProperty("getIdentityNegativeName");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }


}