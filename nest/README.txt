Product: Integration tests for WSO2 ESB Nest connector
Pre-requisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform: 

- Mac OSx 10.9
- WSO2 ESB wso2esb-4.9.0-BETA-SNAPSHOT
- Java 1.7

STEPS:

1. Make sure the Download the ESB 4.9.0-BETA-SNAPSHOT.zip file at "<ESB_CONNECTORS_HOME>/repository/".

2. Follow these steps to setup Nest
     1) Navigate to https://developer.nest.com/ and create an account.
     2) Add nest extension at google chrome.
     3) Navigate to https://developer.nest.com/clients and register a client.
           Follow https://developer.nest.com/documentation/cloud/register-client to create a client.
     4) To get an accesstoken, follow the steps after creating a client.
     4) Go to your installed nest extension at google chrome browser and add Thermostats and CO-Alarms.
     5) Find CO alarm Id, Thermostat Id and Structure Id and put the values in properties for smokeCOAlarmDeviceId, deviceIdThermostat and structureId accordingly.
     6) Set an event for your structure under Rush Hour Rewards via Nest Developer Tool (tool is added by Step 2).

Note: Last connection information should be available for the CO alarm Id mentioned in step 5.

3. Edit the "nest.properties" at nest/src/test/resources/artifacts/connector/config/ using valid and relevant data. Parameters to be changed are mentioned below.

	- proxyDirectoryRelativePath: relative path of the Rest Request files folder from target.
	- requestDirectoryRelativePath: relative path of proxy folder from target.
	- propertiesFilePath: relative path of properties file from target.
	- accessToken: required to access API resources
	- clientId: to get the access token for a particular client_id.
    - clientSecret: to get the access token for a particular client_secret.
	- code: authorization code to get the access token.
	- apiUrl: API URL.
	- apiRedirectUrl: When we invoke the API URL, it will be redirected to this redirect URL.

4. Following data set can be used for the first test-suite to execute.

    proxyDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/proxies/nest/
    requestDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/restRequests/nest/
    propertiesFilePath=/../src/test/resources/artifacts/ESB/connector/config/
    clientId=ab44cdb3-f056-4c5b-b8a5-e3fd2c6d055f
    clientSecret=Yfjdp7dKJ7p8ipXhAmjNIeF9R
    code=9BTUS4DQ
    apiUrl=https://developer-api.nest.com
    apiRedirectUrl=https://firebase-apiserver01-tah01-iad01.dapi.production.nest.com:9553

5. Required to change following parameters to execute test

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

6. Make sure that the nest connector is set as a module in esb-connectors parent pom.
       <module>nest/nest-connector/nest-connector-1.0.0/org.wso2.carbon.connector</module>

7. Navigate to "{ESB_CONNECTORS_HOME}/" and run the following command.
       $ mvn clean install