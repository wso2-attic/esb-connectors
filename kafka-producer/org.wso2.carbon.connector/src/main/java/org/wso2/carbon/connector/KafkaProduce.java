/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.apache.axis2.AxisFault;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.*;

/**
 * Produce the messages to the kafka brokers
 */
public class KafkaProduce extends AbstractConnector {
	public void connect(MessageContext messageContext) throws ConnectException {

		SynapseLog log = getLog(messageContext);
		log.auditLog("SEND : send message to  Broker lists");
        //Get the producer with the configuration
		Producer<String, String> producer = KafkaUtils
				.getProducer(messageContext);
		String topic = this.getTopic(messageContext);
		String key = this.getKey(messageContext);
		try {
            String message = this.getMessage(messageContext);
            send(producer, topic, key, message);
        }
        catch (Exception e) {
            log.error("Kafka producer connector : Error sending the message to broker lists ");
			throw new ConnectException(e);
		}
        finally {
            //Close the producer pool connections to all kafka brokers.Also closes the zookeeper client connection if any
            producer.close();
        }
    }

    /**
     * Read the topic from the parameter,if the topic is null, topic is set to test
     */
	private String getTopic(MessageContext messageContext) {
		String topic = KafkaUtils.lookupTemplateParameter(messageContext,
				KafkaConnectConstants.PARAM_TOPIC);
        if(topic==null)
        {
            log.error("Kafka producer connector : The request does not contain the required 'topic' field");
            throw new SynapseException("The request does not contain the required 'topic' field");
        }
		return topic;
	}

    /**
     * Read the key from the parameter
     */
    private String getKey(MessageContext messageContext) {
		String key = KafkaUtils.lookupTemplateParameter(messageContext,
				KafkaConnectConstants.PARAM_KEY);
		return key;
	}

    /**
     * Get the messages from the message context and format the messages
     */
    private String getMessage(MessageContext messageContext) throws AxisFault {
		Axis2MessageContext axisMsgContext = (Axis2MessageContext) messageContext;
		org.apache.axis2.context.MessageContext msgContext = axisMsgContext
				.getAxis2MessageContext();
		String messages = KafkaUtils
				.formatMessage((org.apache.axis2.context.MessageContext) msgContext);
		return messages;
	}

    /**
     * Send the messages to the kafka broker with topic and the key that is optional
     */
    private void send(Producer<String, String> producer, String topic,
			String key, String message) {
		if (key == null) {
			producer.send(new KeyedMessage<String, String>(topic, message));
		} else {
			producer.send(new KeyedMessage<String, String>(topic, key, message));
		}
	}
}