package org.twatter.summary

import scala.actors._
import Actor._
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._
import java.io.{File, BufferedWriter, FileWriter}
import java.io.{BufferedReader, FileReader}

/**
 * Helper utility to summarise a given file using the
 * ots utility.
 *
 * @param inputPath The path to the documents to summarize
 * @param outputPath The path to store the merged results in
 */
class OTSSummarizer(inputPath:String, outputPath:String, percent:Double)
    extends AbstractSummarizer {

    private val inputs = new File(inputPath)
    private val output = new File(outputPath)

    /**
     * The main acting loop for receiving new messages
     */
    def start() {
        logger.info("Summarizing files from {} to {}", inputPath, outputPath)
        inputs.listFiles.foreach { input => actor {
            if (!runOTS(input)) {
                logger.error("Failed to process: ", input.getName)
            }
        } }
    }

    /**
     * Helper method to process each input file with ots
     *
     * @param topic The input file for the specified topic
     */
    private def runOTS(input:File) : Boolean = {
        val command = "/usr/bin/ots -r %d -o %s %s".format(
            math.ceil(percent * 100).intValue,
            new File(output, input.getName + "-summary").getAbsolutePath,
            input.getAbsolutePath)
        val process = Runtime.getRuntime exec command
        process.waitFor == 0
    }
}

