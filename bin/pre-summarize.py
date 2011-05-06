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
import copy
from optparse import OptionParser

#---------------------------------------------------------------------------# 
# classes
#---------------------------------------------------------------------------# 
class Cluster(object):
    ''' A helper class to organize the results

    @attr name The unique id of this cluster
    @attr value The current size of this cluster
    @attr terms The top terms contributing to this cluster
    '''
    def __init__(self, terms=None, tweets=None):
        self.terms  = terms  or []
        self.tweets = tweets or []
        self.name   = "-".join(self.terms).replace(" ", "_")

#---------------------------------------------------------------------------# 
# helper methods
#---------------------------------------------------------------------------# 
def findClusters(input):
	results = []
	terms = []
	tweetIDs = []
	clusterInfo = []
	inTerms = False
	
	for line in input:	
		if not inTerms:
			# Locate Top Terms
			if "=>" in line:
				term = line.split("=>")[0].strip()
				terms.append(term)
				
			# Found all the terms, move on to next cluster
			elif "Weight:" in line: inTerms = True
						
		# Found all the terms, now gather all the tweet IDs
		else:
			if "1.0: /" in line:
				tweetID = line.split("/")[1].split(" = ")[0]
				tweetIDs.append(tweetID)
				
			# Starting on next cluster, so bail out
			elif "{n=" in line:
				inTerms = False
				results.append(Cluster(terms, tweetIDs))
				terms = []
				tweetIDs = []
	return results

def parse_arguments():
    ''' Parses the input arguments to the script

    @returns (parsed options, extra options)
    '''
    parser = OptionParser()
    parser.add_option("-i", "--input", action="store", type="string",
        dest="input", help="path to the raw tweets directory",
        default="twatter")
    parser.add_option("-c", "--clusters", action="store", type="string",
        dest="clusters", help="path and file of the cluster-results",
        default="twatter-results")
    parser.add_option("-o", "--output", action="store", type="string",
        dest="output", help="root of path to store output files",
        default="twatter")
    parser.add_option("-n", "--number", action="store", type="int",
        dest="number", help="the percent of data to be set aside for testing",
        default=30)
    return parser.parse_args()

def main():
    (options, extra) = parse_arguments()
    
    # Read cluster results input file
    with open(options.clusters) as input:
        clusters = findClusters(input)
    
    if not os.path.exists(options.output):
        os.makedirs(options.output)
    
    for cluster in clusters:
        with open(os.path.join(options.output, cluster.name), 'w') as output:
            for tweetID in cluster.tweets:
                filename = os.path.join(options.input, tweetID)
                if os.path.exists(filename):
                    with open(filename) as tweet:
                        output.write(tweet.readline() + "\n")
	
if __name__ == '__main__':
    sys.exit(main())
