Product: Integration tests for WSO2 ESB Redmine connector
    Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
	- The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

    Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1

Note:
  This test suite can execute based on two scenarios.
    1. Use the given test account and parameters. - in this scenario you only need to replace apiKey in property file
    2. Setup new redmine account and follow all the instruction given below
  
Steps to follow in setting integration test.
 1.  Download ESB 4.8.1 from official website.
 
 2.  Deploy relevant patches, if applicable.
            
 3.  Navigate to location "/wso2esb-4.8.1/repository/conf/axis2" and add/uncomment following lines in "axis2.xml" and Message Formatters and Message Builders should be added for each of the content types of the files to be added as attachments. 
  
            <messageFormatter contentType="text/javascript" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

            <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

            <messageFormatter contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
			
			<messageBuilder contentType="text/javascript" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

            <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

			<messageBuilder contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

 4.  Based on the content type of the attachment file you are adding in step 7-g-6, add the relevant message formatter and builder.

            e.g. for image/png:
            <messageFormatter contentType="image/png" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

            <messageBuilder contentType="image/png" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>


 6.  Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "<ESB_CONNECTORS_HOME>/repository/".

         
 7.  Prerequisites for Redmine Connector Integration Testing

     Follow these steps before start testing.
     a)  Create a fresh account in Redmine using the URL http://m.redmine.org/ with the web browser.
     b)  Login to the administrations panel of the account created using the login credentials with the specific API URL.
     c)  Go to Administration -> Settings -> Authentication and enable the check box in "Enable REST web service".
     d)  Go to /my/account URL of your Redmine demo site and copy the API Key from the right hand side of the screen to apiKey property in the redmine.properties property file.
     e)  Update the API URL of the created account in the property named apiUrl in the redmine.properties property file.
     f)  Go to Administration and click on 'Load Default Configuration' to load some default settings to the demo instance of Redmine.  
        
     g)  Following fields in the property file also should be updated appropriately.

        1)    createUserLogin is the login name for the user to be created.

        2)    createUserMail is the email address for the user to be created.

        3)    updateUserLogin is the login name used to change in the update user.

        4)    updateUserMail is the email address used to change in the update user.

        5)    attachmentContentType is the content type of the attachment to be added.

        6)    attachmentFileName is the name of the attachment file to be added. Set the resourceDirectoryRelativePath property to the relative path leading to this attachment file.

        7)    createProjectName is the name of a project to be created.

        8)    createProjectIdentifier is the identifier of the project created with name createProjectName.

        9)    createProjectOptName is the name of a project to be created.

        10)   createProjectOptIdentifier is the identifier of the project created with name createProjectOptName.

        11)   createProjectOptDescription is the description of the project created with name createProjectOptName.

        12)   include is the additional parameters for retrieving an issue or a project.

        13)   updateProjectName is the new name to be used for updating a project.

        14)   updateProjectDescription is the new description to be used for updating a project.

        15)   updateIssueSubject is the new subject to be used for updating an issue.

        16)   updateIssueDesc is the new description to be used for updating an issue.

        17)   updateIssueFixedVersionId is a suitable version id for an issue to updated.

        18)   updateIssueCategoryId is a category id of an issue to be created or updated.

        19)   timeOut is waiting time for completing the requests before querying any further.

        20)   spentOn is the date the time was spent on for a time entry.

        21)   hours is the number of hours spent for the specified time entry.

        22)   comments is a short description for the time entry.

8.  Make sure that the redmine connector is set as a module in esb-connectors parent pom.
            <module>redmine/redmine-connector/redmine-connector-1.0.0/org.wso2.carbon.connector</module>


9.  Navigate to "{ESB_CONNECTORS_HOME}/" and run the following command.
          $ mvn clean install

   
     credential of test account:
     API URL: http://connector.m.redmine.org
     username: connector
     password: esbconnector
