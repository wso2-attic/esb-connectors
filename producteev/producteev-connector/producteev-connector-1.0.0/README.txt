Product: Integration tests for WSO2 ESB Producteev connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-BETA-SNAPSHOT by navigating to the following URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/
 
 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
 
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
        <messageFormatter contentType="text/html" 
		    class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
        <messageBuilder contentType="text/html" 
            class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 4. Using the URL "https://www.producteev.com/signup" create a Producteev trial account and login to it.
    i) Navigate to "https://www.producteev.com/settings/apps" and create new application, Obtain the 'Client Id', 'Client Secret' and 'Redirect URI'.
    ii) Go to following URL "https://www.producteev.com/api/doc/#AuthenticationOAuth20Flows" and follow the "Authentication: OAuth 2.0 Flows" to obtain "access_token" and "refresh_token".
    iii) Add following resources to the ESB registry.
        i)    /_system/governance/connectors/Producteev/apiUrl         -  Use following value 'https://www.producteev.com'
        ii)   /_system/governance/connectors/Producteev/accessToken    -  Use the access_token obtained in Step 4 ii). 
        iii)  /_system/governance/connectors/Producteev/clientId       -  Use the Client Id obtained in Step 4 i). 
        iv)   /_system/governance/connectors/Producteev/clientSecret   -  Use the Client Secret obtained in Step 4 i).
        v)    /_system/governance/connectors/Producteev/redirectUrl    -  Use the Redirect URI obtained in Step 4 i).
        vi)   /_system/governance/connectors/Producteev/refreshToken   -  Use the refresh_token obtained in Step 4 ii). 

 5. Prerequisites for Producteev Connector Integration Testing.
    i)      Create a network by navigating to Networks > New Network and keep the network ID (id displays in the URL when the network is selected) for further reference.
    ii)     Create a project in the above created network and keep the project ID (id displays in the URL when the project is selected) for further reference.
    iii)    Create a user in the Network created above by navigating to Networks > People and keep the user ID (id displays in the URL when the user is selected) for further reference.
    iv)     Add a task using the project ID created in step 5 ii) for further reference.
    v)      Add a note with file attachment to the task created in step 5 iv) for further reference.
    vi)     Navigate to Labels and create a label using the default values. Keep the label ID (id displays in the URL when the label is selected)for further reference.
   
 6. Follow the below mentioned steps to add valid certificate to access Producteev API over https.

    i)   Extract the certificate from browser(Mozilla Firefox) by navigating to 'https://www.producteev.com/'.
    ii)  Go to new ESB 4.9.0 folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and 
         "<PRODUCTEEV_CONNECTOR_HOME>/producteev-connector/producteev-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
    iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

         keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

         This command will import Producteev certificate into keystore. 
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE: CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Producteev with the extension. (e.g. Producteev.crt)
               CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Producteev)

    iv) Navigate to "<PRODUCTEEV_CONNECTOR_HOME>/producteev-connector/producteev-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

         keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 

         This command will import Producteev certificate in to keystore. Give "wso2carbon" as password.
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE: CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Producteev with the extension. (e.g. Producteev.crt)
               CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Producteev).
 
 7. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "<ESB_CONNECTOR_HOME>/repository/".
 
 8. Make sure that Producteev is specified as a module in ESB Connector Parent pom.

    <module>producteev/producteev-connector/producteev-connector-1.0.0/org.wso2.carbon.connector</module>
 
 9. Update the Producteev properties file at location "<PRODUCTEEV_CONNECTOR_HOME>/producteev-connector/producteev-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

    i)     apiUrl                      - The API URL specific to the domain of the created account (e.g.: https://www.producteev.com).
    ii)    networkId                   - Use the network ID created under step 5 i).
    iii)   projectId                   - Use the project ID created under step 5 ii).
    iv)    userId                      - Use the user ID created under step 5 iii).
    v)     taskTitleMandatory          - Text to be used as 'title' while creating a task with mandatory parameters.	
    vi)    taskTitleOptional           - Text to be used as 'title' while creating a task with optional parameters.
    vii)   endDate                     - Date to be used as 'deadline' while creating a task with optional parameters. e.g.: 2015-09-15T00:00:00+0000.
    vii)   endDateTimeZone             - Standard time zone code to be used as "deadline_timezone" while creating a task with optional parameters (e.g.: GST).
    ix)   updateTaskTitle             - Text to be used as 'title' while updating a task with optional parameters (Note: This parameter  value must be different than the "taskTitleOptional" parameter value. ). 
    x)    updateTaskEndDate           - Date to be used as 'deadline' while updating a task with optional parameters (Note: This parameter  value must be different than the "endDate" parameter value.). e.g.: 2015-09-20T00:00:00+0000.
    xi)   updateTaskEndDateTimeZone   - Standard time zone code to be used as "deadline_timezone" while updating a task with optional parameters (Note: This parameter  value must be different than the "endDateTimeZone" parameter value.). e.g.: GMT.
    xii)  noteMessageMand             - Text to be used as 'message' while creating a note with mandatory parameters.
    xiii) noteMessageOpt              - Text to be used as 'message' while creating a note with optional parameters.
    xiv)  taskIdWithFileNote          - Use the task ID created under step 5 iv).
    xv)   invitationType              - Type of the network invitation ( Possible values are "member" and "admin").
    xvi)  invitationEmail             - Email address of the person to be used as invite user to the network with mandatory parameters.
    xvii) labelId                     - Use the label ID created under step 5 vi).
    xvii) labelTitle                  - Use a valid string. The value should be different from the title of label created in step 5 vi).
    xix)  foregroundColor             - Use a hexadecimal colour code as the value (The initial value should be different from '#e8cdde').
    xx)   backgroundColor             - Use a hexadecimal colour code as the value (The initial value should be different from '#9f3879').
		
    NOTE: The property values of taskTitleMandatory, taskTitleOptional, invitationEmail, labelId, labelTitle, foregroundColor, backgroundColor should be changed to unique different values for each integration execution.  

 10. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
     $ mvn clean install
