Product: Integration tests for WSO2 ESB SurveyMonkey connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "Integration_Test/products/esb/4.8.1/modules/distribution/target/"

 2. Make sure "integration-base" project is placed at "Integration_Test/products/esb/4.8.1/modules/integration/"

 3. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/integration-base" and run the following command.
      $ mvn clean install

 4. Add following dependancy to the file "Integration_Test/products/esb/4.8.1/modules/integration/connectors/pom.xml"
	<dependency>

		<groupId>org.wso2.esb</groupId>

		<artifactId>org.wso2.connector.integration.test.base</artifactId>

		<version>4.8.1</version>

		<scope>system</scope>

		<systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>

	</dependency>

 5. Copy SurveyMonkey connector zip file (surveymonkey.zip) to the location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/repository/"

 6. Make sure the surveymonkey test suite is enabled (as given below) and all other test suites are commented in the following file - "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/testng.xml"  
     <test name="SurveyMonkey-Connector-Test" preserve-order="true" verbose="2">
	<packages>

            <package name="org.wso2.carbon.connector.integration.test.surveymonkey"/>

        </packages>
 
    </test>

 7. Create a Survey Monkey developer account using the URL "https://developer.surveymonkey.com/" and store the API key you get after successfully registering the application.

 8. Copy the connector properties file at "surveymonkey-connector-test/src/test/resources/artifacts/ESB/connector/config/surveymonkey.properties" to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config/" and update the copied file as below.

    i) accessToken - Using the Survey Monkey API console (https://developer.surveymonkey.com/api_console), get access token by clicking on "Get Access Token" button and then providing the valid credentials and clicking on the "Authorize" button.

    ii) apiKey - API Key of your registerd application, which is stored in step 7.

 9. Copy the java file "surveymonkey-connector-test/src/test/java/org/wso2/carbon/connector/integration/test/surveymonkey/SurveyMonkeyConnectorIntegrationTest.java" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/java/org/wso2/carbon/connector/integration/test/surveymonkey/"

 10. Copy proxy file "surveymonkey-connector-test/src/test/resources/artifacts/ESB/config/proxies/surveymonkey/surveymonkey.xml" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/proxies/surveymonkey/"

 11. Copy rest request folder "surveymonkey-connector-test/src/test/resources/artifacts/ESB/config/restRequests/surveymonkey" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/restRequests/"

 12. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
      $ mvn clean install

 NOTE : Following Survey Monkey test account with the test data, can be used for run the integration tests.
    Username : ConnectorTest
    Password : connector1234

 NOTE : If you are using a new Servey Monkey account, you need to update all the request files at "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/restRequests/" with valid and relevant data, other than the above mentioned steps. You may use the web UI to create Serveys, Questions etc. and Survey Monkey API console to get the ids and other necessary data.
