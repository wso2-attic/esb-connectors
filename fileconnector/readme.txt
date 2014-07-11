Product: Integration tests for WSO2 ESB File Connector

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above

Tested Platform: 

 - UBUNTU 13.10
 - WSO2 ESB 4.8.1

STEPS:

1. Copy the ESB 4.8.1 zip to the location "File_Connector_Home/repository/"

2. Please change the file locations with the accessible file locations in the fileconnector.xml
    (fileconenctor.xml file can be found from <fileconnetor>/src/test/resources/artifacts/ESB/config/proxies/fileconnector/ )

3.  Navigate to "File_Connector_Home" and run the following command.
      $ mvn clean install
