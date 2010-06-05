
from enthought.traits.api import HasTraits, Int, Instance, Str

class Attr(HasTraits):
    """ Represents the class for an attribute object
    
    You can add and/or remove traits:
    - real value
    - interval
    - probability density function
    - File
    - etc.
    
    """
    
    pass

class Node(HasTraits):
    
    name = Str
    
    attr = Instance(Attr)
    
    def __init__(self, name):
        
        self.attr = Attr()
        self.name = name
        
        pass
    

class Edge(HasTraits):

    origin = Int
    
    target = Int

    attr = Instance(Attr)
    
    def __init__(self):
        
        pass
    

class HierarchicalNode(Node):
    pass

class HierarchicalEdge(Edge):
    pass

