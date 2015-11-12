package org.wso2.carbon.inbound.salesforce.poll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public final class SoapLoginUtil {
    private static final Log log = LogFactory.getLog(SoapLoginUtil.class);

    // The enterprise SOAP API endpoint used for the login call in this example.
    private static final String SERVICES_SOAP_PARTNER_ENDPOINT = "/services/Soap/u/22.0/";

    private static final String ENV_START =
            "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
                    + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
                    "xmlns:urn='urn:partner.soap.sforce.com'><soapenv:Body>";

    private static final String ENV_END = "</soapenv:Body></soapenv:Envelope>";
    private static String sessionId,LoginUrl;

    private static byte[] soapXmlForLogin(String username, String password)
            throws UnsupportedEncodingException {
        return (ENV_START +
                "  <urn:login>" +
                "    <urn:username>" + username + "</urn:username>" +
                "    <urn:password>" + password + "</urn:password>" +
                "  </urn:login>" +
                ENV_END).getBytes("UTF-8");
    }

    public static void login(HttpClient client, String username, String password)
            throws IOException, InterruptedException, SAXException,
            ParserConfigurationException {

        ContentExchange exchange = new ContentExchange();
        exchange.setMethod("POST");
        exchange.setURL(getSoapURL());
        exchange.setRequestContentSource(new ByteArrayInputStream(soapXmlForLogin(
                username, password)));
        exchange.setRequestHeader("Content-Type", "text/xml");
        exchange.setRequestHeader("SOAPAction", "''");
        exchange.setRequestHeader("PrettyPrint", "Yes");

        client.send(exchange);
        exchange.waitForDone();

        String response = exchange.getResponseContent();
        String tagSession = "<sessionId>";
        String tagserverUrl = "<serverUrl>";
        String serverUrl = response.substring(response.indexOf(tagserverUrl) + tagserverUrl.length(), response.indexOf("</serverUrl>"));
        sessionId = response.substring(response.indexOf(tagSession) + tagSession.length(), response.indexOf("</sessionId>"));
        LoginUrl = serverUrl.substring(0, serverUrl.indexOf("/services"));

}
    private static String getSoapURL() throws MalformedURLException {
        return new URL(SalesforceStreamData.loginEndpoint + getSoapUri()).toExternalForm();
    }

    private static String getSoapUri() {
        return SERVICES_SOAP_PARTNER_ENDPOINT;
    }

    public static String getSessionId() {
        return sessionId;
    }
    public static String getEndpoint() {
        return LoginUrl;
    }
}