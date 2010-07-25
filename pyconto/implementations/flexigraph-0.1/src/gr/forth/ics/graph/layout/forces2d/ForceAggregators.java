package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.Node;
import java.awt.geom.Point2D;
import gr.forth.ics.util.Args;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class ForceAggregators {
    private ForceAggregators() {
    }
    
    public static ForceAggregator simple(Object key) {
        return new Simple(key);
    }
    
    public static ForceAggregator scaled(ForceAggregator delegate, double scale) {
        return new Scaled(delegate, scale);
    }
    
    public static ForceAggregator temperature(ForceAggregator delegate, double temperature) {
        return new Temperature(delegate, temperature);
    }
    
    private static class Simple implements ForceAggregator {
        private final Object key;
        
        public Simple(Object key) {
            this.key = key;
        }
        
        public GPoint getForce(Node node) {
            return GPoint.toGPoint(((Point2D)node.get(key)));
        }
        
        public void addForce(Node node, double x, double y) {
            Point2D p = ((Point2D)node.get(key));
            p.setLocation(p.getX() + x, p.getY() + y);
        }
    };
    
    private static class Scaled implements ForceAggregator {
        private final ForceAggregator delegate;
        private double scale;
        
        public Scaled(ForceAggregator delegate) {
            this(delegate, 1.0);
        }
        
        public Scaled(ForceAggregator delegate, double scale) {
            Args.notNull(delegate);
            this.delegate = delegate;
            this.scale = scale;
        }
        
        public void setScale(double scale) {
            this.scale = scale;
        }
        
        public double getScale() {
            return scale;
        }
        
        public GPoint getForce(Node node) {
            return delegate.getForce(node).mul(scale);
        }
        
        public void addForce(Node node, double x, double y) {
            delegate.addForce(node, x, y);
        }
    }
    
    private static class Temperature implements ForceAggregator {
        private final ForceAggregator delegate;
        private double temperature;
        private final double E = 0.000001D;
        
        public Temperature(ForceAggregator delegate, double temperature) {
            Args.notNull(delegate);
            this.delegate = delegate;
            this.temperature = temperature;
        }
        
        public double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }
        
        public GPoint getForce(Node node) {
            GPoint f = delegate.getForce(node);
            double deltaLength = Math.max(E, f.length());
            
            double fx = f.x / deltaLength * Math.min(deltaLength, temperature);
            
            double fy = f.y / deltaLength * Math.min(deltaLength, temperature);
            return new GPoint(fx, fy);
        }
        
        public void addForce(Node node, double x, double y) {
            delegate.addForce(node, x, y);
        }
    }
}

