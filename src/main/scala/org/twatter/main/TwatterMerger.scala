package org.twatter.main

import org.apache.commons.cli.Options
import org.twatter.TwitterMerger

/**
 * The main launcher script for the service. This parses the following
 * command line options:
 *
 * - h | help    : prints this help text
 * - v | version : prints the version of the server
 * - o | output  : specify the output file of results
 */
object TwatterMerger extends TwatterMainTrait {

    override val version  = "1.0.0"
    override val mainName = "org.twatter.main.TwatterMerger"

    /**
     * Processes the command line arguments
     *
     * @param options The options to parse
     * @param error The error callback
     */
    override def process(options: Map[String,Any], error: Unit) {

        if (!processDirectory(options("output"))) error
        if (!processDirectory(options("topics"))) error
        if (!processDirectory(options("raws")))   error

        val merger = new TwitterMerger(options("topics"),
            options("raws"), options("output"))
        merger.start
    }

    /**
     * Helper method to create the option parser set
     *
     * @param options The options handle to add options to
     * @return The populated option parser set
     */
    override def addOptions(options:Options) : Options = {
        options.addOption("r", "raws", true, "specify the raw message directory to use")
        options.addOption("t", "topics", true, "specify the topics directory to use")
        options.addOption("o", "output", true, "specify the output directory for files")
    }

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    override def createDefaults() = Map[String,Any](
        "raws"    -> "twatter",
        "topics"  -> "twatter-db",
        "output"  -> "twatter-merge")
}


