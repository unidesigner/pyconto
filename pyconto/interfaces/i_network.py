""" Network base class """
see: http://www.nature.com/nrn/journal/v11/n4/fig_tab/nrn2807_F2.html

has_self_loops = Bool
has_negative_weights = Bool
has_multi_edges = Bool
is_directed = Bool
is_weighted = Bool
is_signed = Bool
is_connected = Bool

metadata = DictStrAny
nodekeys = DictStrAny
# node types
# - data nodes: e.g. containing data or time series
# - "semantic" node: for particular "semantic" edges, representing annotated knowledge
# two different nodetypes: networks are called bi-partite

edgekeys = DictStrAny
# edge types
# - data edges: e.g. containing data or time series
# - "semantic" edges: e.g. representing different types of relationships between nodes
# - hierarchical edge: e.g. edge building up a hierarchy (e.g. for community detection)
#

Algorithms / Datastructures / Databases / Visualization

Subclasses:
    - Hypergraph with Hyperedges
    - Hierarchical Network (non-overlapping and overlapping / covers)
    - Cariani Network
    - Connectome Network
    - Dynamic Network
    - Time-Delayed Network (Time-Duration Network)
    - Layered Network (representing a structural and a functional graph with node identities)
    
Functions: (answer questions: do copy (preserve data) or work inplace)
    IO:
    - parse_graphml / from_graphml / write_graphml
    - from_matrix / to_matrix
    - pickle
    
    Visualization:
    - layouting algorithms
    
    Pipeline:
    - symmetrize (type: sum, avg, abs_difference, max, min, geom_avg)
    - binarize
    - get_largest_connected_component (or smallest)
    - get_min / get_max value of nodes attr / edges attr
    - threshold (return connectedness)
    
    Measures:
    - small_worldness
    - scale_freeness
    
    Plotting:
    - produce standard plots
    - degree distribution
    - weight distributions (with variable binning sizes)
    
    Others:
    - slice a dynamical array -> returns a static array with correct attributes
    - initialize from data or using a generative algorithm
    - fitting model to data
    - employing caching mechanism similar to nitime for attributes (directed etc.) that need some computation
      possbility to invalidate (e.g. when application of particular algorithms that change them)
    
# Ideas

# - M. C. Gonzalez, A.-L. Barabasi. Complex networks - From data to models. Nature Physics 3, 224-225 (2007)
# - Multifunctional Pattern-Generating Circuits K.L. Briggman1 and W.B. Kristan, Jr.2 http://arjournals.annualreviews.org/doi/full/10.1146/annurev.neuro.31.060407.125552?select23=Choose&cookieSet=1    