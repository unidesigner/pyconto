
from enthought.traits.api import HasTraits, Str, Bool, CBool, Any, Dict, implements, \
      List, Instance, DelegatesTo, List

from basenetwork import BaseNetwork

from util import Node, Edge, Attr


class Network(BaseNetwork):
    
    name = Str
    
    nodes = List(Node)
    
    edges = List(Edge)
    
    def __init__(self, name):
        
        self.name = name
        
    def add_node(self, n, **attr):
        """ How to store bidir node names?
        Add a dictionary and/or a list of parameters
        """
        
        a = Node(n)
        # create Traits attribute for dict
        
        a.attr.trait_set(**attr)
        #for k,v in attr.items():
        #    a.attr.add_trait(k, v)
        
        self.nodes.append(a)
        
        

a = Network('myconnectome')


a.add_node('node1', weight=34, fname='/home/sg/my.py', tup=(3,3))
a.add_node('node2', weight=343)

a.nodes[0].attr.print_traits()

