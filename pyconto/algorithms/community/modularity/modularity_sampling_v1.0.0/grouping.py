from scipy import sparse
from numpy import array,trace,dot
import numpy
#from fast import multiply
import copy

##################################################################################
#
#   This module contains classes representing partions of a network as well as
#   a class representing a modification to a partition of a network for use in
#   the simulated annealing algorithm.  In addition, the routines for calculating
#   the modularity of these objects are also located here.
#
##################################################################################


class Grouping(object):
    """The Grouping class represents a grouping (or a partition) of a network."""
        
    def __init__(self, nodes, groups = None, non_empty_groups = None, empty_groups = None, network = None, modularity = None):
        
        self.nodes = nodes
        self.groups = groups
        self.non_empty_groups = non_empty_groups
        self.empty_groups = empty_groups
        self.network = network
        self.modularity = modularity 
        
        self.number_of_nodes = len(nodes)

        if self.number_of_nodes == 0:
            self.number_of_groups = 0
            self.number_of_nonempty_groups = 0
            self.number_of_empty_groups = 0
        else:
            if non_empty_groups == None:
                self.number_of_groups = max(nodes)+1
            else:
                self.number_of_groups = len(non_empty_groups)
                self.number_of_nonempty_groups = self.number_of_groups
        
            if empty_groups == None:
                self.number_of_empty_groups = self.number_of_nodes - self.number_of_groups
            else:
                self.number_of_empty_groups = len(empty_groups)
             
        
        # Deprecated!  Used only for archaic
        # modularity calculation.
        self.grouping_matrix = None

        self.ordered_string_representation = None

    def copy(self):
        """Creates a (deep) copy of this partition."""
        
        nodes = copy.copy(self.nodes)
        groups = copy.deepcopy(self.groups)
        non_empty_nodes = copy.copy(self.non_empty_groups)
        empty_nodes = copy.copy(self.empty_groups)
        network = self.network
        modularity = self.modularity

        return Grouping(nodes, groups, non_empty_nodes, empty_nodes, network, modularity)
    
    ############################################################################
    # The following are partition creation methods:  
    # 
    # Groupings can be created from 
    #
    #   a.) vectors of the form [1,3,2,...]
    #   b.) strings of the form "[1,3,2,...]"
    #   c.) patches
    #   d.) cluster files (i.e. data from other scientists)
    #
    # using the static methods defined below.  
    #
    ############################################################################
             
    def create_from_vector(grouping_vector, network = None, modularity = None):
        """Creates a grouping from a vector (Python list) of length
           n, where n is the number of nodes, and each entry is equal
           to that node's group id (i.e., 0,1,2,3,...)."""        
           
        return Grouping(grouping_vector, network = network, modularity = modularity)    
         
    def create_from_patch(old_grouping, patch, new_modularity = None):
        """Creates a grouping using a previously created grouping
           and a patch to that grouping."""
        
        grouping = patch.modify(old_grouping.copy())
        grouping.modularity = new_modularity
           
        return grouping
        
    def create_from_string(string, network = None, modularity = None):  
        """Creates a grouping from a string representing
           the grouping vector.  Expects a string of the 
           form "[0, 1, 0, 2, 0, 3,...]". """
        
        start = string.find("[")+1
        end = string.find("]")
       
        if start == end:
            vector = []
        else:
            if string.count(",") > 0:
                nodes = string[start:end].split(",")
            else:
                nodes = string[start:end].split()
            n = len(nodes)
        
            vector = range(0,n)
        
            for i in range(0, n):
                vector[i] = int(nodes[i])
            
        return Grouping.create_from_vector(vector, network, modularity)
    
    def create_from_cluster_file(filename, network):
        """Creates a grouping from a cluster file."""
        
        nodes, groups = create_from_cluster(filename)
        return Grouping(nodes, groups = groups, network = network)
    
    # We set the partition creation functions to be static methods.
    create_from_vector = staticmethod(create_from_vector)
    create_from_patch = staticmethod(create_from_patch)
    create_from_string = staticmethod(create_from_string) 
    create_from_cluster_file = staticmethod(create_from_cluster_file) 
            
    #############################################################################
    #
    #   Remaining member functions
    #
    #############################################################################
    
    def __str__(self):
        """Returns a string representation of this partition."""
        return str(self.get_nodes())

    def to_ordered_string(self):
        if self.ordered_string_representation == None:
            nodes = self.get_nodes()
            n = self.number_of_nodes

            group_map = -1*numpy.ones(n)
            current_number = 0
            for i in xrange(0,n):
                if group_map[nodes[i]] < 0:
                    group_map[nodes[i]] = current_number
                    current_number += 1
        
            new_nodes = group_map[nodes].tolist()
            self.ordered_string_representation = str(new_nodes)
        return self.ordered_string_representation
                    
    def get_modularity(self):
        """Returns the modularity of this partition.  If a network
           argument is provided, the modularity is calculated using
           that network (the slow way).  If the network argument is 
           omitted and the partition was created with a network attribute,
           then the modularity is only calculated once and then stored for
           later retrieval."""
        
        if self.modularity == None:
            self.modularity = self.calculate_modularity(self.network)
        return self.modularity
    
    def calculate_modularity(self, network):
        """Actually calculates the modularity of this partition.
           This method is based on the one given by Newman (2008).
           Warning: this method can be slow for large networks."""
        
        if network==None:
            #Error, modularity is not defined!
            return -2
        else:
            return network.calculate_modularity(self)        
        
    def as_matrix(self):
        """Returns the matrix representation of this partition.
           is a k-by-n matrix with a one in i,j if node j is in 
           group i.
           Only creates the matrix once and then stores it for
           later use."""
        
        # If this grouping was created with a vector, then we must
        # create the matrix from scratch...
        
        if self.grouping_matrix == None:
            n = self.number_of_nodes
            data = numpy.ones(n)
            ij = numpy.array([self.nodes,range(0,n)])

            print "Creating grouping matrix:"

            self.grouping_matrix = sparse.csr_matrix((data,ij))
            
        print self.grouping_matrix.shape       
        return self.grouping_matrix   
        
    def get_groups(self):
        """Returns an array of lists.  Each of these lists represents
           a group in the partition and its elements are the nodes
           contained in that group.  Some of these groups may be 
           empty. To get a list of indices corresponding to empty/nonempty
           groups, use methods get_empty_groups/get_non_empty_groups.
           Like the other methods, only creates the groups list once and
           then stores it for later use."""
        
        # If this grouping was created using only a nodes vector, then we must
        # create the groups array from scratch...
        
        if self.groups == None:
            self.groups = create_groups_from_nodes(self.nodes)     
                
        return self.groups
        
    def get_nodes(self):
        """Returns an array whose indices are nodes and whose
           elements give the group containing that node."""
           
        return self.nodes
        
    def get_empty_groups(self):
        """Returns an array of indices of the groups object corresponding to
           empty groups in the groups object.
           
           See: get_groups"""
            
        # If this grouping was created with a nodes vector, then we must
        # create the empty groups array from scratch...
        
        if self.empty_groups == None:
            groups = self.get_groups()
            self.non_empty_groups, self.empty_groups = create_empty_groups_from_groups(groups)

            # now we know how many empty/non-empty groups there are
            self.number_of_groups = len(groups) - len(self.empty_groups)
            self.number_of_empty_groups = len(self.empty_groups)
        
        return self.empty_groups
        
    def get_non_empty_groups(self):
        """Returns an array of indices of the groups object corresponding to
           non-empty groups in the groups object.
           
           See: get_groups"""
           
        # If this grouping was created with a nodes vector, then we must
        # create the empty groups array from scratch...
        
        if self.non_empty_groups == None:
            groups = self.get_groups()
            self.non_empty_groups, self.empty_groups = create_empty_groups_from_groups(groups)
            # now we know how many non-empty groups there are
            self.number_of_groups = len(groups) - len(self.empty_groups)
            self.number_of_empty_groups = len(self.empty_groups)
        
        return self.non_empty_groups
        
    def sanity_check(self):
        nodes = self.get_nodes()
        groups = self.get_groups()
        empty_groups = self.get_empty_groups()
        non_empty_groups = self.get_non_empty_groups()
        
        for node in nodes:
            if not node in groups[nodes[node]]:
                raise SanityException("Node %d not in %d" % (node, nodes[node]))
                
        for group in empty_groups:
            if not len(groups[group]) == 0:
                raise SanityException("Group %d was not empty" % group)
        
        for group in non_empty_groups:
            if not len(groups[group]) > 0:
                raise SanityException("Group %d was empty" % group)
                
    def is_identical(self, new_group):
        nodes = self.get_nodes()
        groups = self.get_groups()
        non_empty_groups = self.get_non_empty_groups()

        new_groups = new_group.get_groups()
        
        for index in non_empty_groups: 
            group = groups[index]
            
            #get the corresponding group in the new partition
            new_group = new_groups[nodes[group[0]]]
            
            if not set(group) == set(new_group):
                return False
                    
        return True

        
class SanityException(Exception):
    
    def __init__(self, message):
        self.message = message
    
    def __str__(self):
        return self.message         
        
class PointPatch:
    """The PointPatch class represents a single move modification to a partition
       of a network.  Used for performance reasons in the simluated annealing algorithm."""
    
    def __init__(self, node, old_group, new_group):
        self.node = node
        self.old_group = old_group
        self.new_group = new_group
            
    def modify(self, grouping):
        """Modifies a grouping to reflect the changes
           represented by this patch.  Does NOT create
           a copy of the grouping first."""
           
        nodes = grouping.get_nodes()
        groups = grouping.get_groups()
        empty_groups = grouping.get_empty_groups()
        non_empty_groups = grouping.get_non_empty_groups()
        
        #print "Moving node", self.node, "from", self.old_group, "into", self.new_group
        
        nodes[self.node] = self.new_group
        
        groups[self.old_group].remove(self.node)
        if len(groups[self.old_group]) == 0:
            # we've completely removed a group
            empty_groups.append(self.old_group)
            non_empty_groups.remove(self.old_group)
            grouping.number_of_groups -= 1
            
            
        groups[self.new_group].append(self.node)
        if len(groups[self.new_group]) == 1:
            # we've created a new group!
            empty_groups.remove(self.new_group)
            non_empty_groups.append(self.new_group)
            grouping.number_of_groups += 1
        
        grouping.grouping_matrix = None
        grouping.ordered_string_representation = None
        return grouping
    
    def revert(self, grouping):
        """Assumes the grouping has already been modified 
           by this patch and undoes the changes."""
    
        nodes = grouping.get_nodes()
        groups = grouping.get_groups()
        empty_groups = grouping.get_empty_groups()
        non_empty_groups = grouping.get_non_empty_groups()
        
        nodes[self.node] = self.old_group
        
        groups[self.new_group].remove(self.node)
        if len(groups[self.new_group]) == 0:
            # we've completely removed a group
            empty_groups.append(self.new_group)
            non_empty_groups.remove(self.new_group)
            grouping.number_of_groups -= 1
            
            
        groups[self.old_group].append(self.node)
        if len(groups[self.old_group]) == 1:
            # we've created a new group!
            empty_groups.remove(self.old_group)
            non_empty_groups.append(self.old_group)
            number_of_groups += 1
            
        grouping.grouping_matrix = None
       
        return grouping
    
    
    def get_modularity_old(self, grouping, network):
        """Calculates the modularity by calculating the small
           difference in modularity and then adding that to the
           previously calculated modularity.  Should run in 0(n)."""
        
        B = network.get_modularity_matrix()
        groups = grouping.get_groups()
        Q = grouping.get_modularity()
        x = self.node
        y = self.old_group
        z = self.new_group
        m = network.number_of_edges       
        
        #
        #      B_xx + B(row x) * S(col z) - B(row x) * S(col y)
        # dQ = ------------------------------------------------
        #                              m
        # 
        
        S_z = groups[z]
        S_y = groups[y]
        B_x = B[x]
        B_xx = B_x[x]
        
	

        #delta_Q = (B[x,x]+B[x,S_z].sum()-B[x,S_y].sum())/m
	    
        delta_Q = (B_xx+multiply(B_x,S_z) - multiply(B_x, S_y))/m
        
        return delta_Q + Q      

    def get_modularity(self,grouping, network):
        B = network.get_modularity_matrix_numpy()
        groups = grouping.get_groups()
        Q = grouping.get_modularity()
        x = self.node
        y = self.old_group
        z = self.new_group
        m = network.number_of_edges

        S_z = groups[z]
        S_y = groups[y]
        B_x = B[x]
        B_xx = B_x[x]


        delta_Q = (B_xx+B_x[S_z].sum()-B_x[S_y].sum())/m
        return delta_Q + Q

class MergePatch:
    """The MergePatch class represents a modification to a partition where two previously
       existing groups are merged.  Used for performance reasons in the simluated annealing algorithm."""
    
    def __init__(self, group1, group2):
        self.group1 = group1
        self.group2 = group2
            
    def modify(self, grouping):
        """Modifies a grouping to reflect the changes
           represented by this patch.  Does NOT create
           a copy of the grouping first."""
           
        nodes = grouping.get_nodes()
        groups = grouping.get_groups()
        empty_groups = grouping.get_empty_groups()
        non_empty_groups = grouping.get_non_empty_groups()
        group1 = self.group1
        group2 = self.group2
        
        #print "Merging", group1, group2
        
        
        # sanity check
        if not (group1 in non_empty_groups and group2 in non_empty_groups):
            print "Serious problem here!" 
        
        
        groups[group1].extend(groups[group2])
        
        for node in groups[group2]:
            nodes[node] = group1
        
        groups[group2] = []
        empty_groups.append(group2)
        non_empty_groups.remove(group2)
        grouping.number_of_groups -= 1
        
        grouping.grouping_matrix = None
        
        return grouping
    
    def get_modularity(self, grouping, network):
        """Calculates the modularity by calculating the small
           difference in modularity and then adding that to the
           previously calculated modularity.  Should run in 0(r^2)
           where r is the size of the groups being merged."""
        
        B = network.get_modularity_matrix_numpy()
        groups = grouping.get_groups()
        Q = grouping.get_modularity()
        group1 = groups[self.group1]
        group2 = groups[self.group2]
        m = network.number_of_edges       
        
        #
        #      Sum( B_ij for i in group1 and j in group 2 )
        # dQ = --------------------------------------------
        #                              m
        #
        
        #delta_Q = 0
        
        #for i in group1:
        #    B_i = B[i]
        #    for j in group2:
        #        delta_Q += B_i[j]
        
        #delta_Q = delta_Q/m
        
        delta_Q = B[numpy.ix_(group1,group2)].sum()/m

        return delta_Q + Q      

class SplitPatch:
    """The MergePatch class represents a modification to a partition where a previously
       existing group is split into two groups.  Used for performance reasons in the 
       simluated annealing algorithm."""
    
    def __init__(self, old_group, new_group_membership_list):
        self.old_group = old_group 
        self.new_group_membership_list = new_group_membership_list
            
    def modify(self, grouping):
        """Modifies a grouping to reflect the changes
           represented by this patch.  Does NOT create
           a copy of the grouping first."""
           
        nodes = grouping.get_nodes()
        groups = grouping.get_groups()
        empty_groups = grouping.get_empty_groups()
        non_empty_groups = grouping.get_non_empty_groups()
        old_group = self.old_group
        new_group_membership_list = self.new_group_membership_list
        
        # santiy check
        if not old_group in non_empty_groups:
            print "Serious problem here!"
        
        
        if len(new_group_membership_list) == 0 or len(new_group_membership_list) == len(groups[old_group]):
            # there is nothing to split!
            return grouping    
        
        # move selected nodes to new group
        new_group = empty_groups[0]
        
        #print "Splitting", old_group, "into", new_group
        
        groups[new_group] = new_group_membership_list
        

        for node in new_group_membership_list:
            nodes[node] = new_group
            groups[old_group].remove(node)
        
        empty_groups.remove(new_group)
        non_empty_groups.append(new_group)        
        grouping.number_of_groups += 1
        
        
        grouping.grouping_matrix = None
        
        return grouping
    
    def get_modularity(self, grouping, network):
        """Calculates the modularity by calculating the small
           difference in modularity and then adding that to the
           previously calculated modularity.  Should run in 0(r^2)
           where r is the size of the groups being merged."""
        
        B = network.get_modularity_matrix_numpy()
        groups = grouping.get_groups()
        Q = grouping.get_modularity()
        old_group = groups[self.old_group]
        new_group = self.new_group_membership_list
        m = network.number_of_edges       
        
        #
        #       - Sum( B_ij for i in old group and j in new group )
        # dQ = -----------------------------------------------------
        #                              m
        #
        
        #delta_Q = 0
        
        #for i in old_group:
        #    if not i in new_group:
        #        B_i = B[i]
        #        for j in new_group:
        #            delta_Q += B_i[j]
        
        #delta_Q = - delta_Q/m
        
        delta_Q = (-B[numpy.ix_(old_group, new_group)].sum() + B[numpy.ix_(new_group,new_group)].sum())/m

        return delta_Q + Q      

class GeneralPatch:
    
    def __init__(self, old_group, new_group, nodes_to_move):
        self.old_group = old_group
        self.new_group = new_group
        self.nodes_to_move = nodes_to_move

    def get_modularity(self,grouping,network):
        self.modify(grouping.copy()).get_modularity()

    def modify(self, grouping):
        nodes = grouping.get_nodes()
        groups = grouping.get_groups()
        empty_groups = grouping.get_empty_groups()
        non_empty_groups = grouping.get_non_empty_groups()
        
        nodes_to_move = self.nodes_to_move
        old_group = self.old_group
        new_group = self.new_group                

        for node in nodes_to_move:
            nodes[node] = new_group
        
        for node in nodes_to_move: 
            groups[old_group].remove(node)

        if len(groups[old_group]) == 0:
            # we've completely removed a group
            empty_groups.append(old_group)
            non_empty_groups.remove(old_group)
            grouping.number_of_groups -= 1
            
            
        groups[new_group].extend(nodes_to_move)
        if new_group in empty_groups:
            # we've created a new group!
            empty_groups.remove(new_group)
            non_empty_groups.append(new_group)
            grouping.number_of_groups += 1
        
        grouping.grouping_matrix = None
        
        return grouping 

class NullPatch:

    def __init__(self):
        pass

    def get_modularity(self,grouping,network):
        return grouping.get_modularity()

    def modify(self,grouping):
        return grouping

########################################################################
#
# Various housekeeping routines to convert between
# representations
#
########################################################################

        
def create_groups_from_nodes(vector):
    n = len(vector)
    c = max(vector)+1
    
    groups = [[] for i in xrange(0,n)]
    
    for i in xrange(0,n):
        groups[vector[i]].append(i)
                
    return groups

def create_empty_groups_from_groups(groups):
    
    empty_groups = []
    non_empty_groups = []
    
    for i in xrange(0, len(groups)):
        if len(groups[i]) == 0:
            empty_groups.append(i)
        else:
            non_empty_groups.append(i)
            
    return non_empty_groups, empty_groups
        
def create_matrix_from_nodes(vector):
    n = len(vector)
    c = max(vector)+1
    matrix = sparse.lil_matrix((n,c))
    for i in range(0, len(vector)):
        matrix[i,vector[i]]=1
    return matrix
    
def create_vector_from_matrix(matrix):
    n = matrix.shape[0]
    vector = range(0,n)
    for i in range(0,n):
        vector[i] = matrix.rows[i][0]
    return vector
    
def create_from_cluster(filename):
    file = open(filename, "r")

    groups = []
    number_of_nodes = 0

    for line in file:
        group = []
        nodes = line.split()
        for node in nodes:
            # node counting starts from 1 in cluster files
            group.append(int(node)-1)
        groups.append(group)
        number_of_nodes += len(nodes)
    file.close()
    
    # fill in the rest
    for i in xrange(len(groups), number_of_nodes):
        groups.append([])

    nodes = [0 for i in xrange(0,number_of_nodes)]

    for i in xrange(0,len(groups)):
        for node in groups[i]:
            nodes[node] = i
            
    return nodes, groups

marker_partition = Grouping([0],modularity=-2)
