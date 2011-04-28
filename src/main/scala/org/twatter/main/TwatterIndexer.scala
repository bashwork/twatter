package org.twatter.main

import org.apache.commons.cli.Options
import org.twatter.index.TwitterIndexer

/**
 * The main launcher script for the service. This parses the following
 * command line options:
 *
 * - h | help    : prints this help text
 * - v | version : prints the version of the server
 * - o | output  : specify the output file of results
 * - i | inputa  : specify the output file of results
 */
object TwatterIndexer extends TwatterMainTrait {

    override val version  = "1.0.0"
    override val mainName = "org.twatter.main.TwatterIndexer"
    
    /**
     * Processes the command line arguments
     *
     * @param options The options to parse
     * @param error The error callback
     */
    override def process(options: Map[String,Any], error: Unit) {
        implicit def _atos(a:Any) = a.asInstanceOf[String]

        if (!processDirectory(options("input")))  error
        if (!processDirectory(options("output"))) error

        val indexer = new TwitterIndexer(options("input"),
            options("output"))
        indexer.start
    }

    /**
     * Helper method to create the option parser set
     *
     * @param options The options handle to add options to
     * @return The populated option parser set
     */
    override def addOptions(options:Options) : Options = {
        options.addOption("i", "input", true, "specify the input directory for files")
        options.addOption("o", "output", true, "specify the output directory for the indexes")
    }

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    override def createDefaults() = Map[String,Any](
        "input"   -> "twatter",
        "output"  -> "twatter-indexes")
}


