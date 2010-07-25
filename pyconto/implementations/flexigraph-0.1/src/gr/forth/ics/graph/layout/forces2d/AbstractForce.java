package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.layout.Locator;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public abstract class AbstractForce implements Force {
    protected Locator locator;
    
    public final void apply(InspectableGraph graph, Locator locator, ForceAggregator forceMap) {
        this.locator = locator;
        apply(graph, forceMap);
    }
    
    protected abstract void apply(InspectableGraph graph, ForceAggregator forceMap);
}
