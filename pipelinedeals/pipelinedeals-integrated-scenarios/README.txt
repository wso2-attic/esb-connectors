Product: PipelineDeals Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0-BETA-SNAPSHOT with patches.
 
 - Upload the following connectors to the ESB.
 
			-> billomat-connector-1.0.0
			-> mandril-connector-1.0.0
			-> mailchimp-connector-1.0.0
			-> zohocrm-connector-1.0.0
			-> pipelinedeals-connector-1.0.0
			-> wunderlist-connector-1.0.0
			-> dropbox-connector-1.0.0
			-> googlecalender-connector-1.0.0
			
 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.
			
			ZohoCRM			- https://docs.wso2.com/display/CONNECTORS/Zohocrm+Connector
			Mandrill 		- https://docs.wso2.com/display/CONNECTORS/Mandrill+Connector
			Mailchimp 		- https://docs.wso2.com/display/CONNECTORS/MailChimp+Connector
			Billomat 		- https://docs.wso2.com/display/CONNECTORS/Billomat+Connector
			PipelineDeals 	- https://docs.wso2.com/display/CONNECTORS/PipelineDeals+Connector
			WunderList 		- https://docs.wso2.com/display/CONNECTORS/Wunderlist+Connector
			Dropbox 		- https://docs.wso2.com/display/CONNECTORS/Dropbox+Connector
			GoogleCalendar 	- https://docs.wso2.com/display/CONNECTORS/Google+Calendar+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<PIPELINEDEALS_CONNECTOR_HOME>/pipelinedeals-integrated-scenarios/src/common), to the ESB that are listed as below.
			- sequences -  faultHandlerSeq.xml
			               removeResponseHeaders.xml
						   
			- templates -  responseHandlerTemplate.xml
						   getCustomFieldIdByName.xml
						   getDealSourceIdByName.xml
						   getDealStageIdByName.xml
							
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			
 
 - Please make sure that the following are created/configured in your PipelineDeals account before starting to execute the scenarios.
	1. Deal sources named 'ZohoCRM', 'Mailchimp' are created. Give any amount for the source value and choose 'Flat Fee' for the fee type.
	2. Deal stages named 'Follow-Up', 'Active', 'Ready For invoice', 'Invoiced'. Give a probability value which is not already taken by existing stages.
	3. Deal custom fields named 'ZohoCRM Potential ID', 'WunderList List ID', 'Billomat Invoice ID' (Custom fields should be created as 'Text' - Not mandatory).
	4. Company custom field named 'Billomat Client ID', 'WunderList Folder ID' (Custom fields should be created as 'Text' - Not mandatory).
	5. Person custom fields named 'Billomat Client ID', 'Billomat Contact ID' (Custom fields should be created as 'Text' - Not mandatory).
	
- Please make sure that the following are configured in your ZohoCRM account before starting to execute the scenarios.
	1. Account custom field named 'PipelineDeals Company ID' (Text field with a limit of 25 characters - Not mandatory).
	2. Contact custom field named 'PipelineDeals Person ID' (Text field with a limit of 25 characters - Not mandatory).
	3. Potential custom field named 'PipelineDeals Deal ID' (Text field with a limit of 25 characters - Not mandatory).
	4. Potential stages named 'Follow-Up' and 'Archive'. 
 
 01. Deal Origination
 
	[i] Case - 001
		- Purpose : (a) Create an email campaign in mailchimp and send that to a specified subscribers' list.
		- Files:	(a)	Proxy - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Origination\Case-001\proxy\pipelinedeals_createCampaignAndSend.xml
		- Request 	Parameters: (a) mailchimp.listName - Name of the selected subscirber ist.
								(b) mailchimp.templateId - The ID of the user-created template to generate the HTML content of the campaign.
								(b) mailchimp.campaignSubject - The description goes as the subject of the mailchimp campaign.
								(b) mailchimp.fromEmail - The email address of the sender of the campaign.
								(b) mailchimp.fromName - The name of the sender of the campaign.
	
	[ii] Case - 002
		- Purpose : (a) Retrieve the campaign clickers and create deals for them in PipelineDeals based on the link probabilities sent via the campaign.
					(b) Create the clicker (in Mailchimp) as person in PipelineDeals if he doesn't already exist.
		- Files:	(a)	Proxy - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Origination\Case-002\proxy\pipelinedeals_retrieveClickersAndCreateDeals.xml
					(b) Sequence - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Origination\Case-002\sequence\mailchimp-retrieveLowProbabilityClickersSeq.xml
								   <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Origination\Case-002\sequence\S01-C002-verifyPrerequisites.xml
					(c) Template - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Origination\Case-002\template\createContactAndDeals.xml
		- Request Parameters:   (a) mailchimp.campaignId - The ID of the campaign to which the deals are going to be created in PipelineDeals.									
								(b) mailchimp.low - An object containing the low probability URL information
									(i).	url - The URL which is considered as the link with low probability.
									(ii). 	probability -  The probability associated with the above url (integer values between 0-100).
								(c) mailchimp.high - An object containing the high probability URL information
									(i).	url - The URL which is considered as the link with high probability.
									(ii). 	probability -  The probability associated with the above url (integer values between 0-100).
								(d) pipelinedeals.expectedCloseDate	-	The expected close date of the PipelineDeals deal.
								(e) pipelinedeals.value	-	The value of the PipelineDeals deal.
								(f) pipelinedeals.summary	-	Summary of the deal associated with the campaign.
		- Special Notes: Clickers should have the first name and last name values set in MailChimp to be properly created in PipelineDeals.
																		
	[iii] Case - 003
		- Purpose : (a) Retrieve potentials in 'follow-up' stage from ZohoCRM and create them as deals in PipelindeDeals (only the potentials which are created/modified on the current day).
						- Make sure the the potentials have values for the following fields (except Mandatory fields): Probability, Amount and Description.
					(b) Create the associated ZohoCRM account as company in PipelindeDeals if it doesn't already exist.
					(c) Create the associated ZohoCRM contact as person in PipelineDeals if he/she doesn't already exist.
					
		- Files:	(a)	Proxy - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Origination\Case-003\proxy\pipelinedeals_retrievePotentialsAndCreateDeals.xml
					(b) Sequence - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Origination\Case-003\sequence\s01-c003-verifyPrerequisites.xml
					
		- Request Parameters:   All parameters are self-explanatory and specific to the individual APIs.
		
		- Special Notes: Execution time of the scenario would be fairly longer since a timeout has been added in the scenario to prevent duplication of companies and people in PipelineDeals.
		
																		
	[iv] Case - 004
		- Purpose : (a) Retrieve people from PipelineDeals add them to the Mailchimp subscribers' list (identified by the list name).
					
		- Files:	(a)	Proxy - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Origination\Case-004\proxy\pipelinedeals_addPeopleToSubscribersList.xml
					
		- Request Parameters:   (a) mailchimp.listName - Name of the list to which subscribers would be added (results would be unpredictable if invalid list name is provided).
		
		
 02. Deal Management	
 
	[i] Case - 001
		- Purpose : (a) Create a calendar entry for a deal in PipeLineDeals API and then create calendar event in Google Calendar API if the Type is equal to 'CalendarEntry' or create task in Wunderlist API if the type is equal to 'CalendarTask'.
		
		- Files:	(a) Proxy - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Management\Case-001\proxy\pipelinedeals_createCalendarEventAndTasks.xml
					(b) Sequences - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Management\Case 001\sequences\pipelinedeals_manageEvents.xml
									<PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Management\Case 001\sequences\pipelinedeals_manageTasks.xml
									<PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Management\Case 001\sequences\S002_C001-verifyPrerequisites.xml
					(c) Templates - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Management\Case 001\templates\createCalendarEvents.xml
		
		- Request Parameters:	(a) pipeLineDeals.calendarEntry - JSON object which containing the following attributes to create a calendar entry in PipeLineDeals API.
										i) name - Name of the calendar entry.
										ii) description - Description for the calendar entry
										iii) type - type of the calendar entry (possible values are 'CalendarEvent' and 'CalendarTask').
										iv) startTime - Start time of the calendar entry only if the calendar entry type is 'CalendarEvent' (e.g.: 2015-06-28 08:00:00).
										v) endTime - End time of the calendar entry only if the calendar entry type is 'CalendarEvent' (e.g.: 2015-06-30 11:47:15).
										vi) dueDate - Due date of the calendar enrty only if the calendar entry type is 'CalendarTask' (e.g.: 2016-01-08).
										vii) dealId	- Pipeline deal ID to create a calendar entry ID.
								(b)	googleCalendar.calendarId - Google calendar ID to create calendar events.
								(c) wunderlist.remindOnDaysBefore - Number of days to remind before the task due date.
		- Special Notes:	Deals used in the scenario must have a person associated with it.
		
	[ii] Case - 002
		- Purpose : (a) Retrieve Wunderlist completed tasks for the PipelineDeals deals in the stage of 'Active'. Update the associated tasks in PipelineDeals as 'completed'.
		- Files:	(a)	Proxy-	<PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Management\Case-002\proxy\pipelinedeals_retrieveDealsAndUpdateTasks.xml
					(b) Sequence - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Management\Case-002\sequence\S02-C002-verifyPrerequisites.xml

	[iii] Case - 003
		- Purpose : (a) Iterate over the files created in a temporary folder in Dropbox and,
						(i)   Move the files to a permanent folder in Dropbox account.
						(ii)  Create them as documents in PipelineDeals under the deal the files are associated with.
						(iii) Send an email to the owner of the deal, notifying that a new document has been created for the Deal in PipelineDeals.
						
		- Files:	(a)	Proxy - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Management\Case-003\proxy\pipelinedeals_synchronizeDocuments.xml
					(b) Sequence - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Management\Case-003\sequence\base64DecodeDropboxResponse.xml
					
		- Request Parameters:   (a) dropbox.newFilesPath - Path of the temporary folder in Dropbox inside which new files will be uploaded.									
								(b) dropbox.permanentDealFilesPath - Path of the permanent folder in Dropbox into which the files would be be moved for permanent storage.
								
		- Special Notes:	(a) New files uploaded to the temporary folder in dropbox should be named according to the following format: <PipelineDeals Deal ID>-<File name>.<extension>
							    Each file should be associated to an existing deal in PipelineDeals. This is done by prepending the deal ID to the file name seperated by a hyphen.
						 
							(b) Execution time of the scenario would be fairly longer since a timeout has been added in the scenario to overcome rate limiting issue.
	
03. Deal Invoicing		
	[i] Case - 001
		- Purpose : (a) Retrieve won deals from Pipelinedeals API (which are not created in Billomat API) and create Invoices and update the relavant potential  status as “closed won” in ZohoCRM API.
		
		- Files:	(a)	Proxy - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Invoicing\Case 001\proxy\pipelinedeals_createClientAndInvoiceForWonDeals.xml
					(b) Sequences - <PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Invoicing\Case 001\sequences\completeInvoiceAndSend.xml
									<PIPELINEDEALS_CONNECTOR_HOME>\pipelinedeals-integrated-scenarios\src\scenarios\Deal Invoicing\Case 001\sequences\S003_C001-verifyPrerequisites.xml						
		
		- Request Parameters:   All parameters are self-explanatory and specific to the individual APIs.
	