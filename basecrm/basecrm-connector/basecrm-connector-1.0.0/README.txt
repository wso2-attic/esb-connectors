Product: Integration tests for WSO2 ESB BaseCRM connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04, Mac OSx 10.9
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-ALPHA by navigating the following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/.

 2. The ESB should be configured as below.
    Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).

        Message Formatter :-
        <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

        Message Builder :-
        <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
 3. Follow the below mentioned steps for adding valid certificate to access BaseCRM API over https.

    i) 	 Extract the certificate from browser(Mozilla Firefox) by navigating to https://app.futuresimple.com/sales

    ii)  Go to new ESB folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/" and "<BASECRM_CONNECTOR_HOME>/basecrm-connector/basecrm-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products"

    iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

         keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

         This command will import BaseCRM certificate into keystore. 
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

		 Navigate to "<BASECRM_CONNECTOR_HOME>/basecrm-connector/basecrm-connector-2.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import BaseCRM certificate in to keystore.
         Give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from BaseCRM with the extension. (e.g. BaseCRM.crt)
               CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. BaseCRM)

 4. Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 5. Make sure that basecrm is specified as a module in ESB_Connector_Parent pom.
       <module>basecrm/basecrm-connector/basecrm-connector-2.0.0/org.wso2.carbon.connector</module>

 6. Prerequisites for BaseCRM Connector Integration Testing
      Create a BaseCRM account using the URL "https://getbase.com/pricing/".
      Note: This is a full featured 14-day free trial account.

 7. Update the BaseCRM properties file at location "{BaseCRM_Connector_Home}/basecrm-connector/basecrm-connector-2.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

    i)   responseType   - Use "json" as the response type since the integration is implemented for JSON results even though the API support "json" and "xml" response types.
    ii)  apiUrl         - Use the sales API URL as "https://sales.futuresimple.com".
    iii) leadServiceUrl - Use the lead service API URL as "https://leads.futuresimple.com".
    iv)  email          - Use the BaseCRM account created email.
    v)   password       - Use the BaseCRM account password.

 8. Navigate to "{ESB_Connector_Home}/" and run the following command.
       $ mvn clean install
