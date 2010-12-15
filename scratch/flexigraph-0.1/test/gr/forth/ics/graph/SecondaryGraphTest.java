package gr.forth.ics.graph;

import java.util.Arrays;
import junit.framework.*;

public class SecondaryGraphTest extends GraphTest {
    
    public SecondaryGraphTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(SecondaryGraphTest.class);
        return suite;
    }
    
    protected Graph create() {
        return new SecondaryGraph();
    }
    
    protected boolean isPrimary() {
        return false;
    }
    
    public void testIsNotPrimary() {
        assertFalse(new SecondaryGraph().isPrimary());
    }
    
    public void testSharing() {
        Graph g = new PrimaryGraph();
        g.newEdge(g.newNode(), g.newNode());
        InspectableGraph secondary = new SecondaryGraph(g);
        for (Node n : g.nodes()) {
            assertTrue(secondary.containsNode(n));
        }
        for (Edge e : g.edges()) {
            assertTrue(secondary.containsEdge(e));
        }
        assertEquals(g.nodeCount(), secondary.nodeCount());
        assertEquals(g.edgeCount(), secondary.edgeCount());
    }
    
    public void testAdoptNull() {
        SecondaryGraph s = new SecondaryGraph();
        Node n = null;
        Edge e = null;
        Graph g = null;
        try {
            s.adoptNode(n);
            fail("Adopted null node");
        } catch (RuntimeException ex) {
            //ok
        }
        try {
            s.adoptEdge(e);
            fail("Adopted null node");
        } catch (RuntimeException ex) {
            //ok
        }
        try {
            s.adoptGraph(g);
            fail("Adopted null node");
        } catch (RuntimeException ex) {
            //ok
        }
    }
    
    public void testCannotAdoptTwice() {
        Graph g = new PrimaryGraph();
        Node n = g.newNode();
        SecondaryGraph secondary = new SecondaryGraph();
        assertTrue(secondary.adoptNode(n));
        assertFalse(secondary.adoptNode(n));
        Edge e = g.newEdge(n, n);
        assertTrue(secondary.adoptEdge(e));
        assertFalse(secondary.adoptEdge(e));
    }
    
    public void testDegrees() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(2);
        Edge e = g.newEdge(n[0], n[1]);
        
        SecondaryGraph s = new SecondaryGraph();
        s.adoptNode(n[0]);
        
        
        assertEquals(0, s.inDegree(n[0]));
        assertEquals(0, g.inDegree(n[0]));
        
        assertEquals(0, s.outDegree(n[0]));
        assertEquals(1, g.outDegree(n[0]));
        
        assertEquals(0, s.degree(n[0]));
        assertEquals(1, g.degree(n[0]));
        
        s.adoptEdge(e);
        assertEquals(1, s.outDegree(n[0]));
        assertEquals(1, s.degree(n[0]));
        
        s.removeEdge(e);
        assertEquals(0, s.inDegree(n[0]));
        assertEquals(0, s.outDegree(n[0]));
        assertEquals(0, s.degree(n[0]));
    }
    
    public void testSelectiveConstructor() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes("1", "2", "3", "4", "5");
        g.newEdge(n[0], n[1]);
        g.newEdge(n[1], n[2]);
        g.newEdge(n[2], n[3]);
        g.newEdge(n[3], n[4]);
        g.newEdge(n[0], n[4]);
        
        SecondaryGraph sg = new SecondaryGraph(g, Arrays.asList(n).subList(0, 4));
        assertEquals(4, sg.nodeCount());
        assertFalse(sg.containsNode(n[4]));
        assertEquals(1, sg.outDegree(n[0]));
        assertEquals(0, sg.outDegree(n[3]));
    }
    
    public void testAdoptionDoesNotChangeGraph() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes("1", "2", "3");
        g.newEdge(n[0], n[1]);
        g.newEdge(n[1], n[2]);
        g.newEdge(n[2], n[0]);
        String textual = g.toString();
        SecondaryGraph sg = new SecondaryGraph();
        sg.adoptGraph(g);
        assertEquals(textual, g.toString());
    }
    
    public void testRemoveGraph() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(4);
        connect(g, n);
        SecondaryGraph sg = new SecondaryGraph(g);
        g.removeNode(n[3]);
        
        sg.removeGraph(g);
        assertEquals(1, sg.nodeCount());
        assertEquals(0, sg.edgeCount());
        assertTrue(sg.containsNode(n[3]));
    }
    
    public void retainGraph1() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(4);
        connect(g, n);
        SecondaryGraph sg = new SecondaryGraph(g);
        g.removeNode(n[3]);
        
        sg.retainGraph(g);
        assertEquals(3, sg.nodeCount());
        assertEquals(2, sg.edgeCount());
        assertFalse(sg.containsNode(n[3]));
    }
    
    public void retainGraph2() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(4);
        g.newEdge(n[0], n[0]);
        connect(g, n);
        SecondaryGraph sg = new SecondaryGraph(g);
        g.newEdge(n[0], g.newNode());
        g.removeNode(n[3]);
        g.removeNode(n[2]);
        g.removeNode(n[1]);
        
        sg.retainGraph(g);
        assertEquals(1, sg.nodeCount());
        assertEquals(1, sg.edgeCount());
        assertTrue(sg.containsNode(n[0]));
    }
    
    private void connect(Graph g, Node[] nodes) {
        for (int i = 1; i < nodes.length; i++) {
            g.newEdge(nodes[i - 1], nodes[i]);
        }
    }
}
