Product: Sirportly Integrated Scenarios

Environment Set-up:

 - Download and initialize wso2esb-4.9.0-BETA-SNAPSHOT.
 
 - Upload the following connectors to the ESB.
 
			-> sirportly-connector-1.0.0
			-> cashboard-connector-1.0.0
			-> formstack-connector-1.0.0
			-> mandrill-connector-1.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            Sirportly 	-	https://docs.wso2.com/display/CONNECTORS/Sirportly+Connector
			Cashboard	-	https://docs.wso2.com/display/CONNECTORS/Cashboard+Connector
			Formstack	-	https://docs.wso2.com/display/CONNECTORS/Formstack+Connector
			Mandrill  	-	https://docs.wso2.com/display/CONNECTORS/Mandrill+Connector	
			
 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory(inside <SIRPORTLY_CONNECTOR_HOME>/Sirportly-integrated-scenarios/src/common), to the ESB that are listed as below.
			- sequences - 	faultHandlerSeq.xml
							removeResponseHeaders.xml
			- templates - 	responseHandlerTemplate.xml
						    base64Decoder.xml
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			

 - Please note that the cases need to be executed in the same order as they appear in this document as they are inter-related and cannot be executed independently.

 - Common Assumptions :	
					(a)All the client companies in Sirportly are available in Cashboard.
					(b)Only the Sirportly tickets that are created through the scenario execution will be considered for this automation and the tickets that are created in Sirportly (offline) will be skipped.
					(c)All the Sirportly tickets which are reported by the clients will be added to the default Brand of the Sirportly account and to the 'Helpdesk Support' department that is created in Common Prerequisites (a) section.
					(d)All the Sirportly tickets will be created as project lists in Cashboard under the same project call 'Helpdesk Support' that is created under Common Prerequisites (b) section.
					(e)All the project lists that are created from this automation will hold only one task call 'Workshop Operations' 
					(f)A ticket in Sirportly is equivalent to a project-list in Cashboard.
					
 - Common Prerequisites:	
					(a)There must be a Department named 'Helpdesk Support' for the Brand of the company (there will be a default Brand created for the company on creation of the Sirportly account).
					(b)Create a project in Cashboard named as 'Helpdesk Support' and keep the projectId for further reference.									
					(c)These custom fields are required in Sirportly
					  (Custom fields can be created by following 'Admin'-->'Work Flow'-->'Custom Fields')
					  
					  NAME			        SYSTEM NAME					TYPE OF FIELD		DESCRIPTION
					- Cashboard Project Id	Cashboard-projectId			Text/String			The Cashboard project Id to which the ticket belongs to.
					- Is BIllable 			IsBIllable					Text/String			Indicates whether the ticket is billable or not.
					- Total Estimation 		totalEstimation				Text/String			Holds the total estimation amount of this ticket.

 01. Ticket Initiation

    Case-001 -> 
		- Scenario Name:	createTicketAndNotifyUsers
		- Purpose:   	    (a)Retrieve unread submission of forms through which the client reports issues from Formstack API and create them as tickets in Sirportly.
							(b)Assign the tickets to assignees in Sirportly.
							(c)If the requester of the ticket doesn't exist in Sirportly, then create them as clients under their company.
							(d)On successful creation of the ticket in Sirportly, notify the requester and the assignee of the ticket about it via Mandril.
							
		- Files:   			(a)Proxy - sirportly_createTicketAndNotifyUsers.xml
							(b)Sequence - authenticateCashboardRequest.xml
										  getClientCompany.xml
		
		- Prerequisites:	(a)A form should be created and published in Formstack with the following fields.
							   (i)  Company ID (Short Answer field)
							   (ii) Your Name (Short Answer field)
							   (iii)Your Email Address (Email Address field)
							   (iv) Priority of the Ticket (Dropdown List field)
									Note: set 'Normal', 'Urgent' and 'High' as options of the dropdown list
							   (v)  Type a brief Subject (Short Answer field) 	
							   (vi) Description (Long Answer field) 
							(b)All the client companies should exist in Sirportly account and Cashboard so that every requester of the ticket know their company ID in advance. 
							(c)There should be at least one assignee in Sirportly account. The assignee (s) should belong to the team type, "Staff".
		- Note:
							(a)When creating the form in FormStack as an offline process, please make sure that the fields are created in the above given order for the case to work accurately.
							(b)The requesters of the tickets should be aware of the Company ID which is provided for their company (This is the relevant client's company in Cashboard.) 
							
		- Request Parameters: 
							(a)formstack.formId - Id of the form that is created under section (a) of Prerequisites. 
							(b)sirportly.agentIds - Comma separated values of assignee ids. Use assignees created under section (c) of Prerequisites. 
							(c)sirportly.brandName - Use the default brand name that is created in Sirportly. Use the brand name obtained under Common Prerequisites section (a).
							(d)sirportly.cashboardProjectId - Cashboard project Id under which all of the tickets should go. Use the project created under Common Prerequisites section (b).
							(e)sirportly.departmentName - Use the department name created in Sirportly under Common Prerequisites section (a).
							(f)mandrill.fromEmail - Use a valid email address from which the Mandril emails are required to be sent.
							(g)mandrill.fromName - Use a valid name indicating from whom the Mandril emails are required to be sent (Company name of the account can be used here).
							
							* Note that all the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request. Refer to the individual connector methods for clarification.
							
		- Note : 			(a)By default all the tickets will be created as billable tickets. If required, this can be changed as non-billable as an offline process. To do so change the boolean value of the Sirportly custom field of the ticket 'Is BIllable' to 'false'.
				
 02. Ticket Management

    Case-001 -> 
		- Scenario Name:	createProjectListAndEstimates
		
		- Purpose:   	    (a) Retrieve unresolved tickets details on a daily basis from Sirportly API and for each ticket, create a new projectList in the Cashboard API.
							(b) Create the client of the ticket as a client in Cashboard API and add him to the company, if they do not exist. Make the client the owner of the ticket in the Cashboard API. 
							(c) Create the assignee as an employee in Cashboard if they do not exist. 
							(d) Add a task named as 'Workshop Operations' in the above created new ProjectList in Cashboard and assign it to the relevant assignee as in Sirportly.
							(e) Create a draft estimate for the Client of the ticket in Cashboard API. 
							
		- Files:   			(a)Proxy - sirportly_createProjectListAndEstimates.xml
							(b)Sequence - mapClientCompanyDetails.xml								  
		
		- Prerequisites:	(a)Need a filter to retrieve unresolved tickets that are created in the current date.
								- Filters can be created by following 'Admin'-->'Ticket Settings'-->'Filters'
								- Filter must match all the following conditions							
									Status -> is not -> Resolved
									Last post time(range) -> within -> Today
									Brand is <Brand of the company>
									Department is <Brand of the company - Helpdesk Support>
		- Request Parameters: 
							(a)sirportly.ticketsFilterName - Name of the filter which is used to filter the daily unresolved tickets of a specific Department of a Brand.Use the filter created under Prerequisites section (a).
							
							* Note that all the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request. Refer to the individual connector methods for clarification.
				
    Case-002 -> 
		- Scenario Name:	retrieveEstimatesAndNotifyClients
		
		- Purpose:   	    (a) Retrieve all unarchived estimates of clients in Cashboard in a daily basis and for each estimate if it needs client agreement and if it is billable, send the estimate to the client via Mandrill. 
							(b) Update the total estimation amount of the ticket in Sirportly with the calculated total estimation value of the project list in Cashboard.
							
		- Files:   			(a)Proxy - sirportly_retrieveEstimatesAndNotifyClients.xml  
		
		- Prerequisites:	(a)For each client's estimation, estimation line items should be added by the assignee as an offline process.
							(d)Each estimate's 'Requires agreement' field should be checked in order to make it eligible to be sent to the client.
							
		- Assumptions:		(a)Estimates will be created for all the clients in each project list but only the billable ones and that require client agreement will be sent to the relevant clients.
		
		- Request Parameters: 
							(a)cashboard.updatedSince - This date will be considered to filter the estimates which are last updated after this date and time. 
								Note:- Follow the format 'YYYY-MM-DD HH:MM:SS'
								If this parameter is not set, the case will consider the beginning of the current day's time by default.
								e.g:- YYYY-MM-DD 00:00:00  
							(b)mandrill.fromName - Use a valid name indicating from whom the Mandrill emails are required to be sent (Company name of the account can be used here).
							(c)mandrill.fromEmail - Use a valid email address from which the Mandrill emails are required to be sent.
							
							* Note that all the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request. Refer to the individual connector methods for clarification.    
	
    Case-003 ->
		- Scenario Name: 	updateTicketsWithComments	
		
		- Purpose:   	    (a)Retrieve comments of project list tasks from Cashboard API and update the ticket with the comment in Sirportly API.
							
		- Files:   			(a)Proxy - sirportly_updateTicketsWithComments.xml
							
		- Assumptions:		(a)A ProjectList can have only one lineItem (task) named as 'Workshop Operations'.
							(b)Only the project lists created by scenario will be considered for this case.	
							
		- Prerequisites:	(a)The timeZone settings should set in Cashboard account having set the account's TimeZone as same as the user's location.
								To set the TimeZone follow :
								Settings -> Preferences -> TimeZone value : {TimeZone of the users' location}
		- Request Parameters: 
							(a)cashboard.updatedSince - Preferred date from which the comments of the projectList task needs to be considered. 
								(Should strictly follow the date format: YYYY-MM-DD HH:MM:SS, example: 2015-05-06 00:00:00)

							* Note that all the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request. Refer to the individual connector methods for clarification.
		
		- Note:				 (a)If the value for cashboard.updatedSince parameter is not provided, the beginning time of the current date (YYYY-MM-DD 00:00:00) will be considered as the default value.
							 		
    Case-004 -> 
		- Scenario Name:	sirportly_sendInvoiceAndNotifyClients
		
		- Purpose:   	    (a)Retrieve invoices of tickets which are billable in Cashboard and send them to the client via Mandrill. 
							(b)Retrieve completed project lists from Cashboard API and complete the ticket in Sirportly API as well and notify the client with the resolution of the ticket via Mandrill API.(Note: Resolution of a ticket is notifed to the client, only if they are not billable.)
							
		- Files:   			(a)Proxy - sirportly_sendInvoiceAndNotifyClients.xml
							(b)Template - sirportly_sendInvoiceToClient.xml
										  sirportly_sendMessageToClient.xml
		- Request Parameters: 
							(a)cashboard.projectId - Id of the project that is created under section (b) of Common Prerequisites. 
							(b)cashboard.updatedSince - This date will be considered to filter the project-list which are last updated after this date and time. 
														Note:- Follow the format 'YYYY-MM-DD HH:MM:SS'
														If this parameter is not set, the case will consider the beginning of the current day's time by default.
														e.g:- YYYY-MM-DD 00:00:00  
							(f)mandrill.fromEmail - Use a valid email address from which the Mandrill emails are required to be sent.
							(g)mandrill.fromName - Use a valid name indicating from whom the Mandrill emails are required to be sent (Company name of the account can be used here).
							
							
							* Note that all the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request. Refer to the individual connector methods for clarification.
		
		- Assumptions:		(a) Invoices are created only for the billable tickets. 
		
		- Prerequisites:	(a) Task of the project list should be completed and closed of the resolved tickets as an offline process. 
							(b) Invoices should be created from estimates for the billable tickets as an offline process. 