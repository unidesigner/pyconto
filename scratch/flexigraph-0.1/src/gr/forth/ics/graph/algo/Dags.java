package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.path.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Algorithms on DAGs.
 * 
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public class Dags {
    private Dags() { }

    /**
     * Finds a longest path in a DAG. The result of this method is undefined if the graph
     * is undirected.
     *
     * @param g the dag of which to find the longest path
     * @return a longest path
     */
    public static Path longestPath(InspectableGraph g) {
        if (g.nodeCount() == 0) {
            return null;
        }
        List<Node> topo = Orders.topological(g);
        Map<Node, Path> paths = new HashMap<Node, Path>(g.nodeCount());
        Path max = g.aNode().asPath();
        for (Node n1 : topo) {
            Path p1 = getPathOrCreate(paths, n1);
            for (Edge e : g.edges(n1, Direction.OUT)) {
                Node n2 = e.opposite(n1);
                Path p2 = getPathOrCreate(paths, n2);
                if (p2.size() < p1.size() + 1) {
                    Path newPath = p1.append(e.asPath(n1));
                    paths.put(n2, newPath);
                    if (max.size() < newPath.size()) {
                        max = newPath;
                    }
                }
            }
        }
        return max;
    }

    private static Path getPathOrCreate(Map<Node, Path> map, Node n) {
        Path p = map.get(n);
        if (p == null) {
            p = n.asPath();
            map.put(n, p);
        }
        return p;
    }
}
