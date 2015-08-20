Product: Integration tests for WSO2 ESB WunderList connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-BETA-SNAPSHOT by following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2.	Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
 
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
		<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
 4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".
 
 5. Make sure that WunderList is specified as a module in ESB Connector Parent pom.
        <module>wunderlist/wunderlist-connector/wunderlist-connector-1.0.0/org.wso2.carbon.connector</module>
		
 6. Create a WunderList trial account and derive the Client ID and Access Token.
	i) 	 Using the URL "https://www.wunderlist.com" create a WunderList trial account.
	ii)	 Create a new App and Obtain the client ID and access token for the created account in 6(i) as instructed in "https://developer.wunderlist.com/documentation/concepts/authorization".
	
 7. Update the wunderlist properties file at location "<WUNDERLIST_CONNECTOR_HOME>/wunderlist-connector/wunderlist-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
																			  
	i)		apiUrl 							- 	The API URL of the WunderList account (e.g.: https://a.wunderlist.com).
	ii) 	accessToken						-   Use the access token obtained under step 6 (ii).
	iii)	clientId						-	Use the client ID obtained under step 6 (ii).
	iv)		listTitleMand					-	Text to be used as 'title' while creating a list with mandatory parameters.
	v)		listTitleUpdate					-	Text to be used as 'title' while updating a list with optional parameters.
	vi)		folderNameMand					- 	Text to be used as 'name' while creating a folder with mandatory parameters.
	vii)	folderNameUpdate				- 	Text to be used as 'name' while updating a folder with optional parameters.
	viii)	updatelistTitle					-	Text to be used as list 'title' while updating a folder with optional parameters.
	ix)		taskTitleMand					-	Text to be used as 'title' while creating a task with mandatory parameters.
	x)		taskTitleOpt					-	Text to be used as 'title' while creating a task with optional parameters.
	xi)		askTitleUpdate					-	Text to be used as 'title' while updating a task with optional parameters.
	xii)	taskDueDate						-	Date to be used as 'due_date' while creating a task with optional parameters (date format should be yyyy-MM-dd).
	xiii)	contentMand						-	Text to be used as 'content' while creating a task note with mandatory parameters.
	xiv)	contentUpdate					-	Text to be used as 'content' while updating a task note with optional parameters.	
	xv)		reminderDate					-	Date to be used as 'reminder_date' while creating a task reminder with mandatory parameters (date format should be yyyy-MM-dd).
		
 8. Navigate to "<ESB_Connector_Home>/" and run the following command.
         $ mvn clean install

		