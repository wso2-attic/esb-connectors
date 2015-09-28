Product: Integration tests for WSO2 ESB Printavo connector

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7
 
STEPS:

 1. Download ESB 4.9.0-BETA-SNAPSHOT by navigating to the following URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
    If required add the X.509 certificate from https://www.printavo.com to the client-truststore.jks of the ESB located in <ESB_HOME>/repository/resources/security folder
    and wso2carbon.jks located in <PRINTAVO_CONNECTOR_HOME>/printavo-connector/printavo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products.

 3. Create a Printavo trial account and derive the API Key and Secret.
   i)    Using the URL "https://www.printavo.com/" create a Printavo trial account.
   ii)   Login to the created Printavo account and go to https://www.printavo.com/users/edit and obtain the API key.

 4. Update the Printavo properties file at location "<PRINTAVO_CONNECTOR_HOME>/printavo-connector/printavo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)        apiUrl                                      - Use https://www.printavo.com.
   ii)       apiKey                                      - Use the API Key obtained under Step 3 ii).
   iii)      customerFirstName                           - First name of the customer for createCustomer mandatory case. Use a valid string value.
   iv)       optionalCustomerFirstName                   - First name of the customer for createCustomer optional case. Use a valid string value.
   v)        optionalCustomerLastName                    - Last name of the customer for createCustomer optional case. Use a valid string value.
   vi)       optionalCustomerCompany                     - Company of the customer for createCustomer optional case. Use a valid string value.
   vii)      optionalCustomerEmail                       - Email of the customer for createCustomer optional case. Use any email address which is correctly formatted.
   viii)     optionalCustomerPhone                       - Phone number of the customer for createCustomer optional case. Use a 10-digit phone number.
   ix)       perPage                                     - No of entries to return in a page. Use 1 or 2.
   x)        page                                        - No of the page to return. Use 1.
   xi)       customerFax                                 - Fax of the customer for updateCustomer optional case. Use a valid fax number.
   xii)      customerTaxExempt                           - Tax exempt status of the customer for updateCustomer optional case. Use a boolean value.
   xiii)     customerTaxResaleNum                        - Tax resale number of the customer for updateCustomer optional case. Use a valid integer value.
   xiv)      customerEmailUpdated                        - Updated email of the customer for updateCustomer optional case. Use any email address which is correctly formatted other than the value given in vii).
   xv)       customerPhoneUpdated                        - Updated phone of the customer for updateCustomer optional case. Use a valid phone number other than the value given in viii).
   xvi)      orderUserId                                 - ID of the user to whom Use a valid user ID.
   xvii)     orderStatusId                               - Use a valid status ID of an order.
   xiii)     orderFormattedDueDate                       - Use valid date formatted as follows 07/30/2015.
   xix)      orderSalesTax                               - Use a valid integer value as the tax value.
   xx)       orderDiscountAsPercentage                   - Use a boolean value to indecate the discount value mus use as persentage or not.
   xxi)      orderDiscount                               - Use a valid integer value to indecate the discount.
   xxii)     orderProductionNotes                        - Use a valid string value as a note of the product.
   xxiii)    orderNickname                               - Use a valid string value as an optional name for the order.
   xxiv)     optionalOrderSalesTaxUpdated                - Sales tax of the order for updateOrder optional case. Use a valid integer value other than the value given in xix).
   xxv)      optionalOrderDiscountAsPercentageUpdated    - Whether discount value should take as persentage of the order for updateOrder optional case. Use the opposite boolean value given in xx).
   xxvi)     optionalOrderDiscountUpdated                - Discount of the order for updateOrder optional case. Use a valid integer value other than the value given in xxi).
   xxvii)    optionalOrderProductionNotesUpdated         - Production notes of the order for updateOrder optional case. Use a valid String value other than the value given in xxii).
   xxviii)   optionalOrderNicknameUpdated                - Nickname for the order for updateOrder optional case. Use a valid String value other than the value given in xxiii).
   xxix)     productPricePer                             - Use a valid integer value as the unit price of a product.
   xxx)      styleNumber                                 - Use a valid String value as the style number of a product.
   xxxi)     brand                                       - Use a valid String value to indecate the brand of the product.
   xxxii)    size                                        - Use a valid String value indecate the size of the product.
   xxxiii)   productPricePerUpdated                      - Unit price of the product for updateProduct optional case. Use a valid integer value other than the value given in xxix).
   xxxiv)    productStyleNumberUpdated                   - Style number of the product for updateProduct optional case. Use a valid String value other than the value given in xxx).
   xxxv)     productBrandUpdated                         - Brand of the product for updateProduct optional case. Use a valid String value other than the value given in xxxi).
   xxxvi)    productSizeUpdated                          - Unit price of the product for updateProduct optional case. Use a valid String value other than the value given in xxxii).
   xxxvii)   paymentTransactionDate                      - Transaction date for payment used in create test case.
   xxxviii)  paymentName                                 - Name for payment used in create test case.
   xxxix)    paymentAmount                               - Amount for payment used in create test case.
   xL)       expenseTransactionDate                      - Transaction date for expense used in create test case.
   xLi)      expenseName                                 - Name for expense used in create test case.
   xLii)     expenseAmount                               - Amount for expense used in create test case.
   xLiii)    expenseNameUpdated                          - Updated name for expense.
   xLiv)     expenseAmountUpdated                        - Updated amount for expense.
   xLv)      expenseTransactionDateUpdated               - Updated transaction date for expense please follow the following format given in the example. (e.g: 2015-08-13T12:00:00).
   xLvi)     timeout                                     - Use 60000 to avoid the api call limit. 

 5. Make sure that printavo is specified as a module in ESB Connector Parent pom.
        <module>printavo/printavo-connector/printavo-connector-1.0.0/org.wso2.carbon.connector</module> 

 6. Navigate to "{ESB_Connector_Home}/" and run the following command.
         $ mvn clean install