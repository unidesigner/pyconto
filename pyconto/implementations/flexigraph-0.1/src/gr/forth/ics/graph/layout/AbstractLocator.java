package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.layout.Locator.LocationListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.EventSupport;

public abstract class AbstractLocator implements Locator {
    protected AffineTransform affineTransform;
    protected boolean isIdentity;
    
    private final EventSupport<LocationListener> eventSupport = new EventSupport<LocationListener>();
    
    protected abstract void flushOldPoints();
    
    public AbstractLocator() {
        setAffineTransform(new AffineTransform());
    }
    
    public AbstractLocator(AffineTransform affineTransform) {
        setAffineTransform(affineTransform);
    }
    
    //override for faster calculations
    public void setLocation(Node node, double x, double y) {
        setLocation(node, new GPoint(x, y));
    }
    
    public void removeLocation(Node node) {
        if (node == null) {
            return;
        }
        setLocation(node, null);
    }
    
    public void removeBends(Edge edge) {
        setBends(edge, (List<GPoint>)null);
    }
    
    public GPoint getBend(Edge edge) {
        List<GPoint> bends = getBends(edge);
        if (bends == null || bends.isEmpty()) {
            return null;
        }
        return bends.get(0);
    }
    
    public void setBends(Edge edge, double[][] bends) {
        Args.notNull((Object[])bends);
        List<GPoint> bendPoints = new ArrayList<GPoint>(bends.length);
        for (double[] coords : bends) {
            Args.notNull(coords);
            Args.isTrue(coords.length == 2);
            GPoint point = new GPoint(coords[0], coords[1]);
            bendPoints.add(point);
        }
        setBends(edge, bendPoints);
    }
    
    public void setBend(Edge edge, GPoint singleBend) {
        setBends(edge, Collections.singletonList(singleBend));
    }
    
    @SuppressWarnings("unchecked")
    public List<GPoint> getBendsSafe(Edge edge) {
        List<GPoint> bends = getBends(edge);
        if (bends == null) {
            return (List<GPoint>)Collections.EMPTY_LIST;
        }
        return bends;
    }
    
    public AffineTransform getAffineTransform() {
        return affineTransform;
    }
    
    //null means identity
    public final void setAffineTransform(AffineTransform trans) {
        if (trans == null) {
            trans = new AffineTransform();
        }
        this.affineTransform = new AffineTransform(trans);
        isIdentity = trans.isIdentity();
        flushOldPoints();
        fireTransformed();
    }
    
    public Locator rotate(double theta) {
        return preconcatenate(AffineTransform.getRotateInstance(theta));
    }
    
    public Locator translate(double tx, double ty) {
        return preconcatenate(AffineTransform.getTranslateInstance(tx, ty));
    }
    
    public Locator shear(double sx, double sy) {
        return preconcatenate(AffineTransform.getShearInstance(sx, sy));
    }
    
    public Locator scale(double sx, double sy) {
        return preconcatenate(AffineTransform.getScaleInstance(sx, sy));
    }
    
    public Locator rotate(double theta, double x, double y) {
        return preconcatenate(AffineTransform.getRotateInstance(theta, x, y));
    }

    private Locator preconcatenate(AffineTransform transform) {
        flushOldPoints();
        isIdentity = false;
        affineTransform.preConcatenate(transform);
        fireTransformed();
        return this;
    }
    
    public void removeLocationListener(Locator.LocationListener listener) {
        eventSupport.removeListener(listener);
    }
    
    public void addLocationListener(LocationListener listener) {
        eventSupport.addListener(listener);
    }
    
    public List<LocationListener> getLocationListeners() {
        return eventSupport.getListeners();
    }
    
    protected void fireNodeLocation(Node node, GPoint location) {
        for (LocationListener listener : eventSupport.getListeners()) {
            listener.onNodeLocation(node, location);
        }
    }
    
    protected void fireEdgeLocation(Edge edge, List<GPoint> bends) {
        for (LocationListener listener : eventSupport.getListeners()) {
            listener.onEdgeLocation(edge, bends);
        }
    }
    
    protected void fireTransformed() {
        for (LocationListener listener : eventSupport.getListeners()) {
            listener.transformed();
        }
    }
}
