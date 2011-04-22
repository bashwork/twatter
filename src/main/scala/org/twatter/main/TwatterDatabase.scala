package org.twatter.main

import org.apache.commons.cli._
import org.slf4j.{Logger, LoggerFactory}
import redis.clients.jedis.JedisPool
import org.twatter.TwitterDatabase

/**
 * The main launcher script for the service. This parses the following
 * command line options:
 *
 * - h | help    : prints this help text
 * - v | version : prints the version of the server
 * - o | output  : specify the output file of results
 */
object TwatterDatabase {

    private val logger = LoggerFactory.getLogger(this.getClass)
    val version  = "1.0.0"
    val mainName = "org.twatter.main.TwatterDatabase"

    /**
     * Processes the redis option
     *
     * @param uri The uri of the redis instance
     */
    def processRedis(uri:String) : JedisPool = {
        val pieces = uri.split(":")
        val port = if (pieces.length == 2) pieces(1) else "6379"
        new JedisPool(pieces(0), port.toInt)
    }

    /**
     * Processes the output directory option
     *
     * @param filename The filename of the poison file
     */
    def processDirectory(filename:String) : Boolean = {
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
    def process(options: Map[String,Any], error: Unit) {
        implicit def _atos(a:Any) = a.asInstanceOf[String]
        implicit def _atoi(a:Any) = a.toString.toInt

        if (!processDirectory(options("output"))) error
        val redis  = processRedis(options("redis"))

        val process = new TwitterDatabase(options("output"), redis)
        process.start
    }

    /**
     * Main program start
     *
     * @param args The command line arguments
     */
    def main(args: Array[String]) = {
        var defaults = createDefaults
        val options  = createOptions
        val parser   = new PosixParser()
        val results  = parser.parse(options, args)

        results.getOptions.foreach { o:Option =>
          o.getOpt match {
              case "i" | "output"   => defaults += ("output" -> o.getValue())
              case "r" | "redis"    => defaults += ("redis"  -> o.getValue())
              case "v" | "version"  => printVersion
              case "h" | "help" | _ => printHelp(options)
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
        options.addOption("r", "redis", true, "specify the redis uri to use")
        options.addOption("o", "output", true, "specify the output directory for files")
    }

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    private def createDefaults() = Map[String,Any](
        "redis"   -> "127.0.0.1:6379",
        "output"  -> "twatter-db")

    /**
     * Helper method to print the current version and exit
     */
    private def printVersion() = {
        println("Twatter Version " + version)
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


