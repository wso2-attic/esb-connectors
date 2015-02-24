Product: Recurly Business Scenarios

Environment Setup:

 - Download and initialize ESB 4.8.1 .
   Deploy relevant patches, if applicable and the ESB should be configured as below.
   Please make sure that the below mentioned Axis configurations are enabled in "<ESB_HOME>/repository/conf/axis2/axis2.xml".
      <messageFormatter contentType="text/html"
                        class="org.apache.synapse.commons.json.JsonStreamFormatter"/>						  
      <messageBuilder contentType="text/html"
                        class="org.apache.synapse.commons.json.JsonStreamBuilder"/>	

 - Upload the following connectors to the ESB.
         -> recurly-connector-1.0.0
         -> basecrm-connector-2.0.0
         -> activecampaign-connector-1.0.0
         -> gmail-connector-1.0.0
         -> freshbooks-connector-1.0.0

 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.

         Freshbooks - https://docs.wso2.com/display/CONNECTORS/Freshbooks+Connector
         ActiveCampaign - https://docs.wso2.com/display/CONNECTORS/ActiveCampaign+Connector
         Recurly - https://docs.wso2.com/display/CONNECTORS/Recurly+Connector
         BaseCRM - https://docs.wso2.com/display/CONNECTORS/Base+CRM+Connector
         Gmail - https://docs.wso2.com/display/CONNECTORS/Gmail+Connector

 - Add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<recurly-connector_Home>/recurly-integrated-scenarios/src/common ), to the ESB.  
 
 - Each scenario folder is consisted of sub-folders named as <Case-xxx>. In order to execute a particular case, upload the corresponding proxy and the template files which reside in sub-folders, into the ESB.

Scenarios in Brief:   

- Initiate Subscription
  
   Case-001 ->
         Scenario Name: Initiate Subscription
         Description:   Create a subscription plan in the Recurly API, create the email message to be used for the campaign in the ActiveCampaign API.
         Pre Requisite: Contacts in ActiveCampaign should have full name and Email specified. 
         Reference:     Scenario Guide Document -> Chapter 3.1 -> Initiate Subscription

   Case-002 ->
         Scenario Name: Initiate Subscription
         Description:   Retrieve campaign clickers from ActiveCampaign API and create leads in the Base CRM API.
         Reference:     Scenario Guide Document -> Chapter 3.1 -> Initiate Subscription

   Case-003 ->
         Scenario Name: Initiate Subscription
         Description:   Retrieve contacts from the BaseCRM API and create account Recurly. And create subscription for that accounts.
         Pre Requisite:	Contacts in BaseCRM should have first name, last name, Email, Billing Information and visa card number recorded in description field like 
                           "Card Number :4111-1111-1111-1111". Accounts that are already created on Recurly must have billing info. If phone number is needed it 
			   specified in work field under the phone section.  
         Note:		In the request Recurly account codes of existing accounts and Base CRM contact Ids which is used to create account in Recurly should be 
			    added in to the recurlyAccounts parameter.
	 Reference:     Scenario Guide Document -> Chapter 3.1 -> Initiate Subscription

- Subscription Payments

   Case-001 -> 
         Scenario Name: Subscription Payments
         Description:   Retrieve ‘Collected’ Invoices from the Recurly API on a daily basis and add them as payments in Freshbooks.
         Pre Requisite: The values for fields First Name, Last Name and Email are mandatory parameters in subscribing to a plan.
         Note:          In retrieving line items from Recurly for an invoice, 
                           The adjustment in the automatically created invoice which has a none zero total is considered as the line item.
                           This adjustment contains all the details including unit cost, quantity, tax and the discount.
         Reference: 	   Scenario Guide Document -> Chapter 3.2 -> Subscription Payments

- Notifications and Reminders
  
   Case-001 ->
         Scenario Name: Send Notifications Through Gmail
         Description:   Create coupon for a plan in Recurly and send notifications about the coupon to the subscribers of that plan through Gmail.
         Reference:     Scenario Guide Document -> Chapter 3.3 -> Notifications and Reminders

   Case-002 ->
         Scenario Name: Notifications and Reminders
         Description:   Retrieve subscriptions which are in a trial period. And trial ends within 3 days for which reminders will be sent through Gmail.
         Pre Requisite: Should have subscriptions which trial period ends within 3 days.
         Reference:     Scenario Guide Document -> Chapter 3.3 -> Notifications and Reminders
