Product: SalesBinder Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0-SNAPSHOT with patches.
 
 - Upload the following connectors to the ESB.
 
			-> salesbinder-connector-1.0.0
			-> clevertimcrm-connector-1.0.0
			-> shopify-connector-1.0.0
			-> zohobooks-connector-1.0.0
			
 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.
			
			SalesBinder - https://docs.wso2.com/display/CONNECTORS/SalesBinder+Connector
			CleverTimCRM - https://docs.wso2.com/display/CONNECTORS/Clevertim+CRM+Connector
			Shopify - https://docs.wso2.com/display/CONNECTORS/Shopify+Connector
			ZohoBooks - https://docs.wso2.com/display/CONNECTORS/Zoho+Books+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<SALESBINDER_CONNECTOR_HOME>/salesbinder-integrated-scenarios/src/common ), to the ESB that are listed as below.
			- sequences - faultHandlerSeq.xml
			- templates - responseHandlerTemplate.xml
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			

 01. Customer Creation
	[i] Case -001
		- Purpose : (a) Retrieve selected set of companies from ClevertimCRM and create new accounts(Customers, Prospects and Suppliers) in SalesBinder or create accounts directly in SalesBinder.
		
		- Files:	(a) Proxy - <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Customer Creation\Case-001\proxy\salesBinder_createCustomers.xml
					(b) Template - <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Customer Creation\Case-001\templates\salesbinder-createCustomers.xml
					(c) Sequence - <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Customer Creation\Case-001\sequences\createAccountIterator.xml
		
		- Request Parameters:	(a) cleverTimCrmCompanyIds - An array containing a list of objects with the clevertimCRM company ID and the flag to indicate whether the address is same for both billing and shipping.  				
								(b) salesbinderCustomers - An array of objects with all the parameters, which are required to create customers directly in SalesBinder.
		
 02. Item Selling
	[i] Case -001
		- Purpose : (a) Retrieve selected inventory items from SalesBinder and create products in Shopify.
		
		- Files:	(a) Proxy - <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Item Selling\Case-001\proxy\SalesBinder_createProductsByRetrievingInventoryItems.xml
		
		- Request Parameters:	(a) salesbinderInventoryItemIds - An array containing a list of objects with the SalesBinder inventory item ID and a boolean key named, "isPublish" to create and publish product in Shopify.
		
	[ii] Case -002
		- Purpose : (a)	Retrieve un-shipped order details from Shopify API and create purchase orders in SalesBinder and ZohoBooks.
		
		- Files:	(a) Proxy - <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Item Selling\Case-002\proxy\SalesBinder_createPurchaseOrdersByRetrievingUnshippedOrderDetails.xml
					(b) Sequences - <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Item Selling\Case-002\sequences\createContactSeq.xml
									<SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Item Selling\Case-002\sequences\createOrderLineItemsSeq.xml
									<SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Item Selling\Case-002\sequences\createPurchaseOrderSeq.xml
		
		- Request Parameters:	(a) zohobooksPurchaseAccountId - ZohoBooks ID of the purchase account to create item(s).
								(b) unshippedOrderDetails - An object containing Shopify order ID (orderId) and orderContactDetails object to contain Shopify order ID related SalesBinder Customer ID (salesBinderCustomerId) and ZohoBooks contact ID(zohoBooksContactId) which created with at least one contact person. 
								(c) lineItemData - An object containing, item objects with the key of the Shopify item ID which contains related SalesBinder item ID (salesBinderItemId) and ZohoBooks ItemID (zohoBooksItemId).
		
	[iii] Case -003
		- Purpose : (a) Retrieve selected set of purchase orders (Dropped Shipped to Customers) from SalesBinder and create fulfillments for those in Shopify.
		
		- Files:	(a) Proxy - <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Item Selling\Case-003\proxy\freeAgent_manageTimeSlipsAndInvoices.xml
			
		- Request Parameters:	(a) salesbinderPurchaseOrderIds - An array of SalesBinder purchase order IDs.  				
								(b) orderMap - List of key-value pairs for which SalesBinder purchase order ID is mapped with the Shopify order ID.
 03. Manage Accounts
	[i] Case -001
		- Purpose : (a) Create a document (Invoice, Estimate and Purchase Order) in SalesBinder and create the same invoice, estimate or purchase order in ZohoBooks.
		
		- Files:	(a) Proxy - <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Manage Accounts\Case-001\proxy\salesbinder_manageAccounts.xml
					(b) Sequence - <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Manage Accounts\Case-001\sequences\zohobooks-createCustomer.xml
								   <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Manage Accounts\Case-001\sequences\salesbinder-createDocuments.xml
								   <SALESBINDER_CONNECTOR_HOME>\salesbinder-integrated-scenarios\src\scenarios\Manage Accounts\Case-001\sequences\zohobooks-createDocuments.xml
		
		- Request Parameters:	(a) salesbinderContextId - The context ID of the document (e.g Invoice : 5, Estimate : 4, Purchase Order : 11).  				
								(b) salesBinderCustomerId - The ID of the customer for whom the document is created.
								(c) salesBinderDropShipCustomerId - The drop ship ID of the customer.
								(d) salesBinderShippingAddress- Shipping address of the document.
								(e) zohobooksCustomerId - The ID of the customer in ZohoBooks.
								(f) issueDate- Issuing date of the document.
								(g) documentItems - An array containing item objects. 
										Note : 1. Parameters named cost and price are mandatory for each document item object in the array.
											   2. Parameter named zohobooksPurchaseAccountId is mandatory if the document item object is to create a purchase order.
 