package examples.algo;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.algo.transitivity.Closure;
import gr.forth.ics.graph.algo.transitivity.PathFinder;
import gr.forth.ics.graph.algo.transitivity.PathFinders;
import gr.forth.ics.graph.algo.transitivity.SuccessorSetFactory;
import gr.forth.ics.graph.algo.transitivity.Transitivity;
import java.util.Set;

/**
 * Simple example showing working with reachability queries.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class NodeReachability {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();

        Node nA = g.newNode("a");
        Node nB = g.newNode("b");
        Node nC = g.newNode("c");
        Node nD = g.newNode("d");

        //lets create a diamond: a-->{b, c}-->d
        g.newEdge(nA, nB);
        g.newEdge(nA, nC);

        g.newEdge(nB, nD);
        g.newEdge(nC, nD);

        //this implementation answers whether a path exists through navigation
        PathFinder pathFinder = PathFinders.navigational(g);

        System.out.println("Path queries through navigation");
        System.out.println(pathFinder.pathExists(nA, nB)); //prints true
        System.out.println(pathFinder.pathExists(nA, nC)); //prints true
        System.out.println(pathFinder.pathExists(nA, nD)); //prints true

        System.out.println(pathFinder.pathExists(nB, nA)); //prints false
        System.out.println(pathFinder.pathExists(nC, nA)); //prints false
        System.out.println(pathFinder.pathExists(nD, nA)); //prints false

        //A closure can answer both path queries and the successors of each node
        Closure closure = Transitivity.acyclicClosure(g, SuccessorSetFactory.hashSetBased());
        pathFinder = closure;

        System.out.println("Path queries through hashset-based closure");
        //prints the same as above
        System.out.println(pathFinder.pathExists(nA, nB)); //prints true
        System.out.println(pathFinder.pathExists(nA, nC)); //prints true
        System.out.println(pathFinder.pathExists(nA, nD)); //prints true

        System.out.println(pathFinder.pathExists(nB, nA)); //prints false
        System.out.println(pathFinder.pathExists(nC, nA)); //prints false
        System.out.println(pathFinder.pathExists(nD, nA)); //prints false

        //and also this is possible:
        Set<Node> nodes = closure.successorsOf(nA).toSet();
        System.out.println(nodes); //prints {b, c, d} (in some order)
    }
}
