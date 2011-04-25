package org.twatter.summary

import scala.actors._
import Actor._
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._
import java.io.{File, BufferedWriter, FileWriter}
import java.io.{BufferedReader, FileReader}
import net.sf.classifier4J.summariser.SimpleSummariser

/**
 * Helper utility to summarise a given file using the
 * classifer4J library.
 *
 * @param inputPath The path to the documents to summarize
 * @param outputPath The path to store the merged results in
 */
class Classifier4JSummarizer(inputPath:String, outputPath:String, percent:Double)
    extends AbstractSummarizer {

    private val inputs = new File(inputPath)
    private val output = new File(outputPath)
    private val lines  = math.max(math.ceil(percent * 10), 1).intValue

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
            saveSummary(input.getName, buildSummary(input))
        }
    }

    /**
     * Given an input file, build a summary
     *
     * @param topic The input file to summarise
     * @return The summary of the given input file
     */
    private def buildSummary(input:File) : String = {
        val source = scala.io.Source.fromFile(input.getAbsolutePath).mkString
        val summariser = new SimpleSummariser()
        summariser.summarise(source, lines)
    }

    /**
     * Saves all the topics to file
     *
     * @param topic The topic to save results for
     * @param posts The posts for the specified file to save
     */
    private def saveSummary(name:String, summary:String) {
        logger.debug("Saving document summary {}", name)
        val file   = new File(output, name + "-summary")
        val writer = new BufferedWriter(new FileWriter(file))
        try {
            writer.write(summary)
        } finally {
            writer.close
        }
    }
}

