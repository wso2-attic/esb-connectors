Product: Integration tests for WSO2 ESB Salesforce connector
Pre-requisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform: 

- UBUNTU 14.04
- WSO2 ESB wso2esb-4.8.1
- Java 1.6

STEPS:

1. Make sure the ESB 4.8.1 zip file at "/repository/".

2. Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following file - "src/test/resources/testng.xml"

    <test name="Salesforce-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.salesforce"/>
        </packages>
    </test> 

3. Copy proxy files to following location "src/test/resources/artifacts/ESB/config/proxies/salesforce/"

4. Edit the "salesforce.properties" at src/test/resources/artifacts/connector/config/ using valid and relevant data. Parameters must be changed to run the integration test.

	Required to change on every test run :
 	deleteObjectId
	undeleteObjectId
	emptyRecycleBinId

5. Navigate to "salesforce-connector-1.0.1/" and run the following command.
     $ mvn clean install
