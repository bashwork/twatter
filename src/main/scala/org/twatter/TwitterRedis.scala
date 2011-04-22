package org.twatter

/**
 * The keys to use with redis
 */
object TwitterRedis {
    val topicsKey = "topics"
    def topicKey(topic:String) = "topic-" + topic
    def topicCountKey(topic:String) = "topic-" + topic + "-count"
}
