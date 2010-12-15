package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.path.Traverser.PathIterator;
import java.util.ArrayList;
import java.util.List;
import junit.framework.*;

public class TraverserTest extends TestCase {
    public TraverserTest(String name) {
        super(name);
    }

    public void testSimple() {
        Graph g = new PrimaryGraph();
        Node a = g.newNode("A");
        Node aa = g.newNode("AA");
        Node aaa = g.newNode("AAA");
        Node ab = g.newNode("AB");
        Node aba = g.newNode("ABA");
        g.newEdge(a, aa);
        g.newEdge(aa, aaa);
        g.newEdge(a, ab);
        g.newEdge(ab, aba);

        List<Path> paths = new ArrayList<Path>();

        //DFS
        for (Path path : Traverser.newDfs().build().traverse(g, a, Direction.OUT)) {
            paths.add(path);
        }
        assertEquals("[[A], [A-->AB], [A-->AB-->ABA], [A-->AA], [A-->AA-->AAA]]", paths.toString());

        //BFS
        paths.clear();
        for (Path path : Traverser.newBfs().build().traverse(g, a, Direction.OUT)) {
            paths.add(path);
        }
        assertEquals("[[A], [A-->AA], [A-->AB], [A-->AA-->AAA], [A-->AB-->ABA]]", paths.toString());

        //opposite DFS
        paths.clear();
        for (Path path : Traverser.newDfs().build().traverse(g, a, Direction.IN)) {
            paths.add(path);
        }
        assertEquals("[[A]]", paths.toString());

        //opposite BFS
        paths.clear();
        for (Path path : Traverser.newBfs().build().traverse(g, a, Direction.IN)) {
            paths.add(path);
        }
        assertEquals("[[A]]", paths.toString());
    }

    public void testNonRepeatingNodes() {
        Graph g = new PrimaryGraph();
        Node a = g.newNode("A");
        Node b = g.newNode("B");
        Node c = g.newNode("C");
        Node d = g.newNode("D");
        g.newEdge(a, b);
        g.newEdge(a, c);
        g.newEdge(b, d);
        g.newEdge(c, d);

        List<Path> paths = new ArrayList<Path>();

        //DFS
        for (Path path : Traverser.newDfs().notRepeatingNodes().build().traverse(g, a, Direction.OUT)) {
            paths.add(path);
        }
        assertEquals("[[A], [A-->C], [A-->C-->D], [A-->B]]", paths.toString());

        //BFS
        paths.clear();
        for (Path path : Traverser.newBfs().notRepeatingNodes().build().traverse(g, a, Direction.OUT)) {
            paths.add(path);
        }
        assertEquals("[[A], [A-->B], [A-->C], [A-->B-->D]]", paths.toString());

        //First start at D, then A
        paths.clear();
        Traverser traverser = Traverser.newDfs().notRepeatingNodes().build();
        for (Path path : traverser.traverse(g, d, Direction.OUT)) {
            paths.add(path);
        }
        assertEquals("[[D]]", paths.toString());
        paths.clear();
        for (Path path : traverser.traverse(g, a, Direction.OUT)) {
            paths.add(path);
        }
        assertEquals("[[A], [A-->C], [A-->B]]", paths.toString());
    }

    public void testNonRepeatingEdges() {
        Graph g = new PrimaryGraph();
        Node a = g.newNode("A");
        Node b = g.newNode("B");
        Node c = g.newNode("C");
        Node d = g.newNode("D");
        g.newEdge(a, b);
        g.newEdge(a, c);
        g.newEdge(b, d);
        g.newEdge(c, d);
        g.newEdge(d, a);

        List<Path> paths = new ArrayList<Path>();

        //DFS
        for (Path path : Traverser.newDfs().notRepeatingEdges().build().traverse(g, a, Direction.OUT)) {
            paths.add(path);
        }
        assertEquals("[[A], [A-->C], [A-->C-->D], [A-->C-->D-->A], [A-->B], [A-->B-->D]]", paths.toString());

        //BFS
        paths.clear();
        for (Path path : Traverser.newBfs().notRepeatingEdges().build().traverse(g, a, Direction.OUT)) {
            paths.add(path);
        }
        assertEquals("[[A], [A-->B], [A-->C], [A-->B-->D], [A-->C-->D], [A-->B-->D-->A]]", paths.toString());
    }

    public void testSkip() {
        Graph g = new PrimaryGraph();
        Node a = g.newNode("A");
        Node b = g.newNode("B");
        Node c = g.newNode("C");
        Node d = g.newNode("D");
        g.newEdge(a, b);
        g.newEdge(a, c);
        g.newEdge(b, d);
        g.newEdge(c, d);
        g.newEdge(d, a);

        List<Path> paths = new ArrayList<Path>();

        //DFS
        PathIterator iterator = Traverser.newDfs().notRepeatingEdges().build().traverse(g, a, Direction.OUT).iterator();
        while (iterator.hasNext()) {
            Path path = iterator.next();
            if (path.tailNode() == c) {
                iterator.skipExplorationOfLastPath();
            }
            paths.add(path);
        }
        assertEquals("[[A], [A-->C], [A-->B], [A-->B-->D], [A-->B-->D-->A]]", paths.toString());

        //BFS
        paths.clear();

        iterator = Traverser.newBfs().notRepeatingEdges().build().traverse(g, a, Direction.OUT).iterator();
        while (iterator.hasNext()) {
            Path path = iterator.next();
            if (path.tailNode() == b) {
                iterator.skipExplorationOfLastPath();
            }
            paths.add(path);
        }
        assertEquals("[[A], [A-->B], [A-->C], [A-->C-->D], [A-->C-->D-->A]]", paths.toString());
    }
}