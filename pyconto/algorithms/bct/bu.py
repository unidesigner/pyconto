""" Binary undirected measures """

from api import bct

def degree(cmatrix):
    """ Density is the proportion of the number of present connections in the
    network, to the maximum possible number of connections.  Connection weights
    are ignored. See e.g. Sporns (2002). Contributor: OS.
    """
    m = bct.to_gslm(cmatrix.tolist())
    deg = bct.degrees_und(m)
    rdeg = bct.from_gsl(deg)  # [2.0, 2.0, 2.0]
    bct.gsl_free(m)
    bct.gsl_free(deg)
    return rdeg
