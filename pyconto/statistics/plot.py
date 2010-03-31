# !/usr/bin/env python

import pylab as P
import networkx as nx

# get graph G

# define nstrip
def nstrip(str):
    return int(str.lstrip('n'))

H = nx.relabel_nodes(analyze_node.currentGraph, nstrip)

# extract the degrees
degdist = H.degree()

# and draw a histogram with 300 bins
#P.hist(degdist,range(0,300), align='left')
P.hist(degdist, 20, align='left')
P.xlabel("Node Degree")
P.ylabel("Number of Nodes")
P.title("Degree distribution") #Preferential Attachment
P.show()


# see nx: nx.degree_