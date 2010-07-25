package gr.forth.ics.graph.layout.circular;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.layout.ArithmeticTestCase;
import gr.forth.ics.graph.Graph;
import junit.framework.*;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.layout.Locator;
import java.util.Arrays;
import java.util.List;

public class CircularOrderTest extends ArithmeticTestCase {
    
    public CircularOrderTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(CircularOrderTest.class);
        
        return suite;
    }
    
    private Graph graph;
    private Node[] n;
    
    protected void setUp() {
        graph = new PrimaryGraph();
        n = graph.newNodes(5);
    }
    
    public void testGetOrder() {
        List<Node> order = Arrays.asList(n[0], n[1], n[2], n[3], n[4]);
        assertEquals(Arrays.asList(n[0], n[1], n[2], n[3], n[4]), new CircularOrder(order).getOrder());
    }
    
    public void testGetCircleLayout() {
        List<Node> order = Arrays.asList(n[0], n[1], n[2], n[3]);
        CircularOrder co = new CircularOrder(order);
        Locator locator = co.getCircleLayout(GPoint.ZERO_POINT, 8.0, 0.0, null);
        final double E = 0.00001;
        assertNear(locator.getLocation(n[0]), new GPoint(8.0, 0.0), E);
        assertNear(locator.getLocation(n[1]), new GPoint(0.0, 8.0), E);
        assertNear(locator.getLocation(n[2]), new GPoint(-8.0, 0.0), E);
        assertNear(locator.getLocation(n[3]), new GPoint(0.0, -8.0), E);
    }
}
