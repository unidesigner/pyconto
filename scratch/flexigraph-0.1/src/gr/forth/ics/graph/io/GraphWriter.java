package gr.forth.ics.graph.io;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.layout.Locator;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface GraphWriter {
    void write(InspectableGraph graph, Locator locator, OutputStream out) throws IOException;
}
