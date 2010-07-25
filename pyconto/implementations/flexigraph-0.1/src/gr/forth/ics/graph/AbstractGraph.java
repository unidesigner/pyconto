package gr.forth.ics.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.ExtendedIterable;

//All optional methods throw UnsupportedOperationException, isPrimary returns false
abstract class AbstractGraph extends AbstractInspectableGraph implements Graph {
    public int removeEdges(Iterable<Edge> edges) {
        if (edges == null) {
            return 0;
        }
        int removed = 0;
        for (Edge e : edges) {
            if (removeEdge(e)) {
                removed++;
            }
        }
        return removed;
    }
    
    public Node[] newNodes(int count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }
        Node[] ids = new Node[count];
        for (int i = 0; i < count; ++i) {
            ids[i] = newNode(null);
        }
        return ids;
    }
    
    public Node newNode() {
        return newNode(null);
    }
    
    public Edge newEdge(Node n1, Node n2) {
        return newEdge(n1, n2, null);
    }
    
    public Node[] newNodes(Object ... values) {
        Node[] ids = new Node[values.length];
        for (int i = 0; i < values.length; ++i) {
            ids[i] = newNode(values[i]);
        }
        return ids;
    }
    
    public int removeNodes(Iterable<Node> nodes) {
        if (nodes == null) {
            return 0;
        }
        int removed = 0;
        for (Node node : nodes) {
            if (removeNode(node)) {
                removed++;
            }
        }
        return removed;
    }
    
    public int removeAllEdges() {
        int countEdges = edgeCount();
        for (Edge e : edges()) {
            removeEdge(e);
        }
        return countEdges;
    }
    
    public int removeAllNodes() {
        int countNodes = nodeCount();
        for (Node n : nodes()) {
            removeNode(n);
        }
        return countNodes;
    }
    
    public boolean isPrimary() {
        return false;
    }
    
    public void importGraph(Graph g) {
        importGraph(g, g.nodes());
    }
    
    public Collection<Edge> importGraph(Graph g, Iterable<Node> nodes) {
        Args.notNull(g);
        Args.notNull(nodes);
        Collection<Edge> innerEdges = new LinkedList<Edge>();
        Collection<Edge> interEdges = new LinkedList<Edge>();
        Collection<Node> importedNodes = ExtendedIterable.wrap(nodes).drainTo(new LinkedList<Node>());
        for (Node n : importedNodes) {
            if (!g.containsNode(n)) {
                throw new IllegalArgumentException("Node not in imported graph");
            }
            for (Edge e : g.edges(n)) {
                if (importedNodes.contains(e.n1()) && importedNodes.contains(e.n2())) {
                    //inner edge
                    innerEdges.add(e);
                } else {
                    //interedge!
                    interEdges.add(e);
                }
            }
            g.removeNode(n);
        }
        for (Node n : importedNodes) {
            reinsertNode(n);
        }
        for (Edge inner : innerEdges) {
            reinsertEdge(inner);
        }
        return interEdges;
    }
}
