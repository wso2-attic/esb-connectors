Product: Integration tests for WSO2 ESB Insightly connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-SNAPSHOT

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by following the URL: https://svn.wso2.org/repos/wso2/people/jeewantha/4.9.0_release/released/M4/wso2esb-4.9.0-SNAPSHOT.zip.
	Apply the patches found in https://www.dropbox.com/s/bs83ll1m8kwgylq/patch0009.zip?dl=0 by copying the extracted files into {ESB_HOME}/repository/components/patches.

 2. Compress the modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{Insightly_Connector_Home}/insightly-connector/insightly-connector-1.0.0/org.wso2.carbon.connector/repository/".
	If required install the Insightly security certificate (extracted from https://api.insight.ly) to client-truststore.jks located in the {ESB_HOME}/repository/resources/security directory.

 3. Create a Insightly trial account and derive the API Key.
	i) 	 Using the URL "https://accounts.insightly.com/?plan=trial" create an Insightly trial account.
	ii)	 Users' API key can be found by logging into Insightly and going to the 'My Info' page, then the 'User Settings' tab.

 4. Update the Insightly properties file at location "{Insightly_Connector_Home}/insightly-connector/insightly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl - Base endpoint URL of the API. Use https://api.insight.ly.
	ii) 	apiKey - API Key obtained via 3.ii)
	
	iii)	salutation 			- Salutation of the Contact (Mandatory case).
	iv) 	firstName 			- Firstname of the Contact (Mandatory case).
	v)  	lastName 			- Lastname of the Contact (Mandatory case).
	vi) 	background 			- Background of the Contact (Mandatory case).
	vii)	tagName 			- Optional Tagname for the Contact (Mandatory case).
	viii)	salutationUpdated 	- Salutation of the Contact to update.
	ix)  	firstNameUpdated 	- Firstname of the Contact to update.
	x)   	lastNameUpdated 	- Lastname of the Contact to update.
	xi)  	backgroundUpdated 	- Background of the Contact to update.
	xii)	tagNameUpdated 		- Optional Tagname for the Contact to update.
	
	xiii) 	opportunityState 			- State of the Opportunity.
	xiv)	opportunityName 			- Name of the Opportunity.
	xv)		opportunityDetails 			- Details for the Opportunity.
	xvi)	probability 				- Probability for the Opportunity.
	xvii)	bidCurrency 				- Bid Currency for the Opportunity.
	xviii)	bidAmount 					- Bid Amount for the Opportunity.
	xix)	bidType 					- Bid Type of the Opportunity.
	xx)		bidDuration 				- Bid Duration of the Opportunity.
	xxi)	opportunityNameUpdated 		- Name of the Opportunity to update.
	xxii)	opportunityDetailsUpdated 	- Details of the Opportunity to update.
	xxiii)	probabilityUpdated 			- Probability of the Opportunity to update.
	xxiv)	bidCurrencyUpdated 			- Bid Currency for the Opportunity to update.
	xxv)	bidAmountUpdated 			- Bid Amount for the Opportunity to update.
	xxvi)	bidTypeUpdated 				- Bid Type of the Opportunity to update.
	xxvii)	bidDurationUpdated 			- Bid Duration of the Opportunity to update.
	
	xxviii)	projectNameMandatory 	- Name of the Project (Mandatory Case).
	xxix)	projectDetails 			- Details of the Project.
	xxx)	projectNameOptional 	- Name of the Project (Optional Case).
	xxxi)   status 					- Status of the Project.
	xxxii)	completedDate 			- Completed Date of the Project (format yyyy-MM-dd HH:mm:ss).
	xxxiii)	projectNameUpdated 		- Name of the Project to update.
	xxxiv)	projectDetailsUpdated 	- Details of the Project to update.
	xxxv)	statusUpdated 			- Status of the Project to update.
	xxxvi)	completedDateUpdated 	- Completed Date of the Project to update (format yyyy-MM-dd HH:mm:ss).
	
	xxxvii)	titleMandatory 			- Title of the Note (Mandatory Case).
	xxxviii)linkSubjectType 		- Subject Type of the Note (Mandatory Case).
	xxxix)	titleOptional 			- Title of the Note (Optional Case).
	xL)		noteBodyOptional 		- Body of the Note (Optional Case).
	xLii)	visibleTo 				- IDs to whom the Note is visible to (Optional Case).
	xLiii)	titleUpdated 			- Title of the Note to update.
	xLiv)	linkSubjectTypeUpdated 	- Subject Type of the Note to update.
	xLv)	visibleToUpdated 		- Updated IDs to whom the Note is visible to.
	xLvi)	noteBodyUpdated 		- Body of the Note to update.
	
	xLvii)	titleMandatory 		- Title of the Task (Mandatory Case).
	xLviii)	titleOptional 		- Title of the Task (Optional Case).
	xLvix)	publiclyVisible 	- Whether or not the Task is publicly visible.
	L)		completed 			- Whether or not the Task is completed.
	Li)		ownerVisible 		- Whether or not the owner of the Task is visible.
	Lii)	startDate 			- Start date of the Task (format yyyy-MM-dd HH:mm:ss).
	Liii)	dueDate 			- Due date of the Task (format yyyy-MM-dd HH:mm:ss).
	Liv)	percentComplete 	- Task completion status.
	Lv)		priority 			- Priority of the Task.
	Lvi)	status 				- Status of the Task.
	Lvii)	details 			- Details of the Task.
	Lviii)	taskTitleUpdated 	- Title of the Task to update.
	Lix)	startDateUpdated 	- Start date of the Task to update (format yyyy-MM-dd HH:mm:ss).
	Lx)		dueDateUpdated 		- Due date of the Task to update (format yyyy-MM-dd HH:mm:ss).
	Lxi)	priorityUpdated 	- Priority of the Task to update.
	Lxii)	statusUpdated 		- Status of the Task to update.
	Lxiii)	detailsUpdated 		- Details of the Task to update.
	
	Note: Test suite can be run without making any changes to the provided property file.
	
 5. Navigate to "{Insightly_Connector_Home}/insightly-connector/insightly-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

		