package org.twatter.main

import org.apache.commons.cli.Options
import org.twatter.parse.StanfordParser

/**
 * The main launcher script for the service. This parses the following
 * command line options:
 *
 * - i | input   : specify the input directory to parse
 * - o | output  : specify the output directory of the results
 * - p | parser  : specify the location of the parser
 */
object TwatterParser extends TwatterMainTrait {

    override val version  = "1.0.0"
    override val mainName = "org.twatter.main.TwatterParser"

    /**
     * Processes the command line arguments
     *
     * @param options The options to parse
     * @param error The error callback
     */
    override def process(options: Map[String,Any], error: Unit) {

        if (!processDirectory(options("input")))  error
        if (!processDirectory(options("output"))) error
        if (!testFile(options("parser"))) error

        val process = new StanfordParser(options("input"),
            options("output"), options("parser"))
        process.start
    }

    /**
     * Helper method to create the option parser set
     *
     * @param options The options handle to add options to
     * @return The populated option parser set
     */
    override def addOptions(options:Options) : Options = {
        options.addOption("i", "input", true, "specify the input directory to parse")
        options.addOption("o", "output", true, "specify the output directory of the results")
        options.addOption("p", "parser", true, "specify the location of the parser")
    }

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    override def createDefaults() = Map[String,Any](
        "parser"  -> "config/englishPCFG.ser.gz",
        "input"   -> "twatter",
        "output"  -> "twatter-parsed")
}


