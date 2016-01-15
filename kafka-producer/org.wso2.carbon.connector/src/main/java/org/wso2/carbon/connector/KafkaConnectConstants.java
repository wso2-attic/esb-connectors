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

public class KafkaConnectConstants {

    // Configuration properties keys
    public static final String BROKER_LIST = "metadata.broker.list";
    public static final String REQUIRED_ACK = "request.required.acks";
    public static final String PRODUCER_TYPE = "producer.type";
    public static final String SERIALIZATION_CLASS = "serializer.class";
    public static final String KEY_SERIALIZER_CLASS = "key.serializer.class";
    public static final String PARTITION_CLASS = "partitioner.class";
    public static final String COMPRESSION_TYPE = "compression.codec";
    public static final String COMPRESSED_TOPIC = "compressed.topics";
    public static final String MESSAGE_SEND_MAX_RETRIES = "message.send.max.retries";
    public static final String TIME_REFRESH_METADATA = "retry.backoff.ms";
    public static final String TIME_REFRESH_METADATA_AFTER_TOPIC = "topic.metadata.refresh.interval.ms";
    public static final String BUFFER_MAX_TIME = "queue.buffering.max.ms";
    public static final String BUFFER_MAX_MESSAGES = "queue.buffering.max.messages";
    public static final String NO_MESSAGE_BATCHED_PRODUCER = "batch.num.messages";
    public static final String BUFFER_SIZE = "send.buffer.bytes";
    public static final String REQUEST_TIMEOUT = "request.timeout.ms";
    public static final String ENQUEUE_TIMEOUT = "queue.enqueue.timeout.ms";
    public static final String CLIENT_ID = "client.id";

    // Configuration properties parameter
    public static final String PARAM_TOPIC = "topic";
    public static final String PARAM_KEY = "key";

    // Configuration parameters for kafka connector
    public static final String KAFKA_BROKER_LIST = "kafka.brokerList";
    public static final String KAFKA_SERIALIZATION_CLASS = "kafka.serializationClass";
    public static final String KAFKA_REQUIRED_ACK = "kafka.requiredAck";
    public static final String KAFKA_PRODUCER_TYPE = "kafka.producerType";
    public static final String KAFKA_KEY_SERIALIZER_CLASS = "kafka.keySerializerClass";
    public static final String KAFKA_PARTITION_CLASS = "kafka.partitionClass";
    public static final String KAFKA_COMPRESSION_TYPE = "kafka.compressionCodec";
    public static final String KAFKA_COMPRESSED_TOPIC = "kafka.compressedTopics";
    public static final String KAFKA_MESSAGE_SEND_MAX_RETRIES = "kafka.messageSendMaxRetries";
    public static final String KAFKA_TIME_REFRESH_METADATA = "kafka.retryBackOff";
    public static final String KAFKA_TIME_REFRESH_METADATA_AFTER_TOPIC = "kafka.refreshInterval";
    public static final String KAFKA_BUFFER_MAX_TIME = "kafka.bufferingMaxTime";
    public static final String KAFKA_BUFFER_MAX_MESSAGES = "kafka.bufferingMaxMessages";
    public static final String KAFKA_NO_MESSAGE_BATCHED_PRODUCER = "kafka.batchNoMessages";
    public static final String KAFKA_BUFFER_SIZE = "kafka.sendBufferSize";
    public static final String KAFKA_REQUEST_TIMEOUT = "kafka.requestTimeout";
    public static final String KAFKA_ENQUEUE_TIMEOUT = "kafka.enqueueTimeout";
    public static final String KAFKA_CLIENT_ID = "kafka.clientId";

    // Configuration properties default values

    public static final String DEFAULT_REQUIRED_ACKS = "0";
    public static final String DEFAULT_SERIALIZATION_CLASS = "kafka.serializer.StringEncoder";
    public static final String DEFAULT_PRODUCER_TYPE = "sync";
    public static final String DEFAULT_KEY_SERIALIZER_CLASS = "";
    public static final String DEFAULT_PARTITION_CLASS = "kafka.producer.DefaultPartitioner";
    public static final String DEFAULT_COMPRESSION_TYPE = "none";
    public static final String DEFAULT_COMPRESSED_TOPIC = "";
    public static final String DEFAULT_TIME_REFRESH_METADATA = "100";
    public static final String DEFAULT_TIME_REFRESH_METADATA_AFTER_TOPIC = "600000";
    public static final String DEFAULT_BUFFER_MAX_MESSAGE = "10000";
    public static final String DEFAULT_MESSAGE_SEND_MAX_RETRIES = "3";
    public static final String DEFAULT_NO_MESSAGE_BATCHED_PRODUCER = "200";
    public static final String DEFAULT_BUFFER_SIZE = "1048576";
    public static final String DEFAULT_REQUEST_TIMEOUT = "10000";
    public static final String DEFAULT_BUFFER_MAX_TIME = "5000";
    public static final String DEFAULT_ENQUEUE_TIMEOUT = "-1";
    public static final String DEFAULT_CLIENT_ID = "";
}
