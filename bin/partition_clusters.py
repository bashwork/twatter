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
import os, sys
import shutil
import copy
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
	
if __name__ == '__main__':
	parser = OptionParser()
	parser.add_option("-i", "--input", action="store", type="string",
		dest="input", help="path to the cluster-results file",
		default="twatter")
	parser.add_option("-c", "--clusterResults", action="store", type="string",
		dest="clusterResults", help="path and file of the cluster-results",
		default="twatter-results")
	parser.add_option("-o", "--output", action="store", type="string",
		dest="output", help="root of path to store output files",
		default="twatter")
	parser.add_option("-n", "--number", action="store", type="int",
		dest="number", help="the percent of data to be set aside for testing",
		default=30)
	(options, extra) = parser.parse_args()

	# Read cluster results input file
	with open(options.clusterResults) as input:
		clusters = findClusters(file)

	if not os.path.exists(options.output):
		os.makedirs(options.output)
	
	for cluster in clusters:
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
		with open(os.path.join(options.output, clusterName), 'w') as output:
			# Find each ID and put the tweet into the new file 
			for tweetID in cluster[1]:
				if os.path.exists(os.path.join(options.input, tweetID)):
					with open(os.path.join(options.input, tweetID)) as tweet:
						output.write(tweet.readline() + "\n")
