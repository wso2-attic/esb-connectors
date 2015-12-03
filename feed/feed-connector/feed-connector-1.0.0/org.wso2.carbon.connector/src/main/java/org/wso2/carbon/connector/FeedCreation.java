/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.connector;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import java.util.Date;

/**
 * Create the Feeds
 */
public class FeedCreation extends AbstractConnector {

    public void connect(MessageContext messageContext) throws ConnectException {
        String hostAddress = (String) getParameter(messageContext, FeedConstant.HOST_ADDRESS);
        String title = (String) getParameter(messageContext, FeedConstant.TITLE);
        String content = (String) getParameter(messageContext, FeedConstant.CONTENT);
        String author = (String) getParameter(messageContext, FeedConstant.AUTHOR);
        String feedID = (String) getParameter(messageContext, FeedConstant.FEED_ID);

        if (StringUtils.isEmpty(hostAddress)) {
            handleException("host address can not be null or empty", messageContext);
        }

        AbderaClient abderaClient = FeedUtil.getAbderaClient();
        Factory factory = FeedUtil.getFactory();
        Entry entry = factory.newEntry();
        entry.setId(feedID);
        entry.setTitle(title);
        entry.setUpdated(new Date());
        entry.addAuthor(author);
        entry.setContent(content);

        RequestOptions opts = new RequestOptions();
        opts.setContentType(FeedConstant.CONTENT_TYPE);
        FeedUtil response = new FeedUtil();
        ClientResponse resp;
        try {
            resp = abderaClient.post(hostAddress, entry, opts);
            response.InjectMessage(messageContext, resp.getStatusText());
        } catch (Exception ex) {
            handleException("error while connect ", ex, messageContext);
        }
    }
}