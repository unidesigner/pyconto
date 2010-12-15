package gr.forth.ics.graph;

import gr.forth.ics.graph.Graph;
import junit.framework.TestSuite;

public class PrimaryGraphSerializableTest extends SerializableTest {
    
    public PrimaryGraphSerializableTest(String testName) {
        super(testName);
    }
    
    public static TestSuite suite() {
        TestSuite suite = new TestSuite(PrimaryGraphSerializableTest.class);
        return suite;
    }
    
    protected Graph createGraph() {
        return new PrimaryGraph();
    }
}
