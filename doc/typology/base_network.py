Base network
============

Basic properties:

* Binary / Weighted / Valued
* Polarity (uni-polar, bi-polar, multi-polar?): Directed, Undirected
* Signed / Non-Signed
* Features on the node/edges
* Loops / No-Loops or Cycles, defines whether it is tree


Types of attributes
-------------------
*Attributes to be used for node and/or edges in an attribute network*
*Need for a good attribute selection mechanism, and checking mechanism*

* probability density functions
* basic Python types: string, tuples, list, numpy number, date-time
* simple types, simple bounded types
* complex types, i.e. classes by themselves, allowing to store rich data
e.g. files, probability density functions, units


Network Attributes
------------------
*What attributes exists is dependent of the class of network!*


Network Methods
---------------
*Derived from NetworkX API*
* Freeze graph so that no node/edge addition/removal possible (why useful?)

Graph Implementation in Memory
-------------------------------
*Depending for what purpose, you can choose*
*Depending on the requirements for data-structure richness and computation-speed
for different algorithms, sensible memory storage strategies have to be choosen*

* (Sparse) Connectivity Matrix
* NetworkX Graph


Persistent Graph data storage
------------------------------
* See ML discussion (SQLLite storage, BigGraph)