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

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

/**
 * Edit Existing Feed By ID
 */
public class FeedUpdation extends AbstractConnector {
    private static Document<Entry> doc;

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        String entryID = getParameter(messageContext, FeedConstant.ENTRY_ID).toString();
        String hostAddress = getParameter(messageContext, FeedConstant.HOST_ADDRESS).toString();
        String title = getParameter(messageContext, FeedConstant.TITLE).toString();
        String content = getParameter(messageContext, FeedConstant.CONTENT).toString();
        String author = getParameter(messageContext, FeedConstant.AUTHOR).toString();

        if (StringUtils.isEmpty(entryID) || StringUtils.isEmpty(hostAddress)) {
            handleException("Entry ID and host address can not be null or empty", messageContext);
        }
        String entryUri = hostAddress + "/" + entryID + "-";
        AbderaClient abderaClient = FeedUtil.getAbderaClient();
        try {
            doc = abderaClient.get(entryUri).getDocument();
        } catch (Exception e) {
            handleException("error while get the entry from server ", e, messageContext);
        }
        if (title != null) {
            doc.getRoot().getTitleElement().setText(title);
        }
        if (content != null) {
            doc.getRoot().getContentElement().setText(content);
        }
        if (author != null) {
            doc.getRoot().getAuthor().setText(author);
        }
        RequestOptions opts = new RequestOptions();
        opts.setContentType(FeedConstant.CONTENT_TYPE);
        ClientResponse resp;
        try {
            resp = abderaClient.put(entryUri, doc.getRoot(), opts);
            FeedUtil response = new FeedUtil();
            response.InjectMessage(messageContext, resp.getStatusText());
        } catch (Exception ex) {
            handleException("error while update the entry: ", ex, messageContext);
        }
    }
}
