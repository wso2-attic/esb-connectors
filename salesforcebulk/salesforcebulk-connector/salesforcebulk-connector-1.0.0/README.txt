Product: Integration tests for WSO2 ESB SalesforceBulk connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by following the URL: http://wso2.com/products/enterprise-service-bus/#

 2. If required add the X.509 certificate from https://{instance_name}.salesforce.com to the client-truststore.jks of the ESB located in <ESB_HOME>/repository/resources/security folder
    and wso2carbon.jks located in <SALESFORCEBULK_CONNECTOR_HOME>/salesforcebulk-connector/salesforcebulk-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products.
 
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
	
      Message Formatters :-
   
       <messageFormatter contentType="text/csv" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
       <messageFormatter contentType="zip/xml" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
       <messageFormatter contentType="zip/csv" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
       <messageFormatter contentType="text/xml" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
       <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
       
      Message Builders :-    
   
       <messageBuilder contentType="text/csv" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
       <messageBuilder contentType="zip/xml" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
       <messageBuilder contentType="zip/csv" class="org.wso2.carbon.relay.BinaryRelayBuilder"/> 
       <messageBuilder contentType="text/xml" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
       <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>


 
 4. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "<ESB_CONNECTOR_HOME>/repository/".
 
 5. Make sure that salesforcebulk is specified as a module in ESB Connector Parent pom.
        <module>SalesForceBulk/salesforcebulk-connector/salesforcebulk-connector-1.0.0/org.wso2.carbon.connector</module>
   
 6. Create a salesforcebulk trial account and derive the API Token.
   i)    Using the URL "https://www.salesforce.com/" create a salesforcebulk trial account.
   ii)   Using the URL "https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_understanding_authentication.htm"  Obtain the 'Access Token','client id','client secret' and 'Refresh Token'.
   
 7. Update the salesforcebulk properties file at location "<SALESFORCEBULK_CONNECTOR_HOME>/salesforcebulk-connector/salesforcebulk-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
   i)       apiUrl                -  Use following value 'https://{instance_name}.salesforce.com'
   ii)      accessToken           -  Use the API Key obtained in step 6.
   iii)     apiVersion            -  Use 34.0 or above version.
   iv)      timeout               -  Salesforcebulk API is take some time to proceed the uploaded file. Keep a suitable time gap between create and retrieve calls.
   
   v)       jobFileName           -  Name of the file which is going to upload to create job.
   vi)      jobContentType        -  Content type of the job which is going to create by uploading file.
 
   vii)     batchFileName         -  Name of the file which is going to upload to create batch.
   viii)    CSVJobId              -  Id of a job which has 'CSV' content type.
   
   ix)      description1     	    -  The description of first object of batch.
   x)       name1                 -  The name of first object of batch.
   xi)      description2          -  The description of second object of batch.
   xii)     name2                 -  The name of second object of batch.
  
   
   Special Notes: 
      1)  Add following resources to the ESB registry.
            
            i)    /_system/governance/connectors/SalesforceBulk/apiUrl         -  Use following value 'https://{instance_name}.salesforce.com'
            ii)   /_system/governance/connectors/SalesforceBulk/accessToken    -  Keep the value blank.
            iii)  /_system/governance/connectors/SalesforceBulk/clientId       -  Use the value obtained in Step 6 ii). 
            iv)   /_system/governance/connectors/SalesforceBulk/clientSecret   -  Use the value obtained in Step 6 ii).
            v)    /_system/governance/connectors/SalesforceBulk/apiVersion     -  Use the value obtained in Step 7 iii).
            vi)   /_system/governance/connectors/SalesforceBulk/refreshToken   -  Use the value obtained in Step 6 ii). 
      
      2)  Add files created in step 7 v) and 7 vii) to following location.

            <SALESFORCEBULK_CONNECTOR_HOME>/salesforcebulk-connector/salesforcebulk-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/config/resources/salesforcebulk.
   
 8. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
         $ mvn clean install


