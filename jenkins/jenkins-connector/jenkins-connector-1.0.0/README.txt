Product: Integration tests for WSO2 ESB Jenkins connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1


Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0
 - Java 1.7
 - Jenkins 1.626

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by navigating to the following URL: 
    http://wso2.com/products/enterprise-service-bus/# 
 
 2. Deploy relevant patches, if applicable. Place the patch files into location "<ESB_HOME>/repository/components/patches".
 
 3. Download Jenkins.war file(Use Jenkins 1.626 version) by navigating to the following URL
    http://mirrors.jenkins-ci.org/war/1.626/
 
 4. Deploy the downloaded.war file using bellow command
      (java -jar jenkins.war --argumentsRealm.passwd.jenkins=test --argumentsRealm.roles.jenkins=admin)

 5. Follow these steps as pre-requisites for the integration
   i)    StartUp Server using "http://localhost:8080" and click on the Credential Link on the top left corner and from the page .
   ii)   On the Credential page, select the option "Global credentials" and create the username , password for further use. 
   iii)  Create a new job by navigating to the section 'New Item'. Then build the created job.
   iv)   Create two .xml files which contains the configuration details to create the job and to updated the job. Add the created files to the following location.
          <JENKINS_CONNECTOR_HOME>/jenkins-connector/jenkins-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/config/resources/jenkins.
         Note : Make sure to include 'id' as a parameter in the two .xml  files.
   
 6. Compress ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".

 7. Make sure that jenkins is specified as a module in ESB_Connector_Parent pom.
    <module>jenkins/jenkins-connector/jenkins-connector-1.0.0/org.wso2.carbon.connector</module>

 8. Update the Jenkins properties file at location "<JENKINS_CONNECTOR_HOME>/jenkins-connector/jenkins-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
      
   i)    apiUrl                     -  The API URL of Jenkins(e.g. http://localhost).
   ii)   username                   -  Use the username obtained under step 5 ii).
   iii)  password                   -  Use the password obtained under step 5 ii).
   iv)   jobName                    -  Text to be used as 'name' while creating a job with mandatory parameters.
   v)    uploadJobName              -  Text to be used as 'name' while creating a job by uploading the configuration file.
   vi)   jobDescription             -  Text to be used as 'description' while creating a job with mandatory parameters.
   Vii)  jobFileName                -  Use the name of the file with the extention which is created for job creation under step 5 iv). (E.g createjob.xml).
   viii) updatedJobFileName         -  Use the name of the file with the extention which is created for job updation under step 5 iv). (E.g createjob.xml).
   ix)   keepDependencies           -  Text to be used as 'keepDependencies' while creating a job with mandatory parameters. (Make sure to use a boolean value).
   x)    updatedJobDescription      -  Text to be used as 'description' while updating a job with mandatory parameters.
   xi)   updatedKeepDependencies    -  Text to be used as 'keepDependencies' while updating a job with mandatory parameters. (Make sure to use a boolean value).
   xii)  buildJobName               -  Use the name of the job which is created under step 5 iii).
   xiii) buildValue                 -  Use a predefined end point value to get build details. (Make sure to use 'lastBuild' or 'lastCompleted').
   xiv)  responseType               -  Text to be used as the 'responseType' to determine the response format of 'getJob','getBuildDetails' and 'listJobs' methods.(Make sure to use the value 'xml' as the responseType).
   xv)   tree                       -  Text to be used as the value of the query parameter while retrieving the build details.(Make sure to use 'duration' as the value).
   xvi)  buildParam                 -  Text to be used as the 'parameter' while building the job with parameters. (Make sure to use 'id' as the parameter).
   xvii) buildParamValue            -  Text to be used as the value for the parameter 'id' while building the job with parameters.
   
   
 9. Navigate to "{ESB_CONNECTOR_HOME}/" and run the following command.
      $ mvn clean install
