Product: Integration tests for WSO2 ESB Bigquery connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by navigating to the following URL: http://wso2.com/products/enterprise-service-bus/#
 
 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.

 3. Set authorization details:
   i)   Using the URL "https://accounts.google.com/SignUp" create a Google account.
   ii)  Go to "https://developers.google.com/oauthplayground/".
   iii) Authorize BigQuery API from "Select & authorize APIs" by selecting all the scopes available.
   iv)  Then go to "Exchange authorization code for tokens" and click on "Exchange authorization code for token" button and get the access token from "Access token" box (Note down the access token for future use.).
   v)   Go to "https://console.developers.google.com/" and log in with the created google account and create a new project using the dropdown in the top bar(Note down the project Id for future use.). 
   vi)  Enable BigQuery API by navigating to the "APIs" tab which is under "APIs & auth" tab.
   vii) Go to "Credentials" tab which is under "APIs & auth" tab and add credentials by selecting OAuth 2.0 client ID option.( Configure consent screen and then create client ID for 'Web application' type of applications. Note down the redirect uri for future use.)
   viii)Note down the client ID and client secret for future use.
   ix)  Get the authorization code by sending a GET request using url, https://accounts.google.com/o/oauth2/auth?redirect_uri=<redirect_uri>&response_type=code&client_id=<client_ID>&scope=https://www.googleapis.com/auth/bigquery&approval_prompt=force&access_type=offline (Replace <redirect_uri> and <client_ID> with the redirect uri and client ID values noted in step vii and viii. Note down the authorization code for future use.)
   x)  Get the access token and refresh token by sending a POST request to the url https://www.googleapis.com/oauth2/v3/token with x-www-form-urlencoded body with code,client_id,client_secret,redirect_uri values noted before and with grant_type value "authorization_code" (Note down the access token and refresh token for future use.).
   xi)    Add following resources to the ESB registry with the noted values before.

      /_system/governance/connectors/BigQuery/accessToken
      /_system/governance/connectors/BigQuery/apiUrl  
      /_system/governance/connectors/BigQuery/clientId
      /_system/governance/connectors/BigQuery/clientSecret
      /_system/governance/connectors/BigQuery/redirectUrl
      /_system/governance/connectors/BigQuery/refreshToken

 
 4. Make sure that Bigquery is specified as a module in ESB Connector Parent pom.

    <module>bigquery\bigquery-connector\bigquery-connector-1.0.0\org.wso2.carbon.connector</module>
 
 5. Update the Bigquery properties file at location "<BIGQUERY_CONNECTOR_HOME>/bigquery-connector/bigquery-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)   apiUrl             -  The API URL of Bigquery(e.g. https://www.googleapis.com) .
   ii)  projectId          -  A valid Project ID.
   iii) datasetId          -  A valid Dataset ID. 
   iv)  tableId            -  A valid Table ID.
   v)   query              -  A query string, following the BigQuery query syntax, of the query to execute(This should be always 'SELECT count(*) FROM [publicdata:samples.github_nested]').   
   vi)  defaultDatasetId   -  A unique ID for the dataset, without the project name. The ID must contain only letters (a-z, A-Z), numbers (0-9), or underscores (_). The maximum length is 1,024 characters.
   vii) runQueryProjectId  -  Id of the project noted in above step 3(v). 

 6. Make sure ESB is up and running. 

 7. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
     $ mvn clean install
