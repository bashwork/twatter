package org.twatter.index

import scala.actors._
import Actor._
//import scala.collection.JavaConversions._
import java.io.{File, BufferedWriter, FileWriter}
import java.io.FileReader
import org.slf4j.{Logger, LoggerFactory}

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{Document, Field}
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.util.Version

/**
 * Helper utility to created indexes for a given directory of files.
 *
 * @param inputPath The path to the documents to index
 * @param outputPath The path to store the indexes in
 */
class TwitterIndexer(inputPath:String, outputPath:String) {

    private val logger = LoggerFactory.getLogger(this.getClass)
    private val inputs = new File(inputPath)
    private val output = FSDirectory.open(new File(outputPath))
    private val analyzer = new StandardAnalyzer(Version.LUCENE_30)
    private val config = new IndexWriterConfig(Version.LUCENE_30, analyzer)
    private val indexer = new IndexWriter(output, config)

    /**
     * The main acting loop for receiving new messages
     */
    def start() {
        logger.info("Indexing files from {} to {}", inputPath, outputPath)
        inputs.listFiles.foreach { file => processFile(file) }
        //indexer.optimize()
        indexer.close()
    }

    /**
     * Helper method to run each file processing in a single thread
     *
     * @param topic The input file for the specified topic
     */
    private def processFile(input:File) {
        actor {
            val document = new Document()
            document.add(new Field("contents", new FileReader(input)))
            document.add(new Field("id", input.getName,
                Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS))
            indexer.addDocument(document)
        }
    }

}

