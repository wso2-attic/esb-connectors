Product: Integration tests for WSO2 ESB Hubspot connector

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

 3. Create a Hubspot free account and login using "http://www.hubspot.com/crm".
   i) Click on the user avatar on the top right corner and from the top down select the 'Integration' option.
   ii) On the Integrations page, select the option "Get your HubSpot API Key" and generate the API key for further use. 

 4. Navigate to "https://api.hubapi.com" and extract the certificate from browser and place the certificate file in following location. 

   i)    "<HUBSPOT_CONNECTOR_HOME>/hubspot-connector/hubspot-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products"

      Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "hubspot"' in command line to import Hubspot certificate in to keystore. Give "wso2carbon" as password.
      NOTE : CERT_FILE_NAME is the file name which was extracted from Hubspot with  the extension, change it accordingly. Remove the copied certificate.
 
 5. Compress ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".

 6. Make sure that hubspot is specified as a module in ESB_Connector_Parent pom.
   <module>hubspot/hubspot-connector/hubspot-connector-1.0.0/org.wso2.carbon.connector</module>
 
 7. Update the hubspot properties file at location "<HUBSPOT_CONNECTOR_HOME>/hubspot-connector/hubspot-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
      
   i)    apiUrl                     -  The API URL of Hubspot(e.g. https://api.hubapi.com).
   ii)   apiKey                     -  Use the API Key obtained under step 3 ii).
   iii)  contactFirstName           -  String value for the contact's first name.
   iv)   contactLastName            -  String value for the contact's last name.
   v)    contactWebsite             -  Website address for the contact's website.
   vi)   contactPhone               -  String value for the contact's phone number.
   vii)  companyName                -  String value for the company name.
   viii) companyDescription         -  String value for the company description.
   ix)   companyCountry             -  String value for the company's country.
   x)    companyCity                -  String value for the company's city.
   xi)   companyWebsite             -  Website address for the company's website.
   xii)  dealName                   -  String value for the deal name.
   xiii) dealAmount                 -  Integer value for deal amount.
   xiv)  timeout                    -  Integer value for timeout(default value is 15000).


 8. Navigate to "{ESB_CONNECTOR_HOME}/" and run the following command.
      $ mvn clean install
