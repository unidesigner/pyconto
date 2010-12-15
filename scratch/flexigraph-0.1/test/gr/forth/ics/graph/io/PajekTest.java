package gr.forth.ics.graph.io;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.algo.Generators;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.PrimaryGraph;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import junit.framework.TestCase;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class PajekTest extends TestCase {
    
    public PajekTest(String testName) {
        super(testName);
    }

    public void test() throws IOException {
        Graph g = new PrimaryGraph();
        Graphs.attachNodeNamer(g);
        Generators.createRandom(g, 100, 0.2);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GraphIO.write(Format.PAJEK, g, out);

        Graph g2 = new PrimaryGraph();
        GraphIO.read(Format.PAJEK, g2, new ByteArrayInputStream(out.toByteArray()));

        assertEquals(g.toString(), g2.toString());
    }
}
