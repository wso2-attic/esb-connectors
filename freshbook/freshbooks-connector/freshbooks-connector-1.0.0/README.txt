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

Steps to follow in setting integration test.
 1.  Download ESB 4.9.0-ALPHA from official website.

 3.  Navigate to location "/wso2esb-4.9.0-ALPHA/repository/conf/axis2" and add/uncomment following lines in "axis2.xml" and Message Formatters and Message Builders should be added for each of the content types of the files to be added as attachments.

        Message Formatters :-

        <messageFormatter contentType="image/png" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
        <messageFormatter contentType="multipart/related" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

        Message Builders :-

        <messageBuilder contentType="image/png" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
        <messageBuilder contentType="multipart/related" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 4.  Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "<ESB_CONNECTORS_HOME>/repository/".
    
 5.  Prerequisites for Freshbooks Connector Integration Testing

     Follow these steps before start testing.
	 
	 NOTE:- Freshbooks restricts only one client to be created in a free trial. So make sure that there are no any client exist in the account before running the integration each time.

     a)  Create a fresh account in Freshbooks using the URL http://www.freshbooks.com/signup with the web browser.
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
	 e)  Create two tasks by navigating to Time Tracking -> Tasks and keep the two task ids for further reference.
     f)  Add the files which are used for creating and updating receipts to the following location and update the values of the parameters 10 and 11 in section 'g' corresponding to the added files.
            Location: "<FRESHBOOKS_CONNECTOR_HOME>/freshbooks-connector/freshbooks-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/config/restRequests/freshbooks"
     g) Update the Freshbooks properties file at location "<FRESHBOOKS_CONNECTOR_HOME>/freshbooks-connector/freshbooks-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.	 
        Following fields in the property file should be updated appropriately.

        1)  apiUrl               	- API URL to which the service calls are made. e.g. https://virasoft.recurly.com
        2)  authenticationToken 	- The authentication token retrieved in the Step b - 2.
        3)  arbitraryPassword    	- The arbitrary password as explained in Step b - 3. (Default value is x).
        4)  staffId              	- The staff ID (user ID) of an existing staff member which is retrieved in Step d above.Most of the time the value of this is '1'.
        5)  categoryName         	- A unique and non-existing name as the category name to be created.
        6)  categoryNameUpdated  	- A unique and non-existing name as the category name to be used for updating the created category.
        7)  taxName             	- A unique and non-existing name as a tax name to be created.
        8)  taxNameOptional     	- A unique and non-existing name as a tax name to be created.
        9)  taxNameUpdated       	- A unique and non-existing name as the tax name to be used for updating the created tax.
        10) uploadFileName       	- The name of the file to be uploaded as the attachment in creating a receipt.
        11) updatedFile          	- The name of the file to be uploaded as the attachment in updating a receipt.
		12)	projectName			 	- A valid string as the new project name.
		13) projectBillMethod	 	- A method of billing for the project. Always use 'task-rate' for this.
		14) projectNameOptional  	- A valid string as the new project name. Use a different name than that is used for 'projectBillMethod'.
		15) projectRate          	- A valid integer as the billing rate for the project.
		16) projectDescription   	- A valid string to describe the project.
		17) projectHourBudget	 	- A valid integer as the hourly budget for the project.
		18) timeEntryTaskId		 	- Use a valid task Id that was obtained from step 5 e).
		19) timeEntryHours		 	- A valid integer as the number of hours of the timeEntry.
		20) timeEntryNote			- A valid string as the description for the timeEntry.
		21) timeEntryDate			- A valid date in the format of YYYY-MM-DD  e.g:-2015-05-10.
		22) timeEntryPage		 	- A valid integer as the number of the time entry page to be retrieved. Always use '1' for this.
		23) timeEntryPerPage	 	- A valid integer as on how many records to be retrieved in one page when retrieving time entry records. Always use a value greater than 2.
		24) timeEntryDateFrom	 	- A valid date in the format of YYYY-MM-DD  e.g:-2015-05-10.
		25) timeEntryTaskIdUpdated	- Use a valid task Id that was obtained from step 5 e).Use a different value than that is used for 'timeEntryTaskId'.
		26) timeEntryHoursUpdated	- A valid integer as the number of hours of the timeEntry. Use a different value than that is used for 'timeEntryHours'.
		27) timeEntryNoteUpdated	- A valid string as the description for the timeEntry. Use a different value than that is used for 'timeEntryNote'.
		28) timeEntryDateUpdated 	- A valid date in the format of YYYY-MM-DD  e.g:-2015-05-10. Use a different date than that is used for 'timeEntryDate'.

		NOTE:- taxName,taxNameOptional,taxNameUpdated values should be changed before executing the integration test each time. 
		
 5. Make sure that the freshbook connector is set as a module in esb-connectors parent pom.
        <module>freshbook/freshbooks-connector/freshbooks-connector-1.0.0/org.wso2.carbon.connector</module>

 6. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
          $ mvn clean install