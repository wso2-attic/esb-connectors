Product: Insightly Integrated Scenarios

Environment Set-up:

 - Download and initialize WSO2 ESB 4.9.0 - SNAPSHOT .
 
 - Upload the following connectors to the ESB.
 
			-> mailchimp-connector-2.0.0
			-> insightly-connector-1.0.0
			-> callrail-connector-1.0.0
			-> freshbooks-connector-2.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            Insightly 	-   https://docs.wso2.com/display/CONNECTORS/Insightly+Connector
			Mailchimp	- 	https://docs.wso2.com/display/CONNECTORS/MailChimp+Connector
			Callrail	- 	https://docs.wso2.com/display/CONNECTORS/CallRail+Connector
			Freshbooks  - 	https://docs.wso2.com/display/CONNECTORS/FreshBooks+Connector
			
 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory and "messages" directory(inside <INSIGHTLY_CONNECTOR_HOME>/insightly-integrated-scenarios/src ), to the ESB that are listed as below.
			- sequences - 	faultHandlerSeq.xml
							removeResponseHeaders.xml
			- templates - 	responseHandlerTemplate.xml		
							insightly_getCustomFieldId.xml
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			

 01. Project Initiation
	[i] Case-001
		- Purpose:	(a) Create a draft campaign in Mailchimp (Subject and Content of the campaign is provided by the user).
					(b) Send the campaign to a list of subscribers already registered in Mailchimp (added to a Subscribers' list).
					
		- Files:	(a) Proxy - <INSIGHTLY_CONNECTOR_HOME>/insightly-integrated-scenarios/src/scenarios/Project Initiation/Case 001/proxy/insightly_createAndSendCampaign.xml
		
		- Request Parameters:   
					(a) mailchimpListName - Name of the subscribers' list, to whose contacts the campaign is expected to be sent.
											Create a subscribers' list in Mailchimp API and add subscribers to it.
											It is necessary to have at least one subscriber in the list in order to send the campaign.
					(b) mailchimpTemplateId - ID of the template using which the campaign is to be created.
											  Create a template in Mailchimp using the provided html file: <INSIGHTLY_CONNECTOR_HOME>/insightly-integrated-scenarios/mailchimp_template.html.
					(c) mailchimpCampaignSubject - Subject of the campaign to be sent.
					(d) mailchimpContent - Content of the campaign to be sent.
					(e) mailchimpFromEmail - Email of the sender of the campaign.
					(f) mailchimpFromName - Name of the sender of the campaign.	
		
	[ii] Case-002
		- Purpose:	(a) Create a subscribers' list in Mailchimp API and add subscribers to it
					(b) Retrieve clickers for the URL in embedded the campaign and create them as Leads in Insightly.
					(c) Retrieve callers from CallRail and create them as Leads in Insightly.
							
		- Files:	(a) Proxy - <INSIGHTLY_CONNECTOR_HOME>/insightly-integrated-scenarios/src/scenarios/Project Initiation/Case 002/proxy/insightly_createCampaignClickersAndCallersAsLeads.xml
					(b) Sequence - <INSIGHTLY_CONNECTOR_HOME>/insightly-integrated-scenarios/src/scenarios/Project Initiation/Case 002/sequence/createCallersAsContacts.xml
		- Request Parameters:	(a) mailchimpCampaignId - ID of the campaign whose embedded URLs are tracked.
								(b) mailchimpClickURL - URL in the campaign which is tracked.
								(c) callrailCompanyId - ID of the company in CallRail.
														Create a company in CallRail and add a Phone number to it for tracking.
														At least one call should be made to that number to track callers.
								(c) callrailStartDate - Start date from when the calls are retrieved.
								(c) callrailEndDate - End date till when the calls are retrieved.
		
		- Special Notes: (a) Note that this case of the scenario needs to be executed following the previous case as it is part of the flow.
						 (b) Prior to the execution of the case, a custom field called 'Contact Type' should be created under contacts in Insightly.
							 The custom field should be a drop down combo field with the following values: Lead, Contact, Client.
						 (c) Note that there is no way of keeping track of which clickers (from MailChimp) and callers (from CallRail) were created in Insightly.
							 If the scenario is executed multiple times, new leads would be created in Insightly for the same clicker or caller multiple times.
		
	[iii] Case-003
		- Purpose:	(a) Update a contact whose is of type 'Lead' to type 'Contact'.
					(b) Create a opporunity for the contact using the information provided by the user (through the request).
							
		- Files:	(a) Proxy - <INSIGHTLY_CONNECTOR_HOME>/insightly-integrated-scenarios/src/scenarios/Project Initiation/Case 003/proxy/insightly_updateContactAndCreateOpportunity.xml
		
		- Request Parameters:	(a) insightlyContactId - ID of the contact whose type is to be updated to 'Contact' (Needs to be a valid ID).
								(Refer to Insightly connector - createOpportunity method for description of related properties)
		
		- Special Notes: (a)  Note that this case of the scenario needs to be executed following the previous case as it is part of the flow.
						 (b)  Provided contact need to be of type 'Lead' for the scenario to be successfully executed.
		
	[iv] Case-004
		- Purpose:	(a) Update the provided opportunity as 'Won'.
					(b) Create a project in Insightly for the won opportunity.
					(c) Create a project in Freshbooks for the won opportunity.
					(d) Update the provided contact as type 'Client' in Insightly.
					(e) Create a client in Freshbooks for the contact.
							
		- Files:	(a) Proxy - <INSIGHTLY_CONNECTOR_HOME>/insightly-integrated-scenarios/src/scenarios/Project Initiation/Case 004/proxy/insightly_createClientAndProjectForWonOpportunity.xml
		
		- Request Parameters:	(a) insightlyOpportunityId - Valid ID of an opportunity in Insightly which is Won.
									The opportunity should have a value/amount associated with it (Currency of the amount and the currency configured in Freshbooks should be consistent).
								(b) insightlyContactId - Valid ID of a contact in Insightly who is of type 'Contact'.
		
		- Special Notes: (a)  Note that this case of the scenario needs to be executed following the previous case as it is part of the flow.
						 (b)  Prior to the execution of the case, a custom field (text) called 'Freshbooks Client ID' should be created under contacts in Insightly.
						 (c)  Prior to the execution of the case, two custom fields called 'Project ID' (text) and 'Freshbooks Project ID' (text) should be created under opportunities in Insightly.
						 (d)  Prior to the execution of the case, those contacts in Insightly who are created from CallRail should be given an email address.
		
 02. Invoicing
 
	[i] Case-001
		- Purpose:	(a) Retrieves monthly time entries and create invoices in the FreshBooks API for each month.
					(b) Add note containing the billing information to the relevant project in the Insightly API.
		
		- Files:	(a) Proxy - <Insightly_Connector_Home>/insightly-integrated-scenarios/src/scenarios/Invoicing/Case 001/proxy/insightly_createInvoicesAndAddNotes.xml
					(b) Template - 	<Insightly_Connector_Home>/insightly-integrated-scenarios/src/scenarios/Invoicing/Case 001/templates/getProjectName.xml
		- Request Parameters:	(a) freshbooksTimeEntryProjectId - The unique identifier of the Freshbooks project of which the monthly time entry records needs to be retrieved.
								(b) freshbooksUnitCostOfInvoiceLineItems - Unit cost of the line items of invoices that will be created in Freshbooks.
								(c) freshbooksClientIdOfInvoice -  The client ID that the Freshbooks invoice would be created for.
		- Prerequisite:  Adding timeEntries for the project created under 'Project Initiation'- case 004 should be done offline and the project should have at least one task in order to add a timeEntry.
			
		- Special Notes: In order to successfully execute the scenario, make sure that the case is executed only once in a month since the creation of Invoices in Freshbooks and the creation of Notes will not be checked for duplicates due to the behaviour of the API.
							
