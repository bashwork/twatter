#!/usr/bin/env python
"""
usage: top_clusters.py -i <cluster-results> -o <results> -n 3

This will scan the cluster-results file to identify the top n clusters 
by size.  It will then output the entire cluster to a file.
"""
import os, sys
import shutil
from optparse import OptionParser


def findClusterInfo(file):
	results = []
	clusterInfo = []
	foundCluster = False
	
	for line in file:	
		if foundCluster == False:
			# locate "{n="
			if line.count("{n=") >= 1:
				cluster = line.split("{")[0]
				count = line.split("{")[1].split(" ")[0].split("=")[1]
				clusterInfo = [cluster, int(count)]
				foundCluster = True
				terms = []
		
		else:
			# Locate Top Terms
			if line.count("=>") >= 1:
				term = line.split("=>")[0].lstrip().rstrip()
				terms.append(term)
						
			# Found all the terms, move on to next cluster
			elif line.count("Weight:") >= 1:
				foundCluster = False
				clusterInfo.append(terms)
				results.append(clusterInfo)
				#print clusterInfo
				
	#print results		
	return results

def identifyTopClusters(counts, clusters):
	sortedClusters = sorted(counts, key=lambda top: top[1], reverse=True)
	topClusters = []
	
	print "The top clusters are:"
	for c in range(0, len(counts)):
		if c > clusters:
			break
		print sortedClusters[c][0], "with count of", sortedClusters[c][1], "and terms", sortedClusters[c][2]
		topClusters.append(sortedClusters[c])
		
	return topClusters

if __name__ == '__main__':
	parser = OptionParser()
	parser.add_option("-i", "--input", action="store", type="string",
		dest="input", help="path to the cluster-results file",
		default="twatter")
	parser.add_option("-o", "--output", action="store", type="string",
		dest="output", help="root of path to store output files",
		default="twatter")
	parser.add_option("-n", "--number", action="store", type="int",
		dest="number", help="the number of top clusters desired",
		default=3)
	(options, extra) = parser.parse_args()
    #split_files(options.input, options.output, options.number)

	# Read input file
	file = open(str(options.input))
	
	# Find the cluster data we're interested in
	clusterInfo = findClusterInfo(file)
	
	# Identify the top n clusters by size
	topClusters = identifyTopClusters(clusterInfo, options.number)
	
	# Write to output file
	newFile = open(str(options.output), 'w')
	for c in topClusters:
		newFile.write(str(c) + "\n")
	
	file.close()
