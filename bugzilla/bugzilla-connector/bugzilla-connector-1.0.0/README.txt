Product: Integration tests for WSO2 ESB Bugzilla connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-SNAPSHOT
 - Bugzilla 5.0rc3

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by following the URL: https://svn.wso2.org/repos/wso2/people/jeewantha/4.9.0_release/released/M4/wso2esb-4.9.0-SNAPSHOT.zip.
	Apply the patches found in https://www.dropbox.com/s/bs83ll1m8kwgylq/patch0009.zip?dl=0 by copying the extracted files into {ESB_HOME}/repository/components/patches.

 2. Compress the modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{Bugzilla_Connector_Home}/bugzilla-connector/bugzilla-connector-1.0.0/org.wso2.carbon.connector/repository/".

 3. Generate a Bugzilla API Key.
	i) 	Log in to the Bugzilla instance Admin dashboard.
	ii)	Navigate to "Preferences" and select the "API Keys" sub-tab.
	iii) generate a new API Key by clicking "Submit Changes" button (make sure to check the "Generate a new API key with optional description" checkbox).

 4.) Prerequisites for Bugzilla Connector Integration Testing.
	i) Navigate to "Custom Fields" in Bugzilla instance "Administration" and create a new custom field. 
 
 5. Update the Bugzilla properties file at location "{Bugzilla_Connector_Home}/bugzilla-connector/bugzilla-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The web service endpoint to the created Bugzilla instance.
	ii) 	apiKey							-   Use the apiKey obtained under step 3 iii).
	iii)*	productName						-	Use a unique string as the product name.
	iv)		productDescription				-   Use a string as the product description.
	v)		productVersion					- 	Use a valid numeric value as the product version (e.g. 1.0.0).
	vi)*	productNameOpt					-	Use a unique string as the product name for the create product with optional parameters.
	vii)	productDescriptionOpt			-   Use a string as the product description for create product with optional parameters.
	viii)   productConfirmation				-	Use a boolean value ("true" or "false") for the product confirmation.
	ix)		productIsOpen					-	Use a boolean value ("true" or "false") to assign product as open.
	x)		componentName					-	Use a valid string as the component name.
	xi)		componentDescription			-	Use a string as the component description.
	xii)	componentIsOpen					-   Use a boolean value ("true" or "false") to assign component as open.
	xiii)*	emailMand						- 	Use a valid and unique email address to create a new user.
	xiv)*	emailOpt						- 	Use a valid and unique email address to create a new user with optional parameters.
	xv)		userName						-	Use a string as the user name (e.g. James Goslin).
	xvi)	bugUpdateSummary				-   Use a string as the bug summary. 
	xvii)	bugUpdateDeadline				-   Use a valid date string as the bug deadline (e.g. 2015-10-31).
	xviii)  bugUpdateWhiteboard				- 	Use a string for the bug whiteboard parameter.
	xix)	bugUpdateUrl					-	Use a valid URL string for the bug (e.g. http://yahoo.com)
	xx)		bugDescription					-	Use a string as the bug description.
	xxi)	bugSummary						-   Use a string as the bug summary.
	xxii)	bugOPSys						-   Place a valid operating system value for the bug creation (e.g. windows). 
	xxiii)	bugPlatform						-	Place a valid platform value for the bug creation (e.g. PC).
	xxiv)	bugSeverity						-	Place a valid value for the bug severity (e.g. normal).
	xxv)    bugPriority                     -	Place a valid value for the bug priority (e.g. high).
	xxvi)*  bugAlias						-	Place a unique string (e.g. BDE184)
	xxvii)  bugStatus						-	Place a valid status value for the bug (e.g. confirmed).
	xxviii) cfName     						-	Place the custom field name (e.g. cf_freshdeskid) created under step 4 i).
	xxix)	cfValue							-	Place a compatible value for the custom field created under step 4 i).

	* Values need to be changed for each execution of the Test Suite. Please make sure the values are unique in the context of the same account.
	
 6. Navigate to "{Bugzilla_Connector_Home}/bugzilla-connector/bugzilla-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

		