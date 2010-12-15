package gr.forth.ics.graph.event;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.PrimaryGraph;
import junit.framework.*;
import static gr.forth.ics.util.Sample.*;

public class GraphEventTest extends TestCase {
    
    public GraphEventTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        reinitData();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(GraphEventTest.class);
        
        return suite;
    }
    
    public void testGetData() {
        try {
            GraphEvent ge = new GraphEvent(graph, GraphEvent.Type.NODE_ADDED, node);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        callWrong(GraphEvent.Type.NODE_ADDED, edge);
        callWrong(GraphEvent.Type.EDGE_REMOVED, node);
        callWrong(GraphEvent.Type.NODE_REORDERED, edge);
        callWrong(GraphEvent.Type.EDGE_REORDERED, node);
    }
    
    private void callWrong(GraphEvent.Type type, Object data) {
        try {
            new GraphEvent(graph, type, data);
            fail("Did not throw class cast exception on input: " + type + ", " + data);
        } catch (ClassCastException ok) { }
    }
    
    public void testRemoveHotFiredListener() {
        final Graph g = new PrimaryGraph();
        g.addGraphListener(new EmptyGraphListener() {
            public void nodeAdded(GraphEvent e) {
                g.removeGraphListener(this);
            }
        });
        //should NOT throw ConcurrentModificationException
        g.newNode();
    }
}
