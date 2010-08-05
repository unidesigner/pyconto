""" Generator function for synthetic connection networks
and graph randomization algorithms 

Synthetic Connection Networks

    * makerandCIJ_dir.m. (BD networks)
      Generates a random, directed network with a specified number of nodes and links.
      Contributor: OS.
      .
    * makerandCIJ_und.m. (BU networks)
      Generates a random, undirected network with a specified number of nodes and links.
      Contributor: OS.
      .
    * makerandCIJdegreesfixed.m. (BD networks)
      Generates a random, directed network with a specified in-degree and out-degree sequence. Returns a flag, denoting whether the algorithm succeeded or failed. Also compare to network randomization algorithms (see below).
      Contributor: OS.
      .
    * makeevenCIJ.m. (BD networks)
      Generates a random, directed network with a specified number of clusters (modules), linked together by evenly distributed random connections.
      Contributor: OS.
      .
    * makefractalCIJ.m. (BD networks)
      Generates a directed network with a hierarchical (fractal) cluster organization.
      Contributor: OS.
      .
    * makelatticeCIJ.m. (BD networks)
      Generates a lattice, directed network of a specified number of nodes and links without toroidal boundary counditions (no ring-like "wrapping around").
      Contributor: OS.
      .
    * makeringlatticeCIJ.m. (BD networks)
      'makelatticeCIJ.m' with toroidal boundary conditions (ring-like "wrapping around").
      Contributor: OS.
      .
    * maketoeplitzCIJ.m. (BD networks)
      Generates a directed network with a specified number of nodes and links, and with links arranged such that their density exhibits a Gaussian drop-off with increasing distance from the main diagonal.
      Contributor: OS.

 
Graph randomization algorithms

All network metrics should be compared to metrics extracted from corresponding reference (null model) networks. The most commonly used null model is that of a random network of the same size and degree distribution as the original network.

    * randmio_dir.m; randmio_dir_connected.m. (BD, WD networks)
      An alternative randomization algorithm, that preserves in and out degree distribution, as well as out strength (but not in strength) distribution in weighted directed networks. In addition, 'randmio_dir_connected.m' ensures that the randomized network maintains connectedness -- the input network for this function must be connected, and the randomization effect may decrease.
      Reference: Maslov and Sneppen (2002). Contributor: MR.
      .
    * randmio_und.m; randmio_und_connected.m. (BD, WD networks)
      A version of 'randmio_dir.m' and 'randmio_dir_connected.m' for undirected networks. The strength distributions are not preserved for weighted networks.
      Reference: Maslov and Sneppen (2002). Contributor: MR.
      .
    * latmio_dir.m; latmio_dir_connected.m. (BD, WD networks)
      An algorithm that latticizes a weighted directed network, with preservation of in- and out-degree distributions, as well as of out-strength (but not in-strength) distribution. In addition, 'latmio_dir_connected.m' ensures that the randomized network maintains connectedness -- the input network for this function must be connected. Surrogate lattice networks may be useful, for example, for comparison of motif frequency distributions.
      References: Maslov and Sneppen (2002), Sporns and Zwi (2004). Contributor: MR.
      .
    * latmio_und.m; latmio_und_connected.m. (BD, WD networks)
      A version of 'latmio_dir.m' and 'latmio_dir_connected.m' for undirected networks. The strength distributions are not preserved for weighted networks.
      Reference: Maslov and Sneppen (2002). Contributor: MR.

"""


def latmio_dir(r, iter):
    """ Latticized graph
    
    Parameters
    ----------
    G :
    iter : scalar
        Number of rewiring steps for each edge
        
    Returns
    -------
    R : 'latticized' graph R, with equivalent degree
    sequence to the original weighted directed graph G.

    Each edge is rewired (on average) ITER times. The out-strength (but not
    the in-strength) distribution is preserved for weighted graphs.

    Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
    Latticizing algorithm: Sporns and Zwi (2004); Neuroinformatics 2:145

    Mika Rubinov, UNSW, 2007 (last modified July 2008).
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

def makerandCIJdegreesfixed(in, out):
    """ Generates a random directed binary graph with the given in-degree and out-
    degree sequences. Returns NULL if the algorithm failed to generate a graph
    satisfying the given degree sequences.
 
    function [cij,flag] = makerandCIJdegreesfixed(in,out)

    Parameters
    ----------
    in : numpy array
        indegree vector
    out : numpy array
        outdegree vector

    Returns
    -------
    cij : numpy array
        binary directed connectivity matrix
%    flag = indicates if the algorithm succeeded ('flag' = 1) or failed ('flag' = 0).

    NOTE: necessary conditions include:

    length(in) = length(out) = n
    sum(in) = sum(out) = k
    in(i), out(i) < n-1
    in(i) + out(j) < n+2
    in(i) + out(i) < n

    No connections are generated on the main diagonal

    Aviad Rubinstein, Indiana University 2005/2007
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

    Parameters
    ----------
    N : number of vertices
    K : number of edges
    
    Returns
    -------
    cmatrix : random connection matrix, nondirected (symmetrical)

    This function generates a random binary CIJ matrix, with size (N,K) and
    no connections on the main diagonal

    Olaf Sporns, Indiana University, 2007/2008
    """
    pass

def makeringlatticeCIJ(N,K):
    """
    function [CIJ] = makeringlatticeCIJ(N,K)

    Parameters
    ----------
    N : number of vertices
    K : number of edges
    
    Returns
    -------
    cmatrix : connection matrix

    makes one lattice CIJ matrix, with size = N,K. The lattice is made by
    placing connections as close as possible to the main diagonal, with
    wrap-around, so it IS a ring. No connections are made on the main
    diagonal. In/Outdegree is kept approx. constant at K/N

    Olaf Sporns, Indiana University, 2005/2007
    """
    pass

def maketoeprand(N,K,s):
    """
    function  [CIJ] = maketoeprandCIJ(N,K,s)

    Parameters
    ----------
    N : number of vertices
    K : number of edges
    s : standard deviation of toeplitz

    Returns
    -------
    cmatrix : connection matrix

    Makes one CIJ matrix, with size = N,K, that has K connections arranged in
    a toeplitz form.
    
    NO RING. No connections on main diagonal

    Olaf Sporns, Indiana University, 2005/2007
    """
    pass

def randmio(cmatrix, iter, edgetype):
    """
    
    Parameters
    ----------
    cmatrix :
    iter :
    edgetype : {'undirected', 'directed'}
        Is the INPUT graph directed?
        
    Returns
    -------
    edgetype == 'directed':

        r : NumPy array

        Randomized graph r, with equivalent degree sequence to the original
        weighted directed graph cmatrix.
        
        Each edge is rewired (on average) ITER times. The out-strength (but not
        the in-strength) distribution is preserved for weighted graphs.
        
        Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
        
        Mika Rubinov, UNSW, 2007 (last modified July 2008).
    
    edgetype == 'undirected':

        r : NumPy array
        
        Randomized graph R, with equivalent degree
        sequence to the original weighted undirected graph cmatrix.
        
        Each edge is rewired (on average) ITER times. The strength distributions 
        are not preserved for weighted graphs.
        
        Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
        
        Mika Rubinov, UNSW
        
        Modification History:
        Jun 2007: Original
        Apr 2008: Edge c-d is flipped with 50% probability, allowing to explore
                  all potential rewirings (Jonathan Power, WUSTL)

    """
    pass

def randmio_connected(cmatrix, iter, edgetype):
    """

    Parameters
    ----------
    cmatrix :
    iter :
    edgetype : {'undirected', 'directed'}
        Is the INPUT graph directed?
        
    Returns
    -------
    edgetype == 'directed':

        R : numpy array
        
        Randomized graph R, with equivalent degree
        sequence to the original weighted directed graph G, and with preserved
        connectedness (hence the input graph must be connected).
        
        Each edge is rewired (on average) ITER times. The out-strength (but not
        the in-strength) distribution is preserved for weighted graphs.
        
        Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
        
        Mika Rubinov, UNSW, 2007 (last modified July 2008).

    edgetype == 'undirected':

        R : numpy array
        
        'latticized' graph R, with equivalent degree
        sequence to the original weighted undirected graph G, and with preserved
        connectedness (hence the input graph must be connected).
        
        Each edge is rewired (on average) ITER times. The strength distributions 
        are not preserved for weighted graphs.
        
        Rewiring algorithm: Maslov and Sneppen (2002) Science 296:910
        Latticizing algorithm: Sporns and Zwi (2004); Neuroinformatics 2:145
        
        Mika Rubinov, UNSW
        
        Modification History:
        Jun 2007: Original
        Apr 2008: Edge c-d is flipped with 50% probability, allowing to explore
                  all potential rewirings (Jonathan Power, WUSTL)


    """
    pass