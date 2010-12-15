package gr.forth.ics.graph;

import gr.forth.ics.graph.Graph;
import junit.framework.*;

public class SecondaryGraphSerializableTest extends SerializableTest {
    public SecondaryGraphSerializableTest(String testName) {
        super(testName);
    }
    
    public static TestSuite suite() {
        TestSuite suite = new TestSuite(SecondaryGraphSerializableTest.class);
        return suite;
    }
    
    protected Graph createGraph() {
        return new SecondaryGraph();
    }
}
