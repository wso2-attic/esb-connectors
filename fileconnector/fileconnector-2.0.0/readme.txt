Product: Integration tests for WSO2 ESB File Connector version 2

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above

Tested Platform: 

 - UBUNTU 14.04, Mac OSx 10.9,Microsoft WINDOWS V-7
 - WSO2 ESB 4.9.0
 - Java 1.7

STEPS:

1. Download ESB 4.9.0 by navigating the following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/.

2. Copy the wso2esb-4.9.0.zip in to location "{ESB_Connector_Home}/repository/".

3. Update the fileconnector properties file at location "<ESB_Connector_Home>/fileconnector/fileconnector-2.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config".

4. Make sure that fileconnector is specified as a module in ESB_Connector_Parent pom.
       <module>fileconnector/fileconnector-2.0.0/org.wso2.carbon.connector</module>

5. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install