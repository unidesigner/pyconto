package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.graph.Node;

/**
 * A closure provides the successor set for every node of some graph, {@literal i.e.} all the
 * reachable nodes from a particular node. Apart from enumerating the successors, it can also,
 * as a {@link PathFinder}, answer whether there is a path from a node to another.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface Closure extends PathFinder {
    /**
     * Returns the successor set of a node, that is, all the reachable nodes from the specified node.
     *
     * @param node the node to get the successors of
     * @return the successor set of the specified node
     */
    SuccessorSet successorsOf(Node node);
}
