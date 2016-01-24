Product: Integration tests for WSO2 ESB LDAP connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is required. this test suite has been configured to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Mac OSX 10.9.2
 - WSO2 ESB 4.9.0-ALPHA
 
 STEPS:

1. Make sure the WSO2 ESB 4.9.0-ALPHA zip file with available at "{ESB_Connector_Home}/repository/"

2. Integration Tests uses Embedded in-memory LDAP server in tests. So normally connector doesn't need an external LDAP server to run its tests.
    If you want to test the Connector with your LDAP server, do necessary changes to LDAP properties file at location
    "{ESB_Connector_Home}/ldap/ldap-connector/ldap-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config".

	providerUrl - URL of you LDAP server
	securityPrincipal - Root user DN
	securityCredentials - Root user password
	ldapUserBase - User Base of the LDAP server
	testUserId - Sample test user id
	baseDN - Base DN of the LDAP server
	ldapPort - Port which Embedded LDAP server should be started. (Default 10389)
	useEmbeddedLDAP - Use embedded LDAP server or outside ldap sever. If you want to use your LDAP server to test with the Connector, make this value - false
	
3.Make sure that the ldap connector is set as a module in esb-connectors parent pom.
        <module>ldap/ldap-connector/ldap-connector-1.0.0/org.wso2.carbon.connector</module>

4. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install

NOTE : 
If you are using Embedded LDAP mode in Integration Testing, please make sure that ldapPort you are assigning in config file is not used by any other application in your local machine.