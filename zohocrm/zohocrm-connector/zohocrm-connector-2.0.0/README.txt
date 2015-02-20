Product: Integration tests for WSO2 ESB ZohoCRM connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Follow the below mentioned steps for adding valid certificates to access Zoho CRM API over https. If the certificates are already available in keystores, you can skip this step.
 
	1)	Extract the certificate from browser by navigating to https://www.zohocrm.com/ and place the certificate file in following locations. 
			- <ZOHOCRM_CONNECTOR_HOME>/zohocrm-connector/zohocrm-connector-2.0.0/org wso2.carbon.connector/src/test/resources/keystores/products
			- <ESB_HOME>/repository/resources/security

	i)  Navigate to the "<ZOHOCRM_CONNECTOR_HOME>/zohocrm-connector/zohocrm-connector-2.0.0/org wso2.carbon.connector/src/test/resources/keystores/products" location from command prompt and execute the following command to import zohocrm certificate in to keystore. Give "wso2carbon" as password.
			keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME".
			NOTE : 	CERT_FILE_NAME is the file name which was extracted from zohocrm with  the extension, change it accordingly (e.g. zohocrm.crt).
					CERT_NAME is an arbitrary name for the certificate. (e.g. zohocrm)
	ii) Navigate to the "<ESB_HOME>/repository/resources/security" location from command prompt and execute the following command to import zohocrm certificate in to keystore. Give "wso2carbon" as password.
			keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME".
			NOTE : 	CERT_FILE_NAME is the file name which was extracted from zohocrm with  the extension, change it accordingly (e.g. zohocrm.crt).
					CERT_NAME is an arbitrary name for the certificate. (e.g. zohocrm)
	
 2. The ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled in <ESB_HOME>/repository/conf/axis2/axis2.xml folder.
   
    <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
	
    <messageBuilder contentType="text/html"	class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
	
	<messageFormatter contentType="application/jsonp" class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
	
	<messageBuilder contentType="application/jsonp" class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

	Note: Add the aforementioned message formatters and the message builder to the axis file, if they are not available by default.
	
 3. Make sure the compressed ESB 4.8.1 zip file with latest patches and the changes in the step 1 and 2, is available at "{ZOHOCRM_CONNECTOR_HOME}/zohocrm-connector/zohocrm-connector-2.0.0/org.wso2.carbon.connector/repository/"

 4. Follow the below steps to create a ZohoCRM account.

	i) Navigate to "https://www.zoho.com/crm/lp/signup.html?src=slid1".
   ii) Enter the required details and complete the account creation. 
 
 5. Follow the steps in the below link to obtain the accesstoken.
 
	https://www.zoho.com/crm/help/api/using-authentication-token.html
	
 6.	Obtaining valid values for "parentModuleId" and "relatedModuleId" parameters in the property file. 
 
	i) 	Create a campaign in ZohoCRM account and navigate to the campaign home page.
	ii)	Obtain the campaign id (parentModuleId) from the url.   	
	iii)Create a lead through the home page of the campaign created above and navigate to the lead home page.
 	iV) Obtain the lead id (relatedModuleId) from the URL.
	
 7. Required properties for ZohoCRM Connector Integration Testing
   
	i)    apiUrl					- The URL of ZohoCRM api(https://crm.zoho.com).
	ii)   parentModuleId      	   	- The campaign id obtained in step 6-> ii
	iii)  relatedModuleId 		   	- The lead id obtained in step 6-> iv
	iv)   accessToken 			   	- The accesstoken obtained in step(5) which gives access to the API.
	v)    scope 			    	- Specify the value as "crmapi".
	vi)	  updateDescription		   	- Description for the updateRecords method mandatory test case. A string value can be assigned to this parameter. Eg: Sample Mandatory Description
	vii)  updateDescriptionOptional - Description for the updateRecords method optional test case. A string value can be assigned to this parameter. Eg: Sample Optional Description
	viii) uploadFileName 	       	- File name of the uploading document. Eg: test.txt - The file should be in the following path.
									  <ZOHOCRM_CONNECTOR_HOME>/zohocrm-connector/zohocrm-connector-2.0.0/org wso2.carbon.connector/src/test/resources/artifacts/ESB/config/resources/zohocrm							 
	ix)   campaignName1				- Use a valid string for the campaign name. 
	x)    campaignName2				- Use a valid string for the campaign name. 
	xi)	  sleepTime					- Provide an integer value to denote the number of milliseconds to sleep between test cases. If test cases fail, try increasing the number. (e.g. 10000)
		
 8. Navigate to "{ZOHOCRM_CONNECTOR_HOME}/zohocrm-connector/zohocrm-connector-2.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install