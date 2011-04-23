package org.twatter

/**
 * The keys to use with redis
 */
object TwitterRedis {
    val topicsKey = "tw-topics"
    val postsCountKey = "tw-posts-count"
    def topicKey(topic:String) = "tw-topic-" + topic
    def topicCountKey(topic:String) = "tw-topic-" + topic + "-count"
}
