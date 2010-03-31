# execute script within ConnectomeViewer with
#   execfile('examples/nx_graphanalysis.py')
# in the IPython Shell

# get a network
mynetwork = cfile.networks[0]

# get the graph
G = mynetwork.graph

# calculate the degrees for every node
d = nx.degree(G, with_labels=True)

# calculate the clustering coefficients
d = nx.clustering(G,with_labels=True)

# get the biggest value for the dict
list = max(d,key = lambda a: d.get(a))

# list back sorted starting with the smallest value
b = d.keys()
b.sort( key = d.__getitem__ )
# now b is sorted by value
# selecting the ten nodes with the biggest degree value/clustering coefficients
biggest = b[-10:]

# select them in the app
mynetwork.set_selectiongraph(biggest)

# unselect all nodes
# mynetwork.unselect_all()
