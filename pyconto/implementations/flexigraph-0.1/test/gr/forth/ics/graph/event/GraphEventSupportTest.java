package gr.forth.ics.graph.event;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import junit.framework.*;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import static gr.forth.ics.util.Sample.*;

public class GraphEventSupportTest extends MockObjectTestCase {
    public GraphEventSupportTest(String testName) {
        super(testName);
    }
    
    GraphEventSupport events;
    
    protected void setUp() throws Exception {
        events = new GraphEventSupport();
        reinitData();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(GraphEventSupportTest.class);
        
        return suite;
    }
    
    public void testNodeRemovalInitsEdgeRemovals() {
        Graph g = new PrimaryGraph();
        Node n = g.newNode();
        Edge e = g.newEdge(n, n);
        
        Mock graphListenerMock = mock(GraphListener.class);
        graphListenerMock.expects(once()).method("preEvent");
        graphListenerMock.expects(once()).method("nodeToBeRemoved").id("1");
        graphListenerMock.expects(once()).method("preEvent");
        graphListenerMock.expects(once()).method("edgeToBeRemoved");
        graphListenerMock.expects(once()).method("edgeRemoved").after("1").id("2");
        graphListenerMock.expects(once()).method("postEvent");
        graphListenerMock.expects(once()).method("nodeRemoved").after("2");
        graphListenerMock.expects(once()).method("postEvent");
        
        g.addGraphListener((GraphListener)graphListenerMock.proxy());
        g.removeNode(n);
    }
}
