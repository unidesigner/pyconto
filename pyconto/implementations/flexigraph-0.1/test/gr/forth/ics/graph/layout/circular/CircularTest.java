package gr.forth.ics.graph.layout.circular;

import junit.framework.*;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.GraphBuilder;
import java.util.LinkedList;
import java.util.List;

public class CircularTest extends TestCase {
    
    public CircularTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CircularTest.class);
        
        return suite;
    }

    public void testSimple() {
        Graph g = new PrimaryGraph();
        GraphBuilder gb = new GraphBuilder(g);
        Node[] n = g.newNodes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        gb.newEdges(n[0], n[1], n[2], n[3], n[4], n[5], n[6], n[7], n[8],
                n[9], n[14], n[13], n[12], n[11], n[10], n[6], n[11]);
        g.newEdge(n[5], n[10]);
        
        List<Node> order = new LinkedList<Node>(new Circular().execute(g).getOrder());
        List<Node> sublist = order.subList(0, order.indexOf(n[0]));
        List<Node> copy = new LinkedList<Node>(sublist);
        sublist.clear();
        order.addAll(copy);
        assertEquals("[1, 11, 12, 13, 14, 15, 10, 9, 8, 7, 6, 5, 4, 3, 2]", order.toString());
    }
}
