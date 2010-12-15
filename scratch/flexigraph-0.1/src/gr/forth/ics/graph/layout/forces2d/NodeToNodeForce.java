package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public abstract class NodeToNodeForce extends AbstractForce {
    protected void apply(InspectableGraph graph, ForceAggregator forceMap) {
        for (Node n1 : graph.nodes()) {
            for (Node n2: graph.nodes()) {
                if (n1 == n2) {
                    continue;
                }
                apply(n1, n2, forceMap);
            }
        }
    }
    
    protected abstract void apply(Node n1, Node n2, ForceAggregator forceMap);
}
