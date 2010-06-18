Typology of Networks
--------------------

* CNetwork : Connectome Network
* SNetwork : Structural Network
* FNetwork : Functional Network
* SFNetwork : Structure-Function Network
* CNetwork : Causal Network
* HNetwork : Hierarchical Network
* PNetwork : Probabilistc Network
* TNetwork : Temporal Network, point process vs. continous time
* MMNetwork : Multi-Modal Network, nodes/edges can have different roles
* FNetwork : Factor Graph Network
* PCNetwork : A Peter Cariani Network
* AONetwork : Alpha-Omega Network, Brain-Body-Environment-History - All in one pack
* PRNetwork : Paul Rogister Network, distance=energy/time networks. 
* Dense Networks : All edges have at least a value; 
* Sparse Networks : Only a few edges have a value
* Single part network vs. bipartite network
* EMNetwork : Erich Minch Network
* Degree correlations: Assortative network, uncorrelated network, disassortative network
* An edge that excites or inhibits another edge (instead of a node)
seen on: http://cneuro.rmki.kfki.hu/sites/default/files/nnw6.pdf
* Probabilistic causal networks, such as Boolean, Bayesian, and probabilistic
Gaussian networks, include edges with direction, and therefore can represent
causal relationships among genes when causality is known. The inclusion of this
information can lead to improved predictions of response to various perturbation
events [19,20]. Recently, significant research interest has shifted to the use of
Bayesian networks to study causal interaction networks of biological systems based
on gene expression data from time series and gene knockout experiments, protein–protein
interaction data derived from predicted genomics features, and other direct experimental interaction data [3,21].
http://www.ploscompbiol.org/article/info%3Adoi%2F10.1371%2Fjournal.pcbi.0030069
* dynamic Bayesian networks [31], which explicitly allow for a temporal representation of how nodes in the network interact with one another. Rabiner LR (1989) A tutorial on Hidden Markov Models and selected applications in speech recognition. Proceedings of the 77th Meeting of the Institute of Electrical and Electronics Engineers, 15–18 May 1989; San Diego, California, United States. IEEE 77: 257–286.
* provenance ontology that might have some insight for modeling dynamic networks' nodes and edges http://wiki.knoesis.org/index.php/Provenir_Ontology


Class Ideas
-----------
AttributeClass


* NetworkXExtractor
* AttributeChoose
* LevelSelector (extract one Level)



Ideas
-----
* The notion of "distance"
* Allow for associative access of nodes, i.e. Graph.node[0] or Graph.node["zero"] if
"zero" is e.g. an attribute of node with index 0. For example, add an associative
mapping function (saw something similar in Boost.BGL and DiPy)
* Employ the Enthought Traits Framework
* Do you have a node or a edge-centric view of graphs?
* References to python function, e.g. include def functions as attributes with parameter values!
