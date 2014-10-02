Product: Integration tests for WSO2 ESB ZohoRecruit connector

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
		2. Set up a new ZohoRecruit account and follow all the instruction given below in step 5.

Steps to follow in setting integration test.

 1. Download ESB 4.8.1 from official website.

 2. The ESB should be configured as below;
	i)  Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).

		<messageFormatter contentType="application/octet-stream" class="org.apache.axis2.format.BinaryFormatter"/>

 	ii) Deploy relevant patches, if applicable.

 3. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{ZohoRecruit_Connector_Home}/zohorecruit-connector/zohorecruit-connector-1.0.0/org.wso2.carbon.connector/repository/".

 4. Create a ZohoRecruit trial account and derive the API Key.
	i) 		Using the URL "https://www.zoho.com/recruit/sign-up.html" create a ZohoRecruit trial account.
	ii)		Login to the created ZohoRecruit account and go to the following URL to generate the Authentication Token.
			https://accounts.zoho.com/apiauthtoken/nb/create?SCOPE=zohopeople/recruitapi&EMAIL_ID={username}&PASSWORD={application_password}
	iii)	Create two Candidates by following the path {Candidates > Add candidate} and fetch the record Ids of those candidates.
	iv)		Create four Job Openings by following the path {Job Openings > Add job opening} and fetch the record Ids of those job openings.

 5. Update the ZohoRecruit properties file at location "{ZohoRecruit_Connector_Home}/zohorecruit-connector/zohorecruit-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL specific to the created account.
	ii) 	authToken						-   Use the API Key obtained under Step 4 ii).
	iii)	scope							- 	Specify this value as recruitapi.
	iv)		updatePostingTitle				- 	Use a valid string for the posting title.
	v)		jobId 							-	Use a valid record Id of a created job under Step 4 iv.
	vi)		getRecordsFromIndex				-	Use a valid number for the start index of range to select records (default value is 1).
	vii)	getRecordsToIndex				-	Use a valid number for the ending index of range to select records (default value is 20 and the maximum value allowed is 200).
	viii)	getRecordsSortColumnString		-	Use a valid column name of a JobOpening.
	ix)		getRecordId 					-	Use a valid record Id of a created job under Step 4 iv.
	x)		getRecordByIdSelectColumns		-	Use a valid column name of a JobOpening.
	xi)		jobRecordId 					-	Use a valid record Id of a created job under Step 4 iv.
	xii)	candidateRecordId 				-	Use a record Id of a created candidate under Step 4 iii.
	xiii)	candidateRecordIdOptional 		-	Use the other record Id of a created candidate under Step 4 iii.
	xiv)	changeStatus					-	Use a valid candidate status value.
	xv)		jobRecordIdAssociate			-	Use a valid record Id of a created job under Step 4 iv.
	
 6. Navigate to "{ZohoRecruit_Connector_Home}/zohorecruit-connector/zohorecruit-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
	  
	  Note:- ZohoRecruit trial account expires within 15 days.

		