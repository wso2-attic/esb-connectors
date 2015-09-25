Product: Integration tests for WSO2 ESB Vend connector

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
      If required add the X.509 certificate from https://{domain-name}.vendhq.com to the client-truststore.jks of the ESB located in <ESB_HOME>/repository/resources/security folder
      and wso2carbon.jks located in <VEND_CONNECTOR_HOME>/vend-connector/vend-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products.
 
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
      <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
      <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

      <messageFormatter contentType="application/pdf" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
      <messageBuilder contentType="application/pdf" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
 4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "<ESB_CONNECTOR_HOME>/repository/".
 
 5. Make sure that vend is specified as a module in ESB Connector Parent pom.
        <module>vend/vend-connector/vend-connector-1.0.0/org.wso2.carbon.connector</module>
   
 6. Create a Vend trial account and derive the API Token.
   i)    Using the URL "https://www.vendhq.com/" create a Vend trial account.
   ii)   Using the URL "https://developers.vendhq.com/" create a Vend developer account.
   iii)  Using the URL "https://developers.vendhq.com/developer/applications" create an application in developer account, Obtain the 'Client Id', 'Client Secret' and 'Redirect URI'.
   iv)   Go to following URL "https://developers.vendhq.com/documentation/oauth.html#connecting-your-application-to-a-vend-retailer-account" and follow the Step 1 and obtain the code.
   v)    Navigate to 'https://cottoncollection.vendhq.com/setup/outlets_and_registers' and create a registry by giving a proper name.
 
 7. Update the vend properties file at location "<VEND_CONNECTOR_HOME>/vend-connector/vend-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
   i)       apiUrl                     -  Use following value 'https://{domain}.vendhq.com'
   ii)      authorizationCode          -  Use the value obtained from Step 6 iv).
   iii)     redirectUri                -  Use the value obtained from Step 6 iii).
   
   iv)      customerCompany            -  Text to be used as 'company_name' while creating a customer with optional parameters.
   v)       customerFirstName          -  Text to be used as 'first_name' while creating a customer with optional parameters.
   vi)      customerLastName           -  Text to be used as 'last_name' while creating a customer with optional parameters.
   vii)     customerEmail              -  Text to be used as 'email' while creating a customer with optional parameters (E.g. connector.dev@gmail.com).
   viii)    customerMobile             -  Numerical value to be used as 'mobile' while creating a customer with optional parameters. (E.g 0772336523).
   
   ix)      updatedCustomerCompany     -  Text to be used as 'company_name' while updating a customer with optional parameters
   x)       updatedCustomerFirstName   -  Text to be used as 'first_name' while updating a customer with optional parameters.
   xi)      updatedCustomerLastName    -  Text to be used as 'last_name' while updating a customer with optional parameters.
   xii)     updatedCustomerEmail       -  Text to be used as 'email' while updating a customer with optional parameters (E.g. connector.dev@gmail.com).
   xiii)    updatedCustomerMobile      -  Numerical value to be used as 'mobile' while updating a customer with optional parameters. (E.g 0772336523).
   xiv)     customerSinceDate          -  Text to be used as 'since' for listing customers modified since the given time. The provided date and time should be in UTC and formatted according to ISO 8601 (e.g. 2015-09-13 06:10:16). (Make sure to use a past date.)
   
   xv)      productHandle              -  Text to be used as 'handle' which is a mandatory parameter while creating a product.
   xvi)     productSKU                 -  Text to be used as 'SKU' which is a mandatory parameter while creating a product.
   xvii)    productRetailPrice         -  Numerical value to be used as 'price' which is a mandatory parameter while creating a product.(E.g 230)
   xviii)   productType                -  Text to be used as 'type' while creating a product with optional parameters.
   xix)     productDescription         -  Text to be used as 'type' while creating a product with optional parameters.
   xx)      productSupplyPrice         -  Numerical value to be used as 'price' while creating a product. Make sure to use a value with two decimal places. (E.g 230.00)
   xxi)     productName                -  Text to be used as 'name' while creating a product with optional parameters.
   xxii)    productBrandName           -  Text to be used as 'brand_name' while creating a product with optional parameters.
   xxiii)   productHandleNegative      -  Text to be used as 'handle' which is used to fail the product creation method.
   
   xxiv)    productOrderBy             -  Text to be used as 'order_by' which selects the order of the product results returned when listing products. Possible values are 'id' and 'updated_at'.
   xxv)     productOrderDirection      -  Text to be used as 'order_direction ' which decided the direction to which the products will be retrieved. Possible values are 'ASC' and 'DESC'.
   xxvi)    productActive              -  Numerical value to be used as 'active' which retrieves only the active products. Make sure to use the value '1'. 
   
   xxvii)   updatedProductSKU          -  Text to be used as 'SKU' while updating a product with optional parameters.
   xxviii)  updatedProductDescription  -  Text to be used as 'description' while updating a product with optional parameters.
   xxix)    updatedProductSupplyPrice  -  Numerical value to be used as 'price' while updating a product. Make sure to use a value with two decimal places. (E.g 230.00)
   xxx)     updatedProductName         -  Text to be used as 'name' while updating a product with optional parameters.
   xxxi)    updatedProductBrandName    -  Text to be used as 'brand_name' while updating a product with optional parameters.
   
   xxxii)   registerName               -  Use the exact name of the register which is created in 6 v).
   
   xxxiii)  saleDate                   -  Text to be used as 'sale_date' while creating a register sale with optional parameters. The provided date and time should be in UTC and formatted according to ISO 8601 (e.g. 2015-09-13 06:10:16). (Make sure to use a past date.)
   xxxiv)   saleNote                   -  Text to be used as 'note' while creating a register sale with optional parameters.
   xxxv)    salePrice                  -  Numerical value to be used as 'price' while creating a register sale with optionla parameters. Make sure to use a value with two decimal places. (E.g 230.00)
   xxxvi)   saleSinceDate              -  Text to be used as 'since' for listing register sales modified since the given time. The provided date and time should be in UTC and formatted according to ISO 8601 (e.g. 2015-09-13 06:10:16). (Make sure to use a past date.)
   xxxvii)  saleStatus                 -  Text to be used as 'status' or listing register sales with optional parameters. Make sure to use the status as 'OPEN'.
   
   xxxviii) supplierNameMandatory      -  Text to be used as 'name' of supplier while creating a supplier with mandatory parameters.
   xxxix)   supplierNameOptional       -  Text to be used as 'name' of supplier while creating a supplier with optional parameters.
   xL)      supplierDescription        -  Text to be used as 'descriprion' of supplier while creating a supplier with optional parameters.
   xLi)     supplierCompanyName        -  Text to be used as contact's, 'company_name' of supplier while creating a supplier with optional parameters.
   xLii)    contactFirstName           -  Text to be used as contact's, 'first_name' of supplier while creating a supplier with optional parameters.
   xLiii)   contactLastName            -  Text to be used as contact's, 'last_name' of supplier while creating a supplier with optional parameters.
   xLiv)    contactPhone               -  Numerical value to be used as contact's, 'phone' of supplier while creating a supplier with optional parameters.
   xLv)     contactMobile              -  Numerical value to be used as contact's, 'mobile' of supplier while creating a supplier with optional parameters.
   
   xLvi)    consignmentType            -  Text to be used as 'type' while creating a consignment with mandatory parameters.
   xLvii)   consignmentName            -  Text to be used as 'name' of consignment while creating a consignment with optional parameters.
   xLviii)  consignmentDate            -  Text to be used as 'consignment_date' while creating a consignment with optional parameters. The provided date and time should be in UTC and formatted according to ISO 8601 (e.g. 2015-09-13 06:10:16).
   xLix)    consignmentDueAt           -  Text to be used as 'due_at' while creating a consignment with optional parameters. The provided date and time should be in UTC and formatted according to ISO 8601 (e.g. 2015-09-13 06:10:16).
   L)       consignmentStatus          -  Text to be used as 'status' while creating a consignment with optional parameters. Make sure to use the status as 'OPEN'.
   
   Li)      consignmentNameUpdate      -  Text to be used as 'name' of consignment while updating a consignment with optional parameters.
   Lii)     consignmentDateUpdate      -  Text to be used as 'consignment_date' while updating a consignment with optional parameters. The provided date and time should be in UTC and formatted according to ISO 8601 (e.g. 2015-09-13 06:10:16).
   Liii)    consignmentDueAtUpdate     -  Text to be used as 'due_at' while updating a consignment with optional parameters. The provided date and time should be in UTC and formatted according to ISO 8601 (e.g. 2015-09-13 06:10:16). 
   
   Liv)     cost                       -  Text to be used as 'cost' of consignment product while creating a consignment product with optional parameters.
   Lv)      sequenceNumber             -  Text to be used as 'sequence_number' of consignment product while creating a consignment product with optional parameters.
   
   
   Special Notes: 
      1)  Make sure to close the registry created in Step 7 xxxii) before each execution.
      2)  Change the value in 6 ii) before each execution.
      3)  Add following resources to the ESB registry.
            
            i)    /_system/governance/connectors/Vend/apiUrl         -  Use following value 'https://{domain}.vendhq.com'
            ii)   /_system/governance/connectors/Vend/accessToken    -  Keep the value blank.
            iii)  /_system/governance/connectors/Vend/clientId       -  Use the value obtained in Step 6 iii). 
            iv)   /_system/governance/connectors/Vend/clientSecret   -  Use the value obtained in Step 6 iii).
            v)    /_system/governance/connectors/Vend/redirectUrl    -  Use the value obtained in Step 6 iii).
            vi)   /_system/governance/connectors/Vend/refreshToken   -  Keep the value blank.
   
 8. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
         $ mvn clean install


