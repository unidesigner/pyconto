package examples.basic;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;

/**
 * Creates some nodes and edges and loops over them.
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class LoopOverNodesAndEdges {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();
        g.newNodes("1", "2", "3", "4", 999);
        
        for (Node n : g.nodes()) {
            System.out.println(n); //prints 1, 2, etc
        }
        
        Node n1 = g.newNode("n1");
        Node n2 = g.newNode("n2");
        Edge edge1 = g.newEdge(n1, n2);
        Edge edge3 = g.newEdge(n1, n1);
        Edge edge2 = g.newEdge(n2, n1);
        Edge edge4 = g.newEdge(n2, n2);
        
        for (Edge edge : g.edges()) {
            System.out.println(edge); //prints {n1, n2}, {n1, n1}, etc
        }
    }
}
