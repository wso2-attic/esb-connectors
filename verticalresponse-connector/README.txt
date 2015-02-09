Product: Integration tests for WSO2 ESB VerticalResponse connector
==================================================================

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:
			    
 1. Create a VerticalResponse account and derive the access token by following the steps below:

	i)  Navigate to URL "http://developer.verticalresponse.com/member/register".

	ii) Provide your details and create/register a VerticalResponse Developer account.

	iii) Activate your account by clicking on the link in the email, sent by VerticalResponse, to the email address provided while registration.

	iV)	Now go to URL "https://secure.mashery.com/login/developer.verticalresponse.com/" and login with your credentials.

	V)	Click on "Get an API Key"

	VI)	Click on Applications => CREATE A NEW APPLICATION

	VII) Provide an Application Name, Description, Company Name, Category = CRM, Select 'Issue a new key for Vertical Response API and I agree to the terms of service' and register you Application.

	VIII)	On the response page, Save the following for future use:
		- Key or ID
		- Secret

    IX) Navigate to this URL https://vr2.verticalresponse.com/users/sign_up and enter the email, password given in step (ii) and sign up. 

	X) Then navigate to "http://developer.verticalresponse.com/io-docs"		and select the correct “Existing Client” and provide the Client ID/Key, Secret used in step (VIII) and click 'Authorize'.

	XII)	You'll be prompted to Login. Login with the credentials provided in step (IX).

	XIII) You'll be given a Authorization Code. Click on the 'Get Access Token'.

	XIV) Store the Access Token in a safe place for future use (The Access Token doesn't expire and can be used indefinitely).

 2. Make sure the latest "integration-base" project is placed at "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/"

 3. Navigate to "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/integration-base" and run the following command.
      	$ mvn clean install

 4. Copy the 'src' folder and pom.xml from the provided VerticalResponse connector source (org.wso2.carbon.connector) to the location "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/connectors/”.  When running integration tests, uncomment the following code snippet in pom.xml and save the changes:

	<parent>
	    <groupId>org.wso2.esb</groupId>
	    <artifactId>esb-integration-tests</artifactId>
	    <version>4.8.1</version>
	    <relativePath>../pom.xml</relativePath>
	</parent> 
	<dependency>
	    <groupId>org.wso2.esb</groupId>
	    <artifactId>org.wso2.connector.integration.test.base</artifactId>
	    <version>4.8.1</version>
	    <scope>system</scope>
	    <systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>
	</dependency>

  5. Make sure the VerticalResponse test suite is enabled (as given below) and all other test suites are commented out in the following file: "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/connectors/src/test/resources/testng.xml"
	
	<test name="VerticalResponse-Connector-Test" preserve-order="true" verbose="2">
	       <packages>
				<package name="org.wso2.carbon.connector.integration.test.verticalresponse"/>
	       </packages>
	</test>
	
  6. Follow the below mentioned steps for adding valid certificate to access VerticalResponse API over https

	i) Extract the certificate from browser by navigating to https://vrapi.verticalresponse.com/api/v1 and place the certificate file in following location.
	   "<Integration_Test>/products/esb/4.8.1/modules/integration/connectors/src/test/resources/keystores/products/"
	  
	ii) Navigate to "<Integration_Test>/products/esb/4.8.1/modules/integration/connectors/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import VerticalResponse certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from verticalrespose, change it accordingly. (e.g. vrapi.verticalresponse.com)
			   CERT_NAME is name of the certificate. (e.g. VResp)
	   
	iii) Goto new ESB 4.8.1 folder and place the downloaded certificate in "<ESB_Home>/repository/resources/security/"

	iv) Navigate to "<ESB_Home>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import VerticalResponse certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from verticalrespose, change it accordingly. (e.g. vrapi.verticalresponse.com)
			   CERT_NAME is name of the certificate. (e.g. VResp)
	
  7. Create and add a zip file of ESB 4.8.1 with applied keystores in step (iii) and latest patches to the location: "Integration_Test/products/esb/4.8.1/modules/integration/connectors/repository"

  8. Update the 'verticalresponse.properties' file at the location "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config" as below.
   	 - accessToken=Use the access token you got from step 1->(XIV)

  9. Make sure per each integration test execution parameters (email, emailOptional, listName and listNameOptional) should be changed in verticalresponse.properties file.

  10.Navigate to "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
      	$ mvn clean install


 
