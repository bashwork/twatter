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
# Poison words came from:
# http://www.bannedwordlist.com/lists/swearWords.txt
#------------------------------------------------------------ #
git clone https://github.com/bashwork/twatter.git
cd twatter
ant package
java -cp jar/twatter.jar org.twatter.main.Twatter -p config/poison.txt -o build/twatter
