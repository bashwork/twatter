package org.twatter.main

import org.apache.commons.cli.Options
import org.twatter.index.TwitterSearcher

/**
 * The main launcher script for the service. This parses the following
 * command line options:
 *
 * - h | help    : prints this help text
 * - v | version : prints the version of the server
 * - i | input   : specify the input location of the index
 * - q | query   : specify the query to execute against the index
 * - c | count   : specify the number of results to return
 */
object TwatterSearcher extends TwatterMainTrait {

    override val version  = "1.0.0"
    override val mainName = "org.twatter.main.TwatterSearcher"
    
    /**
     * Processes the command line arguments
     *
     * @param options The options to parse
     * @param error The error callback
     */
    override def process(options: Map[String,Any], error: Unit) {
        if (!processDirectory(options("input")))  error
        if (!processQuery(options("query")))  error

        val searcher = new TwitterSearcher(options("input"),
            options("query"), options("count"))
        searcher.start
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
        options.addOption("i", "input", true, "specify the input directory of the index")
        options.addOption("q", "query", true, "specify the query to execute on this index")
        options.addOption("c", "count", true, "specify the number of results to return")
    }

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    override def createDefaults() = Map[String,Any](
        "input"  -> "twatter-index",
        "query"  -> "",
        "count"  -> "10")
}


