package org.twatter.main

import org.apache.commons.cli.Options
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
object TwatterDatabase extends TwatterMainTrait {

    override val version  = "1.0.0"
    override val mainName = "org.twatter.main.TwatterDatabase"

    /**
     * Processes the command line arguments
     *
     * @param options The options to parse
     * @param error The error callback
     */
    override def process(options: Map[String,Any], error: Unit) {
        implicit def _atos(a:Any) = a.asInstanceOf[String]
        implicit def _atoi(a:Any) = a.toString.toInt

        if (!processDirectory(options("output"))) error
        val redis  = processRedis(options("redis"))

        val process = new TwitterDatabase(options("output"), redis)
        process.start
    }

    /**
     * Helper method to create the option parser set
     *
     * @param options The options handle to add options to
     * @return The populated option parser set
     */
    override def addOptions(options:Options) : Options = {
        options.addOption("r", "redis", true, "specify the redis uri to use")
        options.addOption("o", "output", true, "specify the output directory for files")
    }

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    override def createDefaults() = Map[String,Any](
        "redis"   -> "127.0.0.1:6379",
        "output"  -> "twatter-db")
}


