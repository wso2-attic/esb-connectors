Product: Integration tests for WSO2 ESB 123ContactForm connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB WSO2 ESB 4.9.0-BETA-SNAPSHOT by navigating the following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/.
 
 2. Deploy relevant patches, if applicable.

 3. Create a 123ContactForm free account and login using "https://www.123contactform.com/index.php?p=login".
   i) Navigate to "http://www.123contactform.com/index.php?p=myaccount" and create a API Key.

 4. After login to 123ContactForm accout Extract the certificate from browser and place the certificate file in following locations. 

   i)    "<123CONTACTFORM_CONNECTOR_HOME>/contactform-connector/contactform-connector-1.0.0/org wso2.carbon.connector/src/test/resources/keystores/products"

      Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "contactform"' in command line to import 123ContactForm certificate in to keystore. Give "wso2carbon" as password.
      NOTE : CERT_FILE_NAME is the file name which was extracted from 123ContactForm with  the extension, change it accordingly. Remove the copied certificate.

   ii)   "<ESB_HOME>/repository/resources/security"
   
      Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "contactform"' in command line to import 123ContactForm certificate in to keystore. Give "wso2carbon" as password.
      NOTE : CERT_FILE_NAME is the file name which was extracted from 123ContactForm with  the extension, change it accordingly. Remove the copied certificate.
 
 5. ESB should be configured as below.
   Please make sure that the below mentioned Axis configurations are enabled (<ESB_HOME>/repository/conf/axis2/axis2.xml).

      <messageBuilder contentType="application/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

      <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

 6. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file into location "{ESB_CONNECTOR_HOME}/repository/".

 7. Make sure that contactform is specified as a module in ESB_Connector_Parent pom.
   <module>123contactform/contactform-connector/contactform-connector-1.0.0/org.wso2.carbon.connector</module> 

 8. Prerequisites for 123ContactForm Connector Integration Testing  

   i)    Navigate to "http://www.123contactform.com/index.php?p=dashboard", create a new form with multiple fields (at least with 2 fields).
   ii)   Add at least 27 submissions for created form in step 8 i).
 
 9. Update the 123ContactForm properties file at location "<123CONTACTFORM_CONNECTOR_HOME>/contactform-connector/contactform-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
      
   i)    apiUrl                     -  The API URL of 123ContactForm(e.g. https://www.123contactform.com).
   ii)   apiKey                     -  Use the API Key obtained under step 3 i).
   iii)  formId                     -  ID of the form which is created in step 8 i).

 10. Navigate to "{ESB_CONNECTOR_HOME}/" and run the following command.
      $ mvn clean install
