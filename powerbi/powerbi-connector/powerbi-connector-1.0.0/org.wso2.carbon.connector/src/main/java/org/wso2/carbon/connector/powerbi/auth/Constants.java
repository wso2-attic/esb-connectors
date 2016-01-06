/**
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

/**
 * Constants class for PowerBI Connector.
 */
public final class Constants {

   /**
    * Instantiates a new constants.
    */
   private Constants() {

   }

   /**
    * PowerBI API_URL.
    */
   public static final String API_URL = "uri.var.authApiUrl";

   /**
    * PowerBI REQUEST_QUERY.
    */
   public static final String REQUEST_QUERY = "uri.var.requestQuery";

   /**
    * PowerBI RESULT_OBJECT.
    */
   public static final String RESULT_OBJECT = "uri.var.resultObject";

   /**
    * PowerBI HTTP_METHOD.
    */
   public static final String HTTP_METHOD = "POST";

   /**
    * PowerBI CONTENT_TYPE.
    */
   public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
}
