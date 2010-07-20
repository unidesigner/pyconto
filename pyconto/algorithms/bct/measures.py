""" The measures of the Brain Connectivity Toolbox with an unified
and simplified interface. Memory management."""

# Wrapped to Python by Stephan Gerhard, EPFL/UNIL-CHUV, 2010

# use for docstring
# - page http://sites.google.com/a/brain-connectivity-toolbox.net/bct/metrics/list-of-measures
# - what is inside the matlab function
# - what is inside the c++ function

# questions
# - what does wmax mean in normalized_path_length(const gsl_matrix* D, double wmax)  ?
# - compare with networkx measures (problem with e.g. degree)

import bct
import numpy as np

def set_safe_mode(status):
    """ Validity of cmatrix tested
    
    By default, bct-cpp checks the status of any connection matrix passed
    to a specialized function (i.e., one such as bct::clustering_coef_bu
    which is only intended to work on binary undirected matrices).
    If this status check fails, a message is printed to stderr, but the
    function still attempts to complete the calculation. To disable this behavior
    and avoid the minor computational overhead, call bct::set_safe_mode(false). 
    
    """
    bct.set_safe_mode(status)

def assortativity(cmatrix, edgetype = 'undirected', weighted = False):
    """ Assortativity coefficient. Essentially, the assortativity a correlation
    coefficient for the degrees of linked nodes. A positive assortativity coefficient
    indicates that nodes tend to link to other nodes with the same or similar degree.
    The function accepts weighted networks, but the connection weights are ignored.
    
    Parameters
    ----------
    
    cmatrix : connection matrix
   
    edgetype : {'undirected', 'directed'} 
   
   
    Returns
    -------

    r : assortativity
     
        
    Computed after Newman (2003)
    
    Note: Weights are discarded, no edges on main diagonal
    
    Olaf Sporns, Indiana University, 2007/2008
    Vassilis Tsiaras, University of Crete, 2009
    
    """
    
    if edgetype == 'undirected':
        m = bct.to_gslm(cmatrix.tolist())
        ass = bct.assortativity_und(m)
        r = bct.from_gsl(ass)
        if np.isnan(ass):
            return 0
        bct.gsl_free(m)
        bct.gsl_free(ass)
        return r
    else:
        m = bct.to_gslm(cmatrix.tolist())
        ass = bct.assortativity_dir(m)
        if np.isnan(ass):
            return 0
        r = bct.from_gsl(ass)
        bct.gsl_free(m)
        bct.gsl_free(ass)
        return r
    
def degree(cmatrix, edgetype):
    """ In an undirected graph, the degree is the number of connections for individual nodes.
    In a directed graph, the indegree (outdegree) is the number of incoming (outgoing) connections
    for individual nodes.  The degree is the sum of indegree and outdegree.Connection weights are ignored.
    
    Parameters
    ----------  
    cmatrix : connection/adjacency matrix


    Returns
    -------
    
    edgetype == 'directed':
    
        id   = indegree for all vertices
        od   = outdegree for all vertices
        deg  = degree for all vertices
    
        Computes the indegree, outdegree, and degree (indegree + outdegree) for a
        directed binary matrix.  Weights are discarded.
    
        Note: Inputs of CIJ are assumed to be on the columns of the matrix.
    
        Olaf Sporns, Indiana University, 2002/2006/2008

    edgetype == 'undirected'
  
        deg : degree for all vertices
    
        Computes the degree for a nondirected binary matrix.  Weights are
        discarded.
    
        Olaf Sporns, Indiana University, 2002/2006/2008

    """
    if edgetype == 'undirected':
        m = bct.to_gslm(cmatrix.tolist())
        deg = bct.degrees_und(m)
        rdeg = bct.from_gsl(deg)
        bct.gsl_free(m)
        bct.gsl_free(deg)
        return rdeg
    else:
        m = bct.to_gslm(cmatrix.tolist())
        deg = bct.degrees_dir(m)
        # XXX how are multiple return values handle in this case?
        rdeg = bct.from_gsl(deg)
        bct.gsl_free(m)
        bct.gsl_free(deg)
        return rdeg
        
def efficiency(cmatrix, local = False, edgetype = 'undirected', weighted = False):
    """ A measure similar to the clustering coefficient, based upon the calculation of the harmonic mean
    of neighbor-neighbor distances. For directed networks, this function works on the out-degree.
    
    A global efficiency matrix is the inverse of the distance matrix (with self-self distances set to 0).
    Calculating the global efficiency is advantageous over distance in disconnected networks:
    the efficiency between disconnected pairs of nodes is set to 0 (the inverse of infinity),
    hence enabling the calculation of network wide averages (which become meaningless on distance matrices).

    Parameters
    ----------
    cmatrix : connection/adjacency matrix
    
    edgetype : {'undirected'} 

    weighted : {False}


    Returns
    -------
    local == True:
    
        Eglob : outputs the inverse distance matrix: the mean of this
                matrix (excluding main diagonal) is equivalent to the global efficiency.
    
    local == False:
    
        Eloc : outputs individual nodal local efficiency.
               For directed networks, local efficiency works with the out-degree. 
               
               
    Reference: Latora and Marchiori, 2001, Phys Rev Lett 87:198701.
    
    Algebraic shortest path algorithm.
    
    Mika Rubinov, UNSW, 2008 (last modified September 2008).
    """
    if edgetype == 'undirected' and weighted == False:
        if local:
            m = bct.to_gslm(cmatrix.tolist())
            eloc = bct.efficiency_local(m)
            elocnp = bct.from_gsl(eloc)
            bct.gsl_free(m)
            bct.gsl_free(eloc)
            return np.asarray(elocnp)
        else:
            m = bct.to_gslm(cmatrix.tolist())
            eloc = bct.efficiency_global(m)
            elocnp = bct.from_gsl(eloc)
            bct.gsl_free(m)
            bct.gsl_free(eloc)
            return np.asarray(elocnp)

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

def density(cmatrix, edgetype):
    """ Density is the proportion of the number of present connections in the network,
    to the maximum possible number of connections.  Connection weights are ignored.
    
    Parameters
    ----------
    
    cmatrix : connection/adjacency matrix
    
    Returns
    -------
    
    edgetype == 'undirected':

        kden : connection density, number of connections present out of all possible (N^2-N)

        Note: Assumes that cmatrix is undirected and that there are no self-connections.
        Note: Function always returns average binary density, regardless of weights.
        
        Olaf Sporns, Indiana University, 2002/2007/2008

        Modification history:
        2009-10: K fixed to sum over one half of cmatrix [Tony Herdman, SFU]

    edgetype == 'directed'

        kden : connection density, number of connections present out of all possible (N^2-N)
    
        Note: Assumes that cmatrix is directed and that there are no self-connections.
        Note: Function always returns average binary density, regardless of weights.
    
        Olaf Sporns, Indiana University, 2002/2007/2008

    """
    if edgetype == 'undirected':
        m = bct.to_gslm(cmatrix.tolist())
        val =  bct.density_und(m)
        bct.gsl_free(m)
        return val

    elif edgetype == 'directed':
        m = bct.to_gslm(cmatrix.tolist())
        val = bct.density_dir(m)
        bct.gsl_free(m)
        return val

def distance(cmatrix, weighted):
    """ Computes the distance matrix for a weighted or binary graph.
    
    Distance matrix for weighted networks. The input matrix must be a mapping from weight to distance.
    For instance, in a weighted correlation network, higher correlations are more naturally interpreted
    as shorter distances. Consequently, in this case, the input matrix should be some inverse of the connectivity matrix.
    
    Distance matrix for binary graphs. If any two nodes u and v are disconnected,
    the value of the entry (u,v) is set to infinity. The value of self-self distances (u,u)
    is set to 0. Consequently, two nodes are connected if they have a finite non-zero distance.
    
    Parameters
    ----------
    
    cmatrix : connection/adjacency matrix
    
    weighted : {True, False}
               Apply the distance computation for weighted or unweighted (binary) matrices
    
    Returns
    -------
    
    weighted == True:
        
        D : distance matrix for a weighted directed graph -
            the mean distance is the characteristic path length.
    
        The input matrix must be a mapping from weight to distance (eg. higher
        correlations may be interpreted as short distances via an inverse mapping).
    
        Dijkstra's Algorithm.
    
        Mika Rubinov, UNSW
    
        Modification history
        2007: original
        2009-08-04: min() function vectorized

    weighted == False:

        D : distance matrix for binary undirected graph G
            Mean distance (excluding the main diagonal) equals the characteristic path length
    
        Algebraic shortest path algorithm.
    
        Mika Rubinov, UNSW, 2007 (last modified September 2008).

    """
    if weighted:
        m = bct.to_gslm(cmatrix.tolist())
        dist = bct.distance_wei(m)
        distnp = bct.from_gsl(dist)
        bct.gsl_free(m)
        bct.gsl_free(dist)
        return np.asarray(distnp)
    else:
        m = bct.to_gslm(cmatrix.tolist())
        dist = bct.distance_bin(m)
        distnp = bct.from_gsl(dist)
        bct.gsl_free(m)
        bct.gsl_free(dist)
        return np.asarray(distnp)

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
    """ Computes the range for each edge (i.e., the shortest path length between the
    nodes it connects after the edge has been removed from the graph).
    Shorcuts are links which significantly reduce the characteristic path length
    This function detects shortcuts, hence being a variant of edge betweenness centrality.
    
    Parameters
    ----------
    cmatrix : connection/adjacency matrix
    
    
    Returns
    -------

    Erange : range for each edge, i.e. the length of the 
             shortest path from i to j for edge c(i,j) AFTER the edge 
             has been removed from the graph.

%           eta      average range for entire graph.
%           Eshort   entries are ones for edges that are shortcuts.
%           fs       fraction of shortcuts in the graph.

    Follows the treatment of 'shortcuts' by Duncan Watts (1999)
    
    Olaf Sporns, Indiana University, 2002/2007/2008

    """
    m = bct.to_gslm(cmatrix.tolist())
    era = bct.erange(m)
    eranp = bct.from_gsl(era)
    bct.gsl_free(m)
    bct.gsl_free(era)
    return np.asarray(eranp)

def jdegree(cmatrix, edgetype = 'directed'):
    """ Joint degree distribution. This function returns a matrix, in which the value of each 
    element (u,v) corresponds to the number of nodes that have u outgoing connections and v incoming connections.
    Connection weights are ignored.

    Parameters
    ----------
    cmatrix : connection/adjacency matrix
    
    Returns
    -------

    J : joint degree distribution matrix (shifted by one)
    
    Note: This function only makes sense for directed matrices.  Weights are
    discarded.

    Olaf Sporns, Indiana University, 2002/2006/2008

    """
    m = bct.to_gslm(cmatrix.tolist())
    jdeg = bct.jdegree(m)
    jdegnp = bct.from_gsl(jdeg)
    bct.gsl_free(m)
    bct.gsl_free(jdeg)
    return np.asarray(jdegnp)

def jdegree_bl(cmatrix):
    """ Given a joint degree distribution matrix, returns the number of nodes with
    in-degree = out-degree.
    """
    m = bct.to_gslm(cmatrix.tolist())
    val = bct.jdegree_bl(m)
    bct.gsl_free(m)
    return val

def jdegree_id(cmatrix):
    """ Given a joint degree distribution matrix, returns the number of nodes with
    in-degree > out-degree.
    """
    m = bct.to_gslm(cmatrix.tolist())
    val = bct.jdegree_id(m)
    bct.gsl_free(m)
    return val

def jdegree_od(cmatrix):
    """ Given a joint degree distribution matrix, returns the number of nodes with
    out-degree > in-degree.
    """
    m = bct.to_gslm(cmatrix.tolist())
    val = bct.jdegree_od(m)
    bct.gsl_free(m)
    return val

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

def matching_ind(cmatrix):
    """ Matching index. For any two nodes u and v, the matching index computes
    the amount of overlap in the connection patterns of u and v. Self-connections
    and cross-connections between u and v are ignored.  For undirected networks,
    all outputs of this function are identical.  The matching index is a symmetric
    quantity, similar to a correlation or a dot product, the function returns only
    the upper triangle of the matching matrix.
    
    Parameters
    ----------

    cmatrix : connection/adjacency matrix

    Returns
    -------
    
    Mall : matching index for all connections    

    Does not use self- or cross connections for comparison.
    Does not use connections that are not present in BOTH i and j.
    All output matrices are calculated for upper triangular only (symmetrical).

    Reference: Hilgetag et al. (2002).

    Olaf Sporns, Indiana University, 2002/2007/2008
    
    """
    m = bct.to_gslm(cmatrix.tolist())
    mi = bct.matching_ind(m)
    minp = bct.from_gsl(mi)
    bct.gsl_free(m)
    bct.gsl_free(mi)
    return np.asarray(minp)

def matching_ind_in(cmatrix):
    """ Computes matching index for incoming connections.
    
    Parameters
    ----------

    cmatrix : connection/adjacency matrix
    
    Returns
    -------
    Min : matching index for incoming connections
    
    """
    m = bct.to_gslm(cmatrix.tolist())
    mi = bct.matching_ind_in(m)
    minp = bct.from_gsl(mi)
    bct.gsl_free(m)
    bct.gsl_free(mi)
    return np.asarray(minp)
        
def matching_ind_out(cmatrix):
    """ Computes matching index for outgoing connections.
    
    Parameters
    ----------

    cmatrix : connection/adjacency matrix
    
    Returns
    -------
    Mout : matching index for outgoing connections
    
    """
    m = bct.to_gslm(cmatrix.tolist())
    mi = bct.matching_ind_out(m)
    minp = bct.from_gsl(mi)
    bct.gsl_free(m)
    bct.gsl_free(mi)
    return np.asarray(minp)

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

def number_of_edges_und(cmatrix):
    """ Returns the number of edges in an undirected graph.
    
    No checking of the edgetype of the cmatrix.
    
    Parameters
    ----------
    cmatrix : connection/adjacency matrix
    
    """
    m = bct.to_gslm(cmatrix.tolist())
    n = bct.number_of_edges_und(m)
    bct.gsl_free(m)
    return n

def number_of_edges_dir(cmatrix):
    """ Returns the number of edges in a directed graph.
    
    No checking of the edgetype of the cmatrix.
    
    Parameters
    ----------
    cmatrix : connection/adjacency matrix
    """
    m = bct.to_gslm(cmatrix.tolist())
    n = bct.number_of_edges_dir(m)
    bct.gsl_free(m)
    return n

def number_of_nodes(cmatrix):
    """ Returns the number of nodes in a graph, assuming the given connection matrix is square.
    
    Parameters
    ----------
    cmatrix : connection/adjacency matrix
    """
    m = bct.to_gslm(cmatrix.tolist())
    n = bct.number_of_nodes(m)
    bct.gsl_free(m)
    return n

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

def normalized_path_length(cmatrix, wmax):
    """ Given a distance matrix, computes the normalized shortest path length.

    Parameters
    ----------
    cmatrix : connection/adjacency matrix
    
    wmax : double
           ???
    
    Results
    -------
    
    n : normalized shortest path length
    
    """

    m = bct.to_gslm(cmatrix.tolist())
    n = bct.normalized_path_length(m)
    bct.gsl_free(m)
    return n
        
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

def strengths(cmatrix, edgetype):
    """ Computes strength for an undirected or directed graph.
    
    Strength is the sum of all connection weights for individual nodes.

    In a directed graph, instrength (outstrength) is the sum of incoming
    (outgoing) connection weights for individual nodes. The strength is the
    sum of instrength and outstrength.
    

    Parameters
    ----------

    cmatrix : connection/adjacency matrix
    
    
    Returns
    -------
    
    edgetype == 'undirected'
    
        str : strength for all vertices
    
    edgetype == 'directed
    
        str : strength for all vertices (indegree + outdegree)

%         is   = instrength for all vertices
%         os   = outstrength for all vertices

    Reference: Barrat et al. (2004). Contributor: OS.

    Olaf Sporns, Indiana University, 2007/2008

    """
    if edgetype == 'undirected':
        m = bct.to_gslm(cmatrix.tolist())
        str = bct.strengths_und(m)
        strnp = bct.from_gsl(str)
        bct.gsl_free(m)
        bct.gsl_free(str)
        return np.asarray(strnp)
    else:
        m = bct.to_gslm(cmatrix.tolist())
        str = bct.strengths_dir(m)
        strnp = bct.from_gsl(str)
        bct.gsl_free(m)
        bct.gsl_free(str)
        return np.asarray(strnp)
