package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.path.Cycles;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import junit.framework.*;
import gr.forth.ics.graph.path.Path;

public class CycleFinderDfsTest extends TestCase {
    
    public CycleFinderDfsTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CycleFinderDfsTest.class);
        
        return suite;
    }
    
    public void testSimple() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes("1", "2");
        g.newEdge(n[0], n[1]);
        Path cycle = Cycles.findCycle(g);
        assertNull(cycle);
        g.newEdge(n[1], n[0]);
        cycle = Cycles.findCycle(g);
        assertEquals("[1-->2-->1]", cycle.toString());
    }
    
    public void testSelfLoop() {
        Graph g = new PrimaryGraph();
        Node n = g.newNode("1");
        g.newEdge(n, n);
        Path cycle = Cycles.findCycle(g);
        assertEquals("[1-->1]", cycle.toString());
    }
}
