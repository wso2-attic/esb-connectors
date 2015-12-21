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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * FeedRetrieval uses to feeds from given Backend
 */
public class FeedRetrieval {
    private static final Log log = LogFactory.getLog(FeedRetrieval.class.getName());
    private long scanInterval;
    private long scanIntervalDefined;
    private long lastRanTime;
    private String host;
    private String feedType;
    private Feed feed = null;
    private java.util.Date lastUpdated;
    private FeedInject rssInject;
    private FeedRegistryHandler feedRegistryHandler;
    private String pathName;
    private String dateFormat;
    private DateFormat format;
    private Parser parser;
    private ParserOptions opts;

    public FeedRetrieval(FeedInject rssInject, long scanInterval, String host, String feedType,
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

    /**check time interval and execute consume method */
    public void execute() {
        long currentTime = (new Date()).getTime();
        if (((lastRanTime + scanInterval) <= currentTime)) {
            lastRanTime = currentTime;
            if (log.isDebugEnabled()) {
                log.debug("lastRanTime " + lastRanTime);
            }
            consume();
        } else if (log.isDebugEnabled()) {
            log.debug("Skip cycle since concurrent rate is higher than the scan interval : Feed Inbound EP " + pathName);
        }
        if (log.isDebugEnabled()) {
            log.debug("End : Feed Inbound EP : " + pathName);
        }
    }

    /**consume feeds from feed url*/
    private void consume() {
        InputStream input = null;
        try {
            input = new URL(host).openStream();
            Document<Feed> doc;
            doc = parser.parse(input, "", opts);
            if (doc.getRoot() == null) {
                log.error("Please check host address or feed type");
                throw new SynapseException("Please check host address or feed type");
            }
            //convert RSS feeds as Atom
            if (feedType.equalsIgnoreCase(FeedEPConstant.FEED_TYPE_RSS)) {
                Factory factory = Abdera.getNewFactory();
                feed = factory.newFeed();
                OMElement item = (OMElement) doc.getRoot();
                Iterator itemValue = item.getFirstElement().getChildrenWithName(FeedEPConstant.FEED_ITEM);
                while (itemValue.hasNext()) {
                    Entry entry = feed.insertEntry();
                    OMElement omElement = (OMElement) itemValue.next();

                    Iterator titleValue = omElement.getChildrenWithName(FeedEPConstant.FEED_TITLE);
                    OMElement Title = (OMElement) titleValue.next();
                    entry.setTitle(Title.getText());

                    Iterator dateValue = omElement.getChildrenWithName(FeedEPConstant.FEED_PUBDATE);
                    OMElement Updated = (OMElement) dateValue.next();
                    Date date;
                    try {
                        date = format.parse(Updated.getText());
                        entry.setUpdated(date);
                    } catch (ParseException e) {
                        if (!StringUtils.isEmpty(dateFormat)) {
                            format = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
                            try {
                                date = format.parse(Updated.getText());
                            } catch (ParseException e1) {
                                log.error("Error while parse date", e1);
                                throw new SynapseException("Error while parse date", e1);
                            }
                            entry.setUpdated(date);
                        } else {
                            log.error("please set correct date format to fix the issue", e);
                            throw new SynapseException("please set correct date format to fix the issue", e);
                        }
                    }
                    Iterator idValue = omElement.getChildrenWithName(FeedEPConstant.FEED_GUID);
                    OMElement guid = (OMElement) idValue.next();
                    entry.setId(guid.getText());

                    Iterator linkValue = omElement.getChildrenWithName(FeedEPConstant.FEED_LINK);
                    OMElement link = (OMElement) linkValue.next();
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
                lastUpdated = formatLastUpdate.parse(feedRegistryHandler.readFromRegistry(pathName).toString());
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
        } catch (ParseException e) {
            log.error("error while parse date ", e);
        } catch (MalformedURLException e) {
            log.error("given url doesn't have feed ", e);
        } catch (IOException e) {
            log.error("error while read feed ", e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                log.error("error while close input feed input stream ", e);
            }
        }
    }

}
