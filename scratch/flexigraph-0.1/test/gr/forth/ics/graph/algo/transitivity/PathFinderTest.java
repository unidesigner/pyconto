package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.Graphs;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class PathFinderTest extends TestCase {
    private final Random random = new Random(0);

    public PathFinderTest(String testName) {
        super(testName);
    }

    public void testNavigationalInDag() {
        Graph g = new PrimaryGraph();
        Generators.createRandomDag(g, random, 20, 0.2);
        PathFinder pathFinder = PathFinders.navigational(g);

        for (Node n : g.nodes()) {
            Set<Node> reachable = Graphs.collectNodes(g, n, Direction.OUT);
            for (Node n2 : g.nodes()) {
                assertEquals(reachable.contains(n2), pathFinder.pathExists(n, n2));
            }
        }
    }

    public void testNavigationalInGeneralGraph() {
        Graph g = new PrimaryGraph();
        Generators.createRandom(g, random, 20, 0.2);
        PathFinder pathFinder = PathFinders.navigational(g);

        for (Node n : g.nodes()) {
            Set<Node> reachable = Graphs.collectNodes(g, n, Direction.OUT);
            for (Node n2 : g.nodes()) {
                assertEquals(reachable.contains(n2), pathFinder.pathExists(n, n2));
            }
        }
    }
}
