/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.braintree;

/**
 * Constants class for Braintree Connector.
 */
public final class Constants {
    
    /**
     * Instantiates a new constants.
     */
    private Constants() {
    
    }
    
    /**
     * Class to contain constants related to JSON keys within client requests.
     */
    public static class JSONKeys {
        
        /**
         * JSON Request Key: is.
         */
        public static final String IS = "is";
        
        /**
         * JSON Request Key: isNot.
         */
        public static final String IS_NOT = "isNot";
        
        /**
         * JSON Request Key: in.
         */
        public static final String IN = "in";
        
        /**
         * JSON Request Key: startsWith.
         */
        public static final String STARTS_WITH = "startsWith";
        
        /**
         * JSON Request Key: endsWith.
         */
        public static final String ENDS_WITH = "endsWith";
        
        /**
         * JSON Request Key: contains.
         */
        public static final String CONTAINS = "contains";
        
        /**
         * JSON Request Key: id.
         */
        public static final String ID = "id";
        
        /**
         * JSON Request Key: gte.
         */
        public static final String GREATER_THAN_OR_EQUAL_TO = "gte";
        
        /**
         * JSON Request Key: lte.
         */
        public static final String LESS_THAN_OR_EQUAL_TO = "lte";
        
        /**
         * JSON Request Key: between.
         */
        public static final String BETWEEN = "between";
        
        /**
         * JSON Request Key: holderName.
         */
        public static final String HOLDER_NAME = "holderName";
        
        /**
         * JSON Request Key: customerLocation.
         */
        public static final String CUSTOMER_LOCATION = "customerLocation";
        
        /**
         * JSON Request Key: type.
         */
        public static final String TYPE = "type";
        
        /**
         * JSON Request Key: firstName.
         */
        public static final String FIRST_NAME = "firstName";
        
        /**
         * JSON Request Key: lastName.
         */
        public static final String LAST_NAME = "lastName";
        
        /**
         * JSON Request Key: streetAddress.
         */
        public static final String STREET_ADDRESS = "streetAddress";
        
        /**
         * JSON Request Key: company.
         */
        public static final String COMPANY = "company";
        
        /**
         * JSON Request Key: countryName.
         */
        public static final String COUNTRY_NAME = "countryName";
        
        /**
         * JSON Request Key: postalCode.
         */
        public static final String POSTAL_CODE = "postalCode";
        
        /**
         * JSON Request Key: failedAt.
         */
        public static final String FAILED_AT = "failedAt";
        
        /**
         * JSON Request Key: gatewayRejectedAt.
         */
        public static final String GATEWAY_REJECTED_AT = "gatewayRejectedAt";
        
        /**
         * JSON Request Key: authorizedAt.
         */
        public static final String AUTHORIZED_AT = "authorizedAt";
        
        /**
         * JSON Request Key: submittedForSettlementAt.
         */
        public static final String SUBMITTED_FOR_SETTLEMENT_AT = "submittedForSettlementAt";
        
        /**
         * JSON Request Key: processorDeclinedAt.
         */
        public static final String PROCESS_DECLINED_AT = "processorDeclinedAt";
        
        /**
         * JSON Request Key: voidedAt.
         */
        public static final String VOIDED_AT = "voidedAt";
        
        /**
         * JSON Request Key: createdAt.
         */
        public static final String CREATED_AT = "createdAt";
        
        /**
         * JSON Request Key: settledAt.
         */
        public static final String SETTLED_AT = "settledAt";
        
        /**
         * JSON Request Key: extendedAddress.
         */
        public static final String EXTENDED_ADDRESS = "extendedAddress";
        
        /**
         * JSON Request Key: locality.
         */
        public static final String LOCALITY = "locality";
        
        /**
         * JSON Request Key: region.
         */
        public static final String REGION = "region";
        
        /**
         * JSON Request Key: fax.
         */
        public static final String FAX = "fax";
        
        /**
         * JSON Request Key: website.
         */
        public static final String WEBSITE = "website";
        
        /**
         * JSON Request Key: phone.
         */
        public static final String PHONE = "phone";
        
        /**
         * JSON Request Key: email.
         */
        public static final String EMAIL = "email";
        
        /**
         * JSON Request Key: revertSubscriptionOnProrationFailure.
         */
        public static final String REVERT_SUBSCRIPTION_ON_PRORATION_FAILURE = "revertSubscriptionOnProrationFailure";
        
        /**
         * JSON Request Key: replaceAllAddOnsAndDiscounts.
         */
        public static final String REPLACE_ALL_ADDONS_AND_DISCOUNTS = "replaceAllAddOnsAndDiscounts";
        
        /**
         * JSON Request Key: action.
         */
        public static final String ACTION = "action";
        
        /**
         * JSON Request Key: inheritedFromId.
         */
        public static final String INHERITED_FROM_ID = "inheritedFromId";
        
        /**
         * JSON Request Key: existingId.
         */
        public static final String EXISTING_ID = "existingId";
        
        /**
         * JSON Request Key: addOnsId.
         */
        public static final String ADDONS_ID = "addOnsId";
        
        /**
         * JSON Request Key: discountId.
         */
        public static final String DISCOUNT_ID = "discountId";
        
        /**
         * JSON Request Key: numberOfBillingCycles.
         */
        public static final String NO_OF_BILLING_CYCLES = "numberOfBillingCycles";
        
        /**
         * JSON Request Key: quantity.
         */
        public static final String QUANTITY = "quantity";
        
        /**
         * JSON Request Key: neverExpires.
         */
        public static final String NEVER_EXPIRES = "neverExpires";
        
        /**
         * JSON Request Key: prorateCharges.
         */
        public static final String PRORATE_CHARGES = "prorateCharges";
        
        /**
         * JSON Request Key: add.
         */
        public static final String ADD = "add";
        
        /**
         * JSON Request Key: neverExpires.
         */
        public static final String UPDATE = "update";
        
        /**
         * JSON Request Key: prorateCharges.
         */
        public static final String REMOVE = "remove";
        
        /**
         * JSON Request Key: planId.
         */
        public static final String PLAN_ID = "planId";
        
        /**
         * JSON Request Key: amount.
         */
        public static final String AMOUNT = "amount";
        
        /**
         * Brain Tree CREDIT_CARD_NO.
         */
        public static final String CREDIT_CARD_NO = "number";
        
        /**
         * Brain Tree CVV.
         */
        public static final String CVV = "cvv";
        
        /**
         * Brain Tree CREDIT_CARD_EXPIRATION_DATE.
         */
        public static final String CREDIT_CARD_EXPIRATION_DATE = "expirationDate";
        
        /**
         * Brain Tree CREDIT_CARD_EXPIRATION_DATE.
         */
        public static final String CREDIT_CARD_EXPIRATION_MONTH = "expirationMonth";
        
        /**
         * Brain Tree CREDIT_CARD_EXPIRATION_DATE.
         */
        public static final String CREDIT_CARD_EXPIRATION_YEAR = "expirationYear";
        
        
        /**
         * Brain Tree COUNTRY_CODE_ALPHA3.
         */
        public static final String COUNTRY_CODE_ALPHA3 = "countryCodeAlpha3";
        
        /**
         * Brain Tree BILLING_COUNTRY_CODE_NUMERIC.
         */
        public static final String BILLING_COUNTRY_CODE_NUMERIC = "countryCodeNumeric";
        
        /**
         * Brain Tree COUNTRY_CODE_ALPHA2.
         */
        public static final String COUNTRY_CODE_ALPHA2 = "countryCodeAlpha2";
        
        /**
         * Brain Tree SUBMIT_FOR_SETTLEMENT.
         */
        public static final String SUBMIT_FOR_SETTLEMENT = "submitForSettlement";
        
        /**
         * Brain Tree STORE_IN_VAULT.
         */
        public static final String STORE_IN_VAULT = "storeInVault";
        
        /**
         * Brain Tree ADD_BILLING_ADDRESS_TO_PAYMENT.
         */
        public static final String ADD_BILLING_ADDRESS_TO_PAYMENT = "addBillingAddressToPaymentMethod";
        
        /**
         * Brain Tree STORE_SHIPPING_ADDRESS_IN_VAULT.
         */
        public static final String STORE_SHIPPING_ADDRESS_IN_VAULT = "storeShippingAddressInVault";
        
        /**
         * Brain Tree CARD_HOLDER_NAME.
         */
        public static final String CARD_HOLDER_NAME = "cardholderName";
        
        /**
         * Brain Tree TOKEN.
         */
        public static final String TOKEN = "token";
        
        /**
         * Brain Tree INDIVIDUAL_FIRST_NAME.
         */
        public static final String INDIVIDUAL_FIRST_NAME = "individualFirstName";
        
        /**
         * Brain Tree INDIVIDUAL_LAST_NAME.
         */
        public static final String INDIVIDUAL_LAST_NAME = "individualLastName";
        
        /**
         * Brain Tree INDIVIDUAL_EMAIL.
         */
        public static final String INDIVIDUAL_EMAIL = "individualEmail";
        
        /**
         * Brain Tree INDIVIDUAL_PHONE.
         */
        public static final String INDIVIDUAL_PHONE = "individualPhone";
        
        /**
         * Brain Tree INDIVIDUAL_DATE_OF_BIRTH.
         */
        public static final String INDIVIDUAL_DATE_OF_BIRTH = "individualDateOfBirth";
        
        /**
         * Brain Tree INDIVIDUAL_SSN.
         */
        public static final String INDIVIDUAL_SSN = "individualSsn";
        
        /**
         * Brain Tree INDIVIDUAL_STREET_ADDRESS.
         */
        public static final String INDIVIDUAL_STREET_ADDRESS = "individualStreetAddress";
        
        /**
         * Brain Tree INDIVIDUAL_LOCALITY.
         */
        public static final String INDIVIDUAL_LOCALITY = "individualLocality";
        
        /**
         * Brain Tree INDIVIDUAL_REGION.
         */
        public static final String INDIVIDUAL_REGION = "individualRegion";
        
        /**
         * Brain Tree INDIVIDUAL_POSTAL_CODE.
         */
        public static final String INDIVIDUAL_POSTAL_CODE = "individualPostalCode";
        
        /**
         * Brain Tree FUNDING_DESTINATION.
         */
        public static final String FUNDING_DESTINATION = "fundingDestination";
        
        /**
         * Brain Tree FUNDING_EMAIL.
         */
        public static final String FUNDING_EMAIL = "fundingEmail";
        
        /**
         * Brain Tree FUNDING_MOBILE_PHONE.
         */
        public static final String FUNDING_MOBILE_PHONE = "fundingMobilePhone";
        
        /**
         * Brain Tree FUNDING_ACCOUNT_NUMBER.
         */
        public static final String FUNDING_ACCOUNT_NUMBER = "fundingAccountNumber";
        
        /**
         * Brain Tree FUNDING_ROUTING_NUMBER.
         */
        public static final String FUNDING_ROUTING_NUMBER = "fundingRoutingNumber";
        
        /**
         * Brain Tree BUSSINESS_LEGAL_NAME.
         */
        public static final String BUSSINESS_LEGAL_NAME = "businessLegalName";
        
        /**
         * Brain Tree BUSSINESS_DBA_NAME.
         */
        public static final String BUSSINESS_DBA_NAME = "businessDbaName";
        
        /**
         * Brain Tree BUSSINESS_TAX_ID.
         */
        public static final String BUSSINESS_TAX_ID = "businessTaxId";
        
        /**
         * Brain Tree BUSSINESS_STREET_ADDRESS.
         */
        public static final String BUSSINESS_STREET_ADDRESS = "businessStreetAddress";
        
        /**
         * Brain Tree BUSSINESS_LOCALITY.
         */
        public static final String BUSSINESS_LOCALITY = "businessLocality";
        
        /**
         * Brain Tree BUSSINESS_REGION.
         */
        public static final String BUSSINESS_REGION = "businessRegion";
        
        /**
         * Brain Tree FUNDING_ROUTING_NUMBER.
         */
        public static final String BUSSINESS_POSTAL_CODE = "businessPostalCode";
        
        /**
         * Brain Tree INDIVIDUAL_ADDRESS.
         */
        public static final String INDIVIDUAL_ADDRESS = "individualAddress";
        
        /**
         * Brain Tree FUNDING_ROUTING_NUMBER.
         */
        public static final String BUSSINESS_ADDRESS = "businessAddress";
        
        /**
         * Brain Tree WEB_SITE.
         */
        public static final String WEB_SITE = "website";
        
        /**
         * Brain Tree MONTHS.
         */
        public static final String MONTHS = "months";
        
        /**
         * Brain Tree DAYS.
         */
        public static final String DAYS = "days";
        
        /**
         * Brain Tree START_IMMEDIATELY.
         */
        public static final String START_IMMEDIATELY = "startImmediately";
        
        /**
         * Brain Tree FIRST_BILLING_DATE.
         */
        public static final String FIRST_BILLING_DATE = "firstBillingDate";
        
        /**
         * Brain Tree BILLING_DAY_OF_MONTH.
         */
        public static final String BILLING_DAY_OF_MONTH = "billingDayOfMonth";
        
        /**
         * JSON Request Key: dateOfBirth.
         */
        public static final String DATE_OF_BIRTH = "dateOfBirth";
        
        /**
         * JSON Request Key: address.
         */
        public static final String ADDRESS = "address";
        
        /**
         * JSON Request Key: phone.
         */
        public static final String SSN = "ssn";
        
        /**
         * Brain Tree LEGAL_NAME.
         */
        public static final String LEGAL_NAME = "legalName";
        
        /**
         * Brain Tree DBA_NAME.
         */
        public static final String DBA_NAME = "dbaName";
        
        /**
         * Brain Tree TAX_ID.
         */
        public static final String TAX_ID = "taxId";
        
        /**
         * Brain Tree DESTINATION.
         */
        public static final String DESTINATION = "destination";
        
        /**
         * Brain Tree MOBILE_PHONE.
         */
        public static final String MOBILE_PHONE = "mobilePhone";
        
        /**
         * Brain Tree ACCOUNT_NUMBER.
         */
        public static final String ACCOUNT_NUMBER = "accountNumber";
        
        /**
         * Brain Tree ROUTING_NUMBER.
         */
        public static final String ROUTING_NUMBER = "routingNumber";
        
    }
    
    /**
     * Brain Tree CREDIT_CARD_EXPIRATION_DATE.
     */
    public static final String CREDIT_CARD_EXPIRATION_MONTH = "uri.var.expirationMonth";
    
    /**
     * Brain Tree CREDIT_CARD_EXPIRATION_DATE.
     */
    public static final String CREDIT_CARD_EXPIRATION_YEAR = "uri.var.expirationYear";
    
    /**
     * Brain Tree BUSINESS_DETAILS.
     */
    public static final String BUSINESS_DETAILS = "uri.var.businessDetails";
    
    /**
     * Brain Tree CREDIT_CARD_FIELDS.
     */
    public static final String CREDIT_CARD_FIELDS = "uri.var.creditCardFields";
    
    /**
     * Brain Tree CUSTOMER_FIELDS.
     */
    public static final String CUSTOMER_FIELDS = "uri.var.customerFields";
    
    /**
     * Brain Tree DATE_TIME_FORMATTER.
     */
    public static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * BrainTree Date formatter for Search dates in Subscriptions.
     */
    public static final String SEARCH_DATE_FORMATTER = "yyyy-MM-dd";
    
    /**
     * Brain Tree DATE_FORMATTER.
     */
    public static final String DATE_FORMATTER = "M/dd/yyyy";
    
    /**
     * Brain Tree PROCESSOR_AUTHORIZATION_CODE.
     */
    public static final String PROCESSOR_AUTHORIZATION_CODE = "uri.var.processorAuthorizationCode";
    
    /**
     * Brain Tree STATUS_CHANGES.
     */
    public static final String STATUS_CHANGES = "uri.var.statusChanges";
    
    /**
     * Brain Tree SHIPPING_ADDRESS_FIELDS.
     */
    public static final String SHIPPING_ADDRESS_FIELDS = "uri.var.shippingAddressFields";
    
    /**
     * Brain Tree BILLING_ADDRESS_FIELDS.
     */
    public static final String BILLING_ADDRESS_FIELDS = "uri.var.billingAddressFields";
    
    /**
     * Brain Tree SEPERATOR.
     */
    public static final String SEPERATOR = ",";
    
    /**
     * Constant for result.
     */
    public static final String RESULT = "result";
    
    /**
     * Brain Tree NEW_SUBSCRIPTION_ID.
     */
    public static final String NEW_SUBSCRIPTION_ID = "uri.var.newSubscriptionId";
    
    /**
     * Brain Tree PAYMENT_METHOD_NONCE.
     */
    public static final String PAYMENT_METHOD_NONCE = "uri.var.paymentMethodNonce";
    
    /**
     * Brain Tree PLAN_ID_ESB.
     */
    public static final String PLAN_ID_ESB = "uri.var.planId";
    
    /**
     * Brain Tree PRICE.
     */
    public static final String PRICE = "uri.var.price";
    
    /**
     * Brain Tree BILLING_DAY_OF_MONTH.
     */
    public static final String BILLING_DAY_OF_MONTH = "uri.var.billingDayOfMonth";
    
    /**
     * Brain Tree FIRST_BILLING_DATE.
     */
    public static final String FIRST_BILLING_DATE = "uri.var.firstBillingDate";
    
    /**
     * Brain Tree TRIAL_DURATION.
     */
    public static final String TRIAL_DURATION = "uri.var.trialDuration";
    
    /**
     * Brain Tree TRIAL_DURATION_UNIT.
     */
    public static final String TRIAL_DURATION_UNIT = "uri.var.trialDurationUnit";
    
    /**
     * Brain Tree HAS_TRIAL_PERIOD.
     */
    public static final String HAS_TRIAL_PERIOD = "uri.var.hasTrialPeriod";
    
    /**
     * Brain Tree NEVER_EXPIRES.
     */
    public static final String NEVER_EXPIRES = "uri.var.neverExpires";
    
    /**
     * Brain Tree NUMBER_OF_BILLING_CYCLES.
     */
    public static final String NUMBER_OF_BILLING_CYCLES = "uri.var.numberOfBillingCycles";
    
    /**
     * Brain Tree OPTIONS.
     */
    public static final String OPTIONS = "uri.var.options";
    
    /**
     * Brain Tree DISCOUNTS.
     */
    public static final String DISCOUNTS = "uri.var.discounts";
    
    /**
     * Brain Tree ADDONS.
     */
    public static final String ADDONS = "uri.var.addOns";
    
    /**
     * Brain Tree TRIAL_PERIOD.
     */
    public static final String TRIAL_PERIOD = "uri.var.trialPeriod";
    
    /**
     * Brain Tree BILLING_DETAILS.
     */
    public static final String BILLING_DETAILS = "uri.var.billingDetails";
    
    /**
     * Brain Tree BILLING_ADDRESS.
     */
    public static final String BILLING_ADDRESS = "uri.var.billingAddress";
    
    /**
     * Brain Tree CUSTOMER_DETAILS.
     */
    public static final String CUSTOMER_DETAILS = "uri.var.customer";
    
    /**
     * Brain Tree SHIPPING_ADDRESS.
     */
    public static final String SHIPPING_ADDRESS = "uri.var.shippingAddress";
    
    /**
     * Brain Tree CREDIT_CARD.
     */
    public static final String CREDIT_CARD = "uri.var.creditCard";
    
    /**
     * Brain Tree MERCHANT ID.
     */
    public static final String MERCHANT_ID = "uri.var.merchantId";
    
    /**
     * Brain Tree PRIVATE KEY.
     */
    public static final String PRIVATE_KEY = "uri.var.privateKey";
    
    /**
     * Brain Tree PUBLIC KEY.
     */
    public static final String PUBLIC_KEY = "uri.var.publicKey";
    
    /**
     * Brain Tree ENVIRONMENT.
     */
    public static final String ENVIRONMENT = "uri.var.environment";
    
    /**
     * Brain Tree ENVIRONMENT SANDBOX.
     */
    public static final String SANDBOX = "SANDBOX";
    
    /**
     * Brain Tree ENVIRONMENT DEVELOPMENT.
     */
    public static final String DEVELOPMENT = "DEVELOPMENT";
    
    /**
     * Brain Tree ENVIRONMENT PRODUCTION.
     */
    public static final String PRODUCTION = "PRODUCTION";
    
    /**
     * Brain Tree AMOUNT.
     */
    public static final String AMOUNT = "uri.var.amount";
    
    /**
     * Brain Tree CREDIT_CARD_NO.
     */
    public static final String CREDIT_CARD_NO = "uri.var.creditCardNumber";
    
    /**
     * Brain Tree CREDIT_CARD_EXPIRE_MONTH.
     */
    public static final String CREDIT_CARD_EXPIRE_MONTH = "uri.var.creditCardExpirationMonth";
    
    /**
     * Brain Tree CREDIT_CARD_EXPIRE_YEAR.
     */
    public static final String CREDIT_CARD_EXPIRE_YEAR = "uri.var.creditCardExpirationYear";
    
    /**
     * Brain Tree ORDER_ID.
     */
    public static final String ORDER_ID = "uri.var.orderId";
    
    /**
     * Brain Tree PAYMENT_METHOD_TOKEN.
     */
    public static final String PAYMENT_METHOD_TOKEN = "uri.var.paymentMethodToken";
    
    /**
     * Brain Tree CVV.
     */
    public static final String CVV = "uri.var.cvv";
    
    /**
     * Brain Tree CREDIT_CARD_EXPIRATION_DATE.
     */
    public static final String CREDIT_CARD_EXPIRATION_DATE = "uri.var.expirationDate";
    
    /**
     * Brain Tree MERCHANT_ACCOUNT_ID.
     */
    public static final String MERCHANT_ACCOUNT_ID = "uri.var.merchantAccountId";
    
    /**
     * Brain Tree SUBSCRIPTION ID.
     */
    public static final String SUBSCRIPTION_ID = "uri.var.subscriptionId";
    
    /**
     * Brain Tree DONOT_INHERIT_ADDONS_DISCOUNTS.
     */
    public static final String DONOT_INHERIT_ADDONS_DISCOUNTS = "uri.var.doNotInheritAddOnsOrDiscounts";
    
    /**
     * Brain Tree customer ID.
     */
    public static final String ID = "uri.var.id";
    
    /**
     * Brain Tree CUSTOMER_FIRST_NAME.
     */
    public static final String CUSTOMER_FIRST_NAME = "uri.var.customerFirstName";
    
    /**
     * Brain Tree CUSTOMER_LAST_NAME.
     */
    public static final String CUSTOMER_LAST_NAME = "uri.var.customerLastName";
    
    /**
     * Brain Tree CUSTOMER_FIRST_NAME.
     */
    public static final String CUSTOMER_COMPANY = "uri.var.customerCompany";
    
    /**
     * Brain Tree CUSTOMER_PHONE.
     */
    public static final String CUSTOMER_PHONE = "uri.var.customerPhone";
    
    /**
     * Brain Tree CUSTOMER_FAX.
     */
    public static final String CUSTOMER_FAX = "uri.var.customerFax";
    
    /**
     * Brain Tree CUSTOMER_WEB_SITE.
     */
    public static final String CUSTOMER_WEB_SITE = "uri.var.customerWebsite";
    
    /**
     * Brain Tree CUSTOMER_EMAIL.
     */
    public static final String CUSTOMER_EMAIL = "uri.var.customerEmail";
    
    /**
     * Brain Tree BILLING_COUNTRY_NAME.
     */
    public static final String BILLING_COUNTRY_NAME = "uri.var.countryName";
    
    /**
     * Brain Tree BILLING_COUNTRY_CODE_ALPHA3.
     */
    public static final String BILLING_COUNTRY_CODE_ALPHA3 = "uri.var.billingCountryCodeAlpha3";
    
    /**
     * Brain Tree BILLING_COUNTRY_CODE_NUMERIC.
     */
    public static final String BILLING_COUNTRY_CODE_NUMERIC = "uri.var.countryCodeNumeric";
    
    /**
     * Brain Tree SHIPPING_FIRST_NAME.
     */
    public static final String SHIPPING_FIRST_NAME = "uri.var.shippingFirstName";
    
    /**
     * Brain Tree SHIPPING_FIRST_NAME.
     */
    public static final String SHIPPING_LAST_NAME = "uri.var.shippingLastName";
    
    /**
     * Brain Tree SHIPPING_COMPANY.
     */
    public static final String SHIPPING_COMPANY = "uri.var.shippingCompany";
    
    /**
     * Brain Tree SHIPPING_STREET_ADDRESS.
     */
    public static final String SHIPPING_STREET_ADDRESS = "uri.var.shippingStreetAddress";
    
    /**
     * Brain Tree SHIPPING_EXTENDED_ADDRESS.
     */
    public static final String SHIPPING_EXTENDED_ADDRESS = "uri.var.shippingExtendedAddress";
    
    /**
     * Brain Tree SHIPPING_LOCALITY.
     */
    public static final String SHIPPING_LOCALITY = "uri.var.shippingLocality";
    
    /**
     * Brain Tree SHIPPING_REGION.
     */
    public static final String SHIPPING_REGION = "uri.var.shippingRegion";
    
    /**
     * Brain Tree SHIPPING_POSTAL_CODE.
     */
    public static final String SHIPPING_POSTAL_CODE = "uri.var.shippingPostalCode";
    
    /**
     * Brain Tree SHIPPING_COUNTRY_CODE_ALPHA2.
     */
    public static final String SHIPPING_COUNTRY_CODE_ALPHA2 = "uri.var.shippingCountryCodeAlpha2";
    
    /**
     * Brain Tree SUBMIT_FOR_SETTLEMENT.
     */
    public static final String SUBMIT_FOR_SETTLEMENT = "uri.var.submitForSettlement";
    
    /**
     * Brain Tree STORE_IN_VAULT.
     */
    public static final String STORE_IN_VAULT = "uri.var.storeInVault";
    
    /**
     * Brain Tree ADD_BILLING_ADDRESS_TO_PAYMENT.
     */
    public static final String ADD_BILLING_ADDRESS_TO_PAYMENT = "uri.var.addBillingAddressToPaymentMethod";
    
    /**
     * Brain Tree STORE_SHOPPING_ADDRESS_IN_VAULT.
     */
    public static final String STORE_SHOPPING_ADDRESS_IN_VAULT = "uri.var.storeShippingAddressInVault";
    
    /**
     * Brain Tree CHANNEL.
     */
    public static final String CHANNEL = "uri.var.channel";
    
    /**
     * Brain Tree RECURRING.
     */
    public static final String RECURRING = "uri.var.recurring";
    
    /**
     * Plan ID for subscriptions.
     */
    public static final String PLAN_ID = "uri.var.planId";
    
    /**
     * Whether a subscription is in its trial period.
     */
    public static final String IN_TRIAL_PERIOD = "uri.var.inTrialPeriod";
    
    /**
     * Status for subscriptions.
     */
    public static final String STATUS = "uri.var.status";
    
    /**
     * Days past due for subscriptions.
     */
    public static final String DAYS_PAST_DUE = "uri.var.daysPastDue";
    
    /**
     * Next billing date for subscriptions.
     */
    public static final String NEXT_BILLING_DATE = "uri.var.nextBillingDate";
    
    /**
     * Billing cycles remaining on a subscription.
     */
    public static final String BILLING_CYCLES_REMAINING = "uri.var.billingCyclesRemaining";
    
    /**
     * Brain Tree CUSTOMER_ID.
     */
    public static final String CUSTOMER_ID = "uri.var.customerId";
    
    /**
     * Brain Tree NUMBER.
     */
    public static final String NUMBER = "uri.var.number";
        
    
    /**
     * Brain Tree CARD_HOLDER_NAME.
     */
    public static final String CARD_HOLDER_NAME = "uri.var.cardholderName";
    
    /**
     * Brain Tree TOKEN.
     */
    public static final String TOKEN = "uri.var.token";
    
    /**
     * Brain Tree BILLING_FIRST_NAME.
     */
    public static final String BILLING_FIRST_NAME = "uri.var.billingFirstName";
    
    /**
     * Brain Tree BILLING_LAST_NAME.
     */
    public static final String BILLING_LAST_NAME = "uri.var.billingLastName";
    
    /**
     * Brain Tree BILLING_COMPANY.
     */
    public static final String BILLING_COMPANY = "uri.var.billingCompany";
    
    /**
     * Brain Tree BILLING_STREET_ADDRESS.
     */
    public static final String BILLING_STREET_ADDRESS = "uri.var.billingStreetAddress";
    
    /**
     * Brain Tree BILLING_EXTENDED_ADDRESS.
     */
    public static final String BILLING_EXTENDED_ADDRESS = "uri.var.billingExtendedAddress";
    
    /**
     * Brain Tree BILLING_LOCALITY.
     */
    public static final String BILLING_LOCALITY = "uri.var.billingLocality";
    
    /**
     * Brain Tree BILLING_REGION.
     */
    public static final String BILLING_REGION = "uri.var.billingRegion";
    
    /**
     * Brain Tree BILLING_POSTAL_CODE.
     */
    public static final String BILLING_POSTAL_CODE = "uri.var.billingPostalCode";
    
    /**
     * Brain Tree BILLING_COUNTRY_CODE_ALPHA2.
     */
    public static final String BILLING_COUNTRY_CODE_ALPHA2 = "uri.var.billingCountryCodeAlpha2";
    
    /**
     * Brain Tree BILLING_ADDRESS_ID.
     */
    public static final String BILLING_ADDRESS_ID = "uri.var.billingAddressId";
    
    /**
     * Brain Tree UPDATE EXISTING.
     */
    public static final String UPDATE_EXISTING = "uri.var.updateExisting";
    
    /**
     * Brain Tree MAKE_DEFAULT.
     */
    public static final String MAKE_DEFAULT = "uri.var.makeDefault";
    
    /**
     * Brain Tree FAIL_ON_DUPLICATE_PAYMENT_METHOD.
     */
    public static final String FAIL_ON_DUPLICATE_PAYMENT_METHOD = "uri.var.failOnDuplicatePaymentMethod";
    
    /**
     * Brain Tree TRANSACTIONID.
     */
    public static final String TRANSACTIONID = "uri.var.transactionId";
    
    /**
     * Brain Tree CUSTOM_FIELD.
     */
    public static final String CUSTOM_FIELDS = "uri.var.customFields";
    
    /**
     * Brain Tree SUBSCRIPTIONID.
     */
    public static final String SUBSCRIPTIONID = "uri.var.subscriptionId";
    
    /**
     * Brain Tree TOS_ACCEPTED.
     */
    public static final String TOS_ACCEPTED = "uri.var.tosAccepted";
    
    /**
     * Brain Tree MASTER_MERCHANT_ACCOUNT_ID.
     */
    public static final String MASTER_MERCHANT_ACCOUNT_ID = "uri.var.masterMerchantAccountId";
    
    /**
     * Brain Tree INDIVIDUAL_FIRST_NAME.
     */
    public static final String INDIVIDUAL_FIRST_NAME = "uri.var.individualFirstName";
    
    /**
     * Brain Tree INDIVIDUAL_LAST_NAME.
     */
    public static final String INDIVIDUAL_LAST_NAME = "uri.var.individualLastName";
    
    /**
     * Brain Tree INDIVIDUAL_EMAIL.
     */
    public static final String INDIVIDUAL_EMAIL = "uri.var.individualEmail";
    
    /**
     * Brain Tree INDIVIDUAL_PHONE.
     */
    public static final String INDIVIDUAL_PHONE = "uri.var.individualPhone";
    
    /**
     * Brain Tree INDIVIDUAL_DATE_OF_BIRTH.
     */
    public static final String INDIVIDUAL_DATE_OF_BIRTH = "uri.var.individualDateOfBirth";
    
    /**
     * Brain Tree INDIVIDUAL_SSN.
     */
    public static final String INDIVIDUAL_SSN = "uri.var.individualSsn";
    
    /**
     * Brain Tree INDIVIDUAL_STREET_ADDRESS.
     */
    public static final String INDIVIDUAL_STREET_ADDRESS = "uri.var.individualStreetAddress";
    
    /**
     * Brain Tree INDIVIDUAL_LOCALITY.
     */
    public static final String INDIVIDUAL_LOCALITY = "uri.var.individualLocality";
    
    /**
     * Brain Tree INDIVIDUAL_REGION.
     */
    public static final String INDIVIDUAL_REGION = "uri.var.individualRegion";
    
    /**
     * Brain Tree INDIVIDUAL_POSTAL_CODE.
     */
    public static final String INDIVIDUAL_POSTAL_CODE = "uri.var.individualPostalCode";
    
    /**
     * Brain Tree FUNDING_DESTINATION.
     */
    public static final String FUNDING_DESTINATION = "uri.var.fundingDestination";
    
    /**
     * Brain Tree FUNDING_EMAIL.
     */
    public static final String FUNDING_EMAIL = "uri.var.fundingEmail";
    
    /**
     * Brain Tree FUNDING_MOBILE_PHONE.
     */
    public static final String FUNDING_MOBILE_PHONE = "uri.var.fundingMobilePhone";
    
    /**
     * Brain Tree FUNDING_ACCOUNT_NUMBER.
     */
    public static final String FUNDING_ACCOUNT_NUMBER = "uri.var.fundingAccountNumber";
    
    /**
     * Brain Tree FUNDING_ROUTING_NUMBER.
     */
    public static final String FUNDING_ROUTING_NUMBER = "uri.var.fundingRoutingNumber";
    
    /**
     * Brain Tree BUSSINESS_LEGAL_NAME.
     */
    public static final String BUSSINESS_LEGAL_NAME = "uri.var.businessLegalName";
    
    /**
     * Brain Tree BUSSINESS_DBA_NAME.
     */
    public static final String BUSSINESS_DBA_NAME = "uri.var.businessDbaName";
    
    /**
     * Brain Tree BUSSINESS_TAX_ID.
     */
    public static final String BUSSINESS_TAX_ID = "uri.var.businessTaxId";
    
    /**
     * Brain Tree BUSSINESS_STREET_ADDRESS.
     */
    public static final String BUSSINESS_STREET_ADDRESS = "uri.var.businessStreetAddress";
    
    /**
     * Brain Tree BUSSINESS_LOCALITY.
     */
    public static final String BUSSINESS_LOCALITY = "uri.var.businessLocality";
    
    /**
     * Brain Tree BUSSINESS_REGION.
     */
    public static final String BUSSINESS_REGION = "uri.var.businessRegion";
    
    /**
     * Brain Tree FUNDING_ROUTING_NUMBER.
     */
    public static final String BUSSINESS_POSTAL_CODE = "uri.var.businessPostalCode";
    
    /**
     * Brain Tree INDIVIDUAL_DETAILS.
     */
    public static final String INDIVIDUAL_DETAILS = "uri.var.individualDetails";
    
    /**
     * Brain Tree BUSSINESS_DETAILS.
     */
    public static final String BUSSINESS_DETAILS = "uri.var.businessDetails";
    
    /**
     * Brain Tree FUNDING.
     */
    public static final String FUNDING = "uri.var.funding";
    
    /**
     * Brain Tree DATE.
     */
    public static final String DATE = "uri.var.date";
    
    /**
     * Brain Tree CUSTOM_FIELD.
     */
    public static final String CUSTOM_FIELD = "uri.var.customField";
    
    /**
     * Brain Tree PARTIAL_AMOUNT.
     */
    public static final String PARTIAL_AMOUNT = "uri.var.partialAmount";
    
    /**
     * The Class ErrorConstants.
     */
    public static class ErrorConstants {
        
        /**
         * Error code constant for number formatting errors.
         */
        public static final int ERROR_CODE_NUMBER_FORMAT_EXCEPTION = 700010;
        
        /**
         * Error code constant for run time exception.
         */
        public static final int ERROR_CODE_RUNTIME_EXCEPTION = 900000;
        
        /**
         * Error code constant for service exception.
         */
        public static final int ERROR_CODE_NOT_FOUND_EXCEPTION = 800004;
        
        /**
         * Error code constant for autherization exception.
         */
        public static final int ERROR_CODE_AUTHERIZATION_EXCEPTION = 800003;
        
        /**
         * Error code constant for XML Streaming exception.
         */
        public static final int ERROR_CODE_AUTHENTICATION_EXCEPTION = 800005;
        
        /**
         * Error code constant for run time exception.
         */
        public static final int ERROR_CODE_BRAINTREE_EXCEPTION = 800006;
        
        /**
         * Error code constant for JSON building exception.
         */
        public static final int ERROR_CODE_JSON_EXCEPTION = 700003;
        
        /**
         * Error code constant for service exception.
         */
        public static final int ERROR_CODE_SERVICE_EXCEPTION = 700004;
        
        /**
         * Error code constant for Parser Exception.
         */
        public static final int ERROR_CODE_PARSER_EXCEPTION = 700004;
        
        /**
         * Invalid number format constant.
         */
        public static final String INVALID_NUMBER_FORMAT_MSG = "The format of the input provided is invalid";
        
        /**
         * Invalid empty element constant.
         */
        public static final String INVALID_EMPTY_ELEMENT_MSG = "Null element is assigned.";
        
        /**
         * Invalid Environment constant.
         */
        public static final String INVALID_ENVIRONMENT_MSG = "Invalid value for environment";
        
        /**
         * Invalid Authorization constant.
         */
        public static final String INVALID_AUTHERIZATION_MSG = "Authorization failed.";
        
        /**
         * Invalid Authentication constant.
         */
        public static final String INVALID_AUTHENTICATION_MSG = "Authentication failed.";
        
        /**
         * Invalid Resource constant.
         */
        public static final String INVALID_RESOURCE_MSG = "Resource not found.";
        
        /**
         * Generic error constant.
         */
        public static final String GENERIC_ERROR_MSG = "Error occured in connector: ";
        
        /**
         * Invalid JSON constant.
         */
        public static final String INVALID_JSON_MSG = "Failed to read JSON message.";
        
        /**
         * Error message for parser exception.
         */
        public static final String PARSER_EXCEPTION_MSG = "Error occured during parsing data";
        
    }
}
