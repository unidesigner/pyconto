""" Testing the dynamic network functionality
"""

# create an attribute network

anet = AttrNetwork(implementation='networkx', directed = True)

anet.add_node('n1', my="attribute")
anet.add_node('n2', other=123)
anet.add_node('n3', money=1000)

anet.add_edge('n1', 'n2', more="data")

# create a dynamic network giving its initial structure
dnet = DynamicNetwork(init=anet, structure='continous/discrete OR homogenous/complex')

# create first history that represents the state transisions of the dynamic network

# ? history can be homogenous time series! not the dynamic network per se
hist1 = History()

# define the state transition of one time step
st1 = StateTrans()

def func1(value):
    return value + 100

# change node attributes (plural?)
st1.add_change_attributes( [('n2', other, func1), ('n3', money, func1), ('n2', blubb, func1)] )
# adding nodes
st1.add_add_nodes( ('n123', {a:'aa', b:23}) )
# adding edges
st1.add_add_edges( ('n3', 'n123', {edgeattr:4321}) )
# removing nodes
st1.add_remove_nodes( ['n3', 'n122'] )

# internal consistency check of a state transition, yield an exception
# if an invalid operation was made (but: can't be sure which nodes exist in the network for which
# the history with this state transition will be applied)

# add state transition to history
hist1.append(st1)

# add a second state transision
st2 = StateTrans()
st2.add_add_nodes( [ ('n4', 'n1')] ) # this adds an existing node
hist1.append(st2)

hist2 = History('homogenous')

# add time series for nodes
hist2.add_darray( ['n1', 'n2'], data = np.array([[1,2,3],[4,5,6]]), axis = 0 )
# add time series for edges (how to differentiate directionality?)
hist2.add_darray( [ ('n1', 'n2')], data = np.array([[6,4,3]]), axis = 0) # what about different lenghts?
# check names axis class by Fernando

# validate history
dnet.validate_history(hist1)
dnet.validate_history(hist2)

# add histories

dnet.add_history('bigbang', hist1)
# consistency check for application of the history to the network
# e.g. can't remove nodes that do not exist
dnet.add_history('timepasses', hist2)