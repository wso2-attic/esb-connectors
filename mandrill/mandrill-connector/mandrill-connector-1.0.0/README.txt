Product: Integration tests for WSO2 ESB Mandrill connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Java 1.7
 - UBUNTU 14.04
 - WSO2 ESB 4.9.0-ALPHA

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/people/jeewantha/4.9.0-ALPHA_release/released/M4/wso2esb-4.9.0-ALPHA.zip.
	Apply the patches found in https://www.dropbox.com/s/bs83ll1m8kwgylq/patch0009.zip?dl=0 by copying the extracted files into {ESB_HOME}/repository/components/patches.

 2. Compress the modified ESB as wso2esb-4.9.0-ALPHA and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 3. Create a Mandrill trial account and derive the API Key.
	i) 	 Using the URL "https://mandrill.com/signup/" create a Mandrill trial account.
	ii)	 Login to the created Mandrill account and turn on test mode.
	iii) Navigate to Settings >> SMTP & API Info and under 'API Keys' Add a new API Key.

 4. Update the Mandrill properties file at location "{Mandrill_Connector_Home}/mandrill-connector/mandrill-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 				- 	The API URL specific to the created Mandrill account (https://mandrillapp.com).
	ii) 	apiKey				-	The api key that is being obtained under step 3 iii).
	iii)	optionalTag			-	A valid string as the tag name.
	iv)		subject				-	A valid string as the subject of the email.
	v)		fromEmail			-	A valid email address as the sender email.
	vi)		toEmail				-	A valid email address as the recipient email.
	vii)	dateFrom			-	A valid date in the format of YYYY-MM-DD (e.g:-2015-02-13). Note that this date should be prior to the date when the mail was sent to obtain the 'emailIdMandatory' in (ix).
	viii)	dateTo				-   A valid date in the format of YYYY-MM-DD (e.g:-2015-02-13). Note that this date should be future to the date when the mail was sent to obtain the 'emailIdMandatory' in (ix).
	ix)	    emailIdMandatory	-	Use a valid email Id. To obtain this, make an api call to send a message having at least one tag value in the request.

5. Make sure that the mandrill connector is set as a module in esb-connectors parent pom.
          <module>mandrill/mandrill-connector/mandrill-connector-1.0.0/org.wso2.carbon.connector</module>

6. Navigate to "{ESB_Connector_Home}/" and run the following command.
         $ mvn clean install