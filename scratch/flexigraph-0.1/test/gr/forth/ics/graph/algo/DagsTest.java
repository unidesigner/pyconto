package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.GraphException;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import junit.framework.TestCase;

public class DagsTest extends TestCase {
    public DagsTest(String testName) {
        super(testName);
    }

    public void test() {
        Graph g = new PrimaryGraph();

        Node n1 = g.newNode("1");
        Node n2 = g.newNode("2");
        Node n3 = g.newNode("3");
        Node n4 = g.newNode("4");
        Node n5 = g.newNode("5");
        g.newEdge(n1, n2);
        g.newEdge(n2, n3);
        g.newEdge(n2, n4);
        g.newEdge(n4, n5);
        g.newEdge(n3, n5);
        assertEquals("[1-->2-->3-->5]", Dags.longestPath(g).toString());
        assertEquals("[5<--3<--2<--1]", Dags.longestPath(Graphs.inverted(g)).toString());
    }

    public void testMustBeDag() {
        Graph g = new PrimaryGraph();

        Node n1 = g.newNode("1");
        g.newEdge(n1, n1);

        try {
            Dags.longestPath(g);
            fail();
        } catch (GraphException ok) { }
    }
}