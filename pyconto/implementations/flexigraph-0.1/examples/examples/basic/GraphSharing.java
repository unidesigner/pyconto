package examples.basic;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.SecondaryGraph;

/**
 * Graph elements can be freely shared in secondary graphs, but can be contained at most 
 * in one primary graph.
 * <p>
 * This ability avoids creating copies of nodes and edges, and maintaining maps between the copies.
 * Nodes and edges are shared without replication, so their identity is preserved.
 * 
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class GraphSharing {
    public static void main(String[] args) {
        Graph g1 = new PrimaryGraph();
        System.out.println(g1.isPrimary()); //prints true
        
        Edge e = g1.newEdge(g1.newNode("1"), g1.newNode("2"));
        
        SecondaryGraph g2 = new SecondaryGraph();
        System.out.println(g2.isPrimary()); //prints false
        
        //graph elements can be freely shared in secondary graphs, but they can
        //be at most contained in _one_ primary graph. Primary graphs are less
        //flexible, but faster.
        g2.adoptEdge(e); //also adopts the edge's nodes
        
        System.out.println(g2.edgeCount()); //prints 1
        System.out.println(g2.nodeCount()); //prints 2
        
        g2.removeEdge(e); //removes edge, but nodes remain
        
        Edge stillExists = g1.anEdge(); //the edge still exists in the first graph, though removed from the second
        
        g1.newNodes(100); //create some nodes in first graph
        
        g2.adoptGraph(g1); //all those nodes are adopted in the second graph
        
        System.out.println(g2.nodeCount()); //prints 102
    }
}
