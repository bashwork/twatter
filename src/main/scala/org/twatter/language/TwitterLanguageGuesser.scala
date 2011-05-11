package org.twatter.language

import scala.actors._
import Actor._
import java.io.{File, BufferedReader, FileReader}
import org.slf4j.{Logger, LoggerFactory}
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.WordlistLoader
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.util.Version

/**
 * A class used to attempt to guess the language of a given file.
 *
 * @param inputPath The input file directory to guess the language for
 * @param languagePath The path to the directory containing the stop word lists
 */
class TwitterLanguageGuesser(inputPath:String, languagePath:String) {

    private val logger = LoggerFactory.getLogger(this.getClass)
    private val inputs = new File(inputPath)
    private val languages = new File(languagePath).listFiles.map { language =>
        val mapper = WordlistLoader.getWordSet(language)
        (language.getName.split('.').head, mapper)
    }
    private val analyzer = new StandardAnalyzer(Version.LUCENE_30, new java.util.HashSet)

    /**
     * The main file processing method
     *
     * @returns A list of (File, language)
     */
    def start() : List[(File, String)] = {
        logger.info("Guessing file languages from {}", inputPath)
        (if (inputs.isFile) List(inputs)
         else inputs.listFiles.toList).map { file =>
            val language = processFile(file)
            logger.info("{} -> {}", language, file.getAbsolutePath)
            (file, language)
        }.toList
    }

    /**
     * Helper method to run each file processing in a single thread
     *
     * @param topic The input file for the specified topic
     */
    private def processFile(input:File) : String = {
        val map = scala.collection.mutable.HashMap[String, Int]()
        val reader = new BufferedReader(new FileReader(input))
        val stream = analyzer.tokenStream("", reader)
        val attr = stream.addAttribute(classOf[CharTermAttribute])
        while (stream.incrementToken) {
            val next = new String(attr.buffer, 0, attr.length)
            languages.foreach { case (language, words) => {
                if (words.contains(next)) {
                  map(language) = map.getOrElse(language, 1) + 1
                }
            }}
        }

        map.toList.sortBy { _._2 }.last._1
    }
}
