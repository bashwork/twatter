package org.twatter.main

import org.apache.commons.cli.Options
import org.twatter.summary.Classifier4JSummarizer

/**
 * The main launcher script for the service. This parses the following
 * command line options:
 *
 * - h | help    : prints this help text
 * - v | version : prints the version of the server
 * - o | output  : specify the output file of results
 */
object TwatterSummarizer extends TwatterMainTrait {

    override val version  = "1.0.0"
    override val mainName = "org.twatter.main.TwatterSummarizer"

    /**
     * Processes the command line arguments
     *
     * @param options The options to parse
     * @param error The error callback
     */
    override def process(options: Map[String,Any], error: Unit) {
        implicit def _atos(a:Any) = a.asInstanceOf[String]
        implicit def _atoi(a:Any) = a.toString.toDouble

        if (!processDirectory(options("input"))) error
        if (!processDirectory(options("output"))) error

        val process = new Classifier4JSummarizer(options("input"),
            options("output"), options("percent"))
        process.start
    }

    /**
     * Helper method to create the option parser set
     *
     * @return The populated option parser set
     */
    override def createOptions() : Options = {
        val options = new Options()
        options.addOption("h", "help", false, "print this help text")
        options.addOption("v", "version", false, "print the version of the server")
        options.addOption("i", "input", true, "specify the input directory to use")
        options.addOption("p", "percent", true, "specify the percent to summarize")
        options.addOption("o", "output", true, "specify the output directory for files")
    }

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    override def createDefaults() = Map[String,Any](
        "percent" -> "0.25",
        "input"   -> "documents",
        "output"  -> "twatter-summary")
}


