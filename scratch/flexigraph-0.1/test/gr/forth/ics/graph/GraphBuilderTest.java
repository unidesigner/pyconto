package gr.forth.ics.graph;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import junit.framework.*;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.path.Path;

public class GraphBuilderTest extends TestCase {
    
    public GraphBuilderTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(GraphBuilderTest.class);
        
        return suite;
    }
    
    private Graph graph;
    private GraphBuilder builder;
    
    protected void setUp() {
        graph = new PrimaryGraph();
        builder = new GraphBuilder(graph);
    }
    
    public void testGetGraph() {
        assertSame(graph, builder.getGraph());
    }
    
    public void testCreateEdgesClosed() {
        Node[] n = graph.newNodes(5);
        Edge[] e = builder.newEdges(true, n);
        assertEquals(5, e.length);
        for (int i = 0; i < n.length; i++) {
            assertTrue(graph.areAdjacent(n[i], n[(i + 1) % n.length], Direction.OUT));
        }
    }
    
    public void testCreateEdgesOpen() {
        Node[] n = graph.newNodes(5);
        Edge[] e = builder.newEdges(false, n);
        assertEquals(4, e.length);
        graph.newEdge(n[n.length - 1], n[0]);
        for (int i = 0; i < n.length; i++) {
            assertTrue(graph.areAdjacent(n[i], n[(i + 1) % n.length], Direction.OUT));
        }
    }
    
    public void testCreatePathClosed() {
        Node[] n = graph.newNodes(5);
        Path path = builder.newPath(true, n);
        assertEquals(5, path.size());
        assertSame(path.headNode(), path.tailNode());
    }
    
    public void testCreatePathOpen() {
        Node[] n = graph.newNodes(5);
        Path path = builder.newPath(false, n);
        assertEquals(4, path.size());
        assertSame(n[0], path.headNode());
        assertSame(n[n.length - 1], path.tailNode());
    }
}
