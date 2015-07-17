Product: Integration tests for WSO2 ESB Teamwork connector
Pre-requisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform: 

- UBUNTU 14.04
- WSO2 ESB wso2esb-4.8.1
- Java 1.7

STEPS:

1. Make sure the ESB 4.8.1 zip file at "/repository/".

2.Deploy relevant patches, if applicable and the ESB should be configured as below.
	Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
 
	<messageFormatter contentType="multipart/form-data"
				class="org.wso2.carbon.relay.ExpandingMessageFormatter"/> 
	<messageBuilder contentType="multipart/form-data"
				class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

3. Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following file - "src/test/resources/testng.xml"

    <test name="Teamwork-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.teamwork"/>
        </packages>
    </test> 

4. Copy proxy files to following location "src/test/resources/artifacts/ESB/config/proxies/teamwork/"

5. Copy request files to following "src/test/resources/artifacts/ESB/config/restRequests/teamwork/"

6. Edit the "teamwork.properties" at src/test/resources/artifacts/connector/config/ using valid and relevant data. Parameters to be changed are mentioned below.

	- proxyDirectoryRelativePath: relative path of the Rest Request files folder from target.
	- requestDirectoryRelativePath: relative path of proxy folder from target.
	- propertiesFilePath: relative path of properties file from target.
	- apiUrl: API URL.
	- apiKey:The API key.

		
7. Following data set can be used for the first testsuite run.

	proxyDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/proxies/teamwork/
	requestDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/restRequests/teamwork/
	propertiesFilePath=/../src/test/resources/artifacts/ESB/connector/config/
	apiUrl=https://kesan.teamwork.com
 	apiKey=cod323ipad
 	
 	Required to change on every test run :
 	deleteActivityId
	deleteFileId
	addFilePendingFileRef
	newVersionPendingFileRef
	deleteActivityId
	deletePersonId
	createProjectName
	deleteProjectId
	deleteEventId
	deleteMilestoneId
	deleteCommentId
	deleteNotebookId
	deletePeopleStatusId

7. Make sure that the teamwork connector is set as a module in esb-connectors parent pom.
           <module>teamwork/teamwork-connector/teamwork-connector-1.0.0/org.wso2.carbon.connector</module>

8. Navigate to "org.wso2.carbon.connector/" and run the following command.
     $ mvn clean install
