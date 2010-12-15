package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import junit.framework.*;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.algo.Generators;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PathTest extends TestCase {
    
    public PathTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(PathTest.class);

        return suite;
    }
    private Graph g = new PrimaryGraph();
    private Node root;
    private Node[] n;
    private Path path;
    
    protected void setUp() {
        this.n = g.newNodes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        this.root = n[0];
        this.path = root.asPath();
    }
    
    private Edge newEdge(int index) {
        return g.newEdge(n[index], n[index + 1]);
    }
    
    private Edge newEdge(int n1, int n2) {
        return g.newEdge(n[n1], n[n2]);
    }

    public void testToString() {
        assertEquals("[1]", path.toString());
        path = path.append(newEdge(0, 1).asPath());
        assertEquals("[1-->2]", path.toString());

        Edge e = newEdge(2, 1);
        path = path.append(e.asPath(e.n2()));
        assertEquals("[1-->2<--3]", path.toString());
    }
    
    public void testRoot() {
        assertEquals(root, path.headNode());
        Edge e = g.newEdge(n[1], n[0]);
        Path p = e.asPath();
        assertEquals(n[1], p.headNode());
    }
    
    public void testSize() {
        assertEquals(0, path.size());
        checkSizeInvariants();
        path = path.append(newEdge(0).asPath());
        assertEquals(1, path.size());
        checkSizeInvariants();
        path = path.append(newEdge(1).asPath());
        assertEquals(2, path.size());
        checkSizeInvariants();
    }
    
    private void checkSizeInvariants() {
        assertEquals(path.size(), path.edgeCount());
        assertEquals(path.size() + 1, path.nodeCount());
    }
    
    public void testEdge() {
        try {
            path.tailEdge();
            fail();
        } catch (NoSuchElementException ok) { }
        assertEquals(n[0], path.headNode());
        assertEquals(n[0], path.tailNode());
        Edge e = newEdge(0);
        path = path.append(e.asPath());
        assertEquals(e, path.tailEdge());
        assertEquals(e.n2(), path.tailNode());
        e = newEdge(1);
        path = path.append(e.asPath());
        assertEquals(e, path.tailEdge());
        assertEquals(e.n2(), path.tailNode());
    }
    
    public void testHamiltonEuler() {
        path = path.append(newEdge(0, 1).asPath());
        //0--1
        assertTrue(path.isHamilton());
        assertTrue(path.isEuler());

        path = path.append(newEdge(1, 0).asPath());
        //0--1--0
        assertFalse(path.isHamilton());
        assertTrue(path.isEuler());

        path = path.append(newEdge(0, 2).asPath());
        //0--1--0--2
        assertFalse(path.isHamilton());
        assertTrue(path.isEuler());

        path = path.append(newEdge(2, 1).asPath());
        //0--1--0--2--1
        assertFalse(path.isHamilton());
        assertTrue(path.isEuler());

        path = path.append(g.anEdge(n[1], n[0]).asPath());
        //0--1--0--2--1--0
        assertFalse(path.isHamilton());
        assertFalse(path.isEuler());
    }
    
    public void testNodes() {
        Edge[] edges = { newEdge(0), newEdge(1), newEdge(2), newEdge(3) };
        for (Edge e : edges) {
            path = path.append(e.asPath());
        }
        List<Node> nodes = path.nodes().drainToList();
        for (int i = 0; i < edges.length; i++) {
            assertEquals(edges[i].n1(), nodes.get(i));
        }
        assertEquals(edges[edges.length - 1].n2(), nodes.get(nodes.size() - 1));
    }
    
    public void testSlices() {
        Edge[] edges = { newEdge(0), newEdge(1), newEdge(2), newEdge(3) };
        for (Edge e : edges) {
            path = path.append(e.asPath());
        }
        Path p2 = path.slice(1, 3);
        assertEquals(2, p2.size());
        assertEquals(edges[1], p2.steps().iterator().next().tailEdge());
        assertEquals(edges[2], p2.tailEdge());
        try {
            path.slice(2, 1);
            fail("Allowed illegal path slice arguments");
        } catch (RuntimeException e) {
            //ok
        }
        
        p2 = path.slice(1, 1);
        assertEquals(0, p2.size());
        assertEquals(p2.headNode(), p2.tailNode());
        assertEquals(n[1], p2.headNode());
        
        p2 = path.headPath(1);
        assertEquals(1, p2.size());
        assertEquals(n[0], p2.headNode());
        assertEquals(n[1], p2.tailNode());
        try {
            path.headPath(-1);
            fail("Allowed illegal head path argument");
        } catch (RuntimeException e) {
            //ok
        }
        
        p2 = path.tailPath(1);
        assertEquals(1, p2.size());
        assertEquals(n[4], p2.tailNode());
        assertEquals(n[3], p2.headNode());
        
        try {
            path.tailPath(-1);
            fail("Allowed illegal tail path argument");
        } catch (RuntimeException e) {
            //ok
        }
    }
    
    public void testAppend() {
        Path p2 = n[1].asPath();
        p2 = p2.append(newEdge(1).asPath());
        
        try {
            Path p3 = path.append(p2);
            fail("Allowed illegal path concatenation");
        } catch (RuntimeException e) {
            //ok
        }
        Path p3 = newEdge(0).asPath();
        path = path.append(p3);
        assertEquals(1, path.size());
        path = path.append(newEdge(1).asPath());
        assertEquals(2, path.size());
        path = path.tailPath(1);
        assertEquals(1, path.size());
        assertEquals(n[1], path.headNode());
        assertEquals(n[2], path.tailNode());
    }
    
    public void testIsCycle() {
        assertFalse(path.isCycle());
        path = path.append(newEdge(0).asPath());
        assertFalse(path.isCycle());
        path = path.append(newEdge(1, 0).asPath());
        assertTrue(path.isCycle());
        path = path.append(newEdge(0).asPath());
        assertFalse(path.isCycle());
        
        assertTrue(path.split(1)[1].isCycle());
    }
    
    public void testReplaceFirst() {
        path = path.append(newEdge(0).asPath()).append(newEdge(1).asPath());
        Path p = path.replaceFirst(g.anEdge(n[0], n[1]).asPath(), n[1].asPath());
        assertEquals(1, p.size());
        assertEquals(n[1], p.headNode());
        assertEquals(n[2], p.tailNode());
        
        try {
            Path p2 = path.replaceFirst(g.anEdge(n[0], n[1]).asPath(), n[0].asPath());
            fail("Illegal replacement path allowed");
        } catch (RuntimeException e) {
            //ok
        }
        
        try {
            Path p3 = path.replaceFirst(g.anEdge(n[0], n[1]).asPath(), g.newEdge(n[0], n[2]).asPath());
            fail("Illegal replacement path allowed");
        } catch (RuntimeException e) {
            //ok
        }
    }
    
    public void testReplace() {
        Path replacement = n[0].asPath().append(newEdge(0, 1).asPath()).append(newEdge(1, 0).asPath());
        Path trivial = path.replace(0, 0, replacement);
        assertEquals(2, trivial.size());
        assertTrue(trivial.isCycle());
        
        Path repl2 = n[0].asPath().append(newEdge(0, 5).asPath()).append(newEdge(5, 1).asPath());
        Path p = trivial.replace(0, 1, repl2);
        assertEquals(3, p.size());
        assertEquals(n[0], p.headNode());
        assertEquals(n[0], p.tailNode());
        assertEquals(n[5], p.getNode(1));
    }
    
    public void testReplaceAll() {
        Path p = n[0].asPath().append(newEdge(0, 1).asPath()).append(newEdge(1, 0).asPath()).append(newEdge(0, 1).asPath());
        Path replacement1 = n[0].asPath().append(newEdge(0, 2).asPath()).append(newEdge(2, 0).asPath());
        Path result1 = p.replaceAll(p.headPath(2), replacement1);
        assertEquals(3, result1.size());
        assertEquals(n[0], result1.getNode(0));
        assertEquals(n[2], result1.getNode(1));
        assertEquals(n[0], result1.getNode(2));
        assertEquals(n[1], result1.getNode(3));
        
        Path result2 = p.replaceAll(n[0].asPath(), replacement1);
        //was: 0--1--0--1
        //--->  0--2--0--1--0--2--0--1
        assertEquals(7, result2.size());
        assertEquals(n[0], result2.getNode(0));
        assertEquals(n[2], result2.getNode(1));
        assertEquals(n[0], result2.getNode(2));
        assertEquals(n[1], result2.getNode(3));
        assertEquals(n[0], result2.getNode(4));
        assertEquals(n[2], result2.getNode(5));
        assertEquals(n[0], result2.getNode(6));
        assertEquals(n[1], result2.getNode(7));
    }
    
    public void testFindAndContains() {
        path = path.append(newEdge(0).asPath()).append(newEdge(1).asPath());
        Path temp;
        temp = g.anEdge(n[0], n[1]).asPath();
        assertEquals(0, path.find(temp));
        assertTrue(path.contains(temp));
        
        temp = g.anEdge(n[1], n[2]).asPath();
        assertEquals(1, path.find(temp));
        assertTrue(path.contains(temp));
        
        temp = path;
        assertEquals(0, path.find(temp));
        assertTrue(path.contains(temp));
        
        temp = path;
        assertEquals(-1, path.find(path, 1));
        assertTrue(path.contains(temp));
        
        temp = n[2].asPath();
        assertEquals(2, path.find(temp));
        assertTrue(path.contains(temp));
    }
    
    public void testEquality() {
        checkEqual(path, path, true);
        checkEqual(path, n[0].asPath(), true);
        Path p2 = path.append(newEdge(0).asPath());
        checkEqual(path, p2, false);
        
        
        path = path.append(g.anEdge(n[0], n[1]).asPath());
        checkEqual(path, p2, true);
        
        path = path.tailPath(0);
        checkEqual(path, p2, false);
        
        p2 = p2.tailPath(0);
        checkEqual(path, p2, true);
    }
    
    private void checkEqual(Object o, Object o2, boolean mustBeEqual) {
        assertEquals(mustBeEqual, o.equals(o2));
        assertEquals(mustBeEqual, o2.equals(o));
        if (mustBeEqual) {
            assertEquals(o.hashCode(), o2.hashCode());
        }
    }
    
    public void testIndexedGets() {
        Edge[] edges = { newEdge(0), newEdge(1), newEdge(2), newEdge(3) };
        for (Edge e : edges) {
            path = path.append(e.asPath());
        }
        checkIndexedGets(path, edges, false);
    }
    
    private void checkIndexedGets(Path path, Edge[] edges, boolean inverted) {
        assertEquals(edges.length, path.edgeCount());
        for (int i = 0; i < edges.length; i++) {
            Node root = inverted ? edges[i].n2() : edges[i].n1();
            Node target = edges[i].opposite(root);
            assertEquals(root, path.getNode(i));
            assertEquals(edges[i], path.getEdge(i));
            assertEquals(target, path.getNode(i + 1));
        }
    }
    
    public void testInverse() {
        Edge[] edges = { newEdge(0), newEdge(1), newEdge(2), newEdge(3) };
        for (Edge e : edges) {
            path = path.append(e.asPath());
        }
        path = path.reverse();
        checkIndexedGets(path, new Edge[] { edges[3], edges[2], edges[1], edges[0] }, true);
        assertEquals(n[4], path.headNode());
        assertEquals(path.getEdge(0), g.anEdge(n[4], n[3]));
        assertEquals(path.getEdge(1), g.anEdge(n[3], n[2]));
        assertEquals(path.getEdge(2), g.anEdge(n[2], n[1]));
        assertEquals(path.getEdge(3), g.anEdge(n[1], n[0]));
    }
    
    public void testSplit() {
        Edge[] edges = { newEdge(0), newEdge(1), newEdge(2), newEdge(3) };
        for (Edge e : edges) {
            path = path.append(e.asPath());
        }
        Path[] paths = path.split(2);
        assertEquals(2, paths[0].size());
        assertEquals(2, paths[1].size());
        assertEquals(n[0], paths[0].headNode());
        assertEquals(n[2], paths[0].tailNode());
        assertEquals(n[2], paths[1].headNode());
        assertEquals(n[4], paths[1].tailNode());
        
        paths = paths[1].split(2);
        assertEquals(2, paths[0].size());
        assertEquals(0, paths[1].size());
        assertEquals(n[4], paths[1].headNode());
        assertEquals(n[4], paths[1].tailNode());
    }
    
    public void testDefaultEdgePathOrientation() {
        Graph g = new PrimaryGraph();
        Node n1 = g.newNode();
        Node n2 = g.newNode();
        Edge e = g.newEdge(n1, n2);
        Path p = e.asPath();
        assertEquals(n1, p.headNode());
        assertEquals(n2, p.tailNode());
    }

    public void testNegativeNodeIndex() {
        final Graph g = new PrimaryGraph();
        final Path path = Generators.createPath(g, 5);
        new NegativeIndexChecker<Node>(path.nodeCount()) {
            @Override
            Node get(int index) {
                return path.getNode(index);
            }
        }.assertOk();
    }

    public void testNegativeEdgeIndex() {
        final Graph g = new PrimaryGraph();
        final Path path = Generators.createPath(g, 5);
        new NegativeIndexChecker<Edge>(path.edgeCount()) {
            @Override
            Edge get(int index) {
                return path.getEdge(index);
            }
        }.assertOk();
    }

    private static abstract class NegativeIndexChecker<T> {
        private final List<T> expected = new ArrayList<T>();
        NegativeIndexChecker(int size) {
            for (int i = 0; i < size; i++) {
                expected.add(get(i));
            }
        }

        abstract T get(int index);

        void assertOk() {
            int expectedIndex = 0;
            for (int i = 0, min = -expected.size(); i >= min; i--) {
                assertSame(expected.get(expectedIndex), get(i));
                if (expectedIndex == 0) {
                    expectedIndex = expected.size() - 1;
                } else {
                    expectedIndex--;
                }
            }
            try {
                get(-expected.size() - 1);
                fail();
            } catch (Exception ok) { }
            try {
                get(expected.size() + 1);
                fail();
            } catch (Exception ok) { }
        }
    }

    public void testNegativeIndexInSingleEdge() {
        Graph g = new PrimaryGraph();
        Edge e = g.newEdge(g.newNode(), g.newNode());
        assertSame(e.n2(), e.asPath().getNode(-1));
        assertSame(e.n1(), e.asPath().getNode(-2));
    }
}