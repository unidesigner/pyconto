package gr.forth.ics.graph.io;

import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.layout.Locator;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface GraphReader {
    void read(Graph graph, Locator locator, InputStream in) throws IOException;
}
