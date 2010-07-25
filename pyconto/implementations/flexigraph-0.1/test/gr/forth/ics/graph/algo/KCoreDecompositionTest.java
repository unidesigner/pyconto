package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import java.util.Arrays;
import java.util.HashSet;
import junit.framework.TestCase;

public class KCoreDecompositionTest extends TestCase {
    
    public KCoreDecompositionTest(String testName) {
        super(testName);
    }

    public void test() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2, 3);

        g.newEdge(n[0], n[1]);
        g.newEdge(n[1], n[2]);
        g.newEdge(n[2], n[3]);

        g.newEdge(n[1], n[2]);
        g.newEdge(n[1], n[1]);
        g.newEdge(n[1], n[1]);

        KCoreDecomposition kcore = KCoreDecomposition.execute(g);

        assertEquals(new HashSet<Node>(Arrays.asList(n[0], n[3])), kcore.getCore(1));
        assertEquals(new HashSet<Node>(Arrays.asList(n[2])), kcore.getCore(2));
        assertEquals(new HashSet<Node>(Arrays.asList(n[1])), kcore.getCore(4));

        assertEquals(3, kcore.getCores().size());

        assertTrue(kcore.getCore(3).isEmpty());
        assertTrue(kcore.getCore(5).isEmpty());
    }
}
