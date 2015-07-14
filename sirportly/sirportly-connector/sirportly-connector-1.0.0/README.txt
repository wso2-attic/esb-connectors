Product: Integration tests for WSO2 ESB Sirportly connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base 

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-BETA-SNAPSHOT by navigating to the following URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2.	Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.

 3. Create a Sirportly trial account and derive the API Token and Secret.
   i)    Using the URL "https://sirportly.com/start" create a Sirportly trial account and note the subdomain used in the registration.
   ii)   Login to the created Sirportly account and go to https://{subdomain}.sirportly.com/admin/api_tokens and add a new Api token.Click the Edit button of the created Api Token and obtain the Token and Secret. 
  
 4. Follow the below mentioned steps to add valid certificate to access Sirportly API over https.

    i)   Extract the certificate from browser(Mozilla Firefox) by navigating to 'https://{subdomain}.sirportly.com' 
    ii)  Go to new ESB 4.9.0 folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and 
         "<SIRPORTLY_CONNECTOR_HOME>/sirportly-connector/sirportly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
    iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

                keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

         This command will import Sirportly certificate into keystore. 
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Sirportly with the extension. (e.g. sirportly.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. sirportly)

    iv) Navigate to "<SIRPORTLY_CONNECTOR_HOME>/sirportly-connector/sirportly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

                keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 

         This command will import Sirportly certificate in to keystore. Give "wso2carbon" as password.
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Sirportly with the extension. (e.g. sirportly.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. sirportly).

 5. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".

 6. Make sure that sirportly is specified as a module in ESB Connector Parent pom.
        <module>sirportly/sirportly-connector/sirportly-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Prerequisites for Sirportly Connector Integration Testing

   i)   Navigate to the URL "https://{subdomain}.sirportly.com/admin/departments", obtain the IDs of default departments(By hovering on top of the department name, the department id will be appear at the bottom of the page).
   ii)  Navigate to the URL "https://{subdomain}.sirportly.com/admin/teams", obtain the ID of default team(By hovering on top of the team name, the team id will be appear at the bottom of the page.).
   iii) Navigate to the URL "https://{subdomain}.sirportly.com/admin/brands", obtain the ID of default brand(By hovering on top of the brand name, the brand id will be appear at the bottom of the page.).
   iv)  Navigate to the URL "https://{subdomain}.sirportly.com/admin/filters", create a new filter as below and obtain the ID of that filter(By hovering on top of the filter name, the filter id will be appear at the bottom of the page).
			- Ticket 'Priority' is the priority value which you give under Step 8 (vi).
			- Ticket 'Status' is the ticketStatus value which you give under Step 8 (xi).
   v)   Navigate to the URL "https://{subdomain}.sirportly.com/admin/statuses", obtain an ID of default status(By hovering on top of the status name, the status id will be appear at the bottom of the page.).
   vi)  Navigate to the URL "https://{subdomain}.sirportly.com/admin/priorities", obtain the IDs of default priorities(By hovering on top of the priority name, the priority id will be appear at the bottom of the page.).
   vii) Navigate to the URL "https://{subdomain}.sirportly.com/admin/users", create two new users and obtain the IDs of those users(By hovering on top of the user name, the user id will be appear at the bottom of the page.). Make sure that both of these users are members of the team which is created under 7 ii).
   
   Note: - It is essential that both the Departments (Step 7 i) should belong to the same Brand (Step 7 iii) that is considered for the integration test.
		 - Both the Users that are created for the integration (Step 7 vii) must belong to the same Team (Step 7 ii) that is considered for the integration.	 

 8. Update the Sirportly properties file at location "<SIRPORTLY_CONNECTOR_HOME>/sirportly-connector/sirportly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)     apiUrl                 - Use https://{subdomain}.sirportly.com.
   ii)    apiToken               - Use the API Token obtained under Step 3 ii).
   iii)   apiSecret              - Use the API Secret obtained under Step 3 ii).
   iv)    name                   - Use a valid string value.
   v)     contactReference       - Use a unique valid string value as contact reference.
   vi)    priority               - Use "Low", "High", "Urgent" or "Normal" as the value.
   vii)   email                  - Use any email address which is correctly formatted.
   viii)  contactType            - Use "email" as the value of this property.
   ix)    userId                 - Use a user Id obtained under step 7 vii).
   x)     ticketSubject          - Use a valid string as the ticket subject.
   xi)    ticketStatus           - Use "New", "Resolved", "Waiting for Contact" or "Waiting for Staff" as the value.
   xii)   department             - Use a department Id obtained under step 7 i).
   xiii)  contactName            - Use a valid String value.
   xiv)   team                   - Use the team Id obtained under step 7 ii).
   xv)    brand                  - Use the brand Id obtained under step 7 iii).
   xvi)   filterId               - Use the filter Id obtained under step 7 iv).
   xvii)  ticketStatusId         - Use a ticket status Id obtained under step 7 v).
   xiii)  ticketPriorityId       - Use the priority Id that have been given in property vi) obtained under step 7 vi).
   xix)   updateTicketSubject    - Use a valid string value.
   xx)    updateTicketPriority   - Use "Low", "High", "Urgent" or "Normal" as the value (Should be differed with the value in vi.).
   xxi)   updateDepartment       - Use a department Id obtained under step 7 i) (Should be differed with the value in xii.).
   xxii)  updateStatus           - Use "New", "Resolved", "Waiting for Contact" or "Waiting for Staff" as the value (Should be differed with the value in xi).
   xxiii) updateAssignedUser     - Use a user Id obtained under step 7 vii) (Should be differed with the value in ix).
   xxiv)  contentMessage         - Use a valid String value.
        
   Note - The property values of contactReference and email should be changed to unique different values for each integration execution.  

 9.Navigate to "<SIRPORTLY_CONNECTOR_HOME>/sirportly-connector/sirportly-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
