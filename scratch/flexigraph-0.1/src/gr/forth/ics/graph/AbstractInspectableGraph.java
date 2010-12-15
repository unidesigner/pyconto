package gr.forth.ics.graph;

import gr.forth.ics.graph.event.EdgeListener;
import gr.forth.ics.graph.event.GraphEventSupport;
import gr.forth.ics.graph.event.GraphListener;
import gr.forth.ics.graph.event.NodeListener;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.ExtendedListIterable;

abstract class AbstractInspectableGraph implements InspectableGraph, Serializable {
    transient GraphEventSupport graphEventSupport = new GraphEventSupport();
    int edgeCount;
    
    private final Set<Hint> hints = EnumSet.noneOf(Hint.class);
    
    private final Tuple tuple = new TupleImpl();
    
    public void hint(Hint hint) {
        Args.notNull(hint);
        hints.add(hint);
    }
    
    final boolean containsHint(Hint hint) {
        return hints.contains(hint);
    }
    
    public void addGraphListener(GraphListener listener) {
        graphEventSupport.addGraphListener(listener);
    }
    
    public void removeGraphListener(GraphListener listener) {
        graphEventSupport.removeGraphListener(listener);
    }
    
    public void addNodeListener(NodeListener listener) {
        graphEventSupport.addNodeListener(listener);
    }
    
    public void removeNodeListener(NodeListener listener) {
        graphEventSupport.removeNodeListener(listener);
    }
    
    public void addEdgeListener(EdgeListener listener) {
        graphEventSupport.addEdgeListener(listener);
    }
    
    public void removeEdgeListener(EdgeListener listener) {
        graphEventSupport.removeEdgeListener(listener);
    }
    
    public List<NodeListener> getNodeListeners() {
        return graphEventSupport.getNodeListeners();
    }
    
    public List<EdgeListener> getEdgeListeners() {
        return graphEventSupport.getEdgeListeners();
    }
    
    public Tuple tuple() {
        return tuple;
    }
    
    public boolean isEmpty() {
        return nodeCount() == 0;
    }
    
    public boolean areAdjacent(Node n1, Node n2) {
        return edges(n1, n2).iterator().hasNext();
    }
    
    public boolean areAdjacent(Node n1, Node n2, Direction direction) {
        return edges(n1, n2, direction).iterator().hasNext();
    }
    
    public ExtendedListIterable<Edge> edges(Node node) {
        return edges(node, Direction.EITHER);
    }
    
    public ExtendedListIterable<Edge> edges(Node n1, Node n2) {
        return edges(n1, n2, Direction.EITHER);
    }
    
    public ExtendedListIterable<Node> adjacentNodes(Node n) {
        return adjacentNodes(n, Direction.EITHER);
    }
    
    public Edge anEdge() {
        return firstEdge(edges());
    }
    
    public Edge anEdge(Node node) {
        return firstEdge(edges(node));
    }
    
    public Edge anEdge(Node node, Direction direction) {
        return firstEdge(edges(node, direction));
    }
    
    public Edge anEdge(Node n1, Node n2) {
        return firstEdge(edges(n1, n2));
    }
    
    public Edge anEdge(Node n1, Node n2, Direction direction) {
        return firstEdge(edges(n1, n2, direction));
    }
    
    public Node aNode() {
        return firstNode(nodes());
    }
    
    public Node aNode(Node neighbor) {
        return firstNode(adjacentNodes(neighbor));
    }
    
    public Node aNode(Node neighbor, Direction direction) {
        return firstNode(adjacentNodes(neighbor, direction));
    }
    
    private Edge firstEdge(Iterable<Edge> e) {
        Iterator<Edge> edges = e.iterator();
        if (edges.hasNext()) {
            return edges.next();
        }
        throw new NoSuchElementException();
    }
    
    private Node firstNode(Iterable<Node> n) {
        Iterator<Node> nodes = n.iterator();
        if (nodes.hasNext()) {
            return nodes.next();
        }
        throw new NoSuchElementException();
    }
    
    public int degree(Node node) {
        return outDegree(node) + inDegree(node);
    }
    
    public int degree(Node node, Direction direction) {
        switch (direction) {
            case OUT:
                return outDegree(node);
            case IN:
                return inDegree(node);
            default:
                return degree(node);
        }
    }
    
    private void readObject(ObjectInputStream in) throws Exception {
        in.defaultReadObject();
        graphEventSupport = new GraphEventSupport();
    }
    
    public String toString() {
        return Graphs.printPretty(this).toString();
    }
}
