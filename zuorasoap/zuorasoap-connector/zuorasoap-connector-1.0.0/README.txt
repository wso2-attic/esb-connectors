Product: Integration tests for WSO2 ESB Zuora SOAP connector

Pre-requisites:

 - Maven 3.x
 - Java 1.8
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:
 - UBUNTU 14.04
 - WSO2 ESB 5.0.0-M1



Steps to follow in setting integration test.

 1. Download ESB WSO2 ESB 5.0.0-M1 from official website.

 2. Create a Zuora account.

 3. Update the Zuora properties file at location "{ESB_Connector_Home}/zuorasoap/zuorasoap-connector/zuorasoap-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL .
	ii) 	username						-   The username of the Zuora account.
	iii)	password						- 	The password of the Zuora account.

 4. Make sure that zuorasoap is specified as a module in ESB_Connector_Parent pom.
         <module>/zuorasoap/zuorasoap-connector/zuorasoap-connector-1.0.0/org.wso2.carbon.connector/</module>

 5. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install


		