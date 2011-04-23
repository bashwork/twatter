package org.twatter

import scala.actors._
import Actor._
import java.io.{File, BufferedWriter, FileWriter}
import twitter4j.Status
import redis.clients.jedis.JedisPool
import org.slf4j.{Logger, LoggerFactory}

/**
 * Actor used to process new twitter messages to file and to
 * redis.
 *
 * @param filename The directory to store the resulting posts in
 * @param database The redis database pool
 * @param poison A list of poison words to drop posts with
 */
class TwitterProcessor(filename:String, database:JedisPool,
    poison: List[String]) extends Actor {

    private val logger = LoggerFactory.getLogger(this.getClass)
    private val output = new File(filename)

    /**
     * The main acting loop for receiving new messages
     */
    def act() {
        logger.info("Starting processing twitter messages to {}", filename)
        loop {
            react {
                case status:Status => {
                    if (shouldSaveStatus(status)) {
                        saveToFile(status)
                        saveToDatabase(status)
                    }
                }
                case "quit" => exit()
            }
        }
    }

    /**
     * Checks if the given messages should be filtered
     *
     * @param status The next status to test
     * @return true if we should save the status, false otherwise
     */
    private def shouldSaveStatus(status:Status) : Boolean = {
        if (poison.isEmpty) return true
        val shouldSave = status.getText.split(" ").map { _.toLowerCase }
            .exists { word => poison.exists { _ contains word } }
        !shouldSave
    }

    /**
     * Saves the next message to the filesystem for processing
     *
     * @param status The next status to save
     */
    private def saveToFile(status:Status) {
        val file   = new File(output, status.getId.toString)
        val writer = new BufferedWriter(new FileWriter(file))

        try {
            writer.write(status.getText)
        } finally {
            writer.close
        }
    }

    /**
     * Saves the next message status to the database

     * @param status The next status to save
     */
    private def saveToDatabase(status:Status) {
        val redis = database.getResource()

        try {
            val id = status.getId.toString
            // todo status.getURLEntities.foreach { entity =>
            //    val lines = scala.io.Source.fromURL(entity.getURL).getLines
            //}
            // todo status.isFavorited multiplier ?
            status.getHashtagEntities.foreach { tag =>
                val text = tag.getText.toLowerCase
                redis.sadd(TwitterRedis.topicsKey,  text)
                redis.sadd(TwitterRedis.topicKey(text), id)
                redis.incrBy(TwitterRedis.topicCountKey(text),
                    math.max(status.getRetweetCount, 1L))
                redis.incr(TwitterRedis.postsCountKey)
            }
        } finally {
            database.returnResource(redis)
        }
    }
}

object TwitterProcessor {
    def apply(filename:String, database:JedisPool, poison:List[String]) : Actor = {
        val processor = new TwitterProcessor(filename, database, poison)
        processor.start
        processor
    }
}
