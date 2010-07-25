package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.layout.Locator;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface Force {
    void apply(InspectableGraph graph, Locator locator, ForceAggregator forceMap);
}
