#!/bin/bash
#------------------------------------------------------------ #
# setup
#------------------------------------------------------------ #
if [ "$JAVA_HOME" == "" ]; then
  JAVA_HOME="`which java`"
fi
START="${JAVA_HOME} -cp jar/twatter.jar org.twatter.main"

#------------------------------------------------------------ #
# script runner
#------------------------------------------------------------ #
case "$1" in 
  index)     COMMAND="${START}.TwatterIndexer"    ;;
  search)    COMMAND="${START}.TwatterSearcher"   ;;
  merge)     COMMAND="${START}.TwatterMerger"     ;;
  twatter)   COMMAND="${START}.Twatter"           ;;
  database)  COMMAND="${START}.TwatterDatabase"   ;;
  summarize) COMMAND="${START}.TwatterSummarizer" ;;
  parse)     COMMAND="${START}.TwatterParser" ;;

  *)
  echo "Usage: ${0} {index|search|merge|twatter|database|summarize|parse}"
  exit 1
  ;;
esac

${COMMAND} ${@:2}
exit 0
