package gr.forth.ics.graph;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import junit.framework.*;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.SecondaryGraph;
import gr.forth.ics.util.Filter;
import static gr.forth.ics.graph.Graphs.printCompact;

public class FiltersTest extends TestCase {
    
    public FiltersTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(FiltersTest.class);
        
        return suite;
    }
    
    private Graph graph;
    private Node[] n;
    
    public void setUp() {
        graph = new PrimaryGraph();
        n = graph.newNodes(0, 1, 2, 3, 4);
        GraphBuilder gb = new GraphBuilder(graph);
        gb.newEdges(false, n);
        graph.newEdge(n[0], n[2]);
        graph.newEdge(n[0], n[3]);
        graph.newEdge(n[0], n[4]);
        graph.newEdge(n[1], n[3]);
        graph.newEdge(n[1], n[4]);
        graph.newEdge(n[2], n[4]);
    }
    
    private String filter(Filter<Node> filter) {
        SecondaryGraph sg = new SecondaryGraph();
        sg.adoptNodes(graph.nodes().filter(filter));
        for (Edge e : graph.edges()) {
            if (sg.containsNode(e.n1()) && sg.containsNode(e.n2())) {
                sg.adoptEdge(e);
            }
        }
        return printCompact(sg).toString();
    }
    
    public void testDegreeEqual() {
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.degreeEqual(graph, 4)));
        assertEquals("[N={}, E={}]", filter(Filters.degreeEqual(graph, 3)));
        assertEquals("[N={}, E={}]", filter(Filters.degreeEqual(graph, 2)));
        assertEquals("[N={}, E={}]", filter(Filters.degreeEqual(graph, 1)));
        assertEquals("[N={}, E={}]", filter(Filters.degreeEqual(graph, 0)));
    }
    
    public void testInDegreeEqual() {
        assertEquals("[N={4}, E={}]", filter(Filters.inDegreeEqual(graph, 4)));
        assertEquals("[N={3}, E={}]", filter(Filters.inDegreeEqual(graph, 3)));
        assertEquals("[N={2}, E={}]", filter(Filters.inDegreeEqual(graph, 2)));
        assertEquals("[N={1}, E={}]", filter(Filters.inDegreeEqual(graph, 1)));
        assertEquals("[N={0}, E={}]", filter(Filters.inDegreeEqual(graph, 0)));
    }
    
    public void testOutDegreeEqual() {
        assertEquals("[N={0}, E={}]", filter(Filters.outDegreeEqual(graph, 4)));
        assertEquals("[N={1}, E={}]", filter(Filters.outDegreeEqual(graph, 3)));
        assertEquals("[N={2}, E={}]", filter(Filters.outDegreeEqual(graph, 2)));
        assertEquals("[N={3}, E={}]", filter(Filters.outDegreeEqual(graph, 1)));
        assertEquals("[N={4}, E={}]", filter(Filters.outDegreeEqual(graph, 0)));
    }
    
    public void testOr() {
        assertEquals("[N={0, 1}, E={{0->1}}]", filter(Filters.<Node>or(
                Filters.outDegreeEqual(graph, 4), Filters.outDegreeEqual(graph, 3))));
    }
    
    public void testAnd() {
        assertEquals("[N={0}, E={}]", filter(Filters.<Node>and(
                Filters.outDegreeEqual(graph, 4), Filters.inDegreeEqual(graph, 0))));
    }
    
    public void testDegreeAtMost() {
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.degreeAtMost(graph, 4)));
        assertEquals("[N={}, E={}]", filter(Filters.degreeAtMost(graph, 3)));
        assertEquals("[N={}, E={}]", filter(Filters.degreeAtMost(graph, 2)));
        assertEquals("[N={}, E={}]", filter(Filters.degreeAtMost(graph, 1)));
        assertEquals("[N={}, E={}]", filter(Filters.degreeAtMost(graph, 0)));
    }
    
    public void testOutDegreeAtMost() {
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.outDegreeAtMost(graph, 4)));
        assertEquals("[N={1, 2, 3, 4}, E={{1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.outDegreeAtMost(graph, 3)));
        assertEquals("[N={2, 3, 4}, E={{2->3}, {2->4}, {3->4}}]", filter(Filters.outDegreeAtMost(graph, 2)));
        assertEquals("[N={3, 4}, E={{3->4}}]", filter(Filters.outDegreeAtMost(graph, 1)));
        assertEquals("[N={4}, E={}]", filter(Filters.outDegreeAtMost(graph, 0)));
    }
    
    public void testInDegreeAtMost() {
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.inDegreeAtMost(graph, 4)));
        assertEquals("[N={0, 1, 2, 3}, E={{0->1}, {0->2}, {0->3}, {1->2}, {1->3}, {2->3}}]", filter(Filters.inDegreeAtMost(graph, 3)));
        assertEquals("[N={0, 1, 2}, E={{0->1}, {0->2}, {1->2}}]", filter(Filters.inDegreeAtMost(graph, 2)));
        assertEquals("[N={0, 1}, E={{0->1}}]", filter(Filters.inDegreeAtMost(graph, 1)));
        assertEquals("[N={0}, E={}]", filter(Filters.inDegreeAtMost(graph, 0)));
    }
    
    public void testDegreeAtLeast() {
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.degreeAtLeast(graph, 4)));
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.degreeAtLeast(graph, 3)));
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.degreeAtLeast(graph, 2)));
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.degreeAtLeast(graph, 1)));
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.degreeAtLeast(graph, 0)));
    }
    
    public void testOutDegreeAtLeast() {
        assertEquals("[N={0}, E={}]", filter(Filters.outDegreeAtLeast(graph, 4)));
        assertEquals("[N={0, 1}, E={{0->1}}]", filter(Filters.outDegreeAtLeast(graph, 3)));
        assertEquals("[N={0, 1, 2}, E={{0->1}, {0->2}, {1->2}}]", filter(Filters.outDegreeAtLeast(graph, 2)));
        assertEquals("[N={0, 1, 2, 3}, E={{0->1}, {0->2}, {0->3}, {1->2}, {1->3}, {2->3}}]", filter(Filters.outDegreeAtLeast(graph, 1)));
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.outDegreeAtLeast(graph, 0)));
    }
    
    public void testInDegreeAtLeast() {
        assertEquals("[N={4}, E={}]", filter(Filters.inDegreeAtLeast(graph, 4)));
        assertEquals("[N={3, 4}, E={{3->4}}]", filter(Filters.inDegreeAtLeast(graph, 3)));
        assertEquals("[N={2, 3, 4}, E={{2->3}, {2->4}, {3->4}}]", filter(Filters.inDegreeAtLeast(graph, 2)));
        assertEquals("[N={1, 2, 3, 4}, E={{1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.inDegreeAtLeast(graph, 1)));
        assertEquals("[N={0, 1, 2, 3, 4}, E={{0->1}, {0->2}, {0->3}, {0->4}, {1->2}, {1->3}, {1->4}, {2->3}, {2->4}, {3->4}}]", filter(Filters.inDegreeAtLeast(graph, 0)));
    }
    
    public void testEqualPropertyFilter() {
        n[3].put("2", 5);
        n[4].put("2", 5);
        assertEquals("[3, 4]", graph.nodes().filter(
                Filters.equalProperty("2", 5)).drainToList().toString());
    }
    
    public void testEqualPropertyFilterWithNulls() {
        n[0].put("1", null);
        n[1].put("1", null);
        assertEquals("[0, 1]", graph.nodes().filter(
                Filters.equalProperty("1", null)).drainToList().toString());
    }
}
