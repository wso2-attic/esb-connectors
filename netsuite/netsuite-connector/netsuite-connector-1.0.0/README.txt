Product: Integration tests for WSO2 ESB Netsuite connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - Supported Netsuite WSDL Version : https://webservices.netsuite.com/wsdl/v2014_1_0/netsuite.wsdl
 - Having a valid Netsuite account with valid user access credentials.
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1
 
STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{NETSUITE_CONNECTOR_HOME}/netsuite-connector/netsuite-connector-1.0.0/org.wso2.carbon.connector/repository/"
		
 2. If you want to add a new user to the Netsuite account, refer the Netsuite Help documentation at https://system.na1.netsuite.com/help/helpcenter/en_US/Output/Help/EmployeeManagement/Employees/AddingEmployee.html
		 	 
 3. Update the Netsuite properties file at location "{NETSUITE_CONNECTOR_HOME}/netsuite-connector/netsuite-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
 
      i) apiUrl - Use https://webservices.na1.netsuite.com/services/NetSuitePort_2014_1
	
	 ii) email - Use the email address that is used to create the account (this also can be an email of the added user that is described under step 2)
	
    iii) password - Use the password of the user.
   
     iv) account - Use the account Id.
	 
	  v) attachCustomerInternalId - Use an internal ID of a customer.
	  
	 vi) attachContactInternalId - Use an internal ID of a contact.
	   
    vii) customerNameMandatory - Specify a desired name for the Customer.
	
   viii) customerNameOptional - Specify a different name for the Customer.
	 
	 ix) customerNameUpdated - Specify a different name for the Customer which needs to be updated.
	 
      x) companyName - Specify a desired name for the Company.
	
	 xi) companyNameUpdated - Specify a different name for the Company which needs to be updated.
	 
    xii) subsidiaryId	- Specify a valid subsidiary ID.

   xiii) customerEmail - Provide a valid email address as the email of the customer.

    xiv) customerComment - Provide a desired customer comment.

     xv) customerUrl - Provide a desired customer Url.

    xvi) getSelectValueFilterByInternalId - The internal ID of a filterBy field.
   
   xvii) getItemAvailabilityInventoryItemInternalId - Internal ID of an Inventory Item.
   
  xviii) getItemAvailabilityLastQtyAvailableChange - A valid past date should be provided in the correct format.(e.g : 2014-04-20T16:09:55.000-07:00).
  
   NOTE : Modify the values of vii), viii), ix) before each run.
    
 4. Navigate to "{NETSUITE_CONNECTOR_HOME}/netsuite-connector/netsuite-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install


