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
	iii)	Create at least one department and note the department name for further reference.
	iv)		Create at least two projects and at least one task should be added to each of them. Keep the project names and the task names for further reference.

 5. Update the PeopleHR properties file at location "{PeopleHR_Connector_Home}/peoplehr-connector/peoplehr-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	Use "https://api.peoplehr.net".
	ii) 	apiKey							-   Use the API Key obtained under Step 4 ii).
	iii)	empIdMandatory					- 	Use a unique employee ID.
	iv)		empIdOptional					- 	Use a unique employee ID.
	v)		firstName						- 	Use a valid string as the first name of the employee.
	vi)		lastName						-   Use a valid string as the last name of the employee.
	vii)	gender			    			-   Use a valid gender value.
	viii)	startDate			   			-   Use a valid date in the format of YYYY-MM-DD.
	ix)	    jobRole			        		-   Use a valid job role to which the employee can be assigned.
	x)	    location			    		-   Use a valid location where the employee can be appointed.
	xi)	    department			    		-   Use a valid department where the employee can be appointed. Use the department name obtained under Step 4 iii).
	xii)	empTitle			    		- 	Use a proper title for the employee (e.g:- Mr., Ms., Mr ).
	xiii)	empEmail			    		- 	Use a valid and unique email for the employee.
	xiv)	empDateOfBirth					- 	Use a valid date in the format of YYYY-MM-DD.
	xv)	    effectiveFromDate				- 	Use a valid date in the format of YYYY-MM-DD.
	xvi)	salaryType			    		- 	Use a proper salary type (e.g:-Annual, Monthly).
	xvii)	salaryAmount					- 	Amount of the salary in the format of D.DDDD (D stands for a Digit).
	xviii)	createSalaryComments			-   Use a comment for creating the salary for the employee.
	xix)	projectTimesheetDate			- 	Use a valid date in the format of YYYY-MM-DD.
	xx)	    timesheetProject				- 	Use the project name to which the time sheet is created for. Use a project name obtained under Step 4 iv).
	xxi)	projectTimeSheetquantity		- 	Use a quantity with the format D.DD (D stands for a Digit).
	xxii)	projectTimeSheetTask			- 	Use a valid task for the project time sheet. Use a task name obtained under 4 iv).
	xxiii)	projectTimeSheetNotes			- 	Use some note to be added to project time sheet.
	xxiv)	updatedTimesheetProject			- 	Use the project name to which the time sheet is created for.Use a project name obtained under Step 4 iv)which is not same as 'timesheetProject'.
	xxv)	updatedprojectTimeSheetNotes	- 	Use some note to be added to project time sheet other than the string given for 'projectTimeSheetNotes'.
	xxvi)	updatedProjectTimesheetQuantity	- 	Use a quantity with the format D.DD (D stands for a Digit).Provide a value different to 'projectTimeSheetquantity'.
	xxvii)	updatedProjectTimesheetTask		- 	Use a valid task for the project time sheet.Use a task name obtained under 4 iv)other than the value given for 'projectTimeSheetTask'.
	xxviii)	firstNameUpdated				- 	Use a valid string as the first name of the employee other than the value given for 'firstName'.
	xxix)	lastNameUpdated					- 	Use a valid string as the last name of the employee other than the value given for 'lastName'.
	xxx)	empEmailUpdated					- 	Use a valid and unique email for the employee other than the value given for 'empEmail'.
	xxxi)	leaveStartDate					- 	Use a valid date in the format of YYYY-MM-DD.
	xxxii)	leaveEndDate					-   Use a valid date in the format of YYYY-MM-DD. This should be a future date than 'leaveStartDate'.
	xxxiii)	leaveNewStartDate				- 	Use a valid date in the format of YYYY-MM-DD. This date must be different than 'leaveStartDate'.
	xxxiv)	leaveNewEndDate					- 	Use a valid date in the format of YYYY-MM-DD and the date must be different than 'leaveEndDate'. This should be a future date than 'leaveNewStartDate'.
	xxxv)	leaveDuration					- 	Use the number of days that the leave consists.This should be the gap between the leaveStartDate and leaveEndDate.
	xxxvi)	leavePaidStatus					- 	Use a valid leave paid status.
	xxxvii)	leaveComment					- 	Mention a comment for the leave.
	xxxviii)timesheetDate					- 	Use a valid date in the format of YYYY-MM-DD.
	xxxix)	timesheetStartDate				- 	Use a valid date in the format of YYYY-MM-DD.
	xl)  	timesheetEndDate				- 	Use a valid date in the format of YYYY-MM-DD. This should be a future date than the 'timesheetStartDate'.
	xli)	timeIn1							- 	A valid time in the format of hh:mm.
	xlii)	timesheetDateOpt				- 	Use a valid date in the format of YYYY-MM-DD.
	xliii)	startDateOpt					-	Use a valid date in the format of YYYY-MM-DD.
	xliv)	endDateOpt						- 	Use a valid date in the format of YYYY-MM-DD. This should be a future date than the 'startDateOpt'.
	xlv)	timeIn1update					- 	A valid time in the format of hh:mm. This should be a different date than timeIn1.
	xlvi)	comments						- 	Use a valid comment.

	Note :- empIdMandatory, empIdOptional,empEmail and empEmailUpdated needs to be set with unique values before running the integration test teach time.
	
 6. Navigate to "{PeopleHR_Connector_Home}/peoplehr-connector/peoplehr-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
	  
	  Note:- People HR trial account expires within 30 days.

		