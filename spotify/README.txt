Product: Integration tests for WSO2 ESB Spotify connector
Pre-requisites:
- Maven 3.x
- Java 1.6 or above

Tested Platform:
- Mac OSx 10.9
- WSO2 ESB 4.9.0-M7-SNAPSHOT
- Java 1.7

STEPS:
1. Make sure the ESB 4.9.0-M7-SNAPSHOT zip file with latest patches available at "spotify/repository/".
	- import the Spotify CERTIFICATE to your ESB client’s keystore.
	- import your spotify certificates in to wso2esb client’s keystore as follows:
		Go to https://developer.spotify.com in your browser and click the HTTPS trust icon (padlock) on the address bar.
		Download and put the certificate at 
		<ESB_HOME>/repository/resources/security/
		Type this command on terminal.
        keytool -importcert -file <CERTIFICATE_FILE_NAME WITH EXTENSION> -keystore client-truststore.jks -alias "<CERTIFICATE_NAME>"
        and place the certificate file in following location also.
		"{SPOTIFY_CONNECTOR_HOME}/src/test/resources/keystores/products/"

2.  ESB should be configured as below.
   	 Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).

	 <messageFormatter contentType="text/html"
							class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
	 <messageBuilder contentType="text/html"
							class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
     <messageFormatter contentType="text/plain"
                            class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
     <messageBuilder contentType="text/plain"
                            class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

3. Add following code block, just after the listeners block (Remove or comment all the other test blocks) in
following file - "spotify/src/test/resources/testng.xml"

	<test name="Spotify-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.spotify"/>
        </packages>
    </test> 

4. Copy proxy files to following location "spotify/src/test/resources/artifacts/ESB/config/proxies/spotify/"

5. Copy request files to following "spotify/src/test/resources/artifacts/ESB/config/restRequests/spotify/"

6. Edit the "spotify.properties" at spotify/src/test/resources/artifacts/connector/config/ using valid and relevant
data. Parameters to be changed are mentioned below.

	- proxyDirectoryRelativePath: relative path of the Rest Request files folder from target.
	- requestDirectoryRelativePath: relative path of proxy folder from target.
	- client_id: to get the access token for a particular client_id.
    - client_secret: to get the access token for a particular client_secret.
   	- grant_type: to get the access token with refresh token.
	- refresh_token: refresh token is to get the access token.
	
		
7. Following data set can be used for the first testsuite run.

	- proxyDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/proxies/spotify/
	- requestDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/restRequests/spotify/
	- client_id=f043fec58a584539ba94c0c588136712
	- client_secret=b421eecdfe184be29b1d297505befcac
	- grant_type=refresh_token
	- refresh_token=AQCQPj6__vnciBWMpS1lJ_V_oefCZUy2V7z_Ha7WBoZZTKRsVPgDTtecOMN5rxXs9mvtencluvX3m_RNCsTqTTJtwZCmcrte_1Ar_SzXDug-AtdbSiZXMObPa8Kw0-8tE8c

8. Navigate to "spotify/” and run the following command.
     $ mvn clean install