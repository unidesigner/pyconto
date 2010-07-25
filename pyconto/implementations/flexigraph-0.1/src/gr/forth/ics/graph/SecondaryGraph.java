package gr.forth.ics.graph;

import gr.forth.ics.graph.event.GraphEvent;

import gr.forth.ics.graph.path.Path;
import gr.forth.ics.util.Args;
import gr.forth.ics.util.Accessor;
import gr.forth.ics.util.FastLinkedList;
import gr.forth.ics.util.SerializableObject;
import java.io.Serializable;

/**
 * Flexible {@link Graph} implementation, which can contain (adopt) nodes and edges from any other graph.
 * This is a bit slower than {@link PrimaryGraph}, since the adjacency lists of the nodes are not stored
 * in directly accessible fields but a hashtable lookup (still O(1)) is required for them.
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail.com
 */
public final class SecondaryGraph extends AbstractListGraph {
    private static final long serialVersionUID = 2348144112946233116L;

    private final Object nodeDataKey = new SerializableObject();
    private final Object edgeOutRefKey = new SerializableObject();
    private final Object edgeInRefKey = new SerializableObject();

    public SecondaryGraph() { }

    public SecondaryGraph(InspectableGraph graph) {
        adoptGraph(graph);
    }

    public SecondaryGraph(InspectableGraph graph, Iterable<Node> nodes) {
        Args.notNull(graph, nodes);
        for (Node n : nodes) {
            adoptNode(n);
        }
        for (Node n : nodes) {
            for (Edge e : graph.edges(n)) {
                if (this.containsNode(e.opposite(n))) {
                    adoptEdge(e);
                }
            }
        }
    }

    public SecondaryGraph(Iterable<Node> nodes, Iterable<Edge> edges) {
        if (nodes != null) {
            for (Node n : nodes) {
                adoptNode(n);
            }
        }
        if (edges != null) {
            for (Edge e : edges) {
                adoptEdge(e);
            }
        }
    }

    @Override FastLinkedList<EdgeImpl> getOutEdges(NodeImpl node) {
        return ((NodeData)node.get(nodeDataKey)).outEdges;
    }

    @Override FastLinkedList<EdgeImpl> getInEdges(NodeImpl node) {
        return ((NodeData)node.get(nodeDataKey)).inEdges;
    }

    @Override final Accessor<NodeImpl> getNodeRef(NodeImpl node) {
        return ((NodeData)node.get(nodeDataKey)).ref;
    }

    @SuppressWarnings("unchecked")
    @Override final Accessor<EdgeImpl> getEdgeOutRef(EdgeImpl edge) {
        return (Accessor<EdgeImpl>)edge.get(edgeOutRefKey);
    }

    @SuppressWarnings("unchecked")
    @Override final Accessor<EdgeImpl> getEdgeInRef(EdgeImpl edge) {
        return (Accessor<EdgeImpl>)edge.get(edgeInRefKey);
    }

    @Override final void removeEdgeRefs(EdgeImpl edge) {
        edge.remove(edgeInRefKey);
        edge.remove(edgeOutRefKey);
    }

    @Override final void setNodeRef(NodeImpl node, Accessor<NodeImpl> ref) {
        ((NodeData)node.get(nodeDataKey)).ref = ref;
    }

    @Override final void setEdgeOutRef(EdgeImpl edge, Accessor<EdgeImpl> ref) {
        edge.putWeakly(edgeOutRefKey, ref);
    }

    @Override final void setEdgeInRef(EdgeImpl edge, Accessor<EdgeImpl> ref) {
        edge.putWeakly(edgeInRefKey, ref);
    }

    @Override final void removeNodeRef(NodeImpl node) {
        ((NodeData)node.get(nodeDataKey)).ref = null;
    }

    @Override final void initNode(NodeImpl node) {
        node.putWeakly(nodeDataKey, new NodeData());
    }

    @Override public final boolean isPrimary() {
        return false;
    }

    //returns true if *any* element of the given graph is actually adopted in this graph (ie, it was not
    //already contained to this graph);
    public boolean adoptGraph(InspectableGraph graph) {
        Args.notNull(graph);
        boolean changed = adoptNodes(graph.nodes());
        changed |= adoptEdges(graph.edges());
        return changed;
    }

    public boolean adoptNode(Node node) {
        Args.notNull(node);
        if (containsNode(node)) {
            return false;
        }
        final NodeImpl nodeImpl = (NodeImpl)node;
        graphEventSupport.fire(new GraphEvent(this, GraphEvent.Type.NODE_ADDED, node), new Runnable() {
            public void run() {
                NodeData nodeData = new NodeData();
                nodeData.ref = nodes.addLast(nodeImpl);
                nodeImpl.putWeakly(nodeDataKey, nodeData);
            }
        });
        return true;
    }

    public boolean adoptEdge(final Edge edge) {
        Args.notNull(edge);
        if (containsEdge(edge)) {
            return false;
        }
        graphEventSupport.fire(new GraphEvent(this, GraphEvent.Type.EDGE_ADDED, edge), new Runnable() {
            public void run() {
                EdgeImpl edgeImpl = (EdgeImpl)edge;
                adoptNode(edgeImpl.n1);
                adoptNode(edgeImpl.n2);
                edgeCount++;
                setEdgeOutRef(edgeImpl, getOutEdges(edgeImpl.n1).addLast(edgeImpl));
                setEdgeInRef(edgeImpl, getInEdges(edgeImpl.n2).addLast(edgeImpl));
            }
        });
        return true;
    }

    public boolean adoptPath(Path path) {
        return adoptEdges(path.edges());
    }

    public boolean adoptNodes(Iterable<Node> nodes) {
        Args.notNull(nodes);
        boolean changed = false;
        for (Node n : nodes) {
            changed |= adoptNode(n);
        }
        return changed;
    }

    public boolean adoptEdges(Iterable<Edge> edges) {
        Args.notNull(edges);
        boolean changed = false;
        for (Edge e : edges) {
            changed |= adoptEdge(e);
        }
        return changed;
    }

    public boolean removeGraph(InspectableGraph graph) {
        Args.notNull(graph);
        return removeNodes(graph.nodes()) != 0;
    }

    public boolean retainGraph(InspectableGraph graph) {
        Args.notNull(graph);
        boolean changed = false;
        for (Node n : nodes()) {
            if (!graph.containsNode(n)) {
                changed = true;
                removeNode(n);
            }
        }
        InspectableGraph one, other;
        if (this.edgeCount() > graph.edgeCount()) {
            one = graph;
            other = this;
        } else {
            one = this;
            other = graph;
        }
        for (Edge e : one.edges()) {
            if (!other.containsEdge(e)) {
                changed = true;
                removeEdge(e);
            }
        }
        return changed;
    }

    public boolean reinsertNode(Node n) {
        return adoptNode(n);
    }

    public boolean reinsertEdge(Edge e) {
        return adoptEdge(e);
    }

    private static class NodeData implements Serializable {
        private static final long serialVersionUID = 1L;
        Accessor<NodeImpl> ref;
        final FastLinkedList<EdgeImpl> inEdges = new FastLinkedList<EdgeImpl>();
        final FastLinkedList<EdgeImpl> outEdges = new FastLinkedList<EdgeImpl>();
    }
}
