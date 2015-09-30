Product: Integration tests for WSO2 ESB Elasticsearch connector

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

Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by navigating to the following URL: http://wso2.com/products/enterprise-service-bus/#
 
 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
 
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
        <messageFormatter contentType="application/yaml"
                        class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

        <messageBuilder contentType="application/yaml"
                        class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

        <messageFormatter contentType="application/javascript"
                         class="org.apache.synapse.commons.json.JsonStreamFormatter"/>

        <messageBuilder contentType="application/javascript"
                         class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

 4. Follow the steps in below mentioned URL to download and setup an instance of the latest version of Elasticsearch.
      https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html

 5. Prerequisites for Elasticsearch Connector Integration Testing.
		i) Create an index using a method call to Elasticsearch API using the following URL.
				PUT 'http://{IP_ADDRESS_OF_THE_MACHINE}:{PORT_NUMBER}/{INDEX_NAME}/'
 
 6. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "<ESB_CONNECTOR_HOME>/repository/".
 
 7. Make sure that Elasticsearch is specified as a module in ESB Connector Parent pom.

    <module>elasticsearch/elasticsearch-connector/elasticsearch-connector-1.0.0/org.wso2.carbon.connector</module>
 
 8. Update the Elasticsearch properties file at location "<ELASTICSEARCH_CONNECTOR_HOME>/elasticsearch-connector/elasticsearch-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)     apiUrl                       -  The API URL should be in the following format http://{IP_ADDRESS_OF_THE_MACHINE}:{PORT_NUMBER}
                                          (The IP address should be IP address of the machine in which the Elasticsearch instance is running.)
   ii)    from                         -  The starting from index of the hits to return. Defaults to 0.
   iii)   size                         -  The number of hits to return.
   iv)    scroll                       -  Specify a time duration according to the following format. (format : {TIME_DURATION}{TIME_UNIT} , e.g. 1m).
   v)     terminateAfter               -  The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early. Provide an integer as the value.
   vi)    indexName                    -  Use the name of the index created in step 5)i.
   vii)   type                         -  The type inside which the ID would be considered. Provide any string value.
   viii)  updateDocValueMandatory      -  Updated value of the document(provide any value).
   ix)    updateDocValueOptional       -  Updated value of the document(provide any value).
   x)     routing                      -  A string name of the shard base to which the document would be routed.
   xi)    routingMessageMandatory      -  A string message to set as routing message for the document routing mandatory case.
   xii)   routingMessageOptional       -  A string message to set as routing message for the document routing optional case.
   xiii)  messageMandatory             -  A string message to create automatic ID mandatory case.  
   xiv)   messageOptional              -  A string message to create automatic ID optional case.  
   xv)    tagValue                     -  A string value to add as a tag for index child document. 
   xvi)   postDate                     -  Date when the index should be inserted (The date formate should be like yyyy-MM-ddTHH:mm:ss).
   xvii)  userName                     -  A string value to set as user Name to create document.
   xviii) message                      -  A string message to create document optional case.
   xvix)  version                      -  An integer value to set as version (e.g.: 23).
   xx)    versionType                  -  Should be set as 'external'.
   xxi)   bulkOperationValue1          -  A string value to set as bulk operation value 1.
   xxii)  bulkOperationValue2          -  A string value to set as bulk operation value 2.
   xxiii) indexNameMand                -  A valid index string name to create automatic index mandatory case. 
   xxiv)  indexNameOpt                 -  A valid index string name to create automatic index optional case.
   xxv)   indexMappingAllowType        -  A boolean value to set as mapping allow type (e.g.:true).
   xxvi)  indexChildDocumentTypeMand   -  A string value to assign as child type for index child document mandatory case.
   xxvii) indexChildDocumentTypeOpt    -  A string value to assign as child type for index child document optional case.
   xxviii)indexChildDocumentChildId    -  An integer value to set as child ID for index child document (e.g.: 2).
   
   NOTE: The property values of 'indexNameMand' and 'indexNameOpt' should be changed to unique different values for each integration execution.  

 9. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
     $ mvn clean install
