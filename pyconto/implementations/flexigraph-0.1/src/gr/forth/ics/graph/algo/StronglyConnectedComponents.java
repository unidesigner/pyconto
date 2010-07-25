package gr.forth.ics.graph.algo;

import gr.forth.ics.graph.Direction;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.FoldingGraph;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.InspectableGraph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.util.Args;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author  Andreou Dimitris, email: jim.andreou (at) gmail (dot) com 
 */
class StronglyConnectedComponents implements Clusterer {
    private final LinkedList<Node> stack = new LinkedList<Node>();
    private final List<Collection<Node>> components = new ArrayList<Collection<Node>>();
    private int indexCounter = 0;
    private int componentCounter = 0;
    
    private final InspectableGraph graph;
    
    private StronglyConnectedComponents(InspectableGraph graph) {
        Args.notNull(graph);
        this.graph = graph;
    }
    
    private final Object DATA = new Object();

    static StronglyConnectedComponents execute(InspectableGraph g) {
        return new StronglyConnectedComponents(g).execute();
    }
    
    private StronglyConnectedComponents execute() {
        for (Node n : graph.nodes()) {
            if (!n.has(DATA)) {
                visit(n);
            }
        }
        return this;
    }

    private void visit(Node u) {
        NodeData uData = new NodeData();
        u.putWeakly(DATA, uData);
        uData.root = indexCounter;
        uData.index = indexCounter;
        indexCounter++;
        stack.addLast(u);
        for (Edge e : graph.edges(u, Direction.OUT)) {
            if (e.has(DATA)) continue;
            e.putWeakly(DATA, null);
            Node w = e.opposite(u);
            if (!w.has(DATA)) {
                visit(w);
            }
            NodeData wData = (NodeData)w.get(DATA);
            if (wData.component == null) {
                uData.root = Math.min(uData.root, wData.root);
            }
        }
        if (uData.root == uData.index) {
            Object componentId = componentCounter++;
            Collection<Node> component = new ArrayList<Node>();
            Node w;
            do {
                w = stack.removeLast();
                ((NodeData)w.get(DATA)).component = componentId;
                component.add(w);
            } while (w != u);
            components.add(component);
        }
    }

    public Collection<Object> getClusters() {
        return new AbstractList<Object>() {
            @Override
            public Object get(int index) {
                if (index < 0 || index >= size()) {
                    throw new IndexOutOfBoundsException("Invalid index: " + index + ", size: " + size());
                }
                return Integer.valueOf(index);
            }

            @Override
            public int size() {
                return componentCounter;
            }
        };
    }
    
    public Object findClusterOf(Node node) {
        NodeData data = (NodeData)node.get(DATA);
        if (data == null) {
            return null;
        }
        return data.component;
    }

    public Collection<Node> getCluster(Object key) {
        if (!(key instanceof Integer)) {
            return Collections.<Node>emptySet();
        }
        int componentId = (Integer)key;
        if (componentId < 0 || componentId >= components.size()) {
            return Collections.<Node>emptySet();
        }
        return components.get(componentId);
    }

    public InspectableGraph getGraph() {
        return graph;
    }

    public Iterator<Collection<Node>> iterator() {
        return Collections.unmodifiableList(components).iterator();
    }
    
    @Override
    public String toString() {
        return "[Components: " + components + "]";
    }
    
    private static class NodeData {
        int root;
        int index;
        Object component;
    }
}
