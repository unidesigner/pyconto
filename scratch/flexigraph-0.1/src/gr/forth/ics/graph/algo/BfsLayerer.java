package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.path.Path;
import gr.forth.ics.graph.algo.Bfs;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.DVMap;

class BfsLayerer extends Bfs implements Layerer {
    private final Map<Integer, Collection<Node>> layers = DVMap.newHashMapWithHashSets();
    private final Object layerKey = new Object();

    private BfsLayerer(InspectableGraph graph, Node startNode, Direction direction) {
        super(graph, startNode, direction);
    }
    
    static BfsLayerer execute(InspectableGraph g, Direction direction) {
        BfsLayerer bfs = new BfsLayerer(g, null, direction);
        bfs.execute();
        return bfs;
    }
    
    static BfsLayerer execute(InspectableGraph g, Node startNode, Direction direction) {
        BfsLayerer bfs = new BfsLayerer(g, startNode, direction);
        bfs.execute();
        return bfs;
    }

    @Override
    protected boolean visitNewTree(Node node) {
        addNodeToLayer(node, 0);
        return false;
    }

    @Override
    protected boolean visitTreeEdge(Path path) {
        Node n = path.tailNode();
        addNodeToLayer(n, getLevel(n));
        return false;
    }
    
    private void addNodeToLayer(Node node, int layer) {
        layers.get(layer).add(node);
        node.putWeakly(layerKey, layer);
    }

    public Collection<Node> getLayer(int level) {
        Args.gte(level, 0);
        Integer key = Integer.valueOf(level);
        if (layers.containsKey(key)) {
            return Collections.unmodifiableCollection(layers.get(key));
        }
        return Collections.<Node>emptySet();
    }

    public Collection<Node> getCluster(Object key) {
        return getLayer((Integer) key);
    }

    public Set<Object> getClusters() {
        Set<Object> keys = new HashSet<Object>();
        for (int i = 0; i < getLayerCount(); i++) {
            keys.add(i);
        }
        return Collections.unmodifiableSet(keys);
    }

    public Iterator<Collection<Node>> iterator() {
        return layers.values().iterator();
    }

    public Object findClusterOf(Node node) {
        return node.get(layerKey);
    }

    public int findLayerOf(Node node) {
        return node.getInt(layerKey);
    }
}
