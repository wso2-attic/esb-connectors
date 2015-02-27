
package org.wso2.carbon.connector.clevertim;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.connector.core.AbstractConnector;

public class CreateCompany extends AbstractConnector {
    
    /**
     * Instance variable to hold the MessageContext object passed in via the Synapse template.
     */
    private MessageContext messageContext;
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param mc Synapse Message Context.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    @Override
    public void connect(final MessageContext mc) {
    
        log.info("MEthod Called");
        this.messageContext = mc;
        
        String errorMessage = null;
        
        try {
            
            org.apache.axis2.context.MessageContext axis2MC = ((Axis2MessageContext) mc).getAxis2MessageContext();
            JsonUtil.newJsonPayload(axis2MC, getJsonPayload(), true, true);
        } catch (JSONException je) {
            errorMessage = Constants.INVALID_JSON_MSG;
            log.error(errorMessage, je);
            storeErrorResponseStatus(errorMessage, Constants.ERROR_CODE_JSON_EXCEPTION);
            handleException(errorMessage, je, mc);
        }
        
    }
    
    /**
     * Create JSON request for CreateCase.
     * 
     * @return JSON payload.
     * @throws JSONException thrown when parsing JSON String.
     */
    private String getJsonPayload() throws JSONException {
    
        JSONObject jsonPayload = new JSONObject();
        
        String companyName = (String) messageContext.getProperty(Constants.COMPANY_NAME);
        if (companyName != null && !companyName.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.COMPANY_NAME, companyName);
        }
        String description = (String) messageContext.getProperty(Constants.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.DESCRIPTION, description);
        }
        String email = (String) messageContext.getProperty(Constants.EMAIL);
        if (email != null && !email.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.EMAIL, new JSONArray(email));
        }
        String phones = (String) messageContext.getProperty(Constants.PHONES);
        if (phones != null && !phones.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.PHONES, new JSONArray(phones));
        }
        String website = (String) messageContext.getProperty(Constants.WEBSITE);
        if (website != null && !website.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.WEBSITE, new JSONArray(website));
        }
        String address = (String) messageContext.getProperty(Constants.ADDRESS);
        if (address != null && !address.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.ADDRESS, address);
        }
        String city = (String) messageContext.getProperty(Constants.CITY);
        if (city != null && !city.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.CITY, city);
        }
        String postCode = (String) messageContext.getProperty(Constants.POST_CODE);
        if (postCode != null && !postCode.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.POST_CODE, postCode);
        }
        String country = (String) messageContext.getProperty(Constants.COUNTRY);
        if (country != null && !country.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.COUNTRY, country);
        }
        String socialMediaIds = (String) messageContext.getProperty(Constants.SOCIAL_MEDIA_IDS);
        if (socialMediaIds != null && !socialMediaIds.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.SOCIAL_MEDIA_IDS, new JSONArray(socialMediaIds));
        }
        String customerType = (String) messageContext.getProperty(Constants.CUSTOMER_TYPE);
        if (customerType != null && !customerType.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.CUSTOMER_TYPE, customerType);
        }
        String customFields = (String) messageContext.getProperty(Constants.CUSTOM_FIELDS);
        if (customFields != null && !customFields.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.CUSTOM_FIELD, new JSONObject(customFields));
        }
        String tags = (String) messageContext.getProperty(Constants.TAGS);
        if (tags != null && !tags.isEmpty()) {
            jsonPayload.put(Constants.JSONKeys.TAGS, new JSONArray(tags));
        }
        
        return jsonPayload.toString();
        
    }
    
    /**
     * Add a <strong>Throwable</strong> to a message context, the message from the throwable is embedded as
     * the Synapse contstant ERROR_MESSAGE.
     * 
     * @param message the error message
     * @param errorCode integer type error code to be added to ERROR_CODE Synapse constant
     */
    private void storeErrorResponseStatus(String message, int errorCode) {
    
        this.messageContext.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        this.messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, message);
        this.messageContext.setFaultResponse(true);
    }
}
