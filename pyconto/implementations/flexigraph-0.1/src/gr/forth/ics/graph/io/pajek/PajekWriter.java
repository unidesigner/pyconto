package gr.forth.ics.graph.io.pajek;

import gr.forth.ics.graph.io.*;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.layout.Locator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class PajekWriter implements GraphWriter {
    private static final PajekWriter instance = new PajekWriter();

    private PajekWriter() {
    }

    public static PajekWriter instance() {
        return instance;
    }

    public void write(InspectableGraph graph, Locator locator, OutputStream out) throws IOException {
        Map<Node, Integer> ids = new HashMap<Node, Integer>(graph.nodeCount());
        int i = 0;
        for (Node node : graph.nodes()) {
            ids.put(node, ++i);
        }
        PrintStream p = new PrintStream(out);
        p.print("*Vertices "); p.println(graph.nodeCount());
        for (Node node : graph.nodes()) {
            p.print(" "); p.print(ids.get(node)); p.print(" "); p.println(node);
        }
        p.println("*Edges ");
        for (Edge edge : graph.edges()) {
            p.print(ids.get(edge.n1())); p.print(" "); p.println(ids.get(edge.n2()));
        }
        out.flush();
    }
}
