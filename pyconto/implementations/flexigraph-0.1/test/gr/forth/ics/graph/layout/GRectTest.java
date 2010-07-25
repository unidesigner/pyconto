package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.layout.GRect;
import junit.framework.*;

public class GRectTest extends TestCase {
    
    public GRectTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(GRectTest.class);
        
        return suite;
    }
    
    private GRect rect;
    
    public void setUp() {
        rect = new GRect(10, 20, 30, 40); //10-->40, 20-->60
    }
    
    public void testX() {
        assertEquals(10.0, rect.minX());
    }
    
    public void testY() {
        assertEquals(20.0, rect.minY());
    }
    
    public void testWidth() {
        assertEquals(30.0, rect.width());
    }
    
    public void testHeight() {
        assertEquals(40.0, rect.height());
    }
    
    public void testCorner() {
        assertEquals(new GPoint(10, 20), rect.corner());
    }
    
    public void testSize() {
        assertEquals(new GPoint(30, 40), rect.size());
    }
    
    public void testContains() {
        assertTrue(rect.contains(rect.corner()));
        assertTrue(rect.contains(rect.corner().add(rect.size())));
    }
    
    public void testReverse() {
        assertEquals(new GRect(0, 0, 10, 10), new GRect(10, 10, -10, -10));
    }
    
    public void testAdd() {
        assertEquals(new GRect(15, 7, 10, 10), new GRect(10, 10, 10, 10).union(new GPoint(5, -3)));
    }
    
    public void testGPointConstructor() {
        assertEquals(new GRect(10, 20, 30, 40), new GRect(new GPoint(10, 20), new GPoint(30, 40)));
        assertEquals(new GRect(-5, -8, 5, 8), new GRect(new GPoint(0, 0), new GPoint(-5, -8)));
    }
}
