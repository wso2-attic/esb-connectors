Product: Integration tests for WSO2 ESB Github connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "Integration_Test/products/esb/4.8.1/modules/integration/connectors/repository"

 2. This ESB should be configured as below;
	In Axis configurations (/repository/conf/axis2/axis2.xml).

    i) Enable message formatter for "application/json" in messageFormatters tag
			<messageFormatter contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamFormatter"/>

   ii) Enable message builder for "application/json" in messageBuilders tag
			<messageBuilder contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

 3. Make sure "integration-base" project is placed at "Integration_Test/products/esb/4.8.1/modules/integration/"

 4. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/integration-base" and run the following command.
      $ mvn clean install

 5. Copy the main and the test folders from the provided Github connector source bundle to the location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/". Copy pom.xml from the source bundle to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/". When running integration tests, uncomment fhe following two blocks from pom.xml:

	<parent>
		<groupId>org.wso2.esb</groupId>
		<artifactId>esb-integration-tests</artifactId>
		<version>4.8.1</version>
		<relativePath>../pom.xml</relativePath>
	</parent>

	<dependency>
		<groupId>org.wso2.esb</groupId>
		<artifactId>org.wso2.connector.integration.test.base</artifactId>
		<version>4.8.1</version>
		<scope>system</scope>
		<systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>
	</dependency>

 
 
 6. Make sure the Github test suite is enabled (as given below) and all other test suites are commented in the following file - "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/testng.xml"  
     <test name="Github-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.github"/>
        </packages>
    </test>

7. Create a Github account and obtain a accesstoken

	  i) Using the URL "https://github.com" create a Github account. Verify Your email address and access the created Github account.
	 ii) Login to the created account and create a Github Application and Configure your Application where you will be directed to a page to generate your access token.
    iii) Go to following URL "https://github.com/settings/applications" and click "Generate new token" in Personal access tokens section. 
     iv) Give a token description and select all checkboxes given and click "generate token" button.

8. Prerequisites for Github Connector Integration Testing

    Login to the Github account you have just created and follow these steps before start testing.	 
	
	a) Create a new repository with a readme file. This will be considered as your repository for integration tests.

	b) Add 2 files to master branch in that repository (eg:- fileA and fileB). Create two branches (e.g:-branchA and branchB) from the master branch and edit fileA in branchA and fileB in branchB.

	c) Create 4 new branches from the master branch and add 2 different files in 2 of those branches.
	 
	d) Goto your repository,select one of the created branch in step b) and click on icon pull request which indicates a tool tip as "Compare, review, create a pull request", click "Create pull request" button and then Click "Send pull request" button. Repeat the same for the other branch which is created in step b). 

	e) Note that these steps will need to be repeated for each individual run of the Integration Tests. 		
	 
 9. Update the github properties file at location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config" as below.
 
      i) accessToken - Use the access token you got from step 7.
	
	 ii) mediaType - Use "application/json"
	
    iii) owner - Use owner of the repository (Basically this will be the user name).
   
     iv) repo - The repository name you mentioned under step 8 a).
   
      v) user - The username(Given in step iii.).
	
     vi) pullRequestNumber - One of the pull request number you obtained under step 8 d).
	
    vii) pullRequestNumberOptional - The other pull request number you obtained under step 8 d).
   
   viii) content - The content which need to create blob. It should be encoded based on the encoding type
   
     ix) encoding - Use "utf-8" or "base64".
	
	  x) base - Give the name of one of the two branches from the 4 branches that were created in step 8 c) where files were not added or edited.
	
     xi) baseOptional - Give the name of the other branch which was not changed as above.
   
    xii) head - Give the name of one of the branches created in step 8 c) where files were added or edited.
   
   xiii) headOptional - Give the name of the other branch which was changed as above.
	
	xiv) commitMessage - Commit description.
	
	 xv) ref - Use "refs/heads/chamath1" as ref.
	
    xvi) issueTitle - Title of the issue.
   
   xvii) issueBody - The issue description.
   
  xviii) issueCommentBody - The comment description.

    xix) timeOut - 30000

 10. Extract the certificate from browser by navigating to https://api.github.com and place the certificate file in following location. 

 "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/keystores/products"

 Then execute "keytool -import -trustcacerts -alias root -file CERT_FILE_NAME -keystore wso2carbon.jks" in command line to import github certificate in to keystore. Give "wso2carbon" as password.
 NOTE : CERT_FILE_NAME is the file name which was extracted from github with  the extension, change it accordingly. 
    
 11. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
      $ mvn clean install

 NOTE : Following Github account, can be used for run the integration tests.
    Username : wso2connector.abdera@gmail.com
    Password : connector1234
