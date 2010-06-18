
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