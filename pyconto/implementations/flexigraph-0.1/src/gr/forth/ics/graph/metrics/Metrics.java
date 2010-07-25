package gr.forth.ics.graph.metrics;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.util.Args;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class Metrics {
    private Metrics() {
    }
    
    public static NodeMetric degreeMetric(InspectableGraph g, Direction direction) {
        return new DegreeMetric(g, direction);
    }
    
    public static NodeMetric inDegreeMetric(InspectableGraph g) {
        return new DegreeMetric(g, Direction.IN);
    }
    
    public static NodeMetric outDegreeMetric(InspectableGraph g) {
        return new DegreeMetric(g, Direction.OUT);
    }
    
    public static NodeMetric degreeMetric(InspectableGraph g) {
        return new DegreeMetric(g);
    }
    
    public static NodeMetric normalizedDegreeMetric(InspectableGraph g, Direction direction) {
        return new NormalizedDegreeMetric(g, direction);
    }
    
    public static NodeMetric normalizedInDegreeMetric(InspectableGraph g) {
        return new NormalizedDegreeMetric(g);
    }
    
    public static NodeMetric normalizedOutDegreeMetric(InspectableGraph g) {
        return new NormalizedDegreeMetric(g, Direction.OUT);
    }
    
    public static NodeMetric normalizedDegreeMetric(InspectableGraph g) {
        return new NormalizedDegreeMetric(g);
    }
    
    private static class DegreeMetric implements NodeMetric {
        protected final InspectableGraph g;
        protected final Direction dir;
        
        public DegreeMetric(InspectableGraph g) {
            this(g, Direction.EITHER);
        }
        
        public DegreeMetric(InspectableGraph g, Direction dir) {
            Args.notNull(g, dir);
            this.g = g;
            this.dir = dir;
        }
        
        public double getValue(Node element) {
            return g.degree(element, dir);
        }
    }
    
    private static class NormalizedDegreeMetric extends DegreeMetric {
        public NormalizedDegreeMetric(InspectableGraph g) {
            super(g);
        }
        
        public NormalizedDegreeMetric(InspectableGraph g, Direction dir) {
            super(g, dir);
        }
        
        public double getValue(Node element) {
            return super.getValue(element) / (g.nodeCount() - 1);
        }
    }
}
