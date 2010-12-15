package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.algo.DegreeSorter;
import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import junit.framework.*;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.RandomMutator;
import gr.forth.ics.graph.PrimaryGraph;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import gr.forth.ics.util.DVMap;

public class DegreeSorterTest extends TestCase {
    
    public DegreeSorterTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(DegreeSorterTest.class);
        
        return suite;
    }
    
    private Graph g;
    private Node[] n;
    
    public void setUp() {
        g = new PrimaryGraph();
        n = g.newNodes(1, 2, 3, 4, 5);
    }
    
    public void testOut() {
        DegreeSorter bs = new DegreeSorter(g, Direction.OUT);
        assertTrue(bs.getNodes(0).size() == n.length);
        g.newEdge(n[0], n[0]);
        assertTrue(bs.getNodes(0).size() == 4);
        assertTrue(bs.getNodes(1).contains(n[0]));
        g.newEdge(n[1], n[0]);
        assertTrue(bs.getNodes(0).size() == 3);
        assertTrue(bs.getNodes(1).contains(n[0]));
        assertTrue(bs.getNodes(1).contains(n[1]));
        
        Edge e = g.newEdge(n[1], n[0]);
        assertTrue(bs.getNodes(1).size() == 1);
        assertTrue(bs.getNodes(2).size() == 1);
        g.removeEdge(e);
        assertTrue(bs.getNodes(2).size() == 0);
        assertTrue(bs.getNodes(1).size() == 2);
    }
    
    public void testNewNode() {
        DegreeSorter bs = new DegreeSorter(g);
        Node n1 = g.newNode();
        assertTrue(bs.getNodes(0).contains(n1));
    }
    
    public void testRandomized() {
        Graph g = new PrimaryGraph();
        DegreeSorter bsOut = new DegreeSorter(g, Direction.OUT);
        DegreeSorter bsIn = new DegreeSorter(g, Direction.IN);
        DegreeSorter bs = new DegreeSorter(g);
        g.newNodes(-10, -9, -8, -7, -6, -5, -4, -3, -2, -1);
        for (int i = 0; i < 1000; i++) {
            RandomMutator.mutate(g);
            checkInvariant(g, bsOut, Direction.OUT);
            checkInvariant(g, bsIn, Direction.IN);
            checkInvariant(g, bs, Direction.EITHER);
        }
    }
    
    private void checkInvariant(InspectableGraph g, DegreeSorter bs, Direction dir) {
        Map<Integer, Collection<Node>> nodes = DVMap.newHashMapWithHashSets();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Node n : g.nodes()) {
            int degree = g.degree(n, dir);
            if (min > degree) {
                min = degree;
            }
            if (max < degree) {
                max = degree;
            }
            nodes.get(degree).add(n);
        }
        assertEquals(dir.toString(), nodes.keySet().size(), bs.keySet().size());
        assertEquals("Max Degree", max, bs.getMaxDegree());
        assertEquals("Min Degree", min, bs.getMinDegree());
        for (Integer key : nodes.keySet()) {
            Collection<Node> bsCol = bs.getNodes(key);
            Collection<Node> exp = nodes.get(key);
            assertEquals(dir.toString(), exp.size(), bsCol.size());
            exp.retainAll(bsCol);
            assertEquals(dir.toString(), exp.size(), bsCol.size());
            exp.removeAll(bsCol);
            assertEquals(dir.toString(), 0, exp.size());
        }
    }
    
    public void testMemoryManagement() {
        DegreeSorter bs = new DegreeSorter(g);
        WeakReference<Graph> ref = new WeakReference<Graph>(g);
        g = null;
        for (int i = 0; i < 20; i++) {
            System.gc();
        }
        assertNull(ref.get());
    }
    
    public void testMaxDegree() {
        DegreeSorter bs = new DegreeSorter(g);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[0], n[2]);
        g.newEdge(n[0], n[3]);
        g.newEdge(n[0], n[4]);
        g.newEdge(n[1], n[2]);
        
        assertEquals(4, bs.getMaxDegree());
        g.removeNode(n[0]);
        assertEquals(1, bs.getMaxDegree());
        g.removeNode(n[4]);
        assertEquals(1, bs.getMaxDegree());
        g.removeNode(n[1]);
        assertEquals(0, bs.getMaxDegree());
        g.newNode();
        assertEquals(0, bs.getMaxDegree());
        g.removeNode(g.aNode());
        assertEquals(0, bs.getMaxDegree());
        g.removeNode(g.aNode());
        assertEquals(0, bs.getMaxDegree());
        g.removeNode(g.aNode());
        try {
            bs.getMaxDegree();
        } catch (NoSuchElementException ok) { }
    }
    
    public void testMinDegree() {
        DegreeSorter bs = new DegreeSorter(g);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[0], n[2]);
        g.newEdge(n[0], n[3]);
        g.newEdge(n[0], n[4]);
        g.newEdge(n[1], n[2]);
        
        assertEquals(1, bs.getMinDegree());
        g.removeNode(n[4]);
        assertEquals(1, bs.getMinDegree());
        g.removeNode(n[3]);
        assertEquals(2, bs.getMinDegree());
        g.removeNode(n[0]);
        assertEquals(1, bs.getMinDegree());
        g.newNode();
        assertEquals(0, bs.getMinDegree());
        g.removeNode(n[1]);
        assertEquals(0, bs.getMinDegree());
        g.removeNode(n[2]);
        assertEquals(0, bs.getMinDegree());
        g.removeNode(g.aNode());
        try {
            bs.getMinDegree();
        } catch (NoSuchElementException ok) { }
    }
    
    public void testRemoveNodes() {
        Graph g = new PrimaryGraph();
        g.newNodes(1, 2, 3);
        DegreeSorter sort = new DegreeSorter(g);
        //should not throw ConcurrentModificationException
        g.removeNodes(sort.getNodes(0));
    }
}
