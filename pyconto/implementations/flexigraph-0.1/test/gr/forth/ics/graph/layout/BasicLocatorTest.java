package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.layout.GPoint;
import java.awt.geom.AffineTransform;
import junit.framework.*;

public class BasicLocatorTest extends LocatorTest {
    public BasicLocatorTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BasicLocatorTest.class);
        
        return suite;
    }

    protected Locator create() {
        return new BasicLocator();
    }
    
    public void testChangingTransform() {
        GPoint initial = new GPoint(5, 5);
        locator.setLocation(node, initial);
        locator.setBend(edge, initial);
        assertNotNull(locator.getLocation(node));
        assertNotNull(locator.getBend(edge));
        locator.setAffineTransform(new AffineTransform());
        assertNotNull(locator.getLocation(node));
        assertNotNull(locator.getBend(edge));
        GPoint point = locator.getLocation(node);
        GPoint bend = locator.getBend(edge);
        assertTrue(initial.equals(point));
        assertTrue(initial.equals(bend));
    }
    
    public void testTranslate() {
        GPoint initial = new GPoint(5, 5);
        locator.translate(5, 5);
        locator.setLocation(node, initial);
        locator.setBend(edge, initial);
        GPoint location = locator.getLocation(node);
        GPoint bend = locator.getBend(edge);
        assertEquals(new GPoint(10, 10), location);
        assertEquals(new GPoint(10, 10), bend);
    }
    
    public void testDoesNotCreateUnnecessaryCopies() {
        locator.rotate(0.7, 10, 10);
        locator.setLocation(node, new GPoint(20, 20));
        locator.setBend(edge, new GPoint(30, 30));
        assertSame(locator.getLocation(node), locator.getLocation(node));
        assertSame(locator.getBend(edge), locator.getBend(edge));
    }
    
    public void testIdentityTransformKeepsSamePoint() {
        GPoint point = new GPoint(3, 3);
        locator.setLocation(node, point);
        locator.setBend(edge, point);
        assertSame(point, locator.getLocation(node));
        assertSame(point, locator.getBend(edge));
        locator.setAffineTransform(new AffineTransform());
        assertSame(point, locator.getLocation(node));
        assertSame(point, locator.getBend(edge));
        locator.setAffineTransform(null);
        assertSame(point, locator.getLocation(node));
        assertSame(point, locator.getBend(edge));
        locator.translate(4, 4);
        assertNotSame(point, locator.getLocation(node));
        assertNotSame(point, locator.getBend(edge));
    }
    
    public void testDefaultLocation() {
        GPoint def = new GPoint(5, 5);
        BasicLocator loc = new BasicLocator(def);
        assertSame(def, loc.getLocation(node));
        loc.setLocation(node, new GPoint(10, 10));
        assertNotSame(def, loc.getLocation(node));
        loc.removeLocation(node);
        assertSame(def, loc.getLocation(node));
    }
}
