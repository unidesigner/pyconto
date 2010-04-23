""" Dynamical network

A dynamical network constitutes the foundation for the study of the dynamics of
the processes that take place on networks.

The framework is kept as abstract as possible to allow their application in
diverse fields on different scales and with different types of entities.

It is to be expected that the availability of data will be a main driver in
better understanding these dynamic phenomena.

Examples of networks with dynamics include:
- reaction kinetics on metabolic networks
- flow of information on computer networks
- spread of viruses and ideas on social networks
- electrophysiological signal propagation on neural networks
- society itself with their interacting individuals
- in biochemical networks, the nodes in the network represent molecular species and the edges represent the interactions between them

What is interacting?
What is the nature of that interaction?
On what time scale does the interaction take place?

--- another set
- cell: metabolic network: node: substrates and enzymes, edges: chemical interactions
- society: node: individuals or organization, edges: social interaction
- business world: node: companies, edges: diverse trade relationships
- www: node: pages, edges: hyperlinks

---

node: a point in a high dimensional state space with N dimensions describing its properties

edge: what is an edge?
- e.g. "connects to other state (node) property" ?
- transitions

clock: t_start, delta_t (sampling freq), t_end

- a set of nodes is synchronized by the discrete clock and related to timeseries of ts_setofnoded1

- add measurement unit support
- how to ring time into nodes?
- properties?
- doit, doit right, do it efficiently
- associated in time or space

The simplest type of dynamic model, which does not require specific rate constants,
is perhaps the Boolean logic network, in which quantities can have only two discrete values:
'present' and 'absent'. Logical operations (for example, 'protein A AND protein B are present')
coupled with explicit time delays (for example, 'protein C is present later') can explain
phenomena such as gene activation patterns.
http://www.nature.com/nrn/journal/v11/n4/full/nrn2807.html
Other parameters are slightly more abstract, such as those that represent an observed modulatory effect on a reaction.

Bender-deMoll S, McFarland DA (2006) The art and science of dynamic
network visualization. JJ Soc Struct 7: 2.


"""