Product: Vend Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0 with patches.
 
 - Upload the following connectors to the ESB.
 
    -> vend-connector-1.0.0
    -> beetrack-connector-1.0.0
    -> formstack-connector-1.0.0
    -> billomat-connector-1.0.0
    -> mailchimp-connector-1.0.0
    -> mandrill-connector-1.0.0
    -> nexmo-connector-1.0.0
    -> shopify-connector-1.0.0

 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.

         Vend  - 
         Beetrack    - 
         Formstack   - https://docs.wso2.com/display/CONNECTORS/Formstack+Connector
         Billomat    - https://docs.wso2.com/display/CONNECTORS/SalesBinder+Connector
         Mailchimp   - https://docs.wso2.com/display/CONNECTORS/Billomat+Connector
         Mandrill    - https://docs.wso2.com/display/CONNECTORS/Mailchimp+Connector
         Shopify     - https://docs.wso2.com/display/CONNECTORS/Shopify+Connector
         Nexmo       - https://docs.wso2.com/display/CONNECTORS/Nexmo+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<VEND_CONNECTOR_HOME>/printavo-integrated-scenarios/src/common), to the ESB that are listed as below.
         - sequences -  faultHandlerSeq.xml
                        removeResponseHeaders.xml
                        shopify-removeResponseHeaders.xml
  
         - template  -  responseHandlerTemplate.xml
         
-  Please note that the cases of Product Marketing, Product Ordering and Sales Fulfillment scenarios should be executed in the same order as they appear in this document due to dependencies between them.
   However cases of Inventory Management, Customer Loyality Management and Product Campaigns scenarios can be executed independently.

-  Prerequisites for the Scenario.
      (a)   Add the following registry entries with valid values to the ESB (on which the scenarios would be executed).
            - /_system/governance/connectors/Vend/apiUrl
            - /_system/governance/connectors/Vend/clientId
            - /_system/governance/connectors/Vend/clientSecret
            - /_system/governance/connectors/Vend/redirectUrl
            - /_system/governance/connectors/Vend/accessToken
            - /_system/governance/connectors/Vend/refreshToken
            
            - /_system/governance/connectors/Beetrack/apiUrl
            - /_system/governance/connectors/Beetrack/accessToken
            
            - /_system/governance/connectors/Mailchimp/apiUrl
            - /_system/governance/connectors/Mailchimp/apiKey
            
            - /_system/governance/connectors/Mandrill/apiUrl
            - /_system/governance/connectors/Mandrill/apiKey
            
            - /_system/governance/connectors/Billomat/apiUrl
            - /_system/governance/connectors/Billomat/apiKey
            
            - /_system/governance/connectors/Nexmo/apiKey
            - /_system/governance/connectors/Nexmo/apiSecret
            - /_system/governance/connectors/Nexmo/apiUrl
            
            - /_system/governance/connectors/Shopify/apiUrl
            - /_system/governance/connectors/Shopify/accessToken
            
            - /_system/governance/connectors/Formstack/apiUrl
            - /_system/governance/connectors/Formstack/accessToken
   
 
 01. Product Marketing
 
   [i] Case - 001
      - Purpose:  (a)   Retrieve products from Vend and create them in Shopify (if the product is marked for e-marketing).
                  (b)   Create and send marketing campaigns to promote the product created in (a) via Mailchimp.
                  (c)   Send SMS promotions to preferred customers about the products in Vend (if the product is marked for preferential marketing).
                        
      - Files: (a)   Proxy - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Product Marketing\Case 001\proxy\vend_createProductsAndMarket.xml
               (b)   Sequences - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Product Marketing\Case 001\sequences\vend-createProductAndMarket.xml
                                 <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Product Marketing\Case 001\sequences\vend-doPreferentialMarketing.xml
      
      - Request Parameters:   (a) vend.considerSince  -  (Optional) Products which are created/modified only after this date-time is considered for the case. If the parameter is not provided, it'll be defaulted to 00:00:00 (12:00 am) of the day of execution.
                              (b) vend.priceBookName  -  (Optional) Name of the price book which contains discount information of the products if any.
                              (c) mailchimp.listName   -  Name of the subscribers' list in mailchimp to whose members the campaign email would be sent.
                              (d) mailchimp.templateId -  ID of the template in Mailchimp using which the campaigns will be created.
                              (e) mailchimp.fromName   -  Name of the company/person to be used as the sender name for the campaigns and promotions.
                              (f) mailchimp.fromEmail  -  Email of the company/person to be used as the sender email for the campaigns and promotions.
                              
      - Special Notes:  (a)   Products that need to be created in Shopify and marketed via Mailchimp should have a tag called 'eMarketing'. This is how the products are marked for e-marketing.
                        (b)   Products should have a tag called 'preferential-marketing' to be considered for preferential marketing. This is how the products are marked for preferential marketing.
                              They should also be accompanied by a separate tag in the form of '@<product-type>:<brand-type>' which identify their generic type and brand. E.g. @chocolate:cadbury.
                              (Only the customers who've bought products with the identical tag, within 30 days before the time of execution, are chosen for preferential marketing)
                        (c)   Unless otherwise specified all the request parameters described below are mandatory.

 02. Product Ordering
 
   [i] Case - 001
      - Purpose:  (a)   Retrieve paid orders from Shopify and create them as register sales in Vend.
                  (b)   Create the corresponding Shopify customer in Vend if he/she doesn't already exist.

      - Files :   (a)   Proxy - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Product Ordering\Case 001\proxy\vend_createPaidOrdersAsRegisterSales.xml

      - Request Parameters:   (a) shopify.considerSince  -  (Optional) Orders which are created/modified only after this date-time is considered for the scenario. If the parameter is not provided, it'll be defaulted to 00:00:00 (12:00 am) of the day of execution.
                              
      - Special Notes:  (a)   A register called 'Shopify Register' must be created in Vend before executing the case.

 03. Sales Fulfillment
 
   [i] Case - 001
      - Purpose:  (a)   Retrieve closed register sales from Vend (daily) and for each sale,
                        (i) If the sale is associated with a Shopify order, create fulfillment for it in Shopify.
                        (ii) Create a route for the sale in Beetrack (for delivery of associated goods).
                        (iii) Create a receipt (paid invoice) for the sale in Billomat. Create the corresponding customer in Billomat if he/she doesn't already exist.
                        (iv) Create a delivery note for the customer including the shipped goods and send it to the customer via mail (in Billomat).

      - Files :   (a)   Proxy - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Sales Fulfillment\Case 001\proxy\vend_createFulfillmentAndRouteForClosedSales.xml
                  (b)   Sequences - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Sales Fulfillment\Case 001\sequence\beetrack_createRoutes.xml
                                    <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Sales Fulfillment\Case 001\sequence\billomat_createCompletedInvoiceSeq.xml
                                    <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Sales Fulfillment\Case 001\sequence\vend_setItemArray.xml

      - Request Parameters:   (a) vend.companyName  -  Name of the company/person to be used as the sender name for the emails.
                              (b) beetrack.truckIdentifier  -  ID of the truck used to create the route in Beetrack.
      
      - Prerequisites:        (a) The following custom fields should be created in Beetrack under the category 'order'.
                                 - vend_saleId
                                 - vend_userName
                                 - vend_saleDate	
                        
      - Special Notes: Register sales should be 'CLOSED' status for them to be considered in the case.

   [ii] Case - 002
      - Purpose:  (a)   Create surveys for delivered orders (to capture feedback) and send them to the customer.

      - Files:    (a)   Proxy - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Sales Fulfillment\Case 002\proxy\vend_createAndSendSurveyForDeliveredSales.xml
                  (b)   Template - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Sales Fulfillment\Case 002\templates\formstack_updateFieldContent.xml

      - Request Parameters:   (a) formstack.templateFormId        -  ID of the form that would be used as a template to create forms for each customer.
                              (b) formstack.saleIdLabel           -  Label name of the field in which the Vend sales ID would be saved.
                              (c) formstack.userNameLabel         -  Label name of the field in which the user name of the user would be saved.
                              (d) formstack.productDetailsLabel   -  Label name of the field in which the sales product details would be saved.
                              (e) mandrill.fromName               -  Name of the company/person to be used as the sender name when sending the surveys.
                              (f) mandrill.fromEmail              -  Email of the company/person to be used as the sender email when sending the surveys.
                        
      - Special Notes: (a) Status of routes in Beetrack should be changed to 'DELIVERED' for those routes to be considered in the case.
      
      - Prerequisites:  (a) A form should be created in Formstack which is used as template ID to clone the form with following fields.
                           - Products Delivered (Long Answer field type - ReadOnly)
                           - Sale Id (Short Answer field type - Hidden)
                              Make sure to make this field one before the last out of all the fields.
                           - Vend User (Short Answer field type - Hidden)
                              Make sure to make this field the last out of all the fields.

                           * Include four or more multiple choice questions to get customer feedback  (Radio button type)

   [iii] Case - 003
      - Purpose:  (a)   Gather responses of surveys in Formstack and send them to the admin of Vend.

      - Files:    (a)   Proxy - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Sales Fulfillment\Case 003\proxy\vend_sendSurveyResponseToAgents.xml

      - Request Parameters:   (a) formstack.questions -  Array of objects each with the index of the question in the template form (in Formstack) and the label of the question.
                                                         Please note that the values given for questions will be included in the mail sent to the admin.
                                                   E.g.  [
                                                            {"index":4, "question":"How satisfied are you with the quality of the products that you have purchased?"},
                                                            {"index":5, "question":"How satisfied are you with speed and the quality of the delivery?"},
                                                            {"index":6, "question":"How satisfied are you with doing business with our company?"},
                                                            {"index":7, "question":"How likely are you to recommend our company to others?"}
                                                         ]
                                 
                              (b) mandrill.fromName   -  Name of the company/person to be used as the sender name when sending the survey response.
                              (c) mandrill.fromEmail  -  Email of the company/person to be used as the sender email when sending the survey response.
                        
      - Special Notes: (a) Surveys should be completed in order to be considered in the case.
                       (b) When multiple submissions are made for a single survey, the most recent submission would be considered.
                       (c) Surveys without submission which are older than 14 days will be deleted upon execution of the case.

 04. Inventory Management
 
   [i] Case - 001
      - Purpose:  (a)   Create consignment for products which are getting low on stock and notify the associated supplier by email.
                  (b)   Additionally if the stocks are critically low, notify the supplier via SMS.

      - Files:    (a)   Proxy - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Inventory Management\Case 001\vend_createConsignmentAndNotifySuppliers.xml

      - Request Parameters:   (a) mandrill.fromName   -  Name of the company/person to be used as the sender name when sending consignment request to the supplier.
                              (b) mandrill.fromEmail  -  Email of the company/person to be used as the sender email when sending consignment request to the supplier.
                        
      - Special Notes:  (a) Only products which are associated with a supplier are considered in the case.
                            To associate a supplier to a product, in addition to assigning the supplier for 'Supplier' attribute of the product when creating/editing it in Web UI, set the ID of the same supplier for the 'Supplier code' attribute of the product.
                        (b) Products which already have an active consignment created for them will not be considered in the case.

 05. Product Campaigns
 
   [i] Case - 001
      - Purpose:  (a)   Create and send campaigns for products which are being given discounts in Vend.

      - Files:    (a)   Proxy - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Product Campaigns\Case 001\proxy\vend_marketProductsWithDiscount.xml

      - Request Parameters:   (a) vend.priceBookName  -   Name of the price book which contains discount information of the products.
                              (b) mailchimp.listName   -  Name of the subscribers' list in mailchimp to whose members the campaign email would be sent.
                              (c) mailchimp.templateId -  ID of the template in Mailchimp using which the campaigns will be created.
                              (d) mailchimp.fromName   -  Name of the company/person to be used as the sender name for the campaigns.
                              (e) mailchimp.fromEmail  -  Email of the company/person to be used as the sender email for the campaigns.
                        
      - Special Notes:  (a) Products need to be added to a price book with the discount rate set. Only such products will be considered for the case.
      
 06. Customer Loyalty Management
 
   [i] Case - 001
      - Purpose:  (a)   Update Mailchimp subscribers' list with the customers in Vend.
                  (b)   Notify customers in Vend regarding changes in their loyalty balance via SMS.
                        
      - Files: (a)   Proxy - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Customer Loyalty Management\Case 001\proxy\vend_updateCustomerBaseAndNotifyLoyaltyBalance.xml
      
      - Request Parameters:   (a) vend.considerSince  - (Optional) Customers who are created/modified only after this date-time is considered for the case. If the parameter is not provided, it'll be defaulted to 7 days prior to the execution of the scenario.
                              (b) vend.fromName       - Name of the company/person to be used as the sender name for the SMS messages.
                              (c) mailchimp.listName  - Name of the subscribers' list in mailchimp to which the customers would be added/updated.
                              
      - Special Notes:  (a)   Loyalty feature should be enabled in the Vend account for the case to be meaningful.
                        (b)   Only the customers who have their loyalty enabled will be considered for loyalty balance updates.
 
   [ii] Case - 002
      - Purpose:  (a)   Discover premium customers based on sales (top three of them).
                  (b)   Create a gift pack for them to be sent as reward.
                  (c)   Create a route in Beetrack for the gift pack to be delivered to the customer.
                  (d)   Notify the customer about the reward via email and SMS.
                        
      - Files: (a)   Proxy - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Customer Loyalty Management\Case 002\proxy\vend_discoverPremiumCustomersAndReward.xml
               (b)   Template - <VEND_CONNECTOR_HOME>\vend-integrated-scenarios\src\scenarios\Customer Loyalty Management\Case 002\templates\vend-rewardCustomerWithGiftPack.xml
      
      - Request Parameters:   (a) vend.salesSince        - Only the sales taken place from the provided date-time would be considered when deciding the premium customers. If the parameter is not provided, it'll be defaulted to 30 days prior to the execution of the scenario.
                              (b) vend.fromName          - Name of the company/person to be used as the sender name for the emails and SMS messages.
                              (c) vend.fromEmail         - Email of the company/person to be used as the sender email for the notification emails.
                              (d) vend.minimumSalesAmount- Minimum amount of sales a customer should've done in order to be eligible for the reward.
                              (e) vend.registerId        - ID of the register using which the gift pack would be created in Vend.
                              (f) vend.firstCustomer, vend.secondCustomer, vend.thirdCustomer - Objects with the following properties:
                                 (i)   minimumCustomers  - Number of total customers (who have done sales within the considered duration) required to reward the respective position (1st, 2nd or 3rd).
                                 (ii)  products          - Array of product objects in Vend which are to be included in the gift pack for the customer in the respective position (1st, 2nd or 3rd). Each product object has the following properties.
                                          - id        - ID of the product in Vend (provide valid ID).
                                          - quantity  - Quantity of the product.
                              (g) beetrack.vehicleId     -  ID of the truck used to create the route in Beetrack for the gift pack.
                                          
                              
      - Special Notes:  (a)   A payment type called 'Gift Voucher' should be created in Vend account prior to executing the scenario.
 