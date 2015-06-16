Product: Integration tests for WSO2 ESB GoToTraining connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-ALPHA

Note:
	This test suite can be executed by setting up a new GoToTraining trial account and a Citrix account and follow all the instruction given below in step 4 and 5.

Steps to follow in setting integration test.

 1. Download WSO2 ESB 4.9.0-ALPHA from official website.

 2. Deploy relevant patches, if applicable and the ESB should be configured as below.
																				           
 3. Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "{ESB_Connector_Home}/repository".

 4. Create a Citrix account and obtain a client ID.
	i) 		Using the URL "https://developer.citrixonline.com/user/register" create a Citrix online account.
	ii)		Login to the Citrix account and go to 'My Apps' and add a new app.
	iii)	Select the above created app and obtain the Consumer Key.

 5. Create a GoToTraining trial account and obtain the access token and the organizer_key.
	i)		Using the URL "https://secure.citrixonline.com/secure/gototraining/commerce/try/register" create a Goto training free trial account using following details.
			a)Select number of organizer seats as 9.
			b)Enter the relevant information to create the account.Me!
    ii)	    Follow the instructions in the 	"https://developer.citrixonline.com/page/direct-login" and make a GET request to the URL "https://api.citrixonline.com/oauth/access_token" passing the below parameters
			a)grant_type -   Use "password".
			b)user_id    -   User email address that you used to create the GoTo Training account.
			c)password 	 -   Password of your GoTo Training account.
			d)client_id  -	 Use the Consumer Key you obtained under Step 4 iii).
	iii)	Get the ornanizer_key and the access token from the response.
			
 6. Update the GoToTraining properties file at location "{ESB_Connector_Home}/gototraining/gototraining-connector/gototraining-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 									- 	Use "https://api.citrixonline.com".
	ii)		accessToken								-	Use the access token obtained in Step 5 iii).
	iii)	organizerKey							-	Use the ornanizer_key obtained in Step 5 iii).
	iv)		createTrainingStartDate					-	Use a valid future date in the ISO date time format(e.g: 2015-11-25T07:00:00Z).
	v)		createTrainingEndDate					-   Use a valid future date in the ISO date time format(e.g: 2015-11-25T09:00:00Z).This date should be a date comes after 'createTrainingStartDate'.
	vi)		createTrainingDescription				-	Use a valid string as the description of the training.
	vii)	createTrainingName						-	Use a valid name for the name of the training.	
	viii)	createTrainingTimeZone					-	Use a valid time zone (e.g: GMT).
	ix)		createTrainingDisableWebRegistration	-	Use either 'true' or 'false'.
	x)		createTrainingDisableConfirmationEmail	-	Use either 'true' or 'false'.
	xi)		createTrainingNegativeStartDate			-	Use a past date in the ISO date time format(e.g: 2013-11-25T09:00:00Z).
	xii)	updateTrainingName						-	Use a valid name for the training that is different than the value given for 'createTrainingName'.
	xiii)	updateTrainingDescription				-	Use a valid string as the description of the training that is different than than the description given for 'createTrainingDescription'.
	xiv)	update									-	Use "times".
	xv)		updateTrainingStartDate					-	Use a valid future date in the ISO date time format(e.g: 2015-11-25T07:00:00Z). Use a date different to 'createTrainingStartDate'.
	xvi)	updateTrainingEndDate					-	Use a valid future date in the ISO date time format(e.g: 2015-11-25T07:00:00Z). Use a date different to 'createTrainingEndDate' and this should be a past date to 'updateTrainingStartDate'.
	xvii)	updateTimeout							-	Use '5000'.
	xviii)	email									-	Use a valid user email address.
	xix)	surname									-	User a valid string as the surname.
	xx)		givenName								-	User valid string as the given name.
	
	Note:- When providing dates for 'createTrainingStartDate','createTrainingEndDate','updateTrainingStartDate','updateTrainingEndDate' provide recent dates that falls within the trial period of the current account.
		
 7. Make sure that the Simplenote connector is set as a module in esb-connectors parent pom.
        <module>gototraining/gototraining-connector/gototraining-connector-1.0.0/org.wso2.carbon.connector</module>

 8. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install
	  
	  Note:- GoToTraining trial account expires within 30 days.

		