Different network algorithm perform different for different implementations.
The implementation should be encapsulated from the different types of networks.
Thus, the user needs to decide which implementation works best for his purposes.

Generic programming, using a well-defined interface with simple operations such as
* Iterating over the edges out of a node
* Access edge information
* Proceeding to a target node of an edge

See
* JDSL, jdsl.graph, separation between interfaces, algorithms, implementations
* Boost graph algorithms

Kinds of implementations

* Unordered sequence of edges
* Adjacency arrays - static graphs
* Adjacency lists - dynamic graphs
* Adjacency matrix
* Implicit representations (e.g. grid graph, interval graph)