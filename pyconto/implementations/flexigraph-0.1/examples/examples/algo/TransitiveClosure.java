package examples.algo;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.algo.transitivity.Closure;
import gr.forth.ics.graph.algo.transitivity.SuccessorSetFactory;
import gr.forth.ics.graph.algo.transitivity.Transitivity;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.PrimaryGraph;

/**
 * Shows how to create the transitive closure of a graph.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class TransitiveClosure {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();
        Graphs.attachNodeNamer(g); //automatically names nodes with a string value

        //0-->1-->2-->3-->4
        Path path = Generators.createPath(g, 5);

        Closure closure = Transitivity.acyclicClosure(g, SuccessorSetFactory.intervalBased(g));

        //now lets see via the closure object whether node "0" is connected to "4"
        Node n0 = path.headNode();
        Node n4 = path.tailNode();
        boolean pathExists = closure.pathExists(n0, n4);
        System.out.println("Path from 0 to 4? " + pathExists); //prints true

        //lets iterate over *all* the successors of n0
        for (Node successor : closure.successorsOf(n0)) {
            System.out.println(successor + " is a successor of 0!"); //prints a line for nodes 1 to 4
        }

        //this connects every two reachable nodes by an explicit edge, if no such edge already exist
        Transitivity.materialize(g, closure);

        //now there are also these edges in the graph:
        //0-->2, 0-->3, 0-->4
        //1-->3, 1-->4
        //2-->4
        System.out.println(g);
    }
}
