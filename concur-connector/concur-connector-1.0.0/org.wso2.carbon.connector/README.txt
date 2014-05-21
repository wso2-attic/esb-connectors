Product: Integration tests for WSO2 ESB Concur connector
    Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above

    Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1

Note:
	This test suite can execute based on two scenarios.
		1. Use the given test account and parameters. - in this scenario you only need to replace apiUrl, accessToken and user in property file
		2. Setup new concur account and follow all the instruction given below
	
Steps to follow in setting integration test.
 1.  Download ESB 4.8.1 from official website.
 2.  Deploy following patches.
            special-char-on-get
            multipart-patch
			PATCH for XSLT with local entry
 3.  Navigate to location "/wso2esb-4.8.1/repository/conf/axis2" and add/uncomment following lines in "axis2.xml" and Message Formatters and Message Builders should be added for each of the content types of the files to be added as attachments.
		
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
			
 4.  Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "Integration_Test/products/esb/4.8.1/modules/distribution/target/".

 5.  Make sure "integration-base" project is placed at "Integration_Test/products/esb/4.8.1/modules/integration/"

 6.  Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/integration-base" and run the following command.
      $ mvn clean install
      
 7.  Add following dependency to the file "Integration_Test/products/esb/4.8.1/modules/integration/connectors/pom.xml"
 
		 <dependency>
			 <groupId>org.wso2.esb</groupId>
			 <artifactId>org.wso2.connector.integration.test.base</artifactId>
			 <version>4.8.1</version>
			 <scope>system</scope>
			 <systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>
		 </dependency>
  
 8.  Copy the src directory "concur/org.wso2.carbon.connector/src/" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/"
        
 9.  Prerequisites for Concur Connector Integration Testing

     Follow these steps before start testing.
	 
     a)  Create a fresh account in Concur using the URL https://developer.concur.com/register with the web browser.
     b)  Concur automatically creates a basic partner application and gives you a Welcome page includes the following information:
	 
			1) The Consumer Key and Consumer Secret you'll use when making requests to your sandbox.
			2) The list of enabled APIs by default for your partner application has access to.
			3) The Access Token tied to your user account that you will provide when authenticating with your sandbox for this partner application. 
			   Note: This Access Token is only for authenticate the APIs enabled by default.
		 Copy this information and store it in a safe place.
		 
     c)  Go to Administration -> Web Services -> Register Partner Application
     d)  Select the partner application available and click modify. This opens Modify Partner Application dialog.
	 e)  Tick all the APIs available under API's category in the Modify Partner Application dialog. Keep the other values as it is and click ok.
	 f)  Go to Expense -> New Expense Report	 
     g)  Create a New Expense Report by giving appropriate data into required fields.
	 h)	 Generate New Access Token using OAuth web flow using bellow steps:
	 
			1) Use following redirection URL format to retrieve OAuth code using web browser.
			
				https://www.concursolutions.com/net2/oauth2/Login.aspx?client_id=(YOUR_CONSUMER_KEY)&scope=ATTEND,CONFIG,EXPRPT,EXTRCT,IMAGE,ITINER,LIST,MTNG,PAYBAT,TRVPRF,TRVREQ,TWS,USER&redirect_uri=https://www.google.lk/
				
				Note: Use the Consumer Key returned from step (b) for client_id URL parameter.
			
			2) This gives OAuth Confirmation page is where the user grants the partner application access to the user’s data and Click Allow.
			3) Use following redirection URL format to retrieve Access Token using web browser.
			
				https://www.concursolutions.com/net2/oauth2/GetAccessToken.ashx?code=(OAuth_CODE)&client_id=(YOUR_CONSUMER_KEY)&client_secret=(YOUR_CONSUMER_SECRET)
				
				Note: Use OAuth Code returned from above step (1) for code URL parameter. Use the Consumer Key and Consumer Secret returned from step (b) 
				      for client_id and client_secret URL parameter.
	 
     i) Following fields in the property file also should be updated appropriately.
	  
	    1) imageContentType - the content type of the image to be added.
		2) imageFileName - the name of the image file to be added with the image extension.
		3) timeOut - waiting time for completing the requests before querying any further.
		4) transactionAmount - the total amount of the expense in the original currency, with up to three decimal places for create a Quick Expense Entry.
		5) updatedTransactionAmount - the total amount of the expense in the original currency, with up to three decimal places for update a Quick Expense Entry.
		6) vendorDescription - The descriptive text of the vendor for create a quick expense entry. Maximum Length is 64.
		7) updatedVendorDescription - The descriptive text of the vendor for update a quick expense entry. Maximum Length is 64.

 10. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
     $ mvn clean install

	 
	 credential of test account:
     API URL: https://www.concursolutions.com
     username: wso2connector.abdera@gmail.com
     password: connector1234
	 Access Token: 0KgYaacRmTc+QO6jGacTIWskcd8=
