Product: Integration tests for WSO2 ESB Quickbooks connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{QUICKBOOKS_CONNECTOR_HOME}/quickbooks-connector/quickbooks-connector-1.0.0/org.wso2.carbon.connector/repository/"

 2. This ESB should be configured as below;
	In Axis configurations (/repository/conf/axis2/axis2.xml).

    i) Enable message formatters for "application/json" and "text/html" in messageFormatters section
			
			<messageFormatter contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
			<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
			
   ii) Enable message builders for "application/json" and "text/html" in messageBuilders section
			
			<messageBuilder contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamBuilder"/>
			<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
			
 

 3. Create a Quickbooks account and obtain a accesstoken

	  i) Using the URL "http://global.intuit.com/row/small-business/financial-accounting-software.jsp" create a Quickbooks free trial account. Verify Your email address and access the created Quickbooks account.
	 ii) Login to the created account and using the URL "https://developer.intuit.com/Application/List" in "My Apps" create a Quickbooks Application in "Quickbooks API" section and Configure your Application under "DATA ACCESS PERMISSIONS" as "All Accounting" where you will be directed to a page to generate your access token.
    iii) Go to following URL "https://appcenter.intuit.com/Playground/OAuth" and give "App Token" which was received in step 3 - (ii) as the App Token value in "Step 1 - Dynamic Consumer Creation".
     iv) Get "Consumer Key","Consumer Secret","Access Token" and "Access Token Secret". 		
	 
 4. Update the quickbooks properties file at location "{QUICKBOOKS_CONNECTOR_HOME}/quickbooks-connector/quickbooks-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
 
      i) consumerKey - Use the consumerKey you got from step 3 iv).
	
	   ii) consumerSecret - Use the consumerSecret you got from step 3 iv).
	
    iii) accessToken - Use the accessToken you got from step 3 iv).
   
     iv) accessTokenSecret - Use the accessTokenSecret you got from step 3 iv).
   
      v) responseType - Use "application/json"
	
     vi) apiUrl - Use "https://qb.sbfinance.intuit.com".
	
    vii) companyId - Use companyId given to your company.
   
   viii) accountNameMandatory - Give a name for the account.
   
     ix) accountNameOptional - Give a name for the account.
	
	  x) customerNameMandatory - Give a name for the customer.
	
     xi) customerNameOptional - Give a name for the customer.
   
    xii) vendorDisplayName1 - Give a name for the vendor.
   
   xiii) vendorDisplayName2 - Give a name for the vendor.
	
	xiv) itemNameMandatory - Give a name for the item.
	
	 xv) itemNameOptional - Give a name for the item.
	 
	xvi) updatedMandatoryItemName  - Give a string to update item name.
	
   xvii) updatedOptionalItemName  - Give a string to update item name.

  xviii) updatedMandatoryVendorTitle  - Give a string to update vendor title.

	xix) updatedOptionalVendorTitle  - Give a string to update vendor title.	
	
	 xx) customerTitle - Give a string to update customer title. Maximum width should be below 15 characters. 

    xxi)  customerDisplayName - Give a string to update customer display name.
	
   xxii) docNumber - 500
   
  xxiii) estimateLineId - 1
   
   xxiv) estimateLineNum - 1

    xxv) billPaymentTxn1Amount - 10.00

   xxvi) billPaymentTxn2Amount - 10.00

  xxvii) billPaymentTotalAmt - 20.00

 xxviii) txnDate - Date after you create the inventory.
   
   xxix) inventoryStartDate - Provide any valid date in the format of YYYY-MM-DD (e.g :- 2014-04-01)
   
    xxx) acceptedDate - Provide any valid date in the format of YYYY-MM-DD (e.g :- 2014-04-01)
   
   xxxi) expirationDate - Provide any valid date in the format of YYYY-MM-DD (e.g :- 2014-04-01)
   
  xxxii) shipDate - Provide any valid date in the format of YYYY-MM-DD (e.g :- 2014-04-01)
   
 xxxiii) serviceDate - Provide any valid date in the format of YYYY-MM-DD (e.g :- 2014-04-01)

  xxxiv) currencyRef - Provide the currency of the specified country that has been used to create QuickBooks account in step 3.
  
   xxxv) updateInvoiceLineAmount - 132.0

  xxxvi) updateInvoiceDocNumber - 144

 xxxvii) updatedItemDescription - A string value to update item description.

xxxviii) updatedVendorGivenName - A String value to update vendor given name.

  xxxix) updateSalesReceiptDescription - A string value tp update sales receipt description.
   
     xl) amount - 123.0 (Strickly follow the given format)
	 
   Repeat viii) to xxi) before each run. Make sure that the names given for each of those parameters are different from each other. 
    
 5. Navigate to "{QUICKBOOKS_CONNECTOR_HOME}/quickbooks-connector/quickbooks-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

 NOTE : Following Quickbooks account, can be used to run the integration tests.
    Username : wso2connector.abdera@gmail.com
    Password : connector@1234
