package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Node;
import java.util.Collection;

public interface Layerer extends Clusterer {
    Collection<Node> getLayer(int level);
    int getLayerCount();

    int findLayerOf(Node node);
}
