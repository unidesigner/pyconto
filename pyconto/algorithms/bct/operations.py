import bct
import numpy as np

def threshold_absolute(cmatrix, threshold):
    """ Applies an absolute weight threshold to a graph.  All weights below this
    threshold, as well as those on the main diagonal, are set to zero.
    """
    pass


def threshold_proportional_dir(cmatrix, p):
    """ Preserves a given proportion of the strongest weights in a directed graph.
    All other weights, as well as those on the main diagonal, are set to zero.
    """
    pass

def threshold_proportional_und(cmatrix, p):
    """ Preserves a given proportion of the strongest weights in an undirected graph.
    All other weights, as well as those on the main diagonal, are set to zero.
    """
    pass