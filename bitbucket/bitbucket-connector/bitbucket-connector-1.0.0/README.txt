Product: Integration tests for WSO2 ESB Bitbucket connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is required. this test suite has been configured to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{BITBUCKET_CONNECTOR_HOME}/bitbucket-connector/bitbucket-connector-1.0.0/org.wso2.carbon.connector/repository/"

 2. This ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).

	<messageFormatter contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
							  
	<messageFormatter contentType="text/html"                             
					  class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
					  
	<messageBuilder contentType="application/json"
					  class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

	<messageBuilder contentType="text/html"                                
					  class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 3. Create a bitbucket account.
	i) 	Using the URL "https://bitbucket.org/account/signup/" to create a bitbucket account.
	ii) Login to the bitbucket account and from "Manage Account" option, observe the username of your account(this username and the password will be used for authentication).
	iii)Create a repository and add an existing project to it using Git. Store the revision ID of the commit for further reference. Add at least one comment to this commit.
	iv) Create five pull requests in the repository mentioned in step 3. iii) without any conflicts and store the pullRequestIDs for further reference. Add a comment to one of the pull request.
	v)  Create two new branches from master branch in the repository mentioned in step 3. iii) and store for further references. 
	vi) Add two commits to one of the branches created under step 3 v)
	
 4. Update the bitbucket properties file at location "{BITBUCKET_CONNECTOR_HOME}/bitbucket-connector/bitbucket-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
   
		i) 		apiUrl - Use https://bitbucket.org	
		ii) 	username - Use the username obtained under step 3 ii)
		iii) 	password - Use the password used to access the bitbucket account created under step 3.
		iv) 	owner - Use the name of the account owner which is as same as the username.
		v) 		repoSlug - Use the identifier of the created repository in bitbucket account mentioned under Step 3 iii).
		vi) 	repoSlugMandatory - A unique identifier to create a repository.Provide a string with no spaces in lower-case.
		vii) 	repoSlugOptional - A unique identifier to create a repository. This should be different than the value of 'repoSlugMandatory'.Provide a string with no spaces in lower-case.
		viii) 	repoName - A unique repository name to create a repository.Provide a string with no spaces in lower-case.
		ix)		revision - A revision ID to retrieve a commit. Use the revision ID obtained from Step 3 iii).
		x)		branchOrTag - A branch name or tag ID to retrieve commits.Use the branch name or tag ID used in commit done under Step     3 iii).
		xi)		exclude - A hash of a commit in a branch mentioned in step x). (This is the value appears at the end of the URL when viewing a commit in a web browser).
		xii)	pullRequestIdComment - A pullRequest Id with commits in that pullRequest.Use the pullRequstID obtained under Step 3 iv) which has the comment.
		xiii)	pullRequestId - A pullRequest Id without any conflicts.Use one of the pullRequestIDs created under Step 3 iv) which is not already used.
		xiv)	pullRequestId1 - A pullRequest Id without any conflicts.Use one of the pullRequestIDs created under Step 3 iv) which is not already used.
		xv)		pullRequestId2 - A pullRequest Id without any conflicts.Use one of the pullRequestIDs created under Step 3 iv) which is not already used.
		xvi)	pullRequestId3 - A pullRequest Id without any conflicts.Use one of the pullRequestIDs created under Step 3 iv) which is not already used.
		xvii)	updatePullRequestMandarotyTitle - A pullRequest title to be updated.
		xviii)	updatePullRequestDescription - A pullRequest description that needs to be updated.
		xix)	updateBranch - A Branch name the pulRequest need to be updated. Use the branch created under Step 3 v) in which there are no commits.
		xx)		commentRevisionId - A commentRevision Id to retrieve comments. Use the commit revision ID obtained in Step 3 iii).
		xxi)	sourceBranchName - A Branch name to put the created pullRequest. Use the branch created in step 3 v) in which a commit added in Step 3 vi). 
		xxii)	sourceRepositoryFullName - The full name of the repository where the pullRequest needs to be made.
		xxiii)	timeOut - 10000
		
		Note: -Following properties should be changed in each run: vi) vii) viii) xvii) xviii)
			  -Property value of 'updateBranch' should be changed between 'master' and branch that was created under Step 3 v) in which there are no commits.
			  -During each run create four new pull requests without conflicts and assign pullRequestIds to xiii),xiv),xv) and xvi) properties respectively.
				
 5. Extract the certificate from browser by navigating to https://bitbucket.org and place the certificate file in following location. 

	 "{BITBUCKET_CONNECTOR_HOME}/bitbucket-connector/bitbucket-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products"

	 Then execute "keytool -import -trustcacerts -alias root -file CERT_FILE_NAME -keystore wso2carbon.jks" in command line to import github certificate in to keystore. Give "wso2carbon" as password.
	 NOTE : CERT_FILE_NAME is the file name which was extracted from github with  the extension, change it accordingly.

 6.	Navigate to "{BITBUCKET_CONNECTOR_HOME}/bitbucket-connector/bitbucket-connector-1.0.0/org.wso2.carbon.connector/" and run the 			following command.
      	$ mvn clean install


 NOTE : Following bitbucket account, can be used to run the integration tests.
    Username : wso2connector.abdera@gmail.com
    Password : connector@123

