Product: Integration tests for WSO2 ESB Confluence connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Ubuntu 12.04
 - WSO2 ESB 4.8.1
		  
STEPS:
1. Make sure the ESB 4.8.1 zip file with latest patches available at "Confluence_Connector_Home/repository/".

2. Make sure the confluence test suite is enabled (as given below) and all other test suites are commented in the following file - "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\testng.xml"  

     <test name="Confluence-Connector-Test" preserve-order="true" verbose="2">
	<packages>
            <package name="org.wso2.carbon.connector.integration.test.confluence"/>
        </packages> 
    </test>

3. Update connector properties file "gmail.properties" located in "Gmail_Connector_Home/src/test/resources/artifacts/ESB/connector/config/", 
with following information
	-username= Admin username of the Confluence instance
	-password= Password of the admin user
	-uri= Confluence instance URL
	-spaceId=Create new space and give the space ID
	-pageID=Create new Page and give the Page ID
	-attachment= Add an attachment and give the attachment file name
	-versionNumber=Give the version number of the above page
	-blogPageId=Create a blog article and give the page ID
	-lable= Create a new lable and give the lable name
	-pageManagementPosition= Give a valid position (Ex:append)
	-permissionPermit=Valid permission (Ex:VIEWSPACE)
	-entity=Create a group and give the group name
	-userName=Create a new user and give the username
	-groupName=Create a group and give the group name
	-removePageID=Create new page and give the page ID
	-removeSpaceID= Create a new Space and give the space ID
	-removeAttachment= Add new attachment and give the attachment file name
	-movePageID=Create new page and add the page ID
	-moveTargetPageID=Create new Page and give the page ID
	-movePageID2=Create new page and give the page ID
	-removePermissiongroup= Add new group and and give the group name
	-removePermissionSpace=Add new Space and give the space ID
	-removePermissionEntity= Add new entity (user or a group) and give the name
	-removeUsername=Add new user and give the username
	-removeGroup=Add new group and give the group name
	-deactivateUser=Add new user and give the username
4. Give random values to the following properties. These are mostly to be used for negative test cases. In most cases no need to change these values.
	-invalidGroupName
	-invalidUserName
	-invalidSpaceId
	-permissionInvalidPermit
	-invalidLable
	-invalidPageID
	-invalidAttachment
	-invalidPassword
	-addgroupGroupName
	-addUserEmail
	-addUserFullName
	-addUserUserName
	-addUserURL
	-addUserPassword
	-addUserPassword2
	-addGroupInvalidNames
	-addUserEmailOptional
	-addUserFullNameOptional
	-addUserUserNameOptional
5.  Navigate to "Confluence_Connector_Home" and run the following command.
      $ mvn clean install

