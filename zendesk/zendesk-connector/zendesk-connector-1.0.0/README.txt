Product: Integration tests for WSO2 ESB Zendesk connector

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

 1. Create a Zendesk account and get user credentials according to below steps:

	i)  Navigate to URL "https://www.zendesk.com/register#getstarted" and create a trial account. 
	    (Save the given username[email address], password.)

	ii) Once you registered it automatically navigates to "https://{company}.zendesk.com/agent/launchpad/configureEmailV2".
		(Note the apiURl for further use. "https://{company}.zendesk.com" )
	iii) When you send the request you have to save username, password and API URL.     
 
 2. Download ESB WSO2 ESB 4.9.0-BETA-SNAPSHOT by navigating the following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/.
 
 3.	Deploy relevant patches, if applicable.
 
 4. Follow the below mentioned steps for adding valid certificate to access Zendesk API over https

	i) Extract the certificate from browser by navigating to apiUrl[Step 1->(iii)] and place the certificate file in following location.
	   "{ZENDESK_CONNECTOR_HOME}/zendesk-connector/zendesk-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/"
	  
	ii) Navigate to "{ZENDESK_CONNECTOR_HOME}/zendesk-connector/zendesk-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import Zendesk certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from zendesk, change it accordingly. (e.g. -.zendesk.com)
			   CERT_NAME is name of the certificate. (e.g. zendesk)
	   
	iii) Go to ESB 4.9.0-BETA-SNAPSHOT folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/"

	iv) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import Zendesk certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from zendesk, change it accordingly. (e.g. -.zendesk.com)
			   CERT_NAME is name of the certificate. (e.g. zendesk)
			   
 5. Add the files which is used for upload to the following location and update the values of the properties step 9 iv, v, vi, vii corresponding to the added files.
    Location: "{ZENDESK_CONNECTOR_HOME}/zendesk-connector/zendesk-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/config/resources/zendesk/"
 
 6. ESB should be configured according to the files which are been uploaded.
		<messageBuilder contentType="image/gif" class="org.wso2.carbon.relay.BinaryRelayBuilder" />
		<messageFormatter contentType="image/gif" class="org.wso2.carbon.relay.ExpandingMessageFormatter" />
		
 7. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 8. Make sure that Zendesk is specified as a module in ESB_Connector_Parent pom.
 	<module>Zendesk/zendesk-connector/zendesk-connector-1.0.0/org.wso2.carbon.connector</module>	
 
 9. Update the 'zendesk.properties' file at the location "{ZENDESK_CONNECTOR_HOME}/zendesk-connector/zendesk-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
   	 i) 	apiUrl						-		Use the api url, step 1->(iii)
	 ii) 	username					-		Use the username, step 1->(iii)
	 iii) 	password					-		Use the password, step 1->(iii)
	 iv) 	fileName					-		The file name of the file to upload with mandatory parameters.
	 v) 	fileContentType				-		The content type of the file upload with mandatory parameters.
	 vi) 	fileNameOptional			-		The file name of the file upload with optional parameters.
	 vii) 	fileContentTypeOptional		-		The content type of the file upload with optional parameters.
	 viii) 	invalidApiUrl				-		An invalid api Url for apiUrl to list tickets and list tickets for recent with negative parameters.
	 ix) 	query						-		A query String for query to search tickets with mandatory/optional and negative parameters.
	 x) 	componentType				-		A valid String value for the component type(tickets,topics,organizations,users) to add tags with mandatory/optional and negative parameters.
	 xi) 	tag							-		A String for tag to add tags with optional and negative parameters.
	 xii) 	timeOut						-		The time in milliseconds.
	 xiii)	dueAt						-		The task due date (format: yyyy-mm-dd) to create ticket with optional parameters.
	 xvi)	dueAtOptional				-		The task due date (format: yyyy-mm-dd) to update ticket with optional parameters.
	 
 10. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install