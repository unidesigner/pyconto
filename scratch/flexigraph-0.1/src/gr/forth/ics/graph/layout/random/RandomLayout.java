package gr.forth.ics.graph.layout.random;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.layout.GRect;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.layout.Layout;
import gr.forth.ics.graph.layout.LayoutProcess;
import gr.forth.ics.graph.layout.Locator;
import gr.forth.ics.graph.layout.SingleStepLayoutProcess;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import gr.forth.ics.util.Args;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class RandomLayout implements Layout {
    private final double x;
    private final double y;
    private final double w;
    private final double h;
    
    private final Random random = new Random();
    
    public RandomLayout(double width, double height) {
        Args.gte(width, 0);
        Args.gte(height, 0);
        x = 0;
        y = 0;
        w = width;
        h = height;
    }
    
    public RandomLayout(GRect rect) {
        Args.notNull(rect);
        x = rect.minX();
        y = rect.minY();
        w = rect.width();
        h = rect.height();
    }
    
    public RandomLayout(Rectangle2D rect) {
        Args.notNull(rect);
        x = rect.getX();
        y = rect.getY();
        w = rect.getWidth();
        h = rect.getHeight();
    }

    public LayoutProcess layout(final InspectableGraph graph) {
        Args.notNull(graph);
        return new SingleStepLayoutProcess() {
            protected void stepImpl(Locator locator) {
                for (Node node : graph.nodes()) {
                    locator.setLocation(node,
                            new GPoint(x + random.nextDouble() * w,
                            y + random.nextDouble() * h));
                }
            }
        };
    }
}
