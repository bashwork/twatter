package org.twatter.main

import org.apache.commons.cli.Options
import org.twatter.language.TwitterLanguageGuesser

/**
 * The main launcher script for the service. This parses the following
 * command line options:
 *
 * - h | help    : prints this help text
 * - v | version : prints the version of the server
 * - i | input   : specify the input directory for files to label
 */
object TwatterLanguageGuesser extends TwatterMainTrait {

    override val version  = "1.0.0"
    override val mainName = "org.twatter.main.TwatterLanguageGuesser"

    /**
     * Processes the command line arguments
     *
     * @param options The options to parse
     * @param error The error callback
     */
    override def process(options: Map[String,Any], error: Unit) {
        if (!processDirectory(options("input")))  error
        if (!processDirectory(options("language")))  error

        val guesser  = new TwitterLanguageGuesser(
            options("input"), options("language"))
        guesser.start
    }

    /**
     * Helper method to create the option parser set
     *
     * @param options The options handle to add options to
     * @return The populated option parser set
     */
    override def addOptions(options:Options) : Options = {
        options.addOption("i", "input", true, "specify the input directory to use")
        options.addOption("l", "language", true, "specify the directory of the language files")
    }

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    override def createDefaults() = Map[String,Any](
        "language" -> "src/main/resources/stopwords/",
        "input"    -> "documents")
}


