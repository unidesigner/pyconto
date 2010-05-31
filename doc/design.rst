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

How do you approach attributes e.g. on edges?
Either add attribute to each edge, or add an attribute_for_edges_list to the graph,
indexed by the edges. How to do it?

Employ a caching mechanism for computationally intensive results of measures.
Make the handling between data in memory and data on disk sensible.

Separate data representation and measurements (=output of algorithms) strictly separate





