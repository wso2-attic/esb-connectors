/**
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
package org.wso2.carbon.connector.powerbi.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.synapse.MessageContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.connector.core.AbstractConnector;

import com.google.gson.Gson;

/**
 * Class which associate with retrieving access token through refresh token and
 * authorization code.
 **/
public class PowerBIAccessTokenGenerator extends AbstractConnector {

   /**
    * Connector method which is executed at the specified point within the
    * corresponding Synapse template within the connector.
    * 
    * @param messageContext
    *           Synapse Message Context
    */
   @Override
   public void connect(MessageContext messageContext) {
      String apiUrl = (String) messageContext.getProperty(Constants.API_URL);
      String requestQuery = (String) messageContext.getProperty(Constants.REQUEST_QUERY);
      JSONObject responseObject = null;
      try {
         responseObject = sendRequest(apiUrl, Constants.HTTP_METHOD, Constants.CONTENT_TYPE, requestQuery);
         messageContext.setProperty(Constants.RESULT_OBJECT, new Gson().toJson(responseObject));
      } catch (IOException ioException) {
         log.error("Failed to contact server:", ioException);
      } catch (JSONException jsonException) {
         log.error("Failed to create Json Object:", jsonException);
      }

   }

   /**
    * Method used to retrieve a JSON object with access token by accessing the
    * web service.
    * 
    * @param endPoint
    *           End point to retrieve access token.
    * @param httpMethod
    *           Method type for HTTP call.
    * @param contentType
    *           Content type of the method call.
    * @param requestQuery
    *           Parameters for the method call.
    * @return JSONObject with access token.
    * @throws IOException
    *            If an error occurs on PowerBI API end.
    * @throws JSONException
    *            If an object creation error occurred.
    */
   private JSONObject sendRequest(String endPoint, String httpMethod, String contentType, String requestQuery)
         throws IOException, JSONException {
      HttpURLConnection httpConnection = writeRequest(endPoint, httpMethod, contentType, requestQuery);

      String responseString = readResponse(httpConnection);
      JSONObject jsonObject = null;
      if (isValidJSON(responseString)) {
         jsonObject = new JSONObject(responseString);
      }
      return jsonObject;

   }

   /**
    * Method use to write the request.
    * 
    * @param endPoint
    *           End point to retrieve access token.
    * @param httpMethod
    *           Method type for HTTP call.
    * @param contentType
    *           Content type of the method call.
    * @param requestQuery
    *           Parameters for method call.
    * @return HttpURLConnection httpUrlConnection object.
    * @throws IOException
    *            If an error occurs on PowerBI API end.
    */
   private HttpURLConnection writeRequest(String endPoint, String httpMethod, String contentType, String requestQuery)
         throws IOException {

      OutputStream output = null;

      URL url = new URL(endPoint);
      HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
      httpConnection.setInstanceFollowRedirects(false);
      httpConnection.setRequestMethod(httpMethod);
      httpConnection.setRequestProperty("Content-Type", contentType);
      httpConnection.setDoOutput(true);
      try {

         output = httpConnection.getOutputStream();
         output.write(requestQuery.getBytes(Charset.defaultCharset()));

      } finally {

         if (output != null) {
            try {
               output.close();
            } catch (IOException ioException) {
               log.error("Failed to contact server:", ioException);
            }
         }

      }
      return httpConnection;
   }

   /**
    * Method used to read the response.
    * 
    * @param httpConnection
    *           httpUrlConnection object.
    * @return String response string.
    * @throws IOException
    *            If an error occurs reading the response.
    */
   private String readResponse(HttpURLConnection httpConnection) throws IOException {

      InputStream responseStream = null;
      String responseString = null;
      if (httpConnection.getResponseCode() >= 400) {
         responseStream = httpConnection.getErrorStream();
      } else {
         responseStream = httpConnection.getInputStream();
      }

      if (responseStream != null) {

         StringBuilder stringBuilder = new StringBuilder();
         byte[] bytes = new byte[1024];
         int len;

         while ((len = responseStream.read(bytes)) != -1) {
            stringBuilder.append(new String(bytes, 0, len));
         }

         if (!stringBuilder.toString().trim().isEmpty()) {
            responseString = stringBuilder.toString();
         }

      }

      return responseString;
   }

   /**
    * Method used to check for a JSON object.
    * 
    * @param json
    *           String query.
    * @return boolean
    *           returns true if valid JSON or returns false.
    */
   private boolean isValidJSON(String json) {

      try {
         new JSONObject(json);
         return true;
      } catch (JSONException ex) {
         return false;
      }
   }
}
