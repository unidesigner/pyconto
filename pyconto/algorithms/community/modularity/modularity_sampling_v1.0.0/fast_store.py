from grouping import Grouping
import numpy
import math


##############################################################################
#
# This module contains a data structure for storing partitions, periodically
# writing them to disk, and keeping track of statistics about the population.
#
##############################################################################

class GroupStore(object):

    def __init__(self, filename = None, memory_size = 100000, create_new = True):
        self.filename = filename
        self.memory_size = memory_size
        self.current_index = 0
        self.groups = [None for x in xrange(0, memory_size)]
        self.modularity_distribution = []
        self.group_number_distribution = []
        self.create_new = create_new
            
    def add(self, group):
        if not (self.current_index < self.memory_size):
            self.flushgroups()
        self.groups[self.current_index] = (group.get_modularity(),group.to_ordered_string())
        self.current_index = self.current_index + 1
        
    def is_unique(self, new_group):
        partition_string = new_group.to_ordered_string()

        for grouping in self.groups[-1000:]:
            # we've reached the end of the partitions
            if grouping == None:
                break
            
            if grouping[1] == partition_string:
                return False
        
        return True
        
    
    def update_stats(self):
        """Updates the statistics to reflect the currently stored partitions."""
        
        for index in xrange(0, len(self.groups)):
            if not (self.groups[index] == None):
                # Update statistics...
                
                Q = (self.groups[index])[0]
                self.modularity_distribution.append(Q)
                
                # Get 'acutal' number of groups in this grouping...
                # not just the number of potential groups
                
                #group_number = len(self.groups[index].get_non_empty_groups())
                #self.group_number_distribution.append(group_number)
        
    def flushgroups(self):
        """Writes the currently stored partitions to disk and updates the statistics."""
        
        self.update_stats()
        
        if not (self.filename == None):
            # write groups to file...
            
            #print "Writing groups to file..."
            if self.create_new:
                file = open(self.filename, "w")
                self.create_new = False
            else:    
                file = open(self.filename,"a")
            for group_record in self.groups:  
                # Write group to file...
                if not (group_record == None):
                    file.write("%f,%s\n" % group_record)
            file.close()
        
        # Clear list...
        for index in xrange(0, len(self.groups)):
            self.groups[index] = None
        self.current_index = 0    

    def get_stats(self):
        """Flushes the stored partitions to disk, updates and then returns the 
           compiled statistics."""
           
        self.flushgroups()
        stats = {}
        modularity_distribution = numpy.array(self.modularity_distribution)
        stats['modularity_distribution'] = modularity_distribution
        stats['average_modularity'] = modularity_distribution.mean()
        stats['max_modularity'] = modularity_distribution.max()
        stats['min_modularity'] = modularity_distribution.min()
        stats['std_modularity'] = modularity_distribution.std()
        
        #stats['group_number_distribution'] = numpy.array(self.group_number_distribution)
        
        return stats

    def load_from_file(load_filename, memory_size = 10000):
        """Loads and returns a GroupStore object from file."""
        
        store = GroupStore(load_filename, memory_size)
        
        file = open(load_filename, "r")
        
        i = 0
        for line in file:
            input = line.split(",",1)
            modularity = float(input[0])
            grouping = Grouping.create_from_string(input[1],None,modularity)
            store.groups[i] = grouping
            i = i+1
            
            if not (i < memory_size):
                i = 0
                store.update_stats()
        
        # Clear the buffer...        
        for j in xrange(i,memory_size):
            store.groups[j] = None
        
        store.update_stats()
        
        # Now clear the rest...
        for j in xrange(0, i):
            store.groups[i] = None
        
        return store
        
    load_from_file = staticmethod(load_from_file) 
