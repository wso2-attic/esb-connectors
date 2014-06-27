Product: Integration tests for WSO2 ESB AmazonSQS connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is rquired. this test suite has been configred to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{PATH_TO_SOURCE_BUNDLE}/amazonsqs-connector/amazonsqs-connector-1.0.0/org.wso2.carbon.connector/repository/"

 2. The ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
   
    <messageFormatter contentType="application/x-www-form-urlencoded"
                          class="org.apache.axis2.transport.http.XFormURLEncodedFormatter"/>
	<messageBuilder contentType="application/x-www-form-urlencoded"
                        class="org.apache.synapse.commons.builders.XFormURLEncodedBuilder"/>

	Note: Add the above message formatter and the corresponding message builder only if they are not available in the axis configurations.
	 
	
 3. Follow these steps before start testing.
 
    a)  Create a fresh account in Amazon AWS and Log on to http://aws.amazon.com/sqs/ with the web browser.
    b)  Save the AWSAccessKeyId and AWSSecretKey while continuing the registration process.	
	c)  Update the "amazonsqs" properties file at location "{PATH_TO_SOURCE_BUNDLE}/amazonsqs-connector/amazonsqs-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
	i) 		accessKeyId 			- Your AWS account is identified by your Access Key ID. Use the saved Access Key ID in step b). 
	ii) 	secretAccessKey         - Secret access key given in the account. Use the saved Secret Access Key in step b). 
	iii) 	version 				- Version of the API. The tested version of the API is 2009-02-01. 
    iv) 	region 					- regional endpoint to make your requests(Eg: us-east-1).
	v) 	    negativeQueueName 		- An invalid value for queueName. Eg: '@'.									
	vi) 	accountId				- The AWS account number of the principal who will be given permission. The principal must have an AWS account, but does not need to be signed up for Amazon SQS.
	vii) 	esbLabel				- The unique identification of the permission you're setting (Eg: AliceSendMessage). Constraints: Maximum 80 characters; alphanumeric characters, hyphens (-), and underscores (_) are allowed.
	viii) 	apiLabel				- The unique identification of the permission you're setting (Eg: AliceSendMessage). Constraints: Maximum 80 characters; alphanumeric characters, hyphens (-), and underscores (_) are allowed.
	
		The following parameters are used for the mandatory and optional test cases of createQueue method. Parameter value will be set as the name of the queue.
		Parameter values can be alphanumeric.
			
	ix)		queueName 				- Name of the queqe eg : queqe
	x) 	    optionalQueueName 		- eg : queue1
	xi) 	apiOptionalQueueName	- eg : 123SampleQueu
	xii)	apiQueueName			- eg : SampleQueqeName	
		
	
 4. Navigate to "{PATH_TO_SOURCE_BUNDLE}/amazonsqs-connector/amazonsqs-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install