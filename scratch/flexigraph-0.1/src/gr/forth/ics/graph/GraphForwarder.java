package gr.forth.ics.graph;

import gr.forth.ics.graph.Graph.OrderManager;
import java.io.Serializable;
import java.util.Collection;
import gr.forth.ics.util.Args;

public class GraphForwarder extends InspectableGraphForwarder implements Graph, Serializable {
    private final Graph graph;
    
    public GraphForwarder(Graph graph) {
        super(graph);
        this.graph = graph;
    }
    
    @Override
    public Graph getDelegateGraph() {
        return graph;
    }
    
    public Edge newEdge(Node node1, Node node2) {
        return newEdge(node1, node2, null);
    }
    
    public Edge newEdge(Node node1, Node node2, Object value) {
        return graph.newEdge(node1, node2, value);
    }
    
    public boolean removeEdge(Edge edge) {
        return graph.removeEdge(edge);
    }
    
    public int removeEdges(Iterable<Edge> edges) {
        if (edges == null) {
            return 0;
        }
        int count = 0;
        for (Edge e : edges) {
            if (removeEdge(e)) {
                count++;
            }
        }
        return count;
    }
    
    public int removeAllEdges() {
        return removeEdges(edges());
    }
    
    public Node newNode() {
        return newNode(null);
    }
    
    public Node newNode(Object value) {
        return graph.newNode(value);
    }
    
    public Node[] newNodes(int count) {
        Args.gte(count, 0);
        Node[] n = new Node[count];
        for (int i = 0; i < n.length; i++) {
            n[i] = newNode(null);
        }
        return n;
    }
    
    public Node[] newNodes(Object ... values) {
        Node[] n = new Node[values.length];
        for (int i = 0; i < n.length; i++) {
            n[i] = newNode(values[i]);
        }
        return n;
    }
    
    public boolean removeNode(Node node) {
        return graph.removeNode(node);
    }
    
    public int removeNodes(Iterable<Node> nodes) {
        if (nodes == null) {
            return 0;
        }
        int count = 0;
        for (Node n : nodes) {
            if (removeNode(n)) {
                count++;
            }
        }
        return count;
    }
    
    public int removeAllNodes() {
        return removeNodes(nodes());
    }
    
    public boolean isPrimary() {
        return graph.isPrimary();
    }
    
    public boolean reinsertNode(Node n) {
        return graph.reinsertNode(n);
    }
    
    public boolean reinsertEdge(Edge e) {
        return graph.reinsertEdge(e);
    }
    
    public void importGraph(Graph g) {
        graph.importGraph(g);
    }
    
    public Collection<Edge> importGraph(Graph g, Iterable<Node> nodes) {
        return graph.importGraph(g, nodes);
    }
    
    public OrderManager getOrderManager() {
        final OrderManager om = graph.getOrderManager();
        return new OrderManager() {
            public void moveNodeToFront(Node node) {
                om.moveNodeToFront(node);
            }
            
            public void moveNodeToBack(Node node) {
                om.moveNodeToBack(node);
            }
            
            public void moveNodeBefore(Node node, Node beforeWhat) {
                om.moveNodeBefore(node, beforeWhat);
            }
            
            public void moveNodeAfter(Node node, Node afterWhat) {
                om.moveNodeAfter(node, afterWhat);
            }
            
            public void moveEdgeToFront(Edge edge, boolean onSourceNode) {
                om.moveEdgeToFront(edge, onSourceNode);
            }
            
            public void moveEdgeToBack(Edge edge, boolean onSourceNode) {
                om.moveEdgeToBack(edge, onSourceNode);
            }
            
            public void moveEdgeBefore(Edge edge, boolean onSourceNode, Edge beforeWhat) {
                om.moveEdgeBefore(edge, onSourceNode, beforeWhat);
            }
            
            public void moveEdgeAfter(Edge edge, boolean onSourceNode, Edge afterWhat) {
                om.moveEdgeAfter(edge, onSourceNode, afterWhat);
            }
        };
    }
}