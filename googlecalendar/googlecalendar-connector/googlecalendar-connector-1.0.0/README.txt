Product: Integration tests for WSO2 ESB GoogleCalendar connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0

STEPS:

 1. This ESB should be configured as below;
	In Axis configurations (\repository\conf\axis2\axis2.xml).

   i) Enable message formatter for "text/html" in messageFormatters tag
			<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

   ii) Enable message builder for "text/html" in messageBuilders tag
			<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

   iii) Enable message formatter for "application/json" in messageFormatters tag
			<messageFormatter contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamFormatter"/>

   iv) Enable message builder for "application/json" in messageBuilders tag
			<messageBuilder contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

 2. Create a Google account and enable Google Calendar API:
	i) 	Using the URL "https://accounts.google.com/SignUp" create a Google account.
	ii) 	Go to "https://developers.google.com/oauthplayground/".
	iii) 	Authorize Google-Calendar API from "Select & authorize APIs" by selecting "https://www.googleapis.com/auth/calendar".
	iv) 	Then go to "Exchange authorization code for tokens" and click on "get authorization code for token" button and get the access token from "Access token" box.

 3. Update the googlecalendar properties file at location "{GOOGLECALENDAR_CONNECTOR_HOME}/googlecalendar-connector/googlecalendar-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

    i) accessToken - Use the access token you got from step 2.
    ii) emailAddress - Email address of the created Google account in step 2.
				iii) clientId - Use the Client ID.
				iv) clientSecret - Use the Client Secret.
				v) refreshToken - Use the Refresh token.

 4. Navigate to "{GOOGLECALENDAR_CONNECTOR_HOME}/googlecalendar-connector/googlecalendar-connector-1.0.0/org.wso2.carbon.connector/src/" and run the following command.
      $ mvn clean install

 NOTE : Following Google account, can be used for run the integration tests.
    Username : gcalendarwso2@gmail.com
    Password : wso22016
