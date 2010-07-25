package gr.forth.ics.util;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;

public class Sample {
    static {
        reinitData();
    }
    public static Graph graph;
    public static Node node;
    public static Edge edge;
    
    public static void reinitData() {
        graph = new PrimaryGraph();
        node = graph.newNode();
        edge = graph.newEdge(node, node);
    }
}
