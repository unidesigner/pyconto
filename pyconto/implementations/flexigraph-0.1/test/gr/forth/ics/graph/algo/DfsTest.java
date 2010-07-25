package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import junit.framework.*;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.algo.Dfs.Time;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.PrimaryGraph;

public class DfsTest extends TestCase {
    
    public DfsTest(String testName) {
        super(testName);
    }
    
    static Instance getTreeInstance() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2, 3, 4, 5, 6);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[0], n[2]);
        g.newEdge(n[1], n[3]);
        g.newEdge(n[1], n[4]);
        g.newEdge(n[2], n[5]);
        g.newEdge(n[2], n[6]);
        return new Instance(g, n);
    }
    
    static Instance getComponentsInstance() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2, 3);
        int[][] edges = {
            { 0, 1 },
            { 0, 2 },
            { 1, 2 },
            { 2, 0 },
            { 3, 2 }
        };
        for (int[] a: edges) {
            g.newEdge(n[a[0]], n[a[1]]);
        }
        return new Instance(g, n);
    }
    
    public void testExecute() {
        Instance instance = getTreeInstance();
        InspectableGraph g = instance.g;
        Node[] n = instance.n;
        Dfs dfs = instance.dfs;
        dfs.execute();
        for (Node node : n) {
            Time outTime = dfs.getTime(node);
            for (Node kid : g.adjacentNodes(node, Direction.OUT)) {
                Time kidTime = dfs.getTime(kid);
                assertTrue(outTime.getStart() < kidTime.getStart());
                assertTrue(outTime.getFinish() > kidTime.getFinish());
            }
        }
        int[] startTimes = { 0, 1, 7, 2, 4, 8, 10 };
        int[] finishTimes = { 13, 6, 12, 3, 5, 9, 11 };
        for (int i = 0 ; i < n.length; ++i) {
            Time time = dfs.getTime(n[i]);
            assertEquals(startTimes[i], time.getStart());
            assertEquals(finishTimes[i], time.getFinish());
        }
    }
    
    public void testIsTreeEdge() {
        Instance instance = getTreeInstance();
        instance.dfs.execute();
        for (Edge e : instance.g.edges()) {
            assertTrue(instance.dfs.isTreeEdge(e));
        }
    }
    
    public void testIsVisited() {
        Instance instance = getTreeInstance();
        instance.dfs.execute();
        for (Node n : instance.n) {
            assertTrue(instance.dfs.isVisited(n));
            assertFalse(instance.dfs.isVisiting(n));
        }
    }
    
    public void testEdges() {
        Instance instance = getComponentsInstance();
        Node[] n = instance.n;
        InspectableGraph g = instance.g;
        Dfs dfs = instance.dfs;
        dfs.execute();
        assertTrue(dfs.isTreeEdge(g.edges(n[0], n[1], Direction.OUT).iterator().next()));
        assertTrue(dfs.isTreeEdge(g.edges(n[1], n[2], Direction.OUT).iterator().next()));
        assertTrue(dfs.isBackEdge(g.edges(n[2], n[0], Direction.OUT).iterator().next()));
        assertTrue(dfs.isCrossEdge(g.edges(n[3], n[2], Direction.OUT).iterator().next()));
        assertTrue(dfs.isForwardEdge(g.edges(n[0], n[2], Direction.OUT).iterator().next()));
    }
    
    public void testUndirectedEdges() {
        Instance instance = getComponentsInstance();
        Node[] n = instance.n;
        Dfs dfs = new Dfs(instance.g, Direction.EITHER);
        dfs.execute();
        for (Edge e : instance.g.edges()) {
            if (e.isIncident(n[0]) && e.isIncident(n[2])) {
                assertTrue(dfs.isBackEdge(e));
            } else {
                assertTrue(dfs.isTreeEdge(e));
            }
        }
    }
    
    public void testTreeNumber() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2, 3, 4, 5);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[0], n[2]);
        g.newEdge(n[3], n[4]);
        g.newEdge(n[4], n[5]);
        Dfs dfs = new Dfs(g, Direction.OUT);
        dfs.setStartNode(n[0]);
        dfs.execute();
        assertNotNull(dfs.getComponentIdentifier(n[0]));
        assertEquals(dfs.getComponentIdentifier(n[0]), dfs.getComponentIdentifier(n[1]));
        assertEquals(dfs.getComponentIdentifier(n[1]), dfs.getComponentIdentifier(n[2]));
        assertNotSame(dfs.getComponentIdentifier(n[2]), dfs.getComponentIdentifier(n[3]));
        assertEquals(dfs.getComponentIdentifier(n[3]), dfs.getComponentIdentifier(n[4]));
        assertEquals(dfs.getComponentIdentifier(n[4]), dfs.getComponentIdentifier(n[5]));
    }
    
    public void testNotNullTreeId() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes("1", "2");
        g.newEdge(n[0], n[1]);
        Dfs dfs = new Dfs(g, Direction.OUT) {
            @Override protected boolean visitPre(Path path) {
                assertNotNull(getComponentIdentifier(path.tailNode()));
                return false;
            }
        };
        dfs.execute();
    }
    
    public void testSimple() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes("1", "2", "3");
        g.newEdge(n[0], n[1]);
        g.newEdge(n[1], n[2]);
        g.newEdge(n[2], n[0]);
        final int[] treeEdges = new int[1];
        final int[] backEdges = new int[1];
        Dfs dfs = new Dfs(g, Direction.OUT) {
            @Override public boolean visitTreeEdge(Path path) {
                treeEdges[0]++;
                return false;
            }
            
            @Override public boolean visitBackEdge(Path path) {
                backEdges[0]++;
                return false;
            }
        };
        dfs.execute();
        assertEquals(2, treeEdges[0]);
        assertEquals(1, backEdges[0]);
    }
    
    public void testParent() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[1], n[2]);
        Dfs dfs = new Dfs(g, Direction.OUT);
        dfs.execute();
        assertEquals(n[0], dfs.getParent(n[1]));
        assertEquals(n[1], dfs.getParent(n[2]));
    }
    
    public void testVisitBreaksEarly() {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2, 3);
        g.newEdge(n[1], n[2]);
        g.newEdge(n[2], n[3]);
        
        Dfs dfs = new Dfs(g, n[0], Direction.OUT) {
            @Override
            protected boolean visitTreeEdge(Path path) {
                return true;
            }
        };
        dfs.execute();
        
        assertTrue(dfs.isVisited(n[0]));
        
        assertFalse(dfs.isVisited(n[1]));
        assertTrue(dfs.isVisiting(n[1]));
        
        assertFalse(dfs.isVisited(n[2]));
        assertFalse(dfs.isVisited(n[3]));
    }
    
    static class Instance {
        final InspectableGraph g;
        final Node[] n;
        final Dfs dfs;
        Instance(InspectableGraph g, Node[] n) {
            this.g = g;
            this.n = n;
            this.dfs = new Dfs(g, n[0], Direction.OUT);
        }
    }
}
