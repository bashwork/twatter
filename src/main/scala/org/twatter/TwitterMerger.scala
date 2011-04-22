package org.twatter

import scala.collection.JavaConversions._
import java.io.{File, BufferedWriter, FileWriter}
import java.io.{BufferedReader, FileReader}
import twitter4j.Status
import redis.clients.jedis.{Jedis, JedisPool}
import org.slf4j.{Logger, LoggerFactory}

/**
 * Actor used to process new twitter messages to file and to
 * redis.
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
        idDirectory.listFiles.filter { topic => topic.getName != "twatter-topics" }
            .foreach { topic =>
                val posts = readPosts(topic)
                val name  = topic.getName.split("-").last
                saveTopic(name, posts)
        }
    }

    /**
     * Reads all the posts specified by the specific topic file
     */
    private def readPosts(topic:File) : List[String] = {
        scala.io.Source.fromFile(topic.getAbsolutePath)
            .getLines.toList.map { id => readFile(new File(rawDirectory, id)) }
    }

    /**
     * Provides a file iterator around a file
     */
    private def readFile(topic:File) : String = {
        val reader = new BufferedReader(new FileReader(topic))
        try {
            return reader.readLine
        } finally {
            reader.close
        }
    }

    /**
     * Saves all the topics to file
     */
    private def saveTopic(topic:String, posts:List[String]) {
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

