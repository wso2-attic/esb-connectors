Product: Integration tests for WSO2 ESB Basecamp connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. 
   If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-ALPHA

STEPS:
  
 1. Extract the certificate from browser by navigating to https://basecamp.com and place the certificate file in following locations. 

	i)  "<ESB_Connector_Home>/basecamp/basecamp-connector/basecamp-connector-1.0.0/org wso2.carbon.connector/src/test/resources/keystores/products"

		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "Basecamp"' in command line to import basecamp certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from basecamp with  the extension, change it accordingly. Remove the copied certificate.
	
	ii) "WSO2 ESB 4.9.0-ALPHA/repository/resources/security"
	
		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "Basecamp"' in command line to import basecamp certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from basecamp with  the extension, change it accordingly. Remove the copied certificate. 
		
 2. Place the WSO2 ESB 4.9.0-ALPHA zip file with the applied changes in step 1->(ii) and the latest patches at "<Basecamp_Connector_Home>/repository/".

 3. Ensure that the below mentioned Axis configurations are enabled in the ESB.(/repository/conf/axis2/axis2.xml).

   <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/> 
   <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/> 
   
 4. Create a Basecamp account and derive the access token:
	i) 	Using the URL "https://www.basecamp.com/" create a basecamp account.
	ii) Derive the access token by following the instructions at "https://github.com/basecamp/api/blob/master/sections/authentication.md#oauth-2-from-scratch".


 5. Update the Bacecamp properties file at location "<ESB_Connector_Home>/basecamp/basecamp-connector/basecamp-connector-1.0.0/org wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
		i)    apiUrl        	  - Basecamp API url(https://basecamp.com)
		ii)   accessToken         - Use the access token you got from step 4.ii		
		iii)  accountId		      - Use the account ID retrieved from step 4.i
		iii)  txtFileName         - Name of the attachment(residing under <ESB_Connector_Home>/basecamp/basecamp-connector/basecamp-connector-1.0.0/org wso2.carbon.connector/src/test/resources/artifacts/ESB/config/resources/basecamp folder) with the extention(attachmentFile.txt).
		iv)   contentType         - Content type of attachment(text/plain).
		v)    sleepTime           - An integer value in milliseconds, to wait between API calls to avoid conflicts at API end. preferred value is 5000. Try increasing this value if test cases fails.
		vi)   uploadContent       - The String which describes the upload("upload content").
		vii)  projectName         - Project Name to create a Project in the testCreateProjectMandatory method in the integration test(sample). 
		viii) optionalProjectName - Project Name to create a Project in the testCreateProjectOptional method in the integration test (sampleOptional).
			
 6. Make sure that the Bacecamp connector is set as a module in esb-connectors parent pom.
            <module>basecamp/basecamp-connector/basecamp-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Navigate to "<ESB_Connector_Home>/" and run the following command.
      $ mvn clean install


 NOTE : Following Basecamp account, can be used for run the integration tests.
    Username : wso2connector.abdera@gmail.com 
    Password : 1qaz2wsx@
