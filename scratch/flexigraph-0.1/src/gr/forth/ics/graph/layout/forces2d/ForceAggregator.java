package gr.forth.ics.graph.layout.forces2d;

import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.Node;

/**
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public interface ForceAggregator {
    void addForce(Node node, double x, double y);
    
    GPoint getForce(Node node);
}
