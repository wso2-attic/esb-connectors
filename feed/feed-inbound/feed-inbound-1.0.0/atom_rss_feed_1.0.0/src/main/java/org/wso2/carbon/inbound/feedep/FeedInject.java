/*
 * Copyright (c) 2005, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.inbound.feedep;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.builder.Builder;
import org.apache.axis2.builder.BuilderUtil;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.TransportUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * FeedInject uses to mediate the received Feeds
 */
public class FeedInject implements InjectHandler {
    private static final Log log = LogFactory.getLog(FeedInject.class);
    private final String injectingSeq;
    private final String onErrorSeq;
    private final boolean sequential;
    private final SynapseEnvironment synapseEnvironment;
    private final String contentType;

    public FeedInject(String injectingSeq, String onErrorSeq, boolean sequential,
                      SynapseEnvironment synapseEnvironment, String contentType) {
        this.injectingSeq = injectingSeq;
        this.onErrorSeq = onErrorSeq;
        this.sequential = sequential;
        this.synapseEnvironment = synapseEnvironment;
        this.contentType = contentType;
    }

    /**
     * Determine the message builder to use, set the feed message to the
     * message context and
     * inject the message to the sequence
     */
    public boolean invoke(Object object) {
        org.apache.synapse.MessageContext messageContext = null;
        try {
            messageContext = createMessageContext();
            MessageContext axis2MsgCtx = null;
            if (messageContext != null) {
                axis2MsgCtx =
                        ((org.apache.synapse.core.axis2.Axis2MessageContext) messageContext).getAxis2MessageContext();
            }
            // Determine the message builder to use
            Builder builder = null;

            if (axis2MsgCtx != null) {
                builder = BuilderUtil.getBuilderFromSelector(contentType, axis2MsgCtx);
            }
            if (builder == null) {
                if (log.isDebugEnabled()) {
                    log.debug("No message builder found for type '" + contentType + "'. Falling back to SOAP.");
                }
            }
            OMElement documentElement = (OMElement) object;
            messageContext.setEnvelope(TransportUtils.createSOAPEnvelope(documentElement));
        } catch (AxisFault axisFault) {
            log.error("Error while setting message to the message context :: " + axisFault.getMessage(), axisFault);
        }
        // Inject the message to the sequence.
        if (StringUtils.isEmpty(injectingSeq)) {
            log.error("Sequence name not specified. Sequence : " + injectingSeq);
            throw new SynapseException("Sequence name not specified. Sequence : " + injectingSeq);
        }
        SequenceMediator seq = (SequenceMediator) synapseEnvironment.getSynapseConfiguration().getSequence(injectingSeq);
        seq.setErrorHandler(onErrorSeq);
        if (log.isDebugEnabled()) {
            log.debug("injecting message to sequence : " + injectingSeq);
        }
        synapseEnvironment.injectInbound(messageContext, seq, sequential);
        return true;
    }

    /**
     * Create the initial messageContext for feed
     */
    private org.apache.synapse.MessageContext createMessageContext() {
        org.apache.synapse.MessageContext messageContext = synapseEnvironment.createMessageContext();
        MessageContext axis2MsgCtx =
                ((org.apache.synapse.core.axis2.Axis2MessageContext) messageContext).getAxis2MessageContext();
        axis2MsgCtx.setServerSide(true);
        axis2MsgCtx.setMessageID(UUIDGenerator.getUUID());
        messageContext.setProperty(MessageContext.CLIENT_API_NON_BLOCKING, true);
        PrivilegedCarbonContext carbonContext =
                PrivilegedCarbonContext.getThreadLocalCarbonContext();
        axis2MsgCtx.setProperty(MultitenantConstants.TENANT_DOMAIN, carbonContext.getTenantDomain());
        return messageContext;
    }
}