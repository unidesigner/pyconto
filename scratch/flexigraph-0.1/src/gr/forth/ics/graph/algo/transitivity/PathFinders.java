package gr.forth.ics.graph.algo.transitivity;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.algo.Dfs;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.path.Traverser;

/**
 * Static factories of {@code PathFinder} instances.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class PathFinders {
    private PathFinders() { }

    /**
     * Creates a {@link PathFinder} that answers whether two nodes are connected
     * simply by doing an exploration starting from the first node, and checking whether the
     * second can be reached via some path.
     *
     * @param g the graph which the created {@code PathFinder} will navigate when
     * searching connecting paths for node pairs
     * @return a {@code PathFinder} that answers whether two nodes are connected by
     * exploring the paths starting from the first node, and checking whether the second can be
     * reached
     */
    public static PathFinder navigational(InspectableGraph g) {
        if (g == null) throw new NullPointerException();
        return new NavigationalPathFinder(g);
    }

    private static class NavigationalPathFinder implements PathFinder {
        private final InspectableGraph g;

        NavigationalPathFinder(InspectableGraph g) {
            this.g = g;
        }

        public boolean pathExists(Node n1, Node n2) {
            for (Path path : Traverser.newDfs().notRepeatingNodesExcludingStart().build().traverse(g, n1, Direction.OUT)) {
                if (path.size() == 0 || path.tailNode() != n2) {
                    continue;
                }
                return true;
            }
            return false;
        }
    }
}
