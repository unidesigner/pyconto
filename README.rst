Python Connectome Toolbox
=========================

Why yet another network / graph toolbox?
----------------------------------------

There are already quite a number of very efficient and well-designed libraries and packages
available that implement different graph representations, algorithms and layouting schemes.

See the recent discussion about a future `Python Graph API <http://wiki.python.org/moin/PythonGraphApi>`_

Existing Graph Libraries (under active development)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
* `NetworkX <http://networkx.lanl.gov/>`_
* `Python-iGraph <http://www.cs.rhul.ac.uk/home/tamas/development/igraph/tutorial/index.html>`_
* `Graph in Sage <http://www-sop.inria.fr/members/Nathann.Cohen/tut/Graphs/>`_
* `Python-Graph <http://code.google.com/p/python-graph/>`_
* `BOOST <http://boost.org/libs/graph/doc/python.html>`_
* and many others

But: The Idea
-------------
Harnessing the power of `NetworkX`, `nitime` and `pyhdf` among others to provide an
efficient framework for connectomics, investigations in structure and function
of complex systems in the domain of neuroscience.

Exposition of a class hierarchy including static and dynamic networks based on Traits,
abstracting from actual implementation of the graph data structures (replacable depending
on ones needs).

Creation of NetworkAnalyzerObjects (see `nitime`) for a pipelined network analysis approach with maximum
flexibility for different types of networks, different algorithms for similar problems,
implementations of algorithms in different languages, caching mechanism to store computationally
intensive network measures on large graphs. Creation of easy-to-read network analysis pipelines
is wanted.

Providing a general playground for exploration of network dynamics by combining graph and time series
data structures, thereby allowing to create novel algorithm and statistical procedures to quantify and understand
structure and function relationships. In macroscale Connectomics in particular, this might be helpful in combining
structural (e.g. Diffusion MRI) and functional (e.g. fMRI, EEG, MEG) data. In microscale Connectomics, for
example in combining neural networks and their spiking activity (or any other quantifiable change over time,
such as Voltage-Sensitive Dye Imaging).


Nonlinear Dynamics of Networks
------------------------------
http://www.cscamm.umd.edu/programs/ntd10/

Date & Location: 5-9 April, 2010, in College Park, MD

Organizers: Michelle Girvan (UMD), Ed Ott (UMD), Raj Roy (UMD) and Eitan Tadmor (UMD)

Description: The interconnection of many dynamical units to form a complex system can lead
to unexpected collective behavior. This dynamics depends upon both the individual characteristics
of the participating units, as well as the topological character and properties of the network of
their connections. This workshop will focus on gaining understanding of general principles and
techniques of analysis that will be of broad use in the many applications where networked system
dynamics is a significant issue. Another aim of the workshop will be to highlight particularly
important examples of applications where the issue of network dynamics arises.

Understanding the dynamics of networked systems is becoming an increasingly important and essential
component in many areas of science and technology. Examples include social networks, communication
and computer networks, gene networks, networks of neurons, etc. Dynamics on such networks include
such problems as synchronization of temporal behavior of units composing a network, robustness of
function to network damage (either intended or unintended), etc. The dynamics of networks themselves
(i.e., change of network topological structure with time) is also an essential issue in many cases.
Examples of issues in this area include adaptive evolution of network topology, formation and growth
of networks, etc.

It is intended that all of the above, as well as related issues, will be open for discussion at this
workshop. The two overarching goals of the workshop will be

* To contribute to the understanding of common, basic principles of network dynamics, and
* To uncover useful general analysis techniques for the study of these systems

