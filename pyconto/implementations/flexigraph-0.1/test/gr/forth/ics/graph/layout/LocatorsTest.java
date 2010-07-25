package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.layout.GRect;
import gr.forth.ics.graph.Graph;
import junit.framework.*;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;

public class LocatorsTest extends TestCase {
    
    public LocatorsTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(LocatorsTest.class);
        
        return suite;
    }
    
    private Locator locator;
    private Graph g;
    private Node n;
    
    protected void setUp() {
        locator = new BasicLocator();
        g = new PrimaryGraph();
        n = g.newNode();
    }
    
    public void testGetBoundingBox() {
        locator.setLocation(n, 50, 60);
        GRect rect = Locators.getBoundingBox(locator, g);
        assertEquals(50.0, rect.minX());
        assertEquals(60.0, rect.minY());
        assertEquals(0.0, rect.width());
        assertEquals(0.0, rect.height());
    }
    
    public void testZoomToRect() {
        Node n2 = g.newNode();
        locator.setLocation(n, 50, 50);
        locator.setLocation(n2, 60, 60);
        Locators.zoomToRect(locator, g, new GRect(20, 20, 80, 80));
        assertEquals(new GPoint(20, 20), locator.getLocation(n));
        assertEquals(new GPoint(100, 100), locator.getLocation(n2));
    }
    
    public void testZoomWhenEmpty() {
        locator.setLocation(n, 50, 50);
        Locators.zoomToRect(locator, g, new GRect(40, 40, 40, 40));
        assertEquals(new GPoint(40, 40), locator.getLocation(n));
    }
    
    public void testZoomToRectTwice() {
        Node n2 = g.newNode();
        locator.setLocation(n, 50, 50);
        locator.setLocation(n2, 60, 60);
        Locators.zoomToRect(locator, g, new GRect(20, 20, 80, 80));
        Locators.zoomToRect(locator, g, new GRect(10, 10, 150, 150));
        assertEquals(new GPoint(10, 10), locator.getLocation(n));
        assertEquals(new GPoint(160, 160), locator.getLocation(n2));
    }
}
