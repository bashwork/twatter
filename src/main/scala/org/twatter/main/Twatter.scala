package org.twatter.main

import org.apache.commons.cli.Options
import redis.clients.jedis.JedisPool
import org.twatter.{TwitterReceiver, TwitterProcessor}

/**
 * The main launcher script for the service. This parses the following
 * command line options:
 *
 * - h | help    : prints this help text
 * - v | version : prints the version of the server
 * - r | redis   : specify the redis uri to use
 * - o | output  : specify the output directory for files
 * - p | poison  : specify a poison word list
 * - t | threads : specify the number of processing threads
 */
object Twatter extends TwatterMainTrait {

    override val version  = "1.0.0"
    override val mainName = "org.twatter.main.Twatter"
    
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
        val poison  = processFile(options("poison"))
        val redis   = processRedis(options("redis"))
        val threads = math.max(options("threads"), 1)
        val process = (1 to threads).map { _ =>
            TwitterProcessor(options("output"), redis, poison)
        }.toList

        val receive = new TwitterReceiver(process)
        receive.start
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
        options.addOption("r", "redis", true, "specify the redis uri to use")
        options.addOption("o", "output", true, "specify the output directory for files")
        options.addOption("p", "poison", true, "specify a poison word list")
        options.addOption("t", "threads", true, "specify the number of processing threads")
    }

    /**
     * Helper method to create the default options
     *
     * @return The default options map
     */
    override def createDefaults() = Map[String,Any](
        "threads" -> "2",
        "redis"   -> "127.0.0.1:6379",
        "poison"  -> "",
        "output"  -> "twatter")
}


