Product: Integration tests for WSO2 ESB Magento connector
    Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
	- org.wso2.esb.integration.integration-base is required. this test suite has been configured to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

    Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1

Note:
  This test suite can execute based on two scenarios.
    1. Use the given test account and parameters. - in this scenario you only need to replace apiKey in property file
    2. Setup a new Magento instance and test based on the instructions given below
  
Steps to follow in setting integration test.
 1.  Download ESB 4.8.1 from official website.
 
 2.  Deploy relevant patches, if applicable.

 3.  Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{Magento_Connector_Home}/magento-connector/magento-connector-1.0.0/org.wso2.carbon.connector/repository/".

         
 4.  Prerequisites for Magento Connector Integration Testing

     Follow these steps before start testing.
     a)  Create a fresh Magento Instance and access its Admin Panel.
     b)  In the Admin Main Menu, go to System->Web Services->SOAP/XML-RPC-Roles and Add a New Role with access to all resources.
     c)  In the Admin Main Menu, go to System->Web Services->SOAP/XML-RPC-Users and Add New User assigning the above created role.
     d)  Add the username you used to create the API user to the apiUser property and the API Key for the user you created to the apiKey property in the magento.properties property file.
     e)  Update the SOAP API Url of the Magento instance in the property named apiUrl in the magento.properties property file.
	 f)  In the Admin Main Menu, go to System->Configuration->Payment Methods and then enable and configure 'Cash On Delivery Payment' payment method.
	 g)  In the Admin Main Menu, go to System->Configuration->Shipping Methods and then enable and configure 'Free Shipping' shipping method.
	 h)  In the Admin Main Menu, go to Customers->Manage Customers->Add New Customer and create an Admin customer in General group and add the created user id to the moveProductFromQuoteToCartCustomerId property in the connector property file.
	 i)  In the Admin Main Menu, go to Sales->Orders->Create New Order and create an order for the above created user and set the following information for that order and set the created order id to the cancelInvoiceOptOrderIncId property in the connector property file.
			- Set the billing and shipping address for the order.
			- Add a product with quantity specified.
			- Set the payment method to 'Cash On Delivery' payment method.
			- Set the shipping method to 'Free Shipping' shipping method.
        
     j)  Following fields in the property file also should be updated appropriately.

        1)  storeId is the ID of the store (default is 1).
		2)  email is the Email used to create a customer and this should be a unique value for each customer.
		3)  emailOptional is the Email used to create a customer with optional parameters and this should be a unique value for each customer.
		4)  updateEmail is the Email used to update a customer with and this should be a unique value for each customer.
		5)  updateFirstName is the name to be used when the customer information is updated.
		6)  productIdMandatory A product has to be created through the Admin Dashboard, Catalog->Manage Products->Add Product. Use the ID of a product created in this manner for this property.
		7)  productSKUMandatory is the value given for SKU field when creating the above product.
		8)  qtyMan is the quantity to be added to the shopping cart from the above created product.
		9)  productIdOptional Another product has to be created through the Admin Dashboard, Catalog->Manage Products->Add Product. Use the ID of a product created in this manner for this property.
		10) productSKUOptional is the value given for SKU field when creating the above product productIdOptional.
		11) productQtyOptional is the quantity to be added to the shopping cart from the above created product.
		12) paymentDataMethod is the payment method to be set to the shopping cart.
		13) cartShippingMethod is the shipping method to be set to the shopping cart.
		14) comment is the comment used when adding the comment to the credit memo.
		15) addComment is another comment used when adding the comments to the credit memo.
		16) status is the status to be set when adding a comment to an order (Default is 'Pending status')
		17) commentOptional is the optional comment used to add to the credit memo.
		18) moveProductFromQuoteToCartCustomerId is the customer Id created in step 'h' for moving product from shopping cart to order.
		19) cancelInvoiceOptOrderIncId is the order Id created for the above user which is used for moving the products into.
		
 5.  Navigate to "{Magento_Connector_Home}/magento-connector/magento-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
     $ mvn clean install

   
     credential of test account:
     
     Admin URL: http://ec2-54-83-58-40.compute-1.amazonaws.com:8080/magento/index.php/admin
     Admin User: admin
     Admin Password: wso2connector
     API URL: http://ec2-54-83-58-40.compute-1.amazonaws.com:8080/magento/index.php/api/v2_soap/index/
     API User: admin
     API Key: admin123
