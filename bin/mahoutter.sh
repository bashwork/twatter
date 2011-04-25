#!/bin/bash
#------------------------------------------------------------ #
# setup
#------------------------------------------------------------ #
MAHOUT_HOME="./"

#------------------------------------------------------------ #
# script runner
#------------------------------------------------------------ #
case "$1" in 
  
  #---------------------------------------------------------- #
  # lucene index to mahout vectors
  #---------------------------------------------------------- #
  vector) ${MAHOUT_HOME}/bin/mahout lucene.vector             \
      --dir build/twatter-index                               \
      --output build/twatter-vectors                          \
      --field contents                                        \
      --dictOut build/twatter-dict                            \
      --idField id                           
      --norm 2                                 
  ;;

  #---------------------------------------------------------- #
  # sequence a directory
  #---------------------------------------------------------- #
  sequence) ${MAHOUT_HOME}/bin/mahout seqdirectory            \
      --input build/twatter                                   \
      --output build/twatter-sequences                        \
      --charset utf-8
  ;;

  #---------------------------------------------------------- #
  # make a sequence directory sparse
  #---------------------------------------------------------- #
  sparse) ${MAHOUT_HOME}/bin/mahout seq2sparse                \
      --input build/twatter-sequences                         \
      --output build/twatter-vectors                          \
      --maxNGramSize 2                                        \
      --namedVector                                           \
      --minDF 4                                               \
      --maxDFPercent 75                                       \
      --weight TFIDF                                          \
      --norm 2                                 
  ;;

  #---------------------------------------------------------- #
  # perorm kmeans on a vector set
  # @param $2 the number of clusters to create
  #---------------------------------------------------------- #
  kmeans) ${MAHOUT_HOME}/bin/mahout kmeans                    \
      --input build/twatter-vectors/tfidf-vectors             \
      --output build/twatter-clusters                         \
      --clusters build/twatter-clusters-initial               \
      --maxIter 10                                            \
      --numClusters $2                                        \
      --clustering                                            \
      --overwrite                                 
  ;;

  #---------------------------------------------------------- #
  # dump the cluster results
  #---------------------------------------------------------- #
  dump) ${MAHOUT_HOME}/bin/mahout clusterdump                 \
      --seqFileDir build/twatter-clusters/clusters-1          \
      --output build/twatter-results                          \
      --pointsDir build/twatter-clusters/clusteredPoints      \
      --numWords 5                                            \
      --dictionary build/twatter-vectors/dictionary.file-0    \
      --dictionaryType sequenceFile                                 
  ;;

  #---------------------------------------------------------- #
  # train a mahout bayesian classifier
  #---------------------------------------------------------- #
  train) ${MAHOUT_HOME}/bin/mahout trainClassifier            \
      --input build/twatter                                   \
      --output build/twatter-model
  ;;

  #---------------------------------------------------------- #
  # test a mahout bayesian classifier
  #---------------------------------------------------------- #
  test) ${MAHOUT_HOME}/bin/mahout testClassifier              \
      -d build/twatter                                        \
      --model build/twatter-model
  ;;

  #---------------------------------------------------------- #
  # script help
  #---------------------------------------------------------- #
  *)
  echo "Usage: ${0} {vector}"
  exit 1
  ;;
esac

exit 0
