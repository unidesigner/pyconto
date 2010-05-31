Hierarchical Network
====================

Branching is a general principle in our universe. It can be observed in
many ways: branching of an unified force into four basic physical forces,
structure of trees.

A hierarchy can be seen as the converse, where the root is start of the branching
process.

Aims
----
* Provide a easy-to-use datastructure to represent and analyse networks of the
hierarchical type

* ...

    Definition Hiearchial Network: vertices form groups, set of groups form
    groups. Covering or not.
    
Ideas
-----
* Think about a probability measure of group affiliation per vertex
* Underatnd how a dendrogram represents hierarchy
* Hierarchy based on clustering of the edges vs. the nodes
* Relation to community finding, which is basically a partition of the nodes,
often the clustering algorithm induces implicitly a hierarchy, but communities
define not per default a hierarchical network!

Definition: Block-model/Non-Overlapping
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
G: graph with n vertices
D = {D_1, D_2, ..., D_{n_1}}, partitioning of the edges
D_i : internal node
(u,v): associated with an unique D_i, as their lowest common ancestor

Model:
\Theta_i associated with D_i: probability that an edge exists in set D_i

Definition: Cover/Overlapping
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Examples
^^^^^^^^
* Default dataset 1: Zachary's karate club
* Default dataset 2: 2000 schedule of NCAA college American football teams
* Fancy dataset: Galois permutation groups

Visualization
^^^^^^^^^^^^^
* Dendrogram (changes in node shape, edge weight, positioning)

References
----------
* Herbert Simon
* Howard Pattee
* Structural Inference of Hierarchies in Networks, Clauset et al.