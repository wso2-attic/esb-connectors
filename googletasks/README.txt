Product: Integration tests for WSO2 ESB Google Tasks Connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - A google account for the Google Tasks API
 

Tested Platform: 

 - Ubuntu 13.04 64bit
 - WSO2 ESB 4.8.1
		  
STEPS:

1. Make sure the ESB 4.8.1 zip file with latest patches available at {basedir}/repository folder. If you want to use another location edit the pom.xml as follows.

          <carbon.zip>
            ${basedir}/../test/wso2esb-${esb.version}.zip
          </carbon.zip>

2. Copy googletasks connector zip file (googletasks.zip) to the location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/repository/"

3. Make sure the google tasks test suite is enabled (as given below) and all other test suites are commented in the following file - "{basedir}/src/test/resources/testng.xml"  
    <test name="GoogleTasks-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.googletasks"/>
        </packages>
    </test>

4. This section describes how to obtain an access token from the google oauth.
	i) 	Login to your google developer account, then navigate to url: https://console.developers.google.com/project and create new project. 
	ii) Select select the new project and navigate to "APIs and auth" from the left panel. In the APIs page enable Google Tasks API from the Google API list. 
	iii)Select "credentials" from the left panel and in that page click "create a new client ID". From the dialog box select "Web Application" and click "create client ID".
	iv)	You can see the client ID, client secret and redirect URIs which will be used when obtaining the access token.
	v)	Use the guide at the url: https://developers.google.com/accounts/docs/OAuth2WebServer and follow the instructions in "offline access" and "using a refresh token" in order to get a refresh token.
	v)	Go to {basedir}/src/test/resources/artifacts/ESB/connector/config/googletasks.properties and replace the values:
			clientId - client ID of the google tasks app	
			clientSecret - client secret of the google tasks app
			refreshToken - refresh token obtained from google oauth 2.0
	vi)getAccessToken.xml configuration will obtain the temporary access token using these property values.

5. From the base directory run the following command.
     $ mvn clean install

Notes: 
More on google oauth: https://developers.google.com/accounts/docs/OAuth2
