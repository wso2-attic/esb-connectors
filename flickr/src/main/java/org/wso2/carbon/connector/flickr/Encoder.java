package org.wso2.carbon.connector.flickr;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by nalin on 4/7/14.
 */
public class Encoder extends AbstractMediator{

    private static final String ENC = "UTF-8";

    public boolean mediate(MessageContext msgctx) {

        String unencoded = msgctx.getProperty("flickr.unencoded").toString();
        try {
            String encoded = URLEncoder.encode(unencoded, ENC);
            encoded = encoded.replace("+","%20");
            msgctx.setProperty("flickr.encoded", encoded);
        } catch (UnsupportedEncodingException e) {
            throw new SynapseException(e);
        }
        return true;
    }
}
