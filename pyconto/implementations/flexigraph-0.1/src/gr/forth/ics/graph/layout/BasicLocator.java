package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Node;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import gr.forth.ics.util.Args;

//otan allazei affinetransform, allazeis to key
public class BasicLocator extends AbstractLocator {
    protected final Object LOCATION;
    private Object CACHED = new Object();
    private final GPoint defaultLocation;
    
    public BasicLocator() {
        this((GPoint)null);
    }
    
    public BasicLocator(BasicLocator copy) {
        LOCATION = copy.LOCATION;
        CACHED = copy.CACHED;
        defaultLocation = copy.defaultLocation;
    }
    
    public BasicLocator(GPoint defaultLocation) {
        this.defaultLocation = defaultLocation;
        LOCATION = new Object();
    }
    
    public BasicLocator(AffineTransform affineTransform) {
        this(affineTransform, null);
    }
    
    public BasicLocator(AffineTransform affineTransform, GPoint defaultLocation) {
        super(affineTransform);
        this.defaultLocation = defaultLocation;
        LOCATION = new Object();
    }
    
    @Override
    public void removeLocation(Node node) {
        if (node == null) {
            return;
        }
        node.remove(LOCATION);
        node.remove(CACHED);
    }
    
    protected void flushOldPoints() {
        CACHED = new Object();
    }
    
    public GPoint getLocation(Node node) {
        if (!node.has(CACHED)) {
            GPoint initialLocation = (GPoint)node.get(LOCATION);
            if (initialLocation == null) {
                return defaultLocation;
            }
            setLocation(node, initialLocation);
        }
        return (GPoint)node.get(CACHED);
    }
    
    @SuppressWarnings("unchecked")
    public List<GPoint> getBends(Edge edge) {
        Args.notNull(edge);
        if (!edge.has(CACHED)) {
            List<GPoint> initialBends = (List<GPoint>)edge.get(LOCATION);
            if (initialBends == null) {
                return null;
            }
            setBends(edge, initialBends);
        }
        return (List<GPoint>)edge.get(CACHED);
    }
    
    public void setLocation(Node node, GPoint location) {
        node.putWeakly(LOCATION, location);
        GPoint target = calcTransformedPoint(location);
        node.putWeakly(CACHED, target);
        fireNodeLocation(node, target);
    }
    
    protected final GPoint calcTransformedPoint(GPoint location) {
        if (isIdentity) {
            return location;
        }
        double[] coords = { location.x, location.y };
        affineTransform.transform(coords, 0, coords, 0, 1);
        return new GPoint(coords[0], coords[1]);
    }
    
    public void setBends(Edge edge, List<GPoint> bends) {
        Args.notNull(edge);
        List<GPoint> readOnly = bends == null ? null : Collections.unmodifiableList(new ArrayList<GPoint>(bends));
        edge.putWeakly(LOCATION, readOnly);
        List<GPoint> cached = isIdentity ? readOnly : calcTransformedBends(readOnly);
        edge.putWeakly(CACHED, cached);
        fireEdgeLocation(edge, cached);
    }
    
    protected final List<GPoint> calcTransformedBends(List<GPoint> readOnly) {
        if (readOnly == null) {
            return null;
        }
        List<GPoint> list = new ArrayList<GPoint>(readOnly.size());
        for (GPoint p : readOnly) {
            list.add(calcTransformedPoint(p));
        }
        return Collections.unmodifiableList(list);
    }
}
