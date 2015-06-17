Product: Integration tests for WSO2 ESB VerticalResponse connector
==================================================================

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 14.04
 - WSO2 ESB 4.9.0-Alpha

STEPS:

			    
 1. Create a VerticalResponse account and derive the access token by following the steps below:

	i)  Navigate to URL "http://developer.verticalresponse.com/member/register".

	ii) Provide your details and create/register a VerticalResponse Developer account.

	iii) Activate your account by clicking on the link in the email, sent by VerticalResponse, to the email address provided while registration.

	iv)	Now go to URL "https://secure.mashery.com/login/developer.verticalresponse.com/" and login with your credentials.

	v)	Click on "Get an API Key"

	vi)	Click on Applications => CREATE A NEW APPLICATION

	vii) Provide an Application Name, Description, Company Name, Category = CRM, Select 'Issue a new key for Vertical Response API and I agree to the terms of service' and register you Application.

	viii)	On the response page, Save the following for future use:
		- Key or ID
		- Secret

    ix) Navigate to this URL https://vr2.verticalresponse.com/users/sign_up and enter the email, password given in step (ii) and sign up. 

	x) Then navigate to "http://developer.verticalresponse.com/io-docs"
		and select the correct “Existing Client” and provide the Client ID/Key, Secret used in step (viii) and click 'Authorize'.

	xi)	You'll be prompted to Login. Login with the credentials provided in step (ix).

	xii) You'll be given a Authorization Code. Click on the 'Get Access Token'.

	xiii) Store the Access Token in a safe place for future use (The Access Token doesn't expire and can be used indefinitely).

 
	
  2. Follow the below mentioned steps for adding valid certificate to access VerticalResponse API over https

	i) Extract the certificate from browser by navigating to https://vrapi.verticalresponse.com/api/v1 and place the certificate file in following location.
	   "{VERTICALRESPONSE_CONNECTOR_HOME}/vertialresponse-connector/vertialresponse-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/"
	  
	ii) Navigate to "{VERTICALRESPONSE_CONNECTOR_HOME}/vertialresponse-connector/vertialresponse-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import VerticalResponse certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from verticalrespose, change it accordingly. (e.g. vrapi.verticalresponse.com)
			   CERT_NAME is name of the certificate. (e.g. VResp)
	   
	iii) Goto new ESB 4.9.0-Alpha folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/"

	iv) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import VerticalResponse certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from verticalrespose, change it accordingly. (e.g. vrapi.verticalresponse.com)
			   CERT_NAME is name of the certificate. (e.g. VResp)
	
  7. Create and add a zip file of ESB 4.9.0-Alpha with applied keystores in step 2 (iii) and latest patches to the location: "{VERTICALRESPONSE_CONNECTOR_HOME}/vertialresponse-connector/vertialresponse-connector-1.0.0/org.wso2.carbon.connector/repository/"

  8. Update the 'verticalresponse.properties' file at the location "{VERTICALRESPONSE_CONNECTOR_HOME}/vertialresponse-connector/vertialresponse-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
   	 - accessToken=Use the access token you got from step 1->(xii)

  9. Make sure per each integration test execution parameters (email, emailOptional, listName and listNameOptional) should be changed in verticalresponse.properties file.

  10. Make sure that the Vertialresponse connector is set as a module in esb-connectors parent pom.
              <module>vertialresponse/vertialresponse-connector/vertialresponse-connector-1.0.0/org.wso2.carbon.connector</module>
      Navigate to "esb-connectors" and run the following command.
      $ mvn clean install


 
