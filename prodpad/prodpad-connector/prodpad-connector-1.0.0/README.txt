Product: Integration tests for WSO2 ESB Prodpad connector

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
 
 2.	Deploy relevant patches, if applicable.

 3. Extract the certificate from browser by navigating to "https://api.prodpad.com" and place the certificate file in following locations. 

	i)  "<PRODPAD_CONNECTOR_HOME>/prodpad-connector/prodpad-connector-1.0.0/org wso2.carbon.connector/src/test/resources/keystores/products"

		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "prodpad"' in command line to import prodpad certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from prodpad with  the extension, change it accordingly. Remove the copied certificate.
	
	ii) "<ESB_HOME>/repository/resources/security"
	
		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "prodpad"' in command line to import prodpad certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from prodpad with  the extension, change it accordingly. Remove the copied certificate.
 
 4. ESB should be configured as below.
	 Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml). 
 
		<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		
		<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 5. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".

 6. Make sure that prodpad is specified as a module in ESB_Connector_Parent pom.
 	<module>prodpad/prodpad-connector/prodpad-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Create a Prodpad trial account and login.
	i) 	Go to your user profile setting page. You can find this on the upper right corner after logging in.
	ii)	Then navigate to API Key tab and generate a API key.
	
 8. Prerequisites for Prodpad Connector Integration Testing

	i) 	Navigate to "DASHBOARD -> USER PERSONAS", create atleast one user persona.
	ii) Navigate to "DASHBOARD -> USER PRODUCTS", create product line and atleast one product.
 
 9. Update the Prodpad properties file at location "<PRODPAD_CONNECTOR_HOME>/prodpad-connector/prodpad-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL of Prodpad(e.g. https://api.prodpad.com).
	ii) 	apiKey							-   Use the API  token obtained under step 7 ii).
	iii)	title							-   A String value for the idea title to create an Idea.
	iv)		comment							-	A String value for the idea comment.
	v)		userStory						-	A String value for the user story to create an User Story.

 10. Navigate to "{ESB_CONNECTOR_HOME}/" and run the following command.
      $ mvn clean install

		