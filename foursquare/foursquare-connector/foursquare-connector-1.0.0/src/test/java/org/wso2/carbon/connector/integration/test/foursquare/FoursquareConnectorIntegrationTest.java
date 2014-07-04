package org.wso2.carbon.connector.integration.test.foursquare;

/**
 * Created by ian on 6/22/14.
 */



import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class FoursquareConnectorIntegrationTest extends ConnectorIntegrationTestBase {


    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> headersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {


        System.out.println("**<<<<<<<<<<<<<<<<<<<<<<<<<* Starting init *>>>>>>>>>>>>>>>>>>>>>>>>>>**");
        super.init("foursquare-connector-1.0.0");
        System.out.println("**<<<<<<<<<<<<<<<<<<<<<<<<<* init Executed *>>>>>>>>>>>>>>>>>>>>>>>>>>**");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer "+connectorProperties.getProperty("accessToken"));

    }


/**
     * Positive test case for getCheckinDetails
     *
     * @throws NoSuchAlgorithmException
     */


    @Test(priority = 1, description = "foursquare {getCheckinDetails} integration test with mandatory fields.")
    public void testgetCheckinDetailsWithMandatoryFields() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getCheckinDetails");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/" +connectorProperties.getProperty("checkinId")
                        + "?v=20140626&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCheckinDetailsMandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("id"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("createdAt"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("createdAt"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("timeZoneOffset"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("timeZoneOffset"));


    }


/**
     * Positive test case for getCheckinDetails
     *
     * @throws NoSuchAlgorithmException
     */

   @Test(priority = 1, description = "foursquare {getCheckinDetails} integration test with optional fields.")
    public void testgetCheckinDetailsWithOptionalFields() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getCheckinDetails");

         String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/checkins/" +connectorProperties.getProperty("checkinId")
                        + "?signature="+ connectorProperties.getProperty("signature")+"&oauth_token="+connectorProperties.getProperty("accessToken")+"&v=20140626";

         RestResponse<JSONObject> esbRestResponse =
          sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCheckinDetailsOptional.txt");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("id"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("id"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("createdAt"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("createdAt"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("timeZoneOffset"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("timeZoneOffset"));


    }


    /**
     * Negative test case for getCheckinDetails
     *
     * @throws NoSuchAlgorithmException
     */


    @Test(priority = 1, description = "foursquare {getCheckinDetails} integration test with negative fields.")
    public void testgetCheckinDetailsNegativeCase() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getCheckinDetails");
        //https://api.foursquare.com/v2/checkins/539a80db11d2f005bb2b605f?v=20131016&oauth_token=3EVR4GJLWJOYADANHOCCNYI4NLGYCTPR41UDIUUYIKF2QQTY
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/" +connectorProperties.getProperty("checkinId")
                        + "?v=20140626&oauth_token="+connectorProperties.getProperty("accessTokenInvalid");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCheckinDetailsNegative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);



        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorType"), apiRestResponse.getBody().getJSONObject("meta").get("errorType"));


    }


    /**
     * Positive test case for addCheckins method with mandatory parameters.
     **/

    @Test(priority = 1, description = "foursquare {addCheckin} integration test with mandatory parameters.")
    public void testaddCheckinsWithMandatoryParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:addCheckins");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addCheckins_mandatory.txt");


        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/add?v=20140626&venueId=" + connectorProperties.getProperty("venueId")
                 +"&broadcast="+ connectorProperties.getProperty("broadcast")+ "&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("timeZoneOffset"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").get("timeZoneOffset"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").getJSONObject("score").get("total"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("checkin").getJSONObject("score").get("total"));




    }





    /**
     * Negative test case for addCheckins method with negative parameters.
     **/

    @Test(priority = 2, description = "foursquare {addCheckin} integration test with mandatory parameters.")
    public void testaddCheckinsWithNegativeParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:addCheckins");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addCheckins_Negative.txt");


        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/add?v=20140626&venueId=" + connectorProperties.getProperty("InvalidvenueId")
                 +"&broadcast="+ connectorProperties.getProperty("broadcast")+ "&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);



        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorDetail"), apiRestResponse.getBody().getJSONObject("meta").get("errorDetail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorType"), apiRestResponse.getBody().getJSONObject("meta").get("errorType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("code"), apiRestResponse.getBody().getJSONObject("meta").get("code"));




    }


    /**
     * Positive test case for addCheckinComment method with mandatory parameters.
     */
   @Test(priority = 2, description = "foursquare {addCheckinComment} integration test with mandatory parameters.")
    public void testaddCheckinCommentMandatoryParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:addCheckinComment");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addCheckinComment_mandatory.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/"+ connectorProperties.getProperty("checkinId")
                            +"/addcomment?v=20140626&text="+ connectorProperties.getProperty("comment")+"&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("comment").get("text"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("comment").get("text"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("comment").getJSONObject("user").get("relationship"), apiRestResponse.getBody().getJSONObject("response").getJSONObject("comment").getJSONObject("user").get("relationship"));



   }

    /**
     * Negative test case for addCheckinComment method with negative parameters.
     */

    @Test(priority = 2, description = "foursquare {addCheckinComment} integration test with negative parameters.")
    public void testaddCheckinCommentNegativeParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:addCheckinComment");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addCheckinComment_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/"+ connectorProperties.getProperty("checkinId")
                +"/addcomment?v=20140626&text="+ connectorProperties.getProperty("comment")+"&oauth_token="+connectorProperties.getProperty("accessTokenInvalid");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorType"), apiRestResponse.getBody().getJSONObject("meta").get("errorType"));



    }


    /**
     * Positive test case for getCheckinRecent method with mandatory parameters.
     */
   @Test(priority = 1, description = "foursquare {getCheckinRecent} integration test with mandatory parameters.")
    public void testgetCheckinRecentMandatoryParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:getCheckinRecent");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCheckinRecent_mandatory.txt");


        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/recent?v=20140626&ll="+ connectorProperties.getProperty("ll")+"&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(apiRestResponse.getHttpStatusCode(),200);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

       Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("recent").getJSONObject(0).get("like").toString(), apiRestResponse.getBody().getJSONObject("response").getJSONArray("recent").getJSONObject(0).get("like").toString());

    }

    /**
    * Positive test case for getCheckinRecent method with optional parameters.
    */

   @Test(priority = 2, description = "foursquare {getCheckinRecent} integration test with optional parameters.")
   public void testgetCheckinRecentOptionalParameters() throws IOException, JSONException, InterruptedException {

       esbRequestHeadersMap.put("Action", "urn:getCheckinRecent");

       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCheckinRecent_optional.txt");


       String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/recent?v=20140626&ll="+connectorProperties.getProperty("ll")+"&limit="+ connectorProperties.getProperty("limit")+  "&oauth_token="+connectorProperties.getProperty("accessToken");

       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

       Assert.assertEquals(apiRestResponse.getHttpStatusCode(),200);
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("recent").getJSONObject(0).get("like").toString(), apiRestResponse.getBody().getJSONObject("response").getJSONArray("recent").getJSONObject(0).get("like").toString());


   }

    /**
     * Negative test case for getCheckinRecent method with Negative parameters.
     */
       @Test(priority = 2, description = "foursquare {getCheckinRecent} integration test with mandatory parameters.")
        public void testgetCheckinRecentNegativeParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:getCheckinRecent");

        RestResponse<JSONObject> esbRestResponse =
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCheckinRecent_Negative.txt");


        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/recent?v=20140626&ll="+ connectorProperties.getProperty("invalidll")+"&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

         Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
         Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
         Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorType"), apiRestResponse.getBody().getJSONObject("meta").get("errorType"));
         Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("code"), apiRestResponse.getBody().getJSONObject("meta").get("code"));


     }

    /**
     * Positive test case for deleteCheckinComment method with mandatory parameters.
    */

    @Test(priority = 2, description = "foursquare {deleteCheckinComment} integration test with mandatory parameters.")
    public void testdeleteCheckinCommentMandatoryParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:deleteCheckinComment");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/"+ connectorProperties.getProperty("checkinId")+ "/deletecomment?v=20140626&commentId="+
                connectorProperties.getProperty("commentId")+"&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCheckinComment_mandatory.txt");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("meta").get("errorDetail").toString(), esbRestResponse.getBody().getJSONObject("meta").get("errorDetail").toString());

    }

    /**
     * Negative test case for deleteCheckinComment method with negative parameters.
     */
         @Test(priority = 2, description = "foursquare {deleteCheckinComment} integration test with negative parameters.")
        public void testdeleteCheckinCommentNegativeParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:deleteCheckinComment");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v2/checkins/"+ connectorProperties.getProperty("checkinId")+ "/deletecomment?v=20140626&commentId="+
        connectorProperties.getProperty("commentId")+"&oauth_token="+connectorProperties.getProperty("accessTokenInvalid");

        RestResponse<JSONObject> esbRestResponse =
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCheckinComment_Negative.txt");


        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

          System.out.println(esbRestResponse.getBody());
          System.out.println(apiRestResponse.getBody());
          Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
          Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);
          Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorType"), apiRestResponse.getBody().getJSONObject("meta").get("errorType"));


          }




    //-------------------------------------------------------User Category----------------------------------------------------------------------------------------------------------------------------------

    /**
     * Positive test case for getUserDetails method with mandatory parameters.
     */

    @Test(priority = 1, description = "foursquare {getUserDetails} integration test with mandatory fields.")
    public void testgetUserDetailsWithMandatoryFields() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getUserDetails");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
                +"?v=20140626&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserDetailsMandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("user").get("id").toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("user").get("id").toString());


    }


    /**
     * Negaive test case for getUserDetails method.
     */

    @Test(priority = 1, description = "foursquare {getUserDetails} integration test with negative case.")
    public void testgetUserDetailsWithNegativeCase() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getUserDetails");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
                +"?v=20140626&oauth_token="+connectorProperties.getProperty("accessTokenInvalid");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserDetailsNegative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorDetail"), apiRestResponse.getBody().getJSONObject("meta").get("errorDetail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorType"), apiRestResponse.getBody().getJSONObject("meta").get("errorType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("code"), apiRestResponse.getBody().getJSONObject("meta").get("code"));



    }

    /**
     * Positive test case for getFriendDetails method with mandatory parameters.
     */

    @Test(priority = 2, description = "foursquare {getFriendDetails} integration test with mandatory fields.")
    public void testgetFriendDetailsWithMandatoryFields() throws IOException, JSONException, NoSuchAlgorithmException {

            esbRequestHeadersMap.put("Action", "urn:getFriendDetails");
            String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")+"/friends"
            +"?v=20131213"
            +"&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFriendDetailsMandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("friends").get("count").toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("friends").get("count").toString());

    }


    /**
     * Positive test case for getFriendDetails method with optional parameters. (limit, offset)
     */

    @Test(priority = 2, description = "foursquare {getFriendDetails} integration test with optional fields.")
    public void testgetFriendDetailsWithOptionalFields() throws IOException, JSONException, NoSuchAlgorithmException {

            esbRequestHeadersMap.put("Action", "urn:getFriendDetails");
            String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")+"/friends"
            +"?v=20131213"
            +"&limit="+connectorProperties.getProperty("limit")
            +"&offset="+connectorProperties.getProperty("offset")
            +"&oauth_token="+connectorProperties.getProperty("accessToken");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFriendDetailsOptional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("friends").get("count").toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("friends").get("count").toString());

    }

    /**
     * Negative test case for getFriendDetails method
     */

   @Test(priority = 2, description = "foursquare {getFriendDetails} integration test with negative case.")
    public void testgetFriendDetailsWithNegativeCase() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getFriendDetails");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")+"/friends"
                +"?v=20140626"
                +"&limit="+connectorProperties.getProperty("limit")
                +"&offset="+connectorProperties.getProperty("offset")
                +"&oauth_token="+connectorProperties.getProperty("accessTokenInvalid");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFriendDetailsNegative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorDetail"), apiRestResponse.getBody().getJSONObject("meta").get("errorDetail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorType"), apiRestResponse.getBody().getJSONObject("meta").get("errorType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("code"), apiRestResponse.getBody().getJSONObject("meta").get("code"));

    }

    /**
     * Positive test case for getUserBadges method with mandatory parameters.
     */

    @Test(priority = 2, description = "foursquare {getBadgeDetails} integration test with mandatory fields.")
    public void testgetBadgeDetailsWithMandatoryFields() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getBadgeDetails");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
                +"/badges"
                +"?v=20140626"
                +"&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBadgeDetailsMandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("badges").getJSONObject("4f6a48ae7beb7e5831d4dffe").get("name").toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("badges").getJSONObject("4f6a48ae7beb7e5831d4dffe").get("name").toString());

    }

    /**
     * Negative test case for getUserBadges method with mandatory parameters.
     */


    @Test(priority = 2, description = "foursquare {getBadgeDetails} integration test with negative case.")
    public void testgetBadgeDetailsWithNegativeCase() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getBadgeDetails");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
                +"/badges"
                +"?v=20140626"
                +"&oauth_token="+connectorProperties.getProperty("accessTokenInvalid");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBadgeDetailsNegative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorDetail"), apiRestResponse.getBody().getJSONObject("meta").get("errorDetail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorType"), apiRestResponse.getBody().getJSONObject("meta").get("errorType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("code"), apiRestResponse.getBody().getJSONObject("meta").get("code"));

    }

    /**
     * Positive test case for getMayorshipDetails method with mandatory parameters.
     */

    @Test(priority = 2, description = "foursquare {getMayorshipDetails} integration test with mandatory fields.")
    public void testgetMayorshipDetailsWithMandatoryFields() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getMayorshipDetails");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
                +"/mayorships"
                +"?v=20140626"
                +"&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMayorshipDetailsMandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getHttpStatusCode(),200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(),200);
    }


    /**
     * Negative test case for getUserMayorships method.
     */

    @Test(priority = 2, description = "foursquare {getMayorshipDetails} integration test with negative fields.")
    public void testgetMayorshipDetailsWithNegativeCase() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getMayorshipDetails");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
                +"/mayorships"
                +"?v=20140626"
                +"&oauth_token="+connectorProperties.getProperty("accessTokenInvalid");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMayorshipDetailsNegative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorDetail"), apiRestResponse.getBody().getJSONObject("meta").get("errorDetail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorType"), apiRestResponse.getBody().getJSONObject("meta").get("errorType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("code"), apiRestResponse.getBody().getJSONObject("meta").get("code"));

    }



    /**
     * Positive test case for getUserTips method with mandatory parameters.
     */

    @Test(priority = 2, description = "foursquare {getTipsDetails} integration test with mandatory fields.")
    public void testgetTipsDetailsWithMandatoryFields() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getTipsDetails");
            String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
            +"/tips"
            +"?v=20140626"
            +"&oauth_token="+connectorProperties.getProperty("accessToken");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTipsDetailsMandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("tips").get("count").toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("tips").get("count").toString());

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(),200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(),200);
    }

    /**
     * Positive test case for getTipsDetails method with optional parameters.
     */

    @Test(priority = 2, description = "foursquare {getTipsDetails} integration test with optional fields.")
    public void testgetTipsDetailsWithOptionlaFields() throws IOException, JSONException, NoSuchAlgorithmException {

          esbRequestHeadersMap.put("Action", "urn:getTipsDetails");
          String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
          +"/tips"
          +"?v=20131213"
          +"&sort="+connectorProperties.getProperty("sort")
          +"&ll="+connectorProperties.getProperty("ll")
          +"&limit="+connectorProperties.getProperty("limit")
          +"&offset="+connectorProperties.getProperty("offset")
          +"&oauth_token="+connectorProperties.getProperty("accessToken");


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTipsDetailsOptional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONObject("tips").get("count").toString(), apiRestResponse.getBody().getJSONObject("response").getJSONObject("tips").get("count").toString());

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(),200);
    }


    /**
     * Negative test case for getUserTips method.
     */

    @Test(priority = 2, description = "foursquare {getTipsDetails} integration test with Negative case")
    public void testgetTipsDetailsWithNegativeCase() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:getTipsDetails");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
                +"/tips"
                +"?v=20140626"
                +"&sort="+connectorProperties.getProperty("sort")
                +"&limit="+connectorProperties.getProperty("limitInvalid")
                +"&offset="+connectorProperties.getProperty("offset")
                +"&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTipsDetailsNegative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorDetail"), apiRestResponse.getBody().getJSONObject("meta").get("errorDetail"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("errorType"), apiRestResponse.getBody().getJSONObject("meta").get("errorType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("code"), apiRestResponse.getBody().getJSONObject("meta").get("code"));


    }


    /**
     * Positive test case for approveFriends method with mandatory parameters.
     */

    @Test(priority = 1, description = "foursquare {approveFriends} integration test with mandatory fields.")
    public void testapproveFriendsWithMandatoryFields() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:approveFriends");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
                +"/approve"
                +"?v=20140626"
                +"&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_approveFriendsMandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);


    }



    /**
     * Negative test case for approveFriends method.
     */

   @Test(priority = 1, description = "foursquare {approveFriends} integration test with mandatory fields.")
    public void testapproveFriendsWithNegativeCase() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:approveFriends");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
                +"/approve"
                +"?v=20140626"
                +"&oauth_token="+connectorProperties.getProperty("accessTokenInvalid");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_approveFriendsNegative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);

    }

   /**
     * Positive test case for unFriends method with mandatory parameters.
     */

    @Test(priority = 1, description = "foursquare {unFriends} integration test with mandatory fields.")
    public void testunFriendsWithMandatoryFields() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:unFriends");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userIdToUnFriend")
                +"/unfriend"
                +"?v=20140626"
                +"&oauth_token="+connectorProperties.getProperty("accessToken");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unFriendsMandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("meta").get("errorDetail").toString(), "No relationship between you and this user.");

    }


    /**
     * Negative test case for unFriends method with mandatory parameters.
     */

    @Test(priority = 1, description = "foursquare {unFriends} integration test with mandatory fields.")
    public void testunFriendsWithNegativeFields() throws IOException, JSONException, NoSuchAlgorithmException {

        esbRequestHeadersMap.put("Action", "urn:unFriends");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/v2/users/"+connectorProperties.getProperty("userId")
                +"/unfriend"
                +"?v=20140626"
                +"&oauth_token="+connectorProperties.getProperty("accessTokenInvalid");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unFriendsNegative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);


        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("meta").get("errorDetail").toString(), "OAuth token invalid or revoked.");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);
    }






}



