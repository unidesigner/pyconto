package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public abstract class EdgeForce extends AbstractForce {
    protected void apply(InspectableGraph graph, ForceAggregator forceMap) {
        for (Edge e : graph.edges()) {
            apply(e, forceMap);
        }
    }
    
    protected abstract void apply(Edge e, ForceAggregator forceMap);
}
