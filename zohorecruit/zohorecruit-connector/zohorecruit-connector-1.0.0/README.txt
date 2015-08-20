Product: Integration tests for WSO2 ESB ZohoRecruit connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-RC1-SNAPSHOT

Note:
	This test suite can be executed based on two scenarios.
		1. Use the given test account and parameters at the end of the file.
		2. Set up a new ZohoRecruit account and follow all the instruction given below in step 5.

Steps to follow in setting integration test.

 1. Download ESB WSO2 ESB 4.9.0-RC1-SNAPSHOT from official website.

 2. The ESB should be configured as below;
	i)  Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).

		<messageFormatter contentType="application/jsonp"
                                  class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
        <messageBuilder contentType="application/jsonp"
                                  class="org.apache.synapse.commons.json.JsonStreamBuilder"/>


 3.  Follow the below mentioned steps to add valid certificate to access zoho recruit API over https.

    	i) 	 Extract the certificate from browser(Mozilla Firefox) by navigating to 'https://recruit.zoho.com/'
    	ii)  Go to new ESB WSO2 ESB 4.9.0-RC1-SNAPSHOT folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and
    		 "{zohorecruit_Connector_Home}/zohorecruit-connector/zohorecruit-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
        iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

    			keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

    		 	This command will import Eloqua certificate into keystore.
    		 	To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

    		 	NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from zohorecruit with the extension. (e.g. zohorecruit.crt)
    			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. zoho)

 4. Compress modified ESB as 4.9.0-RC1-SNAPSHOT and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 4. Create a ZohoRecruit trial account and derive the API Key.
	i) 		Using the URL "https://www.zoho.com/recruit/sign-up.html" create a ZohoRecruit trial account.
	ii)		Login to the created ZohoRecruit account and go to the following URL to generate the Authentication Token.
			https://accounts.zoho.com/apiauthtoken/nb/create?SCOPE=zohopeople/recruitapi&EMAIL_ID={username}&PASSWORD={application_password}
	iii)		Create a Job Opening by following the path {Job Openings > Add job opening} and fetch the record Ids of those job openings.

 5. Update the ZohoRecruit properties file at location "{ESB_Connector_Home}/zohorecruit/zohorecruit-connector/zohorecruit-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL specific to the created account.
	ii) 	authToken						-   Use the API Key obtained under Step 4 ii).
	iii)	scope							- 	Specify this value as recruitapi.
	iv)		updatePostingTitle				- 	Use a valid string for the posting title.
	v)		jobId 							-	Use a valid record Id of a created job under Step 4 iii.
	vi)		getRecordsFromIndex				-	Use a valid number for the start index of range to select records (default value is 1).
	vii)	getRecordsToIndex				-	Use a valid number for the ending index of range to select records (default value is 20 and the maximum value allowed is 200).
	viii)	getRecordsSortColumnString		-	Use a valid column name of a JobOpening.
	ix)		changeStatus					-	Use a valid candidate status value.


 6. Make sure that zohorecruit is specified as a module in ESB_Connector_Parent pom.
         <module>/zohorecruit/zohorecruit-connector/zohorecruit-connector-1.0.0/org.wso2.carbon.connector/</module>

 7. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install
	  
	  Note:- ZohoRecruit trial account expires within 15 days.

		