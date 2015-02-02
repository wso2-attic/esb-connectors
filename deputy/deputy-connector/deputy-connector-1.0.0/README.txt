Product: Integration tests for WSO2 ESB Deputy connector

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

 3. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{Deputy_Connector_Home}/deputy-connector/deputy-connector-1.0.0/org.wso2.carbon.connector/repository/".

 4. Create a Deputy trial account and derive the API Key.
	i) 		Using the URL "http://www.deputy.com/" create a Deputy trial account.
	ii)		Login to the created Deputy account and go to https://{Unique Deputy URL for created account}.deputy.com/exec/devapp/oauth_clients Create a dummy client of yours and Get a permanent Access Token from there.

 5. Update the Deputy properties file at location "{Deputy_Connector_Home}/deputy-connector/deputy-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL specific to the created account.
	ii) 	apiKey							-   Use the API Key obtained under Step 4 ii).
	iii)	leaveStartDate					- 	Use a valid future date as the start date of the leave period. Follow the format DD/MM/YYYY.
	iv)		leaveEndDate					- 	Use a valid future date as the end date of the leave period. This shouldn't be a past value than 'leaveStartDate'. Follow the format DD/MM/YYYY.
	v)		leaveStatus						- 	Use a valid status of a leave as documented in the API documentation.
	vi)		leaveApprovalComment			-   Use a string value as the approval comment of the leave.
	vii)	intStartTimestamp			    -   Use a valid UNIX future timestamp. Note that this time must be of a working day as well as during the working hours.
	viii)	intEndTimestamp			   		-   Use a valid UNIX future timestamp.Note that this time must be of a working day as well as during the working hours. Also note that 'intEndTimestamp' value must be after 'intStartTimestamp' value.
	ix)	    intOpunitId			        	-   Use a valid Operational unit ID (Operational Unit 3 is valid and created by default).
	x)	    blnPublish			    		-   Use a value 1 or 0 as the boolean value representing the roster is published.
	xi)	    companyId			    		-   Use a valid company ID (Default is 1).
	xii)	employeeFirstName			    - 	Use a valid string value as employee's first name.
	xiii)	employeeLastName			    - 	Use a valid string value as employee's last name.
	xiv)	employeeEmail					- 	Use a valid and a unique email address.
	xv)	    employeeDateOfBirth				- 	Use a valid date in the format of YYYY-MM-DD as the birthday of the employee.
	xvi)	updateEmployeeFirstName			- 	Use a valid string value as the updated first name of the employee. Use a value different than employeeFirstName.
	
	Note :- leaveStartDate, leaveEndDate, intStartTimestamp, intEndTimestamp, employeeEmail, updateEmployeeFirstName and employeeFirstName needs to be set with unique values before running the integration test each time.
	
 6. Navigate to "{Deputy_Connector_Home}/deputy-connector/deputy-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
	  
	  Note:- Deputy trial account expires within 30 days.

		