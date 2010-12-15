package gr.forth.ics.graph;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.PrimaryGraph;
import junit.framework.TestCase;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public class GraphCheckerTest extends TestCase {
    public GraphCheckerTest(String testName) {
        super(testName);
    }

    private Graph g = new PrimaryGraph();

    public void testEmptyIsConnected() {
        assertTrue(GraphChecker.isConnected(g));
    }

    public void testSingleNodeConnected() {
        g.newNode();
        assertTrue(GraphChecker.isConnected(g));
    }

    public void testSingleEdgeConnected1() {
        g.newEdge(g.newNode(), g.newNode());
        assertTrue(GraphChecker.isConnected(g));
    }

    public void testSingleEdgeConnected2() {
        Node n1 = g.newNode(); Node n2 = g.newNode();
        g.newEdge(n2, n1);
        assertTrue(GraphChecker.isConnected(g));
    }

    public void testNotConnected() {
        g.newNode(); g.newNode();
        assertFalse(GraphChecker.isConnected(g));
    }

    public void testStronglyConnected1() {
        Generators.createRandomTree(g, 10, Direction.OUT);
        assertFalse(GraphChecker.isStronglyConnected(g));
    }

    public void testStronglyConnected2() {
        Generators.createRandomBiconnectedGraph(g, 20);
        assertFalse(GraphChecker.isStronglyConnected(g));
    }
}
