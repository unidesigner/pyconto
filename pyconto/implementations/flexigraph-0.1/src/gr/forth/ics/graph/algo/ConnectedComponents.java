package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Graphs;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.path.Path;
import java.util.*;
import gr.forth.ics.util.DVMap;

class ConnectedComponents extends Dfs implements Clusterer {
    private final Map<Object, Collection<Node>> components
            = DVMap.newLinkedHashMapWithLinkedLists();
    private final Object MARK = new Object();
    
    private ConnectedComponents(InspectableGraph graph) {
        super(graph, Direction.EITHER);
    }
    
    private ConnectedComponents(InspectableGraph graph, Node startNode) {
        super(graph, startNode, Direction.EITHER);
    }
    
    static ConnectedComponents execute(InspectableGraph g) {
        ConnectedComponents cc = new ConnectedComponents(g);
        cc.execute();
        return cc;
    }
    
    static ConnectedComponents execute(InspectableGraph g, Node startNode) {
        ConnectedComponents cc = new ConnectedComponents(Graphs.undirected(g), startNode);
        cc.execute();
        return cc;
    }
    
    public Iterator<Collection<Node>> iterator() {
        return Collections.unmodifiableMap(components).values().iterator();
    }
    
    public Collection<Node> getCluster(Object cluster) {
        if (!components.containsKey(cluster)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(components.get(cluster));
    }
    
    public Set<Object> getClusters() {
        return Collections.unmodifiableSet(components.keySet());
    }
    
    @Override public boolean visitPre(Path path) {
        Node node = path.tailNode();
        Object tree = getComponentIdentifier(node);
        Collection<Node> component = components.get(tree);
        component.add(node);
        node.putWeakly(MARK, tree);
        return false;
    }

    public Object findClusterOf(Node node) {
        return getComponentIdentifier(node);
    }
    
    @Override
    public String toString() {
        return "[Components: " + components.values() + "]";
    }
}
