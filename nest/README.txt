Product: Integration tests for WSO2 ESB Nest connector
Pre-requisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform: 

- Mac OSx 10.9
- WSO2 ESB wso2esb-4.9.0-SNAPSHOT
- Java 1.7

STEPS:

1. Make sure the wso2esb-4.9.0-SNAPSHOT.zip file at "nest/repository/".

2. Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following
   file - "nest/src/test/resources/testng.xml"

    	<test name="Nest-Connector-Test" preserve-order="true" verbose="2">
        	<packages>
            	<package name="org.wso2.carbon.connector.integration.test.nest"/>
			</packages>
	    </test>

3. Copy proxy files to following location "nest/src/test/resources/artifacts/ESB/config/proxies/nest/"

4. Copy request files to following "nest/src/test/resources/artifacts/ESB/config/restRequests/nest/"

5. Edit the "nest.properties" at nest/src/test/resources/artifacts/connector/config/ using valid and relevant data. Parameters to be changed are mentioned below.

	- proxyDirectoryRelativePath: relative path of the Rest Request files folder from target.
	- requestDirectoryRelativePath: relative path of proxy folder from target.
	- propertiesFilePath: relative path of properties file from target.
	- clientId: to get the access token for a particular client_id.
    - clientSecret: to get the access token for a particular client_secret.
	- code: authorization code to get the access token.
	- apiUrl: API URL.
	- apiRedirectUrl: When we invoke the API URL, it will be redirected to this redirect URL.	
6. Following data set can be used for the first test-suite to execute.

    proxyDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/proxies/nest/
    requestDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/restRequests/nest/
    propertiesFilePath=/../src/test/resources/artifacts/ESB/connector/config/
    clientId=900ca954-ccf9-48ba-aef3-51b215e7a8ba
    clientSecret=Vb982GHVNJiYpcyB7au0h6H2R
    code=9BTUS4DQ
    apiUrl=https://developer-api.nest.com

7. Required to change on test

    smokeCOAlarmDeviceId: Id of smoke CO alarm.

    deviceIdThermostat: Id of thermostat.
    fanTimerState: Fan timer active state. It can be true or false.
    targetTemperature: Integer value
    scale=Temperature scale. It can be 'C' or 'F'.
    temperatureMode: HVAC mode.

    structureId: Id of structure.
    awayState: Away state of the structure. It can be 'home' or 'away'.
    tripId: Id of the trip.
    begin: Estimated arrival window begin time.
    end: Estimated arrival window end time.

8. Navigate to "nest/" and run the following command.
    $ mvn clean install
