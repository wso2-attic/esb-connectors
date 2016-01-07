Product: Integration tests for WSO2 ESB PowerBI connector

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
      Please make sure that the below mentioned Axis configurations are enabled(<ESB_HOME>/repository/conf/axis2/axis2.xml).

         <messageFormatter contentType="application/octet-stream" class="org.apache.axis2.format.BinaryFormatter"/>
         <messageBuilder contentType="application/octet-stream" class="org.apache.axis2.format.BinaryBuilder"/>


 3. Set authorization details:
   i)   Create a Office 365 E3 trial account.
   ii)  Navigate to the URL "https://login.microsoftonline.com/login.srf" and login with Microsoft account which is created in step 3.i .
   iii) Navigate to the URL "https://powerbi.microsoft.com/en-us/" and signup for a Power Bi account using the previously created Microsoft account. 
   iv)  Create a dataset and keep the dataset name for further use.
   v)   Enable PowerBI pro by using the popup by selecting the "Create Group" option.
   vi)  Create a group in PowerBI.
   vii) Navigate to the URL "https://app.powerbi.com/apps" 
            Step 1 - Login using Power BI account which used in step 3.ii .
            Step 2 - Provide App Name, Redirect URL, App Type and Home Page URL and for App Type use "Server-side Web app". Note Redirect URL for further use.  
            Step 3 - select all the check boxes to enable access for the api.
            Step 4 - Click "Register App" and note client-id and client-secret that are returned for further use.
   viii)Generate the authorization code by sending a GET request using url "https://login.windows.net/common/oauth2/authorize?response_type=code&client_id=<client-id>&resource=https://analysis.windows.net/powerbi/api&redirect_uri=<Redirect URL>".
   
   ix)  Add following resources to the ESB registry and add the values for clientId, clientSecret, apiUrl and redirectUrl.

      /_system/governance/connectors/PowerBI/accessToken
      /_system/governance/connectors/PowerBI/apiUrl  
      /_system/governance/connectors/PowerBI/clientId
      /_system/governance/connectors/PowerBI/clientSecret
      /_system/governance/connectors/PowerBI/redirectUrl
      /_system/governance/connectors/PowerBI/refreshToken

 
 4. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".
 
 5. Make sure that PowerBI is specified as a module in ESB Connector Parent pom.

    <module>powerbi/powerbi-connector/powerbi-connector-1.0.0/org.wso2.carbon.connector</module>
 
 6. Update the powerbi properties file at location "<POWERBI_CONNECTOR_HOME>/powerbi-connector/powerbi-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)   apiUrl             -  The API URL of Power BI (e.g. https://api.powerbi.com)
   ii)  authorizationCode  -  Authorization code for genarating the access token which is genarated in step (3. viii).
   iii) datasetName        -  Dataset name which is created in step (3. iv).

 7. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
     $ mvn clean install
