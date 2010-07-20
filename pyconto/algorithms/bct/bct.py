""" API for Brain Connectivity Toolbox with automatic conversion of
NumPy arrays and memory management.

Packages
--------

datasets : publicly available brain connectivity datasets based on tracer injection

generators : generator functions for networkx

operations : operations on networks, e.g. thresholding

measures : network measures shipped with the brain connectivity toolbox 

"""

import sys
# check for linux support
if sys.platform == 'linux2':   
    # examine the architecture
    import platform as pf
    if '32' in pf.architecture()[0]:
        from bit32.bct import *
    elif '64' in pf.architecture()[0]:
        from bit64.bct import *
    else:
        raise('Can not determine your architecture settings.')
else:
    raise('Your platform is not supported.')
        