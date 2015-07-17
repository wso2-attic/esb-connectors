Product: Integration tests for WSO2 ESB EventBrite connector
Prerequisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform:

- Mac OS X V-10.9.5
- WSO2 ESB 4.9.0-ALPHA
- Java 1.7


STEPS:

1.Make sure theWSO2 ESB 4.9.0-ALPHA zip file available at: "{ESB_Connector_Home}/repository/

2. Edit the "eventbrite.properties" at:
"{ESB_Connector_Home}/eventbrite/src/test/resources/artifacts/ESB/connector/config using valid and relevant data. Parameters to be changed are mentioned below.

	- login https://www.eventbrite.com/login/ - you may use the dummy Account details below
	- request access token: Use 'Personal OAuth token'.
	- Create an event and Add Attendees and use the orderId at 3. Similarly give values for attendeesId, teamId and ticketId.

3. Following data set can be used for the first test-suite to execute.

      apiUrl=https://www.eventbriteapi.com
      accessToken=HSX6X635TURH32K5CLMV
      attendeesId=461291099
      teamId=950713
      ticketId=30329833
      orderId=364498473

4. Required to change on test change the relevant data in the corresponding text file(Text files in the rest requests folder)

5. Make sure that the eventbrite connector is set as a module in esb-connectors parent pom.
     <module>eventbrite/eventbrite-connector/eventbrite-connector-1.0.0/org.wso2.carbon.connector</module>

6. Navigate to "{ESB_Connector_Home}/” and run the following command.
      $ mvn clean install

   Account Details:
   username: eventbritetesting@gmail.com
   password: Eventbrite123