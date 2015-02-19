Product: FreeAgent Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0-SNAPSHOT with patches.
 
 - Upload the following connectors to the ESB.
 
			-> freeagent-connector-1.0.0
			-> basecrm-connector-2.0.0
			-> tsheets-connector-1.0.0
			-> zohobooks-connector-1.0.0
			
 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.
			
			FreeAgent - https://docs.wso2.com/display/CONNECTORS/FreeAgent+Connector
			BaseCRM - https://docs.wso2.com/display/CONNECTORS/Base+CRM+Connector
			TSheets - https://docs.wso2.com/display/CONNECTORS/TSheets+Connector
			ZohoBooks - https://docs.wso2.com/display/CONNECTORS/Zoho+Books+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<FREEAGENT_CONNECTOR_HOME>/freeagent-integrated-scenarios/src/common ), to the ESB that are listed as below.
			- sequences - faultHandlerSeq.xml
			- templates - responseHandlerTemplate.xml
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			

 01. Project Management
	[i] Case -001
		- Purpose : (a) Retrieve won deals from BaseCRM and create new projects with a new contacts in FreeAgent and add job codes in Tsheets.
		
		- Files:	(a) Proxy - <FREEAGENT_CONNECTOR_HOME>\freeagent-integrated-scenarios\src\scenarios\Project Management\Case-001\proxy\freeagent_retrieveWonDealsAndCreateProjectsAndJobCodes.xml
					(b) Template - <FREEAGENT_CONNECTOR_HOME>\freeagent-integrated-scenarios\src\scenarios\Project Management\Case-001\templates\createProject.xml
		- Request Parameters:	
		- Special Notes: (a) For project creation in FreeAgent, using project budget unit value as "Hours" for this scenario case. 
						 (b) For add job codes in Tsheets, using short code value as the retrieved won deal ID. 
 
 01. Manage Invoices and Estimates
	[i] Case -001
		- Purpose : (a) Retrieve timesheets for a specific time period from tsheets and create associated tasks, timeslips and invoices in FreeAgent . Create the same invoice in ZohoBooks and send that to the clients.
		
		- Files:	(a) Proxy - <FREEAGENT_CONNECTOR_HOME>\freeagent-integrated-scenarios\src\scenarios\Manage Invoices and Estimates\Case-001\proxy\freeAgent_manageTimeSlipsAndInvoices.xml
					(b) Templates - <FREEAGENT_CONNECTOR_HOME>\freeagent-integrated-scenarios\src\scenarios\Manage Invoices and Estimates\Case-001\templates\manageTimeSlipsAndInvoices.xml
									<FREEAGENT_CONNECTOR_HOME>\freeagent-integrated-scenarios\src\scenarios\Manage Invoices and Estimates\Case-001\templates\createContactsAndInvoices.xml
		
		- Request Parameters:	(a) tSheetsStartDate - Timesheets with the date falling on or after will be retrieved.  				
								(b) tSheetsEndDate - Timesheets with the date falling on or before will be retrieved..
								(c) projectMap - List of key-value pairs for which TSheets jobcobe IDis mapped with the FreeAgent project ID.
								(d) userIdMap - List of key-value pairs for which TSheets  user ID is mapped with the FreeAgent user ID.
								(e) freeAgentTimeSlipDate - The date specified for the FreeAgent timeSlip.
							    (f) freeAgentInvoiceDate - The date specified for the FreeAgent invoice.
								(g) freeAgentPayementTermsInDays - Number of days within which the invoice payment could be made.
								(h) contactMap - List of key-value pairs for which FreeAgent contact ID is mapped with the ZohoBooks contact ID.