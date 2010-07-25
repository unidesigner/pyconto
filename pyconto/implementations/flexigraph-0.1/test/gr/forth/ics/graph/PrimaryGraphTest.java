package gr.forth.ics.graph;

import junit.framework.*;

public class PrimaryGraphTest extends GraphTest {
    
    public PrimaryGraphTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(PrimaryGraphTest.class);
        return suite;
    }
    
    protected Graph create() {
        return new PrimaryGraph();
    }

    protected boolean isPrimary() {
        return true;
    }
}
