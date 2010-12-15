package gr.forth.ics.graph;

import junit.framework.*;
import java.util.Collection;

//TODO: Put these tests in GraphTest, so to automatically run the tests for every available graph implementation
public class InvertedInspectableGraphTest extends TestCase {
    
    public InvertedInspectableGraphTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(InvertedInspectableGraphTest.class);
        
        return suite;
    }
    
    public void test() {
        Graph g = new PrimaryGraph();
        Node n1 = g.newNode();
        Node n2 = g.newNode();
        Edge e1 = g.newEdge(n1, n2);
        Edge e2 = g.newEdge(n2, n1);
        Edge e3 = g.newEdge(n2, n1);
        
        InvertedInspectableGraph ig = new InvertedInspectableGraph(g);
        assertEquals(1, ig.inDegree(n1));
        assertEquals(2, ig.outDegree(n1));
        assertEquals(2, ig.inDegree(n2));
        assertEquals(1, ig.inDegree(n1));
        
        {
            Collection<Edge> cw = ig.edges(n1, Direction.OUT).drainToSet();
            assertEquals(2, cw.size());
            assertTrue(cw.contains(e2));
            assertTrue(cw.contains(e3));
        }
        
            Collection<Edge> cw = ig.edges(n1, Direction.IN).drainToSet();
            assertEquals(1, cw.size());
            assertTrue(cw.contains(e1));
    }
}
