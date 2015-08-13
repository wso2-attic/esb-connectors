Product: Integration tests for WSO2 ESB Sales Binder connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0 SNAPSHOT
 
STEPS:
 
 1. Download ESB 4.9.0-BETA from official website.

 2. Make sure that the ESB 4.9.0 SNAPSHOT zip file with latest patches  and the changes in step 1 and 2, is available at "{SALES_BINDER_CONNECTOR_HOME}/salesbinder-connector/salesbinder-connector-2.0.0/org.wso2.carbon.connector/repository/"
 
 3. The ESB should be configured as below;
 
	Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).
   
    <messageFormatter contentType="application/json"
                          class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
    <messageBuilder contentType="application/json"
                          class="org.apache.synapse.commons.json.JsonStreamBuilder"/>
 
 
 4.	Follow the below steps to create a Sales Binder account and Derive the API key.

	i)  Navigate to "https://www.salesbinder.com/" and click on "Create Account" button.
   ii)  Enter the required details and complete the account creation. 
   iii) Login to the created SalesBinder account and derive the API Key by navigating to Profile and by clicking on 'Generate New API Key' button.
 
 
 5. Create two inventory items with a single location.
	i)   Navigate to "https://www.salesbinder.com/" and login to the newly created Sales Binder account dashboard (Use the account credentials in step 5).
	ii)  Navigate to "https://[subdomain].salesbinder.com/locations" and click on  "Add New Location" button to add a new inventory location.
	iii) Go to "Locations & Zones" sub-category under "Inventory" in Sales Binder account dashboard, select the created location (in step (6)ii) and extract the location ID from URL.
	iv) Navigate to "https://[subdomain].salesbinder.com/items" and create two new inventory items using a single location (use the location in step(6)[ii]).
 
 6. Update the Sales Binder properties file at location "<SALES_BINDER_CONNECTOR_HOME>/salesbinder-connector/salesbinder-connector-2.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
	i)   apiUrl		  				- The URL of Sales Binder api(http://[subdomain].salesbinder.com).
	ii)  apiKey		  				- The API key obtained in step(5)[iii] which gives access to the API.
	iii) contactEmail      			- Email address of the contact to be created with mandatory parameters.
	iv)  contactEmailOpt     		- Email address of the contact to be created with optional parameters.
	v)	 contactFirstName			- First name of the contact.
	vi)	 contactLastName			- Last name of the contact.
	vii) itemName					- Name of the item to be created.		 
	viii)itemCost	 			    - Cost of the item.
	ix)  itemMultiple				- Type of the item.(0 = Unique Item, 1 = Quantity Item)
	x)   documentContextId		    - Context value to determine what type of document to be created (Invoice=5,Estimate=4,Purchase Order=11).
	xi)  issueDate		 			- Date of issuing of the document. Follow the format YYYY-MM-DD (eg:2015-02-12).
	xii) shippingAddress 			- Shipping address related to the document.
	xiii)accountContextId 			- Context value to determine what type of Account to be created (Customer=2,Prospect=8,Supplier=10).
	xiv) accountName 				- Name of the account (Unique). This parameter should be change in each integration run.
	xv)  accountOfficeEmail 		- Official mailing address of the account.	
	xvi) locationId					- ID of the location which created in step(5)[iii].
	

 8. Navigate to "{SALES_BINDER_CONNECTOR_HOME}/salesbinder-connector/salesbinder-connector-2.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
	  
 9. Account Details
	Username: sampathliyanage@hotmail.com
	Password: 1qaz2wsx@
	