Product: Integration tests for WSO2 ESB EventBrite connector
Prerequisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform:

- Mac OS X V-10.9.5
- WSO2 ESB 4.9.0-ALPHA
- Java 1.7


STEPS:

1.Make sure theWSO2 ESB 4.9.0-ALPHA zip file available at:
"{ESB_Connector_Home}/repository/

2. Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following file -
"{ESB_Connector_Home}/eventbrite/src/test/resources/testng.xml"

<test name="eventbrite-Connector-Test" preserve-order="true" verbose="2>
<packages>
<package name="org.wso2.carbon.connector.integration.test.eventbrite"/>
</packages>
</test>


3. Edit the "eventbrite.properties" at:
"{ESB_Connector_Home}/eventbrite/src/test/resources/artifacts/ESB/connector/config using valid and relevant data. Parameters to be changed are mentioned below.

	- login https://www.eventbrite.com/login/ - you may use the dummy Account details below
	- request access token

4. Following data set can be used for the first test-suite to execute.

Proxy Directory Relative Path=/../src/test/resources/artifacts/ESB/config/proxies/eventbrite/

Proxy Directory Relative Path=/../src/test/resources/artifacts/ESB/config/proxies/eventbrite/

Request Directory Relative Path = /../src/test/resources/artifacts/ESB/config/restRequests/eventbrite/

proxyDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/proxies/eventbrite/
requestDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/restRequests/eventbrite/
apiUrl=https://www.eventbriteapi.com
accessToken=HSX6X635TURH32K5CLMV
userId=125733859887
contactListId=772075
eventId=13981437857
attendeesId=461291099
teamId=950713
ticketId=30329833
orderId=364498473
organizerId=11109810743

Account Details:
username: eventbritetesting@gmail.com
password: Eventbrite123

5.Required to change on test change the relevant data in the corresponding text file(Text files in the rest requests folder)

6. Make sure that the eventbrite connector is set as a module in esb-connectors parent pom.
            <module>eventbrite</module>

7. Navigate to "{ESB_Connector_Home}/” and run the following command.
$ mvn clean install