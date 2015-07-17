Product: Integration tests for WSO2 ESB Box connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is rquired. this test suite has been configred to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 14.04
 - WSO2 ESB 4.9.0-ALPHA

STEPS:

 1. Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/

 2. Create a Box account and obtain a accesstoken
	Note: A user can create up to four types of Box accounts as Personal,Starter,Business and Enterprise. The below steps can be followed to create a Personal account which can be created free of charge.
	  i) Using the URL "https://app.box.com/signup/personal/" create a Box account. Verify Your email address and access the created Box account.
	 ii) Login to the created account and create a Box Application and Configure your Application where you will be directed to a page to generate your access token.
    iii) Provide a redirect_uri under the 'OAuth2 Parameters' section and select 'Manage an enterprise' option in front of 'Scopes' field.
     iv) Click on 'Create a developer token' button to generate the access token. The expiration time of the generated access token will be displayed on the page itself thus the access token is valid  till that.

 3. Prerequisites for Box Connector Integration Testing

    Login to the Box account you have just created and follow these steps before start testing.	 
	
	a) Create a folder. This will be considered as the parent folder and know the folder id of this folder as the parentId. 
	
	b) Upload a file to root and share the file. While sharing make sure to set a password. Note the id of the file, shared link and the password as test data.
	 
	c) Create a new User by following the below steps.
		 i) Login to Box Account and go to Admin Console.
		
		ii) Select Users and Groups and then by clicking +Users, create a new user.		
	 
 4. Update the box properties file at location "{PATH_TO_SOURCE_BUNDLE}/box-connector/box-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
   
	Required to change on every test run :
	
	 i) folderName1 - Desired name for the folder that will be created during 'testCreateFolderWithMandatoryParameters'  test case execution.
	
    ii) folderName2 - Desired name for the folder that will be created during 'testCreateFolderWithOptionalParameters'  test case execution.
	
   iii) copyFolderName - Desired name which needs to be given for the copied folder.
	
    iv) updatedFolderName - Desired name which needs to be given while updating a folder.
	
     v) updatedFileName - Desired name which needs to be given while updating a file.
	
	
	Optionally can change on every test run :
	
    vi) accessToken - Use the access token you got from step 2.
	
   vii) parentId - The id of the folder that will be created as the parent folder under step 3 a)
    
  viii) timeOut - Required time-out value in milliseconds.
    
    ix) sourceUserId - Id of the user which will be created under step 3 c).
   
     x) targetUserId - Id of the current user.
	
	xi) sourceUserName - First name of the user which will be created under step 3 c).
	
   xii) multipartProxyName - Name of the proxy file which will be used to execute upload file test cases.
   
  xiii) uploadFileName - Name of the file (with extension) which is used to be uploaded. This file will be stored at "{PATH_TO_SOURCE_BUNDLE}/box-connector/box-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/config/resources/box/"
   
   xiv) targetFileName - Desired name that should be given for the uploading file (uploadFileName will be replaced as this).
	
	xv) version - version of the file
	
   xvi) commentMessage - The comment that needs to be given while adding comments to a file.
	
  xvii) query - Search query which will be used for search method.
   
 xviii) sharedLink - The shared link of the file which is mentioned under step 3 b).
   
   xix) sharedLinkPassword - Password that is set while sharing the file mentioned under step 3 b).
	
	xx) recursive - Boolean value which will be set to true if the files inside a folder needs to be deleted during a folder delete.
    
 5. 5. Make sure that the box connector is set as a module in esb-connectors parent pom.
          <module>box/box-connector/box-connector-1.0.0/org.wso2.carbon.connector</module>

    6. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
          $ mvn clean install

 NOTE : Following Box account, can be used for run the integration tests.
    Username : wso2connector.abdera@gmail.com
    Password : wso2connector@2013
