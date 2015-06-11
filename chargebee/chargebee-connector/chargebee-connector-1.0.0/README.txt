Product: Integration tests for WSO2 ESB ChargeBee connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
         https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-ALPHA

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/

 2. Copy that zip file in to location "<ESB_CONNECTORS_HOME>/repository/".

 3. Create a ChargeBee trial account and derive the API Key.
   i)    Using the URL "http://www.chargebee.com/" create a ChargeBee trial account.
   ii)   Claim the domain by completing the registration by using the mail which is sent to the mail account used in step 3 i) and note the domain used in the registration. 
   iii)  Login to the created ChargeBee account and go to https://{The created domain}.chargebee.com/api and get the Api Key from there.

 4. Update the ChargeBee properties file at location "<CHARGEBEE_CONNECTOR_HOME>/chargebee-connector/chargebee-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)     apiUrl                 - The API URL specific to the domain of the created account. e.g. https://soft-test.chargebee.com
   ii)    apiKey                 - Use the API Key obtained under Step 3 iii).
   iii)   discountAmount         - Use an integer value as a discount amount in coupon.
   iv)    durationType           - Use a valid discount type of a coupon as documented in the API documentation
   v)     invoiceNameOpt         - Use a valid string value as invoice name.
   vi)    invoiceNotesOpt        - Use a valid string value as invoice notes.
   vii)   validTill              - Use a valid future UTC timestamp in seconds.
   viii)  maxRedemptions         - Use an integer value.
   ix)    paidOnAfter            - Use a valid past UTC timestamp in seconds.
   x)     companyName            - Use a valid string value as company name.
   xi)    email                  - Use any email address which is correctly formatted.
   xii)   firstName              - Use a valid string value as customer's first name.
   xiii)  lastName               - Use a valid string value as customer's last name.
   xiv)   notes                  - Use a valid string value as note's content.
   xv)    firstNameUpdated       - Use a valid string value as customer's first name.
   xvi)   lastNameUpdated        - Use a valid string value as customer's last name.
   xvii)  notesUpdated           - Use a valid string value as note's content.
   xviii) emailUpdated           - Use any email address which is correctly formatted.
   xix)   eventOccurredAfter     - Use a valid past UTC timestamp in seconds.
   xx)    eventOccurredBefore    - Use a valid future UTC timestamp in seconds.
        Note - There should be at least a difference of 10 minutes between eventOccurredAfter value and eventOccurredBefore value.   

 5. Make sure that the chargebee connector is set as a module in esb-connectors parent pom.
      <module>chargebee/chargebee-connector/chargebee-connector-1.0.0/org.wso2.carbon.connector</module>

 6. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
          $ mvn clean install
