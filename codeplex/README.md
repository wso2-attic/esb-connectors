WSO2 ESB CodePlex Connector
===========================

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - Codeplex user account


Tested Platform:

 - MacOS 10.9
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

STEPS:

1. Download ESB 4.9.0-ALPHA by navigating the following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/.

2. Copy wso2esb-4.9.0-ALPHA.zip file in to location "{ESB_Connector_Home}/repository/".

3. Make sure that codeplex is specified as a module in ESB_Connector_Parent pom.
    <module>codeplex</module>

4. This section describes how to obtain an access token from the codeplex.

	i)   Login to your codeplex account[1], then navigate to url[2] and create new application. After creating application it will return a client secret and client id.
	
	ii)  Authorize the account with application by sending a GET request to url[3] with client secret, response_type and state as parameters, you will get a temporary code from codeplex.
	     
	iii) In Order to get Access Token, send a POST request to url[4] with code obtained in step (ii) as a parameter.
	
	iv)  Finally, will have 3 properties which will be used to invoke authorization required operations with OAuth2.

			clientId - client ID of the codeplex application
			clientSecret - client secret of the codeplex application
			refreshToken - refresh token obtained from codeplex oauth 2.0

	v) By invoking getAccessToken.xml configuration, it will obtain the temporary access token using these property values. You need to invoke this operation before invoking authentication required operations.
	
	vi) More information on codeplex authentication available at codeplex online documentation site[5].

5. Following authentication information has used in integration test suite.

   Authentication parameters can be modified under following property file which will effective on all authentication required
   positive test cases.
   
   {codeplex-connector-home}/src/test/resources/artifacts/ESB/connector/config/codeplex.properties
   
   Copy request files to following directory.
   
   {codeplex-connector-home}/src/test/resources/artifacts/ESB/config/restRequests/codeplex
   

		proxyDirectoryPath   = "{codeplex-connector-home}/src/test/resources/artifacts/ESB/config/proxies/codeplex/"
		requestDirectoryPath = "{codeplex-connector-home}/src/test/resources/artifacts/ESB/config/restRequests/codeplex/"
		clientId             = "e2ca3fc77e334c679f0c5c50257f9f36"
		clientSecret         = "44c635345fbd408d96e675b0aaf1334d"
		refreshToken         = "L4Eo!IAAAAL3yP6zFRfsriup7jYcQThecBNIpHJ4UJFfYfGKDP2MNsQAAAAHxh0bbfEjjP7RDQBsRrELlW6cZDe4sGcT5jp3jNGxSkmwUZya3qDhgcPVxOyIuj-SC0fhdc5LP8xIqOWqR1SjiDa9d29uvopFxv-da14X2i_QByD1XCKPUHie04VKVphk_q0ta9AjsXc9MrUR9a6O24U_Da9srHendD0TJJKfygjHgYvFzkO3KIcoHmG2abPHybkBOBQHyltaHJwfESYohqHqRhL_FwJfnSRJjrzq8dg"

       Sample CodePlex Account Credentials :
       
       		username: wso2test1
       		password: wso2test1


6. Navigate to "{ESB_Connector_Home}/" and run the following command.
		$ mvn clean install


Release Notes:

Mar 15, 2014 - Currently codeplex project information are not available through codeplex rest api as documented[6]. Therefore,
               project related tests are temporally disabled through testng.xml. In case these resources are available in future
               please uncomment project test class entry in testng.xml.


Reference links :

[1] https://www.codeplex.com/site/login

[2] https://www.codeplex.com/site/developers/apps

[3] https://www.codeplex.com/oauth/authorize

[4] https://www.codeplex.com/oauth/token

[5] https://www.codeplex.com/site/developers/api/authentication

[6] http://www.codeplex.com/site/developers/api/projects
