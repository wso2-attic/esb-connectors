Product: Integration tests for WSO2 ESB SupportBee connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-SNAPSHOT

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by following the URL: https://svn.wso2.org/repos/wso2/people/jeewantha/4.9.0_release/released/M4/wso2esb-4.9.0-SNAPSHOT.zip.
	Apply the patches found in https://www.dropbox.com/s/bs83ll1m8kwgylq/patch0009.zip?dl=0 by copying the extracted files into <ESB_HOME>/repository/components/patches.

 2. Compress the modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "<SUPPORTBEE_CONNECTOR_HOME>/supportbee-connector/supportbee-connector-1.0.0/org.wso2.carbon.connector/repository/".
	If required install the supportbee security certificate (extracted from https://<account-name>.supportbee.com -  refer section 3 for informaion on how to get an account-name) to the following keystores:
		i) 	client-truststore.jks located in the <ESB_HOME>/repository/resources/security directory.
		ii) wso2carbon.jks located in the <SUPPORTBEE_CONNECTOR_HOME>/supportbee-connector/supportbee-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products directory.

 3. Create a supportbee trial account and derive the API Token.
	i) 	 Using the URL "https://app.supportbee.com/" create an supportbee trial account.
	ii)	 In the account homepage go to 'Account' -> 'API Token' and retain the API Token for later use.
	iii) Create at least 2 labels in the SupportBee account. Navigate to 'Admin' panel of the account to create labels.

 4. Update the supportbee properties file at location "<SUPPORTBEE_CONNECTOR_HOME>/supportbee-connector/supportbee-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	01)	apiUrl -  Base endpoint URL of the API. Use https://<account-name>.supportbee.com.
	02) authToken - Authentication obtained in 3. ii).
	03)	requesterEmail - Email to add as requester email when creating ticket. Need not be a valid email, but should be in the appropriate format (<username>@<domain>.com).
	04)	text - Text to be used as 'text' while creating ticket.
	05)	copiedEmails - Email address to be used as CC address while creating ticket. Need not be a valid email, but should be in the appropriate format (<username>@<domain>.com).
	06)	subject - Text to be used as 'subject' while creating ticket.
	07)	requesterName - Text to be used as 'requester name' while creating ticket.
	08)	html - HTML used when creating ticket, reply and comment.
	09)	labelName - Name of a valid label created in SupportBee account in step 3. iii).
	10)	textReplyMandatory - Text to be used while creating reply in mandatory case.
	11)	textReplyOptional - Text to be used while creating reply in optional case.
	12) textCommentMandatory - Text to be used while creating comment in mandatory case.
	13) textCommentOptional - Text to be used while creating comment in optional case.
	
	Note: Test suite can be run without making any changes to the provided property file.
	
 5. Navigate to "<SUPPORTBEE_CONNECTOR_HOME>/supportbee-connector/supportbee-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

		