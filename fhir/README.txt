Product: Integration tests for WSO2 ESB FHIR connector

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

 1. Download ESB WSO2 ESB 4.9.0 by navigating the following the URL: http://wso2.com/products/enterprise-service-bus/.
 
 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.

 3. Go to FHIR root URL and then  Extract the certificate from browser and place the certificate file in following locations.

   i)    "<FHIR_CONNECTOR_HOME>/fhir-connector/fhir-connector-1.0.0/org wso2.carbon.connector/src/test/resources/keystores/products"

      Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "fhircert"' in command line to import FHIR certificate in to keystore. Give "wso2carbon" as password.
      NOTE : CERT_FILE_NAME is the file name which was extracted from FHIR with  the extension, change it accordingly. Remove the copied certificate.

   ii)   "<ESB_HOME>/repository/resources/security"
   
      Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "fhircert"' in command line to import FHIR certificate in to keystore. Give "wso2carbon" as password.
      NOTE : CERT_FILE_NAME is the file name which was extracted from FHIR with  the extension, change it accordingly. Remove the copied certificate.


 4. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file into location "{ESB_CONNECTOR_HOME}/repository/".

 5. Make sure that fhir is specified as a module in ESB_Connector_Parent pom.
   <module>fhir/fhir-connector/fhir-connector-1.0.0/org.wso2.carbon.connector</module>

 
 6. Update the fhir properties file at location "<FHIR_CONNECTOR_HOME>/fhir-connector/fhir-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)    base                       - The service root URL
   ii)   type                       - The name of the resource type
   iii)  format                     - The format of the response
   iv)   id                         - The id of an instance of a particular resource type
   v)    logicalId                  - The logical Id of the resource
   vi)   versionId                  - The version Id of the resource
   vii)  idForHistory               - The particular Id to retrieve the history

 7. Navigate to "{ESB_CONNECTOR_HOME}/" and run the following command.
      $ mvn clean install
