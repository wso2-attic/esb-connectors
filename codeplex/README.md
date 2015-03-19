Product: Integration tests for WSO2 ESB CodePlex Connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - Codeplex user account


Tested Platform:

 - MacOs 10.9
 - WSO2 ESB 4.8.1

STEPS:

1. Make sure the ESB 4.8.1 zip file with latest patches available at {base.codeplex.connector.dir}/repository folder.
   If you want to use another location edit the pom.xml as follows.

          <carbon.zip>
            ${basedir}/../test/wso2esb-${esb.version}.zip
          </carbon.zip>

2. This section describes how to obtain an access token from the codeplex.

	i) 	 Login to your codeplex account, then navigate to url: https://www.codeplex.com/site/developers/apps and
	     create new application.
	ii)  Needs get to authorized the account with application by sending a GET request to
	     https://www.codeplex.com/oauth/authorize with client secret and state as parameters, you will get a code from codeplex.
	iii) In Order to get Access Token a POST request to https://www.codeplex.com/oauth/token with code obtained in above step.
    iv)	 After all you will have 4 properties which will be used to invoke authorization required operations with OAuth2

			clientId - client ID of the codeplex application
			clientSecret - client secret of the codeplex application
			refreshToken - refresh token obtained from codeplex oauth 2.0

	vi) By invoking getAccessToken.xml configuration, it will obtain the temporary access token using these property
	    values. You need to invoke this operation before invoking authentication required operations.
    vii) More information on codeplex authentication available at codeplex online documentation site[1].

3. From the base directory run following command to build and execute integration test suite.
     $ mvn clean install


Release Notes:

Mar 15, 2014 - Currently codeplex project information are not available through codeplex rest api as documented[2]. Therefore,
               project related tests are temporally disabled through testng.xml. In case these resources are available in future
               please uncomment project test class entry in testng.xml.


Reference links :

[1] https://www.codeplex.com/site/developers/api/authentication
[2] http://www.codeplex.com/site/developers/api/projects