package gr.forth.ics.graph.layout.circular;

import gr.forth.ics.graph.layout.GPoint;
import junit.framework.*;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.layout.BasicLocator;
import gr.forth.ics.graph.layout.Locator;
import gr.forth.ics.graph.GraphBuilder;

public class CircularLayoutTest extends TestCase {
    
    public CircularLayoutTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(CircularLayoutTest.class);
        
        return suite;
    }
    
    private Graph g;
    
    protected void setUp() {
        g = new PrimaryGraph();
        GraphBuilder gb = new GraphBuilder(g);
        gb.newNodes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).
                newPath(0, 3).
                newPath(2, 8).
                newPath(4, 5).
                newPath(5, 7).
                newPath(8, 0).
                newPath(8, 4).
                newPath(8, 7).
                newPath(9, 8);
    }
    
    public void testAllNodesAcquireLocations() {
        CircularLayout layout = new CircularLayout(GPoint.ZERO_POINT, 100, 0);
        Locator locator = new BasicLocator();
        layout.layout(g).run(locator);
        for (Node n : g.nodes()) {
            assertNotNull(locator.getLocation(n));
        }
    }
}
