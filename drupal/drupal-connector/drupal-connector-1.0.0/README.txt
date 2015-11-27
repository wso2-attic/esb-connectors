Product: Integration tests for WSO2 ESB Drupal connector

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
 - Drupal 7.41

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by navigating to the following URL: 
    http://wso2.com/products/enterprise-service-bus/#
 
 2. Deploy relevant patches, if applicable. Place the patch files into location "<ESB_HOME>/repository/components/patches".
 
 3. Navigate to below mentioned URL and follow the guidelines to setup Drupal.
      https://www.drupal.org/documentation/install
      
 4. If required add the X.509 certificate from https://{DRUPAL_HOSTED_DOMAIN}  to the client-truststore.jks of the ESB located in "<ESB_HOME>/repository/resources/security" directory
    and wso2carbon.jks located in "<DRUPAL_CONNECTOR_HOME>/drupal-connector/drupal-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products".
      
 5. Prerequisites for Drupal Connector Integration Testing.
      i) Navigate to "http://{DRUPAL_HOSTED_DOMAIN}/#overlay-context=node&overlay=admin/people" and add a new user by clicking '+ Add user' link. Make sure to check roles 'authenticated user' and 'administrator' while adding a user. Keep the username and password for further reference.

      ii) Navigate to "http://{DRUPAL_HOSTED_DOMAIN}/#overlay=admin/modules" and install and enable the below modules by clicking "+ Install new module" link.
          
          http://ftp.drupal.org/files/projects/ctools-7.x-1.9.tar.gz
          http://ftp.drupal.org/files/projects/http_client-7.x-2.4.tar.gz
          http://ftp.drupal.org/files/projects/libraries-7.x-2.2.tar.gz
          http://ftp.drupal.org/files/projects/services-7.x-3.12.tar.gz
          http://ftp.drupal.org/files/projects/services_basic_auth-7.x-1.3.tar.gz
          http://ftp.drupal.org/files/projects/views-7.x-3.11.tar.gz
          
          Note: Make sure to install and enable the missing depended modules from drupal website.  
   
      iii) Navigate to "http://{DRUPAL_HOSTED_DOMAIN}/#overlay=admin/structure/services" and add a new service by clicking '+ Add' link. Make sure to select Server as 'REST' and check "HTTP basic authentication" while creating the service and keep the service path to endpoint for further reference.
      
      iv) Navigate to "http://{DRUPAL_HOSTED_DOMAIN}/#overlay=admin/structure/services" and click 'Edit Resources' in created service in step iii). Select all resources by checking the 'RESOURCE' checkbox and click 'Save' to apply changes. 
 
      v) Navigate to "http://{DRUPAL_HOSTED_DOMAIN}/#overlay-context=node&overlay=admin/structure/types/manage/article/fields" and add text type new field with a label. Keep the custom field machine name for further reference.
      
      vi) Navigate to "http://{DRUPAL_HOSTED_DOMAIN}/#overlay-context=node&overlay=admin/content" and add a new article type content by clicking '+ Add content' link and make sure to upload image while creating the content. Keep the content node ID (id displays in the URL when the network is selected) for further reference.
      
      vii) Navigate to "http://{DRUPAL_HOSTED_DOMAIN}/node#overlay=admin/structure/types/manage/article/comment/fields" and add text type new comment field with a label. Keep the custom field machine name for further reference.
 
 6. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "<ESB_CONNECTOR_HOME>/repository/".
 
 7. Make sure that Drupal is specified as a module in ESB Connector Parent pom.

    <module>drupal/drupal-connector/drupal-connector-1.0.0/org.wso2.carbon.connector</module>
 
 8. Update the Drupal properties file at location "<DRUPAL_CONNECTOR_HOME>/drupal-connector/drupal-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)       apiUrl                     - Combination of Drupal hosted domain and service path to endpoint created in step 5 iii). Use the following value, http://{DUPAL_HOSTED_DOMAIN}/{SERVICE_PATH_TO_ENDPOINT}
   ii)      userName                   - Use the created user's username in step 5 i).
   iii)     password                   - Use the created user's password in step 5 i).
   iv)      nodeTitleMand              - String value to set as node title to create node with mandatory parameters.
   v)       nodeTypeMand               - Value should set as 'article'.
   vi)      nodeTitleOpt               - String value to set as node title to create node with optional parameters.
   vii)     nodeTypeOpt                - Value should set as 'article'.
   viii)    nodeBodyValue              - String value to set node body value.
   ix)      nodeBodySummary            - String value to set node body summary.
   x)       nodeBodyFormat             - Valid node body format to create node with optional parameters (e.g.: filtered_html).
   xi)      nodeCustFieldLabel         - Use created custom field machine name in step 5 v).
   xii)     nodeCustFieldValue         - String value to set node custom field.
   xiii)    nodeComment                - Use 1 or 0 to enable node comments.
   xiv)     nodeBodyValueUpdate        - String value to update node body value (This value must be different than  the property viii) value).
   xv)      nodeTitleUpdate            - String value to update node tile (This value must be different than  the property vi) value).
   xvi)     nodeCustFieldValue         - String value to update node custom field (This value must be different than  the property xii) value).
   xvii)    nodeCommentUpdate          - Set 1 or 0 as the value (This value must be different than  the property xiii) value).
   xviii)   nodeIdWithAttachment       - Place the node ID created in step 5 vi).
   xix)     commentBodyValueMand       - String value to set comment body value.
   xx)      commentSubject             - String value to set comment subject.
   xxi)     commentCustFieldLabel      - Use created custom field machine name in step 5 vii).
   xxii)    commentCustFieldValue      - String value to set comment custom field.
   xxiii)   fileMand                   - Name of the file with extension to upload and create a file with mandatory parameters (e.g.: file1.png).
   xxiv)    fileNameMand               - Title for the uploaded file to store (e.g.: fileNameMand.png).
   xxv)     fileOpt                    - Name of the file with extension to upload and create a file with optional parameters (e.g.: file2.jpg).
   xxvi)    fileNameOpt                - Title for the uploaded file to store (e.g.: fileNameOpt.jpg).
   xxvii)   fileStatus                 - Set 1 or 0 as the value.


 9. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
     $ mvn clean install
