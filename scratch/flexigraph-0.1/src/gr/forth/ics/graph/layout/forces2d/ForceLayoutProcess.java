package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.layout.AbstractLayoutProcess;
import gr.forth.ics.graph.layout.Locator;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import gr.forth.ics.util.Args;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public abstract class ForceLayoutProcess extends AbstractLayoutProcess {
    private final InspectableGraph graph;
    private final List<Force> forces;
    
    public ForceLayoutProcess(InspectableGraph graph, List<Force> forces) {
        Args.notNull(graph, forces);
        this.graph = graph;
        this.forces = forces;
    }
    
    public ForceLayoutProcess(InspectableGraph graph, Force ... forces) {
        this(graph, Arrays.asList(forces));
    }
    
    protected void stepImpl(Locator locator) {
        ForceAggregator forceMap = createForceMap(graph);
        step(graph, locator, forceMap);
        for (Node n : graph.nodes()) {
            locator.setLocation(n, locator.getLocation(n).add(forceMap.getForce(n)));
        }
    }
    
    /**
     * Called before anything else, once.
     */
    protected void initProcess(InspectableGraph graph) { }
    
    protected ForceAggregator createForceMap(InspectableGraph graph) {
        final Object KEY = new Object();
        for (Node n : graph.nodes()) {
            n.putWeakly(KEY, new Point2D.Double());
        }
        return ForceAggregators.simple(KEY);
    }
    
    protected void step(InspectableGraph graph, Locator locator,
            ForceAggregator forceMap) {
        for (Force f : forces) {
            f.apply(graph, locator, forceMap);
        }
    }
}
