package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.graph.Node;

/**
 * A mutable {@link SuccessorSet}. 
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface MutableSuccessorSet extends SuccessorSet {
    /**
     * Adds a node to this set.
     *
     * @param node the node to add to this set (should not be {@code null})
     */
    void add(Node node);
    
    /**
     * Adds a set to this set.
     *
     * @param successorSet the set to add to this set (should not be {@code null})
     */
    void addAll(SuccessorSet successorSet);
}
