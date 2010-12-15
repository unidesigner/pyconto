package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graph;
import junit.framework.*;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import java.util.Collection;

public class LayerFinderBfsTest extends TestCase {
    
    public LayerFinderBfsTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(LayerFinderBfsTest.class);
        
        return suite;
    }
    
    public void test() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes("0", "1", "2", "3", "4", "5");
        g.newEdge(n[0], n[1]);
        g.newEdge(n[0], n[2]);
        g.newEdge(n[0], n[3]);
        g.newEdge(n[3], n[4]);
        g.newEdge(n[3], n[5]);
        
        Node[] n2 = g.newNodes("a", "b");
        g.newEdge(n2[0], n2[1]);
        
        Layerer bfs = Layerers.bfs(g, Direction.OUT);
        
        Collection<Node> layer0 = bfs.getLayer(0);
        Collection<Node> layer1 = bfs.getLayer(1);
        Collection<Node> layer2 = bfs.getLayer(2);
        
        assertEquals(2, layer0.size());
        assertTrue(layer0.contains(n[0]));
        assertTrue(layer0.contains(n2[0]));
        
        assertEquals(4, layer1.size());
        assertTrue(layer1.contains(n[1]));
        assertTrue(layer1.contains(n[2]));
        assertTrue(layer1.contains(n[3]));
        assertTrue(layer1.contains(n2[1]));
        
        assertEquals(2, layer2.size());
        assertTrue(layer2.contains(n[4]));
        assertTrue(layer2.contains(n[5]));

        for (Node node : g.nodes()) {
            assertTrue(bfs.getLayer(bfs.findLayerOf(node)).contains(node));
        }
    }
}
