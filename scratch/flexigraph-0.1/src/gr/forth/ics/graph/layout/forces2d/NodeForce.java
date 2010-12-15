package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public abstract class NodeForce extends AbstractForce {
    protected void apply(InspectableGraph graph, ForceAggregator forceMap) {
        for (Node n : graph.nodes()) {
            apply(n, forceMap);
        }
    }
    
    protected abstract void apply(Node n, ForceAggregator forceMap);
}