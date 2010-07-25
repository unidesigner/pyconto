package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import java.util.HashSet;
import java.util.Set;
import junit.framework.*;

public class BfsTest extends TestCase {
    
    public BfsTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BfsTest.class);
        
        return suite;
    }
    
    private Graph g;
    private Node[] n;
    private Bfs bfs;
    
    protected void setUp() {
        g = new PrimaryGraph();
        n = g.newNodes("0", "1", "2", "3", "4", "5");
        g.newEdge(n[0], n[1]);
        g.newEdge(n[0], n[2]);
        g.newEdge(n[0], n[3]);
        g.newEdge(n[3], n[4]);
        g.newEdge(n[3], n[5]);
        
        bfs = new Bfs(g, Direction.OUT);
        bfs.execute();
    }
    
    public void testLevels() {
        assertEquals(0, bfs.getLevel(n[0]));
        assertEquals(1, bfs.getLevel(n[1]));
        assertEquals(1, bfs.getLevel(n[2]));
        assertEquals(1, bfs.getLevel(n[3]));
        assertEquals(2, bfs.getLevel(n[4]));
        assertEquals(2, bfs.getLevel(n[5]));
    }
    
    public void testParentNodes() {
        assertEquals(null, bfs.getParent(n[0]));
        assertEquals(n[0], bfs.getParent(n[1]));
        assertEquals(n[0], bfs.getParent(n[2]));
        assertEquals(n[0], bfs.getParent(n[3]));
        assertEquals(n[3], bfs.getParent(n[4]));
        assertEquals(n[3], bfs.getParent(n[5]));
    }
    
    public void testVisited() {
        for (Node node : n) {
            assertTrue(bfs.isVisited(node));
        }
    }
    
    public void testParentEdges() {
        assertEquals(null, bfs.getParentEdge(n[0]));
        assertEquals(g.anEdge(n[0], n[1]), bfs.getParentEdge(n[1]));
        assertEquals(g.anEdge(n[0], n[2]), bfs.getParentEdge(n[2]));
        assertEquals(g.anEdge(n[0], n[3]), bfs.getParentEdge(n[3]));
        assertEquals(g.anEdge(n[3], n[4]), bfs.getParentEdge(n[4]));
        assertEquals(g.anEdge(n[3], n[5]), bfs.getParentEdge(n[5]));
    }
    
    public void testCrossEdges() {
        Set<Edge> crossEdges = new HashSet<Edge>();
        crossEdges.add(g.newEdge(n[2], n[1]));
        crossEdges.add(g.newEdge(n[0], n[0]));
        crossEdges.add(g.newEdge(n[5], n[1]));
        
        bfs.execute();
        for (Edge e : g.edges()) {
            assertEquals(e.toString(), crossEdges.contains(e), bfs.isCrossEdge(e));
            assertEquals(e.toString(), !crossEdges.contains(e), bfs.isTreeEdge(e));
        }
    }
    
    public void testComponents() {
        for (int i = 1; i < n.length; i++) {
            assertEquals(bfs.getComponentIdentifier(n[i - 1]), bfs.getComponentIdentifier(n[i]));
        }
        Node outer = g.newNode();
        bfs.execute();
        assertNotSame(bfs.getComponentIdentifier(n[0]), bfs.getComponentIdentifier(outer));
    }
    
    public void testLayerCount() {
        assertEquals(2, bfs.getLayerCount());
        g.newEdge(n[5], g.newNode());
        bfs.execute();
        assertEquals(3, bfs.getLayerCount());
    }
    
    public void testVisitNewTreeBreaksEarly() {
        Bfs bfs = new Bfs(g, n[0], Direction.OUT) {
            @Override
            protected boolean visitNewTree(Node node) {
                return true;
            }
        };
        bfs.execute();
        assertTrue(bfs.isVisited(n[0]));
        
        assertFalse(bfs.isVisited(n[1]));
        assertFalse(bfs.isVisited(n[2]));
        assertFalse(bfs.isVisited(n[3]));
        assertFalse(bfs.isVisited(n[4]));
        assertFalse(bfs.isVisited(n[5]));
    }
    
    public void testVisitTreeEdgeBreaksEarly() {
        Bfs bfs = new Bfs(g, n[0], Direction.OUT) {
            @Override
            protected boolean visitTreeEdge(Path path) {
                return true;
            }
            
        };
        bfs.execute();
        assertTrue(bfs.isVisited(n[0]));
        assertTrue(bfs.isVisited(n[1]));
        
        assertFalse(bfs.isVisited(n[2]));
        assertFalse(bfs.isVisited(n[3]));
        assertFalse(bfs.isVisited(n[4]));
        assertFalse(bfs.isVisited(n[5]));
    }

    public void testVisitCrossEdgeBreaksEarly() {
        g.newEdge(n[1], n[2]);
        Bfs bfs = new Bfs(g, n[0], Direction.OUT) {
            @Override
            protected boolean visitCrossEdge(Path path) {
                return true;
            }
        };
        bfs.execute();
        assertTrue(bfs.isVisited(n[0]));
        assertTrue(bfs.isVisited(n[1]));
        assertTrue(bfs.isVisited(n[2]));
        assertTrue(bfs.isVisited(n[3]));
        
        assertFalse(bfs.isVisited(n[4]));
        assertFalse(bfs.isVisited(n[5]));
    }
    
    public void testCrossEdges2() {
        Graph g = new PrimaryGraph();
        
        Node n1 = g.newNode("1");
        Node n2 = g.newNode("2");
        Node n3 = g.newNode("3");
        Node n4 = g.newNode("4");
        
        Set<Edge> tree = new HashSet<Edge>();
        Set<Edge> cross = new HashSet<Edge>();
        
        tree.add(g.newEdge(n1, n2));
        tree.add(g.newEdge(n1, n3));
        cross.add(g.newEdge(n2, n3));
        tree.add(g.newEdge(n2, n4));
        cross.add(g.newEdge(n3, n4));
        
        Bfs bfs = new Bfs(g, n1, Direction.OUT);
        bfs.execute();
        
        for (Edge e : g.edges()) {
            assertEquals(e.toString(), tree.contains(e), bfs.isTreeEdge(e));
            assertEquals(e.toString(), cross.contains(e), bfs.isCrossEdge(e));
        }
    }
}
