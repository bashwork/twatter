============================================================
Twatter
============================================================

This is basically a twitter topic summarizer. The pieces
currently available are a summarizer and a real-time parser
of messages.

============================================================
How To Run
============================================================

In order to run the parser, you will need an active instance
of Redis running.  You can find a quick setup script in the
config directory that can get you going (or you can download
a Redis executable for your distribution).

============================================================
Building
============================================================

In order to build wdm, you will need the following::

    * some jdk (your pick, but tested on Oracle's...)
    * git (to pull down the source, or just download)
    * ant (to run the build files)

With those dependencies met, this will get you running from
start to finish::

    git clone https://github.com/bashwork/twatter.git
    cd twatter
    ant resolve
    ant package && ./config/twatter.sh help

You can also use sbt if you would like::
  
    ./bin/sbt clean compile package

============================================================
What is Included
============================================================

The following utilities are included in the library. They
can be run on their own (using java -cp org.twatter.main.*)
or by using one of the runner scripts in the config directory
(windows/nix):

* **Twatter** - Start the twatter service that stores every
  tweet from the twitter sample stream to file and to redis.

* **TwatterMerger** - Given a directory of post files and a
  directory of files of each hash topic (with each post
  containing said hash topic), merge all the posts to a
  single file that can be summarized.

* **TwatterDatabase** - Given a twatter redis database, dump
  all the content to parseable files (including post count
  and hash frequency).

* **TwatterSummarizer** - Given a directory of files, create
  summaries of each document (with a given percent of lines
  to create). There are three implementation currently
  supported: classifier4J, OTS, and a quick hack I knocked
  out with Lucene (todo).

* **TwatterIndexer** - Given an input directory, create
  lucene indexes of all the content with the id for each
  document being its file name (twitter post id)

* **TwatterSearcher** - Given a twatter index, perform
  complex searches against all the twitter content currently
  indexed.

============================================================
The Full Process Workflow
============================================================

In order to perform the total clustering and classifying of
twitter tweets:

    ./bin/splitter.py --number=2 --input=twatter --ouput=twatter
    ./bin/mahoutter.sh sequence
    ./bin/mahoutter.sh sparse
    ./bin/mahoutter.sh kmeans 100
    ./bin/mahoutter.sh dump
    ./bin/extract_clusters.py --input=twatter-1-results --number=40
    ./bin/pre_prepare.py -c twatter/twatter-1-results -i twatter/twatter-1 -o twatter
    ./bin/mahoutter.sh prepare train
    ./bin/mahoutter.sh train
    ./bin/mahoutter.sh prepare test
    ./bin/mahoutter.sh test test # note about bad inputs with null error
    mkdir /tmp/twatter/twatter-eval/
    mv /tmp/twatter/twatter-2 /tmp/twatter/twatter-eval
    ./bin/mahoutter.sh prepare eval
    ./bin/mahoutter.sh test eval
    ./bin/partition_clusters.py
    ./bin/twatter.sh summarize --percent 0.25 --input=summary-twatter-test

============================================================
Todo
============================================================

* real time lucene indexing
* lucene -> mahout index
* mahout clustering/classifying
* real time twitter to hadoop

============================================================
Tips
============================================================

If you need to retrieve dependencies behind a proxy, make
sure to set the correct ant options before running ``ant resolve``::

    export ANT_OPTS="-Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=5865"
