Product: Integration tests for WSO2 ESB Basecamp connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is required. This test suite has been configured to download this automatically. 
   However if it fails, download the following project and compile using mvn clean install command to update your local repository.

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{PATH_TO_SOURCE_BUNDLE}/basecamp-connector/basecamp-connector-1.0.0/org.wso2.carbon.connector/repository/"

 2. This ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).

   <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
   
   <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>     


 3. Create a Basecamp account and derive the access token:
	i) 	Using the URL "https://www.basecamp.com/" create a basecamp account.
	ii) Derive the access token by following the instructions at "https://github.com/basecamp/api/blob/master/sections/authentication.md#oauth-2-from-scratch".


 4. Update the basecamp properties file at location "{PATH_TO_SOURCE_BUNDLE}/basecamp-connector/basecamp-connector-1.0.0/org wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
   
		i)    apiUrl        	  - Basecamp API url(https://basecamp.com)
		ii)   accessToken         - Use the access token you got from step 3.ii		
		iii)  accountId		      - Use the account ID retrieved from step 3.i
		iii)  txtFileName         - Name of the attachment(residing under "{PATH_TO_SOURCE_BUNDLE}/basecamp-connector/basecamp-connector-1.0.0/org wso2.carbon.connector/src/test/resources/artifacts/ESB/config/resources/basecamp/" folder) with the extention(attachmentFile.txt).
		iv)   contentType         - Content type of attachment(text/plain).
		v)    sleepTime           - An integer value in milliseconds, to wait between API calls to avoid conflicts at API end. preferred value is 5000. Try increasing this value if test cases fails.
		vi)   uploadContent       - The String which describes the upload("upload content").
		vii)  projectName         - Project Name to create a Project in the testCreateProjectMandatory method in the integration test(sample). 
		viii) optionalProjectName - Project Name to create a Project in the testCreateProjectOptional method in the integration test (sampleOptional).
		
 5. Extract the certificate from browser by navigating to https://basecamp.com and place the certificate file in following locations. 

	i)  "{PATH_TO_SOURCE_BUNDLE}/basecamp-connector/basecamp-connector-1.0.0/org wso2.carbon.connector/src/test/resources/keystores/products"

		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "Basecamp"' in command line to import basecamp certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from basecamp with  the extension, change it accordingly. Remove the copied certificate.
	
	ii) "wso2esb-4.8.1\repository\resources\security"
	
		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "Basecamp"' in command line to import basecamp certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from basecamp with  the extension, change it accordingly. Remove the copied certificate.
		
 6. Navigate to "{PATH_TO_SOURCE_BUNDLE}/basecamp-connector/basecamp-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install


 NOTE : Following Basecamp account, can be used for run the integration tests.
    Username : wso2connector.abdera@gmail.com 
    Password : 1qaz2wsx@
