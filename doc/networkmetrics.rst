.. _networkmetrics:

================
 Network Metrics
================

http://www.pnas.org/content/99/12/7821.abstract
http://en.wikipedia.org/wiki/Network_theory
http://intersci.ss.uci.edu/wiki/index.php/The_complex_network_problem
http://intersci.ss.uci.edu/wiki/index.php/Python_for_networks
http://www.ploscompbiol.org/article/info:doi%2F10.1371%2Fjournal.pcbi.1000408

Hagmann et al.: In particular, we confirm that cortical networks exhibit exponential distributions of node degree and fiber densities, robust small world characteristics, as well as consistent estimates for node centrality at all scales examined.

A multitude of network metrics are in use today to characterize various
properties of these. In the following tutorial, we explore ways to use
the ConnectomeViewer application to apply this metrics
to structural brain networks.

Network analysis has a long-standing tradition in other fields [REF] and has become
a standard analysis method in the neurosciences as well. See [REF SPORNS].


Table: Network metrics and their potential meaning in characterizing nervous systems as
networks.

    * Network Centrality / Centralization
    * Small-World Networks
    * Cluster Analysis
    * Network Density
    * Prestige / Influence
    * Structural Equivalence
    * Network Neighborhood
    * External / Internal Ratio
    * Weighted Average Path Length
    * Shortest Paths & Path Distribution
    * degree
    * strenghts
    * k-core
    * centrality
    * efficency
    * clustering (or transitivity),
    * characteristic path length (distance)
    * maximum flows
    * graph isomorphism
    * scale-free networks
    * community structure finding, etc. (igraph)
    * Connected Components
    * Centrality Measures
    * Path Lengths and Similar Measures
    * Graph Motifs
    * Number of components
    * Diameter
    * Density
    * Average path length: 1.3333
    * largest degree
    * betweenness centrality
    * Assortativity coefficient in Python http://igraph.wikidot.com/python-recipes
    
small-world network and extend this result to show that it may have evolved to reduce axonal wiring.
    
Network Visualization / Graph Layouting
----------------------------------------

* ideas from http://www.plosbiology.org/article/info:doi/10.1371/journal.pbio.0060159 for visualization

http://theneuronetwork.com/group/structureanddynamicsofbrainconnectivitynetworks/forum/topics/some-definitions-for-brain
Component: A component is a set of nodes, for which every pair of nodes is joined by at least one path.

Connectedness: A connected graph has only one component. A disconnected graph has at least two components.

Cycle: A cycle is a path, which links a node to itself.

Degree: The degree of a node is the sum of its incoming (afferent) and outgoing (efferent) connections. The number of afferent and efferent connections is also called the in-degree and out-degree, respectively.

Density (edge density): Proportion of edges (or arcs) existing in the network to the number of all possible edges (arcs).

Distance: The distance between a source node j and a target node i is equal to the length of the shortest path.

Distance matrix: The entries dij of the distance matrix correspond to the distance between node j and i. If no path exists, dij = ∞.

Effective connectivity: Describes the set of causal effects of one neural system over another (Sporns et al. Trends in Cognitive Sciences, 2004). Thus, unlike functional connectivity, effective connectivity is not "model-free", but requires the specification of a causal model including structural parameters. Experimentally, effective connectivity may be inferred through perturbations, or through the observation of the temporal ordering of neural events.

Exponential graph: Erdoes-Renyi random graph (Erdoes, 1960) with binomial degree distribution that can be fitted by an exponential function (Poisson distribution).

Functional connectivity: Captures patterns of deviations from statistical independence between distributed and often spatially remote neuronal units, measuring their correlation/covariance, spectral coherence or phase-locking. Functional connectivity is time-dependent (hundreds of milliseconds) and "model-free", i.e. measuring statistical interdependence (mutual information), without explicit reference to causal effects.

Graph: Graphs are a set of n nodes (vertices, points, units) and k edges (connections, arcs). Graphs may be undirected (all connections are symmetrical) or directed. Because of the polarized nature of most neural connections, we focus on directed graphs, also called digraphs. In addition, graphs are simple, that means, multiple (undirected) edges between nodes or loops (connections of one node to itself) do not exist.

Hodology: The study of pathways. The word is used in several contexts. (1) In brain physiology, it is the study of the interconnections of brain cells. (2) In philosophy, it is the study of interconnected ideas. (3) In geography, it is the study of paths.

Linear graph: Graph with many linear chains of nodes which can be detected by the clustering coefficient being lower than the density (Kaiser & Hilgetag, Physical Review E, 2004).

Path: A path is an ordered sequence of distinct connections and nodes, linking a source node j to a target node i. No connection or node is visited twice in a given path. The length of a path is equal to the number of distinct connections.

Random graph: Also called Erdoes-Renyi graph. A graph with uniform connection probabilities and a binomial degree distribution. All nodes have roughly the same degree ('single-scale').

Scale-free graph: Graph with a power-law degree distribution (Barabasi & Albert, Science, 1999). 'Scale-free' means that degrees are not grouped around one characteristic average degree (scale), but can spread over a very wide range of values, often spanning several orders of magnitude.

Small-world graph: A graph where the clustering coefficient is much higher than in a comparable random network but the characteristic path length remains about the same (Watts & Strogatz, Nature, 1998). The term small-world was coined by the notion that any two persons can be linked over few intermediate acquaintances (Milgram, Psychology Today, 1967).

Spatial graph: Graphs or networks that extent in space, that means that every node has a spatial position. Spatial graphs are usually two- or three-dimensional but more dimensions are possible (Watts, 1999).

Structural connectivity: Physical or synaptic contacts between neural units.


Community detection
-------------------
Synonyms: graph clustering, cluster detection, cohesive subgroup detection, modularity

Objective: group objects that are similar together and dissimilar apart

Need measure: for similarity / dissimilarity

Algorithm taxonomy for hierarchical clustering:
* agglomerative: hierarchical clustering: from single items to the whole dataset, items joined successively
* divisive: from whole dataset to items, dataset is recursively partitioned (number of clusters as input or found by algorithm)
* partitional clustering: dataset is directly partitioned into *k* different clusters usually optimizing some quality function (assignment of nodes to communities)

Procedure to transform into a graph problem: similarity matrix -> thresholding -> graph partitioning procedure

Example algorithm: min-cut with penalty functions (ratio cuts, normalized cutes, min-max cuts)

General understanding of communities: subsets of nodes that are more densely interconnected among each other than with the rest of the network
* hierarchical community structure vs.
* overlapping community structure

Want to find answer: Is my dataset truly modular? So I know what would be the expectation for random graph dataset to compare.

Methods for understanding what the communities mean after you find them are, by contrast, still quite primitive, and much needs to be done if we are to gain real knowledge from the output of our computer programs.

we currently have little idea of how to reliably estimate the properties of networks for which we have incomplete structural data.

Hence a module detection algorithm that assigns proteins into several functional modules is biologically essential -> overlapping!

Want: community detection algorithm for bi-partite community detection

Community detection as artform: skillful module identification requires knowledge of the subject matter and training.

Hierarchical structure information is a topological property of graphs as well