package examples.basic;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;

/**
 * Creates a graph, some nodes and an edge, then printing the graph.
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class Simplest {
    public static void main(String[] args) {
        Graph graph = new PrimaryGraph();
        Node n1 = graph.newNode("1");
        Node n2 = graph.newNode("2");
        
        Edge e = graph.newEdge(n1, n2);
        
        System.out.println(graph);
    }
}
