import unittest

import numpy as np

import pyconto.algorithms.bct.measures as bm

cm = np.random.random( (10,10) )

class TestMeasures(unittest.TestCase):

    def test_degree(self):
        all, ain, aout = bm.degree(cm, True)
        print all
        print ain
        print aout

    def test_edge_betweenness(self):
        ebc, bc = bm.edge_betweenness(cm, True)
        print ebc
        print bc
        
    def test_findpaths(self):
        out = bm.findpaths(cm, np.array([0,1,3]), 1)
        
if __name__ == '__main__':
    unittest.main()
