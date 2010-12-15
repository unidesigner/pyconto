package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.path.Paths;
import gr.forth.ics.graph.GraphChecker;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.PrimaryGraph;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class GeneratorsTest extends TestCase {
    
    public GeneratorsTest(String testName) {
        super(testName);
    }            

    public void testCreateRandomTree() {
        Direction[] directions = { Direction.IN, Direction.OUT };
        for (Direction direction : directions) {
            Graph g = new PrimaryGraph();
            Generators.createRandomTree(g, 100, direction);
            assertEquals(g.nodeCount() - 1, g.edgeCount());
            for (Node n : g.nodes()) {
                assertTrue(g.degree(n, direction.flip()) <= 1);
            }
        }
    }

    public void testCreateRandomBiconnectedGraph() {
        for (int nodes : new int[] { 1, 2, 3, 5, 10, 20}) {
            Graph g = new PrimaryGraph();
            Generators.createRandomBiconnectedGraph(g, nodes);
            InspectableGraph ug = Graphs.undirected(g);
            assertEquals(nodes, g.nodeCount());
            for (Node n : g.nodes().drainToList()) {
                //test by removing each node (the graph should remain connected)
                //and find a path connecting every pair of nodes
                List<Edge> edges = g.edges(n).drainToList();
                g.removeNode(n);
                for (final Node n1 : g.nodes()) {
                    for (final Node n2 : g.nodes()) {
                        if (n1 != n2) {
                            assertNotNull(Paths.findPath(ug, n1, n2, Direction.OUT));
                        } else {
                            assertTrue(g.degree(n1) > 0 || g.edgeCount() == 0);
                        }
                    }
                }
                g.reinsertNode(n);
                for (Edge e : edges) {
                    g.reinsertEdge(e);
                }
            }
        }
    }

    public void testRandomBiconnectedGraph() {
        Graph g = new PrimaryGraph();
        Generators.createRandomBiconnectedGraph(g, 10);
        assertTrue(GraphChecker.isBiconnected(g));
    }

    public void testFoldedGrid() {
        Random random = new Random();
        for (int dimensions = 2; dimensions < 5; dimensions++) {
            Graph g = new PrimaryGraph();
            int[] array = new int[dimensions];
            for (int i = 0; i < array.length; i++) {
                array[i] = 1 + random.nextInt(10);
            }
            Generators.createFoldedGrid(g, array);
            for (Node n : g.nodes()) {
                assertEquals(dimensions, g.outDegree(n));
            }
        }
    }
}
