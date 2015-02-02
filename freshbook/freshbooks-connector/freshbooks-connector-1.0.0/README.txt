Product: Integration tests for WSO2 ESB Freshbooks connector
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
		1. Use the given test account and parameters at the end of the file. - in this scenario you only need to replace apiUrl, authenticationToken and arbitraryPassword in property file.
		2. Set up new freshbooks account and follow all the instruction given below in step 5.
	
Steps to follow in setting integration test.
 1.  Download ESB 4.8.1 from official website.
 2.  Deploy relevant patches, if applicable.

 3.  Navigate to location "/wso2esb-4.8.1/repository/conf/axis2" and add/uncomment following lines in "axis2.xml" and Message Formatters and Message Builders should be added for each of the content types of the files to be added as attachments.
		
		Message Formatters :-
		
		<messageFormatter contentType="image/png" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		<messageFormatter contentType="multipart/related" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		
		Message Builders :-
		
		<messageBuilder contentType="image/png" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
		<messageBuilder contentType="multipart/related" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
			
 4.  Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{FRESHBOOKS_CONNECTOR_HOME}/freshbooks-connector/freshbooks-connector-1.0.0/org.wso2.carbon.connector/repository/".

         
 5.  Prerequisites for Freshbooks Connector Integration Testing

     Follow these steps before start testing.
	 
     a)  Create a fresh account in Freshbooks using the URL https://secure.freshbooks.com/external/subscribe/prices with the web browser.
     b)  Freshbooks automatically creates an account for the company name provided and will redirect to the home page of the account and the following information should be retrieved from the account:
	 
			1) The API URL (Account URL) send to the Email account.
			2) The Authentication Token should be retrieved from following location:
					My Account -> FreshBooks API
			3) The authentication method of Freshbooks requires no password and any arbitrary value can be provided as the password.
		 Copy this information and store it in a safe place.
		
     c)  A default Staff member is created for the account automatically and If needed, a new staff can be created through following path.
			People -> Staff And Contractors -> New Team Member -> Add Staff Member
     d)  Retrieve the staff ID of the default user or the created user from the URL in the address bar of the browser when the corresponding staff member is selected in the Staff And Contractors list in following location.
			People -> Staff And Contractors
	 e)  Add the files which are used for creating and updating receipts to the following location and update the values of the parameters 7 and 8 in section f corresponding to the added files.
			Location: "{FRESHBOOKS_CONNECTOR_HOME}/freshbooks-connector/freshbooks-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/config/restRequests/"

     f) Update the Freshbooks properties file at location "{FRESHBOOKS_CONNECTOR_HOME}/freshbooks-connector/freshbooks-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.	 
		Following fields in the property file should be updated appropriately.
	  
	    1) staffId - the staff ID (user ID) of an existing staff member which is retrieved in step d above.
		2) categoryName - a unique and non-existing name as the category name to be created.
		3) categoryNameUpdated - a unique and non-existing name as the category name to be used for updating the created category.
		4) taxName - a unique and non-existing name as a tax name to be created.
		5) taxNameOptional - a unique and non-existing name as a tax name to be created.
		6) taxNameUpdated - a unique and non-existing name as the tax name to be used for updating the created tax.
		7) uploadFileName - name of the file to be uploaded as the attachment in creating a receipt.
		8) updatedFile - name of the file to be uploaded as the attachment in updating a receipt.

 6. Navigate to "{FRESHBOOKS_CONNECTOR_HOME}/freshbooks-connector/freshbooks-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
     $ mvn clean install

	 
     credential of test account:
     API URL: https://sansu.freshbooks.com
     username: wso2connector.abdera@gmail.com
     password: connector@123
     Authentication Token: c361a63c7456519412fa8051ea605a6d
