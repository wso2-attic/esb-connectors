package org.wso2.carbon.connector.braintree;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.wso2.carbon.connector.core.AbstractConnector;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Subscription;
import com.braintreegateway.exceptions.BraintreeException;

/**
 * The Class AbstractBrainTreeConnector.
 */
public abstract class AbstractBrainTreeConnector extends AbstractConnector {

    /**
     * Get BrainTree Authenticated and provide the service.
     * 
     * @param messageContext
     *            the message context
     * @return the brain tree service
     * @throws BraintreeException
     *             the braintree exception
     */
    public final BraintreeGateway getBrainTreeService(final MessageContext messageContext) throws BraintreeException {

        Environment environment = null;
        final String evn = (String) messageContext.getProperty(Constants.ENVIRONMENT);

        if (Constants.SANDBOX.equalsIgnoreCase(evn)) {
            environment = Environment.SANDBOX;
        } else if (Constants.DEVELOPMENT.equalsIgnoreCase(evn)) {
            environment = Environment.DEVELOPMENT;
        } else if (Constants.PRODUCTION.equalsIgnoreCase(evn)) {
            environment = Environment.PRODUCTION;
        }
        
        if (environment != null) {
            return new BraintreeGateway(environment, (String) messageContext.getProperty(Constants.MERCHANT_ID),
                    (String) messageContext.getProperty(Constants.PUBLIC_KEY),
                    (String) messageContext.getProperty(Constants.PRIVATE_KEY));            
        } else {
            throw new BraintreeException(Constants.ErrorConstants.INVALID_ENVIRONMENT_MSG);
        }
    }

    /**
     * Gets the calender instance.
     * 
     * @param dateString
     *            the date string
     * @return the calender
     * @throws ParseException
     *             the parse exception
     */
    protected final Calendar getCalender(final String dateString) throws ParseException {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat(Constants.DATE_TIME_FORMATTER).parse(dateString));
        return cal;
    }

    /**
     * Builds the mapping for Subscription.Status values. Assigns a set of
     * Website.Rel values to string keys for ease of use.
     * 
     * @return A map containing the Subscription status values mapped to keys.
     */
    protected final Map<String, Subscription.Status> buildSubscriptionStatusMap() {

        final Map<String, Subscription.Status> subscriptionStatusMap = new HashMap<String, Subscription.Status>();
        Subscription.Status[] statusArray = Subscription.Status.values();
        for (Subscription.Status status : statusArray) {
            subscriptionStatusMap.put(Enum.valueOf(Subscription.Status.class, status.name()).toString(), status);
        }
        return subscriptionStatusMap;
    }

    /**
     * Add a <strong>Throwable</strong> to a message context, the message from
     * the throwable is embedded as the Synapse contstant ERROR_MESSAGE.
     * 
     * @param ctxt
     *            Synapse Message Context to which the error tags need to be
     *            added
     * @param message
     *            the message
     * @param errorCode
     *            integer type error code to be added to ERROR_CODE Synapse
     *            constant
     */
    protected final void storeErrorResponseStatus(final MessageContext ctxt, final String message, final int errorCode) {

        ctxt.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, message);
        ctxt.setFaultResponse(true);
    }

}
