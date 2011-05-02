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
      --weight tfidf                                          \
      --norm 2                                 
  ;;

  #---------------------------------------------------------- #
  # make a sequence directory sparse with tf
  #---------------------------------------------------------- #
  sparsetf) ${MAHOUT_HOME}/bin/mahout seq2sparse              \
      --input build/twatter-sequences                         \
      --output build/twatter-vectors                          \
      --maxNGramSize 2                                        \
      --namedVector                                           \
      --minDF 4                                               \
      --maxDFPercent 75                                       \
      --weight tf                                             \
      --numReducers 3                                         \
      --norm 2                                 
  ;;

  #---------------------------------------------------------- #
  # perform kmeans on a vector set
  # @param $2 the number of clusters to create
  #---------------------------------------------------------- #
  kmeans) ${MAHOUT_HOME}/bin/mahout kmeans                    \
      --input build/twatter-vectors/tfidf-vectors             \
      --output build/twatter-clusters                         \
      --clusters build/twatter-clusters-initial               \
      --maxIter 20                                            \
      --numClusters $2                                        \
      --clustering                                            \
      --overwrite                                 
  ;;

  #---------------------------------------------------------- #
  # perform dirichlet clustering on a vector set
  # @param $2 the number of clusters to create
  #---------------------------------------------------------- #
  dirichlet) ${MAHOUT_HOME}/bin/mahout dirichlet              \
      --input build/twatter-vectors/tf-vectors                \
      --output build/twatter-dirichlet                        \
      --maxIter 20                                            \
      -k $2                                                   \
      --overwrite                                 
  ;;

  #---------------------------------------------------------- #
  # perform lda on a vector set
  # @param $2 the number of clusters to create
  #---------------------------------------------------------- #
  lda) ${MAHOUT_HOME}/bin/mahout lda                          \
      --input build/twatter-vectors/tf-vectors                \
      --output build/twatter-lda                              \
      --maxIter 20                                            \
      --numTopics $2                                          \
      --numWords 50000                                        \
      --overwrite                                 
  ;;

  #---------------------------------------------------------- #
  # dump the lda generated topics
  #---------------------------------------------------------- #
  topics) ${MAHOUT_HOME}/bin/mahout ldatopics                 \
      --input build/twatter-lda/state-20                      \
      --dictionary build/twatter-vectors/dictionary.file-0    \
      --dictionaryType sequenceFile                                 
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
  train) ${MAHOUT_HOME}/bin/mahout trainclassifier            \
      --input build/twatter-merged-train                      \
      --output build/twatter-model
  ;;

  #---------------------------------------------------------- #
  # test a mahout bayesian classifier
  #---------------------------------------------------------- #
  test) ${MAHOUT_HOME}/bin/mahout testclassifier              \
      -d build/twatter-merged-test                            \
      --model build/twatter-model                             \
      -v 2>&1 | tee -a /build/classifierResults.log
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
