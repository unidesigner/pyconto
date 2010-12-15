package gr.forth.ics.graph.layout.circular;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.layout.BasicLocator;
import gr.forth.ics.graph.layout.Locator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import gr.forth.ics.util.Args;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class CircularOrder {
    private final List<Node> order;
    
    public CircularOrder(List<Node> order) {
        this.order = order;
    }
    
    public CircularOrder(CircularOrder ... layouts) {
        this.order = new LinkedList<Node>();
        for (CircularOrder layout : layouts) {
            order.addAll(layout.getOrder());
        }
    }
    
    public List<Node> getOrder() {
        return Collections.unmodifiableList(order);
    }
    
    public void append(CircularOrder co) {
        Args.notNull(co);
        this.order.addAll(co.order);
        co.order.clear();
    }
    
    public Locator getCircleLayout(GPoint center, double r, double startArc,
            Locator locator) {
        return getCircleLayout(center, r, startArc, Math.PI * 2, locator);
    }
    
    public Locator getCircleLayout(GPoint center, double r, double startArc,
            double arcLength, Locator locator) {
        Args.notNull("Center point", center);
        if (locator == null) {
            locator = new BasicLocator();
        }
        if (order.isEmpty()) {
            return locator;
        }
        double step = arcLength / order.size();
        double currentArc = startArc;
        for (Node n : order) {
            locator.setLocation(n, center.add(r * Math.cos(currentArc), r * Math.sin(currentArc)));
            currentArc += step;
        }
        
        
        return locator;
    }
}
