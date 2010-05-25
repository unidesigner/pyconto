Typology of Networks
--------------------

CNetwork : Connectome Network

SNetwork : Structural Network

FNetwork : Functional Network

SFNetwork : Structure-Function Network

CNetwork : Causal Network

HNetwork : Hierarchical Network

PNetwork : Probabilistc Network

TNetwork : Temporal Network
point process vs. continous time

MMNetwork : Multi-Modal Network, nodes/edges can have different roles

FNetwork : Factor Graph Network

PCNetwork : A Peter Cariani Network

AONetwork : Alpha-Omega Network
Brain-Body-Environment-History - All in one pack

PRNetwork : Paul Rogister Network
distance=energy/time networks. 

Dense Networks : All edges have at least a value
Sparse Networks : Only a few edges have a value

Single part network vs. bipartite network

Dimensions :
* Binary
* Directed
* Signed
* Weighted
* Features on the nodes

Global characteristics vs. Local characteristics

Degree correlations:
Assortative network, uncorrelated network, disassortative network

The notion of "distance"

Network attributes
------------------
- Important: What attributes exists is dependent of the class of network!
The heritability structure must be correct.
.number_of_nodes
.number_of_edges
.number_of_components


Attribute complexity
--------------------
- simple types, simple bounded types
- complex types, i.e. classes by themselves, allowing to store rich data
e.g. files, probability density functions, units

Need for a good attribute selection mechanism, and checking mechanism

Creators
--------
- Classes of generative models for given to construct networks

Analyzers
---------
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

Layouter
--------
- either 2D or 3D
- dealing with node / edge positions
- selecting the value to be taken for specific layouting algorithms

SpectralLayout

ForceDirectedLayout


Data representation
-------------------
- Behind the scenes, there should be the option to interchange the data structure used
for representing the network. For different algorithms, different representations might be
more efficient.

Interfaces
==========

Network IO from disk-stored files
---------------------------------
- Reader/Writers, from NetworkX
- Skip header lines for text files

adjacency matrix, pairs file, GraphML

Interfaces to other tools, Packages
-----------------------------------

NetworkInterfacer
- e.g. can handle references of objects to other applications, packages
neuron simulator (PyNN) objects, protein-protein (biological) networks,
NetworkX graph object

Link to Connectome File Format
------------------------------
network, surface, volume, time series, tracks
