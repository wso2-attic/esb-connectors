Product: Integration tests for WSO2 ESB BaseCRM connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

Steps to follow in setting integration test.

 1. Download ESB 4.8.1 from official website.
 
 2. Deploy relevant patches, if applicable.
 
 3. The ESB should be configured as below.
    Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).

        Message Formatter :-
        <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

        Message Builder :-
        <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
 4. Follow the below mentioned steps for adding valid certificate to access BaseCRM API over https.

    i) 	 Extract the certificate from browser(Mozilla Firefox) by navigating to https://app.futuresimple.com/sales

    ii)  Go to new ESB 4.8.1 folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/"

    iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

            keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

         This command will import BaseCRM certificate into keystore. 
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from BaseCRM with the extension. (e.g. BaseCRM.crt)
               CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. BaseCRM)

 5. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{BaseCRM_Connector_Home}/basecrm-connector/basecrm-connector-2.0.0/org.wso2.carbon.connector/repository/".

 6. Prerequisites for BaseCRM Connector Integration Testing
      Create a BaseCRM account using the URL "https://getbase.com/pricing/".
      Note: This is a full featured 14-day free trial account.

 7. Update the BaseCRM properties file at location "{BaseCRM_Connector_Home}/basecrm-connector/basecrm-connector-2.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

    i)   responseType   - Use "json" as the response type since the integration is implemented for JSON results even though the API support "json" and "xml" response types.
    ii)  apiUrl         - Use the sales API URL as "https://sales.futuresimple.com".
    iii) leadServiceUrl - Use the lead service API URL as "https://leads.futuresimple.com".
    iv)  email          - Use the BaseCRM account created email.
    v)   password       - Use the BaseCRM account password.

 8. Navigate to "{BaseCRM_Connector_Home}/basecrm-connector/basecrm-connector-2.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
