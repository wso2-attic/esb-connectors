Product: Integration tests for WSO2 ESB PeopleHR connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

Note:
	This test suite can be executed based on two scenarios.
		1. Use the given test account and parameters at the end of the file.
		2. Set up a new PeopleHR account and follow all the instruction given below in step 5.

Steps to follow in setting integration test.

 1. Download ESB 4.8.1 from official website.

 2. Deploy relevant patches, if applicable.

 3. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{PeopleHR_Connector_Home}/peoplehr-connector/peoplehr-connector-1.0.0/org.wso2.carbon.connector/repository/".

 4. Create a PeopleHR trial account and derive the API Key.
	i) 		Using the URL "http://www.trial.peoplehr.com/" create a PeopleHR trial account.
	ii)		Login to the created People HR account and go to Settings >> API >> Under API Key Management >> Create an API Key.
	iii)	Create at least two projects and at least one task should be added for each of them. 
			Go to Settings >> Timesheets >> Project/Task >> Select '+(Manage This List)' from the drop down list and add project/task.
			Keep the project names and the task names for further reference.

 5. Update the PeopleHR properties file at location "{PeopleHR_Connector_Home}/peoplehr-connector/peoplehr-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	Use "https://api.peoplehr.net".
	ii) 	apiKey							-   Use the API Key obtained under Step 4 ii).
	iii)	empIdMandatory					- 	Use a unique employee ID.
	iv)		empIdOptional					- 	Use a unique employee ID.
	v)		firstName						- 	Use a valid string as the first name of the employee.
	vi)		lastName						-   Use a valid string as the last name of the employee.
	vii)	startDate			   			-   Use a valid working date in the format of YYYY-MM-DD.
	viii)	jobRole			        		-   Use a valid job role to which the employee can be assigned.
	ix)	    department			    		-   Use a valid department name where the employee can be appointed(if a non existing department name is used, then the department will be automatically created and be used).
	x)	    empEmail			    		- 	Use a valid and unique email for the employee.
	xi)	    empDateOfBirth					- 	Use a valid date in the format of YYYY-MM-DD.
	xii)	effectiveFromDate				- 	Use a valid working date in the format of YYYY-MM-DD.
	xiii)	salaryType			    		- 	Use a proper salary type (e.g:-Annual).
	xiv)	salaryAmount					- 	Amount of the salary in the format of D.DDDD (D stands for a Digit).
	xv)	    timesheetProject				- 	Use the project name to which the time sheet is created for. Use a project name obtained under Step 4 iii).
	xvi)	projectTimeSheetquantity		- 	Use a quantity with the format D.DD (D stands for a Digit).
	xvii)	projectTimeSheetTask			- 	Use a valid task for the project time sheet. Use a task name obtained under 4 iii).
	xviii)	updatedTimesheetProject			- 	Use the project name to which the time sheet is created for.Use a project name obtained under Step 4 iii)which is not same as 'timesheetProject'.
	xix)	updatedProjectTimesheetQuantity	- 	Use a quantity with the format D.DD (D stands for a Digit).Provide a value different to 'projectTimeSheetquantity'.
	xx)	    updatedProjectTimesheetTask		- 	Use a valid task for the project time sheet.Use a task name obtained under 4 iii)other than the value given for 'projectTimeSheetTask'.
	xxi)	firstNameUpdated				- 	Use a valid string as the first name of the employee other than the value given for 'firstName'.
	xxii)	empEmailUpdated					- 	Use a valid and unique email for the employee other than the value given for 'empEmail'.
	xxiii)	leavePaidStatus					- 	Use a valid leave paid status.
	xxiv)	testComment						- 	Mention a valid string value for comments.
	xxv)	timeIn1							- 	A valid time in the format of hh:mm.
	xxvi)	timeIn1update					- 	A valid time in the format of hh:mm. This should be a different time than timeIn1.
	xxvii)	leaveDate						- 	Use a valid future working date in the format of YYYY-MM-DD.
	xxviii)	timesheetDate					- 	Use a valid future working date in the format of YYYY-MM-DD.
	xxix)	timesheetDateOpt				- 	Use a valid future working date in the format of YYYY-MM-DD.
	
	Note :- 
		1. empIdMandatory, empIdOptional,empEmail and empEmailUpdated needs to be set with unique values before running the integration test teach time.
		2. leaveDate, timesheetDate and timesheetDateOpt should contain three different date values.
	
 6. Navigate to "{PeopleHR_Connector_Home}/peoplehr-connector/peoplehr-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
	  
	  Note:- People HR trial account expires within 30 days.

		