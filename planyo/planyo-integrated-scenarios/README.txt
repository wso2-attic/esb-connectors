Product: Planyo Business Scenarios

Environment Set-up:

 - Download and initialize ESB 4.8.1 .
 
 - Upload the following connectors to the ESB.
 
			-> planyo-connector-1.0.0
			-> constantcontact-connector-1.0.0
			-> mandrill-connector-1.0.0
			-> nexmo-connector-1.0.0
			-> freshbooks-connector-1.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            Planyo - https://docs.wso2.com/display/CONNECTORS/Planyo+Connector
			ConstantContact - https://docs.wso2.com/display/CONNECTORS/Constant+Contact+Connector
			Mandrill - https://docs.wso2.com/display/CONNECTORS/Mandrill+Connector
			Nexmo - https://docs.wso2.com/display/CONNECTORS/Nexmo+Connector
			FreshBooks - https://docs.wso2.com/display/CONNECTORS/FreshBooks+Connector
			
 - If required, add the corresponding security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<Planyo_Connector_Home>/planyo-integrated-scenarios/src/common), to the ESB that are listed as below.
			- sequences - 	faultHandlerSeq.xml
			- templates - 	responseHandlerTemplate.xml
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			

 01. Marketing
 		[i] Case 001
 			- Scenario Name: 		Marketing	
			- Description: 			Retrieve vouchers from Planyo, then creates an email campaign in ConstantContact for the retrieved vouchers and market them by sending the email campaign. Then retrieves existing list of users from the Playo and sends voucher details to Planyo users as an email notification via Mandrill.
			- Note:					Time zone of ConstantContact should always be 'London, United Kingdom' in order to get the exact same scheduled time of the ConstantContact mail campaign.
									Email campaign will create and schedule only if ConstantContact api token has provided
									Email notification will send to planyo users only if Mandrill api key has provided
									You can further filter out the Vouchers to be marketed using the following parameters.
										planyoRentalStartTime	- Planyo vouchers will return the rental to start on specified date. Date format should be 'YYYY-MM-DD' 
										planyoRentalEndTime		- Planyo vouchers will return the rental to end on specified date. Date format should be 'YYYY-MM-DD'
									
			- Prerequisite:			Should consist a contact list with a single(at least) contact in the ConstantContact. Make sure to verify email addresses of contacts which belongs to that created contact list in the ConstantContact. To verify email addresses navigate to My Account -> My Settings -> Verify Address in ConstantContact account.
									There should be atleast one Voucher in Planyo account.
			- Observations:			Created email campaigns will not send at scheduled time in ConstantContact trial account and campaigns may delay to deliver
			- Reference: 			Scenario Guide Document -> Chapter 3.1 -> Marketing
			
		[ii] Case 002
			- Scenario Name: 		Marketing
			- Description: 			Retrieves a list of contacts from ConstantContact who has clicked on a link in the sent email campaign, and create a contact list in ConstantContact.
			- Prerequisite:			Should consist a empty contact list in the ConstantContact.
									There should be at least one email clicker for the specified Campaign.
			- Reference:			Scenario Guide Document -> Chapter 3.1 -> Marketing
			
 02. Reservation Handling
		[i] Case 001
		    - Scenario Name:    	Reservation Handling
			- Description:      	Checks the availability for a given Resource using Planyo API. If the Resource is available, make the Reservation in Planyo API and send the Reservation details to Client via a Nexmo SMS. 
			- Prerequisite:     	In order to send the SMS, it is required to add the mobile number provided in planyoMobileNumber property to the list of 'Test Phone Numbers' in Nexmo account. You can add the number using https://dashboard.nexmo.com/private/settings#test.
									The Resources to be reserved should be added in your Planyo account as an offline process.
			- Note:             	Reservation details will be sent to the Client only if the 'nexmoAPIKey' has been provided. If so, planyoMobilePrefix and planyoMobileNumber properties are mandatory.
			- Observations:			The delivered message gets trimmed in certain phones and NEXMO alters the message by adding certain special characters, etc.
			- Reference:            Scenario Guide Document -> Chapter 3.2 -> Reservation Handling
			
 03. Invoicing
		[i] Case 001
			- Scenario Name: 		Invoicing
			- Description: 			Retrieves Invoiced Items for a given Reservation from Planyo and create Invoices for the Items in FreshBooks. Then, updates the Reservations with created Invoices' Number.						
			- Prerequisite:			Reservations should be added in Planyo as an offline process.
			- Note:					The Scenario retrieves the Users of the Reservations and create the Invoices for them in FreshBooks. If a particular Client(with the retrieved Planyo User email) does not exist in FreshBooks, a new Client will be created before the Invoice creation.
									Tax Details are not incorporated in the scenario.									
			- Assumptions:			Client email is unique in the FreshBooks account. Hence only one client should be available for a given client email.
			- Observations:			FreshBooks connector's createInvoice method randomly fails at times. This was observed in Recurly scenario implementations (Refer the note in https://docs.wso2.com/display/CONNECTORS/Subscription+Payments+in+Recurly). 
									Try re-running the failed request to get a successful response. 
			- Reference: 			Scenario Guide Document -> Chapter 3.3 -> Invoicing