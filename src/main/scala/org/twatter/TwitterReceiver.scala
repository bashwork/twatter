package org.twatter

import scala.actors.Actor
import twitter4j._
import org.slf4j.{Logger, LoggerFactory}

class TwitterReceiver(processor:Actor) {

    private val stream = new TwitterStreamFactory().getInstance();
    private val logger = LoggerFactory.getLogger(this.getClass)

    /**
     * Starts pushing twitter samples to the queue
     */
    def start() {
        val listener = new StatusListener() {
            def onStatus(status:Status) = processor ! status
            def onException(ex:Exception) = logger.error("Processing exception:", ex)
            def onTrackLimitationNotice(status:Int) {}
            def onDeletionNotice(status:StatusDeletionNotice) {}
            def onScrubGeo(userid:Long, toUserid:Long) {}
        };
        stream.addListener(listener);
        stream.sample();
    }

    /**
     * Stops pushing twitter samples to the queue
     */
    def stop() { stream.shutdown() }
}

