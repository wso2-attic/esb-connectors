Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0
 - Java 1.7

Sample Inbound Configuration:

<inboundEndpoint
     class="org.wso2.carbon.inbound.localfile.LocalFileOneTimePolling"
     name="LocalFile" onError="fault" sequence="request" suspend="false">
     <parameters>
         <parameter name="sequential">true</parameter>
         <parameter name="interval">2000</parameter>
         <parameter name="coordination">true</parameter>
         <parameter name="FileURI">/home/Documents/LFC/LFC/Test1/TEST</parameter>
         <parameter name="ActionAfterProcess">delete</parameter>
         <parameter name="ProcessBeforeWatch">no</parameter>
     </parameters>
 </inboundEndpoint>

  Navigate to "<ESB_CONNECTOR_HOME>/localfile/localfile-inbound-endpoint/localfile-inbound-endpoint-1.0.0" and run the following command.
         $ mvn clean install