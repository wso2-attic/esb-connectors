Product: Printavo Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0-BETA-SNAPSHOT with patches.
 
 - Upload the following connectors to the ESB.
 
    -> sendloop-connector-1.0.0
    -> printavo-connector-1.0.0
    -> salesbinder-connector-1.0.0
    -> freshbooks-connector-1.0.0
    -> basecrm-connector-1.0.0

 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.

         Freshbooks  - https://docs.wso2.com/display/CONNECTORS/FreshBooks+Connector
         Printavo    - 
         Sendloop    - 
         Salesbinder - https://docs.wso2.com/display/CONNECTORS/SalesBinder+Connector
         BaseCRM     - https://docs.wso2.com/display/CONNECTORS/Base+CRM+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<PRINTAVO_CONNECTOR_HOME>/printavo-integrated-scenarios/src/common), to the ESB that are listed as below.
         - sequences -  faultHandlerSeq.xml
                        removeResponseHeaders.xml
  
         - templates -  responseHandlerTemplate.xml

 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			
 
 - Please make sure that the following are created/configured in your SalesBinder account before starting to execute the scenarios.
   1. Create an item category named 'Screen Printing'.
   2. Add custom fields to the created category as below :
         i.    'printavo_product'                  -  The product ID of the Printavo product will be stored here once it is created. (Product will be created only if this field is empty).    
         ii.   'campaign_creation (true or false)' -  This field should store 'true' or 'false'. ('true' : Campaign should be created for the item).    
         iii.  'campaign_name'                     -  This field should contain the name of the campaign to be created in Sendloop.  
         iv.   'campaign_link'                     -  This field should contain a html link which should be provided in the campaign. 
         v.    'campaign_subject'                  -  This field should contain the text to be used as the email campaign subject.
         vi.   'campaign_content'                  -  This field should contain a content which should be provided in the campaign. 
         vii.  'campaign_contentHeading'           -  This field should contain a heading which should be provided in the campaign content. 
         viii. 'campaign_listId'                   -  This field should contain a SendLoop subscriber list Id to which the campaign should be sent.
      
    Note : All the properties from (iii) to (viii) should be considered mandatory if the 'campaign_creation' is set to 'true'.
    
- Please make sure that the following are configured in your BaseCRM account before starting to execute the scenarios.
   1. Deal custom field named 'printavo_quote' (Text field with a limit of 25 characters - Not mandatory).
   2. Contact custom field named 'printavo_customer' (Text field with a limit of 25 characters - Not mandatory).
   
- Please make sure that the following are configured in your SendLoop account before starting to execute the scenarios.
   1. Make sure only to add one additional field as 'name' to the subscriber details. (It stores the name by splitting first name and last name with whitespace).
   
 
 01. Product Initiation
 
   [i] Case - 001
      - Purpose : (a)   (i)  Create products in Printavo by retrieving items in the category 'Screen Printing' from SalesBinder.
                        (ii) Create campaigns for the retrieved items if it is requried.
      - Files: (a)   Proxy - <PRINTAVO_CONNECTOR_HOME>\printavo-integrated-scenarios\src\scenarios\Product Initiation\Case-001\proxy\printavo_createProductsAndMarket.xml
               (b)   Sequences - <PRINTAVO_CONNECTOR_HOME>\printavo-integrated-scenarios\src\scenarios\Product Initiation\Case-001\sequences\sendloop-createAndSendCampaignSeq.xml
      
      - Request Parameters:   (a) sendloop.fromEmail     -  The email address of the sender of the campaign.
                              (b) sendloop.fromName      -  The name of the sender of the campaign.
                              (c) sendloop.replyToName   -  The name which appears for the recievers when they are going to reply.
                              (d) sendloop.replyToEmail  -  The email address which the recievers should reply if they want to.

 02. Contact Management
 
   [i] Case - 001
      - Purpose : (a)   (i)   Retrieve clickers of the campaign in SendLoop and add them to a new list. A new list is created for each campaign if it is not already created.
                        (ii)  Create those clickers as Leads in BaseCRM.

      - Files :   (a)   Proxy - <PRINTAVO_CONNECTOR_HOME>\printavo-integrated-scenarios\src\scenarios\Contact Management\Case-001\proxy\printavo_retrieveClickersAndCreateContacts.xml
                        Sequence - <PRINTAVO_CONNECTOR_HOME>\printavo-integrated-scenarios\src\scenarios\Contact Management\Case-001\sequences\basecrm-createLeadsSeq.xml

      - Request Parameters:   (a) sendloop.campaignId - ID of the campaign in Sendloop to which the clickers will be retrieved.

   [iii] Case - 002
      - Purpose : (a)   (i)   Retrieve won deals in BaseCRM and create draft quotes for them in Printavo. A new customer will be created if the customer related to the deal is not already existing in Printavo.

      - Files :   (a)   Proxy -  <PRINTAVO_CONNECTOR_HOME>\printavo-integrated-scenarios\src\scenarios\Contact Management\Case-002\proxy\printavo_retrieveWonDealsAndCreateQuotes.xml
                        Sequence -  <PRINTAVO_CONNECTOR_HOME>\printavo-integrated-scenarios\src\scenarios\Contact Management\Case-001\sequences\basecrm-getUserIdSeq.xml
                                    <PRINTAVO_CONNECTOR_HOME>\printavo-integrated-scenarios\src\scenarios\Contact Management\Case-001\sequences\basecrm-getOrderIdSeq.xml

      - Request Parameters:   (a) basecrm.wonStageId - ID of the 'won' stage of the deals in BaseCRM.
      
      - Special Notes:  (a)   A quote and a user should be already created in Printavo before executing the case.
                        (b)   For the successful execution of the case, there should be atleast one won deal in BaseCRM.

 03. Invoice Handling
 
   [i] Case - 001
      - Purpose : (a)   (i)   Retrieve invoices from Printavo which are in 'Completed' status and create them in Freshbooks.
                        (ii)  If the customer of the invoice is not already in Freshbooks, then create the customer as client.
                        (iii) Delete the invoices in Printavo upon successful creation of them in Freshbooks.

      - Files :   (a)   Proxy - <PRINTAVO_CONNECTOR_HOME>\printavo-integrated-scenarios\src\scenarios\Invoice Handling\Case 001\proxy\printavo_retrieveCompletedOrdersAndCreateInvoices.xml

      - Request Parameters:   All parameters are connector specific and self-explanatory.
                        
      - Special Notes: Invoices/Quotes in Printavo should be in 'Completed' status to be considered in the scenario.

   [iii] Case - 002
      - Purpose : (a)   (i)   Retrieve expenses from Printavo and create them in Freshbooks.
                        (ii)  Delete the expenses in Printavo upon successful creation of them in Freshbooks.

      - Files :   (a)   Proxy -  <PRINTAVO_CONNECTOR_HOME>\printavo-integrated-scenarios\src\scenarios\Invoice Handling\Case 002\proxy\printavo_recordExpenses.xml

      - Request Parameters:   (a) freshbooks.staffId - ID of the staff in Freshbooks to whom the expense would be associated. Provide a valid staff ID.
      
                              All other parameters are connector specific and self-explanatory.
                        
      - Special Notes: A category for expenses called 'Printavo Expenses' should be created in Freshbooks account.
 