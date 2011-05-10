package org.twatter.language

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class TwitterLanguageGuesserSpec extends FlatSpec with ShouldMatchers {

	behavior of "twitter language guesser"

	it should "guess the correct language" in {
        val datasets  = "src/test/resources/languages/"
        val languages = "src/main/resources/stopwords/"
        val guesser = new TwitterLanguageGuesser(datasets, languages)

        val guesses = guesser.start
        guesses.foreach { case(file, guess) =>
            file.getName.split(".").head should be (guess)
        }
	}
}

