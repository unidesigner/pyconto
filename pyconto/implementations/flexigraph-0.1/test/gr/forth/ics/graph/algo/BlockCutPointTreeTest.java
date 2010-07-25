package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.event.EmptyGraphListener;
import gr.forth.ics.graph.event.GraphEvent;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.PrimaryGraph;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class BlockCutPointTreeTest extends TestCase {
    
    public BlockCutPointTreeTest(String testName) {
        super(testName);
    }

    public void testBiconnectedBecomesSingleNode() {
        Graph g = new PrimaryGraph();
        Generators.createRandomBiconnectedGraph(g, 50);
        BlockCutPointTree tree = BlockCutPointTree.execute(g);
        assertTrue(tree.get().nodeCount() == 1);
        assertTrue(tree.get().edgeCount() == 0);
    }

    public void testSeveralBiconnectedComponents() {
        Graph g = new PrimaryGraph();
        final LinkedList<Node> nodes = new LinkedList<Node>();
        g.addNodeListener(new EmptyGraphListener() {
            @Override
            public void nodeAdded(GraphEvent e) {
                nodes.addFirst(e.getNode());
            }
        });
        Graphs.attachNodeNamer(g);
        Random random = new Random(0);
        Node last = null;
        final int biconnectedComponents = 10;
        for (int i = 0; i < biconnectedComponents; i++) {
            Generators.createRandomBiconnectedGraph(g, random, random.nextInt(10) + 1);
            if (last != null) {
                //connecting the biconnected components
                //this will create another trivial biconnected component, and two cut nodes
                g.newEdge(last, nodes.getFirst());
            }
            last = nodes.getFirst();
            nodes.clear();
        }

        BlockCutPointTree tree = BlockCutPointTree.execute(g);
        assertEquals(
                biconnectedComponents /* one node per biconnected component */
                + biconnectedComponents - 1/* each connecting edge is also a biconnected component */
                + biconnectedComponents /* one cut node per component */
                ,
                tree.get().nodeCount());

        for (Edge e : tree.get().edges()) {
            //each edge has exactly one cut node and one block node
            assertTrue(tree.isBlock(e.n1()) ^ tree.isBlock(e.n2()));
            assertTrue(tree.isCutNode(e.n1()) ^ tree.isCutNode(e.n2()));

            //if a node in the tree is marked as a cut node, then it must point to a cut node in the biconnectivity
            assertTrue(!tree.isCutNode(e.n1()) || tree.getBiconnectivity().isCutNode(e.n1()));
            assertTrue(!tree.isCutNode(e.n2()) || tree.getBiconnectivity().isCutNode(e.n2()));
        }
    }
}
