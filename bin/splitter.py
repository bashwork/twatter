#!/usr/bin/env python
"""
usage: splitter.py -i <raw tweets> -o <prefix> -n 3

This will produce three directories with three even splits
of raw twitter feeds. This can be used to split a dataset
into N groups for training, evaluating, and performance
testing.
"""
import os, sys
import shutil
from optparse import OptionParser

# ---------------------------------------------------------- #
# support methods
# ---------------------------------------------------------- #
def generate_paths(path):
    ''' Given a path, return a generator around each file
    in the given directory if it exists

    @param path The path to generate contents for
    @return A generator around the directories contents
    '''
    if os.path.exists(path):
        for name in os.listdir(path):
            yield name
    else: raise Exception("directory doesn't exist")

def initialize(path, number):
    ''' Given a path prefix and a number, generate the
    number of directories of the form path-N.

    @param path The path prefix to generate
    @param number The number of directories to create
    @return A list of the possible output directories
    '''
    paths = [path + "-%d" % v for v in range(1, number + 1)]
    for name in paths:
        os.mkdir(name)
    return paths

def split_files(input, output, number):
    ''' Given an input directory, an output path, and a
    number, spilt the contents of input into N output
    directories evenly.

    @param input The input directory to pull content from
    @param output The output prefix to move files to
    @param number The number of directories to create
    '''
    current, paths = 0, initialize(output, number)
    for name in generate_paths(input):
        shutil.move(os.path.join(input, name),
            os.path.join(paths[current], name))
        current = (current + 1) % number

# ---------------------------------------------------------- #
# main runner
# ---------------------------------------------------------- #
def main():
    parser = OptionParser()
    parser.add_option("-i", "--input", action="store", type="string",
        dest="input", help="path to retrieve the input files",
        default="twatter")
    parser.add_option("-o", "--output", action="store", type="string",
        dest="output", help="root of path to store output files",
        default="twatter")
    parser.add_option("-n", "--number", action="store", type="int",
        dest="number", help="the number of splits to make",
        default=3)
    (options, extra) = parser.parse_args()
    split_files(options.input, options.output, options.number)

if __name__ == "__main__":
    sys.exit(main())
    
