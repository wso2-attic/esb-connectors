Product: Integration tests for WSO2 ESB Zohobooks connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-SNAPSHOT

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by following the URL: https://svn.wso2.org/repos/wso2/people/jeewantha/4.9.0_release/released/M4/wso2esb-4.9.0-SNAPSHOT.zip.
	Apply the patches found in https://www.dropbox.com/s/bs83ll1m8kwgylq/patch0009.zip?dl=0 by copying the extracted files into {ESB_HOME}/repository/components/patches.

 2. ESB should be configured as below.
	 Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
		
		<messageFormatter contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
										  
		<messageBuilder contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

		<messageFormatter contentType="text/html"                             
							  class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

		<messageBuilder contentType="text/html"                                
							  class="org.wso2.carbon.relay.BinaryRelayBuilder"/>


 3. Compress the modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{Zohobooks_Connector_Home}/zohobooks-connector/zohobooks-connector-1.0.0/org.wso2.carbon.connector/repository/".

 4. Create a Zohobooks trial account and derive the API Key.
	i) 	Using the URL "https://www.zoho.com/books/signup/" create a Zohobooks trial account.
	ii)	Obtain the auth token and organization ID as instructed in "https://www.zoho.com/books/api/v3/".
	
 5. Prerequisites for ZohoBooks Connector Integration Testing

	i) 	Navigate to the URL "https://books.zoho.com/app#/accountant/chartofaccounts" and create at least one accountant with the Account type 'Expense'.
			

 6. Update the Zohobooks properties file at location "{Zohobooks_Connector_Home}/zohobooks-connector/zohobooks-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL specific to the created Zohobooks account (https://app.goclio.com).
	ii) 	authToken						-   Use the access token obtained under step 4 ii).
	iii)	organizationId					-	Use the organization ID obtained under step 4 ii).
	iv)*    itemNameMandatory				-	Use a unique string for the item name (Used for createItem - Mandatory testcase).
	v)*		itemNameOptional				- 	Use a unique string for the item name (Used for createItem - Optional testcase).
	vi)		rate							-   Use a numeric value, either integer or decimal (limit to 1 decimal place).
	vii)	unit							-	Use a valid measuring unit (E.g. Kgs, Nos).
	viii)   website							-	Use a properly formed website name (E.g. www.sample.com).
	ix)	    companyName						-	Use a valid string as the company name.
	x)*		contactNameMandatory			-	Use a unique string for the contact name (Used for createContact - Mandatory testcase).
	xi)*	contactNameOptional			    -	Use a unique string for the contact name (Used for createContact - Optional testcase).
	xii)*	contactPersonFirstName			-	Use a unique string for the contact person name (Used for createContact - Mandatory testcase).
	xiii)*	contactPersonLastName			-	Use a unique string for the contact person name (Used for createContact - Mandatory testcase).
	xiv)	contactPersonEmail				- 	Use a valid email address (no need to be a real one).
	xv)*	invoiceNumber					-	Use a unique valid string for the invoice number.
	xvi)	invoiceDueDate					-	Use a valid future date in the format of 'yyyy-mm-dd' (e.g. 2015-02-23).
	xvii)	purchaseDate					-	Use a valid date for purchase order creation in the format of 'yyyy-mm-dd' (e.g. 2015-02-23).
	xviii)	paymentDate						-	Use a valid date for the payment in the format of 'yyyy-mm-dd' (e.g. 2015-02-23).
	xix)	paymentAmount					-	Use a numeric value, either integer or decimal for the payment amount .
	xx)		paymentDescription				-	Use a appropriate description for the payment.
	xxi)	paymentReferenceNumber			-	Use a valid preferred string for the reference number of the payment.
	xxii)	notes							-	Use a preferred string for the note in the contact. 
	
	* Values need to be changed for each execution of the Test Suite. Please make sure the values are unique in the context of the same account.
	
 7. Navigate to "{Zohobooks_Connector_Home}/zohobooks-connector/zohobooks-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

		