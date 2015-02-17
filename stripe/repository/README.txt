copy wso2esb.4.8.1.zip file here...
Make sure your axis2.xml contains following entries.

    <messageFormatter contentType="multipart/form-data" class="org.apache.axis2.transport.http.MultipartFormDataFormatter"/>

    <messageBuilder contentType="multipart/form-data" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

Be sure to import the Stripe certificate to your ESB client keystore.
    You can follow the following steps to import your Stripe certificates into wso2esb client’s keystore as follows:
        1. Go to https://stripe.com/docs/connect/oauth in your browser, and then click the HTTPS trust icon on the address bar (e.g., the padlock next to the URL in Firefox).
        2. View the certificate details (the steps vary by browser) and then export the trust certificate to the file system.
        3. Use the ESB Management Console or the following command to import that certificate into the ESB client keystore.
            keytool -importcert -file <certificate file> -keystore <ESB>/repository/resources/security/client-truststore.jks -alias "StripeTrustCertImport"
        4. Restart the server and deploy the Stripe configuration.
