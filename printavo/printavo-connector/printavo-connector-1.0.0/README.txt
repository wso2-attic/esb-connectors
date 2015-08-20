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

 2.	Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
	If required add the X.509 certificate from https://www.printavo.com to the client-truststore.jks of the ESB located in <ESB_HOME>/repository/resources/security folder
	and wso2carbon.jks located in <PRINTAVO_CONNECTOR_HOME>/printavo-connector/printavo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products.

 3. Create a Printavo trial account and derive the API Token and Secret.
   i)    Using the URL "https://www.printavo.com/" create a Printavo trial account.
   ii)   Login to the created Printavo account and go to https://www.printavo.com/users/edit and obtain the API key.

 4. Update the Printavo properties file at location "<PRINTAVO_CONNECTOR_HOME>/printavo-connector/printavo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)        apiUrl                 					- Use https://www.printavo.com.
   ii)       apiToken               					- Use the API Token obtained under Step 3 ii).
   iii)      customerFirstName              			- Use a valid string value.
   iv)       optionalCustomerFirstName                  - Use a valid string value.
   v)        optionalCustomerLastName       			- Use a valid string value.
   vi)       optionalCustomerCompany               		- Use a valid string value.
   vii)      optionalCustomerEmail                  	- Use any email address which is correctly formatted.
   viii)     optionalCustomerPhone            			- Use a valid phone number.
   ix)       PerPage                 					- Give an integer (prifer 1 or 2).
   x)        Page          								- Give an integer (prifer 1 or 2).
   xi)       customerFax           						- Use a valid fax number.
   xii)      customerTaxExempt             				- Use a boolean value.
   xiii)     customerTaxResaleNum            			- Use a valid integer value.
   xiv)      customerEmailUpdated                   	- Use any email address which is correctly formatted other than the value given in vii).
   xv)       customerPhoneUpdated                  		- Use a valid phone number other than the value given in viii).
   xvi)      orderUserId               					- Use a valid user ID.
   xvii)     orderStatusId         						- Use a valid status ID.
   xiii)     orderFormattedDueDate       				- Use valid date formatted as follows 07/30/2015.
   xix)      orderSalesTax    							- Use a valid integet value.
   xx)       orderDiscountAsPercentage   				- Use a boolean value.
   xxi)      orderDiscount       						- Use a valid integet value.
   xxii)     orderProductionNotes           			- Use a valid string value.
   xxiii)    orderNickname     							- Use a valid string value.
   xxiv)     optionalOrderSalesTaxUpdated         		- Use a valid integet value other than the value given in xix).
   xxv)      optionalOrderDiscountAsPercentageUpdated 	- Use the opposite boolean value given in xx).
   xxvi)     optionalOrderDiscountUpdated         		- Use a valid integet value other than the value given in xxi).
   xxvii)    optionalOrderProductionNotesUpdated        - Use a valid String value other than the value given in xxii).
   xxviii)   optionalOrderNicknameUpdated         		- Use a valid String value other than the value given in xxiii).
   xxix)     productPricePer         					- Use a valid integet value.
   xxx)      styleNumber         						- Use a valid String value.
   xxxi)     brand         								- Use a valid String value.
   xxxii)    size         								- Use a valid String value.
   xxxiii)   productPricePerUpdated         			- Use a valid integet value other than the value given in xxix).
   xxxiv)    productStyleNumberUpdated         			- Use a valid String value other than the value given in xxx).
   xxxv)     productBrandUpdated         				- Use a valid String value other than the value given in xxxi).
   xxxvi)    productSizeUpdated         				- Use a valid String value other than the value given in xxxii).
   xxxvii)   productIdOptional         					- Use a valid String value.
   xxxviii)  paymentTransactionDate         			- Use a valid String value.
   xxxix)    paymentName         						- Use a valid String value.
   xL)       paymentAmount         						- Use a valid String value.
   xLi)      expenseTransactionDate         			- Use a valid String value.
   xLii)     expenseName         						- Use a valid String value.
   xLiii)    expenseAmount         						- Use a valid String value.
   xLiv)     expenseNameUpdated         				- Use a valid String value.
   xLv)      expenseAmountUpdated         				- Use a valid String value.
   xLvi)     expenseTransactionDateUpdated         		- Use a valid String value.

 5. Make sure that printavo is specified as a module in ESB Connector Parent pom.
        <module>printavo/printavo-connector/printavo-connector-1.0.0/org.wso2.carbon.connector</module> 

 6. Navigate to "{ESB_Connector_Home}/" and run the following command.
         $ mvn clean install