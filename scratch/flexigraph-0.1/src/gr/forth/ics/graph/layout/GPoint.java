package gr.forth.ics.graph.layout;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class GPoint {
    public static final GPoint ZERO_POINT = new GPoint(0.0, 0.0);
    
    public final double x, y;
    
    private GPoint() {
        this(0.0, 0.0);
    }
    
    public GPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double distance(GPoint other) {
        return distance(other.x, other.y);
    }
    
    public double distance(double x, double y) {
        return Math.hypot(this.x - x, this.y - y);
    }
    
    public double dotProduct(GPoint other) {
        return x * other.x + y * other.y;
    }
    
    public double length() {
        return Math.hypot(x, y);
    }
    
    public GPoint add(GPoint other) {
        //TODO: is the following optimization worth it?
        if (other == GPoint.ZERO_POINT) {
            return this;
        }
        return add(other.x, other.y);
    }
    
    public GPoint add(double x, double y) {
        return new GPoint(this.x + x, this.y + y);
    }
    
    public GPoint subtract(GPoint other) {
        return subtract(other.x, other.y);
    }
    
    public GPoint subtract(double x, double y) {
        return new GPoint(this.x - x, this.y - y);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GPoint)) {
            return false;
        }
        GPoint other = (GPoint)o;
        return x == other.x && y == other.y;
    }
    
    @Override
    public int hashCode() {
        return (int)(Double.doubleToRawLongBits(x) + Double.doubleToRawLongBits(y));
    }
    
    public Point2D toPoint2D(Point2D p) {
        if (p == null) {
            return new Point2D.Double(x, y);
        }
        p.setLocation(x, y);
        return p;
    }
    
    public GPoint midPoint(GPoint other) {
        return midPoint(other.x, other.y);
    }
    
    public GPoint midPoint(double x, double y) {
        return new GPoint((this.x + x) / 2, (this.y + y) / 2);
    }
    
    //0 ratio is this, 1 ratio is other, 0.5 is midPoint
    public GPoint midPoint(GPoint other, double ratio) {
        return midPoint(other.x, other.y, ratio);
    }
    
    public GPoint midPoint(double x, double y, double ratio) {
        double dx = x - this.x;
        double dy = y - this.y;
        return new GPoint(this.x + ratio * dx, this.y + ratio * dy);
    }
    
    public static GPoint toGPoint(Point2D p) {
        return new GPoint(p.getX(), p.getY());
    }
    
    public GPoint mul(double k) {
        return new GPoint(x * k, y * k);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public static Line2D toLine(GPoint p1, GPoint p2) {
        return new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
    }
    
    //TODO: me8odos gia grammiko ginomeno ka + mb (a, b: GPoint)
}
