Product: Integration tests for WSO2 ESB Reliable Message connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 
Tested Platform:

 - UBUNTU 13.04
 - WSO2 ESB 4.9.0

 1. To build the connector without running tests from any location, run maven build with the -Dmaven.test.skip=true switch.

Steps to follow in setting integration test.

 2. Download ESB 4.9.0 from official website.
			
 3. Compress modified ESB as wso2esb-4.9.0.zip with latest patches and copy that zip file in to location “{RM_CONNECTOR_HOME}/repository/".

 4. Installed WS-RM as a feature from p2-repo 
 
 4. Prerequisites for Reliable message Connector Integration Testing
    
	Follow these steps before start testing.
    a)  Navigate to “{RM_CONNECTOR_HOME}/src/test/resources/rmserver” and run the following command to run backend RM server.
        java -jar rm_server.jar
    b)  Navigate to “{RM_CONNECTOR_HOME}/src/test/resources/artifacts/ESB/config/synapse" and change the synapseRMConnectorConfig.xml configLocation property
        according to the current running environment. Required configuration file(client.xml) locate inside the "test/resource" folder.

 5. Navigate to "{RM_CONNECTOR_HOME}" and run the following command.
    $ mvn clean install
	
