package examples.basic;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import java.util.List;
import java.util.Set;

/**
 * Demonstrates various features to explore adjacent nodes and incident edges
 *  
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class AdjacentNodes {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();
        Node n1 = g.newNode("1");
        Node n2 = g.newNode("2");
        Node n3 = g.newNode("3");
        Edge e12 = g.newEdge(n1, n2);
        Edge e23 = g.newEdge(n2, n3);
        Edge e31 = g.newEdge(n3, n1);
        
        System.out.println(g.adjacentNodes(n1)); //prints [2, 3]
        System.out.println(g.adjacentNodes(n2)); //prints [1, 3]
        System.out.println(g.adjacentNodes(n3)); //prints [1, 2]
        
        System.out.println(g.edges(n1)); //prints [{1, 2}, {3, 1}]
        System.out.println(g.edges(n2)); //prints [{2, 3}, {1, 2}]
        System.out.println(g.edges(n3)); //prints [{3, 1}, {2, 3}]
        
        List<Node> nodesOutOfN2 = g.adjacentNodes(n2, Direction.OUT).drainToList();
        System.out.println(nodesOutOfN2); //prints [3]
        
        Set<Node> nodesTowardsN2 = g.adjacentNodes(n2, Direction.IN).drainToSet();
        System.out.println(nodesTowardsN2); //prints [1]
        
        //draining to a list or set was not required, just demostrating the ability
        
        System.out.println("e31 incident to e12? " + e31.isIncident(e12)); //prints true
        
        Edge e33 = g.newEdge(n3, n3);
        System.out.println("e12 incident to e33? " + e12.isIncident(e33)); //prints false
        
        Node other = e12.opposite(n1); //other == n2
        
        System.out.println("Is e33 self loop? " + e33.isSelfLoop());
    }
}
