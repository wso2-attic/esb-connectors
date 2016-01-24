Product: Integration tests for WSO2 ESB AmazonSNS connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-ALPHA

Steps to follow in setting integration test.
 1.  Download ESB 4.9.0-ALPHA from official website.

 2.  The ESB should be configured as below;
	 Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
		
		<messageFormatter contentType="application/x-www-form-urlencoded"
                          class="org.apache.axis2.transport.http.XFormURLEncodedFormatter"/>
		<messageBuilder contentType="application/x-www-form-urlencoded"
                        class="org.apache.synapse.commons.builders.XFormURLEncodedBuilder"/>

	Note: Add the above message formatter only if it is not available in the axis configurations.
			
 3.  Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{}/repository/".

 
 4. Prerequisites for AmazonSNS Connector Integration Testing
    
	Follow these steps before start testing.
    a)  Create a fresh account in Amazon AWS and Log on to http://aws.amazon.com/sns/ with the web browser.
    b)  Save the AWSAccessKeyId and AWSSecretKey while continuing the registration process.	
	c) 	Follow the steps to get subscriptionToken and related topicArn values
		Logged to AmazonSNS console https://console.aws.amazon.com/sns/home , create two new topics (e.g. Topic_A, Topic_B) and create two subscriptions (e.g. Create Subscription A with recently created Topic_A and Subscription B with recently created Topic_B) by using email protocol.

		Once you create a subscription, you get a confirmation email to selected subscribed email. Open the subscription confirmation email and copy the “Token” from the “Confirm subscription” link URL.  
		place those copied Token and topicArn values to "amazonsns" properties file in file at location "{ESB_CONNECTORS_HOME}/amazonsns/amazonsns-connector/amazonsns-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

	i)		subscriptionToken			- place the copied Subscription A Token.
	ii)		subscriptionTopicArn		- place the Topic_A created TopicArn.
	iii)	subscriptionTokenOpt		- place the copied Subscription B Token.
	iv)		subscriptionTopicArnOpt		- place the Topic_B created TopicArn.
	
	d)	Follow the steps given bellow to get platformApplicationArn and token(registration ID) values.
			
			Step 1: Create a Google API Project and Enable the GCM Service.(Refer http://developer.android.com/google/gcm/gs.html#create-proj for further information)
			Step 2: Obtain the Server API Key.(Refer http://developer.android.com/google/gcm/gs.html#access-key for further information)
			Step 3: Obtain a Registration ID from GCM.
			
			        Note: In this step you can use the sample Android app called 'AndroidMobilePushApp' included in 'snsmobilepush.zip' provided by AWS to 
					obtain a token (registration ID) from GCM.(For more information refer http://docs.aws.amazon.com/sns/latest/dg/mobile-push-gcm.html)
					
			Step 4: Use steps bellow to generate platformApplicationArn values twice (Changing only applicationName twice) by using 'SNSSamples' which contains 
					the 'snsmobilepush.zip' provided by Amazon AWS.
					
					Note: 'SNSSamples' project configuration as an eclipse project with AWS SDK for Java and other libraries can be done by referring link 
						   http://docs.aws.amazon.com/sns/latest/dg/mobile-push-gcm.html
						   
					(a) Specify your AWS Access Key and AWS Secret Key in 'AwsCredentials.properties' in 'SNSSamples' eclipse project.
					(b) Uncomment the code in 'SNSMobilePush.java' for android. e.g. sample.demoAndroidAppNotification();
					(c) Enter the registration information for android in 'SNSMobilePush.java'. e.g. ServerAPIKey, applicationName, registrationId
					(d) Comment out the lines of code to create an Endpoint, publish a push notification to an endpoint and Delete the Platform Application in 'demoNotification' method.
					(e) Run the 'SNSSamples' project to get generate platformApplicationArn to console.
					
			Step 5:	Copy the token(Registration ID) from Step 3 and two platformApplicationArn values(e.g. platformApplicationArn_A and platformApplicationArn_B)
					from Step 4 and past into "amazonsns" properties file in file at location 
					"{ESB_CONNECTORS_HOME}/amazonsns-connector/amazonsns-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
					
					i)		platformApplicationArn 		- place the copied platformApplicationArn_A value.
					ii)		platformApplicationArnOpt 	- place the copied platformApplicationArn_B value.
					iii)	token 						- place the token copied from Step 3.
			
	e)  Update the other properties in the "amazonsns" properties file at location "{ESB_CONNECTORS_HOME}/amazonsns/amazonsns-connector/amazonsns-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
	i) 		awsAccessKeyId 				- Your AWS account is identified by your Access Key ID. Use the saved Access Key ID in step b). 
	ii) 	awsSecretAccessKey         	- Secret access key given in the account. Use the saved Secret Access Key in step b). 
	iii) 	region 						- regional endpoint to make your requests(Eg: us-east-1).
	iv)		name						- Name of the topic (Eg. : Hello_World). This parameter is mandatory to create a topic. It must be made up of only upper case and lower case ASCII letters, numbers, underscores, and hyphens, and must be between 1 and 256 characters long.
	v)		customUserData				- Arbitrary user data to associate with the endpoint. This is optional to create endpoint.
	vi)		message						- Message content to be used in publishing a topic. Eg. : Sample Message
	vii)	subject						- Subject of the message to be used in publishing a topic. Eg. : Welcome!

 5.Make sure that the amazonsns connector is set as a module in esb-connectors parent pom.
            <module>/amazonsns/amazonsns-connector/amazonsns-connector-1.0.0/org.wso2.carbon.connector</module>

 6.Navigate to "<ESB_CONNECTORS_HOME>/" and run the following command.
         $ mvn clean install