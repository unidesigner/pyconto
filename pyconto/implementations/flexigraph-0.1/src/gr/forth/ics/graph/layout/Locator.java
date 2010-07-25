package gr.forth.ics.graph.layout;

import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Node;
import java.awt.geom.AffineTransform;
import java.util.EventListener;
import java.util.List;

public interface Locator {
    GPoint getLocation(Node node);
    void removeLocation(Node node);
    void setLocation(Node node, GPoint location); //null location removes node
    void setLocation(Node node, double x, double y);
    
    //read-only list, may be null
    List<GPoint> getBends(Edge edge);
    
    //not-null (safe)
    List<GPoint> getBendsSafe(Edge edge);
    
    void removeBends(Edge edge);
    void setBend(Edge edge, GPoint singleBend);
    GPoint getBend(Edge edge);
    void setBends(Edge edge, List<GPoint> bends);
    //[N][2] array
    void setBends(Edge edge, double[][] bends);
    
    AffineTransform getAffineTransform();
    void setAffineTransform(AffineTransform trans);
    
    Locator translate(double tx, double ty);
    Locator shear(double sx, double sy);
    Locator rotate(double theta);
    Locator rotate(double theta, double x, double y);
    Locator scale(double sx, double sy);
    
    void addLocationListener(LocationListener listener);
    void removeLocationListener(LocationListener listener);
    List<LocationListener> getLocationListeners();
    
    interface LocationListener extends EventListener {
        void onNodeLocation(Node node, GPoint gpoint);
        void onEdgeLocation(Edge edge, List<GPoint> bends);
        void transformed();
    }
}