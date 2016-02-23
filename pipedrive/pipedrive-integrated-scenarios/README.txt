Product: PipeDrive Business Scenarios

Environment Set-up:

	- Download and initialize ESB 4.9.0
 
	- Upload the following connectors to the ESB.
		. pipedrive-connector-1.0.0
		. proworkflow-connector-1.0.0
		. zohocrm-connector-2.0.0
		. googlecalendar			
		. gmailRest-connector-1.0.0
		. freshbooks-connector-2.0.0
					
	- Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.

		. PipeDrive 		- https://docs.wso2.com/display/CONNECTORS/Pipedrive+Connector
		. ProWorkFlow 		- https://docs.wso2.com/display/CONNECTORS/ProWorkflow+Connector
		. ZohoCRM 			- https://docs.wso2.com/display/CONNECTORS/ZohoCRM+Connector
		. GoogleCalendar 	- https://docs.wso2.com/display/CONNECTORS/Google+Calendar+Connector
		. GMail 			- https://docs.wso2.com/display/CONNECTORS/Gmail+Connector
		. FreshBooks		- https://docs.wso2.com/display/CONNECTORS/FreshBooks+Connector
		
	- If required, add the corresponding security certificates to the ESB for the aforementioned connectors. 
	
	- Add the sequences and templates in the "common" directory (<PipeDrive_Connector_Home>/pipedrive-integrated-scenarios/src/common), to the ESB that are listed as below.
		. sequences - 	faultHandlerSeq.xml
		. templates - 	responseHandlerTemplate.xml
 
	- Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			
	
		01. InitiateDeal
			[i] Case 001
				. Scenario Name	:	Initiate Deal	
				. Description	:	Retrieves Quotes from ProWorkFlow and Potentials from ZhoCRM, then creates Deals using the retrieved details.
									If the Organization does not exist(Checks against the Organization names) in PipeDrive creates a new Organization and associates to the new Deals being created.
									If the Person does not exist(Checks against the Person emails) in PipeDrive creates a new Person and associates to the new Deals being created.
									Deal's source Id (Potential Id or Quote Id) will be added as a custom field.									
				. Prerequisite	:	Should create two custom Fields for Deals to hold corresponding Potenial Ids and Quote Ids.
									Please refer the PipeDrive Connector README.txt file to create these. 
									Provide the system generated hash key values for the corresponding parameters(quoteIdCustomField, potentialCustomField) in the request.
				. Reference		: 	Scenario Guide Document -> Chapter 3.1 -> Initiate Deal
			
		02. FollowUpDeals
			[i] Case 001
				. Scenario Name	:	Create Deal related Activities and Calendar Events.
				. Description	: 	Create Deal related activities in Pipedrive, and create Google Calendar Events using same details. Deal contacts will be notified via an email.
				. Note			:	dueDate(YYYY-MM-DD) parameter specifies the Activity and/or Event date.
									dueTime(HH:MM) parameter specifies the start time of the Activity and/or Event, and the parameter value should be in GMT Time Zone. 
									duration(HH:MM) parameter specifies the time duration of the Activity and/or Event. Maximum value is 23:59.
									Activity and Event times will be displayed in PipeDrive and Google Calendar according to its' respective TimeZones.
									
				. Assumption	:	Deal Persons'(Contacts') email addresses are captured in the account.
				. Reference		: 	Scenario Guide Document -> Chapter 3.2 -> Follow-up Deal
			[ii] Case 002
				. Scenario Name	:	Notify Deal Followers.
				. Description	: 	Retrieves open deals which are expected to be closed in two weeks’ time from Pipedrive, and notify the Deal Followers by sending emails.
				. Assumption	:	Deal Followers’ email addresses are captured when creating such Users.
				. Reference		: 	Scenario Guide Document -> Chapter 3.2 -> Follow-up Deal
		03. InvoicingDeals
			[i] Case 001
				. Scenario Name	:	Update Deal status as Won.
				. Description	: 	Retrieves Deal details from Pipedrive and updates the deal status as 'won'. Then updates Deal origin's status accordingly.
									If the Deal was created using ZohoCRM potential, it will be updated as 'closed won'.
									If the Deal was created using ProWorkFlow quote, it will be updated as 'approved'.	
				. Prerequisite	:	Provide the system generated hash key values(used in the Case001) for the corresponding parameters(quoteIdCustomField, potentialCustomField) in the request.
				. Assumption	:	Deal Persons'(Contacts') email addresses are captured in the account.
				. Reference		: 	Scenario Guide Document -> Chapter 3.2 -> Invoicing Deals
			[ii] Case 002
				. Scenario Name	:	Create and send Invoices.
				. Description	: 	Retrieves Won Deals from Pipedrive and creates Invoices in FreshBooks and sends them. 
									If the Client is not available in Freshbooks, will be created prior to creating the Invoices. Also updates the Deals to associate the corresponding Invoice Id.
				. Prerequisite	:	Should create a custom Field for Deals to hold the created Invoice's Id for the corresponding Deal.									
									Please refer the PipeDrive Connector README.txt file to create these. 
									Provide the system generated hash key value of the created custom field to invoiceIdCustomField parameter.
				. Assumption	:	Deal Persons'(Contacts') email addresses are captured in the account.
				. Reference		: 	Scenario Guide Document -> Chapter 3.3 -> Invoicing Deals
		
