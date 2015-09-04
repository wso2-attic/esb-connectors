Product: WSO2 ESB Connector for Google Contacts + Integration Tests

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
       https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 12.04, Mac OSx 10.9
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT

STEPS:
 

 1. Download ESB 4.9.0-BETA-SNAPSHOT from official site and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 2. Creating a Google Cloud Console account:
	- Go to https://console.developers.google.com/
	- Create a new Google Cloud Console project
	- Go to your newly created project and go to APIs and Auth
	- Enable the Contacts API

 3. Update the property file at googlecontacts.properties found in  to "{GoogleContacts_Connector_Home}/googlecontacts-connector/googlecontacts-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as follows.

    i)  accessToken: Generate an access token through https://developers.google.com/oauthplayground/ by authenticating the Contacts API
    ii) If you wish to generate an accessToken manually;
	    - clientId: Obtain from Google Cloud Console project, APIs & auth->Credentials->Client for Web Applications->Client ID
	    - clientSecret: Obtain from Google Cloud Console project, APIs & auth->Credentials->Client for Web Applications->Client secret
	    - refreshToken: Generate a refresh token through the following:
		    * Send a GET request to https://accounts.google.com/o/oauth2/auth?redirect_uri={YOUR_REDIRECT_URI}&response_type=code&client_id={YOUR_CLIENT_ID}&scope=https://www.googleapis.com/auth/drive&approval_prompt=force&access_type=offline
		    * Click on the 'Allow' button and pick up the 'code' from the address bar of the browser after redirect.
		    * Send a POST request with the content type application/x-www-form-urlencoded to  https://accounts.google.com/o/oauth2/token with;
			    - code={YOUR_AUTHORIZATION_CODE}
			    - redirect_uri={YOUR_REDIRECT_URI}
			    - client_id={YOUR_CLIENT_ID}
			    - client_secrent={YOUR_CLIENT_SECRET}
			    - grant_type=authorization_code

 4. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install -DskipTests=true
      $ mvn test

 NOTE : Following Google Contacts test account can be used for run the integration tests.
 Username : testgooglecontact
 Password : 1qaz2wsx@
