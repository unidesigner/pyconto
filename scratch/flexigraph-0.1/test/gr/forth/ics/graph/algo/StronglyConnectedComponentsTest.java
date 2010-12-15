package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.algo.Clusterer;
import gr.forth.ics.graph.algo.Clusterers;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class StronglyConnectedComponentsTest extends TestCase {
    
    public StronglyConnectedComponentsTest(String testName) {
        super(testName);
    }

    public void test() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2, 3, 4, 5, 6, 7, 8);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[1], n[2]);
        g.newEdge(n[2], n[1]);
        
        g.newEdge(n[3], n[4]);
        g.newEdge(n[4], n[5]);
        g.newEdge(n[5], n[3]);
        
        g.newEdge(n[7], n[6]);
        g.newEdge(n[8], n[7]);
        Clusterer scc = Clusterers.stronglyConnectedComponents(g);
        
        assertEquals(6, scc.getClusters().size());
        Set<Set<Node>> clusters = new HashSet<Set<Node>>();
        for (Object c : scc.getClusters()) {
            clusters.add(new HashSet<Node>(scc.getCluster(c)));
        }
        
        assertTrue(clusters.contains(new HashSet<Node>(Arrays.asList(n[1], n[2]))));
        assertTrue(clusters.contains(new HashSet<Node>(Arrays.asList(n[0]))));
        assertTrue(clusters.contains(new HashSet<Node>(Arrays.asList(n[3], n[4], n[5]))));
        assertTrue(clusters.contains(new HashSet<Node>(Arrays.asList(n[6]))));
        assertTrue(clusters.contains(new HashSet<Node>(Arrays.asList(n[7]))));
    }
}
