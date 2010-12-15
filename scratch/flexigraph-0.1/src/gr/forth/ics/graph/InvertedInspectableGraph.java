package gr.forth.ics.graph;

import gr.forth.ics.util.ExtendedListIterable;

class InvertedInspectableGraph extends InspectableGraphForwarder {
    public InvertedInspectableGraph(InspectableGraph graph) {
        super(graph);
    }
    
    public ExtendedListIterable<Edge> edges(Node node, Direction direction) {
        return inspectableGraph.edges(node, direction.flip());
    }
    
    public ExtendedListIterable<Edge> edges(Node n1, Node n2, Direction direction) {
        return inspectableGraph.edges(n1, n2, direction.flip());
    }
    
    public boolean areAdjacent(Node n1, Node n2, Direction direction) {
        return inspectableGraph.areAdjacent(n1, n2, direction.flip());
    }
    
    public int inDegree(Node node) {
        return inspectableGraph.outDegree(node);
    }
    
    public int outDegree(Node node) {
        return inspectableGraph.inDegree(node);
    }
    
    public ExtendedListIterable<Node> adjacentNodes(Node node, Direction direction) {
        return inspectableGraph.adjacentNodes(node, direction.flip());
    }
}
