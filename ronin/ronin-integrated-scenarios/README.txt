Product: Ronin Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0-SNAPSHOT with patches.
 
 - Upload the following connectors to the ESB.
 
			-> ronin-connector-1.0.0
			-> zohobooks-connector-1.0.0
			-> googletasks-connector-1.0.0
			-> capsulecrm-connector-1.0.0
			-> pipedrive-connector-1.0.0
			
 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.
			
			Ronin - https://docs.wso2.com/display/CONNECTORS/Ronin+Connector
			Zohobooks - https://docs.wso2.com/display/CONNECTORS/Zoho+Books+Connector
			Google Tasks - https://docs.wso2.com/display/CONNECTORS/Google+Tasks+Connector
			Capsule CRM - https://docs.wso2.com/display/CONNECTORS/Capsule+CRM+Connector
			Pipe Drive - https://docs.wso2.com/display/CONNECTORS/Pipedrive+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<RONIN_CONNECTOR_HOME>/ronin-integrated-scenarios/src/common), to the ESB that are listed as below.
			- sequences - faultHandlerSeq.xml
			- templates - responseHandlerTemplate.xml
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			
 
 01. Project Initiation and Management
 
	[i] Case -001
		- Purpose : (a) Retrieve selected won deals from Pipedrive API and create Project in Ronin API. (In case if the related Client/Contact does not exist, create the same in Ronin API.)
					(b)	Retrieve selected won opportunities from CapsuleCRM and create Project in Ronin API. (In case if the related Client/Contact does not exist, create the same in Ronin API.)
		- Files:	(a)	Proxy - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Initiation and Management\Case-001\proxy\Ronin_createProjectsFromDealsAndOpportunities.xml
					(b)	Sequences - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Initiation and Management\Case-001\sequences\createClientsContactsAndProjects.xml
		- Request Parameters: (a) pipedrive.filterId - ID of the deal filter to retrieve won deals. To create a new deal filter, use the following conditions as the filter set.
													   => Deal status = 'Won'
													   => Deal won time = 'Today'
							  (b) capsulecrm.opportunityActualClosedDate - Date value for filter won opportunities, with actual closed date (The format should be yyyy-mm-dd).
	
	[ii] Case -002
		- Purpose : (a) Create tasks for the projects in Ronin API and create the same in Google Tasks if its required.
		- Files:	(a) Proxy - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Initiation and Management\Case-002\proxy\ronin_createTasks.xml		
		- Request Parameters:   (a) ronin.projectId - The ronin project ID to which the the tasks will be created.									
								(b) shared.taskDetails - An array of objects with the following properties.
									(i).	googleTasksTaskListId - List ID of the Google Tasks API. (Task will be created in Google Tasks API only if the list ID is provided.)
									(ii). 	taskTitle -  Title of the task.
									(iii).	taskDescription - Description of the task.
									(iv).	dueDate - The due date of the task.
																		
	[iii] Case -003
		- Purpose : (a)  Retrieve updated tasks from Google Tasks API and do the relevant changes in the corresponding tasks in Ronin API on a daily basis.
		- Files:	(a) Proxy - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Initiation and Management\Case-003\proxy\ronin_updateTasks.xml
		- Request Parameters:	(a) googletasks.taskListId - Task list ID from which the tasks are retrieved.
		
 02. Project Accounting		
 
	[i] Case -001
		- Purpose : (a) Retrieve invoices on a daily basis from Ronin API and created them in Zohobooks API. (In case if the related Client/Contact does not exist, create the same in Zohobooks API.)
		- Files:	(a)	Proxy - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Accounting\Case-001\proxy\ronin_createInvoicesInZohobooks.xml
					(b)	Templates - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Accounting\Case-001\templates\zohobooks-createContact.xml
					(c)	Templates - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Accounting\Case-001\templates\zohobooks-createTax.xml
		- Note : This scenario support for only one tax per invoice.			
					
	[ii] Case -002
		- Purpose : (a) Retrieve estimates for selected clients from Ronin API and created them in Zohobooks API. (In case if the related Client/Contact does not exist, create same in Zohobooks API.)
		- Files:	(a)	Proxy - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Accounting\Case-002\proxy\ronin_createEstimatesInZohobooks.xml
					(b)	Templates - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Accounting\Case-002\templates\zohobooks-createContact.xml
					(c)	Templates - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Accounting\Case-001\templates\zohobooks-createTax.xml
		- Request Parameters:   (a) ronin.clientId - The Ronin client ID to retrieve estimates.
		- Note : This scenario support for only one tax per estimate.

	[iii] Case -003
		- Purpose : (a) Retrieve invoice payments on a daily basis from Zohobooks API and created them in Ronin API.
		- Files:	(a)	Proxy - <RONIN_CONNECTOR_HOME>\ronin-integrated-scenarios\src\scenarios\Project Accounting\Case-003\proxy\ronin_createInvoicePaymentFromZohobooks.xml
					