package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.layout.Locator.LocationListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.framework.*;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public abstract class LocatorTest extends MockObjectTestCase {
    public LocatorTest(String testName) {
        super(testName);
    }
    
    protected abstract Locator create();
    
    protected Graph graph;
    protected Node node;
    protected Edge edge;
    protected Locator locator;
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        
        return suite;
    }
    
    protected void setUp() {
        graph = new PrimaryGraph();
        node = graph.newNode(1);
        edge = graph.newEdge(node, node);
        locator = create();
    }
    
    public void testExistent() {
        double x = 105.54;
        double y = 43.34534;
        locator.setLocation(node, x, y);
        GPoint expected = new GPoint(x, y);
        assertEquals(expected, locator.getLocation(node));
    }
    
    public void testNonExistent() {
        assertNull(locator.getLocation(node));
        assertNull(locator.getBends(edge));
        assertNull(locator.getBend(edge));
    }
    
    public void testBendsList() {
        locator.setBends(edge, Collections.singletonList(new GPoint(50, 50)));
        List<GPoint> bends = locator.getBends(edge);
        assertEquals("[(50.0, 50.0)]", bends.toString());
        assertEquals("(50.0, 50.0)", locator.getBend(edge).toString());
    }
    
    public void testBendsArray() {
        locator.setBends(edge, new double[][] {
            { 1, 1 },
            { 2, 2 },
            { 3, 3 }
        });
        List<GPoint> bends = locator.getBends(edge);
        assertEquals("[(1.0, 1.0), (2.0, 2.0), (3.0, 3.0)]", bends.toString());
        assertEquals("(1.0, 1.0)", locator.getBend(edge).toString());
        try {
            locator.setBends(edge, new double[][] {
                { 1, 1 },
                { 2, },
                { 3, 3 }
            });
            fail();
        } catch (Exception ok) { }
    }
    
    public void testSingleBend() {
        locator.setBend(edge, new GPoint(50, 50));
        List<GPoint> bends = locator.getBends(edge);
        assertEquals("[(50.0, 50.0)]", bends.toString());
        assertEquals("(50.0, 50.0)", locator.getBend(edge).toString());
    }
    
    public void testRemoveLocation() {
        locator.setLocation(node, new GPoint(1, 2));
        locator.setLocation(node, null);
        assertNull(locator.getLocation(node));
    }
    
    public void testRemoveBends() {
        locator.setBend(edge, new GPoint(1, 2));
        locator.removeBends(edge);
        List<GPoint> bends = locator.getBends(edge);
        assertNull(bends);
    }
    
    public void testReadOnly() {
        locator.setBend(edge, new GPoint(1, 2));
        try {
            locator.getBends(edge).add(new GPoint(2, 2));
            fail();
        } catch (Exception ok) { }
    }
    
    public void testNotUnnecessaryEmptyBends() {
        assertTrue(locator.getBends(edge) == locator.getBends(edge));
    }
    
    public void testIllegalBends() {
        try {
            
            locator.setBends(edge, new double[][] { new double[] { 5, 5, 2 } } );
            fail();
        } catch (Exception ok) { }
    }
    
    public void testEvents() {
        GPoint p = new GPoint(3, 3);
        List<GPoint> bends = new ArrayList<GPoint>();
        bends.add(new GPoint(5, 5));
        bends.add(new GPoint(8, 8));
        Mock listener = mock(LocationListener.class);
        listener.expects(once()).method("onNodeLocation").with(eq(node), eq(p));
        listener.expects(once()).method("onEdgeLocation").with(eq(edge), eq(bends));
        listener.expects(once()).method("transformed");
        locator.addLocationListener((LocationListener)listener.proxy());
        
        locator.setLocation(node, 3, 3);
        locator.setBends(edge, new double[][] { new double[] { 5, 5 }, new double[] { 8, 8 } });
        locator.translate(0, 0);
    }
    
    public void testSafeBends() {
        assertNotNull(locator.getBendsSafe(edge));
        locator.setBend(edge, new GPoint(123, 123));
        assertEquals(locator.getBends(edge), locator.getBendsSafe(edge));
        locator.removeBends(edge);
        assertNotNull(locator.getBendsSafe(edge));
    }
}
