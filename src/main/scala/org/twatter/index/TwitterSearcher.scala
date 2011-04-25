package org.twatter.index

import scala.actors._
import Actor._
import org.slf4j.{Logger, LoggerFactory}
import java.io.File

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version

/**
 * Helper utility to created indexes for a given directory of files.
 *
 * @param indexPath The path to the document index
 * @param query The query to execute against the index
 * @param count The number of results to collect
 */
class TwitterSearcher(indexPath:String, query:String, count:Int) {

    private val logger = LoggerFactory.getLogger(this.getClass)
    private val index  = new File(indexPath)
    private val analyzer = new StandardAnalyzer(Version.LUCENE_30)
    private val searcher = new IndexSearcher(FSDirectory.open(index))
    private val parser = new QueryParser(Version.LUCENE_30, "contents", analyzer)

    /**
     * The main jumping point for searching the index
     */
    def start() {
        logger.info("Querying Index {} with {}", indexPath, query)
        val files = queryIndex
        searcher.close
    }

    /**
     * Helper method to perform the actual lucene search
     */
    private def queryIndex {
        val search = parser.parse(query)
        val results = searcher.search(search, count)
        val scores = results.scoreDocs
        val index = math.min(count, scores.length) - 1

        logger.info("Found {} possible matches", results.totalHits)
        (0 to index).foreach { id =>
            parseMatch(searcher.doc(id), scores(id).score) }
    }

    /**
     * Helper method to retrieve the relevant fields from the
     * matched documents.
     *
     * @param document The document that was matched
     */
    private def parseMatch(document:Document, score:Float) {
        actor {
            val id = document.get("id")
            val path = document.get("path")
            val content = new File(path).exists match {
                case true => scala.io.Source.fromFile(path).mkString
                case false => "original tweet not available"
            }

            print("(" + score + ")")
            print("[" + id + "]:" + content)
        }
    }
}

