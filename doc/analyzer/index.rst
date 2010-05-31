- Have an intelligent caching/invalidation mechanism not to recompute intensive measures
- Check for node identity to make comparison

ComparisonAnalyzer
- like Mutual Information

TemporalAnalyzer
- .extract_temporal_kernel_function()

CommunityAnalyzer
- .calculate_modularity_matrix()

StructureFunctionAnalyzer
- If node identity is sensibly given, one could conceive of several
measures to characterize their relationship, e.g. simply the correlations

StatisticalAnalyzer

Measures:
- weight-degree correlation
Strength S_k: Is a measure of the inhomogeneities of the distribution of the weights among the nodes of the network.
Disparity Y2_k: Is a measure of the inhomogeneities of the weights ending at a node of degree k.

Global characteristics vs. Local characteristics

.number_of_nodes
.number_of_edges
.number_of_components
Better, these might go into a StatisticalAnalyzer class.


Seperate analyzers could serve as starting point for other packages, methods,
e.g. a functional connectivity analyzer, e.g. does work on dynamic networks with
time series data.