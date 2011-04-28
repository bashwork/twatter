package org.twatter.main

import org.apache.commons.cli.Options
import org.twatter.summary.SummarizerFactory

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
        implicit def _atod(a:Any) = a.toString.toDouble

        if (!processDirectory(options("input")))  error
        if (!processDirectory(options("output"))) error

        val method  = SummarizerFactory(options("method"))_
        val process = method(options("input"),
            options("output"), options("percent"))
        process.start
    }

    /**
     * Helper method to create the option parser set
     *
     * @param options The options handle to add options to
     * @return The populated option parser set
     */
    override def addOptions(options:Options) : Options = {
        options.addOption("i", "input", true, "specify the input directory to use")
        options.addOption("p", "percent", true, "specify the percent to summarize")
        options.addOption("o", "output", true, "specify the output directory for files")
        options.addOption("m", "method", true, "specify the summary method to use")
    }

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    override def createDefaults() = Map[String,Any](
        "percent" -> "0.25",
        "method"  -> "c4j",
        "input"   -> "documents",
        "output"  -> "twatter-summary")
}


