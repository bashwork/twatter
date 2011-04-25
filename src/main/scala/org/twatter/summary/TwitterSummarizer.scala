package org.twatter.summary

import scala.actors._
import Actor._
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._
import java.io.{File, BufferedWriter, FileWriter}
import java.io.{BufferedReader, FileReader}

/**
 * Helper utility to summarize a given file based on word
 * frequencies.
 *
 * @param inputPath The path to the documents to summarize
 * @param outputPath The path to store the merged results in
 */
class TwitterSummarizer(inputPath:String, outputPath:String, percent:Double)
    extends AbstractSummarizer {

    private val inputs = new File(inputPath)
    private val output = new File(outputPath)

    /**
     * The main acting loop for receiving new messages
     */
    def start() {
        logger.info("Summarizing files from {} to {}", inputPath, outputPath)
        inputs.listFiles.foreach { input => processFile(input) }
    }

    /**
     * Helper method to run each file processing in a single thread
     *
     * @param topic The input file for the specified topic
     */
    private def processFile(input:File) {
        actor {
            val lines   = extractLines(input)
            val words   = countWords(lines)
            val summary = countLines(lines, words)
            saveSummary(input.getName, summary)
        }
    }

    /**
     * Reads all the posts specified by the specific topic file
     *
     * @param topic The input file for the specified topic
     * @return The posts for the specified topic
     */
    private def extractLines(input:File) : List[String] = {
        scala.io.Source.fromFile(input.getAbsolutePath)
            .mkString.split('\n').toList
    }

    /**
     * Reads all the posts specified by the specific topic file
     *
     * @param topic The input file for the specified topic
     * @return The posts for the specified topic
     */
    private def countWords(lines:List[String]) : HashMap[String,Int] = {
        val result = new HashMap[String, Int]() { override def default(key:String) = 1 }
        lines.foreach { line =>
            line.split(" ").foreach { word =>
                result(word) += 1 // todo clean word
            }
        }

        result
    }

    /**
     * Reads all the posts specified by the specific topic file
     *
     * @param topic The input file for the specified topic
     * @return The posts for the specified topic
     */
    private def countLines(lines:List[String], counts:HashMap[String, Int])
        : List[String] = {
        lines.zipWithIndex.map { line =>
            val weight = line._1.split(" ").map { word =>
                counts(word) // todo clean word
            }.sum
            (line._1, line._2, weight) // (word, order, weight)
        }.sortWith { (l,r) => l._3 > r._3
        }.take(math.ceil(lines.size * percent).intValue)
         .sortWith { (l,r) => l._2 > r._2 }.map { _._1 }
    }

    /**
     * Saves all the topics to file
     *
     * @param topic The topic to save results for
     * @param posts The posts for the specified file to save
     */
    private def saveSummary(name:String, lines:List[String]) {
        logger.debug("Saving document summary {}", name)
        val file   = new File(output, name + "-summary")
        val writer = new BufferedWriter(new FileWriter(file))
        try {
            lines.foreach { line =>
                writer.write(line); writer.write(".")
            }
        } finally {
            writer.close
        }
    }
}

