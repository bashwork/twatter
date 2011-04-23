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
    ant package && ./config/runner.sh

============================================================
Todo
============================================================

...
