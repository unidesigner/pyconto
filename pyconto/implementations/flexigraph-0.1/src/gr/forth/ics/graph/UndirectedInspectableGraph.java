package gr.forth.ics.graph;

import gr.forth.ics.util.ExtendedListIterable;

class UndirectedInspectableGraph extends InspectableGraphForwarder {
    public UndirectedInspectableGraph(InspectableGraph graph) {
        super(graph);
    }
    
    public boolean areAdjacent(Node n1, Node n2) {
        return inspectableGraph.areAdjacent(n1, n2) || inspectableGraph.areAdjacent(n2, n1);
    }
    
    /*
     * Ignores direction! Calls delegate by Direction.EITHER
     */
    public boolean areAdjacent(Node n1, Node n2, Direction direction) {
        return areAdjacent(n1, n2);
    }
    
    public int inDegree(Node node) {
        return inspectableGraph.degree(node);
    }
    
    public int outDegree(Node node) {
        return inspectableGraph.degree(node);
    }
    
    public int degree(Node node) {
        return inspectableGraph.degree(node);
    }
    
    /*
     * Ignores direction! Calls delegate by Direction.EITHER
     */
    public ExtendedListIterable<Edge> edges(Node node, Direction direction) {
        return inspectableGraph.edges(node, Direction.EITHER);
    }
    
    public ExtendedListIterable<Edge> edges(Node n1, Node n2) {
        return inspectableGraph.edges(n1, n2);
    }
    
    /**
     * Ignores direction! Calls delegate by Direction.EITHER
     **/
    public ExtendedListIterable<Edge> edges(Node n1, Node n2, Direction direction) {
        return inspectableGraph.edges(n1, n2, Direction.EITHER);
    }
}
