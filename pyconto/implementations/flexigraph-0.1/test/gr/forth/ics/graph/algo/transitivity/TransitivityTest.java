package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Node;
import junit.framework.*;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.SecondaryGraph;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TransitivityTest extends TestCase {
    private final Random random = new Random(0);
    
    public TransitivityTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TransitivityTest.class);
        
        return suite;
    }

    public void testAcyclicReductionAndClosure_HashSetBased() {
        Graph g = new PrimaryGraph();
        Generators.createRandomDag(g, random, 30, 0.2);
        SecondaryGraph copy = new SecondaryGraph(g);
        Transitivity.acyclicReduction(g, PathFinders.navigational(g));

        Closure c1 = Transitivity.acyclicClosure(g, SuccessorSetFactory.hashSetBased());
        Closure c2 = Transitivity.acyclicClosure(copy, SuccessorSetFactory.hashSetBased());

        Transitivity.materialize(g, c1);
        Transitivity.materialize(copy, c2);

        assertEquals(g.edgeCount(), copy.edgeCount());
        for (Edge e : g.edges()) {
            assertTrue(copy.areAdjacent(e.n1(), e.n2(), Direction.OUT));
        }
    }

    public void testAcyclicReductionAndClosure_IntervalBased() {
        Graph g = new PrimaryGraph();
        Generators.createRandomDag(g, random, 30, 0.2);
        SecondaryGraph copy = new SecondaryGraph(g);
        Transitivity.acyclicReduction(g, PathFinders.navigational(g));

        Closure c1 = Transitivity.acyclicClosure(g, SuccessorSetFactory.intervalBased(g));
        Closure c2 = Transitivity.acyclicClosure(copy, SuccessorSetFactory.intervalBased(g));

        Transitivity.materialize(g, c1);
        Transitivity.materialize(copy, c2);

        assertEquals(g.edgeCount(), copy.edgeCount());
        for (Edge e : g.edges()) {
            assertTrue(copy.areAdjacent(e.n1(), e.n2(), Direction.OUT));
        }
    }

    public void testAcyclicClosureSweep_HashSetBased() {
        Graph g = new PrimaryGraph();
        Generators.createRandomDag(g, random, 30, 0.2);
        final Closure closure = Transitivity.acyclicClosure(g, SuccessorSetFactory.hashSetBased());
        final Set<Node> visited = new HashSet<Node>();

        Transitivity.acyclicClosureSweep(g, SuccessorSetFactory.hashSetBased(), new Transitivity.SuccessorsListener() {
            public void process(Node node, SuccessorSet successorSet) {
                assertEquals(closure.successorsOf(node).toSet(), successorSet.toSet());
                visited.add(node);
            }
        });
        assertEquals(g.nodes().drainToSet(), visited);
    }
    
    public void testAcyclicClosureSweep_IntervalBased() {
        Graph g = new PrimaryGraph();
        Generators.createRandomDag(g, random, 30, 0.2);
        final Closure closure = Transitivity.acyclicClosure(g, SuccessorSetFactory.intervalBased(g));
        final Set<Node> visited = new HashSet<Node>();

        Transitivity.acyclicClosureSweep(g, SuccessorSetFactory.intervalBased(g), new Transitivity.SuccessorsListener() {
            public void process(Node node, SuccessorSet successorSet) {
                assertEquals(closure.successorsOf(node).toSet(), successorSet.toSet());
                visited.add(node);
            }
        });
        assertEquals(g.nodes().drainToSet(), visited);
    }

//    public void testClosure() {
//        Graph g = new PrimaryGraph();
//        Graphs.attachNodeNamer(g);
//        Generators.createRandom(g, random, 40, 0.3);
//        Graph copy = new SecondaryGraph(g);
//        FullReachability r0 = Transitivity.closure(g);
//        FullReachability r = Reachability.lazy(copy);
//        for (Node n1 : g.nodes()) {
//            for (Node n2 : g.nodes()) {
//                assertEquals(r.pathExists(n1, n2), g.areAdjacent(n1, n2, Direction.OUT));
//            }
//        }
//        assertEqualReachability(r0, r, copy.nodes());
//    }
//
//    public void testClosureOfCycle() {
//        Graph g = new PrimaryGraph();
//        Graphs.attachNodeNamer(g);
//        Node[] n = g.newNodes(4);
//        g.newEdge(n[0], n[1]);
//        g.newEdge(n[1], n[2]);
//        g.newEdge(n[2], n[3]);
//        g.newEdge(n[3], n[0]);
//
//        Graph copy = new SecondaryGraph(g);
//        Transitivity.closure(g);
//        FullReachability r = Reachability.lazy(copy);
//        for (Node n1 : g.nodes()) {
//            for (Node n2 : g.nodes()) {
//                assertEquals(r.pathExists(n1, n2), g.areAdjacent(n1, n2, Direction.OUT));
//            }
//        }
//    }
    
//    public void testReduction() {
//        Graph g = new PrimaryGraph();
//        Graphs.attachNodeNamer(g);
//        Generators.createRandom(g, random, 40, 0.3);
//
//        Graph copy = new SecondaryGraph(g);
//        FullReachability r0 = Transitivity.reduction(g);
//        FullReachability originalReachability = Reachability.lazy(copy);
//        FullReachability reducedReachability = Reachability.lazy(g);
//
//        for (Edge e : copy.edges().drainToList()) {
//            boolean edgeReduced = !g.areAdjacent(e.n1(), e.n2(), Direction.OUT);
//            //testing for adjacency, rather than testing for edge "e" containment
//            //since it cannot be assumed that the edge remains the same.
//            if (edgeReduced) {
//                assertTrue(originalReachability.pathExists(e.n1(), e.n2()));
//            } else {
//                Edge edge = g.anEdge(e.n1(), e.n2(), Direction.OUT);
//                g.removeEdge(edge);
//                assertFalse(reducedReachability.pathExists(e.n1(), e.n2()));
//                g.reinsertEdge(edge);
//            }
//        }
//        assertEqualReachability(r0, reducedReachability, copy.nodes());
//    }
    
//    public void testReductionAndClosureAndReduction() {
//        Graph g = new PrimaryGraph();
//        Graphs.attachNodeNamer(g);
//        Generators.createRandomDag(g, random, 40, 0.6);
//        FullReachability r1 = Transitivity.reduction(g);
//
//        Graph copy = new SecondaryGraph(g);
//        FullReachability r2 = Transitivity.closure(g);
//        FullReachability r3 = Transitivity.reduction(g);
//        //this assertion applies only in dags
//        assertEqualEdges(copy, g);
//        assertEqualReachability(r1, r2, g.nodes());
//        assertEqualReachability(r2, r3, g.nodes());
//    }
//
//    public void testClosureDoesNotInsertRedundantEdges() {
//        Graph g = new PrimaryGraph();
//        Generators.createRandom(g, random, 40, 0.3);
//
//        Transitivity.closure(g);
//        Graph copy = new SecondaryGraph(g);
//
//        Transitivity.closure(g);
//        assertEqualEdges(copy, g);
//    }
//
//    private static void assertEqualEdges(Graph g1, Graph g2) {
//        assertEquals("Equal edges", g1.edgeCount(), g2.edgeCount());
//        for (Edge e : g1.edges()) {
//            assertTrue(g2.areAdjacent(e.n1(), e.n2(), Direction.OUT));
//        }
//    }
//
//    private static void assertEqualReachability(FullReachability r1, FullReachability r2, Iterable<Node> nodes) {
//        for (Node n1 : nodes) {
//            for (Node n2 : nodes) {
//                assertEquals(r1.pathExists(n1, n2), r2.pathExists(n1, n2));
//            }
//        }
//    }
}
