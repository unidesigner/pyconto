average path length
- clustering coefficient
- small world-ness
- nodal degree distribution
- local efficiency
- global efficiency
- betweenness centrality

standard formulae: Newman 2003

finding a random graph R that is equivalent to G:
what properties do you want to preserve?
usually: degree distribution
two algorithms:
    - iterative randomization algorithm in Maslov and Sneppen (2002)
    - sequential algorithm in Bayati et al. 2007
    
if computationally intractable, relax the definition of equivalence
---
graph analysis in MEG networks

the clustering coefficient, path length, small-worldness, efficiency,
cost-efficiency, assortativity, hierarchy, and synchronizability.


--- 
Test for scale-freeness:
    
(1) degree of each node was ranked from 1...N
    Nodal rank vs. degree on log-log plot
    Roughly linear rank-degree plot is indicative of a scale-free degree distribution
    
    d_i = c * y_i**(-alpha), where y_i is rank of d_i, and c and alpha are constants.
    log(d_i) = log(c) - alpha * log(y_i), yields a link of slope -alpha
    
(2) usually frequency-degree plot, but binning process has been shown to introduce artifacts (Liu et al. 2005)
    
----
exponentially truncated power law model
y_i = c*d_i**(alpha-1)*exp(-d_i/k)

http://sites.google.com/site/randomnetworkplugin/

- check hdf5 http://h5py.alfven.org/

hierarchial networks:
http://arxiv.org/abs/physics/0610051
