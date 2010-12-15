package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.algo.Clusterer;
import gr.forth.ics.graph.algo.Clusterers;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.SecondaryGraph;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import junit.framework.*;

public class ConnectedComponentsTest extends TestCase {
    
    public ConnectedComponentsTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ConnectedComponentsTest.class);
        
        return suite;
    }
    
    public void test() {
        Graph g1 = new PrimaryGraph();
        Node[] n1 = g1.newNodes(2);
        g1.newEdge(n1[0], n1[1]);
        
        Graph g2 = new PrimaryGraph();
        Node[] n2 = g2.newNodes(2);
        g2.newEdge(n2[0], n2[1]);
        
        SecondaryGraph sg = new SecondaryGraph();
        sg.adoptGraph(g1);
        sg.adoptGraph(g2);
        
        Clusterer cc = Clusterers.connectedComponents(sg);
        
        Collection<Object> components = cc.getClusters();
        assertEquals(2, components.size());
        
        Iterator<Object> iterator = components.iterator();
        Object c1 = iterator.next();
        Object c2 = iterator.next();
        assertTrue(c1 != c2);
        assertTrue(components.contains(c1));
        assertTrue(components.contains(c2));
        
        Collection<Node> comp1 = cc.getCluster(c1);
        assertTrue(comp1.containsAll(Arrays.asList(n1)));
        
        Collection<Node> comp2 = cc.getCluster(c2);
        assertTrue(comp2.containsAll(Arrays.asList(n2)));
        
        assertTrue(cc.getCluster(c1).contains(n1[0]));
        assertTrue(cc.getCluster(c2).contains(n2[0]));
    }

    public void testRandomized() {
        Graph g = new PrimaryGraph();
        Node root1 = Generators.createRandomTree(g, 10, Direction.OUT);
        Node root2 = Generators.createRandomTree(g, 10, Direction.IN);

        Clusterer cc = Clusterers.connectedComponents(g);
        assertEquals(2, cc.getClusters().size());

        g.newEdge(root1, root2);
        cc = Clusterers.connectedComponents(g);
        assertEquals(1, cc.getClusters().size());    }
}
