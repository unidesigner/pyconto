package gr.forth.ics.graph;

import gr.forth.ics.graph.Graph.OrderManager;
import gr.forth.ics.graph.event.EdgeListener;
import gr.forth.ics.graph.event.EmptyGraphListener;
import gr.forth.ics.graph.event.GraphEvent;
import gr.forth.ics.graph.event.GraphListener;
import gr.forth.ics.graph.event.NodeListener;

import gr.forth.ics.graph.algo.Generators;
import java.util.*;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import gr.forth.ics.util.DVMap;
import gr.forth.ics.util.ExtendedIterable;
import static gr.forth.ics.util.Sample.*;
import static gr.forth.ics.graph.Graphs.printCompact;

public abstract class GraphTest extends MockObjectTestCase {
    public GraphTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        return suite;
    }
    
    protected abstract boolean isPrimary();
    
    protected abstract Graph create();
    
    public void testIterateNodes() {
        Graph g = create();
        Set<Node> nodes = new HashSet<Node>(Arrays.asList(g.newNodes(10)));
        for (Node n : g.nodes()) {
            nodes.remove(n);
        }
        assertEquals("All nodes iterated", 0, nodes.size());
    }
    
    public void testIterateEdges() {
        Graph g = create();
        Generators.createRandom(g, 100, 0.5);
        Set<Edge> edges = new HashSet<Edge>();
        Collection<Edge> edgesBag = new LinkedList<Edge>();
        for (Edge e : g.edges()) {
            edges.add(e);
            edgesBag.add(e);
        }
        final int edgeCount = g.edgeCount();
        assertEquals("All edges should appear exactly once", edgeCount, edgesBag.size());
        assertEquals("All edges should appear exactly once", edgeCount, edges.size());
        assertEquals(edgeCount, g.edgeCount());
    }
    
    public void testParallel() {
        Graph graph = create();
        Node n1 = graph.newNode();
        Node n2 = graph.newNode();
        assertEquals(2, graph.nodeCount());
        
        Edge e1 = graph.newEdge(n1, n2);
        Edge e2 = graph.newEdge(n1, n2);
        assertEquals(2, graph.edgeCount());
    }
    
    /**
     * Tests, in a parallel graph with one node and a self loop, if the creation of a new edge
     * to the graph will yield a new edge reference
     */
    public void testReturnBagEdge() {
        Graph parallelGraph = create();
        Node n1 = parallelGraph.newNode();
        Edge e1 = parallelGraph.newEdge(n1, n1);
        Edge e2 = parallelGraph.newEdge(n1, n1);
        assertNotSame(e1, e2);
        assertEquals(2, parallelGraph.edgeCount());
    }
    
    public void testRemoveAllNodes() {
        Graph g = create();
        g.newNode();
        g.newNode();
        int count = g.removeAllNodes();
        assertEquals(0, g.nodeCount());
        assertFalse(g.nodes().iterator().hasNext());
        assertEquals(2, count);
    }
    
    public void testRemoveAllEdges() {
        Graph g = create();
        Node n1 = g.newNode("n1");
        Node n2 = g.newNode("n2");
        g.newEdge(n1, n2);
        g.newEdge(n2, n1);
        int count = g.removeAllEdges();
        assertEquals(0, g.edgeCount());
        assertFalse(g.edges().iterator().hasNext());
        assertEquals(2, count);
    }
    
    public void testRemoveSubset() {
        int preNodeCount = graph.nodeCount();
        int preEdgeCount = graph.edgeCount();
        Node[] nodes = graph.newNodes(3);
        Edge[] edges = {
            graph.newEdge(nodes[0], nodes[1]),
            graph.newEdge(nodes[1], nodes[2]),
            graph.newEdge(nodes[2], nodes[0])
        };
        int removedEdges = graph.removeEdges(Arrays.asList(edges));
        assertEquals(3, removedEdges);
        int removedNodes = graph.removeNodes(Arrays.asList(nodes));
        assertEquals(3, removedNodes);
        assertEquals(preNodeCount, graph.nodeCount());
        assertEquals(preEdgeCount, graph.edgeCount());
    }
    
    public void testLookupEdges() {
        Graph g = create();
        Node[] n = g.newNodes(0, 1, 2, 3, 4);
        Edge e1 = g.newEdge(n[0], n[1]);
        Edge e2 = g.newEdge(n[2], n[0]);
        Edge e3 = g.newEdge(n[3], n[4]);
        
        Collection<Edge> allEdges = g.edges().drainToSet();
        assertEquals(3, g.edgeCount());
        
        Collection<Edge> edgesAtN0 = g.edges(n[0]).drainToSet();
        assertEquals(2, edgesAtN0.size());
        assertTrue(edgesAtN0.contains(e1));
        assertTrue(edgesAtN0.contains(e2));
        
        Collection<Edge> edgesFromN2 = g.edges(n[2], Direction.OUT).drainToSet();
        assertEquals(1, edgesFromN2.size());
        assertTrue(edgesFromN2.contains(e2));
        
        Collection<Edge> edgesToN4 = g.edges(n[4], Direction.IN).drainToSet();
        assertEquals(1, edgesToN4.size());
        assertTrue(edgesToN4.contains(e3));
        
        Collection<Edge> edgesFromN2ToN0 = g.edges(n[2], n[0], Direction.OUT).drainToSet();
        assertEquals(1, edgesFromN2ToN0.size());
        assertTrue(edgesFromN2ToN0.contains(e2));
        
        Collection<Edge> edgesFromN0ToN2 = g.edges(n[0], n[2], Direction.OUT).drainToSet();
        assertEquals(0, edgesFromN0ToN2.size());
        
        Collection<Edge> edgesFromN2ToN0Undirectedly = g.edges(n[2], n[0], Direction.EITHER).drainToSet();
        assertEquals(1, edgesFromN2ToN0Undirectedly.size());
        assertTrue(edgesFromN2ToN0Undirectedly.contains(e2));
    }
    
    public void testRandomEdges() {
        Graph g = create();
        Random random = new Random();
        Node[] n = g.newNodes(100);
        for (int i = 0; i < n.length; i++) { n[i].setValue(i); }
        Map<Node, Collection<Edge>> outEdges = DVMap.newHashMapWithHashSets();
        Map<Node, Collection<Edge>> inEdges = DVMap.newHashMapWithHashSets();
        Map<Node, Collection<Edge>> allEdges = DVMap.newHashMapWithHashSets();
        final int edgeCount = 5000;
        for (int i = 0; i < edgeCount; i++) {
            Node n1 = n[random.nextInt(n.length)];
            Node n2 = n[random.nextInt(n.length)];
            Edge e = g.newEdge(n1, n2);
            outEdges.get(n1).add(e);
            inEdges.get(n2).add(e);
            allEdges.get(n1).add(e);
            allEdges.get(n2).add(e);
        }
        for (Node node : n) {
            assertEquals(outEdges.get(node), g.edges(node, Direction.OUT).drainToSet());
            assertEquals(inEdges.get(node), g.edges(node, Direction.IN).drainToSet());
            assertEquals(allEdges.get(node), g.edges(node, Direction.EITHER).drainToSet());
        }
    }
    
    public void testNullChecksFor() {
        Graph g = create();
        Node[] n = g.newNodes(5);
        try {
            g.containsEdge(null);
        } catch (IllegalArgumentException noExceptionHere) {
            fail();
        }
        try {
            g.containsNode(null);
        } catch (RuntimeException noExceptionHere) {
            fail();
        }
        try {
            g.edges(null);
            fail();
        } catch (RuntimeException ok) {
        }
        try {
            g.edges(n[0], (Node)null);
            fail();
        } catch (RuntimeException ok) {
        }
        try {
            g.edges((Node)null, n[0]);
            fail();
        } catch (RuntimeException ok) {
        }
        try {
            g.edges(n[0], n[1], null);
            fail();
        } catch (RuntimeException ok) {
        }
        try {
            g.areAdjacent(n[0], null);
            fail();
        } catch (RuntimeException ok) {
        }
        try {
            g.areAdjacent(null, n[0]);
            fail();
        } catch (RuntimeException ok) {
        }
        try {
            g.areAdjacent(n[0], n[1], null);
            fail();
        } catch (RuntimeException ok) {
        }
        try {
            g.removeEdge(null);
        } catch (RuntimeException noExceptionHere) {
            fail();
        }
        try {
            g.removeEdge(null);
        } catch (RuntimeException noExceptionHere) {
            fail();
        }
    }
    
    public void testRemoveRemovedNode() {
        Graph g = create();
        Node n = g.newNode();
        boolean removed = g.removeNode(n);
        assertTrue(removed);
        try {
            removed = g.removeNode(n);
            assertFalse(removed);
        } catch (Exception noExceptionHere) {
            fail();
        }
    }
    
    public void testCreateNegativeNodes() {
        Graph g = create();
        try {
            Node[] nodes = g.newNodes(-5);
            fail("No exception");
        } catch (IllegalArgumentException iae) {
            return;
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getClass());
        }
    }
    
    public void testDegrees() {
        Graph g = create();
        Node n1 = g.newNode();
        checkDegrees(g, n1, 0, 0);
        Edge e11 = g.newEdge(n1, n1);
        checkDegrees(g, n1, 1, 1);
        Node n2 = g.newNode();
        Edge e21 = g.newEdge(n2, n1);
        checkDegrees(g, n1, 2, 1);
        checkDegrees(g, n2, 0, 1);
        
        g.removeEdge(e11);
        checkDegrees(g, n1, 1, 0);
        checkDegrees(g, n2, 0, 1);
        
        g.removeEdge(e21);
        checkDegrees(g, n1, 0, 0);
        checkDegrees(g, n2, 0, 0);
    }
    
    private void checkDegrees(InspectableGraph graph, Node node, int in, int out) {
        assertEquals(in, graph.inDegree(node));
        assertEquals(out, graph.outDegree(node));
        assertEquals(in + out, graph.degree(node));
    }
    
    public void testDegreeOfForeignNode() {
        Graph g1 = create();
        InspectableGraph g2 = create();
        Node n1 = g1.newNode();
        try {
            g2.degree(n1);
            fail();
        } catch (RuntimeException e) {
            //ok
        }
    }
    
    public void testIncidentEdgesAreReflexive() {
        Graph g = create();
        Node n1 = g.newNode(1);
        Node n2 = g.newNode(2);
        g.newEdge(n1, n2);
        g.newEdge(n2, n2);
        checkEdges(g);
        
        g.newEdge(n2, n1);
        g.newEdge(n1, n1);
        checkEdges(g);
    }
    
    private void checkEdges(InspectableGraph g) {
        for (Direction d : Arrays.asList(Direction.OUT, Direction.EITHER)) {
            for (Node n1 : g.nodes()) {
                for (Node n2 : g.nodes()) {
                    assertEquals(String.format("%s-%s,%s", n1, n2, d),
                            g.edges(n1, n2, d).drainToSet(),
                            g.edges(n2, n1, d.flip()).drainToSet());
                }
            }
        }
    }
    
    public void testRemove() {
        Graph g = create();
        Node n = g.newNode();
        Edge e = g.newEdge(n, n);
        assertTrue(g.containsEdge(e));
        assertTrue(g.removeEdge(e));
        assertFalse(g.containsEdge(e));
        assertFalse(g.removeEdge(e));
        
        assertTrue(g.containsNode(n));
        assertTrue(g.removeNode(n));
        assertFalse(g.containsNode(n));
        assertFalse(g.removeNode(n));
    }
    
    public void testDegreeOfRemovedNode() {
        Graph g = create();
        Node n = g.newNode();
        assertEquals(0, g.degree(n));
        assertTrue(g.removeNode(n));
        try {
            graph.removeNode(n);
            int d = graph.degree(n);
            fail();
        } catch (RuntimeException ok) {
        }
    }
    
    public void testExistenceOfOuterEdge() {
        Graph g = create();
        Node n = g.newNode();
        Edge e = g.newEdge(n, n);
        assertTrue(g.containsEdge(e));
        g.removeNode(n);
        assertFalse(g.containsEdge(e));
    }
    
    public void testNormalRemoval() {
        Graph g = create();
        Node n = g.newNode();
        Edge e = g.newEdge(n, n);
        assertEquals(1, g.inDegree(n));
        assertEquals(1, g.outDegree(n));
        assertEquals(2, g.degree(n));
        g.removeEdge(e);
        assertEquals(0, g.inDegree(n));
        assertEquals(0, g.outDegree(n));
        assertEquals(0, g.degree(n));
    }
    
    public void testRemoveAllFiresEvents() {
        int edges = 3;
        int nodes = 5;
        Mock listener = mock(GraphListener.class);
        for (int i = 0; i < edges; ++i) {
            listener.expects(once()).method("preEvent");
            listener.expects(once()).method("edgeToBeRemoved");
            listener.expects(once()).method("edgeRemoved");
            listener.expects(once()).method("postEvent");
        }
        for (int i = 0; i < nodes; ++i) {
            listener.expects(once()).method("preEvent");
            listener.expects(once()).method("nodeToBeRemoved");
            listener.expects(once()).method("nodeRemoved");
            listener.expects(once()).method("postEvent");
        }
        GraphListener gl = (GraphListener)listener.proxy();
        Graph g = create();
        Node[] n = g.newNodes(nodes);
        for (int i = 0; i < edges; ++i) {
            g.newEdge(n[0], n[0]);
        }
        
        g.addGraphListener(gl);
        
        g.removeAllEdges();
        g.removeAllNodes();
        listener.verify();
    }
    
    public void testNoRemainingEdges() {
        Graph g = create();
        Generators.createRandom(g, 10, 1.0f);
        assertTrue(g.edgeCount() > 0);
        assertTrue(g.nodeCount() > 0);
        g.removeAllEdges();
        for (Edge e : g.edges()) {
            fail();
        }
        g.removeAllNodes();
        for (Node n : g.nodes()) {
            fail();
        }
    }
    
    public void testRemoveNodeThroughIterator() {
        final int nodes = 100;
        Mock mockListener = mock(NodeListener.class);
        for (int i = 0; i < nodes; ++i) {
            mockListener.expects(once()).method("preEvent");
            mockListener.expects(once()).method("nodeToBeRemoved");
            mockListener.expects(once()).method("nodeRemoved");
            mockListener.expects(once()).method("postEvent");
        }
        
        Graph g = create();
        g.newNodes(nodes);
        g.addNodeListener((NodeListener)mockListener.proxy());
        for (Iterator<Node> nodeIterator = g.nodes().iterator(); nodeIterator.hasNext(); ) {
            nodeIterator.next();
            nodeIterator.remove();
        }
    }
    
    public void testRemoveEdgeThroughIterator() {
        final int edges = 100;
        Mock mockListener = mock(EdgeListener.class);
        for (int i = 0; i < edges; ++i) {
            mockListener.expects(once()).method("preEvent");
            mockListener.expects(once()).method("edgeToBeRemoved");
            mockListener.expects(once()).method("edgeRemoved");
            mockListener.expects(once()).method("postEvent");
        }
        
        Graph g = create();
        Node[] n = g.newNodes(edges);
        for (int i = 0; i < edges; ++i) {
            g.newEdge(n[i], n[(i + 1) % edges]);
        }
        g.addEdgeListener((EdgeListener)mockListener.proxy());
        for (Iterator<Edge> edgeIterator = g.edges().iterator(); edgeIterator.hasNext(); ) {
            edgeIterator.next();
            edgeIterator.remove();
        }
    }
    
    public void testConcurrentEdgeModifications() {
        Graph g = create();
        Generators.createRandom(g, 10, 1.0);
        try {
            for (Edge e : g.edges(g.nodes().iterator().next())) {
                g.removeEdge(e);
            }
        } catch (ConcurrentModificationException e) {
            fail();
        }
        try {
            Iterator<Node> i = g.nodes().iterator();
            Node n1 = i.next();
            Node n2 = i.next();
            for (Edge e : g.edges(n1, n2)) {
                g.removeEdge(e);
            }
        } catch (ConcurrentModificationException e) {
            fail();
        }
        try {
            for (Edge e : g.edges()) {
                g.removeEdge(e);
            }
        } catch (ConcurrentModificationException e) {
            fail();
        }
    }
    
    public void testNodeToNodeEdgesRemoval() {
        Graph g = create();
        Node n1 = g.newNode();
        Node n2 = g.newNode();
        for (int i = 0; i < 4; ++i) {
            g.newEdge(n1, n2);
            g.newEdge(n2, n1);
        }
        
        try {
            for (Iterator<Edge> i = g.edges(n1, n2).iterator(); i.hasNext(); ) {
                Edge e = i.next();
                g.removeEdge(e);
                i.remove();
                fail();
            }
        } catch (ConcurrentModificationException e) {
            fail();
        } catch (NoSuchElementException e) {
            //ok!
        }
    }
    
    public void testNodeReinsert() {
        if (!isPrimary()) { return; }
        Graph g = create();
        Node n = g.newNode();
        
        Mock listener = mock(NodeListener.class);
        listener.expects(once()).method("preEvent").id("1");
        listener.expects(once()).method("nodeToBeAdded").after("1").id("2");
        listener.expects(once()).method("nodeAdded").after("2").id("3");
        listener.expects(once()).method("postEvent").after("3");
        NodeListener nl = (NodeListener)listener.proxy();
        
        assertTrue(g.containsNode(n));
        assertTrue(g.removeNode(n));
        assertFalse(g.containsNode(n));
        
        g.addNodeListener(nl);
        g.reinsertNode(n);
        assertEquals(1, g.nodeCount());
        g.removeNodeListener(nl);
        assertTrue(g.containsNode(n));
        assertTrue(g.removeNode(n));
        assertFalse(g.containsNode(n));
    }
    
    public void testEdgeReinsert() {
        if (!isPrimary()) { return; }
        Graph g = create();
        Node n1 = g.newNode();
        Node n2 = g.newNode();
        Edge e = g.newEdge(n1, n2);
        
        Mock listener = mock(EdgeListener.class);
        listener.expects(once()).method("preEvent").id("1");
        listener.expects(once()).method("edgeToBeAdded").after("1").id("2");
        listener.expects(once()).method("edgeAdded").after("2").id("3");
        listener.expects(once()).method("postEvent").after("3");
        EdgeListener el = (EdgeListener)listener.proxy();
        
        assertTrue(g.containsEdge(e));
        assertTrue(g.removeEdge(e));
        assertFalse(g.containsEdge(e));
        
        g.addEdgeListener(el);
        g.reinsertEdge(e);
        assertEquals(1, g.edgeCount());
        g.removeEdgeListener(el);
        listener.verify();
        assertTrue(g.containsEdge(e));
        assertTrue(g.removeEdge(e));
        assertFalse(g.containsEdge(e));
    }
    
    public void testNodeReinsertionTriggeredByEdgeReinsertion() {
        if (!isPrimary()) { return; }
        Graph g = create();
        Edge e = g.newEdge(g.newNode(), g.newNode());
        g.removeAllNodes();
        assertEquals(0, g.nodeCount());
        assertEquals(0, g.edgeCount());
        g.reinsertEdge(e);
        assertEquals(2, g.nodeCount());
        assertEquals(1, g.edgeCount());
    }
    
    public void testShouldNotReinsertEdgeThatIsContained() {
        Graph g = create();
        Edge e = g.newEdge(g.newNode(), g.newNode());
        assertFalse(g.reinsertEdge(e));
    }
    
    public void testAdjacentNodes() {
        Graph g = create();
        Node[] outerNodes = g.newNodes(5);
        Node centerNode = g.newNode();
        for (Node outer : outerNodes) {
            g.newEdge(centerNode, outer);
        }
        for (Node adjacent : g.adjacentNodes(centerNode, Direction.IN)) {
            fail();
        }
        int count = 0;
        for (Node adjacent : g.adjacentNodes(centerNode, Direction.OUT)) {
            count++;
        }
        assertEquals(outerNodes.length, count);
        count = 0;
        for (Node adjacent : g.adjacentNodes(centerNode)) {
            count++;
        }
        assertEquals(outerNodes.length, count);
    }
    
    public void testReinsertionOfForeignNode() {
        if (!isPrimary()) { return; }
        Graph g1 = create();
        Graph g2 = create();
        try {
            g2.reinsertNode(g1.newNode());
            fail("Foreign node must not be reinserted to other graph");
        } catch (IllegalArgumentException ok) {
        }
        SecondaryGraph g3 = new SecondaryGraph();
        Node n = g1.newNode();
        g1.removeNode(n);
        g3.adoptNode(n);
        g1.reinsertNode(n);
        //should not throw any exception
    }
    
    public void testReinsertionOfForeignEdge() {
        if (!isPrimary()) { return; }
        Graph g1 = create();
        Graph g2 = create();
        try {
            Node[] n = g1.newNodes(2);
            g2.reinsertEdge(g1.newEdge(n[0], n[1]));
            fail("Foreign edge must not be reinserted to other graph");
        } catch (IllegalArgumentException ok) {
        }
    }
    
    public void testNodeNullDataContains() {
        Graph g = create();
        Node n = g.newNode();
        assertFalse(n.has(new Object()));
    }
    
    public void testSelfLoopDegree() {
        Graph g = create();
        Node n = g.newNode();
        g.newEdge(n, n);
        assertEquals(2, g.degree(n));
        assertEquals(1, g.inDegree(n));
        assertEquals(1, g.outDegree(n));
    }
    
    public void testGraphImport() {
        Graph g = create();
        Node[] nodes = createClique(g, 5);
        Collection<Node> initialNodes = g.nodes().drainToSet();
        Collection<Edge> initialEdges = g.edges().drainToSet();
        
        Graph superGraph = create();
        superGraph.importGraph(g);
        assertEquals(0, g.nodeCount());
        assertEquals(0, g.edgeCount());
        assertEquals(initialNodes.size(), superGraph.nodeCount());
        assertEquals(initialEdges.size(), superGraph.edgeCount());
        assertTrue(superGraph.nodes().drainToSet().containsAll(initialNodes));
        assertTrue(superGraph.edges().drainToSet().containsAll(initialEdges));
    }
    
    public void testPartialGraphImport() {
        if (!isPrimary()) { return; }
        Graph g = create();
        Node[] nodes = createClique(g, 5);
        
        Graph superGraph = create();
        Collection<Edge> edges = superGraph.importGraph(g, Arrays.asList(nodes[0], nodes[1]));
        assertEquals(3, g.nodeCount());
        assertEquals(6, g.edgeCount()); //6 = 3 * 2
        assertEquals(12, edges.size());
        assertTrue(superGraph.containsNode(nodes[0]));
        assertTrue(superGraph.containsNode(nodes[1]));
        for (Edge interedge : edges) {
            //one node is contained in g, one in supergraph (definition of inter-edge)
            assertTrue(
                    (g.containsNode(interedge.n1()) ^ superGraph.containsNode(interedge.n1())) &&
                    (g.containsNode(interedge.n2()) ^ superGraph.containsNode(interedge.n2()))
                    );
        }
    }
    
    public void testContainsEdge() {
        Graph g = create();
        InspectableGraph g2 = create();
        Edge e = g.newEdge(g.newNode(), g.newNode());
        assertFalse(g2.containsEdge(e));
    }
    
    public void testConvenienceMethods() {
        Graph g = create();
        Node[] n = createClique(g, 5);
        Edge e = g.edges(n[0], Direction.OUT).iterator().next();
        
        assertEquals(n[0], g.aNode());
        assertEquals(n[0], g.aNode(n[1]));
        assertEquals(n[0], g.aNode(n[1], Direction.OUT));
        assertEquals(e, g.anEdge());
        assertEquals(e, g.anEdge(n[0]));
        assertEquals(e, g.anEdge(n[0], Direction.OUT));
        assertEquals(e, g.anEdge(n[0], n[1]));
        assertEquals(e, g.anEdge(n[0], n[1], Direction.OUT));
    }
    
    public void testEmpty() {
        Graph g = create();
        assertTrue(g.isEmpty());
        Node n = g.newNode();
        assertFalse(g.isEmpty());
        g.removeNode(n);
        assertTrue(g.isEmpty());
    }
    
    public void testSimpleEdgeAndDegreeConsistency() {
        Graph g = create();
        Node[] n = g.newNodes("A", "B");
        g.newEdge(n[0], n[1]);
        assertEquals(g.outDegree(n[0]), g.edges(n[0], Direction.OUT).drainToList().size());
        assertEquals(g.inDegree(n[1]), g.edges(n[1], Direction.IN).drainToList().size());
        
        assertEquals(g.outDegree(n[0]), g.edges(n[0], n[1], Direction.OUT).drainToList().size());
        assertEquals(g.inDegree(n[1]), g.edges(n[1], n[0], Direction.IN).drainToList().size());
    }
    
    public void testNodeIsNotAdjacentToItselfUnlessWithSelfLoop() {
        Graph g = create();
        Node n = g.newNode();
        assertFalse(g.areAdjacent(n, n));
        g.newEdge(n, n);
        assertTrue(g.areAdjacent(n, n));
    }
    
    public void testAreIncident() {
        Graph g = create();
        Node n1 = g.newNode(1);
        Node n2 = g.newNode(2);
        assertFalse(g.areAdjacent(n1, n2));
        g.newEdge(n1, n2);
        assertTrue(g.areAdjacent(n1, n2));
        assertTrue(g.areAdjacent(n1, n2, Direction.OUT));
        assertTrue(g.areAdjacent(n2, n1));
        assertFalse(g.areAdjacent(n2, n1, Direction.OUT));
    }
    
    public void testIncidentBug() {
        Graph g = create();
        Node[] n = g.newNodes(1, 2, 3);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[1], n[2]);
        
        assertFalse(g.areAdjacent(n[1], n[1]));
        assertFalse(g.areAdjacent(n[1], n[1], Direction.OUT));
    }
    
    public void testEdgesListIterator() {
        Graph g = create();
        Node[] n = createPath(g, 4);
        List<Edge> e = g.edges().drainToList();
        
        assertEquals(e, ExtendedIterable.drainToList(g.edges().listIterator()));
        ListIterator<Edge> it = g.edges().listIterator();
        
        int i = 0;
        for (i = 0; i < e.size(); i++) {
            Edge edge = it.next();
            assertTrue(it.hasPrevious());
            assertEquals(e.get(i), edge);
        }
        assertEquals("All edges iterated", e.size(), i);
        assertFalse(it.hasNext());
        for (i = e.size() - 1; i >= 0; i--) {
            Edge edge = it.previous();
            assertTrue(it.hasNext());
            assertEquals(e.get(i), edge);
        }
        assertFalse(it.hasPrevious());
    }
    
    public void testEdgeAppearsTwice() {
        Graph g = create();
        Node[] n = createPath(g, 4);
        List<Edge> e = g.edges().drainToList();
        
        assertEquals(e, ExtendedIterable.drainToList(g.edges().listIterator()));
        ListIterator<Edge> it = g.edges().listIterator();
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertFalse(it.hasPrevious());
        int pos = 0;
        while (it.hasNext()) {
            assertEquals(e.get(pos++), it.next());
        }
        assertEquals(e.size(), pos);
    }
    
    private Node[] createPath(Graph g, int n) {
        Node[] nodes = g.newNodes(n);
        for (int i = 0; i < n; i++) {
            nodes[i].setValue(i);
        }
        for (int i = 1; i < nodes.length; i++) {
            g.newEdge(nodes[i - 1], nodes[i]);
        }
        return nodes;
    }
    
    public void testEventSemantics() {
        final Graph g = create();
        //a node and a self-loop will be added, then removed reversely
        g.addGraphListener(new EmptyGraphListener() {
            public void nodeToBeAdded(GraphEvent e) {
                Node n = e.getNode();
                assertFalse(g.containsNode(n));
            }
            
            public void nodeAdded(GraphEvent e) {
                Node n = e.getNode();
                assertTrue(g.containsNode(n));
            }
            
            public void edgeToBeAdded(GraphEvent e) {
                Edge edge = e.getEdge();
                assertFalse(g.containsEdge(edge));
                assertEquals(0, g.degree(edge.n1()));
            }
            
            public void edgeAdded(GraphEvent e) {
                Edge edge = e.getEdge();
                assertTrue(g.containsEdge(edge));
                assertEquals(2, g.degree(edge.n1()));
            }
            
            public void edgeToBeRemoved(GraphEvent e) {
                Edge edge = e.getEdge();
                assertTrue(g.containsEdge(edge));
                assertEquals(2, g.degree(edge.n1()));
            }
            
            public void edgeRemoved(GraphEvent e) {
                Edge edge = e.getEdge();
                assertFalse(g.containsEdge(edge));
                assertEquals(0, g.degree(edge.n1()));
            }
            
            public void nodeToBeRemoved(GraphEvent e) {
                Node n = e.getNode();
                assertTrue(g.containsNode(n));
            }
            
            public void nodeRemoved(GraphEvent e) {
                Node n = e.getNode();
                assertFalse(g.containsNode(n));
            }
        });
        Node n = g.newNode();
        Edge e = g.newEdge(n, n);
        g.removeEdge(e);
        g.removeNode(n);
    }
    
    public void testEventVetoSemantics() {
        final Graph g = create();
        //a node and a self-loop will be added, then removed reversely
        Node n1 = g.newNode(1);
        Node n2 = g.newNode(2);
        Edge e1 = g.newEdge(n1, n2);
        g.addGraphListener(new EmptyGraphListener() {
            public void nodeToBeAdded(GraphEvent e) {
                throw new RuntimeException();
            }
            
            public void edgeToBeAdded(GraphEvent e) {
                throw new RuntimeException();
            }
            
            public void edgeToBeRemoved(GraphEvent e) {
                throw new RuntimeException();
            }
            
            public void nodeToBeRemoved(GraphEvent e) {
                Node n = e.getNode();
                assertTrue(g.containsNode(n));
            }
            
            public void nodeRemoved(GraphEvent e) {
                Node n = e.getNode();
                assertFalse(g.containsNode(n));
            }
        });
        try {
            Node n = g.newNode(3);
            fail();
        } catch (RuntimeException e) {
            //ok
        }
        assertEquals("[N={1, 2}, E={{1->2}}]", printCompact(g).toString());
        
        try {
            g.newEdge(n1, n1);
            fail();
        } catch (RuntimeException ex) {
            //ok
        }
        assertEquals("[N={1, 2}, E={{1->2}}]", printCompact(g).toString());
        assertEquals(1, g.degree(n1));
        
        try {
            g.removeEdge(g.anEdge());
            fail();
        } catch (RuntimeException ex) {
            //ok
        }
        assertEquals("[N={1, 2}, E={{1->2}}]", printCompact(g).toString());
        
        try {
            g.removeNode(n1);
            fail();
        } catch (RuntimeException ex) {
            //ok
        }
        assertEquals("[N={1, 2}, E={{1->2}}]", printCompact(g).toString());
    }
    
    public void testNodeReordering() {
        Graph g = create();
        OrderManager order = g.getOrderManager();
        Node[] n = g.newNodes(1, 2, 3, 4);
        assertEquals("[1, 2, 3, 4]", g.nodes().toString());
        order.moveNodeToFront(n[1]);
        assertEquals("[2, 1, 3, 4]", g.nodes().toString());
        order.moveNodeToBack(n[1]);
        assertEquals("[1, 3, 4, 2]", g.nodes().toString());
        
        order.moveNodeAfter(n[2], n[1]);
        assertEquals("[1, 4, 2, 3]", g.nodes().toString());
        order.moveNodeBefore(n[2], n[1]);
        assertEquals("[1, 4, 3, 2]", g.nodes().toString());
    }
    
    public void testEdgeReordering() {
        Graph g = create();
        OrderManager order = g.getOrderManager();
        Node n1 = g.newNode(1);
        Node n2 = g.newNode(2);
        Edge[] e = {
            g.newEdge(n1, n2, "e1"),
            g.newEdge(n1, n2, "e2"),
            g.newEdge(n1, n2, "e3"),
            g.newEdge(n2, n1, "e4"),
            g.newEdge(n2, n1, "e5"),
            g.newEdge(n2, n1, "e6"),
        };
        assertEquals("[{1->2, (e1)}, {1->2, (e2)}, {1->2, (e3)}]", g.edges(n1, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n2, Direction.OUT).toString());
        
        order.moveEdgeToFront(e[2], true);
        assertEquals("[{1->2, (e3)}, {1->2, (e1)}, {1->2, (e2)}]", g.edges(n1, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n2, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n1, Direction.IN).toString());
        
        order.moveEdgeToBack(e[0], true);
        assertEquals("[{1->2, (e3)}, {1->2, (e2)}, {1->2, (e1)}]", g.edges(n1, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n2, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n1, Direction.IN).toString());
        
        order.moveEdgeAfter(e[2], true, e[1]);
        assertEquals("[{1->2, (e2)}, {1->2, (e3)}, {1->2, (e1)}]", g.edges(n1, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n2, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n1, Direction.IN).toString());
        
        order.moveEdgeBefore(e[0], true, e[1]);
        assertEquals("[{1->2, (e1)}, {1->2, (e2)}, {1->2, (e3)}]", g.edges(n1, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n2, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n1, Direction.IN).toString());
        
        order.moveEdgeToFront(e[4], false);
        assertEquals("[{1->2, (e1)}, {1->2, (e2)}, {1->2, (e3)}]", g.edges(n1, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n2, Direction.OUT).toString());
        assertEquals("[{2->1, (e5)}, {2->1, (e4)}, {2->1, (e6)}]", g.edges(n1, Direction.IN).toString());
        
        order.moveEdgeToBack(e[4], false);
        assertEquals("[{1->2, (e1)}, {1->2, (e2)}, {1->2, (e3)}]", g.edges(n1, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n2, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e6)}, {2->1, (e5)}]", g.edges(n1, Direction.IN).toString());
        
        order.moveEdgeAfter(e[3], false, e[4]);
        assertEquals("[{1->2, (e1)}, {1->2, (e2)}, {1->2, (e3)}]", g.edges(n1, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n2, Direction.OUT).toString());
        assertEquals("[{2->1, (e6)}, {2->1, (e5)}, {2->1, (e4)}]", g.edges(n1, Direction.IN).toString());
        
        order.moveEdgeBefore(e[4], false, e[5]);
        assertEquals("[{1->2, (e1)}, {1->2, (e2)}, {1->2, (e3)}]", g.edges(n1, Direction.OUT).toString());
        assertEquals("[{2->1, (e4)}, {2->1, (e5)}, {2->1, (e6)}]", g.edges(n2, Direction.OUT).toString());
        assertEquals("[{2->1, (e5)}, {2->1, (e6)}, {2->1, (e4)}]", g.edges(n1, Direction.IN).toString());
        
        //edges with different polarity violate precondition
        try {
            order.moveEdgeBefore(e[1], true, e[4]);
            fail("Allowed edges with different polarity");
        } catch (RuntimeException ok) { }
        try {
            order.moveEdgeBefore(e[1], false, e[4]);
            fail("Allowed edges with different polarity");
        } catch (RuntimeException ok) { }
    }
    
    public void testNodeReorderingEvent() {
        Graph g = create();
        if (g instanceof GraphForwarder) {
            return;
        }
        Node[] n = g.newNodes(1, 2);
        Mock m = mock(NodeListener.class);
        m.expects(once()).method("nodeReordered").with(eq(new GraphEvent(g, GraphEvent.Type.NODE_REORDERED, n[1])));
        g.addNodeListener((NodeListener)m.proxy());
        g.getOrderManager().moveNodeToBack(n[1]);
    }
    
    public void testEdgeReorderingEvent() {
        Graph g = create();
        if (g instanceof GraphForwarder) {
            return;
        }
        Node[] n = g.newNodes(1, 2);
        Edge e = g.newEdge(n[0], n[1]);
        Mock m = mock(EdgeListener.class);
        m.expects(once()).method("edgeReordered").with(eq(new GraphEvent(g, GraphEvent.Type.EDGE_REORDERED, e)));
        g.addEdgeListener((EdgeListener)m.proxy());
        g.getOrderManager().moveEdgeToBack(e, true);
    }
    
    public void testReorderIllegalNode() {
        Graph g = create();
        Graph other = create();
        Node n = g.newNode();
        Node illegal = other.newNode();
        
        try {
            g.getOrderManager().moveNodeToFront(null);
            fail();
        } catch (IllegalArgumentException ok) { }
        try {
            g.getOrderManager().moveNodeToFront(illegal);
            fail();
        } catch (IllegalArgumentException ok) { }
        try {
            g.getOrderManager().moveNodeAfter(n, illegal);
            fail();
        } catch (IllegalArgumentException ok) { }
        try {
            g.getOrderManager().moveNodeBefore(illegal, n);
            fail();
        } catch (IllegalArgumentException ok) { }
    }
    
    public void testReorderIllegalEdge() {
        Graph g = create();
        Graph other = create();
        Edge e = g.newEdge(g.newNode(), g.newNode());
        Edge illegal = other.newEdge(other.newNode(), other.newNode());
        
        try {
            g.getOrderManager().moveEdgeToFront(null, true);
            fail();
        } catch (IllegalArgumentException ok) { }
        try {
            g.getOrderManager().moveEdgeToBack(null, false);
            fail();
        } catch (IllegalArgumentException ok) { }
        try {
            g.getOrderManager().moveEdgeBefore(e, false, illegal);
            fail();
        } catch (IllegalArgumentException ok) { }
        try {
            g.getOrderManager().moveEdgeBefore(illegal, true, e);
            fail();
        } catch (IllegalArgumentException ok) { }
    }
    
    public void testIllegalNode() {
        Graph g = create();
        Node n = g.newNode();
        g.removeNode(n);
        try {
            g.edges(n, Direction.OUT);
            fail();
        } catch (IllegalArgumentException ok) {
        }
        try {
            g.edges(n);
            fail();
        } catch (IllegalArgumentException ok) {
        }
    }
    
    public void testIllegalNodes() {
        Graph g = create();
        Node n = g.newNode();
        g.removeNode(n);
        try {
            g.edges(n, g.newNode(), Direction.OUT);
            fail();
        } catch (IllegalArgumentException ok) {
        }
        try {
            g.edges(n, g.newNode());
            fail();
        } catch (IllegalArgumentException ok) {
        }
    }
    
    public void testRemoveNull() {
        Graph g = create();
        g.removeNode(null);
        g.removeEdge(null);
        //no exception
    }
    
    public void testRemoveNullCollections() {
        Graph g = create();
        g.removeNodes(null);
        g.removeEdges(null);
        //no exception
    }
    
    private Node[] createClique(Graph g, int nodeCount) {
        Node[] nodes = g.newNodes(nodeCount);
        int pos = 0;
        for (Node n : nodes) {
            n.setValue(++pos);
            for (Node n2 : nodes) {
                if (n == n2) continue;
                g.newEdge(n, n2);
            }
        }
        return nodes;
    }
}
