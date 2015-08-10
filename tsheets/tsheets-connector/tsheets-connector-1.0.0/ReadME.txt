Product: Integration tests for WSO2 ESB TSheets connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04, Mac OSx 10.9
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

Note:
	This test suite can be executed based on two scenarios.
		1. Use the given test account and parameters at the end of the file.
		2. Set up a new TSheets account and follow all the instruction given below in step 5.

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-ALPHA and compress ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 2. Follow the below steps to create a TSheets account.

	i)	Navigate to "http://www.tsheets.com/" and click on "TRY FREE" to sign up as a new user. This account will be expired after the trial period of 14 days.
    ii) Enter the required details and complete the account creation.

 3. Follow the steps in the below link to obtain the access token.

	i) 	Naviagte to the link "http://developers.tsheets.com/docs/api/authentication" and follow the instructions given to obtain the access token.

 4. Prerequisites for Tsheets Connector Integration Testing

	i) Atleast two users must be created in the TSheets account as the inital step to continue.
	ii) Atleast two job code assignments need be created in the TSheets account as the inital step to continue.


 5. Update the TSheets properties file at location "{tsheets_connector_Home}/tsheets-connector/tsheets-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

	i)		apiUrl						-	Use the API URL as "https://rest.tsheets.com".
	ii)		accessToken					-	Place the accesstoken obtained in step 3[i].
	iii)	jobCodeOneName 				-	Name of a job code.
	iv)		jobCodeTwoName				-	Name of a job code.
	v)		timeSheetType				-	Type of the Timesheet.
<<<<<<< HEAD
	vi)		timeSheetOneStart			-	Starting time of a time sheet to be created (Unique).
	vii)	timeSheetOneEnd			 	-	Ending time of a time sheet to be created (Unique).
	viii)	timeSheetTwoStart			-	Starting time of a time sheet to be created (Unique).
	ix)		timeSheetTwoEnd				-	Ending time of a time sheet to be created (Unique).

	NOTE : Properties numbered as (vi),(vii),(viii),(ix) should change the values after every test run and those dates should be past dates in order to create timeslips successfully.
=======
>>>>>>> upstream/master

 6. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install


 NOTE : Following are the credentials for the TSheets account used for integration tests.
 	    email=sasitest12@gmail.com
	    password=yasasi123