package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Node;
import java.util.ArrayList;
import java.util.List;
import gr.forth.ics.util.Args;
import java.util.Arrays;

public class CompositeLocator extends BasicLocator {
    private final List<Locator> locators = new ArrayList<Locator>(2);
    private final LocationListener myListener = new MyListener();
    private boolean caching = true;

    public CompositeLocator(Locator... locators) {
        this(Arrays.asList(locators));
    }

    public CompositeLocator(Iterable<? extends Locator> locators) {
        Args.notNull(locators);
        for (Locator locator : locators) {
            addLocator(locator);
        }
    }
    
    /**
     * Returns whether this CompositeLocator caches the locations and bends that belong to aggregated
     * locators. So, these do not have to be transformed (by the locator AffineTransform) each time
     * when they are queried, but the memory cost is higher.
     */
    public boolean isCaching() {
        return caching;
    }
    
    /**
     * Sets the caching property of this CompositeLocator. @see #isCaching()
     */
    public void setCaching(boolean caching) {
        if (this.caching == true && caching == false) {
            flushOldPoints();
        }
        this.caching = caching;
    }
    
    public final  void addLocator(Locator locator) {
        Args.notNull(locator);
        locators.add(locator);
        locator.addLocationListener(myListener);
    }
    
    public void removeLocator(Locator locator) {
        locators.remove(locator);
        locator.removeLocationListener(myListener);
    }
    
    @Override public GPoint getLocation(Node node) {
        GPoint point = super.getLocation(node);
        if (point != null) {
            return point;
        }
        if (caching) {
            for (Locator nl : locators) {
                point = nl.getLocation(node);
                if (point != null) {
                    setLocation(node, point);
                    node.remove(LOCATION); //critical for flushing stored points that belong to aggregated locators
                    return super.getLocation(node); //will returned the now cached value
                }
            }
            return null;
        } else {
            for (Locator nl : locators) {
                point = nl.getLocation(node);
                if (point != null) {
                    return calcTransformedPoint(point);
                }
            }
            return null;
        }
    }
    
    @Override public List<GPoint> getBends(Edge edge) {
        List<GPoint> bends = super.getBends(edge);
        if (bends != null) {
            return bends;
        }
        if (caching) {
            for (Locator nl : locators) {
                bends = nl.getBends(edge);
                if (bends != null) {
                    setBends(edge, bends);
                    edge.remove(LOCATION); //critical for flushing stored points that belong to aggregated locators
                    return super.getBends(edge); //will returned the now cached value
                }
            }
            return null;
        } else {
            for (Locator nl : locators) {
                bends = nl.getBends(edge);
                if (bends != null) {
                    return calcTransformedBends(bends);
                }
            }
            return null;
        }
    }
    
    private class MyListener implements LocationListener {
        public void onNodeLocation(Node node, GPoint location) {
            removeLocation(node);
        }
        
        public void onEdgeLocation(Edge edge, List<GPoint> bends) {
            removeBends(edge);
        }
        
        public void transformed() {
            flushOldPoints();
        }
    }
}
