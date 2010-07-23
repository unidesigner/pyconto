from numpy import dot
from scipy import sparse
from grouping import Grouping,PointPatch
import random
import os
import numpy
from scipy.misc import comb as choose


######################################################################
#
# This module contains classes and methods for creating and
# manipulating networks.
#
######################################################################
    
class Network(object):
    """The base network class from which the 
       particular types of networks are derived."""

    def __init__(self, adjacency_matrix):
        """The basic constructor creates a network from an 
           adjacency matrix (assumed to be a sparse matrix)."""
           
        self.adjacency_matrix = adjacency_matrix
        self.number_of_nodes = self.adjacency_matrix.shape[0]
        
        # Initialize for later
        self.modularity_matrix = None
        self.modularity_matrix_numpy = None
        self.adjacency_matrix_numpy = None        
        self.adjacency_matrix_csr = None

    def __str__(self):
        """Returns the adjacency matrix as a string."""
        return str(self.adjacency_matrix.todense())
            
    def get_modularity_matrix(self):

        if self.modularity_matrix == None:    
            self.modularity_matrix = self.calculate_modularity_matrix()
            
        return self.modularity_matrix

    def get_modularity_matrix_numpy(self):
        if self.modularity_matrix_numpy == None:
            self.modularity_matrix_numpy = numpy.array(self.get_modularity_matrix())

        return self.modularity_matrix_numpy

    def get_adjacency_matrix_numpy(self):
        if self.adjacency_matrix_numpy == None:
            self.adjacency_matrix_numpy = self.adjacency_matrix.toarray() 
        return self.adjacency_matrix_numpy

    def get_adjacency_matrix_sparse(self):
        if self.adjacency_matrix_csr == None:
            self.adjacency_matrix_csr = self.adjacency_matrix.tocsr()
        return self.adjacency_matrix_csr
        
    def updated_adjacency_matrix(self):
        self.adjacency_matrix_numpy = None
        self.adjacency_matrix_sparse = None
        self.modularity_matrix_numpy = None
        self.modularity_matrix = None
         
class UndirectedNetwork(Network):
    """UndirectedNetwork implements a simple undirected network.
       Our other network classes extend this class."""
    
    def __init__(self, number_of_nodes, adjacency_matrix = None):
        if adjacency_matrix == None:
            Network.__init__(self, sparse.lil_matrix((number_of_nodes, number_of_nodes)))
        else:
            Network.__init__(self, adjacency_matrix)
            
        self.number_of_edges = self.adjacency_matrix.sum()/2
        
        # Calculate the degree distribution
        self.degrees = self.adjacency_matrix.sum(axis=1)
        
    def add_edge(self, i, j, weight=1):
        """Adds an edge to the network."""           
        self.update_edge(i,j,weight+self.adjacency_matrix[i,j])    

    def remove_edge(self, i, j):
        """Removes an edge in the network.  If this edge does not
           exist, this method has no effect."""
           
        if self.adjacency_matrix[i,j] > 0:
            self.update_edge(i,j,0)
               
        
    def update_edge(self, i, j, weight):
        """Set weight of edge between i and j."""
        A = self.adjacency_matrix
        old_weight = A[i,j]
        A[i,j] = weight
        A[j,i] = weight
        self.number_of_edges = self.number_of_edges - old_weight + weight
        self.degrees[j] = self.degrees[j] - old_weight + weight
        self.degrees[i] = self.degrees[i] - old_weight + weight
        self.updated_adjacency_matrix() 
            
    def has_edge(self, i, j):
        """Tests to see if an edge exists between two nodes."""
        
        if self.adjacency_matrix[i,j] > 0:
            return True
        else:
            return False
    
    def calculate_modularity_matrix(self):
        """Returns the modularity matrix as described in
           Newman (2008)."""
    
        m = self.number_of_edges
        k = self.degrees
        A = self.get_adjacency_matrix_numpy()
    
        # The null model is a random graph obeying the same
        # degree distribution
        null_model = dot(k,k.T)/(2*m)
            
        modularity_matrix = (A - null_model).tolist()
        return modularity_matrix
        
    def calculate_modularity(self, partition):
        """Actually calculates the modularity of this partition.
           This method is based on the one given by Newman (2006)."""
           
        # We use the CSR sparse matrix format for multiplication operations
        
        # First obtain the grouping matrix.
        S = partition.as_matrix()
        # Then the network's adjacency matrix.
        A = self.get_adjacency_matrix_sparse()
        
        # Convenience
        k = self.degrees
        m = self.number_of_edges
        
        # Calculate Tr( S^T B S )
        #
        # where B = A - k^2/2m 
        #
        first_half = (S*A*S.transpose(copy=True)).diagonal().sum()
        d_s = numpy.array(S*k)
        second_half = numpy.square(d_s).sum()/(2*m)            
        
        # Q = Tr(S^T B S)/2m
        return (first_half - second_half)/(2*m)        

    
    def create_from_file(filename):
        """Loads an undirected network from a file.
           Supported formats are adjacency matrices (.adj) and lists of pairs (.pairs).
           If format is unknown, returns None."""
           
        extension = filename.split(".")[-1]
        
        if extension == "pairs":
            return UndirectedNetwork.create_from_pairs_file(filename)
        elif extension == "adj":
            return UndirectedNetwork.create_from_matrix_file(filename)
        else:
            print "Error! Improper file format for network!"
            return None
    
    def create_from_pairs_file(filename):
        """Loads an undirected network from a .pairs file."""
        
        file = open(filename, "r")
        
        # Create a list of pairs of nodes
        # with edges between them.
        edges = []
        weights = []
        for line in file:
            nodes = line.split()
            edges.append((nodes[0],nodes[1]))
            if len(nodes) == 3:
                weights.append(nodes[2])
            else:
                weights.append(1)

        file.close()
        
        # unfortunately, numbers in file may not
        # correspond to numbers in network, so
        # we construct a hashtable
        
        dict = {}
        current_number = 0
        
        for edge in edges:
            if not dict.has_key(edge[0]):
               dict[edge[0]] = current_number
               current_number += 1
            if not dict.has_key(edge[1]):
                dict[edge[1]] = current_number
                current_number += 1  
        
        
        # Find the number of nodes.
        n = current_number
        
        ij = []

        #make sure the adjacency matrix is symmetric
        ij.extend([[dict[edge[0]],dict[edge[1]]] for edge in edges])
        ij.extend([[dict[edge[1]],dict[edge[0]]] for edge in edges])
        ij = numpy.array(ij).transpose()
        weights.extend(weights)
        data = numpy.array(weights)
      
        A = sparse.csr_matrix((data,ij),shape=(n,n))
        network = UndirectedNetwork(n,A)

        return network
        
    def create_from_matrix_file(filename):
        """Loads an undirected network from an .adj file."""
        
        file = open(filename, "r")
        first_row = file.readline().split()
        
        # Get the number of nodes
        number_of_nodes = len(first_row)
        
        # Create the adjacency matrix
        matrix = sparse.lil_matrix((number_of_nodes,number_of_nodes))
        
        # Fill in entries in first row
        for j in range(0, len(first_row)):
            weight = float(first_row[j])
            if weight > 0:
                matrix[0,j] = weight
        
        # Fill in the entries for the remaining rows
        i = 1
        for line in file:
            row = line.split()
            for j in range(0,len(row)):
                weight = float(row[j])
                if weight > 0:
                    matrix[i,j] = weight
            i = i + 1
            
        # since it is an undirected network, we should
        # symmetrize the matrix. If already symmetric, 
        # this will do nothing.
        matrix = (matrix + matrix.T)/2    
        
        file.close()

        return UndirectedNetwork(number_of_nodes,matrix)
    
    # Make these creation methods static.
    create_from_file = staticmethod(create_from_file) 
    create_from_pairs_file = staticmethod(create_from_pairs_file)    
    create_from_matrix_file = staticmethod(create_from_matrix_file)
    
    
    def save_as_matrix_file(self, filename):
        """Saves the network's adjacency matrix to disk as a .matrix file."""
        file = open(filename, "w")
        n = self.number_of_nodes
        A = self.adjacency_matrix
        
        for i in xrange(0, n):
            for j in xrange(0, n):
                file.write("%g " % A[i,j])
            file.write("\n")
    
        file.close

    def save_as_pairs_file(self, filename):
        file = open(filename, "w")
        n = self.number_of_nodes
        A = self.adjacency_matrix
    
        for i in xrange(0, n):
            for j in xrange(0,i+1):
                if A[i,j] > 0:
                    if A[i,j] == 1:
                        file.write("%d\t%d\n" % (i,j))
                    else:
                        file.write("%d\t%d\t%g" % (i,j))
        file.close()

    def create_graphml_file(self, filename, grouping = None):
        """Creates a graphml file that can be used to generate a visualization
           of the network using the optional partition to color the nodes."""
           
        file = open(filename+".graphml", "w")
        
        if grouping == None:
            grouping = Grouping.create_from_vector([0]*self.number_of_nodes)
        
        n = self.number_of_nodes
        m = self.number_of_edges

        nodes = grouping.get_nodes()
        non_empty_groups = grouping.get_non_empty_groups()
        c = len(non_empty_groups)        
        A = self.adjacency_matrix
        
        print n,m,c,len(nodes),len(colors)
        
        # set palettes...
        if c <= len(colors):
            if c == 1:
                palette = ["#FFFFFF"]
            else:
                palette = graphml_colors
        else:
            palette = map(lambda x: "gray"+str(x), range(0,100))
        
        
        # Preamble stuff...
        file.write('<?xml version="1.0" encoding="UTF-8" standalone="no"?>\n<graphml xmlns="http://graphml.graphdrawing.org/xmlns/graphml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:y="http://www.yworks.com/xml/graphml" xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/graphml http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd">\n<key for="node" id="d0" yfiles.type="nodegraphics"/>\n<key attr.name="description" attr.type="string" for="node" id="d1"/>\n<key for="edge" id="d2" yfiles.type="edgegraphics"/>\n<key attr.name="description" attr.type="string" for="edge" id="d3"/>\n<key for="graphml" id="d4" yfiles.type="resources"/>\n')
  
        # Graph header
        
        file.write("""<graph edgedefault="directed" id="G" parse.edges="%d" parse.nodes="%d" parse.order="free">\n""" % (m,n))


        # Now we write the nodes...
        for i in xrange(0,n):
            file.write("""<node id="n%d">
      <data key="d0">
        <y:ShapeNode>
          <y:Geometry height="30.0" width="30.0" x="80.56138057743618" y="0.0"/>
          <y:Fill color="%s" transparent="false"/>
          <y:BorderStyle color="#000000" type="line" width="1.0"/>
          <y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="18.701171875" modelName="internal" modelPosition="c" textColor="#000000" visible="true" width="33.0" x="-1.5" y="5.6494140625">%d</y:NodeLabel>
          <y:Shape type="ellipse"/>
        </y:ShapeNode>
      </data>
      <data key="d1"/>
    </node>\n""" % (i, palette[non_empty_groups.index(nodes[i])], i))
    
        current_edge = 0
        # Now we do the edges...
        for i in range(0, n):
            for j in range(0,i+1):
                if A[i,j] == 1:
                    file.write("""<edge id="e%d" source="n%d" target="n%d">
      <data key="d2">
        <y:PolyLineEdge>
          <y:Path sx="0.0" sy="0.0" tx="0.0" ty="0.0"/>
          <y:LineStyle color="#000000" type="line" width="1.0"/>
          <y:Arrows source="none" target="none"/>
          <y:BendStyle smoothed="false"/>
        </y:PolyLineEdge>
      </data>
      <data key="d3"/>
    </edge>\n""" % (current_edge, i, j))
                    current_edge += 1


        # close off tags...
        file.write(""" </graph>
  <data key="d4">
    <y:Resources/>
  </data>
</graphml>""")
        file.close()

# The colors used for the group coloring scheme.
colors = ["red", "blue", "yellow", "chartreuse", "darkgreen", "darkorchid", "darkorange", "deeppink", "dimgrey", "gold", "sienna3", "royalblue", "wheat", "orangered", "lightskyblue", "gray100", "gray75", "gray56", "gray31", "gray16"]  

graphml_colors = ["#FF0000", "#0000FF", "#FFFF00", "#7FFF00", "#006400", "#9932CC", "#FF8C00", "#FF1493", "#696969", "#FFD700", "#A0522D", "#4169E1", "#F5DEB3", "#FF4500", "#87CEFA"]

class DirectedNetwork(Network):
    
    def __init__(self, number_of_nodes, adjacency_matrix = None):
        if adjacency_matrix == None:
            Network.__init__(self, sparse.lil_matrix((number_of_nodes, number_of_nodes)))
        else:
            Network.__init__(self, adjacency_matrix)
            
        self.number_of_edges = self.adjacency_matrix.sum()
        
        # Calculate the degree distribution
        self.in_degrees = self.adjacency_matrix.sum(axis=1)
        self.out_degrees = self.adjacency_matrix.sum(axis=0)
        
    def add_edge(self, i, j, weight = 1):
        """Adds an edge from node_i to node_j with the specified weight."""           
        self.update_edge(i,j, weight + self.adjacency_matrix[j,i])

    def remove_edge(self, i, j):
        """Removes the edge from node_i to node_j.  If this edge does not
           exist, this method has no effect."""
           
        self.update_edge(i,j, 0)
    
    def update_edge(self, i, j, weight):
        """Set weight of edge from i to j."""
        A = self.adjacency_matrix
        old_weight = A[j,i]
        A[j,i] = weight
        self.number_of_edges = self.number_of_edges - old_weight + weight
        self.in_degrees[j] = self.in_degrees[j] - old_weight + weight
        self.out_degrees[i] = self.out_degrees[i] - old_weight + weight
        self.updated_adjacency_matrix() 
            
    def has_edge(self, i, j):
        """Tests to see if an edge from i to j exists."""
        
        if self.adjacency_matrix[j,i] > 0:
            return True
        else:
            return False
    
    def calculate_modularity_matrix(self):
        """Returns the modularity matrix as described in
           Newman (2008)."""
    
        m = self.number_of_edges
        k_in = self.in_degrees
        k_out = self.out_degrees
        A = self.get_adjacency_matrix_numpy()
    
        # The null model is a random graph obeying the same
        # degree distribution
        null_model = dot(k_in,k_out.T)/(m)
            
        modularity_matrix = (A + A.T - null_model - null_model.T).tolist()
        return modularity_matrix
        
    def calculate_modularity(self, partition):
        """Actually calculates the modularity of this partition.
           This method is based on the one given by Newman (2006)."""
           
        # We use the CSR sparse matrix format for multiplication operations
        
        # First obtain the grouping matrix.
        S = partition.as_matrix()
        # Then the network's adjacency matrix.
        A = self.get_adjacency_matrix_sparse()
        
        # Convenience
        k_in = self.in_degrees
        k_out = self.out_degrees
        m = self.number_of_edges
        
        # Calculate Tr( S^T B S )
        #
        # where B = A + A^T - k_in^T k_out/m - k_out^T k_in/m 
        #
        first_half = (S*(A+A.transpose(copy=True))*S.transpose(copy=True)).diagonal().sum()
        d_in = numpy.array(S*k_in)
        d_out = numpy.array(S*k_out)
        second_half = 2*d_in*d_out/m            
        
        # Q = Tr(S^T B S)/2m
        return (first_half - second_half)/(2*m)        
        
    
    def create_from_file(filename):
        """Loads an undirected network from a file.
           Supported formats are adjacency matrices (.adj) and lists of pairs (.pairs).
           If format is unknown, returns None."""
           
        extension = filename.split(".")[-1]
        
        if extension == "pairs":
            return DirectedNetwork.create_from_pairs_file(filename)
        elif extension == "adj":
            return DirectedNetwork.create_from_matrix_file(filename)
        else:
            print "Error! Improper file format for network!"
            return None
    
    def create_from_pairs_file(filename):
        """Loads a directed network from a .pairs file."""
        
        file = open(filename, "r")
        
        # Create a list of pairs of nodes
        # with edges between them.
        edges = []
        weights = []
        for line in file:
            nodes = line.split()
            edges.append((nodes[0],nodes[1]))
            if len(nodes) == 3:
                weights.append(nodes[2])
            else:
                weights.append(1)

        file.close()
        
        # unfortunately, numbers in file may not
        # correspond to numbers in network, so
        # we construct a hashtable
        
        dict = {}
        current_number = 0
        
        for edge in edges:
            if not dict.has_key(edge[0]):
               dict[edge[0]] = current_number
               current_number += 1
            if not dict.has_key(edge[1]):
                dict[edge[1]] = current_number
                current_number += 1  
        
        
        # Find the number of nodes.
        n = current_number
        
        ij = []

        #make sure the adjacency matrix is symmetric
        ij.extend([[dict[edge[1]],dict[edge[0]]] for edge in edges])
        ij = numpy.array(ij).transpose()
        data = numpy.array(weights)
       
        A = sparse.csr_matrix((data,ij),shape=(n,n))
        network = DirectedNetwork(n,A)

        return network
        
    def create_from_matrix_file(filename):
        """Loads an undirected network from an .adj file."""
        
        file = open(filename, "r")
        first_row = file.readline().split()
        
        # Get the number of nodes
        number_of_nodes = len(first_row)
        
        # Create the adjacency matrix
        matrix = sparse.lil_matrix((number_of_nodes,number_of_nodes))
        
        # Fill in entries in first row
        for j in range(0, len(first_row)):
            weight = float(first_row[j])
            if weight > 0:
                matrix[0,j] = weight
        
        # Fill in the entries for the remaining rows
        i = 1
        for line in file:
            row = line.split()
            for j in range(0,len(row)):
                weight = float(row[j])
                if weight > 0:
                    matrix[i,j] = weight
            i = i + 1
        
        file.close()

        return UndirectedNetwork(number_of_nodes,matrix)
    
    # Make these creation methods static.
    create_from_file = staticmethod(create_from_file) 
    create_from_pairs_file = staticmethod(create_from_pairs_file)    
    create_from_matrix_file = staticmethod(create_from_matrix_file)
    
    
    def save_as_matrix_file(self, filename):
        """Saves the network's adjacency matrix to disk as a .matrix file."""
        file = open(filename, "w")
        n = self.number_of_nodes
        A = self.adjacency_matrix
        
        for i in xrange(0, n):
            for j in xrange(0, n):
                file.write("%g " % A[i,j])
            file.write("\n")
    
        file.close

    def save_as_pairs_file(self, filename):
        file = open(filename, "w")
        n = self.number_of_nodes
        A = self.adjacency_matrix
    
        for i in xrange(0, n):
            for j in xrange(0,n):
                if A[j,i] > 0:
                    if A[j,i] == 1:
                        file.write("%d\t%d\n" % (i,j))
                    else:
                        file.write("%d\t%d\t%g" % (i,j))
        file.close()

        
def run_test():
    ######################################################
    # 
    # Runs a couple sanity checks on the network:
    #
    #    0      0
    #    |\    /|
    #    | 0--0 |
    #    |/    \|
    #    0      0
    #
    # with the obvious correct grouping followed
    # by a slightly non-optimal grouping.
    #
    # Results should be Q1 ~ .357, Q2 ~ .122
    #    
    ######################################################
     
    network = create_test_network()

    print "A = ", network

    group1 = Grouping.create_from_vector([1,1,1,0,0,0], network)
    print "S1 = ", group1

    group2 = Grouping.create_from_vector([1,1,1,1,0,0], network)
    print "S2 = ", group2
    
    group3 = Grouping.create_from_vector([1,1,0,1,0,0], network)
    print "S3 = ", group3



    Q1 = group1.get_modularity()
    Q2 = group2.get_modularity()
    Q3 = group3.get_modularity()
    
    
    print "Modularities:"

    print "Q_1 = ", Q1
    print "Q_2 = ", Q2
    print "Q_3 = ", Q3
    
    if ((Q1 - .357) < .001) and ((Q2 - .122) < .001):
        print "Test passed!"
    else:
        print "*** Test Failed! ***"
        return False
        
    print "Trying point patch method..."
    
    pointpatch = PointPatch(3,0,1)
    
    Q4 = pointpatch.get_modularity(group1, network)    

    print "Q_4 = ", Q4
    
    print group1.get_nodes()
    print group1.get_groups()
    
    pointpatch.modify(group1)
    
    print group1.get_nodes()
    print group1.get_groups()
    
    Q5 = group1.calculate_modularity(network)
    
    print "Q_5 = ", Q5
    
    if Q5-Q2 < .00001:
        print "Test passed!"
    else:
        print "*** Test Failed! ***"
        return False
                
    return True

def create_random_network(number_of_nodes,edge_parameter):
    """Creates a random network with either a fixed number of
       edges m or a probability of connecting two nodes given
       by p"""
    
    n = number_of_nodes
    network = UndirectedNetwork(n)
    
    if edge_parameter > 1:
        # create a random network with a fixed
        # number of edges
        m = edge_parameter
    
        created_edges = 0
        while created_edges < m:
            i = random.randint(0, n-1)
            j = i
            while i == j:
                j = random.randint(0, n-1)
            if not network.has_edge(i,j):
                network.add_edge(i,j)
                created_edges = created_edges + 1    
        return network
    else:
        # create a probabilistic graph
        p = edge_parameter
        for i in range(0,n):
            for j in range(0,i+1):
                if random.random() < p:
                    network.add_edge(i,j)
        return network




def create_ring_network(number_of_cliques, nodes_per_clique):
    """Creates a ring network consisting of n cliques each containing
       m nodes per clique connected by single edges into a ring structure."""
       
    number_of_nodes = number_of_cliques * nodes_per_clique
    network = UndirectedNetwork(number_of_nodes)
    
    # for each clique
    for i in range(0, number_of_cliques):
        
        offset = i*nodes_per_clique
    
        # for each node in each clique
        for j in range(0, nodes_per_clique):
        
            # connect the node to all the other nodes in the clique
            for k in range(0,j):        
                network.add_edge(offset+j,offset+k)
    
        # connect the clique to the previous one
        if not (offset == 0):
            network.add_edge(offset, offset-1)
    
    # we are done so connect first and last...
    network.add_edge(0,number_of_nodes-1)
            
    return network


def simple_probability_function(n,i):
    """In one of our HRG's with n+1 levels, gives the probability of
       connecting two nodes in opposite subtrees at level i."""
       
    return 2**(-(n-i-1))  

def create_bbtree_network(number_of_levels, probability_function = simple_probability_function):
    """Creates once of our specialized HRG's consiting of a balanced binary tree."""
    
    n = number_of_levels - 1
    a = probability_function
    number_of_nodes = 2**n
    
    network = UndirectedNetwork(number_of_nodes)
    
    # to connect the nodes, we iterate through 
    # each level starting from the bottom
    # (excluding level n because there are no
    #  links within single nodes)
    
    for i in range(n-1, -1, -1):
        
        # for each group at this level
        for j in range(0, 2**i):

            # for each possible link in this group
            
            subgroup_size = 2**(n-i-1)
            base_index = j*2**(n-i)
            
            for u in xrange(base_index, base_index + subgroup_size):
                for v in xrange(base_index + subgroup_size, base_index + 2*subgroup_size):
                    if random.random() < a(n,i):
                        network.add_edge(u,v)
                                                
    return network
    
def create_flat_hrg(p_in):
    """Creates a "flat" random network with well defined groups but no hierarchy."""
    
    number_of_nodes = 128
    number_of_groups = 4
    group_size = 32
    
    number_of_edges = 1024
        
    network = UndirectedNetwork(number_of_nodes)
    
    prob_within = number_of_edges*p_in/(number_of_groups*choose(group_size, 2))
    prob_without = number_of_edges*(1-p_in)/(group_size*group_size*choose(number_of_groups, 2))
        
    within_edges = 0
    without_edges = 0    
        
    # connect nodes within modules
    for group in xrange(0, number_of_groups):        
        for i in xrange(group*group_size, (group+1)*group_size):
            for j in xrange(group*group_size, i):
                if random.random() < prob_within:
                    network.add_edge(i,j)
                    within_edges += 1
    
    # connect modules together
    for i in xrange(0, number_of_groups):
        for j in xrange(0, i):
            for u in xrange(i*group_size, (i+1)*group_size):
                for v in xrange(j*group_size, (j+1)*group_size):
                    if random.random() < prob_without:
                        network.add_edge(u,v)
                        without_edges += 1
                        
    return network
        
        
def create_test_network():
    ######################################################
    # 
    # Creates the network:
    #
    #    0      4
    #    |\    /|
    #    | 2--3 |
    #    |/    \|
    #    1      5
    #
    ######################################################
    
    network = UndirectedNetwork(6)
    network.add_edge(0,1)
    network.add_edge(0,2)
    network.add_edge(2,1)
    network.add_edge(2,3)
    network.add_edge(3,4)
    network.add_edge(4,5)
    network.add_edge(3,5)
    return network
    
def create_small_network():
    ######################################################
    #
    # Creates the network:
    #
    # 0--1--4--5
    # |><|  |><|
    # 2--3  6--7
    #    |  |
    #    8--9
    #    |><| 
    #   10--11
    #
    ######################################################
    
    network = create_ring_network(3,4)
    
    return network

def create_nx_from_pairs_file(filename):
    """Loads an undirected network from a .pairs file."""

    file = open(filename, "r")

    # Create a list of pairs of nodes
    # with edges between them.
    edges = []
    for line in file:
        nodes = line.split()
        edges.append((nodes[0],nodes[1]))

    file.close()

    # unfortunately, numbers in file may not
    # correspond to numbers in network, so
    # we construct a hashtable

    import networkx as nx
    
    netx = nx.Graph()
    netx.add_edges_from(edges)
    return netx

def create_nx_from_network(net):
    edges = []
    n = net.number_of_nodes
    A = net.adjacency_matrix
    for i in xrange(0,n):
        for j in xrange(0,i+1):
            if A[i,j] > 0:
                edges.append((i,j))

    import networkx as nx
    netx = nx.Graph()
    netx.add_edges_from(edges)
    return netx

def prune(net):
    import networkx as nx
    # removes the unconnected components
    G = create_nx_from_network(net)
    connected_component = G.subgraph(nx.connected_components(G)[0])
    return UndirectedNetwork(connected_component.number_of_nodes(),nx.adj_matrix(connected_component))
        
if __name__ == '__main__':
    run_test()
