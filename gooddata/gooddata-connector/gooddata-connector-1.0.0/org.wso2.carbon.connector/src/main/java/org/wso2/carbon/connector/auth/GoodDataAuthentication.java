
package org.wso2.carbon.connector.auth;

import java.util.ArrayList;
import java.util.Map;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.constants.GoodDataConstants;
import org.wso2.carbon.connector.core.AbstractConnector;

/**
 * The Class GoodDataAuthentication which helps to generate the ttToken for goodData connector authentication.
 */
public class GoodDataAuthentication extends AbstractConnector {

    /**
     * Connect method which is generating authentication of the connector for each request.
     *
     * @param messageContext ESB messageContext.
     */
    public final void connect(final MessageContext messageContext) {

        final org.apache.axis2.context.MessageContext axis2mc =
                ((Axis2MessageContext) messageContext).getAxis2MessageContext();

        @SuppressWarnings("unchecked")
        final Map<String, ArrayList<String>> headerMap =
                (Map<String, ArrayList<String>>) axis2mc.getProperty(GoodDataConstants.EXCESS_TRANSPORT_HEADERS);
        final ArrayList<String> list = headerMap.get(GoodDataConstants.SET_COOKIE_HEADER_NAME);

        /*
         * An error response is thrown when the set-cookie header value is not present.
         * This would occur when the login API call fails.
         * */
        if (list == null) {
            log.error(GoodDataConstants.INVALID_PARAMETERS);
            storeErrorResponseStatus(messageContext, GoodDataConstants.INVALID_PARAMETERS,
                    GoodDataConstants.ILLEGAL_ARGUMENT_ERROR_CODE);
            handleException(GoodDataConstants.INVALID_PARAMETERS, new IllegalArgumentException(), messageContext);
        } else {
            messageContext.setProperty(GoodDataConstants.SUPER_SECURE_TOKEN_PROPERTY,
                    list.get(GoodDataConstants.HEADER_VALUE_INDEX));
        }
    }


    /**
     * Add a message to message context, the message from the throwable is embedded as the Synapse Constant
     * ERROR_MESSAGE.
     *
     * @param ctxt message context to which the error tags need to be added
     * @param message message to be returned to the user
     * @param errorCode errorCode mapped to the exception
     */
    public final void storeErrorResponseStatus(final MessageContext ctxt, final String message, final int errorCode) {
        ctxt.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, message);
        ctxt.setFaultResponse(true);
    }
}
