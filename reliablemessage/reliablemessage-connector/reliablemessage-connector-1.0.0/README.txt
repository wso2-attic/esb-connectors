Product: Integration tests for WSO2 ESB Reliable Message connector

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download   
   fails, download the following project and compile it using the mvn clean install command to update your local repository: https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1
 
Tested Platform:

 - UBUNTU 13.04
 - WSO2 ESB 5.0
 - Java 1.7

 1. To build the connector without running tests from any location, run maven build with the -Dmaven.test.skip=true switch.

Steps to follow in setting integration test.

 2. Download ESB 4.9.0 from official website.

 3. Installed WS-RM as a feature from p2-repo

 4. Coppy "{RM_CONNECTOR_HOME}/src/test/resources/client.xml" file into the "{ESB_HOME}/repository/conf/cxf" folder.
			
 5. Compress modified ESB as wso2esb-4.9.0.zip with latest patches and copy that zip file in to location “{RM_CONNECTOR_HOME}/repository/".
 
 6. Prerequisites for Reliable message Connector Integration Testing
    
	Follow these steps before start testing.
    a)  Navigate to “{RM_CONNECTOR_HOME}/src/test/resources/rmserver” and run the following command to run backend RM server.
        java -jar rm_server.jar

 7. Navigate to "{RM_CONNECTOR_HOME}" and run the following command.
    $ mvn clean install
	
