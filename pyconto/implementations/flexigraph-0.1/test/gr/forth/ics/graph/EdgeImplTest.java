package gr.forth.ics.graph;

import junit.framework.*;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Edge.Orientation;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.path.Path;
import static gr.forth.ics.graph.Edge.Orientation.*;

public class EdgeImplTest extends TestCase {
    
    public EdgeImplTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(EdgeImplTest.class);
        
        return suite;
    }
    
    private Graph g;
    private Node n1, n2;
    private Edge e;
    
    public void setUp() {
        g = new PrimaryGraph();
        n1 = g.newNode("1");
        n2 = g.newNode("2");
        e = g.newEdge(n1, n2);
    }
    
    public void testN1() {
        assertEquals(n1, e.n1());
    }
    
    public void testN2() {
        assertEquals(n2, e.n2());
    }
    
    public void testIsIncident() {
        Edge e2 = g.newEdge(n2, g.newNode());
        Edge e3 = g.newEdge(g.newNode(), n1);
        Edge e4 = g.newEdge(g.newNode(), g.newNode());
        assertTrue(e.isIncident(e2));
        assertTrue(e2.isIncident(e));
        assertTrue(e.isIncident(e3));
        assertTrue(e3.isIncident(e));
        assertFalse(e.isIncident(e4));
        assertFalse(e4.isIncident(e));
    }
    
    public void testOpposite() {
        assertEquals(n2, e.opposite(n1));
        assertEquals(n1, e.opposite(n2));
        try {
            assertNull(e.opposite(g.newNode()));
            fail();
        } catch (RuntimeException e) {
            //ok
        }
        try {
            assertNull(e.opposite(null));
            fail();
        } catch (RuntimeException e) {
            //ok
        }
    }
    
    public void testIsSelfLoop() {
        assertFalse(e.isSelfLoop());
        Edge e2 = g.newEdge(n1, n1);
        assertTrue(e2.isSelfLoop());
    }
    
    public void testGetIntersection() {
        Edge e2 = g.newEdge(g.newNode(), g.newNode());
        assertNull(e.getIntersection(e2));
        Edge e3 = g.newEdge(n2, n1);
        assertEquals(n1, e.getIntersection(e3));
        assertEquals(n2, e3.getIntersection(e));
        
        assertEquals(n1, e.getIntersection(true, e3));
        assertEquals(n2, e.getIntersection(false, e3));
        
        assertEquals(n2, e3.getIntersection(true, e));
        assertEquals(n1, e3.getIntersection(false, e));
        
        Edge e4 = g.newEdge(g.newNode(), n2);
        assertEquals(n2, e.getIntersection(e4));
        assertEquals(n2, e.getIntersection(true, e4));
        assertEquals(n2, e.getIntersection(false, e4));
        
        assertEquals(n2, e4.getIntersection(true, e));
        assertEquals(n2, e4.getIntersection(false, e));
    }
    
    public void testAsPath() {
        Path p = e.asPath();
        assertEquals(n1, p.headNode());
        assertEquals(n2, p.tailNode());
        assertEquals(e, p.getEdge(0));
        
        assertEquals(p, e.asPath(n1));
        p = e.asPath(n2);
        assertEquals(n2, p.headNode());
        assertEquals(n1, p.tailNode());
        
        try {
            p = e.asPath(g.newNode());
            fail();
        } catch (RuntimeException e) {
            //ok
        }
        
        try {
            p = e.asPath(null);
            fail();
        } catch (RuntimeException e) {
            //ok
        }
    }
    
    public void testAreParallel() {
        Edge e2 = g.newEdge(n1, n2);
        assertTrue(e.areParallel(e2));
        assertTrue(e2.areParallel(e));
        
        e2 = g.newEdge(n1, g.newNode());
        assertFalse(e.areParallel(e2));
        assertFalse(e2.areParallel(e));
    }
    
    public void testOrientation() {
        Edge e2 = g.newEdge(n1, g.newNode());
        orient(OPPOSITE, e2);
        Edge e3 = g.newEdge(n2, g.newNode());
        orient(SAME, e3);
        Edge e4 = g.newEdge(g.newNode(), g.newNode());
        orient(UNDEFINED, e4);
        Edge e5 = g.newEdge(n1, n1);
        orient(UNDEFINED, e5);
        Edge e6 = g.newEdge(n2, n2);
        orient(UNDEFINED, e6);
    }
    
    private void orient(Orientation expected, Edge e2) {
        assertEquals(expected, e.testOrientation(e2));
        assertEquals(expected, e2.testOrientation(e));
    }
}
