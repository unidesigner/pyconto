import sys
import random
import math
import numpy.random as nr
from remove_duplicates import remove_duplicates
from optparse import OptionParser


f = 10^-2

def fun_to_zero(p,n,f):
    return pow(1-p,n)*p-f*1.0/n

def uniform(list):
    return random.choice(current_run)

def geometric(list):
    n = len(list)-1
    
    p = 1e-4 #4.3e-4 #find_zero(lambda(x): fun_to_zero(x,n,f),1.0/(n+1),1.0)
    
    i = nr.geometric(p)-1

    if i > n:
        i = n

    return list[i]    

# Either uniform or geometric
pick_random_partition = uniform


if __name__ == '__main__':
    usage="python filter_samples.py [OPTION]... input_file"
    parser = OptionParser(usage)
    parser.add_option("-f", "--file", dest="filename", help="output filtered partitions to FILENAME (default=stdout)")
    parser.add_option("-u", "--ensure-uniqueness", action="store_true", dest="ensure_uniqueness", help="Ensure that the partitions in the resulting sample are unique (default=False)", default=False)
    parser.add_option("-g", "--geometric-sampling", action="store_true", dest="geometric", help="Sample partitions using a geometric distribution rather than a uniform one (default=False)", default=False)

    (options,args) = parser.parse_args()

    if len(args) < 1:
        print "No input file!"
        sys.exit(0)
    
    input_filename = args[0]
    
    if options.geometric:
        pick_random_partition = geometric
    else:
        pick_random_partition = uniform
            
    file = open(input_filename,"r")

    lines_to_keep = []
    current_run = []

    for line in file:
        Q = float(line.split(",")[0])

        if Q < -1:
            #finished a run, so randomly pick a number
            # with probability 1/4 pick the maximum
            if random.random() < 0.25:
                lines_to_keep.append(current_run[-1])
            else:
                lines_to_keep.append(pick_random_partition(current_run))
            current_run = []
        
        current_run.append(line)

    file.close()

    if options.ensure_uniqueness:
        lines_to_keep = remove_duplicates(lines_to_keep)
        
    if options.filename == None:
        for line in lines_to_keep:
            print line,
    else:
        output_file = open(options.filename,"w")
        for line in lines_to_keep:
            output_file.write(line)
        output_file.close()     