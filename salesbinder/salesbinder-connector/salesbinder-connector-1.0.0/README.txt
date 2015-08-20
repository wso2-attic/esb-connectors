Product: Integration tests for WSO2 ESB Sales Binder connector

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

 1. Download ESB 4.9.0-BETA-SNAPSHOT by following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2.	Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
	If required add the X.509 certificate from https://[subdomain].salesbinder.com (follow step 4 to create a sub domain) to the client-truststore.jks of the ESB located in <ESB_HOME>/repository/resources/security folder
	and wso2carbon.jks located in <SALESBINDER_CONNECTOR_HOME>/salesbinder-connector/salesbinder-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products.

 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml".
   
    <messageFormatter contentType="application/json"
                          class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
    <messageBuilder contentType="application/json"
                          class="org.apache.synapse.commons.json.JsonStreamBuilder"/>
 
 
 4.	Follow the below steps to create a Sales Binder account (sub domain) and derive the API key.
	i)  Navigate to "https://www.salesbinder.com/" and click on "Create Account" button.
   ii)  Enter the required details and complete the account creation. 
   iii) Login to the created SalesBinder account and derive the API Key by navigating to Profile and by clicking on 'Generate New API Key' button. 
 
 5. Create two inventory items with a single location.
	i)   Navigate to "https://www.salesbinder.com/" and login to the newly created Sales Binder account dashboard (Use the account credentials in step 5).
	ii)  Navigate to "https://[subdomain].salesbinder.com/locations" and click on  "Add New Location" button to add a new inventory location.
		 Follow the same and create another location to be used for update test case.
	iii) Go to "Locations & Zones" sub-category under "Inventory" in Sales Binder account dashboard, select the created locations (in step (6)ii) and extract the IDs of both locations from the URL.
	iv) Navigate to "https://[subdomain].salesbinder.com/items" and create two new inventory items using a single location (use the location in step(6)[ii]).
 
 6. Update the Sales Binder properties file at location "<SALES_BINDER_CONNECTOR_HOME>/salesbinder-connector/salesbinder-connector-2.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   	i)   	apiUrl		  			- The URL of Sales Binder api (https://[subdomain].salesbinder.com).
	ii)  	apiKey		  			- The API key obtained in step(4)[iii] which gives access to the API.
	iii) 	contactEmail      		- Email address of the contact to be created with mandatory parameters.
	iv)  	contactEmailOpt     	- Email address of the contact to be created with optional parameters.
	v)	 	contactFirstName		- First name of the contact.
	vi)	 	contactLastName			- Last name of the contact.
	vii) 	itemName				- Name of the item to be created.		 
	viii)	itemCost	 			- Cost of the item.
	ix)  	itemMultiple			- Type of the item.(0 = Unique Item, 1 = Quantity Item)
	x)   	documentContextId		- Context value to determine what type of document to be created (Invoice=5,Estimate=4,Purchase Order=11).
	xi)  	issueDate		 		- Date of issuing of the document. Follow the format YYYY-MM-DD (eg:2015-02-12).
	xii) 	shippingAddress 		- Shipping address related to the document.
	xiii)	accountContextId 		- Context value to determine what type of Account to be created (Customer=2,Prospect=8,Supplier=10).
	xiv) 	accountName 			- Name of the account (Unique). This parameter should be change in each integration run.
	xv)  	accountOfficeEmail 		- Official mailing address of the account.	
	xvi) 	locationId				- ID of one of the locations created in step(5)[iii].
	xvii)	itemNameUpdated			- Updated name of the inventory item. Use a different value to what was used in 6. vii)
	xviii)	itemCostUpdated			- Updated cost of the inventory item. Use a different value to what was used in 6. viii)
	xix)	locationIdUpdated		- Updated location ID of the inventory item. Use the ID the location (the one which was not used in 6. xvi)) created in step(5)[iii].
	xx)		itemDescriptionUpdated	- Updated description of the inventory item.
	xxi)	itemMultipleUpdated		- Updated multiple status of the inventory item. Use a different value to what was used in 6. ix)(0 = Unique Item, 1 = Quantity Item)
	

 7. Navigate to "{ESB_Connector_Home}/" and run the following command.
         $ mvn clean install