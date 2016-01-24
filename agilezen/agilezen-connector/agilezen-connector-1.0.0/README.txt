Product: Integration tests for WSO2 ESB AgileZen connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-ALPHA

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/

 2. i)  Install the formstack security certificate (login to https://agilezen.com and extract the certificate) to the following keystores:
		a) 	client-truststore.jks located in the <ESB_HOME>/repository/resources/security directory.
		b) wso2carbon.jks located in the <AGILEZEN_CONNECTOR_HOME>/agilezen-connector/agilezen-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products directory.
	
	ii)	 Add the following message formatter and message builder to the axis2.xml file of the ESB.
						<messageFormatter contentType="text/html"
									class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
						<messageBuilder contentType="text/html" 
													class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
													
	iii) Compress the modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "<AGILEZEN_CONNECTOR_HOME>/agilezen-connector/agilezen-connector-1.0.0/org.wso2.carbon.connector/repository/".
		
 3. Create a agilezen trial account and derive the API Token.
	i) 	 Using the URL "http://www.agilezen.com/" create an agilezen trial account.
	ii)	 In the account homepage click on 'Settings' -> 'Developer' -> 'New API Key' -> Copy and retain the token for further use (Enable the token, provide a description and save it).
	iii) Create a project in the account. (Free accounts allow only one project to be created)
	iv)  Create a Story inside the project and move it to 'blocked' status. Give any reason for blocking.
	
 4. Update the agilezen properties file at location "<AGILEZEN_CONNECTOR_HOME>/agilezen-connector/agilezen-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
																			  
	01)	apiUrl -  Base endpoint URL of the API. Use https://agilezen.com
	02) apiKey - API Key obtained in 3. ii).
	
	03)	textTaskMandatory -  Text to be used as 'text' while creating task with mandatory parameters.
	04)	textTaskOptional -  Text to be used as 'text' while creating task with optional parameters.
	05)	status -  Text to be used as 'status' while creating task. Refer the API documentation for valid status for task.
	06)	textTaskUpdated -  Text to be used as 'text' while updating task.
	07)	statusUpdated -  Text to be used as 'status' while updating task. Refer the API documentation for valid status for task. Should be different from 4. 05).
	
	08)	textCommentMandatory -  Text to be used as 'text' while creating comment with mandatory parameters.
	09)	textCommentUpdated -  Text to be used as 'text' while updating comment.	
	
	10)	enrichments - enrichments used to get additional data. This must not be changed.
	
	11)	projectUpdateName - Text to be used as 'name' while updating project.
	12)	projectUpdateDescription - Text to be used as 'description' while updating project.
	13)	projectUpdateDetails - Text to be used as 'details' while updating project.
	
	14)	storyText - Text to be used while creating story.
	15)	storyDetails - Text to be used as 'details' while creating story.
	16)	storySize - Numerical value to be used as 'story size' while creating story. Refer the API documentation for valid boundary range for size.
	17)	storyPriority - Numerical value to be used as 'priority' while creating story. Refer the API documentation for valid boundary range for priority.
	18)	storyColor - Text to be used as 'color' while creating story. Refer the API documentation for valid color for story.
	
	19) listFilters - Text to be used as 'filters' while creating story. This must not be changed.
	20) updateStoryDetails - Text to be used as 'details' while updating story. Should be different from 4. 15).
	21) updateStorySize - Numerical value to be used as 'story size' while updating story. Refer the API documentation for valid boundary range for size. Should be different from 4. 16).
	22) updateStoryPriority - Numerical value to be used as 'priority' while updating story. Refer the API documentation for valid boundary range for priority. Should be different from 4. 17).
	23) updateStoryColor - Text to be used as 'color' while updating story. Refer the API documentation for valid color for story. Should be different from 4. 18).
	24) updateStoryText - Text to be used while updating story. Should be different from 4. 14).
	
	Note: i) Properties 4.11), 4.12) and 4.13) needs to be changed for each execution of the test suite. Other properties can be used as it is.
	
 5. Navigate to "<AGILEZEN_CONNECTOR_HOME>/agilezen-connector/agilezen-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

		