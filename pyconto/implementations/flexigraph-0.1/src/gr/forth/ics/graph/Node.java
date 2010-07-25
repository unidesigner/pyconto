package gr.forth.ics.graph;

import gr.forth.ics.graph.path.Path;
import gr.forth.ics.util.Accessor;
import gr.forth.ics.graph.Tuple;

/**
 * A Node is the basic element of a {@link Graph}.
 *
 * @see Graph
 * @see Edge
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface Node extends Tuple {
    Path asPath();
}
