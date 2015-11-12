/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.inbound.salesforce.poll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.SynapseEnvironment;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.wso2.carbon.inbound.endpoint.protocol.generic.GenericPollingConsumer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Salesforce streaming api Inbound
 */
public class SalesforceStreamData extends GenericPollingConsumer {

    private static final Log log = LogFactory.getLog(SalesforceStreamData.class);

    // Mandatory parameters
    private static final boolean version = false;
    private static final boolean useCookies = version;
    protected static String loginEndpoint;
    private static String userName;
    private static String password;
    private static String salesforceObject;
    private static String channel;
    private static String packageVersion;
    private static String packageName;
    private static String streamingEndpointUri;
    private String injectingSeq;

    // optional parameters
    private static int connectionTimeout;
    private static int readTimeout;
    private static int waitTime;
    BayeuxClient client = null;
    HttpClient httpClient;
    public SalesforceStreamData(Properties salesforceProperties, String name,
                                 SynapseEnvironment synapseEnvironment, long scanInterval,
                                 String injectingSeq, String onErrorSeq, boolean coordination,
                                 boolean sequential) {
        super(salesforceProperties, name, synapseEnvironment, scanInterval,
                injectingSeq, onErrorSeq, coordination, sequential);
        log.info("Initialized the Saleforce Streaming consumer");
        loadMandatoryParameters(salesforceProperties);
        loadOptionalParameters(salesforceProperties);
        streamingEndpointUri = version ? "/" + packageName : "/" + packageName + "/" + packageVersion;
        channel = "/topic/" + salesforceObject;
        this.injectingSeq = injectingSeq;
        httpClient = new HttpClient();

        // Establishing connection with salesforce streaming api
        try {
            log.info("Running streaming client example....");
            makeConnect();


        } catch (Exception e) {
            handleException("Error while setup the salesforce connection.", e);
        }
    }



    private void makeConnect() {
        try{
            client = makeClient();
            client.getChannel(Channel.META_HANDSHAKE).addListener
                    (new ClientSessionChannel.MessageListener() {
                        public void onMessage(ClientSessionChannel channel, Message message) {
                            log.info("[CHANNEL:META_HANDSHAKE]: " + message);
                            boolean success = message.isSuccessful();
                            if (!success) {
                                String error = (String) message.get("error");
                                if (error != null) {
                                    log.error("Error during HANDSHAKE: " + error);
                                }

                                Exception exception = (Exception) message.get("exception");
                                if (exception != null) {
                                    handleException("Exception during HANDSHAKE: ", exception);
                                }
                            }
                        }
                    });

            client.getChannel(Channel.META_CONNECT).addListener(
                    new ClientSessionChannel.MessageListener() {
                        public void onMessage(ClientSessionChannel channel, Message message) {
                            log.info("[CHANNEL:META_CONNECT]: " + message);
                            boolean success = message.isSuccessful();

                            if (!success) {
                                client.disconnect();
                                makeConnect();
                                String error = (String) message.get("error");
                                if (error != null) {
                                    //log.error("Error during CONNECT: " + error);
                                }
                            }
                        }

                    });

            client.getChannel(Channel.META_SUBSCRIBE).addListener(
                    new ClientSessionChannel.MessageListener() {
                        public void onMessage(ClientSessionChannel channel, Message message) {
                            log.info("[CHANNEL:META_SUBSCRIBE]: " + message);
                            boolean success = message.isSuccessful();
                            if (!success) {
                                String error = (String) message.get("error");
                                if (error != null) {
                                    makeConnect();
                                    log.error("Error during SUBSCRIBE: " + error);
                                }
                            }
                        }
                    });
            client.handshake();
            log.info("Waiting for handshake");
            boolean handshaken = client.waitFor(waitTime, BayeuxClient.State.CONNECTED);
            if (!handshaken) {
                log.error("Failed to handshake: " + client);
            }
            log.info("Subscribing for channel: " + channel);
            client.getChannel(channel).subscribe(new MessageListener() {
                public void onMessage(ClientSessionChannel channel, Message message) {
                    injectSalesforceMessage(message);
                }
            });
            log.info("Waiting for streamed data from your organization ...");
        }catch (Exception e) {
            handleException("Error while setup the salesforce connection.", e);
        }

    }

    /**
     * Create a http client
     *
     * @return
     * @throws Exception
     */
    private BayeuxClient makeClient() throws Exception {

        httpClient.setConnectTimeout(connectionTimeout);
        httpClient.setTimeout(readTimeout);//TODO optional parameters
        httpClient.start();

        SoapLoginUtil.login(httpClient, userName, password);
        final String sessionId = SoapLoginUtil.getSessionId();
        String endpoint = SoapLoginUtil.getEndpoint();
        System.out.println("Login successful!\nEndpoint: " + endpoint
                + "\nSessionid=" + sessionId);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put(ClientTransport.TIMEOUT_OPTION, readTimeout);
        LongPollingTransport transport = new LongPollingTransport(
                options, httpClient) {

            @Override
            protected void customize(ContentExchange exchange) {
                super.customize(exchange);
                exchange.addRequestHeader("Authorization", "OAuth " + sessionId);
            }
        };

        BayeuxClient client = new BayeuxClient(salesforceStreamingEndpoint(
                endpoint), transport);
        if (useCookies) establishCookies(client, userName, sessionId);
        return client;
    }

    private static String salesforceStreamingEndpoint(String endpoint)
            throws MalformedURLException {
        return new URL(endpoint + streamingEndpointUri).toExternalForm();
    }

    /**
     * establish the Cookies fot the http client
     *
     * @param client
     * @param user
     * @param sid
     */
    private static void establishCookies(BayeuxClient client, String user,
                                         String sid) {
        client.setCookie("com.salesforce.LocaleInfo", "us", waitTime);
        client.setCookie("login", user, waitTime);
        client.setCookie("sid", sid, waitTime);
        client.setCookie("language", "en_US", waitTime);
    }

    /**
     * load essential property for salesforce inbound endpoint
     *
     * @param properties
     */
    private void loadMandatoryParameters(Properties properties) {
        if (log.isDebugEnabled()) {
            log.debug("Starting to load the salesforce credentials");
        }

        userName = properties.getProperty(SalesforceConstant.USER_NAME);
        salesforceObject = properties.getProperty(SalesforceConstant.SOBJECT);
        password = properties.getProperty(SalesforceConstant.PASSWORD);
        loginEndpoint = properties.getProperty(SalesforceConstant.LOGIN_ENDPOINT);
        packageName = properties.getProperty(SalesforceConstant.PACKAGE_NAME);
        packageVersion = properties.getProperty(SalesforceConstant.PACKAGE_VERSION);

        if (log.isDebugEnabled()) {
            log.debug("Loaded the salesforce userName : " + userName
                    + ",password : " + password + ",LoginEndpoint : "
                    + loginEndpoint + "securityToken" + "Loading the object"
                    + salesforceObject);
        }
    }

    /**
     * Load optional parameters for salesforce inbound endpoint file.
     *
     * @param properties
     */
    private void loadOptionalParameters(Properties properties) {
        if (log.isDebugEnabled()) {
            log.debug("Starting to load the salesforce credentials");
        }
        if (properties.getProperty(SalesforceConstant.CONNECTION_TIMEOUT) == null) {
            connectionTimeout = SalesforceConstant.CONNECTION_TIMEOUT_DEFAULT;
        } else {
            connectionTimeout = Integer.parseInt(properties.getProperty(SalesforceConstant.CONNECTION_TIMEOUT));
        }

        if (properties.getProperty(SalesforceConstant.READ_TIMEOUT) == null) {
            readTimeout = SalesforceConstant.READ_TIMEOUT_DEFAULT;
        } else {
            readTimeout = Integer.parseInt(properties.getProperty(SalesforceConstant.READ_TIMEOUT));
        }

        if (properties.getProperty(SalesforceConstant.WAIT_TIME) == null) {
            connectionTimeout = SalesforceConstant.WAIT_TIME_DEFAULT;
        } else {
            waitTime = Integer.parseInt(properties.getProperty(SalesforceConstant.WAIT_TIME));
        }
    }

    public Object poll() {
        return null;
    }

    /**
     * Injecting the salesforce Stream messages to the ESB sequence
     *
     * @param message the salesforce response status
     */
    private void injectSalesforceMessage(Message message) {
        if (injectingSeq != null) {
            injectMessage(message.toString(), SalesforceConstant.CONTENT_TYPE);
            if (log.isDebugEnabled()) {
                log.debug("injecting salesforce message to the sequence : "
                        + injectingSeq);
            }
        } else {
            handleException("the Sequence is not found");
        }
    }

    private void handleException(String msg, Exception ex) {
        log.error(msg, ex);
        throw new SynapseException(ex);
    }

    private void handleException(String msg) {
        log.error(msg);
        throw new SynapseException(msg);
    }

}
