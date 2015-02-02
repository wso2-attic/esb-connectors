Product: WSO2 ESB Connector for Google Contacts + Integration Tests

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 12.04
 - WSO2 ESB 4.8.1

STEPS:
 

1. To build the connector without running tests from any location, run maven build with the -Dmaven.test.skip=true switch.

2. Before attempting to run integration tests, uncomment the following in pom.xml:

	i)	<parent>
        	<groupId>org.wso2.esb</groupId>
        	<artifactId>esb-integration-tests</artifactId>
        	<version>4.8.1</version>
        	<relativePath>../pom.xml</relativePath>
    	</parent>

    ii) <dependency>
            <groupId>org.wso2.esb</groupId>
            <artifactId>org.wso2.connector.integration.test.base</artifactId>
            <version>4.8.1</version>
            <scope>system</scope>
            <systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>
        </dependency> 

 3. Make sure the ESB 4.8.1 zip file with latest patches available at "Integration_Test/products/esb/4.8.1/modules/distribution/target/"

 4. Make sure "integration-base" project is placed at "Integration_Test/products/esb/4.8.1/modules/integration/"

 5. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/integration-base" and run the following command.
      $ mvn clean install

 6. Add following dependancy to the file "Integration_Test/products/esb/4.8.1/modules/integration/connectors/pom.xml"
	<dependency>

		<groupId>org.wso2.esb</groupId>

		<artifactId>org.wso2.connector.integration.test.base</artifactId>

		<version>4.8.1</version>

		<scope>system</scope>

		<systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>

	</dependency>

 7. Copy GoogleContacts connector zip file (GoogleContacts.zip) to the location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/repository/"

 8. Make sure the GoogleContacts test suite is enabled (as given below) and all other test suites are commented in the following file - "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/testng.xml"  
     <test name="GoogleContacts-Connector-Test" preserve-order="true" verbose="2">
	<packages>

            <package name="org.wso2.carbon.connector.integration.test.googlecontacts"/>

        </packages>
 
    </test>

 9. Creating a Google Cloud Console account: 
	- Go to https://console.developers.google.com/
	- Create a new Google Cloud Console project
	- Go to your newly created project and go to APIs and Auth
	- Enable the Contacts API

 10. Copy the connector properties file at "GoogleContacts-connector-test/src/test/resources/artifacts/ESB/connector/config/googlecontacts.properties" to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config/" and update the copied file as below.

    i) accessToken: Generate an access token through https://developers.google.com/oauthplayground/ by authenticating the Contacts API
    ii) If you wish to generate an accessToken manually;
		- clientId: Obtain from Google Cloud Console project, APIs & auth->Credentials->Client for Web Applications->Client ID
		- clientSecret: Obtain from Google Cloud Console project, APIs & auth->Credentials->Client for Web Applications->Client secret
		- refreshToken: Generate a refresh token through the following:
			* Send a GET request to https://accounts.google.com/o/oauth2/auth?redirect_uri={YOUR_REDIRECT_URI}&response_type=code&client_id={YOUR_CLIENT_ID}&scope=https://www.googleapis.com/auth/drive&approval_prompt=force&access_type=offline
			* Click on the 'Allow' button and pick up the 'code' from the address bar of the browser after redirect.
			* Send a POST request with the content type application/x-www-form-urlencoded to  https://accounts.google.com/o/oauth2/token with; 
				- code={YOUR_AUTHORIZATION_CODE}
				- redirect_uri={YOUR_REDIRECT_URI}
				- client_id={YOUR_CLIENT_ID}
				- client_secrent={YOUR_CLIENT_SECRET}
				- grant_type=authorization_code


11. Copy the java file "googlecontacts-connector-test/src/test/java/org/wso2/carbon/connector/integration/test/googlecontacts/GoogleContactsConnectorIntegrationTest.java" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/java/org/wso2/carbon/connector/integration/test/googlecontacts/"

 12. Copy proxy file "googlecontacts-connector-test/src/test/resources/artifacts/ESB/config/proxies/googlecontacts/googlecontacts.xml" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/proxies/googlecontacts/"

 13. Copy contents of request folder "GoogleContacts-connector-test/src/test/resources/artifacts/ESB/config/requests/googlecontacts" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/requests/"

 14. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
      $ mvn clean install

 NOTE : Following Google Contacts test account can be used for run the integration tests.
 Username : testgooglecontact
 Password : 1qaz2wsx@
