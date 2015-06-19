Product: Integration tests for WSO2 ESB PipeDrive connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-SNAPSHOT

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 from official website.
 
 2. Deploy relevant patches, if applicable.
 		
 3. Set up a new Pipedrive account by following the wizard in https://app.pipedrive.com/register url. 
	Then, navigate to https://wso2.pipedrive.com/settings#api to obtain the API key.

 4. Follow the below mentioned steps for adding valid certificate to access Pipedrive API over https.

	i) 	 Extract the certificate from browser(Mozilla Firefox) by navigating to https://api.pipedrive.com
	ii)  Go to new ESB 4.9.0 folder and place the downloaded certificate in both "<ESB_HOME>/repository/resources/security/" and "<PIPEDRIVE_CONNECTOR_HOME>/pipedrive-connector/pipedrive-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" folders.
	iii) Navigate to "<ESB_HOME>/repository/resources/security/" folder using command prompt and execute the following command.
	
				keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" 
				
		 This command will import pipedrive certificate into keystore.
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Pipedrive with the extension. (e.g. pipedrive.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Pipedrive)
				
	iv)  Navigate to "<PIPEDRIVE_CONNECTOR_HOME>/pipedrive-connector/pipedrive-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.
	
				keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 
				
		 This command will import Pipedrive certificate into keystore.
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Pipedrive with the extension. (e.g. pipedrive.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Pipedrive)

 5. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to "<PIPEDRIVE_CONNECTOR_HOME>/pipedrive-connector/pipedrive-connector-1.0.0/org.wso2.carbon.connector/repository/" folder.

 6. Set up the Pipedrive account as mentioned below.
		Log in to the newly created Pipedrive account,
		i)	Navigate to settings(Using the drop down in top right corner) -> Users & Permissions and click on the user's name mentioned as "you" and obtain the user ID of the account owner displayed in URL as follows. https://wso2.pipedrive.com/users/edit/{USER_ID}
		ii) Add a new custom field as follows. Navigate to settings -> Customize fields and to the Organizations tab. Then, click on the "Add a field" button and choose the field type as "Text" and complete the wizard and save the field. Go to the newly added field displayed under "Customize organization fields" page(current page) and Obtain the "Field API Key" value of the field by clicking on the respective field name.
		iii)Add a new pipeline by navigating to settings -> Pipelines and clicking on "Add new pipeline" button and completing the wizard.
				Select the newly created pipeline and add a stage to the pipeline by clicking on Add stage. 
				Right Click on the created stage and open the link in new tab to obtain the stage ID displayed in URL as follows.  https://wso2.pipedrive.com/stages/edit/{STAGE_ID}.json
		iv)	Repeat the step 6)iii to create another stage in same pipeline. Obtain the stage ID.
		v) 	Add a new activity type as follows. Navigate to settings -> Company settings and to "Activity types" tab. Click on the "Add activity type" button and complete the wizard. Save the given name.

 7. Update the properties in 'pipedrive.properties' file at location "<PIPEDRIVE_CONNECTOR_HOME>/pipedrive-connector/pipedrive-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl 		          - Use the API URL as "https://api.pipedrive.com".
	ii)		apiToken 		      - Use the API key obtained in step 3.
	iii)	userId				  - The unique identifier of the account owner. Use the ID obtained in step 6)i.
	iv)		customFieldName		  - Use the field API Key value obtained in step 6)ii. 
	v)		customFieldValue	  - Give a suitable value as the custom field value. 
	vi)		email				  - Use a valid email address.
	vii)	phoneNumber			  - Use a valid phone number.
	viii)	dealValue			  - Use a valid value for the deal. E.g. 5400
	ix)		dealUpdateValue		  - The updated value for the deal. Do not provide the same value given for the dealValue parameter. E.g. 5455
	x)		dealCurrency		  - Use a valid 3-character currency code. E.g. LKR
	xi)		dealUpdateCurrency	  - The updated value for the currency code. Do not provide the same value given for the dealCurrency parameter. E.g. USD
	xii)	activityType          - Use the name of activity type created in step 6)v.
	xiii)	activityDuration      - The duration of the activity. Format HH:MM.
	xiv)	dealStageId			  - Use the stage ID obtained in step 6)iii.
	xv)		updateDealStageId	  - Use the stage ID obtained in step 6)iv.
	xvi)	itemPrice1			  - Use a valid price value for the item.
	xvii)	itemQuantity1		  - Use a valid item quantity.
	xviii)	itemPrice2			  - Use a valid price value for the item.
	xix)	itemQuantity2		  - Use a valid item quantity.

8. Make sure that the pipedrive connector is set as a module in esb-connectors parent pom.
               <module>pipedrive/pipedrive-connector/pipedrive-connector-1.0.0/org.wso2.carbon.connector</module>
9. Navigate to "<PIPEDRIVE_CONNECTOR_HOME>/pipedrive-connector/pipedrive-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
