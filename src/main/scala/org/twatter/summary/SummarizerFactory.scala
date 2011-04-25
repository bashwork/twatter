package org.twatter.summary

/**
 * A helper factory to create the selected summarizer type from the
 * available implementations.
 */
object SummarizerFactory {
    def apply(name:String)(input:String, output:String, percent:Double)
        : AbstractSummarizer = {
        name.toLowerCase match {
            case "ots"      => new OTSSummarizer(input, output, percent)
            case "twitter"  => new TwitterSummarizer(input, output, percent)
            case "c4j" | _  => new Classifier4JSummarizer(input, output, percent)
        }
    }
}
