## Product: Integration tests for WSO2 ESB Google Drive connector ##
Pre-requisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform: 

- Ubuntu 12.04/Ubuntu Server 13.04
- WSO2 ESB 4.8.1
		  
STEPS:

1. Make sure the ESB 4.8.1 zip file with latest patches (including JSON to XML conversion patch) is available at "Integration_Test/products/esb/4.8.1/modules/distribution/target/".

2. Copy Google Drive connector zip file (googledrive.zip) to the location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/repository/"

3. Add the below mentioned code just below listeners tag (remove the remaining test tags) to the file - "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/testng.xml"

	<test name="GoogleDrive-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.googledrive"/>
        </packages>
    </test> 

4. Copy the test packages 'googledrive-connector-test/src/test/java/org/wso2/carbon/connector/integration/test/common' and 'googledrive-connector-test/src/test/java/org/wso2/carbon/connector/integration/test/googledrive' to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/java/org/wso2/carbon/connector/integration/test/"

5. Copy proxy files to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/proxies/googledrive/"

6. Creating a Google Cloud Console account: 
	- Go to https://console.developers.google.com/
	- Create a new Google Cloud Console project
	- Go to your newly created project and go to APIs and Auth
	- Enable the Drive API
	- Go to APIs and auth->Credentials
	- Under OAuth, click Create New Client ID, then Create Service Account and Create Client ID. Under the 'Service Account' section, click on 'Generate new key' and download the provided PKCS12 certificate. This will be the certificate for Service Account actions on the Google Drive connector

7. Rename the .p12 file obtained as 'googledrive_certificate.p12' and place it in 'Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/'

8. Add a file with the name 'test.txt' with any content you want to upload as a new file to Google Drive for the insertFile integration tests at "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/"

9. Add a file with the name 'update_test.txt' with any content you want to update on the file in Google Drive for the updateFile integration tests "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/"

10. Edit the "googledrive.properties" at Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config using valid and relevant data. Parameters to be changed are mentioned below.
	* If you wish to use a service account,
		- useServiceAccount: Set to 'true' if you wish to perform actions on the connector using the service account, and to 'false' if you wish to perform actions using an accessToken (client account).
		- serviceAccountEmail: Obtain this from the Cloud Console project, APIs & auth->Credentials->Service Account->Email address
	* If you wish to use a client account with access token,
		- accessToken: 	You can simply generate an access token through https://developers.google.com/oauthplayground/ by authenticating the drive API
	* If you wish to use a client account with a refresh token,
		- clientId: Obtain from Google Clound Console project, APIs & auth->Credentials->Client for Web Applications->Client ID
		- clientSecret: Obtain from Google Clound Console project, APIs & auth->Credentials->Client for Web Applications->Client secret
		- refreshToken: Generate a refresh token through the following:
			* Send a GET request to https://accounts.google.com/o/oauth2/auth?redirect_uri={YOUR_REDIRECT_URI}&response_type=code&client_id={YOUR_CLIENT_ID}&scope=https://www.googleapis.com/auth/drive&approval_prompt=force&access_type=offline
			* Click on the 'Allow' button and pick up the 'code' from the address bar of the browser after redirect.
			* Send a POST request with the content type application/x-www-form-urlencoded to  https://accounts.google.com/o/oauth2/token with; 
				- code={YOUR_AUTHORIZATION_CODE}
				- redirect_uri={YOUR_REDIRECT_URI}
				- client_id={YOUR_CLIENT_ID}
				- client_secrent={YOUR_CLIENT_SECRET}
				- grant_type=authorization_code
			* Use the refresh_token returned from the above request as the refreshTOken in the properties file.
	* backupFileId: Can be used if you wish by adding the ID of a file already existing in your Drive.
		

11. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/” and run the following command.
     $ mvn clean install
