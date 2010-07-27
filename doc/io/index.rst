
Network IO from disk-stored files
---------------------------------
- Reader/Writers, from NetworkX
- Skip header lines for text files

adjacency matrix, pairs file, GraphML

Should PyConTo handle very large networks?
Yes. (This has implications on the data structures and algorithms to use.)


Database backend?

Interfaces to other tools, Packages
-----------------------------------

NetworkInterfacer
- e.g. can handle references of objects to other applications, packages
neuron simulator (PyNN) objects, protein-protein (biological) networks,
NetworkX graph object

Interacing with Connectome File Format, e.g. export functionality

using object adaptation (through the adapt() function, as described in PEP 246 and as
used in PyProtocols) to allow access to graphs through various perspectives (such as
adjacency maps, incidence maps, adjacency arrays, edge lists...). This would also allow
graphs implementations with completely different interfaces to be adapted to a standard
interface, and thus be used by generic graph algorithms. 

File Formats
------------
* libsea http://www.caida.org/tools/visualization/libsea/