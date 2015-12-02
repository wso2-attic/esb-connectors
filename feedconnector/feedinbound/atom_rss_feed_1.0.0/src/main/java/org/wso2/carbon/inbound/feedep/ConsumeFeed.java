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

package org.wso2.carbon.inbound.feedep;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ListParseFilter;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.filter.WhiteListParseFilter;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * ConsumeFeed uses to feeds from given Backend
 */
public class ConsumeFeed {
    private static final Log log = LogFactory.getLog(ConsumeFeed.class.getName());
    private long scanInterval;
    private long scanIntervalDefined;
    private long lastRanTime;
    private String host;
    private String feedType;
    private Feed feed = null;
    private java.util.Date lastUpdated;
    private RssInject rssInject;
    private FeedRegistryHandler feedRegistryHandler;
    private String pathName;
    private String dateFormat;
    private DateFormat format;
    private Parser parser;
    private ParserOptions opts;
    public ConsumeFeed(RssInject rssInject, long scanInterval, String host, String feedType,
                       FeedRegistryHandler feedRegistryHandler, String name, String dateFormat) {
        this.host = host;
        this.feedType = feedType;
        this.scanInterval = scanInterval;
        this.rssInject = rssInject;
        this.feedRegistryHandler = feedRegistryHandler;
        this.pathName = name;
        this.dateFormat = dateFormat;
        this.scanIntervalDefined = scanInterval;
        format = new SimpleDateFormat(FeedEPConstant.RSS_FEED_DATE_FORMAT, Locale.ENGLISH);
        parser = Abdera.getNewParser();
        //set filter
        opts = parser.getDefaultParserOptions();
        ListParseFilter filter = new WhiteListParseFilter();
        if (feedType.equalsIgnoreCase(FeedEPConstant.FEED_TYPE_RSS)) {
            filter.add(FeedEPConstant.FEED_RSS);
            filter.add(FeedEPConstant.FEED_CHANNEL);
            filter.add(FeedEPConstant.FEED_ITEM);
            filter.add(FeedEPConstant.FEED_TITLE);
            filter.add(FeedEPConstant.FEED_GUID);
            filter.add(FeedEPConstant.FEED_PUBDATE);
            filter.add(FeedEPConstant.FEED_LINK);
        } else if (feedType.equalsIgnoreCase(FeedEPConstant.FEED_TYPE_ATOM)) {
            filter.add(Constants.FEED);
            filter.add(Constants.ENTRY);
            filter.add(Constants.TITLE);
            filter.add(Constants.ID);
            filter.add(Constants.UPDATED);
            filter.add(Constants.LINK);
            filter.add(Constants.AUTHOR);
        }
        opts.setParseFilter(filter);
    }

    //check time interval
    public void execute() {
        try {
            long currentTime = (new Date()).getTime();
            if (((lastRanTime + scanInterval) <= currentTime)) {
                lastRanTime = currentTime;
                if (log.isDebugEnabled()) {
                    log.debug("lastRanTime " + lastRanTime);
                }
                consume();
            } else if (log.isDebugEnabled()) {
                log.debug("Skip cycle since concurrent rate is higher than the scan interval : Feed Inbound EP " + pathName);
            } else if (scanInterval < 999999999) {
                scanInterval = scanInterval + 1000;
            }
            if (log.isDebugEnabled()) {
                log.debug("End : Feed Inbound EP : " + pathName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    //consume feeds
    private void consume() throws IOException, ParseException {
        InputStream input;
        input = new URL(host).openStream();
        Document<Feed> doc;
        doc = parser.parse(input, "", opts);
        if (doc.getRoot() == null) {
            log.error("Please check host address or feed type");
            return;
        }
        //convert RSS feeds as Atom
        if (feedType.equalsIgnoreCase(FeedEPConstant.FEED_TYPE_RSS)) {
            Factory factory = Abdera.getNewFactory();
            feed = factory.newFeed();
            OMElement item = (OMElement) doc.getRoot();
            Iterator values1 = item.getFirstElement().getChildrenWithName(FeedEPConstant.FEED_ITEM);

            while (values1.hasNext()) {
                Entry entry = feed.insertEntry();
                OMElement omElement = (OMElement) values1.next();

                Iterator values2 = omElement.getChildrenWithName(FeedEPConstant.FEED_TITLE);
                OMElement Title = (OMElement) values2.next();
                entry.setTitle(Title.getText());

                Iterator values3 = omElement.getChildrenWithName(FeedEPConstant.FEED_PUBDATE);
                OMElement Updated = (OMElement) values3.next();
                Date date;
                try {
                    date = format.parse(Updated.getText());
                    entry.setUpdated(date);
                } catch (ParseException e) {
                    if (dateFormat != null) {
                        format =
                                new SimpleDateFormat(dateFormat, Locale.ENGLISH);
                        date = format.parse(Updated.getText());
                        entry.setUpdated(date);
                    } else {
                        log.error(e.getMessage(), e);
                        return;
                    }
                }
                Iterator values5 = omElement.getChildrenWithName(FeedEPConstant.FEED_GUID);
                OMElement guid1 = (OMElement) values5.next();
                entry.setId(guid1.getText());

                Iterator values6 = omElement.getChildrenWithName(FeedEPConstant.FEED_LINK);
                OMElement link = (OMElement) values6.next();
                entry.setBaseUri(link.getText());
            }
        } else if (feedType.equalsIgnoreCase(FeedEPConstant.FEED_TYPE_ATOM)) {
            feed = doc.getRoot();
        }

        if (log.isDebugEnabled()) {
            log.debug(lastUpdated + " : " + feed.getEntries().get(0).getUpdated());
        }
        DateFormat formatLastUpdate = new SimpleDateFormat(FeedEPConstant.REGISTRY_TIME_FORMAT, Locale.ENGLISH);
        Date newUpdated = feed.getEntries().get(0).getUpdated();
        if (feedRegistryHandler.readFromRegistry(pathName) != null) {
            lastUpdated =
                    formatLastUpdate.parse(feedRegistryHandler.readFromRegistry(pathName)
                            .toString());
        } else {
            feedRegistryHandler.writeToRegistry(pathName, newUpdated);
        }
        if (lastUpdated == null) {
            rssInject.invoke(feed);
            feedRegistryHandler.writeToRegistry(pathName, newUpdated);
        } else if (newUpdated.after(lastUpdated)) {
            rssInject.invoke(feed);
            feedRegistryHandler.writeToRegistry(pathName, newUpdated);
            scanInterval = scanIntervalDefined;
        }
        input.close();
    }
}
