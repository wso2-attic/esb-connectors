Product: Integration tests for WSO2 ESB Amazon Simple DB connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches are available at "{AMAZONSIMPLEDB_CONNECTOR_HOME}/amazonsimpledb-connector/amazonsimpledb-connector-1.0.0/org.wso2.carbon.connector/repository/"

 2. The ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
   
   <messageFormatter contentType="application/octet-stream" class="org.apache.axis2.format.BinaryFormatter"/>

	Note: Add the above message formatter only if it is not available in the axis configurations.
	
 3. Prerequisites for AmazonSimpleDB Connector Integration Testing
    Follow these steps before start testing.
    a)  Create a fresh account in amazons3 and Log on to http://aws.amazon.com/s3/ with the web browser.
    b)  Save the AWSAccessKeyId and AWSSecretKey while continuing the registration process.	
	c)  Update the "Amazon Simple DB" properties file at location "{AMAZONSIMPLEDB_CONNECTOR_HOME}/amazonsimpledb-connector/amazonsimpledb-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
   
	i) accessKeyId 				- Your AWS account is identified by your Access Key ID. Use the saved Access Key ID in step b). 
	ii) secretAccessKey         - Secret access key given in the account. Use the saved Secret Access Key in step b). 
	iii) version 				- Version of the API. The current version of the API is 2009-04-15. 
	iv) signatureVersion 		- The AWS signature version, current version value is "2".
	v) signatureMethod 			- Explicitly provide the signature method, which is "HmacSHA1".
	vi) amazonSimpleDBApiUrl 	- Current API url is 'https://sdb.amazonaws.com'.
	vii) domainName 			- The name of the domain to create. The name can range between 3 and 255 characters and can only contain the following characters: a-z, A-Z, 0-9, '_', '-', and '.'. 
								  Please make sure that a domain with the given name doesn't exist in the current account. 
	viii) negativeDomainName 	- An invalid value for domain name. Eg: '@'
	ix) sleepTime 				- An integer value in milliseconds, to wait between API calls to avoid conflicts at API end. preferred value is 10000
	
	Allowed characters for the below mentioned parameters are all UTF-8 characters valid in XML documents.
	
	x) itemName 				- Unique identifier of an item. Eg: person 
	xi) attributeName 			- Name of an attribute associated with an item. Eg: age   
	xii) attributeValue 		- Value of attribute associated with attributeName. Eg: 25
	xiii) attributeName2 		- Name of an attribute associated with an item. Eg: name 
	xiv) attributeValue2 		- Value of attribute associated with attributeName2. Eg: andrew 
	
	
 4. Navigate to "{AMAZONSIMPLEDB_CONNECTOR_HOME}/amazonsimpledb-connector/amazonsimpledb-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install