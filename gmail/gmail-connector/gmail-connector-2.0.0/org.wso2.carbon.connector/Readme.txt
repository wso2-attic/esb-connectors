Product: Integration tests for WSO2 ESB Gmail connector via REST

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform:

 -  MAC OS
 - WSO2 ESB 4.8.1

STEPS:

1. Copy the ESB 4.8.1 zip to the location "Gmail_Connector_Home/repository/"

2. Obtain an access token for your Gmail account by following below steps;
	- Access Google OAuth playground using “https://developers.google.com/oauthplayground/”
	- Type “http://mail.google.com” in the text box as the scope and click on “Authorize APIs” button
	- Sign in with your Gmail account or select the preferred account if you have already signed in.
	- Accept the permission to view and manage your e-mails
	- Click on “Exchange authorization code for tokens” button to get the access token

3. Update connector properties file "gmail.properties" located in "Gmail_Connector_Home/src/test/resources/artifacts/ESB/connector/config/",
with following information
	- userId : give your e-mail address
	- accessToken : obtained access token
	and other values should be updated according to your gmail account(labelId,threadId,draftId..).

4. If there are too many simultaneous connections to your Gmail account, sign out from those. Because Gmail allows only 15 simultaneous connections.
(Below steps can be used to sign out from existing Gmail connections)
	- Log in to the Gmail account in browser
	- Scroll down and click on the link "Details" which is near the label "Last account activity: xx minutes ago"
	- Click on "Sign out all other sessions"


5. Change the "enabled" values into true in all test cases in the Integration test.
   There are two java files for integration tests, cannot run both simultaneously. So you need to disable one class and run another class.

6. Navigate to "Gmail_Connector_Home" and run the following command.
      $ mvn clean install
