Product: Integration tests for WSO2 ESB SugarCRM connector

Pre-requisites:

	- Maven 3.x
	- Java 1.6 or above

Tested Platform: 

	- Microsoft WINDOWS V-7
	- WSO2 ESB 4.8.1
		  
STEPS:

	1. Make sure the ESB 4.8.1 zip file with latest patches available at "Integration_Test\products\esb\4.8.1\modules\distribution\target\".

	2. Copy SugarCRM connector zip file (sugarcrm.zip) to the location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\repository\"

	3. Uncomment SugarCRM test suite and comment the other test suites of the following file - "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\testng.xml"  

	4. Obtain a trial sugarCRM account if the given trial account is expired, by using the following steps.
			
		i) Login to https://www.sugarcrm.com.

			
			When creating a trial login, select the deployment region as 'North America' as the integration test results are compared against the pre-defined dataset of that region.
			In case if you need to support for 'Europe' deployment region, you need to modify the request parameters as per the pre-defined dataset of that region. (e.g. Module Ids)
			
			Note: This account is subjected to expire within 7 days. At the event of expiration the user needs to re-register for another trial account.

	
		ii) In order to change the default password of the admin, follow the steps below:
			a) Select admin profile (e.g. Jen Smith) from the dashboard of the trial account
			b) Select profile option from the top right corner of the screen
			c) Click on 'Edit' and  then select 'Password' tab 
			d) Set Current password, New password and Confirm Password
			e) Click on Save
			
		iii) Convert your password (which was set in step 5) to MD5 hash code and change appUrl, userName and password (MD5 hash code) appropriately on 
         	    "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\connector\config\sugarcrm.properties".
       
   	7. Copy proxy files to location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\config\proxies\sugarcrm\"

    	8. Navigate to "Integration_Test\products\esb\4.8.1\modules\integration\connectors\" and run the following command.
      	   $ mvn clean install
