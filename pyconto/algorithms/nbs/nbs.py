import numpy as np
import scipy.stats as stats
import networkx as netwx

def compute_nbs(X, Y, THRESH, K = 1000, TAIL = 'both'):
    """
    
    function [PVAL,ADJ,NULL]=nbs(X,Y,THRESH,K,TAIL)
    
    Computes the network-based statistic (NBS) as described in [1]. 

    PVAL = NBS(X,Y,THRESH) performs the NBS for populations X and Y for a
    T-statistic threshold of THRESH. The third dimension of X and Y 
    references a particular member of the populations. The first two 
    dimensions reference the connectivity value of a particular edge 
    comprising the connectivity matrix. For example, X(i,j,k) stores the 
    connectivity value corresponding to the edge between i and j for the
    kth memeber of the population. PVAL is a vector of corrected p-values 
    for each component identified. If at least one of the p-values is 
    less than 0.05, then the omnibus null hypothesis can be rejected at 
    5% significance. The null hypothesis is that the value of 
    connectivity at each edge comes from distributions of equal mean 
    between the two populations.
    
    [PVAL,ADJ] = NBS(X,Y,THRESH) also returns an adjacency matrix 
    identifying the edges comprising each component. Edges corresponding 
    to the first p-value stored in the vector PVAL are assigned the value
    1 in the adjacency matrix ADJ, edges corresponding to the second 
    p-value are assigned the value 2, etc. 

    [PVAL,ADJ,NULL] = NBS(X,Y,THRESH) also returns a vector of K samples 
    from the the null distribution of maximal component size. 
 
    [PVAL,ADJ] = NBS(X,Y,THRESH,K) enables specification of the number of
    permutations to be generated to estimate the empirical null
    distribution of maximal component size. Default: K=1000. 

    [PVAL,ADJ] = NBS(X,Y,THRESH,K,TAIL) enables specification of the type
    of alternative hypothesis to test. If TAIL:
    'equal' - alternative hypothesis is means are not equal (default)
    'left'  - mean of population X < mean of population Y
    'right' - mean of population X > mean of population Y 

    This function is dependent on components.m, which is available as
    part of Matlab BGL: 
    http://www.stanford.edu/~dgleich/programs/matlab_bgl/

    ALGORITHM DESCRIPTION 
    The NBS is a nonparametric statistical test used to isolate the 
    components of an N x N undirected connectivity matrix that differ 
    significantly between two distinct populations. Each element of the 
    connectivity matrix stores a connectivity value and each member of 
    the two populations possesses a distinct connectivity matrix. A 
    component of a connectivity matrix is defined as a set of 
    interconnected edges. 

    The NBS is essentially a procedure to control the family-wise error 
    rate, in the weak sense, when the null hypothesis is tested 
    independently at each of the N(N-1)/2 edges comprising the 
    connectivity matrix. The NBS can provide greater statistical power 
    than conventional procedures for controlling the family-wise error 
    rate, such as the false discovery rate, if the set of edges at which
    the null hypothesis is rejected constitues a large component or
    components.
    The NBS comprises fours steps:
    1. Perfrom a two-sample T-test at each edge indepedently to test the
       hypothesis that the value of connectivity between the two
       populations come from distributions with equal means. 
    2. Threshold the T-statistic available at each edge to form a set of
       suprathreshold edges. 
    3. Identify any components in the adjacency matrix defined by the set
       of suprathreshold edges. These are referred to as observed 
       components. Compute the size of each observed component 
       identified; that is, the number of edges it comprises. 
    4. Repeat K times steps 1-3, each time randomly permuting members of
       the two populations and storing the size of the largest component 
       identified for each permuation. This yields an empirical estimate
       of the null distribution of maximal component size. A corrected 
       p-value for each observed component is then calculated using this
       null distribution.

    [1] Zalesky A, Fornito A, Bullmore ET (2010) Network-based statistic:
        Identifying differences in brain networks. NeuroImage.
        10.1016/j.neuroimage.2010.06.041

    Written by: Andrew Zalesky, azalesky@unimelb.edu.au
    Wrapped in Python: Stephan Gerhard, connectome@unidesign.ch

    """

    # check input matrices
    Ix,Jx,nx = X.shape
    Iy,Jy,ny = Y.shape
    
    assert Ix == Iy
    assert Jx == Jy
    assert Ix == Jx
    assert Iy == Jy
    
    # number of nodes
    N = Ix

    # Only consider elements above upper diagonal due to symmetry
    ind_mask = ( np.triu(np.ones( (N,N) ),1) == 1 )
    ind_i, ind_j = np.nonzero( np.triu(np.ones( (N,N) ),1) )
    
    # Number of edges
    M = N * (N - 1) / 2
    
    # Look up table
    ind2ij = np.zeros( (M,2) , dtype = np.int16)
    ind2ij[:,0] = ind_i
    ind2ij[:,1] = ind_j
    
    # Vectorize connectivity matrices
    # Not necessary, but may speed up indexing
    # Uses more memory since cmat temporarily replicates X
    cmat = np.zeros( (M, nx) )
    pmat = np.zeros( (M, ny) )
    for i in range(nx):
        cmat[:,i] = X[ind2ij[:,0], ind2ij[:,1],i].ravel()
    for i in range(ny):
        pmat[:,i] = Y[ind2ij[:,0], ind2ij[:,1],i].ravel()
    
    # Perform T-test at each edge
    t_stat = np.zeros( M )
    for i in range(M):
        # compute ttest2
        # assume independent random samples
        t, prob = stats.ttest_ind(cmat[i,:], pmat[i,:], axis=0)
    
        t_stat[i] = t
        #[a,p_val,c,tmp]=ttest2(cmat(i,:),pmat(i,:)); 
        #t_stat(i)=tmp.tstat;

    
    if TAIL == 'both':
        t_stat = np.abs( t_stat )
    elif TAIL == 'left':
        t_stat = -t_stat
    elif TAIL == 'right':
        pass
    else:
        raise('Tail option not recognized')
 
    # Threshold   
    ind_t = np.nonzero( t_stat > THRESH )
    
    # Suprathreshold adjacency matrix
    ADJ = np.zeros( (N,N) )
    reledg = ind2ij[ind_t[0]] # relevant edges
    ADJ[ reledg[:,0], reledg[:,1] ] = 1 # this yields a binary matrix, selecting the edges that are above threshold
    ADJ = ADJ + ADJ.T

    # Find network components
    G = netwx.from_numpy_matrix(ADJ)
    comp_list = netwx.strongly_connected_component_subgraphs(G)
    
    # Return strongly connected components as subgraphs.
    # The list is ordered from largest connected component to smallest. For undirected graphs only.
    
    # number of components with 
    # array representing the number of edges for each component
    nr_edges_per_component = np.zeros( len(comp_list) )
    for idx, componentG in enumerate(comp_list):
        nr_edges_per_component[idx] = componentG.number_of_edges()
        
    print "nr_edges_per_component: ", nr_edges_per_component
    
    # more then one node (= at least one edge)
    nr_edges_per_component_bigenough = nr_edges_per_component[nr_edges_per_component>0]
    
    # renaming
    sz_links = nr_edges_per_component_bigenough
    
    print "nr_edges_per_component_bigenough: ", nr_edges_per_component_bigenough
    
    if len(nr_edges_per_component_bigenough) > 0:
        max_sz = np.max(nr_edges_per_component_bigenough)
    else:
        max_sz = 0

    print "number of edges of the biggest component: ", max_sz        

    if False:
        # additionally, store all the components in the matrix with the value of their number of edges
        all_components = np.zeros( (N,N) )
        for idx, componentG in enumerate(comp_list):
            
            tmp_max = netwx.to_numpy_matrix( componentG , nodelist = range(N) )
            # set nonzero to number of edges
            tmp_max[tmp_max!=0.0] = componentG.number_of_edges()
            all_components[:,:] = all_components[:,:] + tmp_max
    
    print "Max component size is: %s" % max_sz
        
    # Empirically estimate null distribution of maximum component size by
    # generating K independent permutations.
    print "=====================================================" 
    print "Estimating null distribution with permutation testing"
    print "====================================================="
    
    hit=0.0
    NULL = np.zeros( (K, 1) )
    for k in range(K):
        # Randomize
        
        # stack matrices for permutation
        d = np.hstack( (cmat, pmat) )
        indperm = np.random.permutation( nx+ny )
        d = d[:, indperm]
         
        #################
        
        # Perform T-test at each edge
        t_stat_perm = np.zeros( M )
        for i in range(M):
            # compute ttest2
            # assume independent random samples
            t, prob = stats.ttest_ind(d[i,:nx], d[i,nx:nx+ny], axis=0) # [z1,z2,z3,tmp]=ttest2(d(i,1:nx),d(i,nx+1:nx+ny));
        
            t_stat_perm[i] = t
            #[a,p_val,c,tmp]=ttest2(cmat(i,:),pmat(i,:)); 
            #t_stat(i)=tmp.tstat;
        
        if TAIL == 'both':
            t_stat_perm = np.abs( t_stat_perm )
        elif TAIL == 'left':
            t_stat_perm = -t_stat
        elif TAIL == 'right':
            pass
        else:
            raise('Tail option not recognized')
     
        # Threshold   
        ind_t = np.nonzero( t_stat_perm > THRESH )
        
        # Suprathreshold adjacency matrix
        adj_perm = np.zeros( (N,N) )
        reledg = ind2ij[ind_t[0]] # relevant edges
        adj_perm[ reledg[:,0], reledg[:,1] ] = 1 # this yields a binary matrix, selecting the edges that are above threshold
        adj_perm = adj_perm + adj_perm.T
    
        # Find network components
        G = netwx.from_numpy_matrix(adj_perm)
        comp_list = netwx.strongly_connected_component_subgraphs(G)
        
        # Return strongly connected components as subgraphs.
        # The list is ordered from largest connected component to smallest. For undirected graphs only.
        
        # number of components with 
        # array representing the number of edges for each component
        nr_edges_per_component = np.zeros( len(comp_list) )
        for idx, componentG in enumerate(comp_list):
            nr_edges_per_component[idx] = componentG.number_of_edges()
            
        print "nr_edges_per_component: ", nr_edges_per_component
        
        # more then one node (= at least one edge)
        nr_edges_per_component_bigenough = nr_edges_per_component[nr_edges_per_component>0]
        
        # renaming
        sz_links_perm = nr_edges_per_component_bigenough
        
        print "nr_edges_per_component_bigenough: ", nr_edges_per_component_bigenough
        
        if len(nr_edges_per_component_bigenough) > 0:
            sz_links_perm_max = np.max(nr_edges_per_component_bigenough)
        else:
            sz_links_perm_max = 0
    
        NULL[k] = sz_links_perm_max
        print "number of edges of the biggest component for this permutation: ", sz_links_perm_max        

        # if the component size of this random permutation is bigger than
        # the component size of the group difference computed above, this is a hit
        if NULL[k] >= max_sz:
            hit = hit + 1
            
        print "Perm %d of %d. Perm max is: %d. Observed max is: %d. P-val estimate is: %0.3f" % ((k+1),K,NULL[k],max_sz,hit/(k+1))


    # Calculate p-values for each component
    # ??? why like that?
    PVAL = np.zeros( len(sz_links) )
    for i in range( len(sz_links) ):
        PVAL[i] = len( NULL[NULL >= sz_links[i]] ) * 1.0 / K
        
    
    return (PVAL, ADJ, NULL)
