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

1. Create object and pushTopic:

https://developer.salesforce.com/docs/atlas.en-us.api_streaming.meta/api_streaming/create_object.htm#create_object

2. Navigate to "<ESB_CONNECTOR_HOME>/salesforce/salesforce-inbound-endpoint/salesforce-inbound-endpoint-1.0.0/ " and run the following command.
         $ mvn clean install

3. To use the Salesforce inbound endpoint, you need to download the inbound org.apache.synapse.salesforce.poll.class-1.0.0.jar from https://storepreview.wso2.com/store/ and copy the jar to the <ESB_HOME>/repository/components/dropins directory.

4. Configuration:

<inboundEndpoint
        class="org.wso2.carbon.inbound.salesforce.poll.SalesforceStreamData"
        name="SaleforceInboundEP" onError="fault" sequence="reqSequence" suspend="false">
        <parameters>
            <parameter name="sequential">true</parameter>
            <parameter name="interval">10</parameter>
            <parameter name="coordination">true</parameter>
            <parameter name="connection.salesforce.userName">xxxxxx@gmail.com</parameter>
            <parameter name="connection.salesforce.loginEndpoint">https://login.salesforce.com</parameter>
            <parameter name="connection.salesforce.password">xxxxxxxx</parameter>
            <parameter name="connection.salesforce.salesforceObject">InvoiceStatementUpdates</parameter>
            <parameter name="connection.salesforce.connectionTimeout">20000</parameter>
            <parameter name="connection.salesforce.readTimeout">120000</parameter>
            <parameter name="connection.salesforce.waitTime">10000</parameter>
            <parameter name="connection.salesforce.packageName">cometd</parameter>
            <parameter name="connection.salesforce.packageVersion">35.0</parameter>
            <parameter name="connection.salesforce.soapApiVersion">22.0/</parameter>
        </parameters>
    </inboundEndpoint>
   


