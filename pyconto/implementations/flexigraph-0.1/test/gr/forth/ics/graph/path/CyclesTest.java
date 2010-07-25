package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import junit.framework.TestCase;

public class CyclesTest extends TestCase {
    public CyclesTest(String name) {
        super(name);
    }

    Graph g = new PrimaryGraph();

    public void testNoCycle() {
        g.newEdge(g.newNode(), g.newNode());
        assertNull(Cycles.findCycle(g));
    }

    public void testSelfLoop() {
        Node n = g.newNode("1");
        g.newEdge(n, n);
        Path p = Cycles.findCycle(g);
        assertEquals("[1-->1]", p.toString());
    }

    public void testSimpleLoop() {
        Node n1 = g.newNode("1");
        Node n2 = g.newNode("2");
        Node n3 = g.newNode("3");
        Node n4 = g.newNode("4");
        g.newEdge(n1, n2);
        g.newEdge(n2, n3);
        g.newEdge(n3, n4);
        g.newEdge(n4, n2);
        Path p = Cycles.findCycle(g);
        assertEquals("[2-->3-->4-->2]", p.toString());
    }

    public void testDirection() {
        Node n1 = g.newNode("1");
        Node n2 = g.newNode("2");
        Node n3 = g.newNode("3");
        Node n4 = g.newNode("4");
        g.newEdge(n1, n2);
        g.newEdge(n2, n3);
        g.newEdge(n3, n4);
        g.newEdge(n2, n4); // not a directed cycle!
        assertNull(Cycles.findCycle(g));

        Path p = Cycles.findCycle(Graphs.undirected(g));
        assertEquals("[2-->3-->4<--2]", p.toString());
    }
}