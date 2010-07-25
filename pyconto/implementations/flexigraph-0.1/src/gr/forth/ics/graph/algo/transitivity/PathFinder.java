package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.graph.Node;

/**
 * An object that can answer whether there exists a path from a node to another.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface PathFinder {
    /**
     * Returns whether there is a path from node {@code n1} to node {@code n2}.
     *
     * @param n1 the node that a path, if such exists, should start
     * @param n2 the node that a path, if such exists, should end
     * @return {@code true} if there is a path from {@code n1} to {@code n2}, {@code false}
     * otherwise
     */
    boolean pathExists(Node n1, Node n2);
}
