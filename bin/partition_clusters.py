#!/usr/bin/env python
"""
usage: partition_clusters.py -c <cluster-results> -i <original files> -o <results created here> -n 3

example: partition_clusters.py -c ./twatter-results -i ./twatter/ -o ./ -n 30

This will scan the cluster-results file to identify the clusters, randomly
select n% of the tweets in each cluster to be part of a test set, and
create directories for the train and test sets.  These directories are
populated by files named for the class and containing the contents of
each tweet of that class (one per line).
"""
import os, copy
from optparse import OptionParser

def findClusters(file):
	results = []
	terms = []
	tweetIDs = []
	clusterInfo = []
	termsFound = False
	
	for line in file:	
		if termsFound == False:
			# Locate Top Terms
			if line.count("=>") >= 1:
				term = line.split("=>")[0].lstrip().rstrip()
				terms.append(term)
				
			# Found all the terms, move on to next cluster
			elif line.count("Weight:") >= 1:
				termsFound = True
						
		# Found all the terms, now gather all the tweet IDs
		else:
			if line.count("1.0: /") >= 1:
				tweetID = line.split("/")[1].split(" = ")[0]
				tweetIDs.append(tweetID)
				
			# Starting on next cluster, so bail out
			elif line.count("{n=") >= 1:
				termsFound = False
				clusterInfo.append([terms])
				clusterInfo.append(tweetIDs)
				results.append(clusterInfo)
				#print clusterInfo
				
				# reset
				clusterInfo = []
				terms = []
				tweetIDs = []
				
	#print results		
	return results
	
# splits clusters into test and training sets
def partitionClusters(percent, clusters):
	trainClusters = []
	testClusters = []
	for cluster in clusters:
		# Create a new entry for this cluster (the terms) in both sets
		trainCluster = copy.deepcopy(cluster[0])
		testCluster = copy.deepcopy(cluster[0])
		
		trainTweets = []
		testTweets = []
		
		# loop through the tweets and pick out percent number of them for the
		# test set
		n = 0
		for tweetID in cluster[1]:
			# put this in the train set
			if n >= percent:
				trainTweets.append(tweetID)
				
			# put this in the test set
			else:
				testTweets.append(tweetID)
			
			n += 10
			if n > 100:
				n = 0
		
		# put the results into the cluster
		trainCluster.append(trainTweets)
		testCluster.append(testTweets)
		
		# put the cluster into the clusters
		trainClusters.append(trainCluster)
		testClusters.append(testCluster)
	
	return (trainClusters, testClusters)
	
if __name__ == '__main__':
	parser = OptionParser()
	parser.add_option("-i", "--input", action="store", type="string",
		dest="input", help="path to the raw tweets directory",
		default="twatter")
	parser.add_option("-c", "--clusterResults", action="store", type="string",
		dest="results", help="path and file of the cluster-results",
		default="twatter-results")
	parser.add_option("-o", "--output", action="store", type="string",
		dest="output", help="root of path to store output files",
		default="twatter")
	parser.add_option("-n", "--number", action="store", type="int",
		dest="number", help="the percent of data to be set aside for testing",
		default=30)
	(options, extra) = parser.parse_args()

	# Read cluster results input file
	with open(options.results) as results:
		clusters = findClusters(results)
	
	# Parition clusters into train and test set
	(trainClusters, testClusters) = partitionClusters(options.number, clusters)

	# Create two new directories at the output location
	outputs = [options.output + "-twatter-train",
		options.output + "twatter-test"]

	for output in outputs:
		if not os.path.exists(output):
			os.makedirs(output)
	
	for cluster in trainClusters:
		clusterName = ""
		# Build a name for the class from the terms
		for term in cluster[0]:
			if clusterName == "":
				clusterName = str(term)
			else:
				clusterName = clusterName + "-" + str(term)
		# replace white space with _
		clusterName = clusterName.replace(" ", "_")	

		# Create a file for this cluster
		newClassFile = open(str(options.output + "/twatter-train/" + clusterName), 'w')

		# Find each ID and put the tweet into the new file 
		for tweetID in cluster[1]:
			if os.path.exists(options.input + "/" + str(tweetID)):
				#print "Found file for", tweetID, "and put it in",clusterName
				tweetFile = open(options.input + "/" + str(tweetID))
				line = tweetFile.readline();
				newClassFile.write(line + "\n")
				tweetFile.close()
		newClassFile.close()
		
	for cluster in testClusters:
		clusterName = ""
		# Build a name for the class from the terms
		for term in cluster[0]:
			if clusterName == "":
				clusterName = str(term)
			else:
				clusterName = clusterName + "-" + str(term)
		# replace white space with _
		clusterName = clusterName.replace(" ", "_")	

		# Create a file for this cluster
		newClassFile = open(str(options.output + "/twatter-test/" + clusterName), 'w')

		# Find each ID and put the tweet into the new file 
		for tweetID in cluster[1]:
			if os.path.exists(options.input + "/" + str(tweetID)):
				#print "Found file for", tweetID, "and put it in",clusterName
				tweetFile = open(options.input + "/" + str(tweetID))
				line = tweetFile.readline();
				newClassFile.write(line + "\n")
				tweetFile.close()
		newClassFile.close()

	file.close()
