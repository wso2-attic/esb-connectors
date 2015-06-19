Product: Integration tests for WSO2 ESB Sirportly connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1/4.9.0-SNAPSHOT

Steps to follow in setting integration test.

 1. Download ESB 4.8.1 from official website.

 2. Deploy relevant patches, if applicable.
 
 3. Compress the modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "<Sirportly_Connector_Home>/sirportly-connector/sirportly-connector-1.0.0/org.wso2.carbon.connector/repository/".

 4. Create a Sirportly trial account and derive the API Token and Secret.
   i)    Using the URL "https://sirportly.com/start" create a Sirportly trial account and note the subdomain used in the registration.
   ii)   Login to the created Sirportly account and go to https://{subdomain}.sirportly.com/admin/api_tokens and add a new Api token.Click the Edit button of the created Api Token and obtain the Token and Secret. 

 5. Prerequisites for Sirportly Connector Integration Testing

   i)   Navigate to the URL "https://{subdomain}.sirportly.com/admin/departments", obtain the IDs of default departments(By hovering on top of the department name, the department id will be appear at the bottom of the page).
   ii)  Navigate to the URL "https://{subdomain}.sirportly.com/admin/teams", obtain the ID of default team(By hovering on top of the team name, the team id will be appear at the bottom of the page.).
   iii) Navigate to the URL "https://{subdomain}.sirportly.com/admin/brands", obtain the ID of default brand(By hovering on top of the brand name, the brand id will be appear at the bottom of the page.).
   iv)  Navigate to the URL "https://{subdomain}.sirportly.com/admin/filters", create a new filter as below and obtain the ID of that filter(By hovering on top of the filter name, the filter id will be appear at the bottom of the page).
			- Ticket 'Priority' is the priority value which you give under Step 7 (vi).
			- Ticket 'Status' is the ticketStatus value which you give under Step 7 (xi).
   v)   Navigate to the URL "https://{subdomain}.sirportly.com/admin/statuses", obtain an ID of default status(By hovering on top of the status name, the status id will be appear at the bottom of the page.).
   vi)  Navigate to the URL "https://{subdomain}.sirportly.com/admin/priorities", obtain the IDs of default priorities(By hovering on top of the priority name, the priority id will be appear at the bottom of the page.).
   vii) Navigate to the URL "https://{subdomain}.sirportly.com/admin/users", create two new user and obtain the IDs of those users(By hovering on top of the user name, the user id will be appear at the bottom of the page.).

 6. Follow the below mentioned steps to add valid certificate to access Sirportly API over https.

    i)   Extract the certificate from browser(Mozilla Firefox) by navigating to 'https://{account-name}.sirportly.com' 
    ii)  Go to new ESB 4.8.1 folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and 
         "<Sirportly_Connector_Home>/sirportly-connector/sirportly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
    iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

                keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

         This command will import Sirportly certificate into keystore. 
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Sirportly with the extension. (e.g. sirportly.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. sirportly)

    iv) Navigate to "<Sirportly_Connector_Home>/sirportly-connector/sirportly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

                keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 

         This command will import Sirportly certificate in to keystore. Give "wso2carbon" as password.
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Sirportly with the extension. (e.g. sirportly.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. sirportly).

 7. Update the Sirportly properties file at location "<Sirportly_Connector_Home>/sirportly-connector/sirportly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)     apiUrl                 - Use https://{subdomain}.sirportly.com.
   ii)    apiToken               - Use the API Token obtained under Step 4 ii).
   iii)   apiSecret              - Use the API Secret obtained under Step 4 ii).
   iv)    name                   - Use a valid string value.
   v)     contactReference       - Use a unique valid string value as contact reference.
   vi)    priority               - Use "Low", "High", "Urgent" or "Normal" as the value.
   vii)   email                  - Use any email address which is correctly formatted.
   viii)  contactType            - Use "email" as the value of this property.
   ix)    userId                 - Use a user Id obtained under step 5 vii).
   x)     ticketSubject          - Use a valid string as the ticket subject.
   xi)    ticketStatus           - Use "New", "Resolved", "Waiting for Contact" or "Waiting for Staff" as the value.
   xii)   department             - Use a department Id obtained under step 5 i).
   xiii)  contactName            - Use a valid String value.
   xiv)   team                   - Use the team Id obtained under step 5 ii).
   xv)    brand                  - Use the brand Id obtained under step 5 iii).
   xvi)   filterId               - Use the filter Id obtained under step 5 iv).
   xvii)  ticketStatusId         - Use a ticket status Id obtained under step 5 v).
   xiii)  ticketPriorityId       - Use the priority Id that have been given in property vi) obtained under step 5 vi).
   xix)   updateTicketSubject    - Use a valid string value.
   xx)    updateTicketPriority   - Use "Low", "High", "Urgent" or "Normal" as the value (Should be differed with the value in vi.).
   xxi)   updateDepartment       - Use a department Id obtained under step 5 i) (Should be differed with the value in xii.).
   xxii)  updateStatus           - Use "New", "Resolved", "Waiting for Contact" or "Waiting for Staff" as the value (Should be differed with the value in xi).
   xxiii) updateAssignedUser     - Use a user Id obtained under step 5 vii) (Should be differed with the value in xv.).
   xxiv)  contentMessage         - Use a valid String value.
        
   Note - The property values of contactReference and email should be changed to unique different values for each integration execution.  

 8.Navigate to "<Sirportly_Connector_Home>/sirportly-connector/sirportly-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
