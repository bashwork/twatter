#!/bin/bash
#------------------------------------------------------------ #
# setup
#------------------------------------------------------------ #
export JAVA_HOME="/usr"
export MAHOUT_HOME="/tmp/mahout-distribution-0.4"
export MAHOUT_HEAPSIZE=2000
export TWATTER_HOME="/tmp/twatter/"

#------------------------------------------------------------ #
# script runner
#------------------------------------------------------------ #
case "$1" in 
  
  #---------------------------------------------------------- #
  # lucene index to mahout vectors
  #---------------------------------------------------------- #
  vector) ${MAHOUT_HOME}/bin/mahout lucene.vector             \
      --dir ${TWATTER_HOME}/twatter-index                     \
      --output ${TWATTER_HOME}/twatter-vectors                \
      --field contents                                        \
      --dictOut ${TWATTER_HOME}/twatter-dict                  \
      --idField id                           
      --norm 2                                 
  ;;

  #---------------------------------------------------------- #
  # sequence a directory
  #---------------------------------------------------------- #
  sequence) ${MAHOUT_HOME}/bin/mahout seqdirectory            \
      --input ${TWATTER_HOME}/twatter-1                       \
      --output ${TWATTER_HOME}/twatter-1-sequences            \
      --charset utf-8
  ;;

  #---------------------------------------------------------- #
  # make a sequence directory sparse
  #---------------------------------------------------------- #
  sparse) ${MAHOUT_HOME}/bin/mahout seq2sparse                \
      --input ${TWATTER_HOME}/twatter-1-sequences             \
      --output ${TWATTER_HOME}/twatter-1-vectors              \
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
      --input ${TWATTER_HOME}/twatter-sequences               \
      --output ${TWATTER_HOME}/twatter-vectors                \
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
      --input ${TWATTER_HOME}/twatter-1-vectors/tfidf-vectors \
      --output ${TWATTER_HOME}/twatter-1-clusters             \
      --clusters ${TWATTER_HOME}/twatter-1-clusters-initial   \
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
      --input ${TWATTER_HOME}/twatter-1-vectors/tf-vectors    \
      --output ${TWATTER_HOME}/twatter-1-dirichlet            \
      --maxIter 20                                            \
      -k $2                                                   \
      --overwrite                                 
  ;;

  #---------------------------------------------------------- #
  # perform lda on a vector set
  # @param $2 the number of clusters to create
  #---------------------------------------------------------- #
  lda) ${MAHOUT_HOME}/bin/mahout lda                          \
      --input ${TWATTER_HOME}/twatter-1-vectors/tf-vectors    \
      --output ${TWATTER_HOME}/twatter-1-lda                  \
      --maxIter 20                                            \
      --numTopics $2                                          \
      --numWords 50000                                        \
      --overwrite                                 
  ;;

  #---------------------------------------------------------- #
  # dump the lda generated topics
  #---------------------------------------------------------- #
  topics) ${MAHOUT_HOME}/bin/mahout ldatopics                 \
      --input ${TWATTER_HOME}/twatter-1-lda/state-20          \
      --dictionary ${TWATTER_HOME}/twatter-1-vectors/dictionary.file-0    \
      --dictionaryType sequencefile                                 
  ;;

  #---------------------------------------------------------- #
  # dump the cluster results
  #---------------------------------------------------------- #
  dump) ${MAHOUT_HOME}/bin/mahout clusterdump                           \
      --seqFileDir ${TWATTER_HOME}/twatter-1-clusters/clusters-1        \
      --output ${TWATTER_HOME}/twatter-1-results                        \
      --pointsDir ${TWATTER_HOME}/twatter-1-clusters/clusteredPoints    \
      --numWords 5                                                      \
      --dictionary ${TWATTER_HOME}/twatter-1-vectors/dictionary.file-0  \
      --dictionaryType sequencefile                                 
  ;;

  #---------------------------------------------------------- #
  # train a mahout bayesian classifier
  #---------------------------------------------------------- #
  train) ${MAHOUT_HOME}/bin/mahout trainclassifier            \
      --input ${TWATTER_HOME}/twatter-prep-train              \
      --output ${TWATTER_HOME}/twatter-1-model		      \
      --classifierType bayes                                  \
      --gramSize 1						
  ;;

  #---------------------------------------------------------- #
  # test a mahout bayesian classifier on test data
  # @param $2 the dataset to test
  #  test, eval
  #---------------------------------------------------------- #
  test) ${MAHOUT_HOME}/bin/mahout testclassifier              \
      -d ${TWATTER_HOME}/twatter-prep-${2}                    \
      --model ${TWATTER_HOME}/twatter-1-model         	      \
      --classifierType bayes                                  \
      --gramSize 1					      \
      --method sequential				      \
      --verbose 2>&1 | tee ${TWATTER_HOME}/twatter-${2}-output
  ;;

  #---------------------------------------------------------- #
  # run mahout prepare before classification 
  # @param $2 the number of the group to prepare
  #  test, eval, train
  #---------------------------------------------------------- #
  prepare) ${MAHOUT_HOME}/bin/mahout org.apache.mahout.classifier.bayes.PrepareTwentyNewsgroups \
  	-p ${TWATTER_HOME}/twatter-${2}                       \
	-o ${TWATTER_HOME}/twatter-prep-${2}                  \
	-a org.apache.mahout.vectorizer.DefaultAnalyzer       \
  	-c UTF-8
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
