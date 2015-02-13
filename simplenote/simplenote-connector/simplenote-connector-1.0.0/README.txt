Product: Integration tests for WSO2 ESB Simplenote connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

Steps to follow in setting integration test.

 1. Download ESB 4.8.1 from official website.

 2. Deploy relevant patches, if applicable and the ESB should be configured as below.
	 Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
		
	<messageFormatter contentType="text/html" 
				class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
																
	<messageBuilder contentType="text/html" 
				class="org.wso2.carbon.relay.BinaryRelayBuilder"/>	

 3. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{Simplenote_Connector_Home}/simplenote-connector/simplenote-connector-1.0.0/org.wso2.carbon.connector/repository/".

 4. Create a Simplenote account and derive the auth token.
	i) 		Using the URL "https://app.simplenote.com/signup/" create a Simplenote account.
	ii)		Obtain a token by making an HTTP POST call to the login method as described in the SimpleNote api documentation (http://kendersec.github.io/SimpleNote/SimpleNote-API-v2.1.3.pdf) under 'Authentication' section. 

 5. Update the Simplenote properties file at location "{Simplenote_Connector_Home}/Simplenote-connector/Simplenote-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL specific to the created account.
	ii) 	authToken						-   Use the auth token obtained under Step 4 ii).
	iii)	email							- 	Use the email address which was used to create the SimpleNote account under step 4. i)
	iv)		esbCreatetagNameMandatory		-	A valid string as a new tag name.
	v)		esbCreatetagNameOptional		-	A valid string as a new tag name.
	vi)		esbIndexOptional				-	A valid integer as the tag index.
	vii)	createNoteMandatoryContent		-	A valid string as the content of the note. 
	viii)	createNoteOptionalContent		-	A valid string as the content of the note.
	ix)	    createNoteCreateDate			-	A valid date as the creation date of the note in the format in seconds since epoch (e.g:- 1421412755.547400).
	x)	    updateNoteOptionalContent		-   A valid string as the content of the note. Use a string that is different to 'createNoteMandatoryContent'.
	xi)	    updateNoteCreateDate			-   A valid date as the creation date of the note in the format in seconds since epoch (e.g:- 1421412755.547400). Use a date different to the value used in 'createNoteCreateDate'.
	xii)	length							-   Use a valid integer as the number of entries to be returned in the testListNoteIndexesWithOptionalParameters.
	
	Note:-Change the value of 'esbCreatetagNameOptional' to a different value before each time you run the integration test.
		
 6. Navigate to "{Simplenote_Connector_Home}/simplenote-connector/simplenote-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

		