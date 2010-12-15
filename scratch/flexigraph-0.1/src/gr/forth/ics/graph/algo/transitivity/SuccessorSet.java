package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.graph.Node;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a set of nodes, used to model successor sets in {@link Closure}.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface SuccessorSet extends Iterable<Node> {
    /**
     * Returns whether the specified node is contained in this set.
     *
     * @param node the node to check whether is contained in this set
     * @return whether the specified node is contained in this set
     */
    boolean contains(Node node);

    /**
     * Returns a {@link Set} instance containing all the nodes of this set.
     *
     * @return a {@code Set} instance containing all the nodes of this set
     */
    Set<Node> toSet();

    /**
     * Returns an iterator over all nodes of this set. 
     *
     * @return an iterator over all nodes of this set
     */
    Iterator<Node> iterator();
}
