import sys
import network
import grouping
import math
import simulation
from optparse import OptionParser

usage="python anneal.py [OPTION]... network_file"
parser = OptionParser(usage)
parser.add_option("-f", "--file", dest="filename", help="output sampled partitions to NAME")
parser.add_option("-T", "--initial_temperature", type="float", dest="initial_temperature", help="Initial temperature for SA (default = 0.00125)", default=0.00125)
parser.add_option("-r", "--temperature_ratio", type="float", dest="common_ratio", help="Common ratio between successive temperatures in SA (default=0.9995)", default=0.9995)
parser.add_option("-o", "--optima-only", action="store_true", dest="optima_only", help="Only store local optima (default=False)", default=False)
parser.add_option("-k", "--k-max", type="int", dest="k_max", help="Maximum number of groups in initial partition (default=size of network)", default=-1)
parser.add_option("-n", "--number-of-runs", type="int", dest="number_of_runs", help="Number of runs (default = 1)", default=1)
#parser.add_option("-d", "--directed-network", action="store_true", dest="directed", help="The network file contains a directed network (default=False)", default=False)

(options,args) = parser.parse_args()
options.directed=False

if len(args) < 1:
    print "No network file provided!"
    sys.exit(0)

network_filename = args[0]
if options.directed:
    net = network.DirectedNetwork.create_from_file(network_filename)
else:
    net = network.UndirectedNetwork.create_from_file(network_filename)

n = net.number_of_nodes
    
simulation.inverse_temperature = 1/options.initial_temperature
simulation.inverse_cooling_constant = 1/options.common_ratio
simulation.memory_size = 100000
simulation.moveset = simulation.MOVESET_SINGLE
sim = simulation.Simulation(net)

result = sim.run_simulated_annealing(options.filename, number_of_runs=options.number_of_runs, k_max=options.k_max, save_intermediates=(not options.optima_only))
