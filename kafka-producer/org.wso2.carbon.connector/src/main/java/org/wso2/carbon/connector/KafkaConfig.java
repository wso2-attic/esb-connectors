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

import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

/**
 * Kafka producer configuration
 */
public class KafkaConfig extends AbstractConnector {

    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            String serializationClass = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_SERIALIZATION_CLASS);
            String requiredAck = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_REQUIRED_ACK);
            String producerType = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_PRODUCER_TYPE);
            String compressionCodec = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_COMPRESSION_TYPE);
            String keySerializerClass = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_SERIALIZATION_CLASS);
            String partitionClass = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_PARTITION_CLASS);
            String compressedTopics = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_COMPRESSED_TOPIC);
            String messageSendMaxRetries = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_MESSAGE_SEND_MAX_RETRIES);
            String retryBackOff = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_TIME_REFRESH_METADATA);
            String refreshInterval = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_TIME_REFRESH_METADATA_AFTER_TOPIC);
            String bufferingMaxMessages = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_BUFFER_MAX_MESSAGES);
            String batchNoMessages = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_NO_MESSAGE_BATCHED_PRODUCER);
            String sendBufferSize = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_BUFFER_SIZE);
            String requestTimeout = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_REQUEST_TIMEOUT);
            String bufferingMaxTime = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_BUFFER_MAX_TIME);
            String enqueueTimeout = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_ENQUEUE_TIMEOUT);
            String clientId = (String) messageContext.getProperty(KafkaConnectConstants.KAFKA_CLIENT_ID);
            if (StringUtils.isNotEmpty(requiredAck)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_REQUIRED_ACK, requiredAck);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_REQUIRED_ACK,
                        KafkaConnectConstants.DEFAULT_REQUIRED_ACKS);
            }
            if (StringUtils.isNotEmpty(serializationClass)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_SERIALIZATION_CLASS, serializationClass);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_SERIALIZATION_CLASS,
                        KafkaConnectConstants.DEFAULT_SERIALIZATION_CLASS);
            }
            if (StringUtils.isNotEmpty(producerType)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_PRODUCER_TYPE, producerType);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_PRODUCER_TYPE,
                        KafkaConnectConstants.DEFAULT_PRODUCER_TYPE);
            }
            if (StringUtils.isNotEmpty(compressionCodec)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_COMPRESSION_TYPE, compressionCodec);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_COMPRESSION_TYPE,
                        KafkaConnectConstants.DEFAULT_COMPRESSION_TYPE);
            }
            if (StringUtils.isNotEmpty(partitionClass)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_PARTITION_CLASS, partitionClass);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_PARTITION_CLASS,
                        KafkaConnectConstants.DEFAULT_PARTITION_CLASS);
            }
            if (StringUtils.isNotEmpty(keySerializerClass)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_KEY_SERIALIZER_CLASS, keySerializerClass);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_KEY_SERIALIZER_CLASS,
                        KafkaConnectConstants.DEFAULT_KEY_SERIALIZER_CLASS);
            }
            if (StringUtils.isNotEmpty(compressedTopics)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_COMPRESSED_TOPIC, compressedTopics);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_COMPRESSED_TOPIC,
                        KafkaConnectConstants.DEFAULT_COMPRESSED_TOPIC);
            }
            if (StringUtils.isNotEmpty(messageSendMaxRetries)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_MESSAGE_SEND_MAX_RETRIES, messageSendMaxRetries);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_MESSAGE_SEND_MAX_RETRIES,
                        KafkaConnectConstants.DEFAULT_MESSAGE_SEND_MAX_RETRIES);
            }
            if (StringUtils.isNotEmpty(retryBackOff)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_TIME_REFRESH_METADATA, retryBackOff);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_TIME_REFRESH_METADATA,
                        KafkaConnectConstants.DEFAULT_TIME_REFRESH_METADATA);
            }
            if (StringUtils.isNotEmpty(refreshInterval)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_TIME_REFRESH_METADATA_AFTER_TOPIC, refreshInterval);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_TIME_REFRESH_METADATA_AFTER_TOPIC,
                        KafkaConnectConstants.DEFAULT_TIME_REFRESH_METADATA_AFTER_TOPIC);
            }
            if (StringUtils.isNotEmpty(bufferingMaxMessages)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_BUFFER_MAX_MESSAGES, bufferingMaxMessages);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_BUFFER_MAX_MESSAGES,
                        KafkaConnectConstants.DEFAULT_BUFFER_MAX_MESSAGE);
            }
            if (StringUtils.isNotEmpty(bufferingMaxTime)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_BUFFER_MAX_TIME, bufferingMaxTime);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_BUFFER_MAX_TIME,
                        KafkaConnectConstants.DEFAULT_BUFFER_MAX_TIME);
            }
            if (StringUtils.isNotEmpty(batchNoMessages)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_NO_MESSAGE_BATCHED_PRODUCER, batchNoMessages);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_NO_MESSAGE_BATCHED_PRODUCER,
                        KafkaConnectConstants.DEFAULT_NO_MESSAGE_BATCHED_PRODUCER);
            }
            if (StringUtils.isNotEmpty(sendBufferSize)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_BUFFER_SIZE, sendBufferSize);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_BUFFER_SIZE,
                        KafkaConnectConstants.DEFAULT_BUFFER_SIZE);
            }
            if (StringUtils.isNotEmpty(bufferingMaxTime)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_BUFFER_MAX_TIME, bufferingMaxTime);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_BUFFER_MAX_TIME,
                        KafkaConnectConstants.DEFAULT_BUFFER_MAX_TIME);
            }
            if (StringUtils.isNotEmpty(requestTimeout)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_REQUEST_TIMEOUT, requestTimeout);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_REQUEST_TIMEOUT,
                        KafkaConnectConstants.DEFAULT_REQUEST_TIMEOUT);
            }
            if (StringUtils.isNotEmpty(enqueueTimeout)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_ENQUEUE_TIMEOUT, enqueueTimeout);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_ENQUEUE_TIMEOUT,
                        KafkaConnectConstants.DEFAULT_ENQUEUE_TIMEOUT);
            }
            if (StringUtils.isNotEmpty(clientId)) {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_CLIENT_ID, clientId);
            } else {
                messageContext.setProperty(KafkaConnectConstants.KAFKA_CLIENT_ID,
                        KafkaConnectConstants.DEFAULT_CLIENT_ID);
            }

        } catch (Exception e) {
            log.error("Kafka producer connector : Error sending the message to broker lists ");
            throw new ConnectException(e);
        }
    }
}
