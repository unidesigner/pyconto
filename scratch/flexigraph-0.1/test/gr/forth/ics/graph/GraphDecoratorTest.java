package gr.forth.ics.graph;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.GraphTest;
import junit.framework.*;

public class GraphDecoratorTest extends GraphTest {
    
    public GraphDecoratorTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(GraphDecoratorTest.class);
        
        return suite;
    }
    
    protected Graph create() {
            return new GraphForwarder(new PrimaryGraph());
    }
    
    protected boolean isPrimary() {
        return true;
    }
}
