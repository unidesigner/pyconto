package gr.forth.ics.graph;

import java.util.Arrays;
import java.util.Collection;
import junit.framework.*;

public class UndirectedInspectableGraphTest extends TestCase {
    
    public UndirectedInspectableGraphTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(UndirectedInspectableGraphTest.class);
        
        return suite;
    }
    
    public void testUndirectedInspectableGraph() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(1, 2, 3, 4);
        Edge e01 = g.newEdge(n[0], n[1]);
        Edge e12 = g.newEdge(n[1], n[2]);
        Edge e20 = g.newEdge(n[2], n[0]);
        Edge e23 = g.newEdge(n[2], n[3]);
        UndirectedInspectableGraph ug = new UndirectedInspectableGraph(g);
        
        checkNode(ug, n[0], e01, e20);
        checkNode(ug, n[1], e01, e12);
        checkNode(ug, n[2], e20, e12, e23);
        checkNode(ug, n[3], e23);
    }
    
    public void testUndirectedGraph() {
        Graph g = new PrimaryGraph();
        UndirectedInspectableGraph ug = new UndirectedInspectableGraph(g);
        Node[] n = g.newNodes(4);
        Edge e01 = g.newEdge(n[0], n[1]);
        Edge e12 = g.newEdge(n[1], n[2]);
        Edge e20 = g.newEdge(n[2], n[0]);
        Edge e23 = g.newEdge(n[2], n[3]);
        
        checkNode(ug, n[0], e01, e20);
        checkNode(ug, n[1], e01, e12);
        checkNode(ug, n[2], e20, e12, e23);
        checkNode(ug, n[3], e23);
        
        g.removeEdge(e01);
        checkNode(ug, n[0], e20);
        checkNode(ug, n[1], e12);
        checkNode(ug, n[2], e20, e12, e23);
        checkNode(ug, n[3], e23);
        
        g.removeEdge(e20);
        checkNode(ug, n[0]);
        checkNode(ug, n[1], e12);
        checkNode(ug, n[2], e12, e23);
        checkNode(ug, n[3], e23);
        
        g.removeEdge(e23);
        checkNode(ug, n[0]);
        checkNode(ug, n[1], e12);
        checkNode(ug, n[2], e12);
        checkNode(ug, n[3]);
        
        g.removeEdge(e12);
        checkNode(ug, n[0]);
        checkNode(ug, n[1]);
        checkNode(ug, n[2]);
        checkNode(ug, n[3]);
    }
    
    private void checkNode(UndirectedInspectableGraph ug, Node node, Edge ... edges) {
        Collection<Edge> edgesCollection = Arrays.asList(edges);
        int degree = edges.length;
        assertTrue(ug.edges(node, Direction.OUT).drainToSet().containsAll(edgesCollection));
        assertTrue(ug.edges(node, Direction.IN).drainToSet().containsAll(edgesCollection));
        assertEquals(degree, ug.edges(node, Direction.OUT).drainToSet().size());
        assertEquals(degree, ug.edges(node, Direction.IN).drainToSet().size());
        assertEquals(degree, ug.degree(node));
        assertEquals(degree, ug.outDegree(node));
        assertEquals(degree, ug.inDegree(node));
    }
    
    //TODO: test that n1-->n2, then newEdge(n2, n1) yields NEW edge back! convention
}
