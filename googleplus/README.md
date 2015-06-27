Product: Integration tests for WSO2 ESB Google Plus Connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - A google account for the Google Plus API
 

Tested Platform: 

 - Mac OSX 10.9.4 and Ubuntu 14.04 64bit 
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

STEPS:

1. Make sure the ESB 4.9.0-ALPHA zip file with latest patches available at {basedir}/repository folder. If you want to use another location edit the pom.xml as follows.

          <carbon.zip>
            ${basedir}/../test/wso2esb-${esb.version}.zip
          </carbon.zip>

2. This section describes how to obtain an access token from the google oauth.
	
	i) 	Login to your google developer account, then navigate to url: https://console.developers.google.com/project and create new project.
	 
	ii) Select select the new project and navigate to "APIs and auth" from the left panel. In the APIs page enable Google Plus API from the Google API list. 
	
	iii)Select "credentials" from the left panel and in that page click "create a new client ID". From the dialog box select "Web Application" and click "create client ID".
	
	iv)	You can see the client ID, client secret and redirect URIs which will be used when obtaining the access token.
	
	v)	Use the guide at the url: https://developers.google.com/accounts/docs/OAuth2WebServer and follow the instructions in "offline access" and "using a refresh token" in order to get a refresh token.
	
	vi)	Login and create an activity(Post anything) and comment on the activity.
	
	vii)	Go to {basedir}/src/test/resources/artifacts/ESB/connector.config/GooglePlus.properties and add following lines :
			clientId - client ID of the google tasks app	
			clientSecret - client secret of the google tasks app
			refreshToken - refresh token obtained from google oauth 2.0
        	apiUrl - API url of google plus (E.g: https://www.googleapis.com/plus/v1).
            userId - user Identifier of google plus account

	vii)getAccessToken.xml configuration will obtain the temporary access token using these property values.

3. Make sure that googleplus is specified as a module in ESB_Connector_Parent pom.
     <module>googleplus/googleplus-connector/googleplus-connector-connector-1.0.0/org.wso2.carbon.connector</module>

4. Navigate to "{ESB_Connector_Home}/" and run the following command.
     $ mvn clean install

Notes: 
More on google oauth: https://developers.google.com/accounts/docs/OAuth2