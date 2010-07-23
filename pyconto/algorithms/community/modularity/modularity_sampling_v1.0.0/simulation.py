import network
from grouping import Grouping,PointPatch,MergePatch,SplitPatch,NullPatch,marker_partition
import fast_store as store
import numpy.random as random 
import math
#import mincut

burn_tolerance = 0.005
burn_group_size = 4000

progress_ticks = 1000

inverse_cooling_constant = 1.0015
inverse_temperature = 40

MOVESET_SINGLE = 0
MOVESET_MERGESPLIT = 1

moveset = MOVESET_SINGLE

memory_size = 10000


class Simulation:

    def __init__(self, network):
        self.network = network
        self.rejected = 0
        self.accepted = 0
        self.higher = 0
        self.split = 0
        self.merge = 0
        self.single = 0
        self.accepted_probabilities = []
        self.rejection_threshold = 0
        self.flat_threshold = 500
        
        
    def run_simulated_annealing(self, output_filename, number_of_runs=1, save_intermediates=False,k_max = -1, max_sample_size = 100000, initial_grouping = None):
        
        print "Simulated annealing for modularity maximization\n"
        
        print "SA internal settings:"
        print "Initial inverse temperature:", inverse_temperature
        print "Inverse cooling constant:", inverse_cooling_constant
        
        # Set up some SA specific settings...
        
        # Set the correct temperature dependence        
        self.get_inverse_temperature = self.get_inverse_temperature_sa
        
        if moveset == MOVESET_SINGLE:
            self.mergesplit_on = 0
        else:
            self.mergesplit_on = 1
        
        if k_max == -1:
            self.number_of_groups = self.network.number_of_nodes
        else:
            self.number_of_groups = k_max

        self.number_of_nodes = self.network.number_of_nodes

        self.optima = 0 
        self.plateaus = 0
 
        # Run the actual simulation
        partition_store = store.GroupStore(output_filename, 100000)
        
        for i in xrange(0,number_of_runs):
            try:
                print "SA run", i, "out of", number_of_runs
                print "Choosing initial grouping..."
            
                if initial_grouping == None:
                    grouping = self.initialize_grouping()
                else:
                    grouping = initial_grouping
            
                Q = grouping.get_modularity()
                print "Initial grouping: ", grouping
                print "Initial modularity: ", Q
        
                print "Beginning cooling phase..."
                if save_intermediates:
                    avg, grouping = self.sample(max_sample_size,grouping,partition_store)
                    # cooling run has terminated, mark this in the file
                    partition_store.add(marker_partition)
                else:
                    avg, grouping = self.sample(max_sample_size,grouping,None)
                    partition_store.add(grouping)

        
                print "Finished cooling!"
                print "Q_max =", grouping.get_modularity()
                print "\n"
            
            except SameException, e:
                # we hit a large plateau,
                # so we throw out this run
                
                print "We found a plateau, restarting run..."
                self.plateaus += 1
            
        
        stats = partition_store.get_stats()
        stats["optima"] = self.optima
        stats["plateaus"] = self.plateaus
        
        return stats
        
    def run_mcmc(self, filename, number_of_groups, sample_size = 10000, initial_grouping = None):
        
        print "Network Modularity MCMC Sampler\n"
        print "Generating", sample_size, "samples"
        if constrain_group_number == True:
            print "Sampling groupings with", number_of_groups, "groups\n"
        
        print "MCMC internal settings:"
        print "Inverse Temperature: ", inverse_temperature
        print "Burn group size:", burn_group_size
        print "Tolerance for difference in burn average:", burn_tolerance
        
        # 
        self.get_inverse_temperature = self.get_inverse_temperature_mcmc
        self.number_of_groups = number_of_groups              


        # Run the actual simulation...
        
        grouping_store = store.GroupStore(filename, memory_size)
        
        print "Choosing initial grouping..."
        if initial_grouping == None:
            grouping = self.initialize_grouping()
        else:
            grouping = initial_grouping
            
        Q = grouping.get_modularity()
        print "Initial grouping: ", grouping
        print "Initial modularity: ", Q
        
        grouping = self.wait_for_equilibrium(grouping)
        
        print "Beginning sampling phase..."
        avg, grouping = self.sample(sample_size,grouping,grouping_store)
        
        print "Finished sampling!"
        
        return grouping_store.get_stats()
    
    def sample(self, sample_size, initial_grouping, grouping_store = None):
        
        total_modularity = 0
        grouping = initial_grouping
        
        # counts the number of consecutive
        # modularity preserving moves accepted
        consecutive_number_flat = 0
                
        # for display purposes, counts the number
        # of modularity preserving moves accepted
        # per display period
        number_flat_per_display_period = 0
        
        # for display purposes, counts the number
        # of modularity increasing moves accepted
        # per display period
        number_improved_per_display_period = 0
        number_regressed_per_display_period = 0 
        number_proposed_per_display_period = 0        
        
        self.point_patch = PointPatch(0,0,1)
        self.merge_patch = MergePatch(0,1)
        self.split_patch = SplitPatch(0,[0])
        self.null_patch = NullPatch()
        patch = None
        
        try:
            for i in xrange(0, sample_size):
                Q_old = grouping.get_modularity()
                temperature = self.get_inverse_temperature(i)
                
                # Calculate rejection threshold
                
                c = grouping.number_of_groups
                n = self.number_of_nodes

                rejection_threshold = self.calculate_rejection_threshold(n,c)
                
                # counts the number of consecutive proposals
                # rejected in a row
                number_rejected = -1                    

                accepted = False
                while not accepted:
                    
                    number_rejected += 1
                    number_proposed_per_display_period += 1
                    
                    patch = self.propose_grouping(grouping)
                    Q_new = patch.get_modularity(grouping, self.network)

                    accepted = self.accept_grouping(Q_old, Q_new, temperature) 
                    
                    if number_rejected > rejection_threshold:
                        raise OptimumException(i)
                    
                    if accepted:
                        patch.modify(grouping)
                        grouping.modularity = Q_new
                        if not (grouping_store==None):
                            # Save intermediate partitions, 
                            # and make sure we don't do repeats... 
                            if grouping_store.is_unique(grouping):
                                grouping_store.add(grouping)
                            else:
                                accepted = False
                                patch.revert(grouping)
                                grouping.modularity = Q_old

                # the proposal was accepted!

                # keep some stats...
                if Q_new == Q_old:
                    consecutive_number_flat +=1                
                    number_flat_per_display_period += 1
                else:
                    consecutive_number_flat = 0
                    if Q_new > Q_old:
                        number_improved_per_display_period += 1
                    else:
                        number_regressed_per_display_period += 1

                total_modularity = total_modularity+Q_new
            
                # Display the current progress and
                # relevant statistics
                
                if (i % progress_ticks) == 0:
                    print i, "samples" 
                    print "Current modularity:", grouping.get_modularity()
                    print "Current number of groups:", grouping.number_of_groups
                    print "Number proposed:", number_proposed_per_display_period
                    print "Fraction of improvement moves:", number_improved_per_display_period/float(progress_ticks)
                    print "Fraction of regressed moves:", number_regressed_per_display_period/float(progress_ticks)
                    print "Fraction of flat moves:", number_flat_per_display_period/float(progress_ticks)
                    number_improved_per_display_period = 0
                    number_flat_per_display_period = 0
                    number_proposed_per_display_period = 0
                    number_regressed_per_display_period = 0
        
        except OptimumException, e:
            # in case of local optima,
            # we do not wish to finish the sample
         
            self.optima += 1
            sample_size = e.time_step
            
        last_grouping = grouping
        
        grouping_store.flushgroups()
        
        average_modularity = total_modularity/sample_size
        
        #print "Ending modularity:", last_grouping.get_modularity()
        #print "Ending Number of Groups:", last_grouping.number_of_groups
       
        # Checks to make sure our deltaQ algorithms are correctly working... 
        Q1 = last_grouping.get_modularity()
        Q2 = last_grouping.calculate_modularity(self.network)
        if math.fabs(Q1 - Q2) > 1e-09:
            print "Huge problem!"
            print Q1, Q2
        
        return (average_modularity,last_grouping)        
    
    def wait_for_equilibrium(self, initial_grouping):
        print "\nBeginning burn phase..."    
        
        grouping = initial_grouping
        burning = True
        
        print "Generating first burn group..."
        avg_modularity1, grouping = self.sample(burn_group_size, grouping)
            
        print "First burn group average modularity: ", avg_modularity1
        
        while burning:
        
            print "Generating next burn group..."
            avg_modularity2, grouping = self.sample(burn_group_size, grouping)
            
            print "Burn group average modularity: ", avg_modularity2

            # if the difference in the averages is tolerable,
            # we stop burning and start sampling
                        
            if math.fabs(avg_modularity1 - avg_modularity2) < burn_tolerance:
                avg_modularity = (avg_modularity1 + avg_modularity2)/2
                burning = False
            else:
                print "Need to burn more..."
                avg_modularity1 = avg_modularity2
        
        print "Equilibrium achieved!"
        print "Avg. Modularity: ", avg_modularity
        
        return grouping
        
        
    def initialize_grouping(self):
        n = self.network.number_of_nodes
        c = self.number_of_groups
        
        k = random.randint(2,c)

        nodes = range(0,n)
        
        for node in xrange(0, n):
            group = random.random_integers(0, k-1)
            nodes[node]=group

        return Grouping.create_from_vector(nodes, self.network)
        
    def propose_grouping(self,old_grouping, patch = None):
        #####################################################
        #
        # We propose a new grouping in one of two ways:
        #
        #  a.) put a single node in a different group
        #  b.) merge two groups
        #  c.) split a group into two groups
        #
        #####################################################
        
        nodes = old_grouping.get_nodes()
        groups = old_grouping.get_groups()
        empty_groups = old_grouping.get_empty_groups()
        non_empty_groups = old_grouping.get_non_empty_groups()
        n = old_grouping.number_of_nodes
        c = old_grouping.number_of_groups
        
        if True:
                
            # we aren't constraining group number

            # now we can choose between splitting, merging,
            # and moving a node
            
            p = random.random_sample()
            
            if p < self.merge_probability:
                # we merge two groups
                
                if c==1:
                    # Can't merge two groups if there is only one!
                    return NullPatch

                group1 = random.random_integers(0,c-1)
                group2 = random.random_integers(0,c-2)
                
                if group2 >= group1:
                    group2 += 1
                
                self.merge_patch.group1 = non_empty_groups[group1]
                self.merge_patch.group2 = non_empty_groups[group2]
                
                return self.merge_patch
                
            elif p < self.merge_probability + self.split_probability:
                # we split a group
                
                # pick a group, but not one with just one element
                old_group = non_empty_groups[random.random_integers(0,c-1)]
                
                #while len(groups[old_group]) < 2:
                #    old_group = non_empty_groups[random.random_integers(0,c-1)]
                
                # DEPRECATED: decide which nodes are in or out (randomly)
                #new_group_membership_list = []
                #for node in groups[old_group]:
                #    if random.choice([False, True]):
                #        new_group_membership_list.append(node)
                
                # use a min-cut algorithm to decide which nodes are in or out
                new_group_membership_list = mincut.minimum_cut(groups[old_group], self.network.get_adjacency_matrix_numpy())

                self.split_patch.old_group = old_group
                self.split_patch.new_group_membership_list = new_group_membership_list    
                
                return self.split_patch
            
            else:
                # we move a single node!
                
                # Pick a node                
                node = random.random_integers(0,n-1)
                old_group = nodes[node]
                    
                # If this node's group is empty,
                if len(groups[old_group]) == 1:
                    # we don't want to send it to
                    # another empty group
                    
                    index = non_empty_groups.index(old_group)
                    new_index = random.random_integers(0,c-2)
                    if new_index >= index:
                        new_index += 1
                    
                    new_group = non_empty_groups[new_index]
                    
                else:
                    # This node's group is non-empty
                    # so we can send it anywhere
                    # (including an empty group)
                    
                    new_index = random.random_integers(0,c-1) 
                    
                    #currently a hack...
                    if new_index == c-1 and (random.random_sample() < 0.0001 or c==1):
                        new_group = empty_groups[-1]
                    else:
                        # send it to a nonempty group
                        new_index = random.random_integers(0,c-2)
                        if new_index >= non_empty_groups.index(old_group):
                            new_index += 1
                        new_group = non_empty_groups[new_index]
                                
                # Create the patch to the old grouping
                self.point_patch.node = node
                self.point_patch.old_group = old_group
                self.point_patch.new_group = new_group
                
                return self.point_patch
               
           
    def accept_grouping(self, Q_old, Q_new, beta = 1):
                
        if Q_new >= Q_old:
            # we accept!
            return True
        else:
            acceptance_probability = math.exp((Q_new-Q_old)*beta)
            
            return (random.random_sample() < acceptance_probability)
             
    def get_inverse_temperature_sa(self, time_steps = 0):
        return math.pow(inverse_cooling_constant, time_steps)*inverse_temperature

    def get_inverse_temperature_mcmc(self, time_steps = 0):
        return inverse_temperature

    
    def calculate_rejection_threshold(self,n,c):
        """Uses the COUPON COLLECTOR criterion to terminate
           at a local optimum with high probability."""

        number_of_individual_moves = n*c

        if self.mergesplit_on:
            number_of_split_moves = c
            number_of_merge_moves = c*(c-1)/2
            number_of_moves = number_of_individual_moves + number_of_split_moves + number_of_merge_moves
            self.merge_probability = float(number_of_merge_moves) / number_of_moves
            self.split_probability = float(number_of_split_moves) / number_of_moves
        else:
            number_of_moves = number_of_individual_moves
            self.merge_probability = 0
            self.split_probability = 0
        
        # We want a 95% confidence
        beta = 1 - math.log(0.05)/math.log(number_of_moves)
        
        return beta*number_of_moves*math.log(number_of_moves)


class OptimumException(Exception):
    
    def __init__(self, time_step):
        self.time_step = time_step
    
    def __str__(self):
        return "Local optimum reached at time step %d" % self.time_step 
        
class SameException(Exception):

    def __init__(self, grouping = None, patch = None):
        self.grouping = grouping
        self.patch = patch
        
    def __str__(self):
        return "Made enough moves with no modularity difference"   
