package examples.io;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.io.Format;
import gr.forth.ics.graph.io.GraphIO;
import gr.forth.ics.graph.layout.BasicLocator;
import gr.forth.ics.graph.layout.Locator;
import java.io.IOException;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class GmlOutputWithLayout {
    public static void main(String[] args) throws IOException {
        Graph g = new PrimaryGraph();
        Node n1 = g.newNode();
        Node n2 = g.newNode();
        Edge e = g.newEdge(n1, n2);
        
        Locator locator = new BasicLocator();
        locator.setLocation(n1, new GPoint(0, 0));
        locator.setLocation(n2, new GPoint(100, 100));
        locator.setBend(e, new GPoint(80, 20));

        GraphIO.write(Format.GML, g, System.out);
    }
}
