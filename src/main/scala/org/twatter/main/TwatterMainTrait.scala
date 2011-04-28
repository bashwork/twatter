package org.twatter.main

import org.apache.commons.cli._
import org.slf4j.{Logger, LoggerFactory}
import redis.clients.jedis.JedisPool

/**
 * A main trait for the twatter main programs.
 *
 * - h | help    : prints this help text
 * - v | version : prints the version of the server
 */
trait TwatterMainTrait {

    private val logger = LoggerFactory.getLogger(this.getClass)
    val version  : String
    val mainName : String

    /**
     * Implicit conversions
     */
    implicit protected def _atos(a:Any) = a.asInstanceOf[String]
    implicit protected def _atoi(a:Any) = a.toString.toInt

    /**
     * Processes the poison file option
     *
     * @param filename The filename of the poison file
     */
    protected def processQuery(query:String) : Boolean = {
         (query != None) && !query.isEmpty
    }

    /**
     * Processes the poison file option
     *
     * @param filename The filename of the poison file
     */
    protected def processFile(filename:String) : List[String] = {
        if (!filename.isEmpty || new java.io.File(filename).exists)
            scala.io.Source.fromFile(filename).getLines.toList
        else List[String]()
    }

    /**
     * Processes the redis option
     *
     * @param uri The uri of the redis instance
     */
    protected def processRedis(uri:String) : JedisPool = {
        val pieces = uri.split(":")
        val port = if (pieces.length == 2) pieces(1) else "6379"
        new JedisPool(pieces(0), port.toInt)
    }

    /**
     * Processes the output directory option
     *
     * @param filename The filename of the poison file
     */
    protected def processDirectory(filename:String) : Boolean = {
        if (filename.isEmpty) return false

        val file = new java.io.File(filename)
        if (file.exists && file.isDirectory) return true
        if (file.mkdir) return true
        return false
    }
    
    /**
     * Processes the command line arguments
     *
     * @param options The options to parse
     * @param error The error callback
     */
    def process(options: Map[String,Any], error: Unit) : Unit

    /**
     * Helper method to create the option parser set
     *
     * @param options The options handle to add options to
     * @return The populated option parser set
     */
    def addOptions(options:Options) : Options

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    def createDefaults() : Map[String,Any]

    /**
     * Main program start
     *
     * @param args The command line arguments
     */
    def main(args: Array[String]) = {
        var defaults = createDefaults
        val options  = addOptions(createOptions)
        val parser   = new PosixParser()
        val results  = parser.parse(options, args)

        results.getOptions.foreach { o:Option =>
            o.getOpt match {
                case "v" | "version"  => printVersion
                case "h" | "help"     => printHelp(options)
                case option:String if options.hasOption(option) =>
                     defaults += (options.getOption(option).getLongOpt -> o.getValue())
                case _ => printHelp(options)
          }
        }

        process(defaults, () => printHelp(options))
    }

    /**
     * Helper method to create the option parser set
     *
     * @return The populated option parser set
     */
    private def createOptions() : Options = {
        val options = new Options()
        options.addOption("h", "help", false, "print this help text")
        options.addOption("v", "version", false, "print the version of the server")
    }

    /**
     * Helper method to print the current version and exit
     */
    private def printVersion() = {
        println(mainName + " Version " + version)
        exit
    }

    /**
     * Helper method to print the option help and exit
     *
     * @param options The options for the program
     */
    private def printHelp(options: Options) = {
        val format = new HelpFormatter()
        format.printHelp("java -jar " + mainName, options)
        exit
    }
}


