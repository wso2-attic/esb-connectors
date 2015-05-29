Product: ChargeBee Business Scenarios

Environment Setup:

 - Download and initialize ESB 4.9.0 .
 
 - Upload the following connectors to the ESB.
 
            -> chargebee-connector-1.0.0
            -> mailchimp-connector-2.0.0
            -> zohocrm-connector-2.0.0	
            -> billiving-connector-1.0.0
            -> Gmail-connector-2.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            ChargeBee - https://docs.wso2.com/display/CONNECTORS/ChargeBee+Connector
            MailChimp - https://docs.wso2.com/display/CONNECTORS/MailChimp+Connector
            ZohoCRM - https://docs.wso2.com/display/CONNECTORS/ZohoCRM+Connector
            Billiving - https://docs.wso2.com/display/CONNECTORS/Billiving+Connector
            Gmail - https://docs.wso2.com/display/CONNECTORS/Gmail+Connector+Through+REST

 - Add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<CHARGEBEE_CONNECTOR_HOME>/chargebee-integrated-scenarios/src/common ), to the ESB.  
 
 - Each scenario folder is consisted of sub-folders named as <Case-xxx>. In order to execute a particular case, upload the corresponding proxy, sequence and the template files which reside in the sub-folder, into the ESB.

Scenarios in Brief:   

  - Marketing

    Case-001 -> 
            Scenario Name: Create plan and send campaign.
            Description:   Create a plan in Chargebee API and send plan subscription details through Mailchimp API.
            Pre requisite: A mail list with subscribers should be created in Mailchimp API and that Mailchimp list id should be passed as mailchimpListID in input. 
            Note:          Values that are passed in for chargebeeSetupCost, chargebeePrice should be passed as USD cents. Only USD currency type is supported since ChargeBee API doesn't allow currency handling
            Reference:     Scenario Guide Document -> Chapter 3.1

    Case-002 -> 
            Scenario Name: Create and send coupon.
            Description:   Create a coupon in Chargebee API and send the coupon details through Gmail for all customers in Chargebee.
            Note:          Value that is passed in for chargebeeDiscountAmount should be passed as USD cents. Only USD currency type is supported since ChargeBee API doesn't allow currency handling.
            Reference:     Scenario Guide Document -> Chapter 3.1
  
  - ContactManagement
  
    Case-001 ->
            Scenario Name: Manage contacts.
            Description:   Create leads and contacts in Zohocrm API for the customers that is subscribed to Chargebee on a specified time period and add them to the mail lists in Mailchimp according to their plan. 
            Pre requisite: Mail lists should be created in Mailchimp for every plan in Chargebee and named them using planIds of plans in Chargebee. 
            Note:          Leads are created for the customers who are subscribed for a free plan and contacts are created for the customers who are subscribed for a paid plan.
            Reference:     Scenario Guide Document -> Chapter 3.2
            
  - ManageSubscriptions
  
    Case-001 ->
            Scenario Name: Update subscription.
            Description:   Update the subscription to a paid plan from a free plan in Chargebee and convert the lead to a contact in Zohocrm for that customer and remove the subscription from free plans' mail list and add  to the new paid plans' mail list in Mailchimp.
            Pre requisite: There shoud be a mail list in Mailchimp named using planId of the paid plan.
            Note:          Value that is passed in for chargebeePlanId should be a paid plans' planId which the subscription is updated to.
            Reference:     Scenario Guide Document -> Chapter 3.3.
            
    Case-002 ->
            Scenario Name: Cancel subscription.
            Description:   Cancel the subscription in Chargebee and remove the subscription from the maillist in Mailchimp.
            Reference:     Scenario Guide Document -> Chapter 3.3

  - Invoicing
  
    Case-001 ->
            Scenario Name: Invoicing.
            Description:   Retrive the invoices from ChargeBee and add them to Billiving.
            Reference:     Scenario Guide Document -> Chapter 3.4
            Note:          Only USD currency type is supported since ChargeBee API doesn't allow currency handling.
            Observation:   When mulitiple invoices are created two or more invoices with same id may be created. And if that happens only a one of the invoices will be marked as paid that has the same invoice id.

            