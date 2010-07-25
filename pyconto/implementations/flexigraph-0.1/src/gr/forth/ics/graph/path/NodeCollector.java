package gr.forth.ics.graph.path;

import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.path.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @deprecated
 * @author user
 */
public class NodeCollector implements Visitor {
    private final Set<Node> nodes = new HashSet<Node>();

    public Traversal visit(Path path) {
        nodes.add(path.tailNode());
        return Traversal.CONTINUE;
    }
    
    public Set<Node> getVisitedNodes() {
        return Collections.unmodifiableSet(nodes);
    }
}
