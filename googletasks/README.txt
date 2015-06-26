Product: Integration tests for WSO2 ESB Google Tasks Connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - A google account for the Google Tasks API
 

Tested Platform: 

 - Ubuntu 13.04 64bit, Mac OSx 10.9
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

STEPS:

1. Download ESB 4.9.0-ALPHA from official site and copy that wso2esb-4.9.0-ALPHA.zip file in to location "{ESB_Connector_Home}/repository/".

2. This section describes how to obtain an access token from the google oauth.

	i) 	Login to your google developer account, then navigate to url: https://console.developers.google.com/project and create new project.
	ii) Select select the new project and navigate to "APIs and auth" from the left panel. In the APIs page enable Google Tasks API from the Google API list. 
	iii)Select "credentials" from the left panel and in that page click "create a new client ID". From the dialog box select "Web Application" and click "create client ID".
	iv)	You can see the client ID, client secret and redirect URIs which will be used when obtaining the access token.
	v)	Use the guide at the url: https://developers.google.com/accounts/docs/OAuth2WebServer and follow the instructions in "offline access" and "using a refresh token" in order to get a refresh token.
	v)	Go to {basedir}/src/test/resources/artifacts/ESB/connector/config/googletasks.properties and replace the values:
			clientId - client ID of the google tasks app	
			clientSecret - client secret of the google tasks app
			refreshToken - refresh token obtained from google oauth 2.0
			previous - New parent task identifier for moveTask method.
            parent - New previous sibling task identifier for moveTask method.
	vi)getAccessToken.xml configuration will obtain the temporary access token using these property values.

3. Make sure that googletasks is specified as a module in ESB_Connector_Parent pom.
      <module>googletasks/googletaska-connector/googletaska-connector-1.0.0/org.wso2.carbon.connector</module>

4. From the base directory run the following command.
      $ mvn clean install

Notes: 
More on google oauth: https://developers.google.com/accounts/docs/OAuth2
