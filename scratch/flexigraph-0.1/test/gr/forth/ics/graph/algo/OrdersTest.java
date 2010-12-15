package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.GraphException;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.Graphs;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class OrdersTest extends TestCase {
    
    public OrdersTest(String testName) {
        super(testName);
    }

    private static Graph sparseGraph() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(1, 2, 3, 4, 5, 6);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[1], n[2]);
        g.newEdge(n[1], n[3]);
        g.newEdge(n[2], n[3]);
        g.newEdge(n[2], n[4]);
        g.newEdge(n[3], n[5]);
        g.newEdge(n[3], n[4]);
        g.newEdge(n[4], n[5]);
        return g;
    }
    
    private static Graph denseGraph() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(1, 2, 3, 4, 5, 6);
        for (int i = 0; i < n.length - 1; i++) {
            for (int j = i + 1; j < n.length; j++) {
                g.newEdge(n[i], n[j]);
            }
        }
        return g;
    }
    
    public void testTopological1() {
        List<Node> topo = Orders.topological(sparseGraph());
        assertEquals("[1, 2, 3, 4, 5, 6]", topo.toString());
    }
    
    public void testTopological2() {
        List<Node> topo = Orders.topological(denseGraph());
        assertEquals("[1, 2, 3, 4, 5, 6]", topo.toString());
    }
    
    public void testInvertedTopological1() {
        List<Node> topo = Orders.topological(Graphs.inverted(sparseGraph()));
        assertEquals("[6, 5, 4, 3, 2, 1]", topo.toString());
    }
    
    public void testInvertedTopological2() {
        List<Node> topo = Orders.topological(Graphs.inverted(denseGraph()));
        assertEquals("[6, 5, 4, 3, 2, 1]", topo.toString());
    }
    
    public void testCycleForbidden() {
        Graph g = new PrimaryGraph();
        Node n1 = g.newNode();
        Node n2 = g.newNode();
        g.newEdge(n1, n2);
        g.newEdge(n2, n1);
        try {
            Orders.topological(g);
            fail();
        } catch (GraphException ok) {
        }
    }
}
