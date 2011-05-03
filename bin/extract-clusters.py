#!/usr/bin/env python
"""
usage: extract-clusters.py -i <cluster-results> -o <results> -n 3

This will scan the cluster-results file to identify the top n clusters 
by size. It will then output the entire cluster to a file.
"""
from optparse import OptionParser

#---------------------------------------------------------------------------# 
# classes
#---------------------------------------------------------------------------# 
class Cluster(object):
    ''' A helper class to organize the results

    @attr name The unique id of this cluster
    @attr weight The current size of this cluster
    @attr terms The top terms contributing to this cluster
    '''
    def __init__(self, name, weight=0):
        self.name   = name
        self.weight = weight
        self.terms  = []

#---------------------------------------------------------------------------# 
# helper methods
#---------------------------------------------------------------------------# 
def parseClusters(input):
    ''' A simple parser to extract the cluster information
    from a mahout clusterDump example.
    
    @param input The input file to parse
    @return The parsed cluster results
    '''
    results = []
    inCluster = False
	
    for line in input:	
        if not inCluster:
            if "{n=" in line: # locate "{n="
                name    = line.split("{")[0]
                weight  = line.split("{")[1].split(" ")[0].split("=")[1]
                results.append(Cluster(name, int(weight)))
                inCluster = True
        else:
            if "=>" in line: # Locate Top Terms
                term = line.split("=>")[0].strip()
                results[-1].terms.append(term)
            # Found all the terms, move on to next cluster
            elif "Weight:" in line: inCluster = False
        		
    return results

def sortBySize(clusters, count):
    ''' Given a list of clusters, return the
    min(count, len(clusters)) number of clusters from
    the list.
    
    @param clusters The clusters to filter
    @param count The number of clusters to extract
    @return Count number of clusters from the list
    '''
    sortedBySize = sorted(clusters, key=lambda c: c.weight, reverse=True)
    return (sortedBySize[idx] for idx in range(0, min(count, len(clusters))))

def formatCluster(cluster):
    ''' Formats a given cluster in the form expected by
    the protovis library.
    
    @param cluster The cluster to format
    @return The formatted cluster output
    '''
    format = "\t{ id: '%s', weight: %d, terms: %s },\n"
    return format % (cluster.name, cluster.weight, str(cluster.terms))

#---------------------------------------------------------------------------# 
# runner script
#---------------------------------------------------------------------------# 
def main():
    ''' The main runner script
    '''
    parser = OptionParser()
    parser.add_option("-i", "--input", action="store", type="string",
        dest="input", help="path to the cluster-results file",
        default="twatter-results")
    parser.add_option("-o", "--output", action="store", type="string",
        dest="output", help="path to the cluster output file",
        default="twatter-clusters.js")
    parser.add_option("-n", "--number", action="store", type="int",
        dest="number", help="the number of top clusters desired",
        default=10)
    (options, extra) = parser.parse_args()
    
    # Find the cluster data we're interested in
    with open(options.input, 'r') as input:
        clusters = parseClusters(input)
    
    # Write to output file
    with open(options.output, 'w') as output:
        output.write("var twatter = [\n")
        for cluster in sortBySize(clusters, options.number):
            output.write(formatCluster(cluster))
        output.write("];")

if __name__ == '__main__':
    main()
