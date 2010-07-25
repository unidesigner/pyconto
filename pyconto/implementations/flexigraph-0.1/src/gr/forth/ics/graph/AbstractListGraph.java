package gr.forth.ics.graph;

import java.util.ListIterator;
import gr.forth.ics.util.Accessor;
import gr.forth.ics.graph.event.GraphEvent;
import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;
import gr.forth.ics.util.AbstractCompoundListIterator;
import gr.forth.ics.util.FastLinkedList;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.CompoundListIterator;
import gr.forth.ics.util.ExtendedListIterable;
import gr.forth.ics.util.Filter;
import gr.forth.ics.util.FilteringListIterator;

//TODO: find (node, node) implementation should be more configurable...
//TODO: more thorough test (randomized? so that iterators are always in sync)
abstract class AbstractListGraph extends AbstractGraph {
    final FastLinkedList<NodeImpl> nodes = new FastLinkedList<NodeImpl>();
    
    private SoftReference<List<Node>> nodesCache;
    private SoftReference<List<Edge>> edgesCache;
    
    abstract void setNodeRef(NodeImpl node, Accessor<NodeImpl> ref);
    abstract void setEdgeOutRef(EdgeImpl edge, Accessor<EdgeImpl> ref);
    abstract void setEdgeInRef(EdgeImpl edge, Accessor<EdgeImpl> ref);
    abstract Accessor<NodeImpl> getNodeRef(NodeImpl node);
    abstract Accessor<EdgeImpl> getEdgeOutRef(EdgeImpl edge);
    abstract Accessor<EdgeImpl> getEdgeInRef(EdgeImpl edge);
    abstract void removeNodeRef(NodeImpl node);
    abstract void removeEdgeRefs(EdgeImpl edge);
    
    abstract FastLinkedList<EdgeImpl> getOutEdges(NodeImpl node);
    abstract FastLinkedList<EdgeImpl> getInEdges(NodeImpl node);
    
    abstract void initNode(NodeImpl node);
    
    //TODO: clearly document that nodes can only be created here. No other node than the supplied internal
    //type (NodeImpl) will work. The same for Edge/EdgeImpl.
    public Node newNode(Object value) {
        final NodeImpl node = new NodeImpl(value);
        graphEventSupport.fire(new GraphEvent(this, GraphEvent.Type.NODE_ADDED, node) , new Runnable() {
            public void run() {
                initNode(node);
                setNodeRef(node, nodes.addLast(node));
                nodesCache = null;
            }
        });
        return node;
    }
    
    public Edge newEdge(Node n1, Node n2, final Object value) {
        Args.notNull(n1, n2);
        final NodeImpl node1 = checkContainedAndCast(n1);
        final NodeImpl node2 = checkContainedAndCast(n2);
        final EdgeImpl edge = new EdgeImpl(node1, node2, value);
        graphEventSupport.fire(new GraphEvent(this, GraphEvent.Type.EDGE_ADDED, edge), new Runnable() {
            public void run() {
                edgeCount++;
                Accessor<EdgeImpl> outRef = getOutEdges(node1).addLast(edge);
                Accessor<EdgeImpl> inRef = getInEdges(node2).addLast(edge);
                setEdgeOutRef(edge, outRef);
                setEdgeInRef(edge, inRef);
                edgesCache = null;
            }
        });
        return edge;
    }
    
    public int nodeCount() {
        return nodes.size();
    }
    
    public int edgeCount() {
        return edgeCount;
    }
    
    private NodeImpl checkContainedAndCast(Node node) {
        if (!containsNode(node)) {
            throw new IllegalArgumentException("Node " + node + " not contained in graph");
        }
        return (NodeImpl)node;
    }
    
    private EdgeImpl checkContainedAndCast(Edge edge) {
        if (!containsEdge(edge)) {
            throw new IllegalArgumentException("Edge " + edge + " not contained in graph");
        }
        return (EdgeImpl)edge;
    }
    
    final ExtendedListIterable<Node> iterableNodes(final FastLinkedList<NodeImpl> seq, int expectedSize) {
        return new ExtendedListIterable<Node>(expectedSize) {
            protected ListIterator<Node> listIteratorImpl() {
                return new ListIterator<Node>() {
                    final ListIterator<Node> iter = castNodes(seq).listIterator(0);
                    Node last;
                    int index = 0;
                    
                    public boolean hasNext() {
                        return iter.hasNext();
                    }
                    
                    public Node next() {
                        last = iter.next();
                        index++; //only if no exception is thrown
                        return last;
                    }
                    
                    public void remove() {
                        if (last == null) {
                            throw new IllegalStateException();
                        }
                        removeNode(last);
                        last = null;
                    }
                    
                    public void set(Node node) {
                        throw new UnsupportedOperationException();
                    }
                    
                    public void add(Node node) {
                        throw new UnsupportedOperationException();
                    }
                    
                    public Node previous() {
                        Node n = iter.previous();
                        index--; //only if no exception is thrown
                        return n;
                    }
                    
                    public boolean hasPrevious() {
                        return iter.hasPrevious();
                    }
                    
                    public int nextIndex() {
                        return index;
                    }
                    
                    public int previousIndex() {
                        return index - 1;
                    }
                };
            }
        };
    }
    
    //This method is unsafe, but it's used only to create
    //read-only views (no additions), so there is no problem
    @SuppressWarnings("unchecked")
    private static List<Node> castNodes(List<NodeImpl> nodes) {
        List list = nodes;
        return (List<Node>)list;
    }
    
    //This method is unsafe, but it's used only to create
    //read-only views (no additions), so there is no problem
    @SuppressWarnings("unchecked")
    private static List<Edge> castEdges(List<EdgeImpl> edges) {
        List list = edges;
        return (List<Edge>)list;
    }
    
    public ExtendedListIterable<Node> nodes() {
        if (containsHint(Hint.FAST_NODE_ITERATION)) {
            List<Node> nodesList = null;
            if (nodesCache != null) {
                nodesList = nodesCache.get();
            }
            if (nodesList == null) {
                nodesList = new LinkedList<Node>();
                try {
                    for (Node n : nodes) {
                        nodesList.add(n);
                    }
                } catch (OutOfMemoryError e) {
                    return iterableNodes(nodes, nodeCount());
                }
                nodesCache = new SoftReference<List<Node>>(nodesList);
            }
            final List<Node> finalNodesList = nodesCache.get();
            if (finalNodesList != null) {
                return new ExtendedListIterable<Node>(nodes.size()) {
                    protected ListIterator<Node> listIteratorImpl() {
                        return new DelegateListIterator<Node>(finalNodesList.listIterator()) {
                            public void remove() {
                                removeNode(last);
                                last = null;
                            }
                        };
                    }
                };
            }
        }
        
        return iterableNodes(nodes, nodeCount());
    }
    
    public ExtendedListIterable<Node> adjacentNodes(final Node n, final Direction direction) {
        Args.notNull(n, direction);
        return new ExtendedListIterable<Node>(degree(n, direction)) {
            protected ListIterator<Node> listIteratorImpl() {
                return new ListIterator<Node>() {
                    final ListIterator<Edge> incidentEdges = edges(n, direction).listIterator();
                    Node last;
                    public boolean hasNext() {
                        return incidentEdges.hasNext();
                    }
                    
                    public Node next() {
                        return last = incidentEdges.next().opposite(n);
                    }
                    
                    public boolean hasPrevious() {
                        return incidentEdges.hasPrevious();
                    }
                    
                    public Node previous() {
                        return last = incidentEdges.previous().opposite(n);
                    }
                    
                    public void remove() {
                        if (last == null) {
                            throw new IllegalStateException();
                        }
                        removeNode(last);
                        last = null;
                    }
                    
                    public void set(Node n) {
                        throw new UnsupportedOperationException();
                    }
                    
                    public void add(Node n) {
                        throw new UnsupportedOperationException();
                    }
                    
                    public int previousIndex() {
                        return incidentEdges.previousIndex();
                    }
                    
                    public int nextIndex() {
                        return incidentEdges.nextIndex();
                    }
                };
            }
        };
    }
    
    public boolean removeEdge(Edge edge) {
        if (!containsEdge(edge)) {
            return false;
        }
        final EdgeImpl e = (EdgeImpl)edge;
        graphEventSupport.fire(new GraphEvent(this, GraphEvent.Type.EDGE_REMOVED, edge), new Runnable() {
            public void run() {
                NodeImpl n1 = (NodeImpl)e.n1();
                NodeImpl n2 = (NodeImpl)e.n2();
                getEdgeOutRef(e).remove();
                getEdgeInRef(e).remove();
                removeEdgeRefs(e);
                edgeCount--;
                edgesCache = null;
            }
        });
        return true;
    }
    
    public boolean removeNode(final Node node) {
        if (!containsNode(node)) {
            return false;
        }
        graphEventSupport.fire(new GraphEvent(this, GraphEvent.Type.NODE_REMOVED, node), new Runnable() {
            public void run() {
                NodeImpl n = (NodeImpl)node;
                for (Edge e : edges(node)) {
                    removeEdge(e);
                }
                getNodeRef(n).remove();
                removeNodeRef(n);
                edgesCache = null;
                nodesCache = null;
            }
        });
        return true;
    }
    
    public ExtendedListIterable<Edge> edges(Node node, final Direction direction) {
        Args.notNull(direction);
        final NodeImpl n = checkContainedAndCast(node);
        return new ExtendedListIterable<Edge>(degree(node, direction)) {
            protected ListIterator<Edge> listIteratorImpl() {
                return new CompoundListIterator<Edge>(
                        direction.isOut() ? castEdges(getOutEdges(n)).listIterator() : null,
                        direction.isIn() ? castEdges(getInEdges(n)).listIterator() : null
                        );
            }
        };
    }
    
//TODO: slow, typically
    public ExtendedListIterable<Edge> edges(final Node n1, final Node n2, final Direction direction) {
        Args.notNull(direction);
        final NodeImpl node1 = checkContainedAndCast(n1);
        final NodeImpl node2 = checkContainedAndCast(n2);
        Direction flip = direction.flip();
        if (degree(n1, direction) > (degree(n2, flip)) && n1 != n2) {
            return edges(n2, n1, flip);
        }
        final ListIterator<Edge> outIterator = (!direction.isOut() ? null :
            (outDegree(n1) < inDegree(n2) ?
                castEdges(getOutEdges(node1)).listIterator() :
                castEdges(getInEdges(node2)).listIterator()));
        final ListIterator<Edge> inIterator = (!direction.isIn() ? null :
            (inDegree(n1) < outDegree(n2) ?
                castEdges(getInEdges(node1)).listIterator() :
                castEdges(getOutEdges(node2)).listIterator()));
        return new ExtendedListIterable<Edge>() {
            protected ListIterator<Edge> listIteratorImpl() {
                return new FilteringListIterator<Edge>(new CompoundListIterator<Edge>(outIterator, inIterator), new Filter<Edge>() {
                    public boolean accept(Edge e) {
                        return e.isIncident(n1) && e.opposite(n1) == n2;
                    }
                });
            }
        };
    }
    
    public boolean containsEdge(Edge edge) {
        if (edge == null) {
            return false;
        }
        try {
            EdgeImpl e = (EdgeImpl)edge;
            NodeImpl n1 = e.n1;
            return containsNode(n1) && getOutEdges(n1).ownsAccessor(getEdgeOutRef(e));
        } catch (RuntimeException ex) {
            return false;
        }
    }
    
    public boolean containsNode(Node node) {
        if (node == null) {
            return false;
        }
        try {
            NodeImpl n = (NodeImpl)node;
            return nodes.ownsAccessor(getNodeRef(n));
        } catch (RuntimeException ex) {
            return false;
        }
    }
    
    public int inDegree(Node node) {
        NodeImpl n = checkContainedAndCast(node);
        return getInEdges(n).size();
    }
    
    public int outDegree(Node node) {
        NodeImpl n = checkContainedAndCast(node);
        return getOutEdges(n).size();
    }
    
    public int degree(Node node) {
        NodeImpl n = checkContainedAndCast(node);
        return getOutEdges(n).size() + getInEdges(n).size();
    }
    
    public ExtendedListIterable<Edge> edges() {
        if (containsHint(Hint.FAST_EDGE_ITERATION)) {
            List<Edge> edgesList = null;
            if (edgesCache != null) {
                edgesList = edgesCache.get();
            }
            if (edgesList == null) {
                edgesList = new LinkedList<Edge>();
                try {
                    int pos = 0;
                    for (Edge e : edgesImpl()) {
                        edgesList.add(e);
                    }
                } catch (OutOfMemoryError e) {
                    return edgesImpl();
                }
                edgesCache = new SoftReference<List<Edge>>(edgesList);
            }
            
            final List<Edge> edgeList = edgesCache.get();
            if (edgeList != null) {
                return new ExtendedListIterable<Edge>(edgeList.size()) {
                    protected ListIterator<Edge> listIteratorImpl() {
                        return new DelegateListIterator<Edge>(edgeList.listIterator()) {
                            public void remove() {
                                super.remove();
                                removeEdge(last);
                                last = null;
                            }
                        };
                    }
                };
            }
        }
        
        return edgesImpl();
    }
    
    private ExtendedListIterable<Edge> edgesImpl() {
        return new ExtendedListIterable<Edge>(edgeCount()) {
            protected ListIterator<Edge> listIteratorImpl() {
                return new AbstractCompoundListIterator<Edge>() {
                    private Direction direction = null; //keeps the last direction of this iterator
                    
                    final ListIterator<Node> nodeCursor = nodes().listIterator();
                    
                    protected boolean hasNextIterator() {
                        if (direction == Direction.IN) {
                            nodeCursor.next();
                            direction = Direction.OUT;
                        }
                        return nodeCursor.hasNext();
                    }
                    
                    protected boolean hasPreviousIterator() {
                        if (direction == Direction.OUT) {
                            nodeCursor.previous();
                            direction = Direction.IN;
                        }
                        return nodeCursor.hasPrevious();
                    }
                    
                    protected ListIterator<Edge> nextIterator() {
                        direction = Direction.OUT;
                        return getEdgesOfNode(nodeCursor.next(), true);
                    }
                    
                    protected ListIterator<Edge> previousIterator() {
                        direction = Direction.IN;
                        return getEdgesOfNode(nodeCursor.previous(), false);
                    }
                    
                    private ListIterator<Edge> getEdgesOfNode(Node node, boolean start) {
                        NodeImpl n = (NodeImpl)node;
                        int index = start ? 0 : outDegree(n);
                        return castEdges(getOutEdges(n)).listIterator(index);
                    }
                    
                    @Override public void remove() {
                        removeEdge(last);
                        last = null;
                    }
                    
                    @Override public void add(Edge e) {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override public void set(Edge e) {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    public OrderManager getOrderManager() {
        return new OrderManagerImpl();
    }
    
    private static abstract class DelegateListIterator<E> implements ListIterator<E> {
        final ListIterator<E> delegate;
        
        DelegateListIterator(ListIterator<E> delegate) {
            this.delegate = delegate;
        }
        
        protected E last = null;
        
        public void set(E o) {
            throw new UnsupportedOperationException();
        }
        
        public void add(E o) {
            throw new UnsupportedOperationException();
        }
        
        public void remove() {
            if (last == null) {
                throw new IllegalStateException();
            }
        }
        
        public int previousIndex() {
            return delegate.previousIndex();
        }
        
        public E previous() {
            return last = delegate.previous();
        }
        
        public int nextIndex() {
            return delegate.nextIndex();
        }
        
        public E next() {
            return last = delegate.next();
        }
        
        public boolean hasPrevious() {
            return delegate.hasPrevious();
        }
        
        public boolean hasNext() {
            return delegate.hasNext();
        }
    }
    
    private class OrderManagerImpl implements OrderManager {
        public void moveNodeToFront(Node node) {
            NodeImpl n = checkContainedAndCast(node);
            getNodeRef(n).moveToFront();
            nodesCache = null;
            graphEventSupport.fire(new GraphEvent(AbstractListGraph.this, GraphEvent.Type.NODE_REORDERED, node));
        }
        
        public void moveNodeToBack(Node node) {
            NodeImpl n = checkContainedAndCast(node);
            getNodeRef(n).moveToBack();
            nodesCache = null;
            graphEventSupport.fire(new GraphEvent(AbstractListGraph.this, GraphEvent.Type.NODE_REORDERED, node));
        }
        
        public void moveNodeBefore(Node node, Node beforeWhat) {
            NodeImpl n = checkContainedAndCast(node);
            NodeImpl before = checkContainedAndCast(beforeWhat);
            getNodeRef(n).moveBefore(getNodeRef(before));
            nodesCache = null;
            graphEventSupport.fire(new GraphEvent(AbstractListGraph.this, GraphEvent.Type.NODE_REORDERED, node));
        }
        
        public void moveNodeAfter(Node node, Node afterWhat) {
            NodeImpl n = checkContainedAndCast(node);
            NodeImpl after = checkContainedAndCast(afterWhat);
            getNodeRef(n).moveAfter(getNodeRef(after));
            nodesCache = null;
            graphEventSupport.fire(new GraphEvent(AbstractListGraph.this, GraphEvent.Type.NODE_REORDERED, node));
        }
        
        public void moveEdgeToFront(Edge edge, boolean onSourceNode) {
            EdgeImpl e = checkContainedAndCast(edge);
            NodeImpl n;
            Accessor<EdgeImpl> ref;
            FastLinkedList<EdgeImpl> seq;
            if (onSourceNode) {
                n = e.n1();
                seq = getOutEdges(n);
                ref = getEdgeOutRef(e);
            } else {
                n = e.n2();
                seq = getInEdges(n);
                ref = getEdgeInRef(e);
            }
            ref.moveToFront();
            edgesCache = null;
            graphEventSupport.fire(new GraphEvent(AbstractListGraph.this, GraphEvent.Type.EDGE_REORDERED, edge));
        }
        
        public void moveEdgeToBack(Edge edge, boolean onSourceNode) {
            EdgeImpl e = checkContainedAndCast(edge);
            NodeImpl n;
            Accessor<EdgeImpl> ref;
            FastLinkedList<EdgeImpl> seq;
            if (onSourceNode) {
                n = e.n1();
                seq = getOutEdges(n);
                ref = getEdgeOutRef(e);
            } else {
                n = e.n2();
                seq = getInEdges(n);
                ref = getEdgeInRef(e);
            }
            ref.moveToBack();
            edgesCache = null;
            graphEventSupport.fire(new GraphEvent(AbstractListGraph.this, GraphEvent.Type.EDGE_REORDERED, edge));
        }
        
        public void moveEdgeBefore(Edge edge, boolean onSourceNode, Edge beforeWhat) {
            EdgeImpl e = checkContainedAndCast(edge);
            EdgeImpl before = checkContainedAndCast(beforeWhat);
            NodeImpl n;
            Accessor<EdgeImpl> ref;
            Accessor<EdgeImpl> ref2;
            FastLinkedList<EdgeImpl> seq;
            if (onSourceNode) {
                n = e.n1();
                seq = getOutEdges(n);
                ref = getEdgeOutRef(e);
                ref2 = getEdgeOutRef(before);
            } else {
                n = e.n2();
                seq = getInEdges(n);
                ref = getEdgeInRef(e);
                ref2 = getEdgeInRef(before);
            }
            ref.moveBefore(ref2);
            edgesCache = null;
            graphEventSupport.fire(new GraphEvent(AbstractListGraph.this, GraphEvent.Type.EDGE_REORDERED, edge));
        }
        
        public void moveEdgeAfter(Edge edge, boolean onSourceNode, Edge afterWhat) {
            EdgeImpl e = checkContainedAndCast(edge);
            EdgeImpl after = checkContainedAndCast(afterWhat);
            NodeImpl n;
            Accessor<EdgeImpl> ref;
            Accessor<EdgeImpl> ref2;
            FastLinkedList<EdgeImpl> seq;
            if (onSourceNode) {
                n = e.n1();
                seq = getOutEdges(n);
                ref = getEdgeOutRef(e);
                ref2 = getEdgeOutRef(after);
            } else {
                n = e.n2();
                seq = getInEdges(n);
                ref = getEdgeInRef(e);
                ref2 = getEdgeInRef(after);
            }
            ref.moveAfter(ref2);
            edgesCache = null;
            graphEventSupport.fire(new GraphEvent(AbstractListGraph.this, GraphEvent.Type.EDGE_REORDERED, edge));
        }
    }
}
