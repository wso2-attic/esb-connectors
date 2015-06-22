Product: Bugzilla Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0-SNAPSHOT with patches.
 
 - Upload the following connectors to the ESB.
 
			-> bugzilla-connector-1.0.0
			-> tsheets-connector-1.0.0
			-> freshdesk-connector-1.0.0
			-> zohobooks-connector-1.0.0
			-> mandrill-connector-1.0.0
			
 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.
			
			Bugzilla - https://docs.wso2.com/display/CONNECTORS/Bugzilla+Connector
			TSheets - https://docs.wso2.com/display/CONNECTORS/TSheets+Connector
			Freshdesk - https://docs.wso2.com/display/CONNECTORS/FreshDesk+Connector
			ZohoBooks - https://docs.wso2.com/display/CONNECTORS/Zoho+Books+Connector
			Mandrill - https://docs.wso2.com/display/CONNECTORS/Mandrill+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<BUGZILLA_CONNECTOR_HOME>\bugzilla-integrated-scenarios/src/common ), to the ESB that are listed as below.
			- sequences - faultHandlerSeq.xml
			- templates - responseHandlerTemplate.xml
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			
 01. Product Initiation
	[i] Case -001
		- Purpose : (a) Create projects in TSheets API(as job codes) and ZohoBooks API.
		- Files:	(a) Proxy - <BUGZILLA_CONNECTOR_HOME>\bugzilla-integrated-scenarios\src\scenarios\Product Initiation\Case-001\proxy\bugzilla_createProjects.xml		
		- Request Parameters:	(a) projectsDetails - An array of JSON objects which has the project name and the Zohobooks customer ID.
	
	[ii] Case -002
		- Purpose : (a) Retrieve selected projects(job codes) and its relevant sub modules(child job codes) from TSheets API and create the projects as ‘Products’ and sub modules as ‘Components’ in the Bugzilla API. Create users for the components in the ZohoBooks API and assign users to the project which was created in case-001.
		- Files:	(a) Proxy - <BUGZILLA_CONNECTOR_HOME>\bugzilla-integrated-scenarios\src\scenarios\Product Initiation\Case-002\proxy\bugzilla_createProductsAndComponents.xml		
		- Request Parameters:	(a) jobCodeDetails - An array of JSON objects which has the following parameters.
									(i) 	parentJobCode - ID of the Tsheets job code which was created as a project in case-001.
									(ii) 	zohobooksProjectId - ID of the Zohobooks project which created as a project in case-001.
									(iii) 	description - Description of the project.
									(iv) 	version - Product version of Bugzilla.
									(v) 	defaultAssignee - E-mail address of the Bugzilla user to assign to product as default assignee.
									(vi) 	childJobCodes - Array of JSON objects  which has the following data relevant to the Component.
												=> childJobCodeId - ID of the Tsheets job code (child job code) which created under Project(parent job code) as Component.
												=> defaultAsigneeEmail - E-mail address of the Zohobooks user to assign to the component.
												=> defaultAssigneeName - Name of the Zohobooks user to create, if does not exist.
												=> billableRate - Billable rate of each the component.
	[iii] Case -003
		- Purpose : (a) Retrieve project members from the TSheets API and if they do not exist in Bugzilla, create them as users in the Bugzilla API and notify the users with the relevant credentials and other information (Eg: BugZilla URL, Username/Login ID, and Password) using the Mandrill API.
		- Pre-requisites:  When retrieving users which are created in TSheets to Bugzilla, the respective TSheets user must have an email address.
		- Files:	(a) Proxy - <BUGZILLA_CONNECTOR_HOME>\bugzilla-integrated-scenarios\src\scenarios\Product Initiation\Case-003\proxy\bugzilla_createUsersForProjects.xml		
		- Request Parameters:	(a) jobCodeId - Tsheets job code ID which was created as a project to retrieve job code assignments.									
								(b) defaultPassword - Default password for new Bugzilla user accounts.
								(c) bugzillaLoginURL - URL of the Bugzilla login page to notify users.
								(d) fromEmail - From e-mail address to send Mandrill notification.
								(e) fromName - From name to send Mandrill notification.	
 02. Bug Creation

	[i] Case -001
		- Purpose : (a)	Retrieve tickets from the Freshdesk API which falls under the tag, ‘Bugs’ and 
							(i) 	Create tickets as bugs in Bugzilla.
							(ii) 	Create the bugs as tasks in Tsheet under the relevant component and update the task ID for the bug in BugZilla.
		- Pre-requisites: Follow the steps to set-up the Freshdesk account and Bugzilla account.
							(i)		Ensure to create a new ticket of the type "Bugs" if doesn't exist (Navigate to "Admin> Ticket Fields" to modify the ticket "Type" values).
							(ii)	Navigate to "Admin > Ticket Fields" and create the following custom fields as ticket fields in Freshdesk before creating the ticket in Freshdesk.
										a)	Create a new drop down menu called "Severity" with values "blocker" , "critical", "major", "normal", "minor", "trivial" and "enhancement" (Ensure to check 'Required when submitting the form' before saving the field).
										b)	Create a new drop down menu called "Operating System" with values "All", "Windows", Mac OS", Linux" and "Other" (Ensure to check 'Required when submitting the form' before saving the field).
										c) 	Create a new drop down menu called "Platform" with values "All", "PC", "Macintosh" and "Other" (Ensure to check 'Required when submitting the form' before saving the field).
										d)	Create a new drop down menu called "isBugbillable" with values "true" and "false" (Ensure to check 'Required when submitting the form' before saving the field).
										e)	Create a new drop down menu called "Product Version" and add the Bugzilla products version values as the drop down menu values (e.g. 1.0.0) (Ensure to check 'Required when submitting the form' before saving the field).
										f)	Create a new dependant field to map Bugzilla products, components and the Bugzilla components related to Tsheets job codes. Use the following labels to create this dependant field (Ensure to check 'Required when submitting the form' before saving the field).
												=> Level 1 : Product Name
															 Bugzilla Product Name
												=> Level 2 : Component
															 Bugzilla component name related to Level 1 : Bugzilla product.
												=> Level 3 : Child Job Code
															 TSheet child job code ID which related to Level 2 : Bugzilla Component.										
												   Add the dependant values once the dependant field labels are created correctly.
						   (iii)	Navigate to "Tickets" in Freshdesk and create a ticket view by filtering ticket "Created" as "Today" and the "Type" as "Bugs".
						   (iv)		Navigate to "Administration > Field Values" in Bugzilla, remove all the values in "Priority" field and replace the values with "Low", "Medium", "High" and "Urgent".
		
		- Files:	(a) Proxy - <BUGZILLA_CONNECTOR_HOME>\bugzilla-integrated-scenarios\src\scenarios\Bug Creation\Case-001\proxy\bugzilla_createBugsAndTasksFromFreshDeskTickets
					
		- Request Parameters:	(a) freshdeskTicketViewId - The unique identifier of the view tickets in Freshdesk. Place the ticket ID which was created in "Pre-requisites" step (iii).
 
 03. Bug Management
	[i] Case -001
		- Pre-requisite: All user in Bugzilla must be available in Tsheet as well and the email must be same as in Bugzilla.
		- Purpose : (a) Retrieve assignees of the bugs which are in 'IN_PROGRESS' status and assign that particular user for the associated task in the TSheets API.
		
		- Files:	(a) Proxy - <BUGZILLA_CONNECTOR_HOME>\bugzilla-integrated-scenarios\src\scenarios\Bug Management\Case-001\proxy\bugzilla_assignUsersToTasks.xml
	
	[ii] Case -002
		- Purpose : (a)	Retrieve resolved bugs for a particular product
							(i)		Update the related Freshdesk ticket ID status to 'RESOLVED'.
							(ii)	Create time entries and an invoice in the Zohobooks API.
		
		- Files:	(a) Proxy - <BUGZILLA_CONNECTOR_HOME>\bugzilla-integrated-scenarios\src\scenarios\Bug Management\Case-002\proxy\bugzilla_manageResolvedBugs.xml
					(b) Sequences - <BUGZILLA_CONNECTOR_HOME>\bugzilla-integrated-scenarios\src\scenarios\Bug Management\Case-002\sequences\freshDesk_updateTicketsSeq.xml
									<BUGZILLA_CONNECTOR_HOME>\bugzilla-integrated-scenarios\src\scenarios\Bug Management\Case-002\sequences\bugzilla-updateBugsSeq.xml
					(c)	Templates - <BUGZILLA_CONNECTOR_HOME>\bugzilla-integrated-scenarios\src\scenarios\Bug Management\Case-002\templates\createTasksAndTimeEntries.xml

		- Request Parameters:	(a) bugzillaLastInvoicedDate - Date which the last invoice was created.
								(b) bugzillaProduct - Name of the product from which the bugs are retrieved. 
								(c) zohoBookProjectId - ID of the project in Zohobooks which is associated with the Bugzilla product.
								(d) timeEntryMap - List of key-value pairs for which Bugzilla component name is mapped with the Zohobooks user ID .
