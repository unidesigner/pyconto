package gr.forth.ics.graph.layout;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import gr.forth.ics.util.Args;

public class Geom {
    private Geom() { }
    
    public static GPoint findIntersection(Line2D line, Shape shape) {
        return findIntersection(line, shape.getPathIterator(null, 1.0));
    }
    
    public static GPoint findIntersection(Line2D line, PathIterator path) {
        int count = 0;
        double x1 = 0;
        double y1 = 0;
        double x2 = 0;
        double y2 = 0;
        
        while (!path.isDone()) {
            double[] coords = {0, 0, 0, 0, 0, 0};
            int type = path.currentSegment(coords);
            x1 = coords[0];
            y1 = coords[1];
            if (type != PathIterator.SEG_CLOSE) {
                if (count > 0) {
                    Line2D line2 = new Line2D.Double(x1, y1, x2, y2);
                    GPoint point = findIntersection(line, line2);
                    if (point != null) {
                        return point;
                    }
                }
            }
            count++;
            x2 = x1;
            y2 = y1;
            
            path.next();
        }
        return null;
    }
    
    public static GPoint findIntersection(Line2D l1, Line2D l2) {
        /*
         * y = m * x + b;
         * m = (y1 - y2) / (x1 - x2);
         */
        if (! l1.intersectsLine(l2)) {
            return null;
        }
        
        if ( (l1.getX1() == l1.getX2()) && (l2.getX1() != l2.getX2()) ) {
            double m2 = (l2.getY1() - l2.getY2()) / (l2.getX1() - l2.getX2());
            double b2 = l2.getY1() - (l2.getX1() * m2);
            double y2 = (m2 * l1.getX1()) + b2;
            return new GPoint(l1.getX1(), y2);
        } else if ( (l2.getX1() == l2.getX2()) && (l1.getX1() != l1.getX2()) ) {
            double m1 = (l1.getY1() - l1.getY2()) / (l1.getX1() - l1.getX2());
            double b1 = l1.getY1() - (l1.getX1() * m1);
            double y1 = (m1 * l2.getX1()) + b1;
            return new GPoint(l2.getX1(), y1);
        } else {
            double m1 = (l1.getY1() - l1.getY2()) / (l1.getX1() - l1.getX2());
            double b1 = l1.getY1() - (l1.getX1() * m1);
            double m2 = (l2.getY1() - l2.getY2()) / (l2.getX1() - l2.getX2());
            double b2 = l2.getY1() - (l2.getX1() * m2);
            
            double x = (b2 - b1) / (m1 - m2);
            double y1 = (m1 * x) + b1;
            double y2 = (m2 * x) + b2;
            
            int diff = (int) (y1 - y2);
            
            if (diff > -1 && diff < 1 ) {
                return new GPoint(x, y1);
            }
        }
        return null;
    }
}
