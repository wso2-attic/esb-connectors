Product: Integration tests for WSO2 ESB CallRail connector

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
 1.  Download ESB 4.8.1 from official website.
 2.  Deploy relevant patches, if applicable.

STEPS:

 1. Follow the below mentioned steps for adding valid certificate to access callrail API over https

	i) Extract the certificate from browser by navigating to https://api.callrail.com and place the certificate file in following location.
	   "{CALLRAIL_CONNECTOR_HOME}/callrail-connector/callrail-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/"
	  
	ii) Navigate to "{CALLRAIL_CONNECTOR_HOME}/callrail-connector/callrail-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import Callrail certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from callrail, change it accordingly. (e.g. -.callrail.com)
			   CERT_NAME is name of the certificate. (e.g. callrail)
	   
	iii)Go to new ESB 4.8.1 folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/"

	iv) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import Callrail certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from callrail, change it accordingly. (e.g. -.callrail.com)
			   CERT_NAME is name of the certificate. (e.g: callrail)
		
 2. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
	
	Message Formatters :-
		
	<messageFormatter contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
							  
	<messageFormatter contentType="text/html"                             
					  class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
					  
	<messageBuilder contentType="application/json"
					  class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

	<messageBuilder contentType="text/html"                                
					  class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
	
 3. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{CALLRAIL_CONNECTOR_HOME}/callrail-connector/callrail-connector-1.0.0/org.wso2.carbon.connector/repository/".
	
 4. Create a CallRail account and derive the API key:
	i) 	 Using the URL "https://app.callrail.com" create a CallRail Free Trial account.
	ii)  Derive the API Key by following the instructions at "http://apidocs.callrail.com/".
	iii) Create a new Company after creating an account and store the company ID for further reference.
	iv)  Select the company and add numbers for both source tracker and session tracker and store the IDs for further reference.
	Note: - To add a new session tracker number, select the Company >> select Numbers >> click Add Number >> add a new number under "Track Visitors & Keywords"
 	      - To add a new source tracker number, select the Company >> select Numbers >> click Add Number >> add a new number under "Track an Online Campaign" or "Track an Offline Campaign"	
	v)   Place calls for each of the created numbers and store the call IDs for further reference.

 5. Update the CallRail properties file at location "{CALLRAIL_CONNECTOR_HOME}/callrail-connector/callrail-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl - Use the API URL as "https://www.callrail.com".
	ii) 	apiKey - API key that is obtained under Step 4 ii).
	iii) 	companyId - Use a valid company ID obtained under Step 4 iii).
	iv) 	callId - Use a valid call ID obtained under Step 4 v).
	v) 		note - Any text value.
	vi)     tag - Use an existing tag name.
	vii) 	sessionTrackerId - Use a valid session tracker ID obtained under Step 4 iv).	
	viii) 	sourceTrackerId	- Use a valid source tracker ID obtained under Step 4 iv).	
	ix)	    perPage - Use an integer mentioning how many records to return for a request per page.
	x)		page - Use an integer mentioning the page number.
	xi)		startDate - Preferred start date in the format of yyyy-mm-dd
	xii)	timeOut - 10000
		
 6. Navigate to "{CALLRAIL_CONNECTOR_HOME}/callrail-connector/callrail-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

 NOTE : 
	  -CallRail Free trial account is only valid for 14 days.
	  -Following CallRail test account can be used to run the integration tests.
 
	   Username : wso2connector.abdera@gmail.com
	   Password : connector@123
	   API Key  : b90746ad2d256cb85ef19663e8e24bd7
