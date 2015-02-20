Product: ZohoCRM Business Scenarios

Environment Setup:

 - Download and initialize ESB 4.8.1 .
 
 - Upload the following connectors to the ESB.
 
			-> zohocrm-connector-1.0.0
			-> campaignmonitor-connector-1.0.0
			-> facebook-connector-1.0.0	
			-> quickbooks-connector-2.0.0
			-> callrail-connector-1.0.0
			-> jira-connector-1.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            ZohoCRM - https://docs.wso2.com/display/CONNECTORS/ZohoCRM+Connector
			CampaignMonitor - https://docs.wso2.com/display/CONNECTORS/Campaign+Monitor+Connector
			Facebook - https://docs.wso2.com/display/CONNECTORS/Facebook+Connector
			Quickbooks - https://docs.wso2.com/display/CONNECTORS/QuickBooks+Connector
			CallRail - https://docs.wso2.com/display/CONNECTORS/CallRail+Connector
			Jira - https://docs.wso2.com/display/CONNECTORS/JIRA+Connector	
			
 - Add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<ZohoCRM_Connector_Home>/zohocrm-integrated-scenarios/src/common ), to the ESB.  
 
 - Each scenario folder is consisted of sub-folders named as <Case-xxx>. In order to execute a particular case, upload the corresponding proxy and the template files which reside in the sub-folder, into the ESB.
		  
Scenarios in Brief:   

  - Initiate Marketing

	Case-001 -> 
			Scenario Name: Marketing ZohoCRM Campaign.
			Description: Creating a campaign in ZohoCRM and publishing in facebook and CampaignMonitor.
			Reference: Scenario Guide Document -> Chapter 3.1 -> Step 1 -> Sub Category '1a'
			
	Case-002 -> 
			Scenario Name: Lead Creation
			Description: Obtaining campaign clicker details from campaign monitor API and creating leads accordingly in ZohoCRM.
						 Directly creating leads in ZohoCRM.	
			Pre requisite: There has to be at least one person who has clicked the campaign email, sent via campaign monitor.
			Reference: Scenario Guide Document -> Chapter 3.1 -> Step 1 -> Sub Category '1b' 
			
	Case-003 -> 
			Scenario Name: Converting ZohoCRM Lead to a QuickBooks customer.
			Description: Converting an existing Lead in Zoho CRM, then retrieves the created contact details and creates a QuickBooks Customer.
			Pre requisite: There has to be at least one Lead created in ZohoCRM.
			Reference: Scenario Guide Document -> Chapter 3.1 -> Step 2

  - Process Sales			
  
	Case-001 ->
			Scenario Name: Publishing ZohoCRM quote in Quickbooks as an estimate.
			Description: Creates a quote in ZohoCRM and retrieves the details.
						 Creates an estimate in Quickbooks using the details, derived by ZohoCRM.
			Reference: Scenario Guide Document -> Chapter 3.2 -> Step 3
			Special Notes: The corresponding request contains an additional parameter called "productItemMap". This parameter refers to the "id" mapping between the products in ZohoCRM and items in Quickbooks.
			               The parameter value takes the form of {<zohocrm-productId>:<quickbooks-itemId>}
	
	Case-002 ->
			Scenario Name: Publishing ZohoCRM Sales Order in Quickbooks as an Sales Receipt.
			Description: Reads Quote in ZohoCRM, Creates a sales order in ZohoCRM and retrieves the details.
						 If Quote is not Confirmed update Potential in ZohoCRM	and Estimate in Quickbooks.
			Pre requisite: There has to be at least one Quote,Potential created in ZohoCRM and Estimate,Customer in Quickbooks.	
			Reference: Scenario Guide Document -> Chapter 3.2 -> Step 4
			Special Notes: The corresponding request contains an additional parameter called "productItemMap". This parameter refers to the "id" mapping between the products in ZohoCRM and items in Quickbooks.
			               The parameter value takes the form of {<zohocrm-productId>:<quickbooks-itemId>}
			
	Case-003 ->
			Scenario Name: Creating invoices in ZohoCRM and Quickbooks.
			Description: Creates an invoice in ZohoCRM.
						 Creates an invoice in Quickbooks by using the details retrieved from the newly created ZohoCRM invoice.
			Reference: Scenario Guide Document -> Chapter 3.2 -> Step 5 -> 1,2
			Special Notes: The corresponding request contains an additional parameter called "productItemMap". This parameter refers to the "id" mapping between the products in ZohoCRM and items in Quickbooks.
			               The parameter value takes the form of {<zohocrm-productId>:<quickbooks-itemId>} 
			
  - Manage Orders			
  
	Case-001 ->
			Scenario Name: Creating ZohoCRM vendors using the Quickbooks vendors.
			Description: Derives information about all the vendors in quickbooks.
						 Creates vendors in ZohoCRM, using the information derived from Quickbooks.
			Pre requisite: There has to be at least one vendor, created in Quickbooks.			 
			Reference: Scenario Guide Document -> Chapter 3.3 -> Step 6 -> 1  
			
	Case-002 ->
			Scenario Name: Creating ZohoCRM products using the Quickbooks items.
			Description: Derives information about all the items in quickbooks.
						 Creates products in ZohoCRM, using the information derived from Quickbooks.
			Pre requisite: There has to be at least one item, created in Quickbooks.			 			 
			Reference: Scenario Guide Document -> Chapter 3.3 -> Step 6 -> 2  
			
	Case-003 ->
			Scenario Name: Creating purchase orders in ZohoCRM and Quickbooks.
			Description: Creates purchase order in ZohoCRM.
						 Creates purchase order in Quickbooks by using the details retrieved from the newly created ZohoCRM purchase order.
			Pre requisite: All the Quickbooks items in productItemMap, must have ExpenseAccountRef field.
			Reference: Scenario Guide Document -> Chapter 3.3 -> Step 6 -> 3,4  		
			Special Notes: The corresponding request contains an additional parameter called "productItemMap". This parameter refers to the "id" mapping between the products in ZohoCRM and items in Quickbooks.
			               The parameter value takes the form of {<zohocrm-productId>:<quickbooks-itemId>} 			
			
  - Support		
						
	Case-001 -> 
			Scenario Name: Creating a Case in Zoho CRM and a corresponding Issue in Jira.
			Description: Creates a Case in Zoho CRM and a corresponding Issue in Jira for later follow up.
			Reference: Scenario Guide Document -> Chapter 3.4 -> Step 7
									
	Case-002 -> 
			Scenario Name: Follow up issues.
			Description: Comments on Jira issues are being inserted in to the Cases in Zoho CRM.
			Reference: Scenario Guide Document -> Chapter 3.4 -> Step 7