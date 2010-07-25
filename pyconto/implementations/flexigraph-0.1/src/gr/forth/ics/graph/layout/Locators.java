package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.layout.GRect;
import gr.forth.ics.graph.layout.RectBuilder;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import java.awt.geom.AffineTransform;
import gr.forth.ics.util.Args;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class Locators {
    private Locators() {
    }

    public static Locator compose(Locator... locators) {
        return compose(Arrays.asList(locators));
    }

    public static Locator compose(Iterable<? extends Locator> locators) {
        return new CompositeLocator(locators);
    }
    
    public static GRect getBoundingBox(Locator locator, InspectableGraph graph) {
        return getBoundingBox(locator, graph.nodes(), graph.edges());
    }
    
    public static GRect getBoundingBox(Locator locator,
            Iterable<Node> nodes, Iterable<Edge> edges) {
        RectBuilder rectBuilder = new RectBuilder();
        for (Node node : nodes) {
            rectBuilder.add(locator.getLocation(node));
        }
        for (Edge edge : edges) {
            for (GPoint bend : locator.getBendsSafe(edge)) {
                rectBuilder.add(bend);
            }
        }
        return rectBuilder.get();
    }
    
    //TODO: is this useful as-is?
    public static void zoomToRect(Locator locator, InspectableGraph graph, GRect space) {
        Args.notNull(locator, graph, space);
        locator.setAffineTransform(new AffineTransform());
        GRect bbox = getBoundingBox(locator, graph);
        double sx = bbox.width() == 0.0 ? 1.0 : bbox.width();
        double sy = bbox.height() == 0.0 ? 1.0 : bbox.height();
        locator.translate(-bbox.minX(), -bbox.minY());
        locator.scale(1 / sx, 1 / sy);
        locator.scale(space.width(), space.height());
        locator.translate(space.minX(), space.minY());
    }
    
    public static void copy(Locator from, Locator to, InspectableGraph g) {
        Args.notNull(from, to, g);
        for (Node n : g.nodes()) {
            to.setLocation(n, from.getLocation(n));
        }
        for (Edge e : g.edges()) {
            to.setBends(e, from.getBendsSafe(e));
        }
    }
    
    
    private static final Locator emptyLocator = new Locator() {
        public GPoint getLocation(Node node) {
            return null;
        }

        public void removeLocation(Node node) {
        }

        public void setLocation(Node node, GPoint location) {
        }

        public void setLocation(Node node, double x, double y) {
        }

        public List<GPoint> getBends(Edge edge) {
            return null;
        }

        public List<GPoint> getBendsSafe(Edge edge) {
            return Collections.<GPoint>emptyList();
        }

        public void removeBends(Edge edge) {
        }

        public void setBend(Edge edge, GPoint singleBend) {
        }

        public GPoint getBend(Edge edge) {
            return null;
        }

        public void setBends(Edge edge, List<GPoint> bends) {
        }

        public void setBends(Edge edge, double[][] bends) {
        }

        public AffineTransform getAffineTransform() {
            return new AffineTransform();
        }

        public void setAffineTransform(AffineTransform trans) {
        }

        public Locator translate(double tx, double ty) {
            return this;
        }

        public Locator shear(double sx, double sy) {
            return this;
        }

        public Locator rotate(double theta) {
            return this;
        }

        public Locator rotate(double theta, double x, double y) {
            return this;
        }

        public Locator scale(double sx, double sy) {
            return this;
        }

        public void addLocationListener(LocationListener listener) {
        }

        public void removeLocationListener(LocationListener listener) {
        }

        public List<LocationListener> getLocationListeners() {
            return Collections.<LocationListener>emptyList();
        }
    };
    public static Locator emptyLocator() {
        return emptyLocator;
    }
}
