package org.twatter

import scala.collection.JavaConversions._
import java.io.{File, BufferedWriter, FileWriter}
import java.io.{BufferedReader, FileReader}
import twitter4j.Status
import redis.clients.jedis.{Jedis, JedisPool}
import org.slf4j.{Logger, LoggerFactory}

/**
 * Helper utility to merge the id files and raw tweets into a single
 * topic file seperated by new lines.
 *
 * @param idPath The path to the hash to id files
 * @param rawPath The path to the raw posts files
 * @param filename The path to store the merged results in
 */
class TwitterMerger(idPath:String, rawPath:String, filename:String) {

    private val logger = LoggerFactory.getLogger(this.getClass)
    private val output = new File(filename)
    private val idDirectory = new File(idPath)
    private val rawDirectory = new File(rawPath)

    /**
     * The main acting loop for receiving new messages
     */
    def start() {
        logger.info("Merging files from {} and {}", idPath, rawPath)
        idDirectory.listFiles
            .filter  { topic => topic.getName != "twatter-topics" }
            .foreach { topic =>
                val posts = readPosts(topic)
                val name  = topic.getName.split("-").last
                saveTopic(name, posts)
            }
    }

    /**
     * Reads all the posts specified by the specific topic file
     *
     * @param topic The input file for the specified topic
     * @return The posts for the specified topic
     */
    private def readPosts(topic:File) : Iterator[String] = {
        scala.io.Source.fromFile(topic.getAbsolutePath)
            .getLines.map { id => 
                val path = new File(rawDirectory, id).getAbsolutePath
                scala.io.Source.fromFile(path).getLines.next
            }
    }

    /**
     * Saves all the topics to file
     *
     * @param topic The topic to save results for
     * @param posts The posts for the specified file to save
     */
    private def saveTopic(topic:String, posts:Iterator[String]) {
        logger.debug("Saving topic {}", topic)
        val file   = new File(output, "twatter-topic-" + topic)
        val writer = new BufferedWriter(new FileWriter(file))
        try {
            posts.foreach { post => 
                writer.write(post.toString); writer.newLine()
            }
        } finally {
            writer.close
        }
    }
}

