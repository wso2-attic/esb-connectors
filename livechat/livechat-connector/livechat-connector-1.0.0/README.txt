Product: Integration tests for WSO2 ESB LiveChat connector

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
	Set up a new LiveChat account and follow all the instructions given below in step 4 to generate an access token.

Steps to follow in setting integration test.

 1. Download WSO2 ESB 4.9.0-ALPHA from official website.
 
 2. Deploy relevant patches, if applicable.

 3. Create a LiveChat account using URL http://www.livechatinc.com
	
 4. Follow the steps in http://developers.livechatinc.com/rest-api/#authentication to generate the api key.

 5. Follow the below mentioned steps for adding valid certificate to access LiveChat API over https.

	i) 	 Extract the certificate for the api url "https://api.livechatinc.com".
			NOTE : Make sure to use an api method endpoint url to obtain the certificate (e.g. https://api.livechatinc.com/chats )
	ii)  Go to new ESB 4.9.0-SNAPSHOT folder and place the downloaded certificate in both "{ESB_Connector_Home}/repository/wso2esb-4.9.0-ALPHA/resources/security/" and "{ESB_Connector_Home}/livechat-connector/livechat-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" folders.
	iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.
	
				keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" 
				
		 This command will import LiveChat certificate into keystore.
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from LiveChat with the extension. (e.g. livechat.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. LiveChat)
				
	iv)  Navigate to "{ESB_Connector_Home}/livechat-connector/livechat-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.
	
				keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 
				
		 This command will import LiveChat certificate into keystore.
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from LiveChat with the extension. (e.g. livechat.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. LiveChat)

 6. Compress modified ESB as WSO2 ESB 4.9.0-ALPHA.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 7. Pre-requisites for LiveChat Connector Integration Testing.

	Create at least one chat by following steps.
	i) 	Login with the created LiveChat account(in step 3) by using the URL "https://my.livechatinc.com".
	ii) Navigate to the URL "https://my.livechatinc.com/chats" and click the "live preview site" link.
	iii)Click "Chat now" in the re-directed page in step 7[ii], and create a new chat by giving a name and an email address. 
	iv) Integration test could be run, 10 minutes (at least) after step 7(iii) is completed. 
	 
 8. Update the properties in 'livechat.properties' file at location "{ESB_Connector_Home}/livechat-connector/livechat-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl				- Use the API URL as "https://api.livechatinc.com".
	ii)		apiKey			    - Access Token obtained by following the steps in 4.
	iii) 	login		        - Use the email address used for the login.
	iv)*	agentNameMand	 	- Use a unique string for the agent name (Use for createAgent -Mandatory test case).
	v)*		agentLoginMand		- Use a unique valid email address for the agent(Use for createAgent -Mandatory test case).
	vi)*	agentNameOpt  	    - Use a unique string for the agent name (Use for createAgent -Optional test case).
	vii)* 	agentLoginOpt	    - Use a unique valid email address for the agent(Use for createAgent -Optional test case).
	viii)	jobTitle			- Use preferred job title for the Agent.
	ix)		loginStatus		    - Use a valid preffered string for the login status of the Agent.
	x)		groupName			- Use a string value for the name of the 
	xi)		language			- Use a given language code for the language of the group.
	xii)	toEmail				- Use a valid email address which used
	xiii)	startDate			- Use a valid past date in the format of 'yyyy-mm-dd' (e.g. 2015-01-23). 
	xiv)	endDate				- Use a valid date in the format of 'yyyy-mm-dd' (e.g. 2015-02-23). 
	xv)		message				- Use a preferred string value for the text of the message for the ticket.
	xvi)	requesterEmail		- Use a valid email address for the requester.
	xvii)	requesterName		- Use a string value of the name of the requester.
	xviii)	subject				- Use a preffered string as the subject for the email to be sent for the ticket.
	
	* Values need to be changed for each execution of the Test Suite. Please make sure the values are unique in the context of the same account.
	
 9. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install
