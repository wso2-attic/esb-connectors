
Running Integration Tests
=========================

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - UBUNTU 13.10
 - WSO2 ESB 4.8.1

STEPS:

1. Copy the ESB 4.8.1 zip to the location "APNS_CONNECTOR_HOME/repository/"

2. Get a 'production' or 'development' APNs push notification certificate and export it as a .p12 file. 
     (How to : http://goo.gl/gca7c5)

3. Copy the above .p12 file to APNS_CONNECTOR_HOME/src/test/resources/artifacts/ESB/config/auth/

4. Update connector properties file "apns.properties" located in "APNS_CONNECTOR_HOME/src/test/resources/artifacts/ESB/connector/config/", 
	with following information
	- certificateFilename : Name of the .p12 file which has been copied in step 3
	- certificatePassword : Password of the .p12 file
	- deviceToken : Device token of the iOS device which is used in integration testing. 
		(NOTE : If an iOS device is not available put any hexadecimal string of 64 characters.) 

5.  Navigate to "APNS_CONNNECTOR_HOME" and run the following command.
      $ mvn clean test
