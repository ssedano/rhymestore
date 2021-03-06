/**
 * Copyright (c) 2010 Enric Ruiz, Ignasi Barrera
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.rhymestore.proc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import com.rhymestore.config.Configuration;
import com.rhymestore.twitter.stream.GetMentionsListener;

/**
 * Main Twitter listener process.
 * 
 * @author Ignasi Barrera
 */
public class TwitterListener
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterListener.class);

    /** The Twitter API client. */
    private Twitter twitter;

    /** The Twitter streaming API. */
    private TwitterStream stream;

    /**
     * Start listening to tweets.
     */
    public void start() throws IllegalStateException, TwitterException
    {
        twitter = new TwitterFactory().getInstance();
        stream = new TwitterStreamFactory().getInstance();

        LOGGER.info("Connected to Twitter as: {}", twitter.getScreenName());

        LOGGER.info("Starting the Twitter stream listener");
        stream.addListener(new GetMentionsListener(twitter));
        stream.user(); // Start reading to user stream
    }

    /**
     * Shutdown hook to close the connection to Twitter.
     */
    private static class TwitterShutdown extends Thread
    {
        /** The listener to shutdown. */
        private TwitterListener listener;

        public TwitterShutdown(final TwitterListener listener)
        {
            super();
            this.listener = listener;
        }

        @Override
        public void run()
        {
            LOGGER.info("Disconnecting from the Twitter streaming API");
            listener.stream.shutdown();

            LOGGER.info("Disconnecting from Twitter");
            listener.twitter.shutdown();
        }
    }

    public static void main(final String[] args) throws Exception
    {
        Configuration.loadTwitterConfig();

        // Start the Twitter listener
        TwitterListener listener = new TwitterListener();
        listener.start();

        // Register the shutdown hook to close the connection properly
        Runtime.getRuntime().addShutdownHook(new TwitterShutdown(listener));
    }
}
