REQUIREMENTS: Python (version >= 2.4), Matlab, Python modules: NumPy (v >=0.6), SciPy (v >= 0.7)

BASIC USAGE INSTRUCTIONS:

If you want to obtain a quick and dirty visualization of the modularity landscape of a particular network, follow these instructions. 

Step 1.) run the following commands in a terminal window:

> python anneal.py -f output_filename -n number_of_runs path/to/network
> python filter_samples.py -u -g -f filtered_filename output_filename

The anneal.py script runs the SA algorithm and stores all sampled partitions (and their modularity values) in output_filename (see below for file format). The filter_samples.py script selects an independent sample of unique partitions from this set as described in the paper and saves them in filtered_filename. The "filtered" partitions can then be passed to the visualization program. 

NOTE: To increase performance, several SA batches can be run in parallel (either on different processors or on different machines) and the resulting partitions can be glued together with the command "python glue.py output_1 output_2 ... > output" before filter_samples.py is called.

Step 2.) then open matlab and run the command

> create_embedding(filtered_filename)

This will create a matlab figure with the visualization that can be saved in a variety of formats. 

ADVANCED OPTIONS:

Both the anneal.py and filter_samples.py scripts have several options and adjustable parameters that can be specified on the command line. These can be tuned to a particular network to try to obtain a better set of samples to use in the visualization algorithm. More advanced uses will require modifying the code. 

anneal.py:

1.) SA cooling schedule

The SA cooling schedule follows a geometric funtion as described in the paper. Both the initial temperature and common ratio can be specified with the command line options -T and -r, respectively. Default values are T=0.00125 and r=0.9995. Generally, larger networks will require a higher initial temperature and a higher common ratio, but the exact values must be found by hand.

2.) Initial partitions

By default, the SA algorithm chooses an initial partition with the number of groups k selected uniformly at random from 1 to the number of nodes n in the network (see the paper). For performance reasons, it is often desirable to specify a k_max < n and have k chosen uniformly from 1...k_max. This prevents the algorithm from spending large amounts of time reducing the number of modules from ~ n to the optimal value of k. A value of k_max can be specified with the command line option -k. 

filter_samples.py

1.) Uniqueness of sampled partitions

The -u option ensures that the partitions saved in the output file are unique. This is necessary in order to ensure that the visualization algorithm works, since it crashes if the partitions are not unique. 

2.) Geometric vs Uniform sampling

The -g option controls whether sampled "low-modularity" partitions are chosen using a geometric or uniform distribution over the steps in the SA algorithm. For long runs, uniform partitions often produce too many partitions with very low modularity, which can lead to a poor visualization.  

FILE FORMATS:

1.) NETWORKS:

Input formats for networks come in two varieties:

network_name.pairs -- This file contains lines of the form "i j" or "i j w" whenever nodes i and j are connected with weight 1 or w, respectively. (In other words, this is just an adjacency list.) The particular labels i and j will be remapped internally so that nodes are labelled by consecutive integers, in the order in which they appear in the file. If this file represents a directed network (see the "-d" option to the anneal.py script), then an entry "i j w" represents an edge from i to j with weight w.

network_name.adj -- this file is a textual representation of the network's full adjacency matrix. This is an n-by-n matrix (where n is the number of nodes in the network) with columns separated by spaces and rows separated by newlines. In the unweighted case, the entry A[i,j] contains the weight of the edge between i and j. If this file represents a directed network (see the "-d" option to the anneal.py script), then an entry in row i and column j represents the weight of an edge from j to i (note the order of the i and j here, it might not be what you expect).

2.) PARTITIONS:

In the input format for the embedding algorithm (which is also the output format for the SA algorithm), each line represents a different partition and is of the form

Q,[s_1,s_2,...,s_n]

where Q is the modularity of that partition and s_i is the module label for the ith node in the network. The modules are labeled in such a way that 

s_i <= max(s_1,s_2,...,s_(i-i))+1

Lines with Q=-2 are ignored by the embedding algorithm. These are used to denote the end of a particular SA run.