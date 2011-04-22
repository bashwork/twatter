#!/bin/bash
#------------------------------------------------------------ #
# get redis
#------------------------------------------------------------ #
wget http://redis.googlecode.com/files/redis-2.2.5.tar.gz
tar zxf redis-2.2.5.tar.gz
pushd .
cd redis-2.2.5
make
src/redis-server&
popd

#------------------------------------------------------------ #
# get twatter
#------------------------------------------------------------ #
wget http://the dropbox jar
wget --output-document=poison.txt http://www.bannedwordlist.com/lists/swearWords.txt
java -cp twatter.jar org.twatter.main.Twatter -p poison.txt -o twatter-out
