Should also be able to store a modification history which can be run on
different networks!


NetworkOperations
-----------------

NetworkUnion, ...

SubNetworkExtractor

MSTExtractor

Manipulators
------------
- can define a sequence of basic operations (add/remove node/edge, change attribute,
split/merge nodes, symmetrize, threshold), which can then be executed.

each time slice might be represented (e.g. to fed in a temporal analyzer)
- do it in-place (loosing the old network) or generating new network objects?
- synchronous/asynchronous update scheme for node/edge values (also defined by a rule)
- .prune (remove all unconnected, i.e. not connected to the largest component) nodes and edges
