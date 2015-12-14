Product: Integration tests for WSO2 ESB Confluence connector

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
 - Confluence 5.9.1

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by navigating to the following URL: 
    http://wso2.com/products/enterprise-service-bus/#
 
 2. Deploy relevant patches, if applicable. Place the patch files into location "<ESB_HOME>/repository/components/patches".
 
 3. Navigate to "https://www.atlassian.com/software/confluence/download/" URL and download Confluence. Use the below URL to setup Confluence.
      https://confluence.atlassian.com/doc/confluence-installation-guide-135681.html
      
 4. If required add the X.509 certificate from "https://confluence.atlassian.com" to the client-truststore.jks of the ESB located in "<ESB_HOME>/repository/resources/security" directory
    and wso2carbon.jks located in "<CONFLUENCE_CONNECTOR_HOME>/confluence-connector/confluence-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products".
      
 5. Prerequisites for Confluence Connector Integration Testing.
      i) Add a new user to Confluence account by using the below URL instructions. Keep the username and password for further reference.
         https://confluence.atlassian.com/doc/add-users-and-set-permissions-349635148.html
      
      ii) Navigate to 'Spaces> Create space' in confluence dashboard and create a new space. Keep the Space key for further reference.
      
      iii) Click the 'Create' button and create a new page with page title to the same space created in step 5 ii). Keep the page title for further reference.
      
      iv) Click the 'Create' button and create a new page with page title to the same space created in step 5 ii). Keep the page ID (content ID) title for further reference.
      
      v)  Click the 'Create' button and create a new page with page title to the same space created in step 5 ii). And add at least two child pages to the parent page. Keep the parent page ID (content ID) title for further reference.

      vi) Click the 'Create' button and create a new page with page title to the same space created in step 5 ii). And add at least two comments to created page. Keep the page ID (content ID) title for further reference.
 
      vii) Click the 'Create' button and create a new page with page title and file attachment to the same space created in step 5 ii). And add at least two comments to created page. Keep the page ID (content ID) title for further reference.
 
      NOTE: To obtian page ID (content ID), click page edit link and get page ID from browser URL.
 
 6. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "<ESB_CONNECTOR_HOME>/repository/".
 
 7. Make sure that Confluence is specified as a module in ESB Connector Parent pom.

    <module>confluence/confluence-connector/confluence-connector-2.0.0/org.wso2.carbon.connector</module>
 
 8. Update the Confluence properties file at location "<CONFLUENCE_CONNECTOR_HOME>/confluence-connector/confluence-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)    apiUrl                     - Confluence account hosted domain in step 3 (e.g.: http://{CONFLUENCE_HOSTED_DOMAIN}). 
   ii)   username                   - Use the created user's username in step 5 i).
   iii)  password                   - Use the created user's password in step 5 i).
   iv)   spaceKeyMand               - Use the space key created in step 5 ii).
   v)    contentTitleOpt            - Use the created page title in step 5 iii).
   vii)  contentTypeOpt             - Set the content type as 'page'.
   viii) contentIdToUpdate          - Use the page ID (content ID) created in step 5 iv).
   ix)   contentIdWithChildContents - Use the page ID (content ID) created in step 5 v).
   x)    contentIdWithComments      - Use the page ID (content ID) created in step 5 vi).
   xi)   contentIdWithAttachments   - Use the page ID (content ID) created in step 5 vii).
   xii)  spaceNameMand              - String value to set space name with mandatory parameters.
   xiii) spaceNameOpt               - String value to set space name with optional parameters.
   xiv)  spaceDescription           - String value to set space description.
   xv)   spaceDescRepresentation    - String value to set space description representation.

 9. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
     $ mvn clean install
