Product: Integration tests for WSO2 ESB SurveyMonkey connector

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
  This test suite can execute based on two scenarios.
    1. Use the given test account and parameters.
    2. Create a new SurveyMonkey account and test based on the instructions given below.

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{SURVEYMONKEY_CONNECTOR_HOME}/surveymonkey-connector/surveymonkey-connector-2.0.0/org.wso2.carbon.connector/repository/"

 2. Create a Survey Monkey developer account using the URL "https://developer.surveymonkey.com/" and store the API key you get after successfully registering the application. Then follow the following steps to generate the prerequisites.

 	  i)    Create a survey wich has a collector with at least two recipients and retrieve the survey ID and the collector ID.
 	  ii)	  Retrieve two respondent IDs for the above created survey after the recipients responded to the survey.
 	  iii)  Create another survey with at least one question and retrieve the survey ID.
 	  iv)   Go to the Survey Monkey API console (https://developer.surveymonkey.com/api_console), get access token by clicking on "Get Access Token" button and then providing the valid credentials and clicking on the "Authorize" button.

 3. Update the surveymonkey properties file at location "{SURVEYMONKEY_CONNECTOR_HOME}/surveymonkey-connector/surveymonkey-connector-2.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/surveymonkey.properties" .

    i)    apiUrl		    - Use the URL of the Survey Monkey API (Default value is 'https://api.surveymonkey.net').
    ii)   accessToken 		- Use the access token generated in step 2 - iv.
    iii)  apiKey 		    - API Key of your registered application, which is stored in step 2.
    iv)	  title 		    - Use a valid string value for the title of the survey.
	  v)    fromSurveyId  	- Use the survey ID created in step 2 - iii.
	  vi)   startDate 	  	- Use a date with YYYY-MM-DD hh:mm:ss format which is at least one day before the current date. 
	  vii)  endDate 		- Use a date with YYYY-MM-DD hh:mm:ss format which is at lead one day after the current date.
	  viii) surveyId 		- Use the survey ID created in step 2 - i.
	  ix)   collectorId	  	- Use the collector ID created in step 2 - i.
	  x)    respondentId1 	- Use one of the respondent ID retrieved in step 2 - ii.
	  xi)   respondentId2	- Use one of the respondent ID retrieved in step 2 - ii except the one used for 'respondentId1'.
	  xii)  timeOut		    - Time out for waiting since SurveyMonkey API doesn't support multiple call sequentially (Recommended value is 5000).

 4. Navigate to "{SURVEYMONKEY_CONNECTOR_HOME}/surveymonkey-connector/surveymonkey-connector-2.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

 	Credential of test account:

    Username : virtusax
    Password : Colombo1
    accessToken=paTBz61kMlD0mPWrsaHR3921EuYbHlBvqU0GDZQ.5nahBvB1GbdZpbh2WnOzXY4SpgPezvIfjiLKg1h6dpRI5VJpOBjV3RTscaxNGlgM2w8=
	apiKey=v9wjtvutfpvz5x82t5dqtt8v
