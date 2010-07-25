package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import junit.framework.*;

public class BiconnectivityTest extends TestCase {
    
    public BiconnectivityTest(String testName) {
        super(testName);
    }
    
    private Graph g;
    private Node[] n;
    private Edge e12;
    private Edge e23;
    private Edge e13;
    private Edge e34;
    private Edge e45;
    private Edge e46;
    private Edge e56;
    
    protected void setUp() {
        g = new PrimaryGraph();
        n = g.newNodes("1", "2", "3", "4", "5", "6");
        e12 = g.newEdge(n[0], n[1]);
        e13 = g.newEdge(n[0], n[2]);
        e23 = g.newEdge(n[1], n[2]);
        e34 = g.newEdge(n[2], n[3]);
        e45 = g.newEdge(n[3], n[4]);
        e46 = g.newEdge(n[3], n[5]);
        e56 = g.newEdge(n[4], n[5]);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BiconnectivityTest.class);
        
        return suite;
    }
    
    public void testCutNodes() {
        Biconnectivity bicon = Biconnectivity.execute(g);
        
        assertFalse(bicon.isCutNode(n[0]));
        assertFalse(bicon.isCutNode(n[1]));
        assertFalse(bicon.isCutNode(n[4]));
        assertFalse(bicon.isCutNode(n[5]));
        
        assertTrue(bicon.isCutNode(n[2]));
        assertTrue(bicon.isCutNode(n[3]));
    }
    
    public void testEdgeComponents() {
        Biconnectivity bicon = Biconnectivity.execute(g);
        
        assertEquals(bicon.componentOf(e12), bicon.componentOf(e13));
        assertEquals(bicon.componentOf(e13), bicon.componentOf(e23));
        
        assertEquals(bicon.componentOf(e45), bicon.componentOf(e46));
        assertEquals(bicon.componentOf(e46), bicon.componentOf(e56));
        
        assertNotSame(bicon.componentOf(e23), bicon.componentOf(e34));
        assertNotSame(bicon.componentOf(e34), bicon.componentOf(e45));
        assertNotSame(bicon.componentOf(e23), bicon.componentOf(e45));
    }

    public void testEmpty() {
        g = new PrimaryGraph();
        Biconnectivity bicon = Biconnectivity.execute(g);
        Edge e = g.newEdge(g.newNode(), g.newNode());
        assertNull(bicon.componentOf(e));
    }
}
