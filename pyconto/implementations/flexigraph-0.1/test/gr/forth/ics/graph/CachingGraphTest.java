package gr.forth.ics.graph;

import junit.framework.*;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Hint;

public class CachingGraphTest extends GraphTest {
    
    public CachingGraphTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CachingGraphTest.class);
        
        return suite;
    }

    protected Graph create() {
        Graph g = new PrimaryGraph();
        g.hint(Hint.FAST_NODE_ITERATION);
        g.hint(Hint.FAST_EDGE_ITERATION);
        return g;
    }

    protected boolean isPrimary() {
        return true;
    }
}
