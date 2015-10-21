Product: Integration tests for WSO2 ESB connector

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

 1. Download ESB 4.9.0 by navigating to the following URL: 
    http://wso2.com/products/enterprise-service-bus/#
 
 2. Deploy relevant patches, if applicable. Place the patch files into location "<ESB_HOME>/repository/components/patches".
 
 3. Navigate to below mentioned URL and create an account in SalesforceDesk.
      https://reg.desk.com/users/new
      
 4. If required add the X.509 certificate from https://{DOMAIN_NAME}.desk.com  to the client-truststore.jks of the ESB located in "<ESB_HOME>/repository/resources/security" directory
    and wso2carbon.jks located in "<SALESFORCEDESK_CONNECTOR_HOME>/salesforcedesk-connector/salesforcedesk-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products".
      
 5. Prerequisites for SalesforceDesk Connector Integration Testing.
      i) Create a new custom field for Customers by navigating to the following link.
         https://{DOMAIN_NAME}.desk.com/admin/case-management/customer-settings/custom-fields.

      ii) Add at least two users for the account by navigating to the following link.
          https://{DOMAIN_NAME}.desk.com/admin/team/users
   
      iii) Navigate to the following link and add at least two new email cases by providing a customer and a company for the case. Obtain the customer ID and the company ID.
           https://{DOMAIN_NAME}.desk.com/agent
      
      iv) Create an article with attachment and obtain the article ID and attachment ID from browser URLs by clicking article and attachment. Use the following URL to get instructions to create an article with attachment.
          https://support.desk.com/customer/portal/articles/1572-creating-multi-channel-knowledge-base-articles.
      
      v) Navigate to 'https://wso2con.desk.com/agent#' to create a new email type case with attachment, anding with values for 'to', 'cc' or 'bcc' parameters. And obtain the ID of the case.
         To obtain the attachment ID, mouse-over the attachment file and obtain form pop-up navigating URL.

 6. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "<ESB_CONNECTOR_HOME>/repository/".
 
 7. Make sure that SalesforceDesk is specified as a module in ESB Connector Parent pom.

    <module>salesforcedesk/salesforcedesk-connector/salesforcedesk-connector-1.0.0/org.wso2.carbon.connector</module>
 
 8. Update the SalesforceDesk properties file at location "<SALESFORCEDESK_CONNECTOR_HOME>/salesforcedesk-connector/salesforcedesk-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)      apiUrl                   -  Use the following value, https://{DOMAIN_NAME}.desk.com
   ii)     password                 -  The password of the account.
   iii)    email                    -  The email address of the account.
   iv)     caseMessageTo            -  Valid email address to create case.    
   v)      caseNotesText            -  String value to set case note.
   vii)    caseBody                 -  String value to set case body.
   viii)   caseCreatedAt            -  Date value to set case created date (Format should be in yyyy-MM-ddThh:mm:ssZ).
   ix)     caseUpdatedAt            -  Date value to set case updated date (Format should be in yyyy-MM-ddThh:mm:ssZ).
   x)      caseDirection            -  The case direction. The value should be set as "in". 
   xi)     caseStatus               -  Status of the case. Status should be set as "draft".
   xii)    caseMessageSubjectMand   -  String value to set case message subject.
   xiii)   caseMessageBodyMand      -  String value to set case message body.
   xiv)    caseMessageToMand        -  Valid email to set case message to email.
   xv)     casePriority             -  Integer value to set case priority (The priority should be in 1 to 10 range).
   xvi)    caseExternalId           -  Unique integer value to set case external ID.
   xvii)   caseLanguage             -  ISO language code to set case language (e.g.:en).
   xviii)  caseDescription          -  String value to set case description.
   xix)    caseLockedUntil          -  Date value to set case locked until date (Format should be in yyyy-MM-ddThh:mm:ssZ).
   xx)     caseIdWithAttachment     -  Case ID which obtained in step 5 v).
   xxi)    caseAttachmentId         -  Case attachment ID which obtained in step 5 v).
   xxii)   caseCustomerId           -  The customer ID obtained in step 5)iii.
   xxiii)  caseCompanyId            -  The company ID obtained in step 5)iii.
   xxiv)   articleSubject           -  String value to set article subject.
   xxv)    articleBodyOpt           -  String value to set as article body.
   xxvi)   articleQuickCodeOpt      -  Unique String value to set article quick code.
   xxvii)  articleKeywordsOpt       -  String value to set article keyword.
   xxviii) articlePublishAtOpt      -  Date value to set article published date (Format should be in yyyy-MM-ddThh:mm:ssZ).
   xxix)   articleNotesOpt          -  String value to set article note.
   xxx)    articleIdWithAttachment  -  Article ID which obtained in step 5 iv).
   xxxi)   articleAttachmentId      -  Article attachment ID which obtained in step 5 iv).
   xxxii)  companyNameMand          -  Provide any string value. 
   xxxiii) companyNameOpt           -  Provide a string value. (Should be different from the value given in 8 xxxii).
   xxxiv)  companyCustmField        -  Use the name of the custom field created in step 5)i as the value. 
   xxxv)   companyCustmFieldVal     -  Provide a value according to the data type selected when creating the custom field in step 5)i.
   xxxvi)  companyDomain            -  Valid Sting domain name to set company domain (e.g.:abcinc.com).
   xxxvii) topicNameMand            -  String value to set topic name with mandatory parameters.
   xxxviii)topicNameOpt             -  String value to set topic name with optional parameters.
   xxxix)  topicDescription         -  String value to set topic description.
   xl)     topicAllowQuestions      -  Boolean value to set topic allow questions.
   xli)    custFirstName            -  String value to set customer first name.    
   xlii)   custLastName             -  String value to set customer last name. 
   xliii)  custExternalId           -  Unique integer value to set customer external ID.
   xliv)   custLockedUntil          -  Date value to set customer locked until date (Format should be in yyyy-MM-ddThh:mm:ssZ).
   xlv)    custAccessPrivatePortal  -  Boolean value to set to access customer private portal.
                            
    NOTE: The property values of 'articleQuickCodeOpt', 'caseExternalId', 'companyNameMand' and 'companyNameOpt' should be changed to unique different values for each integration execution.  

 9. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
     $ mvn clean install
