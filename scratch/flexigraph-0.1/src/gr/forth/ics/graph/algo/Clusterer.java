package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import java.util.Collection;

public interface Clusterer extends Iterable<Collection<Node>> {
    Collection<Object> getClusters();
    Collection<Node> getCluster(Object key);
    InspectableGraph getGraph();
    
    Object findClusterOf(Node node);
}
