Product: Integration tests for WSO2 ESB EJB2 connector

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above
 - Jboss 5.1
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform:
 - UBUNTU 14.04
 - WSO2 ESB 4.9.0

Dependency jars
EJB2StatefulJboss.jar
EJB2StatelessJboss.jar
jbossall-client.jar

STEPS:

 1. Make sure the ESB 4.9.0 zip file with latest patches available at "{CONNECTOR_HOME}/repository/"

 2. Integration test written based on Jboss 5.1 so Download jboss server from http://sourceforge.net/projects/jboss/files/JBoss/JBoss-5.1.0.GA/

 3. get the Dependency jars file from "{EJB_HOME}/ejb2-connector/ejb2-connector-1.0.9/org.wso2.carbon.connector/src/test/resource/ESB/jar/"

 4. add Dependency jar files into "{ESB_HOME}/repository/component/lib"

 5. go to "{EJB_HOME}/ejb2-connector/ejb2-connector-1.0.9/org.wso2.carbon.connector" and type "mvn clean install" to test and build
