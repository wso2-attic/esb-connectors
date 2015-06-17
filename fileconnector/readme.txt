Product: Integration tests for WSO2 ESB File Connector

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above

Tested Platform: 

 - UBUNTU 13.10, Mac OSx 10.9
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

STEPS:

1. Download ESB 4.9.0-ALPHA by navigating the following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/.

2. Copy the wso2esb-4.9.0-ALPHA.zip in to location "{ESB_Connector_Home}/repository/".

3. Please change the file locations with the accessible file locations in the fileconnector.xml
    (fileconenctor.xml file can be found from <fileconnetor>/src/test/resources/artifacts/ESB/config/proxies/fileconnector/ )

4. Make sure that fileconnector is specified as a module in ESB_Connector_Parent pom.
       <module>fileconnector/fileconnector-1.0.0</module>

5. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install