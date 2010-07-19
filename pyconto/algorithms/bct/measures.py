""" The measures of the Brain Connectivity Toolbox with an unified
and simplified interface. Memory management. Optional network typechecking """

# add: Wrapped to Python by Stephan Gerhard, EPFL, 2010
# within function, matrix type check only upon request (check=False)

import sys
# check for linux support
if sys.platform == 'linux2':   
    # examine the architecture
    import platform as pf
    if '32' in pf.architecture()[0]:
        import bit32.bct as bct
    elif '64' in pf.architecture()[0]:
        import bit64.bct as bct
    else:
        raise('Can not determine your architecture settings.')

def check_matrix(cmatrix, type):
    """ Checks if the cmatrix expresses type.
    
    Valid types are:
    bu : binary undirected
    wu : weighted undirected
    bd : binary directed
    wd : weighted directed

    If not, exceptions are thrown.
    
    """
    
    # valid types, XXX: functions that require only definition of d or u?
    # ev. split into: weighttype, weighted, directionality
    vtype = ['bu', 'wu', 'bd', 'wd']
    if not type.lower() in vtype:
        raise Exception('Not a valid type. Valid types are: bu, wu, bd, wd')

    # SEE: bct-cpp: status.cpp!

    # check if NumPy array or matrix!

    # check binary or weighted
    
    # check if matrix contains NaNs
    
    # check if directed or undirected
    
    # check if square

def assortativity(cmatrix, type = 'bu'):
    """
    Parameters
    ----------
    
    inputs:
                  CIJ       connection matrix
                  flag      1 = directed graph; 0 = non-directed graph
    outputs:
                  r         assortativity
    
    assortativity, computed after Newman (2002)
    
    Note: Weights are discarded, no edges on main diagonal
    
    Olaf Sporns, Indiana University, 2007/2008
    Vassilis Tsiaras, University of Crete, 2009
    
    """
    
def degree(cmatrix):
    """ Density is the proportion of the number of present connections in the
    network, to the maximum possible number of connections.  Connection weights
    are ignored. See e.g. Sporns (2002). Contributor: OS.
    
    % input:  
%         CIJ  = connection/adjacency matrix
% output: 
%         id   = indegree for all vertices
%         od   = outdegree for all vertices
%         deg  = degree for all vertices
%
% Computes the indegree, outdegree, and degree (indegree + outdegree) for a
% directed binary matrix.  Weights are discarded.
%
% Note: Inputs of CIJ are assumed to be on the columns of the matrix.
%
% Olaf Sporns, Indiana University, 2002/2006/2008

% input:  
%         CIJ  = connection/adjacency matrix
% output: 
%         deg  = degree for all vertices
%
% Computes the degree for a nondirected binary matrix.  Weights are
% discarded.
%
% Olaf Sporns, Indiana University, 2002/2006/2008


    """
    m = bct.to_gslm(cmatrix.tolist())
    deg = bct.degrees_und(m)
    rdeg = bct.from_gsl(deg)  # [2.0, 2.0, 2.0]
    bct.gsl_free(m)
    bct.gsl_free(deg)
    return rdeg

def efficiency(cmatrix, local = False, type = 'bu'):
    """ function E=efficiency(G,local)
    %Global and local efficiency for binary undirected graph G.
    %
    %Eglob=efficiency(G); outputs the inverse distance matrix: the mean of this
    %matrix (excluding main diagonal) is equivalent to the global efficiency.
    %
    %Eloc=efficiency(G,1); outputs individual nodal local efficiency.
    %For directed networks, local efficiency works with the out-degree.
    %
    %Reference: Latora and Marchiori, 2001, Phys Rev Lett 87:198701.
    %
    %Algebraic shortest path algorithm.
    %
    %Mika Rubinov, UNSW, 2008 (last modified September 2008).
    """
    pass

def betweenness(cmatrix, type):
    """ Weighted:
    function BC=betweenness_wei(G)
    %BC=betweenness_wei(G); betweenness centrality BC for weighted directed graph
    %
    %The input matrix must be a mapping from weight to distance (eg. higher
    %correlations may be interpreted as short distances - hence an inverse
    %mapping is appropriate in that case).
    %
    %Betweenness may be normalised to [0,1] via BC/[(N-1)(N-2)]
    %
    %Brandes's modified Dijkstra's algorithm; J Math Sociol (2001) 25:163-177.
    %
    %Mika Rubinov, UNSW, 2007 (last modified July 2008)
    
        Binary directed:
        
        %BC=betweenness_bin(G); betweenness centrality BC, for a binary directed graph G
    %
    %Betweenness may be normalised to [0,1] via BC/[(N-1)(N-2)]
    %
    %Algorithm of Kintali, generalised to directed and disconnected graphs
    %http://www.cc.gatech.edu/~kintali/papers/bc.pdf
    %
    %Mika Rubinov, UNSW, 2007 (last modified July 2008)
    """
    pass

def breadth(cmatrix, source):
    """
   function [distance,branch] = breadth(CIJ,source)

    % input:   
    %           CIJ = connection/adjacency matrix
    %           source   = source vertex
    % outputs:  
    %           distance = distance between 'source' and i'th vertex
    %                      (0 for source vertex)
    %           branch   = vertex that precedes i in the breadth-first search tree
    %                      (-1 for source vertex)
    %        
    % note: breadth-first search tree does not contain all paths 
    % (or all shortest paths), but allows the determination of at least one 
    % path with minimum distance.
    % the entire graph is explored, starting from source vertex 'source'
    %
    % Olaf Sporns, Indiana University, 2002/2007/2008
    """
    pass

def breadthdist(cmatrix):
    """
    function  [R,D] = breadthdist(CIJ)

    % input:  
    %           CIJ = connection/adjacency matrix
    % outputs: 
    %           R   = reachability matrix
    %           D   = distance matrix
    
    % This function is potentially less memory-hungry than 'reachdist.m',
    % particularly if the characteristic path length is rather large.
    %
    % Olaf Sporns, Indiana University, 2002/2007/2008
    """
    pass

def charpath(cmatrix):
    """
    function  [lambda,ecc,radius,diameter] = charpath(D)

    % input:  
    %           D          distance matrix
    % outputs: 
    %           lambda     characteristic path length
    %           ecc        eccentricity (for each vertex)
    %           radius     radius of graph
    %           diameter   diameter of graph
    %
    % Characteristic path length is calculated as the global mean of the
    % distance matrix D, not taking into account any 'Infs' but including the
    % distances on the main diagonal.
    %
    % Olaf Sporns, Indiana University, 2002/2007/2008
    """
    pass

def clustering_coef(cmatrix, type):
    """
    
    function C=clustering_coef_bd(A)
%C=clustering_coef_bd(A); clustering coefficient C, for binary directed graph A
%
%Reference: Fagiolo, 2007, Phys Rev E 76:026107.
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008)

%In directed graphs, 3 nodes generate up to 8 triangles (2*2*2 edges)
%The number of existing triangles is the main diagonal of S^3/2
%The number of all (in or out) neighbour pairs is K(K-1)/2
%Each neighbour pair may generate two triangles
%"False pairs" are i<->j edge pairs (these do not generate triangles)
%The number of false pairs is the main diagonal of A^2
%Thus the maximum possible number of triangles = 
%  = (2 edges)*([ALL PAIRS] - [FALSE PAIRS]) =
%  = 2 * (K(K-1)/2 - diag(A^2)) = K(K-1) - 2(diag(A^2))


%C=clustering_coef_bu(G); clustering coefficient C, for binary undirected graph G
%
%Reference: Watts and Strogatz, 1998, Nature 393:440-442
%
%Mika Rubinov, UNSW, 2007 (last modified September 2008)


%C=clustering_coef_wd(W); clustering coefficient C for weighted directed graph W.
%
%Reference: Fagiolo, 2007, Phys Rev E 76:026107
%(also see Onnela et al. 2005, Phys Rev E 71:065103);
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008)

%See comments for clustering_coef_bd
%The weighted modification is as follows:
%- The numerator: adjacency matrix is replaced with weights matrix ^ 1/3
%- The denominator: no changes from the binary version
%
%The above reduces to symmetric and/or binary versions of the
%   clustering coefficient for respective graphs.


%C=clustering_coef_wu(W); clustering coefficient C for weighted undirected graph W.
%
%Reference: Onnela et al. 2005, Phys Rev E 71:065103
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008)

    """
    pass

def cycprob(Py):
    """
    
function [fcyc,pcyc] = cycprob(Pq)

% input:  
%           Pq       3D matrix, with Pq(i,j,q) = number of paths from 
%                    'i' to 'j' of length 'q' (produced by 'findpaths').
% outputs: 
%           fcyc     fraction of all paths that are cycles, 
%                    for each path length 'q'. 
%           pcyc     probability that a non-cyclic path of length 'q-1' 
%                    can be extended to form a cycle of length 'q',
%                    for each path length 'q', 
%
% Olaf Sporns, Indiana University, 2002/2007/2008

    """
    pass


def density(cmatrix, type):
    """
    function [kden,N,K] = density_dir(CIJ)

% input:  
%           CIJ  = connection/adjacency matrix
% output: 
%           kden = connection density, number of connections present out of all possible (N^2-N)
%           N    = number of vertices
%           K    = number of edges for the entire graph

% Note: Assumes that CIJ is directed and that there are no self-connections.
% Note: Function always returns average binary density, regardless of
% weights.
%
% Olaf Sporns, Indiana University, 2002/2007/2008


function [kden,N,K] = density_und(CIJ)

% input:  
%           CIJ  = connection/adjacency matrix
% output: 
%           kden = connection density, number of connections present out of all possible (N^2-N)
%           N    = number of vertices
%           K    = number of edges for the entire graph

% Note: Assumes that CIJ is undirected and that there are no self-connections.
% Note: Function always returns average binary density, regardless of
% weights.
%
% Olaf Sporns, Indiana University, 2002/2007/2008

% Modification history:
% 2009-10: K fixed to sum over one half of CIJ [Tony Herdman, SFU]

    """
    pass

def distance(cmatrix, type):
    """
function D=distance_bin(G)
%D=distance_bin(G); distance matrix for binary undirected graph G
%Mean distance (excluding the main diagonal) equals the characteristic path length
%
%Algebraic shortest path algorithm.
%
%Mika Rubinov, UNSW, 2007 (last modified September 2008).

function D=distance_wei(G)
%D=distance_wei(G); distance matrix for a weighted directed graph -
%the mean distance is the characteristic path length.
%
%The input matrix must be a mapping from weight to distance (eg. higher
%correlations may be interpreted as short distances via an inverse mapping).
%
%Dijkstra's Algorithm.
%
%Mika Rubinov, UNSW

%Modification history
%2007: original
%2009-08-04: min() function vectorized

    """
    pass

def edge_betweenness(cmatrix, type):
    """
    function [EBC BC]=edge_betweenness_bin(G)
%EBC=edge_betweenness_bin(G); edge betweenness centrality EBC, for a binary graph G
%[EBC BC]=edge_betweenness_bin(G), also outputs vertex betweenness centrality BC
%
%Betweenness may be normalised to [0,1] via EBC/[(N-1)(N-2)]
%
%Brandes's modified breadth-first search; J Math Sociol (2001) 25:163-177.
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008).

function [EBC BC]=edge_betweenness_wei(G)
%EBC=edge_betweenness_wei(G); edge betweenness centrality EBC for weighted directed graph
%[EBC BC]=edge_betweenness_wei(G), also outputs vertex betweenness centrality BC
%
%The input matrix must be a mapping from weight to distance (eg. higher
%correlations may be interpreted as short distances - hence an inverse
%mapping is appropriate in that case).
%
%Betweenness may be normalised to [0,1] via EBC/[(N-1)(N-2)]
%
%Brandes's modified Dijkstra's algorithm; J Math Sociol (2001) 25:163-177.
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008)


    """
    pass

def erange(cmatrix):
    """
    function  [Erange,eta,Eshort,fs] = erange(CIJ)

% input:  
%           CIJ      connection/adjacency matrix
% outputs:   
%           Erange   range for each edge, i.e. the length of the 
%                    shortest path from i to j for edge c(i,j) AFTER the edge 
%                    has been removed from the graph.
%           eta      average range for entire graph.
%           Eshort   entries are ones for edges that are shortcuts.
%           fs       fraction of shortcuts in the graph.
%
% Follows the treatment of 'shortcuts' by Duncan Watts
%
% Olaf Sporns, Indiana University, 2002/2007/2008
% =========================================================================
    """
    pass

def find_motif34(m,n):
    """
    
    function M=find_motif34(m,n)
%find_motif34; motif description function
%
%Returns all motif isomorphs for a given motif ID and class (3 or 4):
% MOTIF_MATRICES=find_motif34(MOTIF_ID,MOTIF_CLASS)
%
%Returns the motif id for a given motif matrix (e.g. [0 1 0; 0 0 1; 1 0 0])
% MOTIF_ID=find_motif34(MOTIF_MATRIX)
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008)

    """
    pass

def findpaths(cmatrix, sources, qmax, savepths):
    """
function [Pq,tpath,plq,qstop,allpths,util] = findpaths(CIJ,sources,qmax,savepths)

% inputs:
%           CIJ        connection/adjacency matrix
%           qmax       maximal path length
%           sources    source units from which paths are grown
%           savepths   set to 1 if all paths are to be collected in
%                      'allpths'
% outputs:
%           Pq         3D matrix, with P(i,j,q) = number of paths from
%                      'i' to 'j' of length 'q'.
%           tpath      total number of paths found (lengths 1 to 'qmax')
%           plq        path length distribution as a function of 'q'
%           qstop      path length at which 'findpaths' is stopped
%           allpths    a matrix containing all paths up to 'qmax'
%           util       node use index
%
% Note that Pq(:,:,N) can only carry entries on the diagonal, as all "legal"
% paths of length N-1 must terminate.  Cycles of length N are possible, with
% all vertices visited exactly once (except for source and target).
% 'qmax = N' can wreak havoc (due to memory problems).
%
% Note: Weights are discarded.
% Note: I am fairly certain that this algorithm is rather inefficient -
% suggestions for improvements are welcome.
%
% Olaf Sporns, Indiana University, 2002/2007/2008
% =========================================================================
    """
    pass

def findwalks(cmatrix):
    """
    
function [Wq,twalk,wlq] = findwalks(CIJ)

% input:  
%           CIJ       connection/adjacency matrix
% outputs: 
%           Wq        3D matrix, with Wq(i,j,q) = number of walks from 
%                     'i' to 'j' of length 'q'.
%           twalk     total number of walks found
%           wlq       walk length distribution as function of 'q'
%
% Uses the powers of the adjacency matrix to produce numbers of walks
% Note that Wq grows very quickly for larger N,K,q.
% Note: Weights are discarded.
%
% Written by Olaf Sporns, Indiana University, 2002/2007/2008

    """
    pass

def jdegree(cmatrix, type = 'd'):
    """
    function [J,J_od,J_id,J_bl] = jdegree(CIJ)

% input
%         CIJ  = connection/adjacency matrix
% output
%         J    = joint degree distribution matrix (shifted by one)
%         J_od = number of vertices with od>id.
%         J_id = number of vertices with id>od.
%         J_bl = number of vertices with id=od.
%
% Note: This function only makes sense for directed matrices.  Weights are
% discarded.
%
% Olaf Sporns, Indiana University, 2002/2006/2008

    """
    pass

def latmio_dir(r, iter):
    """
    function R=latmio_dir(R,ITER)
%R=latmio_dir(G,ITER); 'latticized' graph R, with equivalent degree
%sequence to the original weighted directed graph G.
%
%Each edge is rewired (on average) ITER times. The out-strength (but not
%the in-strength) distribution is preserved for weighted graphs.
%
%Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
%Latticizing algorithm: Sporns and Zwi (2004); Neuroinformatics 2:145
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008).
    """
    pass

def latmio_dir_connected(cmatrix):
    """
    function R=latmio_dir_connected(R, ITER)
%R=latmio_dir_connected(G,ITER); 'latticized' graph R, with equivalent degree
%sequence to the original weighted directed graph G, and with preserved
%connectedness (hence the input graph must be connected).
%
%Each edge is rewired (on average) ITER times. The out-strength (but not
%the in-strength) distribution is preserved for weighted graphs.
%
%Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
%Latticizing algorithm: Sporns and Zwi (2004); Neuroinformatics 2:145
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008).
    """
    pass

def latmio_und(r, iter):
    """
    function R=latmio_und(R, ITER)
%R=latmio_und(G,ITER); 'latticized' graph R, with equivalent degree
%sequence to the original weighted undirected graph G.
%
%Each edge is rewired (on average) ITER times. The strength distributions 
%are not preserved for weighted graphs.
%
%Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
%Latticizing algorithm: Sporns and Zwi (2004); Neuroinformatics 2:145
%
%Mika Rubinov, UNSW
%
%Modification History:
%Jun 2007: Original
%Apr 2008: Edge c-d is flipped with 50% probability, allowing to explore
%          all potential rewirings (Jonathan Power, WUSTL)
    """
    pass

def latmio_und_connected(r, iter):
    """
    function R=latmio_und_connected(R, ITER)
%R=latmio_und_connected(G,ITER); 'latticized' graph R, with equivalent degree
%sequence to the original weighted undirected graph G, and with preserved
%connectedness (hence the input graph must be connected).
%
%Each edge is rewired (on average) ITER times. The strength distributions 
%are not preserved for weighted graphs.
%
%Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
%Latticizing algorithm: Sporns and Zwi (2004); Neuroinformatics 2:145
%
%Mika Rubinov, UNSW
%
%Modification History:
%Jun 2007: Original
%Apr 2008: Edge c-d is flipped with 50% probability, allowing to explore
%          all potential rewirings (Jonathan Power, WUSTL)
    """
    pass

def makeevenCIJ(N,K,sz_cl):
    """
    function  [CIJ] = makeevenCIJ(N,K,sz_cl)

% inputs:
%           N        number of vertices (must be power of 2)
%           K        number of edges
%           sz_cl    size of clusters (power of 2)
% outputs:
%           CIJ      connection matrix
%
% Makes a connection matrix with equal sized clusters placed on the
% diagonal, and the remaining connections distributed evenly (randomly) among them
% NOTE: 
% Only works if N is a power of 2.
% A warning is generated if the clusters contain more connections than K.
% Cluster size is 2^sz_cl;
%
% Olaf Sporns, Indiana University, 2005/2007

    """
    pass

def makefractalCIJ(mx_lvl,E,sz_cl):
    """
    function  [CIJ,K] = makefractalCIJ(mx_lvl,E,sz_cl)

% inputs:
%           mx_lvl   number of hierarchical levels, N = 2^mx_lvl
%           E        connection density fall-off per level
%           sz_cl    size of clusters (power of 2)
% outputs:
%           CIJ      connection matrix
%           K        number of connections present in the output CIJ
%
% NOTE: 
% Clusters have by default a connection density of 1
% Connection density decays as 1/(E^n), with n = index of hierarchical level
%
% Olaf Sporns, Indiana University, 2005/2007
    """
    pass

def makelatticeCIJ(N,K):
    """
    function [CIJ] = makelatticeCIJ(N,K)

% inputs:
%           N        number of vertices
%           K        number of edges
% outputs:
%           CIJ      connection matrix
%
% makes one lattice CIJ matrix, with size = N,K. The lattice is made by
% placing connections as close as possible to the main diagonal, without
% wrapping around, so it is NOT a ring. No connections are made on the main
% diagonal. In/Outdegree is kept approx. constant at K/N
%
% Olaf Sporns, Indiana University, 2005/2007
    """
    pass

def make_motif34lib():
    # see bct-cpp:
    pass

def makerandCIJdegreesfixed(in,out):
    """
    function [cij,flag] = makerandCIJdegreesfixed(in,out)

% input:
%    in = indegree vector
%    out = outdegree vector
%
% output:
%    cij = binary directed connectivity matrix
%    flag = indicates if the algorithm succeeded ('flag' = 1) or failed
%    ('flag' = 0).
%
% NOTE: necessary conditions include:
%
%   length(in) = length(out) = n
%   sum(in) = sum(out) = k
%   in(i), out(i) < n-1
%   in(i) + out(j) < n+2
%   in(i) + out(i) < n
%
% No connections are generated on the main diagonal
%
% Aviad Rubinstein, Indiana University 2005/2007
    """
    pass

def makerandCIJ_dir(N,K):
    """
    function [CIJ] = makerandCIJ_dir(N,K)

% inputs:
%           N = number of vertices
%           K = number of edges
% output:
%           CIJ = directed random connection matrix
%
% Generates a random directed binary connection matrix, with size (N,K) and
% no connections on the main diagonal
%
% Olaf Sporns, Indiana University, 2007/2008
    """
    pass

def makerandCIJ_und(N,K):
    """
    function [CIJ] = makerandCIJ_und(N,K)

% inputs:
%           N = number of vertices
%           K = number of edges
% output:
%           CIJ = random connection matrix, nondirected (symmetrical)
%
% This function generates a random binary CIJ matrix, with size (N,K) and
% no connections on the main diagonal
%
% Olaf Sporns, Indiana University, 2007/2008
    """
    pass

def makeringlatticeCIJ(N,K):
    """
    function [CIJ] = makeringlatticeCIJ(N,K)

% inputs:
%           N        number of vertices
%           K        number of edges
% outputs:
%           CIJ      connection matrix
%
% makes one lattice CIJ matrix, with size = N,K. The lattice is made by
% placing connections as close as possible to the main diagonal, with
% wrap-around, so it IS a ring. No connections are made on the main
% diagonal. In/Outdegree is kept approx. constant at K/N
%
% Olaf Sporns, Indiana University, 2005/2007
    """
    pass

def maketoeprand(N,K,s):
    """
    function  [CIJ] = maketoeprandCIJ(N,K,s)

% inputs:
%           N        number of vertices
%           K        number of edges
%           s        standard deviation of toeplitz
% outputs:
%           CIJ      connection matrix
%
% makes one CIJ matrix, with size = N,K, that has K connections arranged in
% a toeplitz form.
% NO RING
% no connections on main diagonal
%
% Olaf Sporns, Indiana University, 2005/2007
    """
    pass

def matching_ind(CIJ):
    """
    function [Min,Mout,Mall] = matching_ind(CIJ)

% input:
%           CIJ  = connection/adjacency matrix
% output:
%           Min  = matching index for incoming connections
%           Mout = matching index for outgoing connections
%           Mall = matching index for all connections

% Does not use self- or cross connections for comparison.
% Does not use connections that are not present in BOTH i and j.
% All output matrices are calculated for upper triangular only (symmetrical).
%
% Olaf Sporns, Indiana University, 2002/2007/2008
    """
    pass

def modularity(cmatrix):
    """
function [Ci Q]=modularity_dir(A)
%[Ci Q]=modularity_dir(A); community detection via optimization of modularity.
%
%Input: weighted directed network (adjacency or weights matrix) A.
%Output: community structure Ci; maximized modularity Q.
%
%Algorithm: Newman's spectral optimization method, generalized to directed networks.
%Reference: Leicht and Newman (2008) Phys Rev Lett.
%
%Mika Rubinov, UNSW
%
%Modification History:
%Jul 2008: Original
%Oct 2008: Positive eigenvalues are now insufficient for division (Jonathan Power, WUSTL)
%Dec 2008: Fine-tuning is now consistent with Newman's description (Jonathan Power)
%Dec 2008: Fine-tuning is now vectorized (Mika Rubinov)

function [Ci Q]=modularity_und(A)
%[Ci Q]=modularity_und(A); community detection via optimization of modularity.
%
%Input: weighted undirected network (adjacency or weights matrix) A.
%Output: community structure Ci; maximized modularity Q.
%
%   Note: use modularity_dir for directed networks
%
%Algorithm: Newman's spectral optimization method:
%References: Newman (2006) -- Phys Rev E 74:036104; PNAS 23:8577-8582.
%
%Mika Rubinov, UNSW
%
%Modification History:
%Jul 2008: Original
%Oct 2008: Positive eigenvalues are now insufficient for division (Jonathan Power, WUSTL)
%Dec 2008: Fine-tuning is now consistent with Newman's description (Jonathan Power)
%Dec 2008: Fine-tuning is now vectorized (Mika Rubinov)

    """
    pass

def module_degree_zscore(A,Ci):
    """
    function Z=module_degree_zscore(A,Ci)
%Z=module_degree_zscore(A,Ci); computes "within module degree z-score"
%
%Input: binary adjacency matrix A, community structure vector Ci.
%Output: z-score, Z.
%Output for directed graphs: "out-neighbor" z-score.
%
%Reference: Guimera R, Amaral L. Nature (2005) 433:895-900.
%
%Mika Rubinov, UNSW, 2008
    """
    pass


# in bct-cpp: utility.cpp
def number_of_links_und(cmatrix):
    pass
def number_of_links_dir(cmatrix):
    pass
def number_of_nodes(cmatrix):
    pass

def motif3funct(cmatrix):
    """
    function [f F]=motif3funct_bin(W)
    %[f F]=motif3funct_bin(G); counts functional motif occurence
    %
    %Input: binary graph G.
    %Output: binary motif count f; binary motif count per vertex F
    %
    %Reference: Sporns and Kotter, PLoS Biology 2004, 2:e369
    %
    %Mika Rubinov, UNSW, 2008

function [I Q F]=motif3funct_wei(W)
%[I Q F]=motif3funct_wei(W); weighted functional motif metrics.
%
%Input: weighted graph W (all weights [0,1]).
%Output by node: total intensity I, total coherence Q, motif count F.
%Average intensity and coherence may be obtained as I./F and Q./F.
%
%References: Onnela et al. 2005, Phys Rev E 71:065103;
%Sporns and Kotter, PLoS Biology 2004, 2:e369
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008)

    """
    pass

def motif3struct(cmatrix):
    """
    function [f F]=motif3struct_bin(A)
%[f F]=motif3struct_bin(G); counts structural motif occurence
%
%Input: binary directed graph G
%Output: binary motif count f; binary motif count per vertex F
%
%Reference: Milo et al., 2002, Science
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008)

    function [I Q F]=motif3struct_wei(W)
%[I Q F]=motif3struct_wei(W); weighted structural motif metrics.
%
%Input: weighted graph W (all weights [0,1]).
%Output by node: total intensity I, total coherence Q, motif count F.
%Average intensity and coherence may be obtained as I./F and Q./F.
%
%Reference: Onnela et al. 2005, Phys Rev E 71:065103;
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008)
    """
    pass

def participation_coef(cmatrix, Ci):
    """
    function P=participation_coef(A,Ci)
%P=participation_coef(A,Ci); computes nodal "participation coefficient".
%
%Input: (binary) adjacency matrix A, community structure vector Ci.
%Output: participation coef P.
%Output for directed graphs: "out-neighbor" participation coef.
%
%Reference: Guimera R, Amaral L. Nature (2005) 433:895-900.
%
%Mika Rubinov, UNSW, 2008
    """
    pass

# in bct-cpp
def norm_avr_shortest_path_length_wei(cmatrix, wmax):
    """
     * Computes the normalized average shortest path length for a weighted graph.
    """
    pass

def norm_avr_shortest_path_length_bin(cmatrix):
    """
    Computes the normalized average shortest path length for a binary graph.
    """
    pass

def randmio(r, iter):
    """
    function R=randmio_dir(R, ITER)
%R=randmio_dir(G,ITER); randomized graph R, with equivalent degree
%sequence to the original weighted directed graph G.
%
%Each edge is rewired (on average) ITER times. The out-strength (but not
%the in-strength) distribution is preserved for weighted graphs.
%
%Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008).

function R=randmio_und(R, ITER)
%R=randmio_und(G,ITER); randomized graph R, with equivalent degree
%sequence to the original weighted undirected graph G.
%
%Each edge is rewired (on average) ITER times. The strength distributions 
%are not preserved for weighted graphs.
%
%Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
%
%Mika Rubinov, UNSW
%
%Modification History:
%Jun 2007: Original
%Apr 2008: Edge c-d is flipped with 50% probability, allowing to explore
%          all potential rewirings (Jonathan Power, WUSTL)
    """
    pass

def randmio_connected(r, iter):
    """
    function R=randmio_dir_connected(R, ITER)
%R=randmio_dir_connected(G,ITER); randomized graph R, with equivalent degree
%sequence to the original weighted directed graph G, and with preserved
%connectedness (hence the input graph must be connected).
%
%Each edge is rewired (on average) ITER times. The out-strength (but not
%the in-strength) distribution is preserved for weighted graphs.
%
%Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
%
%Mika Rubinov, UNSW, 2007 (last modified July 2008).

function R = randmio_und_connected(R, ITER)
%R=randmiou_und_connected(G,ITER); 'latticized' graph R, with equivalent degree
%sequence to the original weighted undirected graph G, and with preserved
%connectedness (hence the input graph must be connected).
%
%Each edge is rewired (on average) ITER times. The strength distributions 
%are not preserved for weighted graphs.
%
%Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
%Latticizing algorithm: Sporns and Zwi (2004); Neuroinformatics 2:145
%
%Mika Rubinov, UNSW
%
%Modification History:
%Jun 2007: Original
%Apr 2008: Edge c-d is flipped with 50% probability, allowing to explore
%          all potential rewirings (Jonathan Power, WUSTL)

    """
    pass

def reachdist(cmatrix):
    """
    function  [R,D] = reachdist(CIJ)

% input:  
%           CIJ     connection/adjacency matrix
% output: 
%           R       reachability matrix
%           D       distance matrix

% This function yields the reachability matrix and the distance matrix
% based on the power of the adjacency matrix - this will execute a lot
% faster for matrices with low average distance between vertices.
% Another way to get the reachability matrix and the distance matrix uses 
% breadth-first search (see 'breadthdist.m').  'reachdist' seems a 
% little faster most of the time.  However, 'breadthdist' uses less memory 
% in many cases.
%
% Olaf Sporns, Indiana University, 2002/2007/2008
    """
    pass

def reorderMAT(MAT,H,cost):
    """
    function [MATreordered,MATindices,MATcost] = reorderMAT(MAT,H,cost);

% MAT       input matrix (can be anything...)
% H         number of reordering attempts
% cost      either 'line' or 'circ', for shape of lattice (linear or ring)
    """
    pass

def strenghts(cmatrix):
    """
    function [is,os,str] = strengths_dir(CIJ)

% input:  
%         CIJ  = connection/adjacency matrix
% output: 
%         is   = instrength for all vertices
%         os   = outstrength for all vertices
%         str  = strength for all vertices
%
% Computes the instrength, outstrength, and strength (indegree + outdegree)
% for a directed weighted matrix.
%
% Olaf Sporns, Indiana University, 2007/2008

function [str] = strengths_und(CIJ)

% input:  
%         CIJ  = connection/adjacency matrix
% output: 
%         str  = strength for all vertices
%
% Computes the strength for a nondirected weighted matrix.
%
% Olaf Sporns, Indiana University, 2007/2008

    """
    pass

