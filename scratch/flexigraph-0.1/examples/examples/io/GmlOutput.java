package examples.io;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.io.Format;
import gr.forth.ics.graph.io.GraphIO;
import java.io.IOException;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class GmlOutput {
    public static void main(String[] args) throws IOException {
        Graph g = new PrimaryGraph();
        Node n1 = g.newNode();
        Node n2 = g.newNode();
        Edge e = g.newEdge(n1, n2);

        GraphIO.write(Format.GML, g, System.out);
    }
}
