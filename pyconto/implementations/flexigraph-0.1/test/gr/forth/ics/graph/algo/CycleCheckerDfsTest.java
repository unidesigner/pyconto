package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.path.Cycles;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.path.Path;
import junit.framework.*;

public class CycleCheckerDfsTest extends TestCase {
    
    public CycleCheckerDfsTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(CycleCheckerDfsTest.class);
        
        return suite;
    }
    
    public void testNotFound() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(4);
        Node prev = null;
        for (Node node : n) {
            if (prev == null) {
                prev = node;
                continue;
            }
            g.newEdge(prev, node);
            prev = node;
        }
        
        assertNull(Cycles.findCycle(g));
    }
    
    public void testFound() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes("1", "2", "3", "4");
        Node prev = null;
        for (Node node : n) {
            if (prev == null) {
                prev = node;
                continue;
            }
            g.newEdge(prev, node);
            prev = node;
        }
        g.newEdge(n[3], n[0]);
        Path path = Cycles.findCycle(g);
        assertEquals("[1-->2-->3-->4-->1]", path.toString());
    }
}
