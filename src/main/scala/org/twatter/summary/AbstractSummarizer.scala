package org.twatter.summary

import org.slf4j.{Logger, LoggerFactory}

/**
 * Interface for a file summarizer.
 */
trait AbstractSummarizer {

    protected val logger = LoggerFactory.getLogger(this.getClass)

    /**
     * The main acting loop for receiving new messages
     */
    def start() : Unit
}

