package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Edge;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @deprecated
 * @author user
 */
public class EdgeCollector implements Visitor {
    private final Set<Edge> edges = new HashSet<Edge>();

    public Traversal visit(Path path) {
        if (path.edgeCount() > 0) {
            edges.add(path.tailEdge());
        }
        return Traversal.CONTINUE;
    }

    public Set<Edge> getVisitedEdges() {
        return Collections.unmodifiableSet(edges);
    }
}
