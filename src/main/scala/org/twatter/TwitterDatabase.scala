package org.twatter

import scala.collection.JavaConversions._
import java.io.{File, BufferedWriter, FileWriter}
import twitter4j.Status
import redis.clients.jedis.{Jedis, JedisPool}
import org.slf4j.{Logger, LoggerFactory}

/**
 * Actor used to process new twitter messages to file and to
 * redis.
 */
class TwitterDatabase(filename:String, database:JedisPool) {

    private val logger = LoggerFactory.getLogger(this.getClass)
    private val output = new File(filename)

    /**
     * The main acting loop for receiving new messages
     */
    def start() {
        logger.info("Dumping twatter database to {}", filename)
        saveTopicList
        saveTopics
    }

    /**
     * Saves the topics from the database to file
     */
    private def saveTopicList() {
        logger.info("Dumping twatter topic list")
        val redis = database.getResource()
        val file   = new File(output, "twatter-topics")
        val writer = new BufferedWriter(new FileWriter(file))
        try {
            writer.write("# Total Posts: ")
            writer.write(redis.get(TwitterRedis.postsCountKey))
            writer.newLine()

            redis.smembers(TwitterRedis.topicsKey).toList.map { topic =>
                (redis.get(TwitterRedis.topicCountKey(topic)).toInt, topic)
            }.sortWith { (l,r) => l._1 > r._1 }.foreach { topic =>
                writer.write(topic.toString); writer.newLine()
            }
        } finally {
            database.returnResource(redis)
            writer.close
        }
    }

    /**
     * Saves all the topics to file
     */
    private def saveTopics() {
        logger.info("Dumping twatter topic identifiers")
        val redis = database.getResource()
        try {
            redis.smembers(TwitterRedis.topicsKey).toList.foreach { topic =>
                saveTopic(redis, topic)
            }
        } finally {
            database.returnResource(redis)
        }
    }

    /**
     * Saves all the topics to file
     */
    private def saveTopic(redis:Jedis, topic:String) {
        val file   = new File(output, "twatter-topic-" + topic)
        val writer = new BufferedWriter(new FileWriter(file))
        try {
            redis.smembers(TwitterRedis.topicKey(topic)).foreach { post =>
                writer.write(post.toString); writer.newLine()
            }
        } finally {
            writer.close
        }
    }
}

