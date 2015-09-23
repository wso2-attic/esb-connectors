Product: Integration tests for WSO2 ESB Beetrack connector

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7
 
STEPS:

 1. Download ESB 4.9.0-BETA-SNAPSHOT by navigating to the following URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
    If required add the X.509 certificate from https://www.beetrack.com to the client-truststore.jks of the ESB located in <ESB_HOME>/repository/resources/security folder
    and wso2carbon.jks located in <BEETRACK_CONNECTOR_HOME>/beetrack-connector/beetrack-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products.

 3. Create a Beetrack trial account and derive the API Key and Secret.
   i)    Using the URL "https://www.beetrack.com" create a Beetrack trial account.
   ii)   Login to the created Beetrack account and navigate to "Settings" -> "Advanced settings" and go to "API Keys" tab and create an API key and obtain the API key.
   iii)  Create a new mobile user by navigating to "Settings" -> "Mobile users" -> "New mobile user" and obtain the username.
   iv)   Create two routes including a shipment with items.
   v)    Click the shipment link of the created route and obtain the dispatch ID (Value under the Guide column) for both routes.

 4. Update the beetrack properties file at location "<BEETRACK_CONNECTOR_HOME>/beetrack-connector/beetrack-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)        apiUrl                                      - Use http://app.beetrack.com.
   ii)       accessToken                                 - Use the API Key obtained under Step 3 ii).
   iii)      vehicleId                                   - Identifier of the vehicle for createVehicle mandatory case. Use a valid string value.
   iv)       routeDate                                   - Date in which the route is going to be managed for createRoute mandatory case. Use the current date value in following format (Format dd-mm-yyyy).
   v)        routeDispatches                             - Dispatch id obtained under Step 3 v).
   vi)       routeDriverIdentifier                       - The username obtained under Step 3 iii).
   vii)      routeEnableEstimations                      - Use any boolean value.
   viii)     routeDispatchesUpdated                      - Dispatch id obtained under Step 3 v). For the second route.
   ix)       routeStartTimeUpdate                        - Time at which the route started. Use the value given in 4 iv) in following date format (2015-09-15).
   x)        routeEndTimeUpdate                          - Time at which the route ended. Use following date format (2015-09-15).

 5. Make sure that beetrack is specified as a module in ESB Connector Parent pom.
        <module>beetrack/beetrack-connector/beetrack-connector-1.0.0/org.wso2.carbon.connector</module> 

 6. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
         $ mvn clean install