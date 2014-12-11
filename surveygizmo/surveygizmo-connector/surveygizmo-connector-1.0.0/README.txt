Product: Integration tests for WSO2 ESB SurveyGizmo connector

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
		2. Set up a new SurveyGizmo account and follow all the instruction given below in step 5.

Steps to follow in setting integration test.

 1. Download ESB 4.8.1 from official website.
 
 2. Deploy relevant patches, if applicable.
 
 3. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{surveygizmo_connector_Home}/surveygizmo-connector/surveygizmo-connector-1.0.0/org.wso2.carbon.connector/repository/".

 4. Prerequisites for SurveyGizmo Connector Integration Testing

		i) 	Create a SurveyGizmo account using the URL "https://app.surveygizmo.com/login/v1".
			Note: Sign-up by clicking 'Try it out for free for 7 days!' when creating a new SurveyGizmo account. This account will be expired after the trial period of 7 days.
			
		ii) Once it is completed create a new survey and click on the test tab in the menu bar.Create at least 10 test responses by clicking 'Generate Test Responses'.
		

 5. Update the SurveyGizmo properties file at location "{surveygizmo_connector_Home}/surveygizmo-connector/surveygizmo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl						-	Use the API URL as "https://restapi.surveygizmo.com".
	ii)		username					-	Place the username from the created account in step 4 [i].
	iii)	password					-	Place the MD5 converted password from the created account in step 4 [i].
	iv)		surveyTitle					-	Title of the survey.
	v)		surveyType					-	Type of the survey.
	vi)		surveyInternalTitle			-	Internal title for create survey.
	vii)	surveyUpdatedInternalTitle	-	Title to set when updating the surevey title. 
	viii)	surveyTheme					-	Theme of the survey.
	ix)		surveyUpdatedTheme			-	Theme to set when updating the surevey.
	x)		surveyStatus				-	Status of the survey.
	xi)		surveyUpdatedStatus			-	Status to set when updating the survey.
	xii)	campaignType				-	Type of the campaign .
	xiii)	campaignName		        -	Name of the campaign. 
	xiv)	updatedCampaignName			-	Name to set when updating the campaign.Use different value from xiii)
	xv)		campaignLanguage			-	Language of the campaign.
	xvi)	campaignStatus				-	Status of the campaign.
	xvii)	updatedCampaignLanguage		-	Status to set when updating the campaign.Use different value from xv)
	xviii)	surveyIdToListResponse		-	Place the survey Id from the created survey account in step 4[ii]. 
	
		
 6. Navigate to "{surveygizmo_connector_Home}/surveygizmo-connector/surveygizmo-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install


 NOTE : Following are the credentials for the SurveyGizmo account used for integration tests.
 
	    email=sampath.liyanage@myport.ac.uk
	    password=1qaz2wsx@	
