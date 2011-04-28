package org.twatter.parse

import scala.collection.JavaConversions._
import java.io.{File, BufferedWriter, FileWriter}
import java.io.{BufferedReader, FileReader}
import java.io.StringReader
import org.slf4j.{Logger, LoggerFactory}
import edu.stanford.nlp._
import edu.stanford.nlp.ling.Word
import edu.stanford.nlp.trees.PennTreebankLanguagePack
import edu.stanford.nlp.parser.lexparser.LexicalizedParser

/**
 * Helper utility to summarise a given file using the
 * classifer4J library.
 *
 * @param inputPath The path to the documents to parse
 * @param outputPath The path to store the document results
 * @param parserPath The location of the parser to use
 */
class StanfordParser(inputPath:String, outputPath:String,
    parserPath:String) {

    private val logger   = LoggerFactory.getLogger(this.getClass)
    private val inputs   = new File(inputPath)
    private val output   = new File(outputPath)
    private val parser   = new LexicalizedParser(parserPath)
    private val language = new PennTreebankLanguagePack()
    // parser.setOptionFlags(List("-maxLength", "80", "-retainTmpSubcategories"))

    /**
     * The main acting loop for receiving new messages
     */
    def start() {
        logger.info("Parsing files from {} to {}", inputPath, outputPath)
        inputs.listFiles.foreach { input => processFile(input) }
    }

    /**
     * Helper method to run each file processing in a single thread
     *
     * @param topic The input file for the specified topic
     */
    private def processFile(input:File) {
        saveSummary(input.getName, buildTokens(input))
    }

    /**
     * Given an input file, build a list of tokens from
     * its inputs.
     *
     * @param input The input file to tokenize
     * @return The tokenized input file
     */
    private def buildTokens(input:File) : List[Word] = {
        val sentence  = scala.io.Source.fromFile(input.getAbsolutePath).mkString
        val tokenizer = language.getTokenizerFactory()
            .getTokenizer(new StringReader(sentence))
        tokenizer.tokenize.toList
    }

    /**
     * Saves the parsed topic to file
     *
     * @param topic The topic to save results for
     * @param posts The posts for the specified file to save
     */
    private def saveSummary(name:String, tokens:List[Word]) {
        logger.debug("Saving parsed document {}", name)
        parser.parse(tokens)
        val parsed = parser.getBestParse
        val factory = language.grammaticalStructureFactory()
        val structure = factory.newGrammaticalStructure(parsed)
        val dependencies = structure.typedDependenciesCollapsed()

        val file   = new File(output, name + "-parsed")
        val writer = new BufferedWriter(new FileWriter(file))
        try {
            writer.write(parsed.toString)
            writer.newLine
            dependencies.foreach { dependency =>
                writer.write(dependency.toString)
                writer.newLine
            }
        } finally {
            writer.close
        }
    }
}

