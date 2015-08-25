Product: Integration tests for WSO2 ESB Pivotaltracker connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Create a Pivotaltracker account and get user credentials according to below steps:

	i)  Navigate to URL "https://www.pivotaltracker.com/signup/new" and create a trial account. 
		(Note the apiURl for further use. "https://www.pivotaltracker.com" )

	ii) Once you registered it, you have to goto the mail account and confirm your registration by clicking the provided URL and give the password you wish to use.
		(Note the password for further use.)
		
	iii) Go to your user profile page(You can find this on the upper right corner after logging in.) and note the User name.   
 
 2. Download ESB WSO2 ESB 4.9.0-BETA-SNAPSHOT by navigating the following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/.
 
 3.	Deploy relevant patches, if applicable.
 
 4. Follow the below mentioned steps for adding valid certificate to access Pivotaltracker API over https

	i) Extract the certificate from browser by navigating to apiUrl[Step 1->(i)] and place the certificate file in following location.
	   "{PIVOTALTRACKER_CONNECTOR_HOME}/pivotaltracker-connector/pivotaltracker-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/"
	  
	ii) Navigate to "{PIVOTALTRACKER_CONNECTOR_HOME}/pivotaltracker-connector/pivotaltracker-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import pivotaltracker certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from pivotaltracker, change it accordingly. (e.g. -.pivotaltracker.com)
			   CERT_NAME is name of the certificate. (e.g. pivotaltracker)
	   
	iii) Go to ESB 4.9.0-BETA-SNAPSHOT folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/"

	iv) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import pivotaltracker certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from pivotaltracker, change it accordingly. (e.g. -.pivotaltracker.com)
			   CERT_NAME is name of the certificate. (e.g. pivotaltracker)
		
 7. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 8. Make sure that Pivotaltracker is specified as a module in ESB_Connector_Parent pom.
 	<module>PivotalTracker\pivotaltracker-connector\pivotaltracker-connector-1.0.0\org.wso2.carbon.connector</module>	
 
 9. Update the 'pivotaltracker.properties' file at the location "{PIVOTALTRACKER_CONNECTOR_HOME}/pivotaltracker-connector/pivotaltracker-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
   	 i) 	apiUrl						-		Use the api url, step 1->(i)
	 ii) 	userName					-		Use the userName, step 1->(iii)
	 iii) 	password					-		Use the password, step 1->(ii)
	 iv)	projectName					-		A String for project name to create project with mandatory parameters.
	 v)		projectNameOpt				- 		A String for project name to create project with optional parameters.
	 vi)	weekStartDay				- 		A valid String for week start day(Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday) to create project with optional parameters.
	vii)	projectDescription			- 		A String for project description to create project with optional parameters.
	viii)	projectType					- 		A valid String for project type(private, public, or demo) to create project with optional parameters.
	ix)		projectProfile				- 		A String for project profile to create project with optional parameters.
	x)		projectTimeZone				- 		The "native" time zone for the project timezone to create project with optional parameters.
	xi)		accountName					- 		A String for account name.
	xii)	labelName					-		A lowercase String for label name to create label for project with mandatory parameters and create label for story with mandatory parameters.
	xiii)	updatedLabelName			-		A lowercase String for label name to update project label with mandatory parameters.
	xiv)	storyName 					-		A String for story name to create story with mandatory parameters.
	xv)		storyNameOpt				-		A String for story name to create story with optional parameters.
	xvi)	storyDescription			-		A String for story description to create story with optional parameters.
	xvii)	storyState					-		A valid String for story state(This should be always 'accepted') to create story with optional parameters.
	xviii)	storyType					-		A valid String for story type(This should be always 'release') to create story with optional parameters.
	xix)	deadline					-		A datetime(eg:2013-04-30T04:25:15Z) for deadline to create story with optional parameters.
	xx)		acceptedAt					-		A datetime(eg:2013-04-30T04:25:15Z) for accepted date to create story with optional parameters.
	xxi)	createdBefore				-		A datetime(eg:2015-08-20T15:53:00) for created before date to list stories with optional parameters.		
	xxii)	createdAfter				-		A datetime(eg:2015-08-20T15:53:00) for created after date to list stories with optional parameters.
	
	*	projectName and projectNameOpt values should be changed in each run.
	*	createdBefore should be a future date. 
	*	createdAfter should be a past date.
	
 10. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install