Product: Integration tests for WSO2 ESB GoogleCalendar connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "Integration_Test/products/esb/4.8.1/modules/distribution/target/"

 2. This ESB should be configured as below;
	In Axis configurations (\repository\conf\axis2\axis2.xml).

   i) Enable message formatter for "text/html" in messageFormatters tag
			<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

   ii) Enable message builder for "text/html" in messageBuilders tag
			<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

   iii) Enable message formatter for "application/json" in messageFormatters tag
			<messageFormatter contentType=”application/json” class=”org.apache.synapse.commons.json.JsonStreamFormatter”/>

   iv) Enable message builder for "application/json" in messageBuilders tag
			<messageBuilder contentType=”application/json” class=”org.apache.synapse.commons.json.JsonStreamBuilder”/>

   V) Install HTTP PATCH request enabling patch and Json patch to ESB 4.8.1
		patch0804 - http PATCH request patch
		patch0800 - Json string escape ("\") character patch


 3. Make sure "integration-base" project is placed at "Integration_Test/products/esb/4.8.1/modules/integration/"

 4. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/integration-base" and run the following command.
      $ mvn clean install

 5. Add following dependancy to the file "Integration_Test/products/esb/4.8.1/modules/integration/connectors/pom.xml"
	<dependency>

		<groupId>org.wso2.esb</groupId>

		<artifactId>org.wso2.connector.integration.test.base</artifactId>

		<version>4.8.1</version>

		<scope>system</scope>

		<systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>

	</dependency>

 6. Copy GoogleCalendar connector zip file (googlecalendar.zip) to the location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/repository/"

 7. Make sure the googlecalendar test suite is enabled (as given below) and all other test suites are commented in the following file - "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/testng.xml"  
     <test name="GoogleCalendar-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.googlecalendar"/>
        </packages>
    </test>

 8. Create a Google account and enable Google Calendar API:
	i) 	Using the URL "https://accounts.google.com/SignUp" create a Google account.
	ii) 	Go to "https://developers.google.com/oauthplayground/".
	iii) 	Authorize Google-Calendar API from "Select & authorize APIs" by selecting "https://www.googleapis.com/auth/calendar".
	iv) 	Then go to "Exchange authorization code for tokens" and click on "get authorization code for token" button and get the access token from "Access token" box.

 9. Copy the connector properties file at "googlecalendar-connector-test/src/test/resources/artifacts/ESB/connector/config/googlecalendar.properties" to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config/" and update the copied file as below.

    i) accessToken - Use the access token you got from step 8.

    ii) emailAddress - Email address of the created Google account in step 8.

 10. Copy the java file "googlecalendar-connector-test/src/test/java/org/wso2/carbon/connector/integration/test/googlecalendar/GoogleCalendarConnectorIntegrationTest.java" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/java/org/wso2/carbon/connector/integration/test/googlecalendar/"

 11. Copy proxy file "googlecalendar-connector-test/src/test/resources/artifacts/ESB/config/proxies/googlecalendar/googlecalendar.xml" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/proxies/googlecalendar/"

 12. Copy rest request folder "googlecalendar-connector-test/src/test/resources/artifacts/ESB/config/restRequests/googlecalendar" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/restRequests/"

 13. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
      $ mvn clean install

 NOTE : Following Google account, can be used for run the integration tests.
    Username : wso2gcalendar@gmail.com
    Password : connector1234
