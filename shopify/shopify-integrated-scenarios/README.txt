Product: Shopify Business Scenarios

Environment Setup:

 - 	Download and initialize ESB 4.8.1 .
	Deploy relevant patches, if applicable and the ESB should be configured as below.
	Please make sure that the below mentioned Axis configurations are enabled in "<ESB_HOME>/repository/conf/axis2/axis2.xml".
		<messageFormatter contentType="text/javascript"
                        class="org.apache.synapse.commons.json.JsonStreamFormatter"/>						  
		<messageBuilder contentType="text/javascript"
                        class="org.apache.synapse.commons.json.JsonStreamBuilder"/>	
	
 
 - Upload the following connectors to the ESB.
 			-> mailchimp-connector-1.0.0
			-> facebook
			-> zohocrm-connector-2.0.0
 			-> shopify-connector-1.0.0
			-> shippo-connector-1.0.0
			-> callrail-connector-1.0.0
			
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
 			MailChimp - https://docs.wso2.com/display/CONNECTORS/MailChimp+Connector
 			Facebook - https://docs.wso2.com/display/CONNECTORS/Facebook+Connector
           	Shopify - https://docs.wso2.com/display/CONNECTORS/Shopify+Connector
			ZohoCRM - https://docs.wso2.com/display/CONNECTORS/ZohoCRM+Connector
			Shippo - https://docs.wso2.com/display/CONNECTORS/Shippo+Connector
			CallRail - https://docs.wso2.com/display/CONNECTORS/CallRail+Connector
			
 - Add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<shopify-connector_Home>/shopify-integrated-scenarios/src/common ), to the ESB.  
 
 - Each scenario folder is consisted of sub-folders named as <Case-xxx>. In order to execute a particular case, upload the corresponding proxy and the template files which reside in sub-folders, into the ESB.
		  
Scenarios in Brief:   

- ProductMarketing		
  
	Case-001 ->
			Scenario Name:	Product Marketing
			Description: 	Retrieve product details from Shopify API. Then create and send draft campaign in Mailchimp API and publish a page post on the company’s page in Facebook with instructions to viewers to comment on the post.
			Pre Requisite:	Create a custom template in MailChimp and provide it's Id as mcTemplateId parameter in request. 
							Please refer the "mailchimp_template.html" file located in "<shopify-connector_Home>/shopify-integrated-scenarios" folder to create the custom template.
							There should be atleast one product in Shopify. Provide product Id Array in shopifyProductIDArray parameter to market the product details in the scenario.
			Reference: 		Scenario Guide Document -> Chapter 3.1 -> Product Marketing

- Lead Generation & Customer Creation

	Case-001 -> 
			Scenario Name: 	Lead Generation
			Description:   	Retrieve details of the potential customers from Facebook, MailChimp and CallRail and create leads in ZohoCRM.
			Note: 		   	Potential customers are identified from each API according to following criteria.
								- Facebook:  People who commented on the post created in Product Marketing Scenario, according to the guideline mentioned in the post.
								- MailChimp: People who clicked on the Read More button of the emailed campaign.
								- CallRail:  People who called the Tracker number of the given CallRail Company.
							Provide crCompanyId parameter with a valid CallRail company Id. Caller details of the people who called the company's tracking number will be used to create Leads in ZohoCRM. 
							If the parameter is not specified, will create Leads using callers under all the companies of the account.
			Reference: 	   	Scenario Guide Document -> Chapter 3.2 -> Lead Creation
		
			
	Case-002 -> 
					
			Scenario Name:  Customer Creation
			Description: 	Retrieve contacts from the ZohoCRM API and create them as customers in the Shopify API. 
			Reference: 		Scenario Guide Document -> Chapter 3.2 -> Customer Creation
			 

- Shipping			
  
	Case-001 ->
			Scenario Name: 	Shipping
			Description: 	Retrieves unshipped orders from shopify and fulfill the orders, then create shipments in Shippo.						   
			Pre Requisite:	Users should create a default Parcel and a default Sender Address in Shippo and provide the two Ids in the request, Note that these Ids will be used to create all the shipments.							
			Reference: 		Scenario Guide Document -> Chapter 3.3 -> Shipping
	
	