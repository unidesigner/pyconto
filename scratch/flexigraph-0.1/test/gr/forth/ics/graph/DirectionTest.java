package gr.forth.ics.graph;

import junit.framework.*;

public class DirectionTest extends TestCase {
    
    public DirectionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DirectionTest.class);
        
        return suite;
    }

    public void testAll() {
        assertFalse(Direction.OUT.isIn());
        assertTrue(Direction.OUT.isOut());
        
        assertTrue(Direction.IN.isIn());
        assertFalse(Direction.IN.isOut());
        
        assertTrue(Direction.EITHER.isIn());
        assertTrue(Direction.EITHER.isOut());
    }

    public void testIsIn() {
    }

    public void testFlip() {
    }
    
}
