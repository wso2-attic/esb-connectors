Product: Integration tests for WSO2 ESB GoodData connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
	https://github.com/wso2-dev/esb-connectors/tree/master/integration-base
		
 - Upload.zip package should be available in the GoodData WebDav Server to execute the uploadData method. Please follow the steps in below tutorial to prepare the package and upload it to the WebDav server.
   https://www.youtube.com/watch?v=Cl5ZTvQSFLQ

   Note: Make sure that the directory you created in the above process is available in the WebDav Server by navigationg to the below URL. 
   https://secure-di.gooddata.com/uploads/

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-Alpha

STEPS:

 1. Extract the certificate from browser by navigating to https://www.gooddata.com/ and place the certificate file in following locations. 

	i)  "<GOODDATA_CONNECTOR_HOME>/gooddata-connector/gooddata-connector-1.0.0/org wso2.carbon.connector/src/test/resources/keystores/products"

		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "GoodData"' in command line to import gooddata certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from gooddata with  the extension, change it accordingly. Remove the copied certificate.
	
	ii) "wso2esb-4.9.0-Alpha/repository/resources/security"
	
		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "GoodData"' in command line to import gooddata certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from gooddata with  the extension, change it accordingly. Remove the copied certificate. 
		 
 2. The ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).

	<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
   
	<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 3. Place the ESB 4.9.0-Alpha.zip file under "<GOODDATA_CONNECTOR_HOME>/gooddata-connector/gooddata-connector-1.0.0/org.wso2.carbon.connector/repository/"
	
 4. Create a GoodData account using the URL "https://www.gooddata.com/".	

 5. Update the GoodData properties file at location "<GOODDATA_CONNECTOR_HOME>/gooddata-connector/gooddata-connector-1.0.0/org wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
		i)    apiUrl        	  - GoodData API url(https://secure.gooddata.com)
		ii)   username	          - Username of the GoodData account. 
		iii)  password		      - Password of the GoodData account. 
		iii)  projectId           - Unique identifier of the project. Project id can be derived through following steps.
		
								  --> Navigate to "https://www.gooddata.com/" and select the project from the projects panel, located next to GoodData logo. 
								  --> After selecting the project, browser URL will be formed as below.
								      "https://secure.gooddata.com/#s=/gdc/projects/<projectId>|projectDashboardPage|/gdc/md/<projectId>/...
								  --> Project id can be derived through that URL. 
								  
		iv)   title               - Title of the report that will be created during the test execution. Any string value can be assigned to this parameter. eg: "Test Title".
		v)    sleepTime           - An integer value in milliseconds, to wait between API calls to avoid conflicts at API end. preferred value is 20000. Try increasing this value if test cases fails.
		vi)   summary             - Summary of the report which will be created during the test execution. Any string value can be assigned to this parameter. eg:"Test report summary".
		vii)  alias               - Alias of the report which will be created during the test execution. Any string value can be assigned to this parameter. eg:"Test report alias".
		viii) pullIntegration     - Name of the directory at WebDav Server, which you created in the pre-requisites. 
		viii) maql                - A valid maql script. Below mentioned link can be used to obtain maql scripts which are relevant to GoodData.
									https://developer.gooddata.com/article/creating-analytical-project-with-maql-ddl
		
 6. Make sure that the gooddata connector is set as a module in esb-connectors parent pom.
       <module>gooddata/gooddata-connector/gooddata-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Navigate to "{ESB_Connector_Home}/" and run the following command.
    $ mvn clean install

 NOTE : Following GoodData account, can be used to run the integration tests.
    Username : gooddatacondev@gmail.com
    Password : 1qaz2wsx@