package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.layout.GPoint;
import junit.framework.*;
import java.awt.geom.Point2D;

public class GPointTest extends TestCase {
    
    public GPointTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(GPointTest.class);
        
        return suite;
    }
    
    public void testEquals() {
        assertEquals(new GPoint(2, 2), new GPoint(2, 2));
        assertFalse(new GPoint(2, 2).equals(new GPoint(2, 2.0001)));
    }
    
    public void testHashCode() {
        assertEquals(new GPoint(2.7, 3.1).hashCode(), new GPoint(2.7, 3.1).hashCode());
    }
    
    public void testToPoint2D() {
        GPoint gp = new GPoint(34.5, 12.1);
        Point2D p = gp.toPoint2D(null);
        assertEquals(34.5, p.getX());
        assertEquals(12.1, p.getY());
    }
    
    public void testMidPoint() {
        GPoint p1 = new GPoint(2, 2);
        GPoint p2 = new GPoint(5, 5);
        GPoint p3 = p1.midPoint(p2);
        assertEquals(new GPoint(3.5, 3.5), p3);
        assertEquals(p3, p1.midPoint(p2, 0.5));
    }
    
    public void testAdd() {
        GPoint g1 = new GPoint(2, 2);
        GPoint g2 = new GPoint(4, -1);
        GPoint result = new GPoint(6, 1);
        assertEquals(result, g1.add(g2));
        assertEquals(result, g2.add(g1));
    }
    
    public void testSubtract() {
        GPoint g1 = new GPoint(2, 2);
        GPoint g2 = new GPoint(4, -1);
        assertEquals(new GPoint(-2, 3), g1.subtract(g2));
        assertEquals(new GPoint(2, -3), g2.subtract(g1));
    }
    
    public void testMul() {
        assertEquals(new GPoint(3, -3), new GPoint(1, -1).mul(3));
    }
    
    public void testDistance() {
        assertEquals(5.0, new GPoint(3, 0).distance(new GPoint(0, 4)));
        assertEquals(1.0, new GPoint(3, 0).distance(new GPoint(3, 1)));
    }
    
    public void testLength() {
        assertEquals(5.0, new GPoint(3, 4).length());
    }
    
    public void testDotProduct() {
        assertEquals((double)2 * 4 + 3 * 5, new GPoint(2, 3).dotProduct(new GPoint(4, 5)));
    }
}
