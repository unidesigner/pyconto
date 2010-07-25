package gr.forth.ics.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import junit.framework.*;

public class FoldingGraphTest extends GraphTest {
    private Graph g;
    private FoldingGraph hg;
    private Node[] n;
    
    public FoldingGraphTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        g = new PrimaryGraph();
        n = g.newNodes(1, 2, 3, 4, 5);
        Edge e12 = g.newEdge(n[0], n[1]);
        Edge e13 = g.newEdge(n[0], n[2]);
        Edge e14 = g.newEdge(n[0], n[3]);
        Edge e25 = g.newEdge(n[1], n[4]);
        Edge e35 = g.newEdge(n[2], n[4]);
        Edge e45 = g.newEdge(n[3], n[4]);
        hg = new FoldingGraph(g);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(FoldingGraphTest.class);
        
        return suite;
    }
    
    protected boolean isPrimary() {
        return true;
    }
    
    protected Graph create() {
        return new FoldingGraph(new PrimaryGraph());
    }
    
    public void testInterFolderEdge1() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(1, 2);
        Edge e = g.newEdge(n[0], n[1]);
        FoldingGraph hg = new FoldingGraph(g);
        Node folder1 = hg.fold(n[0]);
        Node folder2 = hg.fold(n[1]);
        folder1.setValue("folder1");
        folder2.setValue("folder2");
        
        hg.unfold(folder1);
        assertFalse(hg.containsNode(folder1));
        assertFalse(hg.containsEdge(e));
        hg.unfold(folder2);
        assertFalse(hg.containsNode(folder2));
        assertTrue(hg.containsEdge(e));
    }
    
    public void testInterFolderEdge2() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(1, 2);
        Edge e = g.newEdge(n[0], n[1]);
        FoldingGraph hg = new FoldingGraph(g);
        Node folder1 = hg.fold(n[0]);
        Node folder2 = hg.fold(n[1]);
        folder1.setValue("folder1");
        folder2.setValue("folder2");
        
        hg.unfold(folder2);
        assertFalse(hg.containsNode(folder2));
        assertFalse(hg.containsEdge(e));
        hg.unfold(folder1);
        assertFalse(hg.containsNode(folder1));
        assertTrue(hg.containsEdge(e));
    }
    
    public void testFolder() {
        Node folder = hg.fold(Arrays.asList(n[1]));
        assertTrue(g.containsNode(folder));
    }
    
    public void testRealEdge() {
        Set<Edge> edges = g.edges(n[0]).drainToSet();
        Node folder = hg.fold(n[0]);
        Node folder2 = hg.fold(folder);
        for (Edge e : hg.edges(folder2)) {
            assertTrue(edges.remove(hg.getRealEdge(e)));
        }
        //all real edges found
        assertTrue(edges.isEmpty());
    }
    
    public void testEmptyFold() {
        Node folder = hg.fold();
        assertEquals(6, g.nodeCount());
    }
    
    public void testFold() {
        Node folder = hg.fold(Arrays.asList(n[1], n[2], n[3]));
        for (Node n : hg.nodes()) {
            assertEquals(n == folder, hg.isFolder(n));
        }
        hg.unfold(folder);
        assertFalse(hg.containsNode(folder));
        assertEquals(5, hg.nodeCount());
    }
    
    public void testDegree() {
        Node folder = hg.fold(Arrays.asList(n[1], n[2], n[3]));
        assertEquals(6, hg.degree(folder));
    }
    
    public void testEdges() {
        Node folder = hg.fold(Arrays.asList(n[1], n[2], n[3]));
        Collection<Edge> startToFolderEdges = hg.edges(n[0], folder).drainToSet();
        assertEquals(3, startToFolderEdges.size());
    }
    
    public void testCount() {
        Node[] nodes = { n[1], n[2], n[3] };
        Node folder = hg.fold(Arrays.asList(nodes));
        assertEquals(3, hg.nodeCount());
        assertEquals(6, hg.edgeCount());
    }
    
    public void testContainment() {
        Node[] nodes = { n[1], n[2], n[3] };
        Node folder = hg.fold(Arrays.asList(nodes));
        Collection<Node> foldedNodes = hg.viewFolder(folder).nodes().drainToSet();
        assertEquals(3, foldedNodes.size());
        for (int i = 0; i < nodes.length; ++i) {
            assertTrue(hg.viewFolder(folder).containsNode(nodes[i]));
        }
    }
    
    public void testWrongUnfold() {
        Node[] nodes = { n[1], n[2], n[3] };
        Node folder = hg.fold(Arrays.asList(nodes));
        try {
            hg.unfold(nodes[1]);
            fail();
        } catch (IllegalArgumentException ok) {
        }
    }
    
    public void testWrongContentsView() {
        try {
            hg.viewFolder(n[1]);
            fail();
        } catch (IllegalArgumentException ok) {
        }
    }
    
    public void testSubfolding() {
        Node subfolder = hg.fold(Arrays.asList(n[1], n[2], n[3]));
        subfolder.setValue("subfolder");
        Node folder = hg.fold(Arrays.asList(subfolder, n[4]));
        folder.setValue("folder");
        
        assertEquals(folder, hg.getParent(subfolder));
        assertEquals(null, hg.getParent(folder));
        
        assertEquals(2, hg.nodeCount());
        assertEquals(3, hg.edgeCount());
        assertTrue(g.containsNode(folder));
        assertTrue(g.containsNode(n[0]));
        assertFalse(g.containsNode(subfolder));
        int edges = 0;
        int selfLoops = 0;
        for (Edge e : g.edges()) {
            if (e.isSelfLoop()) {
                selfLoops++;
            } else {
                edges++;
            }
        }
        assertEquals(3, edges);
        assertEquals(0, selfLoops);
        assertEquals(3, g.degree(n[0]));
        assertEquals(3, g.degree(folder));
        
        hg.unfold(folder);
        assertEquals(null, hg.getParent(subfolder));
        assertEquals(3, hg.nodeCount());
        assertEquals(6, hg.edgeCount());
        
        hg.unfold(subfolder);
        assertEquals(5, g.nodeCount());
        assertEquals(6, g.edgeCount());
        assertEquals(3, g.degree(n[0]));
        assertEquals(2, g.degree(n[1]));
        assertEquals(2, g.degree(n[2]));
        assertEquals(2, g.degree(n[3]));
        assertEquals(3, g.degree(n[4]));
    }
    
    public void testInnerUnfolding() {
        Node subfolder = hg.fold(Arrays.asList(n[1], n[2], n[3]));
        subfolder.setValue("subfolder");
        Node folder = hg.fold(Arrays.asList(subfolder, n[4]));
        folder.setValue("folder");
        
        hg.unfold(subfolder);
        hg.unfold(folder);
        assertEquals(5, hg.nodeCount());
        assertEquals(6, hg.edgeCount());
        for (int i = 0; i < 5; ++i) {
            assertTrue(hg.containsNode(n[i]));
        }
        assertFalse(hg.containsNode(folder));
        assertFalse(hg.containsNode(subfolder));
        
        assertEquals(3, hg.degree(n[0]));
        assertEquals(2, hg.degree(n[1]));
        assertEquals(2, hg.degree(n[2]));
        assertEquals(2, hg.degree(n[3]));
        assertEquals(3, hg.degree(n[4]));
    }
    
    public void testSubSubFolding() {
        Node f1 = hg.fold(Arrays.asList(n[2], n[3]));
        f1.setValue("f1");
        assertEquals(4, hg.nodeCount());
        assertEquals(6, hg.edgeCount());
        assertEquals(2, hg.inDegree(f1));
        assertEquals(2, hg.outDegree(f1));
        InspectableGraph inner = hg.viewFolder(f1);
        assertEquals(2, inner.nodeCount());
        assertEquals(0, inner.edgeCount());
        
        Node f2 = hg.fold(Arrays.asList(f1, n[0], n[4]));
        f2.setValue("f2");
        assertEquals(2, hg.nodeCount());
        assertEquals(2, hg.edgeCount());
        inner = hg.viewFolder(f2);
        assertEquals(3, inner.nodeCount());
        assertEquals(4, inner.edgeCount());
        
        Node n5 = g.newNode("n5");
        Edge edgeToFolder = g.newEdge(n5, f2);
        
        Node f3 = hg.fold(hg.nodes());
        f3.setValue("f3");
        assertEquals(1, hg.nodeCount());
        assertEquals(0, hg.edgeCount());
        
        hg.unfold(f3);
        assertEquals(3, hg.nodeCount());
        assertEquals(3, hg.edgeCount());
        
        hg.unfold(f2);
        assertFalse(hg.containsEdge(edgeToFolder)); //this edge must have been deleted
        hg.unfold(f1);
        assertEquals(6, hg.nodeCount());
        assertEquals(6, hg.edgeCount());
    }
    
    public void testMultipleKids() {
        Node f1 = hg.fold(Arrays.asList(n[0], n[1]));
        Node f2 = hg.fold(Arrays.asList(n[3], n[4]));
        Node f3 = hg.fold(hg.nodes());
        assertEquals(f1, hg.getParent(n[0]));
        assertEquals(f1, hg.getParent(n[1]));
        assertEquals(f2, hg.getParent(n[3]));
        assertEquals(f2, hg.getParent(n[4]));
        assertEquals(f3, hg.getParent(f1));
        assertEquals(f3, hg.getParent(f2));
    }
    
    public void testSynthetic() {
        Edge e = g.newEdge(n[1], n[2]);
        Node innerFolder = hg.fold(Arrays.asList(n[2], n[3]));
        assertEquals(hg.getRealEdge(hg.anEdge(n[1], innerFolder)), e);
        Node folder = hg.fold(Arrays.asList(n[0], innerFolder));
        //two level nesting
        assertEquals(hg.getRealEdge(hg.getRealEdge(hg.anEdge(n[1], folder, Direction.OUT))), e);
    }
    
    public void testWorksWithSecondary() {
        SecondaryGraph sg = new SecondaryGraph(g);
        FoldingGraph hg = new FoldingGraph(sg, false);
        hg.fold(Arrays.asList(n[1], n[2], n[3]));
    }
    
    public void testRemoveAndReinsertFolder() {
        Node subfolder = hg.fold(Arrays.asList(n[1], n[2], n[3]));
        subfolder.setValue("subfolder");
        Node folder = hg.fold(Arrays.asList(subfolder, n[4]));
        folder.setValue("folder");
        Graph subgraph = hg.viewFolder(folder);
        subgraph.removeNode(subfolder);
        hg.unfold(folder);
        assertFalse(hg.containsNode(subfolder));
    }
}
