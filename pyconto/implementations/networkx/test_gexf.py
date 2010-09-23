#!/usr/bin/python

from gexf import write_gexf, read_gexf, Gexf

# test writing

gexf_container = Gexf("The Creator","The description")
graph=gexf_container.addGraph("directed","static","A New Graph")
graph.addNode("0","hello")
graph.addNode("1","World")
graph.addEdge("0","0","1")
write_gexf(gexf_container,'test.gexf')

# test reading

gexf_container_read = read_gexf('test.gexf')
print gexf_container_read.print_stat()