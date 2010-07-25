package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
public class Layerers {
    private Layerers() { }
    
    public static Layerer bfs(InspectableGraph g, Direction direction) {
        return BfsLayerer.execute(g, direction);
    }
    
    public static Layerer bfs(InspectableGraph g, Node n, Direction direction) {
        return BfsLayerer.execute(g, n, direction);
    }
}
