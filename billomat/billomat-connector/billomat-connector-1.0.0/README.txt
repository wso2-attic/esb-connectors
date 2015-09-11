Product: Integration tests for WSO2 ESB Billomat connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-BETA-SNAPSHOT by following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2.   Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
 
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
      <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
      <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

      <messageFormatter contentType="application/pdf" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
      <messageBuilder contentType="application/pdf" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
 4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".
 
 5. Make sure that Billomat is specified as a module in ESB Connector Parent pom.
        <module>billomat/billomat-connector/billomat-connector-1.0.0/org.wso2.carbon.connector</module>
   
 6. Create a Billomat trial account and derive the API Token.
   i)     Using the URL "https://www.billomat.net" create a Billomat trial account.
   ii)    Obtain the api key for the created account in 6(i) as instructed in http://www.billomat.com/en/api/basics/authentication.
   
 7. Update the billomat properties file at location "<BILLOMAT_CONNECTOR_HOME>/billomat-connector/billomat-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
   i)       apiUrl                     -  The API URL specific to the created Billomat account (E.g. https://besafe.billomat.net).
   ii)      apiKey                     -  Use the api key obtained under step 6 (ii).
   
   iii)     clientName                 -  Text to be used as 'name' while creating a client with optional parameters.
   iv)      clientSalutation           -  Text to be used as 'salutation' while creating a client with optional parameters (E.g. Mr).
   v)       clientNumberPre            -  Text to be used as 'number_pre' while creating a client with optional parameters.
   vi)      clientNumber               -  Numerical value to be used as 'number' while creating a client with optional parameters.
   vii)     clientFirstName            -  Text to be used as 'first_name' while creating a client with optional parameters.
   viii)    clientLastName             -  Text to be used as 'last_name' while creating a client with optional parameters.
   ix)      updatedClientName          -  Text to be used as 'name' while updating a client with optional parameters.
   x)       updatedClientSalutation    -  Text to be used as 'salutation' while updating a client with optional parameters (E.g. Miss).
   xi)      updatedClientNumberPre     -  Text to be used as 'number_pre' while updating a client with optional parameters.
   xii)     updatedClientFirstName     -  Text to be used as 'first_name' while updating a client with optional parameters.
   xiii)    updatedClientLastName      -  Text to be used as 'last_name' while updating a client with optional parameters.
   
   xiv)     contactName                -  Text to be used as 'name' while creating a contact with optional parameters.
   xv)      contactSalutation          -  Text to be used as 'salutation' while creating a contact with optional parameters.
   xvi)     contactFirstName           -  Text to be used as 'first_name' while creating a contact with optional parameters.
   xvii)    contactCity                -  Text to be used as 'city' while creating a contact with optional parameters.
   xviii)   contactEmail               -  Text to be used as 'email' while creating a contact with optional parameters.
   xix)     updatedContactName         -  Text to be used as 'name' while updating a contact with optional parameters.
   xx)      updatedContactSalutation   -  Text to be used as 'salutation' while updating a contact with optional parameters.
   xxi)     updatedContactFirstName    -  Text to be used as 'first_name' while updating a contact with optional parameters.
   xxii)    updatedContactEmail        -  Text to be used as 'email' while updating a contact with optional parameters.
   xxiii)   updateContactCity          -  Text to be used as 'city' while updating a contact with optional parameters.
   
   xxiv)    invoiceTitle               -  Text to be used as 'title' while creating an invoice with optional parameters.
   xxv)     invoiceDate                -  Text to be used as 'date' while creating an invoice with optional parameters in the format of 'yyyy-mm-dd' (e.g. 2015-05-30).
   xxvi)    invoiceNumber              -  Numerical value to be used as 'number' while creating an invoice with optional parameters.
   xxvii)   invoiceNumberPre           -  Text to be used as 'number_pre' while creating an invoice with optional parameters.
   xxviii)  invoiceStatus              -  Text to be used as 'status' while creating an invoice with optional parameters.(Make sure to use the value 'DRAFT' as the status).
   xxix     invoiceDiscountDate        -  Text to be used as 'discount_date' while creating an invoice with optional parameters in the format of 'yyyy-mm-dd' (e.g. 2015-05-30).
   
   xxx)     invoiceItemTitle           -  Text to be used as 'title' while creating an invoice item with optional parameters.
   xxxi)    invoiceItemDescription     -  Text to be used as 'description' while creating an invoice item with optional parameters.
   xxxii)   invoiceItemUnit            -  Text to be used as 'title' while creating an invoice item with optional parameters (e.g. piece).
   xxxiii)  invoiceItemUnitPrice       -  Numerical value to be used as 'unit_price' while creating an invoice item with optional parameters.
   xxxiv)   invoiceItemQuantity        -  Numerical value to be used as 'quantity' while creating an invoice item with optional parameters.
   
   xxxv)    page                       -  Numerical value to be used as 'page' while listing clients/contacts/invoices/invoice-items with optional parameters.
   xxxvi)   clientPerPage              -  Numerical value to be used as 'per_page' while listing contacts/invoices/invoice-items with optional parameters.
   
   xxxvii)  deliveryNoteTitle          -  Text to be used as 'title' while creating a delivery note with optional parameters.
   xxxviii) deliveryNoteDate           -  Text to be used as 'date' while creating a  delivery note with optional parameters in the format of 'yyyy-mm-dd' (e.g. 2015-05-30).
   xxxix)   deliveryNoteNumber         -  Numerical value to be used as 'number' while creating a delivery note with optional parameters.
   xL)      deliveryNoteNumberPre      -  Text to be used as 'number_pre' while creating a delivery note with optional parameters.
   xLi)     deliveryNoteStatus         -  Text to be used as 'status' while creating a delivery note with optional parameters.(Make sure to use the value 'DRAFT' as the status).
   
   
 8. Navigate to "{ESB_Connector_Home}/" and run the following command.
         $ mvn clean install


