package gr.forth.ics.graph.layout;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import gr.forth.ics.util.Args;

public class GRect {
    private final GPoint topLeft;
    private final GPoint size;
    
    public GRect(double x, double y, double width, double height) {
        if (width < 0) {
            x += width;
            width = - width;
        }
        if (height < 0) {
            y += height;
            height = -height;
        }
        topLeft = new GPoint(x, y);
        size = new GPoint(width, height);
    }
    
    public GRect(GPoint corner, GPoint size) {
        Args.notNull(corner, size);
        if (size.x < 0 || size.y < 0) {
            double x = corner.x;
            double y = corner.y;
            double width = size.x;
            double height = size.y;
            if (width < 0) {
                x += width;
                width = - width;
            }
            if (height < 0) {
                y += height;
                height = -height;
            }
            this.topLeft = new GPoint(x, y);
            this.size = new GPoint(width, height);
            return;
        }
        this.topLeft = corner;
        this.size = size;
    }
    
    //throws npe
    public GRect(Dimension dimension) {
        this(0, 0, dimension.width, dimension.height);
    }
    
    public double minX() {
        return topLeft.x;
    }
    
    public double minY() {
        return topLeft.y;
    }
    
    public double width() {
        return size.x;
    }
    
    public double height() {
        return size.y;
    }
    
    public boolean contains(double x, double y) {
        return x >= topLeft.x && x <= topLeft.x + size.x && y >= topLeft.y && y <= topLeft.y + size.y;
    }
    
    public GPoint corner() {
        return topLeft;
    }
    
    public GPoint size() {
        return size;
    }
    
    public double maxX() {
        return width() + minX();
    }
    
    public double maxY() {
        return height() + minY();
    }

    public double midX() {
        return (minX() + maxX()) / 2.0;
    }

    public double midY() {
        return (minY() + maxY()) / 2.0;
    }

    public GPoint midPoint() {
        return new GPoint(midX(), midY());
    }
    
    //throws npe
    public boolean contains(GPoint point) {
        return contains(point.x, point.y);
    }
    
    public int hashCode() {
        return topLeft.hashCode() + size.hashCode();
    }
    
    //throws npe
    public GRect union(GPoint point) {
        return new GRect(topLeft.x + point.x, topLeft.y + point.y, size.x, size.y);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GRect)) {
            return false;
        }
        GRect other = (GRect)o;
        return topLeft.equals(other.topLeft) && size.equals(other.size);
    }
    
    public GRect transformedBy(AffineTransform trans) {
        throw new UnsupportedOperationException();
    }
    
    public static GRect wrap(Rectangle2D rect) {
        return new GRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }
    
    public static GRect wrap(Rectangle rect) {
        return new GRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }
    
    @Override
    public String toString() {
        return "[" + topLeft + "-" + topLeft.add(size) + "]";
    }
}
