package gr.forth.ics.graph.layout;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author Andreou Dimitris, mail: andreou (at) csd (dot) uoc (dot) gr
 */
public class RectBuilder {
    private Rectangle2D rect;
    
    public RectBuilder() {
    }
    
    public void add(Rectangle2D rect) {
        if (rect == null) {
            return;
        }
        if (this.rect == null) {
            this.rect = rect;
        } else {
            this.rect.add(rect);
        }
    }
    
    public void add(GPoint point) {
        if (point == null) {
            return;
        }
        if (this.rect == null) {
            this.rect = new Rectangle2D.Double(point.x, point.y, 0, 0);
        } else {
            this.rect.add(point.toPoint2D(null));
        }
    }
    
    public GRect get() {
        return rect != null ? GRect.wrap(rect) : new GRect(0.0, 0.0, 0.0, 0.0);
    }
}
