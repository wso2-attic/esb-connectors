Product: Integration tests for WSO2 ESB Confluence connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Ubuntu 12.04, Mac OSx 10.9
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7
		  
STEPS:
1. Download ESB 4.9.0-ALPHA and copy the wso2esb-4.9.0-ALPHA.zip in to location "{ESB_Connector_Home}/repository/".

2. Update connector properties file "confluence.properties" located in "{Confluence_Connector_Home}/src/test/resources/artifacts/ESB/connector/config/",
with following information.

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

3. Give random values to the following properties. These are mostly to be used for negative test cases. In most cases no need to change these values.

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

4.  Make sure that confluence is specified as a module in ESB_Connector_Parent pom.
       <module>confluence/confluence-connector/confluence-connector-1.0.0/org.wso2.carbon.connector</module>

5.  Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install

