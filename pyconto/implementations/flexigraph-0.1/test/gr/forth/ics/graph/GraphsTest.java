package gr.forth.ics.graph;

import junit.framework.*;

import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.algo.transitivity.SuccessorSetFactory;
import gr.forth.ics.graph.algo.transitivity.Transitivity;
import gr.forth.ics.graph.path.Path;
import java.util.List;
import java.util.Set;
import randomunit.*;
import static gr.forth.ics.graph.Graphs.printCompact;

public class GraphsTest extends RandomizedTestCase {
    public GraphsTest(String testName) {
        super(testName, 1000, new SimpleLogStrategy(4));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(GraphsTest.class);
        
        return suite;
    }
    
    public void testIsTree() {
        Graph g = new PrimaryGraph();
        assertTrue(GraphChecker.isTree(g));
        Node n1 = g.newNode("1");
        Node n2 = g.newNode("2");
        Node n3 = g.newNode("3");
        g.newEdge(n1, n2);
        g.newEdge(n2, n3);
        assertTrue(GraphChecker.isTree(g));
        g.newEdge(n3, n1);
        assertFalse(GraphChecker.isTree(g));
    }
    
    public void testIsForest() {
        Graph g = new PrimaryGraph();
        assertTrue(GraphChecker.isTree(g));
        Node n1 = g.newNode("1");
        Node n2 = g.newNode("2");
        Node n3 = g.newNode("3");
        g.newEdge(n1, n2);
        g.newEdge(n2, n3);
        g.newEdge(g.newNode(), g.newNode());
        assertTrue(GraphChecker.isForest(g));
        assertFalse(GraphChecker.isTree(g));
    }

    public void testIsBiconnected() {
        Graph g = new PrimaryGraph();
        assertTrue(GraphChecker.isBiconnected(g));

        Node n1 = g.newNode();
        assertTrue(GraphChecker.isBiconnected(g));

        Node n2 = g.newNode();
        assertFalse(GraphChecker.isBiconnected(g));

        g.newEdge(n1, n2);
        assertTrue(GraphChecker.isBiconnected(g));

        g.newNode();
        assertFalse(GraphChecker.isBiconnected(g));
    }

    public void testEquality() {
        SecondaryGraph g1 = new SecondaryGraph();
        SecondaryGraph g2 = new SecondaryGraph();
        assertTrue(Graphs.equalGraphs(g1, g2));
        Node n1 = g1.newNode();
        Edge e1 = g1.newEdge(n1, n1);
        assertFalse(Graphs.equalGraphs(g1, g2));
        g2.adoptNode(n1);
        assertFalse(Graphs.equalGraphs(g1, g2));
        g2.adoptEdge(e1);
        assertTrue(Graphs.equalGraphs(g1, g2));
        g1.removeAllEdges();
        assertFalse(Graphs.equalGraphs(g1, g2));
        g2.removeAllEdges();
        assertTrue(Graphs.equalGraphs(g1, g2));
        g1.removeAllNodes();
        assertFalse(Graphs.equalGraphs(g1, g2));
        g2.removeAllNodes();
        assertTrue(Graphs.equalGraphs(g1, g2));
    }
    
    public void testBasicUnion() {
        SecondaryGraph g1 = new SecondaryGraph();
        SecondaryGraph g2 = new SecondaryGraph();
        InspectableGraph union = Graphs.union(g1, g2);
        Node n1 = g1.newNode(1);
        assertTrue(union.containsNode(n1));
        Node n2 = g2.newNode(2);
        assertTrue(union.containsNode(n2));
        Edge e1 = g1.newEdge(n1, n1);
        assertTrue(union.containsEdge(e1));
        Edge e2 = g2.newEdge(n2, n2);
        assertTrue(union.containsEdge(e2));
        g2.adoptEdge(e1);
        assertEquals(2, union.nodeCount());
        assertEquals(2, union.edgeCount());
        g1.removeNode(n1);
        assertEquals(2, union.nodeCount());
        assertEquals(2, union.edgeCount());
        g2.removeNode(n1);
        assertEquals(1, union.nodeCount());
        assertEquals(1, union.edgeCount());
    }
    
    public void testBasicIntersection() {
        SecondaryGraph g1 = new SecondaryGraph();
        SecondaryGraph g2 = new SecondaryGraph();
        InspectableGraph inter = Graphs.intersection(g1, g2);
        Node n1 = g1.newNode(1);
        assertTrue(!inter.containsNode(n1));
        Node n2 = g2.newNode(2);
        assertTrue(!inter.containsNode(n2));
        Edge e1 = g1.newEdge(n1, n1);
        assertTrue(!inter.containsEdge(e1));
        Edge e2 = g2.newEdge(n2, n2);
        assertTrue(!inter.containsEdge(e2));
        assertTrue(inter.isEmpty());
        g1.adoptNode(n2);
        assertTrue(inter.containsNode(n2));
        g2.adoptNode(n1);
        assertTrue(inter.containsNode(n1));
        g1.adoptEdge(e2);
        assertTrue(inter.containsEdge(e2));
        g2.adoptEdge(e1);
        assertTrue(inter.containsEdge(e1));
        assertEquals(2, inter.nodeCount());
        assertEquals(2, inter.edgeCount());
        
        g1.removeAllEdges();
        assertEquals(0, inter.edgeCount());
        g1.removeAllNodes();
        assertTrue(inter.isEmpty());
    }
    
    public void testBasicSubtraction() {
        SecondaryGraph g1 = new SecondaryGraph();
        SecondaryGraph g2 = new SecondaryGraph();
        InspectableGraph sub = Graphs.subtraction(g1, g2);
        Node n1 = g1.newNode(1);
        assertTrue(sub.containsNode(n1));
        g2.adoptNode(n1);
        assertTrue(!sub.containsNode(n1));
        Edge e1 = g1.newEdge(n1, n1);
        assertTrue(sub.containsEdge(e1));
        g2.adoptEdge(e1);
        assertTrue(!sub.containsEdge(e1));
        
        g2.removeEdge(e1);
        assertTrue(sub.containsEdge(e1));
        g1.removeEdge(e1);
        assertTrue(!sub.containsEdge(e1));
        
        g1.removeAllNodes();
        assertTrue(sub.isEmpty());
    }
    
    public void testBasicXor() {
        SecondaryGraph g1 = new SecondaryGraph();
        Node[] n = g1.newNodes(1, 2, 3);
        SecondaryGraph g2 = new SecondaryGraph();
        g2.adoptNode(n[0]);
        InspectableGraph xor = Graphs.xor(g1, g2);
        assertFalse(xor.containsNode(n[0]));
        assertTrue(xor.containsNode(n[1]));
        assertTrue(xor.containsNode(n[2]));
        
        Node n4 = g1.newNode(4);
        assertTrue(xor.containsNode(n4));
        g2.adoptNode(n4);
        assertFalse(xor.containsNode(n4));
        g1.removeNode(n4);
        assertTrue(xor.containsNode(n4));
        g1.adoptNode(n4);
        
        g2.removeNode(n4);
        Edge e1 = g1.newEdge(n4, n4);
        assertTrue(xor.containsEdge(e1));
        g2.adoptEdge(e1);
        assertFalse(xor.containsEdge(e1));
        
        g1.removeAllNodes();
        assertTrue(xor.containsNode(n4));
    }
    
    public void testXorReinsertsEdges() {
        Graph g1 = new PrimaryGraph();
        SecondaryGraph g2 = new SecondaryGraph();
        InspectableGraph xor = Graphs.xor(g1, g2);
        Node[] n = g1.newNodes("owned", "shared");
        Edge e = g1.newEdge(n[0], n[1]);
        g2.adoptEdge(e);
        assertTrue(xor.isEmpty());
        g2.removeNode(n[0]);
        g2.removeNode(n[1]);
        assertTrue(xor.containsEdge(e));
    }
    
    //randomized test
    private final Graph g1 = new PrimaryGraph();
    private final Graph g2 = new SecondaryGraph();
    private final InspectableGraph union = Graphs.union(g1, g2);
    private final InspectableGraph intersection = Graphs.intersection(g1, g2);
    private final InspectableGraph subtraction = Graphs.subtraction(g1, g2);
    private final InspectableGraph xor = Graphs.xor(g1, g2);
    
    @Prob(1)
    void randomAddNode() {
        Graph graph = random.nextBoolean() ? g1 : g2;
        Node n = graph.newNode(getCurrentStep());
        checkConditions();
    }
    
    @Prob(1)
    void randomAddEdge() {
        Graph graph = random.nextBoolean() ? g1 : g2;
        precondition(graph.nodeCount() > 0);
        List<Node> nodes = graph.nodes().drainToList();
        graph.newEdge(pickRandom(nodes), pickRandom(nodes));
        checkConditions();
    }
    
    @Prob(1)
    void randomRemoveNode() {
        Graph graph = random.nextBoolean() ? g1 : g2;
        precondition(graph.nodeCount() > 0);
        List<Node> nodes = graph.nodes().drainToList();
        graph.removeNode(pickRandom(nodes));
        checkConditions();
    }
    
    @Prob(1)
    void randomRemoveEdge() {
        Graph graph = random.nextBoolean() ? g1 : g2;
        precondition(graph.edgeCount() > 0);
        List<Edge> edges = graph.edges().drainToList();
        graph.removeEdge(pickRandom(edges));
        checkConditions();
    }
    
    private void checkConditions() {
        checkUnion();
        checkIntersection();
        checkSubtraction();
        checkXor();
    }
    
    private void checkUnion() {
        for (Node n : allNodes()) {
            invariant(union.containsNode(n));
        }
        for (Edge e : allEdges()) {
            invariant(union.containsEdge(e));
        }
    }
    
    private void checkIntersection() {
        for (Node n : allNodes()) {
            if (g1.containsNode(n) && g2.containsNode(n)) {
                invariant(intersection.containsNode(n));
            } else {
                invariant(!intersection.containsNode(n));
            }
        }
        for (Edge e : allEdges()) {
            if (g1.containsEdge(e) && g2.containsEdge(e)) {
                invariant(intersection.containsEdge(e));
            } else {
                invariant(!intersection.containsEdge(e));
            }
        }
    }
    
    private void checkSubtraction() {
        for (Node n : allNodes()) {
            if (g1.containsNode(n) && !g2.containsNode(n)) {
                invariant(subtraction.containsNode(n));
            } else {
                invariant(!subtraction.containsNode(n));
            }
        }
        for (Edge e : allEdges()) {
            if (g1.containsEdge(e) && !g2.containsEdge(e)) {
                invariant(subtraction.containsEdge(e));
            } else {
                invariant(!subtraction.containsEdge(e));
            }
        }
    }
    
    private void checkXor() {
        for (Node n : allNodes()) {
            if (g1.containsNode(n) ^ g2.containsNode(n)) {
                invariant(xor.containsNode(n));
            } else {
                invariant(!xor.containsNode(n));
            }
        }
        for (Edge e : allEdges()) {
            if (g1.containsEdge(e) ^ g2.containsEdge(e)) {
                invariant(xor.containsEdge(e));
            } else {
                invariant(!xor.containsEdge(e));
            }
        }
    }
    
    private Set<Node> allNodes() {
        Set<Node> nodes = g1.nodes().drainToSet();
        g2.nodes().drainTo(nodes);
        return nodes;
    }
    
    private Set<Edge> allEdges() {
        Set<Edge> edges = g1.edges().drainToSet();
        g2.edges().drainTo(edges);
        return edges;
    }
    
    private <T> T pickRandom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
    
    public void testIsConnected() {
        Graph g = new PrimaryGraph();
        GraphBuilder pg = new GraphBuilder(g);
        Path path = pg.newPath(g.newNodes(5));
        assertTrue(GraphChecker.isConnected(g));
        Edge e = path.getEdge(2);
        g.removeEdge(e);
        assertFalse(GraphChecker.isConnected(g));
        g.reinsertEdge(e);
        assertTrue(GraphChecker.isConnected(g));
    }
    
    public void testPrettyPrint() {
        StringBuilder sb = new StringBuilder();
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(1, 2);
        g.newEdge(n[0], n[1]);
        Graphs.printPretty(g, sb);
        assertEquals("Nodes (count = 2):\n" +
                "1\n" +
                "2\n" +
                "\n" +
                "Edges (count = 1):\n" +
                "{1->2}\n", sb.toString());
    }
    
    public void testMaxDegree() {
        Graph g = new PrimaryGraph();
        assertEquals(0, Graphs.maxDegree(g));
        Node[] n = g.newNodes(3);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[2], n[0]);
        assertEquals(1, Graphs.maxDegree(g, Direction.OUT));
        assertEquals(2, Graphs.maxDegree(g, Direction.EITHER));
        assertEquals(2, Graphs.maxDegree(g));
    }
    
    public void testMinDegree() {
        Graph g = new PrimaryGraph();
        assertEquals(Integer.MAX_VALUE, Graphs.minDegree(g));
        Node[] n = g.newNodes(3);
        g.newEdge(n[0], n[1]);
        g.newEdge(n[2], n[0]);
        g.newEdge(n[2], n[2]);
        assertEquals(0, Graphs.minDegree(g, Direction.OUT));
        assertEquals(1, Graphs.minDegree(g, Direction.IN));
        assertEquals(1, Graphs.minDegree(g, Direction.EITHER));
        assertEquals(1, Graphs.minDegree(g));
    }
    
    public void testIsSequenceGraphical() {
        assertFalse(GraphChecker.isSequenceGraphical(3, 2, 0));
        assertTrue(GraphChecker.isSequenceGraphical(3, 3, 2, 2, 2));
        assertTrue(GraphChecker.isSequenceGraphical(3, 2, 2, 1));
        assertTrue(GraphChecker.isSequenceGraphical(1, 2, 1));
        assertTrue(GraphChecker.isSequenceGraphical(3, 3, 2, 1, 1));
    }
    
    public void testCollectNodes() {
        Graph g = new PrimaryGraph();
        Graphs.attachNodeNamer(g);
        Generators.createRandomDag(g, 7, 0.2);
        Graph closure = new SecondaryGraph(g);
        Transitivity.materialize(closure, Transitivity.acyclicClosure(closure, SuccessorSetFactory.hashSetBased()));
        for (Node n : g.nodes()) {
            Set<Node> nextNodes = Graphs.collectNodes(g, n, Direction.OUT);
            Set<Node> neighbors = closure.adjacentNodes(n, Direction.OUT).drainToSet();
            assertEquals(nextNodes, neighbors);
        }
    }
    
    public void testCollectNodesEmpty() {
        Graph g = new PrimaryGraph();
        Node n = g.newNode();
        assertTrue(Graphs.collectNodes(g, n, Direction.OUT).isEmpty());
    }
    
    public void testCollectNodesSingleton() {
        Graph g = new PrimaryGraph();
        Node n = g.newNode();
        g.newEdge(n, n);
        assertTrue(Graphs.collectNodes(g, n, Direction.OUT).size() == 1);
    }
}
