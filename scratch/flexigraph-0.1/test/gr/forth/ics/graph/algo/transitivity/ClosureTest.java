package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.Graphs;
import java.util.Random;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class ClosureTest extends TestCase {
    private final Random random = new Random(0);
    
    public ClosureTest(String testName) {
        super(testName);
    }

    public void testClosureInDag() {
        Graph g = new PrimaryGraph();
        Generators.createRandomDag(g, random, 20, 0.2);
        Closure closure = Transitivity.acyclicClosure(g, SuccessorSetFactory.hashSetBased());

        for (Node n : g.nodes()) {
            Set<Node> reachable = Graphs.collectNodes(g, n, Direction.OUT);
            for (Node n2 : g.nodes()) {
                assertEquals(reachable.contains(n2), closure.successorsOf(n).contains(n2));
                assertEquals(reachable.contains(n2), closure.successorsOf(n).toSet().contains(n2));
            }
        }
    }
}
