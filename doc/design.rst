Design Decisions
================

Taxonomy
^^^^^^^^
- node: the basic unit
- edge: the basic unit of relation
- graph: a collection of node and edge instances
- attribute: a graph/node/edge can have multiple attributes. Sometimes refered as keys.
- value: the outcome of a particular attribute
- raw data: unprocessed (i.e. within the framework) data, e.g. an adjacency matrix
- measure: any derived quantity using an algorithm from the raw data
- caching: having data elements ready for fast access, different kinds e.g.
  reading memory from disk when needed into memory, storage of measures without re-computing
- attribute invalidation: meaning that the value of the attribute is not valid anymore
  (and has to be re-computed or re-loaded)


Are nodes/edges allowed to have attributes with multiple values, such as a list of strings?
Yes.

Should PyConTo handle very large networks?
Yes. (This has implications on the data structures and algorithms to use.)

How do you approach attributes e.g. on edges?
Either add attribute to each edge, or add an attribute_for_edges_list to the graph,
indexed by the edges. How to do it?

Employ a caching mechanism for computationally intensive results of measures.
Make the handling between data in memory and data on disk sensible.

Separate data representation and measurements (=output of algorithms) strictly separate

Employ the Enthought Traits Framework

What license to use?
GPL. Much scientific code available and for inclusion, it must be GPL (and that is good)

Allow for associative access of nodes, i.e. Graph.node[0] or Graph.node["zero"] if
"zero" is e.g. an attribute of node with index 0. For example, add an associative
mapping function (saw something similar in Boost.BGL and DiPy)



