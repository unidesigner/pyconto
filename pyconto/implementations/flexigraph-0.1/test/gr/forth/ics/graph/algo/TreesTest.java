package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import junit.framework.*;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.algo.Trees.DiameterFinder;
import gr.forth.ics.graph.algo.Trees.NodeLevelFinder;
import gr.forth.ics.graph.algo.Trees.SubtreeAnalyzer;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.path.Paths;
import gr.forth.ics.graph.path.Traverser;
import java.util.Arrays;
import java.util.Random;

public class TreesTest extends TestCase {
    
    public TreesTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(TreesTest.class);
        
        return suite;
    }

    public void testFindTreeCenter() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2, 3, 4, 5, 6, 7);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[0], n[2]);
        g.newEdge(n[1], n[3]);
        g.newEdge(n[1], n[4]);
        g.newEdge(n[1], n[5]);
        g.newEdge(n[3], n[6]);
        g.newEdge(n[3], n[7]);
        Node center = Trees.findCenter(g);
        assertEquals(n[1], center);
    }

    public void testNodeLevelFinder_Randomized() {
        for (Direction direction : new Direction[] { Direction.OUT, Direction.IN }) {
            Graph g = new PrimaryGraph();
            Graphs.attachNodeNamer(g);
            Node root = Generators.createRandomTree(g, 10, direction);
            NodeLevelFinder finder = Trees.findNodeLevels(g, root, direction);

            for (Node n : g.nodes()) {
                Path pathFromRoot = Paths.findPath(g, root, n, direction);
                int maxAbove = pathFromRoot.size();
                Node parent = pathFromRoot.edgeCount() > 0 ?
                    pathFromRoot.getNode(pathFromRoot.nodeCount() - 2) : null;

                int maxBelow = 0;
                for (Path path : Traverser.newDfs().notRepeatingEdges().build().traverse(g, n, direction)) {
                    if (path.tailNode() == parent) {
                        continue;
                    }
                    maxBelow = Math.max(maxBelow, path.size());
                }

                assertEquals(maxAbove, finder.getLayersAbove(n));
                assertEquals(maxBelow, finder.getLayersBelow(n));
            }
        }
    }
    
    public void testFindDiameter() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[0], n[2]);
        g.newEdge(n[1], n[3]);
        g.newEdge(n[1], n[4]);
        g.newEdge(n[3], n[5]);
        g.newEdge(n[4], n[6]);
        g.newEdge(n[5], n[7]);
        g.newEdge(n[6], n[8]);
        g.newEdge(n[1], n[9]);
        g.newEdge(n[9], n[10]);
        DiameterFinder finder = Trees.findDiameter(g, n[0], Direction.OUT);
        assertEquals(7, finder.diameterOf(n[0]));
        assertEquals(7, finder.diameterOf(n[1]));
        assertEquals(1, finder.diameterOf(n[2]));
        assertEquals(3, finder.diameterOf(n[3]));
        assertEquals(3, finder.diameterOf(n[4]));
        assertEquals(2, finder.diameterOf(n[5]));
        assertEquals(2, finder.diameterOf(n[6]));
        assertEquals(1, finder.diameterOf(n[7]));
        assertEquals(1, finder.diameterOf(n[8]));

        assertEquals(7, Trees.findDiameter(g));
    }

    public void testNodeLevelFinder_NonTree() {
        Graph g = new PrimaryGraph();
        Node n1 = g.newNode("1");
        Node n2 = g.newNode("2");
        NodeLevelFinder finder = Trees.findNodeLevels(g, n1, Direction.OUT);

        assertEquals(0, finder.getLayersAbove(n1));
        assertEquals(0, finder.getLayersBelow(n1));

        try {
            finder.getLayersAbove(n2);
            fail();
        } catch (IllegalArgumentException ok) { }
        try {
            finder.getLayersAbove(n2);
            fail();
        } catch (IllegalArgumentException ok) { }
    }

    public void testSubtreeNodeCounter_Randomized() {
        Random r = new Random(0);
        for (Direction d : Arrays.asList(Direction.IN, Direction.OUT)) {
            Graph g = new PrimaryGraph();
            Node root = Generators.createRandomTree(g, r, 10, d);
            SubtreeAnalyzer snc = Trees.analyzeSubtrees(g, root, d);

            for (Node n : g.nodes()) {
                assertEquals(Graphs.collectNodes(g, n, d).size() + 1, snc.nodeCountOf(n));
            }
        }
    }

    public void testSubtreeLeafCounter_Randomized() {
        Random r = new Random(0);
        for (Direction d : Arrays.asList(Direction.IN, Direction.OUT)) {
            Graph g = new PrimaryGraph();

            Node root = Generators.createRandomTree(g, r, 10, d);
            SubtreeAnalyzer snc = Trees.analyzeSubtrees(g, root, d);

            for (Node n : g.nodes()) {
                int leaves = 0;
                for (Node n2 : Graphs.collectNodes(g, n, d)) {
                    if (g.degree(n2) == 1) {
                        leaves++;
                    }
                }
                leaves = Math.max(1, leaves);
                assertEquals(leaves, snc.leafCountOf(n));
            }
        }
    }
}
