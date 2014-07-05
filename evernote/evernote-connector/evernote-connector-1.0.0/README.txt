Product: Integration tests for WSO2 ESB evernote connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is rquired. this test suite has been configred to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{PATH_TO_SOURCE_BUNDLE}/evernote-connector/evernote-connector-1.0.0/org.wso2.carbon.connector/repository/"


 2. Create a Evernote account and derive the developer token:
	i) 	Using the URL "http://evernote.com/sign-up/" create a Evernote account.
	ii) Derive the developer token,notestore url and developer token type from https://www.evernote.com/api/DeveloperToken.action.



 3. Update the Evernote properties file at location "{PATH_TO_SOURCE_BUNDLE}/evernote-connector/evernote-connector-1.0.0/src/test/resources/artifacts/ESB/connector/config" as below.

		i) developerToken - Use the developer token you got from step 3.
		ii)noteStoreUrl - Use the notestore url that you got from step 3
		iii)devTokenType - Use the developer token type that you choose from step 3

	Following properties should be changed to facilitate the createTag test cases.
        iv) tagName1 - Tag name to test createTag method
         v) tagName2 - Tag name to test createTag method

	Following properties should be changed to facilitate the updateTag test cases.

        vi) updateTagName - Tag name to test updateTag method

    Following properties should be changed to facilitate the createSearch test cases.

        vii) searchName - Search name to test createSearch method
        viii) query - Search query to test createSearch method. Search queries should be created according to the Evernote search grammar
              defined in https://dev.evernote.com/doc/articles/search_grammar.php. you could use "tag:wso2" query for this test case.

    Following properties should be changed to facilitate the createNote, updateNote test cases. Values should be set based on the properties of the file which will uploaded.
        ix) sourceURL - source url to test createNote and updateNote methods
         x) mime - mime type to test createNote and updateNote methods
         x) noteTitle - note title to test createNote method

    Following properties should be changed to facilitate the createNotebook test cases.
        xi) notebookName -notebook name to test createNotebook test cases

    Following properties should be changed to facilitate the createSharedNotebook test cases.
        xii) email - email address to test createSharedNotebook

 4. Navigate to "{PATH_TO_SOURCE_BUNDLE}/evernote-connector/evernote-connector-1.0.0/" and run the following command.
      $ mvn clean install


 NOTE : Following Evernote account, can be used for run the integration tests.
    Username : wso2evernoteconnector@gmail.com
    Password : evernotecon
    Developer token :S=s424:U=48524b0:E=14e531dc27f:C=146fb6c9600:P=1cd:A=en-devtoken:V=2:H=4a4e288787109ed314ebce861c5c2c92
    Note store url : https://www.evernote.com/shard/s424/notestore
    devTokenType : SANDBOX

