Product: Integration tests for WSO2 ESB Concur connector
    Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
	- The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
            https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

    Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - Mac OSx 10.9
    - WSO2 ESB 4.8.1

Note:
	This test suite can execute based on two scenarios.
		1. Use the given test account and parameters at the end of the file. - in this scenario you only need to replace access Token in property file
		2. Setup new concur account and follow all the instruction given below in step 5.
	
Steps to follow in setting integration test.
 1.  Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/
    
 2.  Navigate to location "/wso2esb-4.8.1/repository/conf/axis2" and add/uncomment following lines in "axis2.xml" and Message Formatters and Message Builders should be added for each of the content types of the files to be added as attachments.
		
		Message Formatters :-
		
        <messageFormatter contentType="image/gif" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
        <messageFormatter contentType="img/gif" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
        <messageFormatter contentType="image/jpeg" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
        <messageFormatter contentType="image/png" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		
		Message Builders :-
		
		<messageBuilder contentType="image/gif" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
        <messageBuilder contentType="img/gif" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
        <messageBuilder contentType="image/jpeg" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
        <messageBuilder contentType="image/png" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
			
 3.  Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "<ESB_CONNECTORS_HOME>/repository/".

         
 4.  Prerequisites for Concur Connector Integration Testing

     Follow these steps before start testing.
	 
     a)  Create a fresh account in Concur using the URL https://developer.concur.com/register with the web browser.
     b)  Concur automatically creates a basic partner application and gives you a Welcome page includes the following information:
	 
			1) The Consumer Key and Consumer Secret you'll use when making requests to your sandbox.
			2) The list of enabled APIs by default for your partner application has access to.
			3) The Access Token tied to your user account that you will provide when authenticating with your sandbox for this partner application. 
			   Note: This Access Token is only for authenticate the APIs enabled by default.
		 Copy this information and store it in a safe place.
		
     c)  Click On Get Started in the welcome page and it directs to Set up Expense for your company page.
     d)  Give values for required fields and proceed with each and every step to setup the expense category for your company in concur account.
			Note: Some steps you can proceed with its default values.
     e)  Go to Administration -> Web Services -> Register Partner Application
     f)  Select the partner application available and click modify. This opens Modify Partner Application dialog.
	 g)  Tick all the APIs available under API's category in the Modify Partner Application dialog. Keep the other values as it is and click ok.
	 h)  Go to Expense -> New Expense Report. Once the Expense Report is created, add a new Expense.	 
	 i)	 Generate New Access Token using OAuth web flow using bellow steps:
	 
			1) Use following redirection URL format to retrieve OAuth code using web browser.
			
				https://www.concursolutions.com/net2/oauth2/Login.aspx?client_id=(YOUR_CONSUMER_KEY)&scope=ATTEND,CONFIG,EXPRPT,EXTRCT,IMAGE,ITINER,LIST,MTNG,PAYBAT,TRVPRF,TRVREQ,TWS,USER&redirect_uri=https://www.google.lk/
				
				Note: Use the Consumer Key returned from step (b) for client_id URL parameter.
			
			2) This gives OAuth Confirmation page is where the user grants the partner application access to the user’s data and Click Allow.
			3) Use following redirection URL format to retrieve Access Token using web browser.
			
				https://www.concursolutions.com/net2/oauth2/GetAccessToken.ashx?code=(OAuth_CODE)&client_id=(YOUR_CONSUMER_KEY)&client_secret=(YOUR_CONSUMER_SECRET)
				
			Note: Use OAuth Code returned from above step (1) for code URL parameter. Use the Consumer Key and Consumer Secret returned from step (b) 
		          for client_id and client_secret URL parameter.
	 
     j) Update the Concur properties file at location "{CONCUR_CONNECTOR_HOME}/concur-connector/concur-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.	 
		Following fields in the property file should be updated appropriately.
	  
	    1) imageContentType - the content type of the image to be added.
		2) imageFileName - the name of the image file to be added with the image extension. Set the resourceDirectoryRelativePath property to the relative path leading to this attachment file.
		3) timeOut - waiting time for completing the requests before querying any further.
		4) transactionAmount - the total amount of the expense in the original currency, with up to three decimal places for create a Quick Expense Entry.
		5) updatedTransactionAmount - the total amount of the expense in the original currency, with up to three decimal places for update a Quick Expense Entry.
		6) vendorDescription - The descriptive text of the vendor for create a quick expense entry. Maximum Length is 64.
		7) updatedVendorDescription - The descriptive text of the vendor for update a quick expense entry. Maximum Length is 64.
		
		NOTE : Update the access token , user info.

 5. Make sure that the concur connector is set as a module in esb-connectors parent pom.
         <module>concur/concur-connector/concur-connector-1.0.0/org.wso2.carbon.connector</module>


 6. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
          $ mvn clean install

     credential of test account:
     API URL: https://www.concursolutions.com
     username: testint@suremail.info
     password: 1qaz2wsx@
     Access Token: rRl3Cfi17SWGMFCnaBmO8zpRFUo=
