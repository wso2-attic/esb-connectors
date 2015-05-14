Product: Integration tests for WSO2 ESB Insightly connector

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

 2. Compress the modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "<INSIGHTLY_CONNECTOR_HOME>/insightly-connector/insightly-connector-1.0.0/org.wso2.carbon.connector/repository/".
	If required install the Insightly security certificate (extracted from https://api.insight.ly) to client-truststore.jks located in the <ESB_HOME>/repository/resources/security directory.

 3. Create a Insightly trial account and derive the API Key.
	i) 	 Using the URL "https://accounts.insightly.com/?plan=trial" create an Insightly trial account.
	ii)	 Users' API key can be found by logging into Insightly and going to the 'My Info' page, then the 'User Settings' tab.

 4. Update the Insightly properties file at location "<INSIGHTLY_CONNECTOR_HOME>/insightly-connector/insightly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	01)	apiUrl - Base endpoint URL of the API. Use https://api.insight.ly.
	02) apiKey - API Key obtained via 3.ii)
	
	03)	salutation 			- Salutation of the Contact (Mandatory case).
	04) firstName 			- Firstname of the Contact (Mandatory case).
	05) lastName 			- Lastname of the Contact (Mandatory case).
	06) background 			- Background of the Contact (Mandatory case).
	07)	tagName 			- Optional Tagname for the Contact (Mandatory case).
	08)	salutationUpdated 	- Salutation of the Contact to update.
	09) firstNameUpdated 	- Firstname of the Contact to update.
	10) lastNameUpdated 	- Lastname of the Contact to update.
	11) backgroundUpdated 	- Background of the Contact to update.
	12)	tagNameUpdated 		- Optional Tagname for the Contact to update.
	
	13) opportunityState 			- State of the Opportunity.
	14)	opportunityName 			- Name of the Opportunity.
	15)	opportunityDetails 			- Details for the Opportunity.
	16)	probability 				- Probability for the Opportunity.
	17)	bidCurrency 				- Bid Currency for the Opportunity.
	18)	bidAmount 					- Bid Amount for the Opportunity.
	19)	bidType 					- Bid Type of the Opportunity.
	20)	bidDuration 				- Bid Duration of the Opportunity.
	21)	opportunityNameUpdated 		- Name of the Opportunity to update.
	22)	opportunityDetailsUpdated 	- Details of the Opportunity to update.
	23)	probabilityUpdated 			- Probability of the Opportunity to update.
	24)	bidCurrencyUpdated 			- Bid Currency for the Opportunity to update.
	25)	bidAmountUpdated 			- Bid Amount for the Opportunity to update.
	26)	bidTypeUpdated 				- Bid Type of the Opportunity to update.
	27)	bidDurationUpdated 			- Bid Duration of the Opportunity to update.
	
	28)	projectNameMandatory 	- Name of the Project (Mandatory Case).
	29)	projectDetails 			- Details of the Project.
	30)	projectNameOptional 	- Name of the Project (Optional Case).
	31)   status 					- Status of the Project/Task.
	32)	completedDate 			- Completed Date of the Project (format yyyy-MM-dd HH:mm:ss).
	33)	projectNameUpdated 		- Name of the Project to update.
	34)	projectDetailsUpdated 	- Details of the Project to update.
	35)	statusUpdated 			- Status of the Project/Task to update.
	36)	completedDateUpdated 	- Completed Date of the Project to update (format yyyy-MM-dd HH:mm:ss).
	
	37)	titleMandatory 			- Title of the Note/Task (Mandatory Case).
	38) linkSubjectType 		- Subject Type of the Note (Mandatory Case).
	39)	titleOptional 			- Title of the Note/Task (Optional Case).
	40)	noteBodyOptional 		- Body of the Note (Optional Case).
	41)	visibleTo 				- IDs to whom the Note is visible to (Optional Case).
	42)	titleUpdated 			- Title of the Note to update.
	43)	linkSubjectTypeUpdated 	- Subject Type of the Note to update.
	44)	visibleToUpdated 		- Updated IDs to whom the Note is visible to.
	45)	noteBodyUpdated 		- Body of the Note to update.
	
	46)	publiclyVisible 	- Whether or not the Task is publicly visible.
	47)	completed 			- Whether or not the Task is completed.
	48)	ownerVisible 		- Whether or not the owner of the Task is visible.
	49)	startDate 			- Start date of the Task (format yyyy-MM-dd HH:mm:ss).
	50)	dueDate 			- Due date of the Task (format yyyy-MM-dd HH:mm:ss).
	51)	percentComplete 	- Task completion status.
	52)	priority 			- Priority of the Task.
	53)	details 			- Details of the Task.
	54)	taskTitleUpdated 	- Title of the Task to update.
	55)	startDateUpdated 	- Start date of the Task to update (format yyyy-MM-dd HH:mm:ss).
	56)	dueDateUpdated 		- Due date of the Task to update (format yyyy-MM-dd HH:mm:ss).
	57)	priorityUpdated 	- Priority of the Task to update.
	58)	detailsUpdated 		- Details of the Task to update.
	
	Note: Test suite can be run without making any changes to the provided property file.
	
 5. Navigate to "<INSIGHTLY_CONNECTOR_HOME>/insightly-connector/insightly-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

		