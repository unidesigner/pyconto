""" API for Brain Connectivity Toolbox with automatic conversion of
NumPy arrays and memory management.

Packages
--------

datasets : publicly available brain connectivity datasets based on tracer injection

generators : generator functions for networkx

operations : operations on networks, e.g. thresholding

measures : network measures shipped with the brain connectivity toolbox 

"""

import measures
import datasets
import operations
import generators

import logging
logging.warn("These functions have not been tested extensively.")