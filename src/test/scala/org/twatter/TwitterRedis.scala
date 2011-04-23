package org.twatter

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class TwitterRedisSpec extends FlatSpec with ShouldMatchers {

	behavior of "twitter redis key lookup"

	it should "generate correct keys" in {
		TwitterRedis.topicsKey should be ("tw-topics")
		TwitterRedis.postsCountKey should be ("tw-posts-count")
		TwitterRedis.topicKey("key") should be ("tw-topic-key")
		TwitterRedis.topicCountKey("key") should be ("tw-topic-key-count")
	}
}

