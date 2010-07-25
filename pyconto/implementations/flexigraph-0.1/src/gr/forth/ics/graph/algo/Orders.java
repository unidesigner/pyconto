package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.GraphException;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.path.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class Orders {
    private Orders() { }
    
    /**
     * Returns a topological order of the specified graph, which must be acyclic.
     *
     * @param g the graph (must be acyclic) of which the nodes to be put in topological order
     * @return a list representing the topological order
     */
    public static List<Node> topological(InspectableGraph g) {
        return topological0(Graphs.inverted(g));
    }

    /**
     * Returns an inverse topological order of the specified graph, which must be acyclic.
     *
     * @param g the graph (must be acyclic) of which the nodes to be put in topological order
     * @return a list representing the topological order
     */
    public static List<Node> reverseTopological(InspectableGraph g) {
        return topological0(g);
    }

    private static List<Node> topological0(InspectableGraph g) {
        final List<Node> list = new ArrayList<Node>(g.nodeCount());
        new Dfs(g, Direction.OUT) {
            @Override
            protected boolean visitPost(Path path) {
                list.add(path.tailNode());
                return false;
            }

            @Override
            protected boolean visitBackEdge(Path path) {
                Path cycle = path.tailPath(path.size() - path.find(path.tailNode().asPath()));
                throw new GraphException("Cycle detected while doing topological sort: " + cycle);
            }
        }.execute();

        return list;
    }
}
