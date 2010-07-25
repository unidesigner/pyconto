package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.layout.Layout;
import gr.forth.ics.graph.layout.LayoutProcess;
import gr.forth.ics.graph.layout.Locator;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class FruchtermanReingoldLayout implements Layout {
    private final double temperatureDecrease;
    private double initialTemperature;
    private final List<Force> forces;
    
    public FruchtermanReingoldLayout() {
        this(10, 40, 100, 0.01);
    }
    
    public FruchtermanReingoldLayout(double force, double lambda,
            double initialTemperature, double temperatureDecrease) {
        this.forces = Arrays.<Force>asList(
                new Forces.NodeRepulsion(force),
                new Forces.Gravity(GPoint.ZERO_POINT, 0.001),
                new Forces.FRSpring(lambda));
        this.initialTemperature = initialTemperature;
        this.temperatureDecrease = temperatureDecrease;
    }
    
    public LayoutProcess layout(InspectableGraph graph) {
        return new ForceLayoutProcess(graph, forces) {
            double temperature = initialTemperature;
            
            public boolean isDone() {
                return temperature < 1.0;
            }
            
            protected void step(InspectableGraph graph, Locator locator, ForceAggregator forceMap) {
                super.step(graph, locator, forceMap);
                temperature *= (1.0 - temperatureDecrease);
            }
            
            protected ForceAggregator createForceMap(InspectableGraph graph) {
                return ForceAggregators.temperature(super.createForceMap(graph),
                        temperature);
            }
        };
    }
}
