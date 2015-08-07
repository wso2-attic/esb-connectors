Product: Integration tests for WSO2 ESB BugHerd connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
	- The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

    Tested Platforms: 

	 - Microsoft WINDOWS V-7
	 - UBUNTU 13.04
	 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
	 - Java 1.7
 
Steps to follow in setting integration test.

 1. Download ESB 4.9.0-BETA-SNAPSHOT by following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2.	Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.

STEPS: 
 
 1. Follow the below mentioned steps for adding valid certificate to access bugherd API over https.

	i) Extract the certificate from browser by navigating to 'https://www.bugherd.com/' and place the certificate file in following location.
	   "<BUGHERD_CONNECTOR_HOME>/bugherd/bugherd-connector/bugherd-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/"
			NOTE : Make sure to use an API method endpoint URL to obtain the certificate (e.g.: https://www.bugherd.com/api_v2/projects.json )
	
	  
	ii) Navigate to "<ESB_CONNECTOR_HOME>/bugherd-connector/bugherd-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import Bugherd certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from bugherd, change it accordingly. (e.g.: bugherd.cer)
			   CERT_NAME is name of the certificate. (e.g.: bugherd)
	   
	iii) Go to new WSO2 ESB 4.9.0-BETA-SNAPSHOT folder and place the downloaded certificate in "<ESB_CONNECTOR_HOME>/repository/resources/security/"

	iv) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import Bugherd certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from bugherd, change it accordingly. (e.g.: bugherd.cer)
		       CERT_NAME is name of the certificate. (e.g.: bugherd)

 2. Navigate to location "<ESB_CONNECTOR_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml" and Message Formatters and Message Builders should be added for each of the content types of the files to be added as attachments.
	
	Message Formatters :-
		
        <messageFormatter contentType="image/gif" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
        <messageFormatter contentType="img/gif" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
        <messageFormatter contentType="image/jpeg" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
        <messageFormatter contentType="image/png" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
	    <messageFormatter contentType="text/plain" class="org.apache.axis2.format.PlainTextFormatter"/> 
		<messageFormatter contentType="application/binary" class="org.apache.axis2.transport.http.MultipartFormDataFormatter"/>
		
	Message Builders :-
		
		<messageBuilder contentType="image/gif" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
        <messageBuilder contentType="img/gif" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
        <messageBuilder contentType="image/jpeg" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
        <messageBuilder contentType="image/png" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>  
		<messageBuilder contentType="application/binary" class="org.apache.axis2.builder.MultipartFormDataBuilder"/>
        <messageBuilder contentType="text/plain" class="org.apache.axis2.format.PlainTextBuilder"/>
 
 3. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "<ESB_CONNECTOR_HOME>/repository/".
 
 4. Create a BugHerd account and derive the access token:
	i) 	 Using the URL "https://www.bugherd.com/" create a BugHerd account.
	ii)  Derive the API Key by following the instructions at "http://www.bugherd.com/api_v2".
	iii) Create a new project after creating an account.
	iv)  Create two valid users for userId and assignedToId.

 5. Update the BugHerd properties file at location "<BUGHERD_CONNECTOR_HOME>/bugherd-connector/bugherd-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl - Use the API URL as "https://www.bugherd.com".
	ii) 	apiKey - Use the API Key you got in step 4.
	iii) 	projectId - Use the ID of a valid project created in step 4.
	iv) 	timeOut - Currently Bugherd API has restricted request rate to 1 per 3 seconds. Please make sure timeOut >= 3000 (ms).
	v) 		fileMandatory, fileOptional - Add two files (txt files preferred) to the following location "<BUGHERD_CONNECTOR_HOME>/bugherd-connector/bugherd-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/config/resources/bugherd". Specify their names with extensions (<name>.<ext>) as values (e.g.: fileMandatory=File1.txt, fileOptional=File2.txt).
	vi)     fileContentType -  Change the content type of the files that are used to upload in step (v).
	vii) 	commentMandatoryText - Add some text to be used for adding comment for Project Task.
	viii) 	userId, assignedToId - ID of valid users registered on Bugherd account in step 4 (e.g.: 57285).		
	ix)	    Leave the remaining properties unchanged.
		
 6. Make sure that the BugHerd connector is set as a module in esb-connectors parent pom.
        <module>bugherd/bugherd-connector/bugherd-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
         $ mvn clean install
 NOTE :  
    i)  The created trial account is only valid for 14 days.
    ii) Following BugHerd apiKey, can be used to run the integration tests.
 	    apiKey=zbnljvrokbgqx4hjucgwla
