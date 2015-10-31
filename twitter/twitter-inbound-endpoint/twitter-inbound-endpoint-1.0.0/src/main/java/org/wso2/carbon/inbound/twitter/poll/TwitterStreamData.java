/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.inbound.twitter.poll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.SynapseEnvironment;
import org.wso2.carbon.inbound.endpoint.protocol.generic.GenericPollingConsumer;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.User;
import twitter4j.UserStreamListener;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.UserList;
import twitter4j.TwitterException;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.StatusDeletionNotice;
import twitter4j.SiteStreamsListener;
import twitter4j.TwitterStream;
import twitter4j.FilterQuery;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Properties;

public class TwitterStreamData extends GenericPollingConsumer {

    private static final Log log = LogFactory.getLog(TwitterStreamData.class);

    // OAuth1 credentials for the twitter account
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessSecret;

    // URL parameters for the twitter streaming API methods
    private String followParam;
    private String trackParam;
    private String languageParam;
    private String countParam;
    private String withFollowingsParam;
    private String filterLevelParam;
    private String locationParam;

    // Twitter URL parameters value
    private int count;
    private String[] tracks;
    private String[] languages;
    private long[] follow;
    private boolean withFollowings;
    private String filterLevel;
    private double[][] locations;
    private String[] locationPair;

    private String injectingSeq;

    private boolean isPolled = false;

    public TwitterStreamData(Properties twitterProperties, String name,
                             SynapseEnvironment synapseEnvironment, long scanInterval,
                             String injectingSeq, String onErrorSeq, boolean coordination,
                             boolean sequential) {
        super(twitterProperties, name, synapseEnvironment, scanInterval,
                injectingSeq, onErrorSeq, coordination, sequential);
        log.info("Initialized the Twitter Streaming consumer");
        this.injectingSeq = injectingSeq;
        loadCredentials(twitterProperties);
        loadRequestParameters(twitterProperties);
        if (trackParam != null) {
            this.tracks = trackParam.split(",");
        }

        if (languages != null) {
            this.languages = languageParam.split(",");
        }

        if (followParam != null) {
            follow = new long[(followParam.split(",")).length];
            for (int i = 0; i < (followParam.split(",")).length; i++) {
                follow[i] = Long.parseLong((followParam.split(","))[i]);
            }
        }

        if (countParam != null) {
            try {
                this.count = Integer.parseInt(countParam);
            } catch (NumberFormatException nfe) {
                handleException("the count should be a number.", nfe);
            }
        }

        if (withFollowingsParam != null) {
            try {
                withFollowings = Boolean.parseBoolean(withFollowingsParam);
            } catch (Exception e) {
                handleException("the withFollowings should be true or false.", e);
            }
        }
        filterLevel = filterLevelParam;

        if (locationParam != null) {
            locationPair = new String[locationParam.split(",").length];
            for (int i = 0; i < (locationParam.split(",")).length; i++) {
                locationPair[i] = locationParam.split(",")[i];
            }
        }
        if (locationPair != null) {
            locations = new double[locationPair.length][2];
            for (int j = 0; j < locationPair.length; j++) {
                locations[j][0] = Double.parseDouble(locationPair[j].split(":")[0]);
                locations[j][1] = Double.parseDouble(locationPair[j].split(":")[1]);
            }
        }
    }

    public Object poll() {
        // Establishing connection with twitter streaming api
        try {
            if (!isPolled) {
                setupConnection();
                isPolled = true;
            }
        } catch (TwitterException te) {
            handleException("Error while setup the twitter connection.", te);
        }
        return null;
    }

    /**
     * Load credentials from the Twitter inbound endpoint file.
     *
     * @param properties the twitter properties
     */
    private void loadCredentials(Properties properties) {
        if (log.isDebugEnabled()) {
            log.debug("Starting to load the twitter credentials");
        }

        this.consumerKey = properties.getProperty(TwitterConstant.CONSUMER_KEY);
        this.consumerSecret = properties
                .getProperty(TwitterConstant.CONSUMER_SECRET);
        this.accessSecret = properties
                .getProperty(TwitterConstant.ACCESS_SECRET);
        this.accessToken = properties.getProperty(TwitterConstant.ACCESS_TOKEN);
        if (log.isDebugEnabled()) {
            log.debug("Loaded the twitter consumerKey : " + consumerKey
                    + ",consumerSecret : " + consumerSecret + ",accessToken : "
                    + accessToken + ",accessSecret : " + accessSecret);
        }
    }

    /**
     * Load the parameters from the Twitter inbound endpoint file.
     *
     * @param properties the twitter properties
     */
    private void loadRequestParameters(Properties properties) {
        if (log.isDebugEnabled()) {
            log.debug("Starting to load the URL parameters");
        }

        this.countParam = properties.getProperty(TwitterConstant.TWITTER_COUNT);
        this.followParam = properties
                .getProperty(TwitterConstant.TWITTER_FOLLOW);
        this.trackParam = properties.getProperty(TwitterConstant.TWITTER_TRACK);
        this.languageParam = properties
                .getProperty(TwitterConstant.TWITTER_LANGUAGE);
        this.withFollowingsParam = properties
                .getProperty(TwitterConstant.TWITTER_WITH_FOLLOWINGS);
        this.filterLevelParam = properties
                .getProperty(TwitterConstant.TWITTER_FILTER_LEVEL);
        this.locationParam = properties
                .getProperty(TwitterConstant.TWITTER_LOCATIONS);
        if (log.isDebugEnabled()) {
            log.debug("Loading the twitter URL parameters. countParam: "
                    + countParam + ",follow : " + followParam + ",track : "
                    + trackParam + ",language : " + languageParam);
        }
    }

    /**
     * Injecting the twitter Stream messages to the sequence
     *
     * @param status the twitter response status
     */
    public void injectTwitterMessage(Status status) {
        if (injectingSeq != null) {
            injectMessage(status.toString(), TwitterConstant.CONTENT_TYPE);
            if (log.isDebugEnabled()) {
                log.debug("injecting twitter message to the sequence : "
                        + injectingSeq);
            }
        } else {
            handleException("the Sequence is not found");
        }
    }

    /**
     * Setting up a connection with Twitter Stream API with the given
     * credentials
     *
     * @throws TwitterException
     */
    private void setupConnection() throws TwitterException {
        if (log.isDebugEnabled()) {
            log.debug("Starting to setup the connection with the twitter streaming endpoint");
        }
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret);
        StatusListener statusStreamsListener;
        UserStreamListener userStreamListener;
        SiteStreamsListener siteStreamslistener;
        TwitterStream twitterStream = new TwitterStreamFactory(
                configurationBuilder.build()).getInstance();
        String twitterOperation = properties
                .getProperty(TwitterConstant.TWITTER_OPERATION);
        if (twitterOperation.equals(TwitterConstant.FILTER_STREAM_OPERATION)
                || twitterOperation.equals(TwitterConstant.FIREHOSE_STREAM_OPERATION)
                || twitterOperation.equals(TwitterConstant.LINK_STREAM_OPERATION)
                || twitterOperation.equals(TwitterConstant.SAMPLE_STREAM_OPERATION)
                || twitterOperation.equals(TwitterConstant.RETWEET_STREAM_OPERATION)) {
            statusStreamsListener = new StatusListenerImpl();
            twitterStream.addListener(statusStreamsListener);
        } else if (twitterOperation.equals(TwitterConstant.USER_STREAM_OPERATION)) {
            userStreamListener = new UserStreamListenerImpl();
            twitterStream.addListener(userStreamListener);
        } else if (twitterOperation.equals(TwitterConstant.SITE_STREAM_OPERATION)) {
            siteStreamslistener = new siteStreamsListenerImpl();
            twitterStream.addListener(siteStreamslistener);
        } else {
            handleException("The operation :" + twitterOperation + " not found");
        }

		/* Synchronously retrieves public statuses that match one or more filter predicates.*/
        if ((properties.getProperty(TwitterConstant.TWITTER_OPERATION))
                .equals(TwitterConstant.FILTER_STREAM_OPERATION)) {
            FilterQuery query = new FilterQuery();
            if (languages != null) {
                query.language(languages);
            }
            if (tracks != null) {
                query.track(tracks);
            }
            if (follow != null) {
                query.follow(follow);
            }
            if (locations != null) {
                query.locations(locations);
            }
            if (filterLevel != null) {
                query.filterLevel(filterLevel);
            }
            if (follow == null & tracks == null & locations == null) {
                handleException("At least follow, locations, or track must be specified.");
            }
            query.count(count);
            twitterStream.filter(query);
        }

		/* Returns a small random sample of all public statuses. */
        if ((properties.getProperty(TwitterConstant.TWITTER_OPERATION))
                .equals(TwitterConstant.SAMPLE_STREAM_OPERATION)) {

            if (languages != null) {
                if (languages.length == 1) {
                    twitterStream.sample(languages[1]);
                } else {
                    handleException("A language can be used for the sample operation");
                }
            } else {
                twitterStream.sample();
            }
        }
        /* Asynchronously retrieves all public statuses.*/
        if ((properties.getProperty(TwitterConstant.TWITTER_OPERATION))
                .equals(TwitterConstant.FIREHOSE_STREAM_OPERATION)) {
            if (countParam != null) {
                twitterStream.firehose(count);
            }
        }
        /*
         User Streams provide a stream of data and events specific to the
		 authenticated user.This provides to access the Streams messages for a
		 single user.
		 */
        if ((properties.getProperty(TwitterConstant.TWITTER_OPERATION))
                .equals(TwitterConstant.USER_STREAM_OPERATION)) {
            if (tracks != null) {
                twitterStream.user(tracks);
            } else {
                twitterStream.user();
            }
        }
        /*
         * Link Streams provide asynchronously retrieves all statuses containing 'http:' and 'https:'.
		 */
        if ((properties.getProperty(TwitterConstant.TWITTER_OPERATION))
                .equals(TwitterConstant.LINK_STREAM_OPERATION)) {
            if (countParam != null) {
                twitterStream.links(count);
            }
        }
        /*
         * User Streams provide a stream of data and events specific to the
		 * authenticated user.This provides to access the Streams messages for a
		 * single user.
		 */
        if ((properties.getProperty(TwitterConstant.TWITTER_OPERATION))
                .equals(TwitterConstant.RETWEET_STREAM_OPERATION)) {
            twitterStream.retweet();
        }
        /*
         * Site Streams allows services, such as web sites or mobile push
		 * services, to receive real-time updates for a large number of users.
		 * Events may be streamed for any user who has granted OAuth access to
		 * your application. Desktop applications or applications with few users
		 * should use user streams.
		 */
        if ((properties.getProperty(TwitterConstant.TWITTER_OPERATION))
                .equals(TwitterConstant.SITE_STREAM_OPERATION)) {
            twitterStream.site(withFollowings, follow);
        }

    }

    /**
     * Twitter Stream Listener Impl onStatus will invoke whenever new twitter
     * come. New tweet injects to the sequence
     */
    class StatusListenerImpl implements StatusListener {
        public void onStatus(Status status) {
            injectTwitterMessage(status);
        }

        public void onException(Exception ex) {
            log.error("Twitter source threw an exception", ex);
        }

        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            log.debug("Got a status deletion notice id:"
                    + statusDeletionNotice.getStatusId());
        }

        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            log.debug("Got track limitation notice: " + numberOfLimitedStatuses);
        }

        public void onScrubGeo(long userId, long upToStatusId) {
            log.debug("Got scrub_geo event userId:" + userId + " upToStatusId:"
                    + upToStatusId);
        }

        public void onStallWarning(StallWarning warning) {
            log.debug("Got stall warning:" + warning);
        }

    }

    /**
     * Twitter Stream Listener Impl onStatus will invoke whenever new twitter
     * come. New tweet injects to the sequence
     */
    class siteStreamsListenerImpl implements SiteStreamsListener {

        public void onStatus(long forUser, Status status) {
            injectTwitterMessage(status);
        }

        public void onDeletionNotice(long forUser,
                                     StatusDeletionNotice statusDeletionNotice) {
            log.debug("Got a status deletion notice for_user:" + forUser
                    + " id:" + statusDeletionNotice.getStatusId());
        }

        public void onFriendList(long forUser, long[] friendIds) {
            log.debug("Friend list for the user:" + forUser);
            for (long friendId : friendIds) {
                log.debug(" " + friendId);
            }
            log.debug("");
        }

        public void onFavorite(long forUser, User source, User target,
                               Status favoriteStatus) {
            log.debug("Favorite for the user:" + forUser + " source:@"
                    + source.getScreenName() + " target:@"
                    + target.getScreenName() + " @"
                    + favoriteStatus.getUser().getScreenName() + " - "
                    + favoriteStatus.getText());
        }

        public void onUnfavorite(long forUser, User source, User target,
                                 Status unFavoriteStatus) {
            log.debug("Un favorite for the user:" + forUser + " source:@"
                    + source.getScreenName() + " target:@"
                    + target.getScreenName() + " @"
                    + unFavoriteStatus.getUser().getScreenName() + " - "
                    + unFavoriteStatus.getText());
        }

        public void onFollow(long forUser, User source, User followedUser) {
            log.debug("Follow for the user:" + forUser + " source:@"
                    + source.getScreenName() + " target:@"
                    + followedUser.getScreenName());
        }

        public void onUnfollow(long forUser, User source, User followedUser) {
            log.debug("Un follow for the user:" + forUser + " source:@"
                    + source.getScreenName() + " target:@"
                    + followedUser.getScreenName());
        }

        public void onDirectMessage(long forUser, DirectMessage directMessage) {
            log.debug("DirectMessage for the user:" + forUser + " text:"
                    + directMessage.getText());
        }

        public void onDeletionNotice(long forUser, long directMessageId,
                                     long userId) {
            log.debug("Got a direct message deletion notice for the user:"
                    + forUser + " id:" + directMessageId);
        }

        public void onUserListMemberAddition(long forUser, User addedMember,
                                             User listOwner, UserList list) {
            log.debug("User list member addition:" + forUser
                    + " member:@" + addedMember.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName() + " list:"
                    + list.getName());
        }

        public void onUserListMemberDeletion(long forUser, User deletedMember,
                                             User listOwner, UserList list) {
            log.debug("User list member deletion:" + forUser
                    + " member:@" + deletedMember.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName() + " list:"
                    + list.getName());
        }

        public void onUserListSubscription(long forUser, User subscriber,
                                           User listOwner, UserList list) {
            log.debug("User list subscribed:" + forUser
                    + " subscriber:@" + subscriber.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName() + " list:"
                    + list.getName());
        }

        public void onUserListUnsubscription(long forUser, User subscriber,
                                             User listOwner, UserList list) {
            log.debug("User list un subscribed :" + forUser
                    + " subscriber:@" + subscriber.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName() + " list:"
                    + list.getName());
        }

        public void onUserListCreation(long forUser, User listOwner,
                                       UserList list) {
            log.debug("User list created for:" + forUser + " listOwner:@"
                    + listOwner.getScreenName() + " list:" + list.getName());
        }

        public void onUserListUpdate(long forUser, User listOwner, UserList list) {
            log.debug("user list updated for:" + forUser + " listOwner:@"
                    + listOwner.getScreenName() + " list:" + list.getName());
        }

        public void onUserListDeletion(long forUser, User listOwner,
                                       UserList list) {
            log.debug("User list destroyed for:" + forUser
                    + " listOwner:@" + listOwner.getScreenName() + " list:"
                    + list.getName());
        }

        public void onUserProfileUpdate(long forUser, User updatedUser) {
            log.debug("User profile updated for:" + forUser + " user:@"
                    + updatedUser.getScreenName());
        }

        public void onUserDeletion(long forUser, long deletedUser) {
            log.debug("User deletion for:" + forUser + " user:@");
        }

        public void onUserSuspension(long forUser, long suspendedUser) {
            log.debug("UserSuspension for_user:" + forUser + " user:@"
                    + suspendedUser);
        }

        public void onBlock(long forUser, User source, User blockedUser) {
            log.debug("Block for_user:" + forUser + " source:@"
                    + source.getScreenName() + " target:@"
                    + blockedUser.getScreenName());
        }

        public void onUnblock(long forUser, User source, User unblockedUser) {
            log.debug("Unblock for_user:" + forUser + " source:@"
                    + source.getScreenName() + " target:@"
                    + unblockedUser.getScreenName());
        }

        public void onRetweetedRetweet(User source, User target,
                                       Status reTweetedStatus) {
            log.debug("Re tweeted re tweeted source:" + source.getScreenName()
                    + " target:@" + target.getScreenName()
                    + " reTweetedStatus:@"
                    + reTweetedStatus.getUser().getScreenName() + " - "
                    + reTweetedStatus.getText());
        }

        public void onFavoritedRetweet(User source, User target,
                                       Status favoriteStatus) {
            log.debug("Favorite re tweet source:" + source.getScreenName()
                    + " target:@" + target.getScreenName()
                    + " favoriteStatus:@"
                    + favoriteStatus.getUser().getScreenName() + " - "
                    + favoriteStatus.getText());
        }

        public void onDisconnectionNotice(String line) {
            log.debug("onDisconnectionNotice:" + line);
        }

        public void onException(Exception ex) {
            log.error("Twitter source threw an exception", ex);
        }

    }

    /**
     * Twitter Stream Listener Impl onStatus will invoke whenever new twitter
     * come. New tweet injects to the sequence
     */
    class UserStreamListenerImpl implements UserStreamListener {

        public void onStatus(Status status) {
            injectTwitterMessage(status);
        }

        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            log.debug("Got a status deletion notice id:"
                    + statusDeletionNotice.getStatusId());
        }

        public void onDeletionNotice(long directMessageId, long userId) {
            log.debug("Got a direct message deletion notice id:"
                    + directMessageId);
        }

        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            log.debug("Got a track limitation notice:"
                    + numberOfLimitedStatuses);
        }

        public void onScrubGeo(long userId, long upToStatusId) {
            log.debug("Got scrub_geo event user id:" + userId + " upToStatusId:"
                    + upToStatusId);
        }

        public void onStallWarning(StallWarning warning) {
            log.debug("Got stall warning:" + warning);
        }

        public void onFriendList(long[] friendIds) {
            log.debug("onFriendList");
            for (long friendId : friendIds) {
                log.debug(" " + friendId);
            }
            log.debug("");
        }

        public void onFavorite(User source, User target, Status favoriteStatus) {
            log.debug("onFavorite source:@" + source.getScreenName()
                    + " target:@" + target.getScreenName() + " @"
                    + favoriteStatus.getUser().getScreenName() + " - "
                    + favoriteStatus.getText());
        }

        public void onUnfavorite(User source, User target,
                                 Status unFavoriteStatus) {
            log.debug("onUnFavorite source:@" + source.getScreenName()
                    + " target:@" + target.getScreenName() + " @"
                    + unFavoriteStatus.getUser().getScreenName() + " - "
                    + unFavoriteStatus.getText());
        }

        public void onFollow(User source, User followedUser) {
            log.debug("onFollow source:@" + source.getScreenName()
                    + " target:@" + followedUser.getScreenName());
        }

        public void onUnfollow(User source, User followedUser) {
            log.debug("onFollow source:@" + source.getScreenName()
                    + " target:@" + followedUser.getScreenName());
        }

        public void onDirectMessage(DirectMessage directMessage) {
            log.debug("onDirectMessage text:" + directMessage.getText());
        }

        public void onUserListMemberAddition(User addedMember, User listOwner,
                                             UserList list) {
            log.debug("onUserListMemberAddition added member:@"
                    + addedMember.getScreenName() + " listOwner:@"
                    + listOwner.getScreenName() + " list:" + list.getName());
        }

        public void onUserListMemberDeletion(User deletedMember,
                                             User listOwner, UserList list) {
            log.debug("onUserListMemberDeleted deleted member:@"
                    + deletedMember.getScreenName() + " listOwner:@"
                    + listOwner.getScreenName() + " list:" + list.getName());
        }

        public void onUserListSubscription(User subscriber, User listOwner,
                                           UserList list) {
            log.debug("onUserListSubscribed subscriber:@"
                    + subscriber.getScreenName() + " listOwner:@"
                    + listOwner.getScreenName() + " list:" + list.getName());
        }

        public void onUserListUnsubscription(User subscriber, User listOwner,
                                             UserList list) {
            log.debug("onUserListUnSubscribed subscriber:@"
                    + subscriber.getScreenName() + " listOwner:@"
                    + listOwner.getScreenName() + " list:" + list.getName());
        }

        public void onUserListCreation(User listOwner, UserList list) {
            log.debug("onUserListCreated  listOwner:@"
                    + listOwner.getScreenName() + " list:" + list.getName());
        }

        public void onUserListUpdate(User listOwner, UserList list) {
            log.debug("onUserListUpdated  listOwner:@"
                    + listOwner.getScreenName() + " list:" + list.getName());
        }

        public void onUserListDeletion(User listOwner, UserList list) {
            log.debug("onUserListDestroyed  listOwner:@"
                    + listOwner.getScreenName() + " list:" + list.getName());
        }

        public void onUserProfileUpdate(User updatedUser) {
            log.debug("onUserProfileUpdated user:@"
                    + updatedUser.getScreenName());
        }

        public void onUserDeletion(long deletedUser) {
            log.debug("onUserDeletion user:@" + deletedUser);
        }

        public void onUserSuspension(long suspendedUser) {
            log.debug("onUserSuspension user:@" + suspendedUser);
        }

        public void onBlock(User source, User blockedUser) {
            log.debug("onBlock source:@" + source.getScreenName() + " target:@"
                    + blockedUser.getScreenName());
        }

        public void onUnblock(User source, User unblockedUser) {
            log.debug("onUnblock source:@" + source.getScreenName()
                    + " target:@" + unblockedUser.getScreenName());
        }

        public void onRetweetedRetweet(User source, User target,
                                       Status reTweetedStatus) {
            log.debug("onReTweetedReTweet source:@" + source.getScreenName()
                    + " target:@" + target.getScreenName()
                    + reTweetedStatus.getUser().getScreenName() + " - "
                    + reTweetedStatus.getText());
        }

        public void onFavoritedRetweet(User source, User target,
                                       Status favoriteReTweet) {
            log.debug("onFavoriteReTweet source:@" + source.getScreenName()
                    + " target:@" + target.getScreenName()
                    + favoriteReTweet.getUser().getScreenName() + " - "
                    + favoriteReTweet.getText());
        }

        public void onQuotedTweet(User source, User target, Status quotingTweet) {
            log.debug("onQuotedTweet" + source.getScreenName() + " target:@"
                    + target.getScreenName()
                    + quotingTweet.getUser().getScreenName() + " - "
                    + quotingTweet.getText());
        }

        public void onException(Exception ex) {
            log.error("onException:" + ex.getMessage());
        }

    }

    private void handleException(String msg, Exception ex) {
        log.error(msg, ex);
        throw new SynapseException(ex);
    }

    private void handleException(String msg) {
        log.error(msg);
        throw new SynapseException(msg);
    }

}
