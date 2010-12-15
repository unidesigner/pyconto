package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.layout.Locator;
import gr.forth.ics.util.Args;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class Forces {
    private Forces() {
    }
    
    public static class Gravity extends NodeForce {
        private double constant;
        private final GPoint center;
        
        public Gravity(GPoint center) {
            this(center, 10);
        }
        
        public Gravity(GPoint center, double constant) {
            Args.notNull(center);
            this.setConstant(constant);
            this.center = center;
        }
        
        protected void apply(Node n, ForceAggregator forceMap) {
            GPoint p = locator.getLocation(n);
            
            double gdx = p.x - center.x;
            double dx = -gdx * Math.abs(gdx) * getConstant();
            double gdy = p.y - center.y;
            double dy = -gdy * Math.abs(gdy) * getConstant();
            forceMap.addForce(n, dx, dy);
        }
        
        public double getConstant() {
            return constant;
        }
        
        public void setConstant(double constant) {
            this.constant = constant;
        }
    }
    
    public static class NodeRepulsion extends NodeToNodeForce {
        private double constant;
        private static final double E = 0.000001D;
        
        public NodeRepulsion() {
            this(0.001);
        }
        
        public NodeRepulsion(double constant) {
            this.constant = constant;
        }
        
        protected void apply(Node n1, Node n2, ForceAggregator forceMap) {
            GPoint p1 = locator.getLocation(n1);
            GPoint p2 = locator.getLocation(n2);
            double d = Math.max(E, p1.distance(p2));
            double f3 = constant * constant / d;
            double dx = f3 * (p2.x - p1.x) / d;
            double dy = f3 * (p2.y - p1.y) / d;
            forceMap.addForce(n2, dx, dy);
            
            
            /*
                double xDelta = p1.x() - p2.x();
                double yDelta = p1.y() - p2.y();
             
                double d = Math.max(EPSILON, Math
                        .sqrt((xDelta * xDelta) + (yDelta * yDelta)));
             
                double force = (forceConstant * forceConstant) / d;
             
                if (Double.isNaN(force)) { throw new RuntimeException(
                        "Unexpected mathematical result in FRLayout:calcPositions [repulsion]"); }
             
                fvd1.add(new Point(
                        (float)((xDelta / deltaLength) * force),
                        (float)((yDelta / deltaLength) * force)));
             */
        }
        
        public double getConstant() {
            return constant;
        }
        
        public void setConstant(double constant) {
            this.constant = constant;
        }
    }
    
    public static class EadesSpring extends EdgeForce {
        private double lambda;
        private double k;
        
        public EadesSpring() {
            this(10.0);
        }
        
        public EadesSpring(double lambda) {
            this(lambda, 4.0);
        }
        
        public EadesSpring(double lambda, double k) {
            this.setLambda(lambda);
            this.setK(k);
        }
        
        protected void apply(Edge e, ForceAggregator forceMap) {
            Node n1 = e.n1();
            Node n2 = e.n2();
            GPoint p1 = locator.getLocation(n1);
            GPoint p2 = locator.getLocation(n2);
            double dist = p1.distance(p2);
            if (dist == 0.0) {
                return;
            }
            double v = getK() * Math.log(dist / getLambda());
            double scaleFactor = (v / dist);
            double dx = scaleFactor * (p2.x - p1.x);
            double dy = scaleFactor * (p2.y - p1.y);
            forceMap.addForce(n1, dx, dy);
            forceMap.addForce(n2, -dx, -dy);
        }
        
        public double getLambda() {
            return lambda;
        }
        
        public void setLambda(double lambda) {
            this.lambda = lambda;
        }
        
        public double getK() {
            return k;
        }
        
        public void setK(double k) {
            this.k = k;
        }
    }
    
    public static class FRSpring extends EdgeForce {
        private double force;
        private double k;
        private static final double E = 0.000001D;
        
        public FRSpring() {
            this(40);
        }
        
        public FRSpring(double force) {
            this.setForce(force);
        }
        
        protected void apply(Edge e, ForceAggregator forceMap) {
            Node n1 = e.n1();
            Node n2 = e.n2();
            
            GPoint p1 = locator.getLocation(n1);
            GPoint p2 = locator.getLocation(n2);
            double d = Math.max(E, p1.distance(p2));
            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;
            double f = d * d / getForce();
            double fdx = f * dx / d;
            double fdy = f * dy / d;
            forceMap.addForce(n1, fdx, fdy);
            forceMap.addForce(n2, -fdx, -fdy);
        }
        
        public double getForce() {
            return force;
        }
        
        public void setForce(double lambda) {
            this.force = lambda;
        }
    }
    
    public static class ScaledForce implements Force {
        private final Force force;
        private double scale;
        
        public ScaledForce(Force delegate) {
            this(delegate, 1.0);
        }
        
        public ScaledForce(Force delegate, double scale) {
            Args.notNull(delegate);
            this.force = delegate;
            this.scale = scale;
        }
        
        public double getScale() {
            return scale;
        }
        
        public void setScale(double scale) {
            this.scale = scale;
        }
        
        public void apply(InspectableGraph graph, final Locator locator, final ForceAggregator forceMap) {
            ForceAggregator wrappedForceMap = new ForceAggregator() {
                public GPoint getForce(Node node) {
                    return forceMap.getForce(node);
                }
                
                public void addForce(Node node, double x, double y) {
                    forceMap.addForce(node, x * scale, y * scale);
                }
            };
            force.apply(graph, locator, wrappedForceMap);
        }
    }
}
