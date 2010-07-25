package examples.algo;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.algo.transitivity.Closure;
import gr.forth.ics.graph.algo.transitivity.SuccessorSetFactory;
import gr.forth.ics.graph.algo.transitivity.Transitivity;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.PrimaryGraph;

/**
 * Example that shows how to create the transitive reduction of a graph.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class TransitiveReduction {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();
        Graphs.attachNodeNamer(g); //automatically names nodes with a string value

        //0-->1-->2-->3-->4
        Path path = Generators.createPath(g, 5);

        //quick way to materialize the closure of the above graph
        Closure closure = Transitivity.acyclicClosure(g, SuccessorSetFactory.intervalBased(g));
        Transitivity.materialize(g, closure);
        //i.e. now there also exist edges {0-->2}, {0-->3}, {0-->4}, {1-->3} etc

        //Now we perform transitive reduction of the graph. We need an object that answers (fast!)
        //whether two nodes are connected. We reuse the computed closure from above
        Transitivity.acyclicReduction(g, closure);

        System.out.println(g); //Prints the graph, which now has only 4 edges, the initial ones
    }
}
