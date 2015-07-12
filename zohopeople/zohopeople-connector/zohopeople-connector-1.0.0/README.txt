Product: Integration tests for WSO2 ESB ZohoPeople connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04, Maven 3.x
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

STEPS:
 1.  Download ESB 4.9.0-ALPHA from official site.

 1. Extract the certificate from browser by navigating to "https://people.zoho.com/people/" and place the certificate file in following locations. 

	i)  "<ESB_Connector_Home>/zohopeople/zohopeople-connector/zohopeople-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products"

		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "zohopeople"' in command line to import ZohoPeople certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from ZohoPeople with  the extension, change it accordingly. Remove the copied certificate.
	
	ii) "{ESB_Connector_Home}/repository/resources/security"
	
		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "zohopeople"' in command line to import ZohoPeople certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from ZohoPeople with  the extension, change it accordingly. Remove the copied certificate.

 2. The ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).
   
	<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>	
	<messageFormatter contentType="application/x-www-form-urlencoded" class="org.apache.axis2.transport.http.XFormURLEncodedFormatter"/>
	<messageFormatter contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
	<messageFormatter contentType="application/octet-stream" class="org.apache.axis2.format.BinaryFormatter"/>	
	<messageFormatter contentType="text/javascript" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>	
	<messageFormatter contentType="text/x-javascript" class="org.apache.synapse.commons.json.JsonStreamFormatter"/>			
	
    <messageBuilder contentType="text/html"	class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
    <messageBuilder contentType="application/x-www-form-urlencoded" class="org.apache.synapse.commons.builders.XFormURLEncodedBuilder"/>	
	<messageBuilder contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamBuilder"/>
	<messageBuilder contentType="application/octet-stream" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>		
	<messageBuilder contentType="text/javascript"  class="org.wso2.carbon.relay.BinaryRelayBuilder"/>	
	<messageBuilder contentType="text/x-javascript" class="org.apache.synapse.commons.json.JsonStreamBuilder"/>	
	
	Note: Add the aforementioned message formatters and the message builders to the axis file, if they are not available by default.
 
 3.Compress the ESB and copy the wso2esb-4.9.0-ALPHA.zip file in to location "{ESB_Connector_Home}/repository/".
 
 4. Complete features of ZohoPeople API can be accessed via Zoho People Premium Edition account. Follow the below steps to create an account.

	i)  Navigate to "https://www.zoho.com/people/signup.html".
    ii) Enter the required details and complete the account creation. 
 
 5. Follow the steps in the below link to obtain the accesstoken.
 
	https://www.zoho.com/people/help/api/auth-token.html
	
 6. Creating a leave type using ZOHO People UI. 
 
	i)   Navigate to Leave Tracker -> Settings through the main menu. 
	ii)  Click Add Leave Type button.
	iii) Add the leave type details to create a record. 
	
 7. Obtaining the "recordId", "employeeEmailId" and "employeeId".

	i) recordId		- Navigate to Organization -> Employee -> Employee View
					  Click the employee account of the admin user (account owner) and obtain the "recordId" from the URL.

	ii) employeeEmailId & employeeId - Navigate to Organization -> Employee -> Employee View					
									  Click the employee account of the admin user (account owner) and obtain the "EmployeeID" and the "Email ID" from the UI.
					  	
 8. Update the ZOHO People properties file at location "{ZOHO_PEOPLE_CONNECTOR_HOME}/zohopeople-connector/zohopeople-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.	 
	Following fields in the property file should be updated appropriately.
   
	i)    apiUrl		  	- The URL of ZohoPeople api(https://people.zoho.com).
	ii)   accessToken  		- The accesstoken obtained in step(5) which gives access to the API.
	iii)  recordId       	- The API level record ID of the account user (Obtained via step 7).  
	iv)   employeeEmailId	- Employee email ID of the account user (Obtained via step 7). 
	v)    employeeId 		- Employee ID of the account user (Obtained via step 7).
	vi)   fromLeaveDateESB	- A date string in the following format,dd-mmm-yyyy.
	vii)  toLeaveDateESB    - A date string in the following format,dd-mmm-yyyy.
	viii) fromLeaveDateAPI	- A date string in the following format,dd-mmm-yyyy. Make sure this is different to the fromLeaveDateESB given in step vi.
	ix)   toLeaveDateAPI	- A date string in the following format,dd-mmm-yyyy. Make sure this is different to the toLeaveDateESB given in step vii.

 9. Make sure that zohopeople is specified as a module in ESB_Connector_Parent pom.
      <module>zohopeople/zohopeople-connector/zohopeople-connector-1.0.0/org.wso2.carbon.connector</module>

 10. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install
	  
 11. Account Details
	Username: zohopeopletest2014@gmail.com
	Password: 1qaz2wsx@
	