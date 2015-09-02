Product: Integration tests for WSO2 ESB Canvas connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

Note:
	This test suite can be executed based on two scenarios.
		1. Use the given test account and parameters at the end of the file.
		2. Set up a new Canvas account and follow all the instruction given below in step 5.

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-ALPHA.
 
 2. The ESB should be configured as below.
	i) Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).
		
		Message Formatter :
		<messageFormatter contentType="multipart/form-data" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
						
		Message Builder :
		<messageBuilder contentType="multipart/form-data" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
	
	ii) Please make sure that the below mentioned Axis configurations are disabled (if already enabled).(/repository/conf/axis2/axis2.xml).
	
		Message Formatter :
		<messageFormatter contentType="multipart/form-data" class="org.apache.axis2.transport.http.MultipartFormDataFormatter"/>
						
		Message Builder :
		<messageBuilder contentType="multipart/form-data" class="org.apache.axis2.builder.MultipartFormDataBuilder"/>

 3. Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "<ESB_CONNECTORS_HOME>/repository/"..

 4. Extract the certificate from browser by navigating to https://canvas.instructure.com/register_from_website

 5. Go to new ESB folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/" and "<CANVAS_CONNEC  TOR_HOME>/canvas-connector/canvas-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products"

 6.Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import BaseCRM certificate in to keystore.
		 Give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
 7.Navigate to "<CANVAS_CONNECTOR_HOME>/canvas-connector/canvas-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import BaseCRM certificate in to keystore.
         	Give "wso2carbon" as password. Press "Y" to complete certificate import process.
         	
 8. Prerequisites for Canvas Connector Integration Testing

		i) 	Create a Canvas account using the URL "https://canvas.instructure.com/register_from_website".
			Note: Sign-up as a Teacher account (Select "I'm a Teacher" option) when the creating a new Canvas account.
			
		ii) After the  account creation verified, create a new course by selecting "Start a New Course" option on canvas main dashboard (Make sure to check "Make course publicly visible" option during the course creation).
		
		iii) Retrieve a new access token from canvas dashboard "settings" tab, by selecting "New Access Token" option. 

 9. Update the Canvas properties file at location "{Canvas_Connector_Home}/canvas-connector/canvas-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl					-	Use the API URL as "https://canvas.instructure.com".
	ii)		accessToken				-	Place the created access token in step 4 [iii].
	iii)	calenderEventStartDate	-	Date for create calendar event start (The date must be a future date and date format should be as '2014-10-21').
	iv)		eventTitle				-	Title for create event.
	v)		eventDescription		-	Description for the event creation.
	vi)		attachmentFileName		-	Attached file name for the create entry optional case (e.g.:github.txt).This file should be at location "{Canvas_Connector_Home}\canvas-connector\canvas-connector-1.0.0\org.wso2.carbon.connector\src\test\resources\artifacts\ESB\config\resources\canvas"
	vii)	entryMessage			-	Message text for create entry optional case. 
	viii)	title					-	Title for create discussion topic.
	ix)		message					-	Message text for create discussion topic optional case.
	x)		entryMessage			-	Message text for update entry.
	xi)		courseName				-	Course name for create course optional case (e.g.: Java Messaging Services).
	xii)	courseCode				-	Short course code for create course optional case (e.g.:JMS).
	
	
 10. Make sure that the canvas connector is set as a module in esb-connectors parent pom.
       <module>canvas/canvas-connector/canvas-connector-1.0.0/org.wso2.carbon.connector</module>


 11. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
       $ mvn clean install


 NOTE : Following are the credentials for the Canvas account used for integration tests.
 
	    email=wso2connector.abdera@gmail.com
	    password=1qaz2wsx@
