Product: Integration tests for WSO2 ESB Gmail connector through REST API

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 -  MAC OS
 - WSO2 ESB 4.8.1

STEPS:

1. Copy the ESB 4.8.1 zip to the location "Gmail_Connector_Home/repository/"

2. Obtain the authorization credentials following below steps;
	- Access https://developers.google.com/gmail/api/auth/web-server
	
3. Update connector properties file "gmail.properties" located in "Gmail_Connector_Home/src/test/resources/artifacts/ESB/connector/config/", 
with following information
	- Update the refreshToken, clientId, clientSecret, grantType values.
	- userId : give your e-mail address
	- accessToken : obtained access token
	and other values should be updated according to your gmail account(labelId,threadId,draftId..).

4. If there are too many simultaneous connections to your Gmail account, sign out from those. Because Gmail allows only 15 simultaneous connections.
(Below steps can be used to sign out from existing Gmail connections)
	- Log in to the Gmail account in browser
	- Scroll down and click on the link "Details" which is near the label "Last account activity: xx minutes ago"
	- Click on "Sign out all other sessions"

5.  Navigate to "Gmail_Connector_Home" and run the following command.
      $ mvn clean install

	
