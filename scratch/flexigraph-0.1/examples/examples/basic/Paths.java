package examples.basic;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.path.Path;

/**
 * A simple example that shows paths in action.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class Paths {
    public static void main(String[] args) {
        Graph g = new PrimaryGraph();
        Node[] n = g.newNodes(0, 1, 2, 3);
        Edge e01 = g.newEdge(n[0], n[1]);
        Edge e12 = g.newEdge(n[1], n[2]);
        Edge e23 = g.newEdge(n[2], n[3]);

        Path p1 = n[1].asPath(); //[1]
        Path p12 = p1.append(e12.asPath()); //[1-->2]
        Path p123 = p12.append(e23.asPath()); //[1-->2-->3]

        Path p10 = p1.append(e01.asPath(e01.n2())); //[1<--0]
        //p10.headNode() == n[1]
        //p10.tailNode() == n[0]

        Path p101 = p10.append(e01.asPath()); //[1<--0-->1]

        //replaces [1] with [1<--0-->1]
        Path p10123 = p123.replaceFirst(p1, p101);
        System.out.println(p10123); //prints [1-->0<--1-->2-->3]
    }
}
