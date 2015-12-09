Product: Integration tests for WSO2 ESB Jira connector

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

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by navigating to the following URL: http://wso2.com/products/enterprise-service-bus/#
 
 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches. 

 3. Create a Jira account using URL https://id.atlassian.com/signup?application=mac&tenant=&continue=https%3A%2F%2Fmy.atlassian.com by giving required values for relevant fields.
    Navigate to "Atlassin Home" and select "JIRA Software" under the products. Start a new JIRA Software account for free by providing necessary details.
    Keep the username, password, domain of the account for further reference.
   
 4.Prerequisites for Jira Connector Integration Testing.  
    Log in to the account created in step 3 and follow the below steps.
   i)   Create a project. Keep the project key for further reference.
   ii)  Navigate to the link https://{domain}.atlassian.net/admin/users?referrerUrl=https%3A%2F%2Fapp-tests.atlassian.net%2Fissues%2F%3Fjql%3D&referrerName=JIRA
        and add two users. Keep the usernames for further reference.
   iii) Create three issues under the project created in step 4 i). Add one of the users (created in step 4 ii), as a watcher to one of the issues created. Keep the issue keys and watcher username for further reference.
   iv)  Add a comment to one of the issues created in step 4 ii). Right click on the embedded URL icon which is appeared on the comment section and copy link address to obtain the comment id. Keep the comment id for further reference.
   v)   Create an issue and add an attachment to it. Keep the issue key. Hover the mouse point over the attachment and obtain the attachment id for further reference.
   vi)  Create an optional user for the account and verify it, keep the username, password for further reference.
   vii) Navigate to https://{domain}.atlassian.net/secure/admin/ViewIssueTypes.jspa. Obtain any issue type id which is applicable for the project created in step(i).

 5. Follow the below mentioned steps for adding valid certificate to access Jira API over https.
   i)  Extract the certificate from browser(Mozilla Firefox) by navigating to https://{domain}.atlassian.net
   ii) Add the certificate obtained in step 5 i)to the client-truststore.jks of the ESB located in "<ESB_HOME>/repository/resources/security" folder and wso2carbon.jks located in "{JIRA_CONNECTOR_HOME}/jira-connector/jira-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products".
   
 6. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml".
 
	   Message Formatters :
           <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
       
      Message Builders :   
            <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
       
 6. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository".
 
 7. Make sure that Jira is specified as a module in ESB Connector Parent pom.

    <module>jira/jira-connector/jira-connector-1.0.0/org.wso2.carbon.connector</module>
 
 8. Update the jira properties file at location "{JIRA_CONNECTOR_HOME}/jira-connector/jira-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
   i)      apiUrl                      - Use the API URL as https://{domain}.atlassian.net
   ii)     username                    - Use the username obtained in step 3.
   iii)    password                    - Use the password obtained in step 3.
   iv)     issueIdWithWatchList        - Use the key of the issue which has a watcher created in step 4(iii).
   v)      issueWatcher                - Use the username of the watcher obtained in step 4(ii).
   vi)     issueLinkType               - Use a valid string value for issue link type. e.g.: Duplicate
   vii)    inwardIssueKey              - Use a key of an issue created in step 4(iii).
   viii)   outwardIssueKey             - Use a key of an issue which was not used in step 8(vii), created in step 4(ii).
   ix)     summary                     - Use a string value as the summary.
   x)      issueTypeId                 - Use the issue type id obtained in step 4(vii).
   xi)     issueIdOrKey                - Use the issue key which has a comment on it. Mentioned in step 4(iv).
   xii)    commentId                   - Use the comment id obtained in step 4(iv).
   xiii)   expand                      - Use a valid string value for expand. e.g.:renderedBody
   xiv)    assigneeName                - Use the username of a user created in step 4(ii).
   xv)     notifyUser                  - Use the username of a user created in step 4(ii).
   xvi)    notificationSubject         - Use a string value as the notification subject.
   xvii)   reporterName                - Use the username of a user created in step 4(ii).
   xviii)  description                 - Use a string value as the description.
   xix)    label                       - Use a string value as the label.
   xx)     summaryOptional             - Use a string value as the summary. Should be different than the value given in step 8 x).
   xxi)    componentNameMandatory      - Use a string value as the name of the component.
   xxii)   componentNameOptional       - Use a string value as the name of the component.
   xxiii)  updatedComponentName        - Use a string value as the name of the component.
   xxiv)   project                     - Use the key of the project obtained in step 4(i).
   xxv)    componentDescription        - Use a string value as the description of the component.
   xxvi)   leadUserName                - Use the username of a user created in step 4(ii).
   xxvii)  assigneeType                - Use 'PROJECT_LEAD' as the value.
   xxviii) updatedComponentDescription - Use a string value as the description of the component.
   xxix)   updatedLeadUserName         - Use the username of a user created in step 4(ii) which was not used in step 8(xxvi).
   xxx)    updatedAssigneeType         - Use 'PROJECT_DEFAULT' as the value.
   xxxi)   attachmentId                - Use the attachment id obtained in step 4(v).
   xxxii)  issueId                     - Use the issue key obtained in step 4(v).
   xxxiii) optionalUsername            - Use the username obtained in step 4(vi).
   xxxiv)  optionalPassword            - Use the password obtained in step 4(vi).
   
      NOTE : issueIdWithWatchList,issueWatcher,inwardIssueKey,outwardIssueKey,componentNameMandatory,componentNameOptional,updatedComponentName,attachmentId and issueId should be changed before running the integration test each time. 

 10. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
 
    $ mvn clean install
